package com.linkage.gas_station.gonglve;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.animation.Animator.AnimatorListener;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

public class ShareRedEnvelopesLucjDrawActivity extends BaseActivity {
	
	ImageView shareredenvelopes_lottery_point=null;
	ImageView shareredenvelopes_lottery_view=null;
	TextView shareredenvelopes_lottery_drawnum=null;
	TextView shareredenvelopes_lottery_lists=null;
	
	//旋转结束位置
	int lastValue=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_shareredenvelopes_luckdraw);
		
		init();
	}
	
	private void init() {
		shareredenvelopes_lottery_point=(ImageView) findViewById(R.id.shareredenvelopes_lottery_point);
		shareredenvelopes_lottery_point.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				drawLottery();
			}});
		shareredenvelopes_lottery_view=(ImageView) findViewById(R.id.shareredenvelopes_lottery_view);
		shareredenvelopes_lottery_drawnum=(TextView) findViewById(R.id.shareredenvelopes_lottery_drawnum);
		shareredenvelopes_lottery_lists=(TextView) findViewById(R.id.shareredenvelopes_lottery_lists);
		shareredenvelopes_lottery_lists.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Intent intent=new Intent(ShareRedEnvelopesLucjDrawActivity.this, MemberluckMyPrizeActivity.class);
				Bundle bundle=new Bundle();
				bundle.putLong("activityId", Long.parseLong(getIntent().getExtras().getString("activityId")));
				bundle.putBoolean("isShowGet", false);
				intent.putExtras(bundle);
				startActivity(intent);
			}});
		
		getJSLotteryCondition();
	}
	
	public void getJSLotteryCondition() {
		showProgressDialog(R.string.tishi_loading);
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				dismissProgressDialog();
				if(msg.what==1) {
					Map map=(Map) msg.obj;
					shareredenvelopes_lottery_drawnum.setText(map.get("lottery_cnt").toString());
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
				LinkedList<String> wholeUrl=Util.getWholeUrl(ShareRedEnvelopesLucjDrawActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(ShareRedEnvelopesLucjDrawActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(ShareRedEnvelopesLucjDrawActivity.this);					
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(ShareRedEnvelopesLucjDrawActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map map=strategyManager.getJSLotteryCondition(Long.parseLong(list.get(0)), list.get(1), Long.parseLong(getIntent().getExtras().getString("activityId")));
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
						int place=4-level_id;
						System.out.println("当前的位置:"+place+"");
						
						//最终角度
						final int finalRotate=(int) (360/4*(place+0.5));

						AnimatorSet set=new AnimatorSet();
						set.playTogether(ObjectAnimator.ofFloat(shareredenvelopes_lottery_view, "rotation", lastValue, finalRotate+360*3));
						set.setDuration(3000);
						set.start();
						set.addListener(new AnimatorListener() {
							
							@Override
							public void onAnimationStart(Animator arg0) {
								// TODO Auto-generated method stub
								shareredenvelopes_lottery_point.setEnabled(false);
								shareredenvelopes_lottery_point.setClickable(false);
							}
							
							@Override
							public void onAnimationRepeat(Animator arg0) {
								// TODO Auto-generated method stub
								
							}
							
							@Override
							public void onAnimationEnd(Animator arg0) {
								// TODO Auto-generated method stub
								shareredenvelopes_lottery_point.setEnabled(true);
								shareredenvelopes_lottery_point.setClickable(true);
								lastValue=finalRotate;
								shareredenvelopes_lottery_drawnum.setText(map.get("lottery_cnt").toString());	
								new AlertDialog.Builder(ShareRedEnvelopesLucjDrawActivity.this).setTitle("提示").setMessage(map.get("comments").toString()).setPositiveButton("确定", new DialogInterface.OnClickListener() {
									
									@Override
									public void onClick(DialogInterface dialog, int which) {
										// TODO Auto-generated method stub
										
									}
								}).show();
							}
							
							@Override
							public void onAnimationCancel(Animator arg0) {
								// TODO Auto-generated method stub
								
							}
						});
					}					
					else {
						showCustomToast(map.get("comments").toString());
						shareredenvelopes_lottery_point.setEnabled(true);
						shareredenvelopes_lottery_point.setClickable(true);
					}
				}
				else if(msg.what==-1) {
					showCustomToast("链路连接失败");
					shareredenvelopes_lottery_point.setEnabled(true);
					shareredenvelopes_lottery_point.setClickable(true);
				}
				else {
					showCustomToast(getResources().getString(R.string.timeout_exp));
					shareredenvelopes_lottery_point.setEnabled(true);
					shareredenvelopes_lottery_point.setClickable(true);
				}
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(ShareRedEnvelopesLucjDrawActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(ShareRedEnvelopesLucjDrawActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(ShareRedEnvelopesLucjDrawActivity.this);					
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(ShareRedEnvelopesLucjDrawActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map map=strategyManager.drawJSLottery(Long.parseLong(list.get(0)), Util.getDeviceId(ShareRedEnvelopesLucjDrawActivity.this)+Util.getMacAddress(ShareRedEnvelopesLucjDrawActivity.this), list.get(1), Long.parseLong(getIntent().getExtras().getString("activityId")));
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
}
