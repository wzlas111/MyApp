package com.eastelsoft.lbs.activity.visit;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.activity.visit.adapter.VisitMcAdapter;
import com.eastelsoft.lbs.bean.VisitMcBean;
import com.eastelsoft.lbs.db.VisitMcDBTask;

public class VisitMcListActivity extends BaseActivity implements OnClickListener {
	
	private String visit_id;
	private List<VisitMcBean> mList;
	private VisitMcAdapter mAdapter;
	
	private ListView mListView;
	private Button mBackBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		visit_id = intent.getStringExtra("id");
		
		setContentView(R.layout.activity_visit_mc_list);
		
		initView();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		new DBCacheTask().execute("");
	}
	
	private void initView() {
		mBackBtn = (Button)findViewById(R.id.btBack);
		mListView = (ListView)findViewById(R.id.listview);
		
		mList = new ArrayList<VisitMcBean>();
		mAdapter = new VisitMcAdapter(this, mList);
		mListView.setAdapter(mAdapter);
		
		mBackBtn.setOnClickListener(this);
		mListView.setOnItemClickListener(new ListViewOnItemClick());
	}
	
	private class ListViewOnItemClick implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			//status: 0-已出发, 1-已到达, 2-提交成功, 3-提交失败
			VisitMcBean bean = mList.get(position);
			System.out.println("bean : "+bean.toString());
		}
		
	}

	private class DBCacheTask extends AsyncTask<String, Integer, Boolean> { 
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
		@Override
		protected Boolean doInBackground(String... params) {
			try { //load from db
				//先取数据库数据，若有更新，再读取网络数据
				System.out.println("load from db");
				mList = VisitMcDBTask.getBeanList(visit_id);
			} catch (Exception e) {
				System.out.println("信息加载失败....");
				mList = new ArrayList<VisitMcBean>();
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			mAdapter = new VisitMcAdapter(VisitMcListActivity.this, mList);
			mListView.setAdapter(mAdapter);
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btBack:
			finish();
			break;
		}
	}
}
