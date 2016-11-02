package com.example.user.myapplication.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadSampleListener;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.util.FileDownloadUtils;

import java.io.File;

/**
 * 文件下载帮助类
 * Created by user on 2016/10/31.
 */

public class DownLoadFileUtils {
    private Context context;
    private String url;
    private String fileType;
    private int downLoadTask;
    public static  final  String fileDir= Environment.getExternalStorageDirectory().toString()+"/MyApplication/";
    private String TAG = "DownLoadFileUtils";
    private String path = null;

    private String fileFinalPath=null;


    /**
     * 初始化方式一
     * @param context
     * @param url
     * @param fileType
     */
    public DownLoadFileUtils(Context context, String url, String fileType, String fileName) {
        this.context = context;
        this.url = url;
        this.fileType = fileType;
        File mainDir=new File(fileDir);
        if(!mainDir.exists()){
            mainDir.mkdirs();
        }
        fileFinalPath=fileDir+fileName;
     //   file = DemoApplication.getDefaultSaveRootPath() + File.separator + FileName + File.separator + fileName;
    }

//    /**
//     * 初始化方式二 配合开始方式二使用
//     * @param context
//     */
//    public DownLoadFileUtils(Context context) {
//        this.context = context;
//        file = DemoApplication.getDefaultSaveRootPath() + File.separator + FileName + File.separator + System.currentTimeMillis();
//    }
//
//    /**
//     * 开始方式二 配合初始化方式二使用
//     * @param callBack
//     * @param url
//     * @param fileType
//     */
//    public void start(DownLoadCallBack callBack, String url, String fileType) {
//        this.url = url;
//        this.fileType = fileType;
//        //通过网络下载文件并通过不同的type转存到SDCARD的自定义文件下,并重命名
//        downLoadTask = createDownloadTask(callBack).start();
//    }

    /**
     * 开始方式一
     * @param callBack
     */
    public void start(DownLoadCallBack callBack) {
        //通过网络下载文件并通过不同的type转存到SDCARD的自定义文件下,并重命名
        downLoadTask = createDownloadTask(callBack).start();
    }

    /**
     * 暂停
     */
    public void stop() {
        FileDownloader.getImpl().pause(downLoadTask);
    }

    /**
     * 打开文件
     */
    public void open() {
        if (fileIsExists(path) == false){
            Toast.makeText(context, "查看文件不存在", Toast.LENGTH_SHORT).show();
        }else{
            openFile(new File(this.path),context);
        }
    }
    /**
     * 删除文件
     */
    public void delete() {
        if (path !=null){
            new File(path).delete();
            new File(FileDownloadUtils.getTempPath(path)).delete();
        }else{
            Toast.makeText(context, "文件地址不存在", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * 创建下载线程
     * @param callBack
     * @return
     */
    private BaseDownloadTask createDownloadTask(final DownLoadCallBack callBack) {
        boolean isDir = false;

        if (fileType.contains(".") || fileType.equals(".")){
            path= fileFinalPath+fileType;
            Log.d(TAG,"走了包含\".\"");
        }else{
            path = fileFinalPath +"."+fileType;
            Log.d(TAG,"走了不包含\".\"");
        }
        return FileDownloader.getImpl().create(url)
                .setPath(path, isDir)
                .setCallbackProgressTimes(300)
                .setMinIntervalUpdateSpeed(400)
//                .setTag(tag)
                .setListener(new FileDownloadSampleListener() {

                    @Override
                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        super.pending(task, soFarBytes, totalBytes);
                    }

                    @Override
                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        super.progress(task, soFarBytes, totalBytes);
                        callBack.onProgress(task,soFarBytes,totalBytes);
                    }

                    @Override
                    protected void error(BaseDownloadTask task, Throwable e) {
                        super.error(task, e);
                        callBack.onFiled(e.getMessage().toString());
                    }

                    @Override
                    protected void connected(BaseDownloadTask task, String etag, boolean isContinue, int soFarBytes, int totalBytes) {
                        super.connected(task, etag, isContinue, soFarBytes, totalBytes);
                        Log.d(TAG, "开始下载,总大小:" + totalBytes + ",已下载:" + soFarBytes + ",速度:" + task.getSpeed());
//                        Toast.makeText(context, "开始下载,总大小:" + totalBytes, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        super.paused(task, soFarBytes, totalBytes);
                        callBack.onPaused(task, soFarBytes, totalBytes);
                        Log.d(TAG, "已暂停,总大小:" + totalBytes + ",已下载:" + soFarBytes + ",速度:" + task.getSpeed());
//                        Toast.makeText(context, "已暂停,总大小:" + totalBytes + ",已下载:" + soFarBytes + ",速度:" + task.getSpeed(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    protected void completed(BaseDownloadTask task) {
                        super.completed(task);
                        callBack.onSuccess(path);
                    }

                    @Override
                    protected void warn(BaseDownloadTask task) {
                        super.warn(task);
                    }
                });
    }

    public interface DownLoadCallBack {
        void onSuccess(String filePath);
        void onPaused(BaseDownloadTask task, int soFarBytes, int totalBytes);
        void onFiled(String error);
        void onProgress(BaseDownloadTask task, int soFarBytes, int totalBytes);
    }

    /**
     * 打开文件
     *
     * @param file
     */
    public static final void openFile(File file,Context _context) {
        try{
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        //设置intent的Action属性
        intent.setAction(Intent.ACTION_VIEW);
        //获取文件file的MIME类型
        String type = getMIMEType(file);
            Log.d("type--->","type:"+type);
        //设置intent的data和Type属性。
        intent.setDataAndType(/*uri*/Uri.fromFile(file), type);
        //跳转
            _context.startActivity(intent);     //这里最好try一下，有可能会报错。 //比如说你的MIME类型是打开邮箱，但是你手机里面没装邮箱客户端，就会报错。
        }catch(Exception e){
         Log.d("type--->","报错:"+e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 根据文件后缀名获得对应的MIME类型。
     *
     * @param file
     */
    public static final String getMIMEType(File file) {

        String type = "*/*";
        String fName = file.getName();
        //获取后缀名前的分隔符"."在fName中的位置。
        int dotIndex = fName.lastIndexOf(".");
        if (dotIndex < 0) {
            return type;
        }
    /* 获取文件的后缀名*/
        String end = fName.substring(dotIndex, fName.length()).toLowerCase();
        if (end == "") return type;
        //在MIME和文件类型的匹配表中找到对应的MIME类型。
        for (int i = 0; i < MIME_MapTable.length; i++) { //MIME_MapTable??在这里你一定有疑问，这个MIME_MapTable是什么？
            if (end.equals(MIME_MapTable[i][0]))
                type = MIME_MapTable[i][1];
        }
        return type;
    }

    public static final String[][] MIME_MapTable = {
            //{后缀名，MIME类型}
            {".3gp", "video/3gpp"},
            {".apk", "application/vnd.android.package-archive"},
            {".asf", "video/x-ms-asf"},
            {".avi", "video/x-msvideo"},
            {".bin", "application/octet-stream"},
            {".bmp", "image/bmp"},
            {".c", "text/plain"},
            {".class", "application/octet-stream"},
            {".conf", "text/plain"},
            {".cpp", "text/plain"},
            {".doc", "application/msword"},
            {".docx", "application/vnd.openxmlformats-officedocument.wordprocessingml.document"},
            {".xls", "application/vnd.ms-excel"},
            {".xlsx", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"},
            {".exe", "application/octet-stream"},
            {".gif", "image/gif"},
            {".gtar", "application/x-gtar"},
            {".gz", "application/x-gzip"},
            {".h", "text/plain"},
            {".htm", "text/html"},
            {".html", "text/html"},
            {".jar", "application/java-archive"},
            {".java", "text/plain"},
            {".jpeg", "image/jpeg"},
            {".jpg", "image/jpeg"},
            {".js", "application/x-javascript"},
            {".log", "text/plain"},
            {".m3u", "audio/x-mpegurl"},
            {".m4a", "audio/mp4a-latm"},
            {".m4b", "audio/mp4a-latm"},
            {".m4p", "audio/mp4a-latm"},
            {".m4u", "video/vnd.mpegurl"},
            {".m4v", "video/x-m4v"},
            {".mov", "video/quicktime"},
            {".mp2", "audio/x-mpeg"},
            {".mp3", "audio/x-mpeg"},
            {".mp4", "video/mp4"},
            {".mpc", "application/vnd.mpohun.certificate"},
            {".mpe", "video/mpeg"},
            {".mpeg", "video/mpeg"},
            {".mpg", "video/mpeg"},
            {".mpg4", "video/mp4"},
            {".mpga", "audio/mpeg"},
            {".msg", "application/vnd.ms-outlook"},
            {".ogg", "audio/ogg"},
            {".pdf", "application/pdf"},
            {".png", "image/png"},
            {".pps", "application/vnd.ms-powerpoint"},
            {".ppt", "application/vnd.ms-powerpoint"},
            {".pptx", "application/vnd.openxmlformats-officedocument.presentationml.presentation"},
            {".prop", "text/plain"},
            {".rc", "text/plain"},
            {".rmvb", "audio/x-pn-realaudio"},
            {".rtf", "application/rtf"},
            {".sh", "text/plain"},
            {".tar", "application/x-tar"},
            {".tgz", "application/x-compressed"},
            {".txt", "text/plain"},
            {".wav", "audio/x-wav"},
            {".wma", "audio/x-ms-wma"},
            {".wmv", "audio/x-ms-wmv"},
            {".wps", "application/vnd.ms-works"},
            {".xml", "text/plain"},
            {".z", "application/x-compress"},
            {".zip", "application/x-zip-compressed"},
            {"", "*/*"}
    };
    public boolean fileIsExists(String file){
        File filepath = new File(file);
        try{
            if(!filepath.exists()){
                return false;
            }

        }catch (Exception e) {
            Log.d(TAG,"fileIsExists:"+e.getMessage());
            return false;
        }
        return true;
    }
}
