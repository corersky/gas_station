package com.linkage.gas_station.gonglve;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

import com.linkage.gas_station.BaseActivity;
import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
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
import android.widget.TextView;

public class RankingFragment extends Fragment {
	
	View view=null;
	
	LinearLayout fragment_ranking_search=null;
	TextView ranking_me=null;
	TextView ranking_time=null;
	TextView ranking_fast_num=null;
	TextView ranking_love_num=null;
	TextView ranking_fast_desp=null;
	TextView ranking_love_desp=null;
	
	TextView xuzhou=null;
	TextView lianyungang=null;
	TextView suqian=null;
	TextView huaian=null;
	TextView yancheng=null;
	TextView yangzhou=null;
	TextView taizhou=null;
	TextView nanjing=null;
	TextView zhenjiang=null;
	TextView changzhou=null;
	TextView wuxi=null;
	TextView suzhou=null;
	TextView nantong=null;
	TextView ranking_whole=null;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		if(view==null) {
			view=LayoutInflater.from(getActivity()).inflate(R.layout.fragment_ranking, null);
			ranking_me=(TextView) view.findViewById(R.id.ranking_me);
			ranking_me.setText("尊敬的"+Util.getUserInfo(getActivity()).get(0)+"，您好：");
			ranking_time=(TextView) view.findViewById(R.id.ranking_time);
			SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd");
			ranking_time.setText("截止"+df.format(new Date()));
			ranking_fast_num=(TextView) view.findViewById(R.id.ranking_fast_num);
			ranking_love_num=(TextView) view.findViewById(R.id.ranking_love_num);
			ranking_fast_desp=(TextView) view.findViewById(R.id.ranking_fast_desp);
			ranking_love_desp=(TextView) view.findViewById(R.id.ranking_love_desp);
			
			fragment_ranking_search=(LinearLayout) view.findViewById(R.id.fragment_ranking_search);
			fragment_ranking_search.setOnClickListener(new LinearLayout.OnClickListener() {

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
			ranking_whole=(TextView) view.findViewById(R.id.ranking_whole);
			xuzhou=(TextView) view.findViewById(R.id.xuzhou);
			lianyungang=(TextView) view.findViewById(R.id.lianyungang);
			suqian=(TextView) view.findViewById(R.id.suqian);
			huaian=(TextView) view.findViewById(R.id.huaian);
			yancheng=(TextView) view.findViewById(R.id.yancheng);
			yangzhou=(TextView) view.findViewById(R.id.yangzhou);
			taizhou=(TextView) view.findViewById(R.id.taizhou);
			nanjing=(TextView) view.findViewById(R.id.nanjing);
			zhenjiang=(TextView) view.findViewById(R.id.zhenjiang);
			changzhou=(TextView) view.findViewById(R.id.changzhou);
			wuxi=(TextView) view.findViewById(R.id.wuxi);
			suzhou=(TextView) view.findViewById(R.id.suzhou);
			nantong=(TextView) view.findViewById(R.id.nantong);
			
			redPacketsPerson();
		}
		ViewGroup parent=(ViewGroup) view.getParent();
		if(parent!=null) {
			parent.removeView(view);
		}
		return view;
	}
	
	private void redPacketsPerson() {
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				if(msg.what==100) {
					Map map=(Map) msg.obj;
					if(map.get("quicker")!=null) {
						if(((Map)map.get("quicker")).get("phone_num")!=null) {
							ranking_love_num.setText(((Map)map.get("quicker")).get("phone_num").toString());
						}
						if(((Map)map.get("quicker")).get("times")!=null) {
							ranking_love_desp.setText("抢得"+((Map)map.get("quicker")).get("times").toString()+"个红包，被评为圈中");
							
						}
					}
					if(map.get("shorter")!=null) {
						if(((Map)map.get("shorter")).get("phone_num")!=null) {
							ranking_fast_num.setText(((Map)map.get("shorter")).get("phone_num").toString());
						}
						if(((Map)map.get("shorter")).get("keep_time")!=null) {
							int extraTime=(int) Double.parseDouble(((Map)map.get("shorter")).get("keep_time").toString());
							if(extraTime<60) {
								ranking_fast_desp.setText(extraTime+"秒"+"抢到红包，被评为圈中  ");
							}
							else if(extraTime>=60&&extraTime<3600) {
								ranking_fast_desp.setText(extraTime/60+"分"+(extraTime-extraTime/60*60)+"秒"+"抢到红包，被评为圈中  ");
							}
							else if(extraTime>=3600&&extraTime<3600*24) {
								ranking_fast_desp.setText(extraTime/3600+"小时"+(extraTime-extraTime/3600*3600)/60+"分"+(extraTime-extraTime/3600*3600-(extraTime-extraTime/3600*3600)/60*60)+"秒"+"抢到红包，被评为圈中  ");
							}
							else if(extraTime>=3600*24) {
								ranking_fast_desp.setText(extraTime/(3600*24)+"天"+(extraTime-extraTime/(3600*24)*(3600*24))/3600+"小时"
										+(extraTime-extraTime/(3600*24)*(3600*24)-(extraTime-extraTime/(3600*24)*(3600*24))/3600*3600)/60+"分"
										+(extraTime-extraTime/(3600*24)*(3600*24)-(extraTime-extraTime/(3600*24)*(3600*24))/3600*3600-(extraTime-extraTime/(3600*24)*(3600*24)-(extraTime-extraTime/(3600*24)*(3600*24))/3600*3600)/60*60)+"秒"+"抢到红包，被评为圈中  ");
							}
						}
					}
					if(map.get("areas")!=null) {
						Object[] areas=(Object[]) map.get("areas");
						LinkedHashMap<String, String> map_temp=new LinkedHashMap<String, String>();
						String text_show="";
						for(int i=0;i<areas.length;i++) {
							Map areas_map=(Map) areas[i];
							map_temp.put(areas_map.get("area_name").toString(), areas_map.get("persons").toString());
							text_show+=areas_map.get("area_name").toString()+":"+areas_map.get("persons").toString()+"人\n";
						}
						ranking_whole.setText(text_show);
						for(int i=0;i<map_temp.size();i++) {							
							if(map_temp.get("连云港")!=null) {
								lianyungang.setText("x"+map_temp.get("连云港").toString());
							}
							else {
								lianyungang.setVisibility(View.GONE);
							}
							
							if(map_temp.get("徐州")!=null) {
								xuzhou.setText("x"+map_temp.get("徐州").toString());
							}
							else {
								xuzhou.setVisibility(View.GONE);
							}
							
							if(map_temp.get("宿迁")!=null) {
								suqian.setText("x"+map_temp.get("宿迁").toString());
							}
							else {
								suqian.setVisibility(View.GONE);
							}
							
							if(map_temp.get("淮安")!=null) {
								huaian.setText("x"+map_temp.get("淮安").toString());
							}
							else {
								huaian.setVisibility(View.GONE);
							}
							
							if(map_temp.get("盐城")!=null) {
								yancheng.setText("x"+map_temp.get("盐城").toString());
							}
							else {
								yancheng.setVisibility(View.GONE);
							}
							
							if(map_temp.get("扬州")!=null) {
								yangzhou.setText("x"+map_temp.get("扬州").toString());
							}
							else {
								yangzhou.setVisibility(View.GONE);
							}
							
							if(map_temp.get("泰州")!=null) {
								taizhou.setText("x"+map_temp.get("泰州").toString());
							}
							else {
								taizhou.setVisibility(View.GONE);
							}
							
							if(map_temp.get("南京")!=null) {
								nanjing.setText("x"+map_temp.get("南京").toString());
							}
							else {
								nanjing.setVisibility(View.GONE);
							}
							
							if(map_temp.get("镇江")!=null) {
								zhenjiang.setText("x"+map_temp.get("镇江").toString());
							}
							else {
								zhenjiang.setVisibility(View.GONE);
							}
							
							if(map_temp.get("南通")!=null) {
								nantong.setText("x"+map_temp.get("南通").toString());
							}
							else {
								nantong.setVisibility(View.GONE);
							}
							
							if(map_temp.get("常州")!=null) {
								changzhou.setText("x"+map_temp.get("常州").toString());
							}
							else {
								changzhou.setVisibility(View.GONE);
							}
							
							if(map_temp.get("无锡")!=null) {
								wuxi.setText("x"+map_temp.get("无锡").toString());
							}
							else {
								wuxi.setVisibility(View.GONE);
							}
							
							if(map_temp.get("苏州")!=null) {
								suzhou.setText("x"+map_temp.get("苏州").toString());
							}
							else {
								suzhou.setVisibility(View.GONE);
							}

						}
					}
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
						Map result=strategyManager.redPacketsPerson(Long.parseLong(list.get(0)), list.get(1), Long.parseLong(getActivity().getIntent().getExtras().getString("activityId")));
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
