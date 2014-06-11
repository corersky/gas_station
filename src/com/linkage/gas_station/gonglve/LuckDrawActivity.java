package com.linkage.gas_station.gonglve;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.lidroid.xutils.BitmapUtils;
import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.main.MainActivity;
import com.linkage.gas_station.myview.Circleview;
import com.linkage.gas_station.myview.Circleview.OnFinishListener;
import com.linkage.gas_station.oil_treasure.TreasurePullRichDetailActivity;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.LotteryManager;
import com.linkage.gas_station.util.hessian.StrategyManager;

public class LuckDrawActivity extends BaseActivity {
	
	ImageView share_lottery_back=null;
	TextView share_lottery_my=null;
	TextView share_lottery_notice=null;
	TextView share_lottery_detail=null;
	ScrollView share_lottery_my_layout=null;
	LinearLayout share_lottery_notice_layout=null;
	LinearLayout share_lottery_detail_layout=null;
	LinearLayout share_lottery_buy=null;
	ImageView share_lottery_arrow_left=null;
	ImageView share_lottery_arrow_right=null;
	TextView share_lottery_month=null;
	Button begin_btn=null;
	TextView luck_draw_total_num=null;
	TextView luck_draw_last_num=null;
	Circleview claert=null;
	ImageView lottery_view=null;
	TextView luckdraw_desp=null;
	
	TextView share_lottery_notice_my_address=null;
	ListView share_lottery_notice_whole_list=null;
	ListView share_lottery_notice_my_list=null;
	LuckDrawAdapter adapter_whole=null;
	SimpleAdapter adapter_my=null;
	ArrayList<String> strArrays_whole=null;
	ArrayList<HashMap<String, Object>> strArrays_my=null;
	LinearLayout share_lottery_notice_my_list_no=null;
	LinearLayout share_lottery_first_bg=null;
	
	TextView share_lottery_desp=null;
	
	//当前加载最大数
	int maxNum=0;
	//第二屏加载完成过
	boolean isFinish2=false;
	//抽奖结果
	Map map_result=null;
	
	BitmapUtils bitmapUtils=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_luckdraw);
		
		strArrays_whole=new ArrayList<String>();
		strArrays_my=new ArrayList<HashMap<String, Object>>();
		maxNum=getCurrentMonth();
		
		bitmapUtils=new BitmapUtils(this);
		
		init();
	}
	
	@SuppressWarnings("deprecation")
	private void init() {
				
		share_lottery_back=(ImageView) findViewById(R.id.share_lottery_back);
		share_lottery_back.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				finish();
			}});
		share_lottery_my=(TextView) findViewById(R.id.share_lottery_my);
		share_lottery_my.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				share_lottery_my.setBackgroundResource(R.drawable.share_lottery_menu_selected);
				share_lottery_notice.setBackgroundColor(Color.TRANSPARENT);
				share_lottery_detail.setBackgroundColor(Color.TRANSPARENT);
				share_lottery_my_layout.setVisibility(View.VISIBLE);
				share_lottery_notice_layout.setVisibility(View.GONE);
				share_lottery_detail_layout.setVisibility(View.GONE);
			}});
		share_lottery_notice=(TextView) findViewById(R.id.share_lottery_notice);
		share_lottery_notice.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				share_lottery_my.setBackgroundColor(Color.TRANSPARENT);
				share_lottery_notice.setBackgroundResource(R.drawable.share_lottery_menu_selected);
				share_lottery_detail.setBackgroundColor(Color.TRANSPARENT);
				share_lottery_my_layout.setVisibility(View.GONE);
				share_lottery_notice_layout.setVisibility(View.VISIBLE);
				share_lottery_detail_layout.setVisibility(View.GONE);
				if(!isFinish2) {
					loadWholeList();
					getLotteryInfo();
				}
			}});
		share_lottery_detail=(TextView) findViewById(R.id.share_lottery_detail);
		share_lottery_detail.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				share_lottery_my.setBackgroundColor(Color.TRANSPARENT);
				share_lottery_notice.setBackgroundColor(Color.TRANSPARENT);
				share_lottery_detail.setBackgroundResource(R.drawable.share_lottery_menu_selected);
				share_lottery_my_layout.setVisibility(View.GONE);
				share_lottery_notice_layout.setVisibility(View.GONE);
				share_lottery_detail_layout.setVisibility(View.VISIBLE);				
			}});
		share_lottery_my_layout=(ScrollView) findViewById(R.id.share_lottery_my_layout);
		share_lottery_notice_layout=(LinearLayout) findViewById(R.id.share_lottery_notice_layout);
		bitmapUtils.display(share_lottery_notice_layout, "assets/share_lottery_detail_bg.jpg");
		share_lottery_detail_layout=(LinearLayout) findViewById(R.id.share_lottery_detail_layout);
		bitmapUtils.display(share_lottery_detail_layout, "assets/share_lottery_detail_bg.jpg");
		share_lottery_first_bg=(LinearLayout) findViewById(R.id.share_lottery_first_bg);		
		if(Util.getUserArea(LuckDrawActivity.this).equals("0971")) {
			bitmapUtils.display(share_lottery_first_bg, "assets/share_lottery_bg_qh.jpg");
	    }
	    else {
	    	bitmapUtils.display(share_lottery_first_bg, "assets/share_lottery_bg_js.jpg");
	    }
		
		FrameLayout layout=(FrameLayout) findViewById(R.id.Lottery);
	    int screnWidth=getWindowManager().getDefaultDisplay().getWidth();
	    
	    claert=new Circleview(this,screnWidth);
	    claert.setOnFinishListener(new OnFinishListener() {
			
			@Override
			public void changeState() {
				// TODO Auto-generated method stub
				hander_refresh.sendEmptyMessage(0);
			}
		});
	    layout.addView(claert,new LayoutParams(LayoutParams.FILL_PARENT, Util.dip2px(this, 300)));
	    
	    lottery_view=(ImageView) findViewById(R.id.lottery_view);
	    if(Util.getUserArea(LuckDrawActivity.this).equals("0971")) {
	    	lottery_view.setImageResource(R.drawable.share_lottery);
	    }
	    else {
	    	lottery_view.setImageResource(R.drawable.share_lottery_js);
	    }
	    luck_draw_total_num=(TextView) findViewById(R.id.luck_draw_total_num);
	    luck_draw_last_num=(TextView) findViewById(R.id.luck_draw_last_num);
	    begin_btn=(Button) findViewById(R.id.begin_btn);
	    begin_btn.setOnClickListener(new Button.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				drawLottery();
				begin_btn.setEnabled(false);
				begin_btn.setClickable(false);
				begin_btn.setText("正在抽奖中...");
			}});
	    
	    share_lottery_buy=(LinearLayout) findViewById(R.id.share_lottery_buy);
	    share_lottery_buy.setOnClickListener(new LinearLayout.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				((GasStationApplication) getApplicationContext()).jumpJiayouFrom=(int) getIntent().getExtras().getLong("activityId");
				finish();
				MainActivity.getInstance().jumpToJiayou(0, 4);
			}});
	    luckdraw_desp=(TextView) findViewById(R.id.luckdraw_desp);
	    if(Util.getUserArea(LuckDrawActivity.this).equals("0971")) {
	    	luckdraw_desp.setText("购买10元以上流量包可增加一次抽奖机会");
	    }
	    else {
	    	luckdraw_desp.setText("购买20元以上流量包可增加三次抽奖机会");
	    }
	    
	    share_lottery_arrow_left=(ImageView) findViewById(R.id.share_lottery_arrow_left);
	    share_lottery_arrow_left.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(getCurrentShowMonth()<=4) {
					return;
				}
				if(isFinish2) {
					return;
				}
				int showMonth=getCurrentShowMonth()-1;
				setCurrentShowMonth(showMonth);
				if(showMonth<=4) {
					share_lottery_arrow_left.setVisibility(View.INVISIBLE);
				}
				else {
					share_lottery_arrow_left.setVisibility(View.VISIBLE);
				}
				if(showMonth<maxNum) {
					share_lottery_arrow_right.setVisibility(View.VISIBLE);
				}
				else {
					share_lottery_arrow_right.setVisibility(View.INVISIBLE);
				}
				loadWholeList();
				getLotteryInfo();
			}});
	    share_lottery_arrow_right=(ImageView) findViewById(R.id.share_lottery_arrow_right);
	    share_lottery_arrow_right.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(getCurrentShowMonth()>=maxNum) {
					return;
				}
				if(isFinish2) {
					return;
				}
				int showMonth=getCurrentShowMonth()+1;
				setCurrentShowMonth(showMonth);
				if(showMonth>4) {
					share_lottery_arrow_left.setVisibility(View.VISIBLE);
				}
				else {
					share_lottery_arrow_left.setVisibility(View.INVISIBLE);
				}
				if(showMonth>=maxNum) {
					share_lottery_arrow_right.setVisibility(View.INVISIBLE);
				}
				else {
					share_lottery_arrow_right.setVisibility(View.VISIBLE);
				}
				loadWholeList();
				getLotteryInfo();
			}});
	    share_lottery_month=(TextView) findViewById(R.id.share_lottery_month);
	    if(getCurrentMonth()>4) {
	    	share_lottery_arrow_left.setVisibility(View.VISIBLE);
	    }
	    else {
	    	share_lottery_arrow_left.setVisibility(View.INVISIBLE);
	    }
	    share_lottery_arrow_right.setVisibility(View.INVISIBLE);
	    setCurrentShowMonth(getCurrentMonth());
	    
	    share_lottery_notice_whole_list=(ListView) findViewById(R.id.share_lottery_notice_whole_list);
	    adapter_whole=new LuckDrawAdapter(strArrays_whole, LuckDrawActivity.this);
	    share_lottery_notice_whole_list.setAdapter(adapter_whole);
	    share_lottery_notice_my_address=(TextView) findViewById(R.id.share_lottery_notice_my_address);
	    if(Util.getUserArea(LuckDrawActivity.this).equals("0971")) {
	    	share_lottery_notice_my_address.setVisibility(View.VISIBLE);
	    }
	    else {
	    	share_lottery_notice_my_address.setVisibility(View.GONE);
	    }
	    share_lottery_notice_my_address.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(LuckDrawActivity.this, LuckDrawAddressActivity.class);
				startActivity(intent);
			}});
	    
	    share_lottery_notice_my_list=(ListView) findViewById(R.id.share_lottery_notice_my_list);
	    adapter_my=new SimpleAdapter(LuckDrawActivity.this, strArrays_my, R.layout.adapter_share_lottery_notice_my_list, new String[]{"myInfo"}, new int[]{R.id.notice_title_my_info});
	    share_lottery_notice_my_list.setAdapter(adapter_my);
	    share_lottery_notice_my_list_no=(LinearLayout) findViewById(R.id.share_lottery_notice_my_list_no);
	    
	    share_lottery_desp=(TextView) findViewById(R.id.share_lottery_desp);
	    share_lottery_desp.setText(getIntent().getExtras().getString("desp"));
	    
	    getLotteryCondition();
	}
	
	private int getCurrentMonth() {
		Calendar cal=Calendar.getInstance();
		return cal.get(Calendar.MONTH)+1;
	}
	
	private int getCurrentShowMonth() {
		String month=share_lottery_month.getText().toString().substring(share_lottery_month.getText().toString().indexOf("年")+1, share_lottery_month.getText().toString().indexOf("月"));
		return Integer.parseInt(month);
	}
	
	private void setCurrentShowMonth(int num) {
		if(num>=10) {
			share_lottery_month.setText("2014年"+num+"月");
		}
		else {
			share_lottery_month.setText("2014年0"+num+"月");
		}
	}
	
	/**
	 * 获取特等奖，一，二，三....等奖的中奖信息
	 */
	private void loadWholeList() {
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.what==1) {
					Object[] obj_array=(Object[]) msg.obj;
					strArrays_whole.clear();
					for(int w=0;w<obj_array.length;w++) {
						Map map=(Map) obj_array[w];
						ArrayList<String> array_temp=new ArrayList<String>();
						
						if(map.get("level_id").toString().equals("0")||map.get("level_id").toString().equals("1")||map.get("level_id").toString().equals("2")||map.get("level_id").toString().equals("3")) {
							array_temp.add("1&"+map.get("level_name").toString()+"&"+map.get("prize_name").toString()+"\n(总量:"+(Integer.parseInt(map.get("cnt").toString())+Integer.parseInt(map.get("left_cnt").toString()))+"，剩余:"+map.get("left_cnt").toString()+")");
							if(map.get("prize_info")!=null) {
								String[] array=(String[]) map.get("prize_info");
								int totalNum=array.length%3==0?array.length/3:array.length/3+1;
								for(int i=0;i<totalNum;i++) {
									String temp="2&";
									for(int j=0;j<3;j++) {
										if(j==0) {
											temp+=array[i*3]+"  ";
										}
										else {
											if(i*3+j<array.length) {
												temp+=array[i*3+j]+"  ";
											}
										}
									}
									array_temp.add(temp);
								}
							}							
						}
						else {
							array_temp.add("1&"+map.get("level_name").toString()+"&"+map.get("prize_name").toString()+"\n(总量:"+(Integer.parseInt(map.get("cnt").toString())+Integer.parseInt(map.get("left_cnt").toString()))+"，剩余:"+map.get("left_cnt").toString()+")");
						}
						strArrays_whole.addAll(array_temp);
					}
					adapter_whole.notifyDataSetChanged();
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
				LinkedList<String> wholeUrl=Util.getWholeUrl(LuckDrawActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(LuckDrawActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(LuckDrawActivity.this);					
						int month=getCurrentShowMonth();
						String temp="2014-"+(month<10?"0"+month:""+month);
						Map[] map=null;
						if(Util.getUserArea(LuckDrawActivity.this).equals("0971")) {
							LotteryManager strategyManager=GetWebDate.getHessionFactiory(LuckDrawActivity.this).create(LotteryManager.class, currentUsedUrl+"/hessian/lotteryManager", getClassLoader());
							map=strategyManager.getLotteryList(temp);
						}
						else {
							StrategyManager strategyManager=GetWebDate.getHessionFactiory(LuckDrawActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
							map=strategyManager.getLotteryList(temp, getIntent().getExtras().getLong("activityId"));
						}
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
	
	Handler hander_refresh=new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);

			begin_btn.setEnabled(true);
			begin_btn.setClickable(true);
			begin_btn.setText("开始抽奖");
			luck_draw_total_num.setText(map_result.get("lottery_totalcnt").toString());
			luck_draw_last_num.setText(map_result.get("lottery_cnt").toString());
			
			final String result=map_result.get("comments").toString(); 
			String title="确定";
			if(Util.getUserArea(LuckDrawActivity.this).equals("0971")&&Integer.parseInt(map_result.get("level_id").toString())<=3) {
				title="分享";
			}
			new AlertDialog.Builder(LuckDrawActivity.this).setTitle("奖品信息").setMessage(result).setPositiveButton(title, new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					if(Util.getUserArea(LuckDrawActivity.this).equals("0971")&&Integer.parseInt(map_result.get("level_id").toString())<=3) {
						Intent intent=new Intent(LuckDrawActivity.this, TreasurePullRichDetailActivity.class);
						Bundle bundle=new Bundle();
						bundle.putString("title", getIntent().getExtras().getString("activityName"));
						bundle.putString("pull_rich_title", getIntent().getExtras().getString("activityName"));
						bundle.putString("pull_rich_message", result);
						bundle.putString("url", "http://www.lljyz.cn");
						intent.putExtras(bundle);
						startActivity(intent);
					}
				}
			}).show();
			
		}
	};
	
	/**
	 * 获取当前用户剩余的抽奖机会
	 */
	public void getLotteryCondition() {
		
		showProgressDialog(R.string.tishi_loading);
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				dismissProgressDialog();
				if(msg.what==1) {
					Map map=(Map) msg.obj;
					luck_draw_total_num.setText(map.get("lottery_totalcnt").toString());
					luck_draw_last_num.setText(map.get("lottery_cnt").toString());
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
				LinkedList<String> wholeUrl=Util.getWholeUrl(LuckDrawActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(LuckDrawActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(LuckDrawActivity.this);					
						Map map=null;
						if(Util.getUserArea(LuckDrawActivity.this).equals("0971")) {
							LotteryManager strategyManager=GetWebDate.getHessionFactiory(LuckDrawActivity.this).create(LotteryManager.class, currentUsedUrl+"/hessian/lotteryManager", getClassLoader());
							map=strategyManager.getLotteryCondition(Long.parseLong(list.get(0)), list.get(1));
						}
						else {
							StrategyManager strategyManager=GetWebDate.getHessionFactiory(LuckDrawActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
							map=strategyManager.getLotteryCondition(Long.parseLong(list.get(0)), list.get(1), getIntent().getExtras().getLong("activityId"));
						}
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
	 * 用户抽奖
	 */
	public void drawLottery() {
		
		showProgressDialog(R.string.tishi_loading);
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				dismissProgressDialog();
				if(msg.what==1) {
					Map map=(Map) msg.obj;
					map_result=map;
					if(map.get("result").toString().equals("1")) {
						int level_id=Integer.parseInt(map.get("level_id").toString());
						int place=12-level_id;
						System.out.println("当前的位置:"+place+"");
						claert.setStopPlace(place); 
						claert.setStopRoter(false);
					}					
					else {
						showCustomToast(map.get("comments").toString());
						
						begin_btn.setEnabled(true);
						begin_btn.setClickable(true);
						begin_btn.setText("开始抽奖");
					}
				}
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");
					
					begin_btn.setEnabled(true);
					begin_btn.setClickable(true);
					begin_btn.setText("开始抽奖");
				}
				else {
					showCustomToast(getResources().getString(R.string.timeout_exp));
					
					begin_btn.setEnabled(true);
					begin_btn.setClickable(true);
					begin_btn.setText("开始抽奖");
				}
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(LuckDrawActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(LuckDrawActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(LuckDrawActivity.this);					
						Map map=null;
						if(Util.getUserArea(LuckDrawActivity.this).equals("0971")) {
							LotteryManager strategyManager=GetWebDate.getHessionFactiory(LuckDrawActivity.this).create(LotteryManager.class, currentUsedUrl+"/hessian/lotteryManager", getClassLoader());
							map=strategyManager.drawLottery(Long.parseLong(list.get(0)), Util.getDeviceId(LuckDrawActivity.this)+Util.getMacAddress(LuckDrawActivity.this), list.get(1));
						}
						else {
							StrategyManager strategyManager=GetWebDate.getHessionFactiory(LuckDrawActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
							map=strategyManager.drawLottery(Long.parseLong(list.get(0)), Util.getDeviceId(LuckDrawActivity.this)+Util.getMacAddress(LuckDrawActivity.this), list.get(1), getIntent().getExtras().getLong("activityId"));
						}
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
	 * 当前用户中奖信息
	 */
	public void getLotteryInfo() {

		isFinish2=true;
		showProgressDialog(R.string.tishi_loading);
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				isFinish2=false;
				dismissProgressDialog();
				if(msg.what==1) {
					Object[] obj=(Object[]) msg.obj;
					strArrays_my.clear();
					for(int i=0;i<obj.length;i++) {
						Map map=(Map)obj[i];
						HashMap<String, Object> map_temp=new HashMap<String, Object>();
						map_temp.put("myInfo", map.get("generate_time").toString()+" "+map.get("level_name").toString()+" "+map.get("prize_name").toString());
						strArrays_my.add(map_temp);
					}
					adapter_my.notifyDataSetChanged();
					if(strArrays_my.size()>0) {
						share_lottery_notice_my_list_no.setVisibility(View.GONE);
						share_lottery_notice_my_list.setVisibility(View.VISIBLE);
					}
					else {
						share_lottery_notice_my_list_no.setVisibility(View.VISIBLE);
						share_lottery_notice_my_list.setVisibility(View.GONE);
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
				LinkedList<String> wholeUrl=Util.getWholeUrl(LuckDrawActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(LuckDrawActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(LuckDrawActivity.this);					
						int month=getCurrentShowMonth();
						String temp="2014-"+(month<10?"0"+month:""+month);
						Map[] map=null;
						if(Util.getUserArea(LuckDrawActivity.this).equals("0971")) {
							LotteryManager strategyManager=GetWebDate.getHessionFactiory(LuckDrawActivity.this).create(LotteryManager.class, currentUsedUrl+"/hessian/lotteryManager", getClassLoader());
							map=strategyManager.getLotteryInfo(Long.parseLong(list.get(0)), temp);							
						}
						else {
							StrategyManager strategyManager=GetWebDate.getHessionFactiory(LuckDrawActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
							map=strategyManager.getLotteryInfo(Long.parseLong(list.get(0)), temp, getIntent().getExtras().getLong("activityId"));
						}
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
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		claert.setFlagStop();
	}
}
