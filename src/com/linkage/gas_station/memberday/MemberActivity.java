package com.linkage.gas_station.memberday;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.jiayou.JiayouDetaiActivity;
import com.linkage.gas_station.model.MemberBuyModel;
import com.linkage.gas_station.model.MemberModel;
import com.linkage.gas_station.model.ValidMemberModel;
import com.linkage.gas_station.qqapi.QQActivity;
import com.linkage.gas_station.sinaweiboapi.WBMainActivity;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.StrategyManager;
import com.linkage.gas_station.wxapi.SendWeixin;
import com.linkage.gas_station.yxapi.SendYixin;

public class MemberActivity extends BaseActivity {
	
	TextView title_name=null;
	ImageView title_back=null;

	TextView member_left_tab=null;
	TextView member_right_tab=null;
	ListView member_right_listview=null;
	MemberRightAdapter adapter_right=null;
	ListView member_next_listview=null;
	MemberLeftAdapter adapter_before=null;
	ListView member_left_listview=null;
	MemberLeftAdapter adapter_left=null;
	RelativeLayout member_left_layout=null;
	LinearLayout member_day_no_layout=null;
	LinearLayout member_day_start_layout=null;
	LinearLayout member_day_notstart_layout=null;
	TextView member_day=null;
	TextView member_hour=null;
	TextView member_min=null;
	TextView member_sec=null;
	TextView member_level_desp=null;
	
	//是否已经关闭刷新时间线程
	boolean isStopThread=false;
	ArrayList<MemberBuyModel> strs_right=null;
	ArrayList<MemberModel> strs_left=null;
	ArrayList<MemberModel> strs_left_before=null;
	boolean isLeftLoading=false;
	boolean isRightLoading=false;
	Date date_start=null;
	
	int permitValue[]={1, 2, 3, 4};
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_member);
		
		((GasStationApplication) getApplication()).tempActivity.add(MemberActivity.this);
		
		strs_right=new ArrayList<MemberBuyModel>();
		strs_left=new ArrayList<MemberModel>();
		strs_left_before=new ArrayList<MemberModel>();
		
		init();
	}
	
	private void init() {
		
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText("会员日");
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
				member_left_on.setBounds(Util.dip2px(MemberActivity.this, 10), 0, member_left_on.getMinimumWidth()+Util.dip2px(MemberActivity.this, 10), member_left_on.getMinimumHeight());				
				member_left_tab.setCompoundDrawables(member_left_on, null, null, null);
				
				Drawable member_right_off=getResources().getDrawable(R.drawable.member_right_off);
				member_right_off.setBounds(Util.dip2px(MemberActivity.this, 10), 0, member_right_off.getMinimumWidth()+Util.dip2px(MemberActivity.this, 10), member_right_off.getMinimumHeight());				
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
				member_left_off.setBounds(Util.dip2px(MemberActivity.this, 10), 0, member_left_off.getMinimumWidth()+Util.dip2px(MemberActivity.this, 10), member_left_off.getMinimumHeight());				
				member_left_tab.setCompoundDrawables(member_left_off, null, null, null);
				
				Drawable member_right_on=getResources().getDrawable(R.drawable.member_right_on);
				member_right_on.setBounds(Util.dip2px(MemberActivity.this, 10), 0, member_right_on.getMinimumWidth()+Util.dip2px(MemberActivity.this, 10), member_right_on.getMinimumHeight());				
				member_right_tab.setCompoundDrawables(member_right_on, null, null, null);
				
				member_left_layout.setVisibility(View.GONE);
				member_right_listview.setVisibility(View.VISIBLE);
			}});
		
		member_day_no_layout=(LinearLayout) findViewById(R.id.member_day_no_layout);
		member_day_start_layout=(LinearLayout) findViewById(R.id.member_day_start_layout);
		member_day_notstart_layout=(LinearLayout) findViewById(R.id.member_day_notstart_layout);
		
		member_left_listview=(ListView) findViewById(R.id.member_left_listview);
		adapter_left=new MemberLeftAdapter(strs_left, MemberActivity.this, true);
		member_left_listview.setAdapter(adapter_left);
		member_left_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				validMember(Long.parseLong(strs_left.get(arg2).getPrize_id()), strs_left.get(arg2).getPrize_type());
			}
		});
		
		member_right_listview=(ListView) findViewById(R.id.member_right_listview);
		adapter_right=new MemberRightAdapter(strs_right, MemberActivity.this, MemberActivity.this);
		member_right_listview.setAdapter(adapter_right);
		member_right_listview.setVisibility(View.GONE);
		member_right_listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				if(strs_right.get(arg2).getPrize_type()==1) {
					Intent intent=new Intent(MemberActivity.this, MemberShowCodeActivity.class);
					Bundle bundle=new Bundle();
					bundle.putString("id", strs_right.get(arg2).getId());
					bundle.putString("name", strs_right.get(arg2).getPrize_name());
					intent.putExtras(bundle);
					startActivity(intent);
				}
			}
		});
		member_left_layout=(RelativeLayout) findViewById(R.id.member_left_layout);
		member_next_listview=(ListView) findViewById(R.id.member_next_listview);
		adapter_before=new MemberLeftAdapter(strs_left_before, MemberActivity.this, false);
		member_next_listview.setAdapter(adapter_before);
		
		member_day=(TextView) findViewById(R.id.member_day);
		member_hour=(TextView) findViewById(R.id.member_hour);
		member_min=(TextView) findViewById(R.id.member_min);
		member_sec=(TextView) findViewById(R.id.member_sec);
		
		userPrizes();
		memberPrizes();
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
					int sec=(int) ((calTime-60*60*24*1000*day-60*60*1000*hour-60*1000*minute)/1000);
					System.out.println(day+" "+hour+" "+minute);
					member_day.setText(day<10?"0"+day:""+day);
					member_hour.setText(hour<10?"0"+hour:""+hour);
					member_min.setText(minute<10?"0"+minute:""+minute);
					member_sec.setText(sec<10?"0"+sec:""+sec);
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
	
	protected void onDestroy() {
		super.onDestroy();
		isStopThread=true;
		handler.removeCallbacks(runnable);
		
		((GasStationApplication) getApplication()).tempActivity.remove(MemberActivity.this);
	};
	
	/**
	 * 判断是否当前客户端可以处理类型
	 * @param value
	 * @return
	 */
	private boolean check(int value) {
		for(int i=0;i<permitValue.length;i++) {
			if(permitValue[i]==value) {
				return true;
			}
		}
		return false;
	};
	
	private void memberPrizes() {
		isLeftLoading=true;
		final Handler handlerLeftList=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.what==1) {
					Map map=(Map)msg.obj;
					if(map.get("memberDay").toString().equals("")) {
						member_day_no_layout.setVisibility(View.VISIBLE);
						member_day_start_layout.setVisibility(View.GONE);
						member_day_notstart_layout.setVisibility(View.GONE);
					}
					else if(!map.get("memberDay").toString().equals("")) {
						member_level_desp.setVisibility(View.VISIBLE);
						if(Integer.parseInt(map.get("memberLevel").toString())==0) {
							member_level_desp.setText("亲，您还不是会员！点击所选商品一键成为会员吧！");
						}
						else {
							member_level_desp.setText(Html.fromHtml("亲，您已是<font color='red'>流量"+map.get("memberLevel").toString()+"级会员</font>！可抢"+map.get("memberLevel").toString()+"级及以下商品！"));
						}
						member_day_no_layout.setVisibility(View.GONE);
						Calendar cal=Calendar.getInstance();
						String year=""+cal.get(Calendar.YEAR);
						String month=(cal.get(Calendar.MONTH)+1)<10?"0"+(cal.get(Calendar.MONTH)+1):""+(cal.get(Calendar.MONTH)+1);
						String day=cal.get(Calendar.DAY_OF_MONTH)<10?"0"+cal.get(Calendar.DAY_OF_MONTH):""+cal.get(Calendar.DAY_OF_MONTH);
						
						String year_=map.get("memberDay").toString().split("-")[0];
						String month_=map.get("memberDay").toString().split("-")[1];
						String day_=map.get("memberDay").toString().split("-")[2];
						
						Object[] memberPrizes=(Object[])map.get("memberPrizes");
						strs_left.clear();
						strs_left_before.clear();
						for(int i=0;i<memberPrizes.length;i++) {
							Map memberPrizes_map=(Map) memberPrizes[i];
							if(!check(Integer.parseInt(memberPrizes_map.get("prize_type").toString()))) {
								continue;
							}
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
							
							String endTime=map.get("memberDay").toString()+" 00:00:00";
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
				LinkedList<String> wholeUrl=Util.getWholeUrl(MemberActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(MemberActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(MemberActivity.this);
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(MemberActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map map=strategyManager.memberPrizes(Long.parseLong(list.get(0)), list.get(1));
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
	
	private void userPrizes() {
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
				LinkedList<String> wholeUrl=Util.getWholeUrl(MemberActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(MemberActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(MemberActivity.this);
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(MemberActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map[] map=strategyManager.userPrizes(Long.parseLong(list.get(0)), list.get(1));
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
	
	private void validMember(final long prize_id, final int prize_type) {
		showProgressDialog(R.string.tishi_loading);
		showCustomToastWithContext("正在提交", MemberActivity.this);
		isRightLoading=true;
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				dismissProgressDialog();
				super.handleMessage(msg);
				if(msg.what==1) {
					final ValidMemberModel model=new ValidMemberModel();
					Map map=(Map) msg.obj;
					model.setComments(map.get("comments").toString());
					model.setCost(Long.parseLong(map.get("cost").toString()));
					model.setOffer_amount(map.get("offer_amount")==null?"":map.get("offer_amount").toString());
					model.setOffer_content(map.get("offer_content").toString());
					model.setOffer_description(map.get("offer_description").toString());
					model.setOffer_id(Integer.parseInt(map.get("offer_id").toString()));
					model.setOffer_image(map.get("offer_image").toString());
					model.setOffer_name(map.get("offer_name").toString());
					model.setOffer_state(Integer.parseInt(map.get("offer_state").toString()));
					model.setOffer_type_id(Integer.parseInt(map.get("offer_type_id").toString()));
					model.setResult(Integer.parseInt(map.get("result").toString()));
					if(model.getResult()==1) {
						Intent intent=new Intent(MemberActivity.this, MemberReceiveActivity.class);
						Bundle bundle=new Bundle();
						bundle.putLong("prize_id", prize_id);
						bundle.putInt("prize_type", prize_type);
						intent.putExtras(bundle);
						startActivityForResult(intent, 700);
					}
					else {
						new AlertDialog.Builder(MemberActivity.this).setTitle("开通会员").setMessage(model.getComments()).setPositiveButton("订购", new DialogInterface.OnClickListener() {

							@Override
							public void onClick(DialogInterface arg0, int arg1) {
								// TODO Auto-generated method stub
								((GasStationApplication) getApplicationContext()).jumpJiayouFrom=34;
								Intent intent=new Intent();
								Bundle bundle=new Bundle();
								intent.setClass(MemberActivity.this, JiayouDetaiActivity.class);
								bundle.putString("offerId", ""+model.getOffer_id());
								bundle.putString("offer_name", model.getOffer_name());
								bundle.putString("offer_description", "说明："+model.getOffer_description());
								bundle.putString("type", "simple_station");
								intent.putExtras(bundle);
								startActivity(intent);
							}}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
	
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								
							}}).show();
					}
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
				LinkedList<String> wholeUrl=Util.getWholeUrl(MemberActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(MemberActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(MemberActivity.this);
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(MemberActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map map=strategyManager.validMember(Long.parseLong(list.get(0)), list.get(1), prize_id);
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
	
	public void showShare(final String seqId) {
		final Dialog dialog=new Dialog(MemberActivity.this, R.style.shareDialog);
		dialog.setCanceledOnTouchOutside(true);
		View view=LayoutInflater.from(MemberActivity.this).inflate(R.layout.view_sharelayout, null);
		TextView gonglve_share_cancel=(TextView) view.findViewById(R.id.gonglve_share_cancel);
		gonglve_share_cancel.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}});
		ImageView gonglve_qqkj_logo_share=(ImageView) view.findViewById(R.id.gonglve_qqkj_logo_share);
		gonglve_qqkj_logo_share.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				
				((GasStationApplication) getApplicationContext()).shareType=6;
				((GasStationApplication) getApplicationContext()).activityId=Integer.parseInt(getIntent().getExtras().getString("activityId"));
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(MemberActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				((GasStationApplication) getApplicationContext()).content=getIntent().getExtras().getString("activity_rule");
				Intent intent=new Intent(MemberActivity.this, QQActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("title", getIntent().getExtras().getString("activity_name"));
				bundle.putString("url", currentUsedUrl+getIntent().getExtras().getString("activity_url")+"?activityId="+getIntent().getExtras().getString("activityId")+"&seqId="+seqId);
				bundle.putString("text", getIntent().getExtras().getString("activity_rule"));
				bundle.putString("send_imageUrl", "http://a2.mzstatic.com/us/r30/Purple6/v4/98/a8/48/98a84887-be7a-9402-24ce-59284e6bf0f8/mzl.rwwplqzr.175x175-75.jpg");
				bundle.putString("type", "qqkj");
				intent.putExtras(bundle);
				startActivity(intent);
				
			}});
		ImageView gonglve_yixin_pengyou_share=(ImageView) view.findViewById(R.id.gonglve_yixin_pengyou_share);
		gonglve_yixin_pengyou_share.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				
				((GasStationApplication) getApplicationContext()).shareType=5;
				((GasStationApplication) getApplicationContext()).activityId=Integer.parseInt(getIntent().getExtras().getString("activityId"));
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(MemberActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				((GasStationApplication) getApplicationContext()).content=getIntent().getExtras().getString("activity_rule");
				SendYixin yixin=new SendYixin();
				yixin.sendYixin(MemberActivity.this , getIntent().getExtras().getString("activity_rule"), currentUsedUrl+getIntent().getExtras().getString("activity_url")+"?activityId="+getIntent().getExtras().getString("activityId")+"&seqId="+seqId, getIntent().getExtras().getString("activity_name"), R.drawable.ic_launcher, true);
				
			}});
		ImageView gonglve_weixin_pengyou_share=(ImageView) view.findViewById(R.id.gonglve_weixin_pengyou_share);
		gonglve_weixin_pengyou_share.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				
				((GasStationApplication) getApplicationContext()).shareType=3;
				((GasStationApplication) getApplicationContext()).activityId=Integer.parseInt(getIntent().getExtras().getString("activityId"));
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(MemberActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				((GasStationApplication) getApplicationContext()).content=getIntent().getExtras().getString("activity_rule");
				SendWeixin weixin=new SendWeixin();
				weixin.sendWeixin(MemberActivity.this, getIntent().getExtras().getString("activity_name")+"\n"+getIntent().getExtras().getString("activity_rule"), currentUsedUrl+getIntent().getExtras().getString("activity_url")+"?activityId="+getIntent().getExtras().getString("activityId")+"&seqId="+seqId, getIntent().getExtras().getString("activity_name")+"\n"+getIntent().getExtras().getString("activity_rule"), R.drawable.ic_launcher, true);
			}});
		ImageView gonglve_sinaweibo_logo_share=(ImageView) view.findViewById(R.id.gonglve_sinaweibo_logo_share);
		gonglve_sinaweibo_logo_share.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				
				((GasStationApplication) getApplicationContext()).shareType=4;
				((GasStationApplication) getApplicationContext()).activityId=Integer.parseInt(getIntent().getExtras().getString("activityId"));
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(MemberActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				((GasStationApplication) getApplicationContext()).content=getIntent().getExtras().getString("activity_rule");
				Intent intent=new Intent(MemberActivity.this, WBMainActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("title", getIntent().getExtras().getString("activity_name"));
				bundle.putString("url", currentUsedUrl+getIntent().getExtras().getString("activity_url")+"?activityId="+getIntent().getExtras().getString("activityId")+"&seqId="+seqId);
				bundle.putString("text", getIntent().getExtras().getString("activity_rule"));
				bundle.putString("defaultText", "流量加油站");
				intent.putExtras(bundle);
				startActivity(intent);
			}});
		dialog.setContentView(view);
		dialog.show();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_OK&&requestCode==700) {
			userPrizes();
			memberPrizes();
			if(data.getExtras().getInt("prize_type")==3||data.getExtras().getInt("prize_type")==4) {
				new AlertDialog.Builder(MemberActivity.this).setTitle("购买成功").setMessage("现在立即分享到朋友圈?").setPositiveButton("立即分享", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						showShare(data.getExtras().getString("seqId"));
					}
				}).setNegativeButton("稍后分享", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// TODO Auto-generated method stub
						
					}
				}).show();
				
			}
		}
	}

}
