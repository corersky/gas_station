package com.linkage.gas_station.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedHashMap;

import com.linkage.gas_station.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;

public class AsyncImageLoad {
	
	Context context=null;
	
	static AsyncImageLoad async=null;
	
	public static AsyncImageLoad getInstance(Context context) {
		if(async==null) {
			async=new AsyncImageLoad(context);
		}
		return async;
	}
	
	public LinkedHashMap<String, SoftReference<Bitmap>> gonglve_title_3=null;
	
	public AsyncImageLoad(Context context) {
		gonglve_title_3=new LinkedHashMap<String, SoftReference<Bitmap>>();
		this.context=context;
	}	

	public interface ImageCallBack {
		public void loadImage(Bitmap bmp, String imageUrl);
	}
	
	public Bitmap loadImageBmp(final int type, final String imageUrl, final ImageCallBack icb) {
		LinkedHashMap<String, SoftReference<Bitmap>> map=getChoice(type);
		if(map.containsKey(imageUrl)) {
			if(map.get(imageUrl)!=null) {
				if(map.get(imageUrl).get()!=null) {
					return map.get(imageUrl).get();
				}
			}			
		}
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.what==0) {
					icb.loadImage((Bitmap) msg.obj, imageUrl);	
				}				
			}
		};
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub	
				Bitmap bmp=null;
				String filePath=Environment.getExternalStorageDirectory().getPath()+"/gas/"+imageUrl.substring(imageUrl.lastIndexOf("/")+1, imageUrl.length());
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
					bmp=loadWebImage(imageUrl);
					Util.downloadOfflinePic(imageUrl);				
				}
				if(bmp==null) {
					switch(type) {
					case 0:
						bmp=BitmapFactory.decodeResource(context.getResources(), R.drawable.gonglve_title_2_default);
						break;
					case 1:
						bmp=BitmapFactory.decodeResource(context.getResources(), R.drawable.gonglve_title_3_default);
						break;
					}					
				}
				getChoice(type).put(imageUrl, new SoftReference<Bitmap>(bmp));
				Message m=handler.obtainMessage(0, bmp);
				handler.sendMessage(m);
			}}).start();
		return null;
	}
	
	//加载获取网络图片
	private Bitmap loadWebImage(String image_url) {
		try {
			URL url=new URL(image_url);
			URLConnection conn=url.openConnection();
			//设置图片加载超时时间
			conn.setConnectTimeout(10000);
			conn.setReadTimeout(10000);
			InputStream is=conn.getInputStream();
			BufferedInputStream bis=new BufferedInputStream(is);
			return BitmapFactory.decodeStream(bis);
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public LinkedHashMap<String, SoftReference<Bitmap>> getChoice(int type) {
		switch(type) {
		case 1:
			return gonglve_title_3;
		}
		return null;
	}

}
