package com.linkage.gas_station.gonglve;

import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class SendPacketsActivity extends FragmentActivity {
	
	TextView title_name=null;
	ImageView title_back=null;

	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_sendpackets);
		
		((GasStationApplication) getApplication()).tempActivity.add(SendPacketsActivity.this);
		
		init();
	}
	
	private void init() {
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText("ÍÁºÀËÍºì°ü");
		title_back=(ImageView) findViewById(R.id.title_back);
		title_back.setVisibility(View.VISIBLE);
		title_back.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		((GasStationApplication) getApplication()).tempActivity.remove(SendPacketsActivity.this);
	}
}
