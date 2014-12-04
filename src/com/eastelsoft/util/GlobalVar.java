/**
 * Copyright (c) 2012-7-21 www.eastelsoft.com
 * $ID GlobalVar.java 上午12:42:13 $
 */
package com.eastelsoft.util;

import java.util.Arrays;
import java.util.LinkedList;
import com.baidu.mapapi.SDKInitializer;
import android.app.Application;
import android.content.res.Configuration;
import android.location.Location;
import android.util.Log;

/**
 * 全局变量
 * @author lengcj
 */
public class GlobalVar extends Application {
	public static final String TAG="GlobalVar";
	private int msg_seq = 1;
	
	private long locationtime = 0;
	
	private Location location = null;
	//临时定位地址存储，以便用来校正gps偏差明显的错误
	private Location location_correct =null;
	
	private int satellitesCount = 0;
	
	private boolean gpsIsRunning = false;
	
	private boolean gpsIsFirst = true; // 默认第一次
	
	private long gpsFirstTime = 0;
	
	private LinkedList<byte[]> sendList = new LinkedList<byte[]>();
	
	private Location infoLocation = null;
	
	private String imgFileName = "";
	
	private String imgFileName1 = "";
	
	private String imgFileName2 = "";
	
	private String imgFileName3 = "";
	
	private String[] imgs = new String[0];
	private String reSoundName;
	private int idle;
	private String video1;
	private String title= "";
	private String remark= "";
	
	private int timenumber= 0;
	
	public int getMsg_seq() {
		return msg_seq;
	}

	public void setMsg_seq(int msg_seq) {
		this.msg_seq = msg_seq;
	}

	@Override
	public String toString() {
		return "GlobalVar [msg_seq=" + msg_seq + ", locationtime="
				+ locationtime + ", location=" + location
				+ ", satellitesCount=" + satellitesCount + ", gpsIsRunning="
				+ gpsIsRunning + ", gpsIsFirst=" + gpsIsFirst
				+ ", gpsFirstTime=" + gpsFirstTime + ", sendList=" + sendList
				+ ", infoLocation=" + infoLocation + ", imgFileName="
				+ imgFileName + ", imgFileName1=" + imgFileName1
				+ ", imgFileName2=" + imgFileName2 + ", imgFileName3="
				+ imgFileName3 + ", imgs=" + Arrays.toString(imgs) + ", idle="
				+ idle + ", video1=" + video1 +", reSoundName"+reSoundName+"]";
	}

	

	public long getLocationtime() {
		return locationtime;
	}

	public void setLocationtime(long locationtime) {
		this.locationtime = locationtime;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public int getSatellitesCount() {
		return satellitesCount;
	}

	public void setSatellitesCount(int satellitesCount) {
		this.satellitesCount = satellitesCount;
	}

	public LinkedList<byte[]> getSendList() {
		return sendList;
	}

	public void setSendList(LinkedList<byte[]> sendList) {
		this.sendList = sendList;
	}

	public boolean isGpsIsRunning() {
		return gpsIsRunning;
	}

	public void setGpsIsRunning(boolean gpsIsRunning) {
		this.gpsIsRunning = gpsIsRunning;
	}

	public boolean isGpsIsFirst() {
		return gpsIsFirst;
	}

	public void setGpsIsFirst(boolean gpsIsFirst) {
		this.gpsIsFirst = gpsIsFirst;
	}

	public long getGpsFirstTime() {
		return gpsFirstTime;
	}

	public void setGpsFirstTime(long gpsFirstTime) {
		this.gpsFirstTime = gpsFirstTime;
	}

	public Location getInfoLocation() {
		return infoLocation;
	}

	public void setInfoLocation(Location infoLocation) {
		this.infoLocation = infoLocation;
	}
	public String getImgFileName1() {
		return imgFileName1;
	}

	public void setImgFileName1(String imgFileName1) {
		this.imgFileName1 = imgFileName1;
	}

	public String getImgFileName2() {
		return imgFileName2;
	}

	public void setImgFileName2(String imgFileName2) {
		this.imgFileName2 = imgFileName2;
	}

	public String getImgFileName3() {
		return imgFileName3;
	}

	public void setImgFileName3(String imgFileName3) {
		this.imgFileName3 = imgFileName3;
	}

	public int getIdle() {
		return idle;
	}

	public void setIdle(int idle) {
		this.idle = idle;
	}

	public String getVideo1() {
		return video1;
	}

	public void setVideo1(String video1) {
		this.video1 = video1;
	}

	public String[] getImgs() {
		return imgs;
	}

	public void setImgs(String[] imgs) {
		this.imgs = imgs;
	}
	
	
	public String getReSoundName() {
		return reSoundName;
	}

	public void setReSoundName(String reSoundName) {
		this.reSoundName = reSoundName;
	}
	private static GlobalVar mInstance = null;
    public boolean m_bKeyRight = true;
    public static final String strKey = Contant.STRKEY;
    @Override
    public void onCreate() {
    	Log.i(TAG, "onCreate");
	    super.onCreate();
		mInstance = this;
		SDKInitializer.initialize(this);
	}
    
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onLowMemory() {
		// TODO Auto-generated method stub
		Log.i(TAG, "onLowMemory");
		super.onLowMemory();
	}

	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		super.onTerminate();
	}

	@Override
	public void onTrimMemory(int level) {
		// TODO Auto-generated method stub
		if(level>20){
			Log.i(TAG," onTrimMemory ... level:" + level);
		System.gc();
		}
		super.onTrimMemory(level);
	}
	public static GlobalVar getInstance() {
		return mInstance;
	}
	
	
	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}


	public String getRemark() {
		return remark;
	}

	public void setRemark(String remark) {
		this.remark = remark;
	}


	public int getTimenumber() {
		return timenumber;
	}

	public void setTimenumber(int timenumber) {
		this.timenumber = timenumber;
	}

	public Location getLocation_correct() {
		return location_correct;
	}

	public void setLocation_correct(Location location_correct) {
		this.location_correct = location_correct;
	}
	
}
