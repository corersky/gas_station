package com.linkage.gas_station.gonglve;

import java.util.ArrayList;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.R;
import com.linkage.gas_station.model.RedPacketsModel;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RedenvelopeAdapter extends BaseAdapter {
	
	ArrayList<RedPacketsModel> strs=null;
	Context context=null;
	RedenvelopeSearchActivity activity=null;
	
	public RedenvelopeAdapter(Context context, ArrayList<RedPacketsModel> strs, RedenvelopeSearchActivity activity) {
		this.strs=strs;
		this.context=context;
		this.activity=activity;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return strs.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		RedenvelopeHolder holder=null;
		if(arg1==null) {
			holder=new RedenvelopeHolder();
			arg1=LayoutInflater.from(context).inflate(R.layout.adapter_redenvelope, null);
			holder.adapter_redenvelope_state_image=(ImageView) arg1.findViewById(R.id.adapter_redenvelope_state_image);
			holder.adapter_redenvelope_name=(TextView) arg1.findViewById(R.id.adapter_redenvelope_name);
			holder.adapter_redenvelope_time=(TextView) arg1.findViewById(R.id.adapter_redenvelope_time);
			holder.adapter_redenvelope_state=(TextView) arg1.findViewById(R.id.adapter_redenvelope_state);
			holder.adapter_redenvelope_lastnum=(TextView) arg1.findViewById(R.id.adapter_redenvelope_lastnum);
			holder.adapter_redenvelope_send=(ImageView) arg1.findViewById(R.id.adapter_redenvelope_send);
			holder.adapter_redenvelope_layout=(LinearLayout) arg1.findViewById(R.id.adapter_redenvelope_layout);
			arg1.setTag(holder);
		}
		else {
			holder=(RedenvelopeHolder) arg1.getTag();
		}
		holder.adapter_redenvelope_name.setText(strs.get(arg0).getOffer_name());
		holder.adapter_redenvelope_time.setText(strs.get(arg0).getGenerate_time());
		if(strs.get(arg0).getResidue_times()==5) {
			holder.adapter_redenvelope_state.setText("已结束");
			holder.adapter_redenvelope_state.setTextColor(Color.parseColor("#333333"));
			holder.adapter_redenvelope_state_image.setImageResource(R.drawable.redenvelope_wallet_2);
		}
		else {
			holder.adapter_redenvelope_state.setText("进行中");
			holder.adapter_redenvelope_state.setTextColor(Color.parseColor("#e97d03"));
			holder.adapter_redenvelope_state_image.setImageResource(R.drawable.redenvelope_wallet_1);
		}
		final int pos=arg0;
		holder.adapter_redenvelope_lastnum.setText(strs.get(arg0).getResidue_times()+"/"+strs.get(arg0).getTotal_times());
		holder.adapter_redenvelope_send.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(strs.get(pos).getResidue_times()==5) {
					BaseActivity.showCustomToastWithContext("红包已经发完啦", context);
					return;
				}
				activity.showShare(strs.get(pos).getInterface_id());
				
			}});
		holder.adapter_redenvelope_layout.setOnClickListener(new LinearLayout.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub

				Intent intent=new Intent(context, SendRedenvelopeDetail.class);
				Bundle bundle=new Bundle();
				bundle.putString("seqId", strs.get(pos).getInterface_id());
				intent.putExtras(bundle);
				context.startActivity(intent);
			}});
		return arg1;
	}

}

class RedenvelopeHolder {
	ImageView adapter_redenvelope_state_image=null;
	TextView adapter_redenvelope_name=null;
	TextView adapter_redenvelope_time=null;
	TextView adapter_redenvelope_state=null;
	TextView adapter_redenvelope_lastnum=null;
	ImageView adapter_redenvelope_send=null;
	LinearLayout adapter_redenvelope_layout=null;
}
