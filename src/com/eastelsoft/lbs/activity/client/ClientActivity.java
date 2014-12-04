package com.eastelsoft.lbs.activity.client;

import java.util.List;

import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.bean.ClientDto.ClientBean;
import com.eastelsoft.lbs.widget.ClientListView;

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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_client);
	}

	private class SearchTask extends AsyncTask<String, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			mFilterList.clear();
			String keyword = params[0];
			for (ClientBean bean : mList) {
				boolean isPinyin = bean.py.indexOf(keyword) > -1;
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
