package com.linkage.gas_station.gonglve;

import java.util.ArrayList;

import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.model.GonglveModel3;
import com.linkage.gas_station.util.AsyncImageLoad;
import com.linkage.gas_station.util.AsyncImageLoad.ImageCallBack;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.AbsListView.RecyclerListener;

public class Adapter_Gonglve_Title_3 extends BaseAdapter implements RecyclerListener {
	
	ArrayList<GonglveModel3> modelList3=null;
	Context context=null;
	//»º´æimageview
	ArrayList<ImageView> tempImages_face=null;
	AsyncImageLoad async=null;
	
	public Adapter_Gonglve_Title_3(Context context, ArrayList<GonglveModel3> modelList3) {
		this.context=context;
		this.modelList3=modelList3;
		
		tempImages_face=new ArrayList<ImageView>();
		async=AsyncImageLoad.getInstance(context);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return modelList3.size();
	}

	@Override
	public Object getItem(int arg0) {
		// TODO Auto-generated method stub
		return modelList3.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		// TODO Auto-generated method stub
		return arg0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		App_Holder holder=null;
		if(convertView==null) {
			holder=new App_Holder();
			convertView=LayoutInflater.from(context).inflate(R.layout.adapter_app_grid, null);
			holder.app_img=(ImageView) convertView.findViewById(R.id.app_img);
			convertView.setTag(holder);
		}
		else {
			holder=(App_Holder) convertView.getTag();
		}
		final ImageView temp=holder.app_img;
		String imageUrl=((GasStationApplication) context.getApplicationContext()).AreaUrl+modelList3.get(position).getApp_image_name();
		tempImages_face.remove(temp);
		tempImages_face.add(temp);
		temp.setTag(imageUrl);
		Bitmap bmp=async.loadImageBmp(1, imageUrl, new ImageCallBack() {

			@Override
			public void loadImage(Bitmap bmp, String imageUrl) {
				// TODO Auto-generated method stub
				setIcon(temp, imageUrl, bmp, tempImages_face);
			}});
		if(bmp==null) {
			setIcon(temp, imageUrl, null, tempImages_face);
		}
		else {
			setIcon(temp, imageUrl, bmp, tempImages_face);
		}
		return convertView;
	}
	
	//¸ù¾Ýtag¼ÓÔØÍ¼Æ¬
	public void setIcon(ImageView imageview, String tag, Bitmap bmp, ArrayList<ImageView> souce) {
		if(souce.contains(imageview)&&imageview.getTag().toString().equals(tag)) {
			if(bmp==null) {
				imageview.setImageResource(R.drawable.gonglve_title_3_default);
			}
			else {
				imageview.setImageBitmap(bmp);
			}
		}
	}

	@Override
	public void onMovedToScrapHeap(View view) {
		// TODO Auto-generated method stub
		App_Holder holder=(App_Holder)view.getTag();
		tempImages_face.remove(holder.app_img);
	}

}

class App_Holder {
	ImageView app_img=null;
}
