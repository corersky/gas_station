package com.linkage.gas_station.oil_treasure;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.model.OilListModel;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.PublicManager;
import com.linkage.gas_station.util.hessian.StrategyManager;

public class TreasureBuyActivity extends BaseActivity {
	
	TextView title_name=null;
	ImageView title_back=null;
	ImageView title_refresh=null;
	ProgressBar title_refresh_progress=null;
	
	GridView treasure_buy_grid=null;
	ImageView treasure_bug_choicecontacts=null;
	EditText treasure_buy_phone=null;
	TextView treasure_buy_commit=null;
	EditText treasure_buy_yz=null;
	ImageView treasure_buy_getyz=null;
	TextView treasure_buy_time=null;
	
	ArrayList<OilListModel> oilListModel=null;
	TreasureBuyAdapter adapter=null;
	//当前选中的pos
	int pos=-1;
	//提交时间戳
	long currentPayTime=0;
	//开启倒计时
	boolean isStartTime=false;
	//倒计时初始化时间
	long day=0;
	boolean isLoadActivity=false;
	boolean isAleradyOper=false;
	
	Timer timer=null;
	DetailTimer timetask=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_treasure_buy);
		
		oilListModel=new ArrayList<OilListModel>();
		
		((GasStationApplication) getApplication()).tempActivity.add(TreasureBuyActivity.this);
		
		init();
		getJybOilList();
	}
	
	private void init() {
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText("买流量送金币");
		title_back=(ImageView) findViewById(R.id.title_back);
		title_back.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=getIntent();
				Bundle bundle=new Bundle();
				bundle.putBoolean("isAleradyOper", isAleradyOper);
				intent.putExtras(bundle);
				setResult(RESULT_OK, intent);
				finish();
			}});
		title_back.setVisibility(View.VISIBLE);
		title_refresh=(ImageView) findViewById(R.id.title_refresh);
		title_refresh.setVisibility(View.VISIBLE);
		title_refresh.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(!isLoadActivity) {
					getJybOilList();
				}
			}});
		title_refresh_progress=(ProgressBar) findViewById(R.id.title_refresh_progress);
		
		
		treasure_buy_grid=(GridView) findViewById(R.id.treasure_buy_grid);
		treasure_buy_grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				pos=arg2;
				for(int i=0;i<oilListModel.size();i++) {
					if(arg2==i) {
						oilListModel.get(i).setChecked(true);
					}
					else {
						oilListModel.get(i).setChecked(false);
					}
				}
				adapter.notifyDataSetChanged();
			}
		});
		adapter=new TreasureBuyAdapter(TreasureBuyActivity.this, oilListModel);
		treasure_buy_grid.setAdapter(adapter);
		
		treasure_bug_choicecontacts=(ImageView) findViewById(R.id.treasure_bug_choicecontacts);
		treasure_bug_choicecontacts.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(TreasureBuyActivity.this, TreasureBuyContactsActivity.class);
				startActivityForResult(intent, 700);
			}});
		treasure_buy_phone=(EditText) findViewById(R.id.treasure_buy_phone);
		treasure_buy_commit=(TextView) findViewById(R.id.treasure_buy_commit);
		treasure_buy_commit.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				if(!treasure_buy_phone.getText().toString().equals("")) {
					ArrayList<String> list=Util.getUserInfo(TreasureBuyActivity.this);
					if(treasure_buy_phone.getText().toString().equals(list.get(0))) {
						showCustomToast("推荐人手机号不能为本人手机号");
					}
					else {
						
						String result=Util.getPhoneHeader(TreasureBuyActivity.this);
						String[] result_array=result.substring(0, result.length()-1).split("&");
						ArrayList<String> result_list=new ArrayList<String>();
						for(int i=0;i<result_array.length;i++) {
							result_list.add(result_array[i]);
						}
						if(treasure_buy_phone.getText().toString().length()==11&&result_list.contains(treasure_buy_phone.getText().toString().substring(0, 7))) {
							if(treasure_buy_yz.getText().toString().equals("")) {
								showCustomToast("请您输入验证码");
							}
							else {
								jybOrder();
							}
						}
						else {
							showCustomToast("只能分享无锡电信用户");
						}
					}
				}
				else {
					showCustomToast("请您输入推荐人手机号码");
				}
			}});

		treasure_buy_yz=(EditText) findViewById(R.id.treasure_buy_yz);
		treasure_buy_getyz=(ImageView) findViewById(R.id.treasure_buy_getyz);
		treasure_buy_time=(TextView) findViewById(R.id.treasure_buy_time);
		treasure_buy_getyz.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View arg0) {
				// TODO Auto-generated method stub
				verCode();
			}});
	}
	
	/**
	 * 流量币汇总
	 */
	public void getJybOilList() {
		
		isLoadActivity=true;
		title_refresh.setVisibility(View.INVISIBLE);
		title_refresh_progress.setVisibility(View.VISIBLE);
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if(msg.what==1) {
					Object[] obj_array=(Object[]) msg.obj;
					oilListModel.clear();
					for(int i=0;i<obj_array.length;i++) {
						Map map=(Map) obj_array[i];
						OilListModel model=new OilListModel();
						model.setOffer_content(map.get("offer_content").toString());
						model.setOffer_id(map.get("offer_id").toString());
						model.setChecked(false);
						oilListModel.add(model);
					}
					adapter.notifyDataSetChanged();
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
				LinkedList<String> wholeUrl=Util.getWholeUrl(TreasureBuyActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(TreasureBuyActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(TreasureBuyActivity.this);					
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(TreasureBuyActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map[] map=strategyManager.getJybOilList(Long.parseLong(list.get(0)), 1, list.get(1));
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
	 * 订购流量套餐
	 */
	public void jybOrder() {
		
		showProgressDialog(R.string.tishi);
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				dismissProgressDialog();
				if(msg.what==1) {
					Map map=(Map) msg.obj;
					showCustomToast(map.get("comments").toString());
					treasure_buy_yz.setText("");
					if(map.get("deal_result").toString().equals("1")) {
						isAleradyOper=true;
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
				LinkedList<String> wholeUrl=Util.getWholeUrl(TreasureBuyActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(TreasureBuyActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						long temp_time=(currentPayTime==0?System.currentTimeMillis():currentPayTime);
						currentPayTime=temp_time;
						ArrayList<String> list=Util.getUserInfo(TreasureBuyActivity.this);					
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(TreasureBuyActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map map=strategyManager.jybOrder(String.valueOf(temp_time), Long.parseLong(list.get(0)), Long.parseLong(treasure_buy_phone.getText().toString()), Long.parseLong(oilListModel.get(pos).getOffer_id()), treasure_buy_yz.getText().toString(), list.get(1), 5, 1, 1, 0, 0);
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		if(resultCode==RESULT_OK&&requestCode==700) {
			treasure_buy_phone.setText(data.getExtras().getString("phoneNum"));
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 获取验证码
	 */
	public void verCode() {
		showProgressDialog(R.string.yzm_now);		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if(msg.what==1) {
					showCustomToast(getResources().getString(R.string.yzm_comp));
					//验证码开启标志位
					treasure_buy_time.setVisibility(View.VISIBLE);
					treasure_buy_getyz.setVisibility(View.GONE);
					day=new Date().getTime();
					isStartTime=true;
				}
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");
				}
				else {
					showCustomToast(getResources().getString(R.string.timeout_exp));
				}
				treasure_buy_getyz.setEnabled(true);
				dismissProgressDialog();
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(TreasureBuyActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(TreasureBuyActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(TreasureBuyActivity.this);										
						PublicManager publicManager=GetWebDate.getHessionFactiory(TreasureBuyActivity.this).create(PublicManager.class, currentUsedUrl+"/hessian/publicManager", getClassLoader());
						int verCode=publicManager.sendVerCode(Long.parseLong(list.get(0)), list.get(1));
						m.what=verCode;
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
	
	class DetailTimer extends TimerTask {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			Message m=new Message();
			handler_detailTime.sendMessage(m);
		}
		
	}
	
	Handler handler_detailTime=new Handler() {
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if(isStartTime) {
				
				Date today=new Date();		
				int day_=0;
				int hour_=0;
				int minute_=0;
				int second_=0;
				day_=(int) ((today.getTime()-day)/(24*60*60*1000));
				hour_=(int)(today.getTime()-day)/(60*60*1000)-day_*24;
				minute_=(int) (today.getTime()-day)/(60*1000)-day_*24*60-hour_*60;
				second_=(int) (today.getTime()-day)/1000-day_*24*60*60-hour_*60*60-minute_*60;
				if(minute_>0) {
					treasure_buy_time.setVisibility(View.GONE);
					treasure_buy_getyz.setVisibility(View.VISIBLE);
					((GasStationApplication) getApplicationContext()).loginTime=0;
				}
				else {
					treasure_buy_time.setText(""+(60-second_)+"秒发");
				}
			}
		}
	};
	
	protected void onResume() {
		super.onResume();
		timer=new Timer();
		timetask=new DetailTimer();
		timer.schedule(timetask, new Date(), 1000);
	};
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		timer.cancel();
	}
	
	public boolean onKeyDown(int keyCode, android.view.KeyEvent event) {
		if(keyCode==KeyEvent.KEYCODE_BACK) {
			Intent intent=getIntent();
			Bundle bundle=new Bundle();
			bundle.putBoolean("isAleradyOper", isAleradyOper);
			intent.putExtras(bundle);
			setResult(RESULT_OK, intent);
			finish();
		}
		return super.onKeyDown(keyCode, event);		
	};
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
		((GasStationApplication) getApplication()).tempActivity.remove(TreasureBuyActivity.this);
	}
}
