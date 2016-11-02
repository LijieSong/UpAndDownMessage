package com.example.user.myapplication.utils;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.Process;
import android.util.Log;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

/**
 * Crash异常拦截器
 * 异常拦截器 捕获全局异常并上传至服务器
 * create by songlijie 2016-11-2
 */
public class CrashHandler implements UncaughtExceptionHandler {
    public static final String TAG = "CrashHandler";

    private static CrashHandler instance;
    private Context context;
    private String errorLogDir = null;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HH:mm:ss");
    /**
     * 错误报告文件的扩展名
     */
    private static final String CRASH_REPORTER_EXTENSION = ".txt";
    private static final String CRASH_FILE_NAME = "Error";

    private final int expirationDate = 7;//设置过期时间(单位：天)
    private File file;//保存的文件路径

    private CrashHandler() {
    }

    public synchronized static CrashHandler getInstance() {
        if (instance == null) {
            instance = new CrashHandler();
        }
        return instance;
    }
    public void init(Context context) {
        init(context,getDefaultSaveRootPath(context));
    }
    public void init(Context context, String errorLogDir) {
        this.context = context;
        this.errorLogDir = errorLogDir;
        Thread.setDefaultUncaughtExceptionHandler(this);
        //删除过期文件
        new Thread(new Runnable() {
            @Override
            public void run() {
                deleteExpirationFile();
            }
        }).start();
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        if (ex != null) {
            //收集设备信息
            disposeThrowable(ex);
            ex.printStackTrace();
        }
    }

    private void disposeThrowable(Throwable ex) {
        getInfo();
        String mtype = android.os.Build.MODEL; // 手机型号
        String mtyb = android.os.Build.BRAND;//手机品牌
        String cpuAbi = android.os.Build.CPU_ABI;//cpu架构
        String sdk = android.os.Build.VERSION.SDK;//sdk版本号
        String release = android.os.Build.VERSION.RELEASE;//依赖的系统版本号
        String manufacturer = android.os.Build.MANUFACTURER;//手机品牌
        StringBuffer sb = new StringBuffer();
        sb.append("手机系统信息:" + "\n" + "手机品牌:" + mtyb + "\n" + "手机型号:" + mtype + "\n" + "制造商:" + manufacturer + "\n" + "手机系统版本号:" + release + "\n" + "系统SDK版本号:" + sdk + "\n" + "cpu架构:" + cpuAbi + "\n");
        sb.append("错误信息:" +"\n"+ ex.toString() + "\n");
        sb.append("错误所在:" +"\n");
        StackTraceElement[] steArray = ex.getStackTrace();
        for (StackTraceElement ste : steArray) {
            sb.append( "System.err at " + ste.getClassName() + "." + ste.getMethodName() + "(" + ste.getFileName() + ":" + ste.getLineNumber() + ")\n");
        }
        if (errorLogDir != null) {
            try {
                file = new File(errorLogDir + File.separator + CRASH_FILE_NAME + File.separator + sdf.format(System.currentTimeMillis()) + CRASH_REPORTER_EXTENSION);
                if (!file.exists()) {
                    file.getParentFile().mkdirs();
                    file.createNewFile();
                    Log.d(TAG, "chuangjianfile");
                }
                FileOutputStream fos = new FileOutputStream(file);
                fos.write(sb.toString().getBytes("utf-8"));
                fos.flush();
                fos.close();
                Log.d(TAG, "xie如成功");
                fos = null;
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, "errorLogDir:" + errorLogDir + "Exception:" + e.getMessage());
            }
        }
        Process.killProcess(Process.myPid());
        System.exit(10);
    }

    /**
     * 在程序启动时候, 可以调用该函数来发送以前没有发送的报告
     */
    public void sendPreviousReportsToServer() {
        Log.d(TAG, "走了发送异常报告");
        sendCrashReportsToServer();
    }

    /**
     * 把错误报告发送给服务器,包含新产生的和以前没发送的.
     */
    private void sendCrashReportsToServer() {
        String[] crFiles = getCrashReportFiles();
        if (crFiles != null && crFiles.length > 0) {
            TreeSet<String> sortedFiles = new TreeSet<String>();
            sortedFiles.addAll(Arrays.asList(crFiles));
            for (String fileName : sortedFiles) {
                File cr = new File(errorLogDir + File.separator + CRASH_FILE_NAME, fileName);
                Log.d(TAG, "fileName:" + fileName);
                postReport(cr);
                cr.delete();// 删除已发送的报告
            }
        }
    }

    private void postReport(File file) {
        List<Param> params = new ArrayList<>();
        params.add(new Param("crash", "crash"));
        List<File> files = new ArrayList<File>();
        if (file.exists()) {
            files.add(file);
        }
        Log.d(TAG, "走了发送异常报告:" + file.toString());
        // TODO 发送错误报告到服务器
//        new OKHttpUtils(context).post(params, files, FXConstant.HOST, new OKHttpUtils.HttpCallBack() {
//            @Override
//            public void onResponse(JSONObject jsonObject) {
//                Log.d(TAG, "走了发送异常报告:" + jsonObject);
//                Log.d(TAG, "crashjsonObject:" + jsonObject);
//            }
//
//            @Override
//            public void onFailure(String errorMsg) {
//                Log.d(TAG, "crasherrorMsg:" + errorMsg);
//                Log.d(TAG, "没走:" + errorMsg);
//            }
//        });
    }

    /**
     * 获取错误报告文件名
     *
     * @return
     */
    private String[] getCrashReportFiles() {
        File filesDir = new File(errorLogDir + File.separator + CRASH_FILE_NAME);
        FilenameFilter filter = new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(CRASH_REPORTER_EXTENSION);
            }
        };
        return filesDir.list(filter);
    }

    private void deleteExpirationFile() {
        if (errorLogDir == null) {
            return;
        }
        long expirationTime = expirationDate * 24 * 3600 * 1000;
        long nowTime = System.currentTimeMillis();
        List<File> deleteList = new ArrayList<File>();
        File dir = new File(errorLogDir);
        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            for (File f : files) {
                if (f.exists()) {
                    long time = f.lastModified();
                    if (nowTime - time >= expirationTime) {
                        deleteList.add(f);
                    }
                }
            }
            for (File f : deleteList) {
                f.delete();
            }
        }
    }

    /**
     * 获取IMEI号，IESI号，手机型号
     */
    private String getInfo() {
        String mtype = android.os.Build.MODEL; // 手机型号
        String mtyb = android.os.Build.BRAND;//手机品牌
        String cpuAbi = android.os.Build.CPU_ABI;//cpu架构
        String sdk = android.os.Build.VERSION.SDK;//sdk版本号
        String release = android.os.Build.VERSION.RELEASE;//依赖的系统版本号
        String device = android.os.Build.DEVICE;//设备
        String manufacturer = android.os.Build.MANUFACTURER;//手机品牌
        String mobileInfo = "手机品牌：" + mtyb + ",手机型号：" + mtype + ",cpu架构:" + cpuAbi + ",系统SDK版本号:" + sdk + ",手机系统版本号:" + release + ",制造商:" + manufacturer;
        Log.d(TAG, "mobile:" + mobileInfo);
        return mobileInfo;
    }

    /**
     * .获取手机MAC地址
     * 只有手机开启wifi才能获取到mac地址
     */
    private String getMacAddress(Context context) {
        String result = "";
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        result = wifiInfo.getMacAddress();
        return result;
    }

    /**
     * 获取APP文件目录下的cache文件家
     * @return
     */
    private String getDefaultSaveRootPath(Context applicion) {

        if (applicion.getExternalCacheDir() == null) {
            return Environment.getDownloadCacheDirectory().getAbsolutePath();
        } else {
            //noinspection ConstantConditions
            return applicion.getExternalCacheDir().getAbsolutePath();
        }
    }
}