package com.eastelsoft.lbs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.mapapi.map.BaiduMap;

import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MyLocationData;
import com.baidu.mapapi.model.LatLng;
import com.eastelsoft.lbs.activity.PoiSearchDemo;
import com.eastelsoft.lbs.activity.RoutePlanDemo;
import com.eastelsoft.lbs.db.DBUtil;
import com.eastelsoft.lbs.db.LocationSQLiteHelper;
import com.eastelsoft.lbs.entity.ReportResp;
import com.eastelsoft.lbs.entity.SetInfo;
import com.eastelsoft.lbs.location.BaiduMapActionForCheckin;
import com.eastelsoft.lbs.location.BaseStationAction.SItude;
import com.eastelsoft.lbs.service.LocationService;
import com.eastelsoft.lbs.service.LocationService.MBinder;
import com.eastelsoft.util.CallBack;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.IUtil;
import com.eastelsoft.util.Util;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

public class CheckinOnBaiduMapActivity extends Activity {
	private static final String TAG = "CheckinOnBaiduMapActivity";
	private Button btOK;
	private Button btOutOK;
	private Button btBack;
	private TextView tvCheckTitle;
	protected SharedPreferences sp;
	private SetInfo set;
	private Intent intent;
	private String reportTag;
	protected boolean networkAvailable;
	protected String respMsg;
	private LocationService locationService;
	private boolean mBound = false;
	private String msg_seq = "";
	private LocationSQLiteHelper locationHelper;
	private BaiduMapActionForCheckin bmafc;
	private Button bt_checkhistroy;
	private Button bt_lookaround;
	private Button bt_pathplanning;
	private Button img_refresh;
	 MapView mapView = null;
	 BaiduMap mBaiduMap = null;
	LocationClient mLocClient;
	MyLocationData.Builder locData = null;
	boolean isFirstLoc = true;
	public MyLocationListenner myListener = new MyLocationListenner();
	public LocationClientOption option;
	private double lon;
	private double lat;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_checkinonmap);
		MapStatusUpdate u1 = MapStatusUpdateFactory.zoomTo(18);
		mapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap=mapView.getMap();
		mBaiduMap.setMapStatus(u1);
		btBack = (Button) findViewById(R.id.btBack);
		btBack.setOnClickListener(new OnBtBackClickListenerImpl());
		btOK = (Button) findViewById(R.id.btOK);
		btOutOK = (Button) findViewById(R.id.btOutOK);
		btOK.setOnClickListener(new OnBtCheckClickListenerImpl("1"));
		btOutOK.setOnClickListener(new OnBtCheckClickListenerImpl("0"));
		tvCheckTitle = (TextView) findViewById(R.id.tvCheckTitle);
		// bottom按钮处理
		img_refresh = (Button) findViewById(R.id.img_refresh);
		img_refresh.setOnClickListener(new OnBtrefleshClickListenerImpl());
		bt_checkhistroy = (Button) findViewById(R.id.bt_checkhistroy);
		bt_checkhistroy.setOnClickListener(new OnBthistroyClickListenerImpl());
		bt_lookaround = (Button) findViewById(R.id.bt_lookaround);
		bt_lookaround.setOnClickListener(new OnBtlookaroundClickListenerImpl());
		bt_pathplanning = (Button) findViewById(R.id.bt_pathplanning);
		bt_pathplanning.setOnClickListener(new OnBtbt_pathplanningClickListenerImpl());
		sp = getSharedPreferences("userdata", 0);
		set = IUtil.initSetInfo(sp);
		locationHelper = new LocationSQLiteHelper(this, null, null, 5);
		intent = this.getIntent();
		reportTag = intent.getStringExtra("reportTag");
		String checkTitle = "";
		String ckeckhis = "";
		if ("1".equals(reportTag)){
			btOK.setVisibility(View.VISIBLE);
			btOutOK.setVisibility(View.GONE);
			checkTitle = getResources().getString(
					R.string.title_activity_checkin);
			tvCheckTitle.setText(checkTitle);
			ckeckhis = getResources().getString(
					R.string.title_activity_inhistroy);
			bt_checkhistroy.setText(ckeckhis);
		} else {
			btOK.setVisibility(View.GONE);
			btOutOK.setVisibility(View.VISIBLE);
			checkTitle = getResources().getString(
					R.string.title_activity_checkout);
			tvCheckTitle.setText(checkTitle);
			ckeckhis = getResources().getString(
					R.string.title_activity_outhistroy);
			bt_checkhistroy.setText(ckeckhis);
		}
		// 业务分发
		if (set.getSerialNumber().length() == 0){
			// 初次安装或者系统参数丢失，进入注册页面，开始或者重新注册
			Intent intent = new Intent();
			intent.setClass(CheckinOnBaiduMapActivity.this, RegActivity.class);
			startActivity(intent);
			// 停止当前的Activity,如果不写,则按返回键会跳转回原来的Activity
			finish();
		} else {

			networkAvailable = isNetworkAvailable();

			if (!networkAvailable) {
				respMsg = getResources().getString(R.string.net_error);
				Toast.makeText(getApplicationContext(), respMsg,
						Toast.LENGTH_SHORT).show();
			}

			this.startService(new Intent(this, LocationService.class));
			Intent intent = new Intent(
					"com.eastelsoft.lbs.service.LocationService");
			this.getApplicationContext().bindService(intent, sc,
					Context.BIND_AUTO_CREATE);
		}

		try {
			// 地图初始化
			mBaiduMap.setMyLocationEnabled(true);
			mLocClient = new LocationClient(this);
			mLocClient.registerLocationListener(myListener);
			option = new LocationClientOption();
			option.setOpenGps(true);// 打开gps
			option.setCoorType("bd09ll"); // 设置坐标类型
			option.setScanSpan(20000);// 设置发起定位请求的间隔时间为2000ms
			option.setIsNeedAddress(true);
			option.setProdName("eastelsoftwqt");
			mLocClient.setLocOption(option);
			mLocClient.start();
			// createPaopao();
		} catch (Exception e) {
			e.printStackTrace();
			FileLog.i(TAG, "地图初始化异常");
		}
	}

	/**
	 * 检测网络连接状态
	 * 
	 * @return
	 */
	public boolean isNetworkAvailable() {
		Context context = getApplicationContext();
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			Log.e("NetWork available", "getSystemService rend null");
		} else {// 获取所有网络连接信息
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {// 逐一查找状态为已连接的网络
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}

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

	private class OnBtBackClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				CheckinOnBaiduMapActivity.this.finish();
				// openPopupWindow();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	/**
	 * 签到签退按钮时间
	 */
	protected PopupWindow popupWindowPg;
	protected TextView btPopGps;
	private Thread checkThread;
	private Location currentBaiduLocation = null;

	private class OnBtCheckClickListenerImpl implements OnClickListener {
		private String reportTag;

		public OnBtCheckClickListenerImpl(String reportTag) {
			this.reportTag = reportTag;
		}

		public void onClick(View v) {

			try {

				if (currentBaiduLocation != null) {

					// 检测网络连接情况
					networkAvailable = isNetworkAvailable();
					if (!networkAvailable) {
						respMsg = getResources().getString(R.string.net_error);
						Toast.makeText(getApplicationContext(), respMsg,
								Toast.LENGTH_SHORT).show();
					} else {
						if (set.getAdapter_ip().length() == 0) {
							respMsg = getResources().getString(
									R.string.check_error_login);
							Toast.makeText(getApplicationContext(), respMsg,
									Toast.LENGTH_SHORT).show();
						} else {
							CheckinOnBaiduMapActivity.this
									.openPopupWindowPG("");
							checkThread = new Thread(new CheckThread(reportTag));
							checkThread.start();
						}
					}

				} else {
					Toast.makeText(CheckinOnBaiduMapActivity.this,
							getString(R.string.tip_waitmoment),
							Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				FileLog.e("TabCheckinActivity checkin", "error");
			}

		}

	}

	private class OnBthistroyClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {

				intent = new Intent(CheckinOnBaiduMapActivity.this,
						CheckinHistoryActivity.class);
				intent.putExtra("reportTag", reportTag);
				startActivity(intent);

			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	class CheckThread implements Runnable {
		private String reportTag;

		public CheckThread(String reportTag) {
			this.reportTag = reportTag;
		}

		@Override
		public void run() {
			Looper.prepare();
			locationService.startListener(reportTag, locationCallback,
					currentBaiduLocation);
			Looper.loop();
		}
	}

	protected void openPopupWindowPG(String msg) {
		try {
			LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			ViewGroup menuView = (ViewGroup) mLayoutInflater.inflate(
					R.layout.pop_gps, null, true);
			popupWindowPg = new PopupWindow(menuView, LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT, true); // 全部背景置灰
			btPopGps = (TextView) menuView.findViewById(R.id.pop_gps);
			popupWindowPg.setBackgroundDrawable(new BitmapDrawable());
			popupWindowPg.setAnimationStyle(R.style.PopupAnimation);
			popupWindowPg.showAtLocation(findViewById(R.id.parent),
					Gravity.CENTER | Gravity.CENTER, 0, 0);
			popupWindowPg.update();
		} catch (Exception e) {
		}
	}

	private CallBack locationCallback = new CallBack() {
		public void execute(Object[] paramArrayOfObject) {
			Message msg = handler.obtainMessage();
			msg.what = 2;

			Location location = null;
			SItude sItude = null;

			if (paramArrayOfObject[0] != null) {
				try {
					location = (Location) paramArrayOfObject[0];
				} catch (Exception e) {
					sItude = (SItude) paramArrayOfObject[0];
				}
			}

			ReportResp reportResp = null;
			if (paramArrayOfObject[1] != null)
				reportResp = (ReportResp) paramArrayOfObject[1];
			String reportTag = (String) paramArrayOfObject[2];
			String isSocketOk = (String) paramArrayOfObject[3];
			if (location == null && sItude == null) {
				if (isSocketOk.equals("socketisgood")) {
					respMsg = getResources().getString(R.string.location_error);

				} else {
					respMsg = getResources().getString(R.string.socketnotready);
				}

			} else {
				if (reportResp != null) {
					if ("0".equals(reportResp.getRet())
							|| "2".equals(reportResp.getRet())) {
						if ("1".equals(reportTag)) {
							respMsg = getResources().getString(
									R.string.checkin_succ);
						} else {
							respMsg = getResources().getString(
									R.string.checkout_succ);
						}
						// 写本地数据库
						if (location != null) {
							float tmp = location.getAccuracy();
							float tmpend = tmp;
							if (tmp < 0)
								tmpend = -tmp;
							location.setAccuracy(tmpend);

							SimpleDateFormat sdf = new SimpleDateFormat(
									"yyyy-MM-dd HH:mm:ss");
							Date dt = new Date(location.getTime());
							String sDateTime = sdf.format(dt);
							DBUtil.insertLLoc(
									locationHelper.getWritableDatabase(),
									sDateTime,
									reportTag,
									Util.format(location.getLongitude(),
											"#.######"),
									Util.format(location.getLatitude(),
											"#.######"),
									location.getExtras().getString("desc"),
									Util.format(String.valueOf(location
											.getAccuracy())) + "米");
						}
						if (sItude != null) {
							DBUtil.insertLLoc(
									locationHelper.getWritableDatabase(),
									Util.getLocaleTime("yyyy-MM-dd HH:mm:ss"),
									reportTag,
									Util.format(Double
											.parseDouble(sItude.longitude),
											"#.######"),
									Util.format(
											Double.parseDouble(sItude.latitude),
											"#.######"), "",
									Util.format(sItude.accuracy) + "米");
						}
					} else {
						if ("1".equals(reportTag))
							respMsg = getResources().getString(
									R.string.checkin_error);
						else
							respMsg = getResources().getString(
									R.string.checkout_error);
					}
				} else {
					if ("1".equals(reportTag))
						respMsg = getResources().getString(
								R.string.checkin_error);
					else
						respMsg = getResources().getString(
								R.string.checkout_error);
				}
			}
			msg.obj = respMsg;
			handler.sendMessage(msg);
		}
	};

	private class OnBtlookaroundClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {

				if (currentBaiduLocation != null) {
					// CheckinOnMapActivity.this.finish();

					if (mLocClient != null) {
						mLocClient.unRegisterLocationListener(myListener);
						mLocClient.stop();
					}
					intent = new Intent(CheckinOnBaiduMapActivity.this,
							PoiSearchDemo.class);
					intent.putExtra("city", currentBaiduLocation.getExtras()
							.getString("city"));
					intent.putExtra("lon", Util.format(
							lon, "#.########"));
					intent.putExtra("lat", Util.format(
							lat, "#.########"));
					startActivity(intent);

				} else {
					Toast.makeText(CheckinOnBaiduMapActivity.this,
							getString(R.string.tip_waitmoment),
							Toast.LENGTH_SHORT).show();
				}

			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	private class OnBtbt_pathplanningClickListenerImpl implements
			OnClickListener {
		public void onClick(View v) {
			try {

				if (currentBaiduLocation != null) {
					if (mLocClient != null) {
						mLocClient.unRegisterLocationListener(myListener);
						mLocClient.stop();
					}

					intent = new Intent(CheckinOnBaiduMapActivity.this,
							RoutePlanDemo.class);
					intent.putExtra("city", currentBaiduLocation.getExtras()
							.getString("city"));
					intent.putExtra("desc", currentBaiduLocation.getExtras()
							.getString("desc"));
					intent.putExtra("lon", Util.format(
							lon, "#.########"));
					intent.putExtra("lat", Util.format(
							lat, "#.########"));
					startActivity(intent);
				} else {
					Toast.makeText(CheckinOnBaiduMapActivity.this,
							getString(R.string.tip_waitmoment),
							Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	// 刷新重新定位
	private class OnBtrefleshClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
				mLocClient.stop();
				mLocClient.start();
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
				// IUtil.writeSharedPreference(sp, "chkact", "0");
				switch (msg.what) {
				case 2:
					dialog(CheckinOnBaiduMapActivity.this, msg.obj.toString());
					break;
				case 1:
					// 异常转回注册页面
					Intent intent = new Intent();
					intent.setClass(CheckinOnBaiduMapActivity.this,
							RegActivity.class);
					startActivity(intent);
					finish();
					break;
				case 0:
					// 登录成功
					break;
				default:
					respMsg = getResources().getString(R.string.checkin_error);
					dialog(CheckinOnBaiduMapActivity.this, respMsg);
					break;
				}

			} catch (Exception e) {
				// 异常中断
				respMsg = getResources().getString(R.string.checkin_error);
				dialog(CheckinOnBaiduMapActivity.this, respMsg);
			}
		}
	};

	protected void dialog(Context context, String text) {
		openPopupWindow(text);
	}

	protected Button btClose;
	protected Button btClose1;
	protected TextView btPopText;
	protected PopupWindow popupWindow;

	protected void openPopupWindow(String msg) {
		try {
			LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			ViewGroup menuView = (ViewGroup) mLayoutInflater.inflate(
					R.layout.pop_comm, null, true);
			btClose = (Button) menuView.findViewById(R.id.btClose);
			btClose.setOnClickListener(new OnBtCloseClickListenerImpl());
			btClose1 = (Button) menuView.findViewById(R.id.btClose1);
			btClose1.setOnClickListener(new OnBtCloseClickListenerImpl());
			btPopText = (TextView) menuView.findViewById(R.id.btPopText);
			btPopText.setText(msg);
			popupWindow = new PopupWindow(menuView, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT, true); // 背景
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
			popupWindow.setAnimationStyle(R.style.PopupAnimation);
			popupWindow.showAtLocation(findViewById(R.id.parent),
					Gravity.CENTER | Gravity.CENTER, 0, 0);
			popupWindow.update();
		}catch (Exception e) {
		}

	}

	public class MyLocationListenner implements BDLocationListener {

		@Override
		public void onReceiveLocation(BDLocation location) {
			// map view 销毁后不在处理新接收的位置
			if (location == null || mapView == null)
				return;
			MyLocationData locData = new MyLocationData.Builder()
					.accuracy(location.getRadius())
					// 此处设置开发者获取到的方向信息，顺时针0-360
					.direction(100).latitude(location.getLatitude())
					.longitude(location.getLongitude()).build();
			mBaiduMap.setMyLocationData(locData);
			LatLng ll = new LatLng(location.getLatitude(),location.getLongitude());
			MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(ll);
			mBaiduMap.animateMapStatus(u);
			/*******************************************************************/
			Location currentBaiduLocationtemp = new Location("lbs");

			currentBaiduLocationtemp.setProvider("lbs");
			currentBaiduLocationtemp.setAccuracy(location.getRadius());
			// 高德数据与百度数据---经度相差0.0065f---纬度相差0.006f
			lon = location.getLongitude();
			lat = location.getLatitude();
			currentBaiduLocationtemp.setLongitude(lon - 0.0065f);
			currentBaiduLocationtemp.setLatitude(lat - 0.006f);

			// currentBaiduLocationtemp
			// .setLongitude(location.getLongitude());
			// currentBaiduLocationtemp
			// .setLatitude(location.getLatitude());
			// 增加时间2013-07-10 13:13:05
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			long timeStart = 0;
			try {
				timeStart = sdf.parse(location.getTime()).getTime();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			Log.i(TAG,
					"Radius:" + location.getRadius() + "AddrStr:"
							+ location.getAddrStr() + "City:"
							+ location.getCity());
			currentBaiduLocationtemp.setTime(timeStart);
			currentBaiduLocationtemp.setAccuracy(-location.getRadius());
			Bundle b = new Bundle();
			b.putString("desc", location.getAddrStr());
			b.putString("city", location.getCity());
			currentBaiduLocationtemp.setExtras(b);
			currentBaiduLocation = currentBaiduLocationtemp;
			/************************************/
		}
		public void onReceivePoi(BDLocation poiLocation) {
		}
	}

	protected class OnBtCloseClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				popupWindow.dismiss();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}
	
	@Override
	protected void onPause() {
		mapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		mapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		// 退出时销毁定位
		mLocClient.stop();
		// 关闭定位图层
		mBaiduMap.setMyLocationEnabled(false);
	if (mBound) {
		this.getApplicationContext().unbindService(sc);
		mBound = false;
	}
		mapView.onDestroy();
		mapView = null;
		super.onDestroy();
	}
	
	
}
