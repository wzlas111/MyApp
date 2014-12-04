/**
 * Copyright (c) 2012-8-13 www.eastelsoft.com
 * $ID InfoAddActivity.java 下午9:47:37 $
 */
package com.eastelsoft.lbs;

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
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
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
import android.view.inputmethod.InputMethodManager;
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

import com.eastelsoft.lbs.SalesReportUpActivity.InitDataThread;
import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.adapter.CustSelectAdapter;
import com.eastelsoft.lbs.adapter.InfoListItemAdapterA;
import com.eastelsoft.lbs.adapter.SalesAllocationAddAdapterA;
import com.eastelsoft.lbs.db.DBUtil;
import com.eastelsoft.lbs.db.LocationSQLiteHelper;
import com.eastelsoft.lbs.entity.CustBean;
import com.eastelsoft.lbs.entity.GoodsMonthCustBean;
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
import com.eastelsoft.util.contact.PinyinComparatorTwo;
import com.eastelsoft.util.contact.Utility;
import com.eastelsoft.util.http.AndroidHttpClient;

/**
 * 新增信息上报
 * 
 * @author xl
 */
public class SalestaskAllocationUpActivity extends BaseActivity {
	public static final String TAG = "SalestaskAllocationAddActivity";

	// 返回
	private Button btBack;
	private Button btAddInfo;
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

	// 日期
	private EditText info_date;
	private SimpleDateFormat fmtDateAndTime = new SimpleDateFormat("yyyy-MM");
	private Calendar dateAndTime = Calendar.getInstance(Locale.CHINA);
	// 商品
	private ListView lv;
	private SalesAllocationAddAdapterA listadpter;
	private ArrayList<GoodsMonthCustBean> all_info = new ArrayList<GoodsMonthCustBean>();

	private String custid = "";
	private String clientName = "";
	private String uploadDate = "";

	private String c_id = "";
	private String target = "";

	private LocationSQLiteHelper locationHelper;
	private SetInfo set;

	private String info_auto_id = "";
	HashMap<String, Object> localMap;
	private String goods_id = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.salesallocation_add);

		btBack = (Button) findViewById(R.id.btBack);
		btBack.setOnClickListener(new OnBtBackClickListenerImpl());

		btAddInfo = (Button) findViewById(R.id.btAddInfo);
		btAddInfo.setOnClickListener(new OnBtAddInfoClickListenerImpl());

		// 客户
		info_cust = (EditText) findViewById(R.id.info_cust);
		info_cust.setOnTouchListener(new OnEdCustTouchListenerImpl());
		info_cust.setOnClickListener(new OnEdCustClickListenerImpl());

		info_date = (EditText) findViewById(R.id.info_date);
		info_date.setOnTouchListener(new OnEdAreaTouchListenerImpl());
		info_date.setOnClickListener(new OnEdAreaClickListenerImpl());

		// SimpleDateFormat simpleDateTimeFormat = new
		// SimpleDateFormat("yyyyMM");
		// Calendar c = Calendar.getInstance();
		// c.add(Calendar.MONTH, 1);
		// uploadDate = simpleDateTimeFormat.format(c.getTime());
		// if (uploadDate != null) {
		// info_date.setText(uploadDate);
		// // 搜索
		// all_info.clear();
		// showDialog(PROCESS_DIALOG);
		// Thread sinitThread = new Thread(new SinitThread());
		// sinitThread.start();
		// }

		lv = (ListView) findViewById(android.R.id.list);

		// 实例化适配器
		listadpter = new SalesAllocationAddAdapterA(
				SalestaskAllocationUpActivity.this, all_info);
		// 填充适配器
		lv.setAdapter(listadpter);
		Utility.setListViewHeightBasedOnChildren(lv);

		// locationHelper = new LocationSQLiteHelper(this, null, null, 5);
		// sp = getSharedPreferences("userdata", 0);
		// set = IUtil.initSetInfo(sp);
		//
		// // 初始化一些数据
		//
		// Thread initThread = new Thread(new InitThread());
		// initThread.start();

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

	// class InitThread implements Runnable {
	// @Override
	// public void run() {
	// Message msg = handler.obtainMessage();
	// msg.what = 5;
	// try {
	// // 客户
	// mData = DBUtil.getDataFromLCustMg(locationHelper
	// .getWritableDatabase());
	// Collections.sort(mData, new PinyinComparatorTwo());
	//
	// } catch (Exception e) {
	// FileLog.e(TAG, "getCustFromServer initdata==>" + e.toString());
	// } finally {
	//
	// handler.sendMessage(msg);
	// }
	// }
	// }

	class InitDataThread implements Runnable {
		@Override
		public void run() {
			Message msg = handler.obtainMessage();
			msg.what = 5;
			try {
				// 客户
				mData = DBUtil.getDataFromLCustMg(locationHelper
						.getWritableDatabase());
				Collections.sort(mData, new PinyinComparatorTwo());

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
			cadapter = new CustSelectAdapter(
					SalestaskAllocationUpActivity.this, mData_tp);
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

	private class OnItemClickListenerImpl implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub

			custid = mData_tp.get(arg2).getId();
			info_cust.setText(mData_tp.get(arg2).getClientName());

			try {
				popupWindow.dismiss();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}

		}
	}

	// protected class OnBtCloseLClickListenerImpl implements OnClickListener {
	// public void onClick(View v) {
	// try {
	// popupWindow.dismiss();
	// } catch (Exception e) {
	// FileLog.e(TAG, e.toString());
	// }
	// }
	// }

	private class OnBtAddInfoClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				// 上传数据
				uploadDate = info_date.getText().toString();
				// title = etTitle.getText().toString();
				// remark = etContent.getText().toString();
				all_info = listadpter.getArrayList();
				c_id = "";
				target = "";
				for (int i = 0; i < all_info.size(); i++) {
					if (all_info.get(i).getAmount() != null
							&& !"".equals(all_info.get(i).getAmount())) {
						c_id += all_info.get(i).getId() + "|";

						target += all_info.get(i).getAmount() + "|";

					}

				}
				if (c_id.endsWith("|"))
					c_id = c_id.substring(0, (c_id.length() - 1));
				if (target.endsWith("|"))
					target = target.substring(0, (target.length() - 1));
				// String s =target;

				if ("".equals(custid.trim())) {
					respMsg = "请输入客户";
					Toast.makeText(getApplicationContext(), respMsg,
							Toast.LENGTH_SHORT).show();
					return;
				}
				if ("".equals(c_id.trim())) {
					respMsg = "商品至少填一项哦";
					Toast.makeText(getApplicationContext(), respMsg,
							Toast.LENGTH_SHORT).show();
					return;
				}
				//发送个修改广播给列表界面
				Intent it = new Intent(Contant.SALESTASKALLOCATIONCHANGE_ACTION);
				SalestaskAllocationUpActivity.this.sendBroadcast(it);
				
				SalestaskAllocationUpActivity.this.openPopupWindowPG("");
				btPopGps.setText(getResources().getString(
						R.string.loading_allocationadd));

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

				String url = set.getHttpip() + Contant.COMMODITYLISTACTION;
				Map<String, String> map = new HashMap<String, String>();
				map.put("reqCode", Contant.CLIENT_MARKET_ACTION);
				map.put("gpsid", set.getDevice_id());
				map.put("pin", set.getAuth_code());
				map.put("clientid", custid);
				map.put("date", uploadDate);
				map.put("c_id", c_id);
				map.put("target", target);

				String jsonStr = AndroidHttpClient.getContent(url, map);
				jsonStr = IUtil.chkJsonStr(jsonStr);
				FileLog.i(TAG, jsonStr);

				JSONArray array = new JSONArray(jsonStr);
				String resultcode = "";
				if (array.length() > 0) {
					JSONObject obj = array.getJSONObject(0);
					resultcode = obj.getString("ResultCode");
					FileLog.i(TAG, "resultcode==>" + resultcode);
				}
				msg.what = 3;
				msg.obj = resultcode;
				handler.sendMessage(msg);

			} catch (Exception e) {

				respMsg = getResources().getString(
						R.string.allocation_upload_err);
				msg.what = 2;
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

				switch (msg.what) {
				case 0:
					break;

				case 2:
					try {
						popupWindowPg.dismiss();
					} catch (Exception e) {
						FileLog.e(TAG, e.toString());
					}
					openPopupWindowAx("上传失败，是否存入本地");

					break;

				case 3:
					try {
						popupWindowPg.dismiss();
					} catch (Exception e) {
						FileLog.e(TAG, e.toString());
					}
					if ("1".equals(msg.obj.toString())) {

						try {
							// 存入数据库
							String ss = UUID.randomUUID().toString();
							DBUtil.insertSalesallocation(locationHelper
									.getWritableDatabase(), UUID.randomUUID()
									.toString(), custid, info_cust.getText()
									.toString(), uploadDate, ss, "11");
							for (int i = 0; i < all_info.size(); i++) {

								DBUtil.insertSalesallocationGoods(
										locationHelper.getWritableDatabase(),
										UUID.randomUUID().toString(), ss,
										all_info.get(i).getGoodsname(),
										all_info.get(i).getAmount(), all_info
												.get(i).getTarget(), all_info
												.get(i).getGoodsunit(),
										all_info.get(i).getId());

							}
							// 删除原始数据
							DBUtil.deleteAllocationByID(
									locationHelper.getWritableDatabase(), info_auto_id);

							DBUtil.deleteAllocationGoodsByGoodsId(
									locationHelper.getWritableDatabase(), goods_id);

							Toast.makeText(
									SalestaskAllocationUpActivity.this,
									getResources().getString(
											R.string.allocation_upload_succ),
									Toast.LENGTH_SHORT).show();
							SalestaskAllocationUpActivity.this.finish();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					} else {

						openPopupWindowAx("上传失败，是否存入本地");

					}

					break;

				case 5:
					dismissDialog(PROCESS_DIALOG);
					System.out.println("sd" + all_info);
					listadpter = new SalesAllocationAddAdapterA(
							SalestaskAllocationUpActivity.this, all_info);
					// 填充适配器
					lv.setAdapter(listadpter);
					Utility.setListViewHeightBasedOnChildren(lv);

					initdataback();
					break;
				case 6:
					listadpter.notifyDataSetChanged();
					Utility.setListViewHeightBasedOnChildren(lv);
					if (all_info.size() > 0) {
					} else {
						Toast.makeText(SalestaskAllocationUpActivity.this,
								"您选择的月份无月目标任务", Toast.LENGTH_SHORT).show();

					}
					dismissDialog(PROCESS_DIALOG);
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
				e.getMessage();
			}
		}
	};

	private void initdataback() {
		// TODO Auto-generated method stub

		// "id,clientid,clientName,date,goods_id,istijiao"

		if (localMap != null) {
			if (localMap.containsKey("clientid")) {
				if (localMap.get("clientid") != null) {

					custid = localMap.get("clientid").toString();

				}
			}

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

				String ss = UUID.randomUUID().toString();
				DBUtil.insertSalesallocation(locationHelper
						.getWritableDatabase(), UUID.randomUUID().toString(),
						custid, info_cust.getText().toString(), uploadDate, ss,
						"00");
				for (int i = 0; i < all_info.size(); i++) {

					DBUtil.insertSalesallocationGoods(locationHelper
							.getWritableDatabase(), UUID.randomUUID()
							.toString(), ss, all_info.get(i).getGoodsname(),
							all_info.get(i).getAmount(), all_info.get(i)
									.getTarget(), all_info.get(i)
									.getGoodsunit(), all_info.get(i).getId());

				}
				// 删除原始数据
				DBUtil.deleteAllocationByID(
						locationHelper.getWritableDatabase(), info_auto_id);

				DBUtil.deleteAllocationGoodsByGoodsId(
						locationHelper.getWritableDatabase(), goods_id);

				Toast.makeText(SalestaskAllocationUpActivity.this, "存入本地成功",
						Toast.LENGTH_SHORT).show();
				SalestaskAllocationUpActivity.this.finish();

			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
				Toast.makeText(SalestaskAllocationUpActivity.this, "存入本地失败",
						Toast.LENGTH_SHORT).show();

			}

		}
	}

	private class OnBtBackClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				SalestaskAllocationUpActivity.this.finish();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	private class OnEdAreaTouchListenerImpl implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (v.getId()) {
			case R.id.info_date:
				// actionAlertDialog();
				info_date.requestFocus();
				break;
			default:
				break;
			}
			return false;
		}

	}

	private class OnEdAreaClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				// 生成一个DatePickerDialog对象，并显示。显示的DatePickerDialog控件可以选择年月日，并设置
				new DatePickerDialog(SalestaskAllocationUpActivity.this, d,
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
			uploadDate = fmtDateAndTime.format(dateAndTime.getTime());
			info_date.setText(uploadDate);

			// 搜索
			all_info.clear();
			showDialog(PROCESS_DIALOG);
			Thread sinitThread = new Thread(new SinitThread());
			sinitThread.start();

		}

	};

	class SinitThread implements Runnable {
		@Override
		public void run() {
			Message msg = handler.obtainMessage();
			msg.what = 6;
			try {

				sp = getSharedPreferences("userdata", 0);
				SetInfo set = IUtil.initSetInfo(sp);
				all_info.clear();
				String url = set.getHttpip() + Contant.COMMODITYLISTACTION;
				Map<String, String> map = new HashMap<String, String>();
				map.put("reqCode", Contant.P_M_E_REQCODE);
				map.put("actiontype", "2");
				map.put("gpsid", set.getDevice_id());
				// map.put("Pin", set.getAuth_code());
				map.put("date1", uploadDate);
				map.put("date2", uploadDate);
				String jsonStr1 = AndroidHttpClient.getContent(url, map);
				FileLog.i(TAG, "jsonStr1==>" + jsonStr1);

				// {"clientdata":[{"id":"100011",
				// "date":"201308","list":[{"c_id":"2ebe14f682f34c76adffb3bf89a1fe17",
				// "c_name":"可乐啊", "packing":"箱", "target":"25",
				// "distribution":"25"}]}],"updatecode":"1"}
				jsonStr1 = IUtil.chkJsonStr(jsonStr1);
				JSONArray array1 = new JSONArray(jsonStr1);
				if (array1.length() > 0) {
					JSONObject obj1 = array1.getJSONObject(0);

					JSONArray array2 = obj1.getJSONArray("clientdata");
					for (int i = 0; i < array2.length(); i++) {
						JSONObject obj2 = array2.getJSONObject(i);

						String data = obj2.getString("date");
						if (uploadDate.equals(data)) {
							all_info.clear();
							JSONArray packing1 = obj2.getJSONArray("list");
							for (int j = 0; j < packing1.length(); j++) {
								JSONObject obj3 = packing1.getJSONObject(j);
								// private ArrayList<GoodsMonthCustBean>
								// all_info = new
								// ArrayList<GoodsMonthCustBean>();
								GoodsMonthCustBean tlp = new GoodsMonthCustBean();

								tlp.setId(obj3.getString("c_id"));
								tlp.setGoodsname(obj3.getString("c_name"));
								tlp.setTarget(obj3.getString("target"));
								tlp.setGoodsunit(obj3.getString("packing"));
								tlp.setAmount("");

								all_info.add(tlp);

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

}
