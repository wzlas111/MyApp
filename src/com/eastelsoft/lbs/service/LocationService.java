/**
 * Copyright (c) 2012-8-1 www.eastelsoft.com
 * $ID LocationService.java 下午3:16:43 $
 */
package com.eastelsoft.lbs.service;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract.Contacts;
import android.telephony.PhoneStateListener;
import android.telephony.SignalStrength;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.SlidingDrawer;
import android.widget.Toast;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.DistanceUtil;
import com.eastelsoft.lbs.BulletinListActivity;
import com.eastelsoft.lbs.InfoActivity;
import com.eastelsoft.lbs.InfoAddActivity;
import com.eastelsoft.lbs.InfoViewActivity;
import com.eastelsoft.lbs.KnowledgeBaseListActivity;
import com.eastelsoft.lbs.PlanActivity;
import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.db.DBUtil;
import com.eastelsoft.lbs.db.LocationSQLiteHelper;
import com.eastelsoft.lbs.entity.ContrReq;
import com.eastelsoft.lbs.entity.InfoBean;
import com.eastelsoft.lbs.entity.Login1Resp;
import com.eastelsoft.lbs.entity.Login2Resp;
import com.eastelsoft.lbs.entity.ReportResp;
import com.eastelsoft.lbs.entity.SetInfo;
import com.eastelsoft.lbs.entity.TimingLocationBean;
import com.eastelsoft.lbs.location.BaiduMapAction;
import com.eastelsoft.lbs.location.BaseStationAction;
import com.eastelsoft.lbs.location.BaseStationAction.SCell;
import com.eastelsoft.lbs.location.GpsAction;
import com.eastelsoft.util.CallBack;
import com.eastelsoft.util.Contant;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.FileUtil;
import com.eastelsoft.util.GlobalVar;
import com.eastelsoft.util.IUtil;
import com.eastelsoft.util.SeparatorUtil;
import com.eastelsoft.util.TcpPackage;
import com.eastelsoft.util.UDPUtil;
import com.eastelsoft.util.UMMPUtil;
import com.eastelsoft.util.Util;
import com.eastelsoft.util.http.AndroidHttpClient;

/**
 * 任务调度器
 * 
 * @author lengcj
 */

public class LocationService extends Service {
	private static final String TAG = "LocationService";
	private SharedPreferences sp;
	private SetInfo set;
	private GlobalVar globalVar;
	private String power = "50";
	private int interval = 60;
	private Thread tcpReceThread = null;
	private Socket socket = null;
	private InputStream in = null;
	private OutputStream out = null;
	private AlarmManager am,pm;
	private Intent intent;
	private PendingIntent sender;
	private PendingIntent sender_1;
	private PendingIntent deleteDaily;

	private CallBack callBack;
	private Location checklocation;
	private CallBack refreshCallBack;
	// private Context context;
	// private String reportTag;
	// private LocationManager locationManager;
	// private LocationListener locationListener;
	public boolean callFlag = false;

	// 实例化自定义的Binder类
	private final IBinder mBinder = new MBinder();

	// 数据存储
	private Map<String, Object> tcpMap = new HashMap<String, Object>();

	private MyReceiver receiver;

	// private NetworkReceiver networkReceiver;

	// private ConnReceiver connReceiver;

	private String networkState = "none";

	private long lastReceivePackTime = System.currentTimeMillis();

	private int notification_id = 20120001;
	private int notificationinform_id = 20120002;

	private int notificationbulletin_id = 20120003;
	private int notificationknowledge_id = 20120004;
	NotificationManager nm;

	private boolean netchange = false;
	private int nettimes = 0;
	// private short msg_seq_temp;
	private boolean isstoptcp = false;

	private ConnectivityManager connectivityManager;
	private NetworkInfo info;

	// 缓存模块
	private LocationSQLiteHelper locationHelper;
	// private String uploadDate;
	private boolean isbeginSendCache = false;

	ArrayList<TimingLocationBean> tb;

	private String softVersion = "NULL";
	private String phoneModel = "";

	private TelephonyManager tm;
	private int signalStrengthValue = 0; // 信号强度
	MyPhoneStateListener myListener;

	private boolean isInfoUploading = false; // 当前是否正在进行文件上传
	// private int infoUploadTime = 0; // 当前进程中文件上传次数，最大允许3次

	private String online_states = Contant.OFF_LINE;

	public void onCreate() {
		FileLog.i(TAG, "============> onCreate");

		super.onCreate();
		sp = getSharedPreferences("userdata", 0);
		set = IUtil.initSetInfo(sp);
		interval = set.getInterval();
		if ("".equals(set.getSerialNumber()) || "".equals(set.getAdapter_ip()))
			return;

		// 注册接收器
		receiver = new MyReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.alarm.service");
		this.getApplicationContext().registerReceiver(receiver, filter);
		// 启动闹钟定时,初始60秒后执行
		intent = new Intent("android.alarm.service");
		sender = PendingIntent.getBroadcast(this, 0, intent,
				PendingIntent.FLAG_CANCEL_CURRENT);
		this.setAlarmTime(60000); // 启动后1分钟执行
		// 初始化网络参数
		// intNetworkState();
		networkState = this.getNetworkState();
		FileLog.i(TAG, "networkState==>" + networkState);
		// 注册网络监听模块
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mReceiver, mFilter);
		// 初始化全局变量
		globalVar = (GlobalVar) getApplication();
		// 电量
		this.registerReceiver(batteryChangedReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		// 如果网络正常且定时上报时间大于1分钟一次，连接tcp
		// if(isNetworkAvailable()) {
		DisConnectToServer();
		// ConnectToServer(); // 初始化连接
		if (tcpReceThread != null) {
			tcpReceThread.interrupt();
			tcpReceThread = null;
		}
		tcpReceThread = new Thread(new TcpReceThread());
		tcpReceThread.start();
		FileLog.i(TAG, "============>TCP Running");
		// 初始化通知
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

		locationHelper = new LocationSQLiteHelper(LocationService.this, null,
				null, 5);
		try {
			softVersion = this.getPackageManager().getPackageInfo(
					this.getPackageName(), 0).versionName;
			if (Build.MODEL != null) {
				phoneModel = Build.MODEL;
			}
		} catch (NameNotFoundException e){
			softVersion = "NULL";
		}

		// 初始化信号强度
		tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		myListener = new MyPhoneStateListener();
		tm.listen(myListener, PhoneStateListener.LISTEN_SIGNAL_STRENGTHS);
		/*
		 * 开始定时清除日志
		 */
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					while(true){
					Thread.sleep(86400000);
					FileUtil.deleteDailyFile();
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void onStart(Intent intent, int startId) {
		FileLog.i(TAG, "============> onStart");
		globalVar = (GlobalVar) getApplication();
		// 重启tcp长连接

		if (tcpReceThread == null) {
			tcpReceThread = new Thread(new TcpReceThread());
			tcpReceThread.start();
			FileLog.i(TAG, "============>TCP Running");
		}
		locationHelper = new LocationSQLiteHelper(LocationService.this, null,
				null, 5);
		super.onStart(intent, startId);
	}

	private void torepeatStartAlarm(Context paramContext) {
		Intent localIntent = new Intent();
		localIntent.setAction("com.eastelsoft.lbs.service.repeating");
		PendingIntent localPendingIntent = PendingIntent.getBroadcast(
				paramContext, 0, localIntent, 0);
		// long l1 = SystemClock.elapsedRealtime();
		long l2 = 60000L;
		((AlarmManager) paramContext.getSystemService("alarm")).setRepeating(
				AlarmManager.RTC, 0, l2, localPendingIntent);
		FileLog.i(TAG,
				"repeat lbs service....................................." + l2);
	}

	public void onDestroy() {
		FileLog.i(TAG, "============> onDestroy");
		// stopListener();
		super.onDestroy();

		if (locationHelper != null) {
			locationHelper.getWritableDatabase().close();
		}
		isstoptcp = true;
		// 当销毁时从新启动
		// 启动并绑定service
		this.getApplicationContext().unregisterReceiver(receiver);
		this.unregisterReceiver(mReceiver);
		DisConnectToServer();
		if (tcpReceThread != null) {
			tcpReceThread.interrupt();
			tcpReceThread = null;
		}
		this.unregisterReceiver(batteryChangedReceiver);
		/* this.startService(new Intent(this, LocationService.class)); */
		torepeatStartAlarm(this);
	}

	public IBinder onBind(Intent paramIntent) {
		FileLog.i(TAG, "============> onBind");
		return mBinder;
	}

	/**
	 * 自定义的Binder类，这个是一个内部类，所以可以知道其外围类的对象，通过这个类，让Activity知道其Service的对象
	 */
	public class MBinder extends Binder {
		public LocationService getService() {
			// 返回Activity所关联的Service对象，这样在Activity里，就可调用Service里的一些公用方法和公用属性
			return LocationService.this;
		}
	}

	/**
	 * 获取电量
	 */
	protected BroadcastReceiver batteryChangedReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
				int level = intent.getIntExtra("level", 0);
				int scale = intent.getIntExtra("scale", 100);
				power = String.valueOf(level * 100 / scale);
			}
		}
	};

	/**
	 * 启动定时器
	 * 
	 * @param context
	 * @param timeInMillis
	 */
	public void setAlarmTime(long timeInMillis){
		if (am != null) {
			am.cancel(sender);
		}
		am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		// 获取最新数据
		FileLog.i(TAG, "========================================第一次");
		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
				+ timeInMillis, interval * 1000, sender);
	}

	public class MyReceiver extends BroadcastReceiver{
		// 自定义一个广播接收器
		@Override
		public void onReceive(Context context, Intent intent) {
			FileLog.i(TAG, "MyReceiver");
			// 开始行上报任务
			sp = getSharedPreferences("userdata", 0);
			set = IUtil.initSetInfo(sp);
			FileLog.i(TAG, "定时上报开始");
			startListener("");
		}
	}

	/**
	 * 启动定时器，检测网络切换及TCP长连接
	 * 
	 * @param context
	 * @param timeInMillis
	 */
	public void setAlarmTime_1(long timeInMillis) {
		if (am != null) {
			am.cancel(sender_1);
		}
		am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		// 60秒，检测一次网络连接情况，检测一次tcp长连接情况
		am.setRepeating(AlarmManager.RTC, timeInMillis, 1800 * 1000, sender_1);
	}

	/**
	 * 网络监听
	 */
	private BroadcastReceiver mReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
				Log.d(TAG + "network", "网络状态已经改变");
				connectivityManager = (ConnectivityManager)
				getSystemService(Context.CONNECTIVITY_SERVICE);
				info = connectivityManager.getActiveNetworkInfo();
				netchange = true; // 网络状态发生了变化
				nettimes += 1; // 网络变化的次数
				if (info != null && info.isAvailable()) {
					String name = info.getTypeName();
					FileLog.i(TAG + "network", "当前网络名称：" + name);
					// 有网络，检测是否有未上传的缓存信息
					if (!isInfoUploading) {
						Thread mThread = new Thread(new InfoAutoThread());
						mThread.start();
					}
				} else {
					Log.d(TAG + "network", "没有可用网络");
				}
			}
		}
	};

	public void showNotification(int icon, String tickertext, String title,
			String content, Class<?> cls, int id) {

		sp = getSharedPreferences("userdata", 0);
		set = IUtil.initSetInfo(sp);

		Notification notification = new Notification(icon, tickertext, System.currentTimeMillis());
		/*
		 * notification.defaults = Notification.DEFAULT_ALL |
		 * Notification.DEFAULT_VIBRATE;
		 */
		notification.flags = Notification.FLAG_AUTO_CANCEL;

		if (set.getShock_select().equals("1")) {
			// 振动提示
			notification.defaults = Notification.DEFAULT_ALL;
		} else {
			notification.defaults = Notification.DEFAULT_SOUND;
			notification.vibrate = null;
		}
		Intent intent = new Intent(this, cls);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pt = PendingIntent.getActivity(this, 0, intent, 0);
		notification.setLatestEventInfo(this, title, content, pt);
		nm.notify(id, notification);

	}
	public void showNotificationKnowledge(int icon, String tickertext, String title,
			String content, Class<?> cls, int id) {
		sp = getSharedPreferences("userdata", 0);
		set = IUtil.initSetInfo(sp);
		Notification notification = new Notification(icon, tickertext, System.currentTimeMillis());
		/*
		 * notification.defaults = Notification.DEFAULT_ALL |
		 * Notification.DEFAULT_VIBRATE;
		 */
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		if (set.getShock_select().equals("1")) {
			// 振动提示
			notification.defaults = Notification.DEFAULT_ALL;
		} else {
			notification.defaults = Notification.DEFAULT_SOUND;
			notification.vibrate = null;
		}
		Intent intent = new Intent(this, cls);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		intent.putExtra("info_auto_id", "firstpage");
		PendingIntent pt = PendingIntent.getActivity(this, 0, intent, 0);
		notification.setLatestEventInfo(this, title, content, pt);
		nm.notify(id, notification);

	}

	public void showNotificationInformation(int icon, String tickertext,
			String title, String content) {
		sp = getSharedPreferences("userdata", 0);
		set = IUtil.initSetInfo(sp);

		Notification notification = new Notification(icon, tickertext, System.currentTimeMillis());
		/*
		 * notification.defaults = Notification.DEFAULT_ALL |
		 * Notification.DEFAULT_VIBRATE;
		 */
		notification.flags = Notification.FLAG_AUTO_CANCEL;

		if (set.getShock_select().equals("1")) {

			// 振动提示
			notification.defaults = Notification.DEFAULT_ALL;
		} else {
			notification.defaults = Notification.DEFAULT_SOUND;
			notification.vibrate = null;
		}
		Intent intent = new Intent(this, InfoActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		PendingIntent pt = PendingIntent.getActivity(this, 0, intent, 0);
		notification.setLatestEventInfo(this, title, content, pt);
		nm.notify(notificationinform_id, notification);
	}

	public void getPlanFromServer() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					sp = getSharedPreferences("userdata", 0);
					SetInfo set = IUtil.initSetInfo(sp);
					// 先发消息检是否需要更新
					String url = set.getHttpip() + Contant.PLAN_UPDATE_ACTION;
					url += "&ActionType=1";
					url += "&GpsId=" + set.getDevice_id();
					url += "&Pin=" + set.getAuth_code();
					String jsonStr = AndroidHttpClient.getContent(url);
					jsonStr = IUtil.chkJsonStr(jsonStr);
					// String jsonStr = Contant.TEST_DATA_INFO_TAG;
					String updatecode = "";
					JSONArray array = new JSONArray(jsonStr);
					if (array.length() > 0) {
						JSONObject obj = array.getJSONObject(0);
						updatecode = obj.getString("updatecode");
						FileLog.i(TAG, "updatecode==>" + updatecode);
					}
					// 如果需要更新
					if (updatecode != null && !"".equals(updatecode)
							&& !"0".equals(updatecode)
							&& !set.getPlanupdatecode().equals(updatecode)) {
						Message msg = handler.obtainMessage();
						msg.what = 80; // 任务通知
						handler.sendMessage(msg);
					}

				} catch (Exception e) {
					FileLog.e(TAG, "getPlanFromServer==>" + e.toString());
				}

			}
		});
		t.start();
	}

	private String getNetworkState() {
		State wifiState = null;
		State mobileState = null;
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		wifiState = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
		mobileState = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE)
				.getState();
		if (wifiState != null && mobileState != null
				&& State.CONNECTED != wifiState
				&& State.CONNECTED == mobileState) {
			networkState = "gprs";
		} else if (wifiState != null && mobileState != null
				&& State.CONNECTED != wifiState
				&& State.CONNECTED != mobileState) {
			networkState = "none";
		} else if (wifiState != null && State.CONNECTED == wifiState) {
			networkState = "wifi";
		}
		return networkState;
	}

	/**
	 * 立即定位
	 */
	public void reportNow() {
		FileLog.i(TAG, "report Now...........................");
		startListener("-1");
	}

	/**
	 * 使用TCP长连接发送数据
	 * 
	 * @param data
	 */
	public void sendTcpData(byte[] data) {
		try {
			if (socket != null && socket.isConnected() && !socket.isClosed()) {
				FileLog.i(TAG, "Send TCP data start...................");
				FileLog.i(TAG, "out==>" + socket.isOutputShutdown());
				FileLog.i(TAG, " in==>" + socket.isInputShutdown());
				if (out != null && data != null) {
					out.write(data);
					out.flush();
					FileLog.i(TAG, "Send TCP data over...................");
				} else {
					FileLog.i(TAG, "Send TCP data err...." + out + ":" + data);
				}
			} else {
				FileLog.i(TAG, "Send TCP data start1...................");
				// 在接收线程里面会重新连接服务端
				// DisConnectToServer();
				// ConnectToServer();

				// out.write(data);
				// out.flush();
				// FileLog.i(TAG, "Send TCP data over1...................");
			}
		} catch (IOException e) {
			FileLog.i(TAG, "Send TCP data fail...................");
		} catch (Exception e) {
			FileLog.i(TAG, "Send TCP data fail...................");
		}
	}

	/**
	 * tcp长连时发送登录包
	 */
	public void testTcp(){
		set = IUtil.initSetInfo(sp);
		FileLog.i(TAG, "tcp长连时发送登录包");
		// 先做登录
		/* short msg_seq = IUtil.getMsgReq(globalVar); */
		/* FileLog.i(TAG, "msg_seq" +msg_seq); */
		// Login1Resp login1Resp = UDPUtil.sendLogin1((short) 0,
		// set,softVersion);
		Login1Resp login1Resp = UMMPUtil.sendLogin1((short) 0, set, phoneModel,
				softVersion);
		FileLog.i(TAG, "tcp长连时发送登录包:" + login1Resp);
		if (login1Resp != null) {
			if ("0".equals(login1Resp.getRet())) {
				IUtil.writeSharedPreferences(sp, login1Resp);
				// 二次登录
				// short msg_seq = IUtil.getMsgReq(globalVar);
				short msg_seq = 0;
				// msg_seq_temp = msg_seq;
				// byte[] data = UDPUtil
				// .getLogin2ReqData(msg_seq, set, login1Resp);
				byte[] data = UMMPUtil.getLogin2ReqData(msg_seq, set,
						login1Resp);
				try {
					out.write(data);
					out.flush();
					FileLog.i(TAG, "out.write(data)");
				} catch (IOException e) {

					e.printStackTrace();
				}
			}
			// if ("5".equals(login1Resp.getRet())) {
			// // 账号已经注销，清楚数据
			// Editor editor = sp.edit();
			// editor.putString("serialNumber", "");
			// editor.commit();
			// //LocationService.this.stopSelf();
			// }
			if (!"0".equals(login1Resp.getRet())
					&& !"5".equals(login1Resp.getRet())) {
				// 账号已经注销，清楚数据
				Editor editor = sp.edit();
				editor.putString("serialNumber", "");
				editor.commit();
				try {
					LocationService.this.stopSelf();
				} catch (Exception e) {
				}
			}
		}
	}

	/**
	 * 断开与服务器端的链接
	 */
	public void DisConnectToServer() {
		try {
			if (in != null)
				in.close();
			if (out != null)
				out.close();
			if (socket != null)
				socket.close();
		} catch (IOException e) {
			FileLog.e(TAG, "Connect close error==>" + e.getMessage());
		}
	}

	/**
	 * 连接到服务端，测试用，使用子线程，未用到
	 */
	public void ConnectToServerInThread() {
		class ConnectThread implements Runnable {
			@Override
			public void run() {
				try {
					// 建立连接
					socket = new Socket(InetAddress.getByName(set
							.getAdapter_ip()), set.getAdapter_port());
					socket.setKeepAlive(true);
					in = socket.getInputStream();
					out = socket.getOutputStream();
					testTcp();
				} catch (IOException e) {
					if (socket != null) {
						try {
							in.close();
							out.close();
							socket.close();
						} catch (IOException e1){
						}
					}
					FileLog.e(TAG, "Connect server error==>" + e.getMessage());
				}
			}
		};
		Thread connectThread = new Thread(new ConnectThread());
		connectThread.start();
	}

	/**
	 * 连接到服务端
	 */
	public void ConnectToServer() {
		try {
			// 发送连接广播
			if (isNetworkAvailable()) {
				this.sendBroadcast(Contant.LINGING);

			} else {
				this.sendBroadcast(Contant.OFF_LINE);
			}
			socket = new Socket(InetAddress.getByName(set.getAdapter_ip()),
					set.getAdapter_port());
			socket.setKeepAlive(true);
			FileLog.i(TAG, "localport" + socket.getLocalPort());
			FileLog.i(TAG, "port" + socket.getPort());
			in = socket.getInputStream();
			out = socket.getOutputStream();
			socket.setSoTimeout(10000);
			testTcp();
		} catch (IOException e) {
			if (socket != null) {
				try {
					in.close();
					out.close();
					socket.close();
				} catch (IOException e1) {
				}
			}
			FileLog.e(TAG, "Connect server error==>" + e.getMessage());
		}

	}

	/**
	 * Tcp接收线程
	 * 
	 * @author lengcj
	 */
	class TcpReceThread implements Runnable {
		@Override
		public void run() {
			FileLog.i(TAG, "TcpReceThread==> is Running");
			ConnectToServer();

			while (!isstoptcp) {

//				FileLog.i(TAG, "循环等待接收数据");

//				FileLog.i(TAG, "isNetworkAvailable==>" + isNetworkAvailable());

				if (isNetworkAvailable()) {
					networkState = LocationService.this.getNetworkState();
				}
				if (socket != null && socket.isConnected()
						&& !socket.isClosed()) {
//					FileLog.i(TAG, "huilocalport" + socket.getLocalPort());
//					FileLog.i(TAG, "huiport" + socket.getPort());
					try {
						int length = 0;
						byte[] buffer = new byte[1024];
//						FileLog.i(TAG, "in数据");
						length = in.read(buffer);
						FileLog.i(TAG, "length==>" + length);
						if (length > 0) {

							// 解析获取到的信息
							TcpPackage pack = new TcpPackage();
							pack.parseResp(buffer);

							FileLog.i(TAG,
									"msg command_id==>" + pack.getCommand_id());
							FileLog.i(TAG, "msg seq==>" + pack.getSeq());

							if (pack.getLogin2Resp() != null)
								FileLog.i(TAG, "Login2Resp==>"
										+ pack.getLogin2Resp().toString());

							if (pack.getSeq() >= 0) {
								lastReceivePackTime = System
										.currentTimeMillis();
								if (pack.getCommand_id() == 0x0007) {
									// 配置下发请求
									FileLog.i(TAG, "配置下发");
									if (pack.getLogin2Resp() != null) {
										IUtil.writeSharedPreferences(sp,
												pack.getLogin2Resp());
										Message msg = handler.obtainMessage();
										msg.what = 77;
										handler.sendMessage(msg);
									}
								}
								if (pack.getCommand_id() == 0x0008) {
									FileLog.i(TAG, "======指令下发");
									if (pack.getContrReq() != null) {
										ContrReq cr = pack.getContrReq();
										if (cr.getCmd() != null) {
											if ("1".equals(cr.getNum())) {
												// 立即上报请求，先回复消息，再开始上报数据
												FileLog.i(TAG, "lijishangbao");
												short ack_seq = (short) pack
														.getSeq();
												// short msg_seq =
												// IUtil.getMsgReq(globalVar);
												short msg_seq = 0;
												byte[] data = UDPUtil
														.getContrRespData(
																msg_seq,
																ack_seq, set,
																"1");
												out.write(data);
												out.flush();
												// 开始上报数据
												/* reportNow(); */
												Message msg = handler
														.obtainMessage();
												msg.what = 67; // 放到主线程
												handler.sendMessage(msg);

											}
											if ("2".equals(cr.getNum())) {
												// 有新的工作计划
												// ，先回复消息，
												FileLog.i(TAG, "工作计划");
												short ack_seq = (short) pack
														.getSeq();
												short msg_seq = 0;
												byte[] data = UDPUtil
														.getContrRespData(
																msg_seq,
																ack_seq, set,
																"2");
												out.write(data);
												out.flush();
												sp = getSharedPreferences(
														"userdata", 0);
												set = IUtil.initSetInfo(sp);
												if (set.getMsg_select().equals(
														"1")) {
													FileLog.i(TAG, "faxiaoxi");
													showNotification(
															R.drawable.notify,
															getResources()
																	.getString(
																			R.string.app_name),
															getResources()
																	.getString(
																			R.string.app_name),
															getResources()
																	.getString(
																			R.string.plan_new),
															PlanActivity.class,
															notification_id);
												} else {
													FileLog.i(TAG, "bufaxiaoxi");
												}
											}
											if ("3".equals(cr.getNum())) {
												FileLog.i(TAG, "公告通知");
												short ack_seq = (short) pack
														.getSeq();
												short msg_seq = 0;
												byte[] data = UDPUtil
														.getContrRespData(
																msg_seq,
																ack_seq, set,
																"3");
												out.write(data);
												out.flush();
												sp = getSharedPreferences(
														"userdata", 0);
												set = IUtil.initSetInfo(sp);
												if (set.getMsg_select().equals(
														"1")) {
													FileLog.i(TAG, "faxiaoxi");
													showNotification(
															R.drawable.notify,
															getResources()
																	.getString(
																			R.string.app_name),
															getResources()
																	.getString(
																			R.string.app_name),
															getResources()
																	.getString(
																			R.string.bulletin_new),
															BulletinListActivity.class,
															notificationbulletin_id);
												} else {
													FileLog.i(TAG, "bufaxiaoxi");

												}
											}
											if ("4".equals(cr.getNum())) {
												FileLog.i(TAG, "知识库");
												short ack_seq = (short) pack
														.getSeq();
												short msg_seq = 0;
												byte[] data = UDPUtil
														.getContrRespData(
																msg_seq,
																ack_seq, set,
																"4");
												out.write(data);
												out.flush();
												sp = getSharedPreferences(
														"userdata", 0);
												set = IUtil.initSetInfo(sp);
												if (set.getMsg_select().equals(
														"1")) {
													FileLog.i(TAG, "faxiaoxi");
													showNotificationKnowledge(
															R.drawable.notify,
															getResources()
																	.getString(
																			R.string.app_name),
															getResources()
																	.getString(
																			R.string.app_name),
															getResources()
																	.getString(
																			R.string.knowledge_new),
															KnowledgeBaseListActivity.class,
															notificationknowledge_id);
												} else {
													FileLog.i(TAG, "bufaxiaoxi");
												}
											}

										}

									}
								}
								if (pack.getCommand_id() == 0x0004) {
									// 心跳包，回复消息
									short ack_seq = (short) pack.getSeq();
									short msg_seq = 0;
									byte[] data = UDPUtil.getHeartbeatRespData(
											msg_seq, ack_seq, set);
									out.write(data);
									out.flush();
								}
								if (pack.getCommand_id() == 0x8011) {
									// 上报、签到的返回包
									short ack_seq = (short) pack.getSeq();
									if (tcpMap.containsKey(String
											.valueOf(ack_seq) + "11")) {
										tcpMap.remove(String.valueOf(ack_seq)
												+ "11");
									}
									if (pack.getReportResp() != null) {
										tcpMap.put(String.valueOf(ack_seq)
												+ "11", pack.getReportResp());
									} else {
										tcpMap.put(String.valueOf(ack_seq)
												+ "11", null);
									}
								}
								if (pack.getCommand_id() == 0x8002) {

									if (pack.getLogin2Resp() != null) {
										// 登录返回1时候，重新登录
										if (pack.getLogin2Resp().getRet() != null) {
											if ("0".equals(pack.getLogin2Resp()
													.getRet())
													|| "2".equals(pack
															.getLogin2Resp()
															.getRet())) {
												// 登录成功
												LocationService.this
														.sendBroadcast(Contant.ON_LINE);
											}

											if ("1".equals(pack.getLogin2Resp()
													.getRet())) {
												FileLog.i(
														TAG,
														"START RELOGIN==>"
																+ pack.getLogin2Resp());
												/*
												 * short msg_seq =
												 * IUtil.getMsgReq(globalVar);
												 */
												Login1Resp login1Resp = UDPUtil
														.sendLogin1((short) 0,
																set,
																softVersion);
												if (login1Resp != null
														&& "0".equals(login1Resp
																.getRet())) {
													IUtil.writeSharedPreferences(
															sp, login1Resp);
													FileLog.i(
															TAG,
															"START RELOGIN1==>"
																	+ login1Resp
																			.toString());
												} else if (login1Resp != null
														&& "1".equals(login1Resp
																.getRet())) {
													// 账号已经注销，清楚数据
													Editor editor = sp.edit();
													editor.putString(
															"serialNumber", "");
													editor.commit();
													// 退出
													LocationService.this
															.stopSelf();
												}
											}

											if ("2".equals(pack.getLogin2Resp()
													.getRet())) {
												IUtil.writeSharedPreferences(
														sp,
														pack.getLogin2Resp());
												Message msg = handler
														.obtainMessage();
												msg.what = 77;
												handler.sendMessage(msg);
											}

											/*
											 * if
											 * ("1".equals(pack.getLogin2Resp(
											 * ).getRet())) { if(msg_seq>0 &&
											 * msg_seq_temp
											 * <(short)pack.getSeq()){
											 * IUtil.setMsgReq(globalVar); }
											 * 
											 * }
											 */

										}
									}

									// 二次登陆的返回包
									short ack_seq = (short) pack.getSeq();
									if (tcpMap.containsKey(String
											.valueOf(ack_seq) + "02")) {
										tcpMap.remove(String.valueOf(ack_seq)
												+ "02");
									}
									if (pack.getLogin2Resp() != null) {
										tcpMap.put(String.valueOf(ack_seq)
												+ "02", pack.getLogin2Resp());
									} else {
										tcpMap.put(String.valueOf(ack_seq)
												+ "02", null);
									}
								}

							}
							// 立即定位的，要用command_id判断一下
						} else {
							FileLog.i(TAG, "length<0");
							DisConnectToServer();
							try {
								Thread.sleep(6000);
							} catch (InterruptedException e1) {
							}
							FileLog.i(TAG, "sleep 6miao");
							ConnectToServer();
						}
					} catch (SocketTimeoutException e) {
						// FileLog.e(TAG, "TcpReceThread e==>" + e.toString());
						// FileLog.e(TAG, "TcpReceThread e nettimes==>" +
						// nettimes);
						// FileLog.e(TAG, "TcpReceThread e netchange==>" +
						// netchange);
						// 网络切换
						if (netchange) {
							FileLog.e(TAG, "TcpReceThread e netchange==>网络切换");
							netchange = false;
							if (nettimes > 1) {
								// 网络切换时，重新启动连接线程
								Message msg = handler.obtainMessage();
								msg.what = 99;
								handler.sendMessage(msg);
								break;
							}
						}
						// 检测10分钟内没有收到消息，重新连接
						long packTime = System.currentTimeMillis()
								- lastReceivePackTime;
						if (packTime >= 10 * 60 * 1000) {
							FileLog.e(TAG, "TcpReceThread e packTime==>"
									+ packTime);
							// 10分钟没有任何消息收到，重新连接
							Message msg = handler.obtainMessage();
							msg.what = 99;
							handler.sendMessage(msg);
							break;
						}
					} catch (Exception e) {
						FileLog.e(TAG, "TcpReceThread e2==>" + e.toString());

						DisConnectToServer();
						try {
							Thread.sleep(6000);
						} catch (InterruptedException e1) {
						}
						FileLog.i(TAG, "sleep 6miao");
						ConnectToServer();
						FileLog.e(TAG, "TcpReceThread e==>" + "goooooo");

						/* break; */

					}
				} else {
					FileLog.e(TAG, "TcpReceThread==> Connection is Not working");

					DisConnectToServer();
					try {
						Thread.sleep(6000);
					} catch (InterruptedException e1) {
					}
					FileLog.i(TAG, "sleep 6miao");
					ConnectToServer();
				}
			}
		}
	};

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

	// 定位间隔改变，重新开启定时器
	public void dointerval() {

		FileLog.i(TAG, "dointerval()");
		sp = getSharedPreferences("userdata", 0);
		set = IUtil.initSetInfo(sp);
		if (interval != set.getInterval()) {
			FileLog.i(TAG, "重启定时器");
			interval = set.getInterval();
			LocationService.this.setAlarmTime(interval*10);
		}
	}

	/**
	 * 签到,再封装一层for地图签到
	 * 
	 * @param reportTag
	 * @param callBack
	 */
	public void startListener(String reportTag, CallBack callBack,
			Location location) {
		this.callBack = callBack;
		this.checklocation = location;
		startListener(reportTag);
	}

	/**
	 * 签到
	 * 
	 * @param reportTag
	 * @param callBack
	 */
	public void startListener(String reportTag, CallBack callBack) {
		this.callBack = callBack;
		startListener(reportTag);
	}

	/**
	 * 数据上报
	 * 
	 * @param reportTag
	 */
	public void startListener(String reportTag) {
		try {
			boolean need = IUtil.isNeedSendReport(set.getTimePeriod(),
					set.getWeek(), set.getFilterDate());
			// -1 立即上报，空时定时上报，0和1签退签到
			if (!"".equals(reportTag) || ("".equals(reportTag) && need)) {
				FileLog.i(TAG, "定位标识——》"+reportTag+"(-1 立即上报，空时定时上报，0和1签退签到)");
				if ("0".equals(reportTag) || "1".equals(reportTag)) {
					Object[] arrayOfObject = new Object[2];
					arrayOfObject[0] = checklocation;
					arrayOfObject[1] = reportTag;
					Message msg = handler.obtainMessage();
					msg.what = 2;
					msg.obj = arrayOfObject;
					handler.sendMessage(msg);
				} else if ("-1".equals(reportTag)){
					new BaiduMapAction(this, baiduMapCallback, reportTag)
							.startListener();
				} else if ("0".equals(reportTag)){
					sp = getSharedPreferences("userdata", 0);
					set = IUtil.initSetInfo(sp);
					new GpsAction(this, gpsCallback, reportTag,
							set.getGps_time()).startListener();
				}
			}
		} catch (Exception e) {
			FileLog.i(TAG, e.getMessage());
		}
	}

	private CallBack gpsCallback = new CallBack() {
		public void execute(Object[] paramArrayOfObject) {
			Message msg = handler.obtainMessage();
			msg.what = 0;
			msg.obj = paramArrayOfObject;
			handler.sendMessage(msg);
		}
	};

	private CallBack baiduMapCallback = new CallBack() {
		public void execute(Object[] paramArrayOfObject) {
			Message msg = handler.obtainMessage();
			msg.what = 2;
			msg.obj = paramArrayOfObject;
			handler.sendMessage(msg);
		}
	};

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				Location location = null;
				String reportTag = "";
				if (msg.obj != null) {
					Object[] paramArrayOfObject = (Object[]) msg.obj;
					if (paramArrayOfObject[0] != null)
						location = (Location) paramArrayOfObject[0];
					if (paramArrayOfObject[1] != null)
						reportTag = (String) paramArrayOfObject[1];
				}
				if (location != null&&(location.getLongitude() >= 60&&location.getLongitude()<=150||
						location.getLatitude()>=0&&location.getLatitude()<=135)) {
					if (location.getAccuracy() == 0)
						location.setAccuracy(1f);
					LocationService.this.sendLocation(location, reportTag); // gps定位
				} else {
					if (isNetworkAvailable()) {
						new BaiduMapAction(LocationService.this,
								baiduMapCallback, reportTag).startListener();
					} else {
						FileLog.i(TAG, "============>百度定时定位无网络");
					}
				}
				break;
			case 2:
				Location location2 = null;
				String reportTag2 = "";
				if (msg.obj != null) {
					Object[] paramArrayOfObject = (Object[]) msg.obj;
					if (paramArrayOfObject[0] != null)
						location2 = (Location) paramArrayOfObject[0];
					if (paramArrayOfObject[1] != null)
						reportTag2 = (String) paramArrayOfObject[1];
				}
				if (location2 != null) {
					if (location2.getProvider() != null
							&& "gps".equalsIgnoreCase(location2.getProvider())){
						if (location2.getAccuracy() == 0)
							location2.setAccuracy(-1f);
					} else {
						// 错误的位置，如果签到签退、立即上报，不发送；如果定时上报，发送998
						if (location2.getLongitude() <= 60||location2.getLongitude()>=150||
								location2.getLatitude()<=0||location2.getLatitude()>=135){
							if ("".equals(reportTag2)) {
								location2 = new Location("lbs");
								location2.setLongitude(998.0d);
								location2.setLatitude(998.0d);
								location2.setAccuracy(998.0f);
							} else {
								location2 = null; // 错误的签到数据，不上传
							}
						}
						globalVar.setLocation_correct(location2);
					}
				} else {
					// 触发返回，如果定时上报，发送998
					if ("".equals(reportTag2)) {
						location2 = new Location("lbs");
						location2.setLongitude(998.0d);
						location2.setLatitude(998.0d);
						location2.setAccuracy(998f);
					}
				}
				LocationService.this.sendLocation(location2, reportTag2);
				break;
			case 67:
				// 立即定位上报
				reportNow();
				break;
			case 77:
				// 新的配置参数，重新启动定时器
				dointerval();
				break;
			case 78:
				// 上报缓存数据
				// 刷缓存报数据
				try {
					tb = DBUtil.getDataFromLLTiminglocation(locationHelper
							.getWritableDatabase());
					// 开启线程上报缓存数据
					if (tb.size() > 0 && !isbeginSendCache) {
						Thread sendThread = new Thread(new SendCacheThread());
						sendThread.start();
					}
				} catch (Exception e) {
					FileLog.i(TAG, "刷缓存失败");
				}
				break;
			case 80:
				showNotification(R.drawable.notify,
						getResources().getString(R.string.app_name),
						getResources().getString(R.string.app_name),
						getResources().getString(R.string.plan_new),
						PlanActivity.class, notification_id);
				break;
			case 99:
				if (tcpReceThread != null) {
					tcpReceThread = null;
				}
				tcpReceThread = new Thread(new TcpReceThread());
				tcpReceThread.start();
				FileLog.i(TAG, "============>TCP RE Running");
				break;
			}
		}
	};

	class SendCacheThread implements Runnable {
		@Override
		public void run() {
			try {
				isbeginSendCache = true;

				for (int i = 0; i < tb.size(); i++) {
					FileLog.i(TAG, tb.get(i).getTl_id() + ";;;;;;"
							+ tb.get(i).getTl_uploadDate());
					ReportResp reportResp = null;
					short msg_seq = IUtil.getMsgReq(globalVar);

					// 定时上报，立即定位
					// udp模式

					// reportResp = UDPUtil.sendReportCache(msg_seq,
					// tb.get(i).getTl_lon(),
					// tb.get(i).getTl_lat(),tb.get(i).getTl_accuracy()
					// ,tb.get(i).getTl_uploadDate(),set,power);
					reportResp = UMMPUtil.sendReport(Contant.PACKAGETYPECA,
							msg_seq, tb.get(i).getTl_lon(), tb.get(i)
									.getTl_lat(), tb.get(i).getTl_accuracy(),
							tb.get(i).getTl_uploadDate(), tb.get(i)
									.getTl_power(), tb.get(i).getTl_states(),
							tb.get(i).getTl_signalStrengthValue(), tb.get(i)
									.getTl_cell(), set);
					if (reportResp != null) {
						FileLog.i(TAG, "msg_seq" + msg_seq);
						FileLog.i(TAG, reportResp.getSeq());
						if (msg_seq > 0
								&& msg_seq < (short) reportResp.getSeq()) {
							IUtil.setMsgReq(globalVar);
						}
						if ("0".equals(reportResp.getRet())
								|| "2".equals(reportResp.getRet())) {
							DBUtil.deleteLTiminglocation(locationHelper
									.getWritableDatabase(), tb.get(i)
									.getTl_id());
						}
					}
				}
				isbeginSendCache = false;
			} catch (Exception e) {
				isbeginSendCache = false;
			} finally {
				isbeginSendCache = false;

			}
		}

	}

	/**
	 * 向接口发送数据
	 * 
	 * @param location
	 */
	private void sendLocation(Location location, String reportTag) {
		Thread sendThread = new Thread(new SendThread(location, reportTag));
		sendThread.start();
	}

	class SendThread implements Runnable {
		private Location location;
		private String reportTag;
		private String uploadDate;

		public SendThread(Location location, String reportTag) {
			this.location = location;
			this.reportTag = reportTag;
		}

		@Override
		public void run() {
			ReportResp reportResp = null;
			short msg_seq = 0;
			boolean needCache = false;
			if (location != null) {
				if ("".equals(reportTag)) // 只有定时上报，序列增加
					msg_seq = IUtil.getMsgReq(globalVar);
				
				
				FileLog.i(TAG, "reportTag==" + reportTag);
				// 获取基站信息
				SCell cell = new BaseStationAction(LocationService.this)
						.location2();
				uploadDate = String.valueOf(location.getTime() / 1000);
				if (uploadDate == null || "0".equals(uploadDate)) {
					// 获取当前时间，取本机时间
					uploadDate = Util.getLocaleTime();
				}
				FileLog.i(TAG, "uploadDate==" + uploadDate);
				// 开始发送数据
				
				
				
				reportResp = UMMPUtil.sendReportUDP(Contant.PACKAGETYPE,
						msg_seq, location, uploadDate, set, power, cell,
						reportTag, signalStrengthValue);
				// reportResp = null;
				if (reportResp != null) { // 服务端有返回信息
					// 处理序列号问题，发送序列号小于返回，重置序列号
					if (msg_seq > 0 && msg_seq < (short) reportResp.getSeq()) {
						IUtil.setMsgReq(globalVar);
					}
					// 判断返回结果
					if ("0".equals(reportResp.getRet())) {
						// 发送成功
						FileLog.i(TAG, "Send Succ==" + reportResp.getRet());
						// 上报缓存数据
						Message msg = handler.obtainMessage();
						msg.what = 78;
						handler.sendMessage(msg);
					} else if ("2".equals(reportResp.getRet())) {
						FileLog.i(TAG, "Send Succ==" + reportResp.getRet());
						// 发送成功，有参数刷新
						IUtil.writeSharedPreferences(sp, reportResp);
						// 定位周期设置调整，重新启动定时器
						Message msg = handler.obtainMessage();
						msg.what = 77;
						handler.sendMessage(msg);

					} else if ("1".equals(reportResp.getRet())
							|| "3".equals(reportResp.getRet())) {
						// 重新一次登录，并缓存数据
						FileLog.i(TAG, "Send Fail==" + reportResp.getRet());
						// 发送失败，需要重新登录
						Login1Resp login1Resp = UMMPUtil.sendLogin1((short) 0,
								set, phoneModel, softVersion);
						if (login1Resp != null
								&& "0".equals(login1Resp.getRet())) {
							IUtil.writeSharedPreferences(sp, login1Resp);
							// 二次登录
							byte[] data = UMMPUtil.getLogin2ReqData((short) 0,
									set, login1Resp);
							Login2Resp login2Resp = UMMPUtil.sendLogin2(
									login1Resp.getAdapter_ip(),
									login1Resp.getAdapter_port(), data);
							if (login2Resp != null) {
								if ("2".equals(login2Resp.getRet())) {
									// 二次登录成功，有配置信息更新
									IUtil.writeSharedPreferences(sp, login2Resp);
									Message msg = handler.obtainMessage();
									msg.what = 77;
									handler.sendMessage(msg);
								}
								if ("1".equals(login2Resp.getRet())
										|| "3".equals(login2Resp.getRet())) {
									// 二次登录失败
									needCache = true;
								}
							} else {
								// 二次登录失败，缓存数据
								needCache = true;
							}
						} else {
							// 一次登录失败，缓存数据
							needCache = true;
						}
					} else {
						// 错误的返回标识
						FileLog.e(TAG, "错误的返回标识====" + msg_seq);
					}
				} else {
					// 开始通过HTTP发送数据
					sp = getSharedPreferences("userdata", 0);
					SetInfo set = IUtil.initSetInfo(sp);

					String url = set.getHttpip() + Contant.ACTION;
					Map<String, String> map = new HashMap<String, String>();
					map.put("reqCode", Contant.CLINET_REPORT_ACTION);
					map.put("package_type", Contant.PACKAGETYPE);
					map.put("msg_seq", String.valueOf(msg_seq));
					map.put("lon",
							Util.format(location.getLongitude(), "#.######"));
					map.put("lat",
							Util.format(location.getLatitude(), "#.######"));
					map.put("accuracy",
							Util.format(String.valueOf(location.getAccuracy())));
					map.put("uploadtime ", uploadDate);
					map.put("device_id ", set.getDevice_id());
					map.put("auth_code ", set.getAuth_code());
					map.put("power", power);
					String cellStr = "0,0,0,0"; // 基站信息
					if (cell != null)
						cellStr = cell.MCC + "," + cell.MNC + "," + cell.LAC
								+ "," + cell.CID;
					map.put("cell", cellStr);
					map.put("reportTag ", reportTag);
					map.put("signalStrengthValue",
							String.valueOf(Math.abs(signalStrengthValue)));
					String jsonStr = AndroidHttpClient.getContent(url, map);
					jsonStr = IUtil.chkJsonStr(jsonStr);
					FileLog.i(TAG, "jsonStr==>" + jsonStr);
					if (jsonStr == null || "".equals(jsonStr)
							|| "[]".equals(jsonStr)) {
						needCache = true;
					} else {
						try {
							JSONArray array = new JSONArray(jsonStr);
							String resultcode = "";
							if (array.length() > 0) {
								JSONObject obj = array.getJSONObject(0);
								resultcode = obj.getString("ret");
								FileLog.i(TAG,
										"ret==>" + String.valueOf(resultcode));
								reportResp = new ReportResp();
								reportResp.setRet(String.valueOf(resultcode));
								if (resultcode.equals("2")) {
									try {
										int i = obj.getInt("interval");
										String w = obj.getString("week");
										String t = obj.getString("timePeriod");
										String f = obj.getString("filterDate");
										String m = obj.getString("minDistance");
										String r = obj.getString("rept");
										int s = obj.getInt("seq");
										reportResp.setInterval(i);
										reportResp.setWeek(w);
										reportResp.setTimePeriod(t);
										reportResp.setFilterDate(f);
										reportResp.setMinDistance(m);
										reportResp.setRept(r);
										reportResp.setSeq(s);
									} catch (Exception e) {
										reportResp.setRet("0");
									}
								}
								FileLog.i(TAG, reportResp.toString());
							}
						} catch (JSONException e) {
							e.printStackTrace();
							// 缓存数据
							needCache = true;
						}
					}

				}
				if (needCache && "".equals(reportTag)) {
					// 缓存数据
					FileLog.i(TAG, "缓存数据========================" + uploadDate);
					try {
						String cells = "0,0,0,0";
						if (cell != null)
							cells = cell.MCC + "," + cell.MNC + "," + cell.LAC
									+ "," + cell.CID;
						DBUtil.insertLTiminglocation(locationHelper
								.getWritableDatabase(), UUID.randomUUID()
								.toString(), uploadDate, Util.format(
								location.getLongitude(), "#.######"), Util
								.format(location.getLatitude(), "#.######"),
								Util.format(String.valueOf(location
										.getAccuracy())), String
										.valueOf(msg_seq), String
										.valueOf(power), "3",
								String.valueOf(Math.abs(signalStrengthValue)),
								cells, "");
					} catch (Exception e) {
						FileLog.i(TAG, "缓存数据失败!!!!!!!!!!!!!!!!!!!!!!!!!");
					}
				}
			}
			if (LocationService.this.callBack != null && !"".equals(reportTag)
					&& !"-1".equals(reportTag)) {
				if (reportResp != null) {
					if (msg_seq > 0 && msg_seq < (short) reportResp.getSeq()) {
						FileLog.i(TAG, "msg_seq<reportResp.getSeq()");
						IUtil.setMsgReq(globalVar);
						location = null;
					}
				}
				CallBack localCallBack = LocationService.this.callBack;
				Object[] arrayOfObject = new Object[4];
				arrayOfObject[0] = location;
				arrayOfObject[1] = reportResp;
				arrayOfObject[2] = reportTag;
				arrayOfObject[3] = "socketisgood";
				localCallBack.execute(arrayOfObject);
			}

			FileLog.i(TAG, "This ================end");
		}
	};

	public void refreshSet(CallBack callBack) {
		this.refreshCallBack = callBack;
		Thread refreshThread = new Thread(new RefreshThread());
		refreshThread.start();
	}

	class RefreshThread implements Runnable {
		@Override
		public void run() {
			Login2Resp login2Resp = null;
			short msg_seq = 0;
			try {
				set = IUtil.initSetInfo(sp);
				login2Resp = null;
				// 发送失败，需要重新登录后，再发送一次
				Login1Resp login1Resp = new Login1Resp();
				login1Resp.setAdapter_ip(set.getAdapter_ip());
				login1Resp.setAdapter_port(set.getAdapter_port());
				login1Resp.setUdp_adapter_port(set.getUdp_adapter_port());
				login1Resp.setAuth_code(set.getAuth_code());
				login1Resp.setRet("0");
				if (login1Resp != null && "0".equals(login1Resp.getRet())) {
					// 二次登录
					msg_seq = 0;
					// udp方式登录 实现断开服务器也可以上报数据
					// login2Resp = UDPUtil.sendLogin2Udp(msg_seq, set,
					// login1Resp);
					byte[] data = UMMPUtil.getLogin2ReqData(msg_seq, set,
							login1Resp);
					login2Resp = UMMPUtil.sendLogin2UDP(
							login1Resp.getAdapter_ip(),
							login1Resp.getUdp_adapter_port(), data);

					if (login2Resp != null) {
						if ("0".equals(login2Resp.getRet())
								|| "2".equals(login2Resp.getRet())) {
							if ("2".equals(login2Resp.getRet())) {
								IUtil.writeSharedPreferences(sp, login2Resp);
								/* dointerval(); */
								Message msg = handler.obtainMessage();
								msg.what = 77;
								handler.sendMessage(msg);
							}
						} else {
							FileLog.i(TAG,
									"Login2 Fail==" + login2Resp.getRet());
						}
					}
				} else {
					FileLog.i(TAG, "Login1 Fail");
				}
			} catch (Exception e) {
				FileLog.e(TAG, "refreshSet==>" + e.toString());
			} finally {
				CallBack localCallBack = LocationService.this.refreshCallBack;
				Object[] arrayOfObject = new Object[1];
				arrayOfObject[0] = login2Resp;
				localCallBack.execute(arrayOfObject);
			}
		}
	};

	private class MyPhoneStateListener extends PhoneStateListener {

		@Override
		public void onSignalStrengthsChanged(SignalStrength signalStrength) {
			super.onSignalStrengthsChanged(signalStrength);

			if (signalStrength.isGsm()) {
				if (signalStrength.getGsmSignalStrength() != 99)
					signalStrengthValue = signalStrength.getGsmSignalStrength() * 2 - 113;
				else

					signalStrengthValue = signalStrength.getGsmSignalStrength();

			} else {
				signalStrengthValue = signalStrength.getCdmaDbm();
			}
		}

	};

	
	
	

	
	class InfoAutoThread implements Runnable {
		Intent intent= new Intent("ACTION_ZHENGYUHUI");
		String resultcode = "";
		String resultcode_Audio="";
		int resultCode_Vidio=0;
		private boolean separate=false;//false视频不分片、、、、true需要分片
		@Override
		public void run() {
			isInfoUploading = true;
			try {
				// 开始执行，休息10s
				Thread.sleep(10000);
				List<InfoBean> list = DBUtil.getDataFromLInfoB(locationHelper
						.getWritableDatabase());
				for (InfoBean infoBean : list) {
					Map<String, Object> localMap = DBUtil.getDataFromLInfoByID(
							locationHelper.getWritableDatabase(),
							infoBean.getInfo_auto_id());
					if (localMap != null) {
						String title = "", remark = "", tvInfoUploadDate = "", lon = "", lat = "", imgFileName = "", videoName = "",reSoundName="";
						List<String> listImgs = new ArrayList<String>();

						if (localMap.containsKey("info_title")) {
							if (localMap.get("info_title") != null) {
								title = localMap.get("info_title").toString();
							}
						}
						if (localMap.containsKey("info_remark")) {
							if (localMap.get("info_remark") != null) {
								remark = localMap.get("info_remark").toString();
							}
						}
						if (localMap.containsKey("info_uploadDate")) {
							if (localMap.get("info_uploadDate") != null) {
								tvInfoUploadDate = localMap.get(
										"info_uploadDate").toString();
							}
						}
						if (localMap.get("info_lon") != null) {
							lon = localMap.get("info_lon").toString();
						}
						if (localMap.get("info_lat") != null) {
							lat = localMap.get("info_lat").toString();
						}
						if (localMap.containsKey("imgFile")) {
							if (localMap.get("imgFile") != null) {
								imgFileName = localMap.get("imgFile")
										.toString();
								String[] files = imgFileName.split("\\|");
								for (String s : files) {
									if (s != null && !"".equals(s)) {
										if (s.toLowerCase().endsWith("jpg")) {
											listImgs.add(s);
										}else if(s.toLowerCase().endsWith("amr")){
											reSoundName=s;
										}
										else {
											// 视频
											videoName = s;
										}
									}
								}
							}
							// 开始上传
							sp = getSharedPreferences("userdata", 0);
							SetInfo set = IUtil.initSetInfo(sp);
							String url = set.getHttpip() + Contant.ACTION;
							
							Map<String, String> map = new HashMap<String, String>();
							Map<String, String> map_Audio = new HashMap<String,String>();
							Map<String, String> map_Video = new HashMap<String,String>();
							map_Video.put("reqCode",Contant.VIDEO_UPLOAD_NEW);
							map_Video.put("type", "1");
							map_Video.put("gpsid", set.getDevice_id());
							map_Video.put("id", infoBean.getInfo_auto_id());
							
							map_Audio.put("reqCode", Contant.AUDIO_UPLOAD_ACTION);
							map_Audio.put("type", "1");
							map_Audio.put("gpsid", set.getDevice_id());
							map_Audio.put("id", infoBean.getInfo_auto_id());
							
							map.put("reqCode", Contant.GPS_DATA_UPLOAD_ACTION);
							map.put("GpsId", set.getDevice_id());
							map.put("Pin", set.getAuth_code());
							map.put("Title", title);
							map.put("Remark", remark);
							map.put("Lon", lon);
							map.put("Lat", lat);
							map.put("accuracy", "-100");
							// 新增的上传字段
							map.put("phone_time", tvInfoUploadDate);

							map.put("id", infoBean.getInfo_auto_id());
							Map<String, File> files = new HashMap<String, File>();
							Map<String, File> file_Audio = new HashMap<String, File>();
							Map<String, File> file_Video = new HashMap<String, File>();
							
							for (int i = 0; i < listImgs.size(); i++) {
								File file = new File(listImgs.get(i));
								if (file.exists())
									files.put("file" + i, file);
							}
							
							if (reSoundName != null && !"".equals(reSoundName)) {
								File file = new File(
										Environment.getExternalStorageDirectory()
												+ "/DCIM/eastelsoft/"
												+ reSoundName);
								if (file.exists())
									file_Audio.put("reSoundName", file);
							}
							
							if (videoName != null && !"".equals(videoName)) {
								File videoFile = new File(
										Environment.getExternalStorageDirectory()
												+ "/DCIM/eastelsoft/" + videoName);
								if (videoFile != null && videoFile.exists()){
									if(videoFile.length()<65536){
										files.put("file20", videoFile);
									}else{
										 String jsonStr = AndroidHttpClient.getContent(url, map,
													files, "file1");
										 jsonStr = IUtil.chkJsonStr(jsonStr);
											JSONArray array = new JSONArray(jsonStr);
												if (array.length() > 0) {
													JSONObject obj = array.getJSONObject(0);
													resultcode = obj.getString("resultcode");
											}
										separate=false;
										SeparatorUtil separator = new SeparatorUtil();
										 if(separator.separatorFile(Environment.getExternalStorageDirectory()
													+ "/DCIM/eastelsoft/" + videoName,65536))
										    {
										      FileLog.i(TAG, "文件折分成功!");
										      File Sp_video  = new File(Environment.getExternalStorageDirectory()
												+ "/DCIM/eastelsoft/");
										      File[] sp=Sp_video.listFiles();
										      
										      try {
										      for(int i=0;i<sp.length;i++){
										    	  if(sp[i].getName().contains(videoName+".")){
										    		  resultCode_Vidio++;
										    		  File uploadFile = new File(Environment.getExternalStorageDirectory()
																+ "/DCIM/eastelsoft/"+videoName+"k");
										    		  map_Video.put("ProPar", sp[i].getName().replace(".","|"));
										    		  Log.i(TAG,  map_Video.get("ProPar").toString());
										    		  if(sp[i].renameTo(uploadFile));
										    		  file_Video.put("file20", uploadFile);
										    		  String jsonStr_Video = AndroidHttpClient.getContent(url, map_Video,
										    					file_Video, "file1");
										    			jsonStr_Video = IUtil.chkJsonStr(jsonStr_Video);
										    			Log.i(TAG, "jsonStr_Video:"+jsonStr_Video.toString());
										    			JSONArray array_Video = new JSONArray(jsonStr_Video);
										    			String resultcode = "";
										    			if (array_Video.length() > 0) {
															JSONObject obj = array_Video.getJSONObject(0);
															resultcode = obj.getString("resultcode");
														}
										    			if("1".equals(resultcode)){
										    				FileLog.i(TAG, "视频片段上传成功");
										    				resultCode_Vidio--;
										    				uploadFile.delete();
										    			}
										    	  }
										      }
										      
										      if(resultCode_Vidio==0){
										    	  resultcode="1";
										      }
										      
										      } catch (Exception e) {
										    	  e.printStackTrace();
												}
										    }
										    else
										    {
										     FileLog.e(TAG, "文件折分失败!");
										    }
									}
								}
								
							}


							if(!separate){
								String jsonStr = AndroidHttpClient.getContent(url,
										map, files, "file1");
								jsonStr = IUtil.chkJsonStr(jsonStr);
								JSONArray array = new JSONArray(jsonStr);
								if (array.length() > 0) {
									JSONObject obj = array.getJSONObject(0);
									resultcode = obj.getString("resultcode");
								}
							}
							
							String jsonStr_Audio = AndroidHttpClient.getContent(url, map_Audio, file_Audio, "file1");
							jsonStr_Audio = IUtil.chkJsonStr(jsonStr_Audio);
							JSONArray array_Audio = new JSONArray(jsonStr_Audio);
							if (array_Audio.length() > 0) {
								JSONObject obj = array_Audio.getJSONObject(0);
								resultcode_Audio = obj.getString("resultcode");
							}
							
							if ("1".equals(resultcode)&&"1".equals(resultcode_Audio)) {
								// 更新表记录
								Log.i(TAG, "广播已发送");
								intent.putExtra("SEND_SUCESS", "send_success");
								sendBroadcast(intent);
								showNotificationInformation(R.drawable.notify,
										getResources().getString(R.string.app_name),
										getResources().getString(R.string.app_name),
										"信息上报上传成功");
								DBUtil.updateLInfo(
										locationHelper.getWritableDatabase(),
										infoBean.getInfo_auto_id());
							}
						}
					}
					Thread.sleep(10000); // 10秒上传一次
				}
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
			isInfoUploading = false;
		}
	};

	private void sendBroadcast(String state) {
		try {
			Intent it = new Intent("android.onlinestate.action");
			it.putExtra("onlinestate", state);
			online_states = state;
			this.sendBroadcast(it);
		} catch (Exception e) {
		}
	}

	public String getOnlineState() {
		String st;
		st = online_states;
		return st;
	}

	/**
	 * 信息上报
	 */
	public void updateInformation(String title, String remark, String lon,
			String lat, String uploadDate, String id_info, String location,
			String videoName, String[] imgs,String reSoundName,String setLongtime) {
		Thread addInfoThread = new Thread(new MAddInfoThread(title, remark,
				lon, lat, uploadDate, id_info, location, videoName, imgs,reSoundName,setLongtime));
		addInfoThread.start();
	}
	class MAddInfoThread implements Runnable {
		Intent intent= new Intent("ACTION_ZHENGYUHUI");
		private String title;
		private String remark;
		private String lon;
		private String lat;
		private String uploadDate;
		private String id_info;
		private String location;
		private String videoName;
		private String[] imgs;
		private String reSoundName;
		private String setLongtime;
		private boolean separate=false;//false视频不分片、、、、true需要分片
		String resultcode = "";
		String resultcode_Audio="";
		int resultCode_Vidio=0;

		public MAddInfoThread(String title, String remark, String lon,
				String lat, String uploadDate, String id_info, String location,
				String videoName, String[] imgs,String reSoundName,String setLongtime) {
			this.title = title;
			this.remark = remark;
			this.lon = lon;
			this.lat = lat;
			this.uploadDate = uploadDate;
			this.id_info = id_info;
			this.location = location;
			this.videoName = videoName;
			this.imgs = imgs;
			this.reSoundName=reSoundName;
			this.setLongtime = setLongtime;
		}

		@Override
		public void run() {
//			Looper.prepare();
			globalVar.setInfoLocation(null);
			globalVar.setImgs(new String[0]);
			globalVar.setVideo1("");
			globalVar.setReSoundName("");
			try {
				String mfileNames = "";
				if (imgs.length > 0) {
					for (int i = 0; i < imgs.length; i++) {
						mfileNames += imgs[i] + "|";
					}
				}

				if (videoName != null && !"".equals(videoName)){
					mfileNames += videoName + "|";
				}

				if(reSoundName != null&& !"".equals(reSoundName)){
					System.out.println("reSoundName------进入本地数据库的值-------->"+reSoundName);
					mfileNames += reSoundName + "|";
				}
				
				if (mfileNames.endsWith("|"))
					mfileNames = mfileNames.substring(0,
							(mfileNames.length() - 1));
				System.out.println("传入的setLongtime"+setLongtime);
				DBUtil.insertLInfo(locationHelper.getWritableDatabase(),
						uploadDate, title, mfileNames, remark, lon, lat,
						id_info, location, "22",setLongtime);				
//--------------------------------------------------------------------------
				sp = getSharedPreferences("userdata", 0);
				SetInfo set = IUtil.initSetInfo(sp);
				String url = set.getHttpip() + Contant.ACTION;
				
				Map<String, String> map = new HashMap<String, String>();
				Map<String, String> map_Audio = new HashMap<String, String>();
				Map<String, String> map_Video = new HashMap<String, String>();
				
				map_Video.put("reqCode", Contant.VIDEO_UPLOAD_NEW);
				map_Video.put("type", "1");
				map_Video.put("gpsid", set.getDevice_id());
				map_Video.put("id", id_info);
				
				map_Audio.put("reqCode", Contant.AUDIO_UPLOAD_ACTION);
				map_Audio.put("type", "1");
				map_Audio.put("gpsid", set.getDevice_id());
				map_Audio.put("id", id_info);
				
				map.put("reqCode", Contant.GPS_DATA_UPLOAD_ACTION);
				map.put("GpsId", set.getDevice_id());
				map.put("Pin", set.getAuth_code());
				map.put("Title", title);
				map.put("Remark", remark);
				map.put("Lon", lon);
				map.put("Lat", lat);
				map.put("accuracy", "-100");
				map.put("phone_time", uploadDate);
				map.put("id", id_info);
				
				Map<String, File> fileMap = new HashMap<String, File>();
				Map<String, File> file_Audio = new HashMap<String, File>();
				Map<String,File> file_Video = new HashMap<String ,File>();
				
				if (imgs.length > 0) {
					for (int i = 0; i < imgs.length; i++) {
						File tmpFile = new File(imgs[i]);
						if (tmpFile.exists())
							fileMap.put("file" + i, tmpFile);
					}
				}
				
				if(reSoundName !=null && !"".equals(reSoundName)){
					File reSoundFile = new File(
							Environment.getExternalStorageDirectory()
									+ "/DCIM/eastelsoft/"+reSoundName);
					if(reSoundFile!=null && reSoundFile.exists()){
						file_Audio.put("file22", reSoundFile); 
					}
				}
				
				if (videoName != null && !"".equals(videoName)) {
					File videoFile = new File(
							Environment.getExternalStorageDirectory()
									+ "/DCIM/eastelsoft/" + videoName);
					if (videoFile != null && videoFile.exists()){
						if(videoFile.length()<65536){
							fileMap.put("file20", videoFile);
						}else{
							 String jsonStr = AndroidHttpClient.getContent(url, map,
										fileMap, "file1");
							 jsonStr = IUtil.chkJsonStr(jsonStr);
								JSONArray array = new JSONArray(jsonStr);
									if (array.length() > 0) {
										JSONObject obj = array.getJSONObject(0);
										resultcode = obj.getString("resultcode");
								}
							separate=false;
							SeparatorUtil separator = new SeparatorUtil();
							 if(separator.separatorFile(Environment.getExternalStorageDirectory()
										+ "/DCIM/eastelsoft/" + videoName,65536))
							    {
							      FileLog.i(TAG, "文件折分成功!");
							      File Sp_video  = new File(Environment.getExternalStorageDirectory()
									+ "/DCIM/eastelsoft/");
							      File[] sp=Sp_video.listFiles();
							      
							      try {
							      for(int i=0;i<sp.length;i++){
							    	  if(sp[i].getName().contains(videoName+".")){
							    		  resultCode_Vidio++;
							    		  File uploadFile = new File(Environment.getExternalStorageDirectory()
													+ "/DCIM/eastelsoft/"+videoName+"k");
							    		  map_Video.put("ProPar", sp[i].getName().replace(".","|"));
							    		  Log.i(TAG,  map_Video.get("ProPar").toString());
							    		  if(sp[i].renameTo(uploadFile));
							    		  file_Video.put("file20", uploadFile);
							    		  String jsonStr_Video = AndroidHttpClient.getContent(url, map_Video,
							    					file_Video, "file1");
							    			jsonStr_Video = IUtil.chkJsonStr(jsonStr_Video);
							    			Log.i(TAG, "jsonStr_Video:"+jsonStr_Video.toString());
							    			JSONArray array_Video = new JSONArray(jsonStr_Video);
							    			String resultcode = "";
							    			if (array_Video.length() > 0) {
												JSONObject obj = array_Video.getJSONObject(0);
												resultcode = obj.getString("resultcode");
											}
							    			if("1".equals(resultcode)){
							    				FileLog.i(TAG, "视频片段上传成功");
							    				resultCode_Vidio--;
							    				uploadFile.delete();
							    			}
							    	  }
							      }
							      
							      if(resultCode_Vidio==0){
							    	  resultcode="1";
							      }
							      
							      } catch (Exception e) {
										// TODO: handle exception
							    	  e.printStackTrace();
									}
							    }
							    else
							    {
							     FileLog.e(TAG, "文件折分失败!");
							    }
						}
					}
					
				}
				
			

				if(!separate){
				String jsonStr = AndroidHttpClient.getContent(url, map,
						fileMap, "file1");
				jsonStr = IUtil.chkJsonStr(jsonStr);
				JSONArray array = new JSONArray(jsonStr);
				
					if (array.length() > 0) {
						JSONObject obj = array.getJSONObject(0);
						resultcode = obj.getString("resultcode");
					}
				}
				
				String jsonStr_Audio = AndroidHttpClient.getContent(url, map_Audio,
						file_Audio, "file1");
				jsonStr_Audio = IUtil.chkJsonStr(jsonStr_Audio);
				JSONArray array_Audio = new JSONArray(jsonStr_Audio);
				
				if (array_Audio.length() > 0) {
					JSONObject obj = array_Audio.getJSONObject(0);
					resultcode_Audio = obj.getString("resultcode");
				}
	
				if ("1".equals(resultcode)||"1".equals(resultcode_Audio)) {
					// 返回成功数据写库
					DBUtil.updateLInfo(locationHelper.getWritableDatabase(),
							id_info);
					intent.putExtra("SEND_SUCESS", "send_success");
					sendBroadcast(intent);
					showNotificationInformation(R.drawable.notify,
							getResources().getString(R.string.app_name),
							getResources().getString(R.string.app_name),
							"信息上报上传成功");
				} else {
					DBUtil.updateLInfoToFail(
							locationHelper.getWritableDatabase(), id_info);
					intent.putExtra("SEND_SUCESS", "send_success");
					sendBroadcast(intent);
					showNotificationInformation(R.drawable.notify,
							getResources().getString(R.string.app_name),
							getResources().getString(R.string.app_name),
							"信息上传失败，待网络流畅后会自动上传");
				}
			} catch (Exception e) {
				DBUtil.updateLInfoToFail(locationHelper.getWritableDatabase(),
						id_info);
				intent.putExtra("SEND_SUCESS", "send_success");
				sendBroadcast(intent);
				showNotificationInformation(R.drawable.notify, getResources()
						.getString(R.string.app_name), getResources()
						.getString(R.string.app_name), "信息上报出现异常，待网络流畅后其会自动尝试上传");
			}
//			Looper.loop();
		}
	}

	/**
	 * 信息上报
	 */
	public void updateChangeInformation(String title, String remark,
			String lon, String lat, String uploadDate, String id_info,
			String location, String videoName, String[] imgs,String reSoundName) {
		Thread addInfoThread = new Thread(new CAddInfoThread(title, remark,
				lon, lat, uploadDate, id_info, location, videoName, imgs,reSoundName));
		addInfoThread.start();

	}

	class CAddInfoThread implements Runnable {
		Intent intent= new Intent("ACTION_ZHENGYUHUI");
		private String title;
		private String remark;
		private String lon;
		private String lat;
		private String uploadDate;
		private String id_info;
		private String location;
		private String videoName;
		private String[] imgs;
		private String reSoundName;
		private boolean separate=false;//false视频不分片、、、、true需要分片
		String resultcode = "";
		String resultcode_Audio="";
		int resultCode_Vidio=0;

		public CAddInfoThread(String title, String remark, String lon,
				String lat, String uploadDate, String id_info, String location,
				String videoName, String[] imgs,String reSoundName) {
			this.title = title;
			this.remark = remark;
			this.lon = lon;
			this.lat = lat;
			this.uploadDate = uploadDate;
			this.id_info = id_info;
			this.location = location;
			this.videoName = videoName;
			this.imgs = imgs;
			this.reSoundName=reSoundName;
		}

		@Override
		public void run() {
//			Looper.prepare();
			try {
				sp = getSharedPreferences("userdata", 0);
				SetInfo set = IUtil.initSetInfo(sp);
				String url = set.getHttpip() + Contant.ACTION;
				Map<String, String> map = new HashMap<String, String>();
				Map<String, String> map_Audio = new HashMap<String, String>();
				Map<String, String> map_Video = new HashMap<String, String>();
				map_Video.put("reqCode", Contant.VIDEO_UPLOAD_NEW);
				map_Video.put("type", "1");
				map_Video.put("gpsid", set.getDevice_id());
				map_Video.put("id", id_info);
				map_Audio.put("reqCode", Contant.AUDIO_UPLOAD_ACTION);
				map_Audio.put("type", "1");
				map_Audio.put("gpsid", set.getDevice_id());
				map_Audio.put("id", id_info);
				map.put("reqCode", Contant.GPS_DATA_UPLOAD_ACTION);
				map.put("GpsId", set.getDevice_id());
				map.put("Pin", set.getAuth_code());
				map.put("Title", title);
				map.put("Remark", remark);
				map.put("Lon", lon);
				map.put("Lat", lat);
				map.put("accuracy", "-100");
				// 新增的上传字段
				map.put("phone_time", uploadDate);
				map.put("id", id_info);
				Map<String, File> fileMap = new HashMap<String, File>();
				Map<String, File> fileMap_Audio = new HashMap<String, File>();
				Map<String, File> fileMap_Video = new HashMap<String, File>();
				if (imgs.length > 0) {
					for (int i = 0; i < imgs.length; i++) {
						File tmpFile = new File(imgs[i]);
						if (tmpFile.exists())
							fileMap.put("file" + i, tmpFile);
					}
				}
				
				if (reSoundName != null && !"".equals(reSoundName)) {
					File vreSoundFile = new File(
							Environment.getExternalStorageDirectory()
									+ "/DCIM/eastelsoft/" + reSoundName);
					if (vreSoundFile != null && vreSoundFile.exists())
						fileMap_Audio.put("file22", vreSoundFile);
				}
				
				
				
				if (videoName != null && !"".equals(videoName)) {
					File videoFile = new File(
							Environment.getExternalStorageDirectory()
									+ "/DCIM/eastelsoft/" + videoName);
					if (videoFile != null && videoFile.exists()){
						if(videoFile.length()<65536){
							fileMap.put("file20", videoFile);
						}else{
							 String jsonStr = AndroidHttpClient.getContent(url, map,
										fileMap, "file1");
							 jsonStr = IUtil.chkJsonStr(jsonStr);
								JSONArray array = new JSONArray(jsonStr);
									if (array.length() > 0) {
										JSONObject obj = array.getJSONObject(0);
										resultcode = obj.getString("resultcode");
								}
							separate=false;
							SeparatorUtil separator = new SeparatorUtil();
							 if(separator.separatorFile(Environment.getExternalStorageDirectory()
										+ "/DCIM/eastelsoft/" + videoName,65536))
							    {
							      FileLog.i(TAG, "文件折分成功!");
							      File Sp_video  = new File(Environment.getExternalStorageDirectory()
									+ "/DCIM/eastelsoft/");
							      File[] sp=Sp_video.listFiles();
							      
							      try {
							      for(int i=0;i<sp.length;i++){
							    	  if(sp[i].getName().contains(videoName+".")){
							    		  resultCode_Vidio++;
							    		  File uploadFile = new File(Environment.getExternalStorageDirectory()
													+ "/DCIM/eastelsoft/"+videoName+"k");
							    		  map_Video.put("ProPar", sp[i].getName().replace(".","|"));
							    		  Log.i(TAG,  map_Video.get("ProPar").toString());
							    		  if(sp[i].renameTo(uploadFile));
							    		  fileMap_Video.put("file20", uploadFile);
							    		  String jsonStr_Video = AndroidHttpClient.getContent(url, map_Video,
							    					fileMap_Video, "file1");
							    			jsonStr_Video = IUtil.chkJsonStr(jsonStr_Video);
							    			Log.i(TAG, "jsonStr_Video:"+jsonStr_Video.toString());
							    			JSONArray array_Video = new JSONArray(jsonStr_Video);
							    			String resultcode = "";
							    			if (array_Video.length() > 0) {
												JSONObject obj = array_Video.getJSONObject(0);
												resultcode = obj.getString("resultcode");
											}
							    			if("1".equals(resultcode)){
							    				FileLog.i(TAG, "视频片段上传成功");
							    				resultCode_Vidio--;
							    				uploadFile.delete();
							    			}
							    	  }
							      }
							      
							      if(resultCode_Vidio==0){
							    	  resultcode="1";
							      }
							      
							      } catch (Exception e) {
										// TODO: handle exception
							    	  e.printStackTrace();
									}
							    }
							    else
							    {
							     FileLog.e(TAG, "文件折分失败!");
							    }
						}
					}
					
				}
				

	
				if(!separate){
					String jsonStr = AndroidHttpClient.getContent(url, map,
							fileMap, "file1");
					jsonStr = IUtil.chkJsonStr(jsonStr);
					JSONArray array = new JSONArray(jsonStr);
					if (array.length() > 0) {
						JSONObject obj = array.getJSONObject(0);
						resultcode = obj.getString("resultcode");
					}
					
				}
				String jsonStr_Audio = AndroidHttpClient.getContent(url, map_Audio,
						fileMap_Audio, "file1");
		
				
				jsonStr_Audio = IUtil.chkJsonStr(jsonStr_Audio);
				
				JSONArray array_Audio = new JSONArray(jsonStr_Audio);
		
				
				if (array_Audio.length() > 0) {
					JSONObject obj = array_Audio.getJSONObject(0);
					resultcode_Audio = obj.getString("resultcode");
				}

				if ("1".equals(resultcode)&&"1".equals(resultcode_Audio)) {
					// 返回成功数据写库
					DBUtil.updateLInfo(locationHelper.getWritableDatabase(),
							id_info);
					intent.putExtra("SEND_SUCESS", "send_success");
					sendBroadcast(intent);
					showNotificationInformation(R.drawable.notify,
							getResources().getString(R.string.app_name),
							getResources().getString(R.string.app_name),
							"信息上报上传成功");
				} else {
					intent.putExtra("SEND_SUCESS", "send_success");
					sendBroadcast(intent);
					showNotificationInformation(R.drawable.notify,
							getResources().getString(R.string.app_name),
							getResources().getString(R.string.app_name),
							"信息上传失败，待网络流畅后会自动上传");
				}
			} catch (Exception e){
				intent.putExtra("SEND_SUCESS", "send_success");
				sendBroadcast(intent);
				showNotificationInformation(R.drawable.notify, getResources()
						.getString(R.string.app_name), getResources()
						.getString(R.string.app_name), "信息上报出现异常，待网络流畅后其会自动尝试上传");
			}
//			Looper.loop();
		}
	}
}
