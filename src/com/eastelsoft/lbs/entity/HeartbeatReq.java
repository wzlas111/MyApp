/**
 * Copyright (c) 2012-7-25 www.eastelsoft.com
 * $ID HeartbeatReq.java 上午9:43:29 $
 */
package com.eastelsoft.lbs.entity;

/**
 * 心跳请求包
 * @author lengcj
 */
public class HeartbeatReq extends HeadInfo {
	private String type;
	
	public HeartbeatReq(){
		
	}

	public HeartbeatReq(short command_id, short cmd_id, short msg_seq, String device_id,
			String type){
		this.command_id = command_id;
		this.cmd_id = cmd_id;
		this.msg_seq = msg_seq;
		this.device_id = device_id;
		this.type = type;
		this.length = 2 + type.length() + 48;
	}
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "HeartbeatReq [type=" + type + "]";
	}
	
}
