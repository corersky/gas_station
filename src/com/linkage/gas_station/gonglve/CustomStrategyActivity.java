package com.linkage.gas_station.gonglve;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.jiayou.JiayouDetaiActivity;
import com.linkage.gas_station.jpush.JPushReceiver;
import com.linkage.gas_station.main.MainActivity;
import com.linkage.gas_station.model.GonglveModel1;
import com.linkage.gas_station.myview.GonglveListView;
import com.linkage.gas_station.myview.GonglveListView.OnRefreshListener;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.StrategyManager;
import com.linkage.gas_station.gonglve.CustomStrategyActivity;
import com.linkage.gas_station.life.LifeMainActivity;

public class CustomStrategyActivity extends BaseActivity {
	
	Adapter_Gonglve_Title_1 adapter1=null;
	ArrayList<GonglveModel1> modelList1=null;
	
	ImageView title_back=null;
	TextView title_name=null;
	GonglveListView strategy_list=null;
	//攻略加载标志位
	boolean isLoad1=false;
	int firstItem1=0;
	long initPhoneNum=-1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_strategy);
		
		modelList1=new ArrayList<GonglveModel1>();
		((GasStationApplication) getApplication()).tempActivity.add(CustomStrategyActivity.this);
		
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
		title_name.setText("个性化攻略");
		strategy_list=(GonglveListView) findViewById(R.id.strategy_list);
		strategy_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if(modelList1.size()!=0&&position>0) {
					switch(Integer.parseInt(modelList1.get(position-1).getStrategy_type())) {
						//1:普通攻略;
					case 1:
						break;
						//2:套餐推荐(关联套餐，点击直接订购)
					case 2:
						((GasStationApplication) getApplicationContext()).jumpJiayouFrom=3;
						Intent intent=new Intent();
						Bundle bundle=new Bundle();
						intent.setClass(CustomStrategyActivity.this, JiayouDetaiActivity.class);
						bundle.putString("offerId", modelList1.get(position-1).getOffer_id());
						bundle.putString("offer_name", modelList1.get(position-1).getOffer_name());
						bundle.putString("type", "simple_station");
						bundle.putString("offer_description", getResources().getString(R.string.jiayou_desp1));
						intent.putExtras(bundle);
						startActivity(intent);
						break;
						//3:加油站活动(点击跳转到活动页签里面相应的活动界面)
					case 3:
//						int posNum=-1;
//						if(modelList2.size()>0) {
//							for(int i=0;i<modelList2.size();i++) {
//								if(modelList2.get(i).getActivity_id().equals(modelList1.get(position-1).getActivity_id())) {
//									posNum=i;
//								}
//							}
//						}
//						if(posNum==-1) {
//							showCustomToast("未找到指定的加油站活动");
//							gonglve_tuan_layout.setVisibility(View.VISIBLE);
//							gonglve_tuan.setVisibility(View.GONE);
//							gonglve_tip_layout.setVisibility(View.VISIBLE);
//						}
//						else {
//							gonglve_tuan.snapToScreen(posNum);
//							gonglve_tuan_layout.setVisibility(View.VISIBLE);
//							gonglve_tuan.setVisibility(View.VISIBLE);
//							gonglve_tip_layout.setVisibility(View.GONE);
//							gonglve_list.setVisibility(View.GONE);
//							gonglve_app.setVisibility(View.GONE);
//							gonglve_title_1.setImageResource(R.drawable.gl_left);
//							gonglve_title_2.setImageResource(R.drawable.gl_middle_on);
//							gonglve_title_3.setImageResource(R.drawable.gl_right);
//							currentTab=2;
//						}
						break;
						//4:外部活动(点击跳转到活动详情)
					case 4:
						Intent intent4=new Intent(CustomStrategyActivity.this, Gonglve_Title_2_DetailActivity.class);
						Bundle bundle4=new Bundle();
						bundle4.putInt("activityId", Integer.parseInt(modelList1.get(position-1).getActivity_id()));
						bundle4.putString("activity_image_name", modelList1.get(position-1).getOffer_name());
						intent4.putExtras(bundle4);
						startActivity(intent4);
						break;
						//5:外部链接(点击打开外部一个链接);
					case 5:
						Intent it = new Intent(Intent.ACTION_VIEW, Uri.parse(modelList1.get(position-1).getActivity_url()));  
				        it.setClassName("com.android.browser", "com.android.browser.BrowserActivity");  
				        startActivity(it);  
						break;
					case 7:
						finish();
						MainActivity.getInstance().jumpToDx();
						break;
					case 8:
						Intent intent8=new Intent(CustomStrategyActivity.this, LifeMainActivity.class);
						startActivity(intent8);
						break;
					}
				}			
			}
		});
		strategy_list.setOnRefreshListener(new OnRefreshListener() {
			
			@Override
			public void loadData() {
				// TODO Auto-generated method stub
				if(!isLoad1) {
					loadCustomGonglve();
				}
				else {
					showCustomToast("正在加载中，请稍后");
				}				
			}
			
			@Override
			public int getFirstItem() {
				// TODO Auto-generated method stub
				return firstItem1;
			}
		});
		strategy_list.setOnScrollListener(new OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				// TODO Auto-generated method stub

			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				// TODO Auto-generated method stub
				firstItem1=firstVisibleItem;
			}
		} );
		adapter1=new Adapter_Gonglve_Title_1(CustomStrategyActivity.this, modelList1);
		strategy_list.setAdapter(adapter1);
		strategy_list.setStart();
		loadCustomGonglve();
	}
	

	/**
	 * 加载个性化攻略	
	 * @param type
	 */
	public void loadCustomGonglve() {
		
		isLoad1=true;
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				
				if(msg.what==1) {
					ArrayList<GonglveModel1> temp=msg.getData().getParcelableArrayList("adapter1");
					modelList1.clear();
					modelList1.addAll(temp);	
					initPhoneNum=Long.parseLong(Util.getUserInfo(CustomStrategyActivity.this).get(0));
				}
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");
				}
				else {
					showCustomToast(getResources().getString(R.string.timeout_exp));
				}
							
				isLoad1=false;
				strategy_list.loadComp();
				strategy_list.setRefresh_time();
				adapter1.notifyDataSetChanged();
				MainActivity.getInstance().able_change();
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(CustomStrategyActivity.this);
				Message m=new Message();
				//流量攻略列表
				ArrayList<GonglveModel1> temp1=new ArrayList<GonglveModel1>();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(CustomStrategyActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(CustomStrategyActivity.this);
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(CustomStrategyActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map[] map=strategyManager.getStrategyList(Long.parseLong(list.get(0)), list.get(1));
						for(int i=0;i<map.length;i++) {
							GonglveModel1 model=new GonglveModel1();
							model.setStrategy_type(map[i].get("strategy_type").toString());
							model.setStrategy_description(map[i].get("strategy_description").toString());
							model.setGenerate_time(map[i].get("generate_time").toString());
							if(map[i].get("offer_id")!=null) {
								model.setOffer_id(Util.convertNull(map[i].get("offer_id").toString()));
							}
							if(map[i].get("offer_name")!=null) {
								model.setOffer_name(Util.convertNull(map[i].get("offer_name").toString()));
							}
							if(map[i].get("activity_id")!=null) {
								model.setActivity_id(Util.convertNull(map[i].get("activity_id").toString()));
							}
							if(map[i].get("activity_url")!=null) {
								model.setActivity_url(Util.convertNull(map[i].get("activity_url").toString()));
							}						
							temp1.add(model);
						}
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
				Bundle bundle=new Bundle();
				bundle.putParcelableArrayList("adapter1", temp1);
				m.setData(bundle);
				handler.sendMessage(m);
			}}).start();
	}	
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		((GasStationApplication) getApplicationContext()).isNewGonglve=false;
		IntentFilter filter=new IntentFilter();
		filter.addAction(JPushReceiver.refreshGonglve);
		registerReceiver(receiver, filter);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(receiver);
	}
	
	BroadcastReceiver receiver=new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			System.out.println(intent.getAction());
			((GasStationApplication) getApplicationContext()).isNewGonglve=false;
			strategy_list.setStart();
			loadCustomGonglve();
		}};
		
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		((GasStationApplication) getApplication()).tempActivity.remove(CustomStrategyActivity.this);
	}
}
