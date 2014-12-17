package com.eastelsoft.lbs.bean;

public class VisitMcBean {

	public String id;
	public String visit_id;
	public String client_id;
	public String client_name;
	public String start_time;
	public String end_time;
	public String service_start_time;
	public String service_end_time;
	public String is_repair;
	public String client_sign;
	public String upload_img;
	public String is_upload;
	
	public String mc_register_json = "";
	public String mc_type_json = "";
	public String mc_person_json = "";
	public String mc_info_json = "";
	
	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("id : "+id+", ");
		sb.append("visit_id : "+visit_id+", ");
		sb.append("client_id : "+client_id+", ");
		sb.append("client_name : "+client_name+", ");
		sb.append("start_time : "+start_time+", ");
		sb.append("end_time : "+end_time+", ");
		sb.append("service_start_time : "+service_start_time+", ");
		sb.append("service_end_time : "+service_end_time+", ");
		sb.append("is_repair : "+is_repair+", ");
		sb.append("client_sign : "+client_sign+", ");
		sb.append("upload_img : "+upload_img+", ");
		sb.append("is_upload : "+is_upload+", ");
		sb.append("mc_register_json : "+mc_register_json+", ");
		sb.append("mc_type_json : "+mc_type_json+", ");
		sb.append("mc_person_json : "+mc_person_json+", ");
		sb.append("mc_info_json : "+mc_info_json+", ");
		return sb.toString();
	}
	
//	public List<VisitMcRegisterBean> mc_register_list = new ArrayList<VisitMcRegisterBean>();
//	public List<VisitMcTypeBean> mc_type_list = new ArrayList<VisitMcTypeBean>();
//	public List<VisitMcPersonBean> mc_person_list = new ArrayList<VisitMcPersonBean>();
//	public List<VisitMcInfoBean> mc_info_list = new ArrayList<VisitMcInfoBean>();
}
