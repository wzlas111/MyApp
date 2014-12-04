/**
 * Copyright (c) 2012-11-16 www.eastelsoft.com
 * $ID BaiduMapAction.java 下午1:17:06 $
 */
package com.eastelsoft.lbs.location;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.eastelsoft.util.CallBack;
import com.eastelsoft.util.Contant;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.GlobalVar;
import com.eastelsoft.util.Util;

/**
 * 百度地图api获取位置信息
 * 
 * @author lengcj
 */
public class BaiduMapActionForCheckin {
	private static final String TAG = "BaiduMapActionForCheckin";
	private CallBack callBack;
	private Context context;
	private GlobalVar globalVar;
	private String reportTag = "";
	private boolean isSend = false;

	private LocationClient mLocationClient = null;

	// private BDLocation currentLocation = null;
	private Location currentBaiduLocation = null;
	private int locationTimes = 0;

	private Handler timeoutHandler = new Handler();
	private Runnable timeoutRunnable = new Runnable() {
		public void run() {
			BaiduMapActionForCheckin.this.stopListener();
			BaiduMapActionForCheckin.this.stopCallBack();

			FileLog.i(TAG, "location timeout");
		}
	};

	public BaiduMapActionForCheckin(Context context, CallBack callBack,
			String reportTag) {
		this.context = context.getApplicationContext();
		this.callBack = callBack;
		this.reportTag = reportTag;
		this.globalVar = (GlobalVar) context.getApplicationContext();
		FileLog.i(TAG, "BaiduMapAction 构造器");
	}

	public void startListener() {
		try {
			
			mLocationClient = new LocationClient(context);
			LocationClientOption option = new LocationClientOption();
			option.setOpenGps(true);
			option.setCoorType("bd09ll");// 返回的定位结果是百度经纬度,默认值gcj02
			option.setScanSpan(5000);// 设置发起定位请求的间隔时间为2000ms
			option.setProdName("eastelsoftwqt");
			option.setIsNeedAddress(true);
			mLocationClient.setLocOption(option);
			mLocationClient.registerLocationListener(new BDLocationListener() {
				@Override
				public void onReceiveLocation(BDLocation location) {
					if (location != null) {
						// if(location.getRadius() > 300)
						locationTimes++;
						FileLog.i(TAG, "BDlocation==>" + "sb");
						FileLog.i(TAG, "BDlocation==>" + location);
						currentBaiduLocation = new Location("lbs");
						currentBaiduLocation.setProvider("lbs");
						currentBaiduLocation.setAccuracy(location.getRadius());
//						currentBaiduLocation.setLongitude(location.getLongitude());
//						currentBaiduLocation.setLatitude(location.getLatitude());
						currentBaiduLocation.setLongitude(location.getLongitude() - 0.0065f);
						currentBaiduLocation.setLatitude(location.getLatitude() - 0.006f);
						// 增加时间2013-07-10 13:13:05
						SimpleDateFormat sdf = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss");
						long timeStart = 0;
						try {
							timeStart = sdf.parse(location.getTime()).getTime();
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						currentBaiduLocation.setTime(timeStart);
						currentBaiduLocation.setAccuracy(-location.getRadius());
						Bundle b = new Bundle();
						b.putString("desc", location.getAddrStr());
						currentBaiduLocation.setExtras(b);

						BaiduMapActionForCheckin.this
								.sendBroadcast(currentBaiduLocation);

					}
					FileLog.i(TAG, "currentBaiduLocation==>"
							+ currentBaiduLocation);
				}

				public void onReceivePoi(BDLocation location) {
					// return ;
				}
			});
			this.timeoutHandler.postDelayed(this.timeoutRunnable, 180 * 1000);
			
			mLocationClient.start();
		} catch (Exception e) {

		}
	}

	public void stopall() {
		BaiduMapActionForCheckin.this.stopListener();
		BaiduMapActionForCheckin.this.stopCallBack();

		FileLog.i(TAG, "location timeout");

	}

	private void stopListener() {
		if (mLocationClient != null && mLocationClient.isStarted()) {
			mLocationClient.stop();
			mLocationClient = null;
		}
	}

	private void stopCallBack() {
		if (timeoutHandler != null)
			this.timeoutHandler.removeCallbacks(this.timeoutRunnable);
	}

	/**
	 * 广播定位数据
	 * 
	 * @param location
	 * @param provider
	 */
	public void sendBroadcast(Location location) {
		try {
//			if ("0".equals(reportTag) || "1".equals(reportTag)) {
				Intent it = new Intent("android.location.action");
				// String location_desc = "经度"
				// + Util.format(location.getLongitude(), "#.######")
				// + "，纬度"
				// + Util.format(location.getLatitude(), "#.######")
				// + "，精度" + location.getAccuracy() + "米";
				// it.putExtra("location_desc", location_desc);

				it.putExtra("longitude", location.getLongitude());
				it.putExtra("latitude", location.getLatitude());
				it.putExtra("acc", location.getAccuracy());
				it.putExtra("time", location.getTime());
				
				it.putExtras(location.getExtras());

				context.sendBroadcast(it);
//			}
		} catch (Exception e) {
			FileLog.e(TAG, e + "");
		}
	}
}
