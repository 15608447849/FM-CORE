package bottle.util;



import java.io.File;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.*;

/**
 * 配合文件服务器使用
 * lzp
 */
public class HttpRequest extends HttpUtil.CallbackAbs  {

    private String text;

    private int timeOut = 30 * 1000;

    public HttpRequest setTimeOut(int timeOut){
        this.timeOut = timeOut;
        return this;
    }

    public HttpRequest bindParam(StringBuffer sb,Map<String,String > map){
        Iterator<Map.Entry<String,String>> it = map.entrySet().iterator();
        Map.Entry<String,String> entry ;

        while (it.hasNext()) {
            entry = it.next();
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("&");
        }
        sb.deleteCharAt(sb.length()-1);
        return accessUrl(sb.toString());
    }


    public static String mapToHttpBody(String url,String type,Map<?,?> map){
        return HttpUtil.contentToHttpBody(url,type,GoogleGsonUtil.javaBeanToJson(map));
    }


    public HttpRequest accessUrl(String url){ ;
        new HttpUtil.Request(url,this)
                .setReadTimeout(timeOut)
                .setConnectTimeout(timeOut)
                .text()
                .execute();
        return this;
    }

    private List<String> pathList = new ArrayList<>();
    private List<String> nameList = new ArrayList<>();
    private List<String> imageSizeList = new ArrayList<>();
    private List<HttpUtil.FormItem> formItems = new ArrayList<>();

    /**
     * 上传文件
     */
    public HttpRequest addFile(File file, String remotePath, String remoteFileName){
        if (remotePath==null) remotePath = "/java/";
        if (remoteFileName==null) remoteFileName = file.getName();
        pathList.add(remotePath);
        nameList.add(remoteFileName);
        formItems.add(new HttpUtil.FormItem("file", file.getName(), file));
        return this;
    }

    /**
     * 上传的文件设置大小
     */
    public HttpRequest addImageSize(String... sizes){
        imageSizeList.add(String.join(",",sizes));
        return this;
    }


    /**
     * 上传流
     */
    public HttpRequest addStream(InputStream stream, String remotePath, String remoteFileName){
        if (remotePath==null) remotePath = "/java/";
        if (remoteFileName==null) throw new NullPointerException("需要上传的远程文件名不可以为空");
        pathList.add(remotePath);
        nameList.add(remoteFileName);
        formItems.add(new HttpUtil.FormItem("file", remoteFileName, stream));
        return this;
    }

    private static String _join(List<String> list, String separator) {
        StringBuilder sb = new StringBuilder();
        for (String str : list){
            sb.append(str).append(separator);
        }
        sb.deleteCharAt(sb.length() - 1);
        try {
            return URLEncoder.encode(sb.toString(),"UTF-8");
        } catch (UnsupportedEncodingException e) {
            return sb.toString();
        }
    }

    private boolean isLogo;
    public HttpRequest setLogo(boolean f){
        this.isLogo = f;
        return this;
    }

    private boolean isCompress;
    public HttpRequest setCompress(boolean f){
        this.isCompress= f;
        return this;
    }

    private long compressLimitSize;
    public HttpRequest setCompressLimitSieze(long size){
        this.compressLimitSize= size;
        return this;
    }

    private boolean isThumb = true;
    public HttpRequest setThumb(boolean thumb) {
        isThumb = thumb;
        return this;
    }

    /**
     * 执行表单文件上传
     */
    public HttpRequest fileUploadUrl(String url){
        if (formItems != null && formItems.size() > 0){
            HashMap<String,String> headParams = new HashMap<>();
            headParams.put("specify-path", _join(pathList,";"));
            headParams.put("specify-filename", _join(nameList,";"));
            if(imageSizeList.size() > 0) headParams.put("tailor-list", _join(imageSizeList,";"));
            if (isThumb) headParams.put("image-min-exist","1");//图片最小比例缩略图

            if (isLogo) headParams.put("image-logo", "0");//水印
            if (isCompress) headParams.put("image-compress","0");//图片压缩
            if (compressLimitSize>0) headParams.put("image-compress-size",compressLimitSize+"");//图片压缩至少到多少阔值

            new HttpUtil.Request(url, HttpUtil.Request.POST, this)
                    .setFileFormSubmit()
                    .setParams(headParams)
                    .addFormItemList(formItems)
                    .upload().execute();
        }
        return this;
    }


    /**
     * 检查指定文件是否存在
     */
    public HttpRequest existTargetFile(String url, String filePath){
        HashMap<String,String> headParams = new HashMap<>();
        headParams.put("specify-file",filePath);
        new HttpUtil.Request(url, HttpUtil.Request.POST, this)
                .setParams(headParams)
                .setReadTimeout(timeOut)
                .setConnectTimeout(timeOut)
                .text().execute();
        return this;
    }

    /**
     * 获取文件列表
     */
    public HttpRequest getTargetDirFileList(String url, String dirPath, boolean isSub){
        HashMap<String,String> headParams = new HashMap<>();
        headParams.put("specify-path",dirPath);
        headParams.put("ergodic-sub",isSub+"");
        new HttpUtil.Request(url, HttpUtil.Request.POST, this)
                .setParams(headParams)
                .setReadTimeout(timeOut).
                setConnectTimeout(timeOut)
                .text()
                .execute();
        return this;
    }

    /**
     * 获取批量下载zip路径
     * ps: 前端传递参数需要注意对参数进行 URLEncode 处理中文及空格等
     */
    public HttpRequest getBatchDownloadFileZipUrl(String url, List<String> filePath){
        HashMap<String,String> headParams = new HashMap<>();
        headParams.put("path-list", _join(filePath,";"));
        new HttpUtil.Request(url, HttpUtil.Request.POST, this)
                .setParams(headParams)
                .setReadTimeout(timeOut).setConnectTimeout(timeOut)
                .text().execute();
        return this;
    }

    //获取返回的文本信息
    public String getRespondContent(){
        return text;
    }

    //删除文件
    public HttpRequest deleteFile(String url,String ...fileItem) throws UnsupportedEncodingException{

            if (fileItem != null && fileItem.length > 0) {
                HashMap<String,String> headParams = new HashMap<>();
                String deleteItem = GoogleGsonUtil.javaBeanToJson(fileItem);
                if (deleteItem!=null){
                    headParams.put("delete-list", URLEncoder.encode(deleteItem,"UTF-8"));
                    new HttpUtil.Request(url, HttpUtil.Request.POST, this)
                            .setParams(headParams)
                            .setReadTimeout(timeOut)
                            .setConnectTimeout(timeOut)
                            .text().execute();
                }
            }
        return this;
    }


    @Override
    public void onResult(HttpUtil.Response response) {
        this.text = response.getMessage();
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
        this.text = e.toString();
    }

}
