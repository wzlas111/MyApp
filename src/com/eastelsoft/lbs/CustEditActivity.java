package com.eastelsoft.lbs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.adapter.CustAreaAdapter;
import com.eastelsoft.lbs.adapter.CustTypeAdapter;
import com.eastelsoft.lbs.db.DBUtil;
import com.eastelsoft.lbs.db.LocationSQLiteHelper;
import com.eastelsoft.lbs.entity.CustProp;
import com.eastelsoft.lbs.entity.SetInfo;
import com.eastelsoft.lbs.location.AMapAction;
import com.eastelsoft.lbs.location.BaiduMapAction;
import com.eastelsoft.lbs.location.BaseStationAction;
import com.eastelsoft.lbs.location.BaseStationAction.SItude;
import com.eastelsoft.util.CallBack;
import com.eastelsoft.util.Contant;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.IUtil;
import com.eastelsoft.util.Util;
import com.eastelsoft.util.contact.PingYinUtil;
import com.eastelsoft.util.http.AndroidHttpClient;
import com.mapabc.mapapi.geocoder.Geocoder;

public class CustEditActivity extends BaseActivity {
	public static final String TAG = "CustEditActivity";

	private Button btClosex;
	private Button btCloseok;
	private Button btCloseno;

	private Button btBack;
	private TextView tvTitle;
	private Button btLocation;

	// private Button btAddInfo;
	private Button btSaveCust;

	// private Spinner spType;
	private EditText etArea;
	private EditText etType;

	private EditText cust_name;
	private EditText cust_contacter;
	private EditText cust_job;
	private EditText cust_telephone2;
	private EditText cust_telephone3;
	private EditText cust_telephone4;

	private EditText cust_telephone;
	private EditText cust_youxiang;
	private EditText cust_addressoo;

	private ImageView imageViewLocationIcon;
	private TextView tvInfoLocationDesc;
	private LinearLayout llLoadingLocation;
	private RadioGroup genderGroup = null;
	private RadioButton gogn = null;
	private RadioButton siyou = null;

	private LocationSQLiteHelper locationHelper;

	private String lon = "";
	private String lat = "";
	private String cname;
	private String cnamepy = "";
	private String ccontacter;
	private String ctelephone;

	private String cjob;
	private String ctelephone2;
	private String ctelephone3;
	private String ctelephone4;

	private String cyouxiang = "";
	private String caddress = "";
	private String cid;
	private String locationDesc = "";
	private String type = "3";
	private String istijiao;
	private Geocoder coder;
	// intent 所带参数
	private String myid;

	HashMap<String, Object> localMap;

	ArrayAdapter<CustProp> typeAdapter;

	CustAreaAdapter adapter;
	CustTypeAdapter adapter1;
	List<CustProp> list;
	List<CustProp> list1;
	AlertDialog.Builder builder;
	AlertDialog.Builder builder1;
	AlertDialog alertDialog;
	AlertDialog alertDialog1;
	LayoutInflater inflater;
	LayoutInflater inflater1;
	View layout;
	View layout1;
	ListView myListView;
	ListView typeListView;

	private String areaStr = ""; // 显示项目
	private String area_id = ""; // 待提交的最后结果
	// private String c_t_id = "";
	// private String c_t_name = "";

	private String typeStr = "";
	private String type_id = "";

	private List<CustProp> types = new ArrayList<CustProp>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_custadd);
		btBack = (Button) findViewById(R.id.btBack);
		btBack.setOnClickListener(new OnBtBackClickListenerImpl());
		tvTitle = (TextView) findViewById(R.id.tv_title);
		tvTitle.setText("编辑客户");
		genderGroup = (RadioGroup) findViewById(R.id.genderGroup);
		gogn = (RadioButton) findViewById(R.id.gogn);
		siyou = (RadioButton) findViewById(R.id.siyou);
		type = "3";
		genderGroup
				.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
					public void onCheckedChanged(RadioGroup group, int checkedId) {

						if (gogn.getId() == checkedId) {
							type = "1";
							FileLog.i(TAG, type);

						} else if (siyou.getId() == checkedId) {
							type = "3";
							FileLog.i(TAG, type);

						}
					}
				});

		btLocation = (Button) findViewById(R.id.btcustLocation);
		btLocation.setOnClickListener(new OnBtLocationClickListenerImpl());

		// btAddInfo = (Button) findViewById(R.id.btAddCust);
		// btAddInfo.setOnClickListener(new OnBtAddInfoClickListenerImpl());
		btSaveCust = (Button) findViewById(R.id.btSaveCust);
		btSaveCust.setOnClickListener(new OnBtAddInfoClickListenerImpl());

		cust_name = (EditText) findViewById(R.id.cust_name);
		cust_contacter = (EditText) findViewById(R.id.cust_contacter);
		cust_job = (EditText) findViewById(R.id.cust_job);
		cust_telephone2 = (EditText) findViewById(R.id.cust_telephone2);
		cust_telephone2.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		cust_telephone3 = (EditText) findViewById(R.id.cust_telephone3);
		cust_telephone3.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		cust_telephone4 = (EditText) findViewById(R.id.cust_telephone4);
		cust_telephone4.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		cust_telephone = (EditText) findViewById(R.id.cust_telephone);
		// 设置数字键盘
		cust_telephone.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		cust_youxiang = (EditText) findViewById(R.id.cust_youxiang);
		cust_addressoo = (EditText) findViewById(R.id.cust_addressoo);

		tvInfoLocationDesc = (TextView) findViewById(R.id.custLocationDesc);
		llLoadingLocation = (LinearLayout) findViewById(R.id.loadingLocation);
		imageViewLocationIcon = (ImageView) findViewById(R.id.custLocationIcon);

		locationHelper = new LocationSQLiteHelper(this, null, null, 5);
		coder = new Geocoder(this);

		// spType = (Spinner) findViewById(R.id.cust_type);
		etArea = (EditText) findViewById(R.id.cust_area);
		etArea.setOnTouchListener(new OnEdAreaTouchListenerImpl());
		etArea.setOnClickListener(new OnEdAreaClickListenerImpl());

		etType = (EditText) findViewById(R.id.cust_type);
		etType.setOnTouchListener(new OnEdTypeTouchListenerImpl());
		etType.setOnClickListener(new OnEdTypeClickListenerImpl());

		this.openPopupWindowPG("");
		btPopGps.setText(getResources().getString(R.string.data_reading));
		Thread typeThread = new Thread(new CustPropThread());
		typeThread.start();

		Intent intent = getIntent();
		myid = intent.getStringExtra("myid");

		localMap = DBUtil.getDataFromLCustByClientId(
				locationHelper.getWritableDatabase(), myid);

		if (localMap != null) {
			if (localMap.containsKey("clientName")) {
				if (localMap.get("clientName") != null) {
					cname = localMap.get("clientName").toString();
					cust_name.setText(cname);
				}
				if (localMap.get("contacts") != null) {
					ccontacter = localMap.get("contacts").toString();
					cust_contacter.setText(ccontacter);
				}
				/*
				 * if(localMap.get("location") != null) { this.locationDesc =
				 * localMap.get("location").toString();
				 * cust_addressoo.setText(locationDesc);
				 * 
				 * }
				 */

				if (localMap.get("job") != null) {
					cjob = localMap.get("job").toString();
					cust_job.setText(cjob);
				}

				if (localMap.get("Phone2") != null) {
					ctelephone2 = localMap.get("Phone2").toString();
					cust_telephone2.setText(ctelephone2);
				}
				if (localMap.get("Phone3") != null) {
					ctelephone3 = localMap.get("Phone3").toString();
					cust_telephone3.setText(ctelephone3);
				}
				if (localMap.get("Phone4") != null) {
					ctelephone4 = localMap.get("Phone4").toString();
					cust_telephone4.setText(ctelephone4);
				}
				if (localMap.get("email") != null) {
					cyouxiang = localMap.get("email").toString();
					cust_youxiang.setText(cyouxiang);
				}
				if (localMap.get("phone") != null) {
					this.ctelephone = localMap.get("phone").toString();
					cust_telephone.setText(ctelephone);

				}
				if (localMap.get("lon") != null) {
					this.lon = localMap.get("lon").toString();
				}
				if (localMap.get("lat") != null) {
					this.lat = localMap.get("lat").toString();
				}
				if (localMap.get("address") != null) {
					this.caddress = localMap.get("address").toString();
					cust_addressoo.setText(caddress);

				}

				if (localMap.get("istijiao") != null) {
					this.istijiao = localMap.get("istijiao").toString();
				} else {
					this.istijiao = "11";
				}
				if (localMap.get("type") != null) {
					this.type = localMap.get("type").toString();
					if ("3".equals(type)) {
						siyou.setChecked(true);
					} else {
						gogn.setChecked(true);
					}

				}

				if (localMap.get("c_t_id") != null) {
					this.type_id = localMap.get("c_t_id").toString();
				}
				if (localMap.get("region_id") != null) {
					this.area_id = localMap.get("region_id").toString();
				}
				if (localMap.get("c_t_name") != null) {
					this.typeStr = localMap.get("c_t_name").toString();
					etType.setText(typeStr);
				}
				if (localMap.get("region_name") != null) {
					etArea.setText(localMap.get("region_name").toString());
				}
			}
		}

	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

		if (locationHelper != null) {
			locationHelper.getWritableDatabase().close();
		}
	}

	private class OnBtAddInfoClickListenerImpl implements OnClickListener {
		public void onClick(View v) {

			try {
				// 上传数据

				cname = cust_name.getText().toString();
				FileLog.i(TAG, cname);
				cnamepy = PingYinUtil.getPingYin(cname);
				ccontacter = cust_contacter.getText().toString();
				cjob = cust_job.getText().toString();
				ctelephone2 = cust_telephone2.getText().toString();
				ctelephone3 = cust_telephone3.getText().toString();
				ctelephone4 = cust_telephone4.getText().toString();
				ctelephone = cust_telephone.getText().toString();
				cyouxiang = cust_youxiang.getText().toString();
				caddress = cust_addressoo.getText().toString();

				if ("".equals(cname.trim())) {
					respMsg = "请输入客户名称";
					Toast.makeText(getApplicationContext(), respMsg,
							Toast.LENGTH_SHORT).show();
					return;
				}
				// if ("".equals(ccontacter.trim())) {
				// respMsg = "请输入联系人";
				// Toast.makeText(getApplicationContext(), respMsg,
				// Toast.LENGTH_SHORT).show();
				// return;
				// }
				// if ("".equals(ctelephone.trim())) {
				// respMsg = "请输入电话";
				// Toast.makeText(getApplicationContext(), respMsg,
				// Toast.LENGTH_SHORT).show();
				// return;
				// }
				if (!"".equals(ctelephone.trim())) {

					String expression = "[-0-9]*";
					Pattern pattern = Pattern.compile(expression);
					/* 将Pattern 以参数传入Matcher作Regular expression */
					Matcher matcher = pattern.matcher(ctelephone.trim());
					if (!matcher.matches()) {
						respMsg = "电话格式不对，请重新输入";
						Toast.makeText(getApplicationContext(), respMsg,
								Toast.LENGTH_SHORT).show();
						return;
					}
				}
				if (!"".equals(ctelephone2.trim())) {

					String expression = "[-0-9]*";
					Pattern pattern = Pattern.compile(expression);
					/* 将Pattern 以参数传入Matcher作Regular expression */
					Matcher matcher = pattern.matcher(ctelephone2.trim());
					if (!matcher.matches()) {
						respMsg = "电话2格式不对，请重新输入";
						Toast.makeText(getApplicationContext(), respMsg,
								Toast.LENGTH_SHORT).show();
						return;
					}
				}
				if (!"".equals(ctelephone3.trim())) {

					String expression = "[-0-9]*";
					Pattern pattern = Pattern.compile(expression);
					/* 将Pattern 以参数传入Matcher作Regular expression */
					Matcher matcher = pattern.matcher(ctelephone3.trim());
					if (!matcher.matches()) {
						respMsg = "电话3格式不对，请重新输入";
						Toast.makeText(getApplicationContext(), respMsg,
								Toast.LENGTH_SHORT).show();
						return;
					}
				}
				if (!"".equals(ctelephone4.trim())) {

					String expression = "[-0-9]*";
					Pattern pattern = Pattern.compile(expression);
					/* 将Pattern 以参数传入Matcher作Regular expression */
					Matcher matcher = pattern.matcher(ctelephone4.trim());
					if (!matcher.matches()) {
						respMsg = "电话4格式不对，请重新输入";
						Toast.makeText(getApplicationContext(), respMsg,
								Toast.LENGTH_SHORT).show();
						return;
					}
				}

			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}

			openPopupWindowAx("是否同步客户信息到平台");

		}
	}

	class AddCustThread implements Runnable {
		@Override
		public void run() {
			Looper.prepare();
			Message msg = handler.obtainMessage();
			try {
				sp = getSharedPreferences("userdata", 0);
				SetInfo set = IUtil.initSetInfo(sp);

				String url = set.getHttpip() + Contant.ACTION;
				Map<String, String> map = new HashMap<String, String>();
				map.put("reqCode", Contant.CLINET_UPLOAD_ACTION);
				map.put("gpsid", set.getDevice_id());
				map.put("pin", set.getAuth_code());
				map.put("clientname", cname);
				map.put("contacts", ccontacter);
				//新增字段
				map.put("job", cjob);
				map.put("Phone2", ctelephone2);
				map.put("Phone3", ctelephone3);
				map.put("Phone4", ctelephone4);
				map.put("phone", ctelephone);
				map.put("email", cyouxiang);
				map.put("address", caddress);
				map.put("type", type);
				map.put("lon", lon);
				map.put("lat", lat);
				map.put("accuracy", "-100");
				// CustProp custType = (CustProp)spType.getSelectedItem();
				map.put("c_t_id", type_id);
				map.put("region_id", area_id);
				String jsonStr = AndroidHttpClient.getContent(url, map);
				FileLog.i(TAG, jsonStr);
				jsonStr = IUtil.chkJsonStr(jsonStr);
				JSONArray array = new JSONArray(jsonStr);
				String resultcode = "";
				if (array.length() > 0) {
					JSONObject obj = array.getJSONObject(0);
					resultcode = obj.getString("ResultCode");
					if ("1".equals(resultcode)) {
						cid = obj.getString("Id");
					}

					FileLog.i(TAG, "resultcode==>" + resultcode);
					FileLog.i(TAG, "cid==>" + cid);
				}
				msg.what = 0;
				msg.obj = resultcode;
				// msg.obj = "信息上报成功";
				handler.sendMessage(msg);
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
				respMsg = getResources().getString(R.string.cust_upload_err);
				msg.what = 1;
				msg.obj = respMsg;
				handler.sendMessage(msg);
			}
			Looper.loop();
		}
	}

	private class OnBtBackClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				CustEditActivity.this.finish();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	private class OnBtLocationClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				// 获取定位位置
				new BaiduMapAction(CustEditActivity.this, amapCallback, "2")
						.startListener();
				llLoadingLocation.setVisibility(View.VISIBLE);
				tvInfoLocationDesc.setVisibility(View.GONE);
				imageViewLocationIcon.setVisibility(View.GONE);
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	private CallBack gpsCallback = new CallBack() {
		public void execute(Object[] paramArrayOfObject) {
			Message msg = handler.obtainMessage();
			msg.what = 9;
			msg.obj = paramArrayOfObject;
			handler.sendMessage(msg);
		}
	};

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
			btCloseno.setOnClickListener(new OnBtCloseLnoClickListenerImpl());
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
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
			try {
				// 上传数据

				CustEditActivity.this.openPopupWindowPG("");
				btPopGps.setText(getResources().getString(
						R.string.loading_custadd));
				Thread addInfoThread = new Thread(new AddCustThread());
				addInfoThread.start();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	protected class OnBtCloseLnoClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				popupWindow.dismiss();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}

			try {
				/*DBUtil.insertLCust(locationHelper.getWritableDatabase(), UUID
						.randomUUID().toString(), cname, ccontacter, lon, lat,
						locationDesc, cyouxiang, ctelephone, caddress, type,
						cnamepy, "00", type_id, area_id, typeStr, areaStr, cjob, ctelephone2, ctelephone3, ctelephone4);*/
				
				DBUtil.updateCustAll(locationHelper.getWritableDatabase(),
						cname, ccontacter, lon, lat, locationDesc, cyouxiang,
						ctelephone, caddress, type, cnamepy, "00",
						type_id, area_id, typeStr, etArea.getText().toString(),
						cjob, ctelephone2, ctelephone3, ctelephone4, myid);
				Toast.makeText(CustEditActivity.this, "保存本地成功",
						Toast.LENGTH_SHORT).show();
				CustEditActivity.this.finish();

			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Toast.makeText(CustEditActivity.this, "保存本地失败",
						Toast.LENGTH_SHORT).show();

			}
		}
	}

	// ScrollView用dispatchTouchEvent
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		/*
		 * cust_name = (EditText) findViewById(R.id.cust_name); cust_contacter =
		 * (EditText) findViewById(R.id.cust_contacter); cust_telephone =
		 * (EditText) findViewById(R.id.cust_telephone); cust_youxiang =
		 * (EditText) findViewById(R.id.cust_youxiang); cust_addressoo =
		 * (EditText) findViewById(R.id.cust_addressoo);
		 */
		Rect localRect1 = new Rect();
		Rect localRect2 = new Rect();
		Rect localRect3 = new Rect();
		Rect localRect4 = new Rect();
		Rect localRect5 = new Rect();

		((EditText) findViewById(R.id.cust_name))
				.getGlobalVisibleRect(localRect1);
		((EditText) findViewById(R.id.cust_contacter))
				.getGlobalVisibleRect(localRect2);
		((EditText) findViewById(R.id.cust_telephone))
				.getGlobalVisibleRect(localRect3);
		((EditText) findViewById(R.id.cust_youxiang))
				.getGlobalVisibleRect(localRect4);
		((EditText) findViewById(R.id.cust_addressoo))
				.getGlobalVisibleRect(localRect5);
		Rect localRect6 = new Rect((int) event.getX(), (int) event.getY(),
				(int) event.getX(), (int) event.getY());
		if ((!localRect1.intersect(localRect6))
				&& (!localRect2.intersect(localRect6))
				&& (!localRect3.intersect(localRect6))
				&& (!localRect4.intersect(localRect6))
				&& (!localRect5.intersect(localRect6)))
			((InputMethodManager) getSystemService("input_method"))
					.hideSoftInputFromWindow(getWindow().peekDecorView()
							.getWindowToken(), 0);
		return super.dispatchTouchEvent(event);
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try {
				if (msg.what < 9) {
					// pDialog.cancel();
					try {
						popupWindowPg.dismiss();
					} catch (Exception e) {
						FileLog.e(TAG, e.toString());
					}
				}
				switch (msg.what) {
				case 2:
					if (popupWindowPg.isShowing())
						popupWindowPg.dismiss();
					// typeAdapter = new ArrayAdapter<CustProp>(
					// CustEditActivity.this.getApplicationContext(),
					// R.layout.spinner_dropdown_item, types);

					// spType.setAdapter(typeAdapter);

					// CustProp cp= new CustProp();
					// cp.setId(c_t_id);
					// cp.setName(c_t_name);
					// int loc = -1;
					// for(int i = 0; i < types.size(); i++) {
					// if(c_t_name.equals(types.get(i).getName())) {
					// loc = i;
					// }
					// }
					// int loc = types.indexOf(cp);
					// CustEditActivity.this.spType.setSelection(loc, true);
					// typeAdapter.notifyDataSetChanged();
					break;
				case 0:
					if ("1".equals(msg.obj.toString())) {
						// 任务上报成功
						// dialog(InfoAddActivity.this,
						// getResources().getString(
						// R.string.info_upload_succ));
						// 返回成功数据写库
						// CustProp custType =
						// (CustProp)spType.getSelectedItem();
						// 增加客户通讯录
						DBUtil.insertLCust(
								locationHelper.getWritableDatabase(), cid,
								cname, ccontacter, lon, lat, locationDesc,
								cyouxiang, ctelephone, caddress, type, cnamepy,
								"11", type_id, area_id, typeStr, etArea
										.getText().toString(), cjob, ctelephone2, ctelephone3, ctelephone4);
						// delete保存的cust
						DBUtil.deleteLCustbyId(
								locationHelper.getWritableDatabase(), myid);

						sp = getSharedPreferences("userdata", 0);
						SetInfo set = IUtil.initSetInfo(sp);

						String tp = set.getCustupdatecode();
						FileLog.i(TAG, tp);
						int a = Integer.parseInt(tp);
						a++;
						String re_tp = String.valueOf(a);
						IUtil.writeSharedPreference(sp, "custupdatecode", re_tp);
						FileLog.i(TAG, re_tp);

						Toast.makeText(
								CustEditActivity.this,
								getResources().getString(
										R.string.cust_upload_succ),
								Toast.LENGTH_SHORT).show();
						CustEditActivity.this.finish();
					} else if ("2".equals(msg.obj.toString())) {
						dialog(CustEditActivity.this, "客户名称与平台上重复，请更换客户名称!");

					} else {
						dialog(CustEditActivity.this,
								getResources().getString(
										R.string.cust_upload_err));
					}
					break;
				case 1:
					dialog(CustEditActivity.this, msg.obj.toString());
					break;
				case 11:
					tvInfoLocationDesc.setText("获取不到地址信息，请手动输入地址");
					tvInfoLocationDesc.setVisibility(View.VISIBLE);
					imageViewLocationIcon.setVisibility(View.VISIBLE);
					llLoadingLocation.setVisibility(View.GONE);
					break;
				case 10:
					locationDesc = msg.obj.toString();
					tvInfoLocationDesc.setText(locationDesc);
					tvInfoLocationDesc.setVisibility(View.GONE);
					llLoadingLocation.setVisibility(View.GONE);
					imageViewLocationIcon.setVisibility(View.GONE);
					cust_addressoo.setText(locationDesc);

				case 9:
					Location location = null;
					Object[] obj = (Object[]) msg.obj;
					if (obj[0] != null) {
						location = (Location) obj[0];
					}
					if (location != null) {
						displayLocation(msg, location);
					} else {
						new AMapAction(CustEditActivity.this, amapCallback, "")
								.startListener();
					}
					break;
				case 99:
					Location location1 = null;
					Object[] obj1 = (Object[]) msg.obj;
					if (obj1[0] != null) {
						location1 = (Location) obj1[0];
					}
					if (location1 != null) {
						displayLocation(msg, location1);
					} else {
						tvInfoLocationDesc.setText("获取不到地址信息，请手动输入地址");
						tvInfoLocationDesc.setVisibility(View.VISIBLE);
						imageViewLocationIcon.setVisibility(View.VISIBLE);
						llLoadingLocation.setVisibility(View.GONE);
					}
					break;
				}
			} catch (Exception e) {
				// 异常中断
			}
		}
	};

	private CallBack amapCallback = new CallBack() {
		public void execute(Object[] paramArrayOfObject) {
			Message msg = handler.obtainMessage();
			msg.what = 99;
			msg.obj = paramArrayOfObject;
			handler.sendMessage(msg);
		}
	};

	private void displayLocation(Message msg, Location location) {
		try {
			CustEditActivity.this.lon = Util.format(location.getLongitude(),
					"#.######");
			CustEditActivity.this.lat = Util.format(location.getLatitude(),
					"#.######");

			/*
			 * String locationDesc = "位置坐标经度："+location.getLongitude()+ ",纬度：" +
			 * location.getLatitude();
			 */
			/* String locationDesc =getAddress(lat,lon); */
			String locationDesc = location.getExtras().getString("desc");
			/* locationDesc=""; */
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

	private void displayLocation(Message msg, SItude itude) {
		try {
			CustEditActivity.this.lon = Util.format(
					Double.parseDouble(itude.longitude), "#.######");
			CustEditActivity.this.lat = Util.format(
					Double.parseDouble(itude.latitude), "#.######");
			this.locationDesc = new BaseStationAction(CustEditActivity.this)
					.getLocation(itude);
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

	class LocationThread implements Runnable {
		private Location location;

		public LocationThread(Location location) {
			this.location = location;
		}

		@Override
		public void run() {
			Looper.prepare();
			Message msg = handler.obtainMessage();
			try {
				if (location != null) {
					SItude itude = new BaseStationAction(CustEditActivity.this)
							.location1(String.valueOf(location.getLongitude()),
									String.valueOf(location.getLatitude()));
					displayLocation(msg, itude);
				} else {
					SItude itude = new BaseStationAction(CustEditActivity.this)
							.location1();
					if (itude != null) {
						displayLocation(msg, itude);
					} else {
						// tvInfoLocationDesc.setText("获取不到定位信息");
						// tvInfoLocationDesc.setVisibility(View.VISIBLE);
						// imageViewLocationIcon.setVisibility(View.VISIBLE);
						msg.what = 11;
						handler.sendMessage(msg);
					}
				}
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
				msg.what = 11;
				handler.sendMessage(msg);
			}
			Looper.loop();
		}
	}

	private class OnEdTypeTouchListenerImpl implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (v.getId()) {
			case R.id.cust_type:
				// actionAlertDialog();
				etType.requestFocus();
				break;
			default:
				break;
			}
			return false;
		}

	}

	private class OnEdTypeClickListenerImpl implements OnClickListener {
		public void onClick(View v) {

			try {
				// showToast("test");
				actionAlertDialogType();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	protected void actionAlertDialogType() {
		typeStr = "";
		type_id = "";
		list1 = initTypeData();

		inflater1 = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		// layout1 = inflater1.inflate(R.layout.pop_list, null, true);
		ViewGroup menuView = (ViewGroup) inflater1.inflate(
				R.layout.pop_list_type, null, true);
		typeListView = (ListView) menuView.findViewById(R.id.lv_cust_type);
		adapter1 = new CustTypeAdapter(this, list1);
		typeListView.setAdapter(adapter1);

		typeListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				typeStr += list1.get(position).getName() + " ";
				type_id = list1.get(position).getId();

				popupWindow.dismiss();
				etType.setText(typeStr);
			}
		});

		popupWindow = new PopupWindow(menuView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, true); // 背景
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setAnimationStyle(R.style.PopupAnimation);
		popupWindow.showAtLocation(findViewById(R.id.parent), Gravity.CENTER
				| Gravity.CENTER, 0, 0);
		popupWindow.update();
	}

	protected List<CustProp> initTypeData() {
		List<CustProp> list = DBUtil.getDataFromLCustType(locationHelper
				.getWritableDatabase());
		return list;
	}

	private class OnEdAreaTouchListenerImpl implements OnTouchListener {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			switch (v.getId()) {
			case R.id.cust_area:
				// actionAlertDialog();
				etArea.requestFocus();
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
				// showToast("test");
				actionAlertDialog();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}
	
	protected void actionAlertDialog() {
		areaStr = "";
		area_id = "";
		list = initData();

		inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		// layout1 = inflater1.inflate(R.layout.pop_list, null, true);
		ViewGroup menuView = (ViewGroup) inflater.inflate(
				R.layout.pop_list_area, null, true);

		myListView = (ListView) menuView.findViewById(R.id.lv_cust_area);
		adapter = new CustAreaAdapter(this, list);
		myListView.setAdapter(adapter);

		myListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				String tp = list.get(position).getName();
				if(!"全部".equals(tp)){
					areaStr += list.get(position).getName() + " ";
					area_id = list.get(position).getId();	
				}
				//String st = area_id;
				list = initData1(list.get(position).getId());
				if (list.size() > 1) {
					adapter.list = list;
					adapter.notifyDataSetChanged();
				} else {
					// alertDialog.cancel();
					popupWindow.dismiss();
					etArea.setText(areaStr);
				}
			}
		});

		popupWindow = new PopupWindow(menuView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT, true); // 背景
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setAnimationStyle(R.style.PopupAnimation);
		popupWindow.showAtLocation(findViewById(R.id.parent), Gravity.CENTER
				| Gravity.CENTER, 0, 0);
		popupWindow.update();

	}

	protected void actionAlertDialog1() {
		areaStr = "";
		area_id = "";
		list = initData();

		inflater = (LayoutInflater) this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		layout = inflater.inflate(R.layout.area_view,
				(ViewGroup) findViewById(R.id.layout_cust_area));
		myListView = (ListView) layout.findViewById(R.id.lv_cust_area);
		adapter = new CustAreaAdapter(this, list);
		myListView.setAdapter(adapter);

		myListView.setOnItemClickListener(new OnItemClickListener() {

			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// showToast("-----" + list.get(position).getId() + ":" +
				// list.get(position).getName());
				// alertDialog.cancel();
				// list = initData1();
				areaStr += list.get(position).getName() + " ";
				area_id = list.get(position).getId();

				list = initData1(list.get(position).getId());
				if (list.size() > 0) {
					adapter.list = list;
					adapter.notifyDataSetChanged();
				} else {
					alertDialog.cancel();
					etArea.setText(areaStr);
				}
			}
		});

		builder = new AlertDialog.Builder(this);
		builder.setTitle("选择区域");
		builder.setView(layout);
		alertDialog = builder.create();
		alertDialog.show();

	}

	protected List<CustProp> initData() {
		List<CustProp> list = DBUtil.getDataFromLCustArea(
				locationHelper.getWritableDatabase(), "1");
		return list;
	}

	protected List<CustProp> initData1(String pid) {
		List<CustProp> listall =  new ArrayList<CustProp>();
		CustProp cpone = new CustProp();
		cpone.setId("xlwqtdwdwsm");
		cpone.setName("全部");
		listall.clear();
		listall.add(cpone);
		List<CustProp> list = DBUtil.getDataFromLCustAreaByPid(
				locationHelper.getWritableDatabase(), pid);
		listall.addAll(list);
		return listall;
	}

	class CustPropThread implements Runnable {
		@Override
		public void run() {
			Looper.prepare();
			Message msg = handler.obtainMessage();
			try {
				sp = getSharedPreferences("userdata", 0);
				String oldupdatecode_type = sp.getString("custtype", "");
				String updatecode_type = getCustType("1");
				if (!updatecode_type.equals(oldupdatecode_type)) {
					updatecode_type = getCustType("2");
					// 写数据
					IUtil.writeSharedPreference(sp, "custtype", updatecode_type);
				}
				String oldupdatecode_area = sp.getString("custarea", "");
				String updtecode_area = getCustArea("1");
				if (!updtecode_area.equals(oldupdatecode_area)) {
					updtecode_area = getCustArea("2");
					// 写数据
					IUtil.writeSharedPreference(sp, "custarea", updtecode_area);
				}
				msg.what = 2;
				handler.sendMessage(msg);
			} catch (Exception e) {
				FileLog.e(TAG, "====" + e.toString());
				respMsg = getResources().getString(R.string.cust_upload_err);
				msg.what = 2;
				msg.obj = respMsg;
				handler.sendMessage(msg);
			}
			Looper.loop();
		}
	}

	/**
	 * 获取客户类型
	 * 
	 * @param type
	 *            1查询是否有新类型，2查询所有类型
	 * @return 当前版本号
	 * @throws Exception
	 */
	private String getCustType(String type) throws Exception {
		sp = getSharedPreferences("userdata", 0);
		SetInfo set = IUtil.initSetInfo(sp);

		String url = set.getHttpip() + Contant.ACTION;
		Map<String, String> map = new HashMap<String, String>();
		map.put("reqCode", Contant.CLINET_TYPE_UPDATE_ACTION);
		map.put("gpsid", set.getDevice_id());
		map.put("pin", set.getAuth_code());
		map.put("actiontype", type);

		String jsonStr = AndroidHttpClient.getContent(url, map);
		FileLog.i(TAG, jsonStr);
		jsonStr = IUtil.chkJsonStr(jsonStr);
		JSONArray array = new JSONArray(jsonStr);
		String updatecode = "";
		if (array.length() > 0) {
			JSONObject obj = array.getJSONObject(0);
			updatecode = obj.getString("updatecode");
			FileLog.i(TAG, "updatecode==>" + updatecode);
			if ("2".equals(type)) {
				DBUtil.deleteLCustType(locationHelper.getWritableDatabase());
				JSONArray array2 = obj.getJSONArray("clientdata");
				for (int i = 0; i < array2.length(); i++) {
					JSONObject obj2 = array2.getJSONObject(i);
					String id = obj2.getString("id");
					String name = obj2.getString("name");
					DBUtil.insertLCustType(
							locationHelper.getWritableDatabase(), id, name);
				}
			}
		}
		// 初始化类型数据
		// types =
		// DBUtil.getDataFromLCustType(locationHelper.getWritableDatabase());
		// 初始化类型数据
		List<CustProp> cps = DBUtil.getDataFromLCustType(locationHelper
				.getWritableDatabase());
		CustProp cp = new CustProp();
		cp.setId("");
		cp.setName("请选择客户类型");

		types = new ArrayList<CustProp>();
		types.add(cp);
		for (CustProp tmp : cps) {
			types.add(tmp);
		}
		return updatecode;
	}

	private String getCustArea(String type) throws Exception {
		sp = getSharedPreferences("userdata", 0);
		SetInfo set = IUtil.initSetInfo(sp);

		String url = set.getHttpip() + Contant.ACTION;
		Map<String, String> map = new HashMap<String, String>();
		map.put("reqCode", Contant.CLINET_REGION_UPDATE_ACTION);
		map.put("gpsid", set.getDevice_id());
		map.put("pin", set.getAuth_code());
		map.put("actiontype", type);

		String jsonStr = AndroidHttpClient.getContent(url, map);
		FileLog.i(TAG, jsonStr);
		jsonStr = IUtil.chkJsonStr(jsonStr);
		JSONArray array = new JSONArray(jsonStr);
		String updatecode = "";
		if (array.length() > 0) {
			JSONObject obj = array.getJSONObject(0);
			updatecode = obj.getString("updatecode");
			FileLog.i(TAG, "updatecode==>" + updatecode);
			if ("2".equals(type)) {
				DBUtil.deleteLCustArea(locationHelper.getWritableDatabase());
				JSONArray array2 = obj.getJSONArray("clientdata");
				for (int i = 0; i < array2.length(); i++) {
					JSONObject obj2 = array2.getJSONObject(i);
					String id = obj2.getString("id");
					String name = obj2.getString("name");
					String pid = obj2.getString("pid");
					String level = obj2.getString("level");
					DBUtil.insertLCustArea(
							locationHelper.getWritableDatabase(), id, name,
							pid, level);
				}
			}
		}
		return updatecode;
	}
}
