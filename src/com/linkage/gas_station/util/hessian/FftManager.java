package com.linkage.gas_station.util.hessian;

import java.util.HashMap;
import java.util.Map;

public interface FftManager {
	//按号码获取水、电、煤绑定信息
	//result 1:查询成功 0：失败 原因取comments
	//sfList:水费  dfList:电费  rqList:燃气
	public Map getBindSummary(String areaCode,Long phoneNum);
	
	//按地区获取水、电、煤信息
	//type SF:水费  DF:电费  RQ:燃气
	//type_descript 公司名称  bus_type_id 业务类型标识
	public Map[] getAreaSdm(String areaCode,String type);
	
	//按类型增删当前号码绑定的公共账户
	//operType 1:绑定  2:解绑
	public Map mofidyBindInfo(String areaCode,Long phoneNum,String type,String bussTypeId,String accNbr,int operType);

	//按号码获取水、电、煤绑定信息及欠费信息
	//type SF:水费  DF:电费  RQ:燃气  ALL:查全部
	//result 1:查询成功 0：失败 原因取comments
	//sfList:水费  dfList:电费  rqList:燃气 
	public Map getBindSummaryAndOwe(String areaCode,Long phoneNum,String type);
	
	//账户查询
	//result 1:查询成功 0：失败 原因取comments
	public HashMap yzfQry(Long phoneNum,String areaCode);
	
	//开户 
	//result 1:开户成功 0：失败 原因取comments
	public HashMap yzfReg(Long phoneNum,String areaCode);
	
	//缴费
	//busTypeId:id:owe;busTypeId:id:owe;
	public Map cashOweAll(String seqId,String verCode,String areaCode,Long phoneNum,String typeAndidAndOwes);

}

