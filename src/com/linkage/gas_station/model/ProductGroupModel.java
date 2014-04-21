package com.linkage.gas_station.model;

public class ProductGroupModel implements Cloneable {
	
	String product_id="";
	String product_name="";
	String accu_type_id="";
	double unused_amount=0;
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
	public String getAccu_type_id() {
		return accu_type_id;
	}
	public void setAccu_type_id(String accu_type_id) {
		this.accu_type_id = accu_type_id;
	}
	public double getUnused_amount() {
		return unused_amount;
	}
	public void setUnused_amount(double unused_amount) {
		this.unused_amount = unused_amount;
	}
	
	public Object clone(){
		ProductGroupModel o = null;
        try{
            o = (ProductGroupModel)super.clone();
        }catch(CloneNotSupportedException ex){
            ex.printStackTrace();
        }
        return o;
    }


}
