/**
 * Copyright (c) 2012-8-17 www.eastelsoft.com
 * $ID CustBean.java 下午11:58:07 $
 */
package com.eastelsoft.lbs.entity;

/**
 * 客户
 * 
 * @author lengcj
 */
public class CustBean {
	private String id;
	private String clientName;
	private String contacts;
	private String lon;
	private String lat;
	private String location;
	private String email;
	private String phone;
	private String clientNamePinYin;
	private String istijiao;
	public String getIstijiao() {
		return istijiao;
	}
	public void setIstijiao(String istijiao) {
		this.istijiao = istijiao;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getClientName() {
		return clientName;
	}
	public void setClientName(String clientName) {
		this.clientName = clientName;
	}
	public String getContacts() {
		return contacts;
	}
	public void setContacts(String contacts) {
		this.contacts = contacts;
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
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getClientNamePinYin() {
		return clientNamePinYin;
	}
	public void setClientNamePinYin(String clientNamePinYin) {
		this.clientNamePinYin = clientNamePinYin;
	}
	@Override
	public String toString() {
		return "CustBean [id=" + id + ", clientName=" + clientName
				+ ", contacts=" + contacts + ", lon=" + lon + ", lat=" + lat
				+ ", location=" + location + ", email=" + email + ", phone="
				+ phone + ", clientNamePinYin=" + clientNamePinYin + "]";
	}
}
