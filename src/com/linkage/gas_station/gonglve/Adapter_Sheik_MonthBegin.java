package com.linkage.gas_station.gonglve;

import java.util.HashMap;

import com.linkage.gas_station.R;
import com.linkage.gas_station.model.Sheik_MonthBegin_Model;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class Adapter_Sheik_MonthBegin extends BaseAdapter {
	
	HashMap<String, Sheik_MonthBegin_Model> model_map=null;
	Context context=null;
	
	public Adapter_Sheik_MonthBegin(HashMap<String, Sheik_MonthBegin_Model> model_map, Context context) {
		this.model_map=model_map;
		this.context=context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return model_map.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return model_map.get(""+position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Sheik_MonthBegin_Holder holder=null;
		if(convertView==null) {
			holder=new Sheik_MonthBegin_Holder();
			convertView=LayoutInflater.from(context).inflate(R.layout.adapter_wulinassembly_view_sheik_monthbegin, null);
			holder.sheik_monthbegin_check=(CheckBox) convertView.findViewById(R.id.sheik_monthbegin_check);
			holder.sheik_monthbegin_text_1=(TextView) convertView.findViewById(R.id.sheik_monthbegin_text_1);
			holder.sheik_monthbegin_text_2=(TextView) convertView.findViewById(R.id.sheik_monthbegin_text_2);
			holder.sheik_monthbegin_text_3=(TextView) convertView.findViewById(R.id.sheik_monthbegin_text_3);
			convertView.setTag(holder);
		}
		else {
			holder=(Sheik_MonthBegin_Holder) convertView.getTag();
		}
		if(model_map.get(""+position).isCheck()) {
			holder.sheik_monthbegin_check.setChecked(true);
		}
		else {
			holder.sheik_monthbegin_check.setChecked(false);
		}
		holder.sheik_monthbegin_check.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Sheik_MonthBegin_Model model=model_map.get(""+position);
				model.setCheck(model.isCheck()?false:true);
				model_map.put(""+position, model);
			}
		});
		holder.sheik_monthbegin_text_1.setText(""+(position+1));
		holder.sheik_monthbegin_text_2.setText(model_map.get(""+position).getTribe_name());
		holder.sheik_monthbegin_text_3.setText(model_map.get(""+position).getSum_flows()+"/"+model_map.get(""+position).getPsum_flow());
		return convertView;
	}

}

class Sheik_MonthBegin_Holder {
	CheckBox sheik_monthbegin_check=null;
	TextView sheik_monthbegin_text_1=null;
	TextView sheik_monthbegin_text_2=null;
	TextView sheik_monthbegin_text_3=null;
}
