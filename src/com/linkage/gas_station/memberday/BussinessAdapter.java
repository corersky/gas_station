package com.linkage.gas_station.memberday;

import java.util.ArrayList;

import com.linkage.gas_station.R;
import com.linkage.gas_station.model.MemberBuyModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BussinessAdapter extends BaseAdapter {
	
	ArrayList<MemberBuyModel> str=null;
	Context context=null;
	
	public BussinessAdapter(ArrayList<MemberBuyModel> str, Context context) {
		this.str=str;
		this.context=context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return str.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		BussinessHolder holder=null;
		if(arg1==null) {
			arg1=LayoutInflater.from(context).inflate(R.layout.adapter_bussiness, null);
			holder=new BussinessHolder();
			holder.adapter_bussiness_order_time=(TextView) arg1.findViewById(R.id.adapter_bussiness_order_time);
			holder.adapter_bussiness_last_time=(TextView) arg1.findViewById(R.id.adapter_bussiness_last_time);
			holder.adapter_bussiness_name=(TextView) arg1.findViewById(R.id.adapter_bussiness_name);
			holder.adapter_bussiness_use_time=(TextView) arg1.findViewById(R.id.adapter_bussiness_use_time);
			arg1.setTag(holder);
		}
		else {
			holder=(BussinessHolder) arg1.getTag();
		}
		holder.adapter_bussiness_order_time.setText("抢购时间："+str.get(arg0).getGenerate_time());
		holder.adapter_bussiness_last_time.setText("有效期："+str.get(arg0).getValid_date());
		holder.adapter_bussiness_name.setText("商品："+str.get(arg0).getPrize_name());
		holder.adapter_bussiness_use_time.setText(str.get(arg0).getScan_time());
		return arg1;
	}

}

class BussinessHolder {
	TextView adapter_bussiness_order_time=null;
	TextView adapter_bussiness_last_time=null;
	TextView adapter_bussiness_name=null;
	TextView adapter_bussiness_use_time=null;
}
