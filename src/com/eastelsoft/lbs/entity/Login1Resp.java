/**
 * Copyright (c) 2012-7-20 www.eastelsoft.com
 * $ID Login1Resp.java 下午9:42:40 $
 */
package com.eastelsoft.lbs.entity;

/**
 * 登录认证中心应答
 * @author lengcj
 */
public class Login1Resp {
	private String ret;
	private String adapter_ip;
	private int adapter_port;
	private String auth_code;
	private String ret_desc;
	
	//增加UDP端口
	private int udp_adapter_port;
	
	// 增加搜星时间
	private long gps_time = 50;
	
	
	public int getUdp_adapter_port() {
		return udp_adapter_port;
	}
	public void setUdp_adapter_port(int udp_adapter_port) {
		this.udp_adapter_port = udp_adapter_port;
	}
	public String getRet() {
		return ret;
	}
	public void setRet(String ret) {
		this.ret = ret;
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
	public String getRet_desc() {
		return ret_desc;
	}
	public void setRet_desc(String ret_desc) {
		this.ret_desc = ret_desc;
	}
	public long getGps_time() {
		return gps_time;
	}
	public void setGps_time(long gps_time) {
		this.gps_time = gps_time;
	}
	@Override
	public String toString() {
		return "Login1Resp [ret=" + ret + ", adapter_ip=" + adapter_ip
				+ ", adapter_port=" + adapter_port + ", auth_code=" + auth_code
				+ ", ret_desc=" + ret_desc + ", udp_adapter_port="
				+ udp_adapter_port + ", gps_time=" + gps_time + "]";
	}
}
