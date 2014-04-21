package com.linkage.gas_station.yxapi;

import java.util.Date;

import im.yixin.sdk.api.YXAPIBaseBroadcastReceiver;
import im.yixin.sdk.channel.YXMessageProtocol;

public class AppRegister extends YXAPIBaseBroadcastReceiver {

	@Override
	protected String getAppId() {
		// TODO Auto-generated method stub
		return "yxf2ad8632e7cf45b08a8069a0440989cf";
	}
	
	@Override
	protected void onAfterYixinStart(YXMessageProtocol protocol) {
		// TODO Auto-generated method stub
		super.onAfterYixinStart(protocol);
		System.out.println("ClientonAfterYixinStart@"+(new Date())+",AppId="+protocol.getAppId()+",Command="+protocol.getCommand()+",SdkVersion="+protocol.getSdkVersion()+",appPackage="+protocol.getAppPackage());
	}

}
