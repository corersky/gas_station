package com.linkage.gas_station.gonglve;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.model.RedListModel;
import com.linkage.gas_station.qqapi.QQActivity;
import com.linkage.gas_station.sinaweiboapi.WBMainActivity;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.StrategyManager;
import com.linkage.gas_station.wxapi.SendWeixin;
import com.linkage.gas_station.yxapi.SendYixin;

public class ShareRedEnvelopesRecordActivity extends BaseActivity {
	
	LinearLayout shareredenvelopes_layout=null;
	ArrayList<RedListModel> models=null;
	HashMap<String, ArrayList<RedListModel>> maps=null;
	
	//提交时间戳
	long currentPayTime=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_shareredenvelopesrecord);
		
		models=new ArrayList<RedListModel>();
		maps=new HashMap<String, ArrayList<RedListModel>>();
		
		init();
	}
	
	private void init() {
		shareredenvelopes_layout=(LinearLayout) findViewById(R.id.shareredenvelopes_layout);
	}
	
	private void getRedList() {
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.what==1) {
					Object[] objs=(Object[]) msg.obj;	
					
					shareredenvelopes_layout.removeAllViews();
					maps.clear();
					models.clear();
					
					String parentId="";
					for(int i=0;i<objs.length;i++) {
						Map map=(Map) objs[i];
						
						RedListModel model=new RedListModel();
						model.setId(map.get("id")==null?"":map.get("id").toString());
						model.setOffer_description(map.get("offer_description")==null?"":map.get("offer_description").toString());
						model.setOffer_id(map.get("offer_id")==null?"":map.get("offer_id").toString());
						model.setOffer_name(map.get("offer_name")==null?"":map.get("offer_name").toString());
						model.setPhone_num(map.get("phone_num")==null?"":map.get("phone_num").toString());
						model.setSend_amount(map.get("send_amount")==null?"":map.get("send_amount").toString());
						model.setSend_phone(map.get("send_phone")==null?"":map.get("send_phone").toString());
						model.setSend_time(map.get("send_time")==null?"":map.get("send_time").toString());
						model.setSend_type(map.get("send_type")==null?"":map.get("send_type").toString());
						if(parentId.equals(map.get("id")==null?"":map.get("id").toString())) {
							ArrayList<RedListModel> lists=maps.get(parentId);
							if(!(map.get("send_phone")==null?"":map.get("send_phone").toString()).equals("")) {
								lists.add(model);
							}
						}
						else {
							parentId=map.get("id")==null?"":map.get("id").toString();
							ArrayList<RedListModel> lists=new ArrayList<RedListModel>();
							if(!(map.get("send_type")==null?"":map.get("send_type").toString()).equals("")) {
								if(!(map.get("send_phone")==null?"":map.get("send_phone").toString()).equals("")) {
									lists.add(model);
								}
							}
							maps.put(parentId, lists);
							models.add(model);
						}
					}
					
					for(int i=0;i<models.size();i++) {
						final int i_=i;
						View view_p=LayoutInflater.from(ShareRedEnvelopesRecordActivity.this).inflate(R.layout.view_shareredenvelopesrecordparent, null);
						LinearLayout view_shareredenvelopesrecordparent_childlist=(LinearLayout) view_p.findViewById(R.id.view_shareredenvelopesrecordparent_childlist);
						for(int j=0;j<maps.get(models.get(i).getId()).size();j++) {
							View view_c=LayoutInflater.from(ShareRedEnvelopesRecordActivity.this).inflate(R.layout.view_shareredenvelopesrecordchild, null);
							if(j%2==0) {
								view_c.setBackgroundColor(Color.WHITE);
							}
							else {
								view_c.setBackgroundColor(Color.parseColor("#eff7fc"));
							}
							TextView view_shareredenvelopesrecordchild_name=(TextView) view_c.findViewById(R.id.view_shareredenvelopesrecordchild_name);
							view_shareredenvelopesrecordchild_name.setText(maps.get(models.get(i).getId()).get(j).getSend_phone());
							TextView view_shareredenvelopesrecordchild_type=(TextView) view_c.findViewById(R.id.view_shareredenvelopesrecordchild_type);
							if(maps.get(models.get(i).getId()).get(j).getSend_type().equals("1")) {
								view_shareredenvelopesrecordchild_type.setText("随机派送");
							}
							else if(maps.get(models.get(i).getId()).get(j).getSend_type().equals("2")) {
								view_shareredenvelopesrecordchild_type.setText("指定派送");
							}
							TextView view_shareredenvelopesrecordchild_num=(TextView) view_c.findViewById(R.id.view_shareredenvelopesrecordchild_num);
							view_shareredenvelopesrecordchild_num.setText(maps.get(models.get(i).getId()).get(j).getSend_amount()+"M");
							TextView view_shareredenvelopesrecordchild_time=(TextView) view_c.findViewById(R.id.view_shareredenvelopesrecordchild_time);
							view_shareredenvelopesrecordchild_time.setText(maps.get(models.get(i).getId()).get(j).getSend_time());
							view_shareredenvelopesrecordparent_childlist.addView(view_c);
						}
						TextView view_shareredenvelopesrecordparent_sharedirec=(TextView) view_p.findViewById(R.id.view_shareredenvelopesrecordparent_sharedirec);
						view_shareredenvelopesrecordparent_sharedirec.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								Intent intent=new Intent(ShareRedEnvelopesRecordActivity.this, SendShareRedEnvelopesActivity.class);
								Bundle bundle=new Bundle();
								bundle.putString("total", models.get(i_).getOffer_description());
								bundle.putString("activityId", getIntent().getExtras().getString("activityId"));
								bundle.putString("offer_id", models.get(i_).getOffer_id());
								bundle.putString("orderId", models.get(i_).getId());
								intent.putExtras(bundle);
								startActivity(intent); 
							}
						});
						TextView view_shareredenvelopesrecordparent_sharerandom=(TextView) view_p.findViewById(R.id.view_shareredenvelopesrecordparent_sharerandom);
						view_shareredenvelopesrecordparent_sharerandom.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View v) {
								// TODO Auto-generated method stub
								sendRedPackets(Long.parseLong(models.get(i_).getOffer_id()), models.get(i_).getId());
							}
						});
						TextView view_shareredenvelopesrecordparent_total=(TextView) view_p.findViewById(R.id.view_shareredenvelopesrecordparent_total);
						view_shareredenvelopesrecordparent_total.setText(models.get(i).getOffer_description().subSequence(0, models.get(i).getOffer_description().length()-1));
						if(models.get(i).getSend_type().equals("")) {
							view_shareredenvelopesrecordparent_sharerandom.setVisibility(View.VISIBLE);
							view_shareredenvelopesrecordparent_sharedirec.setVisibility(View.VISIBLE);
						}
						else if(models.get(i).getSend_type().equals("1")) {
							view_shareredenvelopesrecordparent_sharerandom.setVisibility(View.VISIBLE);
							view_shareredenvelopesrecordparent_sharedirec.setVisibility(View.GONE);
						}
						else if(models.get(i).getSend_type().equals("2")) {
							view_shareredenvelopesrecordparent_sharerandom.setVisibility(View.GONE);
							view_shareredenvelopesrecordparent_sharedirec.setVisibility(View.GONE);
						}
						shareredenvelopes_layout.addView(view_p);
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
				LinkedList<String> wholeUrl=Util.getWholeUrl(ShareRedEnvelopesRecordActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(ShareRedEnvelopesRecordActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(ShareRedEnvelopesRecordActivity.this);
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(ShareRedEnvelopesRecordActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map[] map=strategyManager.getRedList(Long.parseLong(list.get(0)), list.get(1), Long.parseLong(getIntent().getExtras().getString("activityId")));
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
	
	private void sendRedPackets(final long offerId, final String orderId) {
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
						getRedList();
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
				LinkedList<String> wholeUrl=Util.getWholeUrl(ShareRedEnvelopesRecordActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(ShareRedEnvelopesRecordActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(ShareRedEnvelopesRecordActivity.this);
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(ShareRedEnvelopesRecordActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map map=strategyManager.sendRedPackets(orderId, Long.parseLong(list.get(0)), list.get(1), Long.parseLong(getIntent().getExtras().getString("activityId")), 1, offerId, "");
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
		final Dialog dialog=new Dialog(ShareRedEnvelopesRecordActivity.this, R.style.shareDialog);
		dialog.setCanceledOnTouchOutside(true);
		View view=LayoutInflater.from(ShareRedEnvelopesRecordActivity.this).inflate(R.layout.view_sharelayout, null);
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
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(ShareRedEnvelopesRecordActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				((GasStationApplication) getApplicationContext()).content=getIntent().getExtras().getString("activity_rule");
				Intent intent=new Intent(ShareRedEnvelopesRecordActivity.this, QQActivity.class);
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
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(ShareRedEnvelopesRecordActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				((GasStationApplication) getApplicationContext()).content=getIntent().getExtras().getString("activity_rule");
				SendYixin yixin=new SendYixin();
				yixin.sendYixin(ShareRedEnvelopesRecordActivity.this, getIntent().getExtras().getString("activity_rule"), currentUsedUrl+getIntent().getExtras().getString("activity_url")+"?activityId="+getIntent().getExtras().getString("activityId")+"&seqId="+seqId, getIntent().getExtras().getString("activity_name"), R.drawable.ic_launcher, true);
				
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
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(ShareRedEnvelopesRecordActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				((GasStationApplication) getApplicationContext()).content=getIntent().getExtras().getString("activity_rule");
				SendWeixin weixin=new SendWeixin();
				weixin.sendWeixin(ShareRedEnvelopesRecordActivity.this, getIntent().getExtras().getString("activity_name")+"\n"+getIntent().getExtras().getString("activity_rule"), currentUsedUrl+getIntent().getExtras().getString("activity_url")+"?activityId="+getIntent().getExtras().getString("activityId")+"&seqId="+seqId, getIntent().getExtras().getString("activity_name")+"\n"+getIntent().getExtras().getString("activity_rule"), R.drawable.ic_launcher, true);
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
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(ShareRedEnvelopesRecordActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				((GasStationApplication) getApplicationContext()).content=getIntent().getExtras().getString("activity_rule");
				Intent intent=new Intent(ShareRedEnvelopesRecordActivity.this, WBMainActivity.class);
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
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		getRedList();
	}
}
