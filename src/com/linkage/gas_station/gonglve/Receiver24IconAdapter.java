package com.linkage.gas_station.gonglve;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.linkage.gas_station.R;
import com.linkage.gas_station.model.Receiver24IconModel;

public class Receiver24IconAdapter extends BaseAdapter {
	
	Context context=null;
	ArrayList<Receiver24IconModel> models=null;
	
	public Receiver24IconAdapter(Context context, ArrayList<Receiver24IconModel> models) {
		this.context=context;
		this.models=models;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return models.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return models.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int arg0, View arg1, ViewGroup arg2) {
		// TODO Auto-generated method stub
		Receiver24Holder holder=null;
		if(arg1==null) {
			arg1=LayoutInflater.from(context).inflate(R.layout.adapter_receiver24icon, null);
			holder=new Receiver24Holder();
			holder.receiver24icon_title=(TextView) arg1.findViewById(R.id.receiver24icon_title);
			holder.receiver24icon_icon=(TextView) arg1.findViewById(R.id.receiver24icon_icon);
			holder.receiver24icon_state=(ImageView) arg1.findViewById(R.id.receiver24icon_state);
			holder.receiver24icon_desp=(TextView) arg1.findViewById(R.id.receiver24icon_desp);
			arg1.setTag(holder);
		}
		else {
			holder=(Receiver24Holder) arg1.getTag();
		}
		holder.receiver24icon_title.setText(models.get(arg0).getFree_flow_name());
		holder.receiver24icon_icon.setText(models.get(arg0).getPersons()+"»À¥Œ");
		if(models.get(arg0).getFree_state()==1) {
			holder.receiver24icon_state.setImageResource(R.drawable.receiver24icon1);
		}
		else if(models.get(arg0).getFree_state()==2) {
			holder.receiver24icon_state.setImageResource(R.drawable.receiver24icon2);
		}
		else if(models.get(arg0).getFree_state()==0) {
			holder.receiver24icon_state.setImageResource(R.drawable.receiver24icon0);
		}
		holder.receiver24icon_desp.setText(models.get(arg0).getFree_flow_content());
		final int pos=arg0;
		holder.receiver24icon_state.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(models.get(pos).getFree_state()==1) {
					((Receiver24IconActivity) context).receiveFreeCoin(models.get(pos).getFree_flow_id());
				}
				
			}});
		return arg1;
	}
	
}

class Receiver24Holder {
	TextView receiver24icon_title=null;
	TextView receiver24icon_icon=null;
	ImageView receiver24icon_state=null;
	TextView receiver24icon_desp=null;
}
