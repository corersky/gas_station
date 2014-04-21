package com.linkage.gas_station.jiayou;

import java.util.ArrayList;

import com.linkage.gas_station.R;
import com.linkage.gas_station.model.OilListModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import hong.specialEffects.wheel.adapter.AbstractWheelTextAdapter;

public class AdapterQuanguo extends AbstractWheelTextAdapter {
	
	ArrayList<OilListModel> model=null;
	Context context=null;
	//显示列个数
	int rowNum=2;

	protected AdapterQuanguo(Context context, ArrayList<OilListModel> model, int rowNum) {
		super(context);
		// TODO Auto-generated constructor stub
		this.model=model;
		this.context=context;
		this.rowNum=rowNum;
	}

	@Override
	public int getItemsCount() {
		// TODO Auto-generated method stub
		return model.size();
	}

	@Override
	public View getItem(int index, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		QuanguoHolder holder=null;
		if(convertView==null) {
			holder=new QuanguoHolder();
			convertView=LayoutInflater.from(context).inflate(R.layout.adapter_jiayou, null);
			holder.jiayou_num=(TextView) convertView.findViewById(R.id.jiayou_num);
			holder.jiayou_money=(TextView) convertView.findViewById(R.id.jiayou_money);
			holder.jiayou_money_layout=(LinearLayout) convertView.findViewById(R.id.jiayou_money_layout);
			convertView.setTag(holder);
		}
		else {
			holder=(QuanguoHolder) convertView.getTag();
		}
		holder.jiayou_num.setText(model.get(index).getOffer_description());
		if(rowNum==2) {
			holder.jiayou_money_layout.setVisibility(View.VISIBLE);
		}
		else {
			holder.jiayou_money_layout.setVisibility(View.GONE);
		}
		holder.jiayou_money.setText(model.get(index).getOffer_cost());
		return convertView;
	}

	@Override
	protected CharSequence getItemText(int index) {
		// TODO Auto-generated method stub
		return model.get(index).getOffer_name();
	}

}

class QuanguoHolder {
	TextView jiayou_num=null;
	TextView jiayou_money=null;
	LinearLayout jiayou_money_layout=null;
}
