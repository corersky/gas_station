package com.linkage.gas_station.gonglve;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.model.BuyShareRedEnvelopesModel;
import com.linkage.gas_station.qqapi.QQActivity;
import com.linkage.gas_station.sinaweiboapi.WBMainActivity;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.PublicManager;
import com.linkage.gas_station.util.hessian.StrategyManager;
import com.linkage.gas_station.wxapi.SendWeixin;
import com.linkage.gas_station.yxapi.SendYixin;

public class BuyShareRedEnvelopesActivity extends BaseActivity {
	
	ListView buyshareredenvelopes_listview=null;
	BuyShareRedEnvelopesAdapter adapter=null;
	TextView buyshareredenvelopes_getyz=null;
	TextView buyshareredenvelopes_time=null;
	EditText buyshareredenvelopes_yz=null;
	TextView buyshareredenvelopes_commit=null;
	
	ArrayList<BuyShareRedEnvelopesModel> models=null;
	
	Timer timer=null;
	DetailTimer timetask=null;
	
	ProgressDialog pd=null;
	
	ArrayList<String> list=null;
	
	//开启倒计时
	boolean isStartTime=false;
	//倒计时初始化时间
	long day=0;
	//提交时间戳
	long currentPayTime=0;
	int choice=-1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_buyshareredenvelopes);
		
		models=new ArrayList<BuyShareRedEnvelopesModel>();
		
		list=Util.getUserInfo(BuyShareRedEnvelopesActivity.this);
		
		init();
	}

	private void init() {
		buyshareredenvelopes_listview=(ListView) findViewById(R.id.buyshareredenvelopes_listview);
		adapter=new BuyShareRedEnvelopesAdapter(this, models);
		buyshareredenvelopes_listview.setAdapter(adapter);
		buyshareredenvelopes_listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				for(int i=0;i<models.size();i++) {
					models.get(i).setChoice(0);
				}
				models.get(position).setChoice(1);
				adapter.notifyDataSetChanged();
				choice=position;
			}
		});
		buyshareredenvelopes_getyz=(TextView) findViewById(R.id.buyshareredenvelopes_getyz);
		buyshareredenvelopes_getyz.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				buyshareredenvelopes_getyz.setEnabled(false);
				verCode();
			}
		});
		buyshareredenvelopes_time=(TextView) findViewById(R.id.buyshareredenvelopes_time);
		buyshareredenvelopes_yz=(EditText) findViewById(R.id.buyshareredenvelopes_yz);
		buyshareredenvelopes_commit=(TextView) findViewById(R.id.buyshareredenvelopes_commit);
		buyshareredenvelopes_commit.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(choice==-1) {
					return;
				}
				saveJSOrder(models.get(choice).getOffer_id());
			}
		});
		
		getRedPackets();
	}
	
	private void getRedPackets() {
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.what==1) {
					Object[] objs=(Object[]) msg.obj;
					for(int i=0;i<objs.length;i++) {
						BuyShareRedEnvelopesModel model=new BuyShareRedEnvelopesModel();
						Map map=(Map) objs[i];
						model.setId(Integer.parseInt(map.get("id").toString()));
						model.setCost(Integer.parseInt(map.get("cost").toString()));
						model.setOffer_description(map.get("offer_description").toString());
						model.setOffer_id(Integer.parseInt(map.get("offer_id").toString()));
						model.setOffer_name(map.get("offer_name").toString());
						models.add(model);
					}
					adapter.notifyDataSetChanged();
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
				LinkedList<String> wholeUrl=Util.getWholeUrl(BuyShareRedEnvelopesActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(BuyShareRedEnvelopesActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(BuyShareRedEnvelopesActivity.this);					
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(BuyShareRedEnvelopesActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map[] map=strategyManager.getRedPackets(Long.parseLong(list.get(0)), list.get(1), Long.parseLong(getIntent().getExtras().getString("activityId")));
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
			}
		}).start();
	}
	
	private void saveJSOrder(final long offerId) {
		showProgressDialog(R.string.tishi_loading);
		currentPayTime=0;
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				dismissProgressDialog();
				if(msg.what==1) {
					Map map=(Map) msg.obj;
					if(Integer.parseInt(map.get("deal_result").toString())==0) {
						showCustomToast(map.get("comments").toString());
					}
					else {
						new AlertDialog.Builder(BuyShareRedEnvelopesActivity.this).setTitle("提示").setMessage(map.get("comments").toString()).setPositiveButton("指定派送", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								Intent intent=new Intent(BuyShareRedEnvelopesActivity.this, SendShareRedEnvelopesActivity.class);
								Bundle bundle=new Bundle();
								bundle.putString("total", models.get(choice).getOffer_description());
								bundle.putString("activityId", getIntent().getExtras().getString("activityId"));
								intent.putExtras(bundle);
								startActivity(intent); 
							}
						}).setNegativeButton("随机派送", new DialogInterface.OnClickListener() {
							
							@Override
							public void onClick(DialogInterface dialog, int which) {
								// TODO Auto-generated method stub
								sendRedPackets(0);
							}
						}).show();
					}
				}
				else if(msg.what==-2) {
					showCustomToast("链路连接失败");
				}
				else if(msg.what==-1) {
					showCustomToast(getResources().getString(R.string.timeout_exp));
				}
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(BuyShareRedEnvelopesActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(BuyShareRedEnvelopesActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(BuyShareRedEnvelopesActivity.this);
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(BuyShareRedEnvelopesActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						long temp_time=(currentPayTime==0?System.currentTimeMillis():currentPayTime);
						currentPayTime=temp_time;
						Map map=strategyManager.saveJSOrder(String.valueOf(temp_time), Long.parseLong(list.get(0)), 
								buyshareredenvelopes_yz.getText().toString(), list.get(1), 
								Long.parseLong(getIntent().getExtras().getString("activityId")), offerId);
						m.obj=map;
						m.what=1;		
						flag=false;
						((GasStationApplication) getApplicationContext()).AreaUrl=currentUsedUrl;
					} catch(Error e) {
						flag=false;
						m.what=-2;
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
						m.what=-1;
						m.obj=null;
					}
				}				
				handler.sendMessage(m);
			}}).start();
	}
	
	private void sendRedPackets(final long offerId) {
		currentPayTime=0;
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.what==1) {
					Map map=(Map) msg.obj;	
					if(Integer.parseInt(map.get("result").toString())==1) {
						showShare(0);
					}
					else {
						showCustomToast(map.get("comments").toString());
					}
				}
				else if(msg.what==-2) {
					showCustomToast("链路连接失败");
				}
				else if(msg.what==-1) {
					showCustomToast(getResources().getString(R.string.timeout_exp));
				}
			}
		};
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(BuyShareRedEnvelopesActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(BuyShareRedEnvelopesActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(BuyShareRedEnvelopesActivity.this);
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(BuyShareRedEnvelopesActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						long temp_time=(currentPayTime==0?System.currentTimeMillis():currentPayTime);
						currentPayTime=temp_time;
						Map map=strategyManager.sendRedPackets(String.valueOf(temp_time), Long.parseLong(list.get(0)), list.get(1), 
								Long.parseLong(getIntent().getExtras().getString("activityId")), 1, offerId, "");
						m.obj=map;
						m.what=1;		
						flag=false;
						((GasStationApplication) getApplicationContext()).AreaUrl=currentUsedUrl;
					} catch(Error e) {
						flag=false;
						m.what=-2;
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
						m.what=-1;
						m.obj=null;
					}
				}				
				handler.sendMessage(m);
			}
		}).start();
	}
	
	private void showShare(final int seqId) {
		final Dialog dialog=new Dialog(BuyShareRedEnvelopesActivity.this, R.style.shareDialog);
		dialog.setCanceledOnTouchOutside(true);
		View view=LayoutInflater.from(BuyShareRedEnvelopesActivity.this).inflate(R.layout.view_sharelayout, null);
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
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(BuyShareRedEnvelopesActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				((GasStationApplication) getApplicationContext()).content=getIntent().getExtras().getString("activity_rule");
				Intent intent=new Intent(BuyShareRedEnvelopesActivity.this, QQActivity.class);
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
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(BuyShareRedEnvelopesActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				((GasStationApplication) getApplicationContext()).content=getIntent().getExtras().getString("activity_rule");
				SendYixin yixin=new SendYixin();
				yixin.sendYixin(BuyShareRedEnvelopesActivity.this, getIntent().getExtras().getString("activity_rule"), currentUsedUrl+getIntent().getExtras().getString("activity_url")+"?activityId="+getIntent().getExtras().getString("activityId")+"&seqId="+seqId, getIntent().getExtras().getString("activity_name"), R.drawable.ic_launcher, true);
				
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
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(BuyShareRedEnvelopesActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				((GasStationApplication) getApplicationContext()).content=getIntent().getExtras().getString("activity_rule");
				SendWeixin weixin=new SendWeixin();
				weixin.sendWeixin(BuyShareRedEnvelopesActivity.this, getIntent().getExtras().getString("activity_name")+"\n"+getIntent().getExtras().getString("activity_rule"), currentUsedUrl+getIntent().getExtras().getString("activity_url")+"?activityId="+getIntent().getExtras().getString("activityId")+"&seqId="+seqId, getIntent().getExtras().getString("activity_name")+"\n"+getIntent().getExtras().getString("activity_rule"), R.drawable.ic_launcher, true);
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
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(BuyShareRedEnvelopesActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				((GasStationApplication) getApplicationContext()).content=getIntent().getExtras().getString("activity_rule");
				Intent intent=new Intent(BuyShareRedEnvelopesActivity.this, WBMainActivity.class);
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
	
	/**
	 * 获取验证码
	 */
	public void verCode() {
		
		pd=ProgressDialog.show(BuyShareRedEnvelopesActivity.this, getResources().getString(R.string.tishi), "正在获取验证码");
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if(msg.what==1) {
					showCustomToast(getResources().getString(R.string.yzm_comp));
					//验证码开启标志位
					buyshareredenvelopes_time.setVisibility(View.VISIBLE);
					buyshareredenvelopes_getyz.setVisibility(View.GONE);
					day=new Date().getTime();
					isStartTime=true;
				}
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");
				}
				else {
					showCustomToast(getResources().getString(R.string.timeout_exp));
				}
				buyshareredenvelopes_getyz.setEnabled(true);
				pd.dismiss();
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(BuyShareRedEnvelopesActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(BuyShareRedEnvelopesActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						
						PublicManager publicManager=GetWebDate.getHessionFactiory(BuyShareRedEnvelopesActivity.this).create(PublicManager.class, currentUsedUrl+"/hessian/publicManager", getClassLoader());
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
					buyshareredenvelopes_time.setVisibility(View.GONE);
					buyshareredenvelopes_getyz.setVisibility(View.VISIBLE);
					((GasStationApplication) getApplicationContext()).loginTime=0;
				}
				else {
					buyshareredenvelopes_time.setText(""+(60-second_)+"秒发");
				}
			}
		}
	};
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		timer=new Timer();
		timetask=new DetailTimer();
		timer.schedule(timetask, new Date(), 1000);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		timer.cancel();
	}
}
