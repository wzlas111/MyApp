package com.eastelsoft.lbs.activity.client;

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
import com.eastelsoft.lbs.bean.ClientDto;
import com.eastelsoft.lbs.bean.ClientDto.ClientBean;
import com.eastelsoft.lbs.db.ClientDBTask;
import com.eastelsoft.lbs.widget.ClientListView;
import com.eastelsoft.util.http.HttpRestClient;
import com.eastelsoft.util.settinghelper.SettingUtility;
import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;

public class ClientActivity extends BaseActivity implements TextWatcher {
	
	private List<ClientBean> mList;
	private List<ClientBean> mFilterList;
	private ClientListView mListView;
	private ClientAdapter mAdapter;
	private String mSearchStr;
	private SearchTask mSearchTask;
	private boolean isSearchMode = false;
	
	private EditText mSearchEt;
	private View mLoadingView;
	private View mBackBtn;
	private View mAddBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_client);
		
		initViews();
		new InitDBDataTask().execute("");
	}
	
	private void initViews() {
		mListView = (ClientListView)findViewById(R.id.listview);
		mSearchEt = (EditText)findViewById(R.id.search_text);
		mLoadingView = findViewById(R.id.circle_progress_bar);
		mBackBtn = findViewById(R.id.btBack);
		mAddBtn = findViewById(R.id.btAdd);
		
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
				startActivity(intent);
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
			
			initDataTask();
		}
	}
	
	private ClientDto clientDto;
	private DataThread mDataThread;
	private void initDataTask() {
		String url = "http://58.240.63.104/managermobile.do?reqCode=custList";
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
					System.out.println("load from net");
					mList = clientDto.data;
					insertDB();
					SettingUtility.setValue(SettingUtility.DEALER_UPDATECODE, clientDto.updatecode);
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
				mAdapter = new ClientAdapter(ClientActivity.this, mList);
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
		ClientDBTask.deleteAll();
		ClientDBTask.addBeanList(mList);
	}

	private class SearchTask extends AsyncTask<String, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			mFilterList.clear();
			String keyword = params[0];
			for (ClientBean bean : mList) {
				boolean isPinyin = bean.py.indexOf(keyword) > -1;
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
}
