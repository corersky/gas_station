package com.linkage.gas_station.util.hessian;

import java.util.ArrayList;

import android.content.Context;

import com.caucho.hessian.client.HessianProxyFactory;
import com.linkage.gas_station.util.Util;
import com.linkage.gasstationjni.GasJni;

public class GetWebDate {
	
	/**
	 * 一般情况下hessian调用
	 * @return HessianProxyFactory对象
	 */
	public static HessianProxyFactory getHessionFactiory(Context context) {
		YeatsHessianProxyFactory factory=new YeatsHessianProxyFactory();
		ArrayList<String> list=Util.getUserInfo(context);
		GasJni hj=new GasJni();
		String nativeResult=hj.stringFromJNI(System.currentTimeMillis()+Util.getTimeExtra(context), Util.getVersionName(context), list.get(0), Util.getDeviceId(context), list.get(3));
		factory.setSignature(nativeResult);
		factory.setDebug(false);
		factory.setReadTimeout(15000);
		factory.setConnectTimeOut(15000);
		return factory;
	}
//	
//	/**
//	 * 提交订单情况下hessian调用
//	 * @return HessianProxyFactory对象
//	 */
//	public static HessianProxyFactory getPayHessionFactiory(Context context) {
//		YeatsHessianProxyFactory factory=new YeatsHessianProxyFactory();
//		factory.setMachineCode(Util.getDeviceId(context));
//		String temp=Util.encodeTime(System.currentTimeMillis()+Util.getTimeExtra(context));		
//		factory.setSignature(Util.getMD5Str(temp+Util.getDeviceId(context)+(Util.getUserInfo(context).get(0).equals("")?"-1":Util.getUserInfo(context).get(0))));
//		factory.setTimeStamp(temp);
//		factory.setDebug(false);
//		factory.setReadTimeout(5000);
//		factory.setConnectTimeout(5000);
//		return factory;
//	}
	
	/**
	 * 摇一摇请求
	 * @param context
	 * @return
	 */
	public static HessianProxyFactory getYaoHessionFactiory(Context context) {
		ArrayList<String> list=Util.getUserInfo(context);
		GasJni hj=new GasJni();
		YeatsHessianProxyFactory factory=new YeatsHessianProxyFactory();
		String nativeResult=hj.stringFromJNI(System.currentTimeMillis()+Util.getTimeExtra(context), Util.getVersionName(context), list.get(0), Util.getDeviceId(context), list.get(3));
		factory.setSignature(nativeResult);
		factory.setDebug(false);
		factory.setReadTimeout(10000);
		factory.setConnectTimeOut(10000);
		return factory;
	}
	
	/**
	 * 流量活动请求
	 * @param context
	 * @return
	 */
	public static HessianProxyFactory getActivityHessionFactiory(Context context) {
		ArrayList<String> list=Util.getUserInfo(context);
		GasJni hj=new GasJni();
		YeatsHessianProxyFactory factory=new YeatsHessianProxyFactory();
		String nativeResult=hj.stringFromJNI(System.currentTimeMillis()+Util.getTimeExtra(context), Util.getVersionName(context), list.get(0), Util.getDeviceId(context), list.get(3));
		factory.setSignature(nativeResult);
		factory.setDebug(false);
		factory.setReadTimeout(30000);
		factory.setConnectTimeOut(30000);
		return factory;
	}
	
	/**
	 * 一般情况下hessian调用
	 * @return HessianProxyFactory对象
	 */
	public static HessianProxyFactory getFirstHessionFactiory(Context context) {
		YeatsHessianProxyFactory factory=new YeatsHessianProxyFactory();
		factory.setDebug(false);
		factory.setReadTimeout(15000);
		factory.setConnectTimeOut(15000);
		return factory;
	}
	
}
