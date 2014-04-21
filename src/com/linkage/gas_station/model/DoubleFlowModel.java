package com.linkage.gas_station.model;

import java.io.Serializable;

public class DoubleFlowModel implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public String getResidue_flow() {
		return residue_flow;
	}
	public void setResidue_flow(String residue_flow) {
		this.residue_flow = residue_flow;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getGenerate_time() {
		return generate_time;
	}
	public void setGenerate_time(String generate_time) {
		this.generate_time = generate_time;
	}
	public String getChange_flow() {
		return change_flow;
	}
	public void setChange_flow(String change_flow) {
		this.change_flow = change_flow;
	}
	String residue_flow="";
	String title="";
	String generate_time="";
	String change_flow="";
	
	
}
