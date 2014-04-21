package com.linkage.gas_station.oil_treasure;

import java.util.ArrayList;

import com.linkage.gas_station.R;
import com.linkage.gas_station.model.OilListModel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TreasureBuyAdapter extends BaseAdapter {
	
	ArrayList<OilListModel> oilListModel=null;
	Context context=null;
	
	public TreasureBuyAdapter(Context context, ArrayList<OilListModel> oilListModel) {
		this.context=context;
		this.oilListModel=oilListModel;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return oilListModel.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return oilListModel.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		TreasureBuyHolder holder=null;
		if(arg1==null) {
			arg1=LayoutInflater.from(context).inflate(R.layout.adapter_treasure_buy, null);
			holder=new TreasureBuyHolder();
			holder.view_treasure_buy_choice=(ImageView) arg1.findViewById(R.id.view_treasure_buy_choice);
			holder.view_treasure_buy_title=(TextView) arg1.findViewById(R.id.view_treasure_buy_title);
			arg1.setTag(holder);
		}
		else {
			holder=(TreasureBuyHolder) arg1.getTag();
		}
		if(oilListModel.get(arg0).isChecked()) {
			holder.view_treasure_buy_choice.setVisibility(View.VISIBLE);
		}
		else {
			holder.view_treasure_buy_choice.setVisibility(View.INVISIBLE);
		}
		holder.view_treasure_buy_title.setText(oilListModel.get(arg0).getOffer_content());
		return arg1;
	}

}

class TreasureBuyHolder {
	TextView view_treasure_buy_title=null;
	ImageView view_treasure_buy_choice=null;
}