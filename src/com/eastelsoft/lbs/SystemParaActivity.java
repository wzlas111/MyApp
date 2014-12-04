/**
 * Copyright (c) 2013-6-9 www.eastelsoft.com
 * $ID SystemParaActivity.java 上午11:01:51 $
 */
package com.eastelsoft.lbs;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.entity.SetInfo;
import com.eastelsoft.lbs.service.LocationService;
import com.eastelsoft.lbs.service.LocationService.MBinder;
import com.eastelsoft.util.Contant;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.GlobalVar;
import com.eastelsoft.util.IUtil;
import com.eastelsoft.util.http.AndroidHttpClient;

/**
 * 系统参数
 * 
 * @author lengcj
 */
public class SystemParaActivity extends BaseActivity {

	private static final String TAG = "SystemParaActivity";
	private Button btBack;
	TextView tvSetIP;

	TextView tvTimePeriod;

	TextView tvInterval;

	TextView tvWeek;

	TextView tvFilterDate;

	private Button btRefresh;

	private LocationService locationService;
	private boolean mBound = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_system_para);
		btBack = (Button) findViewById(R.id.btBack);
		btBack.setOnClickListener(new OnBtBackClickListenerImpl());
		tvSetIP = (TextView) findViewById(R.id.set_ip);
		tvTimePeriod = (TextView) findViewById(R.id.timePeriod);
		tvInterval = (TextView) findViewById(R.id.interval);
		tvWeek = (TextView) findViewById(R.id.week);
		tvFilterDate = (TextView) findViewById(R.id.filterDate);

		btRefresh = (Button) findViewById(R.id.img_refresh);
		btRefresh.setOnClickListener(new OnBtRefreshClickListenerImpl());

		globalVar = (GlobalVar) getApplicationContext();

		// 获取系统参数
		sp = getSharedPreferences("userdata", 0);
		serialNumber = sp.getString("serialNumber", "");
		imei = sp.getString("imei", "");
		imsi = sp.getString("imsi", "");
		adapter_ip = sp.getString("adapter_ip", "");
		adapter_port = sp.getString("adapter_port", "10647");
		auth_code = sp.getString("auth_code", "");
		device_id = sp.getString("device_id", "");
		timePeriod = sp.getString("timePeriod", "");
		interval = sp.getString("interval", "10");
		week = sp.getString("week", "0111110");
		filterDate = sp.getString("filterDate", "");
		minDistance = sp.getString("minDistance", "1000");

		tvSetIP.setText(sp.getString("gateip", ""));
		tvTimePeriod.setText(timePeriod);
		tvInterval.setText((Integer.parseInt(interval)) + "秒");
		tvWeek.setText(IUtil.parseWeek(week));
		tvFilterDate.setText(filterDate);

		// 绑定Service，绑定后就会调用mConnetion里的onServiceConnected方法
		Intent intent = new Intent("com.eastelsoft.lbs.service.LocationService");
		this.getApplicationContext().bindService(intent, sc,
				Context.BIND_AUTO_CREATE);

	}

	@Override
	protected void onDestroy() {
		FileLog.i(TAG, "onDestroy....");
		if (mBound) {
			this.getApplicationContext().unbindService(sc);
			mBound = false;
		}
		super.onDestroy();

	}

	private class OnBtRefreshClickListenerImpl implements OnClickListener {

		public void onClick(View v) {
			networkAvailable = isNetworkAvailable();
			if (networkAvailable) {
				if (adapter_ip.length() == 0) {
					respMsg = getResources().getString(
							R.string.check_error_login);
					Toast.makeText(getApplicationContext(), respMsg,
							Toast.LENGTH_SHORT).show();
				} else {

					SystemParaActivity.this.openPopupWindowPG("");
					btPopGps.setText(getResources().getString(
							R.string.loading_set));

					// Thread setThread = new Thread(new SetThread());
					// setThread.start();
					// locationService.refreshSet(tcpCallback);
					Thread dataThread = new Thread(new DataThread());
					dataThread.start();

				}
			} else {
				respMsg = getResources().getString(R.string.net_error);
				Toast.makeText(getApplicationContext(), respMsg,
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	class DataThread implements Runnable {
		@Override
		public void run() {
			Message msg = handler.obtainMessage();
			msg.what = 1;
			try {
				sp = getSharedPreferences("userdata", 0);
				SetInfo set = IUtil.initSetInfo(sp);

				String url = set.getHttpip() + Contant.ACTION;
				Map<String, String> map = new HashMap<String, String>();
				map.put("reqCode", Contant.EAUSER_MENU);
				map.put("gpsid", set.getDevice_id());
				map.put("pin", set.getAuth_code());

				String jsonStr1 = AndroidHttpClient.getContent(url, map);
				FileLog.i(TAG, "jsonStr1==>" + jsonStr1);
				jsonStr1 = IUtil.chkJsonStr(jsonStr1);
				JSONArray array1 = new JSONArray(jsonStr1);
				if (array1.length() > 0) {
					// {"img_num":"5","img_quality":"1","cycle_datetime":"1111111","resultcode":"1",
					// "prequenc":"180","img_px":"1","time_section":"00:00-17:00","leach_date":
					// "20121224","e_menu":"01,02,03,04,05,06,07,08,09,99"}
					JSONObject obj1 = array1.getJSONObject(0);
					String resultcode = obj1.getString("resultcode");
					if ("1".equals(resultcode)) {
						String cycle_datetime = obj1
								.getString("cycle_datetime");
						//移动最后一位与tcp接口统一
						String tp = IUtil.changeWeek(cycle_datetime);
						IUtil.writeSharedPreference(sp, "week", tp);

						String prequenc = obj1.getString("prequenc");
						IUtil.writeSharedPreference(sp, "interval", prequenc);

						String time_section = obj1.getString("time_section");
						IUtil.writeSharedPreference(sp, "timePeriod",
								time_section);
						String leach_date = obj1.getString("leach_date");
						IUtil.writeSharedPreference(sp, "filterDate",
								leach_date);

					}

				}

			} catch (Exception e) {
				msg.what = 0;
			} finally {
				handler.sendMessage(msg);
			}
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try {
				try {
					popupWindowPg.dismiss();
				} catch (Exception e) {
					FileLog.e(TAG, e.toString());
				}
				FileLog.i(TAG, "msg.what==>" + msg);
				FileLog.i(TAG, "msg.what==>" + msg.what);
				switch (msg.what) {
				case 0:
					dialog(SystemParaActivity.this,
							getResources()
									.getString(R.string.set_refresh_error));
					break;
				case 1:
					// 获取系统参数
					sp = getSharedPreferences("userdata", 0);
					
					timePeriod = sp.getString("timePeriod", "");
					interval = sp.getString("interval", "10");
					week = sp.getString("week", "0111110");
					filterDate = sp.getString("filterDate", "");
					
					tvTimePeriod.setText(timePeriod);
					tvInterval.setText((Integer.parseInt(interval)) + "秒");
					tvWeek.setText(IUtil.parseWeek(week));
					tvFilterDate.setText(filterDate);
					//如果有change 要重启定时器
					locationService.dointerval();
					
					String tmpMsg = getResources().getString(
							R.string.set_refrcesh_succ);
					dialog(SystemParaActivity.this, tmpMsg);
					break;
				default:
					respMsg = getResources().getString(
							R.string.set_refresh_error);
					dialog(SystemParaActivity.this, respMsg);
					break;
				}

			} catch (Exception e) {
				// 异常中断
				respMsg = getResources().getString(R.string.set_refresh_error);
				dialog(SystemParaActivity.this, respMsg);
			}
		}
	};

	/** 定交ServiceConnection，用于绑定Service的 */
	private ServiceConnection sc = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			// 已经绑定了LocalService，强转IBinder对象，调用方法得到LocalService对象
			MBinder binder = (MBinder) service;
			locationService = binder.getService();
			mBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
		}
	};

	// private CallBack tcpCallback = new CallBack() {
	// public void execute(Object[] paramArrayOfObject) {
	// Message msg = handler.obtainMessage();
	// msg.what = 0;
	// msg.obj = respMsg;
	// Login2Resp login2Resp = null;
	// respMsg = getResources().getString(R.string.set_refresh_error);
	// if (paramArrayOfObject[0] != null)
	// login2Resp = (Login2Resp) paramArrayOfObject[0];
	// if (login2Resp != null) {
	// if ("0".equals(login2Resp.getRet())
	// || "2".equals(login2Resp.getRet())) {
	// msg.what = 1; // 成功
	// msg.obj = login2Resp;
	// }
	// }
	// handler.sendMessage(msg);
	// }
	// };

	private class OnBtBackClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				SystemParaActivity.this.finish();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

}
