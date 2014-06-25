package com.linkage.gas_station.gonglve;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.StrategyManager;

public class MovieOrderDetailActivity extends BaseActivity {
	
	TextView title_name=null;
	ImageView title_back=null;
	
	TextView movie_detail_name=null;
	TextView movie_detail_cinema_name=null;
	TextView movie_detail_cinema_time=null;
	TextView movie_detail_pos=null;
	TextView movie_detail_ticket_name=null;
	TextView movie_detail_pass=null;
	TextView movie_detail_price=null;
	TextView movie_detail_time=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_movieorderdetail);
		
		((GasStationApplication) getApplication()).tempActivity.add(MovieOrderDetailActivity.this);
		
		init();
	}
	
	public void init() {
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText("电影票详情");
		title_back=(ImageView) findViewById(R.id.title_back);
		title_back.setVisibility(View.VISIBLE);
		title_back.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
		
		movie_detail_name=(TextView) findViewById(R.id.movie_detail_name);
		movie_detail_cinema_name=(TextView) findViewById(R.id.movie_detail_cinema_name);
		movie_detail_cinema_time=(TextView) findViewById(R.id.movie_detail_cinema_time);
		movie_detail_pos=(TextView) findViewById(R.id.movie_detail_pos);
		movie_detail_ticket_name=(TextView) findViewById(R.id.movie_detail_ticket_name);
		movie_detail_pass=(TextView) findViewById(R.id.movie_detail_pass);
		movie_detail_price=(TextView) findViewById(R.id.movie_detail_price);
		movie_detail_time=(TextView) findViewById(R.id.movie_detail_time);
		
		getMovieOrderDetailInfo();
	}
	
	public void getMovieOrderDetailInfo() {
		showProgressDialog(R.string.tishi_loading);
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				dismissProgressDialog();
				if(msg.what==1) {
					Map map=(Map) msg.obj;
					movie_detail_name.setText(map.get("yxt_filmname").toString());
					movie_detail_cinema_name.setText(map.get("yxt_cinemaname").toString());
					movie_detail_cinema_time.setText(map.get("showtime").toString());
					movie_detail_pos.setText(map.get("seatx").toString()+"排"+map.get("seaty").toString()+"号");
					movie_detail_ticket_name.setText(map.get("yxt_ticket_num").toString());
					movie_detail_pass.setText(map.get("yxt_ticket_pwd").toString());
					movie_detail_price.setText(map.get("yxt_apppric").toString()+"元");
					movie_detail_time.setText(map.get("paytime").toString());
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
				LinkedList<String> wholeUrl=Util.getWholeUrl(MovieOrderDetailActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(MovieOrderDetailActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(MovieOrderDetailActivity.this);
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(MovieOrderDetailActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map map=strategyManager.movieOrderInfo(Long.parseLong(list.get(0)), list.get(1), Long.parseLong(getIntent().getExtras().getString("ticketId")));
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
		((GasStationApplication) getApplication()).tempActivity.remove(MovieOrderDetailActivity.this);
	}

}
