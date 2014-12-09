package com.linkage.gas_station.memberday;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.model.MemberBuyModel;
import com.linkage.gas_station.model.MemberModel;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.StrategyManager;

public class FlowDayActivity extends BaseActivity {
	
	TextView title_name=null;
	ImageView title_back=null;

	TextView member_left_tab=null;
	TextView member_right_tab=null;
	ListView member_right_listview=null;
	FlowDayRightAdapter adapter_right=null;
	ListView member_next_listview=null;
	FlowDayLeftAdapter adapter_before=null;
	ListView member_left_listview=null;
	FlowDayLeftAdapter adapter_left=null;
	RelativeLayout member_left_layout=null;
	LinearLayout member_day_no_layout=null;
	LinearLayout member_day_start_layout=null;
	LinearLayout member_day_notstart_layout=null;
	TextView member_day=null;
	TextView member_hour=null;
	TextView member_min=null;
	TextView member_level_desp=null;
	LinearLayout member_selecttype=null;
	ListView listview=null;
	Button sure_button=null;
	
	//是否已经关闭刷新时间线程
	boolean isStopThread=false;
	ArrayList<MemberBuyModel> strs_right=null;
	ArrayList<MemberModel> strs_left=null;
	ArrayList<MemberModel> strs_left_before=null;
	boolean isLeftLoading=false;
	boolean isRightLoading=false;
	Date date_start=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_member);
		
		((GasStationApplication) getApplication()).tempActivity.add(FlowDayActivity.this);
		
		strs_right=new ArrayList<MemberBuyModel>();
		strs_left=new ArrayList<MemberModel>();
		strs_left_before=new ArrayList<MemberModel>();
		
		init();
	}
	
	private void init() {
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText("节日特惠");
		title_back=(ImageView) findViewById(R.id.title_back);
		title_back.setVisibility(View.VISIBLE);
		title_back.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
		
		member_level_desp=(TextView) findViewById(R.id.member_level_desp);
		member_left_tab=(TextView) findViewById(R.id.member_left_tab);
		member_left_tab.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				member_left_tab.setTextColor(getResources().getColor(R.color.member_title_choice_color));
				member_right_tab.setTextColor(getResources().getColor(R.color.member_title_normal_color));
				
				member_left_tab.setBackgroundResource(R.drawable.member_title_on);
				member_right_tab.setBackgroundResource(R.drawable.member_title_off);
				
				Drawable member_left_on=getResources().getDrawable(R.drawable.member_left_on);
				member_left_on.setBounds(Util.dip2px(FlowDayActivity.this, 10), 0, member_left_on.getMinimumWidth()+Util.dip2px(FlowDayActivity.this, 10), member_left_on.getMinimumHeight());				
				member_left_tab.setCompoundDrawables(member_left_on, null, null, null);
				
				Drawable member_right_off=getResources().getDrawable(R.drawable.member_right_off);
				member_right_off.setBounds(Util.dip2px(FlowDayActivity.this, 10), 0, member_right_off.getMinimumWidth()+Util.dip2px(FlowDayActivity.this, 10), member_right_off.getMinimumHeight());				
				member_right_tab.setCompoundDrawables(member_right_off, null, null, null);
				
				member_left_layout.setVisibility(View.VISIBLE);
				member_right_listview.setVisibility(View.GONE);
			}});
		member_right_tab=(TextView) findViewById(R.id.member_right_tab);
		member_right_tab.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				member_right_tab.setTextColor(getResources().getColor(R.color.member_title_choice_color));
				member_left_tab.setTextColor(getResources().getColor(R.color.member_title_normal_color));
				
				member_right_tab.setBackgroundResource(R.drawable.member_title_on);
				member_left_tab.setBackgroundResource(R.drawable.member_title_off);
				
				Drawable member_left_off=getResources().getDrawable(R.drawable.member_left_off);
				member_left_off.setBounds(Util.dip2px(FlowDayActivity.this, 10), 0, member_left_off.getMinimumWidth()+Util.dip2px(FlowDayActivity.this, 10), member_left_off.getMinimumHeight());				
				member_left_tab.setCompoundDrawables(member_left_off, null, null, null);
				
				Drawable member_right_on=getResources().getDrawable(R.drawable.member_right_on);
				member_right_on.setBounds(Util.dip2px(FlowDayActivity.this, 10), 0, member_right_on.getMinimumWidth()+Util.dip2px(FlowDayActivity.this, 10), member_right_on.getMinimumHeight());				
				member_right_tab.setCompoundDrawables(member_right_on, null, null, null);
				
				member_left_layout.setVisibility(View.GONE);
				member_right_listview.setVisibility(View.VISIBLE);
			}});
		
		member_day_no_layout=(LinearLayout) findViewById(R.id.member_day_no_layout);
		member_day_start_layout=(LinearLayout) findViewById(R.id.member_day_start_layout);
		member_day_notstart_layout=(LinearLayout) findViewById(R.id.member_day_notstart_layout);
		
		member_left_listview=(ListView) findViewById(R.id.member_left_listview);
		adapter_left=new FlowDayLeftAdapter(FlowDayActivity.this, strs_left, true);
		member_left_listview.setAdapter(adapter_left);
		member_left_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if(strs_left.get(arg2).getPrize_type()!=1) {
					member_selecttype(strs_left.get(arg2).getPrize_type(), Long.parseLong(strs_left.get(arg2).getPrize_id()));
				}
				else {
					Intent intent=new Intent(FlowDayActivity.this, FlowReceiveActivity.class);
					Bundle bundle=new Bundle();
					bundle.putLong("prizeId", Long.parseLong(strs_left.get(arg2).getPrize_id()));
					bundle.putLong("flowOfferId", 0);
					intent.putExtras(bundle);
					startActivityForResult(intent, 700);
				}
			}
		});
		
		member_right_listview=(ListView) findViewById(R.id.member_right_listview);
		adapter_right=new FlowDayRightAdapter(FlowDayActivity.this, strs_right);
		member_right_listview.setAdapter(adapter_right);
		member_right_listview.setVisibility(View.GONE);
		
		member_left_layout=(RelativeLayout) findViewById(R.id.member_left_layout);
		member_next_listview=(ListView) findViewById(R.id.member_next_listview);
		adapter_before=new FlowDayLeftAdapter(FlowDayActivity.this, strs_left_before, false);
		member_next_listview.setAdapter(adapter_before);
		
		member_day=(TextView) findViewById(R.id.member_day);
		member_hour=(TextView) findViewById(R.id.member_hour);
		member_min=(TextView) findViewById(R.id.member_min);
		
		member_selecttype=(LinearLayout) findViewById(R.id.member_selecttype);
		listview=(ListView) findViewById(R.id.listview);
		sure_button=(Button) findViewById(R.id.sure_button);
		sure_button.setVisibility(View.GONE);
		
		userFlowPrizes();
		flowPrizes();
	}
	
	Handler handler=new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(!isStopThread) {

				long calTime=date_start.getTime()-new Date().getTime();
				if(calTime>0) {
					int day=(int) ((calTime/(60*60*24*1000))<0?0:(calTime/(60*60*24*1000)));
					int hour=(int) ((calTime-60*60*24*1000*day)/(60*60*1000)<0?0:(calTime-60*60*24*1000*day)/(60*60*1000));
					int minute=(int) ((calTime-60*60*24*1000*day-60*60*1000*hour)/(1000*60)<0?0:(calTime-60*60*24*1000*day-60*60*1000*hour)/(60*1000));
					System.out.println(day+" "+hour+" "+minute);
					member_day.setText(day<10?"0"+day:""+day);
					member_hour.setText(hour<10?"0"+hour:""+hour);
					member_min.setText(minute<10?"0"+minute:""+minute);
					handler.postDelayed(runnable, 1000);
				}
				
			}
		}
	};
	
	//刷新时间
	Runnable runnable=new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			handler.sendEmptyMessage(0);
		}
	};
	
	
	private void userFlowPrizes() {
		isRightLoading=true;
		final Handler handlerRightList=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.what==1) {
					Object[] objs=(Object[])msg.obj;
					strs_right.clear();
					for(int i=0;i<objs.length;i++) {
						Map map=(Map) objs[i];
						MemberBuyModel model=new MemberBuyModel();
						model.setGenerate_time(map.get("generate_time")!=null?map.get("generate_time").toString():"");
						model.setId(map.get("id")!=null?map.get("id").toString():"");
						model.setPhone_num(map.get("phone_num")!=null?map.get("phone_num").toString():"");
						model.setPrize_name(map.get("prize_name")!=null?map.get("prize_name").toString():"");
						model.setPrize_pic(map.get("prize_pic")!=null?map.get("prize_pic").toString():"");
						model.setState(map.get("state")!=null?Integer.parseInt(map.get("state").toString()):2);
						model.setSupplyer_address(map.get("supplyer_address")!=null?map.get("supplyer_address").toString():"");
						model.setSupplyer_name(map.get("supplyer_name")!=null?map.get("supplyer_name").toString():"");
						model.setSupplyer_phone(map.get("supplyer_phone")!=null?map.get("supplyer_phone").toString():"");
						model.setValid_date(map.get("valid_date")!=null?map.get("valid_date").toString():"");
						model.setPrize_type(Integer.parseInt(map.get("prize_type").toString()));
						model.setTotal_times(map.get("total_times")!=null?map.get("total_times").toString():"0");
						model.setResidue_times(map.get("residue_times")!=null?map.get("residue_times").toString():"0");
						model.setSeqId(map.get("interface_id")!=null?map.get("interface_id").toString():"-1");
						strs_right.add(model);
					}
					adapter_right.notifyDataSetChanged();
				}
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");
				}
				else if(msg.what==0) {
					showCustomToast(getResources().getString(R.string.timeout_exp));
				}
				isRightLoading=false;
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(FlowDayActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(FlowDayActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(FlowDayActivity.this);
						
						StrategyManager strategyManager=GetWebDate.getActivityHessionFactiory(FlowDayActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map[] map=strategyManager.userFlowPrizes(Long.parseLong(list.get(0)), list.get(1));
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
	
	private void flowPrizes() {
		isLeftLoading=true;
		final Handler handlerLeftList=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.what==1) {
					Map map=(Map)msg.obj;
					if(map.get("flowDay").toString().equals("")) {
						member_day_no_layout.setVisibility(View.VISIBLE);
						member_day_start_layout.setVisibility(View.GONE);
						member_day_notstart_layout.setVisibility(View.GONE);
					}
					else if(!map.get("flowDay").toString().equals("")) {
						member_day_no_layout.setVisibility(View.GONE);
						Calendar cal=Calendar.getInstance();
						String year=""+cal.get(Calendar.YEAR);
						String month=(cal.get(Calendar.MONTH)+1)<10?"0"+(cal.get(Calendar.MONTH)+1):""+(cal.get(Calendar.MONTH)+1);
						String day=cal.get(Calendar.DAY_OF_MONTH)<10?"0"+cal.get(Calendar.DAY_OF_MONTH):""+cal.get(Calendar.DAY_OF_MONTH);
						
						String year_=map.get("flowDay").toString().split("-")[0];
						String month_=map.get("flowDay").toString().split("-")[1];
						String day_=map.get("flowDay").toString().split("-")[2];
						
						Object[] memberPrizes=(Object[])map.get("flowPrizes");
						strs_left.clear();
						strs_left_before.clear();
						for(int i=0;i<memberPrizes.length;i++) {
							Map memberPrizes_map=(Map) memberPrizes[i];
							MemberModel model=new MemberModel();
							model.setPrize_id(memberPrizes_map.get("prize_id")!=null?memberPrizes_map.get("prize_id").toString():"0");
							model.setPrize_name(memberPrizes_map.get("prize_name")!=null?memberPrizes_map.get("prize_name").toString():"");
							model.setPrize_pic(memberPrizes_map.get("prize_pic")!=null?memberPrizes_map.get("prize_pic").toString():"");
							model.setSupplyer_address(memberPrizes_map.get("supplyer_address")!=null?memberPrizes_map.get("supplyer_address").toString():"");
							model.setSupplyer_name(memberPrizes_map.get("supplyer_name")!=null?memberPrizes_map.get("supplyer_name").toString():"");
							model.setSupplyer_phone(memberPrizes_map.get("supplyer_phone")!=null?memberPrizes_map.get("supplyer_phone").toString():"");
							model.setValid_date(memberPrizes_map.get("valid_date")!=null?memberPrizes_map.get("valid_date").toString():"");
							model.setPrize_resude_cnt(memberPrizes_map.get("prize_resude_cnt")!=null?memberPrizes_map.get("prize_resude_cnt").toString():"");
							model.setLevel_description(memberPrizes_map.get("level_description")!=null?memberPrizes_map.get("level_description").toString():"");
							model.setPrize_type(memberPrizes_map.get("prize_type")==null?-1:Integer.parseInt(memberPrizes_map.get("prize_type").toString()));
							strs_left_before.add(model);
							strs_left.add(model);
						}
						
						if(year.equals(year_)&&month.equals(month_)&&day.equals(day_)) {
							member_day_start_layout.setVisibility(View.VISIBLE);
							member_day_notstart_layout.setVisibility(View.GONE);
							
							adapter_left.notifyDataSetChanged();
						}
						else {
							member_day_start_layout.setVisibility(View.GONE);
							member_day_notstart_layout.setVisibility(View.VISIBLE);
							
							adapter_before.notifyDataSetChanged();
							
							String endTime=map.get("flowDay").toString()+" 00:00:00";
							SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							try {
								date_start=format.parse(endTime);
								handler.post(runnable);
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");
				}
				else if(msg.what==0) {
					showCustomToast(getResources().getString(R.string.timeout_exp));
				}
				isLeftLoading=false;
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(FlowDayActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(FlowDayActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(FlowDayActivity.this);
						
						StrategyManager strategyManager=GetWebDate.getActivityHessionFactiory(FlowDayActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map map=strategyManager.flowPrizes(Long.parseLong(list.get(0)), list.get(1));
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
				handlerLeftList.sendMessage(m);
			}}).start();
	}
	
	/*
	 * 获取流量列表
	 */
	private void member_selecttype(final int prizeType, final long prizeId) {
		showProgressDialog(R.string.tishi_loading);
		final Handler handler_=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				dismissProgressDialog();
				if(msg.what==1) {
					if(msg.obj!=null) {
						member_selecttype.setVisibility(View.VISIBLE);
						final Object[] objs=(Object[]) msg.obj;
						ArrayList<String> strs=new ArrayList<String>();
						for(int i=0;i<objs.length;i++) {
							Map map=(Map) objs[i];
							strs.add(map.get("offer_name").toString());
						}
						ArrayAdapter<String> adapter=new ArrayAdapter<String>(FlowDayActivity.this, R.layout.adapter_arearegion, R.id.area_text, strs);
						listview.setAdapter(adapter);
						listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

							@Override
							public void onItemClick(AdapterView<?> arg0,
									View arg1, int arg2, long arg3) {
								// TODO Auto-generated method stub
								Map map=(Map) objs[arg2];
								member_selecttype.setVisibility(View.GONE);
								
								Intent intent=new Intent(FlowDayActivity.this, FlowReceiveActivity.class);
								Bundle bundle=new Bundle();
								bundle.putLong("prizeId", prizeId);
								bundle.putLong("flowOfferId", Long.parseLong(map.get("offer_id").toString()));
								intent.putExtras(bundle);
								startActivityForResult(intent, 700);
							}
						});
					}
				}
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");
				}
				else if(msg.what==0) {
					showCustomToast(getResources().getString(R.string.timeout_exp));
				}
				isLeftLoading=false;
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(FlowDayActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(FlowDayActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(FlowDayActivity.this);
						
						StrategyManager strategyManager=GetWebDate.getActivityHessionFactiory(FlowDayActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map[] map=strategyManager.getflowByCost(Long.parseLong(list.get(0)), list.get(1), prizeType);
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
				handler_.sendMessage(m);
			}}).start();
	
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_OK&&requestCode==700) {
			userFlowPrizes();
			flowPrizes();
		}
	}
	
	protected void onDestroy() {
		super.onDestroy();
		isStopThread=true;
		handler.removeCallbacks(runnable);
		
		((GasStationApplication) getApplication()).tempActivity.remove(FlowDayActivity.this);
	};
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode==KeyEvent.KEYCODE_BACK&&member_selecttype.getVisibility()==View.VISIBLE) {
			member_selecttype.setVisibility(View.GONE);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}
