package com.eastelsoft.lbs.bean;

import java.util.List;

public class EnterpriseTypeDto {

	public String resultcode;
	public String updatecode;
	public List<EnterpriseTypeBean> clientdata;
	
	public class EnterpriseTypeBean {
		public String enterpriseunits_type_id;
		public String enterpriseunits_type_name;
	}
}
