package com.linkage.gas_station.model;

import java.io.Serializable;

public class MemberBuyModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String id="";
	String supplyer_address="";
	String generate_time="";
	String prize_pic="";
	String phone_num="";
	int state=0;
	String valid_date="";
	String supplyer_name="";
	String prize_name="";
	String supplyer_phone="";
	String scan_time="";
	int rn=0;
	int prize_type=0;
	String residue_times="";
	String total_times="";
	String seqId="-1";
	
	public String getSeqId() {
		return seqId;
	}
	public void setSeqId(String seqId) {
		this.seqId = seqId;
	}
	public int getPrize_type() {
		return prize_type;
	}
	public void setPrize_type(int prize_type) {
		this.prize_type = prize_type;
	}
	public String getResidue_times() {
		return residue_times;
	}
	public void setResidue_times(String residue_times) {
		this.residue_times = residue_times;
	}
	public String getTotal_times() {
		return total_times;
	}
	public void setTotal_times(String total_times) {
		this.total_times = total_times;
	}
	public String getScan_time() {
		return scan_time;
	}
	public void setScan_time(String scan_time) {
		this.scan_time = scan_time;
	}
	public int getRn() {
		return rn;
	}
	public void setRn(int rn) {
		this.rn = rn;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getSupplyer_address() {
		return supplyer_address;
	}
	public void setSupplyer_address(String supplyer_address) {
		this.supplyer_address = supplyer_address;
	}
	public String getGenerate_time() {
		return generate_time;
	}
	public void setGenerate_time(String generate_time) {
		this.generate_time = generate_time;
	}
	public String getPrize_pic() {
		return prize_pic;
	}
	public void setPrize_pic(String prize_pic) {
		this.prize_pic = prize_pic;
	}
	public String getPhone_num() {
		return phone_num;
	}
	public void setPhone_num(String phone_num) {
		this.phone_num = phone_num;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public String getValid_date() {
		return valid_date;
	}
	public void setValid_date(String valid_date) {
		this.valid_date = valid_date;
	}
	public String getSupplyer_name() {
		return supplyer_name;
	}
	public void setSupplyer_name(String supplyer_name) {
		this.supplyer_name = supplyer_name;
	}
	public String getPrize_name() {
		return prize_name;
	}
	public void setPrize_name(String prize_name) {
		this.prize_name = prize_name;
	}
	public String getSupplyer_phone() {
		return supplyer_phone;
	}
	public void setSupplyer_phone(String supplyer_phone) {
		this.supplyer_phone = supplyer_phone;
	}
	
}
