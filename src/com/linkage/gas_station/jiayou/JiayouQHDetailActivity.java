package com.linkage.gas_station.jiayou;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.OilManager;
import com.linkage.gas_station.util.hessian.PublicManager;

public class JiayouQHDetailActivity extends BaseActivity {
	
	TextView title_name=null;
	ImageView title_back=null;
	TextView jiayou_qh_detail_content=null;
	TextView jiayou_qh_detail_desp=null;
	EditText jiayou_qh_detail_yz=null;
	ImageView jiayou_qh_detail_getyz=null;
	Button jiayou_qh_detail_submit=null;
	ProgressDialog pd=null;
	TextView jiayou_qh_detail_time=null;
	LinearLayout jiayou_qh_result_layout=null;
	TextView jiayou_qh_result_text=null;
	TextView jiayou_qh_result_text2=null;
	RadioGroup jiayou_qh_detail_night=null;
	RadioButton jiayou_qh_3=null;
	RadioButton jiayou_qh_6=null;
	RadioButton jiayou_qh_9=null;
	
	ArrayList<String> list=null;
	//开启倒计时
	boolean isStartTime=false;
	//倒计时初始化时间
	long day=0;
	//跳转类型 -1：验证码错误或失效 0：提交失败 1：提交成功 2：CRM接口订购达到最大次数 3:加油成功
	int errorNum=-1;
	//提交时间戳
	long currentPayTime=0;
	
	Timer timer=null;
	DetailTimer timetask=null;
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_jiayouqhdetail);
		
		((GasStationApplication) getApplication()).tempActivity.add(JiayouQHDetailActivity.this);
		list=Util.getUserInfo(JiayouQHDetailActivity.this);
				
		init();
	}
	
	public void init() {
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
		jiayou_qh_detail_content=(TextView) findViewById(R.id.jiayou_qh_detail_content);
		jiayou_qh_detail_content.setText(getIntent().getExtras().getString("offer_name"));
		jiayou_qh_detail_desp=(TextView) findViewById(R.id.jiayou_qh_detail_desp);
		jiayou_qh_detail_desp.setText(getIntent().getExtras().getString("offer_description"));
		jiayou_qh_detail_yz=(EditText) findViewById(R.id.jiayou_qh_detail_yz);
		jiayou_qh_detail_getyz=(ImageView) findViewById(R.id.jiayou_qh_detail_getyz);
		jiayou_qh_detail_getyz.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				jiayou_qh_detail_getyz.setEnabled(false);
				verCode();
			}});
		jiayou_qh_detail_time=(TextView) findViewById(R.id.jiayou_qh_detail_time);
		jiayou_qh_detail_submit=(Button) findViewById(R.id.jiayou_qh_detail_submit);
		jiayou_qh_detail_submit.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub	
				if(jiayou_qh_detail_yz.getText().toString().equals("")) {
					showCustomToast(getResources().getString(R.string.yzm_null));
				}
				else {
					pd=ProgressDialog.show(JiayouQHDetailActivity.this, getResources().getString(R.string.tishi), "提交中");
					saveOrder();
					jiayou_qh_detail_submit.setClickable(false);
				}				
			}});
		jiayou_qh_result_layout=(LinearLayout) findViewById(R.id.jiayou_qh_result_layout);
		jiayou_qh_result_text=(TextView) findViewById(R.id.jiayou_qh_result_text);
		jiayou_qh_result_text2=(TextView) findViewById(R.id.jiayou_qh_result_text2);
		
		jiayou_qh_3=(RadioButton) findViewById(R.id.jiayou_qh_3);
		jiayou_qh_6=(RadioButton) findViewById(R.id.jiayou_qh_6);
		SpannableString sp6=new SpannableString("连订三个月，按月收费\n（预流量包，送免费流量）");
		sp6.setSpan(new ForegroundColorSpan(Color.RED), 11, 23, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		sp6.setSpan(new AbsoluteSizeSpan(12, true), 11, 23, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		jiayou_qh_6.setText(sp6);
		jiayou_qh_9=(RadioButton) findViewById(R.id.jiayou_qh_9);
		SpannableString sp9=new SpannableString("连订六个月，按月收费\n（订的久送的多，有钱，任性）");
		sp9.setSpan(new ForegroundColorSpan(Color.RED), 11, 25, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
		sp9.setSpan(new AbsoluteSizeSpan(12, true), 11, 25, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
		jiayou_qh_9.setText(sp9);
	}
	
	/**
	 * 获取验证码
	 */
	public void verCode() {
		
		pd=ProgressDialog.show(JiayouQHDetailActivity.this, getResources().getString(R.string.tishi), "正在获取验证码");
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if(msg.what==1) {
					showCustomToast(getResources().getString(R.string.yzm_comp));
					//验证码开启标志位
					jiayou_qh_detail_time.setVisibility(View.VISIBLE);
					jiayou_qh_detail_getyz.setVisibility(View.GONE);
					day=new Date().getTime();
					isStartTime=true;
				}
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");
				}
				else {
					showCustomToast(getResources().getString(R.string.timeout_exp));
				}
				jiayou_qh_detail_getyz.setEnabled(true);
				pd.dismiss();
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(JiayouQHDetailActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(JiayouQHDetailActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						
						PublicManager publicManager=GetWebDate.getHessionFactiory(JiayouQHDetailActivity.this).create(PublicManager.class, currentUsedUrl+"/hessian/publicManager", getClassLoader());
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
	 * 套餐订购
	 */
	public void saveOrder() {
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if(msg.what==-1) {
					pd.dismiss();
					jiayou_qh_result_text.setText(getResources().getString(R.string.timeout_exp));
					jiayou_qh_result_text2.setText("");
					errorNum=-2;
					jiayou_qh_result_layout.setBackgroundResource(R.drawable.button_red);
				}
				else if(msg.what==-2) {
					pd.dismiss();
					jiayou_qh_result_text.setText("链路连接失败");
					jiayou_qh_result_text2.setText("");
					errorNum=-2;
					jiayou_qh_result_layout.setBackgroundResource(R.drawable.button_red);
				}
				else {
					pd.dismiss();
					Map map=(Map) msg.obj;
					errorNum=Integer.parseInt(map.get("deal_result").toString());
					switch(errorNum) {
					case 1:
						//订购活动需要重新刷新活动界面
						try {
							if(getIntent().getExtras().getString("from")!=null&&getIntent().getExtras().getString("from").equals("dinggouhuodong")) {
								((GasStationApplication) getApplicationContext()).isRefreshTuan=true;
							}
							else
							{
								((GasStationApplication) getApplicationContext()).isJumpToMonitor=true;
							}
						} catch(Exception e) {
							((GasStationApplication) getApplicationContext()).isJumpToMonitor=true;
						}						
						jiayou_qh_result_text.setText("提交成功");	
						jiayou_qh_result_text2.setText("马上跳转到加油纪录");
						jiayou_qh_result_layout.setBackgroundResource(R.drawable.button_blue);
						break;
					case 3:
						jiayou_qh_result_text.setText("加油成功");	
						jiayou_qh_result_text2.setText("马上回到流量监控");
						jiayou_qh_result_layout.setBackgroundResource(R.drawable.button_blue);
						break;
					}
				}
				if(errorNum==1||errorNum==3) {
					jiayou_qh_result_layout.setVisibility(View.VISIBLE);
					TranslateAnimation animation=new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 2.5f);
					animation.setFillAfter(true);
					animation.setDuration(2000);
					animation.setAnimationListener(new AnimationListener() {
						
						@Override
						public void onAnimationStart(Animation animation) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onAnimationRepeat(Animation animation) {
							// TODO Auto-generated method stub
							
						}
						
						@Override
						public void onAnimationEnd(Animation animation) {
							// TODO Auto-generated method stub	
							try {
								Thread.sleep(2000);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							jiayou_qh_result_layout.clearAnimation();
							switch(errorNum) {
							case 1:
								jumpActivity(1);
								break;
							case 3:
								((GasStationApplication) getApplicationContext()).isJumpToMonitor=true;
								jumpActivity(0);
								break;
							}
							errorNum=-1;
						}
					});
					jiayou_qh_result_layout.startAnimation(animation);
				}
				else {
					final AlertDialog dialog=new AlertDialog.Builder(JiayouQHDetailActivity.this).create();
					dialog.show();
					dialog.getWindow().setContentView(R.layout.jiayou_error_dialog);
					TextView text=(TextView) dialog.getWindow().findViewById(R.id.jiayou_error_text);
					switch(errorNum) {
					case -2:
						text.setText(getResources().getString(R.string.timeout_exp));
						dialog.getWindow().findViewById(R.id.jiayou_error_layout).setBackgroundResource(R.drawable.button_red);
						break;
					case -1:
						Map map=(Map) msg.obj;
						text.setText(map.get("comments").toString());
						dialog.getWindow().findViewById(R.id.jiayou_error_layout).setBackgroundResource(R.drawable.button_red);
						break;
					case 0:
						Map map1=(Map) msg.obj;
						text.setText(map1.get("comments").toString());
						dialog.getWindow().findViewById(R.id.jiayou_error_layout).setBackgroundResource(R.drawable.button_red);
						break;
					case 2:
						Map map3=(Map) msg.obj;
						text.setText(map3.get("comments").toString());
						dialog.getWindow().findViewById(R.id.jiayou_error_layout).setBackgroundResource(R.drawable.button_red);
						break;
					case 4:
						Map map2=(Map) msg.obj;
						text.setText(map2.get("comments").toString());
						dialog.getWindow().findViewById(R.id.jiayou_error_layout).setBackgroundResource(R.drawable.button_blue);
						break;
					}
					dialog.getWindow().findViewById(R.id.jiayou_error_commit).setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							jiayou_qh_detail_submit.setClickable(true);
							if(errorNum==0||errorNum==2) {
								finish();
							}
							else if(errorNum==4) {
								Intent intent=new Intent();
								intent.setClass(JiayouQHDetailActivity.this, OrderTrackActivity.class);
								startActivity(intent);
								finish();
							}
						}});
				
				}
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(JiayouQHDetailActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(JiayouQHDetailActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						
						OilManager oilManager=GetWebDate.getHessionFactiory(JiayouQHDetailActivity.this).create(OilManager.class, currentUsedUrl+"/hessian/oilManager", getClassLoader());
						long temp_time=(currentPayTime==0?System.currentTimeMillis():currentPayTime);
						currentPayTime=temp_time;
						Map map=null;
						if(jiayou_qh_3.isChecked()) {
							map=oilManager.saveOrder2(String.valueOf(temp_time), Long.parseLong(list.get(0)), Long.parseLong(getIntent().getExtras().getString("offerId")), 
									jiayou_qh_detail_yz.getText().toString(), list.get(1), 
									((GasStationApplication) getApplicationContext()).jumpJiayouFrom, 1, 0, 0);
						}
						else if(jiayou_qh_6.isChecked()) {
							map=oilManager.saveOrder2(String.valueOf(temp_time), Long.parseLong(list.get(0)), Long.parseLong(getIntent().getExtras().getString("offerId")), 
									jiayou_qh_detail_yz.getText().toString(), list.get(1), 
									((GasStationApplication) getApplicationContext()).jumpJiayouFrom, 1, -3, -3);
						}
						else if(jiayou_qh_9.isChecked()) {
							map=oilManager.saveOrder2(String.valueOf(temp_time), Long.parseLong(list.get(0)), Long.parseLong(getIntent().getExtras().getString("offerId")), 
									jiayou_qh_detail_yz.getText().toString(), list.get(1), 
									((GasStationApplication) getApplicationContext()).jumpJiayouFrom, 1, -6, -6);
						}
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
	
	public void jumpActivity(final int type) {
		jiayou_qh_result_layout.clearAnimation();
		jiayou_qh_result_layout.setVisibility(View.GONE);
		if(type==1) {
			Intent intent=new Intent();
			intent.setClass(JiayouQHDetailActivity.this, OrderTrackActivity.class);
			startActivity(intent);
			finish();
		}
		else if(type==0) {
			finish();
		}	
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
					jiayou_qh_detail_time.setVisibility(View.GONE);
					jiayou_qh_detail_getyz.setVisibility(View.VISIBLE);
					((GasStationApplication) getApplicationContext()).loginTime=0;
				}
				else {
					jiayou_qh_detail_time.setText(""+(60-second_)+"秒发");
				}
			}
		}
	};
	
	protected void onResume() {
		super.onResume();
		jiayou_qh_result_layout.clearAnimation();
		jiayou_qh_result_layout.setVisibility(View.GONE);
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
		((GasStationApplication) getApplication()).tempActivity.remove(JiayouQHDetailActivity.this);
	}
}
