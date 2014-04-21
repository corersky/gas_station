package com.linkage.gas_station.market;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.linkage.gas_station.R;
import com.linkage.gas_station.model.MarketModel;

public class GridAdapter extends BaseAdapter {

	private class GridHolder {
		ImageView appImage;
		TextView appName;
	}

	private Context context;

	private ArrayList<MarketModel> list=null;
	private LayoutInflater mInflater;
	
	BitmapUtils bitmapUtils=null;
	
	public GridAdapter(Context c) {
		super();
		this.context = c;
		
		bitmapUtils=new BitmapUtils(c);
		bitmapUtils.configDefaultLoadingImage(R.drawable.gonglve_title_2_default);
		bitmapUtils.configDefaultLoadFailedImage(R.drawable.gonglve_title_2_default);
	}

	public void setList(ArrayList<MarketModel> list) {
		this.list = list;
		mInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public int getCount() {
		// TODO Auto-generated method stub
		return list.size();
	}

	public Object getItem(int index) {
		return list.get(index);
	}

	public long getItemId(int index) {
		return index;
	}

	public View getView(int index, View convertView, ViewGroup parent) {
		final GridHolder holder;
		if (convertView == null) {   
			convertView = mInflater.inflate(R.layout.grid_item, null);   
			holder = new GridHolder();
			holder.appImage = (ImageView)convertView.findViewById(R.id.itemImage);
			holder.appName = (TextView)convertView.findViewById(R.id.itemText);
			convertView.setTag(holder);   

		} 
		else {
			holder = (GridHolder) convertView.getTag();   
		}
		final MarketModel info = list.get(index);
		if (info != null) {   
			holder.appName.setText(info.getApp_name());
			bitmapUtils.display(holder.appImage, info.getApp_image_name());
		}
		return convertView;
	}

}
