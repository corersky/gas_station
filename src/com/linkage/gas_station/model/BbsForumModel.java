package com.linkage.gas_station.model;

import java.io.Serializable;

public class BbsForumModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String generate_time="";
	int rn=0;
	int reply_times=0;
	String phone_num="";
	long forum_id=0;
	String forum_name="";
	int is_top=0;
	String forum_content=""; 
	int phoneType=1;
	public int getPhoneType() {
		return phoneType;
	}
	public void setPhoneType(int phoneType) {
		this.phoneType = phoneType;
	}
	public String getGenerate_time() {
		return generate_time;
	}
	public void setGenerate_time(String generate_time) {
		this.generate_time = generate_time;
	}
	public int getRn() {
		return rn;
	}
	public void setRn(int rn) {
		this.rn = rn;
	}
	public int getReply_times() {
		return reply_times;
	}
	public void setReply_times(int reply_times) {
		this.reply_times = reply_times;
	}
	public String getPhone_num() {
		return phone_num;
	}
	public void setPhone_num(String phone_num) {
		this.phone_num = phone_num;
	}
	public long getForum_id() {
		return forum_id;
	}
	public void setForum_id(long forum_id) {
		this.forum_id = forum_id;
	}
	public String getForum_name() {
		return forum_name;
	}
	public void setForum_name(String forum_name) {
		this.forum_name = forum_name;
	}
	public int getIs_top() {
		return is_top;
	}
	public void setIs_top(int is_top) {
		this.is_top = is_top;
	}
	public String getForum_content() {
		return forum_content;
	}
	public void setForum_content(String forum_content) {
		this.forum_content = forum_content;
	}
	public String getLast_modify_time() {
		return last_modify_time;
	}
	public void setLast_modify_time(String last_modify_time) {
		this.last_modify_time = last_modify_time;
	}
	public int getForum_type() {
		return forum_type;
	}
	public void setForum_type(int forum_type) {
		this.forum_type = forum_type;
	}
	String last_modify_time="";
	int forum_type=0;
	
	
}
