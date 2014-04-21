package com.linkage.gas_station.model;

import java.io.Serializable;
import java.util.ArrayList;

public class OutputInfoModel implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String data_time="";
	ArrayList<Flow_Group_Model> model_list=null;
	public String getData_time() {
		return data_time;
	}
	public void setData_time(String data_time) {
		this.data_time = data_time;
	}
	public ArrayList<Flow_Group_Model> getModel_list() {
		return model_list;
	}
	public void setModel_list(ArrayList<Flow_Group_Model> model_list) {
		this.model_list = model_list;
	}
	
}
