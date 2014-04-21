package com.linkage.gas_station.bbs;

import java.util.ArrayList;

import com.linkage.gas_station.R;
import com.linkage.gas_station.model.BbsForumModel;
import com.linkage.gas_station.util.Util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BBSAdapter extends BaseAdapter {
	
	ArrayList<BbsForumModel> model_list=null;
	Context context=null;
	
	public BBSAdapter(ArrayList<BbsForumModel> model_list, Context context) {
		this.model_list=model_list;
		this.context=context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return model_list.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return model_list.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		BBSHolder holder=null;
		if(convertView==null) {
			holder=new BBSHolder();
			convertView=LayoutInflater.from(context).inflate(R.layout.adapter_bbsmain, null);
			holder.bbsmain_title=(TextView) convertView.findViewById(R.id.bbsmain_title);
			holder.bbsmain_time=(TextView) convertView.findViewById(R.id.bbsmain_time);
			holder.bbsmain_phonenum=(TextView) convertView.findViewById(R.id.bbsmain_phonenum);
			holder.bbsmain_isTop=(ImageView) convertView.findViewById(R.id.bbsmain_isTop);
			holder.bbsmain_commentsnum=(TextView) convertView.findViewById(R.id.bbsmain_commentsnum);
			holder.bbsmain_fav=(ImageView) convertView.findViewById(R.id.bbsmain_fav);
			convertView.setTag(holder);
		}
		else {
			holder=(BBSHolder) convertView.getTag();
		}
		holder.bbsmain_title.setText(""+model_list.get(position).getForum_name());
		holder.bbsmain_time.setText(Util.getExtraTime(model_list.get(position).getLast_modify_time()));
		holder.bbsmain_phonenum.setText(model_list.get(position).getPhone_num());
		holder.bbsmain_commentsnum.setText(""+model_list.get(position).getReply_times());
		if(model_list.get(position).getIs_top()==1) {
			holder.bbsmain_isTop.setVisibility(View.VISIBLE);
		}
		else {
			holder.bbsmain_isTop.setVisibility(View.GONE);
		}
		if(model_list.get(position).getForum_type()==2) {
			holder.bbsmain_fav.setVisibility(View.VISIBLE);
		}
		else {
			holder.bbsmain_fav.setVisibility(View.GONE);
		}
		return convertView;
	}

}

class BBSHolder {
	TextView bbsmain_title=null;
	TextView bbsmain_time=null;
	TextView bbsmain_phonenum=null;
	ImageView bbsmain_isTop=null;
	TextView bbsmain_commentsnum=null;
	ImageView bbsmain_fav=null;
}
