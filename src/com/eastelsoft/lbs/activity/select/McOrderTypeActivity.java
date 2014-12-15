package com.eastelsoft.lbs.activity.select;

import java.util.ArrayList;
import java.util.List;

import com.eastelsoft.lbs.R;
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

public class McOrderTypeActivity extends Activity {

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
		
		initView();
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
		initData();
		mListView = (ListView)findViewById(R.id.listview);
		mAdapter = new SelectAdapter(this, mList, mId);
		mListView.setAdapter(mAdapter);
		
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				SelectBean bean = mList.get(position);
				Intent intent = new Intent(McOrderTypeActivity.this, VisitMcRegisterActivity.class);
				intent.putExtra("checked_id", bean.id);
				intent.putExtra("checked_name", bean.name);
				setResult(1, intent);
				finish();
			}
		});
	}
	
	private void initData() {
		SelectBean bean = new SelectBean();
		bean.id = "20";
		bean.name = "自主品牌";
		mList.add(bean);
		
		SelectBean bean1 = new SelectBean();
		bean1.id = "21";
		bean1.name = "代工";
		mList.add(bean1);
		
		SelectBean bean2 = new SelectBean();
		bean2.id = "22";
		bean2.name = "外销";
		mList.add(bean2);
		
		SelectBean bean3 = new SelectBean();
		bean3.id = "23";
		bean3.name = "其他";
		mList.add(bean3);
	}
	
}
