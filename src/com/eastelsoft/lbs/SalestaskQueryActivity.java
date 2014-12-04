package com.eastelsoft.lbs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.eastelsoft.lbs.SalesReportAddActivity.DataThread;
import com.eastelsoft.lbs.SalestaskAllocationAddActivity.SinitThread;
import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.adapter.SalesAllocationAddAdapterA;
import com.eastelsoft.lbs.adapter.SalesMonthsTargetAdapterA;
import com.eastelsoft.lbs.adapter.SalesReportAddAdapterA;
import com.eastelsoft.lbs.db.DBUtil;
import com.eastelsoft.lbs.db.LocationSQLiteHelper;
import com.eastelsoft.lbs.entity.GoodsMonthCustBean;
import com.eastelsoft.lbs.entity.GoodsMonthTargetBean;
import com.eastelsoft.lbs.entity.SetInfo;
import com.eastelsoft.util.Contant;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.IUtil;
import com.eastelsoft.util.contact.Utility;
import com.eastelsoft.util.http.AndroidHttpClient;

/**
 * 新增信息上报
 * 
 * @author xl
 */
public class SalestaskQueryActivity extends BaseActivity {
	public static final String TAG = "SalestaskQueryActivity";
	private Button btBack;
	// 商品
	private ListView lv;
	private SalesMonthsTargetAdapterA listadpter;
	private ArrayList<GoodsMonthTargetBean> all_info = new ArrayList<GoodsMonthTargetBean>();
	private TextView nodata_tv;

	private LocationSQLiteHelper locationHelper;
	private SetInfo set;

	private String uploadDate = "";
	private String uploadDatebackoneyear = "";
	SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat("yyyy-MM");

	private TextView year_month_tv;
	private int months_tp = 0;

	private Button select_left;
	private Button select_right;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.salestask_query);
		btBack = (Button) findViewById(R.id.btBack);
		btBack.setOnClickListener(new OnBtBackClickListenerImpl());
		select_left = (Button) findViewById(R.id.select_left);
		select_left.setOnClickListener(new OnBtleftClickListenerImpl());
		select_right = (Button) findViewById(R.id.select_right);
		select_right.setOnClickListener(new OnBtrightClickListenerImpl());

		lv = (ListView) findViewById(android.R.id.list);
		// 实例化适配器
		listadpter = new SalesMonthsTargetAdapterA(SalestaskQueryActivity.this,
				all_info);
		// 填充适配器
		lv.setAdapter(listadpter);
		Utility.setListViewHeightBasedOnChildren(lv);
		nodata_tv = (TextView) findViewById(R.id.nodata_tv);

		year_month_tv = (TextView) findViewById(R.id.year_month_tv);

		Calendar c = Calendar.getInstance();
		c.add(Calendar.MONTH, months_tp);
		uploadDate = simpleDateTimeFormat.format(c.getTime());
		Calendar cd = Calendar.getInstance();
		cd.add(Calendar.MONTH, -12);
		uploadDatebackoneyear = simpleDateTimeFormat.format(cd.getTime());

		year_month_tv.setText(dateTimeToStr(strToDateTime(uploadDate)));

		locationHelper = new LocationSQLiteHelper(this, null, null, 5);
		sp = getSharedPreferences("userdata", 0);
		set = IUtil.initSetInfo(sp);
		showDialog(PROCESS_DIALOG);
		Thread initThread = new Thread(new InitThread());
		initThread.start();

	}

	@Override
	protected void onPause() {
		super.onPause();
		if (dataThread != null) {
			try {
				dataThread.interrupt();
				dataThread = null;
			} catch (Exception e) {
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

		if (dataThread != null) {
			try {
				dataThread.interrupt();
				dataThread = null;
			} catch (Exception e) {
			}
		}
	}

	private class OnBtleftClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				months_tp--;
				Calendar c = Calendar.getInstance();
				c.add(Calendar.MONTH, months_tp);
				uploadDate = simpleDateTimeFormat.format(c.getTime());
				year_month_tv.setText(dateTimeToStr(strToDateTime(uploadDate)));

				// 搜索
				all_info.clear();
				showDialog(PROCESS_DIALOG);
				Thread sinitThread = new Thread(new SinitThread());
				sinitThread.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	private class OnBtrightClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				months_tp++;
				Calendar c = Calendar.getInstance();
				c.add(Calendar.MONTH, months_tp);
				uploadDate = simpleDateTimeFormat.format(c.getTime());
				year_month_tv.setText(dateTimeToStr(strToDateTime(uploadDate)));

				// 搜索
				all_info.clear();
				showDialog(PROCESS_DIALOG);
				Thread sinitThread = new Thread(new SinitThread());
				sinitThread.start();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

	class SinitThread implements Runnable {
		@Override
		public void run() {
			Message msg = handler.obtainMessage();
			msg.what = 6;
			try {
				localMap = DBUtil.getDataFromMonthstargetByMonths(
						locationHelper.getWritableDatabase(), uploadDate);
				if (localMap != null) {

					if (localMap.containsKey("goods_id")) {
						if (localMap.get("goods_id") != null) {
							goods_id = localMap.get("goods_id").toString();
							if (goods_id != null) {
								all_info.clear();
								all_info = DBUtil
										.getDataFromMonthstargetGoodsByGoodsId(
												locationHelper
														.getWritableDatabase(),
												goods_id);

							}

						}
					}
				}

			} catch (Exception e) {

			} finally {

				handler.sendMessage(msg);
			}
		}
	}

	class InitThread implements Runnable {
		@Override
		public void run() {
			Message msg = handler.obtainMessage();
			msg.what = 5;
			try {

				initData();
			} catch (Exception e) {

			} finally {

				handler.sendMessage(msg);
			}
		}
	}

	HashMap<String, Object> localMap;
	String goods_id;

	private void initData() {
		// uploadDate
		localMap = DBUtil.getDataFromMonthstargetByMonths(
				locationHelper.getWritableDatabase(), uploadDate);
		if (localMap != null) {

			if (localMap.containsKey("goods_id")) {
				if (localMap.get("goods_id") != null) {
					goods_id = localMap.get("goods_id").toString();
					if (goods_id != null) {
						all_info.clear();
						all_info = DBUtil
								.getDataFromMonthstargetGoodsByGoodsId(
										locationHelper.getWritableDatabase(),
										goods_id);

					}

				}
			}
		}

	}

	private Thread dataThread;
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try {

				switch (msg.what) {
				case 0:
					initData(); // 初始化客户数

					listadpter = new SalesMonthsTargetAdapterA(
							SalestaskQueryActivity.this, all_info);
					// 填充适配器
					lv.setAdapter(listadpter);
					Utility.setListViewHeightBasedOnChildren(lv);
					if (all_info.size() > 0) {

						nodata_tv.setVisibility(View.GONE);
					} else {
						Toast.makeText(SalestaskQueryActivity.this,
								"您选择的月份无月目标任务", Toast.LENGTH_SHORT).show();
						nodata_tv.setVisibility(View.VISIBLE);
					}
					dismissDialog(PROCESS_DIALOG);

					break;

				case 5:
					dataThread = new Thread(new DataThread());
					dataThread.start();
					break;
				case 6:
					listadpter = new SalesMonthsTargetAdapterA(
							SalestaskQueryActivity.this, all_info);
					// 填充适配器
					lv.setAdapter(listadpter);
					Utility.setListViewHeightBasedOnChildren(lv);
					if (all_info.size() > 0) {

						nodata_tv.setVisibility(View.GONE);
					} else {
						Toast.makeText(SalestaskQueryActivity.this,
								"您选择的月份无月目标任务", Toast.LENGTH_SHORT).show();
						nodata_tv.setVisibility(View.VISIBLE);
					}
					dismissDialog(PROCESS_DIALOG);
					break;

				}
			} catch (Exception e) {
				// 异常中断
				e.getMessage();
			}
		}
	};

	class DataThread implements Runnable {
		@Override
		public void run() {
			Message msg = handler.obtainMessage();
			msg.what = 0;
			try {
				sp = getSharedPreferences("userdata", 0);
				SetInfo set = IUtil.initSetInfo(sp);
				// 先发消息检是否需要更新
				String url = set.getHttpip() + Contant.P_M_ACTION;
				url += "&actiontype=1";
				url += "&gpsid=" + set.getDevice_id();
				url += "&Pin=" + set.getAuth_code();
				String jsonStr = AndroidHttpClient.getContent(url);
				jsonStr = IUtil.chkJsonStr(jsonStr);
				String updatecode = "";
				JSONArray array = new JSONArray(jsonStr);
				if (array.length() > 0) {
					JSONObject obj = array.getJSONObject(0);
					updatecode = obj.getString("updatecode");
					FileLog.i(TAG, "updatecode==>" + updatecode);
				}
//				String  one  = set.getMonthstargetupdatecode();
//				String  two = updatecode;
				// 如果需要更新
				if (!set.getMonthstargetupdatecode().equals(updatecode)) {

					url = set.getHttpip() + Contant.COMMODITYLISTACTION;
					Map<String, String> map = new HashMap<String, String>();
					map.put("reqCode", Contant.P_M_E_REQCODE);
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
						updatecode = obj1.getString("updatecode");
						FileLog.i(TAG, "Goodupdatecode==>" + updatecode);
						if (updatecode != null && !"".equals(updatecode)) {
							// 先删除所有客户

							DBUtil.deleteLl_monthstarget(locationHelper
									.getWritableDatabase());
							DBUtil.deletel_monthstarget_goods(locationHelper
									.getWritableDatabase());

							JSONArray array2 = obj1.getJSONArray("clientdata");
							for (int i = 0; i < array2.length(); i++) {
								JSONObject obj2 = array2.getJSONObject(i);

								String id = obj2.getString("id");
								String data = obj2.getString("date");
								String goods_id = UUID.randomUUID().toString();
								DBUtil.insertMonthstarget(
										locationHelper.getWritableDatabase(),
										id, data, goods_id);

								JSONArray packing1 = obj2.getJSONArray("list");
								for (int j = 0; j < packing1.length(); j++) {
									JSONObject obj3 = packing1.getJSONObject(j);

									GoodsMonthTargetBean tlp = new GoodsMonthTargetBean();
									// "c_id":"13d41296465b48a2a3cb41af62986f9a",
									// "c_name":"怡宝纯净水",
									// "packing":"箱", "target":"50",
									// "distribution":"165"}

									tlp.setEach_id(obj3.getString("c_id"));
									tlp.setName(obj3.getString("c_name"));
									tlp.setTarget(obj3.getString("target"));
									tlp.setPacking(obj3.getString("packing"));
									tlp.setDistribution("distribution");

									DBUtil.insertMonthstargetGoods(
											locationHelper
													.getWritableDatabase(),
											UUID.randomUUID().toString(),
											goods_id, tlp.getName(), tlp
													.getTarget(), tlp
													.getDistribution(), tlp
													.getEach_id(), tlp
													.getPacking());

								}

							}
						}
						IUtil.writeSharedPreference(sp,
								"monthstargetupdatecode", updatecode);
					}
					msg.what = 0;
				}

			} catch (Exception e) {

			} finally {
				handler.sendMessage(msg);
			}
		}
	}

	private class OnBtBackClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				SalestaskQueryActivity.this.finish();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	public static Date strToDateTime(String arg0) {
		Date resultDate = null;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
		try {
			resultDate = simpleDateFormat.parse(arg0);
			return resultDate;
		} catch (Exception e) {
			return resultDate;
		}
	}

	public static String dateTimeToStr(Date arg0) {
		String resultStr;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年M月");
		resultStr = simpleDateFormat.format(arg0);
		return resultStr;
	}

}
