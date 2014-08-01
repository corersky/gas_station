package com.linkage.gas_station.model;

public class RedPacketsModel {

	String interface_id="";
	String generate_time="";
	int total_times=0;
	String offer_name="";
	int residue_times=0;
	
	public String getInterface_id() {
		return interface_id;
	}
	public void setInterface_id(String interface_id) {
		this.interface_id = interface_id;
	}
	public String getGenerate_time() {
		return generate_time;
	}
	public void setGenerate_time(String generate_time) {
		this.generate_time = generate_time;
	}
	public int getTotal_times() {
		return total_times;
	}
	public void setTotal_times(int total_times) {
		this.total_times = total_times;
	}
	public String getOffer_name() {
		return offer_name;
	}
	public void setOffer_name(String offer_name) {
		this.offer_name = offer_name;
	}
	public int getResidue_times() {
		return residue_times;
	}
	public void setResidue_times(int residue_times) {
		this.residue_times = residue_times;
	}
	
}
