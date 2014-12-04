package com.eastelsoft.lbs.bean;

import java.util.List;

public class ClientDto {

	public String resultcode;
	public String updatecode;
	public List<ClientBean> data;
	
	public class ClientBean {
		public String id;
		public String client_name;
		public String client_code;
		public String py;
		public String dealer_id;
		public String dealer_name;
		public String type;
		public String typename;
		public String region_id;
		public String contact_phone;
		public String fax;
		public String lon;
		public String lat;
		public String email;
		public String remark;
		public String address;
	}
}
