package com.linkage.gas_station.util.hessian;

import java.util.Map;

/** 
 * @author luxiaobo E-mail:xiaobolu@hotmail.com 
 * @version Create Date：2013-3-6 下午02:41:16 
 */

public interface OilManager {
	//获取流量套餐列表(根据入口及附加信息返回) 
	//oilType 1：全国流量 2：夜间流量 3：流量卡
	public Map[] getOilList(Long phoneNum,int oilType,String other,String areaCode);

	//inType 1:流量监控 2：流量加油 3：流量攻略 4：活动  6:端午有礼，限时抢购 7:忙闲时
	//payType 1:账户余额 2：银联 3：翼支付
	//客户端传过来的金额单位为分，流量为KB
	//返回信息 deal_result： -1：验证码错误或失效 0：提交失败 1：提交成功 2：CRM接口订购达到最大次数 3:加油成功
	//cost=-1 amount=-1 就是长期有效的
	public Map saveOrder2(String seqId,Long phoneNum,Long offerId,String verCode,String areaCode,int inType,int payType,int cost,int amount);
	
	//订购历史查询
	public Map[] getOrderList(Long phoneNum,String areaCode);
	
	public Map getUserType(Long phoneNum,String areaCode);
	//获取翼支付余额 result 1 成功 2 失败  yue 成功时为余额，失败时为失败原因
	public Map getYzfyue(Long phoneNum,String areaCode);
	
}
