package com.linkage.gas_station.gonglve;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.share.SelectContactsActivity;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.StrategyManager;

public class SendShareRedEnvelopesActivity extends BaseActivity {
	
	LinearLayout shareredenvelopes_add_layout=null;
	ImageView shareredenvelopes_add=null;
	TextView sendshareredenvelopes_total=null;
	ImageView shareredenvelopes_sendnow=null;
	
	ArrayList<View> views=null;
	
	//提交时间戳
	long currentPayTime=0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_sendshareredenvelopes);
		
		views=new ArrayList<View>();
		
		init();
	}
	
	private void init() {
		shareredenvelopes_add_layout=(LinearLayout) findViewById(R.id.shareredenvelopes_add_layout);
		addView(false);
		shareredenvelopes_add=(ImageView) findViewById(R.id.shareredenvelopes_add);
		shareredenvelopes_add.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				addView(true);
			}});
		sendshareredenvelopes_total=(TextView) findViewById(R.id.sendshareredenvelopes_total);
		sendshareredenvelopes_total.setText(getIntent().getExtras().getString("total").substring(0, getIntent().getExtras().getString("total").length()-1));
		shareredenvelopes_sendnow=(ImageView) findViewById(R.id.shareredenvelopes_sendnow);
		shareredenvelopes_sendnow.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				String params="";
				int totalNum=0;
				for(int i=0;i<views.size();i++) {
					EditText view_shareredenvelopes_num=(EditText) views.get(i).findViewById(R.id.view_shareredenvelopes_num);
					EditText view_shareredenvelopes_phone=(EditText) views.get(i).findViewById(R.id.view_shareredenvelopes_phone);
					if(view_shareredenvelopes_phone.getText().toString().trim().equals("")||view_shareredenvelopes_phone.getText().toString().trim().equals("")) {
						continue;
					}
					if(i==views.size()-1) {
						params+=(view_shareredenvelopes_phone.getText().toString()+":"+view_shareredenvelopes_num.getText().toString());
					}
					else {
						params+=(view_shareredenvelopes_phone.getText().toString()+":"+view_shareredenvelopes_num.getText().toString())+",";
					}
					totalNum+=Integer.parseInt(view_shareredenvelopes_num.getText().toString().trim());
				}
				if(totalNum!=Integer.parseInt(getIntent().getExtras().getString("total").substring(0, getIntent().getExtras().getString("total").length()-1))) {
					showCustomToast("直接派送的流量不等于可派发的流量，请重新检查");
					return;
				}
				if(totalNum==0) {
					showCustomToast("请您输入派送流量");
					return;
				}
				System.out.println(params);
				sendRedPackets(Long.parseLong(getIntent().getExtras().getString("offer_id")), params);
			}});
	}
	
	private void addView(boolean showDelete) {
		final View view=LayoutInflater.from(this).inflate(R.layout.view_shareredenvelopes, null);
		view.setTag(""+System.currentTimeMillis());
		ImageView view_shareredenvelopes_delete=(ImageView) view.findViewById(R.id.view_shareredenvelopes_delete);
		view_shareredenvelopes_delete.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				for(int i=0;i<views.size();i++) {
					if(view.getTag().toString().equals(views.get(i).getTag().toString())) {
						shareredenvelopes_add_layout.removeView(view);
						views.remove(view);
					}
				}
				
			}});
		ImageView view_shareredenvelopes_phone_contact=(ImageView) view.findViewById(R.id.view_shareredenvelopes_phone_contact);
		view_shareredenvelopes_phone_contact.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(((GasStationApplication) getApplicationContext()).getModel_list()==null) {
					Util.getContactData(SendShareRedEnvelopesActivity.this);
				}
				//数据状态清除
				if(((GasStationApplication) getApplicationContext()).getModel_list()!=null) {
					for(int i=0;i<((GasStationApplication) getApplicationContext()).getModel_list().size();i++) {
						((GasStationApplication) getApplicationContext()).getModel_list().get(i).setChecked(false);
					}
				}
				Intent intent=new Intent(SendShareRedEnvelopesActivity.this, SelectContactsActivity.class);
				Bundle bundle=new Bundle();
				bundle.putString("tag", view.getTag().toString());
				bundle.putInt("type", 0);
				intent.putExtras(bundle);
				startActivityForResult(intent, 100);
			}});
		if(showDelete) {
			view_shareredenvelopes_delete.setVisibility(View.VISIBLE);
		}
		else {
			view_shareredenvelopes_delete.setVisibility(View.GONE);
		}
		views.add(view);
		shareredenvelopes_add_layout.addView(view);
	}
	
	private void sendRedPackets(final long offerId, final String params) {
		currentPayTime=0;
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.what==1) {
					Map map=(Map) msg.obj;	
					showCustomToast(map.get("comments").toString());
					finish();
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
				LinkedList<String> wholeUrl=Util.getWholeUrl(SendShareRedEnvelopesActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(SendShareRedEnvelopesActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(SendShareRedEnvelopesActivity.this);
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(SendShareRedEnvelopesActivity.this).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getClassLoader());
						Map map=strategyManager.sendRedPackets(getIntent().getExtras().getString("orderId"), Long.parseLong(list.get(0)), list.get(1), Long.parseLong(getIntent().getExtras().getString("activityId")), 2, offerId, params);
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if(resultCode==RESULT_OK&&requestCode==100) {
			for(int i=0;i<views.size();i++) {
				if(views.get(i).getTag().equals(data.getExtras().getString("tag"))) {
					EditText view_shareredenvelopes_phone=(EditText) views.get(i).findViewById(R.id.view_shareredenvelopes_phone);
					view_shareredenvelopes_phone.setText(data.getExtras().getString("num"));
				}
			}
		}
	}
}
