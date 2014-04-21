package com.linkage.gas_station.gonglve;

import java.util.ArrayList;

import com.linkage.gas_station.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

public class LuckDrawAdapter extends BaseAdapter {
	ArrayList<String> strs=null;
	Context context=null;
	
	public LuckDrawAdapter(ArrayList<String> strs, Context context) {
		this.strs=strs;
		this.context=context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return strs.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return strs.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		LuckDrawHolder holder=null;
		if(arg1==null) {
			arg1=LayoutInflater.from(context).inflate(R.layout.adapter_share_lottery_notice_whole_list, null);
			holder=new LuckDrawHolder();
			holder.notice_title_layout=(LinearLayout) arg1.findViewById(R.id.notice_title_layout);
			holder.notice_title=(TextView) arg1.findViewById(R.id.notice_title);
			holder.notice_title_desp=(TextView) arg1.findViewById(R.id.notice_title_desp);
			holder.notice_title_phone=(TextView) arg1.findViewById(R.id.notice_title_phone);
			arg1.setTag(holder);
		}
		else {
			holder=(LuckDrawHolder) arg1.getTag();
		}
		if(strs.get(arg0).substring(0, 1).equals("1")) {
			holder.notice_title_layout.setVisibility(View.VISIBLE);
			holder.notice_title_phone.setVisibility(View.GONE);
			holder.notice_title.setText(strs.get(arg0).split("&")[1]);
			holder.notice_title_desp.setText(strs.get(arg0).split("&")[2]);
		}
		else {
			holder.notice_title_layout.setVisibility(View.GONE);
			holder.notice_title_phone.setVisibility(View.VISIBLE);
			holder.notice_title_phone.setText(strs.get(arg0).split("&")[1]);
		}
		return arg1;
	}

}

class LuckDrawHolder {
	LinearLayout notice_title_layout=null;
	TextView notice_title=null;
	TextView notice_title_desp=null;
	TextView notice_title_phone=null;
}
