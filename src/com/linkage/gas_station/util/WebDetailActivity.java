package com.linkage.gas_station.util;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.R;

public class WebDetailActivity extends BaseActivity {
	
	WebView web_webview=null;
	ProgressBar web_pb=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_web);
		
		init();
	}
	
	private void init() {
		web_pb=(ProgressBar) findViewById(R.id.web_pb);
		web_pb.setMax(100);
		web_webview=(WebView) findViewById(R.id.web_webview);
		web_webview.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
		web_webview.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		WebSettings settings=web_webview.getSettings();
		settings.setJavaScriptEnabled(true);
		web_webview.setWebViewClient(new WebViewClient());
		web_webview.setWebChromeClient(new MyWebChromeClient());  
		settings.setBuiltInZoomControls(false);
		web_webview.loadUrl(getIntent().getExtras().getString("url"));
	}
	

	private class MyWebChromeClient extends WebChromeClient {  
	    @Override  
	    public void onProgressChanged(WebView view, int newProgress) {  
	    	web_pb.setProgress(newProgress);  
	        if(newProgress==100){  
	        	web_pb.setVisibility(View.GONE);  
	        }  
	        super.onProgressChanged(view, newProgress);  
	    }
	}
//	
//	@Override
//	public void onBackPressed() {
//		// TODO Auto-generated method stub
//		if(web_webview.canGoBack()) {
//			web_webview.goBack();				
//			return;
//		}
//		super.onBackPressed();
//	}
}
