/**
 * Copyright (c) 2012-8-17 www.eastelsoft.com
 * $ID CustBean.java 下午11:58:07 $
 */
package com.eastelsoft.lbs.entity;


public class BulletinBean {
//	db.execSQL("create table if not exists l_bulletin("
//			+ "b_id NTEXT PRIMARY KEY," + "b_name NTEXT,"
//			+ "b_release_date NTEXT," + "b_code NTEXT," + "is_top NTEXT,"
//			+ "b_fail_date NTEXT," + "b_remark NTEXT," + "b_type NTEXT,"
//			+ "u_name NTEXT," + "b_appendix NTEXT,"
//			+ "b_appendix_title NTEXT," + "b_appendix_size NTEXT,"
//			+ "is_read NTEXT)");
	private String b_id;
	private String b_name;
	private String b_release_date;
	private String b_code;
	private String is_top;
	private String b_fail_date;
	private String b_remark;
	private String b_type;
	private String u_name;
	private String b_appendix;
	private String b_appendix_title;
	private String b_appendix_size;
	private String is_read;
	public String getB_id() {
		return b_id;
	}
	public void setB_id(String b_id) {
		this.b_id = b_id;
	}
	public String getB_name() {
		return b_name;
	}
	public void setB_name(String b_name) {
		this.b_name = b_name;
	}
	public String getB_release_date() {
		return b_release_date;
	}
	public void setB_release_date(String b_release_date) {
		this.b_release_date = b_release_date;
	}
	public String getB_code() {
		return b_code;
	}
	public void setB_code(String b_code) {
		this.b_code = b_code;
	}
	public String getIs_top() {
		return is_top;
	}
	public void setIs_top(String is_top) {
		this.is_top = is_top;
	}
	public String getB_fail_date() {
		return b_fail_date;
	}
	public void setB_fail_date(String b_fail_date) {
		this.b_fail_date = b_fail_date;
	}
	public String getB_remark() {
		return b_remark;
	}
	public void setB_remark(String b_remark) {
		this.b_remark = b_remark;
	}
	public String getB_type() {
		return b_type;
	}
	public void setB_type(String b_type) {
		this.b_type = b_type;
	}
	public String getU_name() {
		return u_name;
	}
	public void setU_name(String u_name) {
		this.u_name = u_name;
	}
	public String getB_appendix() {
		return b_appendix;
	}
	public void setB_appendix(String b_appendix) {
		this.b_appendix = b_appendix;
	}
	public String getB_appendix_title() {
		return b_appendix_title;
	}
	public void setB_appendix_title(String b_appendix_title) {
		this.b_appendix_title = b_appendix_title;
	}
	public String getB_appendix_size() {
		return b_appendix_size;
	}
	public void setB_appendix_size(String b_appendix_size) {
		this.b_appendix_size = b_appendix_size;
	}
	public String getIs_read() {
		return is_read;
	}
	public void setIs_read(String is_read) {
		this.is_read = is_read;
	}
	@Override
	public String toString() {
		return "BulletinBean [b_id=" + b_id + ", b_name=" + b_name
				+ ", b_release_date=" + b_release_date + ", b_code=" + b_code
				+ ", is_top=" + is_top + ", b_fail_date=" + b_fail_date
				+ ", b_remark=" + b_remark + ", b_type=" + b_type + ", u_name="
				+ u_name + ", b_appendix=" + b_appendix + ", b_appendix_title="
				+ b_appendix_title + ", b_appendix_size=" + b_appendix_size
				+ ", is_read=" + is_read + "]";
	}
	
	
}
