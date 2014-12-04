package com.eastelsoft.lbs.entity;

import com.eastelsoft.lbs.R;

public class TimingLocationBean {
	private String tl_id;
	private String tl_uploadDate;
	private String tl_lon;
	private String tl_lat;
	private String tl_accuracy;
	private String tl_seq;
	private String tl_power;
	private String tl_states;
	private String tl_signalStrengthValue;
	private String tl_cell;
	private String tl_wifi;
	public String getTl_uploadDate() {
		return tl_uploadDate;
	}
	public void setTl_uploadDate(String tl_uploadDate) {
		this.tl_uploadDate = tl_uploadDate;
	}
	public String getTl_lon() {
		return tl_lon;
	}
	public void setTl_lon(String tl_lon) {
		this.tl_lon = tl_lon;
	}
	public String getTl_lat() {
		return tl_lat;
	}
	public void setTl_lat(String tl_lat) {
		this.tl_lat = tl_lat;
	}
	public String getTl_accuracy() {
		return tl_accuracy;
	}
	public void setTl_accuracy(String tl_accuracy) {
		this.tl_accuracy = tl_accuracy;
	}
	public String getTl_id() {
		return tl_id;
	}
	public void setTl_id(String tl_id) {
		this.tl_id = tl_id;
	}
	public String getTl_seq() {
		return tl_seq;
	}
	public void setTl_seq(String tl_seq) {
		this.tl_seq = tl_seq;
	}
	public String getTl_power() {
		return tl_power;
	}
	public void setTl_power(String tl_power) {
		this.tl_power = tl_power;
	}
	public String getTl_states() {
		return tl_states;
	}
	public void setTl_states(String tl_states) {
		this.tl_states = tl_states;
	}
	public String getTl_signalStrengthValue() {
		return tl_signalStrengthValue;
	}
	public void setTl_signalStrengthValue(String tl_signalStrengthValue) {
		this.tl_signalStrengthValue = tl_signalStrengthValue;
	}
	public String getTl_cell() {
		return tl_cell;
	}
	public void setTl_cell(String tl_cell) {
		this.tl_cell = tl_cell;
	}
	public String getTl_wifi() {
		return tl_wifi;
	}
	public void setTl_wifi(String tl_wifi) {
		this.tl_wifi = tl_wifi;
	}
	@Override
	public String toString() {
		return "TimingLocationBean [tl_id=" + tl_id + ", tl_uploadDate="
				+ tl_uploadDate + ", tl_lon=" + tl_lon + ", tl_lat=" + tl_lat
				+ ", tl_accuracy=" + tl_accuracy + ", tl_seq=" + tl_seq
				+ ", tl_power=" + tl_power + ", tl_states=" + tl_states
				+ ", tl_signalStrengthValue=" + tl_signalStrengthValue
				+ ", tl_cell=" + tl_cell + ", tl_wifi=" + tl_wifi + "]";
	}
	
	
	

}
