package com.linkage.gas_station.gonglve;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;

public class WarnInfoActivity extends BaseActivity {
	
	TextView warninfo=null;
	
	ImageView title_back=null;
	TextView title_name=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_warninfo);
		
		((GasStationApplication) getApplication()).tempActivity.add(WarnInfoActivity.this);
		
		init();
	}
	
	public void init() {
		title_back=(ImageView) findViewById(R.id.title_back);
		title_back.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
		title_back.setVisibility(View.VISIBLE);
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText("×¢ÒâÊÂÏî");
		warninfo=(TextView) findViewById(R.id.warninfo);
		warninfo.setText(getIntent().getExtras().getString("warninfo"));
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		((GasStationApplication) getApplication()).tempActivity.remove(WarnInfoActivity.this);
	}

}
