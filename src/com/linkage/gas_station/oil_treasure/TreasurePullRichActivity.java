package com.linkage.gas_station.oil_treasure;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;

public class TreasurePullRichActivity extends BaseActivity {
	
	TextView title_name=null;
	ImageView title_back=null;
	
	EditText pull_rich_title=null;
	EditText pull_rich_message=null;
	TextView pull_rich_commit=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_treasure_pullrich);
		
		((GasStationApplication) getApplication()).tempActivity.add(TreasurePullRichActivity.this);
		
		init();
	}
	
	private void init() {
		
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText("拉土豪派金币");
		title_back=(ImageView) findViewById(R.id.title_back);
		title_back.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
		title_back.setVisibility(View.VISIBLE);
		
		pull_rich_title=(EditText) findViewById(R.id.pull_rich_title);
		pull_rich_message=(EditText) findViewById(R.id.pull_rich_message);
		pull_rich_commit=(TextView) findViewById(R.id.pull_rich_commit);
		pull_rich_commit.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(pull_rich_title.getText().toString().equals("")||pull_rich_message.getText().toString().equals("")) {
					showCustomToast("请您输入店铺名称和宣传语");
					return ;
				}
				Intent intent=new Intent(TreasurePullRichActivity.this, TreasurePullRichDetailActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("pull_rich_title", pull_rich_title.getText().toString());
				bundle.putString("pull_rich_message", pull_rich_message.getText().toString());
				bundle.putString("url", getIntent().getExtras().getString("url"));
				intent.putExtras(bundle);
				startActivity(intent);
			}});
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		((GasStationApplication) getApplication()).tempActivity.remove(TreasurePullRichActivity.this);
	}
}
