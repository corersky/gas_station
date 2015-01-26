package com.linkage.gas_station.login;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.baidu.mobstat.StatService;
import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.main.MainActivity;
import com.linkage.gas_station.model.UpdateModel;
import com.linkage.gas_station.update.DownloadService;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.PublicManager;
import com.linkage.gasstationjni.GasJni;

import android.app.AlertDialog;
import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class AdvActivity extends BaseActivity {
	
	//各地区服务端全部Url
	String wholeUrl="";
	//确认无法激活
	boolean unLogin=false;
	TextView adv_retry=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_adv);
		
		adv_retry=(TextView) findViewById(R.id.adv_retry);
		adv_retry.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				login(Util.convertNull(Util.getIMSINum(AdvActivity.this)));
				showCustomToast("正在激活中，请稍后");
				adv_retry.setVisibility(View.GONE);
			}});
			
		//如果获取到手机号的话，直接登录；否则开始激活
		if(!Util.getUserInfo(AdvActivity.this).get(0).equals("")) {
			//获取到手机号之后判断是不是之前的sim卡，如果不是就重新激活，否则直接登录
			if(Util.getUserInfo(AdvActivity.this).get(2).equals(Util.convertNull(Util.getIMSINum(AdvActivity.this)))) {
				if(Util.isLoginOut(AdvActivity.this)) {
					login(Util.convertNull(Util.getIMSINum(AdvActivity.this)));
				}
				else {
					if(Util.getPassWord(AdvActivity.this).equals("")) {
						GasJni hj=new GasJni();
						hj.readMessageFromJNI(Util.getUserInfo(AdvActivity.this).get(0), Util.getDeviceId(AdvActivity.this));
						Util.setPassWord(AdvActivity.this, Util.getUserInfo(AdvActivity.this).get(0));
					}
					showAdv();
				}
			}
			else {
				//sim卡更换相当于重新登陆
				Util.setUserInfo(AdvActivity.this, "", "", "");
				Util.setSave(AdvActivity.this, false);
				login(Util.convertNull(Util.getIMSINum(AdvActivity.this)));
			}
		}
		else {
			Util.setUserInfo(AdvActivity.this, "", "", "");
			Util.setSave(AdvActivity.this, false);
			login(Util.convertNull(Util.getIMSINum(AdvActivity.this)));
		}		
		
		((GasStationApplication) getApplication()).tempActivity.add(AdvActivity.this);
	}
	
	public void login(final String IMSI) {
		unLogin=false;
		//IMSI没有获取到的情况下，直接用手机号验证码登陆
		if(Util.convertNull(IMSI).equals("")) {
			showCustomToast("未检测您的电信号码，请手动输入号码登陆");
			jumpActivity(LoginActivity.class);
			return;
		}
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if(msg.what==0) {
					showCustomToast(msg.obj.toString());
					jumpActivity(LoginActivity.class);
					//adv_retry.setVisibility(View.VISIBLE);
					//unLogin=true;
				}
				else {
					if(msg.obj==null) {
						showCustomToast("激活失败，请您稍后再试试");
						jumpActivity(LoginActivity.class);
						//adv_retry.setVisibility(View.VISIBLE);
						//unLogin=true;
					}
					else {
						Map result=(Map) msg.obj;
						if(result.get("active_result").toString().equals("1")) {
							if(!Util.getUserInfo(AdvActivity.this).get(0).equals(msg.getData().getString("phoneNum"))) {
								((GasStationApplication) getApplication()).isChangeSim=true;
							}
							Util.setUserInfo(AdvActivity.this, msg.getData().getString("phoneNum"), result.get("area_code").toString(), Util.convertNull(Util.getIMSINum(AdvActivity.this)));
							Util.setUserArea(AdvActivity.this, result.get("provinceCode").toString());
							//老machineCode标志位
							Util.setSimpleCode(AdvActivity.this, Util.getDeviceId(AdvActivity.this)+""+Util.getMacAddress(AdvActivity.this));
							jumpActivity(MainActivity.class);
						}
						else {
							showCustomToast(result.get("comments").toString());
							jumpActivity(LoginActivity.class);
							//adv_retry.setVisibility(View.VISIBLE);
							//unLogin=true;
						}
					}
				}
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(AdvActivity.this);
				boolean getPhoneNumComp=false;
				String phoneNum="";
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String sendUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				while(flag) {
					try {
						System.out.println("使用："+sendUrl);
						
						PublicManager publicManager=GetWebDate.getHessionFactiory(AdvActivity.this).create(PublicManager.class, sendUrl+"/hessian/publicManager", getClassLoader());
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
							Util.setWholeUrl(AdvActivity.this, wholeUrl_temp);
							Util.setStartUrl(AdvActivity.this, ((GasStationApplication) getApplicationContext()).AreaUrl);
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
							//ip 端口等错误  java.net.SocketTimeoutException
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
						currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(AdvActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
					} catch(Exception e) {
						currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
					}
					while(flag) {
						try {
							System.out.println("使用："+sendUrl);
							
							PublicManager publicManager=GetWebDate.getHessionFactiory(AdvActivity.this).create(PublicManager.class, currentUsedUrl+"/hessian/publicManager", getClassLoader());
							Map result=publicManager.setUserActive(Long.parseLong(phoneNum)
									, IMSI, 2, android.os.Build.MODEL, android.os.Build.VERSION.RELEASE, Util.getDeviceId(AdvActivity.this)+Util.getMacAddress(AdvActivity.this));
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
	public void jumpActivity(Class<?> cls) {
		if(cls==MainActivity.class) {
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					boolean flag=true;
					int num=0;
					String sendUrl=((GasStationApplication) getApplicationContext()).AreaUrl;
					if(sendUrl.equals("")) {
						sendUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
					}
					while(flag) {						
						try {
							
							PublicManager publicManager=GetWebDate.getHessionFactiory(AdvActivity.this).create(PublicManager.class, sendUrl+"/hessian/publicManager", getClassLoader());
							Util.setTimeExtra(AdvActivity.this, publicManager.getSystemTime());
							flag=false;
							System.out.println("获取服务器差值时间成功");
						} catch(Error e) {
							flag=false;
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
						}
					}					
				}}).start();
			
			JPushInterface.resumePush(getApplicationContext());
			BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(getApplicationContext());
			builder.statusBarDrawable = R.drawable.ic_launcher;
			builder.notificationFlags = Notification.FLAG_AUTO_CANCEL;  //设置为点击后自动消失
			builder.notificationDefaults =  Notification.DEFAULT_LIGHTS;  //设置为铃声（ Notification.DEFAULT_SOUND）或者震动（ Notification.DEFAULT_VIBRATE）  
			JPushInterface.setPushNotificationBuilder(1, builder);
			//定义jpush别名
			JPushInterface.setAlias(getApplicationContext(), Util.getDeviceId(AdvActivity.this)+Util.getMacAddress(AdvActivity.this), new TagAliasCallback() {

				@Override
				public void gotResult(int arg0, String arg1, Set<String> arg2) {
					// TODO Auto-generated method stub
					System.out.println("极光推送返回："+arg0);
				}});
			((GasStationApplication) getApplicationContext()).jumpJiayouNum=-1;
			//上传用户信息
			System.out.println("上传");
			Util.uploadClientInfo(AdvActivity.this);
		}
		Intent intent=new Intent();
		intent.setClass(AdvActivity.this, cls);
		startActivity(intent);
		finish();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK) {
			if(!unLogin) {
				return true;
			}			
		}
		return super.onKeyDown(keyCode, event);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		StatService.onResume(this);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		StatService.onPause(this);
	}
	
	public void showAdv() {
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if(android.os.Build.BRAND.toLowerCase().indexOf("xiaomi")!=-1) {
					readXMLInfo();
				}
				else {
					jumpActivity(MainActivity.class);
				}
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Message m=new Message();
				handler.sendMessage(m);
				
				//再次获取请求url集合
				LinkedList<String> wholeUrl=Util.getWholeUrl(AdvActivity.this);
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(AdvActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						PublicManager publicManager=GetWebDate.getHessionFactiory(AdvActivity.this).create(PublicManager.class, currentUsedUrl+"/hessian/publicManager", getClassLoader());
						Map[] result=publicManager.urlList(Util.getUserArea(AdvActivity.this));
						String wholeUrl_temp="";
						Random rand=new Random();
						int random_num=rand.nextInt(result.length*100)%result.length;
						for(int i=0;i<result.length;i++) {
							Map temp=(Map) result[i];
							if(i==random_num) {
								((GasStationApplication) getApplicationContext()).AreaUrl="http://"+temp.get("ip").toString()+":"+temp.get("port").toString()+"/flowOilStation";
							}
							if(i!=result.length-1) {
								wholeUrl_temp+="http://"+temp.get("ip").toString()+":"+temp.get("port").toString()+"/flowOilStation"+"&";
							}
							else {
								wholeUrl_temp+="http://"+temp.get("ip").toString()+":"+temp.get("port").toString()+"/flowOilStation";
							}
						}
						Util.setWholeUrl(AdvActivity.this, wholeUrl_temp);
						Util.setStartUrl(AdvActivity.this, ((GasStationApplication) getApplicationContext()).AreaUrl);
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
				
			}}).start();
	}
	
	/**
	 * 读取升级文档信息 
	 * 有参数代表进入的时候判断升级，无参数代表点击检查更新
	 */
	public void readXMLInfo() {
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if(msg.what!=0) {					
					UpdateModel model=new UpdateModel();
					Map map=(Map)msg.obj;
					model.setVersion(map.get("android").toString());
					model.setAndroid_forced_update(Integer.parseInt(map.get("android_forced_update")==null?"0":map.get("android_forced_update").toString()));
					if(map.get("android_comments")!=null) {
						model.setMessage(map.get("android_comments").toString());
					}
					else {
						model.setMessage("");
					}
					PackageManager manager=AdvActivity.this.getPackageManager();
					try {
						PackageInfo info=manager.getPackageInfo(AdvActivity.this.getPackageName(), PackageManager.GET_CONFIGURATIONS);
						if(info.versionCode<Integer.parseInt(model.getVersion())) {
							if(model.getAndroid_forced_update()==1) {
								showUpdateBoxMust(AdvActivity.this, map.get("android_url").toString(), model.getMessage(), map.get("android").toString());
							}
							else {
								showUpdateBox(map.get("android_url").toString(), model.getMessage(), map.get("android").toString());
							}
						}
						else {
							jumpActivity(MainActivity.class);
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				else {
					jumpActivity(MainActivity.class);
				}
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
						ArrayList<String> list=Util.getUserInfo(AdvActivity.this);
						
						PublicManager publicManager=GetWebDate.getHessionFactiory(AdvActivity.this).create(PublicManager.class, sendUrl+"/hessian/publicManager", getClassLoader());
						Map map=publicManager.clientVersion(0l, list.get(1).equals("")?null:list.get(1));
						flag=false;
						m.obj=map;
						m.what=1;
					} catch(Error e) {
						flag=false;
						m.what=0;
			        } catch(Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						if(e instanceof com.caucho.hessian.client.HessianRuntimeException) {
							//手机自身网络连接异常
							if(e.getMessage().indexOf("java.net.SocketException")!=-1) {
								num++;
								if(num>=10) {
									flag=false;
									m.what=0;
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
								if(sendUrl.equals(((GasStationApplication) getApplicationContext()).COMMONURL[0])) {
									sendUrl=((GasStationApplication) getApplicationContext()).COMMONURL[1];
								}
								else if(sendUrl.equals(((GasStationApplication) getApplicationContext()).COMMONURL[1])) {
									flag=false;
									m.what=0;
								}
							}
						}
						else if(e instanceof com.caucho.hessian.client.HessianConnectionException) {
							//手机自身网络连接异常
							if(e.getMessage().indexOf("java.io.EOFException")!=-1) {
								num++;
								if(num>=10) {
									flag=false;
									m.what=0;
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
									m.what=0;
								}
							}
						}
						else {
							if(sendUrl.equals(((GasStationApplication) getApplicationContext()).COMMONURL[0])) {
								sendUrl=((GasStationApplication) getApplicationContext()).COMMONURL[1];
							}
							else if(sendUrl.equals(((GasStationApplication) getApplicationContext()).COMMONURL[1])) {
								flag=false;
								m.what=0;
							}
						}
					}
				}
				handler.sendMessage(m);
			}}).start();
	}
	
	/**
     * 显示下线提示
     * @param mContext
     */
    public void showUpdateBoxMust(final Context mContext, final String download_url, String update_des, final String download_version) {
    	final WindowManager wmanager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
    	final View view = LayoutInflater.from(mContext).inflate(R.layout.activity_update, null);
    	TextView update_desp = (TextView) view.findViewById(R.id.update_desp);
    	ImageView update_now=(ImageView) view.findViewById(R.id.update_now);
		update_now.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				wmanager.removeView(view);
				if(((GasStationApplication) getApplication()).tempActivity!=null&&((GasStationApplication) getApplication()).tempActivity.size()>0) {
					for(int i=0;i<((GasStationApplication) getApplication()).tempActivity.size();i++) {
						((GasStationApplication) getApplication()).tempActivity.get(i).finish();
					}
				}
				if(Util.isServiceWorked(AdvActivity.this, "com.linkage.gas_station.update.DownloadService")) {
					return;
				}
				Intent intent=new Intent(AdvActivity.this, DownloadService.class);
				Bundle bundle=new Bundle();
				bundle.putString("download_url", download_url);
				bundle.putString("download_name", getResources().getString(R.string.app_name));
				bundle.putString("download_id", "0");
				bundle.putString("download_version", download_version);
				intent.putExtras(bundle);
				startService(intent);
			}});
		ImageView update_later=(ImageView) view.findViewById(R.id.update_later);
		update_later.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				wmanager.removeView(view);
				if(((GasStationApplication) getApplication()).tempActivity!=null&&((GasStationApplication) getApplication()).tempActivity.size()>0) {
					for(int i=0;i<((GasStationApplication) getApplication()).tempActivity.size();i++) {
						((GasStationApplication) getApplication()).tempActivity.get(i).finish();
					}
				}
			}});
    	update_des=update_des.replace("\\n", "<br>");
		update_desp.setText(Html.fromHtml(update_des));
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.format = 1;
        layoutParams.flags =layoutParams.flags | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        layoutParams.alpha = 1f;
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        wmanager.addView(view, layoutParams);
        wmanager.updateViewLayout(view, layoutParams);
    }
    
    private void showUpdateBox(final String download_url, String update_des, final String download_version) {  
        final AlertDialog dialog=new AlertDialog.Builder(AdvActivity.this).create(); 
        dialog.show();  
        Window window = dialog.getWindow();
        window.setContentView(R.layout.activity_update);
        TextView update_desp=(TextView) window.findViewById(R.id.update_desp);
        update_des=update_des.replace("\\n", "<br>");
		update_desp.setText(Html.fromHtml(update_des));
		ImageView update_now=(ImageView) window.findViewById(R.id.update_now);
		update_now.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(Util.isServiceWorked(AdvActivity.this, "com.linkage.gas_station.update.DownloadService")) {
					return;
				}
				Intent intent=new Intent(AdvActivity.this, DownloadService.class);
				Bundle bundle=new Bundle();
				bundle.putString("download_url", download_url);
				bundle.putString("download_name", getResources().getString(R.string.app_name));
				bundle.putString("download_id", "0");
				bundle.putString("download_version", download_version);
				intent.putExtras(bundle);
				startService(intent);
				dialog.cancel();
				jumpActivity(MainActivity.class);
			}});
		ImageView update_later=(ImageView) window.findViewById(R.id.update_later);
		update_later.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.cancel();
				jumpActivity(MainActivity.class);
			}});
    } 
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		((GasStationApplication) getApplication()).tempActivity.remove(AdvActivity.this);
	}
}
