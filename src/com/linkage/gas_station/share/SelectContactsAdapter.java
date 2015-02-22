package com.linkage.gas_station.share;

import java.util.ArrayList;

import com.linkage.gas_station.R;
import com.linkage.gas_station.model.ContactModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

public class SelectContactsAdapter extends BaseAdapter {
	
	ArrayList<ContactModel> model_list=null;
	Context context=null;
	int type=0;
	
	public SelectContactsAdapter(Context context, ArrayList<ContactModel> model_list, int type) {
		this.context=context;
		this.model_list=model_list;
		this.type=type;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return model_list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return model_list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		Share_Holder holder=null;
		if(convertView==null) {
			holder=new Share_Holder();
			convertView=LayoutInflater.from(context).inflate(R.layout.adapter_selectcontacts, null);
			holder.contacts_check=(CheckBox) convertView.findViewById(R.id.contacts_check);
			holder.contacts_name=(TextView) convertView.findViewById(R.id.contacts_name);
			holder.contacts_num=(TextView) convertView.findViewById(R.id.contacts_num);
			convertView.setTag(holder);
		}
		else {
			holder=(Share_Holder) convertView.getTag();
		}
		if(type==1) {
			holder.contacts_check.setVisibility(View.VISIBLE);
		}
		else {
			holder.contacts_check.setVisibility(View.GONE);
		}
		if(model_list.get(position).isChecked()) {
			holder.contacts_check.setChecked(true);
		}
		else {
			holder.contacts_check.setChecked(false);
		}
		holder.contacts_check.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(!model_list.get(position).isChecked()) {
					model_list.get(position).setChecked(true);
				}
				else {
					model_list.get(position).setChecked(false);
				}
			}});
		holder.contacts_name.setText(model_list.get(position).getName());
		holder.contacts_num.setText(model_list.get(position).getPhoneNum());
		return convertView;
	}

}

class Share_Holder {
	CheckBox contacts_check=null;
	TextView contacts_name=null;
	TextView contacts_num=null;
}
