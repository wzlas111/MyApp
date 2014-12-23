package com.eastelsoft.lbs.bean;

import java.util.List;

public class OrderTypeDto {

	public String resultcode;
	public String updatecode;
	public List<OrderTypeBean> clientdata;
	
	public class OrderTypeBean {
		public String order_form_type_id;
		public String order_form_type_name;
	}
}
