package com.linkage.gas_station.gonglve;

import java.util.ArrayList;

import com.linkage.gas_station.R;
import com.linkage.gas_station.model.GonglveModel1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class Adapter_Gonglve_Title_1 extends BaseAdapter {
	
	ArrayList<GonglveModel1> gonglveModel1_list=null;
	Context context=null;
	
	public Adapter_Gonglve_Title_1(Context context, ArrayList<GonglveModel1> gonglveModel1_list) {
		this.context=context;
		this.gonglveModel1_list=gonglveModel1_list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return gonglveModel1_list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return gonglveModel1_list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Gonglve_title_1_Holder holder=null;
		if(convertView==null) {
			holder=new Gonglve_title_1_Holder();
			convertView=LayoutInflater.from(context).inflate(R.layout.adapter_gonglve_title_1, null);
			holder.gonglve_title_1_name=(TextView) convertView.findViewById(R.id.gonglve_title_1_name);
			holder.gonglve_title_1_date=(TextView) convertView.findViewById(R.id.gonglve_title_1_date);
			holder.gonglve_title_1_go=(ImageView) convertView.findViewById(R.id.gonglve_title_1_go);
			convertView.setTag(holder);
		}
		else {
			holder=(Gonglve_title_1_Holder) convertView.getTag();
		}
		holder.gonglve_title_1_name.setText(gonglveModel1_list.get(position).getStrategy_description());
		holder.gonglve_title_1_date.setText(gonglveModel1_list.get(position).getGenerate_time());
		switch(Integer.parseInt(gonglveModel1_list.get(position).getStrategy_type())) {
		case 1:
			holder.gonglve_title_1_go.setVisibility(View.GONE);
			break;
		case 2:
			holder.gonglve_title_1_go.setImageResource(R.drawable.yijianjiayou);
			holder.gonglve_title_1_go.setVisibility(View.VISIBLE);
			break;
		case 3:
			holder.gonglve_title_1_go.setImageResource(R.drawable.lijicanyu);
			holder.gonglve_title_1_go.setVisibility(View.VISIBLE);
			break;
		case 4:
			holder.gonglve_title_1_go.setImageResource(R.drawable.chakanxiangqing);
			holder.gonglve_title_1_go.setVisibility(View.VISIBLE);
			break;
		case 5:
			holder.gonglve_title_1_go.setImageResource(R.drawable.lijicanyu);
			holder.gonglve_title_1_go.setVisibility(View.VISIBLE);
			break;
		case 7:
			holder.gonglve_title_1_go.setImageResource(R.drawable.yijianshezhi);
			holder.gonglve_title_1_go.setVisibility(View.VISIBLE);
			break;
		case 8:
			holder.gonglve_title_1_go.setImageResource(R.drawable.yijianjiaofei);
			holder.gonglve_title_1_go.setVisibility(View.VISIBLE);
			break;
		}
		return convertView;
	}

}

class Gonglve_title_1_Holder {
	TextView gonglve_title_1_name=null;
	TextView gonglve_title_1_date=null;
	ImageView gonglve_title_1_go=null;
}
