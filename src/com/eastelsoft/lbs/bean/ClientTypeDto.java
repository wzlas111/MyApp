package com.eastelsoft.lbs.bean;

import java.util.List;

public class ClientTypeDto {

	public String resultcode;
	public String updatecode;
	public List<TypeBean> clientdata;
	
	public class TypeBean {
		public String id;
		public String name;
	}
}
