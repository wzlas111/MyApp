/**
 * Copyright (c) 2012-7-25 www.eastelsoft.com
 * $ID HeartbeatResp.java 上午9:47:33 $
 */
package com.eastelsoft.lbs.entity;

/**
 * 心跳应答包
 * @author lengcj
 */
public class HeartbeatResp extends HeadInfo {
	private String ret;
	private String rept;
	private short ack_seq;
	private String auth_code;
	public HeartbeatResp(){}
	public HeartbeatResp(short command_id, short cmd_id, short msg_seq, String ret, 
			 short ack_seq, String device_id, String auth_code){
		this.command_id = command_id;
		this.cmd_id = cmd_id;
		this.msg_seq = msg_seq;
		this.ret = ret;
		this.ack_seq = ack_seq;
		this.device_id = device_id;
		this.auth_code = auth_code;
		this.length = 2 + ret.length() + 48;
	}
	public String getRet() {
		return ret;
	}
	public void setRet(String ret) {
		this.ret = ret;
	}
	public String getRept() {
		return rept;
	}
	public void setRept(String rept) {
		this.rept = rept;
	}
	public short getAck_seq() {
		return ack_seq;
	}
	public void setAck_seq(short ack_seq) {
		this.ack_seq = ack_seq;
	}
	public String getAuth_code() {
		return auth_code;
	}
	public void setAuth_code(String auth_code) {
		this.auth_code = auth_code;
	}
	@Override
	public String toString() {
		return "HeartbeatResp [ret=" + ret + ", rept=" + rept + ", ack_seq="
				+ ack_seq + ", auth_code=" + auth_code + "]";
	}
}
