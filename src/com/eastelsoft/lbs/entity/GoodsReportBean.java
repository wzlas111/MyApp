package com.eastelsoft.lbs.entity;

import com.eastelsoft.lbs.R;
/**
 * 商品清单
 * @author admin
 *
 */
public class GoodsReportBean {
	
	private String name;
	private String amount;
	private String packing;
	private String id;
	
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPacking() {
		return packing;
	}
	public void setPacking(String packing) {
		this.packing = packing;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	@Override
	public String toString() {
		return "name=" + name + ", amount=" + amount+ 
				
				 ", packing=" + packing + "]";
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	

}
