package com.linkage.gas_station.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AlarmReceiver extends BroadcastReceiver {
	
	public final static String ALARM_RECEIVER="alarmReceiver";
	public final static String ALARM_SEND="alarmSend";

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if(intent.getAction().equals(ALARM_RECEIVER)) {
			Intent intent_alarm=new Intent();
			intent_alarm.setAction(ALARM_SEND);
			context.sendBroadcast(intent_alarm);			
		}
	}

}
