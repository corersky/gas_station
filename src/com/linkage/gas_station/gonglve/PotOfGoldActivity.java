package com.linkage.gas_station.gonglve;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.R;
import com.linkage.gas_station.util.WebDetailActivity;

public class PotOfGoldActivity extends BaseActivity {
	
	ImageView potofgold_cash_image=null;
	ImageView potofgold_money_image=null;
	ImageView potofgold_flow_image=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_potofgold);
		
		init();
	}
	
	private void init() {
		potofgold_cash_image=(ImageView) findViewById(R.id.potofgold_cash_image);
		potofgold_cash_image.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				enter();
			}
		});
		potofgold_money_image=(ImageView) findViewById(R.id.potofgold_money_image);
		potofgold_money_image.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				enter();
			}
		});
		potofgold_flow_image=(ImageView) findViewById(R.id.potofgold_flow_image);
		potofgold_flow_image.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				enter();
			}
		});
	}
	
	public void enter() {
		Intent intent=new Intent(PotOfGoldActivity.this, WebDetailActivity.class);
		Bundle bundle=new Bundle();
		bundle.putString("url", "http://www.baidu.com");
		intent.putExtras(bundle);
		startActivity(intent);
	}
}
