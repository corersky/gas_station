package com.linkage.gas_station.memberday;

import java.util.ArrayList;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.model.MemberModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MemberLeftAdapter extends BaseAdapter {
	
	ArrayList<MemberModel> members=null;
	Context context=null;
	boolean isStart=false;
	
	BitmapUtils bitmapUtils=null;
	BitmapDisplayConfig config=null;
	
	public MemberLeftAdapter(ArrayList<MemberModel> members, Context context, boolean isStart) {
		this.context=context;
		this.members=members;
		this.isStart=isStart;
		
		bitmapUtils=new BitmapUtils(context);
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
		MemberLeftHolder holder=null;
		if(arg1==null) {
			arg1=LayoutInflater.from(context).inflate(R.layout.adapter_member_left, null);
			holder=new MemberLeftHolder();
			holder.adapter_left_image=(ImageView) arg1.findViewById(R.id.adapter_left_image);
			holder.adapter_left_name=(TextView) arg1.findViewById(R.id.adapter_left_name);
			holder.adapter_left_shop=(TextView) arg1.findViewById(R.id.adapter_left_shop);
			holder.adapter_left_address=(TextView) arg1.findViewById(R.id.adapter_left_address);
			holder.adapter_left_phone=(TextView) arg1.findViewById(R.id.adapter_left_phone);
			holder.adapter_left_last=(TextView) arg1.findViewById(R.id.adapter_left_last);
			holder.adapter_left_order=(TextView) arg1.findViewById(R.id.adapter_left_order);
			arg1.setTag(holder);
		}
		else {
			holder=(MemberLeftHolder) arg1.getTag();
		}
		holder.adapter_left_name.setText(members.get(arg0).getPrize_name());
		holder.adapter_left_shop.setText("商家："+members.get(arg0).getSupplyer_name());
		holder.adapter_left_address.setText("描述："+members.get(arg0).getSupplyer_address());
		holder.adapter_left_phone.setText("电话："+members.get(arg0).getSupplyer_phone());
		if(isStart) {
			holder.adapter_left_order.setVisibility(View.VISIBLE);
		}
		else {
			holder.adapter_left_order.setVisibility(View.GONE);
		}
		holder.adapter_left_last.setText(members.get(arg0).getPrize_resude_cnt());
		bitmapUtils.display(holder.adapter_left_image, ((GasStationApplication) context.getApplicationContext()).AreaUrl+members.get(arg0).getPrize_pic(), config);
		return arg1;
	}

}

class MemberLeftHolder {
	ImageView adapter_left_image=null;
	TextView adapter_left_name=null;
	TextView adapter_left_shop=null;
	TextView adapter_left_address=null;
	TextView adapter_left_phone=null;
	TextView adapter_left_last=null;
	TextView adapter_left_order=null;
}
