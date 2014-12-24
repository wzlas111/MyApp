package com.eastelsoft.lbs.receiver;

import com.eastelsoft.lbs.service.AutoUploadService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class AutoUploadReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Intent service = new Intent();
		service.setClass(context, AutoUploadService.class);
		context.startService(service);
	}

}
