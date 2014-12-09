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
import com.linkage.gas_station.model.MemberBuyModel;
import com.linkage.gas_station.util.BitmapHelp;

public class FlowDayRightAdapter extends BaseAdapter {
	
	ArrayList<MemberBuyModel> memberBuyModels=null;
	Context context=null;
	
	BitmapUtils bitmapUtils=null;
	BitmapDisplayConfig config=null;
	
	public FlowDayRightAdapter(Context context, ArrayList<MemberBuyModel> memberBuyModels) {
		this.memberBuyModels=memberBuyModels;
		this.context=context;
		
		bitmapUtils=BitmapHelp.getBitmapUtils(context);
		config=new BitmapDisplayConfig();
		config.setLoadFailedDrawable(context.getResources().getDrawable(R.drawable.member_define_icon));
		config.setLoadingDrawable(context.getResources().getDrawable(R.drawable.member_define_icon));
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return memberBuyModels.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return memberBuyModels.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		FlowDayRightHolder holder=null;
		if(convertView==null) {
			convertView=LayoutInflater.from(context).inflate(R.layout.adapter_flowdayrecord, null);
			holder=new FlowDayRightHolder();
			holder.flowday_right_image=(ImageView) convertView.findViewById(R.id.flowday_right_image);
			holder.flow_right_title=(TextView) convertView.findViewById(R.id.flow_right_title);
			holder.flow_right_time=(TextView) convertView.findViewById(R.id.flow_right_time);
			holder.flow_right_desp=(TextView) convertView.findViewById(R.id.flow_right_desp);
			convertView.setTag(holder);
		}
		else {
			holder=(FlowDayRightHolder) convertView.getTag();
		}
		bitmapUtils.display(holder.flowday_right_image, ((GasStationApplication) context.getApplicationContext()).AreaUrl+memberBuyModels.get(position).getPrize_pic(), config);
		holder.flow_right_time.setText(memberBuyModels.get(position).getGenerate_time());
		holder.flow_right_desp.setText(memberBuyModels.get(position).getSupplyer_address());
		holder.flow_right_title.setText(memberBuyModels.get(position).getPrize_name());
		return convertView;
	}

}

class FlowDayRightHolder {
	ImageView flowday_right_image=null;
	TextView flow_right_title=null;
	TextView flow_right_time=null;
	TextView flow_right_desp=null;
}