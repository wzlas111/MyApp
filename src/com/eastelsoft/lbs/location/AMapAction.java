/**
 * Copyright (c) 2012-8-22 www.eastelsoft.com
 * $ID AMapAction.java 下午1:27:58 $
 */
package com.eastelsoft.lbs.location;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.Handler;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.location.BaseStationAction.SCell;
import com.eastelsoft.util.CallBack;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.GlobalVar;
import com.eastelsoft.util.MapDistance;
import com.eastelsoft.util.Util;
import com.mapabc.mapapi.location.LocationManagerProxy;
import com.mapabc.mapapi.location.LocationProviderProxy;

/**
 * 基于高德地图的定位
 * 
 * @author lengcj
 */
public class AMapAction {
	private static final String TAG = "AMapAction";
	private CallBack callBack;
	private Context context;
	private GlobalVar globalVar;
	private String reportTag = "";
	private LocationManagerProxy locationManager = null;
	private LocationListener locationListener;
	private static final long mLocationUpdateMinTime = 1000;
	private static final float mLocationUpdateMinDistance = 10;
	
	private Location currentLocation = null;

	private Handler timeoutHandler = new Handler();
	private Runnable timeoutRunnable = new Runnable() {
		public void run() {
			AMapAction.this.stopListener();
			AMapAction.this.stopCallBack();
			//AMapAction.this.sendLocation(null);
			AMapAction.this.sendLocation(currentLocation);
			FileLog.i(TAG, "location timeout");
		}
	};
	
	public AMapAction(Context context, CallBack callBack, String reportTag) {
		this.context = context.getApplicationContext();
		this.callBack = callBack;
		this.reportTag = reportTag;
		this.globalVar = (GlobalVar)context.getApplicationContext();
		
		SCell cell = new BaseStationAction(context).location2();
		try {
			// 临时增加处理吴堡点
			if(cell != null && cell.LAC==48005 && cell.CID==204292063) {
				Location tmpLocation = new Location("gps");
				tmpLocation.setLongitude(110.728948);
				tmpLocation.setLatitude(37.451179);
				tmpLocation.setAccuracy(99);
				if("0".equals(reportTag) || "1".equals(reportTag)) {
					Intent it = new Intent("android.location.action");
					String location_desc = "位置经度"
							+ Util.format(tmpLocation.getLongitude(), "#.######")
							+ "，纬度"
							+ Util.format(tmpLocation.getLatitude(), "#.######")
							+ "，精度" + tmpLocation.getAccuracy() + "米";
					it.putExtra("location_desc", location_desc);
					context.sendBroadcast(it);
				}
				currentLocation = tmpLocation;
				//globalVar.setLocation(tmpLocation);
				//globalVar.setLocationtime(System.currentTimeMillis());
				FileLog.i("TAG", "tmpLocation = " + tmpLocation);
			
			}
		}catch(Exception e) {
		}
	}

	public void startListener() {
		try {
			//locationManager = LocationManagerProxy.getInstance(context);
			locationManager =LocationManagerProxy.getInstance(context, context.getResources().getString(R.string.maps_api_key));
			gpsLocation();
			this.timeoutHandler.postDelayed(this.timeoutRunnable, 15000L);
		} catch (Exception e) {

		}
	}

	public void gpsLocation() {
		FileLog.i(TAG, "Start locationListener==>" + locationListener);
		// 启动监听
		locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				if (location != null) {
					
					
						FileLog.i(TAG, "location==>" + location);
						//if(location.getAccuracy() < 300) {
							// 停止监听，发送消息
						//	AMapAction.this.stopListener();
						//	AMapAction.this.stopCallBack();
						//	AMapAction.this.sendLocation(location);
						//}
						if("0".equals(reportTag) || "1".equals(reportTag)) {
							Intent it = new Intent("android.location.action");
							String location_desc = "位置经度"
									+ Util.format(location.getLongitude(), "#.######")
									+ "，纬度"
									+ Util.format(location.getLatitude(), "#.######")
									+ "，精度" + location.getAccuracy() + "米";
							it.putExtra("location_desc", location_desc);
							context.sendBroadcast(it);
						}
						
						// 飞点判断
						boolean isNeedUpload = true;
						// 定位位置的飞点判断
						FileLog.i(TAG, "globalVar==>" + globalVar);
						Location lastLocation = globalVar.getLocation();
						long lastLocationTime = globalVar.getLocationtime();
						FileLog.i(TAG, "lastLoaction==>" + lastLocation);
						FileLog.i(TAG, "lastLocationTime==>" + lastLocationTime);
						long currentLocationTime = System.currentTimeMillis();
						if(lastLocation != null && lastLocationTime > 0) {
							// 30分钟时间间隔内，如果距离超过300公里
							double dist = MapDistance.getDistance(location.getLatitude(),
									location.getLongitude(), lastLocation.getLatitude()
									, lastLocation.getLongitude());
							FileLog.i(TAG, "MapDistance dist==>" + dist);
							FileLog.i(TAG, "MapDistance time==>" + (currentLocationTime - lastLocationTime));
							// 10分钟之内，50公里，10-20分钟100公里，20-30分钟200公里，30-60分钟800公里；1小时以上不算
							if((currentLocationTime - lastLocationTime) <= 10*60*1000) {
								if(dist >= 50) {
									// 飞点不上传
									isNeedUpload = false;
								}
							}
							if((currentLocationTime - lastLocationTime) > 10*60*1000 && 
									(currentLocationTime - lastLocationTime) <= 20*60*1000) {
								if(dist >= 100) {
									// 飞点不上传
									isNeedUpload = false;
								}
							}
							if((currentLocationTime - lastLocationTime) > 20*60*1000 && 
									(currentLocationTime - lastLocationTime) <= 30*60*1000) {
								if(dist >= 200) {
									// 飞点不上传
									isNeedUpload = false;
								}
							}
							if((currentLocationTime - lastLocationTime) > 30*60*1000 &&
									(currentLocationTime - lastLocationTime) < 60*60*1000) {
								if(dist >= 800) {
									// 飞点不上传
									isNeedUpload = false;
								}
							}
						}
						if(isNeedUpload) {
							currentLocation = location;
							globalVar.setLocation(location);
							globalVar.setLocationtime(System.currentTimeMillis());
						} else {
							currentLocation = null;
						}
						
					}
					

				
			}

			public void onProviderDisabled(String paramString) {
				FileLog.i(TAG, "onProviderDisabled==>" + paramString);
			}

			public void onProviderEnabled(String paramString) {
				FileLog.i(TAG, "onProviderEnabled==>" + paramString);
			}

			public void onStatusChanged(String paramString, int paramInt,
					Bundle paramBundle) {
				FileLog.i(TAG, "onStatusChanged==>" + paramString);
			}
		};
		/*Criteria cri = new Criteria();
		cri.setAccuracy(Criteria.ACCURACY_COARSE);
		cri.setAltitudeRequired(false);
		cri.setBearingRequired(false);
		cri.setCostAllowed(false);
		String bestProvider = locationManager.getBestProvider(cri, true);
		FileLog.i(TAG,  "bestProvider"+bestProvider);
		//String provider = LocationProviderProxy.MapABCNetwork;
		locationManager.requestLocationUpdates(bestProvider,
				mLocationUpdateMinTime, mLocationUpdateMinDistance,
				locationListener);*/
		
		for (final String provider : locationManager.getProviders(true)) {
	     	if ("gps".equals(provider)||LocationProviderProxy.MapABCNetwork.equals(provider)) {
			//	if (LocationManagerProxy.NETWORK_PROVIDER.equals(provider)) {
	     		FileLog.i(TAG,  provider);	
	     		locationManager.requestLocationUpdates(provider, mLocationUpdateMinTime, mLocationUpdateMinDistance,
	     				locationListener);
	     		
			}
		}
		// 定位15秒
		this.timeoutHandler.postDelayed(this.timeoutRunnable, 15000L);
	}

	private void stopListener() {
		FileLog.i(TAG, " Stop locationListener==>" + locationListener);
		if (locationManager != null && locationListener != null) {
			this.locationManager.removeUpdates(this.locationListener);
			this.locationManager.destory();
		}
		if (timeoutHandler != null)
			this.timeoutHandler.removeCallbacks(this.timeoutRunnable);
		locationManager = null;
	}

	private void sendLocation(Location location) {
		// 回调请求数据
		CallBack localCallBack = this.callBack;
		Object[] arrayOfObject = new Object[2];
//		if(location == null) {
//			location = new Location("gps");
//		}

		arrayOfObject[0] = location;
		arrayOfObject[1] = reportTag;
		localCallBack.execute(arrayOfObject);
		FileLog.i(TAG, " callBack==> over");
	}

	private void stopCallBack() {
		if (timeoutHandler != null)
			this.timeoutHandler.removeCallbacks(this.timeoutRunnable);
	}
}
