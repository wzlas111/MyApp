/**
 * Copyright (c) 2012-7-20 www.eastelsoft.com
 * $ID RegResp.java 下午9:40:49 $
 */
package com.eastelsoft.lbs.entity;

/**
 * 统一认证中心
 * @author lengcj
 */
public class AuthCentreResp {
	private String ret;
	private String gateip;
	private String gatetcpport;
	private String gateudpport;
	
	private String httpip;
	private String httpport;
	//平台标志
	private String pfsign;
	public String getRet() {
		return ret;
	}

	public void setRet(String ret) {
		this.ret = ret;
	}

	public String getGateip() {
		return gateip;
	}

	public void setGateip(String gateip) {
		this.gateip = gateip;
	}

	public String getGatetcpport() {
		return gatetcpport;
	}

	public void setGatetcpport(String gatetcpport) {
		this.gatetcpport = gatetcpport;
	}

	public String getGateudpport() {
		return gateudpport;
	}

	public void setGateudpport(String gateudpport) {
		this.gateudpport = gateudpport;
	}

	public String getHttpip() {
		return httpip;
	}

	public void setHttpip(String httpip) {
		this.httpip = httpip;
	}

	public String getHttpport() {
		return httpport;
	}

	public void setHttpport(String httpport) {
		this.httpport = httpport;
	}

	@Override
	public String toString() {
		return "AuthCentreResp [ret=" + ret + ", gateip=" + gateip
				+ ", gatetcpport=" + gatetcpport
				+", gateudpport=" + gateudpport
				+", httpport=" + httpport
				+", pfsign=" + pfsign
				+ ", httpip=" + httpip+ "]";
	}

	public String getPfsign() {
		return pfsign;
	}

	public void setPfsign(String pfsign) {
		this.pfsign = pfsign;
	}
	
}
