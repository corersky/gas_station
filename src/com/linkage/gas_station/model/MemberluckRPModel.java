package com.linkage.gas_station.model;

public class MemberluckRPModel {

	int task_id=0;
	int state=0;
	int task_type=0;
	int offer_id=0;
	int task_benefits=0;
	String offer_name="";
	String task_name="";
	String offer_tips="";
	public String getOffer_tips() {
		return offer_tips;
	}
	public void setOffer_tips(String offer_tips) {
		this.offer_tips = offer_tips;
	}
	public String getTask_name() {
		return task_name;
	}
	public void setTask_name(String task_name) {
		this.task_name = task_name;
	}
	public int getTask_id() {
		return task_id;
	}
	public void setTask_id(int task_id) {
		this.task_id = task_id;
	}
	public int getState() {
		return state;
	}
	public void setState(int state) {
		this.state = state;
	}
	public int getTask_type() {
		return task_type;
	}
	public void setTask_type(int task_type) {
		this.task_type = task_type;
	}
	public int getOffer_id() {
		return offer_id;
	}
	public void setOffer_id(int offer_id) {
		this.offer_id = offer_id;
	}
	public int getTask_benefits() {
		return task_benefits;
	}
	public void setTask_benefits(int task_benefits) {
		this.task_benefits = task_benefits;
	}
	public String getOffer_name() {
		return offer_name;
	}
	public void setOffer_name(String offer_name) {
		this.offer_name = offer_name;
	}
	
}
