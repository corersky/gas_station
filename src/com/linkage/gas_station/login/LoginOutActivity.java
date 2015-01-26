package com.linkage.gas_station.login;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.main.MainActivity;
import com.linkage.gas_station.util.Util;

public class LoginOutActivity extends BaseActivity {
	
	TextView update_desp=null;
	TextView down_now=null;
	TextView down_later=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_down);
		
		Util.setLoginOut(LoginOutActivity.this, true);
		
		init();
	}
	
	public void init() {
		update_desp=(TextView) findViewById(R.id.update_desp);
		update_desp.setText("您的账号在另一地点，您被迫下线了。如果这不是您本人的操作，那么您的密码可能已经被泄露，建议您修改密码");
		down_now=(TextView) findViewById(R.id.down_now);
		down_now.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(LoginOutActivity.this, ReloadActivity.class);
				startActivity(intent);
				if(((GasStationApplication) getApplication()).tempActivity.size()>0) {
					for(int i=0;i<((GasStationApplication) getApplication()).tempActivity.size();i++) {
						if(((GasStationApplication) getApplication()).tempActivity.get(i)==((Activity) MainActivity.getInstance())) {
							continue;
						}
						((GasStationApplication) getApplication()).tempActivity.get(i).finish();
					}
				}
				finish();
				
			}});
		down_later=(TextView) findViewById(R.id.down_later);
		down_later.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(((GasStationApplication) getApplication()).tempActivity.size()>0) {
					for(int i=0;i<((GasStationApplication) getApplication()).tempActivity.size();i++) {
						((GasStationApplication) getApplication()).tempActivity.get(i).finish();
					}
				}
				finish();
			}});
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		StatService.onResume(this);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		StatService.onPause(this);
	}

}
