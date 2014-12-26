package com.eastelsoft.lbs.service;

import java.io.File;
import java.util.List;

import org.apache.http.Header;

import com.eastelsoft.lbs.bean.ResultBean;
import com.eastelsoft.lbs.bean.UploadImgBean;
import com.eastelsoft.lbs.bean.VisitBean;
import com.eastelsoft.lbs.bean.VisitEvaluateBean;
import com.eastelsoft.lbs.bean.VisitMcBean;
import com.eastelsoft.lbs.db.AutoUploadDBTask;
import com.eastelsoft.lbs.db.UploadDBTask;
import com.eastelsoft.lbs.db.VisitDBTask;
import com.eastelsoft.lbs.entity.SetInfo;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.IUtil;
import com.eastelsoft.util.http.HttpRestClient;
import com.eastelsoft.util.http.URLHelper;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

public class AutoUploadService extends Service {

	public static String TAG = "AutoUploadService";

	private int count = 0;
	private boolean is_uploading = false;
	private String gps_id = "";
	private Gson gson = new Gson();
	private String contentType = RequestParams.APPLICATION_OCTET_STREAM;
	private ConnectivityManager connectivityManager;
	private NetworkInfo info;

	@Override
	public void onCreate() {
		super.onCreate();
		FileLog.i(TAG, TAG + "--->onCreate");
		
		count = 0;
		is_uploading = false;
		HttpRestClient.clearBgRequest();
		
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(netReceiver, mFilter);
		
//		Looper.prepare();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		FileLog.i(TAG, TAG + "--->onStartCommand");
		return START_NOT_STICKY;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		is_uploading = false;
		unregisterReceiver(netReceiver);
		HttpRestClient.clearBgRequest();
	}

	/**
	 * 网络监听
	 */
	private BroadcastReceiver netReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				Log.d(TAG + "network", "网络状态已经改变");
				connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
				info = connectivityManager.getActiveNetworkInfo();
				if (info != null && info.isAvailable()) {
					String name = info.getTypeName();
					FileLog.i(TAG + "network", "当前网络名称：" + name);
					// 有网络，检测是否有未上传的缓存信息
					if (!is_uploading) {
//						new Thread(new NetThread()).start();
						init();
					}
				} else {
					Log.d(TAG + "network", "没有可用网络");
				}
			}
		}
	};
	
	private void init() {
		SharedPreferences sp = getSharedPreferences("userdata", 0);
		SetInfo set = IUtil.initSetInfo(sp);
		gps_id = set.getDevice_id();
		
		is_uploading = true;
		//load need upload data
		int list_size = 0;
		try {
			List<VisitBean> visit_list = AutoUploadDBTask.getVisitForm();
			uploadVisitForm(visit_list);
			FileLog.i(TAG,TAG+"visit_list : "+visit_list.size());
			list_size += visit_list.size();
		} catch (Exception e) {
			e.printStackTrace();
			list_size += 0;
		}
		try {
			List<VisitEvaluateBean> evaluate_list = AutoUploadDBTask.getEvaluate();
			uploadEvaluate(evaluate_list);
			FileLog.i(TAG,TAG+"evaluate_list : "+evaluate_list.size());
			list_size += evaluate_list.size();
		} catch (Exception e) {
			e.printStackTrace();
			list_size += 0;
		}
		try {
			List<VisitMcBean> mc_list = AutoUploadDBTask.getMc();
			uploadMc(mc_list);
			FileLog.i(TAG,TAG+"mc_list : "+mc_list.size());
			list_size += mc_list.size();
		} catch (Exception e) {
			e.printStackTrace();
			list_size += 0;
		}
		try {
			List<UploadImgBean> img_list = AutoUploadDBTask.getUploadImg();
			uploadImg(img_list);
			FileLog.i(TAG,TAG+"img_list : "+img_list.size());
			list_size += img_list.size();
		} catch (Exception e) {
			e.printStackTrace();
			list_size += 0;
		}
		
		if (list_size == 0) {
			is_uploading = false;
		}
	}
	
	/**
	 * 拜访记录上传
	 * @param list
	 */
	private void uploadVisitForm(List<VisitBean> list) {
		for (int i = 0; i < list.size(); i++) {
			VisitBean bean = list.get(i);
			String json = gson.toJson(bean);
			String mUrl = URLHelper.BASE_ACTION;
			RequestParams params = new RequestParams();
			params.put("reqCode", URLHelper.UPDATE_FINISH);
			params.put("data_id", bean.id);
			params.put("json", json);
			HttpRestClient.postBg(mUrl, params, new VisitFormResponseHandler(bean));
			count ++;
		}
	}
	
	private class VisitFormResponseHandler extends TextHttpResponseHandler {
		private VisitBean bean;
		public VisitFormResponseHandler(VisitBean b){
			bean = b;
		}
		@Override
		public void onSuccess(int statusCode, Header[] headers, String responseString) {
			FileLog.i(TAG, TAG+"uploadVisitForm,基础数据上传成功.id:"+bean.id);
			try {
				ResultBean resultBean = gson.fromJson(responseString, ResultBean.class);
				if ("1".equals(resultBean.resultcode)) {
					if ("0".equals(bean.visit_img_num)) {
						bean.is_upload = "1";
						bean.status = "2";
						VisitDBTask.updateIsUploadBean(bean);
					} else {
						bean.is_upload = "0";
						bean.status = "4";
						VisitDBTask.updateIsUploadBean(bean);
					}
				} else if("98".equals(resultBean.resultcode)) {//此记录已上传
					if ("0".equals(bean.visit_img_num)) {
						bean.is_upload = "1";
						bean.status = "2";
						VisitDBTask.updateIsUploadBean(bean);
					} else {
						bean.is_upload = "0";
						bean.status = "4";
						VisitDBTask.updateIsUploadBean(bean);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			count--;
			if (count <=0 ) {
				is_uploading = false;
			}
		}
		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
			count--;
			if (count <=0 ) {
				is_uploading = false;
			}
			FileLog.i(TAG, TAG+"uploadVisitForm,基础数据上传失败...");
		}
	}
	
	/**
	 * 服务评价上传
	 * @param list
	 */
	private void uploadEvaluate(List<VisitEvaluateBean> list) throws Exception {
		for (int i = 0; i < list.size(); i++) {
			VisitEvaluateBean bean = list.get(i);
			String json = gson.toJson(bean);
			String mUrl = URLHelper.BASE_ACTION;
			RequestParams params = new RequestParams();
			params.put("reqCode", URLHelper.UPDATE_EVALUATE);
			params.put("data_id", bean.visit_id);
			params.put("json", json);
			params.put("file1", new File(bean.client_sign), contentType);
			HttpRestClient.postBg(mUrl, params, new EvaluateResponseHandler(bean));
			count ++;
		}
	}
	
	private class EvaluateResponseHandler extends TextHttpResponseHandler {
		private VisitEvaluateBean bean;
		public EvaluateResponseHandler(VisitEvaluateBean b){
			bean = b;
		}
		@Override
		public void onSuccess(int statusCode, Header[] headers, String responseString) {
			FileLog.i(TAG, TAG+"uploadEvaluate,基础数据上传成功.id : "+bean.id);
			try {
				ResultBean resultBean = gson.fromJson(responseString, ResultBean.class);
				if ("1".equals(resultBean.resultcode)) {
					AutoUploadDBTask.updateEvaluate(bean.id);
				} else if("98".equals(resultBean.resultcode)) {//此记录已上传
					AutoUploadDBTask.updateEvaluate(bean.id);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			count--;
			if (count <=0 ) {
				is_uploading = false;
			}
		}
		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
			count--;
			if (count <=0 ) {
				is_uploading = false;
			}
			FileLog.i(TAG, TAG+"uploadEvaluate,基础数据上传失败...");
		}
	}
	
	/**
	 * 机修记录上传
	 * @param list
	 * @throws Exception
	 */
	private void uploadMc(List<VisitMcBean> list) throws Exception{
		for (int i = 0; i < list.size(); i++) {
			VisitMcBean bean = list.get(i);
			String json = gson.toJson(bean);
			String mUrl = URLHelper.BASE_ACTION;
			RequestParams params = new RequestParams();
			params.put("reqCode", URLHelper.UPDATE_MC);
			params.put("gps_id", gps_id);
			params.put("data_id", bean.id);
			params.put("json", json);
			params.put("file1", new File(bean.client_sign));
			HttpRestClient.postBg(mUrl, params, new McResponseHandler(bean));
			count ++;
		}
	}
	
	private class McResponseHandler extends TextHttpResponseHandler {
		private VisitMcBean bean;
		public McResponseHandler(VisitMcBean b){
			bean = b;
		}
		@Override
		public void onSuccess(int statusCode, Header[] headers, String responseString) {
			FileLog.i(TAG, TAG+"uploadMc,基础数据上传成功.id:"+bean.id);
			try {
				ResultBean resultBean = gson.fromJson(responseString, ResultBean.class);
				if ("1".equals(resultBean.resultcode)) {
					if ("0".equals(bean.upload_img_num)) {
						AutoUploadDBTask.updateMc(bean.id,"1");
					} else {
						AutoUploadDBTask.updateMc(bean.id,"00");
					}
				} else if("98".equals(resultBean.resultcode)) {//此记录已上传
					if ("0".equals(bean.upload_img_num)) {
						AutoUploadDBTask.updateMc(bean.id,"1");
					} else {
						AutoUploadDBTask.updateMc(bean.id,"00");
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			count--;
			if (count <=0 ) {
				is_uploading = false;
			}
		}
		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
			count--;
			if (count <=0 ) {
				is_uploading = false;
			}
			FileLog.i(TAG, TAG+"uploadMc,基础数据上传失败...");
		}
	}
	
	/**
	 * 图片上传
	 * @param list
	 * @throws Exception
	 */
	private void uploadImg(List<UploadImgBean> list) throws Exception{
		String mUrl = URLHelper.BASE_ACTION;
		for (int i = 0; i < list.size(); i++) {
			UploadImgBean bean = list.get(i);
			String json = gson.toJson(bean);
			File file = new File(bean.path);
			RequestParams params = new RequestParams();
			params.put("reqCode", URLHelper.IMG_UPLOAD);
			params.put("data_id", bean.data_id);
			params.put("type", bean.type);
			params.put("json", json);
			params.put("file"+i, file, contentType);
			params.setHttpEntityIsRepeatable(false);
			params.setUseJsonStreamer(false);
			HttpRestClient.postBg(mUrl, params, new ImgResponseHandler(bean));
			count ++;
		}
	}
	
	private class ImgResponseHandler extends TextHttpResponseHandler {
		private UploadImgBean bean;
		public ImgResponseHandler(UploadImgBean b){
			bean = b;
		}
		@Override
		public void onSuccess(int statusCode, Header[] headers, String responseString) {
			try {
				Gson gson = new Gson();
				ResultBean resultBean = gson.fromJson(responseString, ResultBean.class);
				if ("1".equals(resultBean.resultcode)) { //success
					FileLog.i(TAG, TAG+"uploadImg,图片上传成功.id:"+bean.id+",data_id:"+bean.data_id);
					UploadDBTask.deleteImgBean(bean.id);
					String img_num = resultBean.img_num;
					if (TextUtils.isEmpty(img_num)) {
						FileLog.i(TAG, TAG+"---->");
						FileLog.i(TAG, TAG+"图片上传失败，返回图片num 为空.");
						FileLog.i(TAG, TAG+"<----");
						return;
					}
					int num = Integer.parseInt(img_num);
					if (num <= 0) {
						if ("1".equals(bean.type)) {
							AutoUploadDBTask.updateVisitForm(bean.data_id);
						} else if("2".equals(bean.type)) {
							AutoUploadDBTask.updateMc(bean.data_id, "1");
						}
						FileLog.i(TAG, TAG+"图片已全部上传...");
					}
				} else if("98".equals(resultBean.resultcode)){//repeat
					UploadDBTask.deleteImgBean(bean.id);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			count--;
			if (count <=0 ) {
				is_uploading = false;
			}
		}
		@Override
		public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
			count--;
			if (count <=0 ) {
				is_uploading = false;
			}
			FileLog.i(TAG, TAG+"uploadImg,图片上传失败.");
		}
	}

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
