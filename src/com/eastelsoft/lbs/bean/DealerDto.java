package com.eastelsoft.lbs.bean;

import java.util.List;

public class DealerDto {

	public String resultcode;
	public String updatecode;
	public List<DealerBean> data;
	
	public class DealerBean {
		public String id;
		public String name;
		public String telephone;
		public String group_id;
		public String group_name;
		public String remark;
		public String py_index;
		public String py_name;
	}
}
