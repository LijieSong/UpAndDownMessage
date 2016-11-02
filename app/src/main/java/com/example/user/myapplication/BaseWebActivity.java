package com.example.user.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;

import com.example.user.myapplication.views.ProgressWebView;


/**
 * @Description:WebView界面，带自定义进度条显示
 */
public class BaseWebActivity extends Activity {

	protected ProgressWebView mWebView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_baseweb);
		DemoApplication.getInstance().saveActivity(this);
		mWebView = (ProgressWebView) findViewById(R.id.baseweb_webview);
		initData();
	}

	protected void initData() {
		Intent intent = getIntent();
		Bundle bundle = intent.getExtras();
		String url = bundle.getString("url");
		String title = bundle.getString("title");
		// 开启 DOM storage API 功能
		mWebView.getSettings().setDomStorageEnabled(true);
		//开启 database storage API 功能
		mWebView.getSettings().setDatabaseEnabled(true);
		//支持javascript
		mWebView.getSettings().setJavaScriptEnabled(true);
		//扩大比例的缩放
		mWebView.getSettings().setUseWideViewPort(true);
		mWebView.loadUrl(url);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mWebView = null;
	}
}
