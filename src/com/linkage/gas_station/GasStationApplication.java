package com.linkage.gas_station;

import java.io.File;
import java.util.ArrayList;

import cn.jpush.android.api.JPushInterface;

import com.linkage.gas_station.model.ContactModel;
import com.linkage.gas_station.util.Util;

import android.app.Activity;
import android.app.Application;
import android.content.SharedPreferences;
import android.os.Environment;

public class GasStationApplication extends Application {
	
	//联系人信息
	ArrayList<ContactModel> model_list=null;
	//通用hessian请求     http://192.168.69.42:8099/flowOilStation
	public final String[] COMMONURL=new String[]{"http://61.160.128.38:7010/flowOilStation", "http://61.160.128.39:7010/flowOilStation"};
	//用户专属省份
	public final String PROVINCE="江苏";
	//本地区专属url  激活界面的验证码    IMSI    版本更新检测
	public String AreaUrl="";
	//跳转加油界面序号
	public int jumpJiayouNum=-1;
	//订购页面来自何处 1:流量监控 2：流量加油 3：流量攻略 4：团购
	public int jumpJiayouFrom=0;
	//流量银行跳转到指定的选项卡
	public int flowBankNum=-1;
	//是否需要刷新团购人数
	public boolean isRefreshTuan=false;
	//登陆界面验证码获取时间
	public long loginTime=0;
	//支付界面验证码获取时间
	public long detailTime=0;
	//是否由加油成功跳转到首页
	public boolean isJumpToMonitor=false;
	//是否由加油成功跳转到团购
	public boolean isJumpToTuan=false;
	//是否应用已经被打开
	public boolean isAppOpen=false;
	//附属activity，当用户被强制退出的时候使用
	public ArrayList<Activity> tempActivity=null;
	//判断是否为切换sim卡
	public boolean isChangeSim=false;
	//是否由后台打开退出登录界面
	public boolean isShowLoginOut=false;
	//是否有新的意见反馈
	public boolean isNewSuggest=false;
	//是否有新的流量攻略
	public boolean isNewGonglve=false;
	//是否要刷新流量监控
	public boolean isRefreshMonitor=false;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		
		JPushInterface.setDebugMode(true); 	//设置开启日志,发布时请关闭日志
        JPushInterface.init(this);     		// 初始化 JPush
                
		SharedPreferences sp=getSharedPreferences("gas", Activity.MODE_PRIVATE);
		if(sp.getString("phoneNum", "").equals("")) {
			SharedPreferences.Editor editor=sp.edit();
			editor.putString("phoneNum", "");
			editor.putString("area_code", "");
			editor.putString("imsi", "");
			editor.putLong("time_extra", 0);
			editor.commit();
		}
		if(sp.getString("startUrl", "").equals("")) {
			SharedPreferences.Editor editor=sp.edit();
			editor.putString("startUrl", COMMONURL[0]);
			editor.putString("wholeUrl", "");
			editor.commit();
		}
		if(sp.getLong("uploadApp", 0)==0) {
			SharedPreferences.Editor editor=sp.edit();
			editor.putLong("uploadApp", 0);
			editor.commit();
		}
		//创建默认值
		if(Util.getUserArea(getApplicationContext()).equals("")) {
			Util.setUserArea(getApplicationContext(), "2500");
		}
		//创建SD卡文件
		File file=new File(Environment.getExternalStorageDirectory().getPath()+"/gas");
		if(!file.exists()) {
			file.mkdir();
		}		
		tempActivity=new ArrayList<Activity>();		
	}
	
	public ArrayList<ContactModel> getModel_list() {
		return model_list;
	}

	public void setModel_list(ArrayList<ContactModel> model_list) {
		this.model_list = model_list;
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
	}

}
