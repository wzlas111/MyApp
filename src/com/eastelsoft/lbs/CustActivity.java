/**
 * Copyright (c) 2012-8-17 www.eastelsoft.com
 * $ID CustActivity.java 上午9:15:26 $
 */
package com.eastelsoft.lbs;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.ProgressBar;
import android.widget.SectionIndexer;
import android.widget.TextView;
import android.widget.Toast;

import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.db.DBUtil;
import com.eastelsoft.lbs.db.LocationSQLiteHelper;
import com.eastelsoft.lbs.entity.CustBean;
import com.eastelsoft.lbs.entity.SetInfo;
import com.eastelsoft.util.Contant;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.IUtil;
import com.eastelsoft.util.contact.PingYinUtil;
import com.eastelsoft.util.contact.PinyinComparatorTwo;
import com.eastelsoft.util.contact.SideBar;
import com.eastelsoft.util.http.AndroidHttpClient;

/**
 * 客户类表
 * 
 * @author lengcj
 */
public class CustActivity extends BaseActivity {
	private static final String TAG = "CustActivity";

	private Button btBack;
	private Button btAddInfo;
	private ListView lvContact;
	private SideBar indexBar;
	private WindowManager mWindowManager;
	private TextView mDialogText;
	private ProgressBar loading;
	private EditText etcustSearchText;
	private ImageButton cancel_button;
	private LinearLayout llTabCust;
	private LinearLayout llTabCustv;
	private LinearLayout llTabSalesv;
	private Button btCust;
	private Button btCustv;
	private Button btSalesv;
	private LocationSQLiteHelper locationHelper;
	private Thread dataThread;
	private Thread initThread;
	private ArrayList<CustBean> mData = new ArrayList<CustBean>();
	private ArrayList<CustBean> mDataChange = new ArrayList<CustBean>();
	private String notdisplay = "";

	// 客户信息筛选功能
	private TextView tvTitleCust;
	private ImageView ivArrow;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_cust);
		etcustSearchText = (EditText) findViewById(R.id.custSearchText);
		cancel_button = (ImageButton) findViewById(R.id.cancel_button);
		cancel_button.setOnClickListener(new OnBtCancelClickListenerImpl());

		initSearchListener();
		btBack = (Button) findViewById(R.id.btBack);
		btBack.setOnClickListener(new OnBtBackClickListenerImpl());
		btAddInfo = (Button) findViewById(R.id.btAddCust);
		btAddInfo.setOnClickListener(new OnBtAddCustClickListenerImpl());
		loading = (ProgressBar) findViewById(R.id.loadingCust);

		// 客户列表筛选
		tvTitleCust = (TextView) findViewById(R.id.tvTitleCust);
		ivArrow = (ImageView) findViewById(R.id.ivArrow);
		tvTitleCust.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showPopupWindow();
			}
		});
		llTabCust = (LinearLayout) findViewById(R.id.tabCust);
		llTabCustv = (LinearLayout) findViewById(R.id.tabCustv);
		llTabSalesv = (LinearLayout) findViewById(R.id.tabSalesv);
		btCust = (Button) findViewById(R.id.btCust);
		btCustv = (Button) findViewById(R.id.btCustv);
		btSalesv = (Button) findViewById(R.id.btSalesv);
		btCust.setOnClickListener(new OnTabCustClickListenerImpl());
		btCustv.setOnClickListener(new OnTabCustvClickListenerImpl());
		btSalesv.setOnClickListener(new OnTabSalesvClickListenerImpl());
		mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		locationHelper = new LocationSQLiteHelper(this, null, null, 5);
		sp = getSharedPreferences("userdata", 0);
		notdisplay = sp.getString("notdisplay", "");
		if (!"yes".equals(notdisplay)) {
			Thread sendThread = new Thread(new UpdatePyThread());
			sendThread.start();
		}
		lvContact = (ListView) this.findViewById(R.id.lvContact);
		lvContact.setOnItemClickListener(new OnItemClickListenerImpl());
		lvContact.setOnItemLongClickListener(new OnItemLongClickListenerImpl());
		indexBar = (SideBar) findViewById(R.id.sideBar);
		mDialogText = (TextView) LayoutInflater.from(this).inflate(
				R.layout.list_position, null);
		mDialogText.setVisibility(View.INVISIBLE);
		WindowManager.LayoutParams lp = new WindowManager.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT,
				WindowManager.LayoutParams.TYPE_APPLICATION,
				WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
						| WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
				PixelFormat.TRANSLUCENT);
		mWindowManager.addView(mDialogText, lp);
		findView();
	}
	@Override
	protected void onStart() {
		if (isOnStart) {
			tvTitleCust.setText("客户信息");
			ivArrow.setImageResource(R.drawable.arrow_down);
			loading.setVisibility(View.VISIBLE);
			initThread = new Thread(new InitThread());
			initThread.start();
		} else {
			isOnStart = true;
		}
		super.onStart();
	}
	private boolean isOnStart = true;
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
	}

	@Override
	protected void onDestroy() {
		FileLog.i(TAG, "onDestroy:" + dataThread);
		super.onDestroy();
		if (locationHelper != null
				&& locationHelper.getWritableDatabase() != null) {
			locationHelper.getWritableDatabase().close();
		}
		mWindowManager.removeView(mDialogText);
		mWindowManager = null;
		mDialogText = null;
		if (loading != null)
			loading.cancelLongPress();
		loading = null;
		lvContact = null;
		indexBar = null;
		if (dataThread != null) {
			try {
				dataThread.interrupt();
				dataThread = null;
			} catch (Exception e) {
			}
		}
	}

	private LinearLayout layout;
	private ListView listView;
	private String title[] = { "企业共享", "员工私有", "全部" };

	public void showPopupWindow() {
		ivArrow.setImageResource(R.drawable.arrow_up);
		layout = (LinearLayout) LayoutInflater.from(CustActivity.this).inflate(
				R.layout.dialog, null);
		listView = (ListView) layout.findViewById(R.id.lv_dialog);
		listView.setAdapter(new ArrayAdapter<String>(CustActivity.this,
				R.layout.text, R.id.tv_text, title));

		popupWindow = new PopupWindow(CustActivity.this);

		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setWidth(getWindowManager().getDefaultDisplay().getWidth() / 2);
		popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);
		popupWindow.setContentView(layout);
		// showAsDropDown会把里面的view作为参照物，所以要那满屏幕parent
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		int xPos = popupWindow.getWidth() / 2 - tvTitleCust.getWidth() / 2;
		popupWindow.showAsDropDown(findViewById(R.id.tvTitleCust), -xPos, 6);
		popupWindow.setOnDismissListener(new OnDismissListener() {

			@Override
			public void onDismiss() {
				// TODO Auto-generated method stub
				ivArrow.setImageResource(R.drawable.arrow_down);

			}
		});
		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				String t_title = title[arg2];
				if ("全部".equals(t_title)) {
					t_title = "客户信息";
				}
				tvTitleCust.setText(t_title);
				popupWindow.dismiss();
				popupWindow = null;
				loading.setVisibility(View.VISIBLE);
				switch (arg2) {
				case 0:
					// 查企业共享
					Thread initThread0 = new Thread(new InitThreadZero());
					initThread0.start();
					break;
				case 1:
					// 查员工私有
					Thread initThread1 = new Thread(new InitThreadOne());
					initThread1.start();
					break;
				case 2:
					// 查全部
					Thread initThread2 = new Thread(new InitThreadTwo());
					initThread2.start();
					break;

				default:
					break;
				}

			}
		});
	}

	class InitThreadZero implements Runnable {
		@Override
		public void run() {
			Message msg = handler.obtainMessage();
			msg.what = 32;
			try {
				initDataZero();
			} catch (Exception e) {
			} finally {
				handler.sendMessage(msg);
			}
		}
	}

	private void initDataZero() {
		mData = DBUtil.getDataFromLCustMgEmployeeshare(locationHelper
				.getWritableDatabase());
		// 排序
		Collections.sort(mData, new PinyinComparatorTwo());
	}

	class InitThreadOne implements Runnable {
		@Override
		public void run() {
			Message msg = handler.obtainMessage();
			msg.what = 32;
			try {
				initDataOne();
			} catch (Exception e) {
			} finally {
				handler.sendMessage(msg);
			}
		}
	}

	private void initDataOne() {
		mData = DBUtil.getDataFromLCustMgEmployeeprivate(locationHelper
				.getWritableDatabase());
		// 排序
		Collections.sort(mData, new PinyinComparatorTwo());
	}

	class InitThreadTwo implements Runnable {
		@Override
		public void run() {
			Message msg = handler.obtainMessage();
			msg.what = 32;
			try {
				initData();
			} catch (Exception e) {
			} finally {
				handler.sendMessage(msg);
			}
		}
	}

	public void hideBtn() {
		// 设置按钮不可见
		if (cancel_button.isShown())
			cancel_button.setVisibility(View.GONE);
	}

	public void showBtn() {
		// 设置按钮可见
		if (!cancel_button.isShown())
			cancel_button.setVisibility(View.VISIBLE);
	}

	private void initSearchListener() {
		etcustSearchText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
				// Toast.makeText(CustActivity.this, etcustSearchText.getText(),
				// Toast.LENGTH_SHORT).show();
				// 开始执行搜索
				String inputText = etcustSearchText.getText().toString();
				if (mData.size() > 0) {
					// nicks = null;
					if (inputText != null && inputText.length() > 0) {
						showBtn();

						ArrayList<CustBean> mDatatemp = new ArrayList<CustBean>();
						for (int i = 0; i < mData.size(); i++) {
							if (mData.get(i).getClientName().indexOf(inputText) > -1) {

								mDatatemp.add(mData.get(i));
							}
						}
						for (int i = 0; i < mData.size(); i++) {

							if (converterToFirstSpell(
									mData.get(i).getClientName()).indexOf(
									inputText) > -1) {

								mDatatemp.add(mData.get(i));
							}
						}
						// 开始处理页面显示

						mData = mDatatemp;
						Collections.sort(mData, new PinyinComparatorTwo());

					} else {
						hideBtn();

						mData = DBUtil.getDataFromLCustMg(locationHelper
								.getWritableDatabase());
						// 排序
						Collections.sort(mData, new PinyinComparatorTwo());

					}
					findView();
				} else {
					hideBtn();

					mData = DBUtil.getDataFromLCustMg(locationHelper
							.getWritableDatabase());
					// 排序
					Collections.sort(mData, new PinyinComparatorTwo());
					findView();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(Editable s) {
			}
		});
	}

	private class OnBtBackClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				CustActivity.this.finish();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	private class OnBtAddCustClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				Intent intent = new Intent(CustActivity.this,
						CustAddActivity.class);
				startActivity(intent);
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	private class OnBtCancelClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			hideBtn();
			// 设置输入框内容为空
			etcustSearchText.setText("");

		}
	}

	private class OnItemClickListenerImpl implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			// TODO Auto-generated method stub
			isOnStart = false;
			String clientName = mData.get(arg2).getClientName();
			String myid = mData.get(arg2).getId();

			// Toast.makeText(CustActivity.this, ":" + clientName + ":",
			// Toast.LENGTH_SHORT).show();
			// 跳转到查看页面
			Intent intent = new Intent(CustActivity.this,
					CustViewActivity.class);
			intent.putExtra("clientName", clientName);

			intent.putExtra("myid", myid);
			startActivity(intent);
		}
	}

	//
	private class OnTabCustClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				llTabCust.setBackgroundResource(R.drawable.bg_tab);
				llTabCustv.setBackgroundColor(Color.TRANSPARENT);
				llTabSalesv.setBackgroundColor(Color.TRANSPARENT);
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	private class OnTabCustvClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				llTabCust.setBackgroundColor(Color.TRANSPARENT);
				llTabCustv.setBackgroundResource(R.drawable.bg_tab);
				llTabSalesv.setBackgroundColor(Color.TRANSPARENT);

				CustActivity.this.finish();
				Intent intent = new Intent(CustActivity.this,
						BaifangjiluActivity.class);
				startActivity(intent);

			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	private class OnTabSalesvClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				llTabCust.setBackgroundColor(Color.TRANSPARENT);
				llTabCustv.setBackgroundColor(Color.TRANSPARENT);
				llTabSalesv.setBackgroundResource(R.drawable.bg_tab);
				CustActivity.this.finish();
				Intent intent = new Intent(CustActivity.this,
						SalesReportActivity.class);
				startActivity(intent);
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	class InitThread implements Runnable {
		@Override
		public void run() {
			Message msg = handler.obtainMessage();
			msg.what = 1;
			try {
				initData();
			} catch (Exception e) {
				FileLog.e(TAG, "getCustFromServer initdata==>" + e.toString());
			} finally {
				handler.sendMessage(msg);
			}
		}
	}

	class DataThread implements Runnable {
		@Override
		public void run() {
			Message msg = handler.obtainMessage();
			msg.what = 0;
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
					FileLog.i(TAG, "updatecode==>" + updatecode);
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
					FileLog.i(TAG, url);
					FileLog.i(TAG, "reqCode=" + "ClientUpdateAction");
					FileLog.i(TAG, "ActionType=" + "2");
					FileLog.i(TAG, "GpsId=" + set.getDevice_id());
					FileLog.i(TAG, "Pin=" + set.getAuth_code());

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
							// mData =
							// DBUtil.getDataFromLCustMg(locationHelper.getWritableDatabase());
							// System.out.println(mData.size());
							DBUtil.deleteLCustHasUp(locationHelper
									.getWritableDatabase());
							// mData =
							// DBUtil.getDataFromLCustMg(locationHelper.getWritableDatabase());
							// System.out.println(mData.size());
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
					initData(); // 初始化客户数
					findView();
					loading.setVisibility(View.GONE);
					break;
				case 1:
					findView();
					dataThread = new Thread(new DataThread());
					dataThread.start();
					break;
				case 32:
					findView();
					loading.setVisibility(View.GONE);
					break;

				default:
					break;
				}
			} catch (Exception e) {
				// 异常中断
			}
		}
	};

	/**
	 * 读取手机本地数据
	 */
	private void initData() {
		mData = DBUtil.getDataFromLCustMg(locationHelper.getWritableDatabase());
		// 排序
		Collections.sort(mData, new PinyinComparatorTwo());
	}

	void findView() {

		lvContact.setAdapter(new ContactAdapter(this, mData));
		indexBar.setListView(lvContact);
		indexBar.setTextView(mDialogText);
	}

	class ContactAdapter extends BaseAdapter implements SectionIndexer {
		private Context mContext;
		private ArrayList<CustBean> alll;

		/*
		 * private String[] mNicks; private LocationSQLiteHelper locationHelper;
		 */
		/* private String[] mIstijiaos; */
		public ContactAdapter(Context mContext, ArrayList<CustBean> alll) {
			this.mContext = mContext;
			this.alll = alll;
			/* this.mNicks = nicks; */
			/* this.mIstijiaos = istijiaos ; */
			// 排序(实现了中英文混排)
			/*
			 * Arrays.sort(mNicks, new PinyinComparator()); Arrays.sort(ids, new
			 * PinyinComparator());
			 */
			// 对alll安照name排序
			/*
			 * Arrays.sort(mIstijiaos, new PinyinComparator()); locationHelper =
			 * new LocationSQLiteHelper(mContext, null, null, 3);
			 */
		}

		@Override
		public int getCount() {
			return alll.size();
		}

		@Override
		public Object getItem(int position) {
			return alll.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {

			final String nickName = alll.get(position).getClientName();
			/* String istijiaoSt = mIstijiaos[position]; */

			ViewHolder viewHolder = null;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.contact_item, null);
				viewHolder = new ViewHolder();
				viewHolder.tvCatalog = (TextView) convertView
						.findViewById(R.id.contactitem_catalog);
				viewHolder.ivAvatar = (ImageView) convertView
						.findViewById(R.id.contactitem_avatar_iv);
				viewHolder.tvNick = (TextView) convertView
						.findViewById(R.id.contactitem_nick);
				viewHolder.ivIstijiao = (ImageView) convertView
						.findViewById(R.id.iv_tijiao);

				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			String catalog = "";
			if (converterToFirstSpell(nickName) != null
					&& converterToFirstSpell(nickName).length() > 0) {
				catalog = converterToFirstSpell(nickName).substring(0, 1)
						.toUpperCase();
			}

			if (position == 0) {
				if (catalog != null && !catalog.equals("")) {
					viewHolder.tvCatalog.setVisibility(View.VISIBLE);
					viewHolder.tvCatalog.setText(catalog);

				} else {
					viewHolder.tvCatalog.setVisibility(View.GONE);

				}

			} else {
				if (catalog != null && !catalog.equals("")) {

					String lastCatalog = "";
					if (converterToFirstSpell(alll.get(position - 1)
							.getClientName()) != null
							&& converterToFirstSpell(
									alll.get(position - 1).getClientName())
									.length() > 0) {
						lastCatalog = converterToFirstSpell(
								alll.get(position - 1).getClientName())
								.substring(0, 1).toUpperCase();
					}

					if (catalog.equals(lastCatalog)) {
						viewHolder.tvCatalog.setVisibility(View.GONE);
					} else {
						viewHolder.tvCatalog.setVisibility(View.VISIBLE);
						viewHolder.tvCatalog.setText(catalog);
					}
				} else {
					viewHolder.tvCatalog.setVisibility(View.GONE);

				}

			}

			viewHolder.ivAvatar.setImageResource(R.drawable.default_avatar);
			viewHolder.tvNick.setText(nickName);
			String myid = alll.get(position).getId();
			String istijiao = alll.get(position).getIstijiao();

			if ("00".equals(istijiao)) {

				viewHolder.ivIstijiao.setVisibility(View.VISIBLE);

			} else {
				viewHolder.ivIstijiao.setVisibility(View.INVISIBLE);

			}
			return convertView;
		}

		class ViewHolder {
			TextView tvCatalog;// 目录
			ImageView ivAvatar;// 头像
			TextView tvNick;// 昵称
			ImageView ivIstijiao;// 是否上传
		}

		@Override
		public int getPositionForSection(int section) {
			for (int i = 0; i < alll.size(); i++) {

				// String l = converterToFirstSpell(alll.get(i).getClientName())
				// .substring(0, 1);
				String l = converterToFirstSpell(alll.get(i).getClientName());
				if (l != null && l.length() > 0) {
					l = l.substring(0, 1);
					char firstChar = l.toUpperCase().charAt(0);
					if (firstChar == section) {
						return i;
					}
				}

			}
			return -1;
		}

		@Override
		public int getSectionForPosition(int position) {
			return 0;
		}

		@Override
		public Object[] getSections() {
			return null;
		}
	}

	/**
	 * 汉字转换位汉语拼音首字母，英文字符不变
	 * 
	 * @param chines
	 *            汉字
	 * @return 拼音
	 */
	public String converterToFirstSpell(String chines) {
		String pinyinName = "";
		char[] nameChar = chines.toCharArray();
		HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
		defaultFormat.setCaseType(HanyuPinyinCaseType.UPPERCASE);
		defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
		for (int i = 0; i < nameChar.length; i++) {
			if (nameChar[i] > 128) {
				try {
					pinyinName += PinyinHelper.toHanyuPinyinStringArray(
							nameChar[i], defaultFormat)[0].charAt(0);
				} catch (BadHanyuPinyinOutputFormatCombination e) {
					e.printStackTrace();
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				pinyinName += nameChar[i];
			}
		}
		return pinyinName;
	}

	private class OnItemLongClickListenerImpl implements
			OnItemLongClickListener {

		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				final int position, long arg3) {

			new AlertDialog.Builder(CustActivity.this)
					.setTitle(Contant.OP)
					.setItems(R.array.planarrcontent,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									String[] PK = getResources()
											.getStringArray(
													R.array.planarrcontent);
									if (PK[which].equals(Contant.OP_DEL)) {
										String info_auto_id = mData.get(
												position).getId();
										String istijiao = mData.get(position)
												.getIstijiao();
										if ("00".equals(istijiao)) {
											DBUtil.deleteLCustbyId(
													locationHelper
															.getWritableDatabase(),
													info_auto_id);
											/* mData.remove(position); */
											mData.remove(position);
											Collections.sort(mData,
													new PinyinComparatorTwo());
											findView();
											Toast.makeText(
													CustActivity.this,
													PK[which] + Contant.OP_SUCC,
													Toast.LENGTH_SHORT).show();

										} else {
											Toast.makeText(CustActivity.this,
													"平台上的客户信息不能删除",
													Toast.LENGTH_SHORT).show();

										}

									}

									if (PK[which].equals(Contant.OP_VIEW)) {
										isOnStart = false;
										String clientName = mData.get(position)
												.getClientName();
										String myid = mData.get(position)
												.getId();

										// Toast.makeText(CustActivity.this, ":"
										// + clientName + ":",
										// Toast.LENGTH_SHORT).show();
										// 跳转到查看页面
										Intent intent = new Intent(
												CustActivity.this,
												CustViewActivity.class);
										intent.putExtra("clientName",
												clientName);

										intent.putExtra("myid", myid);
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

	class UpdatePyThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			FileLog.i(TAG, "UpdatePyThread");

			mDataChange = DBUtil.getDataFromLCustMg(locationHelper
					.getWritableDatabase());
			for (int i = 0; i < mDataChange.size(); i++) {
				if (mDataChange.get(i).getClientNamePinYin() == null
						|| "".equals(mDataChange.get(i).getClientNamePinYin())) {
					// 修改拼音
					String py = PingYinUtil.getPingYin(mDataChange.get(i)
							.getClientName());
					DBUtil.updateCustPy(locationHelper.getWritableDatabase(),
							py, mDataChange.get(i).getId());

				}

			}
			sp = getSharedPreferences("userdata", 0);
			IUtil.writeSharedPreference(sp, "notdisplay", "yes");

			FileLog.i(TAG, "UpdatePyThread end");

		}

	}

}
