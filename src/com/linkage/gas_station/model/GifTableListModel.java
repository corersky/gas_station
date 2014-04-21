package com.linkage.gas_station.model;

import java.util.ArrayList;

public class GifTableListModel {
	int giftable_num=0;
	ArrayList<ProductGroupModel> productGroupModel_list=null;
	public int getGiftable_num() {
		return giftable_num;
	}
	public void setGiftable_num(int giftable_num) {
		this.giftable_num = giftable_num;
	}
	public ArrayList<ProductGroupModel> getProductGroupModel_list() {
		return productGroupModel_list;
	}
	public void setProductGroupModel_list(
			ArrayList<ProductGroupModel> productGroupModel_list) {
		this.productGroupModel_list = productGroupModel_list;
	}
}
