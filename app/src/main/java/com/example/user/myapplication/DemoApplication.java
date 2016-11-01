package com.example.user.myapplication;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.support.multidex.MultiDex;
import android.util.DisplayMetrics;

import com.example.user.myapplication.utils.OkHttpManager;
import com.liulishuo.filedownloader.FileDownloader;
import com.liulishuo.filedownloader.util.FileDownloadHelper;
import com.liulishuo.filedownloader.util.FileDownloadLog;
import java.net.Proxy;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;

/**
 * Created by user on 2016/11/1.
 */

public class DemoApplication extends Application {
    private List<Activity> activities = new ArrayList<>();
    public static Context applicationContext;
    private static DemoApplication instance;
    private DisplayMetrics displayMetrics = null;
    //测试下载地址
    public static final String TEST_DOWNLOAD ="http://cdn.llsapp.com/android/LLS-v4.0-595-20160908-143200.apk";
    //    public static final String TEST_DOWNLOAD = "http://www.liguda.com/wp-content/uploads/2015/08/7.jpg";
    public static DemoApplication getApp() {
        if (instance != null && instance instanceof DemoApplication) {
            return (DemoApplication) instance;
        } else {
            instance = new DemoApplication();
            instance.onCreate();
            return (DemoApplication) instance;
        }
    }

    public static boolean sRunningOnIceCreamSandwich;

    static {
        sRunningOnIceCreamSandwich = Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH;
    }

    private String groupId = null;

    @Override
    public void onCreate() {
        MultiDex.install(this);
        super.onCreate();
        applicationContext = this;
        instance = this;
        OkHttpManager.init(instance);
        FileDownloadLog.NEED_LOG = BuildConfig.DEBUG;
        /**
         * just for cache Application's Context, and ':filedownloader' progress will NOT be launched
         * by below code, so please do not worry about performance.
         * @see FileDownloader#init(Context)
         */
        FileDownloader.init(getApplicationContext(),
                new FileDownloadHelper.OkHttpClientCustomMaker() { // is not has to provide.
                    @Override
                    public OkHttpClient customMake() {
                        // just for OkHttpClient customize.
                        final OkHttpClient.Builder builder = new OkHttpClient.Builder();
                        // you can set the connection timeout.
                        builder.connectTimeout(15_000, TimeUnit.MILLISECONDS);
                        // you can set the HTTP proxy.
                        builder.proxy(Proxy.NO_PROXY);
                        // etc.
                        return builder.build();
                    }
                });
    }

    public static DemoApplication getInstance() {
        return instance;
    }

    public static int getRandomStreamId() {
        Random random = new Random();
        int randint = (int) Math.floor((random.nextDouble() * 10000.0 + 10000.0));
        return randint;
    }

    public float getScreenDensity() {
        if (this.displayMetrics == null) {
            setDisplayMetrics(getResources().getDisplayMetrics());
        }
        return this.displayMetrics.density;
    }

    public int getScreenHeight() {
        if (this.displayMetrics == null) {
            setDisplayMetrics(getResources().getDisplayMetrics());
        }
        return this.displayMetrics.heightPixels;
    }

    public int getScreenWidth() {
        if (this.displayMetrics == null) {
            setDisplayMetrics(getResources().getDisplayMetrics());
        }
        return this.displayMetrics.widthPixels;
    }

    public void setDisplayMetrics(DisplayMetrics DisplayMetrics) {
        this.displayMetrics = DisplayMetrics;
    }

    public int dp2px(float f) {
        return (int) (0.5F + f * getScreenDensity());
    }

    public int px2dp(float pxValue) {
        return (int) (pxValue / getScreenDensity() + 0.5f);
    }

    //获取应用的data/data/....File目录
    public String getFilesDirPath() {
        return getFilesDir().getAbsolutePath();
    }

    //获取应用的data/data/....Cache目录
    public String getCacheDirPath() {
        return getCacheDir().getAbsolutePath();
    }

    public void saveActivity(Activity activity) {
        if (activity != null) {
            activities.add(activity);
        }
    }

    public void finishActivities() {
        for (Activity activity : activities) {
            if (activity != null && !activity.isFinishing()) {
                activity.finish();
            }
        }
    }
    public static String getDefaultSaveRootPath() {

        if (DemoApplication.getApp().getExternalCacheDir() == null) {
            return Environment.getDownloadCacheDirectory().getAbsolutePath();
        } else {
            //noinspection ConstantConditions
            return DemoApplication.getApp().getExternalCacheDir().getAbsolutePath();
        }
    }
}
