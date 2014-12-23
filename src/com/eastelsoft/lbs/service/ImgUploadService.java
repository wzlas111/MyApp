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

public class ImgUploadService extends Service {
	
	public static final String TAG = "ImgUploadService";
	
	private String mId;
	private String[] photos_path;
	private String upload_date;
	private List<UploadImgBean> u_list;
	
	private Gson gson = new Gson();
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
		photos_path = intent.getStringArrayExtra("photos_path");
		upload_date = Util.getLocaleTime("yyyy-MM-dd HH:mm:ss");
		u_list = new ArrayList<UploadImgBean>();
		
		new InsertDBTask().execute("");
		return START_NOT_STICKY;
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
			//upload img
			uploadImg();
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
				if ("1".equals(bean.resultcode)) { //success
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
								"补录拜访记录上传成功.");
						
						stopService();
					}
				} else if("98".equals(bean.resultcode)){//repeat
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
