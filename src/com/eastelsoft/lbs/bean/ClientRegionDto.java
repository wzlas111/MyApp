package com.eastelsoft.lbs.bean;

import java.util.List;

public class ClientRegionDto {

	public List<RegionBean> clientdata;
	
	public class RegionBean {
		public String id;
		public String name;
		public String pid;
		public String level;
	}
}
