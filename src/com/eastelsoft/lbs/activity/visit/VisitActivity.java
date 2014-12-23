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
import android.widget.TextView;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.activity.visit.adapter.VisitAdapter;
import com.eastelsoft.lbs.bean.VisitBean;
import com.eastelsoft.lbs.db.VisitDBTask;

public class VisitActivity extends BaseActivity implements OnClickListener {
	
	private List<VisitBean> mList;
	private VisitAdapter mAdapter;
	
	private ListView mListView;
	private Button mBackBtn;
	private TextView mAddBtn;
	private TextView mAddAfterBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_visit);
		
		initView();
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		new DBCacheTask().execute("");
	}
	
	private void initView() {
		mBackBtn = (Button)findViewById(R.id.btBack);
		mAddBtn = (TextView)findViewById(R.id.add);
		mAddAfterBtn = (TextView)findViewById(R.id.add_additional);
		mListView = (ListView)findViewById(R.id.listview);
		
		mList = new ArrayList<VisitBean>();
		mAdapter = new VisitAdapter(this, mList);
		mListView.setAdapter(mAdapter);
		
		mBackBtn.setOnClickListener(this);
		mAddBtn.setOnClickListener(this);
		mAddAfterBtn.setOnClickListener(this);
		mListView.setOnItemClickListener(new ListViewOnItemClick());
	}
	
	private class ListViewOnItemClick implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			//status: 0-已出发, 1-已到达, 2-提交成功, 3-表单提交中, 4-图片提交中
			VisitBean bean = mList.get(position);
			String visit_id = bean.id;
			String visit_status = bean.status;
			Intent intent;
			if ("0".equals(visit_status)) {//go to VisitArriveActivity
				intent = new Intent(VisitActivity.this, VisitArriveActivity.class);
				intent.putExtra("id", visit_id);
				startActivity(intent);
			} else if("1".equals(visit_status)) {//go to VisitFinishActivity
				intent = new Intent(VisitActivity.this, VisitFinishActivity.class);
				intent.putExtra("id", visit_id);
				intent.putExtra("type", "add");
				startActivity(intent);
			} else {//commit,go to detail
				intent = new Intent(VisitActivity.this, VisitFinishActivity.class);
				intent.putExtra("id", visit_id);
				intent.putExtra("type", "detail");
				startActivity(intent);
			}
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
				mList = VisitDBTask.getBeanList();
			} catch (Exception e) {
				System.out.println("信息加载失败....");
				mList = new ArrayList<VisitBean>();
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			mAdapter = new VisitAdapter(VisitActivity.this, mList);
			mListView.setAdapter(mAdapter);
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btBack:
			finish();
			break;
		case R.id.add:
			Intent intent = new Intent(this, VisitStartActivity.class);
			startActivity(intent);
			break;
		case R.id.add_additional:
			Intent intent2 = new Intent(this, VisitAdditionalActivity.class);
			startActivity(intent2);
			break;
		}
	}
}
