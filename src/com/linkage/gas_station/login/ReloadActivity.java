package com.linkage.gas_station.login;

import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.baidu.mobstat.StatService;
import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.main.MainActivity;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.PublicManager;

public class ReloadActivity extends BaseActivity {
	
	LinearLayout manual_login=null;
	LinearLayout automatic_login=null;
	EditText login_phone_num=null;//18951765220  18951765198
	EditText login_hqyz_num=null;//222222
	TextView login_hqyz=null;
	TextView login=null;
	TextView login_hqyz_time=null;
	
	ProgressDialog pd=null;
	
	Timer timer=null;
	LoginTimer timetask=null;
	
	//各地区服务端全部Url
	String wholeUrl_temp="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_login);
		
		Util.setLoginOut(ReloadActivity.this, true);
		
		init();
	}
	
	public void init() {
		manual_login=(LinearLayout) findViewById(R.id.manual_login);
		manual_login.setVisibility(View.GONE);
		automatic_login=(LinearLayout) findViewById(R.id.automatic_login);
		automatic_login.setVisibility(View.VISIBLE);
		login_hqyz_time=(TextView) findViewById(R.id.login_hqyz_time);
		login_phone_num=(EditText) findViewById(R.id.login_phone_num);
		login_phone_num.setText(Util.getUserInfo(ReloadActivity.this).get(0));
		login_phone_num.setEnabled(false);
		login_hqyz_num=(EditText) findViewById(R.id.login_hqyz_num);
		login_hqyz=(TextView) findViewById(R.id.login_hqyz);
		if(((GasStationApplication) getApplicationContext()).loginTime!=0) {
			login_hqyz.setVisibility(View.GONE);
			login_hqyz_time.setVisibility(View.VISIBLE);
		}
		login_hqyz.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String ct = "^((133)|(153)|(177)|(18[0,1,9]))\\d{8}$";
				if(login_phone_num.getText().toString().matches(ct)) {
					verCode();
				}
				else {
					showCustomToast("请输入天翼手机号码");
				}
			}});
		login=(TextView) findViewById(R.id.login);
		login.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				String ct = "^((133)|(153)|(177)|(18[0,1,9]))\\d{8}$";
				if(login_phone_num.getText().toString().matches(ct)) {
					userActive();
				}
				else {
					showCustomToast("请输入天翼手机号码");
				}				
			}});
		
		imsi_login(Util.convertNull(Util.getIMSINum(ReloadActivity.this)));
	}
	
	public void imsi_login(final String IMSI) {
		
		//IMSI没有获取到的情况下，直接用手机号验证码登陆
		if(Util.convertNull(IMSI).equals("")) {
			showCustomToast("未检测您的电信号码，请手动输入号码登陆");
			manual_login.setVisibility(View.VISIBLE);
			automatic_login.setVisibility(View.GONE);
			return;
		}
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if(msg.what==0) {
					showCustomToast(msg.obj.toString());
					manual_login.setVisibility(View.VISIBLE);
					automatic_login.setVisibility(View.GONE);
				}
				else {
					if(msg.obj==null) {
						showCustomToast("激活失败，请您稍后再试试");
						manual_login.setVisibility(View.VISIBLE);
						automatic_login.setVisibility(View.GONE);
					}
					else {
						Map result=(Map) msg.obj;
						if(result.get("active_result").toString().equals("1")) {
							Util.setUserInfo(ReloadActivity.this, msg.getData().getString("phoneNum"), result.get("area_code").toString(), Util.convertNull(Util.getIMSINum(ReloadActivity.this)));
							Util.setUserArea(ReloadActivity.this, result.get("provinceCode").toString());
							jumpMainActivity(MainActivity.class);
						}
						else {
							showCustomToast(result.get("comments").toString());
							manual_login.setVisibility(View.VISIBLE);
							automatic_login.setVisibility(View.GONE);
						}
					}
				}
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(ReloadActivity.this);
				boolean getPhoneNumComp=false;
				String phoneNum="";
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String sendUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				while(flag) {
					try {
						
						PublicManager publicManager=GetWebDate.getHessionFactiory(ReloadActivity.this).create(PublicManager.class, sendUrl+"/hessian/publicManager", getClassLoader());
						Map map=publicManager.getPhoneNumAndUrl(IMSI);
						if(map==null) {
							m.what=0;
							m.obj=getResources().getString(R.string.login_phonenum_error);
						}
						else {
							Object[] host=(Object[])map.get("hosts");
							String wholeUrl_temp="";
							Random rand=new Random();
							int random_num=rand.nextInt(host.length*100)%host.length;
							for(int i=0;i<host.length;i++) {
								Map temp=(Map) host[i];
								if(i==random_num) {
									((GasStationApplication) getApplicationContext()).AreaUrl="http://"+temp.get("ip").toString()+":"+temp.get("port").toString()+"/flowOilStation";
								}
								if(i!=host.length-1) {
									wholeUrl_temp+="http://"+temp.get("ip").toString()+":"+temp.get("port").toString()+"/flowOilStation"+"&";
								}
								else {
									wholeUrl_temp+="http://"+temp.get("ip").toString()+":"+temp.get("port").toString()+"/flowOilStation";
								}
							}
							phoneNum=map.get("phone_num").toString();
							Util.setWholeUrl(ReloadActivity.this, wholeUrl_temp);
							Util.setStartUrl(ReloadActivity.this, ((GasStationApplication) getApplicationContext()).AreaUrl);
							if(Util.convertNull(phoneNum).equals("")) {
								m.what=0;
								m.obj=getResources().getString(R.string.login_phonenum_error);
							}
							else {
								getPhoneNumComp=true;
							}
						}
						flag=false;
					} catch(Error e) {
						flag=false;
						m.what=0;
						m.obj="链路连接失败";
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
						m.what=0;
						m.obj=getResources().getString(R.string.timeout_exp);
					}
				}
				
				//通过imsi获取手机号码失败的话，就直接进入登陆界面手动输入手机号
				if(!getPhoneNumComp) {
					handler.sendMessage(m);
				}
				else {
					num=0;
					flag=true;
					String currentUsedUrl="";
					try {
						currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(ReloadActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
					} catch(Exception e) {
						currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
					}
					while(flag) {
						try {
							
							PublicManager publicManager=GetWebDate.getHessionFactiory(ReloadActivity.this).create(PublicManager.class, currentUsedUrl+"/hessian/publicManager", getClassLoader());
							Map result=publicManager.setUserActive(Long.parseLong(phoneNum)
									, IMSI, 2, android.os.Build.MODEL, android.os.Build.VERSION.RELEASE, Util.getDeviceId(ReloadActivity.this)+Util.getMacAddress(ReloadActivity.this));
							m.what=1;
							m.obj=result;
							Bundle bundle=new Bundle();
							bundle.putString("phoneNum", phoneNum);
							m.setData(bundle);
							flag=false;
							((GasStationApplication) getApplicationContext()).AreaUrl=currentUsedUrl;
						} catch(Error e) {
							flag=false;
							m.what=0;
							m.obj="链路连接失败";
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
							m.obj=getResources().getString(R.string.timeout_exp);
						}
					}					
					handler.sendMessage(m);
				}
				
			}}).start();
	}
	
	/**
	 * 跳转界面
	 * @param cls
	 */
	public void jumpMainActivity(Class<?> cls) {

		//老machineCode标志位
		Util.setSimpleCode(ReloadActivity.this, Util.getDeviceId(ReloadActivity.this)+""+Util.getMacAddress(ReloadActivity.this));
		JPushInterface.resumePush(getApplicationContext());
		JPushInterface.setAlias(getApplicationContext(), Util.getDeviceId(ReloadActivity.this)+Util.getMacAddress(ReloadActivity.this), new TagAliasCallback() {

			@Override
			public void gotResult(int arg0, String arg1, Set<String> arg2) {
				// TODO Auto-generated method stub
				System.out.println("极光推送返回："+arg0);
			}});
		((GasStationApplication) getApplicationContext()).jumpJiayouNum=-1;
		//上传用户信息
		System.out.println("上传");
		Util.uploadClientInfo(ReloadActivity.this);
	
		Intent intent=new Intent();
		intent.setClass(ReloadActivity.this, cls);
		startActivity(intent);
		finish();
	}
	
	/**
	 * 获取验证码
	 */
	public void verCode() {
		
		pd=ProgressDialog.show(ReloadActivity.this, getResources().getString(R.string.tishi), getResources().getString(R.string.tishi_loading));
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if(msg.what==1) {
					showCustomToast(getResources().getString(R.string.yzm_comp));
					//验证码开启标志位
					login_hqyz_time.setVisibility(View.VISIBLE);
					login_hqyz.setVisibility(View.GONE);
					((GasStationApplication) getApplicationContext()).loginTime=new Date().getTime();
				}
				else if(msg.what==0) {
					showCustomToast(getResources().getString(R.string.yzm_exp));
				}
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");
				}
				else {
					showCustomToast(getResources().getString(R.string.timeout_exp));
				}
				pd.dismiss();
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String sendUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				while(flag) {
					try {
						
						PublicManager publicManager=GetWebDate.getHessionFactiory(ReloadActivity.this).create(PublicManager.class, sendUrl+"/hessian/publicManager", getClassLoader());
						Map map=publicManager.getActiveVerCode(Long.parseLong(login_phone_num.getText().toString()));
						Object[] host=(Object[])map.get("hosts");
						Random rand=new Random();
						int random_num=rand.nextInt(host.length*100)%host.length;
						for(int i=0;i<host.length;i++) {
							Map temp=(Map) host[i];
							if(i==random_num) {
								((GasStationApplication) getApplicationContext()).AreaUrl="http://"+temp.get("ip").toString()+":"+temp.get("port").toString()+"/flowOilStation";
							}
							if(i!=host.length-1) {
								wholeUrl_temp+="http://"+temp.get("ip").toString()+":"+temp.get("port").toString()+"/flowOilStation"+"&";
							}
							else {
								wholeUrl_temp+="http://"+temp.get("ip").toString()+":"+temp.get("port").toString()+"/flowOilStation";
							}
						}
						Util.setWholeUrl(ReloadActivity.this, wholeUrl_temp);
						int verCode=Integer.parseInt(map.get("result").toString());
						m.what=verCode;
						flag=false;
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
							//ip 端口等错误 java.net.SocketTimeoutException
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
						m.what=2;
					}
				}				
				handler.sendMessage(m);
			}}).start();
	}
	
	/**
	 * 登陆
	 */
	public void userActive() {
		
		pd=ProgressDialog.show(ReloadActivity.this, getResources().getString(R.string.tishi), getResources().getString(R.string.tishi_loading));
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if(msg.what==0) {
					showCustomToast(getResources().getString(R.string.timeout_exp));
				}
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");
				}
				else {
					if(msg.obj!=null) {

						Map result=(Map) msg.obj;
						if(result.get("active_result").toString().equals("1")) {
							//老machineCode标志位
							Util.setSimpleCode(ReloadActivity.this, Util.getDeviceId(ReloadActivity.this)+""+Util.getMacAddress(ReloadActivity.this));
							//将数据存放至配置文件中
							Util.setUserInfo(ReloadActivity.this, login_phone_num.getText().toString(), result.get("area_code").toString(), Util.convertNull(Util.getIMSINum(ReloadActivity.this)));
							Util.setStartUrl(ReloadActivity.this, ((GasStationApplication) getApplicationContext()).AreaUrl);
							Util.setUserArea(ReloadActivity.this, result.get("provinceCode").toString());
							//将之前的网络判断关闭
							stopService(new Intent("com.linkage.CheckClientState"));
							//定义jpush别名
							JPushInterface.resumePush(getApplicationContext());
							JPushInterface.setAlias(getApplicationContext(), Util.getDeviceId(ReloadActivity.this)+Util.getMacAddress(ReloadActivity.this), new TagAliasCallback() {

								@Override
								public void gotResult(int arg0, String arg1,
										Set<String> arg2) {
									// TODO Auto-generated method stub
									System.out.println("极光推送返回："+arg0);
								}});
							//重置一下加油界面滚动位置
							((GasStationApplication) getApplication()).jumpJiayouNum=-1;
							//intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);					
							//上传用户信息
							System.out.println("上传");
							Util.uploadClientInfo(ReloadActivity.this);
							finish();
						}
						else {
							showCustomToast(result.get("comments").toString());
						}					
					}
					else {
						showCustomToast("激活失败，请您稍后再试试");
					}
				}
				pd.dismiss();
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(ReloadActivity.this);
				Message m=new Message();
				boolean flag=true;
				int num=0;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(ReloadActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {					
						
						PublicManager publicManager=GetWebDate.getHessionFactiory(ReloadActivity.this).create(PublicManager.class, currentUsedUrl+"/hessian/publicManager", getClassLoader());
						Map result=publicManager.setUserActive(Long.parseLong(login_phone_num.getText().toString())
								, login_hqyz_num.getText().toString().trim(), 2, android.os.Build.MODEL, android.os.Build.VERSION.RELEASE, Util.getDeviceId(ReloadActivity.this)+Util.getMacAddress(ReloadActivity.this));
						m.obj=result;
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
	
	class LoginTimer extends TimerTask {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message m=new Message();
			handler_loginTime.sendMessage(m);
		}
		
	}
	
	Handler handler_loginTime=new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if(((GasStationApplication) getApplication()).loginTime!=0) {
				long day=((GasStationApplication) getApplication()).loginTime;
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
					login_hqyz_time.setVisibility(View.GONE);
					login_hqyz.setVisibility(View.VISIBLE);
					((GasStationApplication) getApplication()).loginTime=0;
				}
				else {
					login_hqyz_time.setText(""+(60-second_)+"秒发");
				}
			}
		}
	};
	
	protected void onResume() {
		super.onResume();
		timer=new Timer();
		timetask=new LoginTimer();
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
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK) {
			if(getIntent().getExtras()!=null) {
				return true;
			}
			else {
				MainActivity.getInstance().finish();
			}			
		}
		return super.onKeyDown(keyCode, event);
	}

}
