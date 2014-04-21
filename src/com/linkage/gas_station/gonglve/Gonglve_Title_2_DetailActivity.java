package com.linkage.gas_station.gonglve;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

import com.baidu.mobstat.StatService;
import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.StrategyManager;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class Gonglve_Title_2_DetailActivity extends BaseActivity {
	
	ImageView title_back=null;
	TextView title_name=null;
	TextView gonglve_title_2_detail_name=null;
	TextView gonglve_title_2_detail_content=null;
	TextView gonglve_title_2_detail_time=null;
	TextView gonglve_title_2_detail_link=null;
	LinearLayout gonglve_title_2_layout=null;
	ProgressDialog pd=null;
	
	int activityId=-1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_gonglve_title_2_detail);
		
		((GasStationApplication) getApplication()).tempActivity.add(Gonglve_Title_2_DetailActivity.this);
		activityId=getIntent().getExtras().getInt("activityId");
		
		init();
		getData();
	}
	
	public void init() {
		gonglve_title_2_layout=(LinearLayout) findViewById(R.id.gonglve_title_2_layout);
		gonglve_title_2_layout.setVisibility(View.GONE);
		title_name=(TextView) findViewById(R.id.title_name);
		gonglve_title_2_detail_name=(TextView) findViewById(R.id.gonglve_title_2_detail_name);
		gonglve_title_2_detail_content=(TextView) findViewById(R.id.gonglve_title_2_detail_content);
		gonglve_title_2_detail_time=(TextView) findViewById(R.id.gonglve_title_2_detail_time);
		gonglve_title_2_detail_link=(TextView) findViewById(R.id.gonglve_title_2_detail_link);	
		title_back=(ImageView) findViewById(R.id.title_back);
		title_back.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
	}
	
	public void getData() {
		
		pd=ProgressDialog.show(Gonglve_Title_2_DetailActivity.this, getResources().getString(R.string.tishi), getResources().getString(R.string.tishi_loading));
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if(msg.what==1) {
					if(msg.obj!=null) {					
						Map map=(Map) msg.obj;
						title_name.setText(map.get("activity_name").toString());
						gonglve_title_2_detail_name.setText(map.get("activity_name").toString());
						gonglve_title_2_detail_content.setText(map.get("activity_description").toString());
						gonglve_title_2_detail_time.setText(map.get("activity_time_limit").toString());
						gonglve_title_2_detail_link.setText(map.get("activity_url").toString());
						gonglve_title_2_layout.setVisibility(View.VISIBLE);
					}
					else {
						showCustomToast("获取数据失败，请稍后再试试");
					}
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
				LinkedList<String> wholeUrl=Util.getWholeUrl(Gonglve_Title_2_DetailActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(Gonglve_Title_2_DetailActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(Gonglve_Title_2_DetailActivity.this);
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(Gonglve_Title_2_DetailActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map map=strategyManager.getActivityInfo((long) activityId, list.get(1));
						m.obj=map;
						m.what=1;
						flag=false;
						currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl;
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
		((GasStationApplication) getApplication()).tempActivity.remove(Gonglve_Title_2_DetailActivity.this);
		super.onDestroy();
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

}
