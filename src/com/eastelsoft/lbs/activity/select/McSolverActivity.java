package com.eastelsoft.lbs.activity.select;

import java.util.ArrayList;
import java.util.List;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.activity.visit.VisitMcRegisterActivity;
import com.eastelsoft.lbs.bean.SelectBean;
import com.eastelsoft.lbs.db.ParamsDBTask;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class McSolverActivity extends Activity {

	private String mId;
	private int mIndex;
	private String mModelId = "";
	private List<SelectBean> mList;
	
	private Button mBackBtn;
	private ListView mListView;
	private SelectAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		mId = intent.getStringExtra("id");
		mIndex = intent.getIntExtra("index", 0);
		mModelId = intent.getStringExtra("model_id");
		
		setContentView(R.layout.widget_select_client_type);
		
		initView();
		
		new InitDataTask().execute("");
	}
	
	private void initView() {
		mBackBtn = (Button)findViewById(R.id.btBack);
		mBackBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(4);
				finish();
			}
		});
		
		mList = new ArrayList<SelectBean>();
		mListView = (ListView)findViewById(R.id.listview);
		mAdapter = new SelectAdapter(this, mList, mId);
		mListView.setAdapter(mAdapter);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				SelectBean bean = mList.get(position);
				Intent intent = new Intent(McSolverActivity.this, VisitMcRegisterActivity.class);
				intent.putExtra("index", mIndex);
				intent.putExtra("checked_id", bean.id);
				intent.putExtra("checked_name", bean.name);
				setResult(1, intent);
				finish();
			}
		});
	}
	
	private class InitDataTask extends AsyncTask<String, Integer, Boolean> {
		@Override
		protected Boolean doInBackground(String... params) {
			try {
				mList = ParamsDBTask.getCommoditySolverList(mModelId);
			} catch (Exception e) {
				e.printStackTrace();
				mList = new ArrayList<SelectBean>();
			}
			return true;
		}
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			mAdapter = new SelectAdapter(McSolverActivity.this, mList, mId);
			mListView.setAdapter(mAdapter);
		}
	}
	
}
