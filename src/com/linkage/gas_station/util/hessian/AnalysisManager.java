package com.linkage.gas_station.util.hessian;

import java.util.Map;

/** 
 * @author luxiaobo E-mail:xiaobolu@hotmail.com 
 * @version Create Date：2013-3-6 下午02:43:20 
 */

public interface AnalysisManager {
	//按月获取日流量详情(时间格式:YYYY-MM)
	public Map[] getDayAnalysis(Long phoneNum,String month,String areaCode);

	//获取月流量详情
	public Map[] getMonthAnalysis(Long phoneNum,String areaCode);

}
