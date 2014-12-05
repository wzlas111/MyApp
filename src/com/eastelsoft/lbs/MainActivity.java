/**
 * Copyright (c) 2012-7-21 www.eastelsoft.com
 * $ID MainActivity.java 上午12:42:13 $
 */
package com.eastelsoft.lbs;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;



//import com.baidu.mapapi.BMapManager;
import com.eastelsoft.lbs.CheckinOnMapActivity.MyReceiver;
import com.eastelsoft.lbs.CheckinOnMapActivity.OverItemT;
import com.eastelsoft.lbs.CustActivity.DataThread;
import com.eastelsoft.lbs.CustActivity.InitThread;
import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.activity.client.ClientActivity;
import com.eastelsoft.lbs.activity.dealer.DealerActivity;
import com.eastelsoft.lbs.adapter.MainGridViewAdapter;
import com.eastelsoft.lbs.clock.DeskClockMainActivity;
import com.eastelsoft.lbs.db.DBUtil;
import com.eastelsoft.lbs.db.LocationSQLiteHelper;
import com.eastelsoft.lbs.entity.SetInfo;
import com.eastelsoft.lbs.service.LocationService;
import com.eastelsoft.lbs.service.LocationService.MBinder;
import com.eastelsoft.lbs.service.ProcessService;
import com.eastelsoft.util.Contant;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.GlobalVar;
import com.eastelsoft.util.IUtil;
import com.eastelsoft.util.contact.PingYinUtil;
import com.eastelsoft.util.http.AndroidHttpClient;
import com.mapabc.mapapi.core.GeoPoint;

/**
 * 主界面
 * 
 * @author lengcj
 */
public class MainActivity extends BaseActivity {
	public static final String TAG = "MainActivity";
	private GridView maingv;
	private LinearLayout pb_ll;
	private SetInfo set;
	private LocationService locationService;
	private boolean mBound = false;
	// 平台标志
	private TextView tv_lt;
	// 在线状态
	private ImageView online_state;
	private MyStateReceiver receiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		

		// 检测上下文数据
		sp = getSharedPreferences("userdata", 0);
		set = IUtil.initSetInfo(sp);
		telephonyManager = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		imei = telephonyManager.getDeviceId();
		// imei = "4356";
		imsi = telephonyManager.getSubscriberId();
		if (imsi == null || "".equals(imsi)) {
			imsi = "11111111";
		}

		// FileLog.i(TAG, imei+"+"+imsi);
		online_state = (ImageView) findViewById(R.id.online_state);
		// 注册广播接收器，接收状态
		receiver = new MyStateReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction("android.onlinestate.action");
		this.registerReceiver(receiver, filter);

		if (set.getSerialNumber().length() == 0
				|| set.getGateip().length() == 0
				|| set.getAdapter_ip().length() == 0
				|| set.getAdapter_ip().equals("124.160.28.92")
				|| !set.getImei().equals(imei) || !set.getImsi().equals(imsi)) {
			// 初次安装或者系统参数丢失，进入注册页面，开始或者重新注册
			Intent intent = new Intent();
			intent.setClass(MainActivity.this, RegActivity.class);
			startActivity(intent);
			// 停止当前的Activity,如果不写,则按返回键会跳转回原来的Activity
			finish();
		} else {
			// 启动或者绑定service
			networkAvailable = isNetworkAvailable();
			if (!networkAvailable) {
				respMsg = getResources().getString(R.string.net_error);
				Toast.makeText(getApplicationContext(), respMsg,
						Toast.LENGTH_SHORT).show();
			}
			// 检查版本更新
			Thread checkVersionTask = new Thread(new CheckVersionTask(this));
			checkVersionTask.start();
			// 启动并绑定service
			this.startService(new Intent(this, LocationService.class));
			Intent intent = new Intent(
					"com.eastelsoft.lbs.service.LocationService");
			this.getApplicationContext().bindService(intent, sc,
					Context.BIND_AUTO_CREATE);

			// 启动一个定时器
			globalVar = (GlobalVar) this.getApplication();
			globalVar.setTimenumber(0);
			itt = new Intent("com.eastelsoft.lbs.MyTimeReceiver");
			sender = PendingIntent.getBroadcast(this, 0, itt,
					PendingIntent.FLAG_CANCEL_CURRENT);
			this.setAlarmTime(120000); // 启动后1分钟执行

		}
		// 获取到GridView
		maingv = (GridView) this.findViewById(R.id.gv_all);
		pb_ll = (LinearLayout) this.findViewById(R.id.pb_ll);
		// 给gridview设置数据适配器
		// // String sm = "01,02,03,04,05,06,07,99";
		// String sm = "";
		// maingv.setAdapter(new MainGridViewAdapter(this, sm));
		// 点击事件
		maingv.setOnItemClickListener(new MainItemClickListener());
		tv_lt = (TextView) findViewById(R.id.tv_lt);
		if ("0".equals(set.getPfsign())) {
			tv_lt.setVisibility(View.GONE);
		}

		/**
		 * 使用地图sdk前需先初始化BMapManager. BMapManager是全局的，可为多个MapView共用，它需要地图模块创建前创建，
		 * 并在地图地图模块销毁后销毁，只要还有地图模块在使用，BMapManager就不应该销毁
		 */
		// 获取菜单线程
		maingv.setVisibility(View.GONE);
		pb_ll.setVisibility(View.VISIBLE);
		dataThread = new Thread(new DataThread());
		dataThread.start();

	}

	private Thread dataThread;

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
//		LocationSQLiteHelper helper = new LocationSQLiteHelper(this,null,null,5);
//		SQLiteDatabase db = helper.getReadableDatabase();
//		Cursor c = db.query("l_info",
//				new String[]{"info_auto_id","uploadDate","title","imgFile","remark","location","lon","lat","istijiao","setLongtime"},
//				null,null,null,null,null);
//		if(c.moveToFirst()){//判断游标是否为空
//		    for(int i=0;i<c.getCount();i++){
//		        c.move(i);//移动到指定记录
//		        Log.e("L_INFO", 
//		        "info_auto_id="+ c.getString(c.getColumnIndex("info_auto_id"))+
//		        "uploadDate="+ c.getString(c.getColumnIndex("uploadDate"))+
//		        "title="+ c.getString(c.getColumnIndex("title"))+
//		        "imgFile="+ c.getString(c.getColumnIndex("imgFile"))+
//		        "remark="+ c.getString(c.getColumnIndex("remark"))+
//		        "location="+ c.getString(c.getColumnIndex("location"))+
//		        "lon="+c.getString(c.getColumnIndex("lon"))+
//		        "lat="+c.getString(c.getColumnIndex("lat"))+
//		        "istijiao="+ c.getString(c.getColumnIndex("istijiao"))+
//		        "setLongtime="+ c.getString(c.getColumnIndex("setLongtime"))
//		        );
//		    }
//
//		}
//		
//		
		if (locationService != null) {
			String state = locationService.getOnlineState();
			if (Contant.LINGING.equals(state)) {
				online_state.setImageResource(R.drawable.stat_lineing);
			} else if (Contant.ON_LINE.equals(state)) {
				online_state.setImageResource(R.drawable.stat_online);
			} else {
				online_state.setImageResource(R.drawable.stat_offline);
			}
		}
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}

	private class MainItemClickListener implements OnItemClickListener {
		/**
		 * @param parent
		 *            代表当前的gridview
		 * @param view
		 *            代表点击的item
		 * @param position
		 *            当前点击的item在适配中的位置
		 * @param id
		 *            当前点击的item在哪一行
		 */
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {

			Intent intent = null;
			TextView tv = (TextView) view.findViewById(R.id.main_gv_tv);
			// toast("" + view.getId() + ":" + tv.getText());
			if (Contant.MENUS_MAP.get("01").equalsIgnoreCase(
					tv.getText().toString())) {
				intent = new Intent(MainActivity.this, PlanActivity.class);
			}
			if (Contant.MENUS_MAP.get("02").equalsIgnoreCase(
					tv.getText().toString())) {
				intent = new Intent(MainActivity.this,
						CheckinOnBaiduMapActivity.class);
				intent.putExtra("reportTag", "1");
			}
			if (Contant.MENUS_MAP.get("03").equalsIgnoreCase(
					tv.getText().toString())) {
				intent = new Intent(MainActivity.this,
						CheckinOnBaiduMapActivity.class);
				intent.putExtra("reportTag", "0");
			}
			if (Contant.MENUS_MAP.get("04").equalsIgnoreCase(
					tv.getText().toString())) {
				intent = new Intent(MainActivity.this, InfoActivity.class);
			}
			if (Contant.MENUS_MAP.get("05").equalsIgnoreCase(
					tv.getText().toString())) {
				intent = new Intent(MainActivity.this, CustActivity.class);
			}
			if (Contant.MENUS_MAP.get("06").equalsIgnoreCase(
					tv.getText().toString())) {
				intent = new Intent(MainActivity.this,
						BaifangjiluActivity.class);
			}
			if (Contant.MENUS_MAP.get("07").equalsIgnoreCase(
					tv.getText().toString())) {
				intent = new Intent(MainActivity.this, SalesvMainActivity.class);
			}
			if (Contant.MENUS_MAP.get("10").equalsIgnoreCase(
					tv.getText().toString())) {
				intent = new Intent(MainActivity.this,
						BulletinListActivity.class);
			}

			if (Contant.MENUS_MAP.get("11").equalsIgnoreCase(
					tv.getText().toString())) {
				intent = new Intent(MainActivity.this,
						KnowledgeBaseListActivity.class);
				intent.putExtra("info_auto_id", "firstpage");
			}
			// if (Contant.MENUS_MAP.get("98").equalsIgnoreCase(
			// tv.getText().toString())) {
			// intent = new Intent(MainActivity.this,
			// DeskClockMainActivity.class);
			// }
			if (Contant.MENUS_MAP.get("24").equalsIgnoreCase(
					tv.getText().toString())) {
				intent = new Intent(MainActivity.this, ClientActivity.class);
			}
			if (Contant.MENUS_MAP.get("25").equalsIgnoreCase(
					tv.getText().toString())) {
				intent = new Intent(MainActivity.this, DealerActivity.class);
			}
			if (Contant.MENUS_MAP.get("99").equalsIgnoreCase(
					tv.getText().toString())) {
				intent = new Intent(MainActivity.this, SetallActivity.class);
			}

			if (intent != null)
				startActivity(intent);
			else
				Toast.makeText(MainActivity.this,
						"[" + tv.getText() + "]开发中，敬请期待。。。", Toast.LENGTH_SHORT)
						.show();
		}
	}

	@Override
	protected void onDestroy() {
		if (mBound) {
			this.getApplicationContext().unbindService(sc);
			mBound = false;
		}
		if (receiver != null) {
			this.unregisterReceiver(receiver);
			receiver = null;
		}
		super.onDestroy();
	}

	/** 定交ServiceConnection，用于绑定Service的 */
	private ServiceConnection sc = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			// 已经绑定了LocalService，强转IBinder对象，调用方法得到LocalService对象
			MBinder binder = (MBinder) service;
			locationService = binder.getService();
			mBound = true;
			if (locationService != null) {
				String state = locationService.getOnlineState();
				if (Contant.LINGING.equals(state)) {
					online_state.setImageResource(R.drawable.stat_lineing);
				} else if (Contant.ON_LINE.equals(state)) {
					online_state.setImageResource(R.drawable.stat_online);

				} else {
					online_state.setImageResource(R.drawable.stat_offline);

				}

			}
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
		}
	};

	public class MyStateReceiver extends BroadcastReceiver {
		// 自定义一个广播接收器
		@Override
		public void onReceive(Context context, Intent intent) {
			try {

				Bundle bundle = intent.getExtras();
				String state = bundle.getString("onlinestate");
				if (Contant.LINGING.equals(state)) {

					online_state.setImageResource(R.drawable.stat_lineing);

				} else if (Contant.ON_LINE.equals(state)) {
					online_state.setImageResource(R.drawable.stat_online);

				} else {
					online_state.setImageResource(R.drawable.stat_offline);

				}

			} catch (Exception e) {
			}
		}
	}

	private Intent itt;
	private PendingIntent sender;
	private AlarmManager am;

	public void setAlarmTime(long timeInMillis) {
		if (am != null) {
			am.cancel(sender);
		}
		am = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

		am.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
				+ timeInMillis, 600 * 1000, sender);

	}

	class DataThread implements Runnable {
		@Override
		public void run() {
			Message msg = handler.obtainMessage();
			msg.what = 0;
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
					// {"resultcode":"1","e_menu":"01,02,08,09,0801,0803","img_quality":"0",
					// "img_px","0","img_num":"3","cycle_datetime":"1,2,3,4,5,6,7",
					// "leach_date":"20131212","time_section":"09:00-18:00","prequenc":"300"}
					// {"img_num":"5","img_quality":"1","cycle_datetime":"1111111","resultcode":"1",
					// "prequenc":"180","img_px":"1","time_section":"00:00-17:00","leach_date":
					// "20121224","e_menu":"01,02,03,04,05,06,07,08,09,99"}
					JSONObject obj1 = array1.getJSONObject(0);
					String resultcode = obj1.getString("resultcode");
					if ("1".equals(resultcode)) {
						String e_menu = obj1.getString("e_menu");
						IUtil.writeSharedPreference(sp, "e_menu", e_menu);
						String img_num = obj1.getString("img_num");
						IUtil.writeSharedPreference(sp, "img_num", img_num);
					}

				}

			} catch (Exception e) {
				FileLog.e(TAG, "getMenu==>" + e.toString());
			} finally {
				handler.sendMessage(msg);
			}
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try {
				switch (msg.what) {
				case 0:
					String e_menu = sp.getString("e_menu", "");
					maingv.setAdapter(new MainGridViewAdapter(
							MainActivity.this, e_menu));
					maingv.setVisibility(View.VISIBLE);
					pb_ll.setVisibility(View.GONE);
					break;
				default:
					break;
				}
			} catch (Exception e) {
				// 异常中断
			}
		}
	};

}
