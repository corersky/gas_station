package com.linkage.gas_station.gonglve;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.jiayou.JiayouMovieActivity;
import com.linkage.gas_station.model.MarketModel;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.StrategyManager;

public class Movie_New_Activity extends BaseActivity {
	
	ImageView title_back=null;
	TextView title_name=null;
	ImageView title_refresh=null;
	ProgressBar title_refresh_progress=null;
	
	LinearLayout movie_scroll_layout=null;
	TextView movie_image_name=null;
	TextView movie_cinema_name=null;
	TextView movie_cinema_num=null;
	TextView total_price=null;
	TextView movie_buy=null;
	ImageView movie_image=null;
	
	//电影院信息
	HashMap<String, ArrayList<MarketModel>> market_map=null;
	//当前选中的电影院
	String currentCinema="";
	//当前选中的场次
	int currentCinemaId=0;
	//票务信息
	LinkedHashMap<String, LinkedList<MarketModel>> ticket_map=null;
	//已经订购的票务
	ArrayList<MarketModel> ticket_order_list=null;
	boolean isLoadActivity=true;
	
	BitmapUtils bitmapUtils=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_movie_new);
		
		market_map=new HashMap<String, ArrayList<MarketModel>>();
		ticket_map=new LinkedHashMap<String, LinkedList<MarketModel>>();
		ticket_order_list=new ArrayList<MarketModel>();
		
		bitmapUtils=new BitmapUtils(this);
		bitmapUtils.configDefaultLoadingImage(R.drawable.gonglve_title_2_default);
		bitmapUtils.configDefaultLoadFailedImage(R.drawable.gonglve_title_2_default);
		
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
		title_name.setText("电影换购");
		title_refresh=(ImageView) findViewById(R.id.title_refresh);
		title_refresh.setVisibility(View.VISIBLE);
		title_refresh.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isLoadActivity) {
					showCustomToast("票务信息正在加载中，请稍后");
				}
				else {
					movie_scroll_layout.removeAllViews();
					market_map.clear();
					currentCinema="";
					currentCinemaId=0;
					ticket_map.clear();
					ticket_order_list.clear();
					total_price.setText("0元");
					movie_cinema_name.setText("");
					movie_cinema_num.setText("");
					getCinemaInfo();
				}
			}});
		title_refresh_progress=(ProgressBar) findViewById(R.id.title_refresh_progress);
		
		movie_image=(ImageView) findViewById(R.id.movie_image);
		bitmapUtils.display(movie_image, getIntent().getExtras().getString("image_name"));
		movie_cinema_name=(TextView) findViewById(R.id.movie_cinema_name);
		movie_cinema_name.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(market_map.size()>0) {
					final String[] array=new String[market_map.size()];
					Iterator<Entry<String, ArrayList<MarketModel>>> iter=market_map.entrySet().iterator();
					int pos=0;
					while(iter.hasNext()) {
						Entry<String, ArrayList<MarketModel>> entry=iter.next();
						array[pos]=entry.getKey();
						pos++;
					}
					new AlertDialog.Builder(Movie_New_Activity.this).setItems(array, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							currentCinema=array[which];
							movie_cinema_name.setText(currentCinema);
							movie_cinema_num.setText("");
							movie_scroll_layout.removeAllViews();
							ticket_order_list.clear();
							currentCinemaId=0;
							total_price.setText("0元");
						}
					}).show();
				}
			}
		});
		movie_cinema_num=(TextView) findViewById(R.id.movie_cinema_num);
		movie_cinema_num.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(!currentCinema.equals("")&&market_map.containsKey(currentCinema)) {
					final ArrayList<MarketModel> model_list=market_map.get(currentCinema);
					final String[] array=new String[model_list.size()];
					for(int i=0;i<model_list.size();i++) {
						array[i]=model_list.get(i).getAndroid_package();
					}
					new AlertDialog.Builder(Movie_New_Activity.this).setItems(array, new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							movie_cinema_num.setText(model_list.get(which).getAndroid_package());
							movie_scroll_layout.removeAllViews();
							currentCinemaId=model_list.get(which).getApp_id();
							getTicketsInfo(model_list.get(which).getApp_id());
						}
					}).show();
				}
				else {
					showCustomToast("请您先选择电影院");
				}
			}
		});
		movie_image_name=(TextView) findViewById(R.id.movie_image_name);
		movie_image_name.setText(getIntent().getExtras().getString("name"));
		movie_scroll_layout=(LinearLayout) findViewById(R.id.movie_scroll_layout);
		total_price=(TextView) findViewById(R.id.total_price);
		movie_buy=(TextView) findViewById(R.id.movie_buy);
		movie_buy.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				checkLockTickets();
			}});

		getCinemaInfo();
	}
	
	public void getCinemaInfo() {
		isLoadActivity=true;
		title_refresh.setVisibility(View.INVISIBLE);
		title_refresh_progress.setVisibility(View.VISIBLE);
		showProgressDialog(R.string.tishi_loading);
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				dismissProgressDialog();
				isLoadActivity=false;
				if(msg.what==1) {
					Object[] map_array=(Object[]) msg.obj;			
					if(map_array.length==0) {
						showCustomToast("暂无相关电影院信息");
					}
					else {
						for(int i=0;i<map_array.length;i++) {
							Map map=(Map) map_array[i];
							MarketModel model=new MarketModel();
							model.setApp_id(Integer.parseInt(map.get("id").toString()));
							model.setApp_name(map.get("cinemaname").toString());
							model.setAndroid_package(map.get("showtime").toString());
							if(market_map.containsKey(map.get("cinemaname").toString())) {
								ArrayList<MarketModel> model_list=market_map.get(map.get("cinemaname").toString());
								model_list.add(model);
								market_map.put(map.get("cinemaname").toString(), model_list);
							}
							else {
								ArrayList<MarketModel> model_list=new ArrayList<MarketModel>();
								model_list.add(model);
								market_map.put(map.get("cinemaname").toString(), model_list);
							}
						}
					}
				}
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");
				}
				else {
					showCustomToast(getResources().getString(R.string.timeout_exp));
				}
				title_refresh.setVisibility(View.VISIBLE);
				title_refresh_progress.setVisibility(View.INVISIBLE);
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(Movie_New_Activity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(Movie_New_Activity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(Movie_New_Activity.this);
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(Movie_New_Activity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map[] map=strategyManager.getCinema(Long.parseLong(list.get(0)), list.get(1), (long) getIntent().getExtras().getInt("id"), Long.parseLong(getIntent().getExtras().getString("activity_id")));
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
	
	public void getTicketsInfo(final long cinemaId) {
		isLoadActivity=true;
		title_refresh.setVisibility(View.INVISIBLE);
		title_refresh_progress.setVisibility(View.VISIBLE);
		movie_scroll_layout.removeAllViews();
		ticket_map.clear();
		ticket_order_list.clear();
		total_price.setText("0元");
		showProgressDialog(R.string.tishi_loading);
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				dismissProgressDialog();
				isLoadActivity=false;
				if(msg.what==1) {
					Object[] map_array=(Object[]) msg.obj;			
					if(map_array.length==0) {
						showCustomToast("暂无相关票务信息");
					}
					else {
						for(int i=0;i<map_array.length;i++) {
							Map map=(Map) map_array[i];
							MarketModel model=new MarketModel();
							model.setAmount(Integer.parseInt(map.get("yxt_apppric").toString()));
							model.setApp_id(Integer.parseInt(map.get("id").toString()));
							model.setSeatx(Integer.parseInt(map.get("seatx").toString()));
							model.setSeaty(Integer.parseInt(map.get("seaty").toString()));
							model.setChecked(false);
							if(ticket_map.containsKey(map.get("seatx").toString())) {
								LinkedList<MarketModel> model_ticket=ticket_map.get(map.get("seatx").toString());
								model_ticket.add(model);
								ticket_map.put(map.get("seatx").toString(), model_ticket);
							}
							else {
								LinkedList<MarketModel> model_ticket=new LinkedList<MarketModel>();
								model_ticket.add(model);
								ticket_map.put(map.get("seatx").toString(), model_ticket);
							}
						}
						Iterator<Entry<String, LinkedList<MarketModel>>> iter=ticket_map.entrySet().iterator();
						int pos=0;
						while(iter.hasNext()) {
							Entry<String, LinkedList<MarketModel>> entry=iter.next();

							View view=LayoutInflater.from(Movie_New_Activity.this).inflate(R.layout.adapter_movie_view, null);
							TextView movie_item_row=(TextView) view.findViewById(R.id.movie_item_row);
							movie_item_row.setText(""+entry.getKey()+"行");
							LinearLayout movie_item_row_scroll_layout=(LinearLayout) view.findViewById(R.id.movie_item_row_scroll_layout);
							final LinkedList<MarketModel> model_value_list=entry.getValue();
							for(int j=0;j<model_value_list.size();j++) {
								final int pos_j=j;
								View view_text=LayoutInflater.from(Movie_New_Activity.this).inflate(R.layout.adapter_movie_item_view, null);
								final ImageView movie_checked=(ImageView) view_text.findViewById(R.id.movie_checked);
								TextView movie_name=(TextView) view_text.findViewById(R.id.movie_name);
								movie_name.setText(""+model_value_list.get(j).getSeatx()+"#"+model_value_list.get(j).getSeaty());
								movie_item_row_scroll_layout.addView(view_text);
								view_text.setOnClickListener(new OnClickListener() {
				
									@Override
									public void onClick(View v) {
										// TODO Auto-generated method stub
										
										if(model_value_list.get(pos_j).isChecked()) {
											model_value_list.get(pos_j).setChecked(false);
											movie_checked.setVisibility(View.GONE);
											ticket_order_list.remove(model_value_list.get(pos_j));
										}
										else {
											if(ticket_order_list.size()>=4) {
												showCustomToast("一次购票不能超过4张");
												return ;
											}
											model_value_list.get(pos_j).setChecked(true);
											movie_checked.setVisibility(View.VISIBLE);
											ticket_order_list.add(model_value_list.get(pos_j));
										}
										System.out.println(model_value_list.get(pos_j).getSeatx()+":"+model_value_list.get(pos_j).getSeaty());
										int price=0;
										for(int k=0;k<ticket_order_list.size();k++) {
											price+=ticket_order_list.get(k).getAmount();
										}
										total_price.setText(""+price+"元");
									}});
							}
							movie_scroll_layout.addView(view);	
							pos++;
						}
						
					}
				}
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");
				}
				else {
					showCustomToast(getResources().getString(R.string.timeout_exp));
				}

				title_refresh.setVisibility(View.VISIBLE);
				title_refresh_progress.setVisibility(View.INVISIBLE);
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(Movie_New_Activity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(Movie_New_Activity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(Movie_New_Activity.this);
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(Movie_New_Activity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map[] map=strategyManager.getTickets(Long.parseLong(list.get(0)), list.get(1), cinemaId, Long.parseLong(getIntent().getExtras().getString("activity_id")));
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
	
	public void checkLockTickets() {
		isLoadActivity=true;
		title_refresh.setVisibility(View.INVISIBLE);
		title_refresh_progress.setVisibility(View.VISIBLE);
		if(ticket_order_list.size()==0) {
			showCustomToast("暂未选择任何电影票");
			title_refresh.setVisibility(View.VISIBLE);
			title_refresh_progress.setVisibility(View.INVISIBLE);
			return;
		}
		showProgressDialog(R.string.tishi_loading);
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				dismissProgressDialog();
				isLoadActivity=false;
				if(msg.what==1) {
					Map map=(Map) msg.obj;			
					if(map.get("result").toString().equals("1")) {
						Intent intent=new Intent(Movie_New_Activity.this, JiayouMovieActivity.class);
						Bundle bundle=new Bundle();
						String seatXs="";
						String seatYs="";
						String ids="";
						int price=0;
						for(int i=0;i<ticket_order_list.size();i++) {
							if(i==ticket_order_list.size()-1) {
								seatXs+=(""+ticket_order_list.get(i).getSeatx());
								seatYs+=(""+ticket_order_list.get(i).getSeaty());
								ids+=(""+ticket_order_list.get(i).getApp_id());
							}
							else {
								seatXs+=(""+ticket_order_list.get(i).getSeatx()+",");
								seatYs+=(""+ticket_order_list.get(i).getSeaty()+",");
								ids+=(""+ticket_order_list.get(i).getApp_id()+",");
							}
							price+=ticket_order_list.get(i).getAmount();
						}
						bundle.putString("seatXs", seatXs);
						bundle.putString("seatYs", seatYs);
						bundle.putString("ids", ids);
						bundle.putInt("price", price);
						bundle.putString("activity_id", getIntent().getExtras().getString("activity_id"));
						intent.putExtras(bundle);
						startActivity(intent);
						finish();
					}
					else if(map.get("result").toString().equals("4")) {
						showCustomToast(map.get("comments").toString());
						getTicketsInfo(currentCinemaId);
					}					
				}
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");
				}
				else {
					showCustomToast(getResources().getString(R.string.timeout_exp));
				}

				title_refresh.setVisibility(View.VISIBLE);
				title_refresh_progress.setVisibility(View.INVISIBLE);
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(Movie_New_Activity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(Movie_New_Activity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(Movie_New_Activity.this);
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(Movie_New_Activity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						String ids="";
						for(int i=0;i<ticket_order_list.size();i++) {
							if(i==ticket_order_list.size()-1) {
								ids+=""+ticket_order_list.get(i).getApp_id();
							}
							else {
								ids+=""+ticket_order_list.get(i).getApp_id()+",";
							}
						}
						Map map=strategyManager.lockTickets(Long.parseLong(list.get(0)), list.get(1), ids, Long.parseLong(getIntent().getExtras().getString("activity_id")));
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
}
