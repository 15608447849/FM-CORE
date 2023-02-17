package bottle.tcps.ftc.server;

import bottle.tcps.FTCLog;
import bottle.tcps.ftc.imps.FTCProtocol;
import bottle.tcps.ftc.slice.SliceInfo;
import bottle.tcps.ftc.slice.SliceMapper2;
import bottle.tcps.ftc.slice.SliceUtil;
import bottle.tcps.aio.p.FtcTcpActionsAdapter;
import bottle.tcps.aio.p.Session;
import bottle.util.EncryptUtil;
import bottle.util.FileTool;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.*;

/**
 * Created by user on 2017/11/23.
 */
public class FileUpServerHandle extends FtcTcpActionsAdapter {
    // 执行中的文件的MD5值
    private final static HashSet<String> executingSet = new HashSet<>();

    private static final String SUFFER = ".bup";
    private final Gson gson = new Gson();
    private final String directory;
    private RandomAccessFile randomAccessFile;
    private boolean is_diff_translate = false;//是否差异传输
    private long diff_capacity = 0;//差异传输 当前差异块应接收的数据总量
    private int diff_index = 0;//差异传输 - 当前差异块下标
    private List<SliceMapper2> diff_remote_slice_list = null;//差异传输 客户端传输过来的差异块
    private List<SliceMapper2> diff_local_slice_list = null;//差异传输 当前本地存在的相同数据块

    FileUpServerHandle(String directory) {
        this.directory = directory;
    }

    @Override
    public void connectSucceed(Session session) {
        try {
            FTCLog.info("[服务端] 连接建立成功 >> "+ session.getSocket().getLocalAddress() +" <---> " + session.getSocket().getRemoteAddress());
        } catch (IOException e) {
            e.printStackTrace();
        }
        session.getSocketImp().setAction(this);//绑定传输管道
        session.getSocketImp().getSession().getOperation().writeString("start");//通知客户端可以连接已建立
    }

    @Override
    public void receiveString(Session session, String message) {
//        FTCLog.info("收到字符串: \n"+message);
        try {
            Map<String,String> map = gson.fromJson(message,new TypeToken<Map<String, String>>(){}.getType());
            handle(session,map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connectClosed(Session session) {
        super.connectClosed(session);
        synchronized (executingSet){
            executingSet.remove(remoteMd5);
        }
        closeResource();
    }

    /**
     * 处理
     */
    private void handle(Session session,Map<String, String> map) {
       String protocol = map.get("protocol");
//        System.out.println("服务端收到协议: " + protocol);
        switch (protocol) {
            case FTCProtocol.C_FILE_BACKUP_QUEST:
                backupRequest(session, map);
                break;
            case FTCProtocol.C_FILE_BACKUP_TRS_START:
                receiveFileStart(session, map);
                break;
            case FTCProtocol.C_FILE_BACKUP_TRS_END:
                receiveFileEnd(session, map);
                break;
        }
    }

    private String remoteMd5;
    /**
     * 客户端文件同步请求
     * @param map
     */
    private void backupRequest(Session  session, Map<String, String> map)  {
        String slice = "Node";
        map.put("protocol", FTCProtocol.S_FILE_BACKUP_QUEST_ACK);//响应客户端

        remoteMd5 = map.get("md5");

        if (executingSet.contains(remoteMd5)){
            //正在执行中
            map.put("protocol", FTCProtocol.S_FILE_BACKUP_TRS_OVER);
        }else{
            String dir_path = directory + map.get("path");
            File dir_file = new File(dir_path);
            if (!dir_file.exists()){
                //目录不存在,创建目录
                boolean isCreateDirSuccess = dir_file.mkdirs();
                if (!isCreateDirSuccess){
                    FTCLog.info("服务端无法创建文件路径,"+dir_file);
                    map.put("protocol", FTCProtocol.S_FILE_BACKUP_TRS_OVER);
                }
            }else{
                String file_name = map.get("filename");
                if (file_name.endsWith(SUFFER)){
                    //此文件后缀不进行同步
                    map.put("protocol", FTCProtocol.S_FILE_BACKUP_TRS_OVER);
                }else{
                    File file = new File(dir_path+file_name);
                    if (file.exists()){
                        try {

                            long length =Long.parseLong(map.get("length"));
                            if (file.length()==length){
                                String localMd5 = EncryptUtil.getFileMd5ByString(file);
                                if (localMd5.equals(remoteMd5)){
                                    //相同文件 不需要传输
                                    map.put("protocol", FTCProtocol.S_FILE_BACKUP_TRS_OVER);

                                }else{
                                    //切片
                                    int sliceSize = Integer.parseInt(map.get("block"));
                                    ArrayList<SliceInfo> arrayList = SliceUtil.fileSliceInfoList(file,sliceSize);
                                    if (arrayList!=null){
                                        slice = gson.toJson(arrayList);
                                    }
                                }
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }
            }

        }



        map.put("slice",slice);
        session.getOperation().writeString(gson.toJson(map));
//        System.out.println(">>> "+ map);
    }

    //关闭资源
    private void closeResource() {
        if (randomAccessFile!=null){
            try {
                randomAccessFile.close();
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                randomAccessFile= null;
            }
        }

        is_diff_translate = false;
        diff_index = 0;
        diff_capacity =0;
        if (diff_remote_slice_list !=null) {
            diff_remote_slice_list.clear();
            diff_remote_slice_list = null;
        }
        if (diff_local_slice_list !=null){
            diff_local_slice_list.clear();
            diff_local_slice_list =null;
        }
    }

    //接受客户端发送的传输开始请求
    private void receiveFileStart(Session session, Map<String, String> map) {
//        System.out.println(map + " randomAccessFile " + randomAccessFile);

        boolean isAllowExecute;
        synchronized (executingSet){
            isAllowExecute = executingSet.add(remoteMd5);
        }
        if (!isAllowExecute){
            map.put("protocol", FTCProtocol.S_FILE_BACKUP_TRS_OVER);
            session.getOperation().writeString(gson.toJson(map));
            return;
        }

        closeResource();//关闭资源
        String fs_path = directory + map.get("path")+map.get("filename") + SUFFER; //获取备份后缀的文件
        long fileTotal = Long.valueOf(map.remove("length")); //获取文件大小

        if (FileTool.checkFile(fs_path)){
            //存在一个备份文件
            if (!FileTool.deleteFile(fs_path)){
                //无法删除
                map.put("protocol", FTCProtocol.S_FILE_BACKUP_TRS_OVER);
                session.getOperation().writeString(gson.toJson(map));
                return;
            }
        }

        String trs_type = map.remove("translate");//传输类型 增量或者全部

        if(trs_type.equals("diff")){
            //增量传输
            is_diff_translate = true;
            diff_index = 0;
            String diff_block_str = map.remove("different");
            String[] arr = diff_block_str.split("#");
            String[] arr_sub;
            SliceMapper2 slice;
            diff_remote_slice_list = new ArrayList<>();
            for (String str:arr){
                arr_sub = str.split("-");
                slice = new SliceMapper2();
                slice.position = Long.parseLong(arr_sub[0]);
                slice.length = Long.parseLong(arr_sub[1]);
                diff_remote_slice_list.add(slice);
            }
            String same_block_str = map.remove("same");
            arr = same_block_str.split("#");
            diff_local_slice_list = new ArrayList<>();
            for (String str:arr){
                arr_sub = str.split("-");
                slice = new SliceMapper2();
                slice.position = Long.parseLong(arr_sub[0]);
                slice.length = Long.parseLong(arr_sub[1]);
                slice.mapperPosition = Long.parseLong(arr_sub[2]);
                diff_local_slice_list.add(slice);
            }
        }

        try {
            randomAccessFile = new RandomAccessFile(fs_path,"rw");
            randomAccessFile.setLength(fileTotal);

            if (trs_type.equals("all")){ //全量
                randomAccessFile.seek(0);
            }else{ //增量
                randomAccessFile.seek(diff_remote_slice_list.get(diff_index).position);
            }

            //告知客户端传输流开始
            map.put("protocol", FTCProtocol.S_FILE_BACKUP_NOTIFY_STREAM);
            session.getOperation().writeString(gson.toJson(map));

        } catch (Exception e) {
            //直接结束传输-错误
            map.put("protocol", FTCProtocol.S_FILE_BACKUP_TRS_OVER);
            session.getOperation().writeString(gson.toJson(map));
        }
    }

    //接受完成 (客户端通知 文件传输完成)
    private void receiveFileEnd(Session session, Map<String, String> map) {
        synchronized (executingSet){
            executingSet.remove(remoteMd5);
        }

//        System.out.println(map +" randomAccessFile " + randomAccessFile);
        if (randomAccessFile!=null){
            try {
                //获取备份后缀的文件
                String fs_path = directory+map.remove("path")+map.remove("filename");
                if (is_diff_translate){
                    RandomAccessFile localSrcBlock = null;
                    try {
                        //复制 文件块
                        localSrcBlock = new RandomAccessFile(fs_path, "r");

                        byte[] buf = new byte[Integer.parseInt(map.remove("block"))];

                        for (SliceMapper2 slice : diff_local_slice_list) {
                            localSrcBlock.seek(slice.mapperPosition);
                            localSrcBlock.read(buf, 0, (int) slice.length);
                            randomAccessFile.seek(slice.position);
                            randomAccessFile.write(buf, 0, (int) slice.length);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }finally {
                        try {
                            if (localSrcBlock!=null) localSrcBlock.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
                closeResource();
                if (FileTool.rename(new File(fs_path+ SUFFER), new File(fs_path))){
                    FileTool.deleteFile(fs_path+ SUFFER);
                }
                FTCLog.info("传输-完成文件 "+ fs_path);
            } catch (Exception ignored) { }
        }

        map.put("protocol", FTCProtocol.S_FILE_BACKUP_TRS_OVER);
        session.getOperation().writeString(gson.toJson(map));

    }

    @Override
    public void receiveBytes(Session session, byte[] bytes) {

//        System.out.println("收到字节数:"+receiveFileTotal);
            if (randomAccessFile!=null){
                try {
                    randomAccessFile.write(bytes);
                    // 增量传输
                    if (is_diff_translate){ //如果差异传输 - 特别处理
                        diff_capacity += bytes.length;
                        if (diff_capacity == diff_remote_slice_list.get(diff_index).length){
                            diff_capacity = 0;
                            diff_index++;
                            if (diff_index < diff_remote_slice_list.size()){
                                randomAccessFile.seek(diff_remote_slice_list.get(diff_index).position);
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
    }

}
