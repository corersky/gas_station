package com.linkage.gas_station.memberday;

import java.util.ArrayList;

import com.lidroid.xutils.BitmapUtils;
import com.lidroid.xutils.bitmap.BitmapDisplayConfig;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.model.MemberBuyModel;
import com.linkage.gas_station.util.BitmapHelp;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MemberRightAdapter extends BaseAdapter {
	
	ArrayList<MemberBuyModel> memberBuyModels=null;
	Context context=null;
	
	BitmapUtils bitmapUtils=null;
	BitmapDisplayConfig config=null;
	
	MemberActivity activity=null;
	
	public MemberRightAdapter(ArrayList<MemberBuyModel> memberBuyModels, Context context, MemberActivity activity) {
		this.context=context;
		this.memberBuyModels=memberBuyModels;
		this.activity=activity;
		
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
			holder.adapter_right_red=(LinearLayout) arg1.findViewById(R.id.adapter_right_red);
			holder.adapter_right_redenvelope_state=(TextView) arg1.findViewById(R.id.adapter_right_redenvelope_state);
			holder.adapter_right_redenvelope_lastnum=(TextView) arg1.findViewById(R.id.adapter_right_redenvelope_lastnum);
			holder.adapter_right_redenvelope_send=(ImageView) arg1.findViewById(R.id.adapter_right_redenvelope_send);			
			arg1.setTag(holder);
		}
		else {
			holder=(MemberRightHolder) arg1.getTag();
		}
		final String seqId=memberBuyModels.get(arg0).getSeqId();
		holder.adapter_right_name.setText(memberBuyModels.get(arg0).getPrize_name());
		bitmapUtils.display(holder.adapter_right_image, ((GasStationApplication) context.getApplicationContext()).AreaUrl+memberBuyModels.get(arg0).getPrize_pic(), config);
		if(memberBuyModels.get(arg0).getPrize_type()==1) {
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
			holder.adapter_right_redenvelope_send.setVisibility(View.INVISIBLE);
		}
		else if(memberBuyModels.get(arg0).getPrize_type()==3||memberBuyModels.get(arg0).getPrize_type()==4) {
			holder.adapter_right_redenvelope_send.setVisibility(View.VISIBLE);
			holder.adapter_right_statue_exchange_comp.setVisibility(View.INVISIBLE);
			holder.adapter_right_statue_exchange_now.setVisibility(View.INVISIBLE);
			holder.adapter_right_statue_exchange_overdue.setVisibility(View.INVISIBLE);
			holder.adapter_right_redenvelope_send.setOnClickListener(new ImageView.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					activity.showShare(seqId);
				}});
		}
		else {
			holder.adapter_right_redenvelope_send.setVisibility(View.INVISIBLE);
			holder.adapter_right_statue_exchange_comp.setVisibility(View.INVISIBLE);
			holder.adapter_right_statue_exchange_now.setVisibility(View.INVISIBLE);
			holder.adapter_right_statue_exchange_overdue.setVisibility(View.INVISIBLE);
		}
		if(memberBuyModels.get(arg0).getPrize_type()==3||memberBuyModels.get(arg0).getPrize_type()==4) {
			holder.adapter_right_red.setVisibility(View.VISIBLE);
			holder.adapter_right_redenvelope_lastnum.setText((Integer.parseInt(memberBuyModels.get(arg0).getTotal_times())-Integer.parseInt(memberBuyModels.get(arg0).getResidue_times()))+"/"+memberBuyModels.get(arg0).getTotal_times());
			if((Integer.parseInt(memberBuyModels.get(arg0).getTotal_times())-Integer.parseInt(memberBuyModels.get(arg0).getResidue_times()))==Integer.parseInt(memberBuyModels.get(arg0).getTotal_times())) {
				holder.adapter_right_redenvelope_state.setText("已结束");
				holder.adapter_right_redenvelope_state.setTextColor(Color.parseColor("#333333"));
			}
			else {
				holder.adapter_right_redenvelope_state.setText("进行中");
				holder.adapter_right_redenvelope_state.setTextColor(Color.parseColor("#e97d03"));
			}
		}
		else {
			holder.adapter_right_red.setVisibility(View.GONE);
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
	LinearLayout adapter_right_red=null;
	TextView adapter_right_redenvelope_state=null;
	TextView adapter_right_redenvelope_lastnum=null;
	ImageView adapter_right_redenvelope_send=null;
}