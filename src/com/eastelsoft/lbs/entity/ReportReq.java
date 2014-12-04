/**
 * Copyright (c) 2012-7-20 www.eastelsoft.com
 * $ID ReportReq.java 下午9:50:06 $
 */
package com.eastelsoft.lbs.entity;

import com.eastelsoft.lbs.location.BaseStationAction.SCell;

/**
 * 数据上报请求
 * @author lengcj
 */
public class ReportReq extends HeadInfo {
	private String packageType;
	private String latitude;
	private String longitude;
	private String speed;
	private String mileage;
	private String direct;
	private String signal;
	private String power;
	private String satellite;
	private String reportTag;
	private String auth_code;
	private String accuracy;
	private String reportTime;
	
	private String cells;
	
	public ReportReq(short command_id, short cmd_id, short msg_seq, String device_id, 
			String packageType, String longitude, String latitude,
			String power, String satellite, String auth_code,
			String accuracy, String reportTime) {
		this.command_id = command_id;
		this.cmd_id = cmd_id;
		this.msg_seq = msg_seq;
		this.device_id = device_id;
		this.packageType = packageType;
		this.latitude = latitude;
		this.longitude = longitude;
		this.power = power;
		this.satellite = satellite;
		this.auth_code = auth_code;
		this.accuracy = accuracy;
		this.reportTime = reportTime;
		this.length = 14 + packageType.length() + latitude.length() 
				+ longitude.length() + power.length() + satellite.length() 
				+ accuracy.length() + reportTime.length() + 48;
	}
	
	public ReportReq(short command_id, short cmd_id, short msg_seq, String device_id, 
			String packageType, String longitude, String latitude,
			String power, String satellite, String auth_code,
			String accuracy, String reportTime, SCell cell) {
		this.command_id = command_id;
		this.cmd_id = cmd_id;
		this.msg_seq = msg_seq;
		this.device_id = device_id;
		this.packageType = packageType;
		this.latitude = latitude;
		this.longitude = longitude;
		this.power = power;
		this.satellite = satellite;
		this.auth_code = auth_code;
		this.accuracy = accuracy;
		this.reportTime = reportTime;
		if(cell != null) cells = cell.MNC + "," + cell.MCC + "," + cell.LAC + cell.CID;
		this.length = 14 + packageType.length() + latitude.length() 
				+ longitude.length() + power.length() + satellite.length() 
				+ accuracy.length() + reportTime.length()  + cells.length() + 48;
	}
	
	public ReportReq(short command_id, short cmd_id, short msg_seq, String device_id, 
			String packageType, String longitude, String latitude,
			String power, String satellite, String reportTag, String auth_code,
			String accuracy, String reportTime) {
		this.command_id = command_id;
		this.cmd_id = cmd_id;
		this.msg_seq = msg_seq;
		this.device_id = device_id;
		this.packageType = packageType;
		this.latitude = latitude;
		this.longitude = longitude;
		this.power = power;
		this.satellite = satellite;
		this.reportTag = reportTag;
		this.auth_code = auth_code;
		this.accuracy = accuracy;
		this.reportTime = reportTime;
		this.length = 16 + packageType.length() + latitude.length() 
				+ longitude.length() + power.length() + satellite.length() 
				+ accuracy.length() + reportTime.length() + reportTag.length() + 48;

	}
	public String getPackageType() {
		return packageType;
	}
	public void setPackageType(String packageType) {
		this.packageType = packageType;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getSpeed() {
		return speed;
	}
	public void setSpeed(String speed) {
		this.speed = speed;
	}
	public String getMileage() {
		return mileage;
	}
	public void setMileage(String mileage) {
		this.mileage = mileage;
	}
	public String getDirect() {
		return direct;
	}
	public void setDirect(String direct) {
		this.direct = direct;
	}
	public String getSignal() {
		return signal;
	}
	public void setSignal(String signal) {
		this.signal = signal;
	}
	public String getPower() {
		return power;
	}
	public void setPower(String power) {
		this.power = power;
	}
	public String getSatellite() {
		return satellite;
	}
	public void setSatellite(String satellite) {
		this.satellite = satellite;
	}
	public String getReportTag() {
		return reportTag;
	}
	public void setReportTag(String reportTag) {
		this.reportTag = reportTag;
	}
	public String getAuth_code() {
		return auth_code;
	}
	public void setAuth_code(String auth_code) {
		this.auth_code = auth_code;
	}
	public String getAccuracy() {
		return accuracy;
	}
	public void setAccuracy(String accuracy) {
		this.accuracy = accuracy;
	}
	public String getReportTime() {
		return reportTime;
	}
	public void setReportTime(String reportTime) {
		this.reportTime = reportTime;
	}
	public String getCells() {
		return cells;
	}

	public void setCells(String cells) {
		this.cells = cells;
	}

	@Override
	public String toString() {
		return "ReportReq [packageType=" + packageType + ", latitude="
				+ latitude + ", longitude=" + longitude + ", speed=" + speed
				+ ", mileage=" + mileage + ", direct=" + direct + ", signal="
				+ signal + ", power=" + power + ", satellite=" + satellite
				+ ", reportTag=" + reportTag + ", auth_code=" + auth_code
				+ ", accuracy=" + accuracy + ", reportTime=" + reportTime
				+ ", cells=" + cells + "]";
	}
}
