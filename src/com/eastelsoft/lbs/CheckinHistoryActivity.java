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
import com.eastelsoft.lbs.adapter.LocListAdapterB;
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
public class CheckinHistoryActivity extends BaseActivity {

	private static final String TAG = "CheckinHistoryActivity";

	private Button btBack;
	private TextView tvCheckTitle;
	private Button btOutOK;
	private String reportTag;

	private ListView lv;

	private LocationSQLiteHelper locationHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_checkinhistory);
		btBack = (Button) findViewById(R.id.btBack);
		btBack.setOnClickListener(new OnBtBackClickListenerImpl());
		btOutOK = (Button) findViewById(R.id.btOutOK);
		btOutOK.setOnClickListener(new OnBthistroyClickListenerImpl());
		tvCheckTitle = (TextView) findViewById(R.id.tvCheckTitle);

		lv = (ListView) findViewById(android.R.id.list);
		footer = getLayoutInflater().inflate(R.layout.footer, null);
		lv.setOnScrollListener(new OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

				if ((firstVisibleItem + visibleItemCount) == totalItemCount) {// 达到数据的最后一条记录
					if (totalItemCount > 0) {
						// 当前页

						if ((totalItemCount < totalCount) && loadfinish) {

							loadfinish = false;
							lv.addFooterView(footer);
							new Thread(new AsyncUpdateDatasThread()).start();
						}

					}
				}

			}

		});

		Intent intent = this.getIntent();
		reportTag = intent.getStringExtra("reportTag");
		String checkTitle = "";
		if ("1".equals(reportTag)) {

			checkTitle = getResources().getString(
					R.string.title_activity_checkinhis);
			tvCheckTitle.setText(checkTitle);
		} else {

			checkTitle = getResources().getString(
					R.string.title_activity_checkouthis);
			tvCheckTitle.setText(checkTitle);
		}

		locationHelper = new LocationSQLiteHelper(this, null, null, 5);
		init();

	}

	// 定义适配器
	private LocListAdapterB listadpter;
	private ArrayList<LocBean> all_info = new ArrayList<LocBean>();
	private ArrayList<LocBean> arrayList = new ArrayList<LocBean>();

	private int number = 20;// 每次获取多少条数据
	// 总数
	private int totalCount = 0;
	// 总页数
	private int maxpage = 0;
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

			/*
			 * list = DBUtil.getAllItems(index,
			 * number,locationHelper.getWritableDatabase());
			 */
			list = getNextpageItem(index, number, all_info);
			FileLog.i(TAG, "list " + list.size());

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
				arrayList.addAll((List<LocBean>) (msg.obj));

				listadpter.notifyDataSetChanged();

				if (lv.getFooterViewsCount() > 0)
					lv.removeFooterView(footer);
				loadfinish = true;

				break;
			case 1:
				break;
			}
		}

	};

	private void init() {
		all_info = DBUtil.getDataFromLLocA(
				locationHelper.getWritableDatabase(), reportTag);
		totalCount = all_info.size();
		FileLog.i(TAG, totalCount);
		maxpage = totalCount % number == 0 ? totalCount / number : totalCount
				/ number + 1;
		FileLog.i(TAG, maxpage);

		arrayList = getNextpageItem(index, number, all_info);
		FileLog.i(TAG, arrayList);

		// 实例化适配器
		listadpter = new LocListAdapterB(CheckinHistoryActivity.this, arrayList);
		// 填充适配器
		lv.addFooterView(footer);// 添加页脚(放在ListView最后)
		lv.setAdapter(listadpter);

		lv.removeFooterView(footer);

	}

	private ArrayList<LocBean> getNextpageItem(int index, int number,
			ArrayList<LocBean> al) {
		ArrayList<LocBean> a = new ArrayList<LocBean>();
		for (int i = 0; i < number; i++) {
			int temp = i + index;
			if (temp < 0 || temp >= al.size()) {
				break;
			} else {
				LocBean ib = new LocBean();
				ib = al.get(temp);
				a.add(ib);
			}
		}

		return a;
	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onDestroy() {
		FileLog.i(TAG, "onDestroy....");
		try {
			super.onDestroy();
			if (locationHelper != null) {
				locationHelper.getWritableDatabase().close();
			}
		} catch (Exception e) {
		}
	}

	@Override
	protected void onStop() {
		super.onStop();

	}

	private class OnBtBackClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				CheckinHistoryActivity.this.finish();
				// openPopupWindow();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}
	private Intent intent;
	private class OnBthistroyClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				
				intent = new Intent(CheckinHistoryActivity.this, CheckinStatisticsActivity.class);
				intent.putExtra("reportTag", reportTag);
				startActivity(intent);
				
				
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

}