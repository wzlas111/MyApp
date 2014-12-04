package com.eastelsoft.lbs.entity;

import android.location.Location;

public class InfoAddBean {
	private String[] imgs = new String[0];
	private String reSoundName;
	private String video1;
	private String title= "";
	private String remark= "";
	private Location infoLocation = null;
	public String[] getImgs() {
		return imgs;
	}
	public void setImgs(String[] imgs) {
		this.imgs = imgs;
	}
	public String getReSoundName() {
		return reSoundName;
	}
	public void setReSoundName(String reSoundName) {
		this.reSoundName = reSoundName;
	}
	public String getVideo1() {
		return video1;
	}
	public void setVideo1(String video1) {
		this.video1 = video1;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public Location getInfoLocation() {
		return infoLocation;
	}
	public void setInfoLocation(Location infoLocation) {
		this.infoLocation = infoLocation;
	}
	
}
