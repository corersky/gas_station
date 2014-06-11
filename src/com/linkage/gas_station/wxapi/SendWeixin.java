package com.linkage.gas_station.wxapi;

import android.content.Context;
import android.graphics.BitmapFactory;

import com.linkage.gas_station.R;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.mm.sdk.platformtools.Util;

public class SendWeixin {

	public String sendWeixin(Context context, String text, String url, String title, boolean isFriend) {
		IWXAPI api=WXAPIFactory.createWXAPI(context, "wx60c8073acffe26c7");
		int wxSdkVersion = api.getWXAppSupportAPI();
		if (wxSdkVersion >= 0x21020001) {
			api.registerApp("wx60c8073acffe26c7");    
			WXWebpageObject webpage=new WXWebpageObject();
			webpage.webpageUrl=url;
			WXMediaMessage msg=new WXMediaMessage(webpage);
			msg.title=title;
			msg.description=text;
			msg.thumbData=Util.bmpToByteArray(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher), true);
			
			SendMessageToWX.Req req=new SendMessageToWX.Req();
			req.transaction=buildTransaction("webpage");
			req.message=msg;
			req.scene=isFriend?SendMessageToWX.Req.WXSceneTimeline:SendMessageToWX.Req.WXSceneSession;
			api.sendReq(req);
			return "";
		} else {
			return "您当前使用的微信版本过低或未安装，分享失败";
		}
	}

	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}


}
