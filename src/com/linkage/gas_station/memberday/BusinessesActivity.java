package com.linkage.gas_station.memberday;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Map;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.model.MemberBuyModel;
import com.linkage.gas_station.myview.XListView;
import com.linkage.gas_station.myview.XListView.IXListViewListener;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.StrategyManager;

public class BusinessesActivity extends BaseActivity {
	
	TextView title_name=null;
	ImageView title_back=null;
	
	TextView business_left_tab=null;
	TextView business_right_tab=null;
	
	LinearLayout bussiness_show_layout=null;
	LinearLayout bussiness_no_layout=null;
	LinearLayout right_business_layout=null;
	LinearLayout left_business_layout=null;
	ImageView business_arrow_left=null;
	TextView business_month=null;
	ImageView business_arrow_right=null;
	XListView right_business_listview=null;
	BussinessAdapter adapter=null;
	TextView business_showcode_name=null;
	TextView business_showcode_code=null;
	TextView bussiness_commit=null;
	TextView right_business_total=null;
	
	ArrayList<MemberBuyModel> str=null;
	//是否正在加载
	boolean isLoading=false;
	//时间终结点
	String begin_limit="2014-06";
	String end_limit="";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_businesses);
		
		((GasStationApplication) getApplication()).tempActivity.add(BusinessesActivity.this);
		
		str=new ArrayList<MemberBuyModel>();
		
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
		
		business_left_tab=(TextView) findViewById(R.id.business_left_tab);
		business_left_tab.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(BusinessesActivity.this, CaptureActivity.class);
				startActivityForResult(intent, 800);
				
			}});
		business_right_tab=(TextView) findViewById(R.id.business_right_tab);
		business_right_tab.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				
				business_right_tab.setTextColor(getResources().getColor(R.color.member_title_choice_color));
				business_left_tab.setTextColor(getResources().getColor(R.color.member_title_normal_color));
				
				business_right_tab.setBackgroundResource(R.drawable.member_title_on);
				business_left_tab.setBackgroundResource(R.drawable.member_title_off);
				
				Drawable member_left_off=getResources().getDrawable(R.drawable.bussiness_scancode_off);
				member_left_off.setBounds(Util.dip2px(BusinessesActivity.this, 10), 0, member_left_off.getMinimumWidth()+Util.dip2px(BusinessesActivity.this, 10), member_left_off.getMinimumHeight());				
				business_left_tab.setCompoundDrawables(member_left_off, null, null, null);
				
				Drawable member_right_on=getResources().getDrawable(R.drawable.bussiness_on);
				member_right_on.setBounds(Util.dip2px(BusinessesActivity.this, 10), 0, member_right_on.getMinimumWidth()+Util.dip2px(BusinessesActivity.this, 10), member_right_on.getMinimumHeight());				
				business_right_tab.setCompoundDrawables(member_right_on, null, null, null);
				
				right_business_layout.setVisibility(View.VISIBLE);
				left_business_layout.setVisibility(View.GONE);
				
			}});
		
		bussiness_show_layout=(LinearLayout) findViewById(R.id.bussiness_show_layout);
		bussiness_no_layout=(LinearLayout) findViewById(R.id.bussiness_no_layout);
		right_business_layout=(LinearLayout) findViewById(R.id.right_business_layout);
		left_business_layout=(LinearLayout) findViewById(R.id.left_business_layout);
		business_arrow_left=(ImageView) findViewById(R.id.business_arrow_left);
		business_arrow_left.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				business_month.setText(minCurrentTime()); 
				right_business_listview.setStart();
				memberPrizes(true);
				
				if(begin_limit.equals(business_month.getText().toString())) {
					business_arrow_left.setVisibility(View.GONE);
				}
				else {
					business_arrow_left.setVisibility(View.VISIBLE);
				}
				if(end_limit.equals(business_month.getText().toString())) {
					business_arrow_right.setVisibility(View.GONE);
				}
				else {
					business_arrow_right.setVisibility(View.VISIBLE);
				}
			}});
		business_month=(TextView) findViewById(R.id.business_month);
		end_limit=currentTime();
		business_month.setText(end_limit);
		business_arrow_right=(ImageView) findViewById(R.id.business_arrow_right);
		business_arrow_right.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				business_month.setText(addCurrentTime());
				right_business_listview.setStart();
				memberPrizes(true);
				
				if(begin_limit.equals(business_month.getText().toString())) {
					business_arrow_left.setVisibility(View.GONE);
				}
				else {
					business_arrow_left.setVisibility(View.VISIBLE);
				}
				if(end_limit.equals(business_month.getText().toString())) {
					business_arrow_right.setVisibility(View.GONE);
				}
				else {
					business_arrow_right.setVisibility(View.VISIBLE);
				}
			}});
		right_business_listview=(XListView) findViewById(R.id.right_business_listview);
		adapter=new BussinessAdapter(str, BusinessesActivity.this);
		right_business_listview.setPullLoadEnable(false);
		right_business_listview.setXListViewListener(new IXListViewListener() {
			
			@Override
			public void onRefresh() {
				// TODO Auto-generated method stub
				if(isLoading) {
					return ;
				}
				memberPrizes(true);
			}
			
			@Override
			public void onLoadMore() {
				// TODO Auto-generated method stub
				if(isLoading) {
					return ;
				}
				memberPrizes(false);
			}
		});
		right_business_listview.setRefreshTime(getTime());
		right_business_listview.setAdapter(adapter);
		business_showcode_name=(TextView) findViewById(R.id.business_showcode_name);
		business_showcode_code=(TextView) findViewById(R.id.business_showcode_code);
		if(begin_limit.equals(currentTime())) {
			business_arrow_left.setVisibility(View.GONE);
		}
		else {
			business_arrow_left.setVisibility(View.VISIBLE);
		}
		if(end_limit.equals(currentTime())) {
			business_arrow_right.setVisibility(View.GONE);
		}
		else {
			business_arrow_right.setVisibility(View.VISIBLE);
		}
		bussiness_commit=(TextView) findViewById(R.id.bussiness_commit);
		bussiness_commit.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				supplyerScan();
			}});
		right_business_total=(TextView) findViewById(R.id.right_business_total);
		validSupplyer();
	}
	
	private String currentTime() {
		Calendar cal=Calendar.getInstance();
		int year=cal.get(Calendar.YEAR);
		int month=cal.get(Calendar.MONTH)+1;
		return month<10?year+"-"+"0"+month:year+"-"+month;
	}
	
	private String addCurrentTime() {
		String time=business_month.getText().toString();
		int year=Integer.parseInt(time.split("-")[0]);
		int month=Integer.parseInt(time.split("-")[1]);
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date date=format.parse(year+"-"+month+"-"+"01");
			Calendar calendar=Calendar.getInstance();
			calendar.setTime(date);		
			calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)+1);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			int year_=calendar.get(Calendar.YEAR);
			int month_=calendar.get(Calendar.MONTH)+1;
			return month_<10?year_+"-"+"0"+month_:year_+"-"+month_;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	private String minCurrentTime() {
		String time=business_month.getText().toString();
		int year=Integer.parseInt(time.split("-")[0]);
		int month=Integer.parseInt(time.split("-")[1]);
		SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd");
		
		try {
			Date date=format.parse(year+"-"+month+"-"+"01");
			Calendar calendar=Calendar.getInstance();
			calendar.setTime(date);		
			calendar.set(Calendar.MONTH, calendar.get(Calendar.MONTH)-1);
			calendar.set(Calendar.DAY_OF_MONTH, 1);
			int year_=calendar.get(Calendar.YEAR);
			int month_=calendar.get(Calendar.MONTH)+1;
			return month_<10?year_+"-"+"0"+month_:year_+"-"+month_;
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}
	
	private String getTime() {
        return new SimpleDateFormat("MM-dd HH:mm", Locale.CHINA).format(new Date());
    }
	
	private void validSupplyer() {
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.what==1) {
					Map map=(Map) msg.obj;
					if(map.get("result").toString().equals("1")) {
						memberPrizes(true);
						bussiness_show_layout.setVisibility(View.VISIBLE);
						bussiness_no_layout.setVisibility(View.GONE);
					}
					else {
						bussiness_show_layout.setVisibility(View.GONE);
						bussiness_no_layout.setVisibility(View.VISIBLE);
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
				LinkedList<String> wholeUrl=Util.getWholeUrl(BusinessesActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(BusinessesActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(BusinessesActivity.this);
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(BusinessesActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map map=strategyManager.validSupplyer(Long.parseLong(list.get(0)), list.get(1));
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
	
	private void memberPrizes(final boolean isRefresh) {
		business_arrow_right.setClickable(false);
		business_arrow_right.setEnabled(false);
		business_arrow_left.setClickable(false);
		business_arrow_left.setEnabled(false);
		isLoading=true;
		final Handler handlerLeftList=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.what==1) {
					if(isRefresh) {
						str.clear();
					}
					Object[] obj=(Object[]) msg.obj;
					int total=0;
					for(int i=0;i<obj.length;i++) {
						Map map=(Map)obj[i];
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
						model.setRn(map.get("rn")!=null?Integer.parseInt(map.get("rn").toString()):0);
						model.setScan_time(map.get("scan_time")!=null?map.get("scan_time").toString():"");
						total=map.get("total")!=null?Integer.parseInt(map.get("total").toString()):0;
						str.add(model);
					}
					right_business_total.setText(""+total);
					adapter.notifyDataSetChanged();
					if(obj.length==10) {
						right_business_listview.setPullLoadEnable(true);
					}
					else {
						right_business_listview.setPullLoadEnable(false);
					}		
				}
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");	
				}
				else {
					showCustomToast(getResources().getString(R.string.timeout_exp));
				}
				onLoad();
				isLoading=false;
				business_arrow_right.setClickable(true);
				business_arrow_right.setEnabled(true);
				business_arrow_left.setClickable(true);
				business_arrow_left.setEnabled(true);
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(BusinessesActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(BusinessesActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(BusinessesActivity.this);
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(BusinessesActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map[] map=strategyManager.supplyerScanList(Long.parseLong(list.get(0)), list.get(1), business_month.getText().toString(), 0, 10);
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
	
	private void supplyerScan() {
		final Handler handlerLeftList=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.what==1) {
					Map map=(Map) msg.obj;
					if(map.get("result").toString().equals("1")) {
						right_business_layout.setVisibility(View.VISIBLE);
						left_business_layout.setVisibility(View.GONE);
						business_month.setText(currentTime());
						memberPrizes(true);
					}
					else {
						showCustomToast(map.get("comments").toString());
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
				LinkedList<String> wholeUrl=Util.getWholeUrl(BusinessesActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(BusinessesActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(BusinessesActivity.this);
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(BusinessesActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map map=strategyManager.supplyerScan(Long.parseLong(list.get(0)), list.get(1), business_showcode_code.getText().toString());
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
	
	private void onLoad() {
		right_business_listview.stopRefresh();
		right_business_listview.stopLoadMore();
		right_business_listview.setRefreshTime(getTime());
    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_OK&&requestCode==800) {
			
			business_left_tab.setTextColor(getResources().getColor(R.color.member_title_choice_color));
			business_right_tab.setTextColor(getResources().getColor(R.color.member_title_normal_color));
			
			business_left_tab.setBackgroundResource(R.drawable.member_title_on);
			business_right_tab.setBackgroundResource(R.drawable.member_title_off);
			
			Drawable member_left_on=getResources().getDrawable(R.drawable.bussiness_scancode_on);
			member_left_on.setBounds(Util.dip2px(BusinessesActivity.this, 10), 0, member_left_on.getMinimumWidth()+Util.dip2px(BusinessesActivity.this, 10), member_left_on.getMinimumHeight());				
			business_left_tab.setCompoundDrawables(member_left_on, null, null, null);
			
			Drawable member_right_off=getResources().getDrawable(R.drawable.bussiness_off);
			member_right_off.setBounds(Util.dip2px(BusinessesActivity.this, 10), 0, member_right_off.getMinimumWidth()+Util.dip2px(BusinessesActivity.this, 10), member_right_off.getMinimumHeight());				
			business_right_tab.setCompoundDrawables(member_right_off, null, null, null);
			
			right_business_layout.setVisibility(View.GONE);
			left_business_layout.setVisibility(View.VISIBLE);
			String result=data.getExtras().getString("code");
			System.out.println(result);
			if(result.indexOf("!#$&")!=-1) {
				business_showcode_name.setText(result.substring(0, result.indexOf("!#$&")));
				business_showcode_code.setText(result.substring(result.indexOf("!#$&")+4, result.length()));
			}			
		}
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		((GasStationApplication) getApplication()).tempActivity.remove(BusinessesActivity.this);
	}
}
