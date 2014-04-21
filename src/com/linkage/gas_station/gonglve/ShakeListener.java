package com.linkage.gas_station.gonglve;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.StrategyManager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;

public class ShakeListener implements SensorEventListener {
	
	Vibrator vibrator=null;
	Context context=null;
	SensorManager sm=null;
	Sensor sen=null;
	
	//两次时间间隔
	final int UPDATE_TIME=500;
	//上一次更新的时间
	long lastTime=0;
	//摇一摇通过允许速度
	final int SPEED_LIMIT=300;
	float x=0;
	float y=0;
	float z=0;
	long activityId=0;
	//允许摇标志位
	boolean isYao=true;
	
	ImageView shake_image=null;
	ProgressBar shake_bar=null;
	
	public ShakeListener (Context context, long activityId, ImageView shake_image, ProgressBar shake_bar) {
		this.context=context;
		sm=(SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
		vibrator=(Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		this.activityId=activityId;
		this.shake_image=shake_image;
		this.shake_image.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				loadData();
			}});
		this.shake_bar=shake_bar;
	}

	@Override
	public void onSensorChanged(SensorEvent event) {
		// TODO Auto-generated method stub
		float currentX = event.values[0];
		float currentY = event.values[1];
		float currentZ = event.values[2];

		long currentTime = System.currentTimeMillis();
		long deltaTime = currentTime - lastTime;
		if (deltaTime < UPDATE_TIME) {
			return;
		}
		lastTime = currentTime;

		float deltaX = x - currentX;
		float deltaY = y - currentY;
		float deltaZ = z - currentZ;

		x = currentX;
		y = currentY;
		z = currentZ;

		double currentSpeed = Math.sqrt(deltaX * deltaX + deltaY * deltaY
				+ deltaZ * deltaZ) / deltaTime * 10000;
		if (currentSpeed > SPEED_LIMIT) {
			loadData();
			
			shake_image.setVisibility(View.GONE);
			shake_bar.setVisibility(View.VISIBLE);
		}
	}
	
	public void loadData() {
		vibrator.vibrate(400);
		shake_image.setVisibility(View.GONE);
		shake_bar.setVisibility(View.VISIBLE);
		if(isYao) {
			isYao=false;
			yao();
		}
		else {
			notYao();
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
		// TODO Auto-generated method stub

	}
	
	public void start() {
		if(sm!=null) {
			sen=sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
			if(sen!=null) {
				sm.registerListener(this, sen, SensorManager.SENSOR_DELAY_GAME);
			}
		}
	}
	
	public void stop() {
		if(sm!=null) {
			sm.unregisterListener(this);
		}
	}
	
	public void notYao() {
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				shake_image.setImageResource(R.drawable.y6);
				shake_image.setVisibility(View.VISIBLE);
				shake_bar.setVisibility(View.GONE);
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(1200);
				} catch (InterruptedException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				Message m=new Message();
				handler.sendMessage(m);
			}}).start();
	}
	
	public void yao() {
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				stop();
				String tip="";
				if(msg.what==1) {
					Map map=(Map) msg.obj;
					tip=map.get("comments").toString();
					switch(Integer.parseInt(map.get("result").toString())) {
					case 1:
						((GasStationApplication) context.getApplicationContext()).isRefreshTuan=true;
						break;
					case 2:
						break;
					case 4:
						isYao=true;
						break;
					}
				}
				else if(msg.what==-1) {
					tip="链路连接失败";
					isYao=true;
				}
				else {
					tip=context.getResources().getString(R.string.timeout_exp);
					isYao=true;
				}
				shake_image.setImageResource(R.drawable.y6);
				shake_image.setVisibility(View.VISIBLE);
				shake_bar.setVisibility(View.GONE);
				try {
					new AlertDialog.Builder(context).setTitle("每日签到").setMessage(tip).setPositiveButton("确认", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							start();
						}
					}).show();
				} catch(Exception e) {
					
				}
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e2) {
					// TODO Auto-generated catch block
					e2.printStackTrace();
				}
				LinkedList<String> wholeUrl=Util.getWholeUrl(context);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) context.getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(context).get(0):((GasStationApplication) context.getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) context.getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					
					try {
						
						ArrayList<String> list=Util.getUserInfo(context);
						StrategyManager strategyManager=GetWebDate.getYaoHessionFactiory(context).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", context.getClassLoader());
						Map map=strategyManager.sign_activity(Long.parseLong(list.get(0)), activityId, list.get(1));
						flag=false;
						m.what=1;
						m.obj=map;
					} catch(Error e) {
						flag=false;
						m.what=-1;
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
								wholeUrl.remove(currentUsedUrl);
								if(wholeUrl.size()>0) {
									currentUsedUrl=wholeUrl.get(0);
									try {
										Thread.sleep(500);
									} catch (InterruptedException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
								}
								else {
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
								wholeUrl.remove(currentUsedUrl);
								if(wholeUrl.size()>0) {
									currentUsedUrl=wholeUrl.get(0);
									try {
										Thread.sleep(500);
									} catch (InterruptedException e1) {
										// TODO Auto-generated catch block
										e1.printStackTrace();
									}
								}
								else {
									flag=false;
								}
							}
						}
						else {
							wholeUrl.remove(currentUsedUrl);
							if(wholeUrl.size()>0) {
								currentUsedUrl=wholeUrl.get(0);
								try {
									Thread.sleep(500);
								} catch (InterruptedException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
							else {
								flag=false;
							}
						}
						m.what=0;
					}
				}
				handler.sendMessage(m);
			}}).start();
	}
	
}
