package com.linkage.gas_station.main;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

import com.baidu.mobstat.StatService;
import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.gonglve.CustomStrategyActivity;
import com.linkage.gas_station.jpush.JPushReceiver;
import com.linkage.gas_station.login.LoginOutActivity;
import com.linkage.gas_station.main.Fragment1.OnJumpListener;
import com.linkage.gas_station.model.OutputInfoModel;
import com.linkage.gas_station.util.GetConnData;
import com.linkage.gas_station.util.OutputInfoParse;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.MonitorManager;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class HomeActivity extends FragmentActivity {
	
	ViewPager home_pager=null;
	HomeAdapter adapter=null;
	TextView home_refresh=null;
	ImageView home_page0=null;
	ImageView home_page1=null;
	LinearLayout guid_layout = null;
	ImageView guid_left = null;
	TextView guid_text = null;
	ImageView title_back=null;
	ImageView title_new_strategy=null;
	TextView title_name=null;
	ImageView title_refresh=null;
	ProgressBar title_refresh_progress=null;
	LinearLayout home_with_meal=null;
	RelativeLayout home_without_meal=null;
	TextView without_meal_num=null;
	ImageView without_meal_wantjiayou=null;
	
	ArrayList<Fragment> fragment_list=null;
	//数据初次加载判读是否成功
	boolean isFirstLoadComp=false;
	//判断是否正在加载中
	boolean isLoad=true;
	//初始号码
	long initPhoneNum=-1;
	//最后一次刷新时间
	long lastTime=0;
	static HomeActivity instance=null;
	//上一次home_refresh显示的文字
	String last_home_refresh_text="";
	//判断当前activity是否已经销毁 退出的时候由于fragment.notifyDataSetChanged会抛出异常，所以需要判断是否已经退出
	boolean isActivityDestory=false;
	
	GetConnData cData=null;
	
	public static HomeActivity getInstance() {
		return instance;
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_home);
		
		instance=HomeActivity.this;
		
		initPhoneNum=Long.parseLong(Util.getUserInfo(HomeActivity.this).get(0));
		cData=new GetConnData(HomeActivity.this);
		fragment_list=new ArrayList<Fragment>();
		
		init();
		MainActivity.getInstance().not_able_change();
	}
	
	public void init() {
		title_back=(ImageView) findViewById(R.id.title_back);
		title_back.setImageResource(R.drawable.mail_box);
		title_new_strategy=(ImageView) findViewById(R.id.title_new_strategy);
		title_back.setVisibility(View.VISIBLE);
		title_back.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(HomeActivity.this, CustomStrategyActivity.class);
				startActivity(intent);
			}});
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText(getResources().getString(R.string.home));
		title_refresh=(ImageView) findViewById(R.id.title_refresh);
		title_refresh.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isLoad) {
					BaseActivity.showCustomToastWithContext(getResources().getString(R.string.now_loading), HomeActivity.this);
					home_refresh.setText(last_home_refresh_text);
				}
				else {
					isLoad=true;
					MainActivity.getInstance().not_able_change();
					if(isFirstLoadComp) {
						getData(2);
					}
					else {
						getData(1);
					}
				}
				
			}});
		title_refresh_progress=(ProgressBar) findViewById(R.id.title_refresh_progress);
		title_back=(ImageView) findViewById(R.id.title_back);
		home_refresh=(TextView) findViewById(R.id.home_refresh);
		home_page0=(ImageView) findViewById(R.id.home_page0);
		home_page1=(ImageView) findViewById(R.id.home_page1);
		home_pager=(ViewPager) findViewById(R.id.home_pager);
		guid_layout = (LinearLayout) findViewById(R.id.guid_layout);
		guid_left = (ImageView) findViewById(R.id.guid_left);
		guid_text = (TextView) findViewById(R.id.guid_text);
		home_pager.setOffscreenPageLimit(2);
		if(cData.getMonitor()!=null) {
			if(!isActivityDestory) {
				home_refresh.setText("截至"+cData.getMonitor().getData_time()+"的收费流量");
				last_home_refresh_text="截至"+cData.getMonitor().getData_time()+"的收费流量";
				addFragment();
			}
			home_page0.setVisibility(View.VISIBLE);
			home_page1.setVisibility(View.VISIBLE);
			guid_layout.setVisibility(View.VISIBLE);
			isFirstLoadComp=true;
		}
		adapter=new HomeAdapter(getSupportFragmentManager(), fragment_list);
		home_pager.setAdapter(adapter);
		if(cData.getMonitor()!=null) {
			getData(2);
		}
		else {
			getData(1);
		}
		home_pager.setOnPageChangeListener(new OnPageChangeListener() {
			
			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				switch(arg0) {
				case 0:
					home_page0.setImageResource(R.drawable.intro_select);
					home_page1.setImageResource(R.drawable.intro_noselect);
					title_name.setText(getResources().getString(R.string.home));
					guid_left.setImageResource(R.drawable.arrow_shouye_left);
					guid_text.setText("向左滑，更多使用详情");
					break;
				case 1:
					home_page0.setImageResource(R.drawable.intro_noselect);
					home_page1.setImageResource(R.drawable.intro_select);
					title_name.setText(getResources().getString(R.string.home_detail));
					guid_left.setImageResource(R.drawable.arrow_shouye_right);
					guid_text.setText("向右滑，返回流量监控");
					break;
				}
			}
			
			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub
				
			}
		});
		home_with_meal=(LinearLayout) findViewById(R.id.home_with_meal);
		home_without_meal=(RelativeLayout) findViewById(R.id.home_without_meal);
		without_meal_num=(TextView) findViewById(R.id.without_meal_num);
		without_meal_wantjiayou=(ImageView) findViewById(R.id.without_meal_wantjiayou);
		without_meal_wantjiayou.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				MainActivity.getInstance().jumpToJiayou(0, 1);
			}});
		if(cData.getMonitor()!=null&&cData.getMonitor().getModel_list().size()==1&&cData.getMonitor().getModel_list().get(0).getFlow_type_id()==9) {
			home_without_meal.setVisibility(View.VISIBLE);
			String tempText=cData.getMonitor().getModel_list().get(0).getFlow_type_used();
			without_meal_num.setText(tempText.substring(0, tempText.length()-2));
			home_with_meal.setVisibility(View.GONE);
		}
		else {
			home_with_meal.setVisibility(View.VISIBLE);
			home_without_meal.setVisibility(View.GONE);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK) {
			MainActivity.getInstance().loginout();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	/**
	 * 
	 * @param type 1：没有缓存数据时的加载  2：刷新加载
	 */
	public void getData(final int type) {
		
		title_refresh_progress.setVisibility(View.VISIBLE);
		title_refresh.setVisibility(View.INVISIBLE);
		home_refresh.setText("正在努力加载，请稍后...");
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if(msg.obj!=null) {
					Map map=(Map) msg.obj;

					if(type==1) {
						if(map.get("xml")!=null) {
							String result=map.get("xml").toString();
							System.out.println(result);
							if(!result.equals("-1")) {
								OutputInfoParse o=new OutputInfoParse();
								OutputInfoModel model=o.getOutputInfoModel(result, HomeActivity.this);
								if(model!=null) {
									cData.saveMonitor(model);
									if(!isActivityDestory) {
										try {
											addFragment();
											adapter.notifyDataSetChanged();
										} catch(Exception e) {
											System.out.println("绘制fragment时出现问题");
										}									
										home_refresh.setText("截至"+cData.getMonitor().getData_time()+"的收费流量");
										last_home_refresh_text="截至"+cData.getMonitor().getData_time()+"的收费流量";
									}								
									home_page0.setVisibility(View.VISIBLE);
									home_page1.setVisibility(View.VISIBLE);
									guid_layout.setVisibility(View.VISIBLE);
									isFirstLoadComp=true;
									if(cData.getMonitor()!=null&&cData.getMonitor().getModel_list().size()==1&&cData.getMonitor().getModel_list().get(0).getFlow_type_id()==9) {
										home_without_meal.setVisibility(View.VISIBLE);
										String tempText=cData.getMonitor().getModel_list().get(0).getFlow_type_used();
										without_meal_num.setText(tempText.substring(0, tempText.length()-2));
										home_with_meal.setVisibility(View.GONE);
									}
									else {
										home_with_meal.setVisibility(View.VISIBLE);
										home_without_meal.setVisibility(View.GONE);
									}
								}
								else {
									BaseActivity.showCustomToastWithContext("请求数据失败", HomeActivity.this);
									home_refresh.setText(last_home_refresh_text);
								}
							}
							else {
								BaseActivity.showCustomToastWithContext("请求数据失败", HomeActivity.this);
								home_refresh.setText(last_home_refresh_text);
							}
						}
						else {
							BaseActivity.showCustomToastWithContext("请求数据失败", HomeActivity.this);
							home_refresh.setText(last_home_refresh_text);
						}
					}
					else {
						if(map.get("xml")!=null) {
							String result=map.get("xml").toString();
							System.out.println(result);
							if(!result.equals("-1")) {
								OutputInfoParse o=new OutputInfoParse();
								OutputInfoModel model_new=o.getOutputInfoModel(result, HomeActivity.this);
								if(model_new!=null) {
									if(initPhoneNum!=-1&&initPhoneNum!=Long.parseLong(Util.getUserInfo(HomeActivity.this).get(0))) {
										initPhoneNum=Long.parseLong(Util.getUserInfo(HomeActivity.this).get(0));
										cData.deleteMonitor();
										cData.saveMonitor(model_new);
										if(!isActivityDestory) {
											home_refresh.setText("截至"+cData.getMonitor().getData_time()+"的收费流量");
											last_home_refresh_text="截至"+cData.getMonitor().getData_time()+"的收费流量";
											try {
												addFragment();
												adapter.notifyDataSetChanged();
												jumpViewPager();
											} catch(Exception e) {
												System.out.println("绘制fragment时出现错误");
											}
										}							
									}
									else {
										if(Util.isNewData(model_new, HomeActivity.this)) {										
											if(((GasStationApplication) getApplicationContext()).isJumpToMonitor) {
												System.out.println("正在检查变动");
												GetConnData cData=new GetConnData(HomeActivity.this);
												OutputInfoModel model_old=cData.getMonitor();
												double amount_new=0;
												double amount_old=0;
												for(int i=0;i<model_new.getModel_list().size();i++) {
													amount_new+=Double.parseDouble(model_new.getModel_list().get(i).getFlow_type_amount().substring(0, model_new.getModel_list().get(i).getFlow_type_amount().length()-2));
												}
												for(int i=0;i<model_old.getModel_list().size();i++) {
													amount_old+=Double.parseDouble(model_old.getModel_list().get(i).getFlow_type_amount().substring(0, model_old.getModel_list().get(i).getFlow_type_amount().length()-2));
												}
												System.out.println(amount_new+" "+amount_old);
												if(amount_new!=amount_old) {
													System.out.println("检查变动成功");
													((GasStationApplication) getApplication()).isJumpToMonitor=false;
												}
											}
											cData.deleteMonitor();
											cData.saveMonitor(model_new);
											if(!isActivityDestory) {
												home_refresh.setText("截至"+cData.getMonitor().getData_time()+"的收费流量");
												last_home_refresh_text="截至"+cData.getMonitor().getData_time()+"的收费流量";
												try {
													addFragment();
													adapter.notifyDataSetChanged();
													jumpViewPager();
												} catch(Exception e) {
													System.out.println("绘制fragment时出现错误");
												}
											}
											
										}
									}	
									if(cData.getMonitor()!=null&&cData.getMonitor().getModel_list().size()==1&&cData.getMonitor().getModel_list().get(0).getFlow_type_id()==9) {
										home_without_meal.setVisibility(View.VISIBLE);
										String tempText=cData.getMonitor().getModel_list().get(0).getFlow_type_used();
										without_meal_num.setText(tempText.substring(0, tempText.length()-2));
										home_with_meal.setVisibility(View.GONE);
									}
									else {
										home_with_meal.setVisibility(View.VISIBLE);
										home_without_meal.setVisibility(View.GONE);
									}
								}
								else {
									BaseActivity.showCustomToastWithContext("请求数据失败", HomeActivity.this);
									home_refresh.setText(last_home_refresh_text);
								}
							}
							else {
								BaseActivity.showCustomToastWithContext("请求数据失败", HomeActivity.this);
								home_refresh.setText(last_home_refresh_text);
							}
						}
						else {
							BaseActivity.showCustomToastWithContext("请求数据失败", HomeActivity.this);
							home_refresh.setText(last_home_refresh_text);
						}
					}
					if(map.get("state").toString().equals("2")) {
						BaseActivity.showCustomToastWithContext("后台维护中，数据可能有延迟", HomeActivity.this);
						home_refresh.setText(last_home_refresh_text);
					}
				}
				else {
					BaseActivity.showCustomToastWithContext(getResources().getString(R.string.timeout_exp), HomeActivity.this);
					home_refresh.setText(last_home_refresh_text);
				}
				lastTime=System.currentTimeMillis();
				isLoad=false;
				MainActivity.getInstance().able_change();
				title_refresh_progress.setVisibility(View.INVISIBLE);
				title_refresh.setVisibility(View.VISIBLE);				
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(HomeActivity.this);
				System.out.println("可置换url数量："+wholeUrl.size());
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(HomeActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						System.out.println("使用："+currentUsedUrl);
						ArrayList<String> list=Util.getUserInfo(HomeActivity.this);
						
						MonitorManager monitorManager=GetWebDate.getHessionFactiory(HomeActivity.this).create(MonitorManager.class, currentUsedUrl+"/hessian/monitorManager", getClassLoader());
						Map result=monitorManager.getMonitorData2(Long.parseLong(list.get(0)), list.get(1));
						m.obj=result;
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
	
	//在详情界面点击第一个按钮，滑动至首页
	public void jumpViewPager() {
		home_pager.setCurrentItem(0, true);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		
		IntentFilter filter=new IntentFilter();
		filter.addAction(JPushReceiver.refreshGonglve);
		registerReceiver(receiver, filter);

		home_pager.setCurrentItem(0);
		
		if(((GasStationApplication) getApplication()).isShowLoginOut) {			
			((GasStationApplication) getApplication()).isShowLoginOut=false;
			Intent intent_=new Intent();
	        intent_.setClass(HomeActivity.this, LoginOutActivity.class);
	        intent_.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	        startActivity(intent_);
	        MainActivity.getInstance().not_able_change();
	        return;
		}
		
		//当前加载的号码与初始化后的号码不一致的时候，则认为已经切换号码，重新加载
		if(initPhoneNum!=-1&&initPhoneNum!=Long.parseLong(Util.getUserInfo(HomeActivity.this).get(0))&&!isLoad) {
			isLoad=true;
			MainActivity.getInstance().not_able_change();
			if(isFirstLoadComp) {
				getData(2);
			}
			else {
				getData(1);
			}
			return;
		}
		//一般情况或者切换号码之和仍然用原号码登陆
		else if(initPhoneNum!=-1&&initPhoneNum==Long.parseLong(Util.getUserInfo(HomeActivity.this).get(0))&&!isLoad) {
			MainActivity.getInstance().able_change();
		}
		
		//加油成功跳转首页
		if(((GasStationApplication) getApplicationContext()).isJumpToMonitor&&!isLoad) {
			((GasStationApplication) getApplicationContext()).isJumpToMonitor=false;
			isLoad=true;
			MainActivity.getInstance().not_able_change();
			if(isFirstLoadComp) {
				getData(2);
			}
			else {
				getData(1);
			}
			return;
		}
		
		if(((GasStationApplication) getApplicationContext()).isRefreshMonitor&&!isLoad) {
			((GasStationApplication) getApplicationContext()).isRefreshMonitor=false;
			isLoad=true;
			MainActivity.getInstance().not_able_change();
			if(isFirstLoadComp) {
				getData(2);
			}
			else {
				getData(1);
			}
			return;
		}
		
		if(((GasStationApplication) getApplicationContext()).isNewGonglve) {
			title_new_strategy.setVisibility(View.VISIBLE);
		}
		else {
			title_new_strategy.setVisibility(View.GONE);
		}
		
		//一般15分钟刷新
		if(!((GasStationApplication) getApplicationContext()).isJumpToMonitor&&!isLoad&&System.currentTimeMillis()>lastTime+1000*60*15) {
			isLoad=true;
			MainActivity.getInstance().not_able_change();
			if(isFirstLoadComp) {
				getData(2);
			}
			else {
				getData(1);
			}
			return;
		}
		
		StatService.onResume(this);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		unregisterReceiver(receiver);
		StatService.onPause(this);
	}
	
	BroadcastReceiver receiver=new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			System.out.println(intent.getAction());
			title_new_strategy.setVisibility(View.VISIBLE);
		}};
	
	public void addFragment() {
		if(fragment_list != null){
			FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
			for(Fragment f: fragment_list){
				ft.remove(f);
			}
			ft.commitAllowingStateLoss();
			ft=null;
			getSupportFragmentManager().executePendingTransactions();
		}
		fragment_list.clear();
		Fragment1 f1=new Fragment1();
		f1.setOnJumpListener(new OnJumpListener() {
			
			@Override
			public void jumpToDetail() {
				// TODO Auto-generated method stub
				home_pager.setCurrentItem(1, true);
			}
		});
		fragment_list.add(f1);
		fragment_list.add(new Fragment2());
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		isActivityDestory=true;
	}

}
