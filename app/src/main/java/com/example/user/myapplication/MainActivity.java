package com.example.user.myapplication;

import android.content.Intent;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private ArrayList<String> imageUrlList = new ArrayList<String>();
    private ArrayList<String> linkUrlArray= new ArrayList<String>();
    private ArrayList<String> titleList= new ArrayList<String>();
    private LinearLayout notice_ll;
    private ViewFlipper notice_vf;
    private int mCurrPos;
    private ImageView iv_notice;//通知图片点击
    private Handler handler = new Handler();
    private Runnable taskRun;//保证是同一个runanble
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initDate();
        initRollNotice();
        //TODO 这是第二种方法 可以减少内存消耗
        taskRun = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(taskRun,3000);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        moveNext();
                        Log.d("slj", "下一个");
                    }
                });
            }
        };
    }

    private void initDate() {
        imageUrlList
                .add("http://b.hiphotos.baidu.com/image/pic/item/d01373f082025aaf95bdf7e4f8edab64034f1a15.jpg");
        imageUrlList
                .add("http://g.hiphotos.baidu.com/image/pic/item/6159252dd42a2834da6660c459b5c9ea14cebf39.jpg");
        imageUrlList
                .add("http://d.hiphotos.baidu.com/image/pic/item/adaf2edda3cc7cd976427f6c3901213fb80e911c.jpg");
        imageUrlList
                .add("http://g.hiphotos.baidu.com/image/pic/item/b3119313b07eca80131de3e6932397dda1448393.jpg");

        linkUrlArray
                .add("http://blog.csdn.net/finddreams/article/details/44301359");
        linkUrlArray
                .add("http://blog.csdn.net/finddreams/article/details/43486527");
        linkUrlArray
                .add("http://blog.csdn.net/finddreams/article/details/44648121");
        linkUrlArray
                .add("http://blog.csdn.net/finddreams/article/details/44619589");
        titleList.add("测试通知1");
        titleList.add("测试通知2");
        titleList.add("测试通知3");
        titleList.add("测试通知4 ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(taskRun,0);
        Log.d("slj","走了resume");
    }

    private void initRollNotice() {
        iv_notice = (ImageView) findViewById(R.id.iv_notice);
        notice_ll = ((LinearLayout) findViewById(R.id.homepage_notice_ll));
        notice_vf = ((ViewFlipper) findViewById(R.id.homepage_notice_vf));

        //TODO 这是第一种方法 消耗内存
//        TimerTask task = new TimerTask() {
//            @Override
//            public void run() {
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        moveNext();
//                        Log.d("Task", "下一个");
//                    }
//                });
//
//            }
//        };
//        Timer timer = new Timer();
//        timer.schedule(task, 0, 3000);

        //TODO 公告图标点击事件
        iv_notice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO do while you want to do
                Snackbar.make(notice_ll,"跳转第二个公告页",Snackbar.LENGTH_SHORT).show();
                startActivity(new Intent(MainActivity.this,Main2Activity.class));
            }
        });
    }

    private void moveNext() {
        setView(this.mCurrPos, this.mCurrPos + 1);
        this.notice_vf.setInAnimation(this, R.anim.in_bottomtop);
        this.notice_vf.setOutAnimation(this, R.anim.out_bottomtop);
        this.notice_vf.showNext();
    }

    private void setView(int curr, int next) {

        View noticeView = getLayoutInflater().inflate(R.layout.item_notice,
                null);
        RelativeLayout rl_notice = (RelativeLayout) noticeView.findViewById(R.id.rl_notice);

        TextView notice_tv = (TextView) noticeView.findViewById(R.id.notice_tv);
        ImageView imageView = (ImageView) noticeView.findViewById(R.id.imageView);
        if ((curr < next) && (next > (titleList.size() - 1))) {
            next = 0;
        } else if ((curr > next) && (next < 0)) {
            next = titleList.size() - 1;
        }
        notice_tv.setText(titleList.get(next));
        Glide.with(this).load(imageUrlList.get(next)).diskCacheStrategy(DiskCacheStrategy.ALL).placeholder(R.mipmap.ic_launcher).into(imageView);
                rl_notice.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                //TODO 做你想要的操作
                Bundle bundle = new Bundle();
                bundle.putString("url", linkUrlArray.get(mCurrPos));
                bundle.putString("title", titleList.get(mCurrPos));
                Intent intent = new Intent(MainActivity.this,
                        BaseWebActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });
        if (notice_vf.getChildCount() > 1) {
            notice_vf.removeViewAt(0);
        }
        notice_vf.addView(noticeView, notice_vf.getChildCount());
        mCurrPos = next;

    }

    @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(taskRun);
        Log.d("slj", "onPause:移除了taskRUn");
    }
}
