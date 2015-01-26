package com.linkage.gas_station.service;

import java.util.ArrayList;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.text.Html;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.linkage.gas_station.GasStationApplication;
import com.linkage.gas_station.R;
import com.linkage.gas_station.model.UpdateModel;
import com.linkage.gas_station.update.DownloadService;
import com.linkage.gas_station.util.Util;
import com.linkage.gas_station.util.hessian.GetWebDate;
import com.linkage.gas_station.util.hessian.PublicManager;

public class UpdateService extends Service {

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		if(intent!=null) {
			if(intent.getExtras()!=null) {
				readXMLInfo(intent.getExtras().getString("isFrom"));
			}
			else {
				readXMLInfo("");
			}
		}
		return super.onStartCommand(intent, flags, startId);
	}
	

	/**
	 * 读取升级文档信息 
	 * 有参数代表进入的时候判断升级，无参数代表点击检查更新
	 */
	public void readXMLInfo(final String from) {
		
		final Handler handler=new Handler() {
			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				if(msg.what!=0) {					
					UpdateModel model=new UpdateModel();
					Map map=(Map)msg.obj;
					model.setAndroid_forced_update(Integer.parseInt(map.get("android_forced_update")==null?"0":map.get("android_forced_update").toString()));
					model.setVersion(map.get("android").toString());
					if(map.get("android_comments")!=null) {
						model.setMessage(map.get("android_comments").toString());
					}
					else {
						model.setMessage("");
					}
					PackageManager manager=UpdateService.this.getPackageManager();
					try {
						PackageInfo info=manager.getPackageInfo(UpdateService.this.getPackageName(), PackageManager.GET_CONFIGURATIONS);
						if(info.versionCode<Integer.parseInt(model.getVersion())) {
							if(model.getAndroid_forced_update()==1) {
								showUpdateBoxMust(UpdateService.this, map.get("android_url").toString(), model.getMessage(), map.get("android").toString());
							}
							else {
								showUpdateBox(map.get("android_url").toString(), model.getMessage(), map.get("android").toString());
							}
						}
						else {
							if(!from.equals("")) {
								Toast.makeText(UpdateService.this, "当前版本已经是最新版本", 2000).show();
							}
						}
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}					
				}
			}
		};
		
		new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				Message m=new Message();
				int num=0;
				boolean flag=true;
				String sendUrl=((GasStationApplication) getApplicationContext()).COMMONURL[0];
				while(flag) {
					try {
						ArrayList<String> list=Util.getUserInfo(UpdateService.this);
						
						PublicManager publicManager=GetWebDate.getHessionFactiory(UpdateService.this).create(PublicManager.class, sendUrl+"/hessian/publicManager", getClassLoader());
						Map map=publicManager.clientVersion(0l, list.get(1).equals("")?null:list.get(1));
						flag=false;
						m.obj=map;
						m.what=1;
					} catch(Error e) {
						flag=false;
						m.what=0;
			        } catch(Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						if(e instanceof com.caucho.hessian.client.HessianRuntimeException) {
							//手机自身网络连接异常
							if(e.getMessage().indexOf("java.net.SocketException")!=-1) {
								num++;
								if(num>=10) {
									flag=false;
									m.what=0;
								}
								try {
									Thread.sleep(500);
								} catch (InterruptedException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
							//ip 端口等错误  java.net.SocketTimeoutException
							else {
								if(sendUrl.equals(((GasStationApplication) getApplicationContext()).COMMONURL[0])) {
									sendUrl=((GasStationApplication) getApplicationContext()).COMMONURL[1];
								}
								else if(sendUrl.equals(((GasStationApplication) getApplicationContext()).COMMONURL[1])) {
									flag=false;
									m.what=0;
								}
							}
						}
						else if(e instanceof com.caucho.hessian.client.HessianConnectionException) {
							//手机自身网络连接异常
							if(e.getMessage().indexOf("java.io.EOFException")!=-1) {
								num++;
								if(num>=10) {
									flag=false;
									m.what=0;
								}
								try {
									Thread.sleep(500);
								} catch (InterruptedException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
							else {
								if(sendUrl.equals(((GasStationApplication) getApplicationContext()).COMMONURL[0])) {
									sendUrl=((GasStationApplication) getApplicationContext()).COMMONURL[1];
								}
								else if(sendUrl.equals(((GasStationApplication) getApplicationContext()).COMMONURL[1])) {
									flag=false;
									m.what=0;
								}
							}
						}
						else {
							if(sendUrl.equals(((GasStationApplication) getApplicationContext()).COMMONURL[0])) {
								sendUrl=((GasStationApplication) getApplicationContext()).COMMONURL[1];
							}
							else if(sendUrl.equals(((GasStationApplication) getApplicationContext()).COMMONURL[1])) {
								flag=false;
								m.what=0;
							}
						}
					}
				}
				handler.sendMessage(m);
			}}).start();
	}
	
	private void showUpdateBox(final String download_url, String update_des, final String download_version) {  
        final AlertDialog dialog=new AlertDialog.Builder(UpdateService.this).create(); 
        dialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);//设定为系统级警告，关键  
        dialog.show();  
        Window window = dialog.getWindow();
        window.setContentView(R.layout.activity_update);
        TextView update_desp=(TextView) window.findViewById(R.id.update_desp);
        update_des=update_des.replace("\\n", "<br>");
		update_desp.setText(Html.fromHtml(update_des));
		ImageView update_now=(ImageView) window.findViewById(R.id.update_now);
		update_now.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(Util.isServiceWorked(UpdateService.this, "com.linkage.gas_station.update.DownloadService")) {
					return;
				}
				Intent intent=new Intent(UpdateService.this, DownloadService.class);
				Bundle bundle=new Bundle();
				bundle.putString("download_url", download_url);
				bundle.putString("download_name", getResources().getString(R.string.app_name));
				bundle.putString("download_id", "0");
				bundle.putString("download_version", download_version);
				intent.putExtras(bundle);
				startService(intent);
				dialog.cancel();
			}});
		ImageView update_later=(ImageView) window.findViewById(R.id.update_later);
		update_later.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dialog.cancel();
			}});
    } 

	/**
     * 显示下线提示
     * @param mContext
     */
    public void showUpdateBoxMust(final Context mContext, final String download_url, String update_des, final String download_version) {
    	final WindowManager wmanager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
    	final View view = LayoutInflater.from(mContext).inflate(R.layout.activity_update, null);
    	TextView update_desp = (TextView) view.findViewById(R.id.update_desp);
    	ImageView update_now=(ImageView) view.findViewById(R.id.update_now);
		update_now.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				wmanager.removeView(view);
				if(((GasStationApplication) getApplication()).tempActivity!=null&&((GasStationApplication) getApplication()).tempActivity.size()>0) {
					for(int i=0;i<((GasStationApplication) getApplication()).tempActivity.size();i++) {
						((GasStationApplication) getApplication()).tempActivity.get(i).finish();
					}
				}
				if(Util.isServiceWorked(UpdateService.this, "com.linkage.gas_station.update.DownloadService")) {
					return;
				}
				Intent intent=new Intent(UpdateService.this, DownloadService.class);
				Bundle bundle=new Bundle();
				bundle.putString("download_url", download_url);
				bundle.putString("download_name", getResources().getString(R.string.app_name));
				bundle.putString("download_id", "0");
				bundle.putString("download_version", download_version);
				intent.putExtras(bundle);
				startService(intent);
			}});
		ImageView update_later=(ImageView) view.findViewById(R.id.update_later);
		update_later.setOnClickListener(new ImageView.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				wmanager.removeView(view);
				if(((GasStationApplication) getApplication()).tempActivity!=null&&((GasStationApplication) getApplication()).tempActivity.size()>0) {
					for(int i=0;i<((GasStationApplication) getApplication()).tempActivity.size();i++) {
						((GasStationApplication) getApplication()).tempActivity.get(i).finish();
					}
				}
			}});
    	update_des=update_des.replace("\\n", "<br>");
		update_desp.setText(Html.fromHtml(update_des));
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
        layoutParams.format = 1;
        layoutParams.flags =layoutParams.flags | WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS;
        layoutParams.alpha = 1f;
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
        layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
        wmanager.addView(view, layoutParams);
        wmanager.updateViewLayout(view, layoutParams);
    }
}
