package com.eastelsoft.lbs.entity;

import com.eastelsoft.lbs.R;

public class VisitBean {
	
	
	private String id;
	private String clientid;
	private String clientname;
	//上传图片的名字
	private String date;
	private String title;
	private String remark;
	private String lon;
	private String lat;
	private String location;
	private String imgFile;
	private String istijiao;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getClientid() {
		return clientid;
	}
	public void setClientid(String clientid) {
		this.clientid = clientid;
	}
	public String getClientName() {
		return clientname;
	}
	public void setClientName(String clientName) {
		this.clientname = clientName;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
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
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getImgFile() {
		return imgFile;
	}
	public void setImgFile(String imgFile) {
		this.imgFile = imgFile;
	}
	public String getIstijiao() {
		return istijiao;
	}
	public void setIstijiao(String istijiao) {
		this.istijiao = istijiao;
	}
	@Override
	public String toString() {
		return "VisitBean [id=" + id + ", clientid=" + clientid
				+ ", clientname=" + clientname + ", date=" + date + ", title="
				+ title + ", remark=" + remark + ", lon=" + lon + ", lat="
				+ lat + ", location=" + location + ", imgFile=" + imgFile
				+ ", istijiao=" + istijiao + "]";
	}
	
	
}
