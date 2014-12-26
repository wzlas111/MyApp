package com.eastelsoft.lbs.activity.client;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.entity.SetInfo;
import com.eastelsoft.util.IUtil;

public class ClientDetailActivity extends FragmentActivity implements OnClickListener{
	
	private String mId;
	private boolean need_update;
	private FragmentManager mFragmentManager;
	private ClientInfoFragment mInfoFragment;
	private ClientContactsFragment mContactsFragment;
	private ClientMechanicsFragment mMechanicsFragment;

	private Button mClientBtn;
	private Button mContactsBtn;
	private Button mMechanicsBtn;
	private Button mBackBtn;
	
	protected SharedPreferences sp;
	
	public String getGpsId() {
		SetInfo set = IUtil.initSetInfo(sp);
		return set.getDevice_id();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		mId = intent.getStringExtra("id");
		need_update = intent.getBooleanExtra("need_update", true);
		
		setContentView(R.layout.activity_client_detail);
		initViews();
		mFragmentManager = getSupportFragmentManager();
		setTabSection(0);
		sp = getSharedPreferences("userdata", 0);
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
	}
	
	private void setTabSection(int index) {
		FragmentTransaction transaction = mFragmentManager.beginTransaction();
		hideFragments(transaction);
		switch (index) {
		case 0:
			if (mInfoFragment == null) {
				mInfoFragment = new ClientInfoFragment(mId,need_update);
				transaction.add(R.id.content, mInfoFragment);
			} else {
				transaction.show(mInfoFragment);
			}
			break;
		case 1:
			if (mContactsFragment == null) {
				mContactsFragment = new ClientContactsFragment(mId,need_update);
				transaction.add(R.id.content, mContactsFragment);
			} else {
				transaction.show(mContactsFragment);
			}
			break;
		case 2:
			if (mMechanicsFragment == null) {
				mMechanicsFragment = new ClientMechanicsFragment(mId,need_update);
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
