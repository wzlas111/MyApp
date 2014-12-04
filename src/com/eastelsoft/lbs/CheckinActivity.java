/**
 * Copyright (c) 2012-7-21 www.eastelsoft.com
 * $ID TabCheckinActivity.java 上午12:42:13 $
 */
package com.eastelsoft.lbs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
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
import com.eastelsoft.lbs.activity.BaseActivity;

import com.eastelsoft.lbs.adapter.LocListAdapterA;
import com.eastelsoft.lbs.db.DBUtil;
import com.eastelsoft.lbs.db.LocationSQLiteHelper;
import com.eastelsoft.lbs.entity.ReportResp;
import com.eastelsoft.lbs.entity.SetInfo;
import com.eastelsoft.lbs.entity.LocBean;
import com.eastelsoft.lbs.location.BaseStationAction.SItude;
import com.eastelsoft.lbs.service.LocationService;
import com.eastelsoft.lbs.service.LocationService.MBinder;
import com.eastelsoft.util.CallBack;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.GlobalVar;
import com.eastelsoft.util.IUtil;
import com.eastelsoft.util.Util;

/**
 * 签到签退页面
 * 
 * @author lengcj
 */
public class CheckinActivity extends BaseActivity {

	private static final String TAG = "TabCheckinActivity";

	private boolean hasPopup = false;
	private Button btOK;
	private Button btOutOK;
	// private ImageView btOKImg;
	// private ImageView btOutImg;
	private TextView btOKText;
	private TextView btOutText;
	//private Button btOK2;
	//private Button btOutOK2;


	private Button btBack;
	private TextView tvCheckTitle;
	private Thread gpsThread;

	private Thread checkThread;

	private MyReceiver receiver;
	private String msg_seq = "";

	private SetInfo set;
	private String cknotdisplay="";
	private LocationService locationService;
	private boolean mBound = false;
	// 电池电量 %
	protected String power;

	private Intent intent;
	private String reportTag;

	private ListView lv;
	//private SimpleAdapter listItemAdapter;
	private LocationSQLiteHelper locationHelper;
	//private List<HashMap<String, Object>> mData;
	
	private boolean isGps = false;
	

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_checkin);
		btBack = (Button) findViewById(R.id.btBack);
		btBack.setOnClickListener(new OnBtBackClickListenerImpl());
		btOK = (Button) findViewById(R.id.btOK);
		btOutOK = (Button) findViewById(R.id.btOutOK);
		btOK.setOnClickListener(new OnBtCheckClickListenerImpl("1"));
		btOutOK.setOnClickListener(new OnBtCheckClickListenerImpl("0"));
		tvCheckTitle = (TextView) findViewById(R.id.tvCheckTitle);
		// btOKImg = (ImageView)findViewById(R.id.btOKImg);
		// btOutImg = (ImageView)findViewById(R.id.btOutImg);
		// btOKImg.setOnClickListener(new OnBtCheckClickListenerImpl("1"));
		// btOutImg.setOnClickListener(new OnBtCheckClickListenerImpl("0"));
		btOKText = (TextView) findViewById(R.id.btOKText);
		btOutText = (TextView) findViewById(R.id.btOutText);
		btOKText.setOnClickListener(new OnBtCheckClickListenerImpl("1"));
		btOutText.setOnClickListener(new OnBtCheckClickListenerImpl("0"));
		
//		btOK2 = (Button) findViewById(R.id.btOK2);
//		btOutOK2 = (Button) findViewById(R.id.btOutOK2);
//		btOK2.setOnClickListener(new OnBtCheckClickListenerImpl("1"));
//		btOutOK2.setOnClickListener(new OnBtCheckClickListenerImpl("0"));
		lv = (ListView) findViewById(android.R.id.list);
		footer = getLayoutInflater().inflate(R.layout.footer, null);
		lv.setOnScrollListener(new OnScrollListener(){  
	        	public void onScrollStateChanged(AbsListView view, int scrollState) {  
	        	}  
	            public void onScroll(AbsListView view, int firstVisibleItem,  
	        	int visibleItemCount, int totalItemCount) {
	            	
	            	    			
	    			if((firstVisibleItem+visibleItemCount)==totalItemCount){//达到数据的最后一条记录
	    				if(totalItemCount > 0){
	    					//当前页
	    					
	    					/*int currentpage = totalItemCount%number == 0 ? totalItemCount/number : totalItemCount/number+1;
	    					FileLog.i(TAG, currentpage);
	    					FileLog.i(TAG, maxpage);*/
	    					//int nextpage = currentpage + 1;//下一页
	    					 
	    					if((totalItemCount<totalCount) && loadfinish){
	    						
	    					loadfinish = false;
							lv.addFooterView(footer);
	    			        new Thread(new AsyncUpdateDatasThread()).start();
	    					}
	    			        
	    				}
	        	    }

	    			
	            }

	     });  
		sp = getSharedPreferences("userdata", 0);
		set = IUtil.initSetInfo(sp);
		cknotdisplay = sp.getString("cknotdisplay", "");
		intent = this.getIntent();
		reportTag = intent.getStringExtra("reportTag");
		String checkTitle = "";
		if ("1".equals(reportTag)) {
			btOutText.setVisibility(View.GONE);
			btOutOK.setVisibility(View.GONE);
//			btOutOK2.setVisibility(View.GONE);
			checkTitle = getResources().getString(
					R.string.title_activity_checkin);
			tvCheckTitle.setText(checkTitle);
		} else {
			btOKText.setVisibility(View.GONE);
			btOK.setVisibility(View.GONE);
//			btOK2.setVisibility(View.GONE);
			checkTitle = getResources().getString(
					R.string.title_activity_checkout);
			tvCheckTitle.setText(checkTitle);
		}
		// 业务分发
		if (set.getSerialNumber().length() == 0) {
			// 初次安装或者系统参数丢失，进入注册页面，开始或者重新注册
			Intent intent = new Intent();
			intent.setClass(CheckinActivity.this, RegActivity.class);
			startActivity(intent);
			// 停止当前的Activity,如果不写,则按返回键会跳转回原来的Activity
			finish();
		} else {

			networkAvailable = isNetworkAvailable();
			// 注册一个接受广播类型，电量
			registerReceiver(batteryChangedReceiver, new IntentFilter(
					Intent.ACTION_BATTERY_CHANGED));

			// 初始化全局变量
			globalVar = (GlobalVar) getApplicationContext();

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
			
			// 注册广播接收器，接收定位数据信息
			receiver=new MyReceiver();
			IntentFilter filter=new IntentFilter();
			filter.addAction("android.location.action");
			this.registerReceiver(receiver,filter);

			locationHelper = new LocationSQLiteHelper(this, null, null, 5);
			init();
		}
	}
	// 定义适配器  
		private LocListAdapterA listadpter;  
		private ArrayList<LocBean> all_info = new ArrayList<LocBean>();
		private ArrayList<LocBean> arrayList = new ArrayList<LocBean>();  
		
		private int number = 20;//每次获取多少条数据
		//总数
		private int totalCount=0;
		//总页数
		private int maxpage =0 ; 
		private boolean loadfinish = true;
		private int index = 0; 
		 
		View footer;
		class AsyncUpdateDatasThread implements Runnable {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				  
	            
				index += number;  

				List<LocBean> list = new ArrayList<LocBean>();
				

				/*list = DBUtil.getAllItems(index, number,locationHelper.getWritableDatabase()); */ 
				list = getNextpageItem(index,number,all_info);
				FileLog.i(TAG, "list "+list.size());

				Message msg = handlerA.obtainMessage();
				msg.obj = list;
				msg.what = 0;
				handlerA.sendMessage(msg);  

				
				
			}
			
		}
		private Handler handlerA = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				// TODO Auto-generated method stub
				switch (msg.what) {
				case 0:
					arrayList.addAll((List<LocBean>)(msg.obj));
					
					listadpter.notifyDataSetChanged(); 
					
					if(lv.getFooterViewsCount() > 0) lv.removeFooterView(footer);
					loadfinish = true;  

					break;
	            case 1:
					break;	
				}
			}
			
			
		};
	

	private void init() {
		// 做几条测试数据
		// 写本地数据库
		// DBUtil.insertLLoc(locationHelper.getWritableDatabase(),
		// Util.getLocaleTime("yyyy-MM-dd hh:mm:ss"),
		// reportTag,
		// "120.1232",
		// "30.76122",
		// "",
		// "35米");

		// ////////////////////////////////////
		/*if (mData != null)
			mData.clear();
		mData = DBUtil.getDataFromLLoc(locationHelper.getWritableDatabase(),
				reportTag);
		listItemAdapter = new SimpleAdapter(this, mData,// 数据源
				R.layout.location_list_item,// ListItem的XML实现
				// 动态数组与ImageItem对应的子项
				new String[] { "locTime", "accuracy" },
				// ImageItem的XML文件里面的一个ImageView,两个TextView ID
				new int[] { R.id.locTime, R.id.accuracy });
		lv.setAdapter(listItemAdapter);*/
		
		//index每次清0
		index=0;
		/*ArrayList<LocBean> xxx = new ArrayList<LocBean>();
		for(int i =0;i<200;i++){
			LocBean lb = new LocBean();  
			lb.setLocTime("222211112");  
			lb.setAccuracy("12");
			xxx.add(lb);	
		}
		all_info=xxx;*/
		all_info = DBUtil.getDataFromLLocA(locationHelper.getWritableDatabase(), reportTag);
		totalCount =all_info.size();
		FileLog.i(TAG, totalCount);
		maxpage = totalCount%number == 0 ? totalCount/number : totalCount/number+1;
		FileLog.i(TAG, maxpage);
		
		arrayList = getNextpageItem(index,number,all_info);
		FileLog.i(TAG, arrayList);
		
		// 实例化适配器  
		listadpter = new LocListAdapterA(CheckinActivity.this, arrayList);
		// 填充适配器
		lv.addFooterView(footer);//添加页脚(放在ListView最后)
		lv.setAdapter(listadpter); 
		
		lv.removeFooterView(footer);
		
		
	}
	private ArrayList<LocBean> getNextpageItem(int index ,int number, ArrayList<LocBean> al) {
		ArrayList<LocBean> a =  new ArrayList<LocBean>();
		for(int i =0;i<number;i++){
			int temp =i+index;
			if(temp < 0 || temp >= al.size()){
				break;	
			}else{
				LocBean ib =new LocBean();
				ib = al.get(temp);
				a.add(ib);	
			}
		}
		
		return a;
	}



	@Override
	public void onWindowFocusChanged(boolean hasFocus) {
		
		
		if (hasFocus && !hasPopup&&!"yes".equals(cknotdisplay)) {
			hasPopup = true;
			openPopupWindowck();
			// 定位60秒
			this.timeoutHandler.postDelayed(this.timeoutRunnable, 5000L);
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		FileLog.i(TAG, "onDestroy....");
		try {
			if (gpsThread != null) {
				gpsThread.interrupt();
				gpsThread = null;
			}
			if (mBound) {
				this.getApplicationContext().unbindService(sc);
				mBound = false;
			}

			if (batteryChangedReceiver != null) {
				this.unregisterReceiver(batteryChangedReceiver);
				batteryChangedReceiver = null;
			}
			super.onDestroy();
			if (locationHelper != null) {
				locationHelper.getWritableDatabase().close();
			}
			if(receiver != null) {
				this.unregisterReceiver(receiver);
				receiver = null;
			}
		} catch (Exception e) {
		}
	}

	@Override
	protected void onStop() {
		FileLog.i(TAG, "onStop....");
		if (pDialog != null) {
			pDialog.cancel();
		}
		if (gpsThread != null) {
			gpsThread.interrupt();
			gpsThread = null;
		}

		super.onStop();

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

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try {
				/**
				pDialog.cancel();

				if (gpsThread != null) {
					gpsThread.interrupt();
					gpsThread = null;
				}
				*/
				try {
					popupWindowPg.dismiss();
				} catch (Exception e) {
					FileLog.e(TAG, e.toString());
				}
				IUtil.writeSharedPreference(sp, "chkact", "0");
				switch (msg.what) {
				case 2:
					dialog(CheckinActivity.this, msg.obj.toString());
					CheckinActivity.this.init();
					break;
				case 1:
					// 异常转回注册页面
					Intent intent = new Intent();
					intent.setClass(CheckinActivity.this, RegActivity.class);
					startActivity(intent);
					finish();
					break;
				case 0:
					// 登录成功
					break;
				default:
					respMsg = getResources().getString(R.string.checkin_error);
					dialog(CheckinActivity.this, respMsg);
					break;
				}

			} catch (Exception e) {
				// 异常中断
				respMsg = getResources().getString(R.string.checkin_error);
				dialog(CheckinActivity.this, respMsg);
			}
		}
	};

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
				// openPopupWindow();

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
						
						
						/*if (!((LocationManager) getSystemService(Context.LOCATION_SERVICE))
								.isProviderEnabled("gps")) {
							openGps();*/
							/*respMsg = getResources().getString(
									R.string.gps_error);
							dialog(CheckinActivity.this, respMsg);*/

						/*} */
						
						/**
						pDialog = new ProgressDialog(CheckinActivity.this);
						pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
						pDialog.setTitle(getResources().getString(
								R.string.checkin_tips));
						pDialog.setMessage(getResources().getString(
								R.string.gps_find));
						pDialog.setIndeterminate(false);
						pDialog.setProgress(100);
						pDialog.show();

						if (gpsThread != null) {
							gpsThread.interrupt();
							gpsThread = null;
						}

						// 100秒的等待时间，gps搜星50秒，发送消息最大40秒，10秒预留
						gpsThread = new Thread(new GpsThread());
						gpsThread.start();
						*/
						isGps = false;
						
						CheckinActivity.this.openPopupWindowPG("");
						checkThread = new Thread(new CheckThread(reportTag));
						checkThread.start();

						
					}
				}
			} catch (Exception e) {
				FileLog.e("TabCheckinActivity checkin", "error");
				dialog(CheckinActivity.this,
						getResources().getString(R.string.check_error_login));
			}
		} // */
	}

	class GpsThread implements Runnable {

		@Override
		public void run() {
			int n_count = 0;
			try {
				while (n_count <= 25) {
					// 由线程来控制进度。
					pDialog.setProgress(n_count++);
					Thread.sleep(1000);
				}
			} catch (InterruptedException e) {
			}
		}
	}

	// *
	class CheckThread implements Runnable {
		private String reportTag;

		public CheckThread(String reportTag) {
			this.reportTag = reportTag;
		}

		@Override
		public void run() {
			Looper.prepare();
			locationService.startListener(reportTag, locationCallback);

			Looper.loop();
		}
	}

	// */

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
			String isSocketOk =(String) paramArrayOfObject[3];
			if (location == null && sItude == null) {
				if(isSocketOk.equals("socketisgood")){
					respMsg = getResources().getString(R.string.location_error);
					
				}else{
					respMsg =getResources().getString(R.string.socketnotready);	
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
						if(location != null) {
							float tmp = location.getAccuracy();
							float tmpend =tmp;
							if(tmp < 0)
								tmpend =-tmp;
							location.setAccuracy(tmpend);
							
							SimpleDateFormat sdf= new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
							Date dt = new Date(location.getTime()); 
							String sDateTime = sdf.format(dt);
							
							DBUtil.insertLLoc(locationHelper.getWritableDatabase(),
									sDateTime,
									reportTag, Util.format(location.getLongitude(),
											"#.######"), Util.format(
											location.getLatitude(), "#.######"),
									"", Util.format(String.valueOf(location
											.getAccuracy()))+"米");
						}
						if(sItude != null) {
							DBUtil.insertLLoc(locationHelper.getWritableDatabase(),
									Util.getLocaleTime("yyyy-MM-dd HH:mm:ss"),
									reportTag, Util.format(Double.parseDouble(sItude.longitude),
											"#.######"), Util.format(
											Double.parseDouble(sItude.latitude), "#.######"),
									"", Util.format(sItude.accuracy)+"米");
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

	/**
	 * 开启或者关闭GPS
	 */
	public void openGps() {
		// 2.1版本
		Intent GPSIntent = new Intent();
		GPSIntent.setClassName("com.android.settings",
				"com.android.settings.widget.SettingsAppWidgetProvider");
		GPSIntent.addCategory("android.intent.category.ALTERNATIVE");
		GPSIntent.setData(Uri.parse("custom:3"));
		try {
			PendingIntent.getBroadcast(this, 0, GPSIntent, 0).send();
		} catch (CanceledException e) {
			e.printStackTrace();
		}
		// 2.2以上版本 -- 程序会报错
		// Settings.Secure.setLocationProviderEnabled(act.getContentResolver(),
		// LocationManager.GPS_PROVIDER, true);
	}

	public class MyReceiver extends BroadcastReceiver {
		// 自定义一个广播接收器
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				FileLog.i(TAG, "OnReceiver");
				Bundle bundle = intent.getExtras();
				//byte[] data = bundle.getByteArray("data");
				String provider = bundle.getString("provider");
				if(provider.equalsIgnoreCase("gps")) {
					isGps = true;
				}
				String location_desc = bundle.getString("location_desc");
				if("".equals(location_desc)) {
					// 暂时未用到
					String type = bundle.getString("type");
					if("out".equals(type)) {
						msg_seq = bundle.getString("msg_seq");
					}
					if("in".equals(type)) {
						String in_msg_seq = bundle.getString("msg_seq");
						if(!"".equals(in_msg_seq) && msg_seq.equals(in_msg_seq)) {
							msg_seq = "";
						}
					}
				} else {
					if(isGps) {
						if("provider".equalsIgnoreCase("GPS")) {
							btPopGps.setText(location_desc);
						}
					} else {
						btPopGps.setText(location_desc);
					}
				}
				//FileLog.i(TAG, "OnReceiver data==>" + data);
				FileLog.i(TAG, "OnReceiver location_desc==>" + location_desc);
			} catch (Exception e) {
			}
		}
	}

	/**
	 * 电池电量
	 */
	protected BroadcastReceiver batteryChangedReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
				int level = intent.getIntExtra("level", 0);
				int scale = intent.getIntExtra("scale", 100);
				// power = (level * 100 / scale) + "%";
				power = String.valueOf(level * 100 / scale);
			}
		}
	};

	private class OnBtBackClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				CheckinActivity.this.finish();
				// openPopupWindow();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	private Handler timeoutHandler = new Handler();
	private Runnable timeoutRunnable = new Runnable() {
		public void run() {
			CheckinActivity.this.popupWindow.dismiss();
			FileLog.i(TAG, "location timeout");
		}
	};
	
	private void openPopupWindowck() {
		try {
			LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			ViewGroup menuView = (ViewGroup) mLayoutInflater.inflate(
					R.layout.pop_check, null, true);
			btClose = (Button) menuView.findViewById(R.id.btClose);
			btClose.setOnClickListener(new OnBtCloseClickListenerImpl());
			cb_nosee = (CheckBox) menuView.findViewById(R.id.cb_nosee);
			cb_nosee.setOnCheckedChangeListener(new OnBtCkClickListenerImpl());
			
			popupWindow = new PopupWindow(menuView, LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT, true); // 全部背景置灰
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
			popupWindow.setAnimationStyle(R.style.PopupAnimation);
			popupWindow.showAtLocation(findViewById(R.id.parent), Gravity.CENTER
					| Gravity.CENTER, 0, 0);
			popupWindow.update();
		} catch (Exception e) {

		}

	}
	private class OnBtCloseClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				popupWindow.dismiss();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}
	private class OnBtCkClickListenerImpl implements OnCheckedChangeListener {

		@Override
		public void onCheckedChanged(CompoundButton buttonView,
				boolean isChecked) {
			sp = getSharedPreferences("userdata",0);

			if(isChecked){
				IUtil.writeSharedPreference(sp, "cknotdisplay", "yes");
				
			} else {
				IUtil.writeSharedPreference(sp, "cknotdisplay", "no");
			}
			
			
		}
		
	}
}