/**
 * Copyright (c) 2013-5-16 www.eastelsoft.com
 * $ID ScheduleService.java 下午6:55:43 $
 */
package com.eastelsoft.lbs.service;

import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.util.Log;

import com.eastelsoft.lbs.entity.SetInfo;

/**
 * 定位任务调度
 * 
 * @author lengcj
 */
public class ScheduleService extends Service {
	private static final String TAG = "ScheduleService";
	
	private SharedPreferences sp;
	private SetInfo set;

	
	@Override
	public IBinder onBind(Intent arg0) {
		Log.i(TAG, "********************************* ScheduleService onBind");
		return null;
	}
	
	public void onCreate() {
		Log.i(TAG, "********************************* ScheduleService onCreate");
	}
	
	
}
