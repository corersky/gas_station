package com.linkage.gas_station.gonglve;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.market.GridAdapter;
import com.linkage.gas_station.model.MarketModel;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.StrategyManager;

public class Movie_Choice_Activity extends BaseActivity {
	
	ImageView title_back=null;
	TextView title_name=null;
	ImageView title_refresh=null;
	ProgressBar title_refresh_progress=null;
	private GridView movie_gridview;
	private GridAdapter adapter;
	TextView order_detail=null;
	
	private ArrayList<MarketModel> movie_list=null;
	boolean isLoadActivity=true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_movie_choice);
		
		((GasStationApplication) getApplication()).tempActivity.add(Movie_Choice_Activity.this);
		
		movie_list=new ArrayList<MarketModel>();
		
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
		title_refresh=(ImageView) findViewById(R.id.title_refresh);
		title_refresh.setVisibility(View.VISIBLE);
		title_refresh.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(isLoadActivity) {
					showCustomToast("电影信息正在加载中，请稍后");
				}
				else {
					getAllMovies();
				}
			}
			
		});
		title_refresh_progress=(ProgressBar) findViewById(R.id.title_refresh_progress);
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText("电影");
		
		movie_gridview=(GridView) findViewById(R.id.movie_gridview);
		movie_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(Movie_Choice_Activity.this, Movie_New_Activity.class);
				Bundle bundle=new Bundle();
				bundle.putString("image_name", movie_list.get(position).getApp_image_name());
				bundle.putString("name", movie_list.get(position).getApp_name());
				bundle.putInt("id", movie_list.get(position).getApp_id());
				bundle.putString("activity_id", getIntent().getExtras().getString("activity_id"));
				intent.putExtras(bundle);
				startActivity(intent);
			}
		});
		adapter=new GridAdapter(this);
		order_detail=(TextView) findViewById(R.id.order_detail);
		order_detail.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(Movie_Choice_Activity.this, MovieOrderActivity.class);
				startActivity(intent);
			}});
		
		getAllMovies();
	}
	
	/**
	 * 获取全部电影信息
	 */
	public void getAllMovies() {
		isLoadActivity=true;
		title_refresh.setVisibility(View.INVISIBLE);
		title_refresh_progress.setVisibility(View.VISIBLE);
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if(msg.what==1) {
					Map map=(Map) msg.obj;			
					if(map.get("result").toString().equals("1")) {
						movie_list.clear();
						Object[] obj_map=(Object[]) map.get("data");
						for(int i=0;i<obj_map.length;i++) {
							Map data_map=(Map) obj_map[i];
							MarketModel model=new MarketModel();
							model.setApp_id(Integer.parseInt(data_map.get("film_id").toString()));
							model.setApp_image_name(((GasStationApplication) getApplication()).AreaUrl+data_map.get("film_name_image").toString());
							model.setApp_name(data_map.get("film_name").toString());
							movie_list.add(model);
						}
						adapter.setList(movie_list);
						movie_gridview.setAdapter(adapter);
						adapter.notifyDataSetChanged();
					}
					else if(map.get("result").toString().equals("4")) {
						showCustomToast(map.get("comments").toString());
					}
				}
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");
				}
				else {
					showCustomToast(getResources().getString(R.string.timeout_exp));
				}
				isLoadActivity=false;
				title_refresh.setVisibility(View.VISIBLE);
				title_refresh_progress.setVisibility(View.INVISIBLE);
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(Movie_Choice_Activity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(Movie_Choice_Activity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(Movie_Choice_Activity.this);
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(Movie_Choice_Activity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map map=strategyManager.getMovies(Long.parseLong(list.get(0)), list.get(1), Long.parseLong(getIntent().getExtras().getString("activity_id")));
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
		((GasStationApplication) getApplication()).tempActivity.remove(Movie_Choice_Activity.this);
	}

}
