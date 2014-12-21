package com.eastelsoft.lbs.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class VisitMcBean implements Parcelable{

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
	public String upload_img_num;
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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(visit_id);
		dest.writeString(client_id);
		dest.writeString(client_name);
		dest.writeString(start_time);
		dest.writeString(end_time);
		dest.writeString(service_start_time);
		dest.writeString(service_end_time);
		dest.writeString(is_repair);
		dest.writeString(client_sign);
		dest.writeString(upload_img);
		dest.writeString(upload_img_num);
		dest.writeString(is_upload);
		dest.writeString(mc_register_json);
		dest.writeString(mc_type_json);
		dest.writeString(mc_person_json);
		dest.writeString(mc_info_json);
	}
	public static final Parcelable.Creator<VisitMcBean> CREATOR = new Parcelable.Creator<VisitMcBean>() {
		public VisitMcBean createFromParcel(Parcel in) {
			VisitMcBean bean = new VisitMcBean();
			bean.id = in.readString();
			bean.visit_id = in.readString();
			bean.client_id = in.readString();
			bean.client_name = in.readString();
			bean.start_time = in.readString();
			bean.end_time = in.readString();
			bean.service_start_time = in.readString();
			bean.service_end_time = in.readString();
			bean.is_repair = in.readString();
			bean.client_sign = in.readString();
			bean.upload_img = in.readString();
			bean.upload_img_num = in.readString();
			bean.is_upload = in.readString();
			bean.mc_register_json = in.readString();
			bean.mc_type_json = in.readString();
			bean.mc_person_json = in.readString();
			bean.mc_info_json = in.readString();
			return bean;
		}

		public VisitMcBean[] newArray(int size) {
			return new VisitMcBean[size];
		}
	};
}
