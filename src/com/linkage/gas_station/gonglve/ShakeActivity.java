package com.linkage.gas_station.gonglve;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;

public class ShakeActivity extends BaseActivity {
	
	ShakeListener lis=null;
	long activityId=0;
	
	ProgressBar shake_bar=null;
	ImageView shake_image=null;
	ImageView title_back=null;
	TextView title_name=null;
	RelativeLayout yao_layout=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		DisplayMetrics dm=new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		if(dm.density==2.0) {
			setContentView(R.layout.activity_shake);
		}
		else {
			setContentView(R.layout.activity_shake_15);
		}
		
		((GasStationApplication) getApplication()).tempActivity.add(ShakeActivity.this);
		
		activityId=Long.parseLong(getIntent().getExtras().getString("activityId"));
		
		init();
	}
	
	public void init() {
		yao_layout=(RelativeLayout) findViewById(R.id.yao_layout);
		yao_layout.setBackgroundResource(R.drawable.yao_bg);
		title_back=(ImageView) findViewById(R.id.title_back);
		title_back.setVisibility(View.VISIBLE);
		title_back.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText("ҡһҡ");
		shake_image=(ImageView) findViewById(R.id.shake_image);
		shake_bar=(ProgressBar) findViewById(R.id.shake_bar);
		lis=new ShakeListener(ShakeActivity.this, activityId, shake_image, shake_bar);
		
		lis.start();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		lis.stop();
		((GasStationApplication) getApplication()).tempActivity.remove(ShakeActivity.this);
	}

}
