package com.linkage.gas_station.model;

public class UpdateModel {
	
	String message="";
	String version="";
	int android_forced_update=0;
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public int getAndroid_forced_update() {
		return android_forced_update;
	}
	public void setAndroid_forced_update(int android_forced_update) {
		this.android_forced_update = android_forced_update;
	}
	

}
