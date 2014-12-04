/**
 * Copyright (c) 2012-7-21 www.eastelsoft.com
 * $ID TabCheckinActivity.java 上午12:42:13 $
 */
package com.eastelsoft.lbs;

import java.util.ArrayList;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.adapter.CstatisticsAdapter;
import com.eastelsoft.lbs.db.DBUtil;
import com.eastelsoft.lbs.db.LocationSQLiteHelper;
import com.eastelsoft.lbs.entity.LocBean;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.KCalendar;
import com.eastelsoft.util.KCalendar.OnCalendarClickListener;
import com.eastelsoft.util.KCalendar.OnCalendarDateChangedListener;
import com.eastelsoft.util.Util;

/**
 * 签到签退页面
 * 
 * @author lengcj
 */
public class CheckinStatisticsActivity extends BaseActivity {

	private static final String TAG = "CheckinStatisticsActivity";

	private Button btBack;
	private TextView tvCheckTitle;
	private String reportTag;
	private LocationSQLiteHelper locationHelper;
	LinearLayout te_c;
	private TextView nianyue;
	private String uploadDate = "";
	private ArrayList<LocBean> all_info = new ArrayList<LocBean>();
	private CstatisticsAdapter cadapter;
	private ListView lvContact;
	private TextView tvContact;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_checkinstatistics);
		btBack = (Button) findViewById(R.id.btBack);
		btBack.setOnClickListener(new OnBtBackClickListenerImpl());
		tvCheckTitle = (TextView) findViewById(R.id.tvCheckTitle);
		nianyue = (TextView) findViewById(R.id.year_month_tv);
		uploadDate = Util.getLocaleTime("yyyy年M月");
		if (uploadDate != null) {
			nianyue.setText(uploadDate);
		}
		Intent intent = this.getIntent();
		reportTag = intent.getStringExtra("reportTag");
		String checkTitle = "";
		if ("1".equals(reportTag)) {

			checkTitle = getResources().getString(
					R.string.title_activity_checkinsta);
			tvCheckTitle.setText(checkTitle);
		} else {

			checkTitle = getResources().getString(
					R.string.title_activity_checkoutsta);
			tvCheckTitle.setText(checkTitle);
		}

		locationHelper = new LocationSQLiteHelper(this, null, null, 5);
		te_c = (LinearLayout) findViewById(R.id.te_c);
		KCalendar calendar = new KCalendar(this, reportTag);
		te_c.addView(calendar);

		calendar.setOnCalendarClickListener(new OnCalendarClickListener() {
			@Override
			public void onCalendarClick(int row, int col, String dateFormat) {
				// Toast.makeText(CheckinStatisticsActivity.this, dateFormat,
				// Toast.LENGTH_SHORT).show();
				all_info.clear();
				all_info = DBUtil.getDataFromLLocA(
						locationHelper.getWritableDatabase(), reportTag,
						dateFormat);
				if (all_info.size() > 0) {
//					Toast.makeText(CheckinStatisticsActivity.this,
//							"有" + all_info.size() + "条", Toast.LENGTH_SHORT)
//							.show();
					openPopupWindowCust("");

				} else {
					if ("1".equals(reportTag)) {
						Toast.makeText(CheckinStatisticsActivity.this, "无签到记录哦！",
								Toast.LENGTH_SHORT).show();
						
					} else {
						Toast.makeText(CheckinStatisticsActivity.this, "无签退记录哦！",
								Toast.LENGTH_SHORT).show();
					}
					

				}

			}
		});

		calendar.setOnCalendarDateChangedListener(new OnCalendarDateChangedListener() {
			@Override
			public void onCalendarDateChanged(int year, int month) {

				nianyue.setText(year + "年" + month + "月");
			}
		});

	}

	

	protected void openPopupWindowCust(String msg) {
		try {
			LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			ViewGroup menuView = (ViewGroup) mLayoutInflater.inflate(
					R.layout.pop_c_statis, null, true);
			lvContact = (ListView) menuView.findViewById(R.id.lvContact);
			cadapter = new CstatisticsAdapter(CheckinStatisticsActivity.this,
					all_info);
			lvContact.setAdapter(cadapter);
			tvContact = (TextView) menuView.findViewById(R.id.tvContact);
			if ("1".equals(reportTag)) {
				tvContact.setText(getResources().getString(R.string.title_activity_checkin));
				
			} else {
				tvContact.setText(getResources().getString(R.string.title_activity_checkout));
			}

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

	private class OnBtBackClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				CheckinStatisticsActivity.this.finish();
				// openPopupWindow();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

}