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
import com.eastelsoft.lbs.entity.GoodsMonthCustBean;
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
public class SalesReportUpActivity extends BaseActivity {
	public static final String TAG = "SalesReportUpActivity";
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
	private String info_auto_id = "";

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
		listadpter = new SalesReportAddAdapterA(SalesReportUpActivity.this,
				all_info);
		// 填充适配器
		lv.setAdapter(listadpter);
		Utility.setListViewHeightBasedOnChildren(lv);

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

		// locationHelper = new LocationSQLiteHelper(this, null, null, 5);
		// sp = getSharedPreferences("userdata", 0);
		// set = IUtil.initSetInfo(sp);
		// showDialog(PROCESS_DIALOG);
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

	private void initdataback() {
		// TODO Auto-generated method stub

		// "id,clientid,clientName,date,goods_id,imgFile,remark,lon,lat,location,istijiao"

		if (localMap != null) {

			if (localMap.containsKey("clientid")) {
				if (localMap.get("clientid") != null) {

					custid = localMap.get("clientid").toString();

				}
			}

			if (localMap.containsKey("submitdate")) {
				if (localMap.get("submitdate") != null) {

					submitdate = localMap.get("submitdate").toString();

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
						tvInfoLocationDesc.setText(location);
						tvInfoLocationDesc.setVisibility(View.VISIBLE);
						imageViewLocationIcon.setVisibility(View.VISIBLE);
						llLoadingLocation.setVisibility(View.GONE);

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
							imageView.setVisibility(View.VISIBLE);

						} catch (FileNotFoundException e) {
							FileLog.d(TAG, e.getMessage());

						}
					}
				}
			}
		}

	}

	private Bitmap bm;
	private String clientName = "";
	HashMap<String, Object> localMap;
	private String goods_id = "";

	class InitDataThread implements Runnable {
		@Override
		public void run() {
			Message msg = handler.obtainMessage();
			msg.what = 5;
			try {
				mData = DBUtil.getDataFromLCustMgHasUp(locationHelper
						.getWritableDatabase());
				Collections.sort(mData, new PinyinComparatorTwo());

				localMap = DBUtil.getDataFromSalesReportByID(
						locationHelper.getWritableDatabase(), info_auto_id);

				if (localMap != null) {

					if (localMap.containsKey("goods_id")) {
						if (localMap.get("goods_id") != null) {
							goods_id = localMap.get("goods_id").toString();
							if (goods_id != null) {
								all_info.clear();
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

				// submitdate = Util.getLocaleTime("yyyy-MM-dd HH:mm:ss");
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
				Intent it = new Intent(Contant.SALESREPORTCHANGE_ACTION);
				SalesReportUpActivity.this.sendBroadcast(it);
				
				SalesReportUpActivity.this.openPopupWindowPG("");
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

	@Override
	protected void onDestroy() {
		super.onDestroy();

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
			cadapter = new CustSelectAdapter(SalesReportUpActivity.this,
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
			custid = mData.get(arg2).getId();
			info_cust.setText(mData.get(arg2).getClientName());

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
				new BaiduMapAction(SalesReportUpActivity.this, amapCallback,
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

							// 删除原始数据
							DBUtil.deleteSalesReportByID(
									locationHelper.getWritableDatabase(),
									info_auto_id);

							DBUtil.deleteSalesReportGoodsByGoodsId(
									locationHelper.getWritableDatabase(),
									goods_id);

							Toast.makeText(
									SalesReportUpActivity.this,
									getResources().getString(
											R.string.salesreport_upload_succ),
									Toast.LENGTH_SHORT).show();
							SalesReportUpActivity.this.finish();
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
					listadpter = new SalesReportAddAdapterA(
							SalesReportUpActivity.this, all_info);
					// 填充适配器
					lv.setAdapter(listadpter);
					Utility.setListViewHeightBasedOnChildren(lv);

					initdataback();

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
				// 删除原始数据
				DBUtil.deleteSalesReportByID(
						locationHelper.getWritableDatabase(), info_auto_id);

				DBUtil.deleteSalesReportGoodsByGoodsId(
						locationHelper.getWritableDatabase(), goods_id);

				Toast.makeText(SalesReportUpActivity.this, "存入本地成功",
						Toast.LENGTH_SHORT).show();
				SalesReportUpActivity.this.finish();

			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
				Toast.makeText(SalesReportUpActivity.this, "存入本地失败",
						Toast.LENGTH_SHORT).show();

			}

		}
	}

	private void displayLocation(Message msg, Location location) {
		try {
			SalesReportUpActivity.this.lon = Util.format(
					location.getLongitude(), "#.######");
			SalesReportUpActivity.this.lat = Util.format(
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
				Intent intent = new Intent(SalesReportUpActivity.this,
						PhotoViewActivity.class);
				intent.putExtra("imgFileName", imgFileName);
				SalesReportUpActivity.this.startActivityForResult(intent,
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
			Toast.makeText(SalesReportUpActivity.this,
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
				this.imgFile = FileUtil.saveBitmapToFile(photoViewBitmap,
						imgFileName);

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
				SalesReportUpActivity.this.finish();
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
				new DatePickerDialog(SalesReportUpActivity.this, d,
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
