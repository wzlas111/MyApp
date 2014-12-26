package com.eastelsoft.lbs.bean;

import java.util.List;

public class CommodityDto {

	public String resultcode;
	public String updatecode;
	public List<CommodityBean> clientdata;
	
	public class CommodityBean {
		public String id;
		public String name;
		public String packing;
	}
}
