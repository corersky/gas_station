package com.linkage.gas_station.main;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

import cn.jpush.android.api.JPushInterface;

import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.login.LoginOutActivity;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.CommonManager;
import com.linkage.gas_station.util.hessian.GetWebDate;

import android.app.AlertDialog;
import android.app.Service;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.WindowManager;

public class CheckClientState extends Service {
	
	boolean flag=true;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		flag=true;
		updateMachineCode();	
		return super.onStartCommand(intent, flags, startId);
	}
	
	public void updateMachineCode() {
		System.out.println("唯一码："+Util.getSimpleCode(CheckClientState.this));
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.what==1) {
					
					Util.setSimpleCode(CheckClientState.this, Util.getDeviceId(CheckClientState.this)+""+Util.getMacAddress(CheckClientState.this));
					//检查客户端登陆状态
					checkClientState();	
				}
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message m=new Message();
				if(Util.getSimpleCode(CheckClientState.this).equals(""+Util.getDeviceId(CheckClientState.this)+""+Util.getMacAddress(CheckClientState.this))) {
					System.out.println("无需替换唯一码成功");
					m.what=1;
				}
				else {
					System.out.println("开始上传唯一码");
					LinkedList<String> wholeUrl=Util.getWholeUrl(CheckClientState.this);
					int num=0;
					boolean flag=true;
					String currentUsedUrl="";
					try {
						currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(CheckClientState.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
					} catch(Exception e) {
						currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
					}
					while(flag) {					
						try {
							ArrayList<String> list=Util.getUserInfo(CheckClientState.this);
							
							CommonManager commonManager = GetWebDate.getHessionFactiory(CheckClientState.this).create(CommonManager.class, currentUsedUrl+"/hessian/commonManager", getClassLoader());
							int result=1;
							if(Util.getSimpleCode(CheckClientState.this).equals("")) {
								System.out.println("从未上传过更新后的唯一码");
								result=commonManager.updateMachineCode(Util.getDeviceId(CheckClientState.this),
										Util.getDeviceId(CheckClientState.this)+""+Util.getMacAddress(CheckClientState.this), Long.parseLong(list.get(0)), list.get(1));
							}
							else {
								System.out.println("已经上传过更新后的唯一码");
								result=commonManager.updateMachineCode(Util.getSimpleCode(CheckClientState.this),
										Util.getDeviceId(CheckClientState.this)+""+Util.getMacAddress(CheckClientState.this), Long.parseLong(list.get(0)), list.get(1));
							}
							m.what=result;
							System.out.println("唯一码上传成功"+result);
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
				}
				handler.sendMessage(m);
			}}).start();
	}
	
	/**
	 * 检查客户端状态
	 */
	public void checkClientState() {
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				System.out.println("客户端状态："+msg.what);
				if(msg.what==4) {
					//极光推送关闭
		    		JPushInterface.stopPush(getApplicationContext());
		    		((GasStationApplication) getApplicationContext()).isJumpToMonitor=false;
		    		
		    		Intent intent_=new Intent();
	    	        intent_.setClass(CheckClientState.this, LoginOutActivity.class);
	    	        intent_.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	    	        startActivity(intent_);
				}
				else {
					//上传用户信息
					saveSource();
				}
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(CheckClientState.this);
				int num=0;
				boolean flag=true;
				Message m=new Message();
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(CheckClientState.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}				
				while(flag) {					
					try {
						ArrayList<String> list=Util.getUserInfo(CheckClientState.this);
						
						CommonManager commonManager = GetWebDate.getHessionFactiory(CheckClientState.this).create(CommonManager.class, currentUsedUrl+"/hessian/commonManager", getClassLoader());
						int result=commonManager.clientState(Util.getDeviceId(CheckClientState.this)+Util.getMacAddress(CheckClientState.this), 3);
						m.what=result;
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
					}
				}
				handler.sendMessage(m);
			}}).start();
	}
	
	/**
	 * 上传用户渠道信息
	 */
	public void saveSource() {
		if(Util.getSave(CheckClientState.this)) {
			return ;
		}
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				Util.setSave(CheckClientState.this, true);
				super.handleMessage(msg);
				Map map=(Map) msg.obj;
				if(map!=null&&map.get("result").toString().equals("1")) {
					AlertDialog alert=new AlertDialog.Builder(CheckClientState.this).create();
					alert.setTitle("提示");
					alert.setMessage(map.get("comments").toString());
					alert.setButton(DialogInterface.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							
						}
					});
					alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
					alert.show();
				}
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				
				LinkedList<String> wholeUrl=Util.getWholeUrl(CheckClientState.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(CheckClientState.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(CheckClientState.this);
						
						PackageManager manager=getPackageManager();
						CommonManager commonManager=GetWebDate.getHessionFactiory(CheckClientState.this).create(CommonManager.class, ((GasStationApplication) getApplicationContext()).AreaUrl+"/hessian/commonManager", getClassLoader());
						Map map=commonManager.saveSource(Util.getDeviceId(CheckClientState.this)+Util.getMacAddress(CheckClientState.this), "", Long.parseLong(list.get(0)), list.get(1));
						flag=false;
						((GasStationApplication) getApplicationContext()).AreaUrl=currentUsedUrl;
						m.obj=map;
						handler.sendMessage(m);
					} catch(Error e) {
						flag=false;
						m.what=-1;
			        } catch(Exception e) {
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
  	
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		flag=false;
		super.onDestroy();
	}
}
