package com.linkage.gas_station.oil_treasure;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.qqapi.QQActivity;
import com.linkage.gas_station.sinaweiboapi.WBMainActivity;
import com.linkage.gas_station.wxapi.SendWeixin;
import com.linkage.gas_station.yxapi.SendYixin;

public class TreasurePullRichDetailActivity extends BaseActivity {
	
	TextView title_name=null;
	ImageView title_back=null;
	
	TextView treasure_send_title=null;
	TextView treasure_send_message=null;
	ImageView yixin_pengyou_share=null;
	ImageView weixin_pengyou_share=null;
	ImageView qqkj_logo_share=null;
	ImageView sinaweibo_logo_share=null;
	
	String url="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_treasure_detail_pullrich);
		
		((GasStationApplication) getApplication()).tempActivity.add(TreasurePullRichDetailActivity.this);
		
		url=getIntent().getExtras().getString("url");
		
		init();
	}
	
	private void init() {
		
		title_name=(TextView) findViewById(R.id.title_name);
		if(getIntent().getExtras().getString("title")!=null) {
			title_name.setText(getIntent().getExtras().getString("title"));
		}
		else {
			title_name.setText("拉土豪派金币");
		}
		title_back=(ImageView) findViewById(R.id.title_back);
		title_back.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
		title_back.setVisibility(View.VISIBLE);
		
		treasure_send_title=(TextView) findViewById(R.id.treasure_send_title);
		treasure_send_title.setText(getIntent().getExtras().getString("pull_rich_title"));
		treasure_send_message=(TextView) findViewById(R.id.treasure_send_message);
		treasure_send_message.setText(getIntent().getExtras().getString("pull_rich_message"));
		yixin_pengyou_share=(ImageView) findViewById(R.id.yixin_pengyou_share);
		yixin_pengyou_share.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(url.equals("")) {
					showCustomToast("加载分享数据失败，请重新退出余额宝再试");
					return;
				}
				((GasStationApplication) getApplicationContext()).shareType=5;
				((GasStationApplication) getApplicationContext()).activityId=28;
				((GasStationApplication) getApplicationContext()).content=treasure_send_message.getText().toString();
				SendYixin yixin=new SendYixin();
				yixin.sendYixin(TreasurePullRichDetailActivity.this, treasure_send_message.getText().toString(), url+"5&shopname="+Uri.encode(treasure_send_title.getText().toString())+"&shopTitle="+Uri.encode(treasure_send_message.getText().toString()), treasure_send_title.getText().toString(), true);
			}});
		weixin_pengyou_share=(ImageView) findViewById(R.id.weixin_pengyou_share);
		weixin_pengyou_share.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(url.equals("")) {
					showCustomToast("加载分享数据失败，请重新退出余额宝再试");
					return;
				}
				((GasStationApplication) getApplicationContext()).shareType=3;
				((GasStationApplication) getApplicationContext()).activityId=28;
				((GasStationApplication) getApplicationContext()).content=treasure_send_message.getText().toString();
				SendWeixin weixin=new SendWeixin();
				weixin.sendWeixin(TreasurePullRichDetailActivity.this, treasure_send_message.getText().toString(), 
						url+"3&shopname="+Uri.encode(treasure_send_title.getText().toString())+"&shopTitle="+Uri.encode(treasure_send_message.getText().toString()),
						treasure_send_title.getText().toString()+"\n"+treasure_send_message.getText().toString(), true);
			}});
		qqkj_logo_share=(ImageView) findViewById(R.id.qqkj_logo_share);
		qqkj_logo_share.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(url.equals("")) {
					showCustomToast("加载分享数据失败，请重新退出余额宝再试");
					return;
				}
				((GasStationApplication) getApplicationContext()).shareType=6;
				((GasStationApplication) getApplicationContext()).activityId=28;
				((GasStationApplication) getApplicationContext()).content=treasure_send_message.getText().toString();
				Intent intent=new Intent(TreasurePullRichDetailActivity.this, QQActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("title", treasure_send_title.getText().toString());
				bundle.putString("text", treasure_send_message.getText().toString());
				bundle.putString("url", url+"6&shopname="+Uri.encode(treasure_send_title.getText().toString())+"&shopTitle="+Uri.encode(treasure_send_message.getText().toString()));
				bundle.putString("send_imageUrl", "http://a2.mzstatic.com/us/r30/Purple6/v4/98/a8/48/98a84887-be7a-9402-24ce-59284e6bf0f8/mzl.rwwplqzr.175x175-75.jpg");
				bundle.putString("type", "qqkj");
				bundle.putString("from", "oil_treasure");
				intent.putExtras(bundle);
				startActivity(intent);
			}});
		sinaweibo_logo_share=(ImageView) findViewById(R.id.sinaweibo_logo_share);
		sinaweibo_logo_share.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(url.equals("")) {
					showCustomToast("加载分享数据失败，请重新退出余额宝再试");
					return;
				}
				((GasStationApplication) getApplicationContext()).shareType=4;
				((GasStationApplication) getApplicationContext()).activityId=28;
				((GasStationApplication) getApplicationContext()).content=treasure_send_message.getText().toString();
				Intent intent=new Intent(TreasurePullRichDetailActivity.this, WBMainActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("text", treasure_send_message.getText().toString());
				bundle.putString("title", treasure_send_title.getText().toString());
				bundle.putString("url", url+"4&shopname="+Uri.encode(treasure_send_title.getText().toString())+"&shopTitle="+Uri.encode(treasure_send_message.getText().toString()));
				bundle.putString("defaultText", "流量加油站");
				bundle.putString("from", "activity");
				intent.putExtras(bundle);
				startActivity(intent);
			}});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		((GasStationApplication) getApplication()).tempActivity.remove(TreasurePullRichDetailActivity.this);
	}
}
