/**
 * Copyright (c) 2012-8-2 www.eastelsoft.com
 * $ID CheckinAction.java 下午10:03:47 $
 */
package com.eastelsoft.lbs.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;

import com.eastelsoft.util.CallBack;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.GlobalVar;

/**
 * 获取GPS定位数据
 * 
 * @author lengcj
 */
public class GpsAction {
	private static final String TAG = "GpsAction";
	private CallBack callBack;
	private Context context;
	private GlobalVar globalVar;
	private String reportTag = "";

	private LocationManager locationManager;
	private LocationListener locationListener;
	private long gps_time;

	private Handler timeoutHandler = new Handler();
	private Runnable timeoutRunnable = new Runnable() {
		public void run() {
			GpsAction.this.stopListener();
			GpsAction.this.stopCallBack();
			GpsAction.this.sendLocation(null);
			FileLog.i(TAG, "location timeout");
		}
	};

	public GpsAction(Context context, CallBack callBack, String reportTag, long gps_time) {
		this.context = context.getApplicationContext();
		this.callBack = callBack;
		this.reportTag = reportTag;
		this.globalVar = (GlobalVar)context.getApplicationContext();
		if (gps_time == 0) {
			gps_time = 40000L; // 默认40s
		} else if(gps_time > 0 && gps_time <= 15) {
			gps_time = 15000L; // 最小15s 
		} else {
			gps_time = (long) (gps_time * 1000);
		}
		if (globalVar.isGpsIsFirst()
				&& "".equals(reportTag)) {
			gps_time = 50000L; // 第一次工作50s
		}
		this.gps_time = gps_time;
	}

	public void startListener() {
		try {
			this.locationManager = (LocationManager) this.context
					.getSystemService(Context.LOCATION_SERVICE);
			if (this.locationManager.isProviderEnabled("gps")) {
				FileLog.i(TAG, "Start location");
				gpsLocation();
			} else {
				// gps未开，直接返回
				FileLog.i(TAG, "Gps not open");
				GpsAction.this.sendLocation(null);
				//globalVar.setLocation(null);
				//globalVar.setLocationtime(System.currentTimeMillis());
				// 检测到gps未开时，设置下一次工作为第一次
				globalVar.setGpsIsFirst(true);
			}
		} catch (Exception e) {

		}
	}

	public void gpsLocation() {
		FileLog.i(TAG, "Start locationListener==>" + locationListener);
		// 启动监听
		locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				FileLog.i(TAG, "location==>" + location);
				
				// 停止监听，发送消息
				GpsAction.this.stopListener();
				GpsAction.this.stopCallBack();
				GpsAction.this.sendLocation(location);
				globalVar.setLocation(location);
				globalVar.setLocation_correct(location);
				globalVar.setLocationtime(System.currentTimeMillis());
				//location.hasAccuracy();
			}
			  /** 
	         * GPS禁用时触发 
	         */ 
			public void onProviderDisabled(String paramString) {
				FileLog.i(TAG, "onProviderDisabled==>" + paramString);
			}
			  /** 
	         * GPS开启时触发 
	         */ 
			public void onProviderEnabled(String paramString) {
				FileLog.i(TAG, "onProviderEnabled==>" + paramString);
			}
			  /** 
	         * GPS状态变化时触发 
	         */ 
			public void onStatusChanged(String paramString, int paramInt,
					Bundle paramBundle) {
				FileLog.i(TAG, "onStatusChanged==>" + paramString);
			}
		};
		this.locationManager.requestLocationUpdates("gps", 2000L, 0.0F,
				this.locationListener);
		globalVar.setGpsIsFirst(false);
		FileLog.i(TAG, "gpsTime==================>" + gps_time);
		this.timeoutHandler.postDelayed(this.timeoutRunnable, gps_time);
	}

	private void stopListener() {
		FileLog.i(TAG, " Stop locationListener==>" + locationListener);
		if(locationManager != null && locationListener != null)
			this.locationManager.removeUpdates(this.locationListener);
		if(timeoutHandler != null)
			this.timeoutHandler.removeCallbacks(this.timeoutRunnable);
		locationManager = null;
	}
	
	private void sendLocation(Location location) {
		// 回调请求数据 
		CallBack localCallBack = this.callBack; 
		Object[] arrayOfObject = new Object[2];
		arrayOfObject[0] = location;
		arrayOfObject[1] = reportTag;
		localCallBack.execute(arrayOfObject);
		FileLog.i(TAG, " callBack==> over");
	}
	
	private void stopCallBack() {
		if(timeoutHandler != null)
			this.timeoutHandler.removeCallbacks(this.timeoutRunnable);
	}
}