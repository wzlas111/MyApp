package com.eastelsoft.lbs.service;

import java.io.File;
import java.util.Random;

import org.apache.http.Header;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.activity.visit.VisitActivity;
import com.eastelsoft.lbs.bean.ResultBean;
import com.eastelsoft.lbs.bean.VisitEvaluateBean;
import com.eastelsoft.lbs.db.VisitEvaluateDBTask;
import com.eastelsoft.util.FileLog;
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
import android.os.IBinder;

public class VisitEvaluateService extends Service {
	
	public static final String TAG = "VisitEvaluateService";
	
	private String mId;
	private VisitEvaluateBean mBean;
	
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
		mId = intent.getStringExtra("id");
		mBean = intent.getParcelableExtra("bean");
		
		insertDB();
		return START_NOT_STICKY;
	}
	
	private void insertDB() {
		if (mBean != null) {
			try {
				VisitEvaluateDBTask.addBean(mBean);
			} catch (Exception e) {
				e.printStackTrace();
			}
			uploadData();
		}
	}
	
	private void uploadData() {
		try {
			Gson gson = new Gson();
			String contentType = RequestParams.APPLICATION_OCTET_STREAM;
			String json = gson.toJson(mBean);
			String mUrl = URLHelper.BASE_ACTION;
			RequestParams params = new RequestParams();
			params.put("reqCode", URLHelper.UPDATE_EVALUATE);
			params.put("data_id", mId);
			params.put("json", json);
			params.put("file1", new File(mBean.client_sign), contentType);
			HttpRestClient.postSingle(mUrl, params, new UploadResponseHandler());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private class UploadResponseHandler extends TextHttpResponseHandler {
		@Override
		public void onSuccess(int statusCode, Header[] headers,String responseString) {
			try {
				Gson gson = new Gson();
				ResultBean bean = gson.fromJson(responseString, ResultBean.class);
				if ("1".equals(bean.result_code)) {
					FileLog.i(TAG, TAG+"基础数据上传成功...");
					mBean.is_upload = "1";
					VisitEvaluateDBTask.updateIsUploadBean(mBean);
					showNotificationInformation(R.drawable.notify,
							getResources().getString(R.string.app_name),
							getResources().getString(R.string.app_name),
							"服务评价上传成功.");
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			stopService();
		}
		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
			FileLog.i(TAG, TAG+"基础数据上传失败...");
			stopService();
		}
		@Override
		public void onRetry(int retryNo) {
			super.onRetry(retryNo);
			FileLog.i(TAG, TAG+"基础数据上传中,重试次数:"+retryNo);
		}
	}
	
	private void stopService() {
		stopForeground(true);
        stopSelf();
	}
	
	public void showNotificationInformation(int icon, String tickertext,
			String title, String content) {
		Notification notification = new Notification(icon, tickertext, System.currentTimeMillis());
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
