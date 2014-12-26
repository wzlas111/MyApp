package com.eastelsoft.lbs.bean;

import java.util.List;

public class DealerDto {

	public String resultcode;
	public String updatecode;
	public List<DealerBean> clientdata;
	
	public class DealerBean {
		public String id;
		public String dealer_name;
		public String parent_dealer_name;
		public String dealer_code;
		public String region_id;
		public String region_name;
		public String type_id;
		public String type_name;
		public String contact_person;
		public String contact_phone;
		public String fax;
		public String address;
		public String lon;
		public String lat;
		public String accuracy;
		public String remark;
		public String py_index;
		public String first_py;
		public String type;
		public String updatecode;
	}
}
