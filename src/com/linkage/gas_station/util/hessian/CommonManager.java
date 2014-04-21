package com.linkage.gas_station.util.hessian;

import java.util.Map;

/** 
 * @author luxiaobo E-mail:xiaobolu@hotmail.com 
 * @version Create Date：2012-5-16 上午10:42:31 
 */

public interface CommonManager {
	//获取验证码
	//public int getVerCode(Long phoneNum);
	//激活用户
	//public Map setUserActive(Long phoneNum,String verCode,int phonePlatform,String machineModel,String systemVersion,String machineCode);
	
	//消息推送用户注册 推送开关 1:open 2:close
	//public int setUserOnline(String machineCode,String deviceToken,int openOrClose);
	
	//意见反馈
	public int feedBack(Long phoneNum,String advice);
	
	//软件分享
	public int softwareRecommend(Long phoneNum,int phoneType,String RephoneNum);
	
	//记录日志
	public void logInfo(Long phoneNum,String moduleName,String logInfo);
	
	//ANDROID平台根据IMSI号获取手机号码
	//public String getPhoneNum(String imsi);
	
	//记录手机和客户端版本信息
	public int saveVersion(String machineCode,String systemVersion,String clientVersion);
	
	//软件分享记录
	public Map[] getRecommendList(Long phoneNum);
	
	public int clientState(String machineCode,int state);
		
	//阀值查询
	public int thresHoldQuery(Long phoneNum,String areaCode);
	
	public Map[] feedBackList(Long phoneNum,int start,int pageSize);
	
	//更新机器码
	public int updateMachineCode(String oldMachineCode,String newMachineCode,Long phoneNum,String areaCode);
	
	//记录客户端渠道信息
	//result 1:显示返回信息(comments) 0：不显示
	public Map saveSource(String machineCode,String source,Long phoneNum,String areaCode);
	
	/**
	 * level 0:未设置或不提醒 1-6：流量加油站提醒 7：短信提醒 
	 * result 0:失败 1:成功  comments 原因
	 */
	public Map setThresHold(Long phoneNum,int level,String areaCode);
}
