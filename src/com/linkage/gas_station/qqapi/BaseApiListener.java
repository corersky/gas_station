package com.linkage.gas_station.qqapi;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.tencent.open.HttpStatusException;
import com.tencent.open.NetworkUnavailableException;
import com.tencent.tauth.IRequestListener;

public class BaseApiListener implements IRequestListener {

	private String mScope="all";
	private Boolean mNeedReAuth=false;
	private Activity mActivity;
	
	public BaseApiListener(String scope, boolean needReAuth, Activity activity) {
		mScope=scope;
		mNeedReAuth=needReAuth;
		mActivity=activity;
	}
	private Handler mHandler=new Handler(){

		@Override
		public void handleMessage(Message msg) {
			Bundle params=msg.getData();
			String title=params.getString("title");
			String response=params.getString("response");
			System.out.println(title+" "+response);
			super.handleMessage(msg);
		}
		
	};
	
	@Override
	public void onComplete(final JSONObject response, Object state) {
		Message msg=new Message();
		Bundle params=new Bundle();
		params.putString("response", response.toString());
		params.putString("title", "onComplete");
		mHandler.sendMessage(msg);
	}

	@Override
	public void onIOException(final IOException e, Object state) {
		Message msg=new Message();
		Bundle params=new Bundle();
		params.putString("response", e.getMessage());
		params.putString("title", "onIOException");
		mHandler.sendMessage(msg);
	}

	@Override
	public void onMalformedURLException(final MalformedURLException e,
			Object state) {
		Message msg=new Message();
		Bundle params=new Bundle();
		params.putString("response", e.getMessage());
		params.putString("title", "onMalformedURLException");
		mHandler.sendMessage(msg);
	}

	@Override
	public void onJSONException(final JSONException e, Object state) {
		Message msg=new Message();
		Bundle params=new Bundle();
		params.putString("response", e.getMessage());
		params.putString("title", "onJSONException");
		mHandler.sendMessage(msg);
	}

	@Override
	public void onConnectTimeoutException(ConnectTimeoutException e, Object arg1) {
		Message msg=new Message();
		Bundle params=new Bundle();
		params.putString("response", e.getMessage());
		params.putString("title", "onConnectTimeoutException");
		mHandler.sendMessage(msg);
	}

	@Override
	public void onSocketTimeoutException(SocketTimeoutException e, Object arg1) {
		Message msg=new Message();
		Bundle params=new Bundle();
		params.putString("response", e.getMessage());
		params.putString("title", "onSocketTimeoutException");
		mHandler.sendMessage(msg);
	}

	@Override
	public void onUnknowException(Exception e, Object arg1) {
		Message msg=new Message();
		Bundle params = new Bundle();
		params.putString("response", e.getMessage());
		params.putString("title", "onUnknowException");
		mHandler.sendMessage(msg);
	}

	@Override
	public void onHttpStatusException(HttpStatusException e, Object arg1) {
		Message msg=new Message();
		Bundle params=new Bundle();
		params.putString("response", e.getMessage());
		params.putString("title", "onHttpStatusException");
		mHandler.sendMessage(msg);
	}

	@Override
	public void onNetworkUnavailableException(NetworkUnavailableException e,
			Object arg1) {
		Message msg=new Message();
		Bundle params=new Bundle();
		params.putString("response", e.getMessage());
		params.putString("title", "onNetworkUnavailableException");
		mHandler.sendMessage(msg);
	}

	public String getmScope() {
		return mScope;
	}

	public void setmScope(String mScope) {
		this.mScope=mScope;
	}

	public Boolean getmNeedReAuth() {
		return mNeedReAuth;
	}

	public void setmNeedReAuth(Boolean mNeedReAuth) {
		this.mNeedReAuth=mNeedReAuth;
	}

	public Activity getmActivity() {
		return mActivity;
	}

	public void setmActivity(Activity mActivity) {
		this.mActivity=mActivity;
	}
	
}
