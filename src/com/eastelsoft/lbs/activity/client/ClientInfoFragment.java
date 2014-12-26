package com.eastelsoft.lbs.activity.client;

import org.apache.http.Header;

import com.eastelsoft.lbs.ItemizedOverlayBaiduActivity;
import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.bean.ClientDto.ClientBean;
import com.eastelsoft.lbs.db.ClientDBTask;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.http.HttpRestClient;
import com.eastelsoft.util.http.URLHelper;
import com.eastelsoft.util.settinghelper.SettingUtility;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class ClientInfoFragment extends Fragment implements OnClickListener{
	
	public static String TAG = "ClientInfoFragment";
	
	private String mId;
	private boolean need_update = true;
	private ClientBean mBean;
	private String updatecode = SettingUtility.getUpdatecodeValue(SettingUtility.CLIENT_UPDATECODE);
	
	private View mLoadingView;
	private TextView client_name;
	private TextView nick_name;
	private TextView client_code;
	private TextView type;
	private TextView dealer_name;
	private TextView region_name;
	private TextView type_name;
	private TextView fax;
	private TextView address;
	private TextView remark;
	private Button address_img;
	
	public ClientInfoFragment(String id, boolean flag) {
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
		return inflater.inflate(R.layout.fragment_client_info, null);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mLoadingView = view.findViewById(R.id.progress_bar);
		client_name = (TextView)view.findViewById(R.id.client_name);
		nick_name = (TextView)view.findViewById(R.id.nick_name);
		client_code = (TextView)view.findViewById(R.id.client_code);
		dealer_name = (TextView)view.findViewById(R.id.dealer_name);
		type = (TextView)view.findViewById(R.id.type);
		region_name = (TextView)view.findViewById(R.id.region_name);
		type_name = (TextView)view.findViewById(R.id.type_name);
		fax = (TextView)view.findViewById(R.id.fax);
		address = (TextView)view.findViewById(R.id.address);
		remark = (TextView)view.findViewById(R.id.remark);
		address_img = (Button)view.findViewById(R.id.address_img);
		
		address_img.setOnClickListener(this);
		
		new DBCacheTask().execute("");
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if (mDataThread != null) {
			mDataThread.interrupt();
			mDataThread = null;
		}
	}
	
	private class DBCacheTask extends AsyncTask<String, Integer, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mLoadingView.setVisibility(View.VISIBLE);
		}
		
		@Override
		protected Boolean doInBackground(String... arg0) {
			try {
				mBean = ClientDBTask.getBeanById(mId);
			} catch (Exception e) {
				System.out.println("客户详情加载失败.");
				e.printStackTrace();
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			mLoadingView.setVisibility(View.GONE);
			if (mBean != null) {
				fillData();
			}
			if (!updatecode.equals(mBean.updatecode)) {
				RefreshDataTask();
			}
		}
	}
	
	private void RefreshDataTask() {
		String mUrl = URLHelper.TEST_ACTION;
		RequestParams params = new RequestParams();
		params.put("reqCode", "ClientUpdateData1ActionJk");
		params.put("GpsId", ((ClientDetailActivity)getActivity()).getGpsId());
		params.put("id", mId);
		HttpRestClient.get(mUrl, params, new TextHttpResponseHandler() {
			
			@Override
			public void onStart() {
				super.onStart();
				mLoadingView.setVisibility(View.VISIBLE);
			}
			
			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
				FileLog.i(TAG, TAG+"客户基本数据下载成功.data: "+responseString);
				if (!TextUtils.isEmpty(responseString)) {
					Message msg = new Message();
					msg.what = 0;
					msg.obj = responseString;
					mHandler.sendMessage(msg);
				} else {
					mLoadingView.setVisibility(View.GONE);
				}
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				mLoadingView.setVisibility(View.GONE);
			}
		});
	}
	
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
				mBean = gson.fromJson(responseString, ClientBean.class);
				if (mBean != null) {
					mBean.updatecode = updatecode;
					updateDB();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			mHandler.sendEmptyMessage(1);
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
				mLoadingView.setVisibility(View.GONE);
				fillData();
				break;
			}
		};
	};
	
	private void updateDB() {
		ClientDBTask.updateBean(mBean);
	}
	
	private void fillData() {
		client_name.setText(mBean.client_name);
		nick_name.setText(mBean.client_name);
		client_code.setText(mBean.client_code);
		dealer_name.setText(mBean.dealer_name);
		if (!TextUtils.isEmpty(mBean.type)) {
			if ("1".equals(mBean.type)) {
				type.setText("企业共享");
			} else if("3".equals(mBean.type)) {
				type.setText("员工共享");
			}
		}
		region_name.setText(mBean.region_name);
		type_name.setText(mBean.type_name);
		fax.setText(mBean.fax);
		address.setText(mBean.address);
		remark.setText(mBean.remark);
		
		if (!TextUtils.isEmpty(mBean.lon) && !TextUtils.isEmpty(mBean.lat)) {
			address_img.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.address_img:
			try {
				if(!TextUtils.isEmpty(mBean.lon) && !TextUtils.isEmpty(mBean.lat)){
					Intent intent = new Intent(getActivity(),ItemizedOverlayBaiduActivity.class);
					intent.putExtra("lon", mBean.lon);
					intent.putExtra("lat", mBean.lat);
					intent.putExtra("location", mBean.address);
					intent.putExtra("title", "客户位置");
	                startActivity(intent);	
				}
			} catch (Exception e) {
			}
			break;
		}
	}
	
}
