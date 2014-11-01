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

public class CashReceiverActivity extends BaseActivity {
	
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
		
		((GasStationApplication) getApplication()).tempActivity.add(CashReceiverActivity.this);
		
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
		title_name.setText("我的现金券");	
		
		receiver_qxjy_group=(LinearLayout) findViewById(R.id.receiver_qxjy_group);
		receiver_qxjy_desp=(TextView) findViewById(R.id.receiver_qxjy_desp);
		receiver_qxjy_desp.setText("使用现金券订购流量包，现金券抵扣当月流量包费用。使用现金券订购闲时包或定向包，现金券次月自动抵扣上月流量包费用，最多连续享受3个月（期间用户未退订）");
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
					if(mapTemp.containsKey("package10")) {
						map.put("package10", mapTemp.get("package10").toString());
					}
					if(mapTemp.containsKey("package20")) {
						map.put("package20", mapTemp.get("package20").toString());
					}
					if(mapTemp.containsKey("package30")) {
						map.put("package30", mapTemp.get("package30").toString());
					}
					if(map.size()!=0) {
						receiver_qxjy_group.removeAllViews();
					}
					Iterator<Entry<String, String>> it=map.entrySet().iterator();
					while(it.hasNext()) {
						Entry<String, String> entry= it.next();
						ImageView imageview=new ImageView(CashReceiverActivity.this);
						if(entry.getKey().toString().substring(7).equals("10")) {
							imageview.setImageResource(R.drawable.xj_4);
						}
						if(entry.getKey().toString().substring(7).equals("20")) {
							imageview.setImageResource(R.drawable.xj_8);
						}
						if(entry.getKey().toString().substring(7).equals("30")) {
							imageview.setImageResource(R.drawable.xj_12);
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
				LinkedList<String> wholeUrl=Util.getWholeUrl(CashReceiverActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(CashReceiverActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(CashReceiverActivity.this);
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(CashReceiverActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						HashMap result=strategyManager.queryXjTicket(Long.parseLong(list.get(0)), list.get(1), 3000);
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
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		((GasStationApplication) getApplication()).tempActivity.remove(CashReceiverActivity.this);
	}

}
