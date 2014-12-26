package com.eastelsoft.lbs.bean;

import java.util.List;

public class CommodityReasonDto {

	public String resultcode;
	public String updatecode;
	public List<CommodityReasonBean> clientdata;
	
	public class CommodityReasonBean {
		public String commodity_maintenance_id;
		public String commodity_id;
		public String commodity_name;
		public String commodity_maintenance_content;
		public String commodity_solve_content;
	}
}
