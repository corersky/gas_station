package com.linkage.gas_station.util.hessian;

import java.util.Map;

public interface PublicManager {
	//激活用户
	public Map setUserActive(Long phoneNum,String verCode,int phonePlatform,String machineModel,String systemVersion,String machineCode);
	//消息推送用户注册 推送开关 1:open 2:close
	public int setUserOnline(String machineCode,String deviceToken,int openOrClose);
	//获取系统当前时间
	public long getSystemTime();
	//ANDROID平台根据IMSI号获取手机号码及请求地址 ip port phone_num
	public Map getPhoneNumAndUrl(String imsi);
	//获取验证码
	public int sendVerCode(Long phoneNum,String areaCode);
	// 获取验证码及请求地址 hosts(MAP数组 ip port) phone_num
	public Map getActiveVerCode(Long phoneNum);
	//上传手机本地应用 name;流量
	public void updateLocalApps(Long phoneNum,String[] apps);
	//更新接口模块
	public Map clientVersion(Long phoneNum,String areaCode);
	//获取本省请求地址列表
	public Map[] urlList(String provinceCode);
}
