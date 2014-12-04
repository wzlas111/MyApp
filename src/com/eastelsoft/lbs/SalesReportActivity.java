/**
 * Copyright (c) 2012-8-13 www.eastelsoft.com
 * $ID InfoActivity.java 下午3:22:36 $
 */
package com.eastelsoft.lbs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.eastelsoft.lbs.BaifangjiluActivity.AsyncUpdateDatasThread;
import com.eastelsoft.lbs.CheckinOnMapActivity.MyReceiver;
import com.eastelsoft.lbs.CheckinOnMapActivity.OverItemT;
import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.adapter.InfoListItemAdapterA;
import com.eastelsoft.lbs.adapter.SalesReportListAdapterA;
import com.eastelsoft.lbs.clock.Alarms;
import com.eastelsoft.lbs.db.DBUtil;
import com.eastelsoft.lbs.db.LocationSQLiteHelper;
import com.eastelsoft.lbs.entity.InfoBean;
import com.eastelsoft.lbs.entity.SalesReportBean;
import com.eastelsoft.lbs.entity.VisitBean;

import com.eastelsoft.util.Contant;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.GlobalVar;
import com.mapabc.mapapi.core.GeoPoint;

/**
 * 销量上报记录页面
 * 
 * @author xl
 */
public class SalesReportActivity extends BaseActivity {
	public static final String TAG = "SalesReportActivity";
	private ListView lv;
	private Button btBack;
	private Button btAddInfo;
	private LocationSQLiteHelper locationHelper;
	// 定义适配器
	private SalesReportListAdapterA listadpter;
	private ArrayList<SalesReportBean> all_info = new ArrayList<SalesReportBean>();

	private ArrayList<SalesReportBean> arrayList = new ArrayList<SalesReportBean>();

	private int number = 50;// 每次获取多少条数据
	// 总数
	private int totalCount = 0;
	// 总页数
	private int maxpage = 0;
	private boolean loadfinish = true;
	private int index = 0;

	View footer;

	// 查询
	private ImageView ivSearch;
	private ImageView ivReset;
	private TextView tvCustSearchText;
	String custSearchText = "";

	private ListChangeReceiver receiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		FileLog.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.salesreport_list);
		btBack = (Button) findViewById(R.id.btBack);
		btBack.setOnClickListener(new OnBtBackClickListenerImpl());
		btAddInfo = (Button) findViewById(R.id.btAddInfo);
		btAddInfo.setOnClickListener(new OnBtAddInfoClickListenerImpl());
		lv = (ListView) findViewById(android.R.id.list);
		footer = getLayoutInflater().inflate(R.layout.footer, null);
		lv.setOnItemClickListener(new OnItemClickListenerImpl());
		lv.setOnItemLongClickListener(new OnItemLongClickListenerImpl());
		lv.setOnScrollListener(new OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {

				if ((firstVisibleItem + visibleItemCount) == totalItemCount) {// 达到数据的最后一条记录
					if (totalItemCount > 0) {

						if ((totalItemCount < totalCount) && loadfinish) {

							loadfinish = false;
							lv.addFooterView(footer);
							new Thread(new AsyncUpdateDatasThread()).start();
						}

					}
				}
			}

		});

		tvCustSearchText = (TextView) this.findViewById(R.id.searchText);
		ivSearch = (ImageView) this.findViewById(R.id.doSearch);

		ivSearch.setOnClickListener(new OnBtSearchClickListenerImpl());

		locationHelper = new LocationSQLiteHelper(this, null, null, 5);
		// 初始化全局变量
		globalVar = (GlobalVar) getApplicationContext();

		// 注册广播接收器，接收定位数据信息
		receiver = new ListChangeReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Contant.SALESREPORTCHANGE_ACTION);
		this.registerReceiver(receiver, filter);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (locationHelper != null
				&& locationHelper.getWritableDatabase() != null) {
			locationHelper.getWritableDatabase().close();
		}
		if (receiver != null) {
			this.unregisterReceiver(receiver);
			receiver = null;
		}

	}

	@Override
	protected void onStart() {

		if (isOnStart) {
			FileLog.i(TAG, "onStart");
			custSearchText = "";
			init(custSearchText);
		} else {
			isOnStart = true;
		}
		super.onStart();
	}

	private boolean isOnStart = true;

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();

	}

	public class ListChangeReceiver extends BroadcastReceiver {
		// 自定义一个广播接收器
		@Override
		public void onReceive(Context context, Intent intent) {
			try {
				if (Contant.SALESREPORTCHANGE_ACTION.equals(intent.getAction())) {
					isOnStart = true;

				}

			} catch (Exception e) {
			}
		}
	}

	private class OnBtSearchClickListenerImpl implements OnClickListener {
		public void onClick(View v) {

			custSearchText = tvCustSearchText.getText().toString();
			showDialog(PROCESS_DIALOG);
			Thread dataThread = new Thread(new GetSearchInforThread());
			dataThread.start();

		}
	}

	class GetSearchInforThread implements Runnable {
		@Override
		public void run() {
			Message msg = handler.obtainMessage();
			msg.what = 6;

			try {

				// init(custSearchText);

			} catch (Exception e) {
				FileLog.i(TAG, e.getMessage());

			} finally {
				handler.sendMessage(msg);
			}
		}
	}

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

			List<SalesReportBean> list = new ArrayList<SalesReportBean>();

			/*
			 * list = DBUtil.getAllItems(index,
			 * number,locationHelper.getWritableDatabase());
			 */
			list = getNextpageItem(index, number, all_info);
			FileLog.i(TAG, "list " + list.size());

			Message msg = handler.obtainMessage();
			msg.obj = list;
			msg.what = 0;
			handler.sendMessage(msg);

		}

	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 0:
				arrayList.addAll((List<SalesReportBean>) (msg.obj));

				listadpter.notifyDataSetChanged();

				if (lv.getFooterViewsCount() > 0)
					lv.removeFooterView(footer);
				loadfinish = true;
				break;
			case 6:
				init(custSearchText);
				dismissDialog(PROCESS_DIALOG);
				break;
			}

		}

	};

	private void init(String key) {

		index = 0;

		all_info.clear();
		all_info = DBUtil.getDataFromSalesReport(
				locationHelper.getWritableDatabase(), key);
		totalCount = all_info.size();
		arrayList.clear();
		arrayList = getNextpageItem(index, number, all_info);

		// 实例化适配器
		listadpter = new SalesReportListAdapterA(SalesReportActivity.this,
				arrayList);
		// // 填充适配器
		lv.addFooterView(footer);// 添加页脚(放在ListView最后)
		lv.setAdapter(listadpter);
		lv.removeFooterView(footer);

	}

	private ArrayList<SalesReportBean> getNextpageItem(int index, int number,
			ArrayList<SalesReportBean> al) {
		ArrayList<SalesReportBean> a = new ArrayList<SalesReportBean>();
		for (int i = 0; i < number; i++) {
			int temp = i + index;
			if (temp < 0 || temp >= al.size()) {
				break;
			} else {
				SalesReportBean ib = new SalesReportBean();
				ib = al.get(temp);
				a.add(ib);
			}
		}

		return a;
	}

	private class OnBtBackClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				SalesReportActivity.this.finish();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	private class OnBtAddInfoClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				// 先清理掉原始数据
				// globalVar.setInfoLocation(null);
				// globalVar.setVideo1(null);
				// globalVar.setImgFileName1(null);
				// globalVar.setImgFileName2(null);
				// globalVar.setImgFileName3(null);

				Intent intent = new Intent(SalesReportActivity.this,
						SalesReportAddActivity.class);
				startActivity(intent);
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	private class OnItemClickListenerImpl implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long id) {
			isOnStart = false;
			String info_auto_id = all_info.get(position).getId();
			// 跳转到查看页面
			Intent intent = new Intent(SalesReportActivity.this,
					SalesReportDetailActivity.class);
			intent.putExtra("info_auto_id", info_auto_id);
			startActivity(intent);
		}
	}

	private HashMap<String, Object> localMap;

	private class OnItemLongClickListenerImpl implements
			OnItemLongClickListener {
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				final int position, long id) {
			new AlertDialog.Builder(SalesReportActivity.this)
					.setTitle(Contant.OP)
					.setItems(R.array.planarrcontent,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									String[] PK = getResources()
											.getStringArray(
													R.array.planarrcontent);
									if (PK[which].equals(Contant.OP_DEL)) {

										String info_auto_id = all_info.get(
												position).getId();
										// 删除原始数据

										localMap = DBUtil.getDataFromSalesReportByID(
												locationHelper
														.getWritableDatabase(),
												info_auto_id);

										if (localMap != null) {

											if (localMap
													.containsKey("goods_id")) {
												if (localMap.get("goods_id") != null) {
													String goods_id = localMap
															.get("goods_id")
															.toString();
													if (goods_id != null) {

														DBUtil.deleteSalesReportGoodsByGoodsId(
																locationHelper
																		.getWritableDatabase(),
																goods_id);

													}

												}
											}
										}
										DBUtil.deleteSalesReportByID(
												locationHelper
														.getWritableDatabase(),
												info_auto_id);

										init(custSearchText);
										Toast.makeText(
												SalesReportActivity.this,
												PK[which] + Contant.OP_SUCC,
												Toast.LENGTH_SHORT).show();
									}
									if (PK[which].equals(Contant.OP_VIEW)) {
										isOnStart = false;
										String info_auto_id = all_info.get(
												position).getId();
										// 跳转到查看页面
										Intent intent = new Intent(
												SalesReportActivity.this,
												SalesReportDetailActivity.class);
										intent.putExtra("info_auto_id",
												info_auto_id);
										startActivity(intent);
									}
								}
							})
					.setNegativeButton(Contant.OP_CLOSE,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// 关闭
								}
							}).show();
			return true;
		}

	}
}
