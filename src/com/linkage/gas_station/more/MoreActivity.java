package com.linkage.gas_station.more;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.baidu.mobstat.StatService;
import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.bbs.BBSMainTabActivity;
import com.linkage.gas_station.jpush.JPushReceiver;
import com.linkage.gas_station.main.MainActivity;
import com.linkage.gas_station.share.ShareActivity_New;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.BbsManager;
import com.linkage.gas_station.util.hessian.CommonManager;
import com.linkage.gas_station.util.hessian.GetWebDate;

public class MoreActivity extends BaseActivity {
	
	TextView title_name=null;
	RelativeLayout more_tel=null;
	TextView more_phone_num=null;
	RelativeLayout more_value=null;
	RelativeLayout more_his=null;
	RelativeLayout more_suggest=null;
	RelativeLayout more_share=null;
	RelativeLayout more_about=null;
	RelativeLayout more_update=null;
	RelativeLayout more_dx=null;
	RelativeLayout more_luntan=null;
	ImageView suggest_new=null;
	
	TextView more_set_value = null;
	
	//初始号码
	long initPhoneNum=-1;
	
	int select_type = 0;
	
	private Handler handler = new Handler() {
		public void handleMessage(Message msg)  {
			switch (msg.what) {
			case 0:
				dismissProgressDialog();
				more_set_value.setText(setValue(select_type));
				SharedPreferences sp=getSharedPreferences("gas", Activity.MODE_PRIVATE);
				sp.edit().putInt("threshold_type", select_type).commit();
				break;
			case 1:
				dismissProgressDialog();
				showCustomToastWithContext(getString(R.string.set_value_fail), MoreActivity.this);
				break;
			case 2:
				dismissProgressDialog();
				more_set_value.setText(setValue(select_type));
				SharedPreferences sp1=getSharedPreferences("gas", Activity.MODE_PRIVATE);
				sp1.edit().putInt("threshold_type", select_type).commit();
				showCustomToastWithContext(getString(R.string.set_value_success), MoreActivity.this);
				break;
			case 3:
				dismissProgressDialog();
				break;
			default:
				break;
			}
		};
	};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_more);
		
		initPhoneNum=Long.parseLong(Util.getUserInfo(MoreActivity.this).get(0));
		
		init();
		if(!Util.getUserArea(MoreActivity.this).equals("0971")) {
			getDataInit();
		}		
	}
	
	public void init() {
		suggest_new=(ImageView) findViewById(R.id.suggest_new);
		
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText(getResources().getString(R.string.more));
		
		more_tel=(RelativeLayout) findViewById(R.id.more_tel);
		more_tel.setOnClickListener(lis);
		more_phone_num=(TextView) findViewById(R.id.more_phone_num);
		more_phone_num.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}});
		ArrayList<String> list=Util.getUserInfo(MoreActivity.this);
		more_phone_num.setText(list.get(0));
		more_value = (RelativeLayout) findViewById(R.id.more_value);
		more_value.setOnClickListener(lis);
		if(Util.getUserArea(MoreActivity.this).equals("0971")) {
			more_value.setVisibility(View.GONE);
		}
		more_set_value = (TextView) findViewById(R.id.more_set_value);
		SharedPreferences sp=getSharedPreferences("gas", Activity.MODE_PRIVATE);
		select_type = sp.getInt("threshold_type", 0);
		more_set_value.setText(setValue(select_type));
		more_his=(RelativeLayout) findViewById(R.id.more_his);
		more_his.setOnClickListener(lis);
		more_suggest=(RelativeLayout) findViewById(R.id.more_suggest);
		more_suggest.setOnClickListener(lis);
		more_share=(RelativeLayout) findViewById(R.id.more_share);
		more_share.setOnClickListener(lis);
		more_about=(RelativeLayout) findViewById(R.id.more_about);
		more_about.setOnClickListener(lis);
		more_update=(RelativeLayout) findViewById(R.id.more_update);
		more_update.setVisibility(View.GONE);
		more_update.setOnClickListener(lis);
		more_dx=(RelativeLayout) findViewById(R.id.more_dx);
		more_dx.setOnClickListener(lis);
		more_luntan=(RelativeLayout) findViewById(R.id.more_luntan);
		more_luntan.setOnClickListener(lis);
		if(Util.getUserArea(MoreActivity.this).equals("0971")) {
			more_luntan.setVisibility(View.GONE);
		}
	}
	
	private void getDataInit() {
		showProgressDialog(R.string.tishi_loading);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(MoreActivity.this);
				boolean flag=true;
				int num=0;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(MoreActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(MoreActivity.this);
						SharedPreferences sp=getSharedPreferences("gas", Activity.MODE_PRIVATE);
						String phoneNum = sp.getString("phoneNum", "");
						String area_code = sp.getString("area_code", "");
						
						CommonManager manager=GetWebDate.getHessionFactiory(MoreActivity.this).create(CommonManager.class, currentUsedUrl+"/hessian/commonManager", getClassLoader());
						select_type=manager.thresHoldQuery(Long.parseLong(phoneNum), area_code);
						flag=false;
						handler.sendEmptyMessage(0);
					} catch(Error e) {
						flag=false;
						handler.sendEmptyMessage(3);
			        } catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						if(e instanceof com.caucho.hessian.client.HessianRuntimeException) {
							//手机自身网络连接异常
							if(e.getMessage().indexOf("java.net.SocketException")!=-1) {
								num++;
								if(num>=10) {
									flag=false;
									handler.sendEmptyMessage(3);
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
									handler.sendEmptyMessage(3);
								}
							}
							
						}
						else if(e instanceof com.caucho.hessian.client.HessianConnectionException) {
							//手机自身网络连接异常
							if(e.getMessage().indexOf("java.io.EOFException")!=-1) {
								num++;
								if(num>=10) {
									flag=false;
									handler.sendEmptyMessage(3);
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
									handler.sendEmptyMessage(3);
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
								handler.sendEmptyMessage(3);
							}
						}
					}
				}
				
			}}).start();
	}
	
	private void setThresholdValue(final int type) {
		showProgressDialog(R.string.tishi_loading);
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(MoreActivity.this);
				boolean flag=true;
				int num=0;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(MoreActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(MoreActivity.this);
						SharedPreferences sp=getSharedPreferences("gas", Activity.MODE_PRIVATE);
						String phoneNum = sp.getString("phoneNum", "");
						String area_code = sp.getString("area_code", "");
						
						CommonManager manager=GetWebDate.getHessionFactiory(MoreActivity.this).create(CommonManager.class, currentUsedUrl+"/hessian/commonManager", getClassLoader());
						Map result = manager.setThresHold(Long.parseLong(phoneNum),type, area_code);
						if(result!=null&&result.get("result").toString().equals("1")) {
							select_type = type;
							handler.sendEmptyMessage(2);
						}
						else {
							handler.sendEmptyMessage(1);
						}
						flag=false;
					} catch(Error e) {
						flag=false;
						handler.sendEmptyMessage(1);
			        } catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						if(e instanceof com.caucho.hessian.client.HessianRuntimeException) {
							//手机自身网络连接异常
							if(e.getMessage().indexOf("java.net.SocketException")!=-1) {
								num++;
								if(num>=10) {
									flag=false;
									handler.sendEmptyMessage(1);
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
									handler.sendEmptyMessage(1);
								}
							}
							
						}
						else if(e instanceof com.caucho.hessian.client.HessianConnectionException) {
							//手机自身网络连接异常
							if(e.getMessage().indexOf("java.io.EOFException")!=-1) {
								num++;
								if(num>=10) {
									flag=false;
									handler.sendEmptyMessage(1);
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
									handler.sendEmptyMessage(1);
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
								handler.sendEmptyMessage(1);
							}
						}			
					}
				}				
			}}).start();
	}
	
	RelativeLayout.OnClickListener lis=new RelativeLayout.OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(v.getId()==R.id.more_update) {
				
				return;
			}
			switch(v.getId()) {
			case R.id.more_tel:
//				new AlertDialog.Builder(MoreActivity.this).setTitle("温馨提示").setTitle("确定要切换号码？").setNegativeButton("取消", new DialogInterface.OnClickListener() {
//					
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						// TODO Auto-generated method stub
//						
//					}
//				}).setPositiveButton("确定", new DialogInterface.OnClickListener() {
//					
//					@Override
//					public void onClick(DialogInterface dialog, int which) {
//						// TODO Auto-generated method stub
//						JPushInterface.stopPush(getApplicationContext());
//						((GasStationApplication) getApplicationContext()).isJumpToMonitor=false;
//						Intent intent=new Intent(MoreActivity.this, ReloadActivity.class);
//						Bundle bundle=new Bundle();
//						bundle.putString("isFrom", "reloading");
//						intent.putExtras(bundle);
//						startActivity(intent);
//					}
//				}).show();
//				
				break;
			case R.id.more_value:
				Intent intent_threshold=new Intent(MoreActivity.this, ThresholdActivity.class);
				Bundle bundle=new Bundle();
				bundle.putInt("select_type", select_type);
				intent_threshold.putExtras(bundle);
				startActivityForResult(intent_threshold, 100);
				break;
			case R.id.more_dx:
				setdxNo();
				break;
			case R.id.more_his:
				Intent intent_his=new Intent(MoreActivity.this, HistoryActivity.class);
				startActivity(intent_his);
				break;
			case R.id.more_suggest:
				Intent intent_suggest=new Intent(MoreActivity.this, SuggestActivity_New.class);
				startActivity(intent_suggest);
				break;
			case R.id.more_share:
				Intent intent_share=new Intent(MoreActivity.this, ShareActivity_New.class);
				startActivity(intent_share);
				break;
			case R.id.more_about:
				Intent intent=new Intent(MoreActivity.this, AboutActivity.class);
				startActivity(intent);
				break;
			case R.id.more_luntan:
				luntan_role();
				break;
			}
		}};
		
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
			if(requestCode==100&&resultCode==RESULT_OK) {
				Bundle bundle = data.getExtras();
				int type = bundle.getInt("select_type");
				if(type != select_type)
				{
					setThresholdValue(type);
				}
			}
		};
		
		private String setValue(int type) {
			String result = "";
			if(type == 0)
			{
				result = "不提醒";
			}
			else if(type == 1)
			{
				result = "40%";
			}
			else if(type == 2)
			{
				result = "60%";
			}
			else if(type == 3)
			{
				result = "70%";
			}
			else if(type == 4)
			{
				result = "80%";
			}
			else if(type == 5)
			{
				result = "90%";
			}
			else if(type == 6)
			{
				result = "100%";
			}
			return result;
		}
		
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		//判断是否有新信息
		if(((GasStationApplication) getApplication()).isNewSuggest) {
			suggest_new.setVisibility(View.VISIBLE);
		}
		else {
			suggest_new.setVisibility(View.GONE);
		}
		IntentFilter filter=new IntentFilter();
		filter.addAction(JPushReceiver.refreshSuggest);
		registerReceiver(receiver, filter);
		//当前加载的号码与初始化后的号码不一致的时候，则认为已经切换号码
		if(initPhoneNum!=-1&&initPhoneNum!=Long.parseLong(Util.getUserInfo(MoreActivity.this).get(0))) {
			initPhoneNum=Long.parseLong(Util.getUserInfo(MoreActivity.this).get(0));
			ArrayList<String> list=Util.getUserInfo(MoreActivity.this);
			more_phone_num.setText(list.get(0));
		}
		MainActivity.getInstance().able_change();
		StatService.onResume(this);
	}
		
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK) {
			MainActivity.getInstance().loginout();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}	
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(receiver);
		StatService.onPause(this);
	}
	
	BroadcastReceiver receiver=new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			System.out.println(intent.getAction());
			suggest_new.setVisibility(View.VISIBLE);
		}};
		
	public void setdxNo() {
		showProgressDialog(R.string.tishi_loading);
		
		final Handler handler2=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				dismissProgressDialog();
				if(msg.obj!=null) {
					Map map=(Map) msg.obj;
					if(map.get("result").toString().equals("1")) {
						showCustomToast("短信退订成功");
					}
					else {
						showCustomToast(map.get("comments").toString());
					}
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
				Message m=new Message();
				LinkedList<String> wholeUrl=Util.getWholeUrl(MoreActivity.this);
				boolean flag=true;
				int num=0;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(MoreActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(MoreActivity.this);
						SharedPreferences sp=getSharedPreferences("gas", Activity.MODE_PRIVATE);
						String phoneNum = sp.getString("phoneNum", "");
						String area_code = sp.getString("area_code", "");
						
						CommonManager manager=GetWebDate.getHessionFactiory(MoreActivity.this).create(CommonManager.class, currentUsedUrl+"/hessian/commonManager", getClassLoader());
						Map result = manager.setThresHold(Long.parseLong(phoneNum), 7, area_code);
						if(result!=null) {
							m.obj=result;
						}
						flag=false;
					} catch(Error e) {
						flag=false;
						handler.sendEmptyMessage(1);
			        } catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						if(e instanceof com.caucho.hessian.client.HessianRuntimeException) {
							//手机自身网络连接异常
							if(e.getMessage().indexOf("java.net.SocketException")!=-1) {
								num++;
								if(num>=10) {
									flag=false;
									handler.sendEmptyMessage(1);
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
									handler.sendEmptyMessage(1);
								}
							}
							
						}
						else if(e instanceof com.caucho.hessian.client.HessianConnectionException) {
							//手机自身网络连接异常
							if(e.getMessage().indexOf("java.io.EOFException")!=-1) {
								num++;
								if(num>=10) {
									flag=false;
									handler.sendEmptyMessage(1);
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
									handler.sendEmptyMessage(1);
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
								handler.sendEmptyMessage(1);
							}
						}	
						m.obj=null;	
					}
					handler2.sendMessage(m);
				}				
			}}).start();
	}	
	
	public void luntan_role() {
		showProgressDialog(R.string.tishi_loading);
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				dismissProgressDialog();
				if(msg.what==1) {
					HashMap map=(HashMap) msg.obj;
					Intent intent_luntan=new Intent(MoreActivity.this, BBSMainTabActivity.class);
					Bundle bundle=new Bundle();
					bundle.putString("role_id", map.get("role_id").toString());
					intent_luntan.putExtras(bundle);
					startActivity(intent_luntan);
					
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
				LinkedList<String> wholeUrl=Util.getWholeUrl(MoreActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(MoreActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(MoreActivity.this);
						
						BbsManager bbsManager=GetWebDate.getHessionFactiory(MoreActivity.this).create(BbsManager.class, currentUsedUrl+"/hessian/bbsManager", getClassLoader());
						HashMap map=bbsManager.getBbsRole(Long.parseLong(list.get(0)));
						if(map==null) {
							m.what=0;
						}
						else {
							m.what=1;
							m.obj=map;
						}
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

}
