/**
 * Copyright (c) 2012-7-20 www.eastelsoft.com
 * $ID Login1Req.java 下午9:42:05 $
 */
package com.eastelsoft.lbs.entity;

/**
 * 登录认证中心请求
 * @author lengcj
 */
public class Login1Req extends HeadInfo {
	private String deviceType;
	private String serialNumber;
	private String imei;
	private String imsi;
	private String softVersion;
	
	public Login1Req(short command_id, short cmd_id, short msg_seq, String device_id, 
			String deviceType, String serialNumber, String imei, String imsi,String softVersion) {
		this.command_id = command_id;
		this.cmd_id = cmd_id;
		this.msg_seq = msg_seq;
		this.device_id = device_id;
		this.deviceType = deviceType;
		this.serialNumber = serialNumber;
		this.imei = imei;
		this.imsi = imsi;
		this.softVersion = softVersion;
		this.length = 10 + deviceType.length() + serialNumber.length() + imei.length() 
				+ imsi.length()+ softVersion.length() + 48;
	}
	
	public String getSoftVersion() {
		return softVersion;
	}
	public void setSoftVersion(String softVersion) {
		this.softVersion = softVersion;
	}
	public String getDeviceType() {
		return deviceType;
	}
	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	public String getImsi() {
		return imsi;
	}
	public void setImsi(String imsi) {
		this.imsi = imsi;
	}
	@Override
	public String toString() {
		return "RegReq [deviceType=" + deviceType + ", serialNumber="
				+ serialNumber + ", imei=" + imei + ", imsi=" + imsi + "]";
	}
	
}
