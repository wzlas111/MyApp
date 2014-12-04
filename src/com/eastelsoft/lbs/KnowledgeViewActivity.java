package com.eastelsoft.lbs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.adapter.BulletinListAnpendixAdapter;
import com.eastelsoft.lbs.adapter.SalesReportAddAdapterA;
import com.eastelsoft.lbs.db.DBUtil;
import com.eastelsoft.lbs.db.LocationSQLiteHelper;
import com.eastelsoft.lbs.entity.BulletinAnpendixBean;
import com.eastelsoft.lbs.entity.BulletinBean;
import com.eastelsoft.lbs.entity.SetInfo;

import com.eastelsoft.util.Contant;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.IUtil;
import com.eastelsoft.util.Util;

import com.eastelsoft.util.contact.OpenFile;
import com.eastelsoft.util.contact.Utility;
import com.eastelsoft.util.http.AndroidHttpClient;

public class KnowledgeViewActivity extends BaseActivity {
	public static final String TAG = "BulletinviewActivity";

	private String cid;
	private String isread;
	private String k_code;
	private LocationSQLiteHelper locationHelper;
	private BulletinBean btb = new BulletinBean();
	// 附近路径 大小 标题
	private String[] appendixs = new String[0];
	private String[] appendix_titles = new String[0];
	private String[] appendix_sizes = new String[0];
	private ArrayList<BulletinAnpendixBean> bals = new ArrayList<BulletinAnpendixBean>();

	private ListView anpendixlist;
	private BulletinListAnpendixAdapter listadpter;
	private Button btBack;
	private TextView b_name;
	private TextView b_remark;
	//private TextView b_fail_date;
	// 加载条
	protected LinearLayout loading;
	// 内容项
	protected LinearLayout ui;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.knewledgebase_view);

		btBack = (Button) findViewById(R.id.btBack);
		btBack.setOnClickListener(new OnBtBackClickListenerImpl());
		b_name = (TextView) this.findViewById(R.id.b_name);
		b_remark = (TextView) this.findViewById(R.id.b_remark);
		//b_fail_date = (TextView) this.findViewById(R.id.b_fail_date);
		anpendixlist = (ListView) findViewById(R.id.anpendixlist);
		anpendixlist.setOnItemClickListener(new OnItemClickListenerImpl());
		listadpter = new BulletinListAnpendixAdapter(
				KnowledgeViewActivity.this, bals);
		anpendixlist.setAdapter(listadpter);
		Utility.setListViewHeightBasedOnChildren(anpendixlist);

		// 获取传入的id参数
		Intent intent = getIntent();
		cid = intent.getStringExtra("info_auto_id");
		isread = intent.getStringExtra("is_read");
		k_code = intent.getStringExtra("k_code");
		loading = (LinearLayout) this.findViewById(R.id.loading);
		ui = (LinearLayout) this.findViewById(R.id.ui);

		loading.setVisibility(View.VISIBLE);
		ui.setVisibility(View.GONE);

		// 打开数据库
		locationHelper = new LocationSQLiteHelper(this, null, null, 5);
		sp = getSharedPreferences("userdata", 0);
		File tempfile = new File("/sdcard"
				+ Contant.SDCARD_ANPENDIX_PATH_NOLINE);
		if (!tempfile.exists()) {
			tempfile.mkdirs();
		}

		if (isread != null) {

			// 已读的在本地获取，未读的在服务器上获取
			Thread thread = new Thread(new InitThread());
			thread.start();

			

		}

	}

	String theroad;
	String thesize;
	String thename;

	private class OnItemClickListenerImpl implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long id) {
			if (Environment.getExternalStorageState().equals(
					Environment.MEDIA_MOUNTED)) {
				theroad = bals.get(position).getAppendix();
				thesize = bals.get(position).getAppendix_size();
				thename = bals.get(position).getAppendix_title();
				String df = theroad+"["+thesize+"["+thename;
				
				File tempfile = new File("/sdcard"
						+ Contant.SDCARD_ANPENDIX_PATH + thename);
				String sm = String.valueOf(tempfile.length());
				if (tempfile.exists()
						&& String.valueOf(tempfile.length()).equals(thesize)) {
					// 直接打开
					Util.playfile("/sdcard" + Contant.SDCARD_ANPENDIX_PATH
							+ thename, KnowledgeViewActivity.this);

				} else {
					// 下载
					showDialog(PROCESS_DIALOG);

					Thread thread = new Thread(new DownloadThread()); // -1表示视频
					thread.start();

				}
			} else {
				Toast.makeText(KnowledgeViewActivity.this,
						getString(R.string.noSDCard), Toast.LENGTH_SHORT)
						.show();

			}

		}
	}

	public class DownloadThread extends Thread {

		@Override
		public void run() {
			String filepath = null;
			try {
				sp = getSharedPreferences("userdata", 0);
				SetInfo set = IUtil.initSetInfo(sp);
				String stp = set.getHttpip() + theroad;
				filepath = download(stp);
			} catch (Exception e) {
				Log.d(TAG, "download......................." + e);
			}
			Message msg = handler.obtainMessage();
			msg.what = 5;
			msg.obj = filepath;
			handler.sendMessage(msg);

		}

		private String download(String url) throws Exception {
			String str = url;

			str = AndroidHttpClient.download(KnowledgeViewActivity.this, url,
					thename);

			return str;
		}

	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 0:
				// 本地数据

				btb = DBUtil.getDataFromLknowledgebasedetailById(
						locationHelper.getWritableDatabase(), cid);
				display();

				break;
			case 1:
				// 网络数据
				if (btb == null) {
				} else {
					// 更新到数据库
					// DBUtil.updateLbulletin(
					// locationHelper.getWritableDatabase(),
					// btb.getB_code(), btb.getB_name(),
					// btb.getB_release_date(), btb.getB_fail_date(),
					// btb.getB_remark(), btb.getB_type(),
					// btb.getU_name(), btb.getB_appendix(),
					// btb.getB_appendix_title(),
					// btb.getB_appendix_size(), cid);
					// localContentValues.put("b_id", parms[0]);
					// localContentValues.put("b_name", parms[1]);
					// localContentValues.put("b_fail_date", parms[2]);
					// localContentValues.put("b_remark", parms[3]);
					// localContentValues.put("b_appendix", parms[4]);
					// localContentValues.put("b_appendix_title", parms[5]);
					// localContentValues.put("b_appendix_size", parms[6]);
					// 先删除 再增加
					DBUtil.deleteLknowledgeDetail(
							locationHelper.getWritableDatabase(), cid);
					DBUtil.insertLknowledgeDetail(
							locationHelper.getWritableDatabase(), cid,
							btb.getB_name(), btb.getB_fail_date(),
							btb.getB_remark(), btb.getB_appendix(),
							btb.getB_appendix_title(), btb.getB_appendix_size());
					String updatecode = (String) msg.obj;
					//修改主表版本号
					DBUtil.updateLknowledgebasecode(locationHelper.getWritableDatabase(), updatecode,cid);
					
					
				}
				display();

				break;
			case 5:

				String obj = (String) msg.obj;
				dismissDialog(PROCESS_DIALOG);

				Util.playfile(obj, KnowledgeViewActivity.this);

				break;
			case 6:
				if ("1".equals(resultcode)) {
					// 未读改为已读
					// 更新到数据库
					DBUtil.updateLknowledgebaseToRead(
						locationHelper.getWritableDatabase(), "1", cid);

				}

				break;

			}
		}

	};

	class InitThread implements Runnable {
		@Override
		public void run() {

			Message msg = handler.obtainMessage();
			//
			msg.what = 0;
			try {

				sp = getSharedPreferences("userdata", 0);
				SetInfo set = IUtil.initSetInfo(sp);
				// k_code
				// 先发消息检是否需要更新
				String url = set.getHttpip()
						+ Contant.KNOWLEDGEDETAIL_UPDATE_ACTION;
				url += "&actiontype=1";
				url += "&gpsid=" + set.getDevice_id();
				url += "&pin=" + set.getAuth_code();
				url += "&k_id=" + cid;
				String jsonStr = AndroidHttpClient.getContent(url);
				jsonStr = IUtil.chkJsonStr(jsonStr);
				String updatecode = "";
				JSONArray array = new JSONArray(jsonStr);
				if (array.length() > 0) {
					JSONObject obj = array.getJSONObject(0);
					updatecode = obj.getString("k_code");
				}

				// 如果需要更新
				if (!k_code.equals(updatecode)) {
					String urls = set.getHttpip() + Contant.ACTION;
					Map<String, String> map = new HashMap<String, String>();
					map.put("reqCode", Contant.KNOWLEDGEDETAILDETAIL_ACTION);
					map.put("actiontype", "2");
					map.put("gpsid", set.getDevice_id());
					map.put("pin", set.getAuth_code());
					map.put("k_id", cid);

					String jsonStr1 = AndroidHttpClient.getContent(urls, map);
					FileLog.i(TAG, "jsonStr1==>" + jsonStr1);
					jsonStr1 = IUtil.chkJsonStr(jsonStr1);
					JSONArray array1 = new JSONArray(jsonStr1);
					if (array1.length() > 0) {
						JSONObject obj2 = array1.getJSONObject(0);

						// {"k_fail_date":"2014-03-08","k_remark":"sss","k_appendix":
						// "uploaddata/knowledgeFile/10000139_6566db8aa8b64382bbfef829aa5cd5ae.xls|",
						// "k_id":"3e8c8a1280868b5a5955ed3759a53ad0","k_code":3,"k_appendix_size":"13824|",
						// "k_appendix_title":"今夜深了.xls|","k_name":"sss"}
						BulletinBean tp = new BulletinBean();
						tp.setB_code(obj2.getString("k_code"));
						tp.setB_name(obj2.getString("k_name"));

						tp.setB_fail_date(obj2.getString("k_fail_date"));
						tp.setB_remark(obj2.getString("k_remark"));

						tp.setB_appendix(obj2.getString("k_appendix"));
						tp.setB_appendix_title(obj2
								.getString("k_appendix_title"));
						tp.setB_appendix_size(obj2.getString("k_appendix_size"));

						btb = tp;

					}
					msg.obj = updatecode;
					msg.what = 1;

				}

			} catch (Exception e) {
				msg.what = 0;

			} finally {
				handler.sendMessage(msg);
			}

		}
	}

	/**
	 * 将内容显示到主界面上
	 */
	private void display() {
		loading.setVisibility(View.GONE);
		ui.setVisibility(View.VISIBLE);

		if (btb != null) {
			if (btb.getB_name() != null) {
				b_name.setText(btb.getB_name() != null ? btb.getB_name() : "");
				b_remark.setText(btb.getB_remark() != null ? btb.getB_remark()
						: "");
//				b_fail_date.setText(btb.getB_fail_date() != null ? btb
//						.getB_fail_date() : "");

				if (btb.getB_appendix_title() != null
						&& !"".equals(btb.getB_appendix_title())) {

					appendixs = btb.getB_appendix().split("\\|");
					appendix_sizes = btb.getB_appendix_size().split("\\|");
					appendix_titles = btb.getB_appendix_title().split("\\|");
					if (appendixs.length > 0) {
						for (int i = 0; i < appendixs.length; i++) {

							BulletinAnpendixBean tp1 = new BulletinAnpendixBean();
							tp1.setAppendix(appendixs[i]);
							tp1.setAppendix_title(appendix_titles[i]);
							tp1.setAppendix_size(appendix_sizes[i]);
							bals.add(tp1);
						}

					}

				}
				listadpter = new BulletinListAnpendixAdapter(
						KnowledgeViewActivity.this, bals);
				anpendixlist.setAdapter(listadpter);
				Utility.setListViewHeightBasedOnChildren(anpendixlist);

				// 上报已读消息到服务器
				if ("0".equals(isread)) {
					Thread thread = new Thread(new UpdateReadThread());
					thread.start();
				}

			}

		}

	}

	private class OnBtBackClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				KnowledgeViewActivity.this.finish();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	String b_date = "";
	String resultcode = "";

	class UpdateReadThread implements Runnable {
		@Override
		public void run() {

			Message msg = handler.obtainMessage();
			msg.what = 6;
			try {
				sp = getSharedPreferences("userdata", 0);
				SetInfo set = IUtil.initSetInfo(sp);
				b_date = Util.getLocaleTime("yyyy-MM-dd HH:mm:ss");
				String url = set.getHttpip() + Contant.ACTION;
				Map<String, String> map = new HashMap<String, String>();
				map.put("reqCode", Contant.KNOWLEDGEBASE_READ_ACTION);
				map.put("gpsid", set.getDevice_id());
				map.put("pin", set.getAuth_code());
				map.put("k_id", cid);
				map.put("k_date", b_date);

				String jsonStr1 = AndroidHttpClient.getContent(url, map);
				// {"resultcode":"1"}
				jsonStr1 = IUtil.chkJsonStr(jsonStr1);
				JSONArray array1 = new JSONArray(jsonStr1);
				if (array1.length() > 0) {
					JSONObject obj2 = array1.getJSONObject(0);

					resultcode = obj2.getString("resultcode");

				}

			} catch (Exception e) {

			} finally {
				handler.sendMessage(msg);
			}

		}
	}

}
