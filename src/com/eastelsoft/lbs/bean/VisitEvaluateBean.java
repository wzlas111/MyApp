package com.eastelsoft.lbs.bean;

import android.os.Parcel;
import android.os.Parcelable;

public class VisitEvaluateBean implements Parcelable {

	public String id;
	public String visit_id;
	public String visit_num;
	public String service_name;
	public String service_value;
	public String other_job;
	public String advise;
	public String client_sign;
	public String is_upload;

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(visit_id);
		dest.writeString(visit_num);
		dest.writeString(service_name);
		dest.writeString(service_value);
		dest.writeString(other_job);
		dest.writeString(advise);
		dest.writeString(client_sign);
		dest.writeString(is_upload);
	}

	public static final Parcelable.Creator<VisitEvaluateBean> CREATOR = new Parcelable.Creator<VisitEvaluateBean>() {
		public VisitEvaluateBean createFromParcel(Parcel in) {
			VisitEvaluateBean bean = new VisitEvaluateBean();
			bean.id = in.readString();
			bean.visit_id = in.readString();
			bean.visit_num = in.readString();
			bean.service_name = in.readString();
			bean.service_value = in.readString();
			bean.other_job = in.readString();
			bean.advise = in.readString();
			bean.client_sign = in.readString();
			bean.is_upload = in.readString();
			return bean;
		}

		public VisitEvaluateBean[] newArray(int size) {
			return new VisitEvaluateBean[size];
		}
	};
}
