package com.linkage.gas_station.gonglve;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.qqapi.QQActivity;
import com.linkage.gas_station.sinaweiboapi.WBMainActivity;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.StrategyManager;
import com.linkage.gas_station.wxapi.SendWeixin;
import com.linkage.gas_station.yxapi.SendYixin;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

public class MemberDrawActivity extends BaseActivity {
	ImageView title_back=null;
	TextView title_name=null;
	
	ImageView member_lottery_view=null;
	ImageView member_lottery_point=null;
	ImageView memberluck_myprize=null;
	ImageView memberluck_rp=null;
	ImageView memberluck_update=null;
	ImageView memberluck_list=null;
	TextView memberdraw_drawnum=null;
	TextView memberdraw_rp=null;
	TextView memberdraw_center=null;
	TextView memberluck_prizelist=null;
	
	//旋转结束位置
	int lastValue=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_memberdraw);
		
		((GasStationApplication) getApplication()).tempActivity.add(MemberDrawActivity.this);
		
		init();
	}
	
	private void init() {
		title_back=(ImageView) findViewById(R.id.title_back);
		title_back.setVisibility(View.VISIBLE);
		title_back.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText("我要抽奖");
		member_lottery_point=(ImageView) findViewById(R.id.member_lottery_point);
		member_lottery_point.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				drawLottery();
			}});
		member_lottery_view=(ImageView) findViewById(R.id.member_lottery_view);
		memberluck_myprize=(ImageView) findViewById(R.id.memberluck_myprize);
		memberluck_myprize.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(MemberDrawActivity.this, MemberluckMyPrizeActivity.class);
				Bundle bundle=new Bundle();
				bundle.putLong("activityId", getIntent().getExtras().getLong("activityId"));
				bundle.putBoolean("isShowGet", true);
				intent.putExtras(bundle);
				startActivity(intent);
			}});
		memberluck_rp=(ImageView) findViewById(R.id.memberluck_rp);
		memberluck_rp.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(MemberDrawActivity.this, MemberluckRPActivity.class);
				Bundle bundle=new Bundle();
				bundle.putLong("activityId", getIntent().getExtras().getLong("activityId"));
				bundle.putString("activity_name", getIntent().getExtras().getString("activity_name"));
				bundle.putString("activity_url", getIntent().getExtras().getString("activity_url"));
				bundle.putString("activity_rule", getIntent().getExtras().getString("activity_rule"));
				intent.putExtras(bundle);
				startActivity(intent);
			}});
		memberluck_update=(ImageView) findViewById(R.id.memberluck_update);
		memberluck_update.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(MemberDrawActivity.this, MemberluckUpdateActivity.class);
				Bundle bundle=new Bundle();
				bundle.putLong("activityId", getIntent().getExtras().getLong("activityId"));
				intent.putExtras(bundle);
				startActivity(intent);
			}});
		memberluck_list=(ImageView) findViewById(R.id.memberluck_list);
		memberluck_list.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(MemberDrawActivity.this, MemberluckListActivity.class);
				Bundle bundle=new Bundle();
				bundle.putLong("activityId", getIntent().getExtras().getLong("activityId"));
				intent.putExtras(bundle);
				startActivity(intent);
			}});
		memberdraw_drawnum=(TextView) findViewById(R.id.memberdraw_drawnum);
		memberdraw_rp=(TextView) findViewById(R.id.memberdraw_rp);
		memberdraw_center=(TextView) findViewById(R.id.memberdraw_center);
		memberluck_prizelist=(TextView) findViewById(R.id.memberluck_prizelist);
		
		getLotteryCondition();
	}
	
	/**
	 * 获取当前用户剩余的抽奖机会
	 */
	private void getLotteryCondition() {
		
		showProgressDialog(R.string.tishi_loading);
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				dismissProgressDialog();
				if(msg.what==1) {
					Map map=(Map) msg.obj;
					memberdraw_drawnum.setText(map.get("lottery_cnt").toString());
					memberdraw_rp.setText(map.get("lottery_totalcnt").toString());
					memberdraw_center.setText(map.get("vip_name").toString());
					
					if(map.get("lottery_list")!=null) {
						Object[] objs=(Object[]) map.get("lottery_list");
						String str="";
						for(int i=0;i<objs.length;i++) {
							str+=((Map) objs[i]).get("prize_name").toString()+"\n";
						}
						memberluck_prizelist.setText(str);
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
				LinkedList<String> wholeUrl=Util.getWholeUrl(MemberDrawActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(MemberDrawActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(MemberDrawActivity.this);					
						Map map=null;
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(MemberDrawActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						map=strategyManager.getLotteryCondition(Long.parseLong(list.get(0)), list.get(1), getIntent().getExtras().getLong("activityId"));
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
	private void drawLottery() {
		
		showProgressDialog(R.string.tishi_loading);
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				dismissProgressDialog();
				if(msg.what==1) {
					final Map map=(Map) msg.obj;
					if(map.get("result").toString().equals("1")) {
						int level_id=Integer.parseInt(map.get("level_id").toString());
						int place=6-level_id;
						System.out.println("当前的位置:"+place+"");
						
						//最终角度
						final int finalRotate=(int) (360/6*(place+0.5));

						AnimatorSet set=new AnimatorSet();
						set.playTogether(ObjectAnimator.ofFloat(member_lottery_view, "rotation", lastValue, finalRotate+360*3));
						set.setDuration(3000);
						set.start();
						set.addListener(new AnimatorListener() {
							
							@Override
							public void onAnimationStart(Animator arg0) {
								// TODO Auto-generated method stub
								member_lottery_point.setEnabled(false);
								member_lottery_point.setClickable(false);
							}
							
							@Override
							public void onAnimationRepeat(Animator arg0) {
								// TODO Auto-generated method stub
								
							}
							
							@Override
							public void onAnimationEnd(Animator arg0) {
								// TODO Auto-generated method stub
								member_lottery_point.setEnabled(true);
								member_lottery_point.setClickable(true);
								lastValue=finalRotate;
								memberdraw_rp.setText(map.get("lottery_totalcnt").toString());
								memberdraw_drawnum.setText(map.get("lottery_cnt").toString());
								showResult(map.get("comments").toString(), Long.parseLong(map.get("userwin_id").toString()));
							}
							
							@Override
							public void onAnimationCancel(Animator arg0) {
								// TODO Auto-generated method stub
								
							}
						});
					}					
					else {
						showCustomToast(map.get("comments").toString());
						member_lottery_point.setEnabled(true);
						member_lottery_point.setClickable(true);
					}
				}
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");
					member_lottery_point.setEnabled(true);
					member_lottery_point.setClickable(true);
				}
				else {
					showCustomToast(getResources().getString(R.string.timeout_exp));
					member_lottery_point.setEnabled(true);
					member_lottery_point.setClickable(true);
				}
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(MemberDrawActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(MemberDrawActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(MemberDrawActivity.this);					
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(MemberDrawActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map map=strategyManager.drawLottery(Long.parseLong(list.get(0)), Util.getDeviceId(MemberDrawActivity.this)+Util.getMacAddress(MemberDrawActivity.this), list.get(1), getIntent().getExtras().getLong("activityId"));
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
	 * 显示结果
	 */
	private void showResult(final String comments, final long userwin_id) {
		final AlertDialog dialog=new AlertDialog.Builder(MemberDrawActivity.this).create();
		dialog.show();
		dialog.getWindow().setContentView(R.layout.dialog_memberdraw);
		TextView memberluck_content=(TextView)dialog.getWindow().findViewById(R.id.memberluck_content);
		memberluck_content.setText(comments);
		TextView memberluck_get=(TextView)dialog.getWindow().findViewById(R.id.memberluck_get);
		memberluck_get.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				getPrizeById(userwin_id);
			}});
		TextView memberluck_share=(TextView)dialog.getWindow().findViewById(R.id.memberluck_share);
		memberluck_share.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				showShare("0");
			}});
	}
	
	private void showReceiverOK(String message) {
		final AlertDialog dialog=new AlertDialog.Builder(MemberDrawActivity.this).create();
		dialog.show();
		dialog.getWindow().setContentView(R.layout.dialog_memberluck_receiverok);
		TextView receiverok_text=(TextView) dialog.getWindow().findViewById(R.id.receiverok_text);
		receiverok_text.setText(message);
		TextView receiverok_commit=(TextView) dialog.getWindow().findViewById(R.id.receiverok_commit);
		receiverok_commit.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}});
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		((GasStationApplication) getApplication()).tempActivity.remove(MemberDrawActivity.this);
	}
	
	private void showShare(final String seqId) {

		final Dialog dialog=new Dialog(MemberDrawActivity.this, R.style.shareDialog);
		dialog.setCanceledOnTouchOutside(true);
		View view=LayoutInflater.from(MemberDrawActivity.this).inflate(R.layout.view_sharelayout, null);
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
				((GasStationApplication) getApplicationContext()).activityId=Integer.parseInt(""+getIntent().getExtras().getLong("activityId"));
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(MemberDrawActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				((GasStationApplication) getApplicationContext()).content=getIntent().getExtras().getString("activity_rule");
				Intent intent=new Intent(MemberDrawActivity.this, QQActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("title", getIntent().getExtras().getString("activity_name"));
				bundle.putString("url", currentUsedUrl+getIntent().getExtras().getString("activity_url")+"?activityId="+getIntent().getExtras().getLong("activityId")+"&seqId="+seqId);
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
				((GasStationApplication) getApplicationContext()).activityId=Integer.parseInt(""+getIntent().getExtras().getLong("activityId"));
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(MemberDrawActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				((GasStationApplication) getApplicationContext()).content=getIntent().getExtras().getString("activity_rule");
				SendYixin yixin=new SendYixin();
				yixin.sendYixin(MemberDrawActivity.this , getIntent().getExtras().getString("activity_rule"), currentUsedUrl+getIntent().getExtras().getString("activity_url")+"?activityId="+getIntent().getExtras().getLong("activityId")+"&seqId="+seqId, getIntent().getExtras().getString("activity_name"), R.drawable.ic_launcher, true);
				
			}});
		ImageView gonglve_weixin_pengyou_share=(ImageView) view.findViewById(R.id.gonglve_weixin_pengyou_share);
		gonglve_weixin_pengyou_share.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				((GasStationApplication) getApplicationContext()).shareType=3;
				((GasStationApplication) getApplicationContext()).activityId=Integer.parseInt(""+getIntent().getExtras().getLong("activityId"));
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(MemberDrawActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				((GasStationApplication) getApplicationContext()).content=getIntent().getExtras().getString("activity_rule");
				SendWeixin weixin=new SendWeixin();
				weixin.sendWeixin(MemberDrawActivity.this, getIntent().getExtras().getString("activity_name")+"\n"+getIntent().getExtras().getString("activity_rule"), currentUsedUrl+getIntent().getExtras().getString("activity_url")+"?activityId="+getIntent().getExtras().getLong("activityId")+"&seqId="+seqId, getIntent().getExtras().getString("activity_name")+"\n"+getIntent().getExtras().getString("activity_rule"), R.drawable.ic_launcher, true);
			}});
		ImageView gonglve_sinaweibo_logo_share=(ImageView) view.findViewById(R.id.gonglve_sinaweibo_logo_share);
		gonglve_sinaweibo_logo_share.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				((GasStationApplication) getApplicationContext()).shareType=4;
				((GasStationApplication) getApplicationContext()).activityId=Integer.parseInt(""+getIntent().getExtras().getLong("activityId"));
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(MemberDrawActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				((GasStationApplication) getApplicationContext()).content=getIntent().getExtras().getString("activity_rule");
				Intent intent=new Intent(MemberDrawActivity.this, WBMainActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("title", getIntent().getExtras().getString("activity_name"));
				bundle.putString("url", currentUsedUrl+getIntent().getExtras().getString("activity_url")+"?activityId="+getIntent().getExtras().getLong("activityId")+"&seqId="+seqId);
				bundle.putString("text", getIntent().getExtras().getString("activity_rule"));
				bundle.putString("defaultText", "流量加油站");
				intent.putExtras(bundle);
				startActivity(intent);
			}});
		dialog.setContentView(view);
		dialog.show();
	
	}
	
	/**
	 * 领取奖品
	 * @param userWinId
	 */
	private void getPrizeById(final Long userWinId) {
		
		showProgressDialog(R.string.tishi_loading);
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				dismissProgressDialog();
				if(msg.what==1) {
					if(msg.obj!=null) {
						Map map=(Map) msg.obj;
						showReceiverOK(map.get("comments").toString());
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
				LinkedList<String> wholeUrl=Util.getWholeUrl(MemberDrawActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(MemberDrawActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(MemberDrawActivity.this);					
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(MemberDrawActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map map=strategyManager.getPrizeById(Long.parseLong(list.get(0)), list.get(1), getIntent().getExtras().getLong("activityId"), userWinId);
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
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getLotteryCondition();
	}

}

