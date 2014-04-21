package com.linkage.gas_station.gonglve;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.myview.GonglveListView;
import com.linkage.gas_station.myview.GonglveListView.OnRefreshListener;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.StrategyManager;

public class MovieOrderActivity extends BaseActivity {
	
	TextView title_name=null;
	GonglveListView order_list=null;
	ImageView title_back=null;
	
	SimpleAdapter adapter=null;
	ArrayList<HashMap<String, Object>> list=null;
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
		
		((GasStationApplication) getApplication()).tempActivity.add(MovieOrderActivity.this);
		
		list=new ArrayList<HashMap<String, Object>>();
		
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
		title_name.setText("购买明细");
		
		adapter=new SimpleAdapter(MovieOrderActivity.this, list, R.layout.adapter_movieorder, new String[]{"yxt_filmname", "yxt_cinemaname", "showtime"}, new int[]{R.id.movie_order_name, R.id.movie_order_cinemaname, R.id.movie_order_time});
		order_list=(GonglveListView) findViewById(R.id.order_list);
		order_list.setAdapter(adapter);
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
		order_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(MovieOrderActivity.this, MovieOrderDetailActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("ticketId", list.get(position-1).get("id").toString());
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		order_list.setStart();
		getOrderList();
	}
	
	public void getOrderList() {
		isLoad=true;
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				isLoad=false;
				order_list.loadComp();
				order_list.setRefresh_time();
				if(msg.what==1) {
					Object[] map_array=(Object[]) msg.obj;		
					list.clear();
					if(map_array.length==0) {
						showCustomToast("暂无订票记录信息");
					}
					else {
						for(int i=0;i<map_array.length;i++) {
							Map map=(Map) map_array[i];
							HashMap<String, Object> map_=new HashMap<String, Object>();
							map_.put("yxt_filmname", map.get("yxt_filmname").toString());
							map_.put("yxt_cinemaname", map.get("yxt_cinemaname").toString());
							map_.put("showtime", map.get("showtime").toString());
							map_.put("id", map.get("id").toString());
							list.add(map_);
						}
						adapter.notifyDataSetChanged();
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
				LinkedList<String> wholeUrl=Util.getWholeUrl(MovieOrderActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(MovieOrderActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(MovieOrderActivity.this);
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(MovieOrderActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map[] map=strategyManager.orderList(Long.parseLong(list.get(0)), list.get(1));
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
		((GasStationApplication) getApplication()).tempActivity.remove(MovieOrderActivity.this);
	}

}
