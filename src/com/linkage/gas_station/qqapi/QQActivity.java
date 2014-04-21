package com.linkage.gas_station.qqapi;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import com.tencent.open.HttpStatusException;
import com.tencent.open.NetworkUnavailableException;
import com.tencent.tauth.Constants;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Window;
import android.widget.Toast;

public class QQActivity extends Activity {
	
	public Tencent mTencent;
	public static String mAppid="101026419";
	
	private int shareType=Tencent.SHARE_TO_QQ_TYPE_DEFAULT;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		mTencent=Tencent.createInstance(mAppid, getApplicationContext());
		if(!mTencent.isSupportSSOLogin(QQActivity.this)) {
			Toast.makeText(QQActivity.this, "请您先安装手机版QQ再执行分享操作", 3000);
			finish();
			return ;
		}
				
		if(getIntent().getExtras().getString("type").equals("qqkj")) {
			shareType=Tencent.SHARE_TO_QQ_NO_SHARE_TYPE;
			sendTextKJ(getIntent().getExtras().getString("text"), getIntent().getExtras().getString("send_imageUrl"), getIntent().getExtras().getString("title"), getIntent().getExtras().getString("url"));
		}
		else if(getIntent().getExtras().getString("type").equals("qq")) {
			shareType=Tencent.SHARE_TO_QQ_TYPE_DEFAULT;
			sendText(getIntent().getExtras().getString("text"), getIntent().getExtras().getString("send_imageUrl"));
		}
		else if(getIntent().getExtras().getString("type").equals("tweibo")) {
			sendWeiboWithPic(getIntent().getExtras().getString("text"), getIntent().getExtras().getString("path"));
		}
	}
	
	private class BaseUiListener implements IUiListener {

		@Override
		public void onComplete(JSONObject response) {
			Toast.makeText(QQActivity.this, "登录成功", 3000);
			System.out.println(response.toString());
			doComplete(response);
		}

		protected void doComplete(JSONObject values) {

		}

		@Override
		public void onError(UiError e) {
			Toast.makeText(QQActivity.this, "onError: "+e.errorDetail, 3000);
		}

		@Override
		public void onCancel() {
			Toast.makeText(QQActivity.this, "onCancel: ", 3000);
		}
	}
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // must call mTencent.onActivityResult.
    	if(resultCode==RESULT_CANCELED) {
    		finish();
    		return;
    	}
    }
	
	public void sendText(String text, String imageUrl) {
		final Bundle params=new Bundle();
		params.putString(Tencent.SHARE_TO_QQ_TITLE, "易迪乐园优秀动画片推荐");
        params.putString(Tencent.SHARE_TO_QQ_TARGET_URL, "http://www.kidsedu.com");
        params.putString(Tencent.SHARE_TO_QQ_SUMMARY, text);
        params.putString(Tencent.SHARE_TO_QQ_IMAGE_URL, imageUrl);
        params.putString(Tencent.SHARE_TO_QQ_APP_NAME, "易迪乐园");
        params.putInt(Tencent.SHARE_TO_QQ_KEY_TYPE, shareType);
        params.putInt(Tencent.SHARE_TO_QQ_EXT_INT, 0x00);
        doShareToQQ(params);
	}
	
	public void sendTextKJ(String text, String imageUrl, String title, String url) {
        ArrayList<String> imageUrls=new ArrayList<String>();
        imageUrls.add(imageUrl);
		final Bundle params=new Bundle();
        params.putInt(Tencent.SHARE_TO_QQ_KEY_TYPE, shareType);
        params.putString(Tencent.SHARE_TO_QQ_TITLE, title);
        params.putString(Tencent.SHARE_TO_QQ_SUMMARY, text);
        params.putString(Tencent.SHARE_TO_QQ_TARGET_URL, url);
        params.putStringArrayList(Tencent.SHARE_TO_QQ_IMAGE_URL, imageUrls);
        doShareToQzone(params);
	}
	
	/**
     * 用异步方式启动分享QQ
     * @param params
     */
    private void doShareToQQ(final Bundle params) {
        final Tencent tencent=Tencent.createInstance(mAppid, QQActivity.this);        
        final Activity activity=QQActivity.this;
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                // TODO Auto-generated method stub
                tencent.shareToQQ(activity, params, new IUiListener() {

                    @Override
                    public void onComplete(JSONObject response) {
                        // TODO Auto-generated method stub
                    	Toast.makeText(activity, "分享成功", 3000);
                        finish();
                    }

                    @Override
                    public void onError(UiError e) {
                    	Toast.makeText(activity, "分享失败"+e.errorMessage, 3000);
                        finish();
                    }

                    @Override
                    public void onCancel() {
                    	if(shareType!=Tencent.SHARE_TO_QQ_TYPE_IMAGE){
                    		Toast.makeText(activity, "分享取消", 3000);
                    		finish();
                    	}
                    }

                });
            }
        }).start();
    }
    
    /**
     * 用异步方式启动分享QQ空间
     * @param params
     */
    private void doShareToQzone(final Bundle params) {
        final Activity activity=QQActivity.this;
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                // TODO Auto-generated method stub
            	mTencent.shareToQzone(activity, params, new IUiListener() {

                    @Override
                    public void onCancel() {
                    	Toast.makeText(activity, "分享取消", 3000);
                    	finish();
                    }

                    @Override
                    public void onComplete(JSONObject response) {
                        // TODO Auto-generated method stub
                    	Toast.makeText(activity, "分享成功", 3000);
                    	finish();
                    }

                    @Override
                    public void onError(UiError e) {
                        // TODO Auto-generated method stub
                    	Toast.makeText(activity, "分享失败"+e.errorMessage, 3000);
                    	finish();
                    }

                });
            }
        }).start();
    }
    
    /**
	 * 发送带图微博
	 */
	private void sendWeiboWithPic(String text, String path) {
		if(mTencent.ready(QQActivity.this)) {
			Bundle bundle=new Bundle();
			bundle.putString("format", "json");
			bundle.putString("content", "易迪乐园优秀动画片推荐："+text);
			ByteArrayOutputStream baos=new ByteArrayOutputStream();
			Bitmap bmp=BitmapFactory.decodeFile(path);
			bmp.compress(Bitmap.CompressFormat.JPEG, 40, baos);
			byte[] buff=baos.toByteArray();
			bundle.putByteArray("pic", buff);
			mTencent.requestAsync(Constants.GRAPH_ADD_PIC_T, bundle, Constants.HTTP_POST, new TQQApiListener("add_pic_t", false, QQActivity.this), null);
			bmp.recycle();
		}
	}
	
	private class TQQApiListener extends BaseApiListener {
		
		private String mScope="all";
        private Boolean mNeedReAuth=false;
        private Activity mActivity;
        
    	public TQQApiListener(String scope, boolean needReAuth, Activity activity) {
			super(scope, needReAuth, activity);
			this.mScope = scope;
			this.mNeedReAuth = needReAuth;
			this.mActivity = activity;
		}

		@Override
		public void onComplete(final JSONObject response, Object state) {
			final Activity activity=QQActivity.this;
			try {
				int ret=response.getInt("ret");
				if(ret==0) {
					Message msg=mHandler.obtainMessage(0, mScope);
					Bundle data=new Bundle();
					data.putString("response", response.toString());
					msg.setData(data);
					mHandler.sendMessage(msg);
				} else if(ret == 100030) {
					if(mNeedReAuth) {
						Runnable r=new Runnable() {
							public void run() {
								mTencent.reAuth(activity, mScope, new BaseUiListener());
							}
						};
						QQActivity.this.runOnUiThread(r);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
				finish();
			}
		}
		
	  	@Override
        public void onIOException(final IOException e, Object state) {
	  		Toast.makeText(QQActivity.this, "分享失败，onIOException: "+e.getMessage(), 3000);
            finish();
        }

        @Override
        public void onMalformedURLException(final MalformedURLException e,
                Object state) {
        	Toast.makeText(QQActivity.this, "分享失败，onMalformedURLException: "+e.getMessage(), 3000);
        	finish();
        }

        @Override
        public void onJSONException(final JSONException e, Object state) {
        	Toast.makeText(QQActivity.this, "分享失败，onJSONException: "+e.getMessage(), 3000);
        	finish();
        }

        @Override
        public void onConnectTimeoutException(ConnectTimeoutException e,
                Object arg1) {
        	Toast.makeText(QQActivity.this, "分享失败，onConnectTimeoutException: "+e.getMessage(), 3000);
        	finish();
        }

        @Override
        public void onSocketTimeoutException(SocketTimeoutException e,
                Object arg1) {
        	Toast.makeText(QQActivity.this, "分享失败，onSocketTimeoutException: "+e.getMessage(), 3000);
        	finish();
        }

        @Override
        public void onUnknowException(Exception e, Object arg1) {
        	Toast.makeText(QQActivity.this, "分享失败，onUnknowException: "+e.getMessage(), 3000);
        	finish();
        }

        @Override
        public void onHttpStatusException(HttpStatusException e, Object arg1) {
        	Toast.makeText(QQActivity.this, "分享失败，onHttpStatusException: "+e.getMessage(), 3000);
        	finish();
        }

        @Override
        public void onNetworkUnavailableException(
                NetworkUnavailableException e, Object arg1) {
        	Toast.makeText(QQActivity.this, "分享失败，onNetworkUnavailableException: "+e.getMessage(), 3000);
        	finish();
        }
	}
	
	/**
	 * 异步显示结果
	 */
	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			String response=msg.getData().getString("response");
			if(response!=null) {
				// 换行显示
				response=response.replace(",", "\r\n");
				Toast.makeText(QQActivity.this, "分享成功", 3000);
				System.out.println(response);
				finish();
			}
		};
	};
}
