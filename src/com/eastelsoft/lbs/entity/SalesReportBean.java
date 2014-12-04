package com.eastelsoft.lbs.entity;

import com.eastelsoft.lbs.R;

/**
 * 商品清单
 * 
 * @author admin
 * 
 */
public class SalesReportBean {
	// db.execSQL("create table if not exists l_salesreport("
	// + "id NTEXT,"
	// + "clientid NTEXT,"
	// + "clientName NTEXT,"
	// + "date NTEXT,"
	// + "goods_id NTEXT,"
	// + "imgFile NTEXT,"
	// + "remark NTEXT,"
	// + "lon NTEXT,"
	// + "lat NTEXT,"
	// + "location NTEXT,"
	// + "istijiao NTEXT)");
	private String id;
	private String clientid;
	private String clientName;
	private String date;

	private String goods_id;

	private String imgFile;
	private String remark;
	private String lon;
	private String lat;
	private String location;
	private String istijiao;
	private String submitdate;

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
		return clientName;
	}

	public void setClientName(String clientName) {
		this.clientName = clientName;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getGoods_id() {
		return goods_id;
	}

	public void setGoods_id(String goods_id) {
		this.goods_id = goods_id;
	}

	public String getImgFile() {
		return imgFile;
	}

	public void setImgFile(String imgFile) {
		this.imgFile = imgFile;
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

	public String getIstijiao() {
		return istijiao;
	}

	public void setIstijiao(String istijiao) {
		this.istijiao = istijiao;
	}

	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}

	@Override
	public String toString() {
		return "id=" + id + ", clientName=" + clientName+ 
				", date=" + date +
				 ", goods_id=" + goods_id +
				", imgFile=" + imgFile +
				", remark=" + remark
				+ ", istijiao=" + istijiao + "]";
	}

	public String getSubmitdate() {
		return submitdate;
	}

	public void setSubmitdate(String submitdate) {
		this.submitdate = submitdate;
	}

	

}
