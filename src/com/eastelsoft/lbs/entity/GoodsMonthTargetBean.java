package com.eastelsoft.lbs.entity;

import com.eastelsoft.lbs.R;

public class GoodsMonthTargetBean {
//	+ "id NTEXT," 
//			+ "goods_id NTEXT," 
//			+ "name NTEXT," 
//			+ "target NTEXT,"
//			+ "distribution NTEXT,"
//			+ "each_id NTEXT,"
//			+ "packing NTEXT)");
	
	//商品
	private String each_id;
	//商品
	private String name;
	//数量
	private String distribution;
	//目标
	private String target;
	//单位
	private String packing;
	public String getEach_id() {
		return each_id;
	}
	public void setEach_id(String each_id) {
		this.each_id = each_id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDistribution() {
		return distribution;
	}
	public void setDistribution(String distribution) {
		this.distribution = distribution;
	}
	public String getTarget() {
		return target;
	}
	public void setTarget(String target) {
		this.target = target;
	}
	public String getPacking() {
		return packing;
	}
	public void setPacking(String packing) {
		this.packing = packing;
	}
	
	

}
