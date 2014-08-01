package com.linkage.gas_station.model;

import java.io.Serializable;

public class NiubilityModel implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int ITEM = 0;
	public static final int SECTION = 1;
	
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getPhoneNum() {
		return phoneNum;
	}
	public void setPhoneNum(String phoneNum) {
		this.phoneNum = phoneNum;
	}
	public String getEndTime() {
		return endTime;
	}
	public void setEndTime(String endTime) {
		this.endTime = endTime;
	}
	public String getSection_desp() {
		return section_desp;
	}
	public void setSection_desp(String section_desp) {
		this.section_desp = section_desp;
	}
	public String getEndtime_desp() {
		return endtime_desp;
	}
	public void setEndtime_desp(String endtime_desp) {
		this.endtime_desp = endtime_desp;
	}
	public int getPosition() {
		return position;
	}
	public void setPosition(int position) {
		this.position = position;
	}
	public String getItem_desp() {
		return item_desp;
	}
	public void setItem_desp(String item_desp) {
		this.item_desp = item_desp;
	}
	public boolean isLast() {
		return isLast;
	}
	public void setLast(boolean isLast) {
		this.isLast = isLast;
	}

	int type=0;
	
	int position=0;
	
	String phoneNum="";
	String item_desp="";
	
	String endTime="";
	String section_desp="";
	String endtime_desp="";
	boolean isLast=false;
}
