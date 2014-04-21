package com.linkage.gas_station.jiayou;

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
import com.linkage.gas_station.model.OrderModel;
import com.linkage.gas_station.myview.GonglveListView;
import com.linkage.gas_station.myview.GonglveListView.OnRefreshListener;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.OilManager;

public class OrderTrackActivity extends BaseActivity {
	
	TextView title_name=null;
	GonglveListView order_list=null;
	ImageView title_back=null;
	
	ArrayList<OrderModel> orderModel_list=null;
	AdapterOrderTrack adapter=null;
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
		
		((GasStationApplication) getApplication()).tempActivity.add(OrderTrackActivity.this);
		orderModel_list=new ArrayList<OrderModel>();
		
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
		title_name.setText(getResources().getString(R.string.order_title));
		order_list=(GonglveListView) findViewById(R.id.order_list);
		order_list.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void loadData() {
				// TODO Auto-generated method stub
				if(!isLoad) {
					getOrderList();
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
		order_list.setOnScrollListener(new OnScrollListener() {
			
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
		
		adapter=new AdapterOrderTrack(OrderTrackActivity.this, orderModel_list);
		order_list.setAdapter(adapter);
		
		order_list.setStart();
		getOrderList();
	}
	
	public void getOrderList() {
		isLoad=true;
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if(msg.obj!=null) {
					orderModel_list.clear();
					Map[] map=(Map[]) msg.obj;
					for(int i=0;i<map.length;i++) {
						OrderModel model=new OrderModel();
						if(map[i].get("order_id")!=null) {
							model.setOrder_id(map[i].get("order_id").toString());
						}
						model.setOffer_name(map[i].get("offer_name").toString());
						model.setOrder_time(map[i].get("order_time").toString());
						model.setOrder_state(map[i].get("order_state").toString());
						if(map[i].get("order_state").toString().equals("3")) {
							model.setComments(map[i].get("comments").toString());
						}						
						if(map[i].get("offer_image_name")!=null) {
							model.setOffer_image_name(map[i].get("offer_image_name").toString());
						}			
						model.setOffer_type_id(Integer.parseInt(map[i].get("offer_type_id").toString()));
						model.setPay_type("["+map[i].get("pay_type").toString()+"]");
						model.setOffer_type_name(map[i].get("offer_type_name").toString());
						orderModel_list.add(model);
					}
					adapter.notifyDataSetChanged();
				}
				else {
					showCustomToast(getResources().getString(R.string.timeout_exp));
				}
				isLoad=false;
				order_list.loadComp();
				order_list.setRefresh_time();
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(OrderTrackActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(OrderTrackActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(OrderTrackActivity.this);
						
						OilManager oilManager=GetWebDate.getHessionFactiory(OrderTrackActivity.this).create(OilManager.class, currentUsedUrl+"/hessian/oilManager", getClassLoader());
						Map[] map=oilManager.getOrderList(Long.parseLong(list.get(0)), list.get(1));
						m.obj=map;
						flag=false;
						((GasStationApplication) getApplicationContext()).AreaUrl=currentUsedUrl;
					} catch(Error e) {
						flag=false;
						m.obj=null;
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
						m.obj=null;
					}
				}				
				handler.sendMessage(m);
			}}).start();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		((GasStationApplication) getApplication()).tempActivity.remove(OrderTrackActivity.this);
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
