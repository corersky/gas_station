package com.linkage.gas_station.wxapi;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
	 private IWXAPI api;  
	@Override  
   public void onCreate(Bundle savedInstanceState) {  
       super.onCreate(savedInstanceState);  
       api = WXAPIFactory.createWXAPI(this,  
               "wx114b42bb28c1396d", false);  
       api.handleIntent(getIntent(), this);  
 
   }  
	@Override  
   public void onReq(BaseReq arg0) {  
 
   }  
 
   @Override  
   public void onResp(BaseResp resp) {   
       switch (resp.errCode) {  
       case BaseResp.ErrCode.ERR_OK:
    	   	Toast.makeText(WXEntryActivity.this, "分享成功", 3000).show();
    	   	System.out.println("OK");
    	   	com.linkage.gas_station.util.Util.shareInfo(WXEntryActivity.this);
    	   	break;
       case BaseResp.ErrCode.ERR_USER_CANCEL:
			Toast.makeText(WXEntryActivity.this, "分享取消", 3000).show();
			break;
       case BaseResp.ErrCode.ERR_AUTH_DENIED:
			Toast.makeText(WXEntryActivity.this, "您没有微信分享权限", 3000).show();
			break;
       default:
			Toast.makeText(WXEntryActivity.this, "未知错误，分享失败", 3000).show();
			break;
       }
 
       // TODO 微信分享 成功之后调用接口  
       this.finish();  
   }  

}
