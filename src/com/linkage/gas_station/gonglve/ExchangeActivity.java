package com.linkage.gas_station.gonglve;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.myview.MyActivityExchangeScrollView;
import com.linkage.gas_station.myview.MyActivityExchangeScrollView.ScreenChangeListener;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.PublicManager;
import com.linkage.gas_station.util.hessian.StrategyManager;

public class ExchangeActivity extends BaseActivity {
	ImageView title_back=null;
	TextView title_name=null;
	
	MyActivityExchangeScrollView activity_exchange_scrollview=null;
	ImageView exchangeactivity_my_image=null;
	ImageView exchangeactivity_other_image=null;
	EditText activity_exchange_dh=null;
	EditText activity_exchange_yz=null;
	EditText exchangeactivity_otherphonenum=null;
	ImageView activity_exchange_getyz=null;
	TextView activity_exchange_time=null;
	TextView exchangeactivity_mynum=null;
	ImageView activity_exchange_commit=null;
	
	Timer timer=null;
	DetailTimer timetask=null;
	
	//开启倒计时
	boolean isStartTime=false;
	//倒计时初始化时间
	long day=0;
	//当前屏幕
	int mscreen=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_activityexchange);

		((GasStationApplication) getApplication()).tempActivity.add(ExchangeActivity.this);
		
		init();
	}
	
	private void init() {
		title_back=(ImageView) findViewById(R.id.title_back);
		title_back.setVisibility(View.VISIBLE);
		title_back.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText("兑换流量");
		
		activity_exchange_dh=(EditText) findViewById(R.id.activity_exchange_dh);
		activity_exchange_yz=(EditText) findViewById(R.id.activity_exchange_yz);
		activity_exchange_getyz=(ImageView) findViewById(R.id.activity_exchange_getyz);
		activity_exchange_getyz.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				activity_exchange_getyz.setEnabled(false);
				verCode();
			}});
		activity_exchange_time=(TextView) findViewById(R.id.activity_exchange_time);
		activity_exchange_scrollview=(MyActivityExchangeScrollView) findViewById(R.id.activity_exchange_scrollview);
		activity_exchange_scrollview.setOnScreenChangeListener(new ScreenChangeListener() {
			
			@Override
			public void onScreenChange(int screen) {
				// TODO Auto-generated method stub
				if(screen==1) {
					exchangeactivity_my_image.setVisibility(View.VISIBLE);
					exchangeactivity_other_image.setVisibility(View.GONE);
				}
				else if(screen==0) {
					exchangeactivity_other_image.setVisibility(View.VISIBLE);
					exchangeactivity_my_image.setVisibility(View.GONE);
				}
				mscreen=screen;
			}
		});
		exchangeactivity_my_image=(ImageView) findViewById(R.id.exchangeactivity_my_image);
		exchangeactivity_my_image.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				activity_exchange_scrollview.smoothScrollTo(0, 0);
				mscreen=0;
				exchangeactivity_other_image.setVisibility(View.VISIBLE);
				exchangeactivity_my_image.setVisibility(View.GONE);
			}});
		exchangeactivity_other_image=(ImageView) findViewById(R.id.exchangeactivity_other_image);
		exchangeactivity_other_image.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				activity_exchange_scrollview.smoothScrollTo(Util.getScreenWidth(ExchangeActivity.this), 0);
				mscreen=1;
				exchangeactivity_my_image.setVisibility(View.VISIBLE);
				exchangeactivity_other_image.setVisibility(View.GONE);
			}});
		exchangeactivity_mynum=(TextView) findViewById(R.id.exchangeactivity_mynum);
		exchangeactivity_mynum.setText(Util.getUserInfo(ExchangeActivity.this).get(0));
		exchangeactivity_otherphonenum=(EditText) findViewById(R.id.exchangeactivity_otherphonenum);
		activity_exchange_commit=(ImageView) findViewById(R.id.activity_exchange_commit);
		activity_exchange_commit.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(mscreen==1) {
					if(exchangeactivity_otherphonenum.getText().toString().equals("")) {
						showCustomToast("请您输入对方手机号码");
						return;
					}
				}
				if(activity_exchange_dh.getText().toString().equals("")) {
					showCustomToast("请您输入兑换码");
					return;
				}
				if(activity_exchange_yz.getText().toString().equals("")) {
					showCustomToast("请您输入验证码");
					return;
				}
				getFlowByCode();
			}});
	}
	
	/**
	 * 获取验证码
	 */
	public void verCode() {
		showProgressDialog(R.string.yzm_now);
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if(msg.what==1) {
					showCustomToast(getResources().getString(R.string.yzm_comp));
					//验证码开启标志位
					activity_exchange_time.setVisibility(View.VISIBLE);
					activity_exchange_getyz.setVisibility(View.GONE);
					day=new Date().getTime();
					isStartTime=true;
				}
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");
				}
				else {
					showCustomToast(getResources().getString(R.string.timeout_exp));
				}
				activity_exchange_getyz.setEnabled(true);
				dismissProgressDialog();
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(ExchangeActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(ExchangeActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(ExchangeActivity.this);
						PublicManager publicManager=GetWebDate.getHessionFactiory(ExchangeActivity.this).create(PublicManager.class, currentUsedUrl+"/hessian/publicManager", getClassLoader());
						int verCode=publicManager.sendVerCode(Long.parseLong(list.get(0)), list.get(1));
						m.what=verCode;
						flag=false;
						((GasStationApplication) getApplicationContext()).AreaUrl=currentUsedUrl;
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
	
	class DetailTimer extends TimerTask {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message m=new Message();
			handler_detailTime.sendMessage(m);
		}
		
	}
	
	Handler handler_detailTime=new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if(isStartTime) {
				
				Date today=new Date();		
				int day_=0;
				int hour_=0;
				int minute_=0;
				int second_=0;
				day_=(int) ((today.getTime()-day)/(24*60*60*1000));
				hour_=(int)(today.getTime()-day)/(60*60*1000)-day_*24;
				minute_=(int) (today.getTime()-day)/(60*1000)-day_*24*60-hour_*60;
				second_=(int) (today.getTime()-day)/1000-day_*24*60*60-hour_*60*60-minute_*60;
				if(minute_>0) {
					activity_exchange_time.setVisibility(View.GONE);
					activity_exchange_getyz.setVisibility(View.VISIBLE);
					((GasStationApplication) getApplicationContext()).loginTime=0;
				}
				else {
					activity_exchange_time.setText(""+(60-second_)+"秒发");
				}
			}
		}
	};
	
	private void getFlowByCode() {
		showProgressDialog(R.string.tishi_loading);
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if(msg.what==1) {
					Map map=(Map) msg.obj;
					if(map==null) {
						return ;
					}
					showCustomToast(map.get("comments").toString());
				}
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");
				}
				else {
					showCustomToast(getResources().getString(R.string.timeout_exp));
				}
				dismissProgressDialog();
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(ExchangeActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(ExchangeActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(ExchangeActivity.this);
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(ExchangeActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map map=strategyManager.getFlowByCode(Long.parseLong(list.get(0)), list.get(1), getIntent().getExtras().getLong("activityId"), mscreen==0?1:2, activity_exchange_yz.getText().toString(), activity_exchange_dh.getText().toString(), mscreen==0?Long.parseLong(list.get(0)):Long.parseLong(exchangeactivity_otherphonenum.getText().toString()));
						m.what=1;
						m.obj=map;
						flag=false;
						((GasStationApplication) getApplicationContext()).AreaUrl=currentUsedUrl;
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
	
	protected void onResume() {
		super.onResume();
		timer=new Timer();
		timetask=new DetailTimer();
		timer.schedule(timetask, new Date(), 1000);
		StatService.onResume(this);
	};
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		timer.cancel();
		StatService.onPause(this);
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		//unregisterReceiver(receiver);
		((GasStationApplication) getApplication()).tempActivity.remove(ExchangeActivity.this);
	}
}
