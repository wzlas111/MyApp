/**
 * Copyright (c) 2012-8-13 www.eastelsoft.com
 * $ID InfoActivity.java 下午3:22:36 $
 */
package com.eastelsoft.lbs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
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

import com.eastelsoft.lbs.SalesReportActivity.GetSearchInforThread;
import com.eastelsoft.lbs.SalesReportActivity.ListChangeReceiver;
import com.eastelsoft.lbs.SalestaskAllocationAddActivity.SinitThread;
import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.adapter.InfoListItemAdapterA;
import com.eastelsoft.lbs.adapter.SalesAllocatinListAdapterA;
import com.eastelsoft.lbs.adapter.SalesReportListAdapterA;
import com.eastelsoft.lbs.db.DBUtil;
import com.eastelsoft.lbs.db.LocationSQLiteHelper;
import com.eastelsoft.lbs.entity.GoodsMonthCustBean;
import com.eastelsoft.lbs.entity.GoodsMonthTargetBean;
import com.eastelsoft.lbs.entity.InfoBean;
import com.eastelsoft.lbs.entity.SalesReportBean;
import com.eastelsoft.lbs.entity.SalestaskAllocationBean;
import com.eastelsoft.lbs.entity.SetInfo;
import com.eastelsoft.util.Contant;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.GlobalVar;
import com.eastelsoft.util.IUtil;
import com.eastelsoft.util.http.AndroidHttpClient;

/**
 * 销量上报记录页面
 * 
 * @author xl
 */
public class SalestaskAllocationActivity extends BaseActivity {
	public static final String TAG = "SalestaskAllocationActivity";
	private ListView lv;
	private Button btBack;
	private Button btAddInfo;

	private LocationSQLiteHelper locationHelper;

	// 定义适配器
	private SalesAllocatinListAdapterA listadpter;
	private ArrayList<SalestaskAllocationBean> all_info = new ArrayList<SalestaskAllocationBean>();

	private ArrayList<SalestaskAllocationBean> arrayList = new ArrayList<SalestaskAllocationBean>();

	private int number = 50;// 每次获取多少条数据
	// 总数
	private int totalCount = 0;
	// 总页数
	private int maxpage = 0;
	private boolean loadfinish = true;
	private int index = 0;

	View footer;

	private String uploadDatebackoneyear = "";
	SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat("yyyy-MM");

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
		setContentView(R.layout.salesallocatin_list);
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

		locationHelper = new LocationSQLiteHelper(this, null, null, 5);
		// 初始化全局变量
		globalVar = (GlobalVar) getApplicationContext();
		Calendar cd = Calendar.getInstance();
		cd.add(Calendar.MONTH, -6);
		uploadDatebackoneyear = simpleDateTimeFormat.format(cd.getTime());

		tvCustSearchText = (TextView) this.findViewById(R.id.searchText);
		ivSearch = (ImageView) this.findViewById(R.id.doSearch);

		ivSearch.setOnClickListener(new OnBtSearchClickListenerImpl());

		// 注册广播接收器，接收定位数据信息
		receiver = new ListChangeReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(Contant.SALESTASKALLOCATIONCHANGE_ACTION);
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
		FileLog.i(TAG, "onStart");
		super.onStart();

		if (isOnStart) {
			custSearchText = "";
			init(custSearchText);
			if (all_info.size() < 1) {
				showDialog(PROCESS_DIALOG);
				Thread sinitThread = new Thread(new SinitThread());
				sinitThread.start();

			}
		} else {
			isOnStart = true;
		}

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
				if (Contant.SALESTASKALLOCATIONCHANGE_ACTION.equals(intent.getAction())) {
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

	class SinitThread implements Runnable {
		@Override
		public void run() {
			Message msg = handler.obtainMessage();
			msg.what = 6;
			try {

				sp = getSharedPreferences("userdata", 0);
				SetInfo set = IUtil.initSetInfo(sp);
				String url = set.getHttpip() + Contant.COMMODITYLISTACTION;
				Map<String, String> map = new HashMap<String, String>();
				map.put("reqCode", Contant.CLIENT_M_UPDATE_ACTION);
				map.put("actiontype", "2");
				map.put("gpsid", set.getDevice_id());
				map.put("date1", uploadDatebackoneyear);
				map.put("date2", "");
				String jsonStr1 = AndroidHttpClient.getContent(url, map);
				FileLog.i(TAG, "jsonStr1==>" + jsonStr1);
				jsonStr1 = IUtil.chkJsonStr(jsonStr1);
				JSONArray array1 = new JSONArray(jsonStr1);
				if (array1.length() > 0) {
					JSONObject obj1 = array1.getJSONObject(0);

					JSONArray array2 = obj1.getJSONArray("clientdata");
					for (int i = 0; i < array2.length(); i++) {
						JSONObject obj2 = array2.getJSONObject(i);
						// {"id":"de1ada9538294a3296cac67714cf1603",
						// "date":"201307","clientid":"67b9334297564989a3ea4bf82233cf58",
						// "clientname":"胡莹","list":[{"c_id":"13d41296465b48a2a3cb41af62986f9a",
						// "c_name":"怡宝纯净水", "packing":"箱",
						// "target":"2", "totaltarget":"350"},
						// {"c_id":"c7d44efb251b427798388bc7cc67418e",
						// "c_name":"咪咪虾条",
						// "packing":"袋", "target":"8", "totaltarget":"990"},
						// {"c_id":"d473fc56342a4828b351ba315b3463ac",
						// "c_name":"上好佳薯片", "packing":"袋", "target":"89",
						// "totaltarget":"270"},
						// {"c_id":"2ebe14f682f34c76adffb3bf89a1fe17",
						// "c_name":"可乐啊", "packing":"箱", "target":"5",
						// "totaltarget":"1441"}]}
						String id = obj2.getString("id");
						String clientid = obj2.getString("clientid");
						String clientname = obj2.getString("clientname");
						String date = obj2.getString("date");

						String goods_id = UUID.randomUUID().toString();
						DBUtil.insertSalesallocation(
								locationHelper.getWritableDatabase(), id,
								clientid, clientname, date, goods_id, "11");

						JSONArray packing1 = obj2.getJSONArray("list");
						for (int j = 0; j < packing1.length(); j++) {
							JSONObject obj3 = packing1.getJSONObject(j);

							String c_id = obj3.getString("c_id");
							String c_name = obj3.getString("c_name");
							String packing = obj3.getString("packing");
							String target = obj3.getString("target");
							String totaltarget = obj3.getString("totaltarget");

							DBUtil.insertSalesallocationGoods(locationHelper
									.getWritableDatabase(), UUID.randomUUID()
									.toString(), goods_id, c_name, target,
									totaltarget, packing, c_id);

						}

					}
				}

			} catch (Exception e) {

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

			List<SalestaskAllocationBean> list = new ArrayList<SalestaskAllocationBean>();

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
				arrayList.addAll((List<SalestaskAllocationBean>) (msg.obj));

				listadpter.notifyDataSetChanged();

				if (lv.getFooterViewsCount() > 0)
					lv.removeFooterView(footer);
				loadfinish = true;

				break;
			case 1:
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

		all_info = DBUtil.getDataFromSalesallocation(
				locationHelper.getWritableDatabase(), key);
		totalCount = all_info.size();

		arrayList.clear();
		arrayList = getNextpageItem(index, number, all_info);

		// 实例化适配器
		listadpter = new SalesAllocatinListAdapterA(
				SalestaskAllocationActivity.this, arrayList);
		// 填充适配器
		lv.addFooterView(footer);// 添加页脚(放在ListView最后)
		lv.setAdapter(listadpter);
		lv.removeFooterView(footer);

	}

	private ArrayList<SalestaskAllocationBean> getNextpageItem(int index,
			int number, ArrayList<SalestaskAllocationBean> al) {
		ArrayList<SalestaskAllocationBean> a = new ArrayList<SalestaskAllocationBean>();
		for (int i = 0; i < number; i++) {
			int temp = i + index;
			if (temp < 0 || temp >= al.size()) {
				break;
			} else {
				SalestaskAllocationBean ib = new SalestaskAllocationBean();
				ib = al.get(temp);
				a.add(ib);
			}
		}

		return a;
	}

	private class OnBtBackClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				SalestaskAllocationActivity.this.finish();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	private class OnBtAddInfoClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				Intent intent = new Intent(SalestaskAllocationActivity.this,
						SalestaskAllocationAddActivity.class);
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
			Intent intent = new Intent(SalestaskAllocationActivity.this,
					SalestaskAllocationDetailActivity.class);
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
			new AlertDialog.Builder(SalestaskAllocationActivity.this)
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

										localMap = DBUtil
												.getDataFromSalesallocationByID(
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
														DBUtil.deleteAllocationGoodsByGoodsId(
																locationHelper
																		.getWritableDatabase(),
																goods_id);

													}

												}
											}
										}

										DBUtil.deleteAllocationByID(
												locationHelper
														.getWritableDatabase(),
												info_auto_id);

										init(custSearchText);
										Toast.makeText(
												SalestaskAllocationActivity.this,
												PK[which] + Contant.OP_SUCC,
												Toast.LENGTH_SHORT).show();
									}
									if (PK[which].equals(Contant.OP_VIEW)) {
										isOnStart = false;
										String info_auto_id = all_info.get(
												position).getId();
										// 跳转到查看页面
										Intent intent = new Intent(
												SalestaskAllocationActivity.this,
												SalestaskAllocationDetailActivity.class);
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
