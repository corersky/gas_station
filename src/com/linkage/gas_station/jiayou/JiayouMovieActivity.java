package com.linkage.gas_station.jiayou;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.gonglve.MovieOrderActivity;
import com.linkage.gas_station.gonglve.QXJYReceiverActivity;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.PublicManager;
import com.linkage.gas_station.util.hessian.StrategyManager;

public class JiayouMovieActivity extends BaseActivity {
	
	TextView title_name=null;
	ImageView title_back=null;
	
	EditText movie_yz=null;
	ImageView movie_getyz=null;
	TextView movie_time=null;
	LinearLayout movie_result_layout=null;
	TextView movie_result_text=null;
	TextView movie_result_text2=null;
	Button movie_submit=null;
	TextView movie_desp=null;
	
	TextView movie_content=null;
	String[] seatXs;
	String[] seatYs;
	String[] ids; 
	int price=0;
	
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
		setContentView(R.layout.activity_jiayoumovie);
		
		((GasStationApplication) getApplication()).tempActivity.add(JiayouMovieActivity.this);
		
		init();
	}
	
	public void init() {
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText("电影票支付");
		title_back=(ImageView) findViewById(R.id.title_back);
		title_back.setVisibility(View.VISIBLE);
		title_back.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
		
		movie_content=(TextView) findViewById(R.id.movie_content);
		seatXs=getIntent().getExtras().getString("seatXs").split(",");
		seatYs=getIntent().getExtras().getString("seatYs").split(",");
		ids=getIntent().getExtras().getString("ids").split(",");
		price=getIntent().getExtras().getInt("price");
		String movie_content_text="";
		for(int i=0;i<seatXs.length;i++) {
			movie_content_text+=seatXs[i]+"排"+seatYs[i]+"号 ";
		}
		movie_content.setText(movie_content_text);
		movie_yz=(EditText) findViewById(R.id.movie_yz);
		movie_getyz=(ImageView) findViewById(R.id.movie_getyz);
		movie_getyz.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				movie_getyz.setEnabled(false);
				verCode();
			}});
		movie_time=(TextView) findViewById(R.id.movie_time);
		movie_result_layout=(LinearLayout) findViewById(R.id.movie_result_layout);
		movie_result_text=(TextView) findViewById(R.id.movie_result_text);
		movie_result_text2=(TextView) findViewById(R.id.movie_result_text2);
		movie_submit=(Button) findViewById(R.id.movie_submit);
		movie_submit.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				movie_submit.setEnabled(false);
				movie_submit.setClickable(false);
				orderTicketsInfo();
			}});
		movie_desp=(TextView) findViewById(R.id.movie_desp);
		movie_desp.setText("共"+price+"元");
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
				dismissProgressDialog();
				if(msg.what==1) {
					showCustomToast(getResources().getString(R.string.yzm_comp));
					//验证码开启标志位
					movie_time.setVisibility(View.VISIBLE);
					movie_getyz.setVisibility(View.GONE);
					day=new Date().getTime();
					isStartTime=true;
				}
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");
				}
				else {
					showCustomToast(getResources().getString(R.string.timeout_exp));
				}
				movie_getyz.setEnabled(true);
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(JiayouMovieActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(JiayouMovieActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(JiayouMovieActivity.this);
						
						PublicManager publicManager=GetWebDate.getHessionFactiory(JiayouMovieActivity.this).create(PublicManager.class, currentUsedUrl+"/hessian/publicManager", getClassLoader());
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
	
	public void orderTicketsInfo() {
		showProgressDialog(R.string.tishi_loading);
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				dismissProgressDialog();
				movie_submit.setEnabled(true);
				movie_submit.setClickable(true);
				if(msg.what==-3) {					
					movie_result_text.setText(getResources().getString(R.string.timeout_exp));
					movie_result_text2.setText("");
					errorNum=-3;
					movie_result_layout.setBackgroundResource(R.drawable.button_red);
				}
				else if(msg.what==-2) {
					movie_result_text.setText("链路连接失败");
					movie_result_text2.setText("");
					errorNum=-2;
					movie_result_layout.setBackgroundResource(R.drawable.button_red);
				}
				else {
					Map map=(Map) msg.obj;
					errorNum=Integer.parseInt(map.get("deal_result").toString());
					switch(errorNum) {
					case -1:
						movie_result_text.setText("验证码错误或失效");	
						movie_result_text2.setText("");
						movie_result_layout.setBackgroundResource(R.drawable.button_red);
						break;
					case 0:
						movie_result_text.setText("其他异常");	
						movie_result_text2.setText("");
						movie_result_layout.setBackgroundResource(R.drawable.button_red);
						break;
					case 1:
						movie_result_text.setText("提交成功");	
						movie_result_text2.setText("马上跳转到购买明细界面");
						movie_result_layout.setBackgroundResource(R.drawable.button_blue);
						break;
					case 3:
						movie_result_text.setText("支付成功");	
						movie_result_text2.setText("马上跳转到购买明细界面");
						movie_result_layout.setBackgroundResource(R.drawable.button_blue);
						break;
					case 4:
						movie_result_text.setText("支付失败");	
						movie_result_text2.setText("");
						movie_result_layout.setBackgroundResource(R.drawable.button_red);
						break;
					}
				}
				if(errorNum==1||errorNum==3) {
					movie_result_layout.setVisibility(View.VISIBLE);
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
							movie_result_layout.clearAnimation();
							jumpActivity();
						}
					});
					movie_result_layout.startAnimation(animation);
				}
				else {
					final AlertDialog dialog=new AlertDialog.Builder(JiayouMovieActivity.this).create();
					dialog.show();
					dialog.getWindow().setContentView(R.layout.jiayou_error_dialog);
					TextView text=(TextView) dialog.getWindow().findViewById(R.id.jiayou_error_text);
					switch(errorNum) {
					case -2:
						text.setText("链路连接失败");
						dialog.getWindow().findViewById(R.id.jiayou_error_layout).setBackgroundResource(R.drawable.button_red);
						break;
					case -3:
						text.setText(getResources().getString(R.string.timeout_exp));
						dialog.getWindow().findViewById(R.id.jiayou_error_layout).setBackgroundResource(R.drawable.button_red);
						break;
					default:
						Map map=(Map) msg.obj;
						text.setText(map.get("comments").toString());
						dialog.getWindow().findViewById(R.id.jiayou_error_layout).setBackgroundResource(R.drawable.button_red);
					}
					dialog.getWindow().findViewById(R.id.jiayou_error_commit).setOnClickListener(new View.OnClickListener() {

						@Override
						public void onClick(View v) {
							// TODO Auto-generated method stub
							dialog.dismiss();
							movie_submit.setClickable(true);
						}});
				
				}
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(JiayouMovieActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(JiayouMovieActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(JiayouMovieActivity.this);
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(JiayouMovieActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						long temp_time=(currentPayTime==0?System.currentTimeMillis():currentPayTime);
						currentPayTime=temp_time;
						Map map=strategyManager.orderTickets(""+temp_time, Long.parseLong(list.get(0)), movie_yz.getText().toString(), list.get(1), 2, getIntent().getExtras().getString("ids"), price*100, Long.parseLong(getIntent().getExtras().getString("activity_id")));
						if(map!=null) {
							m.obj=map;
							m.what=1;	
						}
						else {
							m.obj=null;
							m.what=-3;	
						}	
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
						m.what=-3;
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
					movie_time.setVisibility(View.GONE);
					movie_getyz.setVisibility(View.VISIBLE);
					((GasStationApplication) getApplicationContext()).loginTime=0;
				}
				else {
					movie_time.setText(""+(60-second_)+"秒发");
				}
			}
		}
	};
	
	protected void onResume() {
		super.onResume();
		movie_result_layout.clearAnimation();
		movie_result_layout.setVisibility(View.GONE);
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
		((GasStationApplication) getApplication()).tempActivity.remove(JiayouMovieActivity.this);
	}
	
	public void jumpActivity() {
		movie_result_layout.clearAnimation();
		movie_result_layout.setVisibility(View.GONE);
		Intent intent=new Intent();
		intent.setClass(JiayouMovieActivity.this, MovieOrderActivity.class);
		startActivity(intent);
		finish();	
	}
	
}
