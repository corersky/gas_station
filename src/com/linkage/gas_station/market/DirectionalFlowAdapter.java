package com.linkage.gas_station.market;

import java.util.ArrayList;

import com.lidroid.xutils.BitmapUtils;
import com.linkage.gas_station.R;
import com.linkage.gas_station.model.OilListModel;
import com.linkage.gas_station.util.BitmapHelp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DirectionalFlowAdapter extends BaseAdapter {
	
	Context context=null;
	ArrayList<OilListModel> model_list=null;
	TextView direc_buy_button=null;
	TextView direc_buy_money=null;
	
	BitmapUtils bitmapUtils=null;
	//显示金额
	int price=0;
	
	public DirectionalFlowAdapter(Context context, ArrayList<OilListModel> model_list, TextView direc_buy_button, TextView direc_buy_money) {
		this.context=context;
		this.model_list=model_list;
		this.direc_buy_button=direc_buy_button;
		this.direc_buy_money=direc_buy_money;
		
		bitmapUtils=BitmapHelp.getBitmapUtils(context);
		bitmapUtils.configDefaultLoadingImage(R.drawable.gonglve_title_2_default);
		bitmapUtils.configDefaultLoadFailedImage(R.drawable.gonglve_title_2_default);
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
		Direction_flow_holder holder=null;
		if(convertView==null) {
			holder=new Direction_flow_holder();
			convertView=LayoutInflater.from(context).inflate(R.layout.adapter_market_direc_flow, null);
			holder.direc_img_name=(ImageView) convertView.findViewById(R.id.direc_img_name);
			holder.direc_img_text=(TextView) convertView.findViewById(R.id.direc_img_text);
			holder.direc_img_content=(TextView) convertView.findViewById(R.id.direc_img_content);
			holder.direc_img_content_small=(TextView) convertView.findViewById(R.id.direc_img_content_small);
			holder.direc_check=(ImageView) convertView.findViewById(R.id.direc_check);
			convertView.setTag(holder);
		}
		else {
			holder=(Direction_flow_holder) convertView.getTag();
		}
		holder.direc_img_text.setText(model_list.get(position).getOffer_name());
		holder.direc_img_content.setText(model_list.get(position).getOffer_content());
		holder.direc_img_content.setVisibility(View.GONE);
		final TextView direc_img_content=holder.direc_img_content;
		final TextView direc_img_content_small=holder.direc_img_content_small;
		holder.direc_img_content.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				direc_img_content.setVisibility(View.GONE);
				direc_img_content_small.setVisibility(View.VISIBLE);
			}});
		holder.direc_img_content_small.setVisibility(View.VISIBLE);
		holder.direc_img_content_small.setText(model_list.get(position).getOffer_content());
		holder.direc_img_content_small.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				direc_img_content_small.setVisibility(View.GONE);
				direc_img_content.setVisibility(View.VISIBLE);
			}});
		bitmapUtils.display(holder.direc_img_name, model_list.get(position).getOffer_image_name());
		if(model_list.get(position).isChecked()) {
			holder.direc_check.setImageResource(R.drawable.direc_check_on);
		}
		else {
			holder.direc_check.setImageResource(R.drawable.direc_check_off);
		}
//		final int pos=position;
//		final ImageView state_image=holder.direc_check;
//		holder.direc_check.setOnClickListener(new ImageView.OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// TODO Auto-generated method stub
//				price=0;
//				if(model_list.get(pos).isChecked()) {
//					model_list.get(pos).setChecked(false);
//					state_image.setImageResource(R.drawable.direc_check_off);
//				}
//				else {
//					model_list.get(pos).setChecked(true);
//					state_image.setImageResource(R.drawable.direc_check_on);
//				}
//				int num=0;
//				for(int j=0;j<model_list.size();j++) {
//					if(model_list.get(j).isChecked()) {
//						num++;
//						price+=Integer.parseInt(model_list.get(j).getOffer_cost().substring(0, model_list.get(j).getOffer_cost().indexOf("元")));
//					}
//				}
//				if(num==0) {
//					direc_buy_button.setText("付  款");
//				}
//				else {
//					direc_buy_button.setText("付  款（"+num+"）");
//				}
//				direc_buy_money.setText("金额："+price);
//			}});
		return convertView;
	}

}

class Direction_flow_holder {
	ImageView direc_img_name=null;
	TextView direc_img_text=null;
	TextView direc_img_content_small=null;
	TextView direc_img_content=null;
	ImageView direc_check=null;
}
