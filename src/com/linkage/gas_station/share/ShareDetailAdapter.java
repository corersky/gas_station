package com.linkage.gas_station.share;

import java.util.ArrayList;

import com.linkage.gas_station.R;
import com.linkage.gas_station.model.RecommendModel;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class ShareDetailAdapter extends BaseAdapter {
	
	ArrayList<RecommendModel> recommendModel_list=null;
	Context context=null;
	
	public ShareDetailAdapter(ArrayList<RecommendModel> recommendModel_list, Context context) {
		this.recommendModel_list=recommendModel_list;
		this.context=context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return recommendModel_list.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return recommendModel_list.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View convertView, ViewGroup arg2) {
		// TODO Auto-generated method stub
		ShareDetailHolder holder=null;
		if(convertView==null) {
			holder=new ShareDetailHolder();
			convertView=LayoutInflater.from(context).inflate(R.layout.adapter_share_detail, null);
			holder.share_detail_phone=(TextView) convertView.findViewById(R.id.share_detail_phone);
			holder.share_detail_state=(ImageView) convertView.findViewById(R.id.share_detail_state);
			holder.share_detail_time=(TextView) convertView.findViewById(R.id.share_detail_time);
			convertView.setTag(holder);
		}
		else {
			holder=(ShareDetailHolder) convertView.getTag();
		}
		holder.share_detail_phone.setTextColor(Color.BLUE);
		holder.share_detail_time.setTextColor(Color.BLUE);
		holder.share_detail_phone.setText(recommendModel_list.get(arg0).getRecommended_phone());
		if(recommendModel_list.get(arg0).getRecommend_state()==2) {
			holder.share_detail_state.setImageResource(R.drawable.button_shared);
		}
		else {
			holder.share_detail_state.setImageResource(R.drawable.button_sharing);
		}
		holder.share_detail_time.setText(recommendModel_list.get(arg0).getRecommend_time());
		return convertView;
	}

}

class ShareDetailHolder {
	TextView share_detail_phone=null;
	ImageView share_detail_state=null;
	TextView share_detail_time=null;
}
