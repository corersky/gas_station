package com.linkage.gas_station.gonglve;

import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.jiayou.JiayouDetaiActivity;

public class SelectTypeActivity extends BaseActivity implements OnClickListener , OnItemClickListener{
	
	private ListView listview = null;
	private TextView selecttype_title=null;
	private Button sure_button = null;
	
	private ArrayList<TypeObject> list = null;	
	private ListviewAdapter adapter = null;
	private int select_type = -1;
	
	private TypeObject selectObj = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_selecttype);
		
		((GasStationApplication) getApplication()).tempActivity.add(SelectTypeActivity.this);
		
		Bundle bundle = getIntent().getExtras();
		ArrayList<String> valuelist = bundle.getStringArrayList("list");
		list = new ArrayList<TypeObject>();
		for(int i = 0 ; i < valuelist.size() ; i ++)
		{
			String[] str = valuelist.get(i).split("&");
			TypeObject obj = new TypeObject(str[0], str[1], Integer.parseInt(str[2]), Integer.parseInt(str[3]), Integer.parseInt(str[4]));
			list.add(obj);
		}
		init();
	}
	
	public void init() {
		
		selecttype_title=(TextView) findViewById(R.id.selecttype_title);
		sure_button = (Button) findViewById(R.id.sure_button);
		sure_button.setOnClickListener(this);
		if(getIntent().getExtras().getInt("activity_type")==13||getIntent().getExtras().getInt("activity_type")==18) {
			selecttype_title.setText("请选择流量包档次");
			sure_button.setText("订购");
		}
		else {
			selecttype_title.setText("请选择加餐包档次");
			sure_button.setText("抢购");
		}
		listview = (ListView) findViewById(R.id.listview);
		listview.setOnItemClickListener(this);
		adapter = new ListviewAdapter(this);
		adapter.setList(list);
		listview.setAdapter(adapter);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.sure_button:
			if(select_type == -1) {
				showCustomToast("请选择加餐包");
				return;
			}
			((GasStationApplication) getApplicationContext()).jumpJiayouFrom=getIntent().getExtras().getInt("activity_id");
			Intent data=null;
			Bundle bundle = new Bundle();
			bundle.putString("from", "dinggouhuodong");
			bundle.putString("offerId", ""+selectObj.getTypeId());
			if(selectObj.getTypeName().indexOf("$")!=-1) {
				bundle.putString("offer_name", selectObj.getTypeName().substring(0, selectObj.getTypeName().indexOf("$"))+"\n"+
						selectObj.getTypeName().substring(selectObj.getTypeName().indexOf("$")+1));
			}
			else {
				bundle.putString("offer_name", selectObj.getTypeName());
			}
			bundle.putString("type", "simple_station");
			bundle.putInt("activity_id", getIntent().getExtras().getInt("activity_id"));
			if(getIntent().getExtras().getInt("activity_type")==13) {
				data=new Intent(this , QXJY_Pay_Activity.class);
				bundle.putInt("cost", selectObj.cost);
				bundle.putInt("offer_type_id", selectObj.offer_type_id);
				bundle.putInt("activity_type", getIntent().getExtras().getInt("activity_type"));
			}
			else if(getIntent().getExtras().getInt("activity_type")==27) {
				data=new Intent(this , Cash_Pay_Activity.class);
				bundle.putInt("cost", selectObj.cost);
				bundle.putInt("offer_type_id", selectObj.offer_type_id);
				bundle.putInt("activity_type", getIntent().getExtras().getInt("activity_type"));
			}
			else if(getIntent().getExtras().getInt("activity_type")==23) {
				data=new Intent(this , SendFlow_Pay_Activity.class);
			}
			else {
				data=new Intent(this , JiayouDetaiActivity.class);
			}
			switch(selectObj.offer_type_id) {
			case 1:
				if(getIntent().getExtras().getInt("activity_type")==13) {
					bundle.putString("offer_description", "全国漫游,全天候,本款流量包仅当月有效；\n" +
							"电子券赠送流量立即到帐，当月有效");
				}
				else if(getIntent().getExtras().getInt("activity_type")==27) {
					bundle.putString("offer_description", "全国漫游，全天候，本款流量包仅当月有效\n" +
							"现金券自动抵扣当月流量包费用。");
				}
				else {
					bundle.putString("offer_description", getResources().getString(R.string.jiayou_desp_1));
				}
				break;
			case 2:
				if(getIntent().getExtras().getInt("activity_type")==13) {
					bundle.putString("offer_description", "省内漫游，23：00-7：00，当月有效，本款流量包为自动续订\n" +
							"电子券赠送流量次月到帐，连送3个月，取消订购则券作废、赠送终止");
				}
				else if(getIntent().getExtras().getInt("activity_type")==27) {
					bundle.putString("offer_description", "省内漫游,23:00-7:00,当月有效,本款流量包为自动续订;\n" +
							"现金券次月自动抵扣上月流量包费用，连送3个月，取消订购则券作废、赠送终止。");
				}
				else {
					bundle.putString("offer_description", getResources().getString(R.string.jiayou_desp_3));
				}
				break;
			case 4:
				if(getIntent().getExtras().getInt("activity_type")==13) {
					bundle.putString("offer_description", "省内漫游，全天候，当月有效，本款流量包为自动续订\n" +
							"电子券赠送流量次月到帐，连送3个月，取消订购则券作废、赠送终止");
				}
				else if(getIntent().getExtras().getInt("activity_type")==27) {
					bundle.putString("offer_description", "省内漫游,全天候,当月有效,本款流量包为自动续订;\n" +
							"现金券次月自动抵扣上月流量包费用，连送3个月，取消订购则券作废、赠送终止。");
				}
				else {
					bundle.putString("offer_description", getResources().getString(R.string.jiayou_desp_1));
				}
				break;
			default:
				if(getIntent().getExtras().getInt("activity_type")==18) {
					if(selectObj.getTypeId()==84) {
						bundle.putString("offer_description", "说明：省内漫游,有效期24小时");
					}
					else if(selectObj.getTypeId()==85) {
						bundle.putString("offer_description", "说明：省内漫游,全天候,6月13日0时-7月15日0时");
					}
				}
				else if(getIntent().getExtras().getInt("activity_type")==23) {
					if(selectObj.getTypeId()==84) {
						bundle.putString("offer_description", "说明：省内漫游,有效期24小时");
					}
					else if(selectObj.getTypeId()==92) {
						bundle.putString("offer_description", "说明:省内漫游,有效期:8月16日0时 - 28日24时");
					}
					else if(selectObj.getTypeId()==93) {
						bundle.putString("offer_description", "说明:省内漫游,有效期:9月6日0时 - 8日24时");
					}
					else if(selectObj.getTypeId()==94) {
						bundle.putString("offer_description", "说明:省内漫游,有效期:10月1日0时 - 7日24时");
					}
				}
				else {
					bundle.putString("offer_description", "");
				}
			}
			data.putExtras(bundle);
			startActivity(data);
			finish();
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		select_type = arg2;
		adapter.setSelect(select_type);
		adapter.notifyDataSetChanged();
		selectObj = (TypeObject) adapter.getItem(select_type);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		((GasStationApplication) getApplication()).tempActivity.remove(SelectTypeActivity.this);
	}
	
	public static String ToDBC(String input) {
		char[] c = input.toCharArray();
		for (int i = 0; i< c.length; i++) {
			if (c[i] == 12288) {
				c[i] = (char) 32;
				continue;
			}
			if (c[i]> 65280&& c[i]< 65375)
				c[i] = (char) (c[i] - 65248);
		}
		return new String(c);
	}
}
