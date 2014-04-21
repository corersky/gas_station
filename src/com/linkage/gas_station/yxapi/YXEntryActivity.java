package com.linkage.gas_station.yxapi;

import android.widget.Toast;
import im.yixin.sdk.api.BaseReq;
import im.yixin.sdk.api.BaseResp;
import im.yixin.sdk.api.BaseYXEntryActivity;
import im.yixin.sdk.api.IYXAPI;
import im.yixin.sdk.api.SendMessageToYX;
import im.yixin.sdk.api.YXAPIFactory;
import im.yixin.sdk.util.YixinConstants;

public class YXEntryActivity extends BaseYXEntryActivity {

	@Override
	public void onReq(BaseReq arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onResp(BaseResp resp) {
		// TODO Auto-generated method stub
		switch (resp.getType()) {
		case YixinConstants.RESP_SEND_MESSAGE_TYPE:
			SendMessageToYX.Resp resp1 = (SendMessageToYX.Resp) resp;
			switch (resp1.errCode) {
			case BaseResp.ErrCode.ERR_OK:
				Toast.makeText(YXEntryActivity.this, "分享成功", 3000);
				break;
			case BaseResp.ErrCode.ERR_COMM:
				Toast.makeText(YXEntryActivity.this, "分享失败", 3000);
				break;
			case BaseResp.ErrCode.ERR_USER_CANCEL:
				Toast.makeText(YXEntryActivity.this, "用户取消", 3000);
				break;
			case BaseResp.ErrCode.ERR_SENT_FAILED:
				Toast.makeText(YXEntryActivity.this, "发送失败", 3000);
				break;
			}
			finish();
		}
	}

	@Override
	protected IYXAPI getIYXAPI() {
		// TODO Auto-generated method stub
		return YXAPIFactory.createYXAPI(this, "yxf2ad8632e7cf45b08a8069a0440989cf");
	}

}
