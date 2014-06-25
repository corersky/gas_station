package com.linkage.gas_station.model;

import android.os.Parcel;
import android.os.Parcelable;

public class GonglveTuanModel implements Parcelable {
	String activity_id="";
	String activity_type="";
	String activity_name="";
	String activity_start_time="";
	String activity_end_time="";
	String activity_image_name="";
	String offer_amount="";
	String offer_cost="";
	String offer_description="";
	String old_cost="";
	String persons="";
	String offer_id="";
	String activity_description="";
	String activity_url="";
	String offer_name="";
	String activity_rule="";
	
	int is_jion=0;
	double unrecevice_num=0;
	int total_num=0;
	int is_sign=0;

	public String getOffer_name() {
		return offer_name;
	}

	public void setOffer_name(String offer_name) {
		this.offer_name = offer_name;
	}

	public int getIs_sign() {
		return is_sign;
	}

	public void setIs_sign(int is_sign) {
		this.is_sign = is_sign;
	}

	public GonglveTuanModel() {
		
	}
	
	public GonglveTuanModel(Parcel source) {
		activity_id=source.readString();
		activity_type=source.readString();
		activity_name=source.readString();
		activity_start_time=source.readString();
		activity_end_time=source.readString();
		activity_image_name=source.readString();
		offer_amount=source.readString();
		offer_cost=source.readString();
		offer_description=source.readString();
		old_cost=source.readString();
		persons=source.readString();
		offer_id=source.readString();
		activity_description=source.readString();
		is_jion=source.readInt();
		unrecevice_num=source.readDouble();
		total_num=source.readInt();
		activity_url=source.readString();
		offer_name=source.readString();
		activity_rule=source.readString();
	}
	
	public String getActivity_description() {
		return activity_description;
	}

	public void setActivity_description(String activity_description) {
		this.activity_description = activity_description;
	}

	public String getActivity_id() {
		return activity_id;
	}
	public void setActivity_id(String activity_id) {
		this.activity_id = activity_id;
	}
	public String getActivity_type() {
		return activity_type;
	}
	public void setActivity_type(String activity_type) {
		this.activity_type = activity_type;
	}
	public String getActivity_name() {
		return activity_name;
	}
	public void setActivity_name(String activity_name) {
		this.activity_name = activity_name;
	}
	public String getActivity_start_time() {
		return activity_start_time;
	}
	public void setActivity_start_time(String activity_start_time) {
		this.activity_start_time = activity_start_time;
	}
	public String getActivity_end_time() {
		return activity_end_time;
	}
	public void setActivity_end_time(String activity_end_time) {
		this.activity_end_time = activity_end_time;
	}
	public String getActivity_image_name() {
		return activity_image_name;
	}
	public void setActivity_image_name(String activity_image_name) {
		this.activity_image_name = activity_image_name;
	}
	public String getOffer_amount() {
		return offer_amount;
	}
	public void setOffer_amount(String offer_amount) {
		this.offer_amount = offer_amount;
	}
	public String getOffer_cost() {
		return offer_cost;
	}
	public void setOffer_cost(String offer_cost) {
		this.offer_cost = offer_cost;
	}
	public String getOffer_description() {
		return offer_description;
	}
	public void setOffer_description(String offer_description) {
		this.offer_description = offer_description;
	}
	public String getOld_cost() {
		return old_cost;
	}
	public void setOld_cost(String old_cost) {
		this.old_cost = old_cost;
	}
	public String getPersons() {
		return persons;
	}
	public void setPersons(String persons) {
		this.persons = persons;
	}
	public String getOffer_id() {
		return offer_id;
	}
	public void setOffer_id(String offer_id) {
		this.offer_id = offer_id;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(activity_id);
		dest.writeString(activity_type);
		dest.writeString(activity_name);
		dest.writeString(activity_start_time);
		dest.writeString(activity_end_time);
		dest.writeString(activity_image_name);
		dest.writeString(offer_amount);
		dest.writeString(offer_cost);
		dest.writeString(offer_description);
		dest.writeString(old_cost);
		dest.writeString(persons);
		dest.writeString(offer_id);
		dest.writeString(activity_description);
		dest.writeInt(is_jion);
		dest.writeDouble(unrecevice_num);
		dest.writeInt(total_num);
		dest.writeString(activity_url);
		dest.writeString(offer_name);
		dest.writeString(activity_rule);
	}
	
	public int getIs_jion() {
		return is_jion;
	}

	public void setIs_jion(int is_jion) {
		this.is_jion = is_jion;
	}

	public double getUnrecevice_num() {
		return unrecevice_num;
	}

	public void setUnrecevice_num(double unrecevice_num) {
		this.unrecevice_num = unrecevice_num;
	}

	public int getTotal_num() {
		return total_num;
	}

	public void setTotal_num(int total_num) {
		this.total_num = total_num;
	}
	
	public String getActivity_url() {
		return activity_url;
	}

	public void setActivity_url(String activity_url) {
		this.activity_url = activity_url;
	}

	public String getActivity_rule() {
		return activity_rule;
	}

	public void setActivity_rule(String activity_rule) {
		this.activity_rule = activity_rule;
	}



	public static final Parcelable.Creator<GonglveTuanModel> CREATOR=new Parcelable.Creator<GonglveTuanModel>() {

		@Override
		public GonglveTuanModel createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new GonglveTuanModel(source);
		}

		@Override
		public GonglveTuanModel[] newArray(int size) {
			// TODO Auto-generated method stub
			return new GonglveTuanModel[size];
		}};

}
