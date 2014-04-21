package com.linkage.gas_station.receiver;

import java.util.ArrayList;

import com.linkage.gas_station.service.GuardService;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class GuardReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		//全广播服务均可打开守护服务
		System.out.println("由 "+intent.getAction()+" 打开守护服务");
		//重复打开
		if(!isServiceWorked(context, "com.linkage.gas_station.service.GuardService")) {
			System.out.println("守护服务不存在，重启守护服务");
			Intent intent_guard=new Intent(context, GuardService.class);
			intent_guard.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startService(intent_guard);
		}
	}
	  
	/**
	 * 判断服务是否存在
	 * @param context
	 * @return
	 */
	public static boolean isServiceWorked(Context context, String serviceName) {  
		ActivityManager myManager=(ActivityManager)context.getSystemService(Context.ACTIVITY_SERVICE);  
		ArrayList<RunningServiceInfo> runningService = (ArrayList<RunningServiceInfo>) myManager.getRunningServices(30);  
		for(int i = 0 ; i<runningService.size();i++) {  
			if(runningService.get(i).service.getClassName().toString().equals(serviceName)) {  
				return true;  
			}  
		}  
		return false;  
	}
}
