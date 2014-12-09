package com.linkage.gas_station.memberday;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.model.MemberModel;
import com.linkage.gas_station.util.BitmapHelp;

public class FlowDayLeftAdapter extends BaseAdapter {
	
	ArrayList<MemberModel> members=null;
	Context context=null;
	boolean isStart=false;
	
	BitmapUtils bitmapUtils=null;
	BitmapDisplayConfig config=null;
	
	public FlowDayLeftAdapter(Context context, ArrayList<MemberModel> members, boolean isStart) {
		this.context=context;
		this.members=members;
		this.isStart=isStart;
		
		bitmapUtils=BitmapHelp.getBitmapUtils(context);
		config=new BitmapDisplayConfig();
		config.setLoadFailedDrawable(context.getResources().getDrawable(R.drawable.member_define_icon));
		config.setLoadingDrawable(context.getResources().getDrawable(R.drawable.member_define_icon));
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return members.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return members.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		FlowDayHolder holder=null;
		if(arg1==null) {
			holder=new FlowDayHolder();
			arg1=LayoutInflater.from(context).inflate(R.layout.adapter_flowday, null);
			holder.flowday_left_image=(ImageView) arg1.findViewById(R.id.flowday_left_image);
			holder.flowday_left_last=(TextView) arg1.findViewById(R.id.flowday_left_last);
			holder.flow_left_title=(TextView) arg1.findViewById(R.id.flow_left_title);
			holder.flowday_left_order=(TextView) arg1.findViewById(R.id.flowday_left_order);
			holder.flow_left_desp=(TextView) arg1.findViewById(R.id.flow_left_desp);
			arg1.setTag(holder);
		}
		else {
			holder=(FlowDayHolder) arg1.getTag();
		}
		if(isStart) {
			holder.flowday_left_order.setVisibility(View.VISIBLE);
		}
		else {
			holder.flowday_left_order.setVisibility(View.GONE);
		}
		holder.flow_left_title.setText(members.get(arg0).getPrize_name());
		holder.flow_left_desp.setText(members.get(arg0).getSupplyer_address());
		holder.flowday_left_last.setText(members.get(arg0).getPrize_resude_cnt());
		bitmapUtils.display(holder.flowday_left_image, ((GasStationApplication) context.getApplicationContext()).AreaUrl+members.get(arg0).getPrize_pic(), config);
		
		return arg1;
	}

}

class FlowDayHolder {
	ImageView flowday_left_image=null;
	TextView flowday_left_last=null;
	TextView flow_left_title=null;
	TextView flowday_left_order=null;
	TextView flow_left_desp=null;
}