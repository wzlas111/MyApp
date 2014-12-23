package com.eastelsoft.lbs.bean;

import java.util.List;

public class ClientDto {

	public String resultcode;
	public String updatecode;
	public List<ClientBean> clientdata;
	
	public class ClientBean {
		public String id;
		public String client_name;
		public String client_code;
		public String dealer_id;
		public String dealer_name;
		public String region_id;
		public String region_name;
		public String type_id;
		public String type_name;
		public String fax;
		public String address;
		public String lon;
		public String lat;
		public String accuary;
		public String remark;
		public String py_index;
		public String first_py;
		public String type;
		public String is_upload = "1";
	}
}
