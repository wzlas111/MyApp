/**
 * Copyright (c) 2012-8-1 www.eastelsoft.com
 * $ID SetInfo.java 下午3:24:59 $
 */
package com.eastelsoft.lbs.entity;

/**
 * 参数信息
 * @author lengcj
 */
public class SetInfo {
	private String imei;
	private String imsi;
	private String power = "50";
	private String adapter_ip;
	private int adapter_port;
	private long gps_time;
	//增加UDP端口
	private int udp_adapter_port;
	public int getUdp_adapter_port() {
		return udp_adapter_port;
	}
	public void setUdp_adapter_port(int udp_adapter_port) {
		this.udp_adapter_port = udp_adapter_port;
	}
	private String serialNumber;
	private String auth_code;
	private String device_id;
	private String timePeriod;
	private String filterDate;
	private String week;
	private int interval;
	
	private String planupdatecode;
	private String custupdatecode;
	//商品版本
	private String goodsupdatecode;
	//yue target
	private String monthstargetupdatecode;
	
	private String shock_select;
	private String msg_select;
	
	
	
	//认证中心返回参数
	private String gateip;
	private int gatetcpport;
	private int gateudpport;
	private String httpip;
	private String pfsign;
	
	
	public String getGateip() {
		return gateip;
	}
	public void setGateip(String gateip) {
		this.gateip = gateip;
	}
	public int getGatetcpport() {
		return gatetcpport;
	}
	public void setGatetcpport(int gatetcpport) {
		this.gatetcpport = gatetcpport;
	}
	public int getGateudpport() {
		return gateudpport;
	}
	public void setGateudpport(int gateudpport) {
		this.gateudpport = gateudpport;
	}
	public String getHttpip() {
		return httpip;
	}
	public void setHttpip(String httpip) {
		this.httpip = httpip;
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
	public String getPower() {
		return power;
	}
	public void setPower(String power) {
		this.power = power;
	}
	public String getAdapter_ip() {
		return adapter_ip;
	}
	public void setAdapter_ip(String adapter_ip) {
		this.adapter_ip = adapter_ip;
	}
	public int getAdapter_port() {
		return adapter_port;
	}
	public void setAdapter_port(int adapter_port) {
		this.adapter_port = adapter_port;
	}
	public String getSerialNumber() {
		return serialNumber;
	}
	public void setSerialNumber(String serialNumber) {
		this.serialNumber = serialNumber;
	}
	public String getAuth_code() {
		return auth_code;
	}
	public void setAuth_code(String auth_code) {
		this.auth_code = auth_code;
	}
	public String getDevice_id() {
		return device_id;
	}
	public void setDevice_id(String device_id) {
		this.device_id = device_id;
	}
	public String getTimePeriod() {
		return timePeriod;
	}
	public void setTimePeriod(String timePeriod) {
		this.timePeriod = timePeriod;
	}
	public String getFilterDate() {
		return filterDate;
	}
	public void setFilterDate(String filterDate) {
		this.filterDate = filterDate;
	}
	public String getWeek() {
		return week;
	}
	public void setWeek(String week) {
		this.week = week;
	}
	public int getInterval() {
		return interval;
	}
	public void setInterval(int interval) {
		this.interval = interval;
	}
	public String getPlanupdatecode() {
		return planupdatecode;
	}
	public void setPlanupdatecode(String planupdatecode) {
		this.planupdatecode = planupdatecode;
	}
	public String getCustupdatecode() {
		return custupdatecode;
	}
	public void setCustupdatecode(String custupdatecode) {
		this.custupdatecode = custupdatecode;
	}
	public String getShock_select() {
		return shock_select;
	}
	public void setShock_select(String shock_select) {
		this.shock_select = shock_select;
	}
	public String getMsg_select() {
		return msg_select;
	}
	public void setMsg_select(String msg_select) {
		this.msg_select = msg_select;
	}
	public long getGps_time() {
		return gps_time;
	}
	public void setGps_time(long gps_time) {
		this.gps_time = gps_time;
	}
	@Override
	public String toString() {
		return "SetInfo [imei=" + imei + ", imsi=" + imsi + ", power=" + power
				+ ", adapter_ip=" + adapter_ip + ", adapter_port="
				+ adapter_port + ", gps_time=" + gps_time
				+ ", udp_adapter_port=" + udp_adapter_port + ", serialNumber="
				+ serialNumber + ", auth_code=" + auth_code + ", device_id="
				+ device_id + ", timePeriod=" + timePeriod + ", filterDate="
				+ filterDate + ", week=" + week + ", interval=" + interval
				+ ", planupdatecode=" + planupdatecode + ", custupdatecode="
				+ custupdatecode + ", shock_select=" + shock_select
				+ ", msg_select=" + msg_select + "]";
	}
	public String getPfsign() {
		return pfsign;
	}
	public void setPfsign(String pfsign) {
		this.pfsign = pfsign;
	}
	public String getGoodsupdatecode() {
		return goodsupdatecode;
	}
	public void setGoodsupdatecode(String goodsupdatecode) {
		this.goodsupdatecode = goodsupdatecode;
	}
	public String getMonthstargetupdatecode() {
		return monthstargetupdatecode;
	}
	public void setMonthstargetupdatecode(String monthstargetupdatecode) {
		this.monthstargetupdatecode = monthstargetupdatecode;
	}
}
