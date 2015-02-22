package com.linkage.gas_station.gonglve;

import java.util.ArrayList;

import com.linkage.gas_station.R;
import com.linkage.gas_station.model.BuyShareRedEnvelopesModel;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class BuyShareRedEnvelopesAdapter extends BaseAdapter {
	
	Context context=null;
	ArrayList<BuyShareRedEnvelopesModel> models=null;
	
	public BuyShareRedEnvelopesAdapter(Context context, ArrayList<BuyShareRedEnvelopesModel> models) {
		this.context=context;
		this.models=models;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return models.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return models.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		BuyShareRedEnvelopesHolder holder=null;
		if(convertView==null) {
			convertView=LayoutInflater.from(context).inflate(R.layout.adapter_buyshareredenvelopes, null);
			holder=new BuyShareRedEnvelopesHolder();
			holder.adapter_buyshareredenvelopes_money=(TextView) convertView.findViewById(R.id.adapter_buyshareredenvelopes_money);
			holder.adapter_buyshareredenvelopes_flow=(TextView) convertView.findViewById(R.id.adapter_buyshareredenvelopes_flow);
			convertView.setTag(holder);
		}
		else {
			holder=(BuyShareRedEnvelopesHolder) convertView.getTag();
		}
		if(models.get(position).getChoice()==1) {
			holder.adapter_buyshareredenvelopes_money.setBackgroundColor(Color.parseColor("#fff0bc"));
		}
		else {
			holder.adapter_buyshareredenvelopes_money.setBackgroundColor(Color.WHITE);
		}
		holder.adapter_buyshareredenvelopes_money.setText((models.get(position).getCost()/100)+"Ԫ");
		holder.adapter_buyshareredenvelopes_flow.setText(models.get(position).getOffer_description());
		return convertView;
	}

}

class BuyShareRedEnvelopesHolder  {
	TextView adapter_buyshareredenvelopes_money=null;
	TextView adapter_buyshareredenvelopes_flow=null;
}
