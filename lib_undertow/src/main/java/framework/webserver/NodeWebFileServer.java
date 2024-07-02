package framework.webserver;


import bottle.util.Log4j;
import com.alibaba.excel.EasyExcel;
import io.undertow.Handlers;
import io.undertow.Undertow;
import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.PathHandler;
import io.undertow.server.handlers.resource.PathResourceManager;
import io.undertow.servlet.Servlets;
import io.undertow.servlet.api.DeploymentInfo;
import io.undertow.servlet.api.DeploymentManager;
import java.io.*;
import java.nio.file.Paths;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import static bottle.util.FileTool.deleteFiles;
import static bottle.util.WebServerUtil.getFreePort;
import static bottle.util.WebServerUtil.localHostPublicNetIpAddr;


public class NodeWebFileServer {

    private static final Timer timer = new Timer();

    private static int port = -1;

    private static String tempFileDirPath = "./node_temp";

    private static long tempFilePeriodTime = 10 * 60 * 1000L;

    private static String domain = "file://"+tempFileDirPath;

    //当前web服务对象
    private static Undertow webObject;

    /* 被代理的静态方法 */
    public static void startWebServer(int httpPort, String httpFilePath, long httpFileTime){
        try {
            Log4j.info("startWebServer httpPort=" +httpPort
                    +" httpFilePath=" +httpFilePath
                    +" httpFileTime="+httpFileTime
                    +" tempFilePeriodTime="+tempFilePeriodTime
            );

            if (httpFileTime > 0 ){
                tempFilePeriodTime = httpFileTime*1000L;
            }
            if (httpFilePath!=null && httpFilePath.length()>0) {
                tempFileDirPath = httpFilePath;
            }
            if (httpPort>=0 && httpPort<=65535){
                port = httpPort;
            }

            if (port<0) return;

            if (port == 0) port = getFreePort();

            if (webObject != null) return;

            File dir = new File(tempFileDirPath);
            if (!dir.exists()) {
                if (!dir.mkdir()) {
                    Log4j.info("无法创建文件夹: " + tempFileDirPath);
                    return;
                }
            }

            //删除过期文件
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    Log4j.info("deleteFiles start tempFilePeriodTime=" +tempFilePeriodTime);
                    deleteFiles(tempFileDirPath,tempFilePeriodTime);
                }
            }, tempFilePeriodTime, tempFilePeriodTime);


            DeploymentInfo servletBuilder = Servlets.deployment()
                    .setClassLoader(NodeWebFileServer.class.getClassLoader())
                    .setContextPath("/")
                    .setDeploymentName("web-server.war")
                    .setResourceManager(
                            new PathResourceManager(Paths.get(dir.toURI()), 16 * 4069L)
                    );

            DeploymentManager manager = Servlets.defaultContainer().addDeployment(servletBuilder);
            manager.deploy();
            HttpHandler httpHandler = manager.start();
            //路径默认处理程序
            PathHandler pathHandler = Handlers.path(httpHandler);

            Undertow.Builder builder = Undertow.builder();
            builder.addHttpListener(port, "0.0.0.0", httpHandler);
            String netip = localHostPublicNetIpAddr();
            if (netip == null) throw new RuntimeException("无法获取到外网真实IP地址");
            domain = String.format("http://%s:%d", netip, port);
            webObject = builder.build();
            webObject.start();
            Log4j.info("WEB " + "0.0.0.0:" + port + " FILE: " + dir.toURI() + " DOMAIN: " + domain);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    /* 被代理的静态方法 */
    public static void stopWebServer(){
        timer.cancel();
        if (webObject!=null) webObject.stop();
    }


    public static String findFilesToURL(String fileName){
        fileName = fileName.replace("/","_").replace("\\","_");
        File file = new File(tempFileDirPath,fileName);
        if (!file.exists()){
            return null;
        }
        return domain+"/"+fileName;
    }

    public static boolean storageFileByte(String fileName, byte[] bytes){
        fileName = fileName.replace("/","_").replace("\\","_");
        File file = new File(tempFileDirPath,fileName);
        Log4j.info("node节点存储file: " + file +" 字节大小: "+ bytes.length);
        try(FileOutputStream fos  = new FileOutputStream(file)){
            fos.write(bytes);
            return true;
        }catch (Exception e){
            e.printStackTrace();
        }

        return false;
    }

    public static boolean storageFileStream(String fileName, ByteArrayOutputStream bos){
        return storageFileByte(fileName,bos.toByteArray());
    }

    /*** 使用EasyExcel导出EXCEL保存在本地 */
    public static String storageExcelFile(String fileName,List<List<String>> header, List<? extends List<?>> datas){
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            long t0 = System.currentTimeMillis();
            EasyExcel.write(bos).head(header).automaticMergeHead(false).sheet().doWrite(datas);
            long t1 =  System.currentTimeMillis();
            boolean isSave = storageFileStream(fileName,bos);
            long t2 =  System.currentTimeMillis();
            Log4j.info("需导出数据条数: "+ datas.size() +" EasyExcel生成EXCEL耗时: "+ (t1 - t0)+" ,保存文件耗时: "+ (t2-t1) +" 执行方法总耗时: "+ (t2-t0));

            if (isSave){
                String url = findFilesToURL(fileName);
                Log4j.info("本地Excel文件保存: " + fileName+" -> "+ url);
                if (url!=null) return url;
            }
        } catch (Exception e) {
            Log4j.error("节点保存临时文件(excel)异常",e);
        }
        return "";
    }

}
