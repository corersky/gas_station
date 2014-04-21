package com.linkage.gas_station.model;

import android.os.Parcel;
import android.os.Parcelable;

public class GonglveModel1 implements Parcelable {
	
	String strategy_type="";
	String strategy_description="";
	String generate_time="";
	String offer_id="";
	String offer_name="";
	String activity_id="";
	String activity_url="";

	
	public GonglveModel1() {
		
	}
	
	public GonglveModel1(Parcel source) {
		strategy_type=source.readString();
		strategy_description=source.readString();
		generate_time=source.readString();
		offer_id=source.readString();
		offer_name=source.readString();
		activity_id=source.readString();
		activity_url=source.readString();
	}
	
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(strategy_type);
		dest.writeString(strategy_description);
		dest.writeString(generate_time);
		dest.writeString(offer_id);
		dest.writeString(offer_name);
		dest.writeString(activity_id);
		dest.writeString(activity_url);
	}

	public String getStrategy_type() {
		return strategy_type;
	}

	public void setStrategy_type(String strategy_type) {
		this.strategy_type = strategy_type;
	}

	public String getStrategy_description() {
		return strategy_description;
	}

	public void setStrategy_description(String strategy_description) {
		this.strategy_description = strategy_description;
	}

	public String getGenerate_time() {
		return generate_time;
	}

	public void setGenerate_time(String generate_time) {
		this.generate_time = generate_time;
	}

	public String getOffer_id() {
		return offer_id;
	}

	public void setOffer_id(String offer_id) {
		this.offer_id = offer_id;
	}

	public String getOffer_name() {
		return offer_name;
	}

	public void setOffer_name(String offer_name) {
		this.offer_name = offer_name;
	}

	public String getActivity_id() {
		return activity_id;
	}

	public void setActivity_id(String activity_id) {
		this.activity_id = activity_id;
	}

	public String getActivity_url() {
		return activity_url;
	}

	public void setActivity_url(String activity_url) {
		this.activity_url = activity_url;
	}

	public static final Parcelable.Creator<GonglveModel1> CREATOR=new Parcelable.Creator<GonglveModel1>() {

		@Override
		public GonglveModel1 createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new GonglveModel1(source);
		}

		@Override
		public GonglveModel1[] newArray(int size) {
			// TODO Auto-generated method stub
			return new GonglveModel1[size];
		}};
	
}

