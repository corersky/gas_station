package com.linkage.gas_station.yxapi;

import com.linkage.gas_station.R;

import im.yixin.sdk.api.IYXAPI;
import im.yixin.sdk.api.SendMessageToYX;
import im.yixin.sdk.api.YXAPIFactory;
import im.yixin.sdk.api.YXMessage;
import im.yixin.sdk.api.YXWebPageMessageData;
import im.yixin.sdk.util.BitmapUtil;
import android.app.Activity;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Window;

public class YX_SendActivity extends Activity {
	
	private IYXAPI api;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		api=YXAPIFactory.createYXAPI(this, "yxf2ad8632e7cf45b08a8069a0440989cf");
		api.registerApp();
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		sendText();
	}
	
	private void sendText() {
		YXWebPageMessageData webpage=new YXWebPageMessageData();
		webpage.webPageUrl=getIntent().getExtras().getString("url");
		YXMessage msg=new YXMessage(webpage);
		msg.title=getIntent().getExtras().getString("title");
		msg.description=getIntent().getExtras().getString("text");
		msg.thumbData=BitmapUtil.bmpToByteArray(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher), true);
		SendMessageToYX.Req req=new SendMessageToYX.Req();
		req.transaction=buildTransaction("webpage");
		req.message=msg;
		req.scene=getIntent().getExtras().getBoolean("isFriend")?SendMessageToYX.Req.YXSceneTimeline:SendMessageToYX.Req.YXSceneSession;
		api.sendRequest(req);
		finish();
	}
	
	private String buildTransaction(final String type) {
		return (type ==null)?String.valueOf(System.currentTimeMillis()):type+System.currentTimeMillis();
	}

}
