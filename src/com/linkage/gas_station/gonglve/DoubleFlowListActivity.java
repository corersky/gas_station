package com.linkage.gas_station.gonglve;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

import com.baidu.mobstat.StatService;
import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.model.DoubleFlowModel;
import com.linkage.gas_station.more.Double_flow_list_Adapter;
import com.linkage.gas_station.myview.GonglveListView;
import com.linkage.gas_station.myview.GonglveListView.OnRefreshListener;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.StrategyManager;

public class DoubleFlowListActivity extends BaseActivity {
	
	ArrayList<DoubleFlowModel> doubleFlowModel_list=null;
	TextView title_name=null;
	GonglveListView flowList=null;
	ImageView title_back=null;
	
	Double_flow_list_Adapter adapter=null;
	
	//加载标志位
	boolean isLoad=false;
	//起始item位置
	int firstItem=0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_ordertrack);
		
		((GasStationApplication) getApplication()).tempActivity.add(DoubleFlowListActivity.this);
		
		doubleFlowModel_list=new ArrayList<DoubleFlowModel>();
		
		init();
	}
	
	public void init() {
		title_back=(ImageView) findViewById(R.id.title_back);
		title_back.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
		title_back.setVisibility(View.VISIBLE);
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText("参与明细");
		flowList=(GonglveListView) findViewById(R.id.order_list);
		flowList.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void loadData() {
				// TODO Auto-generated method stub
				if(!isLoad) {
					getDoubleFlowListResult(getIntent().getExtras().getLong("activityId"));
				}
				else {
					showCustomToast("正在加载中，请稍后");
				}				
			}
			
			@Override
			public int getFirstItem() {
				// TODO Auto-generated method stub
				return firstItem;
			}
		});
		flowList.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				firstItem=firstVisibleItem;
			}
		} );
		
		adapter=new Double_flow_list_Adapter(doubleFlowModel_list, DoubleFlowListActivity.this);
		flowList.setAdapter(adapter);
		
		flowList.setStart();
		
		getDoubleFlowListResult(getIntent().getExtras().getLong("activityId"));
	}
	
	public void getDoubleFlowListResult(final Long activityId) {
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if(msg.what==1) {
					doubleFlowModel_list.clear();
					Map[] map=(Map[]) msg.obj;
					for(int i=0;i<map.length;i++) {
						DoubleFlowModel model=new DoubleFlowModel();
						model.setChange_flow(map[i].get("change_flow").toString());
						model.setGenerate_time(map[i].get("generate_time").toString());
						model.setResidue_flow(map[i].get("residue_flow").toString());
						model.setTitle(map[i].get("title").toString());
						doubleFlowModel_list.add(model);
					}
					adapter.notifyDataSetChanged();
				}
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");
				}
				else {
					showCustomToast(getResources().getString(R.string.timeout_exp));
				}
				flowList.loadComp();
				flowList.setRefresh_time();
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(DoubleFlowListActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(DoubleFlowListActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(DoubleFlowListActivity.this);
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(DoubleFlowListActivity.this).create(StrategyManager.class, ((GasStationApplication) getApplicationContext()).AreaUrl+"/hessian/strategyManager", getClassLoader());
						Map[] map=strategyManager.getDoubleFlowList2(Long.parseLong(list.get(0)), list.get(1), activityId);
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
		((GasStationApplication) getApplication()).tempActivity.remove(DoubleFlowListActivity.this);
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
