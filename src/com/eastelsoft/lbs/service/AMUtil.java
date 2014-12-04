package com.eastelsoft.lbs.service;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class AMUtil {
	private static final String TAG = "AMUtil";
	private static AlarmManager am;
	private static Intent intent;
	private static PendingIntent pendingIntent;
	
	public static void startPendingIntent(Context context) {
		am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		intent = new Intent(context, ScheduleService.class);
		pendingIntent = PendingIntent.getService(context, 0, intent, 0);
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 60000, pendingIntent);
		Log.i(TAG, "TEST============");
	}
}
