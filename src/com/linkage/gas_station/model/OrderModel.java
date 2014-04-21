package com.linkage.gas_station.model;

public class OrderModel {
	
	String order_id="";
	String offer_name="";
	String order_time="";
	String order_state="";
	String comments="";
	String offer_image_name="";
	String pay_type="";
	String offer_type_name="";
	public String getOffer_type_name() {
		return offer_type_name;
	}
	public void setOffer_type_name(String offer_type_name) {
		this.offer_type_name = offer_type_name;
	}
	public String getPay_type() {
		return pay_type;
	}
	public void setPay_type(String pay_type) {
		this.pay_type = pay_type;
	}
	int offer_type_id=-1;
	
	public int getOffer_type_id() {
		return offer_type_id;
	}
	public void setOffer_type_id(int offer_type_id) {
		this.offer_type_id = offer_type_id;
	}
	public String getOrder_id() {
		return order_id;
	}
	public void setOrder_id(String order_id) {
		this.order_id = order_id;
	}
	public String getOffer_name() {
		return offer_name;
	}
	public void setOffer_name(String offer_name) {
		this.offer_name = offer_name;
	}
	public String getOrder_time() {
		return order_time;
	}
	public void setOrder_time(String order_time) {
		this.order_time = order_time;
	}
	public String getOrder_state() {
		return order_state;
	}
	public void setOrder_state(String order_state) {
		this.order_state = order_state;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public String getOffer_image_name() {
		return offer_image_name;
	}
	public void setOffer_image_name(String offer_image_name) {
		this.offer_image_name = offer_image_name;
	}
	
	

}
