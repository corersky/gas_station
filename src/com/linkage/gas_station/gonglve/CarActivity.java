package com.linkage.gas_station.gonglve;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.StrategyManager;

public class CarActivity extends BaseActivity {
	
	ImageView title_back=null;
	TextView title_name=null;
	
	TextView amount_text=null;
	TextView unused_text=null;
	TextView car_operate_flow=null;
	TextView car_operate_ok=null;
	ImageView cylinder=null;
	
	String total_flow="";
	String residue_flow="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_car);
		
		total_flow=getIntent().getExtras().getString("total_flow");
		residue_flow=getIntent().getExtras().getString("residue_flow");
		
		((GasStationApplication) getApplication()).tempActivity.add(CarActivity.this);
		
		init();
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
		title_name.setText("领取流量");
		
		amount_text=(TextView) findViewById(R.id.amount_text);
		unused_text=(TextView) findViewById(R.id.unused_text);
		car_operate_flow=(TextView) findViewById(R.id.car_operate_flow);
		car_operate_flow.setText(residue_flow);
		cylinder=(ImageView) findViewById(R.id.cylinder);
		car_operate_ok=(TextView) findViewById(R.id.car_operate_ok);
		car_operate_ok.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				car_flow_operate();
			}});
		initUIData();
	}
	
	/**
	 * 加载页面数据
	 */
	public void initUIData() {
		int ratio=0;
		if(Integer.parseInt(total_flow)!=0) {
			ratio=(int) (Integer.parseInt(residue_flow)*100/Integer.parseInt(total_flow));
		}
		amount_text.setText(total_flow+"MB");
		unused_text.setText(residue_flow+"MB");
		getImageRes(ratio);	
	}
	
	/**
	 * 返回图片id
	 * @param ratio
	 * @return
	 */
	public void getImageRes(int ratio) {
		String imageId=String.valueOf(ratio/10)+String.valueOf(ratio%10>=5?5:0);
		//需要判断是不是小于5并且大于0
		if(imageId.equals("00")&&ratio%10>0) {
			imageId="05";
		}
		cylinder.setImageResource(getResources().getIdentifier(getPackageName()+":drawable/cylinder_small_"+imageId, null,null));
	}
	
	
	/**
	 * 流量倍增领取
	 */
	public void car_flow_operate() {
		showProgressDialog(R.string.tishi_loading);
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				dismissProgressDialog();
				if(msg.what==1) {
					Map map=(Map) msg.obj;			
					showCustomToast(map.get("comments").toString());
					if(map.get("result").toString().equals("1")) {
						residue_flow=""+(Integer.parseInt(residue_flow)-Integer.parseInt(car_operate_flow.getText().toString()));
						car_operate_flow.setText(residue_flow);
						initUIData();
						if(getIntent().getExtras().getBoolean("needRefresh")) {
							((GasStationApplication) getApplicationContext()).isRefreshTuan=true;
						}
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
				LinkedList<String> wholeUrl=Util.getWholeUrl(CarActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(CarActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(CarActivity.this);
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(CarActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map map=strategyManager.double_flow_operate(Long.parseLong(list.get(0)), 1, 0l, Double.parseDouble(car_operate_flow.getText().toString()), Long.parseLong(getIntent().getExtras().getString("activityId")), list.get(1));
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
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		((GasStationApplication) getApplication()).tempActivity.remove(CarActivity.this);
	}
}
