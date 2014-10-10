package com.linkage.gas_station.gonglve;

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

public class RecordListActivity extends BaseActivity {
	
	TextView title_name=null;
	ImageView title_back=null;
	
	ListView send_redenvelope_detaillist=null;
	SimpleAdapter adapter=null;
	ArrayList<HashMap<String, Object>> lists=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_sendredenvelopedetail);
		
		((GasStationApplication) getApplication()).tempActivity.add(RecordListActivity.this);
		
		lists=new ArrayList<HashMap<String, Object>>();
		
		init();
	}
	
	private void init() {
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText("红包明细");
		title_back=(ImageView) findViewById(R.id.title_back);
		title_back.setVisibility(View.VISIBLE);
		title_back.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
		send_redenvelope_detaillist=(ListView) findViewById(R.id.send_redenvelope_detaillist);
		adapter=new SimpleAdapter(RecordListActivity.this, lists, R.layout.adapter_recordlist, new String[]{"phone", "record", "time"}, new int[]{R.id.adapter_gift_num, R.id.adapter_gift_package, R.id.adapter_gift_time});
		send_redenvelope_detaillist.setAdapter(adapter);
		sendPackageList();
	}
	
	private void sendPackageList() {
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.what==100) {
					Object[] obj=(Object[]) msg.obj;
					for(int i=0;i<obj.length;i++) {
						Map map=(Map) obj[i];
						
						HashMap<String, Object> map_temp=new HashMap<String, Object>();
						map_temp.put("phone", map.get("phone_num").toString());
						map_temp.put("time", map.get("generate_time").toString());
						map_temp.put("record", map.get("offer_content").toString());
						lists.add(map_temp);
					}
					adapter.notifyDataSetChanged();
				}
				else if(msg.what==-2) {
					BaseActivity.showCustomToastWithContext("链路连接失败", RecordListActivity.this);
				}
				else {
					BaseActivity.showCustomToastWithContext(getResources().getString(R.string.timeout_exp), RecordListActivity.this);
				}
			}
		};
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(RecordListActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(RecordListActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(RecordListActivity.this);
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(RecordListActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map[] result=strategyManager.sendPackageList(Long.parseLong(list.get(0)), list.get(1), getIntent().getExtras().getLong("activityId"));
						if(result==null) {
							result=new Map[0];
						}
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
				
				handler.sendMessage(m);				
			}
		}).start();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		((GasStationApplication) getApplication()).tempActivity.remove(RecordListActivity.this);
	}
}
