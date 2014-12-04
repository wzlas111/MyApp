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
import android.graphics.Matrix;
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
import android.widget.AdapterView.OnItemClickListener;
import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.adapter.CustSelectAdapter;
import com.eastelsoft.lbs.adapter.SalesReportAddAdapterA;
import com.eastelsoft.lbs.db.DBUtil;
import com.eastelsoft.lbs.db.LocationSQLiteHelper;
import com.eastelsoft.lbs.entity.CustBean;
import com.eastelsoft.lbs.entity.GoodsBean;
import com.eastelsoft.lbs.entity.GoodsReportBean;
import com.eastelsoft.lbs.entity.SetInfo;
import com.eastelsoft.lbs.location.BaiduMapAction;

import com.eastelsoft.util.CallBack;
import com.eastelsoft.util.Contant;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.FileUtil;
import com.eastelsoft.util.IUtil;
import com.eastelsoft.util.ImageThumbnail;
import com.eastelsoft.util.Util;
import com.eastelsoft.util.contact.PingYinUtil;
import com.eastelsoft.util.contact.PinyinComparatorTwo;
import com.eastelsoft.util.contact.Utility;
import com.eastelsoft.util.http.AndroidHttpClient;

/**
 * 新增销量上报
 * 
 * @author xl
 */
public class SalesReportAddActivity extends BaseActivity {
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
	private SimpleDateFormat fmtDateAndTime = new SimpleDateFormat("yyyy-MM-dd");
	private Calendar dateAndTime = Calendar.getInstance(Locale.CHINA);

	// 商品
	private ListView lv;
	private SalesReportAddAdapterA listadpter;
	private ArrayList<GoodsReportBean> all_info = new ArrayList<GoodsReportBean>();
	private ArrayList<GoodsBean> gb_al = new ArrayList<GoodsBean>();
	private TextView nodata_tv;

	// 备注
	private EditText et_remark;

	// 位置
	private Button btLocation;
	private TextView tvInfoLocationDesc;
	private LinearLayout llLoadingLocation;
	private ImageView imageViewLocationIcon;

	// 拍照
	private Button btTakePhoto;
	/* 用来标识请求照相功能的activity */
	private static final int CAMERA_WITH_DATA = 1001;
	private static final int PHOTO_DEL = 9999;
	private ImageView imageView;
	// 照相机拍照得到的图片
	private Bitmap bitMap;

	private String custid = "";
	private String uploadDate = "";

	private String c_id = "";
	private String m_count = "";

	private String remark = "";

	private String lon = "";
	private String lat = "";
	private String location = "";

	private File imgFile;
	private String imgFileName = "";

	private String submitdate = "";

	private LocationSQLiteHelper locationHelper;
	private SetInfo set;

	private Thread dataThread;
	private Thread initThread;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.salesreport_add);

		btBack = (Button) findViewById(R.id.btBack);
		btBack.setOnClickListener(new OnBtBackClickListenerImpl());

		btAddInfo = (Button) findViewById(R.id.btAddInfo);
		btAddInfo.setOnClickListener(new OnBtAddInfoClickListenerImpl());

		// 客户
		info_cust = (EditText) findViewById(R.id.info_cust);
		info_cust.setOnTouchListener(new OnEdCustTouchListenerImpl());
		info_cust.setOnClickListener(new OnEdCustClickListenerImpl());

		// 日期
		info_date = (EditText) findViewById(R.id.info_date);
		info_date.setOnTouchListener(new OnEdAreaTouchListenerImpl());
		info_date.setOnClickListener(new OnEdAreaClickListenerImpl());
		uploadDate = Util.getLocaleTime("yyyy-MM-dd");
		if (uploadDate != null) {
			info_date.setText(uploadDate);
		}

		// 商品
		lv = (ListView) findViewById(android.R.id.list);
		// lv.setOnItemClickListener(new OnItemClickListenerImpl());

		// 实例化适配器
		listadpter = new SalesReportAddAdapterA(SalesReportAddActivity.this,
				all_info);
		// 填充适配器
		lv.setAdapter(listadpter);
		Utility.setListViewHeightBasedOnChildren(lv);

		nodata_tv = (TextView) findViewById(R.id.nodata_tv);
		// 备注
		et_remark = (EditText) findViewById(R.id.info_remark);

		// 位置
		btLocation = (Button) findViewById(R.id.btLocation);
		btLocation.setOnClickListener(new OnBtLocationClickListenerImpl());
		tvInfoLocationDesc = (TextView) findViewById(R.id.infoLocationDesc);
		llLoadingLocation = (LinearLayout) findViewById(R.id.loadingLocation);
		imageViewLocationIcon = (ImageView) findViewById(R.id.infoLocationIcon);

		// 拍照
		btTakePhoto = (Button) findViewById(R.id.btTakePhoto);
		btTakePhoto.setOnClickListener(new OnBtTakePhotoClickListenerImpl());
		imageView = (ImageView) findViewById(R.id.infoImg);
		imageView.setOnClickListener(new OnBtImageViewClickListenerImpl());

		locationHelper = new LocationSQLiteHelper(this, null, null, 5);
		sp = getSharedPreferences("userdata", 0);
		set = IUtil.initSetInfo(sp);
		showDialog(PROCESS_DIALOG);
		initThread = new Thread(new InitThread());
		initThread.start();

	}

	@Override
	protected void onPause() {
		FileLog.i(TAG, "onPause:" + dataThread);
		super.onPause();
		if (dataThread != null) {
			try {
				dataThread.interrupt();
				dataThread = null;
			} catch (Exception e) {
			}
		}
		if (initThread != null) {
			try {
				initThread.interrupt();
				initThread = null;
			} catch (Exception e) {
			}
		}
	}

	@Override
	protected void onDestroy() {
		FileLog.i(TAG, "onDestroy:" + dataThread);
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
		if (initThread != null) {
			try {
				initThread.interrupt();
				initThread = null;
			} catch (Exception e) {
			}
		}
	}

	private class OnBtAddInfoClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				// 上传数据
				uploadDate = info_date.getText().toString();
				remark = et_remark.getText().toString();

				all_info = listadpter.getArrayList();

				c_id = "";
				m_count = "";
				for (int i = 0; i < all_info.size(); i++) {
					System.out.println(all_info.get(i).getAmount());
					if (all_info.get(i).getAmount() != null
							&& !"".equals(all_info.get(i).getAmount())) {
						c_id += all_info.get(i).getId() + "|";

						m_count += all_info.get(i).getAmount() + "|";

					}

				}
				if (c_id.endsWith("|"))
					c_id = c_id.substring(0, (c_id.length() - 1));
				if (m_count.endsWith("|"))
					m_count = m_count.substring(0, (m_count.length() - 1));

				submitdate = Util.getLocaleTime("yyyy-MM-dd HH:mm:ss");

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

				SalesReportAddActivity.this.openPopupWindowPG("");
				btPopGps.setText(getResources().getString(
						R.string.loading_salesreportadd));

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
				map.put("reqCode", Contant.PERSONNEL_MARKET_UPDATE_ACTION);
				map.put("gpsid", set.getDevice_id());
				map.put("pin", set.getAuth_code());
				map.put("clientid", custid);
				map.put("date", uploadDate);
				map.put("c_id", c_id);
				map.put("m_count", m_count);
				map.put("remark", remark);

				map.put("lon", lon);
				map.put("lat", lat);
				map.put("accuracy", "-100");
				map.put("submitdate", submitdate);
				// {clientid=9766781941644afcaf902e5e062136a2,
				// gpsid=6599169361245137,
				// lon=, m_count=7|66|5|5, remark=回家, pin=XotY5jnrcSitHcR6,
				// submitdate=2013-07-30 18:19:56,
				// reqCode=PersonnelMarketUploadAction,
				// c_id=13d41296465b48a2a3cb41af62986f9a|2ebe14f682f34c76adffb3bf89a1fe17|c7d44efb251b427798388bc7cc67418e|d473fc56342a4828b351ba315b3463ac,
				// date=2013-07-30, accuracy=-100, lat=}
				String jsonStr = AndroidHttpClient.getContent(url, map,
						imgFile, "file1");
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
				// msg.obj = "信息上报成功";
				handler.sendMessage(msg);

			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
				respMsg = getResources().getString(
						R.string.salesreport_upload_err);
				msg.what = 2;
				msg.obj = respMsg;
				handler.sendMessage(msg);
			}
			Looper.loop();
		}
	}

	class InitThread implements Runnable {
		@Override
		public void run() {

			Message msg = handler.obtainMessage();
			msg.what = 5;
			try {
				sp = getSharedPreferences("userdata", 0);
				SetInfo set = IUtil.initSetInfo(sp);
				// 先发消息检是否需要更新
				String url = set.getHttpip() + Contant.CLIENT_UPDATE_ACTION;
				url += "&ActionType=1";
				url += "&GpsId=" + set.getDevice_id();
				url += "&Pin=" + set.getAuth_code();
				String jsonStr = AndroidHttpClient.getContent(url);
				jsonStr = IUtil.chkJsonStr(jsonStr);
				String updatecode = "";
				JSONArray array = new JSONArray(jsonStr);
				if (array.length() > 0) {
					JSONObject obj = array.getJSONObject(0);
					updatecode = obj.getString("updatecode");
				}

				// 如果需要更新
				if (!set.getCustupdatecode().equals(updatecode)) {
					// if (true) {
					url = set.getHttpip() + Contant.ACTION;
					Map<String, String> map = new HashMap<String, String>();
					map.put("reqCode", Contant.CLIENT_UPDATE_REQCODE);
					map.put("ActionType", "2");
					map.put("GpsId", set.getDevice_id());
					map.put("Pin", set.getAuth_code());

					String jsonStr1 = AndroidHttpClient.getContent(url, map);
					FileLog.i(TAG, "jsonStr1==>" + jsonStr1);
					jsonStr1 = IUtil.chkJsonStr(jsonStr1);
					JSONArray array1 = new JSONArray(jsonStr1);
					if (array1.length() > 0) {
						JSONObject obj1 = array1.getJSONObject(0);
						updatecode = obj1.getString("updatecode");
						FileLog.i(TAG, "custupdatecode==>" + updatecode);
						if (updatecode != null && !"".equals(updatecode)) {
							// 先删除所有客户
							DBUtil.deleteLCustHasUp(locationHelper
									.getWritableDatabase());
							JSONArray array2 = obj1.getJSONArray("clientdata");
							for (int i = 0; i < array2.length(); i++) {
								JSONObject obj2 = array2.getJSONObject(i);
								String id = obj2.getString("id");
								String clientname = obj2
										.getString("clientname");
								String py = PingYinUtil.getPingYin(clientname);
								String contacts = obj2.getString("contacts");
								String phone = obj2.getString("phone");
								String email = obj2.getString("email");
								String lon = obj2.getString("lon");
								String lat = obj2.getString("lat");
								String location = obj2.getString("location");
								String address = obj2.getString("address");
								String type = obj2.getString("type");
								String c_t_id = obj2.getString("c_t_id");
								String region_id = obj2.getString("region_id");
								String c_t_name = "", region_name = "";

								String job = "";
								String Phone2 = "";
								String Phone3 = "";
								String Phone4 = "";
								try {
									c_t_name = obj2.getString("c_t_name");
								} catch (Exception e) {
									e.printStackTrace();
								}
								try {
									region_name = obj2.getString("region_name");
								} catch (Exception e) {
									e.printStackTrace();
								}
								try {
									job = obj2.getString("job");
									Phone2 = obj2.getString("Phone2");
									Phone3 = obj2.getString("Phone3");
									Phone4 = obj2.getString("Phone4");
								} catch (Exception e) {
									e.printStackTrace();
								}

								// 增加客户通讯录
								DBUtil.insertLCust(
										locationHelper.getWritableDatabase(),
										id, clientname, contacts, lon, lat,
										location, email, phone, address, type,
										py, "11", c_t_id, region_id, c_t_name,
										region_name, job, Phone2, Phone3,
										Phone4);
							}
						}
						IUtil.writeSharedPreference(sp, "custupdatecode",
								updatecode);
					}
					msg.what = 5;
				}

			} catch (Exception e) {
				FileLog.e(TAG, "getCustFromServer==>" + e.toString());
			} finally {
				handler.sendMessage(msg);
			}

		}
	}

	private void initData() {

		// 商品
		// 查询商品更新接口
		// 更新商品 或者去本地数据

		// 模拟
		// GoodsBean sm = new GoodsBean();
		// sm.setId("111123");
		// sm.setName("二十年");
		// sm.setPacking("箱");
		// for (int i = 0; i < 5; i++) {
		//
		// gb_al.add(sm);
		// }
		// {"commoditydata1":[{"id":"13d41296465b48a2a3cb41af62986f9a",
		// "name":"怡宝纯净水","packing":"箱"}
		// ,{"id":"2ebe14f682f34c76adffb3bf89a1fe17",
		// "name":"可乐啊","packing":"箱"}
		// ,{"id":"c7d44efb251b427798388bc7cc67418e",
		// "name":"咪咪虾条","packing":"袋"},
		// {"id":"d473fc56342a4828b351ba315b3463ac",
		// "name":"上好佳薯片","packing":"袋"}],"updatecode":"1"}
		// GoodsBean sm1 = new GoodsBean();
		// sm1.setId("13d41296465b48a2a3cb41af62986f9a");
		// sm1.setName("怡宝纯净水");
		// sm1.setPacking("箱");
		// gb_al.add(sm1);
		// GoodsBean sm2 = new GoodsBean();
		// sm2.setId("2ebe14f682f34c76adffb3bf89a1fe17");
		// sm2.setName("可乐啊");
		// sm2.setPacking("箱");
		// gb_al.add(sm2);
		// GoodsBean sm3 = new GoodsBean();
		// sm3.setId("c7d44efb251b427798388bc7cc67418e");
		// sm3.setName("咪咪虾条");
		// sm3.setPacking("袋");
		// gb_al.add(sm3);
		// GoodsBean sm4 = new GoodsBean();
		// sm4.setId("d473fc56342a4828b351ba315b3463ac");
		// sm4.setName("上好佳薯片");
		// sm4.setPacking("袋");
		// gb_al.add(sm4);

		// 真实
		gb_al = DBUtil.getDataFromLGoods(locationHelper.getWritableDatabase());

		// 初始化 上报商品
		all_info.clear();
		for (int i = 0; i < gb_al.size(); i++) {
			GoodsReportBean gr = new GoodsReportBean();
			gr.setId(gb_al.get(i).getId());
			gr.setName(gb_al.get(i).getName());
			gr.setAmount("");
			gr.setPacking(gb_al.get(i).getPacking());
			all_info.add(gr);
		}

		if (all_info.size() < 1) {
			nodata_tv.setVisibility(View.VISIBLE);

		} else {
			nodata_tv.setVisibility(View.GONE);

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
			cadapter = new CustSelectAdapter(SalesReportAddActivity.this,
					mData_tp);
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

	private class OnItemClickListenerImpl implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub

			// String clientName = mData.get(arg2).getClientName();
			custid = mData_tp.get(arg2).getId();
			info_cust.setText(mData_tp.get(arg2).getClientName());

			try {
				popupWindow.dismiss();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}

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

		@SuppressWarnings("deprecation")
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

	private class OnBtLocationClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				// 获取定位位置
				new BaiduMapAction(SalesReportAddActivity.this, amapCallback,
						"2").startListener();
				llLoadingLocation.setVisibility(View.VISIBLE);
				tvInfoLocationDesc.setVisibility(View.GONE);
				imageViewLocationIcon.setVisibility(View.GONE);
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
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

	class DataThread implements Runnable {
		@Override
		public void run() {
			Message msg = handler.obtainMessage();
			msg.what = 0;
			try {
				sp = getSharedPreferences("userdata", 0);
				SetInfo set = IUtil.initSetInfo(sp);
				// 先发消息检是否需要更新
				String url = set.getHttpip() + Contant.C_U_ACTION;
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
//				String  one  = set.getGoodsupdatecode();
//				String  two = updatecode;
	
				// 如果需要更新
				if (!set.getGoodsupdatecode().equals(updatecode)) {
					// if (true) {
					url = set.getHttpip() + Contant.COMMODITYLISTACTION;
					Map<String, String> map = new HashMap<String, String>();
					map.put("reqCode", Contant.CO_UPDATE_REQCODE);
					map.put("actiontype", "2");
					map.put("gpsid", set.getDevice_id());
					map.put("Pin", set.getAuth_code());

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

							DBUtil.deleteLGoods(locationHelper
									.getWritableDatabase());

							JSONArray array2 = obj1
									.getJSONArray("commoditydata");
							for (int i = 0; i < array2.length(); i++) {
								JSONObject obj2 = array2.getJSONObject(i);
								String id = obj2.getString("id");
								String name = obj2.getString("name");
								String packing = obj2.getString("packing");

								// 增加客户通讯录
								DBUtil.insertGoods(
										locationHelper.getWritableDatabase(),
										id, name, packing);
							}
						}
						IUtil.writeSharedPreference(sp, "goodsupdatecode",
								updatecode);
					}
					msg.what = 0;
				}

			} catch (Exception e) {
				FileLog.e(TAG, "getCustFromServer==>" + e.toString());
			} finally {
				handler.sendMessage(msg);
			}
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try {

				switch (msg.what) {
				case 0:
					mData = DBUtil.getDataFromLCustMgHasUp(locationHelper
							.getWritableDatabase());
					Collections.sort(mData, new PinyinComparatorTwo());
					initData(); // 初始化客户数

					listadpter.notifyDataSetChanged();
					Utility.setListViewHeightBasedOnChildren(lv);
					dismissDialog(PROCESS_DIALOG);

					break;
				case 2:
					try {
						popupWindowPg.dismiss();
					} catch (Exception e) {
						FileLog.e(TAG, e.toString());
					}
					/* dialog(BaifangAddActivity.this, msg.obj.toString()); */
					openPopupWindowAx("上传失败，是否存入本地");

					break;

				case 3:
					// 假设成功
					try {
						popupWindowPg.dismiss();
					} catch (Exception e) {
						FileLog.e(TAG, e.toString());
					}
					if ("1".equals(msg.obj.toString())) {

						try {
							// 存入数据库

							String ss = UUID.randomUUID().toString();
							DBUtil.insertSalesReport(locationHelper
									.getWritableDatabase(), UUID.randomUUID()
									.toString(), custid, info_cust.getText()
									.toString(), uploadDate, ss, imgFileName,
									remark, lon, lat, location, "11",
									submitdate);
							for (int i = 0; i < all_info.size(); i++) {

								DBUtil.insertSalesReportGoods(locationHelper
										.getWritableDatabase(), UUID
										.randomUUID().toString(), ss, all_info
										.get(i).getName(), all_info.get(i)
										.getAmount(), all_info.get(i)
										.getPacking(), all_info.get(i).getId());

							}
							Toast.makeText(
									SalesReportAddActivity.this,
									getResources().getString(
											R.string.salesreport_upload_succ),
									Toast.LENGTH_SHORT).show();
							SalesReportAddActivity.this.finish();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					} else {

						openPopupWindowAx("上传失败，是否存入本地");

					}

					break;

				case 5:
					dataThread = new Thread(new DataThread());
					dataThread.start();
					break;

				case 11:
					tvInfoLocationDesc.setText("获取不到定位信息");

					tvInfoLocationDesc.setVisibility(View.VISIBLE);
					imageViewLocationIcon.setVisibility(View.VISIBLE);
					llLoadingLocation.setVisibility(View.GONE);
					break;
				case 10:
					String locationDesc = msg.obj.toString();
					location = locationDesc;
					tvInfoLocationDesc.setText(locationDesc);
					tvInfoLocationDesc.setVisibility(View.VISIBLE);
					llLoadingLocation.setVisibility(View.GONE);
					imageViewLocationIcon.setVisibility(View.VISIBLE);

				case 9:

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
						tvInfoLocationDesc.setVisibility(View.VISIBLE);
						imageViewLocationIcon.setVisibility(View.VISIBLE);
						llLoadingLocation.setVisibility(View.GONE);
					}
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
				DBUtil.insertSalesReport(locationHelper.getWritableDatabase(),
						UUID.randomUUID().toString(), custid, info_cust
								.getText().toString(), uploadDate, ss,
						imgFileName, remark, lon, lat, location, "00",
						submitdate);
				for (int i = 0; i < all_info.size(); i++) {

					DBUtil.insertSalesReportGoods(locationHelper
							.getWritableDatabase(), UUID.randomUUID()
							.toString(), ss, all_info.get(i).getName(),
							all_info.get(i).getAmount(), all_info.get(i)
									.getPacking(), all_info.get(i).getId());

				}

				Toast.makeText(SalesReportAddActivity.this, "存入本地成功",
						Toast.LENGTH_SHORT).show();
				SalesReportAddActivity.this.finish();

			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
				Toast.makeText(SalesReportAddActivity.this, "存入本地失败",
						Toast.LENGTH_SHORT).show();

			}

		}
	}

	private void displayLocation(Message msg, Location location) {
		try {
			SalesReportAddActivity.this.lon = Util.format(
					location.getLongitude(), "#.######");
			SalesReportAddActivity.this.lat = Util.format(
					location.getLatitude(), "#.######");
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

	private class OnBtImageViewClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				Intent intent = new Intent(SalesReportAddActivity.this,
						PhotoViewActivity.class);
				intent.putExtra("imgFileName", imgFileName);
				SalesReportAddActivity.this.startActivityForResult(intent,
						PHOTO_DEL);
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

	private void doPickPhotoAction() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
			doTakePhoto();// 用户点击了从照相机获取
		} else {
			Toast.makeText(SalesReportAddActivity.this,
					getString(R.string.noSDCard), Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 拍照获取图片
	 * 
	 */
	protected void doTakePhoto() {
		try {
			Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			imgFileName = Util.getLocaleTime("yyyyMMddHHmmss") + ".jpg";
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

	// 因为调用了Camera和Gally所以要判断他们各自的返回情况,他们启动时是这样的startActivityForResult
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK) {
			FileLog.i(TAG, "xl");
			// this.imgFile = null;
			// this.imgFileName = "";
			// imageView.setVisibility(View.GONE);

			return;
		}
		switch (requestCode) {
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

	private class OnBtBackClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				SalesReportAddActivity.this.finish();
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
				new DatePickerDialog(SalesReportAddActivity.this, d,
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
			info_date.setText(fmtDateAndTime.format(dateAndTime.getTime()));

		}

	};

}
