/**
 * Copyright (c) 2012-7-21 www.eastelsoft.com
 * $ID TabCheckinActivity.java 上午12:42:13 $
 */
package com.eastelsoft.lbs;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
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
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;

import com.eastelsoft.lbs.CheckinActivity.CheckThread;
import com.eastelsoft.lbs.CheckinActivity.MyReceiver;
import com.eastelsoft.lbs.activity.BaseActivity;

import com.eastelsoft.lbs.adapter.LocListAdapterA;
import com.eastelsoft.lbs.db.DBUtil;
import com.eastelsoft.lbs.db.LocationSQLiteHelper;
import com.eastelsoft.lbs.entity.ReportResp;
import com.eastelsoft.lbs.entity.SetInfo;
import com.eastelsoft.lbs.entity.LocBean;
import com.eastelsoft.lbs.location.BaiduMapAction;
import com.eastelsoft.lbs.location.BaiduMapActionForCheckin;
import com.eastelsoft.lbs.location.BaseStationAction.SItude;
import com.eastelsoft.lbs.service.LocationService;
import com.eastelsoft.lbs.service.LocationService.MBinder;
import com.eastelsoft.util.CallBack;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.GlobalVar;
import com.eastelsoft.util.IUtil;
import com.eastelsoft.util.Util;
import com.mapabc.mapapi.core.GeoPoint;
import com.mapabc.mapapi.core.OverlayItem;
import com.mapabc.mapapi.map.ItemizedOverlay;
import com.mapabc.mapapi.map.MapActivity;
import com.mapabc.mapapi.map.MapController;
import com.mapabc.mapapi.map.MapView;
import com.mapabc.mapapi.map.MyLocationOverlay;
import com.mapabc.mapapi.map.Projection;

/**
 * 签到签退页面 地图
 * 
 * @author lengcj
 */
public class CheckinOnMapActivity extends MapActivity {

	// 签到有关的变量
	private static final String TAG = "CheckinOnMapActivity";

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
	private MyReceiver receiver;
	private String msg_seq = "";
	private LocationSQLiteHelper locationHelper;
	private BaiduMapActionForCheckin bmafc;

	// 地图有关的变量

	private MapView mMapView;
	private MapController mMapController;
	private GeoPoint point;
	private Button bt_checkhistroy;
	private Button bt_lookaround;
	private Button bt_pathplanning;
	private Button img_refresh;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_checkinonmap);
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

		intent = this.getIntent();
		reportTag = intent.getStringExtra("reportTag");
		String checkTitle = "";
		String ckeckhis = "";
		if ("1".equals(reportTag)) {
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
		if (set.getSerialNumber().length() == 0) {
			// 初次安装或者系统参数丢失，进入注册页面，开始或者重新注册
			Intent intent = new Intent();
			intent.setClass(CheckinOnMapActivity.this, RegActivity.class);
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
		// 注册广播接收器，接收定位数据信息
		receiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.location.action");
		this.registerReceiver(receiver, filter);
		locationHelper = new LocationSQLiteHelper(this, null, null, 5);

		bmafc = new BaiduMapActionForCheckin(CheckinOnMapActivity.this,
				amapCallback, "haqisi");

		mMapView = (MapView) findViewById(R.id.mapView);
		mMapView.setVectorMap(true);
		mMapView.setBuiltInZoomControls(true);
		mMapController = mMapView.getController();
		// point = new GeoPoint((int) (39.90923 * 1E6),
		// (int) (116.397428 * 1E6)); //用给定的经纬度构造一个GeoPoint，单位是微度 （度 * 1E6）
		// mMapController.setCenter(point); //设置地图中心点
		mMapController.setZoom(17); // 设置地图缩放级别

		// 获取定位位置
		bmafc.startListener();

	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stu
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		FileLog.i(TAG, "onDestroy....");
		try {

			if (mBound) {
				this.getApplicationContext().unbindService(sc);
				mBound = false;
			}

			if (receiver != null) {
				this.unregisterReceiver(receiver);
				receiver = null;
			}
			if (bmafc != null) {
				bmafc.stopall();
				bmafc = null;
			}
			super.onDestroy();

		} catch (Exception e) {
		}
	}

	private class OnBtlookaroundClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {

//				if (currentBaiduLocation != null) {
//					// CheckinOnMapActivity.this.finish();
//					intent = new Intent(CheckinOnMapActivity.this,
//							PoiSearchDemo.class);
//					intent.putExtra("lon", Util.format(
//							currentBaiduLocation.getLongitude(), "#.######"));
//					intent.putExtra("lat", Util.format(
//							currentBaiduLocation.getLatitude(), "#.######"));
//					startActivity(intent);
//
//				} else {
//					Toast.makeText(CheckinOnMapActivity.this,
//							getString(R.string.tip_waitmoment),
//							Toast.LENGTH_SHORT).show();
//				}

			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	private class OnBtbt_pathplanningClickListenerImpl implements
			OnClickListener {
		public void onClick(View v) {
			try {
//				if (currentBaiduLocation != null) {
//
//					// CheckinOnMapActivity.this.finish();
//					intent = new Intent(CheckinOnMapActivity.this,
//							RouteDemo.class);
//					intent.putExtra("lon", Util.format(
//							currentBaiduLocation.getLongitude(), "#.######"));
//					intent.putExtra("lat", Util.format(
//							currentBaiduLocation.getLatitude(), "#.######"));
//					startActivity(intent);
//				} else {
//					Toast.makeText(CheckinOnMapActivity.this,
//							getString(R.string.tip_waitmoment),
//							Toast.LENGTH_SHORT).show();
//				}

			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	private class OnBthistroyClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {

				// CheckinOnMapActivity.this.finish();
				intent = new Intent(CheckinOnMapActivity.this,
						CheckinHistoryActivity.class);
				intent.putExtra("reportTag", reportTag);
				startActivity(intent);

			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	private class OnBtrefleshClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				longitudetemp = 0;
				latitudetemp = 0;
				acctemp = 0;

				if (bmafc != null) {
					bmafc.stopall();
					bmafc = null;
				}
				bmafc = new BaiduMapActionForCheckin(CheckinOnMapActivity.this,
						amapCallback, "haqisi");
				bmafc.startListener();

			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	private Location currentBaiduLocation = null;
	private double longitudetemp;
	private double latitudetemp;
	private float acctemp;

	public class MyReceiver extends BroadcastReceiver {
		// 自定义一个广播接收器
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				FileLog.i(TAG, "OnReceiver");
				Bundle bundle = intent.getExtras();

				double longitude = bundle.getDouble("longitude");
				double latitude = bundle.getDouble("latitude");
				float acc = bundle.getFloat("acc");
				long time = bundle.getLong("time");
				String addr = bundle.getString("desc");
				if (longitudetemp == longitude && latitudetemp == latitude
						&& acctemp == acc) {
				} else {
					currentBaiduLocation = new Location("lbs");
					currentBaiduLocation.setProvider("lbs");
					currentBaiduLocation.setAccuracy(acc);
					currentBaiduLocation.setLongitude(longitude);
					currentBaiduLocation.setLatitude(latitude);
					currentBaiduLocation.setTime(time);
					Bundle b = new Bundle();
					b.putString("desc", addr);
					currentBaiduLocation.setExtras(b);

					// 刷新地图

					point = new GeoPoint(
							(int) (currentBaiduLocation.getLatitude() * 1E6),
							(int) (currentBaiduLocation.getLongitude() * 1E6));
					mMapController.setCenter(point); // 设置地图中心点

					Drawable marker = getResources().getDrawable(
							R.drawable.da_marker_red); // 得到需要标在地图上的资源
					marker.setBounds(0, 0, marker.getIntrinsicWidth(),
							marker.getIntrinsicHeight()); // 为maker定义位置和边界
					mMapView.getOverlays().clear();
					mMapView.getOverlays().add(
							new OverItemT(marker, context, currentBaiduLocation
									.getLatitude(), currentBaiduLocation
									.getLongitude(), ""));

				}
				longitudetemp = longitude;
				latitudetemp = latitude;
				acctemp = acc;

			} catch (Exception e) {
			}
		}
	}

	private CallBack amapCallback = new CallBack() {
		public void execute(Object[] paramArrayOfObject) {

		}
	};

	private class OnBtBackClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				CheckinOnMapActivity.this.finish();
				// openPopupWindow();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	/**
	 * 签到签退按钮时间
	 */
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
							//环境一切正常开始定位
							CheckinOnMapActivity.this.openPopupWindowPG("");
							checkThread = new Thread(new CheckThread(reportTag));
							checkThread.start();

						}
					}

				} else {
					Toast.makeText(CheckinOnMapActivity.this,
							getString(R.string.tip_waitmoment),
							Toast.LENGTH_SHORT).show();
				}
			} catch (Exception e) {
				FileLog.e("TabCheckinActivity checkin", "error");
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
					dialog(CheckinOnMapActivity.this, msg.obj.toString());

					break;
				case 1:
					// 异常转回注册页面
					Intent intent = new Intent();
					intent.setClass(CheckinOnMapActivity.this,
							RegActivity.class);
					startActivity(intent);
					finish();
					break;
				case 0:
					// 登录成功
					break;
				default:
					respMsg = getResources().getString(R.string.checkin_error);
					dialog(CheckinOnMapActivity.this, respMsg);
					break;
				}

			} catch (Exception e) {
				// 异常中断
				respMsg = getResources().getString(R.string.checkin_error);
				dialog(CheckinOnMapActivity.this, respMsg);
			}
		}
	};

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
					//签到签退
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
		} catch (Exception e) {
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

	private Thread checkThread;
	protected PopupWindow popupWindowPg;
	protected TextView btPopGps;

	protected void openPopupWindowPG(String msg) {
		try {
			LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			ViewGroup menuView = (ViewGroup) mLayoutInflater.inflate(
					R.layout.pop_gps, null, true);
			popupWindowPg = new PopupWindow(menuView, LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT, true); // 全部背景置灰
			btPopGps = (TextView) menuView.findViewById(R.id.pop_gps);
			// btPopGps.setText(msg);
			popupWindowPg.setBackgroundDrawable(new BitmapDrawable());
			popupWindowPg.setAnimationStyle(R.style.PopupAnimation);
			popupWindowPg.showAtLocation(findViewById(R.id.parent),
					Gravity.CENTER | Gravity.CENTER, 0, 0);
			popupWindowPg.update();
		} catch (Exception e) {

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

	/**
	 * 某个类型的覆盖物，包含多个类型相同、显示方式相同、处理方式相同的项时，使用此类.
	 */
	class OverItemT extends ItemizedOverlay<OverlayItem> {
		private List<OverlayItem> GeoList = new ArrayList<OverlayItem>();
		private Drawable marker;
		private Context mContext;

		private double mLat1 = 30.9022; // point1纬度
		private double mLon1 = 116.3922; // point1经度
		private String mLocation1 = ""; // 位置名称

		public OverItemT(Drawable marker, Context context, double mLat1,
				double mLon1, String m) {
			super(boundCenterBottom(marker));
			this.marker = marker;
			this.mContext = context;
			this.mLat1 = mLat1;
			this.mLon1 = mLon1;
			this.mLocation1 = m;
			// 用给定的经纬度构造GeoPoint，单位是微度 (度 * 1E6)
			GeoPoint p1 = new GeoPoint((int) (mLat1 * 1E6), (int) (mLon1 * 1E6));
			// 构造OverlayItem的三个参数依次为：item的位置，标题文本，文字片段
			GeoList.add(new OverlayItem(p1, mLocation1, ""));
			populate(); // createItem(int)方法构造item。一旦有了数据，在调用其它方法前，首先调用这个方法
		}

		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			// Projection接口用于屏幕像素点坐标系统和地球表面经纬度点坐标系统之间的变换
			Projection projection = mapView.getProjection();
			for (int index = size() - 1; index >= 0; index--) { // 遍历GeoList
				OverlayItem overLayItem = getItem(index); // 得到给定索引的item
				String title = overLayItem.getTitle();
				// 把经纬度变换到相对于MapView左上角的屏幕像素坐标
				Point point = projection.toPixels(overLayItem.getPoint(), null);
				// 可在此处添加您的绘制代码
				Paint paintText = new Paint();
				paintText.setColor(Color.BLACK);
				paintText.setTextSize(15);
				canvas.drawText(title, point.x - 30, point.y - 25, paintText); // 绘制文本
			}
			super.draw(canvas, mapView, shadow);
			// 调整一个drawable边界，使得（0，0）是这个drawable底部最后一行中心的一个像素
			boundCenterBottom(marker);
		}

		@Override
		protected OverlayItem createItem(int i) {
			// TODO Auto-generated method stub
			return GeoList.get(i);
		}

		@Override
		public int size() {
			// TODO Auto-generated method stub
			return GeoList.size();
		}

		@Override
		// 处理当点击事件
		protected boolean onTap(int i) {
			setFocus(GeoList.get(i));

			return true;
		}

		@Override
		public boolean onTap(GeoPoint point, MapView mapView) {
			// TODO Auto-generated method stub
			return super.onTap(point, mapView);
		}

	}

}