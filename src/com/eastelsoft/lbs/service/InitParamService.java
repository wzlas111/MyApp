package com.eastelsoft.lbs.service;

import java.util.List;

import org.apache.http.Header;

import com.eastelsoft.lbs.bean.ClientDto;
import com.eastelsoft.lbs.bean.ClientRegionDto;
import com.eastelsoft.lbs.bean.ClientTypeDto;
import com.eastelsoft.lbs.bean.CommodityDto;
import com.eastelsoft.lbs.bean.CommodityReasonDto;
import com.eastelsoft.lbs.bean.DealerDto;
import com.eastelsoft.lbs.bean.ClientDto.ClientBean;
import com.eastelsoft.lbs.bean.ClientRegionDto.RegionBean;
import com.eastelsoft.lbs.bean.ClientTypeDto.TypeBean;
import com.eastelsoft.lbs.bean.CommodityDto.CommodityBean;
import com.eastelsoft.lbs.bean.CommodityReasonDto.CommodityReasonBean;
import com.eastelsoft.lbs.bean.DealerDto.DealerBean;
import com.eastelsoft.lbs.bean.EnterpriseTypeDto;
import com.eastelsoft.lbs.bean.EnterpriseTypeDto.EnterpriseTypeBean;
import com.eastelsoft.lbs.bean.EvaluateDto;
import com.eastelsoft.lbs.bean.EvaluateDto.EvaluateBean;
import com.eastelsoft.lbs.bean.OrderTypeDto;
import com.eastelsoft.lbs.bean.OrderTypeDto.OrderTypeBean;
import com.eastelsoft.lbs.bean.ProductTypeDto;
import com.eastelsoft.lbs.bean.ProductTypeDto.ProductTypeBean;
import com.eastelsoft.lbs.db.ClientDBTask;
import com.eastelsoft.lbs.db.DealerDBTask;
import com.eastelsoft.lbs.db.ParamsDBTask;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.GlobalVar;
import com.eastelsoft.util.http.HttpRestClient;
import com.eastelsoft.util.http.URLHelper;
import com.eastelsoft.util.settinghelper.SettingUtility;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

/**
 * 初始化系统参数 1:客户类型;2:客户区域;3:服务评价;4:产品类型;5:订单类型;6:单位类型
 * @author wangzl
 *
 */
public class InitParamService extends Service {
	
	public static String TAG = "InitParamService";
	private String gps_id;
	private boolean is_reg = false;
	private Gson gson = new Gson();

	@Override
	public void onCreate() {
		super.onCreate();
		FileLog.i(TAG, TAG+"---->onCreate");
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		FileLog.i(TAG, TAG+"---->onStartCommand");
		gps_id = intent.getStringExtra("gps_id");
		is_reg = intent.getBooleanExtra("is_reg", false);
		FileLog.i(TAG, TAG+"gpsid : "+gps_id);
		if (TextUtils.isEmpty(gps_id)) {
			stopService();
		}else {
			try {
				new Thread(new InitThread()).start();
			} catch (Exception e) {
				e.printStackTrace();
			}
			
		}
		return START_NOT_STICKY;
	}
	
	/**
	 * 另起一线程，新建一个looper和消息队列，即使线程耗时很长也不会阻塞UI线程.
	 * 注意:httplib 会自动识别looper是否为主线程
	 * @author wangzl
	 *
	 */
	private class InitThread extends Thread {
		@Override
		public void run() {
			Looper.prepare();
			if (is_reg) {//初次注册默认加载联系人和客户信息
				try {
					initDealerList();
				} catch (Exception e) {
					GlobalVar.getInstance().setDealer_uploading(true);
				}
				try {
					initClientList();
				} catch (Exception e) {
					GlobalVar.getInstance().setClient_uploading(true);
				}
			}
			initClientType();
			initClientRegion();
			initVisitEvaluate();
			initProductType();
			initOrderType();
			initEnterpriseType();
			initCommodity();
			initCommodityReason();
			Looper.loop();
		}
	}
	
	private void initClientType() {
		String updatecode = SettingUtility.getUpdatecodeValue(SettingUtility.CLIENT_TYPE_UPDATECODE);
		String mUrl = URLHelper.TEST_ACTION;
		RequestParams params = new RequestParams();
		params.put("reqCode", URLHelper.UPDATE_CLIENT_TYPE);
		params.put("code", updatecode);
		params.put("GpsId", gps_id);
		HttpRestClient.getSingle(mUrl, params, new TextHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,String responseString) {
				//insert to db
				FileLog.i(TAG, TAG+"客户类型下载成功.");
				Log.i(TAG, TAG+"客户类型下载成功.data:"+responseString);
				try {
					ClientTypeDto dto = gson.fromJson(responseString, ClientTypeDto.class);
					if ("1".equals(dto.resultcode)) {
						List<TypeBean> list = dto.clientdata;
						if (list != null && list.size() > 0) {
							ClientDBTask.deleteType();
							ClientDBTask.addType(list);
						}
						SettingUtility.setValue(SettingUtility.CLIENT_TYPE_UPDATECODE, dto.updatecode);
					}
				} catch (Exception e) {
					FileLog.e(TAG, TAG+" e==>" + e.toString());
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
		params.put("GpsId", gps_id);
		HttpRestClient.getSingle(mUrl, params, new TextHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,String responseString) {
				//insert to db
				FileLog.i(TAG, TAG+"客户区域 下载成功.");
				Log.i(TAG, TAG+"客户区域下载成功.data:"+responseString);
				try {
					ClientRegionDto dto = gson.fromJson(responseString, ClientRegionDto.class);
					if ("1".equals(dto.resultcode)) {
						List<RegionBean> list = dto.clientdata;
						if (list != null && list.size() > 0) {
							ClientDBTask.deleteRegion();
							ClientDBTask.addRegion(list);
						}
						SettingUtility.setValue(SettingUtility.CLIENT_REGION_UPDATECODE, dto.updatecode);
					}
				} catch (Exception e) {
					FileLog.e(TAG, TAG+" e==>" + e.toString());
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
				FileLog.i(TAG, TAG+"服务评价下载成功.");
				Log.i(TAG, TAG+"服务评价下载成功.data:"+responseString);
				try {
					EvaluateDto dto = gson.fromJson(responseString, EvaluateDto.class);
					if ("1".equals(dto.resultcode)) {
						List<EvaluateBean> list = dto.clientdata;
						if (list != null && list.size() > 0) {
							ParamsDBTask.deleteEvaluate();
							ParamsDBTask.addEvaluateList(list);
						}
						SettingUtility.setValue(SettingUtility.VISIT_EVALUATE_UPDATECODE, dto.updatecode);
					}
				} catch (Exception e) {
					FileLog.e(TAG, TAG+" e==>" + e.toString());
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
				FileLog.i(TAG, TAG+"产品类型 下载成功.");
				Log.i(TAG, TAG+"产品类型下载成功.data:"+responseString);
				ProductTypeDto dto = gson.fromJson(responseString, ProductTypeDto.class);
				if ("1".equals(dto.resultcode)) {
					List<ProductTypeBean> list = dto.clientdata;
					if (list != null && list.size() > 0) {
						ParamsDBTask.deleteProductType();
						ParamsDBTask.addProductTypeList(list);
					}
					SettingUtility.setValue(SettingUtility.PRODUCT_TYPE_UPDATECODE, dto.updatecode);
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
				FileLog.i(TAG, TAG+"订单类型下载成功.");
				Log.i(TAG, TAG+"订单类型下载成功.data:"+responseString);
				OrderTypeDto dto = gson.fromJson(responseString, OrderTypeDto.class);
				if ("1".equals(dto.resultcode)) {
					List<OrderTypeBean> list = dto.clientdata;
					if (list != null && list.size() > 0) {
						ParamsDBTask.deleteOrderType();
						ParamsDBTask.addOrderTypeList(list);
					}
					SettingUtility.setValue(SettingUtility.ORDER_TYPE_UPDATECODE, dto.updatecode);
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
				FileLog.i(TAG, TAG+"企业类型下载成功.");
				Log.i(TAG, TAG+"企业类型下载成功.data:"+responseString);
				EnterpriseTypeDto dto = gson.fromJson(responseString, EnterpriseTypeDto.class);
				if ("1".equals(dto.resultcode)) {
					List<EnterpriseTypeBean> list = dto.clientdata;
					if (list != null && list.size() > 0) {
						ParamsDBTask.deleteEnterpriseType();
						ParamsDBTask.addEnterpriseTypeList(list);
					}
					SettingUtility.setValue(SettingUtility.ENTERPRISE_TYPE_UPDATECODE, dto.updatecode);
				}
			}
			@Override
			public void onFailure(int statusCode, Header[] headers,String responseString, Throwable throwable) {
				FileLog.i(TAG, TAG+"企业类型下载失败.");
			}
		});
	}
	
	private void initCommodity() {
		String updatecode = SettingUtility.getUpdatecodeValue(SettingUtility.COMMODITY_UPDATECODE);
		String mUrl = URLHelper.TEST_ACTION;
		RequestParams params = new RequestParams();
		params.put("reqCode", URLHelper.UPDATE_COMMODITY);
		params.put("code", updatecode);
		params.put("GpsId", gps_id);
		HttpRestClient.getSingle(mUrl, params, new TextHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,String responseString) {
				//insert to db
				FileLog.i(TAG, TAG+"机器型号下载成功.");
				Log.i(TAG, TAG+"机器型号下载成功.data:"+responseString);
				CommodityDto dto = gson.fromJson(responseString, CommodityDto.class);
				if ("1".equals(dto.resultcode)) {
					List<CommodityBean> list = dto.clientdata;
					if (list != null && list.size() > 0) {
						ParamsDBTask.deleteCommodity();
						ParamsDBTask.addCommodityList(list);
					}
					SettingUtility.setValue(SettingUtility.COMMODITY_UPDATECODE, dto.updatecode);
				}
			}
			@Override
			public void onFailure(int statusCode, Header[] headers,String responseString, Throwable throwable) {
				FileLog.i(TAG, TAG+"机器型号下载失败.");
			}
		});
	}
	
	private void initCommodityReason() {
		String updatecode = SettingUtility.getUpdatecodeValue(SettingUtility.COMMODITY_REASON_UPDATECODE);
		String mUrl = URLHelper.TEST_ACTION;
		RequestParams params = new RequestParams();
		params.put("reqCode", URLHelper.UPDATE_COMMODITY_REASON);
		params.put("code", updatecode);
		params.put("GpsId", gps_id);
		HttpRestClient.getSingle(mUrl, params, new TextHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers,String responseString) {
				//insert to db
				FileLog.i(TAG, TAG+"机器故障下载成功.");
				Log.i(TAG, TAG+"机器故障下载成功.data:"+responseString);
				CommodityReasonDto dto = gson.fromJson(responseString, CommodityReasonDto.class);
				if ("1".equals(dto.resultcode)) {
					List<CommodityReasonBean> list = dto.clientdata;
					if (list != null && list.size() > 0) {
						ParamsDBTask.deleteCommodityReason();
						ParamsDBTask.addCommodityReasonList(list);
					}
					SettingUtility.setValue(SettingUtility.COMMODITY_REASON_UPDATECODE, dto.updatecode);
				}
			}
			@Override
			public void onFailure(int statusCode, Header[] headers,String responseString, Throwable throwable) {
				FileLog.i(TAG, TAG+"机器故障下载失败.");
			}
		});
	}
	
	private void stopService() {
		stopForeground(true);
        stopSelf();
	}
	
	/**
	 * 客户列表及经销商数据更新
	 */
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0: //handle client list
				new ClientThread((String)msg.obj).start();
				break;
			case 1: //handle dealer list
				new DealerThread((String)msg.obj).start();
				break;
			}
		};
	};
	
	private class ClientThread extends Thread {
		private String responseString;
		public ClientThread(String param) {
			responseString = param;
		}
		@Override
		public void run() {
			try {
				Gson gson = new Gson();
				ClientDto clientDto = gson.fromJson(responseString, ClientDto.class);
				if ("1".equals(clientDto.resultcode)) { //load from net
					FileLog.i(TAG, TAG+"经销商数据数据下载:版本号不同，更新数据库.");
					List<ClientBean> mList = clientDto.clientdata;
					ClientDBTask.deleteAll();
					ClientDBTask.addBeanList(mList);
					SettingUtility.setValue(SettingUtility.CLIENT_UPDATECODE, clientDto.updatecode);
				} else {
					FileLog.i(TAG, TAG+"经销商数据数据下载:版本号相同，无需更新.");
				}
			} catch (Exception e) {
				FileLog.e(TAG, TAG+" e==>" + e.toString());
			}
			GlobalVar.getInstance().setClient_uploading(false);
		}
	}
	
	private class DealerThread extends Thread {
		private String responseString;
		public DealerThread(String param) {
			responseString = param;
		}
		@Override
		public void run() {
			try {
				DealerDto dealerDto = gson.fromJson(responseString, DealerDto.class);
				if ("1".equals(dealerDto.resultcode)) { //load from net
					FileLog.i(TAG, TAG+"经销商数据数据下载:版本号不同，更新数据库.");
					List<DealerBean> mList = dealerDto.clientdata;
					DealerDBTask.deleteAll();
					DealerDBTask.addBeanList(mList);
					SettingUtility.setValue(SettingUtility.DEALER_UPDATECODE, dealerDto.updatecode);
				} else {
					FileLog.i(TAG, TAG+"经销商数据数据下载:版本号相同，无需更新.");
				}
			} catch (Exception e) {
				FileLog.e(TAG, TAG+" e==>" + e.toString());
			}
			GlobalVar.getInstance().setDealer_uploading(false);
		}
	}
	
	private void initClientList() {
		String updatecode = SettingUtility.getUpdatecodeValue(SettingUtility.CLIENT_UPDATECODE);
		FileLog.i(TAG, TAG+".updatecode: "+updatecode);
		String mUrl = URLHelper.TEST_ACTION;
		RequestParams params = new RequestParams();
		params.put("reqCode", "ClientUpdateActionJk");
		params.put("GpsId", gps_id);
		params.put("code", updatecode);
		params.put("Pin", "111111");
		HttpRestClient.getSingle(mUrl, params, new TextHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
				FileLog.i(TAG, TAG+"客户数据下载成功.");
				Message msg = new Message();
				msg.what = 0;
				msg.obj = responseString;
				mHandler.sendMessage(msg);
			}
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				FileLog.i(TAG, TAG+"客户数据数据下载失败.");
			}
		});
	}
	
	private void initDealerList() {
		String updatecode = SettingUtility.getUpdatecodeValue(SettingUtility.DEALER_UPDATECODE);
		FileLog.i(TAG, TAG+".updatecode: "+updatecode);
		String mUrl = URLHelper.TEST_ACTION;
		RequestParams params = new RequestParams();
		params.put("reqCode", "DealerUpdateActionJk");
		params.put("GpsId", gps_id);
		params.put("code", updatecode);
		params.put("Pin", "111111");
		HttpRestClient.getSingle(mUrl, params, new TextHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
				FileLog.i(TAG, TAG+"经销商数据数据下载成功.");
				Message msg = new Message();
				msg.what = 1;
				msg.obj = responseString;
				mHandler.sendMessage(msg);
			}
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				FileLog.i(TAG, TAG+"经销商数据数据下载失败.");
			}
		});
	}
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

}