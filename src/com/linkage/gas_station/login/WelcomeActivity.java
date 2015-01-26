package com.linkage.gas_station.login;

import java.util.ArrayList;

import com.baidu.mobstat.StatService;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.util.Util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

public class WelcomeActivity extends Activity {
	
	ArrayList<View> arrayView=new ArrayList<View>();
	int count=3;
	
	ViewPager welcome=null;
	PagerAdapter adapter=null;
	LinearLayout welcome_sign_layout=null;
	
	//判断是否已经跳转
	boolean isJump=false;
	//全部加载图片bitmap
	ArrayList<Bitmap> bmp_list=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_welcome);
		
		bmp_list=new ArrayList<Bitmap>();
		
		//如果一开始没有获取到手机号的话，就是首次使用软件
		if(Util.getUserInfo(WelcomeActivity.this).get(0).equals("")) {
			init();
		}
		else {
			Intent intent=new Intent(WelcomeActivity.this, AdvActivity.class);
			startActivity(intent);
			finish();
		}		
		
		((GasStationApplication) getApplication()).tempActivity.add(WelcomeActivity.this);
	}
	
	public void init() {
		welcome_sign_layout=(LinearLayout) findViewById(R.id.welcome_sign_layout);
		reloadWelcome_sign_layout(0);
		for(int i=0;i<3;i++) {
			loadIntroView(i);
		}
		welcome=(ViewPager) findViewById(R.id.welcome);
		welcome.setOffscreenPageLimit(0);
		adapter=new PagerAdapter() {
			
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				// TODO Auto-generated method stub
				return arg0==arg1;
			}
			
			@Override
			public int getCount() {
				// TODO Auto-generated method stub
				return arrayView.size();
			}
			
			@Override
			public Object instantiateItem(View container, int position) {
				// TODO Auto-generated method stub
				View view=arrayView.get(position);
				((ViewPager) container).addView(view);
				return view;
			}
			
			@Override
			public void destroyItem(View container, int position, Object object) {
				// TODO Auto-generated method stub
				View view=arrayView.get(position);
				((ViewPager) container).removeView(view);
			}
		};
		welcome.setAdapter(adapter);
		welcome.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				reloadWelcome_sign_layout(arg0);
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
			
			
		});
	}
	
	/**
	 * viewpager添加图片
	 * @param pos
	 */ 
	public void loadIntroView(int pos) {
		View view=null;
		//通过文件名获取图片
		int res_id=getResources().getIdentifier(getPackageName()+":drawable/welcome"+(pos+1), null,null);
		if(pos==2) {
			view=LayoutInflater.from(WelcomeActivity.this).inflate(R.layout.introduce_normal1, null);
			ImageView introduce_image2=(ImageView) view.findViewById(R.id.introduce_image2);
			Bitmap bmp=BitmapFactory.decodeResource(getResources(), res_id);
			bmp_list.add(bmp);
			introduce_image2.setBackgroundDrawable(new BitmapDrawable(bmp));
			ImageView guider_kaish=(ImageView) view.findViewById(R.id.guider_kaish);
			guider_kaish.setOnClickListener(new ImageView.OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					Intent intent=new Intent(WelcomeActivity.this, AdvActivity.class);
					startActivity(intent);
					finish();
				}});
		}
		else {
			view=LayoutInflater.from(WelcomeActivity.this).inflate(R.layout.introduce_normal, null);
			ImageView introduce_image=(ImageView) view.findViewById(R.id.introduce_image);			
			Bitmap bmp=BitmapFactory.decodeResource(getResources(), res_id);
			bmp_list.add(bmp);
			introduce_image.setBackgroundDrawable(new BitmapDrawable(bmp));
		}
		
		arrayView.add(view);
	}
	
	public void reloadWelcome_sign_layout(int pos) {
		welcome_sign_layout.removeAllViews();
		LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		params.leftMargin=5;
		params.rightMargin=5;
		for(int i=0;i<3;i++) {
			ImageView image=new ImageView(WelcomeActivity.this);
			if(pos==i) {
				image.setImageResource(R.drawable.point_choice);
			}
			else {
				image.setImageResource(R.drawable.point_nochoice);
			}
			welcome_sign_layout.addView(image, params);
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		Util.cleanBitmap(bmp_list);
		((GasStationApplication) getApplication()).tempActivity.remove(WelcomeActivity.this);
	}


	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		StatService.onResume(this);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		StatService.onPause(this);
	}

}
