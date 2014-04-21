package com.linkage.gas_station.gonglve;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.util.Util;

public class FlowBankHistoryActivity extends BaseActivity {
	
	ListView history_list=null;
	SimpleAdapter adapter=null;
	
	ImageView title_back=null;
	TextView title_name=null;
	
	ArrayList<HashMap<String, String>> history_list_=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_flow_bank_history);
		
		((GasStationApplication) getApplication()).tempActivity.add(FlowBankHistoryActivity.this);
		history_list_=new ArrayList<HashMap<String, String>>();
		Util.insertFlowBankHistory(FlowBankHistoryActivity.this, "18951765220");		
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
		title_name.setText("转增号码历史");
		
		history_list=(ListView) findViewById(R.id.history_list);
		String history=Util.getFlowBankHistory(FlowBankHistoryActivity.this);
		if(!history.equals("")) {
			final String[] history_array=history.split("&");
			for(int i=0;i<history_array.length;i++) {
				HashMap<String, String> map=new HashMap<String, String>();
				map.put("history", history_array[i]);
				history_list_.add(map);
			}
			if(history_array.length!=0) {
				history_list.addFooterView(getFootView());
			}
			adapter=new SimpleAdapter(FlowBankHistoryActivity.this, history_list_, android.R.layout.simple_list_item_1, new String[]{"history"}, new int[]{android.R.id.text1});
			history_list.setAdapter(adapter);
			history_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> parent, View view,
						int position, long id) {
					// TODO Auto-generated method stub				
					Intent intent=getIntent();
					Bundle bundle=new Bundle();
					bundle.putString("history", history_array[position]);
					intent.putExtras(bundle);
					setResult(RESULT_OK, intent);
					finish();
				}
			});
		}
	}
	
	public View getFootView() {
		final View view=LayoutInflater.from(FlowBankHistoryActivity.this).inflate(R.layout.view_footer, null);
		TextView footer_text=(TextView) view.findViewById(R.id.footer_text);
		footer_text.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				history_list.removeFooterView(view);
				history_list_.clear();
				adapter.notifyDataSetChanged();
				Util.cleanFlowBankHistory(FlowBankHistoryActivity.this);
			}});
		return view;
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		((GasStationApplication) getApplication()).tempActivity.remove(FlowBankHistoryActivity.this);
	}

}
