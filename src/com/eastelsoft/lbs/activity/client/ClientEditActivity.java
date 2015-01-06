package com.eastelsoft.lbs.activity.client;

import org.apache.http.Header;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.bean.ClientUploadBean;
import com.eastelsoft.lbs.bean.ResultBean;
import com.eastelsoft.lbs.db.ClientDBTask;
import com.eastelsoft.lbs.entity.SetInfo;
import com.eastelsoft.util.IUtil;
import com.eastelsoft.util.http.HttpRestClient;
import com.eastelsoft.util.http.URLHelper;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

public class ClientEditActivity extends FragmentActivity implements OnClickListener{
	
	private String mId;
	private String info_data = "";
	private String contacts_data = "";
	private String mechanics_data = "";
	private FragmentManager mFragmentManager;
	private ClientInfoEditFragment mInfoFragment;
	private ClientContactsEditFragment mContactsFragment;
	private ClientMechanicsEditFragment mMechanicsFragment;

	private Button mClientBtn;
	private Button mContactsBtn;
	private Button mMechanicsBtn;
	private Button mBackBtn;
	private Button mSaveBtn;
	
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
		
		setContentView(R.layout.activity_client_edit);
		initViews();
		mFragmentManager = getSupportFragmentManager();
		setTabSection(0);
		sp = getSharedPreferences("userdata", 0);
	}
	
	private void initViews() {
		mClientBtn = (Button)findViewById(R.id.frame_btn_client);
		mContactsBtn = (Button)findViewById(R.id.frame_btn_contacts);
		mMechanicsBtn = (Button)findViewById(R.id.frame_btn_mechanics);
		mSaveBtn = (Button)findViewById(R.id.btSave);
		mBackBtn = (Button)findViewById(R.id.btBack);
		
		mBackBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		mSaveBtn.setOnClickListener(this);
		
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
				mInfoFragment = new ClientInfoEditFragment(mId);
				transaction.add(R.id.content, mInfoFragment);
			} else {
				transaction.show(mInfoFragment);
			}
			break;
		case 1:
			if (mContactsFragment == null) {
				mContactsFragment = new ClientContactsEditFragment(mId);
				transaction.add(R.id.content, mContactsFragment);
			} else {
				transaction.show(mContactsFragment);
			}
			break;
		case 2:
			if (mMechanicsFragment == null) {
				mMechanicsFragment = new ClientMechanicsEditFragment(mId);
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
	
	private void save() {
		if (mInfoFragment != null) {
			if (!mInfoFragment.canSend()) {
				return;
			}
			info_data = mInfoFragment.getJSON();
		} 
		if (mContactsFragment != null) {
			contacts_data = mContactsFragment.getJSON();
		} 
		if (mMechanicsFragment != null) {
			mechanics_data = mMechanicsFragment.getJSON();
		} 
		
		openPopupWindowPG("客户信息上传中...");
		
		SharedPreferences sp = getSharedPreferences("userdata", 0);
		SetInfo set = IUtil.initSetInfo(sp);
		
		ClientUploadBean bean = new ClientUploadBean();
		bean.id = mId;
		bean.info_data = info_data;
		bean.contacts_data = contacts_data;
		bean.mechanics_data = mechanics_data;
		
		String json = new Gson().toJson(bean);
		String mUrl = URLHelper.TEST_ACTION;
		RequestParams params = new RequestParams();
		params.put("reqCode", URLHelper.UPDATE_CLIENT);
		params.put("json", json);
		params.put("data_id", mId);
		params.put("gps_id", set.getDevice_id());
		System.out.println("json : "+json);
		HttpRestClient.post(mUrl, params, new TextHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
				try {
					popupWindowPg.dismiss();
				} catch (Exception e) {
				}
				try {
					Gson gson = new Gson();
					ResultBean resultBean = gson.fromJson(responseString, ResultBean.class);
					if ("1".equals(resultBean.resultcode)) {
						ClientDBTask.updateIsUpload(mId);
						Toast.makeText(ClientEditActivity.this, getResources().getString(R.string.upload_success), Toast.LENGTH_SHORT).show();
						setResult(1);
						finish();
					} else {
						Toast.makeText(ClientEditActivity.this, getResources().getString(R.string.upload_fail), Toast.LENGTH_SHORT).show();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				try {
					popupWindowPg.dismiss();
					Toast.makeText(ClientEditActivity.this, getResources().getString(R.string.upload_fail), Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
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
		case R.id.btSave:
			save();
			break;
		}
	}
	
	protected PopupWindow popupWindowPg;
	protected TextView btPopGps;
	protected void openPopupWindowPG(String msg) {
		try {
			LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			ViewGroup menuView = (ViewGroup) mLayoutInflater.inflate(
					R.layout.pop_gps, null, true);
			popupWindowPg = new PopupWindow(menuView, LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT, true); // 全部背景置灰
			btPopGps = (TextView) menuView.findViewById(R.id.pop_gps);
			popupWindowPg.setBackgroundDrawable(new BitmapDrawable());
			popupWindowPg.setAnimationStyle(R.style.PopupAnimation);
			popupWindowPg.showAtLocation(findViewById(R.id.parent), 
					Gravity.CENTER | Gravity.CENTER, 0, 0);
			popupWindowPg.update();
		} catch (Exception e) {
		}
	}
}
