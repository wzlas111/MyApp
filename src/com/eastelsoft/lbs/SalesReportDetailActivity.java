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
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.DatePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView.OnItemClickListener;

import com.eastelsoft.lbs.CustActivity.ContactAdapter;
import com.eastelsoft.lbs.CustActivity.DataThread;
import com.eastelsoft.lbs.CustActivity.InitThread;

import com.eastelsoft.lbs.InfoActivity.AsyncUpdateDatasThread;
import com.eastelsoft.lbs.InfoAddActivity.OnBtCloseLClickListenerImpl;
import com.eastelsoft.lbs.InfoAddActivity.OnBtCloseLokClickListenerImpl;
import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.adapter.CustSelectAdapter;
import com.eastelsoft.lbs.adapter.InfoListItemAdapterA;
import com.eastelsoft.lbs.adapter.SalesAllocationAddAdapterA;
import com.eastelsoft.lbs.adapter.SalesReportAddAdapterA;
import com.eastelsoft.lbs.db.DBUtil;
import com.eastelsoft.lbs.db.LocationSQLiteHelper;
import com.eastelsoft.lbs.entity.GoodsBean;
import com.eastelsoft.lbs.entity.GoodsReportBean;
import com.eastelsoft.lbs.entity.InfoBean;
import com.eastelsoft.lbs.entity.SalesReportBean;
import com.eastelsoft.lbs.entity.SetInfo;
import com.eastelsoft.lbs.location.AMapAction;
import com.eastelsoft.lbs.location.BaiduMapAction;
import com.eastelsoft.util.CallBack;
import com.eastelsoft.util.Contant;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.FileUtil;
import com.eastelsoft.util.GlobalVar;
import com.eastelsoft.util.IUtil;
import com.eastelsoft.util.ImageThumbnail;
import com.eastelsoft.util.Util;
import com.eastelsoft.util.contact.PinyinComparatorTwo;
import com.eastelsoft.util.contact.Utility;
import com.eastelsoft.util.http.AndroidHttpClient;

/**
 * 新增销量上报
 * 
 * @author xl
 */
public class SalesReportDetailActivity extends BaseActivity {
	public static final String TAG = "SalestaskAllocationAddActivity";

	private String info_auto_id;
	// 返回
	private Button btBack;

	private Button btupCust;

	// 客户
	private EditText info_cust;

	// 日期
	private EditText info_date;

	// 商品
	private ListView lv;
	private SalesReportAddAdapterA listadpter;
	HashMap<String, Object> localMap;
	
	private ArrayList<GoodsReportBean> all_info = new ArrayList<GoodsReportBean>();
	private String goods_id = "";

	// 备注
	private EditText et_remark;

	// 位置info_location
	private EditText info_location;

	// 拍照
	private LinearLayout ll_photo;
	private ImageView imageView;
	private Bitmap bm;

	private String custid = "";
	private String clientName = "";

	private String uploadDate = "";

	private String remark = "";

	private String lon = "";
	private String lat = "";
	private String location = "";

	private File imgFile;
	private String imgFileName = "";
	
	private String istijiao = "";
	

	private LocationSQLiteHelper locationHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.salesreport_detail);
		btupCust = (Button) findViewById(R.id.btupCust);
		btupCust.setOnClickListener(new OnBtEditClickListenerImpl());

		btBack = (Button) findViewById(R.id.btBack);
		btBack.setOnClickListener(new OnBtBackClickListenerImpl());

		// 客户
		info_cust = (EditText) findViewById(R.id.info_cust);
		info_cust.setEnabled(false);
		//
		// 日期
		info_date = (EditText) findViewById(R.id.info_date);
		info_date.setEnabled(false);
		// 商品
		lv = (ListView) findViewById(R.id.goodslist);
		// lv.setOnItemClickListener(new OnItemClickListenerImpl());

		// 实例化适配器
		
		listadpter = new SalesReportAddAdapterA(SalesReportDetailActivity.this,
				all_info);
		// 填充适配器
		lv.setAdapter(listadpter);
		Utility.setListViewHeightBasedOnChildren(lv);

		// 备注
		et_remark = (EditText) findViewById(R.id.info_remark);
		et_remark.setEnabled(false);
		// 位置

		info_location = (EditText) findViewById(R.id.info_location);
		info_location.setEnabled(false);
		// 拍照
		ll_photo = (LinearLayout)findViewById(R.id.ll_photo);
		imageView = (ImageView) findViewById(R.id.info_img);
		imageView.setOnClickListener(new OnBtImageViewClickListenerImpl());

		locationHelper = new LocationSQLiteHelper(this, null, null, 5);
		Intent intent = getIntent();
		info_auto_id = intent.getStringExtra("info_auto_id");

		// 开启数据加载线程

		showDialog(PROCESS_DIALOG);
		Thread initdataThread = new Thread(new InitDataThread());
		initdataThread.start();

	}

	private void initdataback() {
		// TODO Auto-generated method stub
		

		// "id,clientid,clientName,date,goods_id,imgFile,remark,lon,lat,location,istijiao"
		
		if (localMap.get("istijiao") != null) {
			this.istijiao = localMap.get("istijiao").toString();
			if ("00".equals(istijiao)) {
				btupCust.setVisibility(View.VISIBLE);
			}
		}

		if (localMap != null) {

			if (localMap.containsKey("clientName")) {
				if (localMap.get("clientName") != null) {

					clientName = localMap.get("clientName").toString();
					info_cust.setText(clientName);
				}
			}
			if (localMap.containsKey("date")) {
				if (localMap.get("date") != null) {

					uploadDate = localMap.get("date").toString();
					info_date.setText(uploadDate);
				}
			}

			if (localMap.containsKey("remark")) {
				if (localMap.get("remark") != null) {

					remark = localMap.get("remark").toString();
					et_remark.setText(remark);
				}
			}
			if (localMap.containsKey("location")) {
				if (localMap.get("location") != null) {

					location = localMap.get("location").toString();

					if (location != null && !"".equals(location)) {

						info_location.setText(location);
					}

				}
			}
			if (localMap.get("lon") != null) {
				this.lon = localMap.get("lon").toString();
			}
			if (localMap.get("lat") != null) {
				this.lat = localMap.get("lat").toString();
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
							ll_photo.setVisibility(View.VISIBLE);
							imageView.setVisibility(View.VISIBLE);

						} catch (FileNotFoundException e) {
						}
					}
				}
			}
		}

	}

	class InitDataThread implements Runnable {
		@Override
		public void run() {
			Message msg = handler.obtainMessage();
			msg.what = 5;
			try {
				localMap = DBUtil.getDataFromSalesReportByID(
						locationHelper.getWritableDatabase(), info_auto_id);
				
				if (localMap != null) {

					if (localMap.containsKey("goods_id")) {
						if (localMap.get("goods_id") != null) {
							goods_id = localMap.get("goods_id").toString();
							if (goods_id != null) {
								all_info = DBUtil
										.getDataFromSalesReportGoodsByGoodsId(
												locationHelper
														.getWritableDatabase(),
												goods_id);

							}

						}
					}
				}
				
				
				
			} catch (Exception e) {
				FileLog.e(TAG, " initdata==>" + e.toString());
			} finally {

				handler.sendMessage(msg);
			}
		}
	}

	private class OnBtEditClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				SalesReportDetailActivity.this.finish();
				Intent intent = new Intent(SalesReportDetailActivity.this,
						SalesReportUpActivity.class);
				intent.putExtra("info_auto_id", info_auto_id);
				startActivity(intent);

			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try {

				switch (msg.what) {
				case 0:
					break;

				case 5:
					dismissDialog(PROCESS_DIALOG);
					System.out.println("sd"+all_info);
					listadpter = new SalesReportAddAdapterA(SalesReportDetailActivity.this,
							all_info);
					// 填充适配器
					lv.setAdapter(listadpter);
					Utility.setListViewHeightBasedOnChildren(lv);
					
					initdataback();

					break;

				}
			} catch (Exception e) {
				// 异常中断
				e.getMessage();
			}
		}

	};

	private class OnBtImageViewClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				Intent intent = new Intent(SalesReportDetailActivity.this,
						PhotoViewActivity.class);
				intent.putExtra("imgFileName", imgFileName);
				intent.putExtra("opTag", "1");
				SalesReportDetailActivity.this.startActivityForResult(intent,
						9999);
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	private class OnBtBackClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				SalesReportDetailActivity.this.finish();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

}
