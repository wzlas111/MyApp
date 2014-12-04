/**
 * Copyright (c) 2012-8-13 www.eastelsoft.com
 * $ID InfoActivity.java 下午3:22:36 $
 */
package com.eastelsoft.lbs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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

import com.eastelsoft.lbs.SalesReportAddActivity.InitThread;
import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.adapter.BulletinListItemAdapter;
import com.eastelsoft.lbs.adapter.InfoListItemAdapterA;
import com.eastelsoft.lbs.adapter.SalesReportAddAdapterA;
import com.eastelsoft.lbs.db.DBUtil;
import com.eastelsoft.lbs.db.LocationSQLiteHelper;
import com.eastelsoft.lbs.entity.BulletinBean;
import com.eastelsoft.lbs.entity.InfoBean;
import com.eastelsoft.lbs.entity.SetInfo;
import com.eastelsoft.util.Contant;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.GlobalVar;
import com.eastelsoft.util.IUtil;
import com.eastelsoft.util.contact.PingYinUtil;
import com.eastelsoft.util.http.AndroidHttpClient;

/**
 * 信息上报记录页面
 * 
 * @author lengcj
 */
public class BulletinListActivity extends BaseActivity {
	public static final String TAG = "InfoActivity";
	private ListView lv;
	private Button btBack;
	private LocationSQLiteHelper locationHelper;

	// 定义适配器
	private BulletinListItemAdapter listadpter;
	private ArrayList<BulletinBean> arrayList = new ArrayList<BulletinBean>();

	private SetInfo set;
	private Thread dataThread;
	private Thread initThread;

	private int first = 1;

	// 查询
	private ImageView ivSearch;
	private ImageView ivReset;
	private TextView tvCustSearchText;
	String custSearchText = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		FileLog.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bulletin_list);
		btBack = (Button) findViewById(R.id.btBack);
		btBack.setOnClickListener(new OnBtBackClickListenerImpl());
		lv = (ListView) findViewById(android.R.id.list);
		lv.setOnItemClickListener(new OnItemClickListenerImpl());
		// lv.setOnItemLongClickListener(new OnItemLongClickListenerImpl());
		locationHelper = new LocationSQLiteHelper(this, null, null, 5);
		// 初始化全局变量
		globalVar = (GlobalVar) getApplicationContext();

		sp = getSharedPreferences("userdata", 0);
		set = IUtil.initSetInfo(sp);
		first = 1;
		
		tvCustSearchText = (TextView) this.findViewById(R.id.searchText);
		tvCustSearchText.setHint("标题");
		ivSearch = (ImageView) this.findViewById(R.id.doSearch);

		ivSearch.setOnClickListener(new OnBtSearchClickListenerImpl());

	}

	private void init(String searchstring) {
		try {
			arrayList.clear();
			arrayList = DBUtil.getDataFromLbulletin(locationHelper
					.getWritableDatabase(),searchstring);
			listadpter = new BulletinListItemAdapter(BulletinListActivity.this,
					arrayList);
			// 填充适配器
			lv.setAdapter(listadpter);

		} catch (Exception e) {
		}
	}

	@Override
	protected void onStart() {
		super.onStart();
		init(custSearchText); // 初始化本地数据
		if (first == 1) {
			showDialog(PROCESS_DIALOG);
			initThread = new Thread(new InitThread());
			initThread.start();
			first++;

		}

	}

	@Override
	protected void onStop() {
		super.onStop();
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

	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		FileLog.i(TAG, "onNewIntent");
		init(custSearchText);
		showDialog(PROCESS_DIALOG);
		initThread = new Thread(new InitThread());
		initThread.start();
	}

	class InitThread implements Runnable {
		@Override
		public void run() {

			Message msg = handler.obtainMessage();
			msg.what = 4;
			try {
				sp = getSharedPreferences("userdata", 0);
				SetInfo set = IUtil.initSetInfo(sp);
				String bulletinupdatecode = sp.getString("bulletinupdatecode",
						"");
				// 先发消息检是否需要更新
				String url = set.getHttpip() + Contant.BULLETIN_UPDATE_ACTION;
				url += "&actiontype=1";
				url += "&gpsid=" + set.getDevice_id();
				url += "&pin=" + set.getAuth_code();
				String jsonStr = AndroidHttpClient.getContent(url);
				jsonStr = IUtil.chkJsonStr(jsonStr);
				String updatecode = "";
				JSONArray array = new JSONArray(jsonStr);
				if (array.length() > 0) {
					JSONObject obj = array.getJSONObject(0);
					updatecode = obj.getString("updatecode");
				}

				// 如果需要更新
				if (!bulletinupdatecode.equals(updatecode)) {
					url = set.getHttpip() + Contant.ACTION;
					Map<String, String> map = new HashMap<String, String>();
					map.put("reqCode", Contant.BULLETIN_ACTION);
					map.put("actiontype", "2");
					map.put("gpsid", set.getDevice_id());
					map.put("pin", set.getAuth_code());
					// "bulletindata":[{"b_id":"1fbdfa2be41dc66f53d1d024e65d2bf9","b_code":3,"b_name":"117",
					// "is_distribute":1,"b_release_date":"2013-11-07 10:04:31","is_top":1,"is_read":0},
					// {"b_id":"4f24a2e92cc0fedb53
					// }
					// {"b_id":"e49a3cc1bdbfefee07ddef3e390e7cf8","b_code":4,"b_name":"iOS公告测试详情",
					// "is_distribute":0,"b_release_date":"","is_top":0,"is_read":0}],"updatecode":"59"}]
					String jsonStr1 = AndroidHttpClient.getContent(url, map);
					FileLog.i(TAG, "jsonStr1==>" + jsonStr1);
					jsonStr1 = IUtil.chkJsonStr(jsonStr1);
					JSONArray array1 = new JSONArray(jsonStr1);
					if (array1.length() > 0) {
						JSONObject obj1 = array1.getJSONObject(0);
						updatecode = obj1.getString("updatecode");
						if (updatecode != null && !"".equals(updatecode)) {
							// 第一步 删除掉未读的
							DBUtil.deleteLbulletinAll(locationHelper
									.getWritableDatabase());
							// 第二步 插入未读的
							JSONArray array2 = obj1
									.getJSONArray("bulletindata");
							for (int i = 0; i < array2.length(); i++) {
								JSONObject obj2 = array2.getJSONObject(i);
								String b_id = obj2.getString("b_id");
								String b_code = String.valueOf(obj2
										.getInt("b_code"));

								String b_name = obj2.getString("b_name");
								String b_release_date = obj2
										.getString("b_release_date");
								String is_top = String.valueOf(obj2
										.getInt("is_top"));
								String is_read = String.valueOf(obj2
										.getInt("is_read"));

								// 判断已读中是否有相同的id的记录
								// if (DBUtil.checkHasTheId(
								// locationHelper.getWritableDatabase(),
								// b_id)) {
								// DBUtil.deleteLbulletinHasRepeat(
								// locationHelper
								// .getWritableDatabase(),
								// b_id);
								//
								// DBUtil.insertLbulletin(locationHelper
								// .getWritableDatabase(), b_id,
								// b_name, b_release_date, b_code,
								// is_top, "0");
								//
								// } else {
								DBUtil.insertLbulletin(
										locationHelper.getWritableDatabase(),
										b_id, b_name, b_release_date, b_code,
										is_top, is_read);

								// }

							}
						}
						IUtil.writeSharedPreference(sp, "bulletinupdatecode",
								updatecode);
					}
					msg.what = 5;
				}

				// for (int i = 1; i < 51; i++) {
				// DBUtil.insertLbulletin(
				// locationHelper.getWritableDatabase(), i + "", i
				// + "标题", "2013-" + i, "444", "0", "0");
				//
				// }

			} catch (Exception e) {
				FileLog.e(TAG, "getBulletinServer==>" + e.toString());
			} finally {
				handler.sendMessage(msg);
			}

		}
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 0:
				break;
			case 4:
				dismissDialog(PROCESS_DIALOG);
				break;
			case 5:
				arrayList.clear();
				arrayList = DBUtil.getDataFromLbulletin(locationHelper
						.getWritableDatabase(),custSearchText);
				listadpter = new BulletinListItemAdapter(
						BulletinListActivity.this, arrayList);
				// 填充适配器
				lv.setAdapter(listadpter);
				dismissDialog(PROCESS_DIALOG);
				break;
			case 6:
				init(custSearchText);
				dismissDialog(PROCESS_DIALOG);
				break;
			
			}

		}

	};

	private class OnBtBackClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				BulletinListActivity.this.finish();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	private class OnItemClickListenerImpl implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long id) {

			String info_auto_id = arrayList.get(position).getB_id();
			String is_read = arrayList.get(position).getIs_read();
			// 跳转到查看页面
			Intent intent = new Intent(BulletinListActivity.this,
					BulletinviewActivity.class);
			intent.putExtra("info_auto_id", info_auto_id);
			intent.putExtra("is_read", is_read);
			startActivity(intent);
		}
	}

	private class OnItemLongClickListenerImpl implements
			OnItemLongClickListener {
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				final int position, long id) {
			new AlertDialog.Builder(BulletinListActivity.this)
					.setTitle(Contant.OP)
					.setItems(R.array.infoarrcontent,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									String[] PK = getResources()
											.getStringArray(
													R.array.infoarrcontent);
									if (PK[which].equals(Contant.OP_DEL)) {

										// String info_auto_id = arrayList.get(
										// position).getInfo_auto_id();
										// DBUtil.deleteLInfo(locationHelper
										// .getWritableDatabase(),
										// info_auto_id);
										// init();
										Toast.makeText(
												BulletinListActivity.this,
												PK[which] + Contant.OP_SUCC,
												Toast.LENGTH_SHORT).show();
									}
									if (PK[which].equals(Contant.OP_ADD)) {
										// 跳转到新增页面
										Intent intent = new Intent(
												BulletinListActivity.this,
												InfoAddActivity.class);
										startActivity(intent);
									}
									if (PK[which].equals(Contant.OP_VIEW)) {
										// String info_auto_id = arrayList.get(
										// position).getInfo_auto_id();
										// // 跳转到查看页面
										// Intent intent = new Intent(
										// BulletinListActivity.this,
										// InfoViewActivity.class);
										// intent.putExtra("info_auto_id",
										// info_auto_id);
										// startActivity(intent);
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
}
