/**
 * Copyright (c) 2012-7-20 www.eastelsoft.com
 * $ID Login2Req.java 下午9:44:33 $
 */
package com.eastelsoft.lbs.entity;

/**
 * 登录适配请求
 * @author lengcj
 */
public class Login2Req extends HeadInfo {
	private String adapter_ip;
	private int adapter_port;
	//增加UDP端口
	private int udp_adapter_port;
	public int getUdp_adapter_port() {
		return udp_adapter_port;
	}
	public void setUdp_adapter_port(int udp_adapter_port) {
		this.udp_adapter_port = udp_adapter_port;
	}
	private String auth_code;
	private String imei;
	public Login2Req(short command_id, short cmd_id, short msg_seq, String device_id, 
			String adapter_ip, int adapter_port, String auth_code, String imei) {
		this.command_id = command_id;
		this.cmd_id = cmd_id;
		this.msg_seq = msg_seq;
		this.device_id = device_id;
		this.adapter_ip = adapter_ip;
		this.adapter_port = adapter_port;
		this.auth_code = auth_code;
		this.imei = imei;
		this.length = 4 + imei.length() + auth_code.length() + 48;
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
	public String getAuth_code() {
		return auth_code;
	}
	public void setAuth_code(String auth_code) {
		this.auth_code = auth_code;
	}
	public String getImei() {
		return imei;
	}
	public void setImei(String imei) {
		this.imei = imei;
	}
	@Override
	public String toString() {
		return "Login2Req [adapter_ip=" + adapter_ip + ", adapter_port="
				+ adapter_port + ", auth_code=" + auth_code + ", device_id="
				+ device_id + ", imei=" + imei + "]";
	}
}
