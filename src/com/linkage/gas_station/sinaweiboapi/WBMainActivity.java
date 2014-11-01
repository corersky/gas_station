package com.linkage.gas_station.sinaweiboapi;

import com.linkage.gas_station.R;
import com.linkage.gas_station.util.Util;
import com.sina.weibo.sdk.api.ImageObject;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WebpageObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.WeiboMultiMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboDownloadListener;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.SendMultiMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuth;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.constant.WBConstants;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.exception.WeiboShareException;
import com.sina.weibo.sdk.utils.Utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.Window;
import android.widget.Toast;

public class WBMainActivity extends Activity implements IWeiboHandler.Response {

	/** 微博 Web 授权类，提供登陆等功能  */
    private WeiboAuth mWeiboAuth;
    /** 封装了 "access_token"，"expires_in"，"refresh_token"，并提供了他们的管理功能  */
    private Oauth2AccessToken mAccessToken;
    /** 注意：SsoHandler 仅当 SDK 支持 SSO 时有效 */
    private SsoHandler mSsoHandler;
    
    public static final String app_key="2679815435";
    public static final String scope= 
            "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog," + "invitation_write";
    
    /** 微博微博分享接口实例 */
    private IWeiboShareAPI  mWeiboShareAPI=null;
    
    Bitmap bmp=null;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	// TODO Auto-generated method stub
    	super.onCreate(savedInstanceState);
    	requestWindowFeature(Window.FEATURE_NO_TITLE);
    	
    	// 创建微博实例
        mWeiboAuth=new WeiboAuth(this, app_key, "https://api.weibo.com/oauth2/default.html", scope);
        
        // 从 SharedPreferences 中读取上次已保存好 AccessToken 等信息，
        // 第一次启动本应用，AccessToken 不可用
        mAccessToken=readAccessToken(this);
        if (mAccessToken.isSessionValid()) {
        	sendBefore();
        }
        else {
        	mSsoHandler=new SsoHandler(WBMainActivity.this, mWeiboAuth);
            mSsoHandler.authorize(new AuthListener());
        }
    }
    
    /**
     * 微博认证授权回调类。
     * 1. SSO 授权时，需要在 {@link #onActivityResult} 中调用 {@link SsoHandler#authorizeCallBack} 后，
     *    该回调才会被执行。
     * 2. 非 SSO 授权时，当授权结束后，该回调就会被执行。
     * 当授权成功后，请保存该 access_token、expires_in、uid 等信息到 SharedPreferences 中。
     */
    class AuthListener implements WeiboAuthListener {
        
        @Override
        public void onComplete(Bundle values) {
            // 从 Bundle 中解析 Token
            mAccessToken=Oauth2AccessToken.parseAccessToken(values);
            if (mAccessToken.isSessionValid()) {
                // 保存 Token 到 SharedPreferences
            	writeAccessToken(WBMainActivity.this, mAccessToken);
            	Toast.makeText(WBMainActivity.this, "授权成功", 3000);
            	sendBefore();
            } else {
                // 当您注册的应用程序签名不正确时，就会收到 Code，请确保签名正确
                String code=values.getString("code");
                Toast.makeText(WBMainActivity.this, "授权失败\nObtained the code: " + code, 3000).show();
                finish();
            }
        }

        @Override
        public void onCancel() {
        	Toast.makeText(WBMainActivity.this, "取消授权", 3000).show();
        	finish();
        }

        @Override
        public void onWeiboException(WeiboException e) {
        	Toast.makeText(WBMainActivity.this, "Auth exception : " + e.getMessage(), 3000).show();
        	finish();
        }
    }
    
    /**
     * 保存 Token 对象到 SharedPreferences。
     * 
     * @param context 应用程序上下文环境
     * @param token   Token 对象
     */
    public static void writeAccessToken(Context context, Oauth2AccessToken token) {
        SharedPreferences pref=context.getSharedPreferences("com_weibo_sdk_android", Context.MODE_APPEND);
        Editor editor=pref.edit();
        editor.putString("uid", token.getUid());
        editor.putString("access_token", token.getToken());
        editor.putLong("expires_in", token.getExpiresTime());
        editor.commit();
    }
    
    /**
     * 从 SharedPreferences 读取 Token 信息。
     * 
     * @param context 应用程序上下文环境
     * 
     * @return 返回 Token 对象
     */
    public static Oauth2AccessToken readAccessToken(Context context) {
        Oauth2AccessToken token = new Oauth2AccessToken();
        SharedPreferences pref = context.getSharedPreferences("com_weibo_sdk_android", Context.MODE_APPEND);
        token.setUid(pref.getString("uid", ""));
        token.setToken(pref.getString("access_token", ""));
        token.setExpiresTime(pref.getLong("expires_in", 0));
        return token;
    }
    
    /**
     * 当 SSO 授权 Activity 退出时，该函数被调用。
     * 
     * @see {@link Activity#onActivityResult}
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        // SSO 授权回调
        // 重要：发起 SSO 登陆的 Activity 必须重写 onActivityResult
        if (mSsoHandler!=null) {
            mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
        }
    }
    
    private void sendBefore() {
    	// 创建微博分享接口实例
        mWeiboShareAPI=WeiboShareSDK.createWeiboAPI(WBMainActivity.this, app_key);
        
        // 如果未安装微博客户端，设置下载微博对应的回调
        if(!mWeiboShareAPI.isWeiboAppInstalled()) {
            mWeiboShareAPI.registerWeiboDownloadListener(new IWeiboDownloadListener() {
                @Override
                public void onCancel() {
                	Toast.makeText(WBMainActivity.this, "取消下载", 3000).show();
                }
            });
        }
        
        try {
            // 检查微博客户端环境是否正常，如果未安装微博，弹出对话框询问用户下载微博客户端
            if (mWeiboShareAPI.checkEnvironment(true)) {
                
                // 注册第三方应用 到微博客户端中，注册成功后该应用将显示在微博的应用列表中。
                // 但该附件栏集成分享权限需要合作申请，详情请查看 Demo 提示
                mWeiboShareAPI.registerApp();
                
                sendMessage();
            }
        } catch (WeiboShareException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * @see {@link #sendMultiMessage} 或者 {@link #sendSingleMessage}
     */
    private void sendMessage() {        
        if (mWeiboShareAPI.isWeiboAppSupportAPI()) {
            int supportApi=mWeiboShareAPI.getWeiboAppSupportAPI();
            if (supportApi>=10351 /*ApiUtils.BUILD_INT_VER_2_2*/) {
                sendMultiMessage();
            } else {
                sendSingleMessage();
            }
        } else {
        	Toast.makeText(WBMainActivity.this, "微博客户端不支持SDK分享或微博客户端未安装或微博客户端是非官方版本。", 3000).show();
        }
    }
    
    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * 注意：当 {@link IWeiboShareAPI#getWeiboAppSupportAPI()} >= 10351 时，支持同时分享多条消息，
     * 同时可以分享文本、图片以及其它媒体资源（网页、音乐、视频、声音中的一种）。
     * 
     * @param hasText    分享的内容是否有文本
     * @param hasImage   分享的内容是否有图片
     * @param hasWebpage 分享的内容是否有网页
     * @param hasMusic   分享的内容是否有音乐
     * @param hasVideo   分享的内容是否有视频
     * @param hasVoice   分享的内容是否有声音
     */
    private void sendMultiMessage() {        
        // 1. 初始化微博的分享消息
        WeiboMultiMessage weiboMessage = new WeiboMultiMessage();
        weiboMessage.textObject=getTextObj(getIntent().getExtras().getString("title")+" "+getIntent().getExtras().getString("text"));
        weiboMessage.imageObject=getImageObj(getIntent().getExtras().getString("send_imageUrl")!=null?getIntent().getExtras().getString("send_imageUrl"):"");
        weiboMessage.mediaObject=getWebpageObj();
        
        // 2. 初始化从第三方到微博的消息请求
        SendMultiMessageToWeiboRequest request=new SendMultiMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction=String.valueOf(System.currentTimeMillis());
        request.multiMessage=weiboMessage;
        
        // 3. 发送请求消息到微博，唤起微博分享界面
        mWeiboShareAPI.sendRequest(request);
    }

    /**
     * 第三方应用发送请求消息到微博，唤起微博分享界面。
     * 当{@link IWeiboShareAPI#getWeiboAppSupportAPI()} < 10351 时，只支持分享单条消息，即
     * 文本、图片、网页、音乐、视频中的一种，不支持Voice消息。
     */
    private void sendSingleMessage() {
        
        // 1. 初始化微博的分享消息
        // 用户可以分享文本、图片、网页、音乐、视频中的一种
        WeiboMessage weiboMessage = new WeiboMessage();
        weiboMessage.mediaObject = getWebpageObj();
        
        // 2. 初始化从第三方到微博的消息请求
        SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.message = weiboMessage;
        
        // 3. 发送请求消息到微博，唤起微博分享界面
        mWeiboShareAPI.sendRequest(request);
    }
    
    /**
     * 创建文本消息对象。
     * 
     * @return 文本消息对象。
     */
    private TextObject getTextObj(String text) {
        TextObject textObject=new TextObject();
        textObject.text=text;
        return textObject;
    }
    
    /**
     * 创建图片消息对象。
     * 
     * @return 图片消息对象。
     */
    private ImageObject getImageObj(String image) {
        ImageObject imageObject=new ImageObject();
        // 设置 Bitmap 类型的图片到视频对象里
        if(image.equals("share_logo_4g")) {
        	bmp=BitmapFactory.decodeResource(getResources(), R.drawable.share_logo_4g);
        }
        else {
        	bmp=BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher);
        }
        imageObject.setImageObject(bmp);
        return imageObject;
    }

    
    /**
     * 创建多媒体（网页）消息对象。
     * 
     * @return 多媒体（网页）消息对象。
     */
    private WebpageObject getWebpageObj() {
        WebpageObject mediaObject=new WebpageObject();
        mediaObject.identify=Utility.generateGUID();
        mediaObject.title=getIntent().getExtras().getString("title");
        mediaObject.description=getIntent().getExtras().getString("text");        
        mediaObject.setThumbImage(bmp);
        mediaObject.actionUrl=getIntent().getExtras().getString("url");
        mediaObject.defaultText=getIntent().getExtras().getString("defaultText");
        return mediaObject;
    }
    
    /**
     * @see {@link Activity#onNewIntent}
     */	
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        
        // 从当前应用唤起微博并进行分享后，返回到当前应用时，需要在此处调用该函数
        // 来接收微博客户端返回的数据；执行成功，返回 true，并调用
        // {@link IWeiboHandler.Response#onResponse}；失败返回 false，不调用上述回调
        mWeiboShareAPI.handleWeiboResponse(intent, this);
    }

    /**
     * 接收微客户端博请求的数据。
     * 当微博客户端唤起当前应用并进行分享时，该方法被调用。
     * 
     * @param baseRequest 微博请求数据对象
     * @see {@link IWeiboShareAPI#handleWeiboRequest}
     */
    @Override
    public void onResponse(BaseResponse baseResp) {
        switch (baseResp.errCode) {
        case WBConstants.ErrorCode.ERR_OK:
        	Toast.makeText(WBMainActivity.this, "分享成功", 3000).show();
        	System.out.println("OK");
        	Util.shareInfo(WBMainActivity.this);
            break;
        case WBConstants.ErrorCode.ERR_CANCEL:
        	Toast.makeText(WBMainActivity.this, "取消分享", 3000).show();
            break;
        case WBConstants.ErrorCode.ERR_FAIL:
        	Toast.makeText(WBMainActivity.this, "分享失败 "+"Error Message:"+baseResp.errMsg, 3000).show();
            break;
        }
        if(bmp!=null&&!bmp.isRecycled()) {
        	bmp.recycle();
        	bmp=null;
        }
        finish();
    }
}
