package com.eastelsoft.lbs.activity.dealer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.http.Header;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
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
import com.eastelsoft.lbs.widget.DealerListView;
import com.eastelsoft.util.http.HttpRestClient;
import com.eastelsoft.util.pinyin.PinYinComparator;
import com.eastelsoft.util.pinyin.PinyinUtil;
import com.eastelsoft.util.settinghelper.SettingUtility;
import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;

//http://58.240.63.104/managermobile.do?reqCode=custList
//http://58.240.63.104/managermobile.do?reqCode=custDetail&id=330106000209498
public class DealerActivity extends BaseActivity implements TextWatcher {

	private List<DealerBean> mList;
	private List<DealerBean> mFilterList;
	private DealerListView mListView;
	private DealerAdapter mAdapter;
	private String mSearchStr;
	private SearchTask mSearchTask;
	private boolean isSearchMode = false;
	
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
		String url = "http://58.240.63.104/test/dealer_list.json";
		HttpRestClient.get(url, null, new TextHttpResponseHandler() {
			
			@Override
			public void onStart() {
				super.onStart();
				mLoadingView.setVisibility(View.VISIBLE);
				System.out.println("dealer list onStart");
			}
			
			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
				System.out.println("dealer list onSuccess");
				Message msg = new Message();
				msg.what = 0;
				msg.obj = responseString;
				mHandler.sendMessage(msg);
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				System.out.println("dealer list onFailure");
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
					System.out.println("load from net");
					mList = dealerDto.data;
					transferPinyin();
					insertDB();
					SettingUtility.setValue(SettingUtility.DEALER_UPDATECODE, dealerDto.updatecode);
				} 
			} catch (Exception e) {
				e.printStackTrace();
			}
			mHandler.sendEmptyMessage(1);
			System.out.println("dealer data handle success.");
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
	 * 拼音排序
	 */
	private void transferPinyin() {
		for (DealerBean bean : mList) {
			String pinyin = PinyinUtil.getPinYin(bean.name);
			if (!TextUtils.isEmpty(pinyin)) {
				bean.py_name = pinyin;
				bean.py_index = pinyin.substring(0, 1).toUpperCase();
				if (!Character.isLetter(pinyin.charAt(0))) {
					bean.py_name = "#";
					bean.py_index = "#";
				}
			} else {
				bean.py_name = "#";
				bean.py_index = "#";
			}
		}
		Collections.sort(mList, new PinYinComparator());
	}
	
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
				boolean isPinyin = bean.py_name.indexOf(keyword) > -1;
				boolean isZhongwen = bean.name.indexOf(keyword) > -1;
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
