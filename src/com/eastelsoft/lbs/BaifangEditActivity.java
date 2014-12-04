/**
 * Copyright (c) 2012-8-13 www.eastelsoft.com
 * $ID InfoAddActivity.java 下午9:47:37 $
 */
package com.eastelsoft.lbs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.adapter.CustSelectAdapter;
import com.eastelsoft.lbs.db.DBUtil;
import com.eastelsoft.lbs.db.LocationSQLiteHelper;
import com.eastelsoft.lbs.entity.CustBean;
import com.eastelsoft.lbs.entity.SetInfo;
import com.eastelsoft.lbs.location.AMapAction;
import com.eastelsoft.lbs.location.BaiduMapAction;
import com.eastelsoft.lbs.location.BaseStationAction;
import com.eastelsoft.lbs.location.BaseStationAction.SItude;
import com.eastelsoft.util.CallBack;
import com.eastelsoft.util.Contant;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.FileUtil;
import com.eastelsoft.util.IUtil;
import com.eastelsoft.util.ImageThumbnail;
import com.eastelsoft.util.Util;
import com.eastelsoft.util.contact.PinyinComparatorTwo;
import com.eastelsoft.util.http.AndroidHttpClient;
import com.mapabc.mapapi.geocoder.Geocoder;

/**
 * 新增信息上报
 * 
 * @author lengcj
 */
public class BaifangEditActivity extends BaseActivity {
	public static final String TAG = "BaifangAddActivity";
	private Button btBack;
	private TextView tv_title;
	private Button btLocation;
	private Button btTakePhoto;
	private Button btAddInfo;
	private ImageView imageView;
	private ImageView imageViewLocationIcon;

	private EditText etTime;
	private EditText etTitle;
	private EditText etContent;
	private TextView tvInfoLocationDesc;
	private LinearLayout llLoadingLocation;

	/* 用来标识请求照相功能的activity */
	private static final int CAMERA_WITH_DATA = 1001;
	/* 用来标识请求gallery的activity */
	private static final int PHOTO_PICKED_WITH_DATA = 1002;

	private static final int PHOTO_DEL = 9999;
	// 照相机拍照得到的图片
	private Bitmap bitMap;

	// 客户
	private EditText info_cust;
	private ArrayList<CustBean> mData = new ArrayList<CustBean>();
	private CustSelectAdapter cadapter;
	private ListView lvContact;
	private Button custbtClose;
	Button search_bt_area;
	EditText search_et_area;
	private String spkeyword;
	private ArrayList<CustBean> mData_tp = new ArrayList<CustBean>();// 搜索变量

	// 上传的文件
	private String uploadDate = "";
	private String title = "";
	private File imgFile;
	private String imgFileName = "";
	private String remark = "";
	private String lon = "";
	private String lat = "";
	private String location = "";
	private String clientName = "";
	private String myid = "";
	private Geocoder coder;
	private LocationSQLiteHelper locationHelper;

	// 传来的id
	private String info_auto_id = "";
	HashMap<String, Object> localMap;
	private Bitmap bm;

	private SimpleDateFormat fmtDateAndTime = new SimpleDateFormat("yyyy-MM-dd");
	private Calendar dateAndTime = Calendar.getInstance(Locale.CHINA);

	private SetInfo set;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_baifangadd);
		btBack = (Button) findViewById(R.id.btBack);
		btBack.setOnClickListener(new OnBtBackClickListenerImpl());
		tv_title = (TextView) findViewById(R.id.tv_title);
		tv_title.setText("编辑");
		btTakePhoto = (Button) findViewById(R.id.btTakePhoto);
		btTakePhoto.setOnClickListener(new OnBtTakePhotoClickListenerImpl());

		btLocation = (Button) findViewById(R.id.btLocation);
		btLocation.setOnClickListener(new OnBtLocationClickListenerImpl());
		btAddInfo = (Button) findViewById(R.id.btAddInfo);
		btAddInfo.setOnClickListener(new OnBtAddInfoClickListenerImpl());

		// 客户
		info_cust = (EditText) findViewById(R.id.info_cust);
		info_cust.setOnTouchListener(new OnEdCustTouchListenerImpl());
		info_cust.setOnClickListener(new OnEdCustClickListenerImpl());

		etTime = (EditText) findViewById(R.id.info_time);
		uploadDate = Util.getLocaleTime("yyyy-MM-dd");
		if (uploadDate != null) {
			etTime.setText(uploadDate);
		}
		etTime.setOnTouchListener(new OnBtTimeTouchListenerImpl());
		etTime.setOnClickListener(new OnBtTimeClickListenerImpl());

		etTitle = (EditText) findViewById(R.id.info_title);
		// if (clientName != null) {
		// etTitle.setText(clientName + "客户拜访");
		// }
		etContent = (EditText) findViewById(R.id.info_rizhi);

		imageView = (ImageView) findViewById(R.id.infoImg);
		imageView.setOnClickListener(new OnBtImageViewClickListenerImpl());
		tvInfoLocationDesc = (TextView) findViewById(R.id.infoLocationDesc);
		llLoadingLocation = (LinearLayout) findViewById(R.id.loadingLocation);
		imageViewLocationIcon = (ImageView) findViewById(R.id.infoLocationIcon);

		locationHelper = new LocationSQLiteHelper(this, null, null, 5);
		coder = new Geocoder(this);

		Intent intent = getIntent();
		info_auto_id = intent.getStringExtra("info_auto_id");
		localMap = DBUtil.getDataFromLVisitByID(
				locationHelper.getWritableDatabase(), info_auto_id);

		if (localMap != null) {

			if (localMap.containsKey("title")) {
				if (localMap.get("title") != null) {

					title = localMap.get("title").toString();
					etTitle.setText(title);
				}
			}
			if (localMap.containsKey("remark")) {
				if (localMap.get("remark") != null) {
					remark = localMap.get("remark").toString();
					etContent.setText(remark);
				}
			}

			if (localMap.containsKey("data")) {
				if (localMap.get("data") != null) {
					uploadDate = localMap.get("data").toString();
					etTime.setText(uploadDate);
				}
			}
			if (localMap.containsKey("location")) {
				if (localMap.get("location") != null) {

					location = localMap.get("location").toString();

					if (location != null && !"".equals(location)) {
						tvInfoLocationDesc.setText(location);
						tvInfoLocationDesc.setVisibility(View.VISIBLE);
						imageViewLocationIcon.setVisibility(View.VISIBLE);
						llLoadingLocation.setVisibility(View.GONE);

					}

				}
			}
			if (localMap.get("lon") != null) {
				this.lon = localMap.get("lon").toString();
			}
			if (localMap.get("lat") != null) {
				this.lat = localMap.get("lat").toString();
			}
			if (localMap.get("clientid") != null) {
				this.myid = localMap.get("clientid").toString();
			}
			if (localMap.get("clientName") != null) {
				this.clientName = localMap.get("clientName").toString();
				info_cust.setText(clientName);
			}

			if (localMap.containsKey("imgFile")) {
				if (localMap.get("imgFile") != null) {
					imgFileName = localMap.get("imgFile").toString();
					if (imgFileName != null && !"".equals(imgFileName)) {
						try {
							FileInputStream f = new FileInputStream(
									"/mnt/sdcard/DCIM/eastelsoft/"
											+ imgFileName);
							bm = null;
							BitmapFactory.Options options = new BitmapFactory.Options();
							options.inSampleSize = 10;// 图片的长宽都是原来的1/10
							BufferedInputStream bis = new BufferedInputStream(f);
							bm = BitmapFactory.decodeStream(bis, null, options);
							imageView.setImageBitmap(bm);
							this.imgFile = new File(
									"/mnt/sdcard/DCIM/eastelsoft/"
											+ imgFileName);
							imageView.setVisibility(View.VISIBLE);

						} catch (FileNotFoundException e) {
						}
					}
				}
			}

		}
		sp = getSharedPreferences("userdata", 0);
		set = IUtil.initSetInfo(sp);
		showDialog(PROCESS_DIALOG);
		Thread initThread = new Thread(new InitThread());
		initThread.start();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (locationHelper != null) {
			locationHelper.getWritableDatabase().close();
		}
		if (bitMap != null && !bitMap.isRecycled()) {
			bitMap.recycle();
		}
	}

	class InitThread implements Runnable {
		@Override
		public void run() {
			Message msg = handler.obtainMessage();
			msg.what = 55;
			try {
				// 客户
				mData = DBUtil.getDataFromLCustMgHasUp(locationHelper
						.getWritableDatabase());
				Collections.sort(mData, new PinyinComparatorTwo());

			} catch (Exception e) {
				FileLog.e(TAG, "getCustFromServer initdata==>" + e.toString());
			} finally {

				handler.sendMessage(msg);
			}
		}
	}

	/**
	 * 相机程序使用，解决三星、魅族等手机的uri获取不到的问题
	 */
	// @Override
	// public void onConfigurationChanged(Configuration config) {
	// super.onConfigurationChanged(config);
	// if (config.orientation == Configuration.ORIENTATION_LANDSCAPE) {
	// // 横屏
	// mCamera.setDisplayOrientation(0);
	// } else {
	// // 竖屏
	// mCamera.setDisplayOrientation(90);
	// }
	// }

	private class OnBtAddInfoClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				// 上传数据
				uploadDate = etTime.getText().toString();
				title = etTitle.getText().toString();
				remark = etContent.getText().toString();

				if ("".equals(uploadDate.trim())) {
					respMsg = "请输入拜访日期";
					Toast.makeText(getApplicationContext(), respMsg,
							Toast.LENGTH_SHORT).show();
					return;
				}

				if ("".equals(title.trim())) {
					respMsg = "请输入标题";
					Toast.makeText(getApplicationContext(), respMsg,
							Toast.LENGTH_SHORT).show();
					return;
				}
				if ("".equals(remark.trim())) {
					respMsg = "请输入拜访日志";
					Toast.makeText(getApplicationContext(), respMsg,
							Toast.LENGTH_SHORT).show();
					return;
				}
				// pDialog = new ProgressDialog(InfoAddActivity.this);
				// pDialog.setTitle(getResources()
				// .getString(R.string.checkin_tips));
				// pDialog.setMessage(getResources().getString(R.string.loading));
				// pDialog.show();
				BaifangEditActivity.this.openPopupWindowPG("");
				btPopGps.setText(getResources().getString(
						R.string.loading_baifangadd));
				Thread addInfoThread = new Thread(new AddInfoThread());
				addInfoThread.start();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	class AddInfoThread implements Runnable {
		@Override
		public void run() {
			Looper.prepare();
			Message msg = handler.obtainMessage();
			try {
				sp = getSharedPreferences("userdata", 0);
				SetInfo set = IUtil.initSetInfo(sp);

				String url = set.getHttpip() + Contant.ACTION;
				Map<String, String> map = new HashMap<String, String>();
				map.put("reqCode", Contant.VISIT_UPLOAD_ACTION);
				map.put("gpsid", set.getDevice_id());
				map.put("pin", set.getAuth_code());
				map.put("clientid", myid);
				map.put("clientname", clientName);
				map.put("title", title);
				map.put("remark", remark);
				map.put("date", uploadDate);
				map.put("lon", lon);
				map.put("lat", lat);
				map.put("accuracy", "-100");

				String jsonStr = AndroidHttpClient.getContent(url, map,
						imgFile, "file1");
				jsonStr = IUtil.chkJsonStr(jsonStr);
				FileLog.i(TAG, jsonStr);

				JSONArray array = new JSONArray(jsonStr);
				String resultcode = "";
				if (array.length() > 0) {
					JSONObject obj = array.getJSONObject(0);
					resultcode = obj.getString("ResultCode");
					FileLog.i(TAG, "resultcode==>" + resultcode);
				}
				msg.what = 0;
				msg.obj = resultcode;
				// msg.obj = "信息上报成功";
				handler.sendMessage(msg);
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
				respMsg = getResources().getString(R.string.baifang_upload_err);
				msg.what = 1;
				msg.obj = respMsg;
				handler.sendMessage(msg);
			}
			Looper.loop();
		}
	}

	class LocationThread implements Runnable {
		private Location location;

		public LocationThread(Location location) {
			this.location = location;
		}

		@Override
		public void run() {
			Looper.prepare();
			Message msg = handler.obtainMessage();
			try {
				if (location != null) {
					SItude itude = new BaseStationAction(
							BaifangEditActivity.this).location1(
							String.valueOf(location.getLongitude()),
							String.valueOf(location.getLatitude()));
					displayLocation(msg, itude);
				} else {
					SItude itude = new BaseStationAction(
							BaifangEditActivity.this).location1();
					if (itude != null) {
						displayLocation(msg, itude);
					} else {
						// tvInfoLocationDesc.setText("获取不到定位信息");
						// tvInfoLocationDesc.setVisibility(View.VISIBLE);
						// imageViewLocationIcon.setVisibility(View.VISIBLE);
						msg.what = 11;
						handler.sendMessage(msg);
					}
				}
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
				msg.what = 11;
				handler.sendMessage(msg);
			}
			Looper.loop();
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try {
				if (msg.what < 9) {
					// pDialog.cancel();
					try {
						popupWindowPg.dismiss();
					} catch (Exception e) {
						FileLog.e(TAG, e.toString());
					}
				}
				switch (msg.what) {
				case 0:
					if ("1".equals(msg.obj.toString())) {
						// 任务上报成功
						try {
							DBUtil.insertLVisit(locationHelper
									.getWritableDatabase(), UUID.randomUUID()
									.toString(), myid, clientName, uploadDate,
									title, remark, lon, lat, location,
									imgFileName, "11");
							DBUtil.deleteLVisit(
									locationHelper.getWritableDatabase(),
									info_auto_id);

							Toast.makeText(
									BaifangEditActivity.this,
									getResources().getString(
											R.string.baifang_upload_succ),
									Toast.LENGTH_SHORT).show();
							BaifangEditActivity.this.finish();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					} else {
						openPopupWindowAx("上传失败，是否存入本地");

					}
					break;
				case 1:
					/* dialog(BaifangAddActivity.this, msg.obj.toString()); */
					openPopupWindowAx("上传失败，是否存入本地");

					break;
				case 11:
					tvInfoLocationDesc.setText("获取不到定位信息");

					tvInfoLocationDesc.setVisibility(View.VISIBLE);
					imageViewLocationIcon.setVisibility(View.VISIBLE);
					llLoadingLocation.setVisibility(View.GONE);
					break;
				case 10:
					String locationDesc = msg.obj.toString();
					location = locationDesc;
					tvInfoLocationDesc.setText(locationDesc);
					tvInfoLocationDesc.setVisibility(View.VISIBLE);
					llLoadingLocation.setVisibility(View.GONE);
					imageViewLocationIcon.setVisibility(View.VISIBLE);
				case 9:
					Location location = null;
					Object[] obj = (Object[]) msg.obj;
					if (obj[0] != null) {
						location = (Location) obj[0];
					}
					if (location != null) {
						displayLocation(msg, location);
					} else {
						new AMapAction(BaifangEditActivity.this, amapCallback,
								"").startListener();
					}
					break;
				case 55:
					FileLog.i(TAG, "55");
					dismissDialog(PROCESS_DIALOG);
					break;
				case 99:
					Location location1 = null;
					Object[] obj1 = (Object[]) msg.obj;
					if (obj1[0] != null) {
						location1 = (Location) obj1[0];
					}
					if (location1 != null) {
						displayLocation(msg, location1);
					} else {
						tvInfoLocationDesc.setText("获取不到定位信息");
						tvInfoLocationDesc.setVisibility(View.VISIBLE);
						imageViewLocationIcon.setVisibility(View.VISIBLE);
						llLoadingLocation.setVisibility(View.GONE);
					}
					break;
				case 88:

					dismissDialog(0);
					mData_tp.clear();
					mData_tp.addAll((ArrayList<CustBean>) msg.obj);
					cadapter.notifyDataSetChanged();
					break;

				}
			} catch (Exception e) {
				// 异常中断
			}
		}
	};

	private CallBack amapCallback = new CallBack() {
		public void execute(Object[] paramArrayOfObject) {
			Message msg = handler.obtainMessage();
			msg.what = 99;
			msg.obj = paramArrayOfObject;
			handler.sendMessage(msg);
		}
	};

	private Button btClosex;
	private Button btCloseok;
	private Button btCloseno;

	protected void openPopupWindowAx(String msg) {
		try {
			LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			ViewGroup menuView = (ViewGroup) mLayoutInflater.inflate(
					R.layout.pop_commax, null, true);
			btClosex = (Button) menuView.findViewById(R.id.btClose);
			btClosex.setOnClickListener(new OnBtCloseLClickListenerImpl());
			btCloseok = (Button) menuView.findViewById(R.id.btClose1);
			btCloseok.setOnClickListener(new OnBtCloseLokClickListenerImpl());
			btCloseno = (Button) menuView.findViewById(R.id.btClose2);
			btCloseno.setOnClickListener(new OnBtCloseLClickListenerImpl());
			btPopText = (TextView) menuView.findViewById(R.id.btPopText);
			btPopText.setText(msg);
			popupWindow = new PopupWindow(menuView, LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT, true); // 背景
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
			popupWindow.setAnimationStyle(R.style.PopupAnimation);
			popupWindow.showAtLocation(findViewById(R.id.parent),
					Gravity.CENTER | Gravity.CENTER, 0, 0);
			popupWindow.update();
		} catch (Exception e) {
		}
	}

	protected class OnBtCloseLClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				popupWindow.dismiss();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	protected class OnBtCloseLokClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				popupWindow.dismiss();

				DBUtil.insertLVisit(locationHelper.getWritableDatabase(), UUID
						.randomUUID().toString(), myid, clientName, uploadDate,
						title, remark, lon, lat, location, imgFileName, "00");
				DBUtil.deleteLVisit(locationHelper.getWritableDatabase(),
						info_auto_id);
				Toast.makeText(BaifangEditActivity.this, "存入本地成功",
						Toast.LENGTH_SHORT).show();
				BaifangEditActivity.this.finish();

			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
				Toast.makeText(BaifangEditActivity.this, "存入本地失败",
						Toast.LENGTH_SHORT).show();

			}

		}
	}

	private void displayLocation(Message msg, SItude itude) {
		try {
			BaifangEditActivity.this.lon = Util.format(
					Double.parseDouble(itude.longitude), "#.######");
			BaifangEditActivity.this.lat = Util.format(
					Double.parseDouble(itude.latitude), "#.######");
			String locationDesc = new BaseStationAction(
					BaifangEditActivity.this).getLocation(itude);
			if (!"".equals(locationDesc)) {
				// tvInfoLocationDesc.setText(locationDesc);
				// tvInfoLocationDesc.setVisibility(View.VISIBLE);
				// llLoadingLocation.setVisibility(View.GONE);
				// imageViewLocationIcon.setVisibility(View.VISIBLE);
				msg.obj = locationDesc;
				msg.what = 10;
				handler.sendMessage(msg);
			} else {
				// tvInfoLocationDesc.setText("获取不到定位信息");
				// tvInfoLocationDesc.setVisibility(View.VISIBLE);
				// llLoadingLocation.setVisibility(View.GONE);
				// imageViewLocationIcon.setVisibility(View.VISIBLE);
				msg.what = 11;
				handler.sendMessage(msg);
			}
		} catch (NumberFormatException e) {
			msg.what = 11;
			handler.sendMessage(msg);
		} catch (Exception e) {
			msg.what = 11;
			handler.sendMessage(msg);
		}
	}

	private void displayLocation(Message msg, Location location) {
		try {
			BaifangEditActivity.this.lon = Util.format(location.getLongitude(),
					"#.######");
			BaifangEditActivity.this.lat = Util.format(location.getLatitude(),
					"#.######");
			/*
			 * String locationDesc = "位置坐标经度："+location.getLongitude()+",纬度：" +
			 * location.getLatitude();
			 */
			/* String locationDesc =getAddress(lat,lon); */
			String locationDesc = location.getExtras().getString("desc");
			msg = handler.obtainMessage();
			if (!"".equals(locationDesc)) {
				// tvInfoLocationDesc.setText(locationDesc);
				// tvInfoLocationDesc.setVisibility(View.VISIBLE);
				// llLoadingLocation.setVisibility(View.GONE);
				// imageViewLocationIcon.setVisibility(View.VISIBLE);
				msg.obj = locationDesc;
				msg.what = 10;
				handler.sendMessage(msg);
			} else {
				// tvInfoLocationDesc.setText("获取不到定位信息");
				// tvInfoLocationDesc.setVisibility(View.VISIBLE);
				// llLoadingLocation.setVisibility(View.GONE);
				// imageViewLocationIcon.setVisibility(View.VISIBLE);
				msg.what = 11;
				handler.sendMessage(msg);
			}
		} catch (NumberFormatException e) {
			msg.what = 11;
			handler.sendMessage(msg);
		} catch (Exception e) {
			msg.what = 11;
			handler.sendMessage(msg);
		}
	}

	private class OnBtBackClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				BaifangEditActivity.this.finish();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	private class OnBtTakePhotoClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				doPickPhotoAction();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	private class OnBtLocationClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				// 获取定位位置
				new BaiduMapAction(BaifangEditActivity.this, amapCallback, "2")
						.startListener();
				llLoadingLocation.setVisibility(View.VISIBLE);
				tvInfoLocationDesc.setVisibility(View.GONE);
				imageViewLocationIcon.setVisibility(View.GONE);
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	private class OnBtTimeTouchListenerImpl implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (v.getId()) {
			case R.id.info_time:
				// actionAlertDialog();
				etTime.requestFocus();
				break;
			default:
				break;
			}
			return false;
		}

	}

	private class OnBtTimeClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				// 生成一个DatePickerDialog对象，并显示。显示的DatePickerDialog控件可以选择年月日，并设置
				new DatePickerDialog(BaifangEditActivity.this, d,
						dateAndTime.get(Calendar.YEAR),
						dateAndTime.get(Calendar.MONTH),
						dateAndTime.get(Calendar.DAY_OF_MONTH)).show();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	// 当点击DatePickerDialog控件的设置按钮时，调用该方法
	DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
		@Override
		public void onDateSet(DatePicker view, int year, int monthOfYear,
				int dayOfMonth) {
			// TODO Auto-generated method stub
			dateAndTime.set(Calendar.YEAR, year);
			dateAndTime.set(Calendar.MONTH, monthOfYear);
			dateAndTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
			etTime.setText(fmtDateAndTime.format(dateAndTime.getTime()));

		}

	};

	private CallBack gpsCallback = new CallBack() {
		public void execute(Object[] paramArrayOfObject) {
			Message msg = handler.obtainMessage();
			msg.what = 9;
			msg.obj = paramArrayOfObject;
			handler.sendMessage(msg);
		}
	};

	private class OnBtImageViewClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				Intent intent = new Intent(BaifangEditActivity.this,
						PhotoViewActivity.class);
				intent.putExtra("imgFileName", imgFileName);
				BaifangEditActivity.this.startActivityForResult(intent,
						PHOTO_DEL);
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	private void doPickPhotoAction() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
			doTakePhoto();// 用户点击了从照相机获取
		} else {
			Toast.makeText(BaifangEditActivity.this,
					getString(R.string.noSDCard), Toast.LENGTH_SHORT).show();
		}
	}

	private void doPickPhotoAction1() {
		Context context = this;

		// Wrap our context to inflate list items using correct theme
		final Context dialogContext = new ContextThemeWrapper(context,
				R.style.PhotoTheme);
		String cancel = "返回";
		String[] choices;
		choices = new String[2];
		choices[0] = getString(R.string.take_photo); // 拍照
		choices[1] = getString(R.string.pick_photo); // 从相册中选择
		final ListAdapter adapter = new ArrayAdapter<String>(dialogContext,
				android.R.layout.simple_list_item_1, choices);

		final AlertDialog.Builder builder = new AlertDialog.Builder(
				dialogContext);
		// builder.setTitle(R.string.attachToContact);
		builder.setSingleChoiceItems(adapter, -1,
				new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						switch (which) {
						case 0: {
							String status = Environment
									.getExternalStorageState();
							if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
								doTakePhoto();// 用户点击了从照相机获取
							} else {
								Toast.makeText(BaifangEditActivity.this,
										getString(R.string.noSDCard),
										Toast.LENGTH_SHORT).show();
							}
							break;

						}
						case 1:
							// doPickPhotoFromGallery();// 从相册中去获取
							String status = Environment
									.getExternalStorageState();
							if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
								doPickPhotoFromGallery();
							} else {
								Toast.makeText(BaifangEditActivity.this,
										getString(R.string.noSDCard),
										Toast.LENGTH_SHORT).show();
							}
							break;
						}
					}
				});
		builder.setNegativeButton(cancel,
				new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
					}

				});
		builder.create().show();
	}

	/**
	 * 拍照获取图片
	 * 
	 */
	protected void doTakePhoto() {
		try {
			Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			// imgFileName = Util.getLocaleTime("yyyyMMddHHmmss") + ".jpg";
			File wallpaperDirectory = new File(
					Environment.getExternalStorageDirectory()
							+ "/DCIM/eastelsoft" + "/");
			wallpaperDirectory.mkdirs();
			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
					.fromFile(new File(Environment
							.getExternalStorageDirectory()
							+ "/DCIM/eastelsoft"
							+ "/a123.jpg")));
			startActivityForResult(cameraIntent, CAMERA_WITH_DATA);

		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}
	}

	// 请求Gallery程序
	protected void doPickPhotoFromGallery() {
		Intent localIntent = new Intent();
		localIntent.setType("image/*");
		localIntent.setAction("android.intent.action.GET_CONTENT");
		Intent localIntent2 = Intent.createChooser(localIntent, "选择图片");
		startActivityForResult(localIntent2, PHOTO_PICKED_WITH_DATA);
	}

	// 因为调用了Camera和Gally所以要判断他们各自的返回情况,他们启动时是这样的startActivityForResult
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
//			FileLog.i(TAG, "xl");
//			this.imgFile = null;
//			this.imgFileName = "";
//			imageView.setVisibility(View.GONE);
			return;
		}
		switch (requestCode) {
		case PHOTO_PICKED_WITH_DATA: // 从本地选择图片
			if (bitMap != null && !bitMap.isRecycled()) {
				bitMap.recycle();
			}
			Uri selectedImageUri = data.getData();
			if (selectedImageUri != null) {
				try {
					BitmapFactory.Options opt = new BitmapFactory.Options();
					opt.inJustDecodeBounds = true;
					bitMap = BitmapFactory.decodeStream(getContentResolver()
							.openInputStream(selectedImageUri));
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				// 下面这两句是对图片按照一定的比例缩放，这样就可以完美地显示出来。有关图片的处理将重新写文章来介绍。
				int scale = ImageThumbnail.reckonThumbnail(bitMap.getWidth(),
						bitMap.getHeight(), 500, 600);

				bitMap = ImageThumbnail.PicZoom(bitMap,
						(int) (bitMap.getWidth() / scale),
						(int) (bitMap.getHeight() / scale));
				imageView.setImageBitmap(bitMap);
				imageView.setVisibility(View.VISIBLE);
				imgFileName = Util.getLocaleTime("yyyyMMddHHmmss") + ".jpg";
				this.imgFile = FileUtil.saveBitmapToFile(bitMap, imgFileName);
			}

			break;

		case CAMERA_WITH_DATA: // 拍照
			// 判断网络如果非wif则压缩
			if (bitMap != null && !bitMap.isRecycled()) {
				bitMap.recycle();
			}
			try {

				// 获取图片的高度和宽度
				String path = Environment.getExternalStorageDirectory()
						+ "/DCIM/eastelsoft/a123.jpg";
				int degree = ImageThumbnail.readPictureDegree(path);
				// 图片压缩
				bitMap = ImageThumbnail.PicZoom(path);

				Matrix matrix = new Matrix();
				matrix.postRotate(degree);
				Bitmap photoViewBitmap = Bitmap.createBitmap(bitMap, 0, 0,
						bitMap.getWidth(), bitMap.getHeight(), matrix, true);

				imgFileName = Util.getLocaleTime("yyyyMMddHHmmss") + ".jpg";
				this.imgFile = FileUtil.saveBitmapToFile(photoViewBitmap,
						imgFileName);

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			if (bitMap != null)
				bitMap.recycle();

			try {
				FileInputStream f = new FileInputStream(
						"/mnt/sdcard/DCIM/eastelsoft/" + imgFileName);
				bitMap = null;
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 10;// 图片的长宽都是原来的1/8

				BufferedInputStream bis = new BufferedInputStream(f);
				bitMap = BitmapFactory.decodeStream(bis, null, options);

				imageView.setImageBitmap(bitMap);
				imageView.setVisibility(View.VISIBLE);
				this.imgFile = new File("/mnt/sdcard/DCIM/eastelsoft/"
						+ imgFileName);
			} catch (FileNotFoundException e) {
			}
			break;
		case PHOTO_DEL:
			this.imgFile = null;
			this.imgFileName = "";
			imageView.setVisibility(View.GONE);
			break;
		}

	}

	// 地理编码
	public void getAddress(final double mlat, final double mLon) {
		Thread t = new Thread(new Runnable() {
			public void run() {
				try {
					Geocoder coder = new Geocoder(BaifangEditActivity.this);
					List<Address> address = coder
							.getFromLocation(mlat, mLon, 3);
					if (address != null && address.size() > 0) {
						Address addres = address.get(0);
						String addressName = addres.getAdminArea()
								+ addres.getSubLocality()
								+ addres.getFeatureName() + "附近";
						Message msg = handler.obtainMessage();
						msg.obj = addressName;
						msg.what = 10;
						handler.sendMessage(msg);

					}
				} catch (Exception e) {
					Message msg = handler.obtainMessage();
					msg.what = 11;
					handler.sendMessage(msg);
				}

			}
		});

		t.start();
	}

	// ScrollView用dispatchTouchEvent

	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		/* Rect localRect1 = new Rect(); */
		Rect localRect2 = new Rect();
		Rect localRect3 = new Rect();
		/*
		 * ((EditText)findViewById(R.id.info_time)).getGlobalVisibleRect(localRect1
		 * );
		 */
		((EditText) findViewById(R.id.info_title))
				.getGlobalVisibleRect(localRect2);
		((EditText) findViewById(R.id.info_rizhi))
				.getGlobalVisibleRect(localRect3);
		Rect localRect4 = new Rect((int) event.getX(), (int) event.getY(),
				(int) event.getX(), (int) event.getY());
		if ((!localRect2.intersect(localRect4))
				&& (!localRect3.intersect(localRect4)))
			((InputMethodManager) getSystemService("input_method"))
					.hideSoftInputFromWindow(getWindow().peekDecorView()
							.getWindowToken(), 0);
		return super.dispatchTouchEvent(event);
	}

	private String networkState = "none";

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

	// 地理编码
	public String getAddress(String mlat, String mLon) {
		String addressName = "";
		double mLatd;
		double mLond;
		try {
			mLatd = Double.parseDouble(mlat);
			mLond = Double.parseDouble(mLon);
		} catch (NumberFormatException e1) {
			// TODO Auto-generated catch block
			return addressName;
		}
		try {
			List<Address> address = coder.getFromLocation(mLatd, mLond, 3);
			if (address != null && address.size() > 0) {
				Address addres = address.get(0);

				addressName = addres.getAdminArea() + addres.getSubLocality()
						+ addres.getFeatureName() + "附近";
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			addressName = "";
		}
		return addressName;
	}

	private class OnEdCustTouchListenerImpl implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (v.getId()) {
			case R.id.info_cust:
				// actionAlertDialog();
				info_cust.requestFocus();
				break;
			default:
				break;
			}
			return false;
		}

	}

	private class OnEdCustClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			openPopupWindowCust("");

		}
	}

	protected void openPopupWindowCust(String msg) {
		try {
			LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			ViewGroup menuView = (ViewGroup) mLayoutInflater.inflate(
					R.layout.pop_cust, null, true);
			lvContact = (ListView) menuView.findViewById(R.id.lvContact);
			lvContact.setOnItemClickListener(new OnItemClickListenerImpl());

			// 还原
			mData_tp.clear();
			mData_tp.addAll(mData);
			cadapter = new CustSelectAdapter(BaifangEditActivity.this, mData_tp);
			lvContact.setAdapter(cadapter);

			custbtClose = (Button) menuView.findViewById(R.id.btClose);
			custbtClose
					.setOnClickListener(new OnCustBtCloseLClickListenerImpl());
			search_bt_area = (Button) menuView
					.findViewById(R.id.search_bt_area);
			search_bt_area
					.setOnClickListener(new OnBtSpinerSearchClickListenerImpl());
			search_et_area = (EditText) menuView
					.findViewById(R.id.search_et_area);

			popupWindow = new PopupWindow(menuView, LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT, true); // 背景
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
			popupWindow.setAnimationStyle(R.style.PopupAnimation);
			popupWindow.showAtLocation(findViewById(R.id.parent),
					Gravity.CENTER | Gravity.CENTER, 0, 0);
			popupWindow.update();
		} catch (Exception e) {
		}
	}

	private class OnItemClickListenerImpl implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub

			// private String clientName = "";
			// private String myid = "";
			myid = mData_tp.get(arg2).getId();
			clientName = mData_tp.get(arg2).getClientName();
			info_cust.setText(clientName);

			if (clientName != null) {
				etTitle.setText(clientName + "客户拜访");
			}

			try {
				popupWindow.dismiss();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}

		}
	}

	protected class OnCustBtCloseLClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				popupWindow.dismiss();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	private class OnBtSpinerSearchClickListenerImpl implements OnClickListener {

		@SuppressWarnings("deprecation")
		@Override
		public void onClick(View v) {
			// pageNo = 1;
			spkeyword = search_et_area.getText().toString();

			if (spkeyword != null) {
				// 开启search线程
				InputMethodManager m = (InputMethodManager) getSystemService("input_method");
				m.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);

				showDialog(0);
				Thread scdhread = new Thread(new ShowCust_Thread());
				scdhread.start();

			}

		}
	}

	private class ShowCust_Thread implements Runnable {

		@Override
		public void run() {

			Looper.prepare();
			// 模糊查询
			ArrayList<CustBean> mData_arr = new ArrayList<CustBean>();
			mData_arr = DBUtil.getDataFromLCustMgHasUpAndLike(
					locationHelper.getWritableDatabase(), spkeyword);
			Collections.sort(mData_arr, new PinyinComparatorTwo());

			Message msg = handler.obtainMessage();
			msg.what = 88;
			msg.obj = mData_arr;
			handler.sendMessage(msg);
			Looper.loop();

		}

	}

}
