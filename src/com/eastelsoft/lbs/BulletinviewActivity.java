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

public class BulletinviewActivity extends BaseActivity {
	public static final String TAG = "BulletinviewActivity";

	private String cid;
	private String isread;
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
	private TextView b_release_date;
	private TextView b_type;
	private TextView u_name;
	
	
	
	// 加载条
	protected LinearLayout loading;
	// 内容项
	protected LinearLayout ui;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bulletin_view);

		btBack = (Button) findViewById(R.id.btBack);
		btBack.setOnClickListener(new OnBtBackClickListenerImpl());
		b_name = (TextView) this.findViewById(R.id.b_name);
		b_remark = (TextView) this.findViewById(R.id.b_remark);
		b_release_date = (TextView) this.findViewById(R.id.b_fail_date);
		b_type = (TextView) this.findViewById(R.id.b_type);
		u_name = (TextView) this.findViewById(R.id.u_name);
		anpendixlist = (ListView) findViewById(R.id.anpendixlist);
		anpendixlist.setOnItemClickListener(new OnItemClickListenerImpl());
		listadpter = new BulletinListAnpendixAdapter(BulletinviewActivity.this,
				bals);
		anpendixlist.setAdapter(listadpter);
		Utility.setListViewHeightBasedOnChildren(anpendixlist);

		// 获取传入的id参数
		Intent intent = getIntent();
		cid = intent.getStringExtra("info_auto_id");
		isread = intent.getStringExtra("is_read");
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
			// // 在本地获取
			// btb = DBUtil.getDataFromLbulletinById(
			// locationHelper.getWritableDatabase(), cid);
			// display();
			//
			// } else {
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
				File tempfile = new File("/sdcard"
						+ Contant.SDCARD_ANPENDIX_PATH + thename);
				String sm = String.valueOf(tempfile.length());
				if (tempfile.exists()
						&& String.valueOf(tempfile.length()).equals(thesize)) {
					// 直接打开
					Util.playfile("/sdcard" + Contant.SDCARD_ANPENDIX_PATH + thename,BulletinviewActivity.this);

				} else {
					// 下载
					showDialog(PROCESS_DIALOG);

					Thread thread = new Thread(new DownloadThread()); // -1表示视频
					thread.start();

				}
			} else {
				Toast.makeText(BulletinviewActivity.this,
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

			str = AndroidHttpClient.download(BulletinviewActivity.this, url,
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

				break;
			case 1:
				// 网络数据
				if (btb != null&&btb.getB_id()!=null&&btb.getB_name()!=null) {
				
					// 更新到数据库
					DBUtil.updateLbulletin(
							locationHelper.getWritableDatabase(),
							btb.getB_code(), btb.getB_name(),
							btb.getB_release_date(), btb.getB_fail_date(),
							btb.getB_remark(), btb.getB_type(),
							btb.getU_name(), btb.getB_appendix(),
							btb.getB_appendix_title(),
							btb.getB_appendix_size(), cid);
				}

				display();

				break;
			case 5:

				String obj = (String) msg.obj;
				dismissDialog(PROCESS_DIALOG);
				
				Util.playfile(obj,BulletinviewActivity.this);

				break;
			case 6:
				if ("1".equals(resultcode)) {
					// 未读改为已读
					// 更新到数据库
					DBUtil.updateLbulletinToRead(
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
			msg.what = 1;
			try {
				sp = getSharedPreferences("userdata", 0);
				SetInfo set = IUtil.initSetInfo(sp);
				String url = set.getHttpip() + Contant.ACTION;
				Map<String, String> map = new HashMap<String, String>();
				map.put("reqCode", Contant.BULLETIN_DETAIL_ACTION);
				map.put("actiontype", "2");
				map.put("gpsid", set.getDevice_id());
				map.put("pin", set.getAuth_code());
				map.put("b_id", cid);

				String jsonStr1 = AndroidHttpClient.getContent(url, map);
				FileLog.i(TAG, "jsonStr1==>" + jsonStr1);
				jsonStr1 = IUtil.chkJsonStr(jsonStr1);
				JSONArray array1 = new JSONArray(jsonStr1);
				if (array1.length() > 0) {
					JSONObject obj2 = array1.getJSONObject(0);
					// [{"b_appendix_size":"67584|",
					// "b_appendix":"uploaddata/noticeFile/10000139_891186e0107c47a1a4b1215e30733a78.xls|"
					// ,"b_remark":"就斤斤计较加加减减斤斤计较斤斤计较","b_name":"快乐的生活",
					// "b_code":2,"b_release_date":"2013-11-06 13:45:52","b_appendix_title":"公司最新通讯录.xls|"
					// ,"b_fail_date":"2013-11-15","b_type":"公司公告","u_name":"刘宁011"}]

					BulletinBean tp = new BulletinBean();
					tp.setB_code(obj2.getString("b_code"));
					tp.setB_name(obj2.getString("b_name"));
					tp.setB_release_date(obj2.getString("b_release_date"));
					tp.setB_fail_date(obj2.getString("b_fail_date"));
					tp.setB_remark(obj2.getString("b_remark"));

					tp.setB_type(obj2.getString("b_type"));
					tp.setU_name(obj2.getString("u_name"));
					tp.setB_appendix(obj2.getString("b_appendix"));
					tp.setB_appendix_title(obj2.getString("b_appendix_title"));
					tp.setB_appendix_size(obj2.getString("b_appendix_size"));

					btb = tp;

				}

			} catch (Exception e) {

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

		b_name.setText(btb.getB_name() != null ? btb.getB_name() : "");
		b_remark.setText(btb.getB_remark() != null ? btb.getB_remark() : "");
		b_release_date.setText(btb.getB_release_date() != null ? btb.getB_release_date()
				: "");
		b_type.setText(btb.getB_type() != null ? "类型："+btb.getB_type()
				: "");
		u_name.setText(btb.getU_name() != null ? "发布人："+btb.getU_name()
				: "");
		

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
		// BulletinAnpendixBean tp1 = new BulletinAnpendixBean();
		// tp1.setAppendix("http://www.hyrc.cn/upfile/3/200611/1123539531579.jpg");
		// tp1.setAppendix_title("1111.jpg");
		// tp1.setAppendix_size("164284");
		// bals.add(tp1);
		listadpter = new BulletinListAnpendixAdapter(BulletinviewActivity.this,
				bals);
		anpendixlist.setAdapter(listadpter);
		Utility.setListViewHeightBasedOnChildren(anpendixlist);

		// 上报已读消息到服务器
		if ("0".equals(isread)) {
			Thread thread = new Thread(new UpdateReadThread());
			thread.start();
		}

	}

	private class OnBtBackClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				BulletinviewActivity.this.finish();
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
				map.put("reqCode", Contant.BULLETIN_READ_ACTION);
				map.put("gpsid", set.getDevice_id());
				map.put("pin", set.getAuth_code());
				map.put("b_id", cid);
				map.put("b_date", b_date);

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
