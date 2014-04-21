package com.linkage.gas_station.market;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.jiayou.JiayouDetaiActivity;
import com.linkage.gas_station.model.OilListModel;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.OilManager;

public class DirectionalFlowActivity extends BaseActivity {
	
	ImageView title_back=null;
	TextView title_name=null;
	TextView direc_buy_button=null;
	TextView direc_buy_money=null;
	
	ListView direc_flow_list=null;
	DirectionalFlowAdapter adapter=null;
	
	ArrayList<OilListModel> model_list=null;
	
	//选中的序号
	int choice_position=-1;
	//选中的imageView
	ImageView choice_position_image=null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_directionalflow);
		
		model_list=new ArrayList<OilListModel>();
		
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
		title_name=(TextView) findViewById(R.id.title_name);
		title_name.setText("特色流量包");
		
		direc_buy_button=(TextView) findViewById(R.id.direc_buy_button);
		direc_buy_button.setOnClickListener(new TextView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(choice_position==-1) {
					showCustomToast("请至少选中一种类型定向流量包");
					return ;
				}
				Intent intent=new Intent();
				Bundle bundle=new Bundle();
				intent.setClass(DirectionalFlowActivity.this, JiayouDetaiActivity.class);
				bundle.putString("offerId", model_list.get(choice_position).getOffer_id());
				bundle.putString("offer_name", model_list.get(choice_position).getOffer_name());
				bundle.putString("offer_description", getResources().getString(R.string.jiayou_desp5));
				bundle.putString("type", "dingxiang_station");
				((GasStationApplication) getApplicationContext()).jumpJiayouFrom=4;
				intent.putExtras(bundle);
				startActivity(intent);
				
			}});
		direc_buy_money=(TextView) findViewById(R.id.direc_buy_money);
		direc_buy_money.setText(Html.fromHtml("<font color='red'>0元</font>"));
		direc_flow_list=(ListView) findViewById(R.id.direc_flow_list);
		direc_flow_list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				ImageView direc_check=(ImageView) view.findViewById(R.id.direc_check);
				if(model_list.get(position).isChecked()) {
					model_list.get(position).setChecked(false);
					direc_check.setImageResource(R.drawable.direc_check_off);
					direc_buy_button.setText("付  款");
					direc_buy_money.setText(Html.fromHtml("<font color='red'>0元</font>"));
					choice_position=-1;
				}
				else {
					boolean isPermitCheck=true;
					for(int i=0;i<model_list.size();i++) {
						if(model_list.get(i).isChecked()) {
							isPermitCheck=false;
							break;
						}
					}
					if(!isPermitCheck) {
						model_list.get(choice_position).setChecked(false);
						choice_position_image.setImageResource(R.drawable.direc_check_off);
					}
					choice_position_image=direc_check;
					choice_position=position;
					model_list.get(position).setChecked(true);
					direc_check.setImageResource(R.drawable.direc_check_on);
					direc_buy_button.setText("付  款（"+1+"）");
					direc_buy_money.setText(Html.fromHtml("<font color='red'>"+model_list.get(position).getOffer_cost().substring(0, model_list.get(position).getOffer_cost().indexOf("元"))+"元</font>"));
				}
			}
		});
		adapter=new DirectionalFlowAdapter(DirectionalFlowActivity.this, model_list, direc_buy_button, direc_buy_money);
		direc_flow_list.setAdapter(adapter);		
		
		loadData();
	}
	
	public void loadData() {
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.obj==null) {
					showCustomToast("定向流量加载失败");
				}
				else {
					Map[] map=(Map[]) msg.obj;
					ArrayList<OilListModel> model_=new ArrayList<OilListModel>();
					for(int i=0;i<map.length;i++) {
						OilListModel model=new OilListModel();
						model.setOffer_id(map[i].get("offer_id").toString());
						model.setOffer_name(map[i].get("offer_name").toString());
						model.setOffer_description(map[i].get("offer_description").toString());
						model.setOffer_type_id(map[i].get("offer_type_id").toString());
						model.setOffer_cost(map[i].get("offer_cost").toString());
						model.setOffer_image_name(map[i].get("offer_image")==null?"":((GasStationApplication) getApplication()).AreaUrl+map[i].get("offer_image").toString());
						model.setOffer_content(Util.convertNull(map[i%map.length].get("offer_content").toString()));	
						model.setChecked(false);
						model_.add(model);
					}
					model_list.addAll(model_);
					adapter.notifyDataSetChanged();
				}
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(DirectionalFlowActivity.this);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(DirectionalFlowActivity.this).get(0):((GasStationApplication) getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(DirectionalFlowActivity.this);
						
						OilManager oilManager=GetWebDate.getYaoHessionFactiory(DirectionalFlowActivity.this).create(OilManager.class, currentUsedUrl+"/hessian/oilManager", getClassLoader());
						Map[] map=oilManager.getOilList(Long.parseLong(list.get(0)), 4, "", list.get(1));
						m.obj=map;
						flag=false;
						((GasStationApplication) getApplicationContext()).AreaUrl=currentUsedUrl;
					} catch(Error e) {
						flag=false;
						m.obj=null;
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
						m.obj=null;
					}
				}
				
				handler.sendMessage(m);
			}}).start();
	}

}
