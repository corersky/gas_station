package com.linkage.gas_station.jiayou;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.baidu.mobstat.StatService;
import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.util.UrlEncodeForPay;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.OilManager;
import com.linkage.gas_station.util.hessian.PublicManager;
import com.linkage.gas_station.util.hessian.StrategyManager;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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

public class JiayouCardDetailActivity extends BaseActivity {
	
	ImageView title_back=null;
	TextView title_name=null;
	TextView jiayou_detail_card_content=null;
	TextView jiayou_card_pay1=null;
	TextView jiayou_card_pay2=null;
	TextView jiayou_card_pay3=null;
	TextView jiayou_card_desp=null;
	EditText jiayou_detail_card_yz=null;
	ImageView jiayou_detail_card_getyz=null;
	Button jiayou_detail_card_submit=null;
	ProgressDialog pd=null;
	TextView jiayou_detail_card_time=null;
	LinearLayout jiayou_card_result_layout=null;
	TextView jiayou_card_result_text=null;
	TextView jiayou_card_result_text2=null;
	TextView jiayou_detail_card_tip=null;
	
	ArrayList<String> list=null;
	//开启倒计时
	boolean isStartTime=false;
	//倒计时初始化时间
	long day=0;
	//跳转类型 -1：验证码错误或失效 0：提交失败 1：提交成功 2：CRM接口订购达到最大次数 3:加油成功
	int errorNum=-1;
	//提交时间戳
	long currentPayTime=0;
	//统一支付平台和对接设备间的交易密钥（定期/手工更新）
	String key="123456123456123456123456";
	//1	HTTP支付接口url
	String uploadUrl="http://202.102.116.186:7001/NetPay/ePayAction.do";
	//支付方式
	int payType=1;
	//显示文字
	String displayMesg="";
	//话费支付接口显示文字
	String jiayou_card_pay1_msg="";
	//翼支付接口显示文字
	String jiayou_card_pay2_msg="";
	
	Timer timer=null;
	CardTimer timetask=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_jiayoucarddetail);
		
		((GasStationApplication) getApplication()).tempActivity.add(JiayouCardDetailActivity.this);
		list=Util.getUserInfo(JiayouCardDetailActivity.this);
		
		if(Util.convertNull(getIntent().getExtras().getString("from")).equals("jiayoucard")) {
			displayMesg="赠款不能抵扣\n加油成功后会扣除"+getIntent().getExtras().getString("cost")+"元，请保持余额充足";
		}
		else {
			displayMesg="赠款不能抵扣\n加油成功后会扣除"+(int) (Integer.parseInt(getIntent().getExtras().getString("cost"))/100)+"元，请保持余额充足";
		}
		
		init();
		
	}
	
	public void init() {
		title_back=(ImageView) findViewById(R.id.title_back);
		title_back.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
		title_back.setVisibility(View.VISIBLE);
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText(getResources().getString(R.string.jiayou));
		jiayou_detail_card_content=(TextView) findViewById(R.id.jiayou_detail_card_content);
		String extra_desp=getIntent().getExtras().getString("desp");
		if(Util.convertNull(getIntent().getExtras().getString("from")).equals("jiayoucard")) {
			jiayou_detail_card_content.setText(extra_desp+getIntent().getExtras().getString("amount")+"MB("+getIntent().getExtras().getString("cost")+"元)");
		}
		else {
			jiayou_detail_card_content.setText(extra_desp+((int) Double.parseDouble(getIntent().getExtras().getString("amount"))/1024)+"MB("+(int) (Integer.parseInt(getIntent().getExtras().getString("cost"))/100)+"元)");
		}
		jiayou_card_pay1=(TextView) findViewById(R.id.jiayou_card_pay1);
		jiayou_card_pay1.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				payType=1;
				jiayou_card_pay1.setBackgroundResource(R.drawable.zhifu_choice);
				jiayou_card_pay1.setTextColor(getResources().getColor(R.color.zjifu_choice));
				jiayou_card_pay2.setBackgroundResource(R.drawable.zhifu_nochoice);
				jiayou_card_pay2.setTextColor(getResources().getColor(R.color.zjifu_nochoice));
				jiayou_card_pay3.setBackgroundResource(R.drawable.zhifu_nochoice_noenable);
				jiayou_card_pay3.setTextColor(getResources().getColor(R.color.zjifu_nochoice));
				if(jiayou_card_pay1_msg.startsWith("当前")) {
					jiayou_card_desp.setText((jiayou_card_pay1_msg+" "+displayMesg).trim());
				}
				else {
					jiayou_card_desp.setText(jiayou_card_pay1_msg);
				}				
			}});
		jiayou_card_pay2=(TextView) findViewById(R.id.jiayou_card_pay2);
		jiayou_card_pay2.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				payType=3;
				jiayou_card_pay2.setBackgroundResource(R.drawable.zhifu_choice);
				jiayou_card_pay2.setTextColor(getResources().getColor(R.color.zjifu_choice));
				jiayou_card_pay1.setBackgroundResource(R.drawable.zhifu_nochoice);
				jiayou_card_pay1.setTextColor(getResources().getColor(R.color.zjifu_nochoice));
				jiayou_card_pay3.setBackgroundResource(R.drawable.zhifu_nochoice_noenable);
				jiayou_card_pay3.setTextColor(getResources().getColor(R.color.zjifu_nochoice));
				if(jiayou_card_pay2_msg.startsWith("当前")) {
					jiayou_card_desp.setText(jiayou_card_pay2_msg+" "+displayMesg);
				}
				else {
					jiayou_card_desp.setText(jiayou_card_pay2_msg);
				}
			}});
		jiayou_card_pay3=(TextView) findViewById(R.id.jiayou_card_pay3);
		jiayou_card_pay3.setEnabled(false);
		jiayou_card_pay3.setClickable(false);
		jiayou_card_pay3.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				payType=2;
				jiayou_card_pay3.setBackgroundResource(R.drawable.zhifu_choice);
				jiayou_card_pay3.setTextColor(getResources().getColor(R.color.zjifu_choice));
				jiayou_card_pay2.setBackgroundResource(R.drawable.zhifu_nochoice);
				jiayou_card_pay2.setTextColor(getResources().getColor(R.color.zjifu_nochoice));
				jiayou_card_pay1.setBackgroundResource(R.drawable.zhifu_nochoice);
				jiayou_card_pay1.setTextColor(getResources().getColor(R.color.zjifu_nochoice));
				//getSendAgentsRecharge();
			}});
		jiayou_card_desp=(TextView) findViewById(R.id.jiayou_card_desp);
		jiayou_card_desp.setText(displayMesg);
		jiayou_detail_card_yz=(EditText) findViewById(R.id.jiayou_detail_card_yz);
		jiayou_detail_card_getyz=(ImageView) findViewById(R.id.jiayou_detail_card_getyz);
		jiayou_detail_card_getyz.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				jiayou_detail_card_getyz.setEnabled(false);
				verCode();
			}});
		jiayou_detail_card_submit=(Button) findViewById(R.id.jiayou_detail_card_submit);
		jiayou_detail_card_submit.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub				
				if(jiayou_detail_card_yz.getText().toString().equals("")) {
					showCustomToast(getResources().getString(R.string.yzm_null));
				}
				else {
					pd=ProgressDialog.show(JiayouCardDetailActivity.this, getResources().getString(R.string.tishi), "提交中");
					saveOrder();
					jiayou_detail_card_submit.setClickable(false);
				}				
			}});
		jiayou_detail_card_time=(TextView) findViewById(R.id.jiayou_detail_card_time);
		jiayou_card_result_layout=(LinearLayout) findViewById(R.id.jiayou_card_result_layout);
		jiayou_card_result_text=(TextView) findViewById(R.id.jiayou_card_result_text);
		jiayou_card_result_text2=(TextView) findViewById(R.id.jiayou_card_result_text2);
		jiayou_detail_card_tip=(TextView) findViewById(R.id.jiayou_detail_card_tip);
		if(Util.getUserArea(JiayouCardDetailActivity.this).equals("0971")) {
			jiayou_card_pay2.setEnabled(false);
			jiayou_card_pay2.setClickable(false);
			jiayou_detail_card_tip.setVisibility(View.VISIBLE);
		}
		getUserType();
		if(!Util.getUserArea(JiayouCardDetailActivity.this).equals("0971")){
			getYiLastMoney();
		}

//		IntentFilter filter=new IntentFilter();
//		filter.addAction("android.provider.Telephony.SMS_RECEIVED");
//		filter.setPriority(1000);
//		registerReceiver(receiver, filter);		
	}
	
	/**
	 * 获取验证码
	 */
	public void verCode() {
		
		pd=ProgressDialog.show(JiayouCardDetailActivity.this, getResources().getString(R.string.tishi), "正在获取验证码");
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if(msg.what==1) {
					showCustomToast(getResources().getString(R.string.yzm_comp));
					//验证码开启标志位
					jiayou_detail_card_time.setVisibility(View.VISIBLE);
					jiayou_detail_card_getyz.setVisibility(View.GONE);
					day=new Date().getTime();
					isStartTime=true;
				}
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");
				}
				else {
					showCustomToast(getResources().getString(R.string.timeout_exp));
				}
				jiayou_detail_card_getyz.setEnabled(true);
				pd.dismiss();
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(JiayouCardDetailActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(JiayouCardDetailActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						
						PublicManager publicManager=GetWebDate.getHessionFactiory(JiayouCardDetailActivity.this).create(PublicManager.class, currentUsedUrl+"/hessian/publicManager", getClassLoader());
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
				
				if(msg.obj==null) {
					if(msg.what==1) {
						if(System.currentTimeMillis()-currentPayTime<5000) {
							saveOrder();
							return;
						}
						else {
							pd.dismiss();
							errorNum=-2;
						}
					}
					else {
						pd.dismiss();
						errorNum=-3;
					}
				}
				else {
					pd.dismiss();
					Map map=(Map) msg.obj;
					if(getIntent().getExtras().getString("from").equals("double_flow")) {
						errorNum=Integer.parseInt(map.get("result").toString());
					}
					else {
						errorNum=Integer.parseInt(map.get("deal_result").toString());
					}
					switch(errorNum) {
					case 1:
						if(getIntent().getExtras().getString("from").equals("double_flow")) {
							jiayou_card_result_text.setText("参与成功，您已获赠"+map.get("gift_flow").toString()+"M流量");
							jiayou_card_result_text2.setText("马上回到流量倍增活动界面");
						}
						else {
							jiayou_card_result_text.setText("提交成功");		
							jiayou_card_result_text2.setText("马上跳转到加油纪录");
						}				
						jiayou_card_result_layout.setBackgroundResource(R.drawable.button_blue);
						break;
					case 3:
						if(getIntent().getExtras().getString("from").equals("tuangou")) {
							jiayou_card_result_text.setText("团购成功");	
						}
						else {
							jiayou_card_result_text.setText("加油成功");	
						}						
						jiayou_card_result_text2.setText("马上回到流量监控");
						jiayou_card_result_layout.setBackgroundResource(R.drawable.button_blue);
						break;
					case 4:
						if(getIntent().getExtras().getString("from").equals("double_flow")) {
							jiayou_card_result_text2.setText("马上回到流量倍增活动界面");
							jiayou_card_result_text.setText(map.get("comments").toString());
						}
						else {
							errorNum=10000;
						}
					}					
				}
				if(errorNum==1||errorNum==3||errorNum==4) {
					jiayou_card_result_layout.setVisibility(View.VISIBLE);
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
							jiayou_card_result_layout.clearAnimation();
							switch(errorNum) {
							case 1:
								if(getIntent().getExtras().getString("from").equals("double_flow")) {
									((GasStationApplication) getApplicationContext()).isRefreshTuan=true;
									jumpActivity(0);
								}
								else {
									jumpActivity(1);
								}
								break;
							case 3:
								//团购的情况下需要重新刷新界面
								if(getIntent().getExtras().getString("from").equals("tuangou")) {
									((GasStationApplication) getApplicationContext()).isRefreshTuan=true;
								}
								((GasStationApplication) getApplicationContext()).isJumpToMonitor=true;
								jumpActivity(0);
								break;
							case 4:
								jumpActivity(0);
							}
							errorNum=-1;
						}
					});
					jiayou_card_result_layout.startAnimation(animation);
				}
				else {
					final AlertDialog dialog=new AlertDialog.Builder(JiayouCardDetailActivity.this).create();
					dialog.show();
					dialog.getWindow().setContentView(R.layout.jiayou_error_dialog);
					TextView text=(TextView) dialog.getWindow().findViewById(R.id.jiayou_error_text);
					switch(errorNum) {
					case -3:
						text.setText(getResources().getString(R.string.network_exp));
						dialog.getWindow().findViewById(R.id.jiayou_error_layout).setBackgroundResource(R.drawable.button_red);
						break;
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
					case 10000:
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
							jiayou_detail_card_submit.setClickable(true);
							if(errorNum==0||errorNum==2) {
								finish();
							}
							else if(errorNum==10000) {
								Intent intent=new Intent();
								intent.setClass(JiayouCardDetailActivity.this, OrderTrackActivity.class);
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
				LinkedList<String> wholeUrl=Util.getWholeUrl(JiayouCardDetailActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(JiayouCardDetailActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						
						OilManager oilManager=GetWebDate.getHessionFactiory(JiayouCardDetailActivity.this).create(OilManager.class, currentUsedUrl+"/hessian/oilManager", getClassLoader());
						long offerId=0l;
						if(!Util.convertNull(getIntent().getExtras().getString("offer_id")).equals("")) {
							offerId=Long.parseLong(getIntent().getExtras().getString("offer_id"));
						}
						Map map=null;
						long temp_time=(currentPayTime==0?System.currentTimeMillis():currentPayTime);	
						currentPayTime=temp_time;
						if(Util.convertNull(getIntent().getExtras().getString("from")).equals("jiayoucard")) {
							map=oilManager.saveOrder2(String.valueOf(temp_time), Long.parseLong(list.get(0)), offerId, 
									jiayou_detail_card_yz.getText().toString(), list.get(1), 
									((GasStationApplication) getApplicationContext()).jumpJiayouFrom, payType, 
									(int) (Double.parseDouble(getIntent().getExtras().getString("cost"))*100), (int) (Double.parseDouble(getIntent().getExtras().getString("amount"))*1024));
						}
						else if(Util.convertNull(getIntent().getExtras().getString("from")).equals("double_flow")) {
							StrategyManager strategyManager=GetWebDate.getHessionFactiory(JiayouCardDetailActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
							map=strategyManager.double_flow_partake(String.valueOf(temp_time), Long.parseLong(list.get(0)), payType, jiayou_detail_card_yz.getText().toString(), Long.parseLong(getIntent().getExtras().getString("activity_id")), list.get(1));
						}
						else {
							map=oilManager.saveOrder2(String.valueOf(temp_time), Long.parseLong(list.get(0)), offerId, 
									jiayou_detail_card_yz.getText().toString(), list.get(1), 
									((GasStationApplication) getApplicationContext()).jumpJiayouFrom, payType, 
									(int) Double.parseDouble(getIntent().getExtras().getString("cost")), (int) Double.parseDouble(getIntent().getExtras().getString("amount")));
							
						}
						m.obj=map;
						m.what=1;
						flag=false;
						((GasStationApplication) getApplicationContext()).AreaUrl=currentUsedUrl;
					} catch(Error e) {
						flag=false;
						m.obj=null;
						m.what=1;
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
						m.obj=null;
						m.what=1;
					}
				}				
				handler.sendMessage(m);
			}}).start();
	}
	
	/**
	 * 用户余额情况
	 */
	public void getUserType() {
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if(msg.obj==null) {
					showCustomToast(getResources().getString(R.string.timeout_exp));
				}
				else {
					Map map=(Map) msg.obj;
					if(map.get("user_pay_type")!=null) {
						if(map.get("user_pay_type").toString().equals("2")) {
							jiayou_card_pay1_msg=map.get("user_balance").toString();
							if(payType==1) {
								jiayou_card_pay1_msg="当前可用余额"+(Double.parseDouble(jiayou_card_pay1_msg)/100+"元 ").trim();
								jiayou_card_desp.setText(jiayou_card_pay1_msg+displayMesg);
							}	
							else {
								jiayou_card_pay1_msg=displayMesg;
							}
						}	
						else {
							jiayou_card_pay1_msg=displayMesg;
						}
					}
				}
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(JiayouCardDetailActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(JiayouCardDetailActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						
						OilManager oilManager=GetWebDate.getHessionFactiory(JiayouCardDetailActivity.this).create(OilManager.class, currentUsedUrl+"/hessian/oilManager", getClassLoader());
						Map map=oilManager.getUserType(Long.parseLong(list.get(0)), list.get(1));
						m.obj=map;
						flag=false;
						((GasStationApplication) getApplicationContext()).AreaUrl=currentUsedUrl;
					} catch(Error e) {
						flag=false;
						m.obj=null;
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
						m.obj=null;
					}
				}				
				handler.sendMessage(m);
			}}).start();
	}
	
	public void getYiLastMoney() {				
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if(msg.obj==null) {
					showCustomToast(getResources().getString(R.string.timeout_exp));
				}
				else {
					Map map=(Map) msg.obj;
					switch(Integer.parseInt(map.get("result").toString())) {
					case 1:
						jiayou_card_pay2_msg="当前可用余额"+Double.parseDouble(map.get("yue").toString())/100+"元";
						if(payType==2) {
							jiayou_card_desp.setText(jiayou_card_pay2_msg+" "+displayMesg);
						}						
						break;
					case 2:
						jiayou_card_pay2_msg=map.get("yue").toString();
						if(payType==2) {
							jiayou_card_desp.setText(jiayou_card_pay2_msg);
						}						
						break;
					}
				}
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(JiayouCardDetailActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(JiayouCardDetailActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						
						OilManager oilManager=GetWebDate.getHessionFactiory(JiayouCardDetailActivity.this).create(OilManager.class, currentUsedUrl+"/hessian/oilManager", getClassLoader());
						Map map=oilManager.getYzfyue(Long.parseLong(list.get(0)), list.get(1));
						m.obj=map;
						flag=false;
						((GasStationApplication) getApplicationContext()).AreaUrl=currentUsedUrl;
					} catch(Error e) {
						flag=false;
						m.obj=null;
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
						m.obj=null;
					}
				}				
				handler.sendMessage(m);
			}}).start();
	
	}
	
	/**
	 * 2秒钟后跳转
	 */
	public void jumpActivity(final int type) {
		jiayou_card_result_layout.clearAnimation();
		jiayou_card_result_layout.setVisibility(View.GONE);
		if(type==1) {
			Intent intent=new Intent();
			intent.setClass(JiayouCardDetailActivity.this, OrderTrackActivity.class);
			startActivity(intent);
			finish();
		}
		else if(type==0) {
			finish();
		}
	}
	
	class CardTimer extends TimerTask {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message m=new Message();
			handler_cardTime.sendMessage(m);
		}
		
	}
	
	Handler handler_cardTime=new Handler() {
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
					jiayou_detail_card_time.setVisibility(View.GONE);
					jiayou_detail_card_getyz.setVisibility(View.VISIBLE);
					((GasStationApplication) getApplicationContext()).loginTime=0;
				}
				else {
					jiayou_detail_card_time.setText(""+(60-second_)+"秒发");
				}
			}
		}
	};	
	
	/**
	 * 执行充值
	 */
	public void getSendAgentsRecharge() {
		
		final ArrayList<String> param_list=new ArrayList<String>();
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				System.out.println(msg.obj.toString()); 
				Intent intent=new Intent(JiayouCardDetailActivity.this, BankActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("bank_url", msg.obj.toString());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				Random random=new Random();
				SimpleDateFormat format=new SimpleDateFormat("yyyyMMddHHmmss");
				Date today=new Date();
				Message m=new Message();
				//==============参数1	TransactionID	交易流水号	String	30	Y	============
				param_list.add(format.format(today)+(100000+random.nextInt(900000)));
				//==============参数2	TransationType	请求类型	    String  3	Y	请求类型，填‘Pay’ ============
				param_list.add("Pay");
				//==============参数3	TimeStamp	时间戳	String	14		yyyyMMddHHmmss  ============
				param_list.add(format.format(today));
				//==============参数4	SPID	SP身份标识	String 	4	Y	1001网厅；1002掌厅;1003代理商============
				//******************************测试
				param_list.add("1006");
				//==============参数5	ServiceID	业务代码	String	16	N	============
				param_list.add("100601");
				//==============参数6	ProductID	产品标识	String	32	N	============
				param_list.add("10060101");
				//==============参数7	OrderType	订单类型	String	2	Y	11表示充值类，12表示消费类============
				param_list.add("11");
				//==============参数8	ProductType	产品类型	String	32	Y	如果订单类型是11，不能为空，计费的产品类型============
				//******************************用户选择类型
				param_list.add("2");
				//==============参数9	AreaCode	区号	String	6	Y	号码归属地============
				//******************************待检
				param_list.add("025");
				//==============参数10	AccNbr	号码	String	20	Y	如果订单类型是11，不能为空，除了产品类型是移动，其他号码需带区号============
				//******************************用户添加手机号等
				param_list.add(list.get(0));
				//==============参数11	LinkName	联系人	String	12	N	如果订单类型是11，============
				//******************************用户添加姓名
				param_list.add(list.get(0));
				//==============参数12	LinkNbr	联系人电话	String	20	N	如果订单类型是11，============
				//******************************用户添加联系人电话
				param_list.add(list.get(0));
				//==============参数13	BssOrgId	渠道	String	9	N	如果订单类型是11， ============
				param_list.add("");
				//==============参数14	StaffId	工号	String	9	N	如果订单类型是11，  ============
				param_list.add("");
				//==============参数15	CurrencyType	货币类型	String	2	Y	人民币 “01” ============
				param_list.add("01");
				//==============参数16	Fee	支付金额	String	14	Y	单位（分）============
				//******************************用户添加金额
				param_list.add("1");
				//==============参数17	Description	订单描述	String	256	N	订单描述，SP产生============
				param_list.add("");
				//==============参数18	SPUserID	应用系统用户ID	String	32	N	============
				param_list.add("");
				//==============参数19	ReturnURL	后台返回地址	String	256	Y	============
				param_list.add("http://132.228.214.27/pay/dealPayResult.action");
				//==============参数20	SUBMERCHANTID	SP下的子SP身份标识	String	30	N	由SP平台自己分配，在做支付时，可以一并发送过来，支付平台可以负责记录，如没有可以不填写============
				param_list.add("");
				//==============参数21	ORDERSEQ	订单号	String	30	Y	由SP提供，在sp处不能重复不小于8位，用于标识一个唯一的订单，订单号可以在没有支付成功的情况下，再次进行交易，但每次交易时的订单请求交易流水号不能重复，定单号的生成规则见说明============
				//******************************待检
				param_list.add("1006"+format.format(today)+(100000+random.nextInt(900000)));
				//==============参数22	MERCHANTURL	前台返回地址	String	255	Y	SP提供的用于接收交易返回的前台url，不做业务处理，仅仅用于前台页面显示结果============
				param_list.add("http://js.189.cn/pay/dealPayView.action");
				//==============参数23	PayInterfaceType	支付接口方式	String	20	Y	
				//注：s号百网上支付(spid为1001)		bestonepay
				//号百手机支付(spid为1002)		bestwappay
				//银联支付		unionpay
				//支付宝		alipay
				//财付通		tenpay
				//建设银行		ccb
				//网易宝		netease
				//============
				//******************************用户选择
				param_list.add("bestwappay");
				//==============参数24	PayOrgan	支付机构	String	20	N	支付机构选择在SP页面完成，指示固网支付平台通过那个支付机构完成支付。============
				param_list.add("0210000002");
				//==============参数25	SubBankID	支付银行编码	String	30	N	当支付机构为银联等其他需要选择二级支付机构时，需要传入，否则不传。============
				//BOC	中国银行	 
				//SPDB	浦发银行	
				//CMBC	中国民生银行	 
				//SDB	深圳发展银行	 
				//CMB	招商银行	 
				//CCB	中国建设银行	 
				//ABC	中国农业银行	 
				//POST	中国邮政储蓄银行	 	
				//ICBC	中国工商银行	 
				//BCOM	交通银行	 
				//HXB	华夏银行	 
				//HSB	徽商银行	 
				//CEB	中国光大银行	 
				//NBCB	宁波银行	 
				//NJCB	南京银行	 
				//BJRCB	北京农村商业银行	 
				//BOB	北京银行	 
				//SHRCC	上海农村商业银行	 
				//SDRCC	顺德农村信用合作社	 
				//HKBANK	汉口银行	 
				//GZCB	广州市商业银行	 
				//GZRCC	广州市农村信用合作社	 
				//ZHRCC	珠海市农村信用合作社	 
				//YDRCC	尧都信用合作联社	 
				//JCB	晋城市商业银行	 
				//BEA	东亚银行	 
				//WZCB	温州市商业银行	 
				//CIB	兴业银行	 
				//CBHB	渤海银行	 
				//GDB	广东发展银行	 
				//CITIC	中信银行	 
				//HZB	杭州银行	 
				//PAB	平安银行	 
				//ALIPAY	支付宝	 
				//EPAYACC	翼支付账户	 
				//BPC	百事购卡	 
				//BESTCARD	天翼支付卡	 	
				//TENPAY	财付通账户	 
				//JSB	江苏银行	 	
				param_list.add("CMB");
				//==============参数26	MP	分期期数	String	12	N	分期期数 当接口支付方式选择（unionpay）分期付款时，不能为空；选择工行分期支付时（ICBC_F），此项才为必填项，取值3,6,9,12,18,24。============
				param_list.add("");
				//==============参数27	FUNDS_TYPE	充值级别	String	12	N	充值级别（可供用户选择）：
				//0-根据计费系统配置
				//1-账户级别
				//2-用户级别
				//============
				param_list.add("1");
				//==============参数28	Query_flag	用户类型	String	2	N	空充用户：2 普通用户：0============
				param_list.add("");
				//==============参数29	ExtData1	扩展参数1	String	60	N	============
				param_list.add("");
				//==============参数30	ExtData2	扩展参数2	String	60	N	============
				param_list.add("");
				//==============参数31	DIVDETAILS	分账明细	String	256	N	分账商户必填,格式参看 说明31：分账明细说明============
				param_list.add("");
				//==============参数32	CLIENTIP	客户端IP	String	15	N	用户在访问商户网站时的IP。============
				param_list.add("");
				
				String sourceStr="";
				for(int i=0;i<param_list.size();i++) {
					sourceStr+=param_list.get(i)+"$";
				}
				String string=sourceStr.substring(0, sourceStr.length()-1);
				System.out.println(string);
				//加密过程
				UrlEncodeForPay encryptPay = new UrlEncodeForPay();
				String encyPayURL = encryptPay.enCodingForPay(string, key);
		        System.out.println("encyPayURL:"+encyPayURL); 	
		        //上传加密文件
		        HashMap<String, String> map=new HashMap<String, String>();
		        map.put("RequestValue", encyPayURL);
		        String value=Util.getWebData(map, uploadUrl);
				m.obj=value;
				handler.sendMessage(m);
			}
			
		}).start();
	}
	
//	BroadcastReceiver receiver=new BroadcastReceiver() {
//
//		@Override
//		public void onReceive(Context context, Intent intent) {
//			// TODO Auto-generated method stub
//			System.out.println(intent.getAction());
//			if(intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
//				//获取intent参数
//	            Bundle bundle=intent.getExtras();
//	            //判断bundle内容
//	            if (bundle!=null)
//	            {
//	                //取pdus内容,转换为Object[]
//	                Object[] pdus=(Object[])bundle.get("pdus");
//	                //解析短信
//	                SmsMessage[] messages = new SmsMessage[pdus.length];
//	                for(int i=0;i<messages.length;i++) {
//	                    byte[] pdu=(byte[])pdus[i];
//	                    messages[i]=SmsMessage.createFromPdu(pdu);
//	                }    
//	                //解析完内容后分析具体参数
//	                for(SmsMessage msg:messages) {
//	                    //获取短信内容
//	                    String content=msg.getMessageBody();
//	                    String sender=msg.getOriginatingAddress();
//	                    //TODO:根据条件判断,然后进一般处理
//	                    if ("10001".equals(sender)) {
//	                    	try {
//	                    		Integer.parseInt(content.substring(0, 6));
//	                    		jiayou_detail_card_yz.setText(content.substring(0, 6));
//							} catch (Exception e) {
//								// TODO Auto-generated catch block
//								e.printStackTrace();
//								jiayou_detail_card_yz.setText("");
//							}
//	                        //对于特定的内容,取消广播
//	                        //abortBroadcast();
//	                    }
//	                }                
//	            }
//			}
//		
//		}};
	
	protected void onResume() {
		super.onResume();
		jiayou_card_result_layout.clearAnimation();
		jiayou_card_result_layout.setVisibility(View.GONE);
		timer=new Timer();
		timetask=new CardTimer();
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
		((GasStationApplication) getApplication()).tempActivity.remove(JiayouCardDetailActivity.this);
	}
	
}
