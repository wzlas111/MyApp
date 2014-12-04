/**
 * Copyright (c) 2012-7-20 www.eastelsoft.com
 * $ID HeadInfo.java 下午10:20:39 $
 */
package com.eastelsoft.lbs.entity;

/**
 * 包头
 * @author lengcj
 */
public class HeadInfo {
	protected int length;
	protected short command_id;
	protected short cmd_id;
	protected short msg_seq;
	protected String device_id;
	public short getCommand_id() {
		return command_id;
	}
	public void setCommand_id(short command_id) {
		this.command_id = command_id;
	}
	public short getCmd_id() {
		return cmd_id;
	}
	public void setCmd_id(short cmd_id) {
		this.cmd_id = cmd_id;
	}
	public short getMsg_seq() {
		return msg_seq;
	}
	public void setMsg_seq(short msg_seq) {
		this.msg_seq = msg_seq;
	}
	public String getDevice_id() {
		return device_id;
	}
	public void setDevice_id(String device_id) {
		this.device_id = device_id;
	}
	public int getLength() {
		return length;
	}
	public void setLength(int length) {
		this.length = length;
	}
	@Override
	public String toString() {
		return "HeadInfo [length=" + length + ", command_id=" + command_id
				+ ", cmd_id=" + cmd_id + ", msg_seq=" + msg_seq
				+ ", device_id=" + device_id + "]";
	}
	
}
