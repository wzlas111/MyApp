package com.eastelsoft.lbs.bean;

import java.util.List;

public class EvaluateDto {

	public String resultcode;
	public String updatecode;
	public List<EvaluateBean> clientdata;
	
	public class EvaluateBean {
		public String evaluate_id;
		public String evaluate_name;
		public String sequence;
	}
}
