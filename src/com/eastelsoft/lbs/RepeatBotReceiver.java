package com.eastelsoft.lbs;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.eastelsoft.lbs.service.LocationService;

public class RepeatBotReceiver extends BroadcastReceiver {
	private static final String ACTION = "com.eastelsoft.lbs.service.repeating";

	@Override
	public void onReceive(Context context, Intent intent) {
		// GpsLocationActivity gpsLocation=GpsLocationActivity.getInstance();
		// gpsLocation.setup(context);
		Log.i("GpsLocationBootReceiver", "==============" + intent.getAction());
		if (intent.getAction().endsWith(ACTION)) {
			Intent i = new Intent(Intent.ACTION_RUN);
			// i.setClass(context, LbsService.class);
			i.setClass(context, LocationService.class);
			context.startService(i);

		}
	}

}
