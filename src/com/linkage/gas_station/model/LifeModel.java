package com.linkage.gas_station.model;

public class LifeModel {
	
	String id="";
	String owe="";
	boolean isChecked=false;
	String parentPhone="";
	
	public String getParentPhone() {
		return parentPhone;
	}
	public void setParentPhone(String parentPhone) {
		this.parentPhone = parentPhone;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getOwe() {
		return owe;
	}
	public void setOwe(String owe) {
		this.owe = owe;
	}
	public boolean isChecked() {
		return isChecked;
	}
	public void setChecked(boolean isChecked) {
		this.isChecked = isChecked;
	}

}
