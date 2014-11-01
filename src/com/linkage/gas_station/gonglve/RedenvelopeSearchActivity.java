package com.linkage.gas_station.gonglve;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.model.RedPacketsModel;
import com.linkage.gas_station.qqapi.QQActivity;
import com.linkage.gas_station.sinaweiboapi.WBMainActivity;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.StrategyManager;
import com.linkage.gas_station.wxapi.SendWeixin;
import com.linkage.gas_station.yxapi.SendYixin;

public class RedenvelopeSearchActivity extends BaseActivity {
	
	TextView title_name=null;
	ImageView title_back=null;
	
	TextView redenvelope_phone=null;
	ListView redenvelope_list=null;
	RedenvelopeAdapter adapter=null;
	TextView redenvelope_list_no=null;
	
	ArrayList<RedPacketsModel> strs=null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_redenvelope);
		
		((GasStationApplication) getApplication()).tempActivity.add(RedenvelopeSearchActivity.this);
		
		strs=new ArrayList<RedPacketsModel>();
		
		init();
	}
	
	private void init() {
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText("红包记录");
		title_back=(ImageView) findViewById(R.id.title_back);
		title_back.setVisibility(View.VISIBLE);
		title_back.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				finish();
			}});
		
		redenvelope_phone=(TextView) findViewById(R.id.redenvelope_phone);
		redenvelope_phone.setText(Util.getUserInfo(RedenvelopeSearchActivity.this).get(0)+"用户，您好：");
		redenvelope_list=(ListView) findViewById(R.id.redenvelope_list);
		redenvelope_list_no=(TextView) findViewById(R.id.redenvelope_list_no);
		adapter=new RedenvelopeAdapter(RedenvelopeSearchActivity.this, strs, this);
		redenvelope_list.setAdapter(adapter);
		buyRedPackets();
		
	}
	
	private void buyRedPackets() {
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.what==100) {
					Object[] obj=(Object[]) msg.obj;
					for(int i=0;i<obj.length;i++) {
						Map map=(Map) obj[i];
						RedPacketsModel model=new RedPacketsModel();
						model.setInterface_id(map.get("interface_id").toString());
						model.setGenerate_time(map.get("generate_time").toString());
						model.setOffer_name(map.get("offer_name").toString());
						model.setResidue_times(Integer.parseInt(map.get("total_times").toString())-Integer.parseInt(map.get("residue_times").toString()));
						model.setTotal_times(Integer.parseInt(map.get("total_times").toString()));
						strs.add(model);
					}
					adapter.notifyDataSetChanged();
					if(obj.length==0) {
						redenvelope_list_no.setVisibility(View.VISIBLE);
					}
				}
				else if(msg.what==-2) {
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
				LinkedList<String> wholeUrl=Util.getWholeUrl(RedenvelopeSearchActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(RedenvelopeSearchActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(RedenvelopeSearchActivity.this);
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(RedenvelopeSearchActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map[] result=strategyManager.buyRedPackets(Long.parseLong(list.get(0)), list.get(1), Long.parseLong(getIntent().getExtras().getString("activityId")));
						if(result==null) {
							result=new Map[0];
						}
						m.what=100;
						m.obj=result;
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
					}
				}
				
				handler.sendMessage(m);				
			}
		}).start();
	}
	
	public void showShare(final String seqId) {
		final Dialog dialog=new Dialog(RedenvelopeSearchActivity.this, R.style.shareDialog);
		dialog.setCanceledOnTouchOutside(true);
		View view=LayoutInflater.from(RedenvelopeSearchActivity.this).inflate(R.layout.view_sharelayout, null);
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
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(RedenvelopeSearchActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				((GasStationApplication) getApplicationContext()).content=getIntent().getExtras().getString("activity_rule");
				Intent intent=new Intent(RedenvelopeSearchActivity.this, QQActivity.class);
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
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(RedenvelopeSearchActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				((GasStationApplication) getApplicationContext()).content=getIntent().getExtras().getString("activity_rule");
				SendYixin yixin=new SendYixin();
				yixin.sendYixin(RedenvelopeSearchActivity.this , getIntent().getExtras().getString("activity_rule"), currentUsedUrl+getIntent().getExtras().getString("activity_url")+"?activityId="+getIntent().getExtras().getString("activityId")+"&seqId="+seqId, getIntent().getExtras().getString("activity_name"), R.drawable.ic_launcher, true);
				
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
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(RedenvelopeSearchActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				((GasStationApplication) getApplicationContext()).content=getIntent().getExtras().getString("activity_rule");
				SendWeixin weixin=new SendWeixin();
				weixin.sendWeixin(RedenvelopeSearchActivity.this, getIntent().getExtras().getString("activity_name")+"\n"+getIntent().getExtras().getString("activity_rule"), currentUsedUrl+getIntent().getExtras().getString("activity_url")+"?activityId="+getIntent().getExtras().getString("activityId")+"&seqId="+seqId, getIntent().getExtras().getString("activity_name")+"\n"+getIntent().getExtras().getString("activity_rule"), R.drawable.ic_launcher, true);
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
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(RedenvelopeSearchActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				((GasStationApplication) getApplicationContext()).content=getIntent().getExtras().getString("activity_rule");
				Intent intent=new Intent(RedenvelopeSearchActivity.this, WBMainActivity.class);
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
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		((GasStationApplication) getApplication()).tempActivity.remove(RedenvelopeSearchActivity.this);
	}
}
