package com.eastelsoft.lbs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.eastelsoft.lbs.service.LocationService;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.GlobalVar;

public class MyTimeReceiver extends BroadcastReceiver {
	private static final String ACTION = "com.eastelsoft.lbs.MyTimeReceiver";
	private GlobalVar globalVar;

	@Override
	public void onReceive(Context context, Intent intent) {
		// GpsLocationActivity gpsLocation=GpsLocationActivity.getInstance();
		// gpsLocation.setup(context);
		if (intent.getAction().endsWith(ACTION)) {
			globalVar = (GlobalVar) context.getApplicationContext();
			FileLog.i("kuaile", System.currentTimeMillis());
			// Intent i = new Intent(Intent.ACTION_RUN);
			// // i.setClass(context, LbsService.class);
			// i.setClass(context, LocationService.class);
			// context.startService(i);
			if (globalVar.getTimenumber() != 0) {
				FileLog.i("kuaile", "startpp");
				Intent i = new Intent(Intent.ACTION_RUN);
				i.setClass(context, LocationService.class);
				context.startService(i);
				

			}
			int sm = globalVar.getTimenumber();
			sm++;
			globalVar.setTimenumber(sm);

		}
	}

}
