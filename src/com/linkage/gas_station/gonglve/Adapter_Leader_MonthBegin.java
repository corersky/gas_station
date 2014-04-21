package com.linkage.gas_station.gonglve;

import java.util.HashMap;

import com.linkage.gas_station.R;
import com.linkage.gas_station.model.Leader_MonthBegin_Model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class Adapter_Leader_MonthBegin extends BaseAdapter {
	
	HashMap<String, Leader_MonthBegin_Model> leader_model_begin_map=null;
	Context context=null;
	
	public Adapter_Leader_MonthBegin(HashMap<String, Leader_MonthBegin_Model> leader_model_begin_map, Context context) {
		this.leader_model_begin_map=leader_model_begin_map;
		this.context=context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return leader_model_begin_map.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return leader_model_begin_map.get(""+position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Leader_MonthBegin_Holder holder=null;
		if(convertView==null) {
			convertView=LayoutInflater.from(context).inflate(R.layout.adapter_wulinassembly_view_leader_monthbegin, null);
			holder=new Leader_MonthBegin_Holder();
			holder.leader_monthbegin_check=(CheckBox) convertView.findViewById(R.id.leader_monthbegin_check);
			holder.leader_monthbegin_text_1=(TextView) convertView.findViewById(R.id.leader_monthbegin_text_1);
			holder.leader_monthbegin_text_2=(TextView) convertView.findViewById(R.id.leader_monthbegin_text_2);
			holder.leader_monthbegin_text_3=(TextView) convertView.findViewById(R.id.leader_monthbegin_text_3);
			convertView.setTag(holder);
		}
		else {
			holder=(Leader_MonthBegin_Holder) convertView.getTag();
		}
		if(leader_model_begin_map.get(""+position).isCheck()) {
			holder.leader_monthbegin_check.setChecked(true);
		}
		else {
			holder.leader_monthbegin_check.setChecked(false);
		}
		holder.leader_monthbegin_check.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Leader_MonthBegin_Model model=leader_model_begin_map.get(""+position);
				model.setCheck(model.isCheck()?false:true);
				leader_model_begin_map.put(""+position, model);
			}
		});
		holder.leader_monthbegin_text_1.setText(""+(position+1));
		holder.leader_monthbegin_text_2.setText(leader_model_begin_map.get(""+position).getMember_name());
		holder.leader_monthbegin_text_3.setText(leader_model_begin_map.get(""+position).getSum_flows()+"/"+leader_model_begin_map.get(""+position).getPsum_flow());
		return convertView;
	}

}

class Leader_MonthBegin_Holder {
	CheckBox leader_monthbegin_check=null;
	TextView leader_monthbegin_text_1=null;
	TextView leader_monthbegin_text_2=null;
	TextView leader_monthbegin_text_3=null;
}
