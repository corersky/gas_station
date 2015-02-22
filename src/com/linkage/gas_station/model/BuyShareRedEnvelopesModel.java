package com.linkage.gas_station.model;

public class BuyShareRedEnvelopesModel {

	int choice=0;
	int id=0;
	String offer_description="";
	int offer_id=0;
	int cost=500;
	String offer_name="";

	public int getChoice() {
		return choice;
	}

	public void setChoice(int choice) {
		this.choice = choice;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getOffer_description() {
		return offer_description;
	}

	public void setOffer_description(String offer_description) {
		this.offer_description = offer_description;
	}

	public int getOffer_id() {
		return offer_id;
	}

	public void setOffer_id(int offer_id) {
		this.offer_id = offer_id;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public String getOffer_name() {
		return offer_name;
	}

	public void setOffer_name(String offer_name) {
		this.offer_name = offer_name;
	}
}
