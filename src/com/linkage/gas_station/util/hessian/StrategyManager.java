package com.linkage.gas_station.util.hessian;

import java.util.HashMap;
import java.util.Map;

/** 
 * @author luxiaobo E-mail:xiaobolu@hotmail.com 
 * @version Create Date：2013-3-6 下午02:42:40 
 */

public interface StrategyManager {
	//获取流量攻略列表(全量)
	public Map[] getStrategyList(Long phoneNum,String areaCode);
	
	//获取流量攻略列表(推送)
	public Map[] pushStrategyList();
	
	//设置流量攻略已推送
	public boolean setStrategyPush();

	//获取流量活动列表
	public Map[] getActivityList(Long phoneNum,String areaCode);
	
	//获取流量活动详情
	public Map getActivityInfo(Long activityId,String areaCode);
	
	//应用推荐
	public Map[] getAppList(Long phoneNum,String areaCode);
	
	//参与分享活动 1:参与成功 2：您已参与过该活动 3:参与失败，请刷新活动界面重新参与。
	public Map joinActivity0510(Long phoneNum,String RephoneNum,String areaCode);
	
	//领取流量 1:领取成功 2：您当前没流量可领取 3：您当月已领取500M流量，请下月再来领取 4:领取失败
	public Map receiveFlow0510(Long phoneNum,Long offerId,String areaCode);
	
	//端午有礼，限时抢购
	public Map[] activity6_packages(Long phoneNum,Long activityId,String areaCode);

	//签到摇一摇 1:签到成功，您已领取5M流量 2：您今日已签到，请明天再来 4:领取失败
	public Map sign_activity(Long phoneNum,Long activityId,String areaCode);
	
	//摇一摇点击广告
	public Map sign_adver(Long phoneNum,Long adverId,String areaCode);
	
	//参与流量倍增活动 result 1:参与成功，您已获赠XXXM流量 2：您已参与该活动 4:参与失败  comments：失败原因  gift_flow:获赠流量
	//payType 1:账户余额 2：银联 3：翼支付
	public Map double_flow_partake(String seqId,Long phoneNum,int payType,String verCode,Long activityId,String areaCode);
	
	//参与消费倍增活动 result 1:参与成功，您已获赠XXXM流量  total_flow:总获赠流量 residue_flow:剩余流量 charge:上月费用
	//result 2:已领取  total_flow:总获赠流量 residue_flow:剩余流量 
	//result 4:参与失败  comments：失败原因 
	public Map double_flow_partake2(Long phoneNum,String areaCode,Long activityId);

	
	//action 1:领取 2：转赠
	//流量倍增领取或转赠 1:赠送成功，XXX已获赠XXXM流量 4:参与失败 
	public Map double_flow_operate(Long phoneNum,int action,Long toPhoneNum,double amount,Long activityId,String areaCode);
	
	//倍增流量历史按活动
	public Map[] getDoubleFlowList2(Long phoneNum,String areaCode,Long activityId);
	
	//验证用户是否合法 result 1:成功 4：失败 comments 失败原因
	public Map validUser(Long phoneNum,String areaCode,Long activityId);
	
	//查询用户当前流量信息
	//map.put("total_flow", 0);
	//map.put("residue_flow", 0);
	public Map double_flow_select(Long phoneNum,Long activityId,String areaCode);

	//流量银行
	//获取用户当前可转套餐
	//map.put("result", 0);map.put("comments", "系统维护，请稍后再试！");
	//map.put("result", 1);map.put("comments", str);str是个XML串
	public Map getGiftedList(Long phoneNum,String areaCode);
	//校验转赠用户是否合法 
	//result 0:失败 1：成功 comments:失败原因
	public Map checkValid(Long phoneNum,Long giftPhoneNum,String areaCode);
	//map.put("result", 0);map.put("comments", "系统维护，请稍后再试！");
	//map.put("result", 1);map.put("comments", str);str是个XML串
	//map.put("result", -1);"流量银行暂未开放"
	public Map giftedList(Long phoneNum,String areaCode,String startTime,String endTIme);
	//转赠接口 (转赠流量以K为单位)
	//result -1:验证码错误或失效 0:失败 1：成功 comments:失败原因
	public Map giftFlow(Long phoneNum,String areaCode,Long giftPhoneNum,String productId,String accuTypeId,String amount,String verCode,String seqId);

	//武林大会
	//获取人员角色信息
	//map.put("result", 1); result 1:酋长 2：首领 3：成员 4：不是成员,显示comments信息
	//result.put("isInit", 1); isInit 1:已授权 2:未授权
	//map.put("comments", "错误信息");
	public Map getWulinRole(Long phoneNum,String areaCode);
	
	
	//获取部落初始化列表 
	//map.put("result", 0);map.put("comments", "错误信息");
	//map.put("result", 1);map.put("tribes", tribes);tribes是Map[]
	public Map getWulinInitList(Long phoneNum,String areaCode);
	
	//获取部落信息
	public Map getTribeInfo(Long tribeId,Long phoneNum,String areaCode);
	
	//新增或修改部落信息
	//actionType 1:新增 2：修改 新增的时候tribeId传空
	//成功：map.put("result", 1);
	//失败：map.put("result", 0);map.put("comments", "错误信息！");
	public Map addOrUpdateTribe(Long tribeId,String tribeName,String chiefName,Long tribeChiefPhone,Long unionId,int actionType,String areaCode);
	
	//初始化部落
	//tribeIds 部落ID,以:间隔
	//成功：map.put("result", 1);
	//失败：map.put("result", 0);map.put("comments", "错误信息！");
	public Map initWulinList(Long phoneNum,String tribeIds,String areaCode);
	
	//获取部落成员初始化列表 
	//map.put("result", 0);map.put("comments", "错误信息");
	//map.put("result", 1);map.put("members", members);members是Map[]
	public Map getInitTribeMembers(Long phoneNum,String areaCode);
	
	//获取成员信息
	public Map getMemberInfo(Long memberId,Long phoneNum,String areaCode);
	
	//新增或修改成员信息 目前应该只有新增
	//actionType 1:新增 2：修改 新增的时候memberId传空
	//成功：map.put("result", 1);
	//失败：map.put("result", 0);map.put("comments", "错误信息！");
	public Map addOrUpdateMember(Long memberId,String memberName,Long memberPhone,Long tribeId,int actionType,String areaCode);
	
	//初始化成员信息
	//memberIds 成员ID,以:间隔
	//成功：map.put("result", 1);
	//失败：map.put("result", 0);map.put("comments", "错误信息！");
	public Map initTribeMembers(Long phoneNum,String memberIds,String areaCode);

	//获取部落管理列表
	//map.put("result", 0);map.put("comments", "错误信息");
	//map.put("result", 1);map.put("tribes", tribes);tribes是Map[]
	public Map getWulinList(Long phoneNum,String areaCode);
	
	//获取成员管理列表
	//map.put("result", 0);map.put("comments", "错误信息");
	//map.put("result", 1);map.put("members", members);members是Map[]
	//剩余量 result.put("residue_flow", Conver.convertLong(flowMap.get("residue_flow")));
	//总量 result.put("total_flow", Conver.convertLong(flowMap.get("total_flow")));
	public Map getTribeMembers(Long phoneNum,String areaCode);
	
	//action 1:领取 2：转赠
	//武林大会领取或转赠 1:赠送成功，XXX已获赠XXXM流量 4:参与失败 
	public Map getOrSendFlow(Long phoneNum,int action,Long toPhoneNum,double amount,String areaCode);
	
	//车友会流量领取
	//map.put("result", 1);map.put("comments", "成功领取1G流量！");
	//map.put("result", 4);map.put("comments", "系统维护，请稍后再试！");
	//成功失败都显示comments字段
	public Map receiveCarFlow(Long phoneNum,String areaCode,Long activityId);

	//新车友会  查询流量
	//map.put("result", 1);map.put("data", map);
	//map.put("result", 4);map.put("comments", "领取失败，请重试！");
	//失败显示comments字段
	public Map getFreeFlow(Long phoneNum,String areaCode,Long activityId);

	//获取当前影片
	//map.put("result", 1);map.put("data", map);
	//map.put("result", 4);map.put("comments", "XXX");
	//失败显示comments字段
	public Map getMovies(Long phoneNum,String areaCode,Long activityId);

	//获取影院及场次
	public Map[] getCinema(Long phoneNum,String areaCode,Long filmId,Long activityId);

	//获取空余电影票
	public Map[] getTickets(Long phoneNum,String areaCode,Long id,Long activityId);

	//锁定座位
	//map.put("result", 1);
	//map.put("result", 4);map.put("comments", "XXX");
	//成功进入支付界面，失败显示comments字段，并刷新当前票位表
	public Map lockTickets(Long phoneNum,String areaCode,String ids,Long activityId);
	
	//提交订单
	public Map orderTickets(String seqId,Long phoneNum,String verCode,String areaCode,int type,String ids,int cost,Long activityId);
	
	//电影票订单列表
	public Map[] orderList(Long phoneNum,String areaCode);
	
	//电影票详情列表
	public Map movieOrderInfo(Long phoneNum,String areaCode,Long ticketId);
	
	//全心券意 获取加油列表
	public Map[] activity26_packages(Long phoneNum,Long activityId,String areaCode);
		
	//领券
	public Map receiveTickets(Long phoneNum,String areaCode);
	
	//查询可用券
	public HashMap queryTicket(Long phoneNum,String areaCode,int cost);
	
	//订购流量套餐(1:成功;2:失败;3:验证码错误;)
	//返回信息 deal_result： -1：验证码错误或失效 0：提交失败 1：提交成功 2：CRM接口订购达到最大次数 3:加油成功 
	public Map saveQxjyOrder(String seqId,Long phoneNum,Long offerId,String verCode,String areaCode,int inType,int payType,Long quanOfferId);

	//聚油宝
	//查询用户当前流量币
	public Map queryFlowCoin(Long phoneNum,String areaCode);
	
	//查询流量币明细
	//type 1:赚取 2：兑换
	public Map[] queryCoinDetail(Long phoneNum,String areaCode,int type);

	//兑换接口(1:成功;2:失败;3:验证码错误;)
	//type -1:话费 -2：流量
	//客户端传过来的金额单位为个
	//返回信息 result： -1：验证码错误或失效  1：兑换成功 4：兑换失败
	public Map exchangeCoin(String seqId,Long phoneNum,String verCode,String areaCode,int type,int coin);
	
	
	//订购流量套餐(1:成功;2:失败;3:验证码错误;)
	//inType 1:流量监控 2：流量加油 3：流量攻略 4:特色流量包 5:聚油宝(内) 6:聚油宝(外)
	//sourceType 1:加油站购买 2:朋友赠送 3:微信朋友圈 4:新浪微薄 5:易信 6:QQ空间
	//payType 1:账户余额 2：银联 3：翼支付
	//客户端传过来的金额单位为分，流量为KB
	//返回信息 deal_result： -1：验证码错误或失效 0：提交失败 1：提交成功 2：CRM接口订购达到最大次数 3:加油成功
	//cost=-1 amount=-1 就是长期有效的
	public Map jybOrder(String seqId,Long phoneNum,Long RePhoneNum,Long offerId,String verCode,String areaCode,int inType,int sourceType,int payType,int cost,int amount);

	//聚油宝
	//查询话费池剩余话费
	public Map queryResidueCharge(Long phoneNum,String areaCode);
	//获取聚油宝流量套餐列表(根据入口及附加信息返回) 
	//oilType 1：标准流量(买流量) 2：所有流量(拉土豪)
	public Map[] getJybOilList(Long phoneNum,int oilType,String areaCode);
	
	//流量币汇总
	//type 1:赚取 2：兑换
	//exchange_type -2:兑换流量;-1:兑换话费;0:总收益或总兑换;1:加油站购买;2:朋友赠送;3:微信朋友圈;4:新浪微薄;5:易信;6:QQ空间
	public Map[] queryCoinSummary(Long phoneNum,String areaCode,int type);

	//获取无锡号段
	public Map[] getWXheads(Long phoneNum,String areaCode);
	
	//获取WAP页面地址
	public Map getWapUrl(Long phoneNum,String areaCode);

	//领取流量币
	public Map receiveFlowCoin(Long phoneNum,String areaCode);
	
	//获取当前用户总的抽奖机会和剩余的抽奖机会
	public Map getLotteryCondition(Long phoneNum,String areaCode,Long activityId);
	
	//获取特等奖，一，二，三....等奖的中奖信息
	public Map[] getLotteryList(String month,Long activityId);
	
	//用户抽奖
	public Map drawLottery(Long phoneNum,String machineCode,String areaCode,Long activityId);
	
	//获取当前用户中奖信息
	public Map[] getLotteryInfo(Long phoneNum,String month,Long activityId);
	
	//无锡一猜到底
	//获取用户当天的答题状态
	//user_state -1:当前无题可答，请改日再来！ 0:答题中 1:完成答题(分享界面)
	public Map getUserGuessInfo(Long phoneNum,String areaCode);
	
	//用户答题
	public Map guessQuestion(Long phoneNum,String areaCode,Long questionId,String userAnswer);
	
	//分享留痕
	//shareType 3:微信朋友圈 4:新浪微薄 5:易信 6:QQ空间
	public Map shareInfo(Long phoneNum,String areaCode,int shareType,Long activityId,String content);
	
	//会员日
	//获取奖品列表
	//result.put("memberDay", minDay); 下期会员日时间，为空就显示敬请期待。
	//result.put("memberPrizes", prizes); 奖品列表数组
	public Map memberPrizes(Long phoneNum,String areaCode);
	
	//抢兑奖品
	public Map receivePrizes2(String seqId, Long phoneNum,String areaCode,String verCode,Long prizeId);
	
	//获取用户兑换列表
	public Map[] userPrizes(Long phoneNum,String areaCode);
	
	//商家扫码
	public Map supplyerScan(Long phoneNum,String areaCode,String code);
	
	//商家记录
	public Map[] supplyerScanList(Long phoneNum,String areaCode,String month,int start,int pageSize);
	
	//是否商家
	public Map validSupplyer(Long phoneNum,String areaCode);
	
	//是否会员
	public Map validMember(Long phoneNum,String areaCode,Long prizeId);
	
	//领金币
	public Map receiveCoin(Long phoneNum,String areaCode,Long activityId);
	
	//获取购买红包记录
	public Map[] buyRedPackets(Long phoneNum,String areaCode,Long activityId);
	
	//红包排行
	//type 0:全部 1:最短时间;2:最广;3:最土豪;
	public Map redPacketsRank(Long phoneNum,String areaCode,Long activityId,int type);
	
	//土豪查询
	public Map redPacketsPerson(Long phoneNum,String areaCode,Long activityId);
	
	//抢红包列表
	public Map[] redPacketById(Long phoneNum,String areaCode,String seqId);
	
	//订购流量套餐(1:成功;2:失败;3:验证码错误;)
	//inType 1:流量监控 2：流量加油 3：流量攻略 4：特色流量包 5:聚油宝(内) 6:聚油宝(外)[areaCode传0000] 活动ID
	//payType 1:账户余额 2：银联 3：翼支付
	//客户端传过来的金额单位为分，流量为KB
	//返回信息 deal_result： -1：验证码错误或失效 0：提交失败 1：提交成功 2：CRM接口订购达到最大次数 3:加油成功 4：青海活动
	//cost=-1 amount=-1 就是长期有效的
	//orderType 1:为自己 2：为他人
	public Map holidayOrder(String seqId,Long phoneNum,Long offerId,String verCode,String areaCode,int inType,int payType,int cost,int amount,int orderType,Long rePhoneNum);

	//赠送记录
	public Map[] sendPackageList(Long phoneNum,String areaCode,Long activityId);

	//获取免费流量活动列表
	public Map[] getFreeFlowList(Long phoneNum,String areaCode,Long activityId);
	
	//领流量币
	public Map receiveFreeCoin(Long phoneNum,String areaCode,Long freeId);
	
	//免费流量活动点击查看
	public void freeFlowHit(Long phoneNum,String areaCode,Long activityId,Long freeFlowId);
	
	//4G活动查询相关信息
	public Map get4GInfo(Long phoneNum,String areaCode,Long activityId);
	
	//4G用户流量领取 
	public Map get4GFlow(Long phoneNum,double amount,Long activityId,String areaCode);
	
	//流量日
	//获取奖品列表
	//result.put("flowDay", minDay); 下期流量日时间，为空就显示敬请期待。
	//result.put("flowPrizes", prizes); 奖品列表数组
	public Map flowPrizes(Long phoneNum,String areaCode);
	
	//抢兑奖品
	public Map receiveFlowPrizes(String seqId,Long phoneNum,String areaCode,String verCode,Long prizeId,Long flowOfferId);
	
	//获取用户兑换列表
	public Map[] userFlowPrizes(Long phoneNum,String areaCode);
	
	//获取流量列表
	public Map[] getflowByCost(Long phoneNum,String areaCode,int prizeType );

	//领现金券
	public Map receiveXjTickets(Long phoneNum,String areaCode);
	
	//查询可用券
	public HashMap queryXjTicket(Long phoneNum,String areaCode,int cost);
	
	//订购流量套餐(1:成功;2:失败;3:验证码错误;)
	//返回信息 deal_result： -1：验证码错误或失效 0：提交失败 1：提交成功 2：CRM接口订购达到最大次数 3:加油成功 
	public Map saveXjqOrder(String seqId,Long phoneNum,Long offerId,String verCode,String areaCode,int inType,int payType,Long quanOfferId);

}
