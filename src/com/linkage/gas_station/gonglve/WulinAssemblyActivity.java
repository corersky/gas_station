package com.linkage.gas_station.gonglve;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.model.Leader_MonthBegin_Model;
import com.linkage.gas_station.model.Leader_MonthUsual_Model;
import com.linkage.gas_station.model.Sheik_MonthBegin_Model;
import com.linkage.gas_station.model.Sheik_MonthUsual_Model;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.StrategyManager;

public class WulinAssemblyActivity extends BaseActivity {
	
	ImageView title_back=null;
	TextView title_name=null;
	ImageView title_refresh=null;
	ProgressBar title_refresh_progress=null;
	LinearLayout wulinassembly_layout=null;
	
	HashMap<String, Sheik_MonthBegin_Model> sheik_model_begin_map=null;
	ListView sheik_month_begin_list=null;
	Adapter_Sheik_MonthBegin adapter_sheik_month_begin=null;
	
	HashMap<String, Sheik_MonthUsual_Model> sheik_model_usual_map=null;
	ListView sheik_month_usual_list=null;
	Adapter_Sheik_MonthUsual adapter_sheik_month_usual=null;
	
	HashMap<String, Leader_MonthBegin_Model> leader_model_begin_map=null;
	ListView leader_month_begin_list=null;
	Adapter_Leader_MonthBegin adapter_leader_month_begin=null;
	
	HashMap<String, Leader_MonthUsual_Model> leader_model_usual_map=null;
	ListView leader_month_usual_list=null;
	Adapter_Leader_MonthUsual adapter_leader_month_usual=null;
	
	int WulinAssemblyType=-1;
	long unionId=-1;
	long tribeId=-1;
	String residue_flow="";
	String total_flow="";
	//是否正在加载数据
	boolean isLoading=false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_wulinassembly);
		
		sheik_model_begin_map=new HashMap<String, Sheik_MonthBegin_Model>();
		sheik_model_usual_map=new HashMap<String, Sheik_MonthUsual_Model>();
		leader_model_begin_map=new HashMap<String, Leader_MonthBegin_Model>();
		leader_model_usual_map=new HashMap<String, Leader_MonthUsual_Model>();
		try {
			WulinAssemblyType=getIntent().getExtras().getInt("WulinAssemblyType");
			if(WulinAssemblyType==1||WulinAssemblyType==2) {
				unionId=getIntent().getExtras().getLong("unionId");	
			}
			else if(WulinAssemblyType==3||WulinAssemblyType==4) {
				tribeId=getIntent().getExtras().getLong("tribeId");	
			}		
			else if(WulinAssemblyType==5) {
				residue_flow=getIntent().getExtras().getString("residue_flow");	
				total_flow=getIntent().getExtras().getString("total_flow");	
			}
			init();
		} catch(Exception e) {
			showCustomToast("初始化数据失败，请重新进入该界面之后再试");
		}
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
		title_name.setText("武林大会");
		title_refresh=(ImageView) findViewById(R.id.title_refresh);
		title_refresh.setVisibility(View.VISIBLE);
		title_refresh.setOnClickListener(new ImageView.OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isLoading) {
					showCustomToast("正在加载中，请稍后");
				}
				else {
					if(WulinAssemblyType==1) {
						loadData_sheik_monthbegin();
					}
					else if(WulinAssemblyType==2) {
						loadData_sheik_monthusual();
					}
					else if(WulinAssemblyType==3) {
						loadData_leader_monthbegin();
					}
					else if(WulinAssemblyType==4) {
						loadData_leader_monthusual();
					}
				}
			}});
		title_refresh_progress=(ProgressBar) findViewById(R.id.title_refresh_progress);
		
		wulinassembly_layout=(LinearLayout) findViewById(R.id.wulinassembly_layout);
		if(WulinAssemblyType==1) {
			loadData_sheik_monthbegin();
		}
		else if(WulinAssemblyType==2) {
			loadData_sheik_monthusual();
		}
		else if(WulinAssemblyType==3) {
			loadData_leader_monthbegin();
		}
		else if(WulinAssemblyType==4) {
			loadData_leader_monthusual();
		}
		else if(WulinAssemblyType==5) {
			title_refresh.setVisibility(View.INVISIBLE);
			loadView_member();
		}
	}
	
	/**
	 * 酋长月初初始化数据
	 */
	public void loadData_sheik_monthbegin() {
		title_refresh.setVisibility(View.INVISIBLE);
		title_refresh_progress.setVisibility(View.VISIBLE);
		isLoading=true;
		WulinAssemblyType=1;
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				isLoading=false;
				title_refresh.setVisibility(View.VISIBLE);
				title_refresh_progress.setVisibility(View.INVISIBLE);
				if(msg.what==1) {
					Map map=(Map)msg.obj;
					if(map.get("result").toString().equals("0")) {
						showCustomToast(map.get("comments").toString());
					}
					else {
						sheik_model_begin_map.clear();
						Object[] tribes_arrays=(Object[]) map.get("tribes");
						System.out.println("数量为："+tribes_arrays.length);
						for(int i=0;i<tribes_arrays.length;i++) {
							Map temp_map=(Map)tribes_arrays[i];
							Sheik_MonthBegin_Model model=new Sheik_MonthBegin_Model();
							model.setCheck(true);
							model.setGenerate_time(Util.convertNull(temp_map.get("generate_time").toString()));
							model.setJoin_month(Util.convertNull(temp_map.get("join_month").toString()));
							model.setMember_id(Long.parseLong(temp_map.get("member_id").toString()));
							model.setMember_name(Util.convertNull(temp_map.get("member_name").toString()));
							model.setMember_phone(Util.convertNull(temp_map.get("member_phone").toString()));
							model.setTribe_name(Util.convertNull(temp_map.get("tribe_name").toString()));
							model.setTrideId(Long.parseLong(temp_map.get("tribe_id").toString()));
							model.setUnionId(Long.parseLong(temp_map.get("union_id").toString()));
							model.setPsum_flow(Util.convertNull(temp_map.get("psum_flow").toString()));
							model.setSum_flows(Util.convertNull(temp_map.get("sum_flow").toString()));
							sheik_model_begin_map.put(""+i, model);
						}
						loadView_sheik_monthbegin();
					}					
				} 
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");
				}
				else if(msg.what==2) {
					showCustomToast("酋长月初初始化数据获取失败");
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
				LinkedList<String> wholeUrl=Util.getWholeUrl(WulinAssemblyActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(WulinAssemblyActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(WulinAssemblyActivity.this);
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(WulinAssemblyActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map map=strategyManager.getWulinInitList(Long.parseLong(list.get(0)), list.get(1));
						if(map==null) {
							m.what=2;
						}
						else {
							m.obj=map;
							m.what=1;
						}
						flag=false;
						((GasStationApplication) getApplicationContext()).AreaUrl=currentUsedUrl;
					} catch(Error e) {
						flag=false;
						m.what=-1;
			        } catch(Exception e) {
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
	
	public void loadView_sheik_monthbegin() {
		wulinassembly_layout.removeAllViews();
		View view=LayoutInflater.from(WulinAssemblyActivity.this).inflate(R.layout.activity_wulinassembly_view_sheik_monthbegin, null);
		sheik_month_begin_list=(ListView) view.findViewById(R.id.sheik_month_begin_list);
		sheik_month_begin_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(WulinAssemblyActivity.this, WulinAssemblySheikUpdateActivity.class);
				Bundle bundle=new Bundle();
				bundle.putLong("trideId", sheik_model_begin_map.get(""+position).getTrideId());
				bundle.putInt("position", position);
				intent.putExtras(bundle);
				startActivityForResult(intent, 100);
			}
		});
		adapter_sheik_month_begin=new Adapter_Sheik_MonthBegin(sheik_model_begin_map, WulinAssemblyActivity.this);
		sheik_month_begin_list.setAdapter(adapter_sheik_month_begin);
		TextView sheik_month_begin_commit=(TextView) view.findViewById(R.id.sheik_month_begin_commit);
		sheik_month_begin_commit.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				ArrayList<Long> data_list=new ArrayList<Long>();
				Iterator<Entry<String, Sheik_MonthBegin_Model>> it=sheik_model_begin_map.entrySet().iterator();
				while(it.hasNext()) {
					Entry<String, Sheik_MonthBegin_Model> entry=it.next();
					Sheik_MonthBegin_Model model=entry.getValue();
					if(model.isCheck()) {
						data_list.add(model.getTrideId());
					}
				}
				initWulin_Sheik(data_list);
			}});
		wulinassembly_layout.addView(view);
	}
	
	public void loadData_sheik_monthusual() {
		title_refresh.setVisibility(View.INVISIBLE);
		title_refresh_progress.setVisibility(View.VISIBLE);		
		isLoading=true;
		WulinAssemblyType=2;
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				isLoading=false;
				title_refresh.setVisibility(View.VISIBLE);
				title_refresh_progress.setVisibility(View.INVISIBLE);
				if(msg.what==1) {
					Map map=(Map)msg.obj;
					if(map.get("result").toString().equals("0")) {
						showCustomToast(map.get("comments").toString());
					}
					else {
						sheik_model_usual_map.clear();
						Object[] tribes_arrays=(Object[]) map.get("tribes");
						System.out.println("数量为："+tribes_arrays.length);
						for(int i=0;i<tribes_arrays.length;i++) {
							Map temp_map=(Map)tribes_arrays[i];
							Sheik_MonthUsual_Model model=new Sheik_MonthUsual_Model();
							model.setCheck(true);
							model.setGenerate_time(Util.convertNull(temp_map.get("generate_time").toString()));
							model.setJoin_month(Util.convertNull(temp_map.get("join_month").toString()));
							model.setMember_id(Long.parseLong(temp_map.get("member_id").toString()));
							model.setMember_name(Util.convertNull(temp_map.get("member_name").toString()));
							model.setMember_phone(Util.convertNull(temp_map.get("member_phone").toString()));
							model.setTribe_name(Util.convertNull(temp_map.get("tribe_name").toString()));
							model.setTrideId(Long.parseLong(temp_map.get("tribe_id").toString()));
							model.setUnionId(Long.parseLong(temp_map.get("union_id").toString()));
							model.setPsum_flow(Util.convertNull(temp_map.get("psum_flow").toString()));
							model.setSum_flows(Util.convertNull(temp_map.get("sum_flow").toString()));
							sheik_model_usual_map.put(""+i, model);
						}
						loadView_sheik_monthusual();
					}					
				} 
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");
				}
				else if(msg.what==2) {
					showCustomToast("酋长月初初始化数据获取失败");
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
				LinkedList<String> wholeUrl=Util.getWholeUrl(WulinAssemblyActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(WulinAssemblyActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(WulinAssemblyActivity.this);
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(WulinAssemblyActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map map=strategyManager.getWulinList(Long.parseLong(list.get(0)), list.get(1));
						if(map==null) {
							m.what=2;
						}
						else {
							m.obj=map;
							m.what=1;
						}
						flag=false;
						((GasStationApplication) getApplicationContext()).AreaUrl=currentUsedUrl;
					} catch(Error e) {
						flag=false;
						m.what=-1;
			        } catch(Exception e) {
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
	
	public void loadView_sheik_monthusual() {
		wulinassembly_layout.removeAllViews();
		View view=LayoutInflater.from(WulinAssemblyActivity.this).inflate(R.layout.activity_wulinassembly_view_sheik_monthusual, null);
		sheik_month_usual_list=(ListView) view.findViewById(R.id.sheik_month_usual_list);
		adapter_sheik_month_usual=new Adapter_Sheik_MonthUsual(sheik_model_usual_map, WulinAssemblyActivity.this);
		sheik_month_usual_list.setAdapter(adapter_sheik_month_usual);
		TextView sheik_month_usual_add=(TextView) view.findViewById(R.id.sheik_month_usual_add);
		sheik_month_usual_add.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(WulinAssemblyActivity.this, WulinAssemblySheikAddActivity.class);
				Bundle bundle=new Bundle();
				bundle.putLong("unionId", unionId);
				intent.putExtras(bundle);
				startActivityForResult(intent, 200);
			}});
		wulinassembly_layout.addView(view);
	}
	
	public void loadData_leader_monthbegin() {
		title_refresh.setVisibility(View.INVISIBLE);
		title_refresh_progress.setVisibility(View.VISIBLE);
		isLoading=true;
		WulinAssemblyType=3;
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				isLoading=false;
				title_refresh.setVisibility(View.VISIBLE);
				title_refresh_progress.setVisibility(View.INVISIBLE);
				if(msg.what==1) {
					Map map=(Map)msg.obj;
					if(map.get("result").toString().equals("0")) {
						showCustomToast(map.get("comments").toString());
					}
					else {
						leader_model_begin_map.clear();
						Object[] members_arrays=(Object[]) map.get("members");
						System.out.println("数量为："+members_arrays.length);
						for(int i=0;i<members_arrays.length;i++) {
							Map temp_map=(Map)members_arrays[i];
							Leader_MonthBegin_Model model=new Leader_MonthBegin_Model();
							model.setCheck(true);
							model.setGenerate_time(Util.convertNull(temp_map.get("generate_time").toString()));
							model.setJoin_month(Util.convertNull(temp_map.get("join_month").toString()));
							model.setMember_id(Long.parseLong(temp_map.get("member_id").toString()));
							model.setMember_name(Util.convertNull(temp_map.get("member_name").toString()));
							model.setMember_phone(Util.convertNull(temp_map.get("member_phone").toString()));
							model.setRole_id(Long.parseLong(temp_map.get("role_id").toString()));
							model.setTribe_id(Long.parseLong(temp_map.get("tribe_id").toString()));
							model.setPsum_flow(Util.convertNull(temp_map.get("psum_flow").toString()));
							model.setSum_flows(Util.convertNull(temp_map.get("sum_flow").toString()));
							leader_model_begin_map.put(""+i, model);
						}						
						loadView_leader_monthbegin();
					}
				}
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");
				}
				else if(msg.what==2) {
					showCustomToast("首领月初初始化数据获取失败");
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
				LinkedList<String> wholeUrl=Util.getWholeUrl(WulinAssemblyActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(WulinAssemblyActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(WulinAssemblyActivity.this);
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(WulinAssemblyActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map map=strategyManager.getInitTribeMembers(Long.parseLong(list.get(0)), list.get(1));
						if(map==null) {
							m.what=2;
						}
						else {
							m.obj=map;
							m.what=1;
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
	
	public void loadView_leader_monthbegin() {
		wulinassembly_layout.removeAllViews();
		View view=LayoutInflater.from(WulinAssemblyActivity.this).inflate(R.layout.activity_wulinassembly_view_leader_monthbegin, null);
		leader_month_begin_list=(ListView) view.findViewById(R.id.leader_month_begin_list);
		adapter_leader_month_begin=new Adapter_Leader_MonthBegin(leader_model_begin_map, WulinAssemblyActivity.this);
		leader_month_begin_list.setAdapter(adapter_leader_month_begin);
		TextView leader_month_begin_commit=(TextView) view.findViewById(R.id.leader_month_begin_commit);
		leader_month_begin_commit.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final ArrayList<Long> data_list=new ArrayList<Long>();
				Iterator<Entry<String, Leader_MonthBegin_Model>> it=leader_model_begin_map.entrySet().iterator();
				while(it.hasNext()) {
					Entry<String, Leader_MonthBegin_Model> entry=it.next();
					Leader_MonthBegin_Model model=entry.getValue();
					if(model.isCheck()) {
						data_list.add(model.getMember_id());
					}
				}
				new AlertDialog.Builder(WulinAssemblyActivity.this).setTitle("提示").setMessage("未被选中的人员本月将被移除").setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						initWulin_Leader(data_list);
					}
				}).show();				
			}});
		wulinassembly_layout.addView(view);
	}
	
	public void loadData_leader_monthusual() {
		title_refresh.setVisibility(View.INVISIBLE);
		title_refresh_progress.setVisibility(View.VISIBLE);
		isLoading=true;
		WulinAssemblyType=4;
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				isLoading=false;
				title_refresh.setVisibility(View.VISIBLE);
				title_refresh_progress.setVisibility(View.INVISIBLE);
				if(msg.what==1) {
					Map map=(Map)msg.obj;
					if(map.get("result").toString().equals("0")) {
						showCustomToast(map.get("comments").toString());
					}
					else {
						leader_model_usual_map.clear();
						Object[] members_arrays=(Object[]) map.get("members");
						System.out.println("数量为："+members_arrays.length);
						for(int i=0;i<members_arrays.length;i++) {
							Map temp_map=(Map)members_arrays[i];
							Leader_MonthUsual_Model model=new Leader_MonthUsual_Model();
							model.setCheck(true);
							model.setGenerate_time(Util.convertNull(temp_map.get("generate_time").toString()));
							model.setJoin_month(Util.convertNull(temp_map.get("join_month").toString()));
							model.setMember_id(Long.parseLong(temp_map.get("member_id").toString()));
							model.setMember_name(Util.convertNull(temp_map.get("member_name").toString()));
							model.setMember_phone(Util.convertNull(temp_map.get("member_phone").toString()));
							model.setRole_id(Long.parseLong(temp_map.get("role_id").toString()));
							model.setTribe_id(Long.parseLong(temp_map.get("tribe_id").toString()));
							model.setPsum_flow(Util.convertNull(temp_map.get("psum_flow").toString()));
							model.setSum_flows(Util.convertNull(temp_map.get("sum_flow").toString()));
							leader_model_usual_map.put(""+i, model);
						}						
						loadView_leader_monthusual(map.get("residue_flow").toString());
					}
				}
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");
				}
				else if(msg.what==2) {
					showCustomToast("首领月初初始化数据获取失败");
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
				LinkedList<String> wholeUrl=Util.getWholeUrl(WulinAssemblyActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(WulinAssemblyActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(WulinAssemblyActivity.this);
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(WulinAssemblyActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map map=strategyManager.getTribeMembers(Long.parseLong(list.get(0)), list.get(1));
						if(map==null) {
							m.what=2;
						}
						else {
							m.obj=map;
							m.what=1;
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
	
	public void loadView_leader_monthusual(final String residue_flow) {
		wulinassembly_layout.removeAllViews();
		View view=LayoutInflater.from(WulinAssemblyActivity.this).inflate(R.layout.activity_wulinassembly_view_leader_monthusual, null);
		leader_month_usual_list=(ListView) view.findViewById(R.id.leader_month_usual_list);
		leader_month_usual_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(WulinAssemblyActivity.this, WulinAssemblyLeaderSendActivity.class);
				Bundle bundle=new Bundle();
				bundle.putLong("memberId", leader_model_usual_map.get(""+position).getMember_id());
				intent.putExtras(bundle);
				startActivityForResult(intent, 300);
			}
		});
		adapter_leader_month_usual=new Adapter_Leader_MonthUsual(leader_model_usual_map, WulinAssemblyActivity.this);
		leader_month_usual_list.setAdapter(adapter_leader_month_usual);
		TextView leader_month_usual_get=(TextView) view.findViewById(R.id.leader_month_usual_get);
		leader_month_usual_get.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				Builder builder=new AlertDialog.Builder(WulinAssemblyActivity.this);
				View view=LayoutInflater.from(WulinAssemblyActivity.this).inflate(R.layout.dialog_text, null);
				final TextView editsystip=(TextView) view.findViewById(R.id.editsystip);
				editsystip.setVisibility(View.GONE);
				final EditText editsystext=(EditText) view.findViewById(R.id.editsystext);
				builder.setView(view);
				builder.setTitle("请您输入领取的流量");
				builder.setPositiveButton("提交", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						if(!editsystext.getText().toString().equals("")) {
							getFlow(editsystext.getText().toString());
						}
						else {
							showCustomToast("请您输入领取的流量");
						}
					}
				});
				builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						
					}
				});
				AlertDialog dialog=builder.create();
				dialog.show();
			
			}});
		TextView leader_month_usual_add=(TextView) view.findViewById(R.id.leader_month_usual_add);
		leader_month_usual_add.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(WulinAssemblyActivity.this, WulinAssemblyLeaderAddActivity.class);
				Bundle bundle=new Bundle();
				bundle.putLong("tribeId", tribeId);
				intent.putExtras(bundle);
				startActivityForResult(intent, 300);
			}});
		TextView leader_month_usual_lastflow=(TextView) view.findViewById(R.id.leader_month_usual_lastflow);
		leader_month_usual_lastflow.setText(residue_flow+" MB");
		wulinassembly_layout.addView(view);	
	}
	
	public void loadView_member() {
		wulinassembly_layout.removeAllViews();
		View view=LayoutInflater.from(WulinAssemblyActivity.this).inflate(R.layout.activity_wulinassembly_view_member, null);
		TextView member_send_amount_text=(TextView) view.findViewById(R.id.member_send_amount_text);
		TextView member_send_unused_text=(TextView) view.findViewById(R.id.member_send_unused_text);
		ImageView member_send_cylinder=(ImageView) view.findViewById(R.id.member_send_cylinder);
		initUIData(member_send_amount_text, member_send_unused_text, member_send_cylinder);
		final EditText member_send_operate_flow=(EditText) view.findViewById(R.id.member_send_operate_flow);
		member_send_operate_flow.setText(""+residue_flow);
		TextView member_send_operate_ok=(TextView) view.findViewById(R.id.member_send_operate_ok);
		member_send_operate_ok.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!member_send_operate_flow.getText().toString().equals("")) {
					getFlow(member_send_operate_flow.getText().toString());
				}
			}});
		wulinassembly_layout.addView(view);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		try {
			if(resultCode==RESULT_OK) {
				if(requestCode==100) {
					Bundle bundle=data.getExtras();
					Sheik_MonthBegin_Model model=(Sheik_MonthBegin_Model)bundle.getSerializable("model");
					int position=bundle.getInt("position");
					sheik_model_begin_map.put(""+position, model);
					adapter_sheik_month_begin.notifyDataSetChanged();
				}
				if(requestCode==200) {
					System.out.println("刷新完成");
					loadData_sheik_monthusual();
				}
				if(requestCode==300) {
					System.out.println("刷新完成");
					loadData_leader_monthusual();
				}
			}
		} catch(Exception e) {
			
		}
		
	}
	
	/**
	 * 武林大会酋长授权
	 * @param data_list
	 */
	public void initWulin_Sheik(final ArrayList<Long> data_list) {
		
		showProgressDialog(R.string.tishi_loading);
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				dismissProgressDialog();
				if(msg.what==1) {
					Map map=(Map) msg.obj;
					if(map.get("result").toString().equals("1")) {
						showCustomToast("授权成功");
						loadData_sheik_monthusual();
					}
					else if(map.get("result").toString().equals("2")) {
						showCustomToast(map.get("comments").toString());
					}
				}
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");
				}
				else if(msg.what==2) {
					showCustomToast("授权数据获取失败");
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
				String temp="";
				for(int i=0;i<data_list.size();i++) {
					if(i!=data_list.size()-1) {
						temp+=data_list.get(i)+":";
					}
					else {
						temp+=data_list.get(i);
					}
				}
				LinkedList<String> wholeUrl=Util.getWholeUrl(WulinAssemblyActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(WulinAssemblyActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(WulinAssemblyActivity.this);
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(WulinAssemblyActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map map=strategyManager.initWulinList(Long.parseLong(list.get(0)), temp, list.get(1));
						if(map==null) {
							m.what=2;
						}
						else {
							m.obj=map;
							m.what=1;
						}
						flag=false;
						((GasStationApplication) getApplicationContext()).AreaUrl=currentUsedUrl;
					} catch(Error e) {
						flag=false;
						m.what=-1;
			        } catch(Exception e) {
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
	 * 武林大会首领授权
	 * @param data_list
	 */
	public void initWulin_Leader(final ArrayList<Long> data_list) {
		
		showProgressDialog(R.string.tishi_loading);
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				dismissProgressDialog();
				if(msg.what==1) {
					Map map=(Map) msg.obj;
					if(map.get("result").toString().equals("1")) {
						showCustomToast("授权成功");
						loadData_leader_monthusual();
					}
					else if(map.get("result").toString().equals("2")) {
						showCustomToast(map.get("comments").toString());
					}
				}
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");
				}
				else if(msg.what==2) {
					showCustomToast("授权数据获取失败");
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
				String temp="";
				for(int i=0;i<data_list.size();i++) {
					if(i!=data_list.size()-1) {
						temp+=data_list.get(i)+":";
					}
					else {
						temp+=data_list.get(i);
					}
				}
				LinkedList<String> wholeUrl=Util.getWholeUrl(WulinAssemblyActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(WulinAssemblyActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(WulinAssemblyActivity.this);
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(WulinAssemblyActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map map=strategyManager.initTribeMembers(Long.parseLong(list.get(0)), temp, list.get(1));
						if(map==null) {
							m.what=2;
						}
						else {
							m.obj=map;
							m.what=1;
						}
						flag=false;
						((GasStationApplication) getApplicationContext()).AreaUrl=currentUsedUrl;
					} catch(Error e) {
						flag=false;
						m.what=-1;
			        } catch(Exception e) {
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
	
	public void getFlow(final String flow) {
		
		showProgressDialog(R.string.tishi_loading);
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				dismissProgressDialog();
				if(msg.what==1) {
					Map map=(Map) msg.obj;
					if(map.get("result").toString().equals("1")) {
						showCustomToast("领取成功"+flow+"M流量");
						if(WulinAssemblyType==5) {
							residue_flow=map.get("residue_flow").toString();
							total_flow=map.get("total_flow").toString();
							loadView_member();
						}
						else if(WulinAssemblyType==4) {
							loadData_leader_monthusual();
						}						
					}
					else if(map.get("result").toString().equals("4")) {
						showCustomToast(map.get("comments").toString());
					}
				}
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");
				}
				else if(msg.what==2) {
					showCustomToast("授权数据获取失败");
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
				LinkedList<String> wholeUrl=Util.getWholeUrl(WulinAssemblyActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(WulinAssemblyActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(WulinAssemblyActivity.this);
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(WulinAssemblyActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map map=strategyManager.getOrSendFlow(Long.parseLong(list.get(0)), 1, Long.parseLong(list.get(0)), Double.parseDouble(flow), list.get(1));
						if(map==null) {
							m.what=2;
						}
						else {
							m.obj=map;
							m.what=1;
						}
						flag=false;
						((GasStationApplication) getApplicationContext()).AreaUrl=currentUsedUrl;
					} catch(Error e) {
						flag=false;
						m.what=-1;
			        } catch(Exception e) {
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
	 * 加载页面数据
	 */
	public void initUIData(TextView amount_text, TextView unused_text, ImageView cylinder) {
		double total=Double.parseDouble(total_flow);
		double last=Double.parseDouble(residue_flow);
		int ratio=0;
		if(total!=0) {
			ratio=(int) (last*100/total);
		}
		amount_text.setText(""+total+"MB");
		unused_text.setText(""+last+"MB");
		getImageRes(ratio, cylinder);	
	}
	
	/**
	 * 返回图片id
	 * @param ratio
	 * @return
	 */
	public void getImageRes(int ratio, ImageView cylinder) {
		String imageId=String.valueOf(ratio/10)+String.valueOf(ratio%10>=5?5:0);
		//需要判断是不是小于5并且大于0
		if(imageId.equals("00")&&ratio%10>0) {
			imageId="05";
		}
		cylinder.setImageResource(getResources().getIdentifier(getPackageName()+":drawable/cylinder_small_"+imageId, null,null));
	}
}
