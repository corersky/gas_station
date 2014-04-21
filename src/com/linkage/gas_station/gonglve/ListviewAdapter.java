package com.linkage.gas_station.gonglve;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.linkage.gas_station.R;

class ListviewAdapter extends BaseAdapter
{
	ArrayList<TypeObject> typelist = null;
	Context context = null;
	
	int select_type = -1;
	
	public ListviewAdapter(Context con)
	{
		context = con;
	}
	
	public void setList(ArrayList<TypeObject> lists)
	{
		typelist = lists;
	}
	
	public void setSelect(int select)
	{
		select_type = select;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return typelist.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return typelist.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View conventview, ViewGroup arg2) {
		// TODO Auto-generated method stub
		Holder holder = null;
		if(conventview == null) {
			conventview = LayoutInflater.from(context).inflate(R.layout.type_list_item, null);
			holder = new Holder();
			holder.name = (TextView)conventview.findViewById(R.id.typename);
			holder.desText = (TextView)conventview.findViewById(R.id.typedes);
			holder.icon = (ImageView)conventview.findViewById(R.id.itemImageCoin);
			holder.typename2=(TextView) conventview.findViewById(R.id.typename2);
			conventview.setTag(holder);  
		}
		else {
			holder = (Holder) conventview.getTag();
		}		
		if(typelist.get(arg0).getTypeName().indexOf("$")!=-1) {
			holder.typename2.setVisibility(View.VISIBLE);
			holder.name.setText(typelist.get(arg0).getTypeName().substring(0, typelist.get(arg0).getTypeName().indexOf("$")));
			holder.typename2.setText(typelist.get(arg0).getTypeName().substring(typelist.get(arg0).getTypeName().indexOf("$")+1));
		}
		else {
			holder.typename2.setVisibility(View.GONE);
			holder.name.setText(typelist.get(arg0).getTypeName());
		}
		holder.desText.setText(typelist.get(arg0).getTypeDes());
		if(arg0 == select_type)
		{
			holder.icon.setImageResource(R.drawable.pop_select);
		}
		else
		{
			holder.icon.setImageDrawable(null);
		}
		return conventview;
	}
	
}

class Holder
{
	TextView name;
	TextView desText;
	ImageView icon;
	TextView typename2;
}
