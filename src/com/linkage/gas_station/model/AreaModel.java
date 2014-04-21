package com.linkage.gas_station.model;

import java.io.Serializable;

public class AreaModel implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
		
	int type=-1;
	String name="";
	int id=-1;
	String parentName="";
	
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

}
