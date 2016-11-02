# UpAndDownMessage 内容说明
  带图片的公告点击事件及文件下载演示demo. 使用了github开源项目filedownloader,及OKhttp3网络请求和glide图片加载.

   使用ViewFlipper及handler结合使用,演示公告的上下翻动,及跳转相应的页面

   主要功能部分代码
       创建一个handler并postDelayed
      taskRun = new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(taskRun,3000);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        moveNext();
                    }
                });
            }
        };

在onresume中
          @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(taskRun,0);
    }

 在onpause()中
      @Override
    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(taskRun);
    }
       主要逻辑实现 
      /* 移动到下一个*/
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
    
    
    包含有一个基于github上开源项目filedownloader封装的一个单任务下载工具类: 
    
        okhttp3二次封装的文件上传及数据请求的网络请求工具类.
        
        详见源码:OkHttpManager.class OKHttpUtils.class 类
        
    包含有一个文件缓存处理类:ACache.class类.
        
        单任务下载 :具体实现步骤 详见 DownLoadTestActivity.class 和 DownLoadFileUtils.class类
        
    修改了部分 DownLoadFileUtils.class类的代码,以便更好的使用

    增加了全部异常捕获保存本地文件的方法,可在下次重启APP时把异常信息上传至服务器.
