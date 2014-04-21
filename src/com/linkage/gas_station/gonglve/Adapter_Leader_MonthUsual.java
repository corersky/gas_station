package com.linkage.gas_station.gonglve;

import java.util.HashMap;

import com.linkage.gas_station.R;
import com.linkage.gas_station.model.Leader_MonthUsual_Model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class Adapter_Leader_MonthUsual extends BaseAdapter {
	
	HashMap<String, Leader_MonthUsual_Model> leader_model_usual_map=null;
	Context context=null;
	
	public Adapter_Leader_MonthUsual(HashMap<String, Leader_MonthUsual_Model> leader_model_usual_map, Context context) {
		this.leader_model_usual_map=leader_model_usual_map;
		this.context=context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return leader_model_usual_map.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return leader_model_usual_map.get(""+position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Leader_MonthUsual_Holder holder=null;
		if(convertView==null) {
			convertView=LayoutInflater.from(context).inflate(R.layout.adapter_wulinassembly_view_leader_monthusual, null);
			holder=new Leader_MonthUsual_Holder();
			holder.leader_monthusual_text_1=(TextView) convertView.findViewById(R.id.leader_monthusual_text_1);
			holder.leader_monthusual_text_2=(TextView) convertView.findViewById(R.id.leader_monthusual_text_2);
			holder.leader_monthusual_text_3=(TextView) convertView.findViewById(R.id.leader_monthusual_text_3);
			convertView.setTag(holder);
		}
		else {
			holder=(Leader_MonthUsual_Holder) convertView.getTag();
		}
		holder.leader_monthusual_text_1.setText(""+(position+1));
		holder.leader_monthusual_text_2.setText(leader_model_usual_map.get(""+position).getMember_name());
		holder.leader_monthusual_text_3.setText(leader_model_usual_map.get(""+position).getSum_flows()+"/"+leader_model_usual_map.get(""+position).getPsum_flow());
		return convertView;
	}

}

class Leader_MonthUsual_Holder {
	TextView leader_monthusual_text_1=null;
	TextView leader_monthusual_text_2=null;
	TextView leader_monthusual_text_3=null;
}
