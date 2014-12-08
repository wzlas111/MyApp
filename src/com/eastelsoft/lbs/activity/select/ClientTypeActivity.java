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

public class ClientTypeActivity extends BaseActivity {
	
	private String mId;
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
		
		initData();
		initView();
	}
	
	private void initView() {
		mBackBtn = (Button)findViewById(R.id.btBack);
		mBackBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(2);
				finish();
			}
		});
		
		mListView = (ListView)findViewById(R.id.listview);
		mAdapter = new SelectAdapter(this, mList, mId);
		mListView.setAdapter(mAdapter);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				SelectBean bean = mList.get(position);
				Intent intent = new Intent(ClientTypeActivity.this, ClientAddActivity.class);
				intent.putExtra("checked_id", bean.id);
				intent.putExtra("checked_name", bean.name);
				setResult(2, intent);
				finish();
			}
		});
	}
	
	private void initData() {
		mList = new ArrayList<SelectBean>();
		SelectBean bean = new SelectBean();
		bean.id = "0";
		bean.name = "企业共享";
		mList.add(bean);
		SelectBean bean1 = new SelectBean();
		bean1.id = "1";
		bean1.name = "员工私有";
		mList.add(bean1);
	}
}
