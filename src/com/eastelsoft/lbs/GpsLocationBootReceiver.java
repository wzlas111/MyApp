package com.eastelsoft.lbs;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.TelephonyManager;
import android.util.Log;

import com.eastelsoft.lbs.entity.SetInfo;
import com.eastelsoft.lbs.service.LocationService;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.GlobalVar;
import com.eastelsoft.util.IUtil;

/**
 * 随机自动启动
 * 
 * @author lengcj
 */
public class GpsLocationBootReceiver extends BroadcastReceiver {
	//private static final String ACTION = "android.intent.action.BOOT_COMPLETED";
	//private static final String ACTION_REPEATING = "com.eastelsoft.lbs.service.repeating";

	@Override
	public void onReceive(Context context, Intent intent) {
		// GpsLocationActivity gpsLocation=GpsLocationActivity.getInstance();
		// gpsLocation.setup(context);
		Log.i("GpsLocationBootReceiver", "==============" + intent.getAction());
		if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)
				|| intent.getAction().equals("android.intent.action.DATA_STATE")
				|| intent.getAction().equals(Intent.ACTION_PACKAGE_RESTARTED)) {
			Intent i = new Intent(Intent.ACTION_RUN);
			// i.setClass(context, LbsService.class);
			i.setClass(context, LocationService.class);
			context.startService(i);
			
			
			globalVar = (GlobalVar) context.getApplicationContext();
			FileLog.i("kuaile", globalVar.getTimenumber());
			globalVar.setTimenumber(0);
			FileLog.i("kuaile", globalVar.getTimenumber());
			itt = new Intent("com.eastelsoft.lbs.MyTimeReceiver");
			sender = PendingIntent.getBroadcast(context, 0, itt,
					PendingIntent.FLAG_CANCEL_CURRENT);

			this.setAlarmTime(120000,context); // 启动后1分钟执行

		}
	}
	private GlobalVar globalVar;
	private Intent itt;
	private PendingIntent sender;
	private AlarmManager am;
	public void setAlarmTime(long timeInMillis,Context context) {
		if (am != null) {
			am.cancel(sender);
		}
		am = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
		
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
				+ timeInMillis, 600 * 1000, sender);

	}
}
