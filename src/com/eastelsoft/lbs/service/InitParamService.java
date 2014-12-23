package com.eastelsoft.lbs.service;

import java.util.List;

import org.apache.http.Header;

import com.eastelsoft.lbs.bean.ClientRegionDto;
import com.eastelsoft.lbs.bean.ClientTypeDto;
import com.eastelsoft.lbs.bean.ClientRegionDto.RegionBean;
import com.eastelsoft.lbs.bean.ClientTypeDto.TypeBean;
import com.eastelsoft.lbs.bean.EnterpriseTypeDto;
import com.eastelsoft.lbs.bean.EnterpriseTypeDto.EnterpriseTypeBean;
import com.eastelsoft.lbs.bean.EvaluateDto;
import com.eastelsoft.lbs.bean.EvaluateDto.EvaluateBean;
import com.eastelsoft.lbs.bean.OrderTypeDto;
import com.eastelsoft.lbs.bean.OrderTypeDto.OrderTypeBean;
import com.eastelsoft.lbs.bean.ProductTypeDto;
import com.eastelsoft.lbs.bean.ProductTypeDto.ProductTypeBean;
import com.eastelsoft.lbs.db.ClientDBTask;
import com.eastelsoft.lbs.db.ParamsDBTask;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.http.HttpRestClient;
import com.eastelsoft.util.http.URLHelper;
import com.eastelsoft.util.settinghelper.SettingUtility;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

/**
 * 初始化系统参数 1:客户类型;2:客户区域;3:服务评价;4:产品类型;5:订单类型;6:单位类型
 * @author wangzl
 *
 */
public class InitParamService extends Service {
	
	public static String TAG = "InitParamService";
	private String gps_id;

	@Override
	public void onCreate() {
		super.onCreate();
		FileLog.i(TAG, TAG+"---->onCreate");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		FileLog.i(TAG, TAG+"---->onStartCommand");
		gps_id = intent.getStringExtra("gps_id");
		FileLog.i(TAG, TAG+"gpsid : "+gps_id);
		if (TextUtils.isEmpty(gps_id)) {
			stopService();
		}else {
			initClientType();
			initClientRegion();
			initVisitEvaluate();
			initProductType();
			initOrderType();
			initEnterpriseType();
		}
		return START_NOT_STICKY;
	}
	
	private void initClientType() {
		String updatecode = SettingUtility.getUpdatecodeValue(SettingUtility.CLIENT_TYPE_UPDATECODE);
		String mUrl = URLHelper.TEST_ACTION;
		RequestParams params = new RequestParams();
		params.put("reqCode", URLHelper.UPDATE_CLIENT_TYPE);
		params.put("code", updatecode);
		params.put("gpsid", gps_id);
		HttpRestClient.getSingle(mUrl, params, new TextHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,String responseString) {
				//insert to db
				FileLog.i(TAG, TAG+"客户类型 : "+responseString);
				try {
					ClientTypeDto dto = new Gson().fromJson(responseString, ClientTypeDto.class);
					if ("1".equals(dto.resultcode)) {
						SettingUtility.setValue(SettingUtility.CLIENT_TYPE_UPDATECODE, dto.updatecode);
						List<TypeBean> list = dto.clientdata;
						if (list != null && list.size() > 0) {
							ClientDBTask.deleteType();
							ClientDBTask.addType(list);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			@Override
			public void onFailure(int statusCode, Header[] headers,String responseString, Throwable throwable) {
				FileLog.i(TAG, TAG+"客户类型下载失败.");
			}
		});
	}
	
	private void initClientRegion() {
		String updatecode = SettingUtility.getUpdatecodeValue(SettingUtility.CLIENT_REGION_UPDATECODE);
		String mUrl = URLHelper.TEST_ACTION;
		RequestParams params = new RequestParams();
		params.put("reqCode", URLHelper.UPDATE_CLIENT_REGION);
		params.put("code", updatecode);
		params.put("gpsid", gps_id);
		HttpRestClient.getSingle(mUrl, params, new TextHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,String responseString) {
				//insert to db
				FileLog.i(TAG, TAG+"客户区域 : "+responseString);
				try {
					ClientRegionDto dto = new Gson().fromJson(responseString, ClientRegionDto.class);
					if ("1".equals(dto.resultcode)) {
						SettingUtility.setValue(SettingUtility.CLIENT_REGION_UPDATECODE, dto.updatecode);
						List<RegionBean> list = dto.clientdata;
						if (list != null && list.size() > 0) {
							ClientDBTask.deleteRegion();
							ClientDBTask.addRegion(list);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			@Override
			public void onFailure(int statusCode, Header[] headers,String responseString, Throwable throwable) {
				FileLog.i(TAG, TAG+"客户区域下载失败.");
			}
		});
	}
	
	private void initVisitEvaluate() {
		String updatecode = SettingUtility.getUpdatecodeValue(SettingUtility.VISIT_EVALUATE_UPDATECODE);
		String mUrl = URLHelper.TEST_ACTION;
		RequestParams params = new RequestParams();
		params.put("reqCode", URLHelper.UPDATE_VISIT_EVALUATE);
		params.put("code", updatecode);
		params.put("GpsId", gps_id);
		HttpRestClient.getSingle(mUrl, params, new TextHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,String responseString) {
				//insert to db
				FileLog.i(TAG, TAG+"服务评价 : "+responseString);
				try {
					EvaluateDto dto = new Gson().fromJson(responseString, EvaluateDto.class);
					if ("1".equals(dto.resultcode)) {
						SettingUtility.setValue(SettingUtility.VISIT_EVALUATE_UPDATECODE, dto.updatecode);
						List<EvaluateBean> list = dto.clientdata;
						if (list != null && list.size() > 0) {
							ParamsDBTask.deleteEvaluate();
							ParamsDBTask.addEvaluateList(list);
						}
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			@Override
			public void onFailure(int statusCode, Header[] headers,String responseString, Throwable throwable) {
				FileLog.i(TAG, TAG+"服务评价下载失败.");
			}
		});
	}
	
	private void initProductType() {
		String updatecode = SettingUtility.getUpdatecodeValue(SettingUtility.PRODUCT_TYPE_UPDATECODE);
		String mUrl = URLHelper.TEST_ACTION;
		RequestParams params = new RequestParams();
		params.put("reqCode", URLHelper.UPDATE_PRODUCT_TYPE);
		params.put("code", updatecode);
		params.put("GpsId", gps_id);
		HttpRestClient.getSingle(mUrl, params, new TextHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,String responseString) {
				//insert to db
				FileLog.i(TAG, TAG+"产品类型 : "+responseString);
				ProductTypeDto dto = new Gson().fromJson(responseString, ProductTypeDto.class);
				if ("1".equals(dto.resultcode)) {
					SettingUtility.setValue(SettingUtility.PRODUCT_TYPE_UPDATECODE, dto.updatecode);
					List<ProductTypeBean> list = dto.clientdata;
					if (list != null && list.size() > 0) {
						ParamsDBTask.deleteProductType();
						ParamsDBTask.addProductTypeList(list);
					}
				}
			}
			@Override
			public void onFailure(int statusCode, Header[] headers,String responseString, Throwable throwable) {
				FileLog.i(TAG, TAG+"产品类型下载失败.");
			}
		});
	}
	
	private void initOrderType() {
		String updatecode = SettingUtility.getUpdatecodeValue(SettingUtility.ORDER_TYPE_UPDATECODE);
		String mUrl = URLHelper.TEST_ACTION;
		RequestParams params = new RequestParams();
		params.put("reqCode", URLHelper.UPDATE_ORDER_TYPE);
		params.put("code", updatecode);
		params.put("GpsId", gps_id);
		HttpRestClient.getSingle(mUrl, params, new TextHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,String responseString) {
				//insert to db
				FileLog.i(TAG, TAG+"订单类型 : "+responseString);
				OrderTypeDto dto = new Gson().fromJson(responseString, OrderTypeDto.class);
				if ("1".equals(dto.resultcode)) {
					SettingUtility.setValue(SettingUtility.ORDER_TYPE_UPDATECODE, dto.updatecode);
					List<OrderTypeBean> list = dto.clientdata;
					if (list != null && list.size() > 0) {
						ParamsDBTask.deleteOrderType();
						ParamsDBTask.addOrderTypeList(list);
					}
				}
			}
			@Override
			public void onFailure(int statusCode, Header[] headers,String responseString, Throwable throwable) {
				FileLog.i(TAG, TAG+"订单类型下载失败.");
			}
		});
	}
	
	private void initEnterpriseType() {
		String updatecode = SettingUtility.getUpdatecodeValue(SettingUtility.ENTERPRISE_TYPE_UPDATECODE);
		String mUrl = URLHelper.TEST_ACTION;
		RequestParams params = new RequestParams();
		params.put("reqCode", URLHelper.UPDATE_ENTERPRISE_TYPE);
		params.put("code", updatecode);
		params.put("GpsId", gps_id);
		HttpRestClient.getSingle(mUrl, params, new TextHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,String responseString) {
				//insert to db
				FileLog.i(TAG, TAG+"企业类型 : "+responseString);
				EnterpriseTypeDto dto = new Gson().fromJson(responseString, EnterpriseTypeDto.class);
				if ("1".equals(dto.resultcode)) {
					SettingUtility.setValue(SettingUtility.ENTERPRISE_TYPE_UPDATECODE, dto.updatecode);
					List<EnterpriseTypeBean> list = dto.clientdata;
					if (list != null && list.size() > 0) {
						ParamsDBTask.deleteEnterpriseType();
						ParamsDBTask.addEnterpriseTypeList(list);
					}
				}
				stopService();
			}
			@Override
			public void onFailure(int statusCode, Header[] headers,String responseString, Throwable throwable) {
				FileLog.i(TAG, TAG+"企业类型下载失败.");
				stopService();
			}
		});
	}
	
	private void stopService() {
		stopForeground(true);
        stopSelf();
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}
