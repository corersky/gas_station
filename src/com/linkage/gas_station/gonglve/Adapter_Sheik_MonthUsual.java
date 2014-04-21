package com.linkage.gas_station.gonglve;

import java.util.HashMap;

import com.linkage.gas_station.R;
import com.linkage.gas_station.model.Sheik_MonthUsual_Model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class Adapter_Sheik_MonthUsual extends BaseAdapter {
	
	HashMap<String, Sheik_MonthUsual_Model> sheik_model_usual_map=null;
	Context context=null;
	
	public Adapter_Sheik_MonthUsual(HashMap<String, Sheik_MonthUsual_Model> sheik_model_usual_map, Context context) {
		this.sheik_model_usual_map=sheik_model_usual_map;
		this.context=context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return sheik_model_usual_map.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return sheik_model_usual_map.get(""+position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Sheik_MonthUsual_Holder holder=null;
		if(convertView==null) {
			convertView=LayoutInflater.from(context).inflate(R.layout.adapter_wulinassembly_view_sheik_monthusual, null);
			holder=new Sheik_MonthUsual_Holder();
			holder.sheik_monthusual_text_1=(TextView) convertView.findViewById(R.id.sheik_monthusual_text_1);
			holder.sheik_monthusual_text_2=(TextView) convertView.findViewById(R.id.sheik_monthusual_text_2);
			holder.sheik_monthusual_text_3=(TextView) convertView.findViewById(R.id.sheik_monthusual_text_3);
			convertView.setTag(holder);
		}
		else {
			holder=(Sheik_MonthUsual_Holder) convertView.getTag();
		}
		holder.sheik_monthusual_text_1.setText(""+(position+1));
		holder.sheik_monthusual_text_2.setText(sheik_model_usual_map.get(""+position).getTribe_name());
		holder.sheik_monthusual_text_3.setText(sheik_model_usual_map.get(""+position).getSum_flows()+"/"+sheik_model_usual_map.get(""+position).getPsum_flow());
		return convertView;
	}

}

class Sheik_MonthUsual_Holder {
	TextView sheik_monthusual_text_1=null;
	TextView sheik_monthusual_text_2=null;
	TextView sheik_monthusual_text_3=null;
}
