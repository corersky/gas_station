package com.linkage.gas_station.gonglve;

import java.util.ArrayList;

import com.hb.views.PinnedSectionListView.PinnedSectionListAdapter;
import com.linkage.gas_station.R;
import com.linkage.gas_station.model.NiubilityModel;

import android.content.Context;
import android.graphics.Color;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NiubilityAdapter extends BaseAdapter implements PinnedSectionListAdapter {

	ArrayList<NiubilityModel> models=null;
	Context context=null;
	
	public NiubilityAdapter(Context context, ArrayList<NiubilityModel> models) {
		this.context=context;
		this.models=models;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return models.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return models.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		if(models.get(arg0).getType()==NiubilityModel.SECTION) {
			NiubilitySectionHolder holder=null;
			if(arg1==null) {
				arg1=LayoutInflater.from(context).inflate(R.layout.adapter_niubilitysection, null);
				holder=new NiubilitySectionHolder();
				holder.adapter_niubilitysection_time=(TextView) arg1.findViewById(R.id.adapter_niubilitysection_time);
				holder.adapter_niubilitysection_desp=(TextView) arg1.findViewById(R.id.adapter_niubilitysection_desp);
				holder.adapter_niubilitysection_endtime_desp=(TextView) arg1.findViewById(R.id.adapter_niubilitysection_endtime_desp);
				holder.adapter_niubilitysection_image=(ImageView) arg1.findViewById(R.id.adapter_niubilitysection_image);
				arg1.setTag(holder);
			}
			else {
				holder=(NiubilitySectionHolder) arg1.getTag();
			}
			holder.adapter_niubilitysection_time.setText(models.get(arg0).getEndTime());
			holder.adapter_niubilitysection_desp.setText(models.get(arg0).getSection_desp());
			holder.adapter_niubilitysection_endtime_desp.setText(models.get(arg0).getEndtime_desp());
			if(models.get(arg0).getSection_desp().equals("人缘最好")) {
				holder.adapter_niubilitysection_image.setImageResource(R.drawable.niubilitysection_adapter_1);
			}
			else if(models.get(arg0).getSection_desp().equals("圈子最广")) {
				holder.adapter_niubilitysection_image.setImageResource(R.drawable.niubilitysection_adapter_2);
			}
			else if(models.get(arg0).getSection_desp().equals("最土豪")) {
				holder.adapter_niubilitysection_image.setImageResource(R.drawable.niubilitysection_adapter_3);
			}
		}
		else {
			NiubilityItemHolder holder=null;
			if(arg1==null) {
				arg1=LayoutInflater.from(context).inflate(R.layout.adapter_niubilityitem, null);
				holder=new NiubilityItemHolder();
				holder.adapter_niubilityitem_phonenum=(TextView) arg1.findViewById(R.id.adapter_niubilityitem_phonenum);
				holder.adapter_niubilityitem_number=(TextView) arg1.findViewById(R.id.adapter_niubilityitem_number);
				holder.adapter_niubilityitem_detailinfo=(TextView) arg1.findViewById(R.id.adapter_niubilityitem_detailinfo);
				holder.adapter_niubilityitem_layout=(LinearLayout) arg1.findViewById(R.id.adapter_niubilityitem_layout);
				holder.adapter_niubilityitem_number_image=(ImageView) arg1.findViewById(R.id.adapter_niubilityitem_number_image);
				holder.adapter_niubilityitem_line=(View) arg1.findViewById(R.id.adapter_niubilityitem_line); 
				arg1.setTag(holder);
			}
			else {
				holder=(NiubilityItemHolder) arg1.getTag();
			}
			holder.adapter_niubilityitem_phonenum.setText(models.get(arg0).getPhoneNum());
			holder.adapter_niubilityitem_number.setText(""+models.get(arg0).getPosition());
			holder.adapter_niubilityitem_detailinfo.setText(models.get(arg0).getItem_desp());
			if(models.get(arg0).getPosition()%2==0) {
				holder.adapter_niubilityitem_layout.setBackgroundColor(Color.parseColor("#e7edf6"));
			}
			else {
				holder.adapter_niubilityitem_layout.setBackgroundColor(Color.WHITE);
			}
			if(models.get(arg0).getPosition()<=3) {
				holder.adapter_niubilityitem_number_image.setVisibility(View.VISIBLE);
				if(models.get(arg0).getPosition()==1) {
					holder.adapter_niubilityitem_number_image.setImageResource(R.drawable.niubilitysection_adapter_pos_1);
				}
				else if(models.get(arg0).getPosition()==2) {
					holder.adapter_niubilityitem_number_image.setImageResource(R.drawable.niubilitysection_adapter_pos_2);
				}
				else if(models.get(arg0).getPosition()==3) {
					holder.adapter_niubilityitem_number_image.setImageResource(R.drawable.niubilitysection_adapter_pos_3);
				}
				holder.adapter_niubilityitem_number.setVisibility(View.GONE);
			}
			else {
				holder.adapter_niubilityitem_number.setVisibility(View.VISIBLE);
				holder.adapter_niubilityitem_number_image.setVisibility(View.GONE);
				holder.adapter_niubilityitem_number.setTextColor(Color.parseColor("#333333"));
			}
			if(models.get(arg0).isLast()) {
				holder.adapter_niubilityitem_line.setVisibility(View.VISIBLE);
			}
			else {
				holder.adapter_niubilityitem_line.setVisibility(View.GONE);
			}
		}
		return arg1;
	}

	@Override
	public boolean isItemViewTypePinned(int viewType) {
		// TODO Auto-generated method stub
		return viewType == NiubilityModel.SECTION;
	}
	
	@Override 
	public int getViewTypeCount() {
		return 2;
	}
	
	@Override 
	public int getItemViewType(int position) {
		return models.get(position).getType();
	}

}

class NiubilitySectionHolder {
	ImageView adapter_niubilitysection_image=null;
	TextView adapter_niubilitysection_time=null;
	TextView adapter_niubilitysection_desp=null;
	TextView adapter_niubilitysection_endtime_desp=null;
}

class NiubilityItemHolder {
	TextView adapter_niubilityitem_phonenum=null;
	TextView adapter_niubilityitem_number=null;
	TextView adapter_niubilityitem_detailinfo=null;
	LinearLayout adapter_niubilityitem_layout=null;
	ImageView adapter_niubilityitem_number_image=null;
	View adapter_niubilityitem_line=null;
}
