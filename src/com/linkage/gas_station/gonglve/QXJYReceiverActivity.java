package com.linkage.gas_station.gonglve;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map.Entry;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.StrategyManager;

public class QXJYReceiverActivity extends BaseActivity {
	
	LinearLayout receiver_qxjy_group=null;
	TextView receiver_qxjy_desp=null;
	

	ImageView title_back=null;
	TextView title_name=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_qxjyreceiver);
		
		init();
		queryTicket();
	}
	
	public void init() {
		title_back=(ImageView) findViewById(R.id.title_back);
		title_back.setVisibility(View.VISIBLE);
		title_back.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText("我的电子券");	
		
		receiver_qxjy_group=(LinearLayout) findViewById(R.id.receiver_qxjy_group);
		receiver_qxjy_desp=(TextView) findViewById(R.id.receiver_qxjy_desp);
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
						receiver_qxjy_group.removeAllViews();
					}
					Iterator<Entry<String, String>> it=map.entrySet().iterator();
					while(it.hasNext()) {
						Entry<String, String> entry= it.next();
						ImageView imageview=new ImageView(QXJYReceiverActivity.this);
						if(entry.getKey().toString().substring(7).equals("30")) {
							imageview.setImageResource(R.drawable.qxjy30);
						}
						if(entry.getKey().toString().substring(7).equals("100")) {
							imageview.setImageResource(R.drawable.qxjy100);
						}
						if(entry.getKey().toString().substring(7).equals("200")) {
							imageview.setImageResource(R.drawable.qxjy200);
						}
						imageview.setPadding(10, 10, 10, 10);
						LinearLayout.LayoutParams params=new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
						params.weight=1;
						receiver_qxjy_group.addView(imageview, params);	
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
				LinkedList<String> wholeUrl=Util.getWholeUrl(QXJYReceiverActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(QXJYReceiverActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(QXJYReceiverActivity.this);
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(QXJYReceiverActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						HashMap result=strategyManager.queryTicket(Long.parseLong(list.get(0)), list.get(1), 3000);
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

}
