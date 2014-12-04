/**
 * Copyright (c) 2012-8-17 www.eastelsoft.com
 * $ID CustBean.java 下午11:58:07 $
 */
package com.eastelsoft.lbs.entity;

public class KnowledgeBean {
	// {"k_type":"0","k_t_id":"kt0001","k_t_name":"天文知识目录","list":
	// [
	// 　{"k_type":"2","k_id":"kt0001_k001","k_name":"海王星知识","k_appendix":"12.ppt","k_appendix_title":"海王星PPT附件","k_appendix_size":"1024","is_read":"0","k_code":"1"},
	// {"k_type":"3","k_id":"kt0001_k002","k_name":"冥王星知识","k_appendix":"12.doc","k_appendix_title":"冥王星DOC附件","k_appendix_size":"1024","is_read":"1","k_code":"1"},
	// {"k_type":"1","k_id":"kt0001_k003","k_name":"科学管理","is_read":"1","k_code":"1"},
	// {"k_type":"0","k_t_id":"kt0002","k_t_name":"地理知识目录","list":
	// [
	// {"k_type":"5","k_id":"kt0001_k002","k_name":"南极地理知识","k_appendix":"12.pdf","k_appendix_title":"南极地理PDF附件","k_appendix_size":"1024","is_read":"1","k_code":"1"}
	// ]}
	// ]}
	private String k_type;

	private String k_id;
	private String k_name;
	private String k_fatherid;
	private String k_appendix;
	private String k_appendix_title;
	private String k_appendix_size;
	private String is_read;
	private String k_code;

	public String getK_type() {
		return k_type;
	}

	public void setK_type(String k_type) {
		this.k_type = k_type;
	}

	public String getK_id() {
		return k_id;
	}

	public void setK_id(String k_id) {
		this.k_id = k_id;
	}

	public String getK_name() {
		return k_name;
	}

	public void setK_name(String k_name) {
		this.k_name = k_name;
	}

	public String getK_fatherid() {
		return k_fatherid;
	}

	public void setK_fatherid(String k_fatherid) {
		this.k_fatherid = k_fatherid;
	}

	public String getK_appendix() {
		return k_appendix;
	}

	public void setK_appendix(String k_appendix) {
		this.k_appendix = k_appendix;
	}

	public String getK_appendix_title() {
		return k_appendix_title;
	}

	public void setK_appendix_title(String k_appendix_title) {
		this.k_appendix_title = k_appendix_title;
	}

	public String getK_appendix_size() {
		return k_appendix_size;
	}

	public void setK_appendix_size(String k_appendix_size) {
		this.k_appendix_size = k_appendix_size;
	}

	public String getIs_read() {
		return is_read;
	}

	public void setIs_read(String is_read) {
		this.is_read = is_read;
	}

	public String getK_code() {
		return k_code;
	}

	public void setK_code(String k_code) {
		this.k_code = k_code;
	}

}
