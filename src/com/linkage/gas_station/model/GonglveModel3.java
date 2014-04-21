package com.linkage.gas_station.model;

import android.os.Parcel;
import android.os.Parcelable;

public class GonglveModel3 implements Parcelable {
	
	String app_id="";
	String app_image_name="";
	String app_url="";
	String app_name="";
	String android_url="";
	
	public String getApp_name() {
		return app_name;
	}

	public void setApp_name(String app_name) {
		this.app_name = app_name;
	}

	public String getAndroid_url() {
		return android_url;
	}

	public void setAndroid_url(String android_url) {
		this.android_url = android_url;
	}

	public GonglveModel3() {
		
	}
	
	public GonglveModel3(Parcel source) {
		app_id=source.readString();
		app_image_name=source.readString();
		app_url=source.readString();
		app_name=source.readString();
		android_url=source.readString();
	}
	
	public String getApp_id() {
		return app_id;
	}
	public void setApp_id(String app_id) {
		this.app_id = app_id;
	}
	public String getApp_image_name() {
		return app_image_name;
	}
	public void setApp_image_name(String app_image_name) {
		this.app_image_name = app_image_name;
	}
	public String getApp_url() {
		return app_url;
	}
	public void setApp_url(String app_url) {
		this.app_url = app_url;
	}
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(app_id);
		dest.writeString(app_image_name);
		dest.writeString(app_url);
		dest.writeString(app_name);
		dest.writeString(android_url);
	}
	
	public static final Parcelable.Creator<GonglveModel3> CREATOR=new Parcelable.Creator<GonglveModel3>() {

		@Override
		public GonglveModel3 createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new GonglveModel3(source);
		}

		@Override
		public GonglveModel3[] newArray(int size) {
			// TODO Auto-generated method stub
			return new GonglveModel3[size];
		}};

}
