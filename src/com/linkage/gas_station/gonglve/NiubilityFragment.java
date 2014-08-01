package com.linkage.gas_station.gonglve;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import com.hb.views.PinnedSectionListView;
import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.model.NiubilityModel;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.StrategyManager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

public class NiubilityFragment extends Fragment {
	
	PinnedSectionListView niubilitylist=null;
	NiubilityAdapter adapter=null;
	LinearLayout fragment_niubility_search=null;
	
	View view=null;
	
	ArrayList<NiubilityModel> models=null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(view==null) {
			models=new ArrayList<NiubilityModel>();
			//loadData();
			view=LayoutInflater.from(getActivity()).inflate(R.layout.fragment_niubility, container, false);
			niubilitylist=(PinnedSectionListView) view.findViewById(R.id.niubilitylist);
			adapter=new NiubilityAdapter(getActivity(), models);
			niubilitylist.setAdapter(adapter);
			fragment_niubility_search=(LinearLayout) view.findViewById(R.id.fragment_niubility_search);
			fragment_niubility_search.setOnClickListener(new LinearLayout.OnClickListener() {

				@Override
				public void onClick(View arg0) {
					// TODO Auto-generated method stub
					Intent intent=new Intent(getActivity(), RedenvelopeSearchActivity.class);
					Bundle bundle=new Bundle();
					bundle.putString("activityId", getActivity().getIntent().getExtras().getString("activityId"));
					bundle.putString("activity_rule", getActivity().getIntent().getExtras().getString("activity_rule"));
					bundle.putString("activity_name", getActivity().getIntent().getExtras().getString("activity_name"));
					bundle.putString("activity_url", getActivity().getIntent().getExtras().getString("activity_url"));
					intent.putExtras(bundle);
					startActivity(intent);
				}});
			redPacketsRank();
		}
		ViewGroup parent=(ViewGroup) view.getParent(); 
		if(parent!=null) {
			parent.removeView(view);
		}
		return view;
	}
	
	private void redPacketsRank() {
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				
				if(msg.what==100) {
					Map map=(Map) msg.obj;
					
					NiubilityModel model1=new NiubilityModel();
					model1.setType(1);
					model1.setPosition(0);
					SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");//设置日期格式
					model1.setEndTime("截止"+df.format(new Date()));
					model1.setEndtime_desp("最短抢光时间");
					model1.setSection_desp("人缘最好");
					models.add(model1);
					if(map.get("shorters")!=null) {
						Object[] arrays=(Object[]) map.get("shorters");
						for(int i=0;i<arrays.length;i++) {
							Map map_=(Map) arrays[i];
							NiubilityModel model=new NiubilityModel();
							model.setType(0);
							int extraTime=(int) Double.parseDouble(map_.get("info").toString());
							if(extraTime<60) {
								model.setItem_desp(extraTime+"秒");
							}
							else if(extraTime>=60&&extraTime<3600) {
								model.setItem_desp(extraTime/60+"分"+(extraTime-extraTime/60*60)+"秒");
							}
							else if(extraTime>=3600&&extraTime<3600*24) {
								model.setItem_desp(extraTime/3600+"小时"+(extraTime-extraTime/3600*3600)/60+"分"+(extraTime-extraTime/3600*3600-(extraTime-extraTime/3600*3600)/60*60)+"秒");
							}
							else if(extraTime>=3600*24) {
								model.setItem_desp(extraTime/(3600*24)+"天"+(extraTime-extraTime/(3600*24)*(3600*24))/3600+"小时"
										+(extraTime-extraTime/(3600*24)*(3600*24)-(extraTime-extraTime/(3600*24)*(3600*24))/3600*3600)/60+"分"
										+(extraTime-extraTime/(3600*24)*(3600*24)-(extraTime-extraTime/(3600*24)*(3600*24))/3600*3600-(extraTime-extraTime/(3600*24)*(3600*24)-(extraTime-extraTime/(3600*24)*(3600*24))/3600*3600)/60*60)+"秒");
							}
							model.setPosition(Integer.parseInt(map_.get("rn").toString()));
							model.setPhoneNum(map_.get("phone_num").toString());
							if(arrays.length==i+1) {
								model.setLast(true);
							}
							else {
								model.setLast(false);
							}
							models.add(model);
						}
					}
					
					NiubilityModel model2=new NiubilityModel();
					model2.setType(1);
					model2.setPosition(1);
					model2.setEndTime("");
					model2.setEndtime_desp("参与抢红包的朋友");
					model2.setSection_desp("圈子最广");
					models.add(model2);
					if(map.get("widers")!=null) {
						Object[] arrays=(Object[]) map.get("widers");
						for(int i=0;i<arrays.length;i++) {
							Map map_=(Map) arrays[i];
							NiubilityModel model=new NiubilityModel();
							model.setType(0);
							model.setItem_desp(map_.get("info").toString());
							model.setPosition(Integer.parseInt(map_.get("rn").toString()));
							model.setPhoneNum(map_.get("phone_num").toString());
							if(arrays.length==i+1) {
								model.setLast(true);
							}
							else {
								model.setLast(false);
							}
							models.add(model);
						}
					}
					
					NiubilityModel model3=new NiubilityModel();
					model3.setType(1);
					model3.setPosition(2);
					model3.setEndTime("");
					model3.setEndtime_desp("派发红包金额");
					model3.setSection_desp("最土豪");
					models.add(model3);
					if(map.get("richers")!=null) {
						Object[] arrays=(Object[]) map.get("richers");
						for(int i=0;i<arrays.length;i++) {
							Map map_=(Map) arrays[i];
							NiubilityModel model=new NiubilityModel();
							model.setType(0);
							model.setItem_desp(map_.get("info").toString());
							model.setPosition(Integer.parseInt(map_.get("rn").toString()));
							model.setPhoneNum(map_.get("phone_num").toString());
							if(arrays.length==i+1) {
								model.setLast(true);
							}
							else {
								model.setLast(false);
							}
							models.add(model);
						}
					}
					
					adapter.notifyDataSetChanged();
				}
				else if(msg.what==-2) {
					BaseActivity.showCustomToastWithContext("链路连接失败", getActivity());
				}
				else {
					BaseActivity.showCustomToastWithContext(getResources().getString(R.string.timeout_exp), getActivity());
				}
			}
		};
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(getActivity());
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) getActivity().getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(getActivity()).get(0):((GasStationApplication) getActivity().getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) getActivity().getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(getActivity());
						
						StrategyManager strategyManager=GetWebDate.getHessionFactiory(getActivity()).create(StrategyManager.class, currentUsedUrl+"/hessian/strategyManager", getActivity().getClassLoader());
						Map result=strategyManager.redPacketsRank(Long.parseLong(list.get(0)), list.get(1), Long.parseLong(getActivity().getIntent().getExtras().getString("activityId")), 0);
						if(result==null) {
							result=new HashMap();
						}
						m.what=100;
						m.obj=result;
						flag=false;
						((GasStationApplication) getActivity().getApplicationContext()).AreaUrl=currentUsedUrl;
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
}
