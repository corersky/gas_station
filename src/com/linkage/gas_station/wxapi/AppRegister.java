package com.linkage.gas_station.wxapi;

import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AppRegister extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		final IWXAPI api = WXAPIFactory.createWXAPI(context, "wx60c8073acffe26c7");

		// 将该app注册到微信
		api.registerApp("wx60c8073acffe26c7");
	}

}
