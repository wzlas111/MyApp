package com.eastelsoft.lbs.activity.select;

import java.util.ArrayList;
import java.util.List;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.activity.visit.VisitMcInfoActivity;
import com.eastelsoft.lbs.activity.visit.VisitMcRegisterActivity;
import com.eastelsoft.lbs.bean.SelectBean;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class McInfoModelActivity extends Activity {

	private String mId;
	private int mIndex;
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
		
		setContentView(R.layout.widget_select_client_type);
		
		initView();
	}
	
	private void initView() {
		mBackBtn = (Button)findViewById(R.id.btBack);
		mBackBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(1);
				finish();
			}
		});
		
		mList = new ArrayList<SelectBean>();
		initData();
		mListView = (ListView)findViewById(R.id.listview);
		mAdapter = new SelectAdapter(this, mList, mId);
		mListView.setAdapter(mAdapter);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				SelectBean bean = mList.get(position);
				Intent intent = new Intent(McInfoModelActivity.this, VisitMcInfoActivity.class);
				intent.putExtra("index", mIndex);
				intent.putExtra("checked_id", bean.id);
				intent.putExtra("checked_name", bean.name);
				setResult(1, intent);
				finish();
			}
		});
	}
	
	private void initData() {
		SelectBean bean = new SelectBean();
		bean.id = "10001";
		bean.name = "平缝";
		mList.add(bean);
		
		SelectBean bean1 = new SelectBean();
		bean1.id = "1002";
		bean1.name = "包缝";
		mList.add(bean1);
		
		SelectBean bean2 = new SelectBean();
		bean2.id = "1003";
		bean2.name = "绷缝";
		mList.add(bean2);
		
		SelectBean bean3 = new SelectBean();
		bean3.id = "1003";
		bean3.name = "特种机";
		mList.add(bean3);
	}
	
}
