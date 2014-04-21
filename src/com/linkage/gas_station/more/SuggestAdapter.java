package com.linkage.gas_station.more;

import java.util.ArrayList;

import com.linkage.gas_station.R;
import com.linkage.gas_station.model.FeedBackModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SuggestAdapter extends BaseAdapter {
	
	ArrayList<FeedBackModel> model_list=null;
	Context context=null;
	
	public SuggestAdapter(ArrayList<FeedBackModel> model_list, Context context) {
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
		Suggest_Holder holder=null;
		if(convertView==null) {
			holder=new Suggest_Holder();
			convertView=LayoutInflater.from(context).inflate(R.layout.adapter_chart, null);
			holder.feed_back_l_layout=(LinearLayout) convertView.findViewById(R.id.feed_back_l_layout);
			holder.feed_back_l=(TextView) convertView.findViewById(R.id.feed_back_l);
			holder.feed_back_l_time=(TextView) convertView.findViewById(R.id.feed_back_l_time);
			holder.feed_back_r_layout=(LinearLayout) convertView.findViewById(R.id.feed_back_r_layout);
			holder.feed_back_r=(TextView) convertView.findViewById(R.id.feed_back_r);
			holder.feed_back_r_time=(TextView) convertView.findViewById(R.id.feed_back_r_time);
			convertView.setTag(holder);
		}
		else {
			holder=(Suggest_Holder) convertView.getTag();
		}

		holder.feed_back_r.setText(model_list.get(position).getFeed_back());
		holder.feed_back_r_time.setText(model_list.get(position).getGenerate_time());
		holder.feed_back_l.setText(model_list.get(position).getReply());
		holder.feed_back_l_time.setText(model_list.get(position).getDeal_time());
		if(model_list.get(position).getGenerate_time().equals("")) {
			holder.feed_back_l_layout.setVisibility(View.VISIBLE);
			holder.feed_back_r_layout.setVisibility(View.GONE);
		}
		else if(model_list.get(position).getDeal_time().equals("")) {
			holder.feed_back_l_layout.setVisibility(View.GONE);
			holder.feed_back_r_layout.setVisibility(View.VISIBLE);
		}
		else {
			holder.feed_back_l_layout.setVisibility(View.VISIBLE);
			holder.feed_back_r_layout.setVisibility(View.VISIBLE);
		}
		return convertView;
	}

}

class Suggest_Holder {
	LinearLayout feed_back_l_layout=null;
	TextView feed_back_l=null;
	TextView feed_back_l_time=null;
	LinearLayout feed_back_r_layout=null;
	TextView feed_back_r=null;
	TextView feed_back_r_time=null;
}
