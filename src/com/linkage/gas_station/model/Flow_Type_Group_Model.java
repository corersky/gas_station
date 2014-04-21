package com.linkage.gas_station.model;

import java.io.Serializable;

public class Flow_Type_Group_Model implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	String product_id="";
	String product_name="";
	String product_amount="";
	String product_used="";
	String product_unused="";
	public String getProduct_id() {
		return product_id;
	}
	public void setProduct_id(String product_id) {
		this.product_id = product_id;
	}
	public String getProduct_name() {
		return product_name;
	}
	public void setProduct_name(String product_name) {
		this.product_name = product_name;
	}
	public String getProduct_amount() {
		return product_amount;
	}
	public void setProduct_amount(String product_amount) {
		this.product_amount = product_amount;
	}
	public String getProduct_used() {
		return product_used;
	}
	public void setProduct_used(String product_used) {
		this.product_used = product_used;
	}
	public String getProduct_unused() {
		return product_unused;
	}
	public void setProduct_unused(String product_unused) {
		this.product_unused = product_unused;
	}
	
	

}
