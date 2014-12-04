/**
 * Copyright (c) 2012-8-15 www.eastelsoft.com
 * $ID TaskActivity.java 下午1:15:02 $
 */
package com.eastelsoft.lbs;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.eastelsoft.lbs.CustActivity.InitThread;
import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.db.DBUtil;
import com.eastelsoft.lbs.db.LocationSQLiteHelper;
import com.eastelsoft.lbs.entity.SetInfo;
import com.eastelsoft.util.Contant;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.GlobalVar;
import com.eastelsoft.util.IUtil;
import com.eastelsoft.util.http.AndroidHttpClient;

/**
 * 我的任务UI
 * 
 * @author lengcj
 */
public class PlanActivity extends BaseActivity {
	public static final String TAG = "TaskActivity";
	private ListView lv;
	private Button btBack;
	// private RelativeLayout layout;
	// private Button tv_front;
	private Button tv_bar_not_do;
	private TextView tv_bar_has_do;
	private ProgressBar loading;
	// private int avg_width = 0;
	private List<HashMap<String, Object>> mData;
	private SimpleAdapter listItemAdapter;
	private LocationSQLiteHelper locationHelper;

	private GlobalVar globalVar;
	private String plan_do;
	private String plan_not_do_str;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		FileLog.i(TAG, "onCreate");
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_plan);
		btBack = (Button) findViewById(R.id.btBack);
		btBack.setOnClickListener(new OnBtBackClickListenerImpl());
		loading = (ProgressBar) findViewById(R.id.loadingPlan);
		// layout = (RelativeLayout) findViewById(R.id.layout_title_bar);
		tv_bar_not_do = (Button) findViewById(R.id.tv_title_bar_not_do);
		tv_bar_has_do = (Button) findViewById(R.id.tv_title_bar_has_do);
		tv_bar_not_do.setOnClickListener(onClickListener);
		tv_bar_has_do.setOnClickListener(onClickListener);

		lv = (ListView) findViewById(android.R.id.list);
		lv.setOnItemClickListener(new OnItemClickListenerImpl());
		lv.setOnItemLongClickListener(new OnItemLongClickListenerImpl());
		locationHelper = new LocationSQLiteHelper(this, null, null, 5);
		globalVar = (GlobalVar) getApplicationContext();

		plan_do = Contant.PLAN_NOT_DO;
		// init(Contant.PLAN_NOT_DO);

		// 初始化通知
		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (locationHelper != null
				&& locationHelper.getWritableDatabase() != null) {
			locationHelper.getWritableDatabase().close();
		}
	}

	@Override
	protected void onStart() {
		FileLog.i(TAG, "onStart");

		if (isOnStart) {
			init(plan_do); // 初始化本地数据
			getPlanFromServer(); // 获取网络数据

		} else {
			isOnStart = true;
		}
		super.onStart();
	}

	private boolean isOnStart = true;
	
	
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		FileLog.i(TAG, "onNewIntent");
		init(plan_do); // 初始化本地数据
		getPlanFromServer(); // 获取网络数据 
	}

	private void init(String paramString) {
		try {
			if (mData != null)
				mData.clear();
			mData = DBUtil.getDataFromLPlan(
					locationHelper.getWritableDatabase(), paramString);
			if (locationHelper != null
					&& locationHelper.getWritableDatabase() != null)
				locationHelper.getWritableDatabase().close();
				listItemAdapter = new SimpleAdapter(this, mData,//数据源
				R.layout.plan_list_item,// ListItem的XML实现
				// 动态数组与ImageItem对应的子项
				new String[] { "planId", "title", "planDate",
							"plan_imgFile" },
				// ImageItem的XML文件里面的一个ImageView,两个TextView ID
				new int[] { R.id.planId, R.id.planRemark, R.id.planDate,
							R.id.plan_imgFile });
			lv.setAdapter(listItemAdapter);

			plan_not_do_str = "";
			for (int i = 0; i < mData.size(); i++) {
				plan_not_do_str += mData.get(i).get("planId") + ","
						+ mData.get(i).get("plancode") + "|";
			}
			if (plan_not_do_str.endsWith("|"))
				plan_not_do_str = plan_not_do_str.substring(0,
						(plan_not_do_str.length() - 1));
		} catch (Exception e) {
		}
	}

	private void init1(String paramString) {
		try {
			if (mData != null)
				mData.clear();
			mData = DBUtil.getDataFromLPlan(
				locationHelper.getWritableDatabase(), paramString);
			if (locationHelper != null
				&& locationHelper.getWritableDatabase() != null)
				locationHelper.getWritableDatabase().close();
			if ("0".equals(plan_do)) {
				listItemAdapter = new SimpleAdapter(this, mData,// 数据源
						R.layout.plan_list_item,// ListItem的XML实现
						// 动态数组与ImageItem对应的子项
						new String[] { "planId", "title", "planDate",
								"plan_imgFile" },
						// ImageItem的XML文件里面的一个ImageView,两个TextView ID
						new int[] { R.id.planId, R.id.planRemark,
								R.id.planDate, R.id.plan_imgFile });
				lv.setAdapter(listItemAdapter);
			}

			plan_not_do_str = "";
			for (int i = 0; i < mData.size(); i++) {
				plan_not_do_str += mData.get(i).get("planId") + ","
						+ mData.get(i).get("plancode") + "|";
			}
			if (plan_not_do_str.endsWith("|"))
				plan_not_do_str = plan_not_do_str.substring(0,
						(plan_not_do_str.length() - 1));
		} catch (Exception e) {}
	}

	public void getPlanFromServer() {
		Thread t = new Thread(new Runnable() {
			public void run() {
				Message msg = handler.obtainMessage();
				msg.what = 0;
				try {
					sp = getSharedPreferences("userdata", 0);
					SetInfo set = IUtil.initSetInfo(sp);
					// 先发消息检是否需要更新
					String url = set.getHttpip() + Contant.PLAN_UPDATE_ACTION;
					url += "&ActionType=1";
					url += "&GpsId=" + set.getDevice_id();
					url += "&Pin=" + set.getAuth_code();
					String jsonStr = AndroidHttpClient.getContent(url);
					jsonStr = IUtil.chkJsonStr(jsonStr);
					// String jsonStr = Contant.TEST_DATA_INFO_TAG;
					String updatecode = "";
					JSONArray array = new JSONArray(jsonStr);
					if (array.length() > 0) {
						JSONObject obj = array.getJSONObject(0);
						updatecode = obj.getString("updatecode");
						FileLog.i(TAG, "updatecode==>" + updatecode);
					}
					// 如果需要更新
					if (!set.getPlanupdatecode().equals(updatecode)) {

						url = set.getHttpip() + Contant.ACTION;
						Map<String, String> map = new HashMap<String, String>();
						map.put("reqCode", Contant.PLAN_UPDATE_REQCODE);
						map.put("ActionType", "2");
						map.put("GpsId", set.getDevice_id());
						map.put("Pin", set.getAuth_code());

						if (plan_not_do_str.length() > 0)
							map.put("PlanId_code", plan_not_do_str);
						// url += "&PlanId_code=" + plan_not_do_str;
						String jsonStr1 = AndroidHttpClient
								.getContent(url, map);
						// String jsonStr1 = "[" + Contant.TEST_DATA_INFO_QUERY
						// + "]";
						FileLog.i(TAG, "jsonStr1==>" + jsonStr1);
						jsonStr1 = IUtil.chkJsonStr(jsonStr1);
						JSONArray array1 = new JSONArray(jsonStr1);
						if (array1.length() > 0) {
							JSONObject obj1 = array1.getJSONObject(0);
							updatecode = obj1.getString("updatecode");
							FileLog.i(TAG, "planupdatecode==>" + updatecode);
							JSONArray array2 = obj1.getJSONArray("plandata");
							for (int i = 0; i < array2.length(); i++) {
								JSONObject obj2 = array2.getJSONObject(i);
								String planid = obj2.getString("planid");
								String type = obj2.getString("type");
								FileLog.i(TAG, "planid==>" + planid);
								FileLog.i(TAG, "type==>" + type);
								if ("1".equals(type)) {
									// 非删除任务
									String title = obj2.getString("title");
									String lon = obj2.getString("lon");
									String remark = obj2.getString("remark");
									String location = obj2
											.getString("location");
									String plandate = obj2
											.getString("plandate");
									String lat = obj2.getString("lat");
									String plancode = obj2
											.getString("plancode");
									String releasedate = "";
									// 新增字段
									try {
										releasedate = obj2
												.getString("releasedate");
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}

									// 新增任务
									DBUtil.insertLPlan(locationHelper
											.getWritableDatabase(), updatecode,
											planid, plancode, plandate, lon,
											lat, location, remark, type, "",
											"", "", "0", title, "11",
											releasedate);
								}
								if ("2".equals(type)) {
									// 非删除任务
									String title = obj2.getString("title");
									String lon = obj2.getString("lon");
									String remark = obj2.getString("remark");
									String location = obj2
											.getString("location");
									String plandate = obj2
											.getString("plandate");
									String lat = obj2.getString("lat");
									String plancode = obj2
											.getString("plancode");
									String releasedate = "";
									// 新增字段
									try {
										releasedate = obj2
												.getString("releasedate");
									} catch (Exception e) {
										// TODO Auto-generated catch block
										e.printStackTrace();
									}
									// 修改任务
									DBUtil.updateLPlan(locationHelper
											.getWritableDatabase(), plancode,
											plandate, lon, lat, location,
											remark, type, title, releasedate,
											planid);
								}
								if ("3".equals(type)) {
									HashMap<String, Object> tmpMap = DBUtil.getDataFromLPlanByID(
											locationHelper
													.getWritableDatabase(),
											planid);
									if (tmpMap.get("resultCode").equals("0")) {
										DBUtil.deleteLPlan(locationHelper
												.getWritableDatabase(), planid);
									}
								}
							}
							IUtil.writeSharedPreference(sp, "planupdatecode",
									updatecode);
						}
						msg.what = 1;
					}

				} catch (Exception e) {
					FileLog.e(TAG, "getPlanFromServer==>" + e.toString());
				} finally {
					handler.sendMessage(msg);
				}

			}
		});
		t.start();
	}

	private int notification_id = 20120001;
	NotificationManager nm;

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try {
				if (nm != null) {

					nm.cancel(notification_id);
				}

				switch (msg.what) {
				case 0:
					// 没有数据变化
					loading.setVisibility(View.GONE);
					break;
				case 1:
					init1(Contant.PLAN_NOT_DO);
					loading.setVisibility(View.GONE);
					break;
				default:
					break;
				}

			} catch (Exception e) {
			}
		}
	};

	private class OnBtBackClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				PlanActivity.this.finish();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	private OnClickListener onClickListener = new OnClickListener() {
		int startX;

		@Override
		public void onClick(View v) {
			// avg_width = findViewById(R.id.layout).getWidth();
			switch (v.getId()) {
			case R.id.tv_title_bar_not_do:
				// MoveBg.moveFrontBg(tv_front, startX, 0, 0, 0);
				// startX = 0;
				// tv_front.setText(R.string.task_not_do);
				// tv_bar_not_do.setTextSize(16);
				// tv_bar_not_do.setTextColor(Color.BLACK);
				// tv_bar_has_do.setTextSize(14);
				// tv_bar_has_do.setTextColor(Color.GRAY);
				tv_bar_not_do.setBackgroundResource(R.drawable.plan_no_nor);
				tv_bar_has_do.setBackgroundResource(R.drawable.plan_has_pre);
				init(Contant.PLAN_NOT_DO);
				PlanActivity.this.plan_do = Contant.PLAN_NOT_DO;
				break;
			case R.id.tv_title_bar_has_do:
				// MoveBg.moveFrontBg(tv_front, startX, avg_width, 0, 0);
				// startX = avg_width;
				// tv_front.setText(R.string.task_has_do);
				// tv_bar_not_do.setTextSize(14);
				// tv_bar_not_do.setTextColor(Color.GRAY);
				// tv_bar_has_do.setTextSize(16);
				// tv_bar_has_do.setTextColor(Color.BLACK);
				tv_bar_not_do.setBackgroundResource(R.drawable.plan_no_pre);
				tv_bar_has_do.setBackgroundResource(R.drawable.plan_has_nor);
				init(Contant.PLAN_HAS_DO);
				PlanActivity.this.plan_do = Contant.PLAN_HAS_DO;
				break;

			default:
				break;
			}

		}
	};

	private class OnItemClickListenerImpl implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long id) {
			/*
			 * 点击列表项时触发onItemClick方法，四个参数含义分别为 arg0：发生单击事件的AdapterView
			 * arg1：AdapterView中被点击的View position：当前点击的行在adapter的下标 id：当前点击的行的id
			 */
			if(plan_do.equals(Contant.PLAN_HAS_DO)){
				isOnStart = false;	
			}
			String planId = mData.get(position).get("planId").toString();
			String res = mData.get(position).get("resultCode").toString();

			// 跳转到查看页面
			Intent intent = new Intent(PlanActivity.this,
					PlanViewActivity.class);
			intent.putExtra("planId", planId);
			startActivity(intent);

		}
	}

	private class OnItemLongClickListenerImpl implements
			OnItemLongClickListener {
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				final int position, long id) {
			new AlertDialog.Builder(PlanActivity.this)
					.setTitle(Contant.OP)
					.setItems(R.array.planarrcontent,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									String[] PK = getResources()
											.getStringArray(
													R.array.planarrcontent);
									if (PK[which].equals(Contant.OP_DEL)) {
										String planId = mData.get(position)
												.get("planId").toString();
										if (PlanActivity.this.plan_do
												.equals(Contant.PLAN_NOT_DO)) {
											// 不能删除
											Toast.makeText(PlanActivity.this,
													Contant.OP_CANNOT_DEL,
													Toast.LENGTH_SHORT).show();
										} else {
											DBUtil.deleteLPlan(locationHelper
													.getWritableDatabase(),
													planId);
											mData.remove(position);
											listItemAdapter = (SimpleAdapter) lv
													.getAdapter();
											// if (!listItemAdapter.isEmpty()) {
											listItemAdapter
													.notifyDataSetChanged(); // 实现数据的实时刷新
											// }
											// Toast.makeText(PlanActivity.this,
											// PK[which]+Contant.OP_SUCC,
											// Toast.LENGTH_SHORT)
											// .show();
											dialog(PlanActivity.this, PK[which]
													+ Contant.OP_SUCC);
										}
									}
									if (PK[which].equals(Contant.OP_VIEW)) {
										String planId = mData.get(position)
												.get("planId").toString();
										// 跳转到查看页面
										if(plan_do.equals(Contant.PLAN_HAS_DO)){
											isOnStart = false;	
										}
										Intent intent = new Intent(
												PlanActivity.this,
												PlanViewActivity.class);
										intent.putExtra("planId", planId);
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
