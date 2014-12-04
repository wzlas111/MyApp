/**
 * Copyright (c) 2012-7-20 www.eastelsoft.com
 * $ID RegReq.java 下午9:39:46 $
 */
package com.eastelsoft.lbs.entity;

/**
 * 注册请求
 * @author lengcj
 */
public class RegReq extends HeadInfo {
	private String deviceType;
	private String serialNumber;
	private String imei;
	private String imsi;
	
	private String phoneBrand;
	private String phoneModel;
	private String phoneOs;
	private String softVersion;
	private String phoneResolution;
	
	public RegReq(){
	}
	public RegReq(short command_id, short cmd_id, short msg_seq, String device_id, 
			String deviceType, String serialNumber, String imei, String imsi,
			String phoneBrand,String phoneModel,String phoneOs,String softVersion,String phoneResolution) {
		this.command_id = command_id;
		this.cmd_id = cmd_id;
		this.msg_seq = msg_seq;
		this.device_id = device_id;
		this.deviceType = deviceType;
		this.serialNumber = serialNumber;
		this.imei = imei;
		this.imsi = imsi;
		this.phoneBrand = phoneBrand;
		this.phoneModel = phoneModel;
		this.phoneOs = phoneOs;
		this.softVersion = softVersion;
		this.phoneResolution = phoneResolution;
		
		this.length = 18 + deviceType.length() + serialNumber.length() + imei.length() 
				+ imsi.length()+ phoneBrand.length()+ phoneModel.length()+ phoneOs.length()+ softVersion.length()+ phoneResolution.length() + 48;
	}
	
	public String getPhoneBrand() {
		return phoneBrand;
	}
	public void setPhoneBrand(String phoneBrand) {
		this.phoneBrand = phoneBrand;
	}
	public String getPhoneModel() {
		return phoneModel;
	}
	public void setPhoneModel(String phoneModel) {
		this.phoneModel = phoneModel;
	}
	public String getPhoneOs() {
		return phoneOs;
	}
	public void setPhoneOs(String phoneOs) {
		this.phoneOs = phoneOs;
	}
	public String getSoftVersion() {
		return softVersion;
	}
	public void setSoftVersion(String softVersion) {
		this.softVersion = softVersion;
	}
	public String getPhoneResolution() {
		return phoneResolution;
	}
	public void setPhoneResolution(String phoneResolution) {
		this.phoneResolution = phoneResolution;
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
