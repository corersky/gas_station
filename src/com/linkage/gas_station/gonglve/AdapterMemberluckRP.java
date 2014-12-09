package com.linkage.gas_station.gonglve;

import java.util.ArrayList;

import com.linkage.gas_station.R;
import com.linkage.gas_station.model.MemberluckRPModel;

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

public class AdapterMemberluckRP extends BaseAdapter {
	
	ArrayList<MemberluckRPModel> models=null;
	String activityId="";
	Context context=null;
	
	public AdapterMemberluckRP(Context context, ArrayList<MemberluckRPModel> models, String activityId) {
		this.context=context;
		this.models=models;
		this.activityId=activityId;
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
		MemberluckRpHolder holder=null;
		if(convertView==null) {
			convertView=LayoutInflater.from(context).inflate(R.layout.adapter_memberluck_rp, null);
			holder=new MemberluckRpHolder();
			holder.adapter_memberluck_rp_name=(TextView) convertView.findViewById(R.id.adapter_memberluck_rp_name);
			holder.adapter_memberluck_rp_num=(TextView) convertView.findViewById(R.id.adapter_memberluck_rp_num);
			holder.adapter_memberluck_rp_drawnum=(TextView) convertView.findViewById(R.id.adapter_memberluck_rp_drawnum);
			holder.adapter_memberluck_rp_oper=(TextView) convertView.findViewById(R.id.adapter_memberluck_rp_oper);
			holder.adapter_memberluck_rp_layout=(LinearLayout) convertView.findViewById(R.id.adapter_memberluck_rp_layout);
			convertView.setTag(holder);
		}
		else {
			holder=(MemberluckRpHolder) convertView.getTag();
		}
		if(position%2==0) {
			holder.adapter_memberluck_rp_layout.setBackgroundColor(Color.parseColor("#eeeeee"));
		}
		else {
			holder.adapter_memberluck_rp_layout.setBackgroundColor(Color.WHITE);
		}
		holder.adapter_memberluck_rp_name.setTextColor(Color.BLACK);
		holder.adapter_memberluck_rp_num.setTextColor(Color.parseColor("#f39c12"));
		holder.adapter_memberluck_rp_num.setText("+"+models.get(position).getTask_benefits());
		holder.adapter_memberluck_rp_drawnum.setTextColor(Color.parseColor("#e74c3c"));
		holder.adapter_memberluck_rp_drawnum.setText("+"+models.get(position).getTask_benefits());
		holder.adapter_memberluck_rp_oper.setVisibility(View.VISIBLE);
		if(models.get(position).getTask_type()==1) {
			holder.adapter_memberluck_rp_oper.setText("订购");
			holder.adapter_memberluck_rp_oper.setBackgroundResource(R.drawable.memberluck_red);
			holder.adapter_memberluck_rp_oper.setOnClickListener(new TextView.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent=new Intent(context, CommonMessageDialogActivity.class);
					Bundle bundle=new Bundle();
					bundle.putString("offerId", ""+models.get(position).getOffer_id());
					bundle.putString("activityId", activityId);
					intent.putExtras(bundle);
					context.startActivity(intent);
				}});
			holder.adapter_memberluck_rp_name.setText(models.get(position).getOffer_name());
		}
		else if(models.get(position).getTask_type()==2) {
			holder.adapter_memberluck_rp_name.setText(models.get(position).getTask_name());
			holder.adapter_memberluck_rp_oper.setText("领取奖励");
			if(models.get(position).getState()==0) {
				holder.adapter_memberluck_rp_oper.setBackgroundResource(R.drawable.memberluck_gray);
				holder.adapter_memberluck_rp_oper.setOnClickListener(new TextView.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						
					}});
			}
			else if(models.get(position).getState()==1) {
				holder.adapter_memberluck_rp_oper.setBackgroundResource(R.drawable.memberluck_yellow);
				holder.adapter_memberluck_rp_oper.setOnClickListener(new TextView.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						((MemberluckRPActivity) context).getPrizeById(Long.parseLong(""+models.get(position).getTask_id()));
					}});
			}
			else if(models.get(position).getState()==2) {
				holder.adapter_memberluck_rp_oper.setBackgroundResource(R.drawable.memberluck_gray);
				holder.adapter_memberluck_rp_oper.setOnClickListener(new TextView.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						
					}});
			}
		}
		else if(models.get(position).getTask_type()==3) {
			holder.adapter_memberluck_rp_name.setText(models.get(position).getTask_name());
			if(models.get(position).getState()==0) {
				holder.adapter_memberluck_rp_oper.setText("转发");
				holder.adapter_memberluck_rp_oper.setBackgroundResource(R.drawable.memberluck_blue);
				holder.adapter_memberluck_rp_oper.setOnClickListener(new TextView.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						((MemberluckRPActivity) context).showShare("0");
					}});
			}
			else if(models.get(position).getState()==1) {
				holder.adapter_memberluck_rp_oper.setText("领取奖励");
				holder.adapter_memberluck_rp_oper.setBackgroundResource(R.drawable.memberluck_yellow);
				holder.adapter_memberluck_rp_oper.setOnClickListener(new TextView.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						((MemberluckRPActivity) context).getPrizeById(Long.parseLong(""+models.get(position).getTask_id()));
					}});
			}
			else if(models.get(position).getState()==2) {
				holder.adapter_memberluck_rp_oper.setText("领取奖励");
				holder.adapter_memberluck_rp_oper.setBackgroundResource(R.drawable.memberluck_gray);
				holder.adapter_memberluck_rp_oper.setOnClickListener(new TextView.OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						
					}});
			}
		}
		return convertView;
	}

}

class MemberluckRpHolder {
	LinearLayout adapter_memberluck_rp_layout=null;
	TextView adapter_memberluck_rp_name=null;
	TextView adapter_memberluck_rp_num=null;
	TextView adapter_memberluck_rp_drawnum=null;
	TextView adapter_memberluck_rp_oper=null;
}