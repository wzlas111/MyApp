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
public class BaiduMapAction {
	private static final String TAG = "BaiduMapAction";
	private CallBack callBack;
	private Context context;
//	private GlobalVar globalVar;
	private String reportTag = "";
	private boolean isSend = false;
	private LocationClient mLocationClient = null;
//  private BDLocation currentLocation = null;
	private Location currentBaiduLocation = null;
	private int locationTimes = 0;
	private Handler timeoutHandler = new Handler();
	private Runnable timeoutRunnable = new Runnable(){
		public void run() {
			BaiduMapAction.this.stopListener();
			BaiduMapAction.this.stopCallBack();
			BaiduMapAction.this.sendLocation(currentBaiduLocation);
			FileLog.i(TAG, "location timeout");
		}
	};

	public BaiduMapAction(Context context, CallBack callBack, String reportTag) {
		this.context = context.getApplicationContext();
		this.callBack = callBack;
		this.reportTag = reportTag;
//		this.globalVar = (GlobalVar) context.getApplicationContext();
		FileLog.i(TAG, "BaiduMapAction 构造器");
	}

	public void startListener() {
		try {
			mLocationClient = new LocationClient(context);
//			mLocationClient.setAK(Contant.STRKEY);
			LocationClientOption option = new LocationClientOption();
	        option.setOpenGps(true);
	        option.setCoorType("bd09ll");//返回的定位结果是百度经纬度,默认值gcj02
	        option.setScanSpan(2000);//设置发起定位请求的间隔时间为2000ms
	        option.setIsNeedAddress(true);
	        option.setProdName("eastelsoftwqt");
	        mLocationClient.setLocOption(option);
	        mLocationClient.registerLocationListener(new BDLocationListener() {
				@Override
				public void onReceiveLocation(BDLocation location) {
					if (location != null) {
						FileLog.i(TAG, "BDlocation==>" + location);
						FileLog.i(TAG, "BDlocation==>" + location.getLocType());
						if(location.getLocType()!=68&&location.getLocType()!=65){
							//if(location.getRadius() > 300)
							locationTimes++;
							currentBaiduLocation = new Location("lbs");
							currentBaiduLocation.setProvider("lbs");
							currentBaiduLocation.setAccuracy(location.getRadius());
							currentBaiduLocation.setLongitude(location.getLongitude() - 0.0065f);
							currentBaiduLocation.setLatitude(location.getLatitude() - 0.006f);
							//增加时间2013-07-10 13:13:05
							SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							long timeStart = 0;
							try {
								timeStart=sdf.parse(location.getTime()).getTime();
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							currentBaiduLocation.setTime(timeStart);
							
							//BaiduMapAction.this.sendBroadcast(currentBaiduLocation, "lbs");
							if("-1".equals(reportTag) || currentBaiduLocation.getAccuracy()<= 100){
								// gps定位未成功，且高德定位获取到精度小于100的数据时，直接返回高德数据
								BaiduMapAction.this.stopListener();
								BaiduMapAction.this.stopCallBack();
								currentBaiduLocation.setAccuracy(-location.getRadius());
								BaiduMapAction.this.sendLocation(currentBaiduLocation);
								FileLog.i(TAG, " baidulocation ==> over");
								isSend = true;
							}
							if("2".equals(reportTag)) {
								// 信息上报等获取位置
								FileLog.i(TAG, "location addr============>" + location.getAddrStr());
								Bundle b = new Bundle();
								b.putString("desc", location.getAddrStr());
								currentBaiduLocation.setExtras(b);
								BaiduMapAction.this.stopListener();
								BaiduMapAction.this.stopCallBack();
								BaiduMapAction.this.sendLocation(currentBaiduLocation);
							}		
							currentBaiduLocation.setAccuracy(-location.getRadius());
						}
						
					}
					FileLog.i(TAG, "currentBaiduLocation==>" + currentBaiduLocation);
				}
				
		        public void onReceivePoi(BDLocation location){
		        	//return ;
		        }
			});
	        this.timeoutHandler.postDelayed(this.timeoutRunnable, 15 * 1000);
	        mLocationClient.start();
		} catch (Exception e) {

		}
	}

	private void stopListener() {
		if (mLocationClient != null && mLocationClient.isStarted()) {
			mLocationClient.stop();
			mLocationClient = null;
		}
	}

	private void sendLocation(Location location) {
		if (!isSend) { // 控制重复调用
			if ("".equals(reportTag) && locationTimes == 1
					&& location.getAccuracy() > 300) {
				// 定位失败
				FileLog.i(TAG, " BaiduLocation ==> 只得到一次数据，返回重新定位");
				location = null;
			}
			// 回调请求数据
			CallBack localCallBack = this.callBack;
			Object[] arrayOfObject = new Object[2];
			arrayOfObject[0] = location;
			arrayOfObject[1] = reportTag;
			localCallBack.execute(arrayOfObject);
			FileLog.i(TAG, " BaiduLocation callBack==> over");
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
	public void sendBroadcast(Location location, String provider) {
		try {
			if ("0".equals(reportTag) || "1".equals(reportTag)) {
				Intent it = new Intent("android.location.action");
				String location_desc = "经度"
						+ Util.format(location.getLongitude(), "#.######")
						+ "，纬度"
						+ Util.format(location.getLatitude(), "#.######")
						+ "，精度" + location.getAccuracy() + "米";
				it.putExtra("location_desc", location_desc);
				it.putExtra("provider", provider);
				context.sendBroadcast(it);
			}
		} catch (Exception e) {
			FileLog.e(TAG, e + "");
		}
	}
}
