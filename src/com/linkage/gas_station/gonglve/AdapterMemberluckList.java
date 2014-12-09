package com.linkage.gas_station.gonglve;

import java.util.ArrayList;

import com.linkage.gas_station.R;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AdapterMemberluckList extends BaseAdapter {
	
	ArrayList<String> strs=null;
	Context context=null;
	
	public AdapterMemberluckList(Context context, ArrayList<String> strs) {
		this.context=context;
		this.strs=strs;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return strs.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return strs.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		MemberluckListHolder holder=null;
		if(convertView==null) {
			convertView=LayoutInflater.from(context).inflate(R.layout.adapter_memberluck_list, null);
			holder=new MemberluckListHolder();
			holder.adapter_memberluck_list_phonenum=(TextView) convertView.findViewById(R.id.adapter_memberluck_list_phonenum);
			holder.adapter_memberluck_list_rp=(TextView) convertView.findViewById(R.id.adapter_memberluck_list_rp);
			holder.adapter_memberluck_list_layout=(LinearLayout) convertView.findViewById(R.id.adapter_memberluck_list_layout);
			convertView.setTag(holder);
		}
		else {
			holder=(MemberluckListHolder) convertView.getTag();
		}
		if(position%2==0) {
			holder.adapter_memberluck_list_layout.setBackgroundColor(Color.parseColor("#eeeeee"));
		}
		else {
			holder.adapter_memberluck_list_layout.setBackgroundColor(Color.WHITE);
		}
		holder.adapter_memberluck_list_phonenum.setTextColor(Color.BLACK);
		holder.adapter_memberluck_list_phonenum.setText(strs.get(position).split("&")[0]);
		holder.adapter_memberluck_list_rp.setTextColor(Color.BLACK);
		holder.adapter_memberluck_list_rp.setText(strs.get(position).split("&")[1]);
		return convertView;
	}

}

class MemberluckListHolder {
	LinearLayout adapter_memberluck_list_layout=null;
	TextView adapter_memberluck_list_phonenum=null;
	TextView adapter_memberluck_list_rp=null;
}