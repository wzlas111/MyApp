package com.eastelsoft.lbs.activity.select;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.activity.client.ClientAdapter;
import com.eastelsoft.lbs.activity.visit.VisitMcAddActivity;
import com.eastelsoft.lbs.bean.ClientDto.ClientBean;
import com.eastelsoft.lbs.db.ClientDBTask;
import com.eastelsoft.lbs.widget.ClientListView;

public class ClientSelectActivity extends BaseActivity implements TextWatcher {
	
	private String mId;
	private List<ClientBean> mList;
	private List<ClientBean> mFilterList;
	private ClientListView mListView;
	private ClientAdapter mAdapter;
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
		
		setContentView(R.layout.widget_select_client);
		
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
		mListView = (ClientListView)findViewById(R.id.listview);
		mSearchEt = (EditText)findViewById(R.id.search_text);
		mBackBtn = findViewById(R.id.btBack);
		
		mBackBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
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
				Intent intent;
				intent = new Intent(ClientSelectActivity.this, VisitMcAddActivity.class);
				
				ClientBean bean = null;
				if (!isSearchMode) {
					bean = mList.get(position);
				} else {
					bean = mFilterList.get(position);
				}
				intent.putExtra("checked_id", bean.id);
				intent.putExtra("checked_name", bean.client_name);
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
				mList = ClientDBTask.getBeanList();
			} catch (Exception e) {
				e.printStackTrace();
				mList = new ArrayList<ClientBean>();
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			isSearchMode = false;
			mAdapter = new ClientAdapter(ClientSelectActivity.this, mList);
			mListView.setAdapter(mAdapter);
		}
	}
	
	private class SearchTask extends AsyncTask<String, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
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
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			isSearchMode = true;
			ClientAdapter adapter = new ClientAdapter(ClientSelectActivity.this, mFilterList);
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
