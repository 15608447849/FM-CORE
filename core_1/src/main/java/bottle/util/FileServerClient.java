package bottle.util;


import com.google.gson.reflect.TypeToken;

import java.io.*;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/*
* 需要配合文件服务器使用
* */
public class FileServerClient {

    public static boolean isDebug = false;

    /* 文件服务器返回的外层结构对象 */
    public static final class ServletResult<T>{
        private int code;
        private String message;
        private T data;
    }

    /* 文件服务器返回的上传文件对象 */
    public static final class UploadFileItem{
        private boolean success = false; //是否本地保存成功
        private String error;//错误信息
        private String httpUrl;//http下载的绝对路径
        private String currentFileName;//现在的文件名
        private String suffix; //文件后缀
        private String md5;//文件MD5值
        private String localAbsolutelyPath;//本地存储的绝对
        private String relativePath;//文件服务的相对路径
        private long fileSize = 0L;// 文件大小
    }


    /***************************************************************文件操作方法********************************************************************/
    /** 文件上传到服务器 */
    public static String uploadFile(String url, String serverFilePath, String fileName, InputStream fio){
        if(isDebug) Log4j.info("上传文件: "+ url + " , "+serverFilePath+" , "+ fileName);
        return new HttpRequest().addStream(fio,serverFilePath, fileName)
                .fileUploadUrl(url)
                .getRespondContent();
    }

    /** 图片上传到服务器 */
    public static String uploadImage(String url, String serverFilePath, String fileName,
                                     boolean isCompress,boolean isLogo,boolean isThumb,
                                     InputStream fio){
        if(isDebug) Log4j.info("上传图片: "+ url + " , "+serverFilePath+" , "+ fileName);
        //上传图片
        return new HttpRequest().addStream(fio, serverFilePath,  fileName)
                .setCompress(isCompress) //服务器压缩
                .setLogo(isLogo) //图片水印
                .setThumb(isThumb) //图片略缩图
                .setCompressLimitSieze(5*1024*1024L)
                .fileUploadUrl(url)//文件上传URL
                .getRespondContent();
    }

    /** 检查一个具体文件是否存在 */
    public static boolean isFileExist(String url,String filePath) {
        try {
            if(isDebug) Log4j.info("检查文件: "+ url + " , "+ filePath);
            String json = new HttpRequest().existTargetFile(url,filePath).getRespondContent();
            if (StringUtil.isEmpty(json) || json.contains("java.net.SocketTimeoutException")) return false;

            ServletResult<Boolean> servletResult =
                    GoogleGsonUtil.jsonToJavaBean(json,new TypeToken<ServletResult<Boolean>>(){}.getType());
            if(servletResult!=null && servletResult.code==200) return servletResult.data;

        } catch (Exception e) {
            Log4j.error("检查远程文件存在错误 "+filePath ,e);
        }
        return false;
    }

    /** 检查目录存在哪些文件 */
    public static List<String> showDirFilesPath(String url, String dirPath, boolean isSubDir) {
        try {
            if(isDebug) Log4j.info("遍历目录: "+ url + " , "+dirPath);
            String json = new HttpRequest().getTargetDirFileList(url,dirPath,isSubDir).getRespondContent();
            if (StringUtil.isEmpty(json) || json.contains("java.net.SocketTimeoutException")) return null;

            ServletResult<List<String>> servletResult =
                    GoogleGsonUtil.jsonToJavaBean(json,new TypeToken<ServletResult<List<String>>>(){}.getType());

            if(servletResult!=null && servletResult.code==200) return servletResult.data;
        } catch (Exception e) {
            Log4j.error("远程文件遍历错误 "+dirPath  ,e);
        }
        return null;
    }


    /** 删除文件 */
    public static boolean deleteFile(String url,String... deleteFileList){
        try {
            String json = new HttpRequest().deleteFile(url,deleteFileList).getRespondContent();
            if (StringUtil.isEmpty(json) || json.contains("java.net.SocketTimeoutException")) return false;

            ServletResult<Object> servletResult =
                    GoogleGsonUtil.jsonToJavaBean(json,new TypeToken<ServletResult<Object>>(){}.getType());

            if(servletResult!=null && servletResult.code==200) return true;
        }catch (Exception e){
            Log4j.error("远程文件删除错误 " + Arrays.toString(deleteFileList),e);
        }
        return false;
    }

    /** 批量下载 */
    public static String batchDownloadUrl(String url,List<String> dictList){
        try {
            if(isDebug) Log4j.info("批量下载: "+ url + " , "+ GoogleGsonUtil.javaBeanToJson(dictList));
            String json = new HttpRequest().getBatchDownloadFileZipUrl(url,dictList).getRespondContent();
            if (StringUtil.isEmpty(json) || json.contains("java.net.SocketTimeoutException")) return null;

            ServletResult<String> servletResult =
                    GoogleGsonUtil.jsonToJavaBean(json,new TypeToken<ServletResult<String>>(){}.getType());
            if(servletResult!=null && servletResult.code==200) return servletResult.data;

        } catch (Exception e) {
            Log4j.error("批量打包ZIP错误 " + GoogleGsonUtil.javaBeanToJson(dictList),e);
        }
        return null;
    }

    /** 上传临时文件并获取上传对象 */
    public static UploadFileItem uploadFileGetUploadItem(String url,String dict,String fileName, InputStream in){
        try {
            String saveDir = dict==null? "/temp" : "/temp/"+dict;
            //上传文件
            String json = uploadFile(url, saveDir, fileName, in);
            if (StringUtil.isEmpty(json) || json.contains("java.net.SocketTimeoutException")) return null;

            ServletResult<List<UploadFileItem>> servletResult =
                    GoogleGsonUtil.jsonToJavaBean(json,new TypeToken<ServletResult<List<UploadFileItem>>>(){}.getType());

            if(servletResult!=null && servletResult.code==200 &&  servletResult.data.size()>0){
                return servletResult.data.get(0);
            }
        } catch (Exception e) {
            Log4j.error("文件上传错误 " + dict + "/" +fileName ,e);
        }
        return null;
    }

    /** 上传临时文件并获取下载路径 */
    public static String uploadFileAndGetDownPathURL(String url,String dict,String fileName, InputStream in){
        try {
            UploadFileItem item = uploadFileGetUploadItem(url,dict,fileName,in);
            if(item!=null) return item.httpUrl;
        } catch (Exception e) {
            Log4j.error("文件上传错误 " + dict + "/" +fileName ,e);
        }
        return null;
    }

    /** 上传临时文件, 返回下载路径 */
    public static String uploadFileAndGetDownPathURL(String url,String fileName, InputStream in){
        return uploadFileAndGetDownPathURL(url,null,fileName,in);
    }

    /** 指定后缀 写入文件服务器 并获取 下载地址 */
    public static String uploadSpecSuffixFileToServerReturnUrl(String url, String suffix, InputStream in) {
        try {
            String downPathURL = uploadFileAndGetDownPathURL(url,suffix,TimeTool.date_yMdHms_2Str(new Date()) +"."+suffix, in);
            if (downPathURL == null) throw new IllegalArgumentException(suffix + "文件 上传失败,无法获取下载URL");
            return downPathURL;
        }catch (Exception e){
            Log4j.error("EXCEL写入文件服务器并获取下载地址错误",e);
        }
        return "";
    }

    /** 指定后缀 写入文件服务器 并获取 下载地址 */
    public static String uploadSpecSuffixFileToServerReturnUrl(String url, String suffix, ByteArrayOutputStream pos) {
        try (ByteArrayInputStream pis = new ByteArrayInputStream(pos.toByteArray())) {
           return uploadSpecSuffixFileToServerReturnUrl(url,suffix,pis);
        }catch (Exception e){
            Log4j.error("EXCEL写入文件服务器并获取下载地址错误",e);
        }
        return "";
    }



    public static UploadFileItem uploadImageGetUploadItem(String url, String serverFilePath, String fileName,
                                                   boolean isCompress,boolean isLogo,boolean isThumb,
                                                   InputStream fio){
        try {
            String saveDir = serverFilePath==null? "/temp/img/" : serverFilePath;
            //上传文件
            String json = uploadImage(url, saveDir,fileName,isCompress,isLogo,isThumb, fio);
            if (StringUtil.isEmpty(json) || json.contains("java.net.SocketTimeoutException")) return null;

            ServletResult<List<UploadFileItem>> servletResult =
                    GoogleGsonUtil.jsonToJavaBean(json,new TypeToken<ServletResult<List<UploadFileItem>>>(){}.getType());

            if(servletResult!=null && servletResult.code==200 && servletResult.data.size()>0){
                return servletResult.data.get(0);
            }
        } catch (Exception e) {
            Log4j.error("文件上传错误 " + serverFilePath + "/" +fileName ,e);
        }
        return null;
    }

    public static String uploadImageGetDownloadURL(String url, String serverFilePath, String fileName,
                                                                 boolean isCompress,boolean isLogo,boolean isThumb,
                                                                 InputStream fio){
        try {
            UploadFileItem item = uploadImageGetUploadItem(url,serverFilePath,fileName,isCompress,isLogo,isThumb,fio);
            if(item!=null) return item.httpUrl;
        } catch (Exception e) {
            Log4j.error("文件上传错误 " + serverFilePath + "/" +fileName ,e);
        }
        return null;
    }

    public static void main(String[] args) {
        String url = "http://localhost:8080/upload";
        try(FileInputStream fos = new FileInputStream(new File("C:\\Users\\Administrator\\Pictures\\3.jpg"))){
            FileServerClient.uploadImage(url,"李世平","1.jpg",false,false,false,fos);
        }catch (Exception e){
            e.printStackTrace();
        }
    }


}
