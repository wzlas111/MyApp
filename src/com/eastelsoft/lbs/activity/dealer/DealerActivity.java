package com.eastelsoft.lbs.activity.dealer;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.Header;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.bean.DealerDto;
import com.eastelsoft.lbs.bean.DealerDto.DealerBean;
import com.eastelsoft.lbs.db.DealerDBTask;
import com.eastelsoft.lbs.entity.SetInfo;
import com.eastelsoft.lbs.widget.DealerListView;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.IUtil;
import com.eastelsoft.util.http.HttpRestClient;
import com.eastelsoft.util.http.URLHelper;
import com.eastelsoft.util.settinghelper.SettingUtility;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

//http://58.240.63.104/managermobile.do?reqCode=custList
//http://58.240.63.104/managermobile.do?reqCode=custDetail&id=330106000209498
public class DealerActivity extends BaseActivity implements TextWatcher {

	public static final String TAG = "DealerActivity";
	
	private List<DealerBean> mList;
	private List<DealerBean> mFilterList;
	private DealerListView mListView;
	private DealerAdapter mAdapter;
	private String mSearchStr;
	private SearchTask mSearchTask;
	private boolean isSearchMode = false;
	private boolean need_update = false;
	
	private EditText mSearchEt;
	private View mLoadingView;
	private View mBackBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_dealer);
		
		initViews();
		new InitDBDataTask().execute("");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		if (mDataThread != null) {
			System.out.println("dealer list interrupt");
			mDataThread.interrupt();
			mDataThread = null;
		}
	}
	
	private void initViews() {
		mListView = (DealerListView)findViewById(R.id.listview);
		mSearchEt = (EditText)findViewById(R.id.search_text);
		mLoadingView = findViewById(R.id.circle_progress_bar);
		mBackBtn = findViewById(R.id.btBack);
		
		mBackBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		
		mSearchEt.addTextChangedListener(this);
		
		mList = new ArrayList<DealerBean>();
		mFilterList = new ArrayList<DealerBean>();
		mAdapter = new DealerAdapter(this, mList);
		mListView.createScroller();
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(DealerActivity.this, DealerDetailActivity.class);
				DealerBean bean = null;
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
				mList = DealerDBTask.getBeanList();
			} catch (Exception e) {
				e.printStackTrace();
				mList = new ArrayList<DealerBean>();
			}
			
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			mLoadingView.setVisibility(View.GONE);
			
			isSearchMode = false;
			mAdapter = new DealerAdapter(DealerActivity.this, mList);
			mListView.setAdapter(mAdapter);
			
			initDataTask();
		}
	}
	
	private DealerDto dealerDto;
	private DataThread mDataThread;
	private void initDataTask() {
		sp = getSharedPreferences("userdata", 0);
		SetInfo set = IUtil.initSetInfo(sp);
		String updatecode = SettingUtility.getUpdatecodeValue(SettingUtility.DEALER_UPDATECODE);
		FileLog.i(TAG, TAG+".updatecode: "+updatecode);
		String mUrl = URLHelper.TEST_ACTION;
		RequestParams params = new RequestParams();
		params.put("reqCode", "DealerUpdateActionJk");
		params.put("GpsId", set.getDevice_id());
		params.put("code", updatecode);
		params.put("Pin", "111111");
		HttpRestClient.get(mUrl, params, new TextHttpResponseHandler() {
			
			@Override
			public void onStart() {
				super.onStart();
				mLoadingView.setVisibility(View.VISIBLE);
				FileLog.i(TAG, TAG+"开始进行经销商数据更新.");
			}
			
			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
				FileLog.i(TAG, TAG+"经销商数据数据下载成功.data: "+responseString);
				Message msg = new Message();
				msg.what = 0;
				msg.obj = responseString;
				mHandler.sendMessage(msg);
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				FileLog.i(TAG, TAG+"经销商数据数据下载失败.");
				mLoadingView.setVisibility(View.GONE);
				Toast.makeText(DealerActivity.this, "sorry,数据下载失败,请稍后再试.", Toast.LENGTH_SHORT).show();
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
				dealerDto = gson.fromJson(responseString, DealerDto.class);
				if ("1".equals(dealerDto.resultcode)) { //load from net
					FileLog.i(TAG, TAG+"经销商数据数据下载:版本号不同，更新数据库.");
					need_update = true;
					mList = dealerDto.clientdata;
					insertDB();// delete and insert
					SettingUtility.setValue(SettingUtility.DEALER_UPDATECODE, dealerDto.updatecode);
				} else {
					FileLog.i(TAG, TAG+"经销商数据数据下载:版本号相同，无需更新.");
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
				mAdapter = new DealerAdapter(DealerActivity.this, mList);
				mListView.setAdapter(mAdapter);
				mLoadingView.setVisibility(View.GONE);
				break;
			}
		};
	};
	
	/**
	 * 录入数据库
	 */
	private void insertDB() {
		DealerDBTask.deleteAll();
		DealerDBTask.addBeanList(mList);
	}
	
	private class SearchTask extends AsyncTask<String, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			mFilterList.clear();
			String keyword = params[0];
			for (DealerBean bean : mList) {
				boolean isPinyin = bean.first_py.indexOf(keyword) > -1;
				boolean isZhongwen = bean.dealer_name.indexOf(keyword) > -1;
				if (isPinyin || isZhongwen) {
					mFilterList.add(bean);
				}
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			System.out.println("search : new PersonAdapter");
			isSearchMode = true;
			DealerAdapter adapter = new DealerAdapter(DealerActivity.this, mFilterList);
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
}
