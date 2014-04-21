package com.linkage.gas_station.more;

import java.text.DecimalFormat;
import java.util.ArrayList;

import com.linkage.gas_station.R;
import com.linkage.gas_station.model.DoubleFlowModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class Double_flow_list_Adapter extends BaseAdapter {
	
	ArrayList<DoubleFlowModel> doubleFlowModel_list=null;
	Context context=null;
	
	public Double_flow_list_Adapter(ArrayList<DoubleFlowModel> doubleFlowModel_list, Context context) {
		this.context=context;
		this.doubleFlowModel_list=doubleFlowModel_list;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return doubleFlowModel_list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return doubleFlowModel_list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Double_Flow_List_Holder holder=null;
		if(convertView==null) {
			convertView=LayoutInflater.from(context).inflate(R.layout.adapter_double_flow_list, null);
			holder=new Double_Flow_List_Holder();
			holder.title=(TextView) convertView.findViewById(R.id.title);
			holder.residue_flow=(TextView) convertView.findViewById(R.id.residue_flow);
			holder.generate_time=(TextView) convertView.findViewById(R.id.generate_time);
			convertView.setTag(holder);
		}
		else {
			holder=(Double_Flow_List_Holder) convertView.getTag();
		}
		holder.title.setText(doubleFlowModel_list.get(position).getTitle());
		DecimalFormat df=new DecimalFormat("0.0");
		holder.residue_flow.setText(df.format(Double.parseDouble(doubleFlowModel_list.get(position).getResidue_flow()))+"M");
		holder.generate_time.setText(doubleFlowModel_list.get(position).getGenerate_time());
		return convertView;
	}

}

class Double_Flow_List_Holder {
	TextView title=null;
	TextView residue_flow=null;
	TextView generate_time=null;
}
