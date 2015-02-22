package com.linkage.gas_station.memberday;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.StrategyManager;

public class FlowDayRecordListActivity extends BaseActivity {
	
	TextView title_name=null;
	ImageView title_back=null;
	
	ArrayList<HashMap<String, Object>> lists=null;
	
	ListView flowdayrecord_list=null;
	SimpleAdapter adapter=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_flowdayrecordlist);
		
		lists=new ArrayList<HashMap<String, Object>>();
		
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText("中奖用户查询");
		title_back=(ImageView) findViewById(R.id.title_back);
		title_back.setVisibility(View.VISIBLE);
		title_back.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
		
		flowdayrecord_list=(ListView) findViewById(R.id.flowdayrecord_list);
		adapter=new SimpleAdapter(FlowDayRecordListActivity.this, lists, R.layout.adapter_flowdayrecordlist, new String[] {"time", "phone"}, new int[]{R.id.adapter_flowdayrecordlist_time, R.id.adapter_flowdayrecordlist_phone});
		flowdayrecord_list.setAdapter(adapter);
		
		qhMSList();
	}
	
	private void qhMSList() {
		final Handler handlerRightList=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.what==1) {
					if(msg.obj!=null) {
						Object[] obj=(Object[]) msg.obj;
						for(int i=0;i<obj.length;i++) {
							HashMap<String, Object> map=new HashMap<String, Object>();
							map.put("time", ((Map) obj[i]).get("generate_time").toString());
							map.put("phone", ((Map) obj[i]).get("phone_num").toString());
							lists.add(map);
						}
						adapter.notifyDataSetChanged();
					}
				}
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");
				}
				else if(msg.what==0) {
					showCustomToast(getResources().getString(R.string.timeout_exp));
				}
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(FlowDayRecordListActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(FlowDayRecordListActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(FlowDayRecordListActivity.this);
						StrategyManager strategyManager=GetWebDate.getActivityHessionFactiory(FlowDayRecordListActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map[] map=strategyManager.qhMSList(Long.parseLong(list.get(0)), list.get(1), Long.parseLong(getIntent().getExtras().getString("activityId")), Long.parseLong(getIntent().getExtras().getString("prizeId")));
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
				handlerRightList.sendMessage(m);
			}}).start();
	}
}
