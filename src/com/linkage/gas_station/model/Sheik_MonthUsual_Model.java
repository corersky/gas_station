package com.linkage.gas_station.model;

import java.io.Serializable;

public class Sheik_MonthUsual_Model implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	boolean check=true;
	Long trideId=0l;
	Long unionId=0l;
	String join_month="";
	String member_phone="";
	String generate_time="";
	String member_name="";
	String tribe_name="";
	long member_id=0l;
	String sum_flows="";
	String psum_flow="";
	
	public String getSum_flows() {
		return sum_flows;
	}
	public void setSum_flows(String sum_flows) {
		this.sum_flows = sum_flows;
	}
	public String getPsum_flow() {
		return psum_flow;
	}
	public void setPsum_flow(String psum_flow) {
		this.psum_flow = psum_flow;
	}
	public boolean isCheck() {
		return check;
	}
	public void setCheck(boolean check) {
		this.check = check;
	}
	public Long getTrideId() {
		return trideId;
	}
	public void setTrideId(Long trideId) {
		this.trideId = trideId;
	}
	public Long getUnionId() {
		return unionId;
	}
	public void setUnionId(Long unionId) {
		this.unionId = unionId;
	}
	public String getJoin_month() {
		return join_month;
	}
	public void setJoin_month(String join_month) {
		this.join_month = join_month;
	}
	public String getMember_phone() {
		return member_phone;
	}
	public void setMember_phone(String member_phone) {
		this.member_phone = member_phone;
	}
	public String getGenerate_time() {
		return generate_time;
	}
	public void setGenerate_time(String generate_time) {
		this.generate_time = generate_time;
	}
	public String getMember_name() {
		return member_name;
	}
	public void setMember_name(String member_name) {
		this.member_name = member_name;
	}
	public String getTribe_name() {
		return tribe_name;
	}
	public void setTribe_name(String tribe_name) {
		this.tribe_name = tribe_name;
	}
	public long getMember_id() {
		return member_id;
	}
	public void setMember_id(long member_id) {
		this.member_id = member_id;
	}

}
