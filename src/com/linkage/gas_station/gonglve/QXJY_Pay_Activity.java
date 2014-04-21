package com.linkage.gas_station.gonglve;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.TimerTask;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.PublicManager;
import com.linkage.gas_station.util.hessian.StrategyManager;

public class QXJY_Pay_Activity extends BaseActivity {
	
	ImageView title_back=null;
	TextView title_name=null;
	
	EditText jiayou_qxjy_yz=null;
	ImageView jiayou_qxjy_getyz=null;
	TextView jiayou_qxjy_time=null;
	Button jiayou_qxjy_submit=null;
	TextView jiayou_qxjy_content=null;
	TextView jiayou_qxjy_desp=null;
	LinearLayout jiayou_qxjy_group=null;
	
	//开启倒计时
	boolean isStartTime=false;
	//倒计时初始化时间
	long day=0;
	//提交时间戳
	long currentPayTime=0;
	//选择的序号
	int qxjy_choice_num=-1;
	//视图集合
	LinkedList<View> viewList=null;
	
	Timer timer=null;
	DetailTimer timetask=null;		
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_qxjypay);
		
		viewList=new LinkedList<View>();
		
		init();
		queryTicket();
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
		title_name.setText("全心券意支付");	
		
		jiayou_qxjy_yz=(EditText) findViewById(R.id.jiayou_qxjy_yz);
		jiayou_qxjy_getyz=(ImageView) findViewById(R.id.jiayou_qxjy_getyz);
		jiayou_qxjy_getyz.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				jiayou_qxjy_getyz.setEnabled(false);
				verCode();
			}});
		jiayou_qxjy_time=(TextView) findViewById(R.id.jiayou_qxjy_time);
		jiayou_qxjy_submit=(Button) findViewById(R.id.jiayou_qxjy_submit);
		jiayou_qxjy_submit.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				cashOweAll();
			}});
		jiayou_qxjy_content=(TextView) findViewById(R.id.jiayou_qxjy_content);
		jiayou_qxjy_content.setText(getIntent().getExtras().getString("offer_name"));
		jiayou_qxjy_desp=(TextView) findViewById(R.id.jiayou_qxjy_desp);
		jiayou_qxjy_desp.setText(getIntent().getExtras().getString("offer_description"));
		jiayou_qxjy_group=(LinearLayout) findViewById(R.id.jiayou_qxjy_group);
		
	}
	
	private void queryTicket() {
		showProgressDialog(R.string.tishi_loading);
		final Handler queryTicket_handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				dismissProgressDialog();
				switch (msg.what) {
				case -1:
					showCustomToast(getResources().getString(R.string.timeout_exp));
					break;
				case -2:
					showCustomToast("链路连接失败");
				case 100:
					HashMap mapTemp = (HashMap) msg.obj;
					LinkedHashMap<String, String> map=new LinkedHashMap<String, String>();
					if(mapTemp.containsKey("package30")) {
						map.put("package30", mapTemp.get("package30").toString());
					}
					if(mapTemp.containsKey("package100")) {
						map.put("package100", mapTemp.get("package100").toString());
					}
					if(mapTemp.containsKey("package200")) {
						map.put("package200", mapTemp.get("package200").toString());
					}
					if(map.size()!=0) {
						jiayou_qxjy_group.removeAllViews();
					}
					Iterator<Entry<String, String>> it=map.entrySet().iterator();
					int i=0;
					while(it.hasNext()) {
						Entry<String, String> entry= it.next();
						final int pos=i;
						View view=LayoutInflater.from(QXJY_Pay_Activity.this).inflate(R.layout.view_qxjy, null);
						ImageView qxjy_choice_image=(ImageView) view.findViewById(R.id.qxjy_choice_image);
						TextView qxjy_choice_text=(TextView) view.findViewById(R.id.qxjy_choice_text);
						qxjy_choice_text.setTag(entry.getValue().toString());
						switch(getIntent().getExtras().getInt("offer_type_id")) {
						case 1:
							qxjy_choice_text.setText("仅当月送"+entry.getKey().toString().substring(7)+"M");
							break;
						case 2:
							qxjy_choice_text.setText("次月"+entry.getKey().toString().substring(7)+"M，连送三个月");
							break;
						case 4:
							qxjy_choice_text.setText("次月"+entry.getKey().toString().substring(7)+"M，连送三个月");
							break;
						}
						if(i==map.size()-1) {
							qxjy_choice_num=i;
							qxjy_choice_image.setImageResource(R.drawable.radiobutton_checked);
						}
						else {
							qxjy_choice_image.setImageResource(R.drawable.radiobutton_normal);
						}
						view.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								for(int j=0;j<viewList.size();j++) {
									View view_=viewList.get(j);
									ImageView qxjy_choice_image_=(ImageView) view_.findViewById(R.id.qxjy_choice_image);
									if(j==pos) {
										if(qxjy_choice_num==pos) {
											qxjy_choice_num=-1;
											qxjy_choice_image_.setImageResource(R.drawable.radiobutton_normal);
										}
										else {
											qxjy_choice_num=pos;
											qxjy_choice_image_.setImageResource(R.drawable.radiobutton_checked);
										}
									}
									else {
										qxjy_choice_image_.setImageResource(R.drawable.radiobutton_normal);
									}
								}
							}
						});
						LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
						params.weight=1;
						jiayou_qxjy_group.addView(view, params);	
						viewList.add(view);
						i++;
					}					
					break;
				default:
					break;
				}
			}
		};
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(QXJY_Pay_Activity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(QXJY_Pay_Activity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(QXJY_Pay_Activity.this);
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(QXJY_Pay_Activity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						HashMap result=strategyManager.queryTicket(Long.parseLong(list.get(0)), list.get(1), getIntent().getExtras().getInt("cost"));
						m.what=100;
						m.obj=result;
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
					}
				}
				
				queryTicket_handler.sendMessage(m);				
			}
		}).start();
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
					jiayou_qxjy_time.setVisibility(View.GONE);
					jiayou_qxjy_getyz.setVisibility(View.VISIBLE);
					((GasStationApplication) getApplicationContext()).loginTime=0;
				}
				else {
					jiayou_qxjy_time.setText(""+(60-second_)+"秒发");
				}
			}
		}
	};
	
	/**
	 * 获取验证码
	 */
	public void verCode() {
		showProgressDialog(R.string.tishi_loading);
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if(msg.what==1) {
					showCustomToast(getResources().getString(R.string.yzm_comp));
					//验证码开启标志位
					jiayou_qxjy_time.setVisibility(View.VISIBLE);
					jiayou_qxjy_getyz.setVisibility(View.GONE);
					day=new Date().getTime();
					isStartTime=true;
				}
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");
				}
				else {
					showCustomToast(getResources().getString(R.string.timeout_exp));
				}
				jiayou_qxjy_getyz.setEnabled(true);
				dismissProgressDialog();
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(QXJY_Pay_Activity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(QXJY_Pay_Activity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(QXJY_Pay_Activity.this);
						
						PublicManager publicManager=GetWebDate.getHessionFactiory(QXJY_Pay_Activity.this).create(PublicManager.class, currentUsedUrl+"/hessian/publicManager", getClassLoader());
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
	
	/**
	 * 缴费
	 * @param typeAndidAndOwes
	 */
	public void cashOweAll() {
		showProgressDialog(R.string.tishi_loading);
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg); 
				dismissProgressDialog();
				Map map=(Map) msg.obj;
				if(msg.what==100) { 
					if(map.get("deal_result").toString().equals("1")) {
						showCustomToast(map.get("comments").toString());
						finish();
					}
					else {
						showCustomInfoDialog(map.get("comments").toString());
					}
				}
				else if(msg.what==-1) {
					showCustomInfoDialog("链路连接失败");
				}
				else {
					showCustomInfoDialog(getResources().getString(R.string.timeout_exp));
				}
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(QXJY_Pay_Activity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(QXJY_Pay_Activity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(QXJY_Pay_Activity.this);
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(QXJY_Pay_Activity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						long temp_time=(currentPayTime==0?System.currentTimeMillis():currentPayTime);
						currentPayTime=temp_time;
						long id=0;
						if(qxjy_choice_num!=-1) {
							TextView qxjy_choice_text=(TextView) viewList.get(qxjy_choice_num).findViewById(R.id.qxjy_choice_text);
							id=Long.parseLong(qxjy_choice_text.getTag().toString());
						}
						Map result=strategyManager.saveQxjyOrder(String.valueOf(temp_time), Long.parseLong(list.get(0)), Long.parseLong(getIntent().getExtras().getString("offerId")), 
								jiayou_qxjy_yz.getText().toString(), list.get(1), getIntent().getExtras().getInt("activity_id")
								, 1, id);
						System.out.println(String.valueOf(temp_time)+" "+Long.parseLong(list.get(0))+" "+Long.parseLong(getIntent().getExtras().getString("offerId"))+" "+ 
								jiayou_qxjy_yz.getText().toString()+" "+list.get(1)+" "+getIntent().getExtras().getInt("activity_id")
								+" "+ 1+" "+ id);
						m.obj=result;
						m.what=100;
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
	};
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		timer.cancel();
	}
}
