package com.linkage.gas_station.model;

import java.io.Serializable;
import java.util.ArrayList;

public class Flow_Group_Model implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	int flow_type_id=-1;
	String flow_type_name="";
	String flow_type_amount="";
	String flow_type_used="";
	String flow_type_unused="";
	ArrayList<Flow_Type_Group_Model> model_list=null;
	
	public int getFlow_type_id() {
		return flow_type_id;
	}
	public void setFlow_type_id(int flow_type_id) {
		this.flow_type_id = flow_type_id;
	}
	public String getFlow_type_name() {
		return flow_type_name;
	}
	public void setFlow_type_name(String flow_type_name) {
		this.flow_type_name = flow_type_name;
	}
	public String getFlow_type_amount() {
		return flow_type_amount;
	}
	public void setFlow_type_amount(String flow_type_amount) {
		this.flow_type_amount = flow_type_amount;
	}
	public String getFlow_type_used() {
		return flow_type_used;
	}
	public void setFlow_type_used(String flow_type_used) {
		this.flow_type_used = flow_type_used;
	}
	public String getFlow_type_unused() {
		return flow_type_unused;
	}
	public void setFlow_type_unused(String flow_type_unused) {
		this.flow_type_unused = flow_type_unused;
	}
	public ArrayList<Flow_Type_Group_Model> getModel_list() {
		return model_list;
	}
	public void setModel_list(ArrayList<Flow_Type_Group_Model> model_list) {
		this.model_list = model_list;
	}
	
	
}
