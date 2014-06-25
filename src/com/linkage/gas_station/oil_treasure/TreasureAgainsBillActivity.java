package com.linkage.gas_station.oil_treasure;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.PublicManager;
import com.linkage.gas_station.util.hessian.StrategyManager;

public class TreasureAgainsBillActivity extends BaseActivity {
	
	TextView title_name=null;
	ImageView title_back=null;
	ImageView title_refresh=null;
	ProgressBar title_refresh_progress=null;
	
	RelativeLayout treasure_againstbill_main_residue_layout=null;
	TextView treasure_againstbill_main_residue=null;
	TextView treasure_againstbill_desp_top=null;
	TextView treasure_againstbill_desp_middle=null;
	TextView treasure_againstbill_desp_bottom=null;
	TextView treasure_againstbill_main_num=null;
	TextView treasure_againstbill_submit=null;
	EditText treasure_againstbill_num_edit=null;
	EditText treasure_againstbill_yz=null;
	ImageView treasure_againstbill_getyz=null;
	TextView treasure_againstbill_time=null;
	
	//提交时间戳
	long currentPayTime=0;
	//开启倒计时
	boolean isStartTime=false;
	//倒计时初始化时间
	long day=0;
	//是否曾经进行过兑换操作
	boolean isAleradyOper=false;
	boolean isLoadActivity=false;

	Timer timer=null;
	DetailTimer timetask=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_treasure_againstbill);
		
		((GasStationApplication) getApplication()).tempActivity.add(TreasureAgainsBillActivity.this);
		
		init();
	}
	
	private void init() {
		title_name=(TextView) findViewById(R.id.title_name);
		title_back=(ImageView) findViewById(R.id.title_back);
		title_back.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=getIntent();
				Bundle bundle=new Bundle();
				bundle.putBoolean("isAleradyOper", isAleradyOper);
				intent.putExtras(bundle);
				setResult(RESULT_OK, intent);
				finish();
			}});
		title_back.setVisibility(View.VISIBLE);
		title_refresh=(ImageView) findViewById(R.id.title_refresh);
		if(getIntent().getExtras().getString("type").equals("bill")) {
			title_refresh.setVisibility(View.VISIBLE);
		}		
		title_refresh.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(!isLoadActivity) {
					queryResidueCharge();
				}				
			}});
		title_refresh_progress=(ProgressBar) findViewById(R.id.title_refresh_progress);		
		
		treasure_againstbill_main_residue_layout=(RelativeLayout) findViewById(R.id.treasure_againstbill_main_residue_layout);
		treasure_againstbill_main_residue=(TextView) findViewById(R.id.treasure_againstbill_main_residue);
		treasure_againstbill_desp_top=(TextView) findViewById(R.id.treasure_againstbill_desp_top);
		treasure_againstbill_desp_middle=(TextView) findViewById(R.id.treasure_againstbill_desp_middle);
		treasure_againstbill_desp_bottom=(TextView) findViewById(R.id.treasure_againstbill_desp_bottom);
		if(getIntent().getExtras().getString("type").equals("bill")) {
			title_name.setText("兑话费");
			treasure_againstbill_desp_top.setText("当前话费池剩余");
			treasure_againstbill_desp_middle.setText("元");
			treasure_againstbill_desp_bottom.setText("兑换说明：每金币兑1元话费");
			queryResidueCharge();
		}
		else if(getIntent().getExtras().getString("type").equals("oil")) {
			title_name.setText("兑流量");
			treasure_againstbill_desp_top.setText("当前流量池剩余");
			treasure_againstbill_desp_middle.setText("M");
			treasure_againstbill_desp_bottom.setText("兑换说明：每金币兑10M省内流量");
			treasure_againstbill_main_residue_layout.setVisibility(View.GONE);
		}
		treasure_againstbill_main_num=(TextView) findViewById(R.id.treasure_againstbill_main_num);
		treasure_againstbill_main_num.setText(getIntent().getExtras().getString("treasure_main_num"));
		treasure_againstbill_submit=(TextView) findViewById(R.id.treasure_againstbill_submit);
		treasure_againstbill_submit.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(treasure_againstbill_num_edit.getText().toString().equals("")) {
					showCustomToast("请您输入欲兑换金币数");
					return;
				}
				else if(treasure_againstbill_yz.getText().toString().equals("")) {
					showCustomToast("请您输入验证码");
					return;
				}
				exchangeCoin();
			}});
		treasure_againstbill_num_edit=(EditText) findViewById(R.id.treasure_againstbill_num_edit);
		treasure_againstbill_yz=(EditText) findViewById(R.id.treasure_againstbill_yz);
		treasure_againstbill_getyz=(ImageView) findViewById(R.id.treasure_againstbill_getyz);
		treasure_againstbill_getyz.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				verCode();
			}});
		treasure_againstbill_time=(TextView) findViewById(R.id.treasure_againstbill_time);
	}
	
	/**
	 * 兑换接口
	 */
	public void exchangeCoin() {
		showProgressDialog(R.string.tishi_loading);
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				dismissProgressDialog();
				if(msg.what==1) {
					Map map=(Map) msg.obj;
					showCustomToast(map.get("comments").toString());
					switch(Integer.parseInt(map.get("result").toString())) {
					case 1:
						isAleradyOper=true;
						int total=Integer.parseInt(treasure_againstbill_main_num.getText().toString());
						int use=Integer.parseInt(treasure_againstbill_num_edit.getText().toString());
						treasure_againstbill_main_num.setText(""+(total-use));
						treasure_againstbill_num_edit.setText("");
						treasure_againstbill_yz.setText("");
						if(getIntent().getExtras().getString("type").equals("bill")) {
							int tesidueChargeTotal=Integer.parseInt(treasure_againstbill_main_residue.getText().toString());
							treasure_againstbill_main_residue.setText(""+(tesidueChargeTotal-use));
						}
						break;
					default:
					}
				}
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");
				}
				else {
					showCustomToast(getResources().getString(R.string.timeout_exp));
				}
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(TreasureAgainsBillActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(TreasureAgainsBillActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						long temp_time=(currentPayTime==0?System.currentTimeMillis():currentPayTime);
						currentPayTime=temp_time;
						ArrayList<String> list=Util.getUserInfo(TreasureAgainsBillActivity.this);					
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(TreasureAgainsBillActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						int buyType=0;
						if(getIntent().getExtras().getString("type").equals("bill")) {
							buyType=-1;
						}
						else if(getIntent().getExtras().getString("type").equals("oil")) {
							buyType=-2;
						}
						Map map=strategyManager.exchangeCoin(String.valueOf(temp_time), Long.parseLong(list.get(0)), treasure_againstbill_yz.getText().toString(), list.get(1), buyType, Integer.parseInt(treasure_againstbill_num_edit.getText().toString()));
						m.obj=map;
						m.what=1;
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
	 * 查询话费池剩余话费
	 */
	public void queryResidueCharge() {
		isLoadActivity=true;
		title_refresh.setVisibility(View.INVISIBLE);
		title_refresh_progress.setVisibility(View.VISIBLE);
		showProgressDialog(R.string.tishi_loading);
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				dismissProgressDialog();
				if(msg.what==1) {
					Map map=(Map) msg.obj;
					treasure_againstbill_main_residue.setText(map.get("residue_charge").toString());
				}
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");
				}
				else {
					showCustomToast(getResources().getString(R.string.timeout_exp));
				}
				isLoadActivity=false;
				title_refresh.setVisibility(View.VISIBLE);
				title_refresh_progress.setVisibility(View.INVISIBLE);
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(TreasureAgainsBillActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(TreasureAgainsBillActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(TreasureAgainsBillActivity.this);					
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(TreasureAgainsBillActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map map=strategyManager.queryResidueCharge(Long.parseLong(list.get(0)), list.get(1));
						m.obj=map;
						m.what=1;
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
					treasure_againstbill_time.setVisibility(View.VISIBLE);
					treasure_againstbill_getyz.setVisibility(View.GONE);
					day=new Date().getTime();
					isStartTime=true;
				}
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");
				}
				else {
					showCustomToast(getResources().getString(R.string.timeout_exp));
				}
				treasure_againstbill_getyz.setEnabled(true);
				dismissProgressDialog();
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(TreasureAgainsBillActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(TreasureAgainsBillActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(TreasureAgainsBillActivity.this);										
						PublicManager publicManager=GetWebDate.getHessionFactiory(TreasureAgainsBillActivity.this).create(PublicManager.class, currentUsedUrl+"/hessian/publicManager", getClassLoader());
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
					treasure_againstbill_time.setVisibility(View.GONE);
					treasure_againstbill_getyz.setVisibility(View.VISIBLE);
					((GasStationApplication) getApplicationContext()).loginTime=0;
				}
				else {
					treasure_againstbill_time.setText(""+(60-second_)+"秒发");
				}
			}
		}
	};
	
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK) {
			Intent intent=getIntent();
			Bundle bundle=new Bundle();
			bundle.putBoolean("isAleradyOper", isAleradyOper);
			intent.putExtras(bundle);
			setResult(RESULT_OK, intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);		
	};
	
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
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		((GasStationApplication) getApplication()).tempActivity.remove(TreasureAgainsBillActivity.this);
	}
}
