package com.linkage.gas_station.jpush;

import java.util.List;

import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.gonglve.CustomStrategyActivity;
import com.linkage.gas_station.login.LoginOutActivity;
import com.linkage.gas_station.main.MainActivity;
import com.linkage.gas_station.more.SuggestActivity_New;
import com.linkage.gas_station.util.Util;

import cn.jpush.android.api.JPushInterface;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class JPushReceiver extends BroadcastReceiver {
	
	public static final String refreshSuggest="refreshSuggest";
	public static final String refreshGonglve="refreshGonglve";
	//推送标志位
	public static final String TuiSong_Flag="type";
	//推送过来的为个性化攻略
	String Gonglve_Flag="1";
	//推送过来的为意见反馈
	String Suggest_Flag="2";

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);  
		List<RunningTaskInfo> runningTasks = manager.getRunningTasks(1);  
		RunningTaskInfo cinfo = runningTasks.get(0);  
		ComponentName component = cinfo.topActivity;  
		
		Bundle bundle=intent.getExtras();
		if(intent.getAction().equals(JPushInterface.ACTION_REGISTRATION_ID)) {
            System.out.println("接收Registration Id : " + bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID));
		}
		else if(intent.getAction().equals(JPushInterface.ACTION_MESSAGE_RECEIVED)) {
            System.out.println("接收到推送下来的自定义消息: " + bundle.getString(JPushInterface.EXTRA_MESSAGE));  

    		//极光推送关闭
    		JPushInterface.stopPush(context.getApplicationContext());
    		((GasStationApplication) context.getApplicationContext()).isJumpToMonitor=false;
    		
    		if("com.linkage.gas_station".equals(component.getPackageName())) {
    			Intent intent_=new Intent();
    	        intent_.setClass(context, LoginOutActivity.class);
    	        intent_.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	        context.startActivity(intent_);
    	        MainActivity.getInstance().not_able_change();
    		}
    		else {
    			((GasStationApplication) context.getApplicationContext()).isShowLoginOut=true;
    		}
    		Util.setLoginOut(context, true);
		}
		else if(intent.getAction().equals(JPushInterface.ACTION_NOTIFICATION_RECEIVED)) {
            System.out.println("接收到推送下来的通知");
            
            //只有意见反馈才在此刷新界面
            if(Util.getJPushResult(bundle.getString(JPushInterface.EXTRA_EXTRA)).equals(Suggest_Flag)) {
            	((GasStationApplication) context.getApplicationContext()).isNewSuggest=true;
                //发送广播出去，使得能够接收到广播的当前页都自动刷新
                Intent i=new Intent();
            	i.setAction(refreshSuggest);
            	context.sendBroadcast(i);
            }
            else if(Util.getJPushResult(bundle.getString(JPushInterface.EXTRA_EXTRA)).equals(Gonglve_Flag)) {
            	((GasStationApplication) context.getApplicationContext()).isNewGonglve=true;
            	//发送广播出去，使得能够接收到广播的当前页都自动刷新
                Intent i=new Intent();
            	i.setAction(refreshGonglve);
            	context.sendBroadcast(i);
            }
		}
		else if(intent.getAction().equals(JPushInterface.ACTION_NOTIFICATION_OPENED)) {
            System.out.println("用户点击打开了通知");  

            if(Util.getJPushResult(bundle.getString(JPushInterface.EXTRA_EXTRA)).equals(Suggest_Flag)) {
            	//此处用来判断当前页
                if("com.linkage.gas_station".equals(component.getPackageName())) {
                	System.out.println(component.getClassName());
                	//当前页即为意见反馈，无需打开页面
                	if(component.getClassName().equals("com.linkage.gas_station.more.SuggestActivity_New")) {
                		return;
                	}
                	//其他页面需要单独打开意见反馈页面
                	else {
                		Intent intent_=new Intent();
            	        intent_.setClass(context, SuggestActivity_New.class);
            	        intent_.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            	        context.startActivity(intent_);
                	}
                }
                //如果不是当前应用，则通过JPushOpenActivity打开首页之后再打开意见反馈，此时不需要刷新界面
                else {           	
                	if(((GasStationApplication) context.getApplicationContext()).tempActivity.size()>0) {
    					for(int i=0;i<((GasStationApplication) context.getApplicationContext()).tempActivity.size();i++) {
    						((GasStationApplication) context.getApplicationContext()).tempActivity.get(i).finish();
    					}
    				}
                	
                	Intent intent_=new Intent();
        	        intent_.setClass(context, JPushOpenActivity.class);
        	        intent_.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        	        context.startActivity(intent_);
                }
            }
            else if(Util.getJPushResult(bundle.getString(JPushInterface.EXTRA_EXTRA)).equals(Gonglve_Flag)) {
            	Intent intent_=new Intent();
    	        intent_.setClass(context, CustomStrategyActivity.class);
    	        intent_.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    	        context.startActivity(intent_);
            }
		}
	}

}
