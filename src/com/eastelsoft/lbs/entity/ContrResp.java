/**
 * Copyright (c) 2012-8-2 www.eastelsoft.com
 * $ID ContrResp.java 下午1:42:09 $
 */
package com.eastelsoft.lbs.entity;

/**
 * 远程控制响应
 * @author lengcj
 */
public class ContrResp extends HeadInfo {
	private String ret;
	private String type;
	

	private String auth_code;
	private short ack_seq;
	
	public ContrResp(short command_id, short cmd_id, short msg_seq, String ret,String type, 
			String device_id, String auth_code, short ack_seq){
		this.command_id = command_id;
		this.cmd_id = cmd_id;
		this.msg_seq = msg_seq;
		this.ret = ret;
		this.type =type;
		this.device_id = device_id;
		this.auth_code = auth_code;
		this.ack_seq = ack_seq;
	   //这里加工作计划后变为4
		this.length = 4 + ret.length()+type.length() + 48;
	}

	public String getRet() {
		return ret;
	}

	public void setRet(String ret) {
		this.ret = ret;
	}
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getAuth_code() {
		return auth_code;
	}

	public void setAuth_code(String auth_code) {
		this.auth_code = auth_code;
	}

	public short getAck_seq() {
		return ack_seq;
	}

	public void setAck_seq(short ack_seq) {
		this.ack_seq = ack_seq;
	}

	@Override
	public String toString() {
		return "ContrResp [ret=" + ret + ", auth_code=" + auth_code
				+ ", ack_seq=" + ack_seq + "]";
	}
	
}
