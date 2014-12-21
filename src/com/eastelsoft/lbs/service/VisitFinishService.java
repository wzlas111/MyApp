package com.eastelsoft.lbs.service;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import org.apache.http.Header;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.activity.visit.VisitActivity;
import com.eastelsoft.lbs.bean.ResultBean;
import com.eastelsoft.lbs.bean.UploadImgBean;
import com.eastelsoft.lbs.bean.VisitBean;
import com.eastelsoft.lbs.db.UploadDBTask;
import com.eastelsoft.lbs.db.VisitDBTask;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.GlobalVar;
import com.eastelsoft.util.Util;
import com.eastelsoft.util.http.HttpRestClient;
import com.eastelsoft.util.http.URLHelper;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.widget.Toast;

public class VisitFinishService extends Service {
	
	public static final String TAG = "VisitFinishService";
	
	private String mId;
	private VisitBean bean;
	private String service_begin_time;
	private String service_end_time;
	private String upload_date;
	private String[] photos_path;
	List<UploadImgBean> u_list;
	Gson gson = new Gson();
	
	private NotificationManager nm;
	private int notification_id;
	
	@Override
	public void onCreate() {
		super.onCreate();
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notification_id = new Random().nextInt(Integer.MAX_VALUE);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		FileLog.i(TAG, TAG+"----> onStartCommand");
		mId = intent.getStringExtra("id");
		service_begin_time = intent.getStringExtra("service_begin_time");
		service_end_time = intent.getStringExtra("service_end_time");
		photos_path = intent.getStringArrayExtra("photos_path");
		upload_date = Util.getLocaleTime("yyyy-MM-dd HH:mm:ss");
		u_list = new ArrayList<UploadImgBean>();
		bean = new VisitBean();	
		
		new InsertDBTask().execute("");
		return START_NOT_STICKY;
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		FileLog.i(TAG, TAG+"----> onDestroy");
	}
	
	private class InsertDBTask extends AsyncTask<String, Integer, Boolean> {
		@Override
		protected Boolean doInBackground(String... params) {
			try {
				FileLog.i(TAG, TAG+"----> InsertDBTask");
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
		FileLog.i(TAG, TAG+"----> insertDB");
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < photos_path.length; i++) {
			FileLog.i(TAG, TAG+"---->i:"+i+", photos_path:"+photos_path[i]);
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
		
		bean.id = mId;
		bean.service_begin_time = service_begin_time;
		bean.service_end_time = service_end_time;
		bean.is_upload = "0";
		bean.status = "3";
		bean.upload_date = upload_date;
		bean.visit_img = sb.toString();
		bean.visit_img_num = String.valueOf(photos_path.length);
		VisitDBTask.updateFinishBean(bean);
	}
	
	/**
	 * 上传基本数据
	 */
	private void uploadForm() {
		FileLog.i(TAG, TAG+"----> uploadForm");
		String json = gson.toJson(bean);
		String mUrl = URLHelper.BASE_ACTION;
		RequestParams params = new RequestParams();
		params.put("reqCode", URLHelper.UPDATE_FINISH);
		params.put("data_id", mId);
		params.put("json", json);
		HttpRestClient.postSingle(mUrl, params, new FormResponseHandler());
	}
	
	private void uploadImg() {
		FileLog.i(TAG, TAG+"----> uploadImg");
		String mUrl = URLHelper.BASE_ACTION;
		String contentType = RequestParams.APPLICATION_OCTET_STREAM;
		for (int i = 0; i < u_list.size(); i++) {
			try {
				UploadImgBean u_bean = u_list.get(i);
				String json = gson.toJson(u_bean);
				File file = new File(u_bean.path);
				RequestParams params = new RequestParams();
				params.put("reqCode", URLHelper.IMG_UPLOAD);
				params.put("data_id", mId);
				params.put("type", "1");
				params.put("json", json);
				params.put("file"+i, file, contentType);
				params.setHttpEntityIsRepeatable(false);
				params.setUseJsonStreamer(false);
				HttpRestClient.postSingle(mUrl, params, new ImgResponseHandler(u_bean.id));
				FileLog.i(TAG, TAG+"----> uploadImg postSingle");
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		FileLog.i(TAG, TAG+"---->u_list size:"+u_list.size());
	}
	
	private class FormResponseHandler extends TextHttpResponseHandler {
		@Override
		public void onSuccess(int statusCode, Header[] headers, String responseString) {
			FileLog.i(TAG, TAG+"基础数据上传成功，等待上传图片...");
			if (photos_path.length == 0) {
				bean.is_upload = "1";
				bean.status = "2";
				VisitDBTask.updateIsUploadBean(bean);
				
				showNotificationInformation(R.drawable.notify,
						getResources().getString(R.string.app_name),
						getResources().getString(R.string.app_name),
						"拜访记录上传成功.");
				
				stopService();
			}
			uploadImg();
		}
		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
			Toast.makeText(GlobalVar.getInstance(), getResources().getString(R.string.upload_fail), Toast.LENGTH_SHORT).show();
			FileLog.i(TAG, TAG+"基础数据上传失败...");
			showNotificationInformation(R.drawable.notify,
					getResources().getString(R.string.app_name),
					getResources().getString(R.string.app_name),
					"拜访记录上传失败,待网络通畅后自动上传.");
			
			stopService();
		}
		@Override
		public void onRetry(int retryNo) {
			super.onRetry(retryNo);
			FileLog.i(TAG, TAG+"基础数据上传中,重试次数:"+retryNo);
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
					FileLog.i(TAG, TAG+"图片上传成功...,img_id:"+img_id);
					UploadDBTask.deleteImgBean(img_id);
					String img_num = bean.img_num;
					int num = Integer.parseInt(img_num);
					if (num <= 0) {
						VisitBean v_bean = new VisitBean();
						v_bean.id = mId;
						v_bean.is_upload = "1";
						v_bean.status = "2";
						VisitDBTask.updateIsUploadBean(v_bean);
						FileLog.i(TAG, TAG+"图片已全部上传...");
						
						showNotificationInformation(R.drawable.notify,
								getResources().getString(R.string.app_name),
								getResources().getString(R.string.app_name),
								"拜访记录上传成功.");
						
						stopService();
					}
				} else if("98".equals(bean.result_code)){//repeat
					UploadDBTask.deleteImgBean(img_id);
					FileLog.i(TAG, TAG+"图片重复上传，本地删除...,img_id:"+img_id);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
			FileLog.i(TAG, TAG+"图片上传失败!!!,img_id:"+img_id);
			showNotificationInformation(R.drawable.notify,
					getResources().getString(R.string.app_name),
					getResources().getString(R.string.app_name),
					"拜访记录上传失败,待网络通畅后自动上传.");
		}
		
		@Override
		public void onRetry(int retryNo) {
			super.onRetry(retryNo);
			FileLog.i(TAG, TAG+"图片上传中,重试次数:"+retryNo+",img_id:"+img_id);
		}
	}
	
	private void stopService() {
		stopForeground(true);
        stopSelf();
	}
	
	public void showNotificationInformation(int icon, String tickertext,
			String title, String content) {
		Notification notification = new Notification(icon, tickertext, System.currentTimeMillis());
		/*
		 * notification.defaults = Notification.DEFAULT_ALL |
		 * Notification.DEFAULT_VIBRATE;
		 */
		notification.flags = Notification.FLAG_AUTO_CANCEL;

		notification.defaults = Notification.DEFAULT_ALL;
		Intent intent = new Intent(this, VisitActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pt = PendingIntent.getActivity(this, 0, intent, 0);
		notification.setLatestEventInfo(this, title, content, pt);
		nm.notify(notification_id, notification);
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
}
