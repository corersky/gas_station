package com.linkage.gas_station.memberday;

import java.util.ArrayList;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.model.MemberBuyModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MemberRightAdapter extends BaseAdapter {
	
	ArrayList<MemberBuyModel> memberBuyModels=null;
	Context context=null;
	
	BitmapUtils bitmapUtils=null;
	BitmapDisplayConfig config=null;
	
	public MemberRightAdapter(ArrayList<MemberBuyModel> memberBuyModels, Context context) {
		this.context=context;
		this.memberBuyModels=memberBuyModels;
		
		bitmapUtils=new BitmapUtils(context);
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
		MemberRightHolder holder=null;
		if(arg1==null) {
			arg1=LayoutInflater.from(context).inflate(R.layout.adapter_member_right, null);
			holder=new MemberRightHolder();
			holder.adapter_right_image=(ImageView) arg1.findViewById(R.id.adapter_right_image);
			holder.adapter_right_name=(TextView) arg1.findViewById(R.id.adapter_right_name);
			holder.adapter_right_time=(TextView) arg1.findViewById(R.id.adapter_right_time);
			holder.adapter_right_address=(TextView) arg1.findViewById(R.id.adapter_right_address);
			holder.adapter_right_statue_exchange_comp=(TextView) arg1.findViewById(R.id.adapter_right_statue_exchange_comp);
			holder.adapter_right_statue_exchange_now=(TextView) arg1.findViewById(R.id.adapter_right_statue_exchange_now);
			holder.adapter_right_statue_exchange_overdue=(TextView) arg1.findViewById(R.id.adapter_right_statue_exchange_overdue);
			holder.adapter_right_lasttime=(TextView) arg1.findViewById(R.id.adapter_right_lasttime);
			arg1.setTag(holder);
		}
		else {
			holder=(MemberRightHolder) arg1.getTag();
		}
		holder.adapter_right_name.setText(memberBuyModels.get(arg0).getPrize_name());
		bitmapUtils.display(holder.adapter_right_image, ((GasStationApplication) context.getApplicationContext()).AreaUrl+memberBuyModels.get(arg0).getPrize_pic(), config);
		if(memberBuyModels.get(arg0).getState()==1) {
			holder.adapter_right_statue_exchange_comp.setVisibility(View.VISIBLE);
			holder.adapter_right_statue_exchange_now.setVisibility(View.INVISIBLE);
			holder.adapter_right_statue_exchange_overdue.setVisibility(View.INVISIBLE);
		}
		else if(memberBuyModels.get(arg0).getState()==0) {
			holder.adapter_right_statue_exchange_comp.setVisibility(View.INVISIBLE);
			holder.adapter_right_statue_exchange_now.setVisibility(View.VISIBLE);
			holder.adapter_right_statue_exchange_overdue.setVisibility(View.INVISIBLE);
		}
		else if(memberBuyModels.get(arg0).getState()==2) {
			holder.adapter_right_statue_exchange_comp.setVisibility(View.INVISIBLE);
			holder.adapter_right_statue_exchange_now.setVisibility(View.INVISIBLE);
			holder.adapter_right_statue_exchange_overdue.setVisibility(View.VISIBLE);
		}
		holder.adapter_right_time.setText("抢购时间："+memberBuyModels.get(arg0).getGenerate_time());
		holder.adapter_right_address.setText("描述："+memberBuyModels.get(arg0).getSupplyer_address());
		holder.adapter_right_lasttime.setText("有效期："+memberBuyModels.get(arg0).getValid_date());
		return arg1;
	}

}

class MemberRightHolder {
	ImageView adapter_right_image=null;
	TextView adapter_right_name=null;
	TextView adapter_right_time=null;
	TextView adapter_right_address=null;
	TextView adapter_right_statue_exchange_comp=null;
	TextView adapter_right_statue_exchange_now=null;
	TextView adapter_right_statue_exchange_overdue=null;
	TextView adapter_right_lasttime=null;
}