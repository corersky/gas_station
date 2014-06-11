package com.linkage.gas_station.model;

import java.io.Serializable;

public class MemberModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	String supplyer_address="";
	String valid_date="";
	String prize_pic="";
	String supplyer_name="";
	String prize_name="";
	String supplyer_phone="";
	String prize_resude_cnt="";
	String prize_id="";
	public String getPrize_id() {
		return prize_id;
	}
	public void setPrize_id(String prize_id) {
		this.prize_id = prize_id;
	}
	public String getPrize_resude_cnt() {
		return prize_resude_cnt;
	}
	public void setPrize_resude_cnt(String prize_resude_cnt) {
		this.prize_resude_cnt = prize_resude_cnt;
	}
	public String getSupplyer_address() {
		return supplyer_address;
	}
	public void setSupplyer_address(String supplyer_address) {
		this.supplyer_address = supplyer_address;
	}
	public String getValid_date() {
		return valid_date;
	}
	public void setValid_date(String valid_date) {
		this.valid_date = valid_date;
	}
	public String getPrize_pic() {
		return prize_pic;
	}
	public void setPrize_pic(String prize_pic) {
		this.prize_pic = prize_pic;
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
