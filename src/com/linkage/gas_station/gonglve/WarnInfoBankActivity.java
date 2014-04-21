package com.linkage.gas_station.gonglve;

import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;

public class WarnInfoBankActivity extends BaseActivity {
	
	ImageView title_back=null;
	TextView title_name=null;
	
	TextView warninfobank=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_warninfobank);
		
		((GasStationApplication) getApplication()).tempActivity.add(WarnInfoBankActivity.this);
		
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
		title_name.setText("流量银行说明");
		
		warninfobank=(TextView) findViewById(R.id.warninfobank);
		warninfobank.setText(getIntent().getExtras().getString("instruction"));
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		((GasStationApplication) getApplication()).tempActivity.remove(WarnInfoBankActivity.this);
	}

}
