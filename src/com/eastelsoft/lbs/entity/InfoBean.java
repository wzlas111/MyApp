package com.eastelsoft.lbs.entity;

import com.eastelsoft.lbs.R;

public class InfoBean {
	private String info_auto_id;
	private String uploadDate;
	private String title;
	//上传图片的名字
	private String imgFile;
	private String remark;
	private String location;
	private String lon;
	private String lat;
	//listview里显示的图片
	private int info_imgFile =R.drawable.line_bg;
	private String istijiao;
	
	public String getIstijiao() {
		return istijiao;
	}
	public void setIstijiao(String istijiao) {
		this.istijiao = istijiao;
	}
	public String getInfo_auto_id() {
		return info_auto_id;
	}
	public void setInfo_auto_id(String info_auto_id) {
		this.info_auto_id = info_auto_id;
	}
	public String getUploadDate() {
		return uploadDate;
	}
	public void setUploadDate(String uploadDate) {
		this.uploadDate = uploadDate;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getImgFile() {
		return imgFile;
	}
	public void setImgFile(String imgFile) {
		this.imgFile = imgFile;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
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
	public int getInfo_imgFile() {
		return info_imgFile;
	}
	public void setInfo_imgFile(int info_imgFile) {
		this.info_imgFile = info_imgFile;
	}
	
	
	

}
