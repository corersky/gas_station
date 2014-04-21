package com.linkage.gas_station.jiayou;

import com.linkage.gas_station.R;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Window;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebSettings.ZoomDensity;
import android.webkit.WebViewClient;

public class BankActivity extends Activity {
	
	WebView source_mainview=null;
	
	//œ‘ æÕ¯“≥–≈œ¢
	String url="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_bank);
		
		url=getIntent().getExtras().getString("bank_url");
		
		init();
	}
	
	public void init() {
		source_mainview=(WebView) findViewById(R.id.source_mainview);
		WebSettings settings=source_mainview.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setBuiltInZoomControls(true);
		settings.setSupportZoom(true);
		settings.setDefaultZoom(ZoomDensity.CLOSE);
		source_mainview.setWebChromeClient(new WebChromeClient());
		source_mainview.setWebViewClient(new MyWebViewClient());
		source_mainview.loadDataWithBaseURL(null, url, "text/html", "utf-8", "");
	}
	
	final class MyWebViewClient extends WebViewClient{   

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }    

        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }

    }

}
