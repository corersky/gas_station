package com.linkage.gas_station.util.hessian;

import java.util.Map;

public interface FLowmarketManager {
	//按类型获取超市商品
	public Map[] getAppList(Long phoneNum,int appTypeId,String areaCode);

}
