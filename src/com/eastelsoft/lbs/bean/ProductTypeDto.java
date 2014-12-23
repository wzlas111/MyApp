package com.eastelsoft.lbs.bean;

import java.util.List;

public class ProductTypeDto {

	public String resultcode;
	public String updatecode;
	public List<ProductTypeBean> clientdata;
	
	public class ProductTypeBean {
		public String product_type_id;
		public String product_type_name;
	}
}
