package com.linkage.gas_station.model;

public class MemberluckUpdate {
	int bill_offer_id=0;
	int vip_level=0;
	int vip_benefits=0;
	int offer_id=0;
	String vip_name="";
	String offer_name="";
	String offer_tips="";
	
	public String getOffer_tips() {
		return offer_tips;
	}
	public void setOffer_tips(String offer_tips) {
		this.offer_tips = offer_tips;
	}
	public int getBill_offer_id() {
		return bill_offer_id;
	}
	public void setBill_offer_id(int bill_offer_id) {
		this.bill_offer_id = bill_offer_id;
	}
	public int getVip_level() {
		return vip_level;
	}
	public void setVip_level(int vip_level) {
		this.vip_level = vip_level;
	}
	public int getVip_benefits() {
		return vip_benefits;
	}
	public void setVip_benefits(int vip_benefits) {
		this.vip_benefits = vip_benefits;
	}
	public int getOffer_id() {
		return offer_id;
	}
	public void setOffer_id(int offer_id) {
		this.offer_id = offer_id;
	}
	public String getVip_name() {
		return vip_name;
	}
	public void setVip_name(String vip_name) {
		this.vip_name = vip_name;
	}
	public String getOffer_name() {
		return offer_name;
	}
	public void setOffer_name(String offer_name) {
		this.offer_name = offer_name;
	}
	
}
