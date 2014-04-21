package com.linkage.gas_station.model;

public class MarketModel {
	int amount=0;
	String android_url="";
	int app_id=0;
	String app_name="";
	int app_type_id=0;
	String app_image_name="";
	String android_package="";
	int seaty=0;
	int seatx=0;
	boolean isChecked=false;
	
	public boolean isChecked() {
		return isChecked;
	}
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}
	public int getSeaty() {
		return seaty;
	}
	public void setSeaty(int seaty) {
		this.seaty = seaty;
	}
	public int getSeatx() {
		return seatx;
	}
	public void setSeatx(int seatx) {
		this.seatx = seatx;
	}
	public String getAndroid_package() {
		return android_package;
	}
	public void setAndroid_package(String android_package) {
		this.android_package = android_package;
	}
	public int getAmount() {
		return amount;
	}
	public void setAmount(int amount) {
		this.amount = amount;
	}
	public String getAndroid_url() {
		return android_url;
	}
	public void setAndroid_url(String android_url) {
		this.android_url = android_url;
	}
	public int getApp_id() {
		return app_id;
	}
	public void setApp_id(int app_id) {
		this.app_id = app_id;
	}
	public String getApp_name() {
		return app_name;
	}
	public void setApp_name(String app_name) {
		this.app_name = app_name;
	}
	public int getApp_type_id() {
		return app_type_id;
	}
	public void setApp_type_id(int app_type_id) {
		this.app_type_id = app_type_id;
	}
	public String getApp_image_name() {
		return app_image_name;
	}
	public void setApp_image_name(String app_image_name) {
		this.app_image_name = app_image_name;
	}
}
