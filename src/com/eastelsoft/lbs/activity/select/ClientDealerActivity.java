package com.eastelsoft.lbs.activity.select;

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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.activity.client.ClientAddActivity;
import com.eastelsoft.lbs.activity.dealer.DealerActivity;
import com.eastelsoft.lbs.activity.dealer.DealerAdapter;
import com.eastelsoft.lbs.activity.dealer.DealerDetailActivity;
import com.eastelsoft.lbs.bean.DealerDto;
import com.eastelsoft.lbs.bean.SelectBean;
import com.eastelsoft.lbs.bean.DealerDto.DealerBean;
import com.eastelsoft.lbs.db.ClientDBTask;
import com.eastelsoft.lbs.db.DealerDBTask;
import com.eastelsoft.lbs.widget.DealerListView;
import com.eastelsoft.util.http.HttpRestClient;
import com.eastelsoft.util.pinyin.PinYinComparator;
import com.eastelsoft.util.pinyin.PinyinUtil;
import com.eastelsoft.util.settinghelper.SettingUtility;
import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;

public class ClientDealerActivity extends BaseActivity implements TextWatcher {
	
	private String mId;
	private List<DealerBean> mList;
	private List<DealerBean> mFilterList;
	private DealerListView mListView;
	private DealerAdapter mAdapter;
	private String mSearchStr;
	private SearchTask mSearchTask;
	private boolean isSearchMode = false;
	
	private EditText mSearchEt;
	private View mBackBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		mId = intent.getStringExtra("id");
		
		setContentView(R.layout.widget_select_client_dealer);
		
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
	}
	
	private void initViews() {
		mListView = (DealerListView)findViewById(R.id.listview);
		mSearchEt = (EditText)findViewById(R.id.search_text);
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
				Intent intent = new Intent(ClientDealerActivity.this, ClientAddActivity.class);
				DealerBean bean = null;
				if (!isSearchMode) {
					bean = mList.get(position);
				} else {
					bean = mFilterList.get(position);
				}
				intent.putExtra("checked_id", bean.id);
				intent.putExtra("checked_name", bean.name);
				setResult(1, intent);
				finish();
			}
		});
	}
	
	private class InitDBDataTask extends AsyncTask<String, Integer, Boolean> { 
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
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
			isSearchMode = false;
			mAdapter = new DealerAdapter(ClientDealerActivity.this, mList);
			mListView.setAdapter(mAdapter);
		}
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
			isSearchMode = true;
			DealerAdapter adapter = new DealerAdapter(ClientDealerActivity.this, mFilterList);
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
