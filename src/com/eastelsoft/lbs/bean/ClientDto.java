package com.eastelsoft.lbs.bean;

import java.util.List;

public class ClientDto {

	public String resultcode;
	public String updatecode;
	public List<ClientBean> data;
	
	public class ClientBean {
		public String id;
		public String name;
		public String py;
		public String is_upload = "1";
	}
}
