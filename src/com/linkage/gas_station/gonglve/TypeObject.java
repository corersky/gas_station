package com.linkage.gas_station.gonglve;

public class TypeObject {

	String typeName = "";
	String typeDes = "";
	int typeId = 0;
	int cost=0;
	int offer_type_id=0;
	
	public TypeObject(String name , String des , int id, int cost, int offer_type_id)
	{
		typeName = name;
		typeDes = des;
		typeId = id;
		this.cost=cost;
		this.offer_type_id=offer_type_id;
	}
	
	public int getOffer_type_id() {
		return offer_type_id;
	}

	public void setOffer_type_id(int offer_type_id) {
		this.offer_type_id = offer_type_id;
	}


	public String getTypeName() {
		return typeName;
	}

	public void setTypeName(String typeName) {
		this.typeName = typeName;
	}

	public String getTypeDes() {
		return typeDes;
	}

	public void setTypeDes(String typeDes) {
		this.typeDes = typeDes;
	}

	public int getCost() {
		return cost;
	}

	public void setCost(int cost) {
		this.cost = cost;
	}

	public int getTypeId() {
		return typeId;
	}

	public void setTypeId(int typeId) {
		this.typeId = typeId;
	}
	
	

}
