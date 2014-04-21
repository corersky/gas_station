package com.linkage.gas_station.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

import com.linkage.gas_station.R;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXWebpageObject;
import com.tencent.mm.sdk.platformtools.Util;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
	
	// IWXAPI 是第三方app和微信通信的openapi接口
    private IWXAPI api;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		if(getIntent().getExtras().getString("text")==null) {
			finish();
			return;
		}
		
		// 通过WXAPIFactory工厂，获取IWXAPI的实例
    	api=WXAPIFactory.createWXAPI(this, "wx60c8073acffe26c7", false);
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		api.handleIntent(getIntent(), this);
		
		int wxSdkVersion = api.getWXAppSupportAPI();
		if (wxSdkVersion >= 0x21020001) {
			api.registerApp("wx60c8073acffe26c7");    
			sendText();
		} else {
			Toast.makeText(WXEntryActivity.this, "您当前使用的微信版本过低或未安装，易迪乐园分享失败", 3000);
			finish();
		}
	}
	
	private void sendText() {
		WXWebpageObject webpage=new WXWebpageObject();
		webpage.webpageUrl=getIntent().getExtras().getString("url");
		WXMediaMessage msg=new WXMediaMessage(webpage);
		msg.title=getIntent().getExtras().getString("title");
		msg.description=getIntent().getExtras().getString("text");
		msg.thumbData=Util.bmpToByteArray(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher), true);
		
		SendMessageToWX.Req req=new SendMessageToWX.Req();
		req.transaction=buildTransaction("webpage");
		req.message=msg;
		req.scene=getIntent().getExtras().getBoolean("isFriend")?SendMessageToWX.Req.WXSceneTimeline:SendMessageToWX.Req.WXSceneSession;
		api.sendReq(req);
		
		finish();
	}
	
	private String buildTransaction(final String type) {
		return (type ==null)?String.valueOf(System.currentTimeMillis()):type+System.currentTimeMillis();
	}

	// 微信发送请求到第三方应用时，会回调到该方法
	@Override
	public void onReq(BaseReq arg0) {
		// TODO Auto-generated method stub

	}

	// 第三方应用发送到微信的请求处理后的响应结果，会回调到该方法
	@Override
	public void onResp(BaseResp resp) {
		// TODO Auto-generated method stub
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			Toast.makeText(WXEntryActivity.this, "分享成功", 3000);
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			Toast.makeText(WXEntryActivity.this, "分享取消", 3000);
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			Toast.makeText(WXEntryActivity.this, "您没有微信分享权限", 3000);
			break;
		default:
			Toast.makeText(WXEntryActivity.this, "未知错误，分享失败", 3000);
			break;
		}
		finish();
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		setIntent(intent);
        api.handleIntent(intent, this);
	}

}
