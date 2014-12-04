package com.eastelsoft.lbs.entity;

import com.eastelsoft.lbs.R;

public class LocBean {
	/*db.execSQL("create table if not exists l_loc("
			+ "locTime NTEXT," 
			+ "chkTag NTEXT,"
			+ "lon NTEXT,"
			+ "lat NTEXT,"
			+ "addr NTEXT,"
			+ "accuracy NTEXT)");*/
	private String locTime;
	private String chkTag;
	private String lon;
	private String lat;
	private String addr;
	private String accuracy;
	public String getLocTime() {
		return locTime;
	}
	public void setLocTime(String locTime) {
		this.locTime = locTime;
	}
	public String getChkTag() {
		return chkTag;
	}
	public void setChkTag(String chkTag) {
		this.chkTag = chkTag;
	}
	public String getLon() {
		return lon;
	}
	public void setLon(String lon) {
		this.lon = lon;
	}
	public String getLat() {
		return lat;
	}
	public void setLat(String lat) {
		this.lat = lat;
	}
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public String getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(String accuracy) {
		this.accuracy = accuracy;
	}
	

}
