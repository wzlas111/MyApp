/**
 * Copyright (c) 2013-5-17 www.eastelsoft.com
 * $ID ProcessService.java 下午10:00:16 $
 */
package com.eastelsoft.lbs.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * 
 * 
 * @author lengcj
 */
public class ProcessService extends Service {
	private static final String TAG = "ProcessService";
	public void onCreate() {
		Log.i(TAG, "============> onCreate");
	}

	public void onStart(Intent intent, int startId) {
		Log.i(TAG, "============> onStart");
	}
	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
