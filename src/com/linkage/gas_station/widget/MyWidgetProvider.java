package com.linkage.gas_station.widget;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Map;

import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.model.Flow_Group_Model;
import com.linkage.gas_station.model.OutputInfoModel;
import com.linkage.gas_station.util.GetConnData;
import com.linkage.gas_station.util.OutputInfoParse;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.MonitorManager;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.Toast;

public class MyWidgetProvider extends AppWidgetProvider {
	
	final String CLICK_ACTION="com.linkage.gas_station.action.widget.click";
	final String REFRESH_ACTION="com.linkage.gas_station.action.widget.refresh";
	RemoteViews rv=null;
	
	boolean isRefresh=false;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		super.onReceive(context, intent);		
		if(rv==null) {
			rv=new RemoteViews(context.getPackageName(), R.layout.view_widget);
		}
		if(intent.getAction().equals(CLICK_ACTION)) {
			if(!isRefresh) {
				getData(context, rv);
			}
		}
		AppWidgetManager manager=AppWidgetManager.getInstance(context);
		int[] appIds = manager.getAppWidgetIds(new ComponentName(context, MyWidgetProvider.class));
		manager.updateAppWidget(appIds, rv);
	}

	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager,
			int[] appWidgetIds) {
		// TODO Auto-generated method stub
		System.out.println("onUpdate");
		super.onUpdate(context, appWidgetManager, appWidgetIds);
		final int num=appWidgetIds.length;
		for(int i=0;i<num;i++) {
			int appWidgetId=appWidgetIds[i];
			rv=new RemoteViews(context.getPackageName(), R.layout.view_widget);
			Intent intent=new Intent(CLICK_ACTION);
			PendingIntent pi=PendingIntent.getBroadcast(context, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
			rv.setOnClickPendingIntent(R.id.widget_refresh, pi);
			appWidgetManager.updateAppWidget(appWidgetId, rv);
		}
		if(rv==null) {
			rv=new RemoteViews(context.getPackageName(), R.layout.view_widget);
		}
		if(!isRefresh) {
			getData(context, rv);
		}		
	}

	public void getData(final Context context, final RemoteViews rv) {
		
		final ArrayList<String> list=Util.getUserInfo(context);
		if(list.get(0).equals("")) {
			Toast.makeText(context, "请您先激活流量加油站后再试试", 3000).show();
			return ;
		}
		else {
			if(Util.getUserInfo(context).get(2).equals(Util.convertNull(Util.getIMSINum(context)))) {
				if(Util.isLoginOut(context)) {
					Toast.makeText(context, "请您先激活流量加油站后再试试", 3000).show();
					return ;
				}
			}
			else {
				Toast.makeText(context, "您已切换手机号码，请您先激活流量加油站后再试试", 3000).show();
				return;
			}
		}
		
		isRefresh=true;
		Toast.makeText(context, "正在刷新数据", 3000).show();
		rv.setViewVisibility(R.id.widget_refresh_progress, View.VISIBLE);
		rv.setViewVisibility(R.id.widget_refresh, View.INVISIBLE);
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				super.handleMessage(msg);
				rv.setViewVisibility(R.id.widget_refresh_progress, View.INVISIBLE);
				rv.setViewVisibility(R.id.widget_refresh, View.VISIBLE);
				if(msg.obj!=null) {
					Map map=(Map) msg.obj;
					if(map.get("xml")!=null) {
						String result=map.get("xml").toString();
						if(!result.equals("-1")) {
							OutputInfoParse o=new OutputInfoParse();
							OutputInfoModel model=o.getOutputInfoModel(result, context);
							GetConnData cData=new GetConnData(context);
							cData.deleteMonitor();
							cData.saveMonitor(model);
							
							rv.setTextViewText(R.id.widget_refresh_time, "截至"+model.getData_time()+"的收费流量");
							
							//主副卡位置
							int flow_type_id11_pos=-1;
							//标准流量里找到主副卡的位置，便于之后使用
							for(int j=0;j<model.getModel_list().size();j++) {
								if(model.getModel_list().get(j).getFlow_type_id()==11) {
									flow_type_id11_pos=j;
								}
							}
							//是否存在标准流量
							boolean flow_type_id1_exists=false;
							for(int j=0;j<model.getModel_list().size();j++) {
								if(model.getModel_list().get(j).getFlow_type_id()==1) {
									flow_type_id1_exists=true;
									break;
								}
							}
							//如果没有1只有11，则用11替换1
							if(!flow_type_id1_exists&&flow_type_id11_pos!=-1) {
								model.getModel_list().get(flow_type_id11_pos).setFlow_type_id(1);
								model.getModel_list().get(flow_type_id11_pos).setFlow_type_name("标准流量");
								flow_type_id11_pos=-1;
							}
							
							for(int i=0;i<model.getModel_list().size();i++) {
								//主副卡时候直接跳过
								if(model.getModel_list().get(i).getFlow_type_id()==11) {
									continue;
								}
								Flow_Group_Model fg_model=model.getModel_list().get(i);
								if(fg_model.getFlow_type_id()==1) {									
									rv.setTextViewText(R.id.widget_amount_text_1, fg_model.getFlow_type_amount());
									rv.setTextViewText(R.id.widget_unused_text_1, fg_model.getFlow_type_unused());
									rv.setImageViewResource(R.id.widget_cylinder_1, getImageRes(fg_model, 0, context));
								}
							}

							for(int j=0;j<model.getModel_list().size();j++) {
								try {
									if(model.getModel_list().get(j).getFlow_type_id()==3) {										
										rv.setTextViewText(R.id.widget_amount_text_e_1, model.getModel_list().get(j).getFlow_type_amount());
										rv.setTextViewText(R.id.widget_unused_text_e_1, model.getModel_list().get(j).getFlow_type_unused());
										rv.setImageViewResource(R.id.widget_cylinder_e_1, getImageRes(model.getModel_list().get(j), 1, context));
										if(Util.getUserArea(context).equals("0971")) {
											rv.setTextViewText(R.id.widget_desp_2, "省内流量");
										}
									}
									if(model.getModel_list().get(j).getFlow_type_id()==5) {										
										rv.setTextViewText(R.id.widget_amount_text_e_2, model.getModel_list().get(j).getFlow_type_amount());
										rv.setTextViewText(R.id.widget_unused_text_e_2, model.getModel_list().get(j).getFlow_type_unused());
										rv.setImageViewResource(R.id.widget_cylinder_e_2, getImageRes(model.getModel_list().get(j), 2, context));
									}
								} catch(Exception e) {
									
								}
							}
							
							AppWidgetManager manager=AppWidgetManager.getInstance(context);
							int[] appIds = manager.getAppWidgetIds(new ComponentName(context, MyWidgetProvider.class));
							manager.updateAppWidget(appIds, rv);
						}
					}
					
				}
				isRefresh=false;
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				LinkedList<String> wholeUrl=Util.getWholeUrl(context);
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String currentUsedUrl="";
				try {
					currentUsedUrl=((GasStationApplication) context.getApplicationContext()).AreaUrl.equals("")?Util.getWholeUrl(context).get(0):((GasStationApplication) context.getApplicationContext()).AreaUrl;
				} catch(Exception e) {
					currentUsedUrl=((GasStationApplication) context.getApplicationContext()).COMMONURL[0];
				}
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(context);
						
						MonitorManager monitorManager=GetWebDate.getHessionFactiory(context).create(MonitorManager.class, currentUsedUrl+"/hessian/monitorManager", context.getClassLoader());						
						Map result=monitorManager.getMonitorData2(Long.parseLong(list.get(0)), list.get(1));
						m.obj=result;
						flag=false;
						((GasStationApplication) context.getApplicationContext()).AreaUrl=currentUsedUrl;
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
	
	/**
	 * 返回图片id
	 * @param ratio
	 * @return
	 */
	public int getImageRes(Flow_Group_Model fg_model, int pos, Context context) {
		double unused=Double.parseDouble(fg_model.getFlow_type_unused().substring(0, fg_model.getFlow_type_unused().length()-2));
		double amount=Double.parseDouble(fg_model.getFlow_type_amount().substring(0, fg_model.getFlow_type_amount().length()-2));
		int ratio=(int) (unused*100/amount);
		String imageId=String.valueOf(ratio/10)+String.valueOf(ratio%10>=5?5:0);
		//需要判断是不是小于5并且大于0
		if(imageId.equals("00")&&ratio%10>0) {
			imageId="05";
		}
		int res_id=0;
		if(pos==0) {
			res_id=context.getResources().getIdentifier(context.getPackageName()+":drawable/cylinder_"+imageId, null,null);
		}
		else if(pos==1) {
			res_id=context.getResources().getIdentifier(context.getPackageName()+":drawable/cylinder_small_another_"+imageId, null,null);
		}
		else {
			res_id=context.getResources().getIdentifier(context.getPackageName()+":drawable/cylinder_small_"+imageId, null,null);
		}
		return res_id;
	}
}
