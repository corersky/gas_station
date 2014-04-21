package com.linkage.gas_station.model;

import java.util.ArrayList;

public class LifePhoneModel {

	ArrayList<LifeModel> model_list=null;
	String id="";
	String phone="";
	
	public LifePhoneModel() {
		model_list=new ArrayList<LifeModel>();
	}

	public ArrayList<LifeModel> getModel_list() {
		return model_list;
	}

	public void addModel(LifeModel model) {
		model_list.add(model);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
}
