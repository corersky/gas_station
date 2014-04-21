package com.linkage.gas_station.service;

import java.util.ArrayList;
import java.util.Set;

import com.linkage.gas_station.R;
import com.linkage.gas_station.util.Util;

import cn.jpush.android.api.BasicPushNotificationBuilder;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class GuardService extends Service {
	
	AlarmManager mAlarmManager = null;
	PendingIntent mPendingIntent = null;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		Intent intent=new Intent(getApplicationContext(), GuardService.class);        
		mAlarmManager=(AlarmManager)getSystemService(ALARM_SERVICE);
		mPendingIntent=PendingIntent.getService(this, 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);
		long now = System.currentTimeMillis();
		mAlarmManager.setInexactRepeating(AlarmManager.RTC, now, 3000, mPendingIntent);
		
		super.onCreate();
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		ArrayList<String> list=Util.getUserInfo(GuardService.this);
		if(!list.get(0).equals("")&&!Util.isLoginOut(GuardService.this)&&JPushInterface.isPushStopped(getApplicationContext())) {
			JPushInterface.resumePush(getApplicationContext());
			BasicPushNotificationBuilder builder = new BasicPushNotificationBuilder(getApplicationContext());
			builder.statusBarDrawable = R.drawable.ic_launcher;
			builder.notificationFlags = Notification.FLAG_AUTO_CANCEL;  //设置为点击后自动消失
			builder.notificationDefaults =  Notification.DEFAULT_VIBRATE;  //设置为铃声（ Notification.DEFAULT_SOUND）或者震动（ Notification.DEFAULT_VIBRATE）  
			JPushInterface.setPushNotificationBuilder(1, builder);
			//定义jpush别名
			JPushInterface.setAlias(getApplicationContext(), Util.getDeviceId(GuardService.this)+Util.getMacAddress(GuardService.this), new TagAliasCallback() {

				@Override
				public void gotResult(int arg0, String arg1,
						Set<String> arg2) {
					// TODO Auto-generated method stub
					System.out.println("极光推送返回："+arg0);
				}});
		}
        return START_STICKY;
	}
	
}
