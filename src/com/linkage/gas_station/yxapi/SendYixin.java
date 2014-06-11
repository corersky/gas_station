package com.linkage.gas_station.yxapi;

import com.linkage.gas_station.R;

import im.yixin.sdk.api.IYXAPI;
import im.yixin.sdk.api.SendMessageToYX;
import im.yixin.sdk.api.YXAPIFactory;
import im.yixin.sdk.api.YXMessage;
import im.yixin.sdk.api.YXWebPageMessageData;
import im.yixin.sdk.util.BitmapUtil;
import android.content.Context;
import android.graphics.BitmapFactory;

public class SendYixin {
	public void sendYixin(Context context, String text, String url, String title, boolean isFriend) {
		IYXAPI api=YXAPIFactory.createYXAPI(context, "yxf2ad8632e7cf45b08a8069a0440989cf");
		api.registerApp();
		
		YXWebPageMessageData webpage=new YXWebPageMessageData();
		webpage.webPageUrl=url;
		YXMessage msg=new YXMessage(webpage);
		msg.title=title;
		msg.description=text;
		msg.thumbData=BitmapUtil.bmpToByteArray(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher), true);
		SendMessageToYX.Req req=new SendMessageToYX.Req();
		req.transaction=buildTransaction("webpage");
		req.message=msg;
		req.scene=isFriend?SendMessageToYX.Req.YXSceneTimeline:SendMessageToYX.Req.YXSceneSession;
		api.sendRequest(req);
	}
	
	private String buildTransaction(final String type) {
		return (type ==null)?String.valueOf(System.currentTimeMillis()):type+System.currentTimeMillis();
	}
}
