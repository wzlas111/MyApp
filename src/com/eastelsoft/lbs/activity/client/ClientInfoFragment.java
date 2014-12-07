package com.eastelsoft.lbs.activity.client;

import org.apache.http.Header;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.bean.ClientDetailBean;
import com.eastelsoft.lbs.db.ClientDBTask;
import com.eastelsoft.util.http.HttpRestClient;
import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class ClientInfoFragment extends Fragment {
	
	private String mId;
	private boolean firstLoad = true;
	private ClientDetailBean mBean;
	
	private View mLoadingView;
	private TextView client_name;
	private TextView nick_name;
	private TextView client_code;
	private TextView dealer_name;
	private TextView type;
	private TextView region_name;
	private TextView typename;
	private TextView contact_phone;
	private TextView fax;
	private TextView address;
	private TextView remark;
	
	public ClientInfoFragment(String id) {
		mId = id;
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
		typename = (TextView)view.findViewById(R.id.typename);
		contact_phone = (TextView)view.findViewById(R.id.contact_phone);
		fax = (TextView)view.findViewById(R.id.fax);
		address = (TextView)view.findViewById(R.id.address);
		remark = (TextView)view.findViewById(R.id.remark);
		
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
				if (mBean.type != null && !"".equals(mBean.type)) {
					firstLoad = false;
				}
				fillData();
			}
			RefreshDataTask();
		}
	}
	
	private void RefreshDataTask() {
		String url = "http://58.240.63.104/managermobile.do?reqCode=custDetail&id="+mId;
		HttpRestClient.get(url, null, new TextHttpResponseHandler() {
			
			@Override
			public void onStart() {
				super.onStart();
				if (firstLoad) {
					mLoadingView.setVisibility(View.VISIBLE);
				}
			}
			
			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
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
				mBean = gson.fromJson(responseString, ClientDetailBean.class);
				updateDB();
			} catch (Exception e) {
				e.printStackTrace();
			}
			mHandler.sendEmptyMessage(1);
			System.out.println("dealer data handle success.");
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
		type.setText(mBean.type);
		region_name.setText(mBean.region_name);
		typename.setText(mBean.typename);
		contact_phone.setText(mBean.contact_phone);
		fax.setText(mBean.fax);
		address.setText(mBean.address);
		remark.setText(mBean.remark);
	}
	
}
