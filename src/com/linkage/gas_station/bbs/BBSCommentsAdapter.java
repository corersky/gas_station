package com.linkage.gas_station.bbs;

import java.util.ArrayList;

import com.linkage.gas_station.R;
import com.linkage.gas_station.model.BbsForumIdModel;
import com.linkage.gas_station.model.BbsForumModel;
import com.linkage.gas_station.util.Util;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class BBSCommentsAdapter extends BaseAdapter {

	Context context=null;
	ArrayList<BbsForumIdModel> model_list=null;
	BbsForumModel model=null;
	
	public BBSCommentsAdapter(ArrayList<BbsForumIdModel> model_list, Context context, BbsForumModel model) {
		this.context=context;
		this.model_list=model_list;
		this.model=model;
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
		CommentsHolder cHolder=null;
		if(convertView==null) {
			convertView=LayoutInflater.from(context).inflate(R.layout.adapter_comments, null);
			cHolder=new CommentsHolder();
			cHolder.bbscomments_header_layout=(LinearLayout) convertView.findViewById(R.id.bbscomments_header_layout);
			cHolder.bbscomments_head_face=(ImageView) convertView.findViewById(R.id.bbscomments_head_face);
			cHolder.bbscomments_head_title=(TextView)convertView.findViewById(R.id.bbscomments_head_title);
			cHolder.bbscomments_head_time=(TextView)convertView.findViewById(R.id.bbscomments_head_time);
			cHolder.bbscomments_head_content=(TextView)convertView.findViewById(R.id.bbscomments_head_content);
			cHolder.bbscomments_nums=(TextView)convertView.findViewById(R.id.bbscomments_nums);
			
			cHolder.bbscomments_layout=(LinearLayout) convertView.findViewById(R.id.bbscomments_layout);
			cHolder.bbscomments_face=(ImageView) convertView.findViewById(R.id.bbscomments_face);
			cHolder.bbscomments_title=(TextView)convertView.findViewById(R.id.bbscomments_title);
			cHolder.bbscomments_area=(TextView) convertView.findViewById(R.id.bbscomments_area);
			cHolder.bscomments_time=(TextView)convertView.findViewById(R.id.bscomments_time);
			cHolder.bscomments_content=(TextView)convertView.findViewById(R.id.bscomments_content);
			cHolder.bscomments_floor=(TextView) convertView.findViewById(R.id.bscomments_floor);
			convertView.setTag(cHolder);
		}
		else {
			cHolder=(CommentsHolder)convertView.getTag();
		}
		if(position==0) {
			cHolder.bbscomments_header_layout.setVisibility(View.VISIBLE);
			cHolder.bbscomments_head_title.setText(model.getForum_name());
			cHolder.bbscomments_head_content.setText(model.getForum_content());
			cHolder.bbscomments_head_time.setText(""+Util.getExtraTime(model.getLast_modify_time()));
			cHolder.bbscomments_layout.setVisibility(View.GONE);
			cHolder.bbscomments_nums.setText("ÆÀÂÛ£º"+model.getReply_times());
			cHolder.bbscomments_head_face.setImageResource(R.drawable.portrait_normal);
		}
		else {
			cHolder.bbscomments_header_layout.setVisibility(View.GONE);
			cHolder.bbscomments_title.setText(model_list.get(position).getPhone_num());
			cHolder.bbscomments_area.setText(model_list.get(position).getArea_name());
			cHolder.bscomments_time.setText(""+Util.getExtraTime(model_list.get(position).getGenerate_time()));
			cHolder.bscomments_content.setText(""+model_list.get(position).getNote_content());
			cHolder.bbscomments_layout.setVisibility(View.VISIBLE);
			cHolder.bscomments_floor.setText("#"+position);
			if(model_list.get(position).getPhoneType()==1) {
				cHolder.bbscomments_face.setImageResource(R.drawable.luntan_apple);
			}
			else {
				cHolder.bbscomments_face.setImageResource(R.drawable.luntan_and);
			}
		}
		return convertView;
	}
	
}

class CommentsHolder {
	LinearLayout bbscomments_header_layout=null;
	ImageView bbscomments_head_face=null;
	TextView bbscomments_head_title=null;
	TextView bbscomments_head_time=null;
	TextView bbscomments_head_content=null;
	TextView bbscomments_nums=null;
	
	LinearLayout bbscomments_layout=null;
	ImageView bbscomments_face=null;
	TextView bbscomments_title=null;
	TextView bbscomments_area=null;
	TextView bscomments_time=null;
	TextView bscomments_content=null;
	TextView bscomments_floor=null;
}
