/**
 * Copyright (c) 2012-8-15 www.eastelsoft.com
 * $ID InfoViewActivity.java 上午10:21:18 $
 */
package com.eastelsoft.lbs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eastelsoft.lbs.BaifangAddActivity.LocationThread;
import com.eastelsoft.lbs.InfoViewActivity.AddInfoThread;
import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.db.DBUtil;
import com.eastelsoft.lbs.db.LocationSQLiteHelper;
import com.eastelsoft.lbs.entity.SetInfo;
import com.eastelsoft.util.Contant;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.IUtil;
import com.eastelsoft.util.http.AndroidHttpClient;

/**
 * 查看信息详情
 * 
 * @author lengcj
 */
public class BaifangViewActivity extends BaseActivity {
	public static final String TAG = "BaifangViewActivity";
	private Button btBack;
	private Button btupCust;
	private Button btAddInfo;
	private Button tvinfolocation_img;
	private TextView info_cust;
	private TextView tvInfoTitle;
	private TextView tvInfoContent;
	private TextView tvInfoUploadDate;
	private TextView tvInfoLocation;
	private LinearLayout ll_cust;
	private LinearLayout ll_title;
	private LinearLayout ll_content;
	private LinearLayout ll_date;
	private LinearLayout ll_location;
	private LinearLayout ll_img;
	private ImageView imageView;
	private LocationSQLiteHelper locationHelper;
	private Bitmap bm;
	HashMap<String, Object> localMap;
	private String imgFileName = "";
	private String location = "";
	private String lon = "";
	private String lat = "";
	private String title = "";
	private String remark = "";
	private String data = "";
	private String info_auto_id;
	private String istijiao = "";
	private File imgFile;
	private String myid;
	private String clientName;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_baifangview);
		btBack = (Button) findViewById(R.id.btBack);
		btBack.setOnClickListener(new OnBtBackClickListenerImpl());
		btupCust = (Button) findViewById(R.id.btupCust);
		btupCust.setOnClickListener(new OnBtEditClickListenerImpl());

		btAddInfo = (Button) findViewById(R.id.btAddInfo);
		btAddInfo.setOnClickListener(new OnBtAddClickListenerImpl());

		ll_cust = (LinearLayout) findViewById(R.id.ll_cust);
		ll_title = (LinearLayout) findViewById(R.id.ll_title);
		ll_content = (LinearLayout) findViewById(R.id.ll_content);
		ll_date = (LinearLayout) findViewById(R.id.ll_date);
		ll_location = (LinearLayout) findViewById(R.id.ll_location);
		ll_img = (LinearLayout) findViewById(R.id.ll_img);

		info_cust = (TextView) findViewById(R.id.info_cust);
		tvInfoTitle = (TextView) findViewById(R.id.info_title);

		tvInfoContent = (TextView) findViewById(R.id.info_content);

		tvInfoUploadDate = (TextView) findViewById(R.id.info_uploadDate);

		tvInfoLocation = (TextView) findViewById(R.id.info_location);

		tvinfolocation_img = (Button) findViewById(R.id.info_location_img);
		tvinfolocation_img
				.setOnClickListener(new OnInfoLocationClickListenerImpl());

		imageView = (ImageView) findViewById(R.id.info_img);

		imageView.setOnClickListener(new OnBtImageViewClickListenerImpl());
		locationHelper = new LocationSQLiteHelper(this, null, null, 5);

		Intent intent = getIntent();
		info_auto_id = intent.getStringExtra("info_auto_id");
		localMap = DBUtil.getDataFromLVisitByID(
				locationHelper.getWritableDatabase(), info_auto_id);

		if (localMap != null) {

			if (localMap.containsKey("title")) {
				if (localMap.get("title") != null) {
					ll_title.setVisibility(View.VISIBLE);
					title = localMap.get("title").toString();
					tvInfoTitle.setText(title);
				}
			}
			if (localMap.containsKey("remark")) {
				if (localMap.get("remark") != null) {
					ll_content.setVisibility(View.VISIBLE);
					remark = localMap.get("remark").toString();
					tvInfoContent.setText(remark);
				}
			}

			if (localMap.containsKey("data")) {
				if (localMap.get("data") != null) {
					ll_date.setVisibility(View.VISIBLE);
					data = localMap.get("data").toString();
					tvInfoUploadDate.setText(data);
				}
			}
			if (localMap.containsKey("location")) {
				if (localMap.get("location") != null) {
					location = localMap.get("location").toString();
					if (location != null && !"".equals(location)) {
						ll_location.setVisibility(View.VISIBLE);
						tvInfoLocation.setText(location);
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

				ll_cust.setVisibility(View.VISIBLE);
				this.clientName = localMap.get("clientName").toString();
				info_cust.setText(clientName);

			}

			if (localMap.get("istijiao") != null) {
				this.istijiao = localMap.get("istijiao").toString();
				if ("00".equals(istijiao)) {
					btAddInfo.setVisibility(View.VISIBLE);
					btupCust.setVisibility(View.VISIBLE);
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
							options.inSampleSize = 10;// 图片的长宽都是原来的1/10
							BufferedInputStream bis = new BufferedInputStream(f);
							bm = BitmapFactory.decodeStream(bis, null, options);
							imageView.setImageBitmap(bm);
							this.imgFile = new File(
									"/mnt/sdcard/DCIM/eastelsoft/"
											+ imgFileName);
							ll_img.setVisibility(View.VISIBLE);

						} catch (FileNotFoundException e) {
						}
					}
				}
			}

		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (locationHelper != null
				&& locationHelper.getWritableDatabase() != null) {
			locationHelper.getWritableDatabase().close();
		}
		if (bm != null && !bm.isRecycled())
			bm.recycle();
		bm = null;

	}

	private class OnInfoLocationClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				if (lon != null && !lon.equals("") && lat != null
						&& !lat.equals("")) {
					Intent intent = new Intent(BaifangViewActivity.this,
							ItemizedOverlayBaiduActivity.class);
					intent.putExtra("lon", lon);
					intent.putExtra("lat", lat);
					intent.putExtra("location", location);
					startActivity(intent);
				}
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	private class OnBtBackClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				BaifangViewActivity.this.finish();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	private class OnBtEditClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				BaifangViewActivity.this.finish();
				Intent intent = new Intent(BaifangViewActivity.this,
						BaifangEditActivity.class);
				intent.putExtra("info_auto_id", info_auto_id);
				startActivity(intent);

			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	private class OnBtAddClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				BaifangViewActivity.this.openPopupWindowPG("");
				btPopGps.setText(getResources().getString(
						R.string.loading_infoadd));
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
				map.put("date", data);
				map.put("lon", lon);
				map.put("lat", lat);
				map.put("accuracy", "-100");
				Log.i("BaiFangViewActivity",map.toString()+"---->"+url);
				String jsonStr = AndroidHttpClient.getContent(url, map,imgFile, "file1");
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
						try {
							DBUtil.updateLVisit(
									locationHelper.getWritableDatabase(),
									info_auto_id);

							Toast.makeText(
									BaifangViewActivity.this,
									getResources().getString(
											R.string.baifang_upload_succ),
									Toast.LENGTH_SHORT).show();
							BaifangViewActivity.this.finish();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					} else {
						/*
						 * dialog(BaifangAddActivity.this,
						 * getResources().getString(
						 * R.string.baifang_upload_err));
						 */
						Toast.makeText(
								BaifangViewActivity.this,
								getResources().getString(
										R.string.baifang_upload_err),
								Toast.LENGTH_SHORT).show();

					}
					break;
				case 1:
					/* dialog(BaifangAddActivity.this, msg.obj.toString()); */
					Toast.makeText(
							BaifangViewActivity.this,
							getResources().getString(
									R.string.baifang_upload_err),
							Toast.LENGTH_SHORT).show();

					break;

				}
			} catch (Exception e) {
				// 异常中断
			}
		}
	};

	private class OnBtImageViewClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				Intent intent = new Intent(BaifangViewActivity.this,
						PhotoViewActivity.class);
				intent.putExtra("imgFileName", imgFileName);
				intent.putExtra("opTag", "1");
				BaifangViewActivity.this.startActivityForResult(intent, 9999);
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}
}
