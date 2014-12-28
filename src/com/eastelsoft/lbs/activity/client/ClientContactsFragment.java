package com.eastelsoft.lbs.activity.client;

import java.util.List;

import org.apache.http.Header;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.bean.ClientContactsBean;
import com.eastelsoft.lbs.bean.ClientContactsDto;
import com.eastelsoft.lbs.db.ClientDBTask;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.http.HttpRestClient;
import com.eastelsoft.util.http.URLHelper;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class ClientContactsFragment extends Fragment implements OnClickListener{
	
	public static String TAG = "ClientContactsFragment";

	private String mId;
	private boolean need_update = true;
	private String mIsUpload;
	private List<ClientContactsBean> mList;
	
	private LinearLayout mFrameTable;
	
	public ClientContactsFragment(String id,boolean flag,String is_upload) {
		mId = id;
		need_update = flag;
		mIsUpload = is_upload;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_client_contacts, null);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mFrameTable = (LinearLayout)view.findViewById(R.id.frame_table);
		
		new DBCacheTask().execute("");
	}
	
	private class DBCacheTask extends AsyncTask<String, Integer, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
		@Override
		protected Boolean doInBackground(String... arg0) {
			try {
				mList = ClientDBTask.getContactsByClientId(mId);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (mList.size() > 0) {
				fillData();
			}
			if ("0".equals(mIsUpload)) {
				return;
			}
			need_update = true;
			if (need_update) {
				RefreshDataTask();
			}
		}
	}
	
	private void RefreshDataTask() {
		String mUrl = URLHelper.TEST_ACTION;
		RequestParams params = new RequestParams();
		params.put("reqCode", "ClientUpdateData2ActionJk");
		params.put("GpsId", ((ClientDetailActivity)getActivity()).getGpsId());
		params.put("id", mId);
		HttpRestClient.get(mUrl, params, new TextHttpResponseHandler() {
			
			@Override
			public void onStart() {
				super.onStart();
			}
			
			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
				FileLog.i(TAG, TAG+"客户联系人下载成功.data: "+responseString);
				if (!TextUtils.isEmpty(responseString)) {
					Message msg = new Message();
					msg.what = 0;
					msg.obj = responseString;
					mHandler.sendMessage(msg);
				} else {
				}
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
			}
		});
	}
	
	private ClientContactsDto mDto;
	private DataThread mDataThread;
	private class DataThread extends Thread {
		private String responseString;
		public DataThread(String param) {
			responseString = param;
		}
		@Override
		public void run() {
			try {
				Gson gson = new Gson();
				mDto = gson.fromJson(responseString, ClientContactsDto.class);
				mList = mDto.clientdata;
				if (mList != null && mList.size() > 0) {
					updateDB();
					mHandler.sendEmptyMessage(1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				mDataThread = new DataThread((String)msg.obj);
				mDataThread.start();
				break;
			case 1:
				updateData();
				break;
			}
		};
	};
	
	private void updateDB() {
		ClientDBTask.deleteContactsAll(mId);
		ClientDBTask.addContacts(mList);
	}
	
	private void fillData() {
		for (int i = 0; i < mList.size(); i++) {
			ClientContactsBean bean = mList.get(i);
			View view = LayoutInflater.from(getActivity()).inflate(R.layout.widget_contact_table, null);
			((TextView)view.findViewById(R.id.contact_person_name)).setText(bean.contact_person_name);
			((TextView)view.findViewById(R.id.contact_phone_1)).setText(bean.contact_phone_1);
			((TextView)view.findViewById(R.id.contact_phone_2)).setText(bean.contact_phone_2);
			((TextView)view.findViewById(R.id.tel)).setText(bean.tel);
			if ("1".equals(bean.is_main)) {
				((TextView)view.findViewById(R.id.is_main)).setText("是");
			} else {
				((TextView)view.findViewById(R.id.is_main)).setText("否");
			}
			
			if (!TextUtils.isEmpty(bean.contact_phone_1)) {
				((Button)view.findViewById(R.id.contact_phone_sms_1)).setVisibility(View.VISIBLE);
				((Button)view.findViewById(R.id.contact_phone_sms_1)).setOnClickListener(new SmsOnClickListener(bean.contact_phone_1));
				((Button)view.findViewById(R.id.contact_phone_tel_1)).setVisibility(View.VISIBLE);
				((Button)view.findViewById(R.id.contact_phone_tel_1)).setOnClickListener(new TelOnClickListener(bean.contact_phone_1));
			}
			
			if (!TextUtils.isEmpty(bean.contact_phone_2)) {
				((Button)view.findViewById(R.id.contact_phone_sms_2)).setVisibility(View.VISIBLE);
				((Button)view.findViewById(R.id.contact_phone_sms_2)).setOnClickListener(new SmsOnClickListener(bean.contact_phone_2));
				((Button)view.findViewById(R.id.contact_phone_tel_2)).setVisibility(View.VISIBLE);
				((Button)view.findViewById(R.id.contact_phone_tel_2)).setOnClickListener(new TelOnClickListener(bean.contact_phone_2));
			}
			
			if (!TextUtils.isEmpty(bean.tel)) {
				((Button)view.findViewById(R.id.contact_phone_sms_3)).setVisibility(View.VISIBLE);
				((Button)view.findViewById(R.id.contact_phone_sms_3)).setOnClickListener(new SmsOnClickListener(bean.tel));
				((Button)view.findViewById(R.id.contact_phone_tel_3)).setVisibility(View.VISIBLE);
				((Button)view.findViewById(R.id.contact_phone_tel_3)).setOnClickListener(new TelOnClickListener(bean.tel));
			}
			
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
			layoutParams.topMargin = 15;
			mFrameTable.addView(view, layoutParams);
		}
	}
	
	private void updateData() {
		mFrameTable.removeAllViews();
		fillData();
	}
	
	private class TelOnClickListener implements OnClickListener {
		private String mTel;
		public TelOnClickListener(String tel){
			mTel = tel;
		}
		@Override
		public void onClick(View v) {
			try {
				Intent intent = new Intent(Intent.ACTION_CALL,Uri.parse("tel:" + mTel));  
				startActivity(intent);
			} catch (Exception e) {
			}
		}
	}
	
	private class SmsOnClickListener implements OnClickListener {
		private String mTel;
		public SmsOnClickListener(String tel){
			mTel = tel;
		}
		@Override
		public void onClick(View v) {
			try {
				Intent intent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:" + mTel));  
				startActivity(intent);
			} catch (Exception e) {
			}
		}
	}

	@Override
	public void onClick(View v) {
	}
}
