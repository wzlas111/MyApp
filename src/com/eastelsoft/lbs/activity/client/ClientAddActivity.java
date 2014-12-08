package com.eastelsoft.lbs.activity.client;

import java.util.List;

import org.apache.http.Header;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.bean.ClientRegionDto;
import com.eastelsoft.lbs.bean.ClientTypeDto;
import com.eastelsoft.lbs.bean.ClientRegionDto.RegionBean;
import com.eastelsoft.lbs.bean.ClientTypeDto.TypeBean;
import com.eastelsoft.lbs.db.ClientDBTask;
import com.eastelsoft.util.http.HttpRestClient;
import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;

public class ClientAddActivity extends FragmentActivity implements OnClickListener{
	
	private String mId;
	private FragmentManager mFragmentManager;
	private ClientInfoAddFragment mInfoFragment;
	private ClientContactsAddFragment mContactsFragment;
	private ClientMechanicsAddFragment mMechanicsFragment;

	private Button mClientBtn;
	private Button mContactsBtn;
	private Button mMechanicsBtn;
	private Button mBackBtn;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		mId = intent.getStringExtra("id");
		
		setContentView(R.layout.activity_client_add);
		initViews();
		mFragmentManager = getSupportFragmentManager();
		setTabSection(0);
	}
	
	private void initViews() {
		mClientBtn = (Button)findViewById(R.id.frame_btn_client);
		mContactsBtn = (Button)findViewById(R.id.frame_btn_contacts);
		mMechanicsBtn = (Button)findViewById(R.id.frame_btn_mechanics);
		mBackBtn = (Button)findViewById(R.id.btBack);
		mBackBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		
		mClientBtn.setOnClickListener(this);
		mContactsBtn.setOnClickListener(this);
		mMechanicsBtn.setOnClickListener(this);
		
		mClientBtn.setEnabled(false);
		
		InitDataTask();
	}
	
	private void InitDataTask() {
		String type_url = "http://58.240.63.104/mobile.do?reqCode=ClientTypeUpdateAction&gpsid=8610818980382688&pin=1&actiontype=2";
		String region_url = "http://58.240.63.104/mobile.do?reqCode=ClientRegionUpdateAction&gpsid=8610818980382688&pin=1&actiontype=2";
		HttpRestClient.get(type_url, null, new TextHttpResponseHandler() {
			@Override
			public void onStart() {
				super.onStart();
			}
			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
				new DataTypeThread(responseString).start();
			}
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				Toast.makeText(ClientAddActivity.this, "sorry,类型数据下载失败,请稍后再试.", Toast.LENGTH_SHORT).show();
			}
		});
		HttpRestClient.get(region_url, null, new TextHttpResponseHandler() {
			@Override
			public void onStart() {
				super.onStart();
			}
			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
				new DataRegionThread(responseString).start();
			}
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				Toast.makeText(ClientAddActivity.this, "sorry,区域数据下载失败,请稍后再试.", Toast.LENGTH_SHORT).show();
			}
		});
	}
	
	/**
	 * 类型数据处理
	 * @author wangzl
	 *
	 */
	private class DataTypeThread extends Thread {
		private String responseString;
		public DataTypeThread(String param) {
			responseString = param;
		}
		@Override
		public void run() {
			try {
				Gson gson = new Gson();
				ClientTypeDto mClientTypeDto = gson.fromJson(responseString, ClientTypeDto.class);
				updateTypeDB(mClientTypeDto.clientdata);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void updateTypeDB(List<TypeBean> mList) {
		ClientDBTask.deleteType();
		ClientDBTask.addType(mList);
	}
	
	/**
	 * 类型数据处理
	 * @author wangzl
	 *
	 */
	private class DataRegionThread extends Thread {
		private String responseString;
		public DataRegionThread(String param) {
			responseString = param;
		}
		@Override
		public void run() {
			try {
				Gson gson = new Gson();
				ClientRegionDto mClientRegionDto = gson.fromJson(responseString, ClientRegionDto.class);
				updateRegionDB(mClientRegionDto.clientdata);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private void updateRegionDB(List<RegionBean> mList) {
		ClientDBTask.deleteRegion();
		ClientDBTask.addRegion(mList);
	}
	
	private void setTabSection(int index) {
		FragmentTransaction transaction = mFragmentManager.beginTransaction();
		hideFragments(transaction);
		switch (index) {
		case 0:
			if (mInfoFragment == null) {
				mInfoFragment = new ClientInfoAddFragment(mId);
				transaction.add(R.id.content, mInfoFragment);
			} else {
				transaction.show(mInfoFragment);
			}
			break;
		case 1:
			if (mContactsFragment == null) {
				mContactsFragment = new ClientContactsAddFragment(mId);
				transaction.add(R.id.content, mContactsFragment);
			} else {
				transaction.show(mContactsFragment);
			}
			break;
		case 2:
			if (mMechanicsFragment == null) {
				mMechanicsFragment = new ClientMechanicsAddFragment(mId);
				transaction.add(R.id.content, mMechanicsFragment);
			} else {
				transaction.show(mMechanicsFragment);
			}
			break;
		}
		transaction.commit();
	}
	
	private void hideFragments(FragmentTransaction transaction) {
		if (mInfoFragment != null) {
			transaction.hide(mInfoFragment);
		}
		if (mContactsFragment != null) {
			transaction.hide(mContactsFragment);
		}
		if (mMechanicsFragment != null) {
			transaction.hide(mMechanicsFragment);
		}
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.frame_btn_client:
			mClientBtn.setEnabled(false);
			mContactsBtn.setEnabled(true);
			mMechanicsBtn.setEnabled(true);
			setTabSection(0);
			break;
		case R.id.frame_btn_contacts:
			mClientBtn.setEnabled(true);
			mContactsBtn.setEnabled(false);
			mMechanicsBtn.setEnabled(true);
			setTabSection(1);
			break;
		case R.id.frame_btn_mechanics:
			mClientBtn.setEnabled(true);
			mContactsBtn.setEnabled(true);
			mMechanicsBtn.setEnabled(false);
			setTabSection(2);
			break;
		}
	}
}
