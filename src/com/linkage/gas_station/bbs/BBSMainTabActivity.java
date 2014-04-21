package com.linkage.gas_station.bbs;

import com.linkage.gas_station.R;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.TabHost.TabSpec;

public class BBSMainTabActivity extends TabActivity {
	ImageView title_refresh=null;
	TextView title_name=null;
	
	TabHost host=null;
	RelativeLayout luntan_select1_layout=null;
	RelativeLayout luntan_select2_layout=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_luntan_tabmain);
		
		init();
	}
	
	public void init() {
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText("º””Õ’æ¬€Ã≥");
		title_refresh=(ImageView) findViewById(R.id.title_refresh);
		if(getIntent().getExtras().getString("role_id").equals("2")) {
			title_refresh.setVisibility(View.VISIBLE);
		} 
		else {
			title_refresh.setVisibility(View.GONE);
		}
		title_refresh.setImageResource(R.drawable.fatie_image);
		title_refresh.setOnClickListener(new ImageView.OnClickListener() {
	
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(BBSMainTabActivity.this, BBSSendActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("type", "saveNewFroum");
				intent.putExtras(bundle);
				startActivityForResult(intent, 1000);
			}});
		luntan_select1_layout=(RelativeLayout) findViewById(R.id.luntan_select1_layout);
		luntan_select1_layout.setOnClickListener(new RelativeLayout.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setTab(0);
			}});
		luntan_select2_layout=(RelativeLayout) findViewById(R.id.luntan_select2_layout);
		luntan_select2_layout.setOnClickListener(new RelativeLayout.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				setTab(1);
			}});
		host=getTabHost();
		addTab("spec1", BBSMainActivity.class, -1);
		addTab("spec2", BBSMainActivity.class, 2);
		setTab(0);
	}
	
	public void addTab(String tag, Class<?> cls, int type) {
		TabSpec spec=host.newTabSpec(tag);
		Intent intent=new Intent(BBSMainTabActivity.this, cls);
		Bundle bundle=new Bundle();
		bundle.putInt("type", type);
		intent.putExtras(bundle);
		spec.setContent(intent);
		spec.setIndicator("", getResources().getDrawable(R.drawable.ic_launcher));
		host.addTab(spec);
	}
	
	public void setTab(int index) {
		
		luntan_select1_layout.setBackgroundResource(R.drawable.luntan_select2);
		luntan_select2_layout.setBackgroundResource(R.drawable.luntan_select2);
		switch(index) {
		case 0:
			luntan_select1_layout.setBackgroundResource(R.drawable.luntan_select1);
			host.setCurrentTabByTag("spec1");
			break;
		case 1:
			luntan_select2_layout.setBackgroundResource(R.drawable.luntan_select1);
			host.setCurrentTabByTag("spec2");
			break;
		}
	}
}
