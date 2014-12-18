package com.eastelsoft.lbs.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.http.Header;

import com.eastelsoft.lbs.bean.ResultBean;
import com.eastelsoft.lbs.bean.UploadImgBean;
import com.eastelsoft.lbs.bean.VisitBean;
import com.eastelsoft.lbs.db.UploadDBTask;
import com.eastelsoft.lbs.db.VisitDBTask;
import com.eastelsoft.util.Util;
import com.eastelsoft.util.http.HttpRestClient;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;

public class VisitFinishService extends Service {
	
	private String mId;
	private String service_begin_time;
	private String service_end_time;
	private String upload_date;
	private String[] photos_path;
	List<UploadImgBean> u_list;

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		mId = intent.getStringExtra("id");
		service_begin_time = intent.getStringExtra("service_begin_time");
		service_end_time = intent.getStringExtra("service_end_time");
		photos_path = intent.getStringArrayExtra("photos_path");
		upload_date = Util.getLocaleTime("yyyy-MM-dd HH:mm:ss");
		u_list = new ArrayList<UploadImgBean>();
				
		new InsertDBTask().execute("");
		return START_REDELIVER_INTENT;
	}
	
	private class InsertDBTask extends AsyncTask<String, Integer, Boolean> {
		@Override
		protected Boolean doInBackground(String... params) {
			try {
				insertDB();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			//upload
			uploadForm();
		}
	}
	
	private void insertDB() {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < photos_path.length; i++) {
			UploadImgBean u_bean = new UploadImgBean();
			u_bean.id = UUID.randomUUID().toString();
			u_bean.data_id = mId;
			u_bean.name = photos_path[i];
			u_bean.type = "1";
			u_bean.path = photos_path[i];
			u_list.add(u_bean);
			sb.append(photos_path[i]+"|");
		}
		UploadDBTask.addImgBeanList(u_list);
		
		VisitBean bean = new VisitBean();
		bean.id = mId;
		bean.service_begin_time = service_begin_time;
		bean.service_end_time = service_end_time;
		bean.is_upload = "0";
		bean.status = "2";
		bean.upload_date = upload_date;
		bean.visit_img = sb.toString();
		VisitDBTask.updateFinishBean(bean);
	}
	
	/**
	 * 上传基本数据
	 */
	private void uploadForm() {
		String mUrl = "";
		RequestParams params = new RequestParams();
		params.put("visit_id", mId);
		params.put("service_begin_time", service_begin_time);
		params.put("service_end_time", service_end_time);
		params.put("status", "2");
		params.put("upload_date", upload_date);
		HttpRestClient.getSingle(mUrl, params, new FormResponseHandler());
	}
	
	private void uploadImg() {
		String mUrl = "";
		final String contentType = RequestParams.APPLICATION_OCTET_STREAM;
		for (int i = 0; i < photos_path.length; i++) {
			try {
				UploadImgBean u_bean = new UploadImgBean();
				File file = new File(u_bean.path);
				RequestParams params = new RequestParams();
				params.put("file"+i, file, contentType);
				HttpRestClient.getSingle(mUrl, params, new ImgResponseHandler(u_bean.id));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private class FormResponseHandler extends TextHttpResponseHandler {
		@Override
		public void onSuccess(int statusCode, Header[] headers, String responseString) {
				uploadImg();
		}
		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
			System.out.println("上传失败!!!");
		}
	}
	
	private class ImgResponseHandler extends TextHttpResponseHandler {
		private String img_id;
		public ImgResponseHandler(String pId){
			img_id = pId;
		}
		@Override
		public void onSuccess(int statusCode, Header[] headers, String responseString) {
			try {
				Gson gson = new Gson();
				ResultBean bean = gson.fromJson(responseString, ResultBean.class);
				if ("1".equals(bean.result_code)) { //success
					UploadDBTask.deleteImgBean(img_id);
					String finish = bean.finish_num;
					if ("0".equals(finish)) {
						VisitBean v_bean = new VisitBean();
						v_bean.id = mId;
						v_bean.is_upload = "1";
						VisitDBTask.updateIsUploadBean(v_bean);
					}
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
			System.out.println("图片上传失败!!!");
		}
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
