package com.linkage.gas_station.util.hessian;

import java.util.Map;

public interface LotteryManager {
	/**
	 * 获取当前用户剩余的抽奖机会以及抽奖的有效时间
	 */
	public Map getLotteryCondition(Long phoneNum,String areaCode);
	
	/**
	 * 获取特等奖，一，二，三....等奖的中奖信息
	 * @return
	 */
	public Map[] getLotteryList(String month);
	
	/**
	 * 获取其他等级奖项的中奖数量和剩余数量
	 * @return
	 */
	//public Map[] getLotteryCnt(String month);
	
	/**
	 * 用户抽奖
	 * @param phoneNum
	 * @param areaCode
	 * @return
	 */
	public Map drawLottery(Long phoneNum,String machineCode ,String areaCode);
	
	/**
	 * 获取当前用户中奖信息
	 * @param phoneNum
	 * @param month
	 * @return
	 */
	public Map[] getLotteryInfo(Long phoneNum,String month);
	
	/**
	 * 保存或者更新用户地址信息
	 * @param phoneNum
	 * @param machineCode
	 * @param areaCode
	 * @param userName
	 * @param address
	 * @return
	 */
	public Map saveAddress(Long phoneNum,String machineCode,String areaCode,String userName,String address);
	
	/**
	 * 获取用户的地址信息
	 * @param phoneNum
	 * @param machineCode
	 * @return
	 */
	public Map getAddress(Long phoneNum,String machineCode);

}
