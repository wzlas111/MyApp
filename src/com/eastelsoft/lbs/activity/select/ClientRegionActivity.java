package com.eastelsoft.lbs.activity.select;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.activity.client.ClientAddActivity;
import com.eastelsoft.lbs.bean.SelectBean;
import com.eastelsoft.lbs.db.ClientDBTask;

public class ClientRegionActivity extends BaseActivity {
	
	private String mId;
	private String mCurrentId;
	private String mCurrentName = "";
	private List<SelectBean> mList;
	
	private Button mBackBtn;
	private ListView mListView;
	private SelectAdapter mAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		mId = intent.getStringExtra("id");
		
		setContentView(R.layout.widget_select_client_type);
		initView();
		getRegionList("1");
	}
	
	private void initView() {
		mBackBtn = (Button)findViewById(R.id.btBack);
		mBackBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(3);
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
				if ("全部".equals(bean.name)) {
					Intent intent = new Intent(ClientRegionActivity.this, ClientAddActivity.class);
					intent.putExtra("checked_id", bean.id);
					intent.putExtra("checked_name", mCurrentName);
					setResult(3, intent);
					finish();
				}
				mCurrentId = bean.id;
				mCurrentName += bean.name + " ";
				getRegionList(bean.id);
			}
		});
	}
	
	private void getRegionList(String params) {
		try {
			mList = ClientDBTask.getRegionList(params);
		} catch (Exception e) {
			mList = new ArrayList<SelectBean>();
		}
		if (mList==null||mList.size()==0){
			Intent intent = new Intent(ClientRegionActivity.this, ClientAddActivity.class);
			intent.putExtra("checked_id", mCurrentId);
			intent.putExtra("checked_name", mCurrentName);
			setResult(3, intent);
			finish();
		}
		mAdapter = new SelectAdapter(ClientRegionActivity.this, mList, mId);
		mListView.setAdapter(mAdapter);
	}
	
}
