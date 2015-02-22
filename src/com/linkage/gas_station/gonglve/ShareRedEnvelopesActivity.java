package com.linkage.gas_station.gonglve;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.linkage.gas_station.R;

public class ShareRedEnvelopesActivity extends Activity {
	
	ImageView shareredenvelopes_luckdraw=null;
	ImageView shareredenvelopes_buy=null;
	ImageView shareredenvelopes_record=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_shareredenvelopes);
		
		init();
	}
	
	private void init() {
		shareredenvelopes_buy=(ImageView) findViewById(R.id.shareredenvelopes_buy);
		shareredenvelopes_buy.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(ShareRedEnvelopesActivity.this, BuyShareRedEnvelopesActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("activityId", getIntent().getExtras().getString("activityId"));
				intent.putExtras(bundle);
				startActivity(intent);
			}});
		shareredenvelopes_record=(ImageView) findViewById(R.id.shareredenvelopes_record);
		shareredenvelopes_record.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(ShareRedEnvelopesActivity.this, ShareRedEnvelopesRecordActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("activityId", getIntent().getExtras().getString("activityId"));
				intent.putExtras(bundle);
				startActivity(intent);
			}});
		shareredenvelopes_luckdraw=(ImageView) findViewById(R.id.shareredenvelopes_luckdraw);
		shareredenvelopes_luckdraw.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(ShareRedEnvelopesActivity.this, ShareRedEnvelopesLucjDrawActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("activityId", getIntent().getExtras().getString("activityId"));
				intent.putExtras(bundle);
				startActivity(intent);
			}});
	}
}
