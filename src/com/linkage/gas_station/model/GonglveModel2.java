package com.linkage.gas_station.model;

import android.os.Parcel;
import android.os.Parcelable;

public class GonglveModel2 implements Parcelable {
	
	String picUrl="";
	String name="";
	String content="";
	String rule="";
	String time="";
	String area="";
	String link="";
	int id=-1;
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getArea() {
		return area;
	}

	public void setArea(String area) {
		this.area = area;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}
	
	public GonglveModel2() {
		
	}
	
	public GonglveModel2(Parcel source) {
		picUrl=source.readString();
		name=source.readString();
		content=source.readString();
		rule=source.readString();
		time=source.readString();
		area=source.readString();
		link=source.readString();
	}
	

	public String getPicUrl() {
		return picUrl;
	}

	public void setPicUrl(String picUrl) {
		this.picUrl = picUrl;
	}

	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// TODO Auto-generated method stub
		dest.writeString(picUrl);
		dest.writeString(name);
		dest.writeString(content);
		dest.writeString(rule);
		dest.writeString(time);
		dest.writeString(area);
		dest.writeString(link);
	}
	
	public static final Parcelable.Creator<GonglveModel2> CREATOR=new Parcelable.Creator<GonglveModel2>() {

		@Override
		public GonglveModel2 createFromParcel(Parcel source) {
			// TODO Auto-generated method stub
			return new GonglveModel2(source);
		}

		@Override
		public GonglveModel2[] newArray(int size) {
			// TODO Auto-generated method stub
			return new GonglveModel2[size];
		}};

}
