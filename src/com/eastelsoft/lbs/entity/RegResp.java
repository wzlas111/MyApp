/**
 * Copyright (c) 2012-7-20 www.eastelsoft.com
 * $ID RegResp.java 下午9:40:49 $
 */
package com.eastelsoft.lbs.entity;

/**
 * 注册应答
 * @author lengcj
 */
public class RegResp {
	private String ret;
	private String device_id;
	private String ret_desc;
	public String getRet() {
		return ret;
	}
	public void setRet(String ret) {
		this.ret = ret;
	}
	public String getDevice_id() {
		return device_id;
	}
	public void setDevice_id(String device_id) {
		this.device_id = device_id;
	}
	public String getRet_desc() {
		return ret_desc;
	}
	public void setRet_desc(String ret_desc) {
		this.ret_desc = ret_desc;
	}
	@Override
	public String toString() {
		return "RegResp [ret=" + ret + ", device_id=" + device_id
				+ ", ret_desc=" + ret_desc + "]";
	}
	
}
