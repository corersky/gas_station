package com.linkage.gas_station.oil_treasure;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.StrategyManager;

public class TreasureDetailActivity extends BaseActivity {
	
	TextView title_name=null;
	ImageView title_back=null;
	ImageView title_refresh=null;
	ProgressBar title_refresh_progress=null;
	
	ListView treasure_detail_list=null;
	SimpleAdapter adapter=null;
	ArrayList<HashMap<String, Object>> lists=null;
	ArrayList<HashMap<String, Object>> lists_add=null;
	ArrayList<HashMap<String, Object>> lists_min=null;
	TextView treasure_detail_headview_add=null;
	TextView treasure_detail_headview_min=null;
	TextView treasure_detail_headview_total=null;
	TextView treasure_detail_headview_qq=null;
	TextView treasure_detail_headview_weibo=null;
	TextView treasure_detail_headview_frombuy=null;
	TextView treasure_detail_headview_fromsend=null;
	TextView treasure_detail_headview_weixin=null;
	TextView treasure_detail_headview_yixin=null;
	LinearLayout treasure_detail_headview_use_layout=null;
	LinearLayout treasure_detail_headview_get_layout=null;
	TextView treasure_detail_headview_use_total=null;
	TextView treasure_detail_headview_use_flow=null;
	TextView treasure_detail_headview_bill=null;
	
	boolean pos1_refresh=false;
	boolean pos2_refresh=false;
	boolean pos3_refresh=false;
	int currentChoice=1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_treasuredetail);
		
		init();
		
		queryCoinSummary(1);
		queryCoinSummary(2);
		queryCoinDetail(1);
		queryCoinDetail(2);
	}
	
	private void init() {
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText("收支明细");
		title_back=(ImageView) findViewById(R.id.title_back);
		title_back.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
		title_back.setVisibility(View.VISIBLE);
		title_refresh=(ImageView) findViewById(R.id.title_refresh);
		title_refresh.setVisibility(View.VISIBLE);
		title_refresh.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(!pos1_refresh&&!pos2_refresh&&!pos3_refresh) {
					if(currentChoice==1) {
						queryCoinSummary(1);
						queryCoinDetail(1);
					}
					if(currentChoice==2) {
						queryCoinSummary(2);
						queryCoinDetail(2);
					}
				}
				else {
					showCustomToast("正在加载中，请稍后");
				}
			}});
		title_refresh_progress=(ProgressBar) findViewById(R.id.title_refresh_progress);
		
		lists=new ArrayList<HashMap<String, Object>>();
		lists_add=new ArrayList<HashMap<String, Object>>();
		lists_min=new ArrayList<HashMap<String, Object>>();
		adapter=new SimpleAdapter(TreasureDetailActivity.this, lists, R.layout.adapter_treasure_detail_list, new String[]{"time", "from", "num"}, new int[]{R.id.treasure_detail_adapter_time, R.id.treasure_detail_adapter_from, R.id.treasure_detail_adapter_num});
		treasure_detail_list=(ListView) findViewById(R.id.treasure_detail_list);
		treasure_detail_list.addHeaderView(loadHeadView());
		treasure_detail_list.setAdapter(adapter);
	}
	
	private View loadHeadView() {
		View view=LayoutInflater.from(TreasureDetailActivity.this).inflate(R.layout.activity_treasuredetail_headview, null);
		treasure_detail_headview_add=(TextView) view.findViewById(R.id.treasure_detail_headview_add);
		treasure_detail_headview_add.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				treasure_detail_headview_add.setBackgroundColor(Color.parseColor("#f5a61a"));
				treasure_detail_headview_min.setBackgroundColor(Color.TRANSPARENT);
				treasure_detail_headview_get_layout.setVisibility(View.VISIBLE);
				treasure_detail_headview_use_layout.setVisibility(View.GONE);
				currentChoice=1;
				lists.clear();
				lists.addAll(lists_add);
				adapter.notifyDataSetChanged();
			}});
		treasure_detail_headview_min=(TextView) view.findViewById(R.id.treasure_detail_headview_min);
		treasure_detail_headview_min.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				treasure_detail_headview_min.setBackgroundColor(Color.parseColor("#f5a61a"));
				treasure_detail_headview_add.setBackgroundColor(Color.TRANSPARENT);
				treasure_detail_headview_get_layout.setVisibility(View.GONE);
				treasure_detail_headview_use_layout.setVisibility(View.VISIBLE);
				currentChoice=2;
				lists.clear();
				lists.addAll(lists_min);
				adapter.notifyDataSetChanged();
			}});
		treasure_detail_headview_total=(TextView) view.findViewById(R.id.treasure_detail_headview_total);
		treasure_detail_headview_qq=(TextView) view.findViewById(R.id.treasure_detail_headview_qq);
		treasure_detail_headview_weibo=(TextView) view.findViewById(R.id.treasure_detail_headview_weibo);
		treasure_detail_headview_frombuy=(TextView) view.findViewById(R.id.treasure_detail_headview_frombuy);
		treasure_detail_headview_fromsend=(TextView) view.findViewById(R.id.treasure_detail_headview_fromsend);
		treasure_detail_headview_weixin=(TextView) view.findViewById(R.id.treasure_detail_headview_weixin);
		treasure_detail_headview_yixin=(TextView) view.findViewById(R.id.treasure_detail_headview_yixin);
		treasure_detail_headview_use_layout=(LinearLayout) view.findViewById(R.id.treasure_detail_headview_use_layout);
		treasure_detail_headview_get_layout=(LinearLayout) view.findViewById(R.id.treasure_detail_headview_get_layout);
		treasure_detail_headview_use_total=(TextView) view.findViewById(R.id.treasure_detail_headview_use_total);
		treasure_detail_headview_use_flow=(TextView) view.findViewById(R.id.treasure_detail_headview_use_flow);
		treasure_detail_headview_bill=(TextView) view.findViewById(R.id.treasure_detail_headview_bill);
		return view;
	}
	
	/**
	 * 查询用户当前流量币
	 */
	public void queryCoinDetail(final int type) {
		
		pos3_refresh=true;
		title_refresh.setVisibility(View.INVISIBLE);
		title_refresh_progress.setVisibility(View.VISIBLE);
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if(msg.what==1) {
					if(type==1) {
						lists_add.clear();
					}
					else if(type==2) {
						lists_min.clear();
					}					
					lists.clear();
					Object[] obj_maps=(Object[]) msg.obj;
					for(int i=0;i<obj_maps.length;i++) {
						Map map=(Map) obj_maps[i];
						HashMap<String, Object> maps=new HashMap<String, Object>();
						maps.put("time", map.get("generate_time").toString());
						maps.put("from", map.get("exchange_type").toString());
						maps.put("num", map.get("change_flowcoin").toString());
						if(type==1) {
							lists_add.add(maps);
						}
						else if(type==2) {
							lists_min.add(maps);
						}						
					}
					if(currentChoice==1) {
						lists.addAll(lists_add);
						adapter.notifyDataSetChanged();
					}
					else if(currentChoice==2) {
						lists.addAll(lists_min);
						adapter.notifyDataSetChanged();
					}
				}
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");
				}
				else {
					showCustomToast(getResources().getString(R.string.timeout_exp));
				}
				pos3_refresh=false;
				if(!pos1_refresh&&!pos2_refresh&&!pos3_refresh) {
					title_refresh.setVisibility(View.VISIBLE);
					title_refresh_progress.setVisibility(View.INVISIBLE);
				}
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(TreasureDetailActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(TreasureDetailActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(TreasureDetailActivity.this);					
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(TreasureDetailActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map[] map=strategyManager.queryCoinDetail(Long.parseLong(list.get(0)), list.get(1), type);
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
				handler.sendMessage(m);
			}}).start();
	}
	
	/**
	 * 流量币汇总
	 */
	public void queryCoinSummary(final int type) {
		
		if(type==1) {
			pos1_refresh=true;
		}
		else if(type==2) {
			pos2_refresh=true;
		}
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if(msg.what==1) {
					Object[] obj_maps=(Object[]) msg.obj;
					for(int i=0;i<obj_maps.length;i++) {
						Map map=(Map) obj_maps[i];
						if(type==1) {
							//[{coins=5, exchange_type=1}, {coins=5, exchange_type=0}]
							if(map.get("exchange_type").toString().equals("0")) {
								treasure_detail_headview_total.setText(Util.convertNull(map.get("coins").toString()));
							}
							else if(map.get("exchange_type").toString().equals("1")) {
								treasure_detail_headview_frombuy.setText(Util.convertNull(map.get("coins").toString()));
							}
							else if(map.get("exchange_type").toString().equals("2")) {
								treasure_detail_headview_fromsend.setText(Util.convertNull(map.get("coins").toString()));
							}
							else if(map.get("exchange_type").toString().equals("3")) {
								treasure_detail_headview_weixin.setText(Util.convertNull(map.get("coins").toString()));
							}
							else if(map.get("exchange_type").toString().equals("4")) {
								treasure_detail_headview_weibo.setText(Util.convertNull(map.get("coins").toString()));
							}
							else if(map.get("exchange_type").toString().equals("5")) {
								treasure_detail_headview_yixin.setText(Util.convertNull(map.get("coins").toString()));
							}
							else if(map.get("exchange_type").toString().equals("6")) {
								treasure_detail_headview_qq.setText(Util.convertNull(map.get("coins").toString()));
							}							
						}
						else if(type==2) {
							if(map.get("exchange_type").toString().equals("0")) {
								treasure_detail_headview_use_total.setText(Util.convertNull(map.get("coins").toString()));
							}
							else if(map.get("exchange_type").toString().equals("-2")) {
								treasure_detail_headview_use_flow.setText(Util.convertNull(map.get("coins").toString()));
							}
							else if(map.get("exchange_type").toString().equals("-1")) {
								treasure_detail_headview_bill.setText(Util.convertNull(map.get("coins").toString()));
							}
						}
					}
					adapter.notifyDataSetChanged();
				}
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");
				}
				else {
					showCustomToast(getResources().getString(R.string.timeout_exp));
				}
				if(type==1) {
					pos1_refresh=false;
				}
				else if(type==2) {
					pos2_refresh=false;
				}
				if(!pos1_refresh&&!pos2_refresh&&!pos3_refresh) {
					title_refresh.setVisibility(View.VISIBLE);
					title_refresh_progress.setVisibility(View.INVISIBLE);
				}
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(TreasureDetailActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(TreasureDetailActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(TreasureDetailActivity.this);					
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(TreasureDetailActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map[] map=strategyManager.queryCoinSummary(Long.parseLong(list.get(0)), list.get(1), type);
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
				handler.sendMessage(m);
			}}).start();
	}
}
