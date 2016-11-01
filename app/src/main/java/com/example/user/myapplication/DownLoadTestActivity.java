package com.example.user.myapplication;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.user.myapplication.utils.DownLoadFileUtils;
import com.liulishuo.filedownloader.BaseDownloadTask;

/**
 * Created by user on 2016/11/1.
 */

public class DownLoadTestActivity extends AppCompatActivity {
    private String FilePath = null;
    private DownLoadFileUtils utils;
    private ProgressBar progress_bar;
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.test_download_activity);

        utils = new DownLoadFileUtils(this, DemoApplication.TEST_DOWNLOAD,".apk");
        Button img_btn_download = (Button) findViewById(R.id.img_btn_download);
        Button img_btn_stop = (Button) findViewById(R.id.img_btn_stop);
        final Button img_btn_delete = (Button) findViewById(R.id.img_btn_delete);
        final Button img_btn_open = (Button) findViewById(R.id.img_btn_open);

        progress_bar = (ProgressBar)findViewById(R.id.progress_bar);


        img_btn_open.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                utils.open();
            }
        });
        img_btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                utils.delete();
            }
        });
        img_btn_stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                utils.stop();
            }
        });
        img_btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                utils.start(new DownLoadFileUtils.DownLoadCallBack() {
                    @Override
                    public void onSuccess(String filePath) {
                        img_btn_delete.setVisibility(View.VISIBLE);
                        img_btn_open.setVisibility(View.VISIBLE);
                        Toast.makeText(DownLoadTestActivity.this, "filePath:"+filePath, Toast.LENGTH_SHORT).show();
                        FilePath = filePath;
                        Log.d("slj","FilePath:"+FilePath);
                    }

                    @Override
                    public void onPaused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        Toast.makeText(DownLoadTestActivity.this, "已暂停,总大小:" + totalBytes + ",已下载:" + soFarBytes + ",速度:" + task.getSpeed(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFiled(String error) {
                        Toast.makeText(DownLoadTestActivity.this, "error:"+error, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onProgress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                        progress_bar.setMax(totalBytes);
                        progress_bar.setProgress(soFarBytes);
//                        Toast.makeText(DownLoadTestActivity.this, "开始下载,总大小:" + totalBytes, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (utils!=null){
            utils.stop();
        }
    }
}
