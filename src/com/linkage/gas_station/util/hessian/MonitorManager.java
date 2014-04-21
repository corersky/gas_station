package com.linkage.gas_station.util.hessian;

import java.util.Map;

/** 
 * @author luxiaobo E-mail:xiaobolu@hotmail.com 
 * @version Create Date：2013-3-6 下午02:39:36 
 */

public interface MonitorManager {
	//获取用户流量使用情况 xml:返回数据  state 1:正常 2：计费接口异常
	public Map getMonitorData2(Long phoneNum,String areaCode);

}
