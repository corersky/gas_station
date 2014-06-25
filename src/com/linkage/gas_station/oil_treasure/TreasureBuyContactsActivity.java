package com.linkage.gas_station.oil_treasure;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.os.Bundle;
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
import com.linkage.gas_station.model.ContactModel;
import com.linkage.gas_station.util.Util;

public class TreasureBuyContactsActivity extends BaseActivity {
	
	TextView title_name=null;
	ImageView title_back=null;
	
	ListView treaasure_buy_contacts_list=null;
	SimpleAdapter adapter=null;
	
	//联系人adapter数据
	ArrayList<ContactModel> model_list=null;
	ArrayList<HashMap<String, Object>> temp_list=null;
	
	ArrayList<String> result_list=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_treasure_buy_contacts);
		
		((GasStationApplication) getApplication()).tempActivity.add(TreasureBuyContactsActivity.this);
		
		model_list=Util.getContactData(TreasureBuyContactsActivity.this);
		temp_list=new ArrayList<HashMap<String, Object>>();
		
		String result=Util.getPhoneHeader(TreasureBuyContactsActivity.this);
		String[] result_array=result.substring(0, result.length()-1).split("&");
		result_list=new ArrayList<String>();
		for(int i=0;i<result_array.length;i++) {
			result_list.add(result_array[i]);
		}
		
		init();
	}
	
	private void init() {
		
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText("选择好友手机号");
		title_back=(ImageView) findViewById(R.id.title_back);
		title_back.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
		title_back.setVisibility(View.VISIBLE);
		
		treaasure_buy_contacts_list=(ListView) findViewById(R.id.treaasure_buy_contacts_list);
		for(int i=0;i<model_list.size();i++) {
			HashMap<String, Object> map=new HashMap<String, Object>();
			map.put("name", model_list.get(i).getName());
			map.put("number", model_list.get(i).getPhoneNum());
			if(model_list.get(i).getPhoneNum().length()==11&&result_list.contains(model_list.get(i).getPhoneNum().substring(0, 7))) {
				map.put("area","江苏无锡");
			}
			else {
				map.put("area", "");
			}
			temp_list.add(map);
		}
		adapter=new SimpleAdapter(TreasureBuyContactsActivity.this, temp_list, R.layout.adapter_treasure_buy_contacts, new String[]{"name", "number", "area"}, new int[]{R.id.treasure_buy_contacts_name, R.id.treasure_buy_contacts_num, R.id.treasure_buy_contacts_area});
		treaasure_buy_contacts_list.setAdapter(adapter);
		treaasure_buy_contacts_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Intent intent=getIntent();
				Bundle bundle=new Bundle();
				bundle.putString("phoneNum", model_list.get(arg2).getPhoneNum());
				intent.putExtras(bundle);
				setResult(RESULT_OK, intent);
				finish();
			}
		});
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		((GasStationApplication) getApplication()).tempActivity.remove(TreasureBuyContactsActivity.this);
	}
}
