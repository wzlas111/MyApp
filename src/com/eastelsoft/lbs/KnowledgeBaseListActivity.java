package com.eastelsoft.lbs;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.eastelsoft.lbs.BulletinviewActivity.DownloadThread;
import com.eastelsoft.lbs.KnowledgeViewActivity.UpdateReadThread;
import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.adapter.BulletinListItemAdapter;
import com.eastelsoft.lbs.adapter.KnowledgeBaseListItemAdapter;
import com.eastelsoft.lbs.db.DBUtil;
import com.eastelsoft.lbs.db.LocationSQLiteHelper;
import com.eastelsoft.lbs.entity.BulletinBean;
import com.eastelsoft.lbs.entity.KnowledgeBean;
import com.eastelsoft.lbs.entity.SetInfo;
import com.eastelsoft.util.Contant;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.GlobalVar;
import com.eastelsoft.util.IUtil;
import com.eastelsoft.util.Util;
import com.eastelsoft.util.http.AndroidHttpClient;

/**
 * 知识库页面
 * 
 * @author lengcj
 */
public class KnowledgeBaseListActivity extends BaseActivity {
	public static final String TAG = "InfoActivity";
	private ListView lv;
	private Button btBack;
	private LocationSQLiteHelper locationHelper;

	// 定义适配器
	private KnowledgeBaseListItemAdapter listadpter;
	private ArrayList<KnowledgeBean> arrayList = new ArrayList<KnowledgeBean>();

	private SetInfo set;
	private Thread dataThread;
	private Thread initThread;

	private int first = 1;

	private Intent intent;
	private String info_auto_id = "";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.knowledgebase_list);
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
		intent = this.getIntent();
		info_auto_id = intent.getStringExtra("info_auto_id");
		File tempfile = new File("/sdcard"
				+ Contant.SDCARD_ANPENDIX_PATH_NOLINE);
		if (!tempfile.exists()) {
			tempfile.mkdirs();
		}
		if ("firstpage".equals(info_auto_id)) {
			init(""); // 初始化本地数据
			if (first == 1) {
				showDialog(PROCESS_DIALOG);
				initThread = new Thread(new InitThread());
				initThread.start();
				first++;
			}

		} else {
			init(info_auto_id);
		}

	}

	private void init(String fatherid) {
		try {
			arrayList.clear();
			arrayList = DBUtil.getDataFromLknowledgebase(
					locationHelper.getWritableDatabase(), fatherid);
			listadpter = new KnowledgeBaseListItemAdapter(
					KnowledgeBaseListActivity.this, arrayList);
			// 填充适配器
			lv.setAdapter(listadpter);

		} catch (Exception e) {
		}
	}

	@Override
	protected void onStart() {
		super.onStart();

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
		init(""); // 初始化本地数据

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
				String knowledgeupdatecode = sp.getString(
						"knowledgeupdatecode", "");
				// 先发消息检是否需要更新
				String url = set.getHttpip() + Contant.KNOWLEDGE_UPDATE_ACTION;
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
				if (!knowledgeupdatecode.equals(updatecode)) {
					// if (true) {
					url = set.getHttpip() + Contant.ACTION;
					Map<String, String> map = new HashMap<String, String>();
					map.put("reqCode", Contant.KNOWLEDGE_ACTION);
					map.put("actiontype", "2");
					map.put("gpsid", set.getDevice_id());
					map.put("pin", set.getAuth_code());
					// String ttts =
					// "{\"knowledgebasedata\":[{\"k_type\":\"0\",\"k_t_id\":\"kt0001\",\"k_t_name\":\"天文知识目录\",\"list\":"
					// +
					// "[{\"k_type\":\"2\",\"k_id\":\"kt0001_k001\",\"k_name\":\"海王星知识\",\"k_appendix\":\"12.ppt\",\"k_appendix_title\":\"海王星PPT附件\",\"k_appendix_size\":\"1024\",\"is_read\":\"0\",\"k_code\":\"1\"}"
					// +
					// ",{\"k_type\":\"3\",\"k_id\":\"kt0001_k002\",\"k_name\":\"冥王星知识\",\"k_appendix\":\"12.doc\",\"k_appendix_title\":\"冥王星DOC附件\",\"k_appendix_size\":\"1024\",\"is_read\":\"1\",\"k_code\":\"1\"}"
					// +
					// ",{\"k_type\":\"4\",\"k_id\":\"kt0001_k008\",\"k_name\":\"冥王星知识\",\"k_appendix\":\"12.doc\",\"k_appendix_title\":\"冥王星DOC附件\",\"k_appendix_size\":\"1024\",\"is_read\":\"1\",\"k_code\":\"1\"}"
					// +
					// ",{\"k_type\":\"6\",\"k_id\":\"kt0001_k009\",\"k_name\":\"冥王星知识\",\"k_appendix\":\"12.doc\",\"k_appendix_title\":\"冥王星DOC附件\",\"k_appendix_size\":\"1024\",\"is_read\":\"1\",\"k_code\":\"1\"}"
					// +
					// ",{\"k_type\":\"13\",\"k_id\":\"kt0001_k013\",\"k_name\":\"冥王星知识\",\"k_appendix\":\"12.doc\",\"k_appendix_title\":\"冥王星DOC附件\",\"k_appendix_size\":\"1024\",\"is_read\":\"1\",\"k_code\":\"1\"}"
					// +
					// ",{\"k_type\":\"1\",\"k_id\":\"kt0001_k003\",\"k_name\":\"科学管理\",\"is_read\":\"1\",\"k_code\":\"1\"},"
					// +
					// "{\"k_type\":\"0\",\"k_t_id\":\"kt0002\",\"k_t_name\":\"地理知识目录\",\"list\":"
					// +
					// "[{\"k_type\":\"5\",\"k_id\":\"kt0002_k002\",\"k_name\":\"南极地理知识\",\"k_appendix\":\"12.pdf\",\"k_appendix_title\":\"南极地理PDF附件\",\"k_appendix_size\":\"1024\",\"is_read\":\"1\",\"k_code\":\"1\"}]}]}"
					// +
					// ",{\"k_type\":\"0\",\"k_t_id\":\"kt0005\",\"k_t_name\":\"天文知识目录\",\"list\":"
					// +
					// "[{\"k_type\":\"2\",\"k_id\":\"kt0005_k001\",\"k_name\":\"海王星知识\",\"k_appendix\":\"12.ppt\",\"k_appendix_title\":\"海王星PPT附件\",\"k_appendix_size\":\"1024\",\"is_read\":\"0\",\"k_code\":\"1\"}"
					// +
					// ",{\"k_type\":\"3\",\"k_id\":\"kt0005_k002\",\"k_name\":\"冥王星知识\",\"k_appendix\":\"12.doc\",\"k_appendix_title\":\"冥王星DOC附件\",\"k_appendix_size\":\"1024\",\"is_read\":\"1\",\"k_code\":\"1\"}"
					// +
					// ",{\"k_type\":\"4\",\"k_id\":\"kt0005_k008\",\"k_name\":\"冥王星知识\",\"k_appendix\":\"12.doc\",\"k_appendix_title\":\"冥王星DOC附件\",\"k_appendix_size\":\"1024\",\"is_read\":\"1\",\"k_code\":\"1\"}"
					// +
					// ",{\"k_type\":\"6\",\"k_id\":\"kt0005_k009\",\"k_name\":\"冥王星知识\",\"k_appendix\":\"12.doc\",\"k_appendix_title\":\"冥王星DOC附件\",\"k_appendix_size\":\"1024\",\"is_read\":\"1\",\"k_code\":\"1\"}"
					// +
					// ",{\"k_type\":\"13\",\"k_id\":\"kt0005_k013\",\"k_name\":\"冥王星知识\",\"k_appendix\":\"12.doc\",\"k_appendix_title\":\"冥王星DOC附件\",\"k_appendix_size\":\"1024\",\"is_read\":\"1\",\"k_code\":\"1\"}"
					// +
					// ",{\"k_type\":\"1\",\"k_id\":\"kt0005_k003\",\"k_name\":\"科学管理\",\"is_read\":\"1\",\"k_code\":\"1\"},"
					// +
					// "{\"k_type\":\"0\",\"k_t_id\":\"kt0009\",\"k_t_name\":\"地理知识目录\",\"list\":"
					// +
					// "[{\"k_type\":\"5\",\"k_id\":\"kt0009_k002\",\"k_name\":\"南极地理知识\",\"k_appendix\":\"12.pdf\",\"k_appendix_title\":\"南极地理PDF附件\",\"k_appendix_size\":\"1024\",\"is_read\":\"1\",\"k_code\":\"1\"}]}]}"
					// + "]" + ",\"updatecode\":\"1\"}";
					String jsonStr1 = AndroidHttpClient.getContent(url, map);
					FileLog.i(TAG, "jsonStr1==>" + jsonStr1);

					// 递归解析
					jsonStr1 = IUtil.chkJsonStr(jsonStr1);
					JSONArray array1 = new JSONArray(jsonStr1);

					if (array1.length() > 0) {
						JSONObject obj = array1.getJSONObject(0);
						updatecode = obj.getString("updatecode");
						if (updatecode != null && !"".equals(updatecode)) {
							// 第一步 删除掉未读的
							DBUtil.deleteLknowledgebaseAll(locationHelper
									.getWritableDatabase());
							JSONArray array2 = obj
									.getJSONArray("knowledgebasedata");
							jiexi(array2, "");

						}
						IUtil.writeSharedPreference(sp, "knowledgeupdatecode",
								updatecode);

					}

					msg.what = 5;

				}

			} catch (Exception e) {
				msg.what = 4;
				FileLog.e(TAG, "getBulletinServer==>" + e.toString());
			} finally {
				handler.sendMessage(msg);
			}

		}
	}

	private void jiexi(JSONArray array2, String fatherid) throws Exception {
		String k_id = "";
		String k_name = "";
		String k_appendix = "";
		String k_appendix_title = "";
		String k_appendix_size = "";
		String is_read = "";
		String k_code = "";
		for (int i = 0; i < array2.length(); i++) {
			JSONObject obj2 = array2.getJSONObject(i);
			String k_type = obj2.getString("k_type");

			if ("0".equals(k_type)) {
				// {\"k_type\":\"0\",\"k_t_id\":\"kt0001\",\"k_t_name\":\"天文知识目录\",\"list\":"
				JSONArray list = obj2.getJSONArray("list");
				k_id = obj2.getString("k_t_id");
				k_name = obj2.getString("k_t_name");
				k_appendix = "";
				k_appendix_title = "";
				k_appendix_size = "";
				is_read = "";
				k_code = "";
				jiexi(list, k_id);
			} else if ("1".equals(k_type)) {
				// {\"k_type\":\"1\",\"k_id\":\"kt0001_k003\",\"k_name\":\"科学管理\",\"is_read\":\"1\",\"k_code\":\"1\"}
				k_id = obj2.getString("k_id");
				k_name = obj2.getString("k_name");
				k_appendix = "";
				k_appendix_title = "";
				k_appendix_size = "";
				is_read = obj2.getString("is_read");
				k_code = "";
			} else {
				// {\"k_type\":\"2\",\"k_id\":\"kt0001_k001\",\"k_name\":\"海王星知识\",\"k_appendix\":\"12.ppt\""
				// +
				// ",\"k_appendix_title\":\"海王星PPT附件\",\"k_appendix_size\":\"1024\",\"is_read\":\"0\",\"k_code\":\"1\"}
				k_id = obj2.getString("k_id");
				k_name = obj2.getString("k_name");
				k_appendix = obj2.getString("k_appendix");
				k_appendix_title = obj2.getString("k_appendix_title");
				k_appendix_size = obj2.getString("k_appendix_size");
				is_read = obj2.getString("is_read");
				k_code = "";

			}
			DBUtil.insertLknowledgebase(locationHelper.getWritableDatabase(),
					k_id, k_name, k_type, fatherid, k_appendix,
					k_appendix_title, k_appendix_size, is_read, k_code);

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
				init("");
				dismissDialog(PROCESS_DIALOG);
				break;
			case 6:

				String obj = (String) msg.obj;
				dismissDialog(PROCESS_DIALOG);

				Util.playfile(obj, KnowledgeBaseListActivity.this);
				// 上报已读消息到服务器
				if ("0".equals(isread)) {
					Thread thread = new Thread(new UpdateReadThread());
					thread.start();
				}

				break;
			case 7:
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

	private class OnBtBackClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				KnowledgeBaseListActivity.this.finish();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	private class OnItemClickListenerImpl implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> arg0, View arg1, int position,
				long id) {
			String k_type = arrayList.get(position).getK_type();
			if ("0".equals(k_type)) {
				String info_auto_id = arrayList.get(position).getK_id();

				ArrayList<KnowledgeBean> ar = DBUtil.getDataFromLknowledgebase(
						locationHelper.getWritableDatabase(), info_auto_id);
				if (ar.size() > 0) {
					// 跳转到查看页面
					Intent intent = new Intent(KnowledgeBaseListActivity.this,
							KnowledgeBaseListActivity.class);
					intent.putExtra("info_auto_id", info_auto_id);

					startActivity(intent);

				} else {

					Toast.makeText(KnowledgeBaseListActivity.this, "此目录下没有内容哦",
							Toast.LENGTH_SHORT).show();
				}

			} else if ("1".equals(k_type)) {

				String info_auto_id = arrayList.get(position).getK_id();
				String is_read = arrayList.get(position).getIs_read();
				String k_code = arrayList.get(position).getK_code();
				// 跳转到查看页面
				Intent intent = new Intent(KnowledgeBaseListActivity.this,
						KnowledgeViewActivity.class);
				intent.putExtra("info_auto_id", info_auto_id);
				intent.putExtra("is_read", is_read);
				intent.putExtra("k_code", k_code);
				startActivity(intent);

			} else {

				if (Environment.getExternalStorageState().equals(
						Environment.MEDIA_MOUNTED)) {
					theroad = arrayList.get(position).getK_appendix();
					thesize = arrayList.get(position).getK_appendix_size();
					thename = arrayList.get(position).getK_appendix_title();
					isread = arrayList.get(position).getIs_read();
					cid = arrayList.get(position).getK_id();
					File tempfile = new File("/sdcard"
							+ Contant.SDCARD_ANPENDIX_PATH + thename);
					String sm = String.valueOf(tempfile.length());
					if (tempfile.exists()
							&& String.valueOf(tempfile.length())
									.equals(thesize)) {
						// 直接打开
						Util.playfile("/sdcard" + Contant.SDCARD_ANPENDIX_PATH
								+ thename, KnowledgeBaseListActivity.this);

						// 上报已读消息到服务器
						if ("0".equals(isread)) {
							Thread thread = new Thread(new UpdateReadThread());
							thread.start();
						}

					} else {
						// 下载
						showDialog(PROCESS_DIALOG);

						Thread thread = new Thread(new DownloadThread()); // -1表示视频
						thread.start();

					}
				} else {
					Toast.makeText(KnowledgeBaseListActivity.this,
							getString(R.string.noSDCard), Toast.LENGTH_SHORT)
							.show();

				}

			}

		}
	}

	String theroad;
	String thesize;
	String thename;

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
			msg.what = 6;
			msg.obj = filepath;
			handler.sendMessage(msg);

		}
	}

	private String download(String url) throws Exception {
		String str = url;

		str = AndroidHttpClient.download(KnowledgeBaseListActivity.this, url,
				thename);

		return str;
	}

	String b_date = "";
	String resultcode = "";
	String cid = "";
	String isread = "";

	class UpdateReadThread implements Runnable {
		@Override
		public void run() {

			Message msg = handler.obtainMessage();
			msg.what = 7;
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
