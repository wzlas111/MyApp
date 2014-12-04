/**
 * Copyright (c) 2012-8-2 www.eastelsoft.com
 * $ID ContrReq.java 下午1:33:33 $
 */
package com.eastelsoft.lbs.entity;

/**
 * 远程控制请求
 * @author lengcj
 */
public class ContrReq {
	private String cmd;
	private String num;
	public ContrReq(){}
	public String getCmd() {
		return cmd;
	}
	public void setCmd(String cmd) {
		this.cmd = cmd;
	}
	public String getNum() {
		return num;
	}
	public void setNum(String num) {
		this.num = num;
	}
	@Override
	public String toString() {
		return "ContrReq [cmd=" + cmd + ", num=" + num + "]";
	}
}
