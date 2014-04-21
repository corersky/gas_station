package com.linkage.gas_station.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import com.linkage.gas_station.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

public class AsyncSingleImageLoad {
	
	Context context=null;
	//攻略活动专区详情
	LinkedHashMap<String, SoftReference<Bitmap>> Gonglve_Title_2_Detail=null;
	
	static AsyncSingleImageLoad imageLoad=null;
	
	public static AsyncSingleImageLoad getInstance(Context context) {
		if(imageLoad==null) {
			imageLoad=new AsyncSingleImageLoad(context);
		}
		return imageLoad;
	}
	
	public AsyncSingleImageLoad(Context context) {
		Gonglve_Title_2_Detail=new LinkedHashMap<String, SoftReference<Bitmap>>();
		this.context=context;
	}
	
	public interface SingleImageCallBack {
		public void loadImage(Bitmap bmp);
	}
	
	public Bitmap loadImageBmp(final String url, final SingleImageCallBack cb, final String from) {
		
		if(Gonglve_Title_2_Detail.containsKey(url)) {
			if(Gonglve_Title_2_Detail.get(url)!=null) {
				if(Gonglve_Title_2_Detail.get(url).get()!=null) {
					return Gonglve_Title_2_Detail.get(url).get();
				}
				
			}
		}

		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.what==0) {
					cb.loadImage((Bitmap) msg.obj);
				}
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Bitmap bmp=null;
				String filePath=Environment.getExternalStorageDirectory().getPath()+"/gas/"+url.substring(url.lastIndexOf("/")+1, url.length());
				File file=new File(filePath);
				if(file.exists()) {
					if(file.isDirectory()) {
						bmp=null;
					}
					else {
						bmp=BitmapFactory.decodeFile(filePath);
					}					
				}
				else {
					bmp=loadWebImage(url);
					Util.downloadOfflinePic(url);		
				}
				if(bmp==null) {
					bmp=BitmapFactory.decodeResource(context.getResources(), R.drawable.gonglve_title_2_default);
				}
				if(from.equals("Gonglve_Title_2_Detail")) {
					Gonglve_Title_2_Detail.put(url, new SoftReference<Bitmap>(bmp));
				}
				Message m=handler.obtainMessage(0, bmp);
				handler.sendMessage(m);
				
			}}).start();
		
		
		return null;
	}
	
	public Bitmap loadWebImage(String imageUrl) {
		try {
			URL url=new URL(imageUrl);
			URLConnection conn=url.openConnection();
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(10000);
			InputStream is=conn.getInputStream();
			return BitmapFactory.decodeStream(is);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public LinkedHashMap<String, SoftReference<Bitmap>> getGonglve_Title_2_Detail() {
		return Gonglve_Title_2_Detail;
	}
	
}
