/**
 * Copyright (c) 2012-7-20 www.eastelsoft.com
 * $ID ReportResp.java 下午9:51:41 $
 */
package com.eastelsoft.lbs.entity;

/**
 * 数据上报应答
 * @author lengcj
 */
public class ReportResp {
	private String ret;
	private int interval = 60;
	private String week;
	private String timePeriod;
	private String filterDate;
	private String minDistance;
	private String rept;
	private int seq = 0;
	public int getSeq() {
		return seq;
	}
	public void setSeq(int seq) {
		this.seq = seq;
	}
	public String getRet() {
		return ret;
	}
	public void setRet(String ret) {
		this.ret = ret;
	}
	public int getInterval() {
		return interval;
	}
	public void setInterval(int interval) {
		this.interval = interval;
	}
	public String getWeek() {
		return week;
	}
	public void setWeek(String week) {
		this.week = week;
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
	public String getMinDistance() {
		return minDistance;
	}
	public void setMinDistance(String minDistance) {
		this.minDistance = minDistance;
	}
	public String getRept() {
		return rept;
	}
	public void setRept(String rept) {
		this.rept = rept;
	}
	@Override
	public String toString() {
		return "ReportResp [ret=" + ret + ", interval=" + interval + ", week="
				+ week + ", timePeriod=" + timePeriod + ", filterDate="
				+ filterDate + ", minDistance=" + minDistance + ", rept="
				+ rept + ", seq=" + seq + "]";
	}
	
}
