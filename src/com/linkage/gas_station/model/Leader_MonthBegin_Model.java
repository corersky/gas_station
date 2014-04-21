package com.linkage.gas_station.model;

public class Leader_MonthBegin_Model {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	boolean check=true;
	
	String join_month="";
	long tribe_id=0l;
	String generate_time="";
	String member_name="";
	long role_id=0l;
	String member_phone="";
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
	public String getJoin_month() {
		return join_month;
	}
	public void setJoin_month(String join_month) {
		this.join_month = join_month;
	}
	public long getTribe_id() {
		return tribe_id;
	}
	public void setTribe_id(long tribe_id) {
		this.tribe_id = tribe_id;
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
	public long getRole_id() {
		return role_id;
	}
	public void setRole_id(long role_id) {
		this.role_id = role_id;
	}
	public String getMember_phone() {
		return member_phone;
	}
	public void setMember_phone(String member_phone) {
		this.member_phone = member_phone;
	}
	public long getMember_id() {
		return member_id;
	}
	public void setMember_id(long member_id) {
		this.member_id = member_id;
	}
	
}
