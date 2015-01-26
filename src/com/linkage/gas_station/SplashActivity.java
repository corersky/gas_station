package com.linkage.gas_station;

import com.baidu.mobstat.StatService;
import com.linkage.gas_station.login.AdvActivity;
import com.linkage.gas_station.login.WelcomeActivity;
import com.linkage.gas_station.main.MainActivity;
import com.linkage.gas_station.service.UpdateService;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.PublicManager;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Window;

public class SplashActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		DisplayMetrics dm=new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		Util.setDisplay(SplashActivity.this, dm.density);
		
		if(getIntent().getDataString()!=null&&!getIntent().getDataString().equals("")) {
			if(((GasStationApplication) getApplication()).tempActivity!=null&&((GasStationApplication) getApplication()).tempActivity.size()>0) {
				for(int i=0;i<((GasStationApplication) getApplication()).tempActivity.size();i++) {
					if(((GasStationApplication) getApplication()).tempActivity.get(i)==((Activity) MainActivity.getInstance())) {
						continue;
					}
					((GasStationApplication) getApplication()).tempActivity.get(i).finish();
				}
			}
			String romateStr=getIntent().getDataString().substring(getIntent().getDataString().lastIndexOf("/")+1, getIntent().getDataString().length());
			((GasStationApplication) getApplication()).webTab=Integer.parseInt(romateStr);
		}
								
		if(((GasStationApplication) SplashActivity.this.getApplication()).isAppOpen) {
			System.out.println("已经有了实例了");
			finish();
		}
		else {
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					boolean flag=true;
					int num=0;
					String sendUrl=((GasStationApplication) getApplicationContext()).AreaUrl;
					if(sendUrl.equals("")) {
						sendUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
					}
					while(flag) {						
						try {
							PublicManager publicManager=GetWebDate.getFirstHessionFactiory(SplashActivity.this).create(PublicManager.class, sendUrl+"/hessian/publicManager", getClassLoader());
							Util.setTimeExtra(SplashActivity.this, publicManager.getSystemTime());
							flag=false;
							System.out.println("获取服务器差值时间成功");
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							if(e instanceof com.caucho.hessian.client.HessianRuntimeException) {
								//手机自身网络连接异常
								if(e.getMessage().indexOf("java.net.SocketException")!=-1) {
									num++;
									if(num>=10) {
										flag=false;
									}
									try {
										Thread.sleep(500);
									} catch (InterruptedException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
								}
								//ip 端口等错误  java.net.SocketTimeoutException
								else {
									if(sendUrl.equals(((GasStationApplication) getApplicationContext()).COMMONURL[0])) {
										sendUrl=((GasStationApplication) getApplicationContext()).COMMONURL[1];
									}
									else if(sendUrl.equals(((GasStationApplication) getApplicationContext()).COMMONURL[1])) {
										flag=false;
									}									
								}
							}
							else if(e instanceof com.caucho.hessian.client.HessianConnectionException) {
								//手机自身网络连接异常
								if(e.getMessage().indexOf("java.io.EOFException")!=-1) {
									num++;
									if(num>=10) {
										flag=false;
									}
									try {
										Thread.sleep(500);
									} catch (InterruptedException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
								}
								else {
									if(sendUrl.equals(((GasStationApplication) getApplicationContext()).COMMONURL[0])) {
										sendUrl=((GasStationApplication) getApplicationContext()).COMMONURL[1];
									}
									else if(sendUrl.equals(((GasStationApplication) getApplicationContext()).COMMONURL[1])) {
										flag=false;
									}									
								}
							}
							else {
								if(sendUrl.equals(((GasStationApplication) getApplicationContext()).COMMONURL[0])) {
									sendUrl=((GasStationApplication) getApplicationContext()).COMMONURL[1];
								}
								else if(sendUrl.equals(((GasStationApplication) getApplicationContext()).COMMONURL[1])) {
									flag=false;
								}									
							}
						}
					}
				}}).start();
			
			//首次使用
			if(Util.getUserInfo(SplashActivity.this).get(0).equals("")) {
				((GasStationApplication) getApplicationContext()).AreaUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				Intent intent=new Intent(this, WelcomeActivity.class);
				startActivity(intent);
				
				//首次使用再次检测版本信息
				Intent intent_update=new Intent(this, UpdateService.class);
				startService(intent_update);
				
			}
			else {
				((GasStationApplication) getApplicationContext()).AreaUrl=Util.getStartURL(SplashActivity.this);
				Intent intent=new Intent(this, AdvActivity.class);
				startActivity(intent);
			}
			finish();
		}
		
		((GasStationApplication) getApplication()).tempActivity.add(SplashActivity.this);
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
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		((GasStationApplication) getApplication()).tempActivity.remove(SplashActivity.this);
	}

}
