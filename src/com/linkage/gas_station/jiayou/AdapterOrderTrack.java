package com.linkage.gas_station.jiayou;

import java.util.ArrayList;

import com.linkage.gas_station.R;
import com.linkage.gas_station.model.OrderModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AdapterOrderTrack extends BaseAdapter {
	
	ArrayList<OrderModel> orderModel_list=null;
	Context context=null;
	
	public AdapterOrderTrack(Context context, ArrayList<OrderModel> orderModel_list) {
		this.context=context;
		this.orderModel_list=orderModel_list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return orderModel_list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return orderModel_list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		OrderTrackHolder holder=null;
		if(convertView==null) {
			holder=new OrderTrackHolder();
			convertView=LayoutInflater.from(context).inflate(R.layout.adapter_ordertrack, null);
			holder.order_name=(TextView) convertView.findViewById(R.id.order_name);
			holder.order_date=(TextView) convertView.findViewById(R.id.order_date);
			holder.order_statue=(ImageView) convertView.findViewById(R.id.order_statue);
			holder.order_type=(TextView) convertView.findViewById(R.id.order_type);
			holder.order_pay_type=(TextView) convertView.findViewById(R.id.order_pay_type);
			convertView.setTag(holder);
		}
		else {
			holder=(OrderTrackHolder) convertView.getTag();
		}
		holder.order_name.setText(orderModel_list.get(position).getOffer_name());
		holder.order_date.setText(orderModel_list.get(position).getOrder_time());
		switch(Integer.parseInt(orderModel_list.get(position).getOrder_state())) {
		case 1:
			holder.order_statue.setImageResource(R.drawable.button_jiayou_ing);
			break;
		case 2:
			holder.order_statue.setImageResource(R.drawable.button_jiayou_comp);
			break;
		case 3:
			holder.order_statue.setImageResource(R.drawable.button_jiayou_fail);
			break;
		}
		holder.order_type.setText(orderModel_list.get(position).getOffer_type_name());
		holder.order_pay_type.setText(orderModel_list.get(position).getPay_type());
		return convertView;
	}
}

class OrderTrackHolder {
	TextView order_name=null;
	TextView order_date=null;
	ImageView order_statue=null;
	TextView order_type=null;
	TextView order_pay_type=null;
}
