package com.linkage.gas_station.gonglve;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.linkage.gas_station.R;
import com.linkage.gas_station.model.MemberluckUpdate;

public class AdapterMemberluckUpdate extends BaseAdapter {
	
	ArrayList<MemberluckUpdate> models=null;
	Context context=null;
	
	public AdapterMemberluckUpdate(Context context, ArrayList<MemberluckUpdate> models) {
		this.context=context;
		this.models=models;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return models.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return models.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		MemberluckUpdateHolder holder=null;
		if(convertView==null) {
			convertView=LayoutInflater.from(context).inflate(R.layout.adapter_memberluck_update, null);
			holder=new MemberluckUpdateHolder();
			holder.adapter_memberluck_rp_update_level=(TextView) convertView.findViewById(R.id.adapter_memberluck_rp_update_level);
			holder.adapter_memberluck_rp_update_limit=(TextView) convertView.findViewById(R.id.adapter_memberluck_rp_update_limit);
			holder.adapter_memberluck_rp_update_benefits1=(TextView) convertView.findViewById(R.id.adapter_memberluck_rp_update_benefits1);
			holder.adapter_memberluck_rp_update_benefits2=(TextView) convertView.findViewById(R.id.adapter_memberluck_rp_update_benefits2);
			holder.adapter_memberluck_rp_update_oper=(TextView) convertView.findViewById(R.id.adapter_memberluck_rp_update_oper);
			holder.adapter_memberluck_rp_update_layout=(LinearLayout) convertView.findViewById(R.id.adapter_memberluck_rp_update_layout);
			convertView.setTag(holder);
		}
		else {
			holder=(MemberluckUpdateHolder) convertView.getTag();
		}
		if(position%2==0) {
			holder.adapter_memberluck_rp_update_layout.setBackgroundColor(Color.parseColor("#eeeeee"));
		}
		else {
			holder.adapter_memberluck_rp_update_layout.setBackgroundColor(Color.WHITE);
		}
		holder.adapter_memberluck_rp_update_level.setTextColor(Color.BLACK);
		holder.adapter_memberluck_rp_update_level.setText(models.get(position).getVip_name());
		holder.adapter_memberluck_rp_update_limit.setTextColor(Color.BLACK);
		holder.adapter_memberluck_rp_update_limit.setText(models.get(position).getOffer_name());
		holder.adapter_memberluck_rp_update_benefits1.setVisibility(View.VISIBLE);
		holder.adapter_memberluck_rp_update_benefits1.setTextColor(Color.BLACK);
		holder.adapter_memberluck_rp_update_benefits1.setText("每月人品值+"+models.get(position).getVip_benefits());
		holder.adapter_memberluck_rp_update_benefits2.setTextColor(Color.BLACK);
		holder.adapter_memberluck_rp_update_benefits2.setVisibility(View.VISIBLE);
		holder.adapter_memberluck_rp_update_benefits2.setText("抽奖次数+"+models.get(position).getVip_benefits());
		holder.adapter_memberluck_rp_update_oper.setVisibility(View.VISIBLE);
		holder.adapter_memberluck_rp_update_oper.setText("升级");
		holder.adapter_memberluck_rp_update_oper.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				((MemberluckUpdateActivity) context).pay(""+models.get(position).getOffer_id());
			}});
		return convertView;
	}

}

class MemberluckUpdateHolder {
	TextView adapter_memberluck_rp_update_level=null;
	TextView adapter_memberluck_rp_update_limit=null;
	TextView adapter_memberluck_rp_update_benefits1=null;
	TextView adapter_memberluck_rp_update_benefits2=null;
	TextView adapter_memberluck_rp_update_oper=null;
	LinearLayout adapter_memberluck_rp_update_layout=null;
}
