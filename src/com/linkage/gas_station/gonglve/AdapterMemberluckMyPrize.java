package com.linkage.gas_station.gonglve;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.linkage.gas_station.R;
import com.linkage.gas_station.model.MemberluckMyPrizeModel;

public class AdapterMemberluckMyPrize extends BaseAdapter {

	ArrayList<MemberluckMyPrizeModel> models=null;
	Context context=null;
	boolean isShowGet=true;
	
	public AdapterMemberluckMyPrize(Context context, ArrayList<MemberluckMyPrizeModel> models, boolean isShowGet) {
		this.context=context;
		this.models=models;
		this.isShowGet=isShowGet;
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
		MemberluckMyPrizeHolder holder=null;
		if(convertView==null) {
			convertView=LayoutInflater.from(context).inflate(R.layout.adapter_memberluck_myprize, null);
			holder=new MemberluckMyPrizeHolder();
			holder.memberluck_myprize_time=(TextView) convertView.findViewById(R.id.memberluck_myprize_time);
			holder.memberluck_myprize_prize=(TextView) convertView.findViewById(R.id.memberluck_myprize_prize);
			holder.memberluck_myprize_oper=(TextView) convertView.findViewById(R.id.memberluck_myprize_oper);
			holder.memberluck_myprize_prize_layout=(LinearLayout) convertView.findViewById(R.id.memberluck_myprize_prize_layout);
			convertView.setTag(holder);
		}
		else {
			holder=(MemberluckMyPrizeHolder) convertView.getTag();
		}
		if(position%2==0) {
			holder.memberluck_myprize_prize_layout.setBackgroundColor(Color.parseColor("#eeeeee"));
		}
		else {
			holder.memberluck_myprize_prize_layout.setBackgroundColor(Color.WHITE);
		}
		holder.memberluck_myprize_time.setTextColor(Color.BLACK);
		holder.memberluck_myprize_time.setText(models.get(position).getGenerate_time());
		holder.memberluck_myprize_prize.setTextColor(Color.BLACK);
		if(isShowGet) {
			holder.memberluck_myprize_prize.setText(models.get(position).getLevel_name());
			holder.memberluck_myprize_oper.setVisibility(View.VISIBLE);
			holder.memberluck_myprize_oper.setText("¡Ï»°");
			holder.memberluck_myprize_oper.setTextColor(Color.WHITE);
		}
		else {
			holder.memberluck_myprize_prize.setText(models.get(position).getPrize_name());
			holder.memberluck_myprize_oper.setVisibility(View.INVISIBLE);
		}
		if(models.get(position).getState()==1) {
			holder.memberluck_myprize_oper.setBackgroundResource(R.drawable.memberluck_gray);
		}
		else {
			holder.memberluck_myprize_oper.setBackgroundResource(R.drawable.memberluck_red);
		}
		holder.memberluck_myprize_oper.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(models.get(position).getState()==0) {
					((MemberluckMyPrizeActivity) context).getPrizeById(Long.parseLong(models.get(position).getUserwin_id()));
				}
			}});
		
		return convertView;
	}

}

class MemberluckMyPrizeHolder {
	LinearLayout memberluck_myprize_prize_layout=null;
	TextView memberluck_myprize_time=null;
	TextView memberluck_myprize_prize=null;
	TextView memberluck_myprize_oper=null;
}
