package com.linkage.gas_station.main;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.R;

public class SysActivity extends BaseActivity {
	
	TextView sys_now=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_syssetting);
		
		sys_now=(TextView) findViewById(R.id.sys_now);
		sys_now.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				try {
					Intent intent = new Intent("/");  
					ComponentName cm = new ComponentName("com.android.settings","com.android.settings.Settings");  
					intent.setComponent(cm);  
					intent.setAction("android.intent.action.VIEW");  
					startActivityForResult(intent , 0);
				} catch(Exception e) {
					showCustomToast("打开系统设置菜单失败，请您手动设置网络连接");
				}				
				finish();
			}});
	}

}
