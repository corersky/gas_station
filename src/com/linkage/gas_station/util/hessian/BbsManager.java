package com.linkage.gas_station.util.hessian;

import java.util.HashMap;
import java.util.Map;

public interface BbsManager {
	//获取用户是否有管理员权限role_id 1：普通用户 2:管理员
	public HashMap getBbsRole(Long phoneNum);
	
	//获取论坛帖子列表
	//type -1 全部 2 精华
	public HashMap[] getBbsForumList(int type,int start,int pageSize);
	
	//获取某个帖子及跟帖
	public HashMap[] getBbsForumById(Long forumId,int start,int pageSize);

	//发帖 返回信息 result： 0：发帖失败！ 1：发帖成功！
	public Map saveNewFroum(Long forumId,String forumName,String forumContent,Long phoneNum, int phoneType);
	
	//回帖 返回信息 result： 0：回帖失败！ 1：回帖成功！
	public Map replyFroum(Long noteId,Long forumId,String noteContent,Long phoneNum, int phoneType);

}
