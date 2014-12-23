package com.eastelsoft.lbs.activity.client;

import java.util.List;

import org.apache.http.Header;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.bean.ClientMechanicsBean;
import com.eastelsoft.lbs.bean.ClientMechanicsDto;
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
public class ClientMechanicsFragment extends Fragment implements OnClickListener{
	
	public static String TAG = "ClientMechanicsFragment";

	private String mId;
	private boolean need_update;
	private List<ClientMechanicsBean> mList;
	
	private LinearLayout mFrameTable;
	
	public ClientMechanicsFragment(String id, boolean flag) {
		mId = id;
		need_update = flag;
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
				mList = ClientDBTask.getMechanicsByClientId(mId);
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
			need_update = true;
			if (need_update) {
				RefreshDataTask();
			}
		}
	}
	
	private void RefreshDataTask() {
		String mUrl = URLHelper.TEST_ACTION;
		RequestParams params = new RequestParams();
		params.put("reqCode", "ClientUpdateData3ActionJk");
		params.put("GpsId", ((ClientDetailActivity)getActivity()).getGpsId());
		params.put("id", mId);
		HttpRestClient.get(mUrl, params, new TextHttpResponseHandler() {
			
			@Override
			public void onStart() {
				super.onStart();
			}
			
			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
				FileLog.i(TAG, TAG+"客户机修下载成功.data: "+responseString);
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
	
	private ClientMechanicsDto mDto;
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
				mDto = gson.fromJson(responseString, ClientMechanicsDto.class);
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
		ClientDBTask.deleteMechanicsAll(mId);
		ClientDBTask.addMechanics(mList);
	}
	
	private void fillData() {
		for (int i = 0; i < mList.size(); i++) {
			ClientMechanicsBean bean = mList.get(i);
			View view = LayoutInflater.from(getActivity()).inflate(R.layout.widget_mechanics_table, null);
			((TextView)view.findViewById(R.id.contact_person_name)).setText(bean.contact_person_name);
			((TextView)view.findViewById(R.id.contact_phone_1)).setText(bean.contact_phone_1);
			((TextView)view.findViewById(R.id.contact_phone_2)).setText(bean.contact_phone_2);

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
