package com.eastelsoft.lbs.activity.client;

import java.util.List;
import java.util.UUID;

import org.apache.http.Header;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.TextUtils;
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
import com.eastelsoft.lbs.bean.ClientContactsBean;
import com.eastelsoft.lbs.bean.ClientMechanicsBean;
import com.eastelsoft.lbs.bean.ClientRegionDto;
import com.eastelsoft.lbs.bean.ClientTypeDto;
import com.eastelsoft.lbs.bean.ClientUploadBean;
import com.eastelsoft.lbs.bean.ResultBean;
import com.eastelsoft.lbs.bean.ClientDto.ClientBean;
import com.eastelsoft.lbs.bean.ClientRegionDto.RegionBean;
import com.eastelsoft.lbs.bean.ClientTypeDto.TypeBean;
import com.eastelsoft.lbs.db.ClientDBTask;
import com.eastelsoft.lbs.db.DBUtil;
import com.eastelsoft.lbs.entity.SetInfo;
import com.eastelsoft.util.IUtil;
import com.eastelsoft.util.http.HttpRestClient;
import com.eastelsoft.util.http.URLHelper;
import com.eastelsoft.util.pinyin.PinyinUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

public class ClientAddActivity extends FragmentActivity implements OnClickListener{
	
	private String mId;
	private String info_data = "";
	private String contacts_data = "";
	private String mechanics_data = "";
	private FragmentManager mFragmentManager;
	private ClientInfoAddFragment mInfoFragment;
	private ClientContactsAddFragment mContactsFragment;
	private ClientMechanicsAddFragment mMechanicsFragment;

	private Button mClientBtn;
	private Button mContactsBtn;
	private Button mMechanicsBtn;
	private Button mBackBtn;
	private Button mSaveBtn;
	
	private PopupWindow popupWindow;
	private Button mPopClose;
	private Button mPopYes;
	private Button mPopNo;
	private TextView mPopTitle;
	
	private Gson gson = new Gson();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		mId = UUID.randomUUID().toString();
		
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
				Intent intent = new Intent(ClientAddActivity.this, ClientActivity.class);
				setResult(0, intent);
				finish();
			}
		});
		mSaveBtn = (Button)findViewById(R.id.btSave);
		mSaveBtn.setOnClickListener(this);
		
		mClientBtn.setOnClickListener(this);
		mContactsBtn.setOnClickListener(this);
		mMechanicsBtn.setOnClickListener(this);
		
		mClientBtn.setEnabled(false);
		
//		InitDataTask();
	}
	
	/**
	 * update type and region from net
	 */
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
	 * handler type data
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
	 * handler region data
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
	
	protected void openPopConfirm(String msg) {
		try {
			LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			ViewGroup menuView = (ViewGroup) mLayoutInflater.inflate(
					R.layout.pop_commax, null, true);
			mPopClose = (Button) menuView.findViewById(R.id.btClose);
			mPopClose.setOnClickListener(this);
			mPopYes = (Button) menuView.findViewById(R.id.btClose1);
			mPopYes.setOnClickListener(this);
			mPopNo = (Button) menuView.findViewById(R.id.btClose2);
			mPopNo.setOnClickListener(this);
			mPopTitle = (TextView) menuView.findViewById(R.id.btPopText);
			mPopTitle.setText(msg);
			popupWindow = new PopupWindow(menuView, LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT, true); // 背景
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
			popupWindow.setAnimationStyle(R.style.PopupAnimation);
			popupWindow.showAtLocation(findViewById(R.id.parent),
					Gravity.CENTER | Gravity.CENTER, 0, 0);
			popupWindow.update();
		} catch (Exception e) {
			e.printStackTrace();
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
		case R.id.btSave:
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
			System.out.println("info_data : "+info_data);
			System.out.println("contacts_data : "+contacts_data);
			System.out.println("mechanics_data : "+mechanics_data);
			openPopConfirm("是否同步客户信息到平台");
			break;
		case R.id.btClose: //pop close
			try {
				popupWindow.dismiss();
			} catch (Exception e) {
			}
			break;
		case R.id.btClose1: //pop yes,upload data
			try {
				popupWindow.dismiss();
			} catch (Exception e) {
			}
			openPopupWindowPG("客户信息上传中...");
			
			SharedPreferences sp = getSharedPreferences("userdata", 0);
			SetInfo set = IUtil.initSetInfo(sp);
			
			ClientUploadBean bean = new ClientUploadBean();
			bean.id = mId;
			bean.info_data = info_data;
			bean.contacts_data = contacts_data;
			bean.mechanics_data = mechanics_data;
			
			String json = gson.toJson(bean);
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
							Toast.makeText(ClientAddActivity.this, getResources().getString(R.string.upload_success), Toast.LENGTH_SHORT).show();
							setResult(1);
							finish();
						} else {
							Toast.makeText(ClientAddActivity.this, getResources().getString(R.string.upload_fail), Toast.LENGTH_SHORT).show();
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				@Override
				public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
					try {
						popupWindowPg.dismiss();
						Toast.makeText(ClientAddActivity.this, getResources().getString(R.string.upload_fail), Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			break;
		case R.id.btClose2: //pop no,insert to DB
			try {
				popupWindow.dismiss();
			} catch (Exception e) {
			}
			if (!TextUtils.isEmpty(info_data)) { //insert info
				ClientBean clientBean = gson.fromJson(info_data, ClientBean.class);
				clientBean.is_upload = "0";
				String name = clientBean.client_name;
				String py = PinyinUtil.getPinYin(name);
				clientBean.first_py = py;
				ClientDBTask.addBean(clientBean);
			}
			if (!TextUtils.isEmpty(contacts_data)) {
				List<ClientContactsBean> list = gson.fromJson(contacts_data, new TypeToken<List<ClientContactsBean>>(){}.getType());
				ClientDBTask.addContacts(list);
			}
			if (!TextUtils.isEmpty(mechanics_data)) {
				List<ClientMechanicsBean> list = gson.fromJson(contacts_data, new TypeToken<List<ClientMechanicsBean>>(){}.getType());
				ClientDBTask.addMechanics(list);
			}
			try {
				popupWindow.dismiss();
			} catch (Exception e) {
			}
			
			Intent intent = new Intent(ClientAddActivity.this, ClientActivity.class);
			intent.putExtra("id", mId);
			setResult(0, intent);
			finish();
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
