package com.linkage.gas_station.gonglve;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.WebDetailActivity;

public class PotOfGoldActivity extends BaseActivity {
	
	TextView title_name=null;
	ImageView title_back=null;
	
	ImageView potofgold_cash_image=null;
	ImageView potofgold_money_image=null;
	ImageView potofgold_flow_image=null;
	
	ArrayList<String> list=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_potofgold);
		
		list=Util.getUserInfo(PotOfGoldActivity.this);
		
		init();
		
		((GasStationApplication) getApplication()).tempActivity.add(PotOfGoldActivity.this);
	}
	
	private void init() {
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText("Ò»Í°½ð");
		title_back=(ImageView) findViewById(R.id.title_back);
		title_back.setVisibility(View.VISIBLE);
		title_back.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
		
		potofgold_cash_image=(ImageView) findViewById(R.id.potofgold_cash_image);
		potofgold_cash_image.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				enter(getIntent().getExtras().getString("activity_url")+"&phonenumber="+list.get(0)+"&mail=&areaid="+list.get(1)+"&type=0");
			}
		});
		potofgold_money_image=(ImageView) findViewById(R.id.potofgold_money_image);
		potofgold_money_image.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				enter(getIntent().getExtras().getString("activity_url")+"&phonenumber="+list.get(0)+"&mail=&areaid="+list.get(1)+"&type=1");
			}
		});
		potofgold_flow_image=(ImageView) findViewById(R.id.potofgold_flow_image);
		potofgold_flow_image.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				enter(getIntent().getExtras().getString("activity_url")+"&phonenumber="+list.get(0)+"&mail=&areaid="+list.get(1)+"&type=2");
			}
		});
	}
	
	public void enter(String url) {
		Intent intent=new Intent(PotOfGoldActivity.this, WebDetailActivity.class);
		Bundle bundle=new Bundle();
		bundle.putString("url", url);
		intent.putExtras(bundle);
		startActivity(intent);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		((GasStationApplication) getApplication()).tempActivity.remove(PotOfGoldActivity.this);
	}
}
