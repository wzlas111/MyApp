package com.eastelsoft.lbs.activity.client;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.PopupWindow.OnDismissListener;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.bean.ClientDto;
import com.eastelsoft.lbs.bean.ClientDto.ClientBean;
import com.eastelsoft.lbs.db.ClientDBTask;
import com.eastelsoft.lbs.entity.SetInfo;
import com.eastelsoft.lbs.widget.ClientListView;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.GlobalVar;
import com.eastelsoft.util.IUtil;
import com.eastelsoft.util.http.HttpRestClient;
import com.eastelsoft.util.http.URLHelper;
import com.eastelsoft.util.settinghelper.SettingUtility;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

public class ClientActivity extends BaseActivity implements TextWatcher {
	
	public static final String TAG = "ClientActivity";
	
	private List<ClientBean> mList;
	private List<ClientBean> mFilterList;
	private ClientListView mListView;
	private ClientAdapter mAdapter;
	private String mSearchStr;
	private SearchTask mSearchTask;
	private boolean isSearchMode = false;
	private boolean need_update = true;
	
	private EditText mSearchEt;
	private View mLoadingView;
	private View mBackBtn;
	private View mAddBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_client);
		
		initViews();
		new InitDBDataTask(true).execute("");
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		System.out.println("ClientActivity -> onStart");
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 0:
			if (resultCode == 0) {//本地
				new InitDBDataTask(false).execute("");
			} else if(resultCode == 1) {//服务器
				initDataTask();
			}
			break;
		}
	}
	
	private void initViews() {
		mListView = (ClientListView)findViewById(R.id.listview);
		mSearchEt = (EditText)findViewById(R.id.search_text);
		mLoadingView = findViewById(R.id.circle_progress_bar);
		mBackBtn = findViewById(R.id.btBack);
		mAddBtn = findViewById(R.id.btAdd);
		
		tvTitleCust = (TextView) findViewById(R.id.tvTitleCust);
		ivArrow = (ImageView) findViewById(R.id.ivArrow);
		tvTitleCust.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showPopupWindow();
			}
		});
		
		mBackBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		
		mAddBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(ClientActivity.this, ClientAddActivity.class);
				startActivityForResult(intent, 0);
			}
		});
		
		mSearchEt.addTextChangedListener(this);
		
		mList = new ArrayList<ClientBean>();
		mFilterList = new ArrayList<ClientBean>();
		mAdapter = new ClientAdapter(this, mList);
		mListView.createScroller();
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(ClientActivity.this, ClientDetailActivity.class);
				ClientBean bean = null;
				if (!isSearchMode) {
					bean = mList.get(position);
				} else {
					bean = mFilterList.get(position);
				}
				intent.putExtra("id", bean.id);
				intent.putExtra("need_update", need_update);
				startActivity(intent);
			}
		});
	}
	
	private class InitDBDataTask extends AsyncTask<String, Integer, Boolean> { 
		private boolean loadingNetData;
		public InitDBDataTask(boolean fromNet) {
			loadingNetData = fromNet;
		}
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mLoadingView.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected Boolean doInBackground(String... params) {
			try { //load from db
				//先取数据库数据，若有更新，再读取网络数据
				System.out.println("load from db");
				mList = ClientDBTask.getBeanList();
			} catch (Exception e) {
				System.out.println("客户信息加载失败....");
				mList = new ArrayList<ClientBean>();
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			mLoadingView.setVisibility(View.GONE);
			isSearchMode = false;
			mAdapter = new ClientAdapter(ClientActivity.this, mList);
			mListView.setAdapter(mAdapter);
			if (loadingNetData) {
				initDataTask();
			}
		}
	}
	
	private ClientDto clientDto;
	private DataThread mDataThread;
	private void initDataTask() {
		boolean is_loading = GlobalVar.getInstance().isClient_uploading();
		if (is_loading) {
			FileLog.i(TAG, TAG+"客户新正在下载中...");
			return;
		}
		sp = getSharedPreferences("userdata", 0);
		SetInfo set = IUtil.initSetInfo(sp);
		String updatecode = SettingUtility.getUpdatecodeValue(SettingUtility.CLIENT_UPDATECODE);
		FileLog.i(TAG, TAG+".updatecode: "+updatecode);
		String mUrl = URLHelper.TEST_ACTION;
		RequestParams params = new RequestParams();
		params.put("reqCode", "ClientUpdateActionJk");
		params.put("GpsId", set.getDevice_id());
		params.put("code", updatecode);
		params.put("Pin", "111111");
		HttpRestClient.get(mUrl, params, new TextHttpResponseHandler() {
			
			@Override
			public void onStart() {
				super.onStart();
				mLoadingView.setVisibility(View.VISIBLE);
				FileLog.i(TAG, TAG+"开始进行客户数据更新.");
			}
			
			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
				FileLog.i(TAG, TAG+"客户数据下载成功.data: "+responseString);
				Message msg = new Message();
				msg.what = 0;
				msg.obj = responseString;
				mHandler.sendMessage(msg);
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				System.out.println("dealer list onFailure");
				mLoadingView.setVisibility(View.GONE);
				Toast.makeText(ClientActivity.this, "sorry,数据下载失败,请稍后再试.", Toast.LENGTH_SHORT).show();
			}

		});
	}
	
	private class DataThread extends Thread {
		private String responseString;
		public DataThread(String param) {
			responseString = param;
		}
		@Override
		public void run() {
			try {
				Gson gson = new Gson();
				clientDto = gson.fromJson(responseString, ClientDto.class);
				if ("1".equals(clientDto.resultcode)) { //load from net
					FileLog.i(TAG, TAG+"经销商数据数据下载:版本号不同，更新数据库.");
					need_update = true;
					mList = clientDto.clientdata;
					insertDB();
					SettingUtility.setValue(SettingUtility.CLIENT_UPDATECODE, clientDto.updatecode);
				} else {
					FileLog.i(TAG, TAG+"经销商数据数据下载:版本号相同，无需更新.");
					need_update = false;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			mHandler.sendEmptyMessage(1);
		}
	}
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0: //handle net data
				mDataThread = new DataThread((String)msg.obj);
				mDataThread.start();
				break;
			case 1: //handle success,hide londing.
				mAdapter = new ClientAdapter(ClientActivity.this, mList);
				mListView.setAdapter(mAdapter);
				mLoadingView.setVisibility(View.GONE);
				break;
			case 2: //handle new add data
				mAdapter.notifyDataSetChanged();
				break;
			}
		};
	};
	
	/**
	 * 录入数据库
	 */
	private void insertDB() {
		ClientDBTask.deleteAll();
		ClientDBTask.addBeanList(mList);
	}

	private class SearchTask extends AsyncTask<String, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				mFilterList.clear();
				String keyword = params[0];
				for (ClientBean bean : mList) {
					boolean isPinyin = bean.first_py.indexOf(keyword) > -1;
					boolean isZhongwen = bean.client_name.indexOf(keyword) > -1;
					if (isPinyin || isZhongwen) {
						mFilterList.add(bean);
					}
				}
				return true;
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (!result) {
				return;
			}
			System.out.println("search : new PersonAdapter");
			isSearchMode = true;
			ClientAdapter adapter = new ClientAdapter(ClientActivity.this, mFilterList);
			mListView.setAdapter(adapter);
		}
	}
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		mSearchStr = mSearchEt.getText().toString().trim().toLowerCase();
		if (mSearchTask != null && mSearchTask.getStatus() != AsyncTask.Status.FINISHED) {
			mSearchTask.cancel(true);
		}
		mSearchTask = new SearchTask();
		mSearchTask.execute(mSearchStr);
	}

	@Override
	public void afterTextChanged(Editable s) {}
	
	private TextView tvTitleCust;
	private ImageView ivArrow;
	private LinearLayout layout;
	private ListView listView;
	private String title[] = { "企业共享", "员工私有", "全部" };
	public void showPopupWindow() {
		ivArrow.setImageResource(R.drawable.arrow_up);
		layout = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.dialog, null);
		listView = (ListView) layout.findViewById(R.id.lv_dialog);
		listView.setAdapter(new ArrayAdapter<String>(this, R.layout.text, R.id.tv_text, title));
		popupWindow = new PopupWindow(this);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		popupWindow.setWidth(getWindowManager().getDefaultDisplay().getWidth() / 2);
		popupWindow.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);
		popupWindow.setOutsideTouchable(true);
		popupWindow.setFocusable(true);
		popupWindow.setContentView(layout);
		popupWindow.setBackgroundDrawable(new BitmapDrawable());
		int xPos = popupWindow.getWidth() / 2 - tvTitleCust.getWidth() / 2;
		popupWindow.showAsDropDown(findViewById(R.id.tvTitleCust), -xPos, 6);
		popupWindow.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss() {
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
				switch (arg2) {
				case 0:
					// 查企业共享
					mFilterList.clear();
					for (int i = 0; i < mList.size(); i++) {
						ClientBean bean = mList.get(i);
						String type = bean.type;
						if (!TextUtils.isEmpty(type)) {
							if ("1".equals(type)) {
								mFilterList.add(bean);
							}
						}
					}
					ClientAdapter adapter0 = new ClientAdapter(ClientActivity.this, mFilterList);
					mListView.setAdapter(adapter0);
					isSearchMode = true;
					break;
				case 1:
					// 查员工私有
					mFilterList.clear();
					for (int i = 0; i < mList.size(); i++) {
						ClientBean bean = mList.get(i);
						String type = bean.type;
						if (!TextUtils.isEmpty(type)) {
							if ("3".equals(type)) {
								mFilterList.add(bean);
							}
						}
					}
					ClientAdapter adapter1 = new ClientAdapter(ClientActivity.this, mFilterList);
					mListView.setAdapter(adapter1);
					isSearchMode = true;
					break;
				case 2:
					// 查全部
					ClientAdapter adapter2 = new ClientAdapter(ClientActivity.this, mList);
					mListView.setAdapter(adapter2);
					break;
				}
			}
		});
	}
	
}
