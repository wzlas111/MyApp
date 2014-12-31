/**
 * Copyright (c) 2012-8-16 www.eastelsoft.com
 * $ID PlanViewActivity.java 下午12:36:53 $
 */
package com.eastelsoft.lbs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.net.NetworkInfo.State;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.PopupWindow;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.eastelsoft.lbs.BulletinviewActivity.InitThread;
import com.eastelsoft.lbs.BulletinviewActivity.UpdateReadThread;
import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.db.DBUtil;
import com.eastelsoft.lbs.db.LocationSQLiteHelper;
import com.eastelsoft.lbs.entity.BulletinBean;
import com.eastelsoft.lbs.entity.SetInfo;
import com.eastelsoft.lbs.location.BaiduMapAction;
import com.eastelsoft.util.CallBack;
import com.eastelsoft.util.Contant;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.FileUtil;
import com.eastelsoft.util.IUtil;
import com.eastelsoft.util.ImageThumbnail;
import com.eastelsoft.util.Util;
import com.eastelsoft.util.http.AndroidHttpClient;

/**
 * 计划任务未办界面
 * 
 * @author lengcj
 */
public class PlanViewActivity extends BaseActivity {
	public static final String TAG = "InfoViewActivity";
	private Button btBack;
	private Button btSavePlan;
	private Button btTakePhoto;
	private Button btlocation;
	//new add*********************//
	private Button btShowCutReply;//
	private ProgressDialog dialog;//
	private int position;         //
	//****************************//
	private TextView tv_plan_date;
	private TextView tv_plan_releasedate;
	private TextView tv_plan_remark;
	private TextView tv_plan_location;
	private LinearLayout tv_plan_location_id;
	private TextView tv_plan_lon;
	private TextView tv_plan_lat;
	private TextView tv_plan_text;
	private EditText et_plan_text;
	private TextView tv_plan_title;
	private ImageView imageView;
	// private ImageView imageView2;
	private Button btLocation;

	private String planId;
	private String planText;
	private String planUploadDate;
	private String disp_planUploadDate;
	/* 用来标识请求照相功能的activity */
	private static final int CAMERA_WITH_DATA = 1001;
	/* 用来标识请求gallery的activity */
	private static final int PHOTO_PICKED_WITH_DATA = 1002;

	private static final int PHOTO_DEL = 9999;
	// 照相机拍照得到的图片
	private Bitmap bitMap;
	private File imgFile;
	private String imgFileName;

	private LocationSQLiteHelper locationHelper;

	HashMap<String, Object> localMap;
	private String resultCode = "0";
	private String istijiao = "11";
	private String lon;
	private String lat;
	private String location;
	private Bitmap bm;

	// private LinearLayout llLoadingLocation;
	// private TextView tvInfoLocationDesc;
	// private ImageView imageViewLocationIcon;
	private TextView tvInfoLocationDesc;
	private TextView tvInfoLocationTip;
	private LinearLayout llInfoLocationId;
	private Button btupLocation;
	private String uplon = "";
	private String uplat = "";
	private String uplocation = "";
	private LinearLayout planUploadDate_ll;
	private TextView tv_planUploadDate;
	private List<String> getplanmsg;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_planview);
		getplanmsg=getPlanMsg();
		System.out.println("PlanMsg----->"+getplanmsg.toString());
		btBack = (Button) findViewById(R.id.btBack);
		tvHeadTitle = (TextView) findViewById(R.id.tvHeadTitle);
		btSavePlan = (Button) findViewById(R.id.btSavePlan);
		btSavePlan.setOnClickListener(new OnBtSavePlanListenerImpl());
		btTakePhoto = (Button) findViewById(R.id.btTakePhoto);
		btTakePhoto.setOnClickListener(new OnBtTakePhotoClickListenerImpl());
		btlocation = (Button) findViewById(R.id.plan_location_img);
		btlocation.setOnClickListener(new OnBtLocationClickListenerImpl());
		btupLocation = (Button) findViewById(R.id.up_location_img);
		btupLocation.setOnClickListener(new OnBtuptoLocationClickListenerImpl());
		tv_plan_date = (TextView) findViewById(R.id.plan_date);

		tv_plan_releasedate = (TextView) findViewById(R.id.plan_releasedate);
		tv_plan_remark = (TextView) findViewById(R.id.plan_remark);
		tv_plan_location = (TextView) findViewById(R.id.plan_location);
		tv_plan_location_id = (LinearLayout) findViewById(R.id.plan_location_id);
		tv_plan_lon = (TextView) findViewById(R.id.plan_lon);
		tv_plan_lat = (TextView) findViewById(R.id.plan_lat);
		tv_plan_text = (TextView) findViewById(R.id.plan_text);
		et_plan_text = (EditText) findViewById(R.id.plan_text_input);
		tv_plan_title = (TextView) findViewById(R.id.plan_title);
		//new add
		btShowCutReply=(Button) findViewById(R.id.btShortCutreply);
		btShowCutReply.setOnClickListener(new ShortCutDialogImpl(getplanmsg));
		imageView = (ImageView) findViewById(R.id.planImg);
		imageView.setOnClickListener(new OnBtImageViewClickListenerImpl());
		// imageView2 = (ImageView) findViewById(R.id.plan_img);
		// imageView2.setVisibility(View.GONE);
		btBack.setOnClickListener(new OnBtBackClickListenerImpl());
		// 上传位置
		btLocation = (Button) findViewById(R.id.btLocation);
		
		btLocation.setOnClickListener(new OnBtUpLocationClickListenerImpl());
		// tvInfoLocationDesc = (TextView) findViewById(R.id.infoLocationDesc);
		// llLoadingLocation = (LinearLayout)
		// findViewById(R.id.loadingLocation);
		// imageViewLocationIcon = (ImageView)
		// findViewById(R.id.infoLocationIcon);

		tvInfoLocationDesc = (TextView) findViewById(R.id.infoLocationDesc);
		tvInfoLocationTip = (TextView) findViewById(R.id.infoLocationTip);

		llInfoLocationId = (LinearLayout) findViewById(R.id.infoLocationId);
		tv_planUploadDate = (TextView) findViewById(R.id.planUploadDate);
		planUploadDate_ll = (LinearLayout) findViewById(R.id.planUploadDate_ll);

		locationHelper = new LocationSQLiteHelper(this, null, null, 5);

		Display display = getWindowManager().getDefaultDisplay();
		int windowWidth = display.getWidth();
		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
				(windowWidth - 120), LayoutParams.WRAP_CONTENT);
		tv_plan_location_id.setLayoutParams(params);

		Intent intent = getIntent();
		planId = intent.getStringExtra("planId");
		localMap = DBUtil.getDataFromLPlanByID(
				locationHelper.getWritableDatabase(), planId);
		if (localMap != null) {
			if (localMap.containsKey("planDate")) {
				if (localMap.get("planDate") != null) {
					tv_plan_date.setText(localMap.get("planDate").toString());
				}
			}
			if (localMap.containsKey("releasedate")) {

				if (localMap.get("releasedate") != null
						&& !"null".equals(localMap.get("releasedate"))) {
					tv_plan_releasedate.setText(localMap.get("releasedate")
							.toString());
				}
			}
			if (localMap.containsKey("remark")) {
				if (localMap.get("remark") != null) {
					tv_plan_remark.setText(localMap.get("remark").toString());
				}
			}
			if (localMap.containsKey("location")) {
				if (localMap.get("location") != null) {
					tv_plan_location.setText(localMap.get("location")
							.toString());
					this.location = localMap.get("location").toString();
				}
			}
			if (localMap.containsKey("lon")) {
				if (localMap.get("lon") != null) {
					tv_plan_lon.setText(localMap.get("lon").toString());
					this.lon = localMap.get("lon").toString();
				}
			}
			if (localMap.containsKey("lat")) {
				if (localMap.get("lat") != null) {
					tv_plan_lat.setText(localMap.get("lat").toString());
					this.lat = localMap.get("lat").toString();
				}
			}
			if (localMap.containsKey("planText")) {
				if (localMap.get("planText") != null) {
					tv_plan_text.setText(localMap.get("planText").toString());
				}
			}
			if (localMap.containsKey("resultCode")) {
				if (localMap.get("resultCode") != null) {
					resultCode = localMap.get("resultCode").toString();
				}
			}
			if (localMap.containsKey("istijiao")) {
				if (localMap.get("istijiao") != null) {
					istijiao = localMap.get("istijiao").toString();
				}
			}
			if (localMap.containsKey("title")) {
				if (localMap.get("title") != null) {
					tv_plan_title.setText(localMap.get("title").toString());
				}
			}

			if (localMap.containsKey("planUploadDate")) {
				if (localMap.get("planUploadDate") != null) {
					tv_planUploadDate.setText(localMap.get("planUploadDate")
							.toString());
					disp_planUploadDate = localMap.get("planUploadDate")
							.toString();
				}
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
							options.inSampleSize = 10;// 图片的长宽都是原来的1/8
							BufferedInputStream bis = new BufferedInputStream(f);
							bm = BitmapFactory.decodeStream(bis, null, options);
							imageView.setImageBitmap(bm);
							imageView.setVisibility(View.VISIBLE);
							this.imgFile = new File(
									"/mnt/sdcard/DCIM/eastelsoft/"
											+ imgFileName);
						} catch (FileNotFoundException e) {
						}
					}
				}
			}

			if (localMap.containsKey("uplocation")) {
				if (localMap.get("uplocation") != null) {
					uplocation = localMap.get("uplocation").toString();
					if (uplocation != null && !"".equals(uplocation)) {
						tvInfoLocationDesc.setText(uplocation);
						// tvInfoLocationDesc.setVisibility(View.VISIBLE);
						// imageViewLocationIcon.setVisibility(View.VISIBLE);
						// llLoadingLocation.setVisibility(View.GONE);

						tvInfoLocationDesc.setVisibility(View.VISIBLE);
						tvInfoLocationTip.setVisibility(View.GONE);
						llInfoLocationId.setVisibility(View.VISIBLE);
					}
				}
			}
			if (localMap.get("uplon") != null) {
				this.uplon = localMap.get("uplon").toString();
			}
			if (localMap.get("uplat") != null) {
				this.uplat = localMap.get("uplat").toString();
			}
		}
		if ("0".equals(resultCode)) {
			// 待办
			tv_plan_text.setVisibility(View.GONE);
			et_plan_text.setVisibility(View.VISIBLE);
			btSavePlan.setVisibility(View.VISIBLE);
			btTakePhoto.setVisibility(View.VISIBLE);
			tvHeadTitle.setText(this.getResources().getText(
					R.string.plan_not_do));

			btLocation.setVisibility(View.VISIBLE);
			planUploadDate_ll.setVisibility(View.GONE);
			// istijiao
			if ("00".equals(istijiao)) {
				et_plan_text.setText(localMap.get("planText").toString());

			}
			btupLocation.setVisibility(View.GONE);

		} else {
			// 已办
			tv_plan_text.setVisibility(View.VISIBLE);
			et_plan_text.setVisibility(View.GONE);
			btSavePlan.setVisibility(View.GONE);
			btTakePhoto.setVisibility(View.GONE);
			btShowCutReply.setVisibility(View.GONE);
			tvHeadTitle.setText(this.getResources().getText(
					R.string.plan_has_do));
			btLocation.setVisibility(View.GONE);
			planUploadDate_ll.setVisibility(View.VISIBLE);
			btupLocation.setVisibility(View.VISIBLE);

		}

		// 阅读接口
		if ("0".equals(resultCode)) {
			Thread thread = new Thread(new UpdateReadThread());
			thread.start();
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (locationHelper != null
				&& locationHelper.getWritableDatabase() != null) {
			locationHelper.getWritableDatabase().close();
		}
		if (bitMap != null && !bitMap.isRecycled()) {
			bitMap.recycle();
		}
		if (bm != null && !bm.isRecycled()) {
			bm.recycle();
		}
	}

	String resultcode = "";

	class UpdateReadThread implements Runnable {
		@Override
		public void run() {
			Message msg = handler.obtainMessage();
			msg.what = 66;
			try {
				sp = getSharedPreferences("userdata", 0);
				SetInfo set = IUtil.initSetInfo(sp);
				String url = set.getHttpip() + Contant.ACTION;
				Map<String, String> map = new HashMap<String, String>();
				map.put("reqCode", Contant.PLANREAD_ACTION);
				map.put("gpsid", set.getDevice_id());
				map.put("pin", set.getAuth_code());
				map.put("planid", planId);
				String jsonStr1 = AndroidHttpClient.getContent(url, map);
				// {"resultcode":"1"}{"resultcode":"1"}
				jsonStr1 = IUtil.chkJsonStr(jsonStr1);
				JSONArray array1 = new JSONArray(jsonStr1);
				if (array1.length() > 0) {
					JSONObject obj2 = array1.getJSONObject(0);
					resultcode = obj2.getString("resultcode");
				}
			} catch (Exception e) {

			} finally {
				handler.sendMessage(msg);
			}

		}
	}

	// @Override
	// public void onConfigurationChanged(Configuration config) {
	// super.onConfigurationChanged(config);
	// }

	private void displayLocation(Message msg, Location location) {
		try {
			this.uplon = Util.format(location.getLongitude(), "#.######");
			this.uplat = Util.format(location.getLatitude(), "#.######");
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

	private CallBack amapCallback = new CallBack() {
		public void execute(Object[] paramArrayOfObject) {
			Message msg = handler.obtainMessage();
			msg.what = 99;
			msg.obj = paramArrayOfObject;
			handler.sendMessage(msg);
		}
	};
	
	//new add
	private class OnbtShowCutReplyClickListenerImpl implements OnClickListener{

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			
		}
		
	}
/////////////////////////////////////////////////////////////////////
	private class OnBtUpLocationClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				// 获取定位位置
				new BaiduMapAction(PlanViewActivity.this, amapCallback, "2")
						.startListener();
				llInfoLocationId.setVisibility(View.VISIBLE);
				tvInfoLocationTip.setVisibility(View.VISIBLE);
				tvInfoLocationDesc.setVisibility(View.GONE);
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	private class OnBtSavePlanListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				// 上传数据
				planText = et_plan_text.getText().toString();

				if ("00".equals(istijiao)) {
					// 已经编辑
					// private String planUploadDate;
					// private String disp_planUploadDate;
					planUploadDate = disp_planUploadDate;

				} else {
					planUploadDate = Util.getLocaleTime("yyyy-MM-dd HH:mm:ss");
				}

				if ("".equals(planText.trim())) {
					respMsg = getResources().getString(
							R.string.plan_text_edit_error);
					Toast.makeText(getApplicationContext(), respMsg,
							Toast.LENGTH_SHORT).show();
					return;
				}
				PlanViewActivity.this.planText = planText;
				// pDialog = new ProgressDialog(PlanViewActivity.this);
				// pDialog.setTitle(getResources().getString(R.string.checkin_tips));
				// pDialog.setMessage(getResources().getString(R.string.loading));
				// pDialog.show();
				PlanViewActivity.this.openPopupWindowPG("");
				btPopGps.setText(getResources().getString(
						R.string.loading_plansubmit));
				Thread submitPlanThread = new Thread(new SubmitPlanThread());
				submitPlanThread.start();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	class SubmitPlanThread implements Runnable {
		@Override
		public void run() {
			Looper.prepare();
			Message msg = handler.obtainMessage();
			try {
				sp = getSharedPreferences("userdata", 0);
				SetInfo set = IUtil.initSetInfo(sp);

				String url = set.getHttpip() + Contant.ACTION;
				Map<String, String> map = new HashMap<String, String>();
				map.put("reqCode", Contant.PLAN_UPLOAD_REQCODE);
				map.put("GpsId", set.getDevice_id());
				map.put("Pin", set.getAuth_code());
				map.put("PlanText", planText);
				map.put("PlanId", planId);
				// 新增
				map.put("lon", uplon);
				map.put("lat", uplat);
				map.put("accuracy", "-100");
				// planUploadDate = Util.getLocaleTime("yyyy-MM-dd HH:mm:ss");
				map.put("phone_time", planUploadDate);

				String jsonStr = AndroidHttpClient.getContent(url, map,
						imgFile, "file1");
				jsonStr = IUtil.chkJsonStr(jsonStr);
				JSONArray array = new JSONArray(jsonStr);
				String resultcode = "";
				if (array.length() > 0) {
					JSONObject obj = array.getJSONObject(0);
					resultcode = obj.getString("resultcode");
					FileLog.i(TAG, "resultcode==>" + resultCode);
				}
				msg.what = 0;
				msg.obj = resultcode;

				if ("1".equals(resultcode) || "2".equals(msg.obj.toString())) {
					// 将待办任务修改为已办任务
					// planUploadDate =
					// Util.getLocaleTime("yyyy-MM-dd HH:mm:ss");
					if (imgFileName == null)
						imgFileName = "";
					DBUtil.updateLPlanResultCode(
							locationHelper.getWritableDatabase(), planText,
							planUploadDate, "1", "11", imgFileName, uplon,
							uplat, uplocation, planId);
				}
				if ("3".equals(resultcode)) {
					// 将待办任务修改为已办任务
					DBUtil.deleteLPlan(locationHelper.getWritableDatabase(),
							planId);
				}
				// msg.obj = "任务办理成功";
				handler.sendMessage(msg);
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
				respMsg = getResources().getString(R.string.info_add_error);
				msg.what = 1;
				msg.obj = respMsg;
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
					try {
						popupWindowPg.dismiss();
					} catch (Exception e) {
						FileLog.e(TAG, e.toString());
					}
				}

				switch (msg.what) {
				case 0:
					if ("1".equals(msg.obj.toString())
							|| "2".equals(msg.obj.toString())) {
						// dialog(PlanViewActivity.this,
						// getResources().getString(
						// R.string.plan_upload_succ));
						//
						Toast.makeText(
								PlanViewActivity.this,
								getResources().getString(
										R.string.plan_upload_succ),
								Toast.LENGTH_SHORT).show();
						PlanViewActivity.this.finish();

					}
					if ("3".equals(msg.obj.toString())) {
						dialog(PlanViewActivity.this,
								getResources().getString(
										R.string.plan_upload_del));
						// 将待办任务修改为已办任务
						// DBUtil.deleteLPlan(locationHelper.getWritableDatabase(),
						// planId);
					}
					if ("".equals(msg.obj.toString())
							|| "99".equals(msg.obj.toString())) {
						// dialog(PlanViewActivity.this,
						// getResources().getString(
						// R.string.plan_upload_err));
						openPopupWindowAx("上传失败，是否存入本地");
					}
					// PlanViewActivity.this.finish();
					break;
				case 1:
					// dialog(PlanViewActivity.this, msg.obj.toString());
					openPopupWindowAx("上传失败，是否存入本地");
					break;
				case 11:
					tvInfoLocationDesc.setText("获取不到定位信息");

					// tvInfoLocationDesc.setVisibility(View.VISIBLE);
					// imageViewLocationIcon.setVisibility(View.VISIBLE);
					// llLoadingLocation.setVisibility(View.GONE);
					tvInfoLocationDesc.setVisibility(View.VISIBLE);
					tvInfoLocationTip.setVisibility(View.GONE);
					llInfoLocationId.setVisibility(View.VISIBLE);
					break;
				case 10:
					String locationDesc = msg.obj.toString();
					uplocation = locationDesc;
					tvInfoLocationDesc.setText(locationDesc);
					// tvInfoLocationDesc.setVisibility(View.VISIBLE);
					// llLoadingLocation.setVisibility(View.GONE);
					// imageViewLocationIcon.setVisibility(View.VISIBLE);
					tvInfoLocationDesc.setVisibility(View.VISIBLE);
					tvInfoLocationTip.setVisibility(View.GONE);
					llInfoLocationId.setVisibility(View.VISIBLE);
				case 66:

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
						// tvInfoLocationDesc.setVisibility(View.VISIBLE);
						// imageViewLocationIcon.setVisibility(View.VISIBLE);
						// llLoadingLocation.setVisibility(View.GONE);
						tvInfoLocationDesc.setVisibility(View.VISIBLE);
						tvInfoLocationTip.setVisibility(View.GONE);
						llInfoLocationId.setVisibility(View.VISIBLE);
					}
					break;
				}
			} catch (Exception e) {
				// 异常中断
			}
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

				if (imgFileName == null)
					imgFileName = "";
				DBUtil.updateLPlanIstijiao(
						locationHelper.getWritableDatabase(), planText,
						planUploadDate, "00", imgFileName, uplon, uplat,
						uplocation, planId);

				Toast.makeText(PlanViewActivity.this, "存入本地成功",
						Toast.LENGTH_SHORT).show();
				PlanViewActivity.this.finish();

			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
				Toast.makeText(PlanViewActivity.this, "存入本地失败",
						Toast.LENGTH_SHORT).show();

			}

		}
	}

	private class OnBtBackClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				PlanViewActivity.this.finish();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	private class OnBtLocationClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				if (lon != null && !lon.equals("") && lat != null
						&& !lat.equals("")) {
					Intent intent = new Intent(PlanViewActivity.this,
							ItemizedOverlayBaiduActivity.class);
					Float mLon = Float.parseFloat(lon)+0.0065f;
					Float mLat = Float.parseFloat(lat)+0.006f;
					intent.putExtra("lon", String.valueOf(mLon));
					intent.putExtra("lat", String.valueOf(mLat));
					intent.putExtra("location", location);
					intent.putExtra("title", "目的地");
					startActivity(intent);
				}

			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	private class OnBtuptoLocationClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			// private String uplon="";
			// private String uplat="";
			// private String uplocation="";
			try {
				if (uplon != null && !uplon.equals("") && uplat != null
						&& !uplat.equals("")) {
					Intent intent = new Intent(PlanViewActivity.this,
							ItemizedOverlayBaiduActivity.class);
					intent.putExtra("lon", uplon);
					intent.putExtra("lat", uplat);
					intent.putExtra("location", uplocation);
					startActivity(intent);
				}

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

	private class OnBtImageViewClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				Intent intent = new Intent(PlanViewActivity.this,
						PhotoViewActivity.class);
				intent.putExtra("imgFileName", imgFileName);
				if ("1".equals(resultCode)) {
					intent.putExtra("opTag", "1");
				}
				PlanViewActivity.this.startActivityForResult(intent, PHOTO_DEL);
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
			Toast.makeText(PlanViewActivity.this, getString(R.string.noSDCard),
					Toast.LENGTH_SHORT).show();
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
								Toast.makeText(PlanViewActivity.this,
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
								Toast.makeText(PlanViewActivity.this,
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
			// FileLog.i(TAG, "xl");
			// this.imgFile = null;
			// this.imgFileName = "";
			// imageView.setVisibility(View.GONE);
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
				this.imgFile = FileUtil.saveBitmapToFile(photoViewBitmap, imgFileName);
				
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

	// ScrollView用dispatchTouchEvent
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		Rect localRect1 = new Rect();
		((EditText) findViewById(R.id.plan_text_input))
				.getGlobalVisibleRect(localRect1);
		Rect localRect3 = new Rect((int) event.getX(), (int) event.getY(),
				(int) event.getX(), (int) event.getY());
		if (!localRect1.intersect(localRect3))
			((InputMethodManager) getSystemService("input_method"))
					.hideSoftInputFromWindow(getWindow().peekDecorView()
							.getWindowToken(), 0);
		return super.dispatchTouchEvent(event);
	}
	
	//new add**********************************************//
	private class ShortCutDialogImpl implements OnClickListener{
		private List<String> s;
		public ShortCutDialogImpl(List<String> s){
			this.s=s;
		}
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			String[] ls=new String[s.size()];
			for(int i=0;i<s.size();i++){
				ls[i]=s.get(i);
			}
			Builder builder = new Builder(PlanViewActivity.this);
			if(ls.length==0||ls==null){
				builder.setTitle("温馨提示").setMessage("管理员未配置快捷回复常用短语").setPositiveButton("确认", null).show();
			}else{
			 builder.setTitle("请选择");
			 builder.setSingleChoiceItems(ls, 0, new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					position=arg1;
				}
			});
			 
			 builder.setNeutralButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					
				}
			});
			 builder.setNegativeButton("确定", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					et_plan_text=(EditText) findViewById(R.id.plan_text_input);
					et_plan_text.setText(s.get(position));
				}
			});
			 AlertDialog alertDialog = builder.create();
			 alertDialog.show();
		}
		
		
	}
	}
	public List<String> getPlanMsg(){
		JSONArray array1;
		List<String> msg_name=new ArrayList<String>();
		sp = getSharedPreferences("userdata", 0);
		SetInfo set = IUtil.initSetInfo(sp);
		String url = set.getHttpip() + Contant.ACTION;
		Map<String, String> map = new HashMap<String, String>();
		map.put("reqCode", Contant.PlANMSGGET);
		map.put("gpsid", set.getDevice_id());
		map.put("actiontype", "2");
		String jsonStr1 = AndroidHttpClient.getContent(url, map);
		jsonStr1 = IUtil.chkJsonStr(jsonStr1);
		try {
			array1 = new JSONArray(jsonStr1);
		if (array1.length() > 0) {
			JSONObject obj2 = array1.getJSONObject(0);
			JSONArray msg_names=obj2.getJSONArray("clientdata");
			for(int i=0;i<msg_names.length();i++){
			JSONObject msg_name_JSON=msg_names.getJSONObject(i);
			msg_name.add(msg_name_JSON.getString("msg_name"));
			}
		}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return msg_name;
	}
	
	
}
