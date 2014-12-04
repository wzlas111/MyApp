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
import java.util.HashMap;
import java.util.Locale;
import java.util.UUID;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;

import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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

import com.eastelsoft.lbs.InfoActivity.AsyncUpdateDatasThread;
import com.eastelsoft.lbs.SalesReportAddActivity.AddInfoThread;
import com.eastelsoft.lbs.SalesReportDetailActivity.InitDataThread;

import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.adapter.CustSelectAdapter;
import com.eastelsoft.lbs.adapter.InfoListItemAdapterA;
import com.eastelsoft.lbs.adapter.SalesAllocationAddAdapterA;
import com.eastelsoft.lbs.adapter.SalesReportAddAdapterA;
import com.eastelsoft.lbs.db.DBUtil;
import com.eastelsoft.lbs.db.LocationSQLiteHelper;
import com.eastelsoft.lbs.entity.CustBean;
import com.eastelsoft.lbs.entity.GoodsMonthCustBean;
import com.eastelsoft.lbs.entity.GoodsReportBean;
import com.eastelsoft.lbs.entity.InfoBean;
import com.eastelsoft.lbs.entity.SalesReportBean;
import com.eastelsoft.lbs.entity.SalestaskAllocationBean;
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
import com.eastelsoft.util.contact.Utility;
import com.eastelsoft.util.http.AndroidHttpClient;

/**
 * 新增信息上报
 * 
 * @author xl
 */
public class SalestaskAllocationDetailActivity extends BaseActivity {
	public static final String TAG = "SalestaskAllocationDetailActivity";
	private String info_auto_id;
	// 返回
	private Button btBack;
	private Button btupCust;
	// 客户
	private EditText info_cust;

	// 日期
	private EditText info_date;
	private SimpleDateFormat fmtDateAndTime = new SimpleDateFormat("yyyyMM");
	private Calendar dateAndTime = Calendar.getInstance(Locale.CHINA);
	// 商品
	private ListView lv;
	private SalesAllocationAddAdapterA listadpter;
	private ArrayList<GoodsMonthCustBean> all_info = new ArrayList<GoodsMonthCustBean>();
	
    HashMap<String, Object> localMap;
	private String goods_id = "";

	private String custid = "";
	private String clientName = "";
	private String uploadDate = "";
	
	private String istijiao = "";

	private LocationSQLiteHelper locationHelper;
	private SetInfo set;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.salesallocation_detail);

		btBack = (Button) findViewById(R.id.btBack);
		btBack.setOnClickListener(new OnBtBackClickListenerImpl());

		btupCust = (Button) findViewById(R.id.btAddInfo);
		btupCust.setOnClickListener(new OnBtAddInfoClickListenerImpl());

		// 客户
		info_cust = (EditText) findViewById(R.id.info_cust);
		info_cust.setEnabled(false);

		info_date = (EditText) findViewById(R.id.info_date);
		info_date.setEnabled(false);
		lv = (ListView) findViewById(android.R.id.list);

		// 实例化适配器
		listadpter = new SalesAllocationAddAdapterA(
				SalestaskAllocationDetailActivity.this, all_info);
		// 填充适配器
		lv.setAdapter(listadpter);
		Utility.setListViewHeightBasedOnChildren(lv);

		locationHelper = new LocationSQLiteHelper(this, null, null, 5);
		
		Intent intent = getIntent();
		info_auto_id = intent.getStringExtra("info_auto_id");
		// 开启数据加载线程

		showDialog(PROCESS_DIALOG);
		Thread initdataThread = new Thread(new InitDataThread());
		initdataThread.start();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();

	}

	class InitDataThread implements Runnable {
		@Override
		public void run() {
			Message msg = handler.obtainMessage();
			msg.what = 5;
			try {
				localMap = DBUtil.getDataFromSalesallocationByID(
						locationHelper.getWritableDatabase(), info_auto_id);

				if (localMap != null) {

					if (localMap.containsKey("goods_id")) {
						if (localMap.get("goods_id") != null) {
							goods_id = localMap.get("goods_id").toString();
							if (goods_id != null) {
								all_info = DBUtil
										.getDataFromSalesallocationGoodsByGoodsId(
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

	private class OnBtAddInfoClickListenerImpl implements OnClickListener {
		public void onClick(View v) {

			try {
				SalestaskAllocationDetailActivity.this.finish();
				Intent intent = new Intent(
						SalestaskAllocationDetailActivity.this,
						SalestaskAllocationUpActivity.class);
				intent.putExtra("info_auto_id", info_auto_id);
				startActivity(intent);

			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}

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
					System.out.println("sd" + all_info);
					listadpter = new SalesAllocationAddAdapterA(
							SalestaskAllocationDetailActivity.this, all_info);
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
	
	
	private void initdataback() {
		// TODO Auto-generated method stub
		

		// "id,clientid,clientName,date,goods_id,istijiao"
		
		
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

			
		}

	}

	private class OnBtBackClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				SalestaskAllocationDetailActivity.this.finish();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

}
