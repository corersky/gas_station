package com.linkage.gas_station.share;

import java.util.ArrayList;

import com.baidu.mobstat.StatService;
import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.model.ContactModel;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.ListView;
import android.widget.TextView;

public class SelectContactsActivity extends BaseActivity {
	
	//联系人adapter数据
	ArrayList<ContactModel> model_list=null;
	//是否显示文字
	boolean isShowLetter=false;
	//临时联系人数据
	ArrayList<ContactModel> model_list_temp=null;
	
	ListView contacts_list=null;
	SelectContactsAdapter adapter=null;
	TextView contacts_letter=null;
	TextView contacts_share=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_selectcontacts);
		
		((GasStationApplication) getApplication()).tempActivity.add(SelectContactsActivity.this);
		model_list=((GasStationApplication) getApplicationContext()).getModel_list();
		model_list_temp=(ArrayList<ContactModel>) model_list.clone();
		
		init();
	}
	
	public void init() {
		contacts_share=(TextView) findViewById(R.id.contacts_share);
		contacts_share.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=getIntent();
				setResult(RESULT_OK, intent);
				finish();
			}});
		contacts_letter=(TextView) findViewById(R.id.contacts_letter);
		contacts_list=(ListView) findViewById(R.id.contacts_list);
		adapter=new SelectContactsAdapter(SelectContactsActivity.this, model_list);
		contacts_list.setAdapter(adapter);
		contacts_list.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub
				if(scrollState==AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
					isShowLetter=false;
					contacts_letter.setVisibility(View.GONE);
				}
				else if(scrollState==AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
					isShowLetter=true;
					contacts_letter.setVisibility(View.VISIBLE);
				}
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				if(model_list!=null&&model_list.size()>0&&isShowLetter) {
					contacts_letter.setText(model_list.get(firstVisibleItem).getLetter());
				}							
			}
		});
		adapter.notifyDataSetChanged();		
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(KeyEvent.KEYCODE_BACK==keyCode) {
			model_list=model_list_temp;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		((GasStationApplication) getApplication()).tempActivity.remove(SelectContactsActivity.this);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		StatService.onResume(this);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		StatService.onPause(this);
	}
}
