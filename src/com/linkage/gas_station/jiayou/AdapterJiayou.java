package com.linkage.gas_station.jiayou;

import java.util.ArrayList;

import com.lidroid.xutils.BitmapUtils;
import com.linkage.gas_station.R;
import com.linkage.gas_station.myview.GalleryFlow;
import com.linkage.gas_station.util.BitmapHelp;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

public class AdapterJiayou extends BaseAdapter {
	
	//int[] resIds={R.drawable.jiayou_1, R.drawable.jiayou_2, R.drawable.jiayou_3};
	Context context=null;
	int height=1;
	ArrayList<String> name=null;
	BitmapUtils bitmapUtils=null;
	
	public AdapterJiayou(Context context, int height, ArrayList<String> name) {
		this.context=context;
		this.height=height;
		this.name=name;
		bitmapUtils=BitmapHelp.getBitmapUtils(context);
		bitmapUtils.configDefaultLoadingImage(R.drawable.gonglve_title_2_default);
		bitmapUtils.configDefaultLoadFailedImage(R.drawable.gonglve_title_2_default);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return Integer.MAX_VALUE;
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return name.get(arg0);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ImageView imageview=new ImageView(context);
		LinearLayout.LayoutParams l_params=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		LinearLayout layout=new LinearLayout(context);
		l_params.topMargin=10;
		l_params.leftMargin=10;
		l_params.rightMargin=10;
		int i =context.getResources().getIdentifier(context.getPackageName()+":drawable/"+name.get(position%name.size()), null, null);
		imageview.setImageResource(i);
		imageview.setScaleType(ScaleType.FIT_XY);
		layout.addView(imageview, l_params);
		GalleryFlow.LayoutParams params=new GalleryFlow.LayoutParams((int) (220*((double) height)/800), (int) (150*((double) height)/800));
		layout.setLayoutParams(params);
		//imageview.setLayoutParams(new GalleryFlow.LayoutParams(230, 157));
//		if(position%3==0) {
//			fb.display(imageview,"http://hiphotos.baidu.com/baidu/pic/item/f603918fa0ec08fabf7a641659ee3d6d55fbda0d.jpg", R.drawable.jiayou_1, context);
//		}
		return layout;
	}

}
