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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.PublicManager;
import com.linkage.gas_station.util.hessian.StrategyManager;

public class SendFlow_Pay_Activity extends BaseActivity {
	
	TextView title_name=null;
	ImageView title_back=null;
	
	TextView sendflow_content=null;
	EditText send_flow_other_num=null;
	EditText send_flow_yz=null;
	ImageView send_flow_getyz=null;
	TextView send_flow_time=null;
	TextView send_flow_desp=null;
	Button send_flow_submit=null;
	RadioGroup send_flow_radio=null;
	
	//开启倒计时
	boolean isStartTime=false;
	//倒计时初始化时间
	long day=0;
	//提交时间戳
	long currentPayTime=0;
	int orderType=1;
	
	Timer timer=null;
	DetailTimer timetask=null;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_sendflow_pay);
		
		((GasStationApplication) getApplication()).tempActivity.add(SendFlow_Pay_Activity.this);
		
		init();
	}
	
	private void init() {
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText(getResources().getString(R.string.jiayou_zhifu));
		title_back=(ImageView) findViewById(R.id.title_back);
		title_back.setVisibility(View.VISIBLE);
		title_back.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
		
		sendflow_content=(TextView) findViewById(R.id.sendflow_content);
		sendflow_content.setText(getIntent().getExtras().getString("offer_name"));
		send_flow_radio=(RadioGroup) findViewById(R.id.send_flow_radio);
		send_flow_radio.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(RadioGroup arg0, int arg1) {
				// TODO Auto-generated method stub
				if(arg1==R.id.send_flow_me) {
					send_flow_other_num.setText(""+Util.getUserInfo(SendFlow_Pay_Activity.this).get(0));
					send_flow_other_num.setEnabled(false);
					orderType=1;
				}
				else if(arg1==R.id.send_flow_other) {
					send_flow_other_num.setText("");
					send_flow_other_num.setEnabled(true);
					orderType=2;
				}
			}
		});
		send_flow_other_num=(EditText) findViewById(R.id.send_flow_other_num);
		send_flow_yz=(EditText) findViewById(R.id.send_flow_yz);
		send_flow_getyz=(ImageView) findViewById(R.id.send_flow_getyz);
		send_flow_getyz.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				send_flow_getyz.setEnabled(false);
				verCode();
			}});
		send_flow_time=(TextView) findViewById(R.id.send_flow_time);
		send_flow_desp=(TextView) findViewById(R.id.send_flow_desp);
		send_flow_desp.setText(getIntent().getExtras().getString("offer_description"));
		send_flow_submit=(Button) findViewById(R.id.send_flow_submit);
		send_flow_submit.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub	
				if(send_flow_yz.getText().toString().equals("")) {
					showCustomToast(getResources().getString(R.string.yzm_null));
				}
				else {
					saveOrder();
					send_flow_submit.setClickable(false);
				}				
			}});

		send_flow_other_num.setText(""+Util.getUserInfo(SendFlow_Pay_Activity.this).get(0));
		send_flow_other_num.setEnabled(false);
	}

	/**
	 * 获取验证码
	 */
	public void verCode() {
				
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if(msg.what==1) {
					showCustomToast(getResources().getString(R.string.yzm_comp));
					//验证码开启标志位
					send_flow_time.setVisibility(View.VISIBLE);
					send_flow_getyz.setVisibility(View.GONE);
					day=new Date().getTime();
					isStartTime=true;
				}
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");
				}
				else {
					showCustomToast(getResources().getString(R.string.timeout_exp));
				}
				send_flow_getyz.setEnabled(true);
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(SendFlow_Pay_Activity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(SendFlow_Pay_Activity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						
						PublicManager publicManager=GetWebDate.getHessionFactiory(SendFlow_Pay_Activity.this).create(PublicManager.class, currentUsedUrl+"/hessian/publicManager", getClassLoader());
						ArrayList<String> list=Util.getUserInfo(SendFlow_Pay_Activity.this);
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
	
	private void saveOrder() {
		if(orderType==2) {
			ArrayList<String> list=Util.getUserInfo(SendFlow_Pay_Activity.this);
			if(send_flow_other_num.getText().toString().equals(list.get(0))) {
				showCustomToast("不可以输入自己的手机号，请重新输入");
				return;
			}
		}
		showProgressDialog(R.string.tishi_loading);
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				dismissProgressDialog();
				if(msg.what==1) {
					Map map=(Map) msg.obj;
					showCustomToast(map.get("comments").toString());
					finish();
				}
				else if(msg.what==-2) {
					showCustomToast("链路连接失败");
				}
				else if(msg.what==-1) {
					showCustomToast(getResources().getString(R.string.timeout_exp));
				}
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(SendFlow_Pay_Activity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(SendFlow_Pay_Activity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(SendFlow_Pay_Activity.this);
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(SendFlow_Pay_Activity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						long temp_time=(currentPayTime==0?System.currentTimeMillis():currentPayTime);
						currentPayTime=temp_time;
						Map map=strategyManager.holidayOrder(String.valueOf(temp_time), Long.parseLong(list.get(0)), Long.parseLong(getIntent().getExtras().getString("offerId")), 
								send_flow_yz.getText().toString(), list.get(1), 
								getIntent().getExtras().getInt("activity_id"), 1, 0, 0, orderType, orderType==1?0l:Long.parseLong(send_flow_other_num.getText().toString()));
						m.obj=map;
						m.what=1;		
						flag=false;
						((GasStationApplication) getApplicationContext()).AreaUrl=currentUsedUrl;
					} catch(Error e) {
						flag=false;
						m.what=-2;
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
						m.what=-1;
						m.obj=null;
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
					send_flow_time.setVisibility(View.GONE);
					send_flow_getyz.setVisibility(View.VISIBLE);
					((GasStationApplication) getApplicationContext()).loginTime=0;
				}
				else {
					send_flow_time.setText(""+(60-second_)+"秒发");
				}
			}
		}
	};
	
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
		((GasStationApplication) getApplication()).tempActivity.remove(SendFlow_Pay_Activity.this);
	}
}
