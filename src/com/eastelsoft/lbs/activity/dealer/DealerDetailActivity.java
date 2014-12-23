package com.eastelsoft.lbs.activity.dealer;

import org.apache.http.Header;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eastelsoft.lbs.ItemizedOverlayBaiduActivity;
import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.bean.DealerDto.DealerBean;
import com.eastelsoft.lbs.db.DealerDBTask;
import com.eastelsoft.lbs.entity.SetInfo;
import com.eastelsoft.util.IUtil;
import com.eastelsoft.util.http.HttpRestClient;
import com.eastelsoft.util.http.URLHelper;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

public class DealerDetailActivity extends BaseActivity implements OnClickListener{
	
	private String mId;
	private DealerBean mBean;
	private boolean need_update;
	
	private View mBackBtn;
	private TextView dealer_name;
	private TextView nick_name;
	private TextView parent_dealer_name;
	private TextView dealer_code;
	private TextView region_name;
	private TextView type_name;
	private TextView contact_person;
	private TextView contact_phone;
	private TextView fax;
	private TextView address;
	private TextView remark;
	private TextView type;
	private Button contact_phone_sms;
	private Button contact_phone_tel;
	private Button address_img;
	private View mLoadingView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		mId = intent.getStringExtra("id");
		need_update = intent.getBooleanExtra("need_update", false);
		
		setContentView(R.layout.activity_dealer_detail);
		
		initViews();
		if (!TextUtils.isEmpty(mId)) {
			new InitAsyncTask().execute("");
		}
	}
	
	private void initViews() {
		dealer_name = (TextView)findViewById(R.id.dealer_name);
		nick_name = (TextView)findViewById(R.id.nick_name);
		parent_dealer_name = (TextView)findViewById(R.id.parent_dealer_name);
		dealer_code = (TextView)findViewById(R.id.dealer_code);
		region_name = (TextView)findViewById(R.id.region_name);
		type_name = (TextView)findViewById(R.id.type_name);
		contact_person = (TextView)findViewById(R.id.contact_person);
		contact_phone = (TextView)findViewById(R.id.contact_phone);
		fax = (TextView)findViewById(R.id.fax);
		address = (TextView)findViewById(R.id.address);
		remark = (TextView)findViewById(R.id.remark);
		type = (TextView)findViewById(R.id.type);
		contact_phone_sms = (Button)findViewById(R.id.contact_phone_sms);
		contact_phone_tel = (Button)findViewById(R.id.contact_phone_tel);
		address_img = (Button)findViewById(R.id.address_img);
		mLoadingView = findViewById(R.id.progress_bar);
		mBackBtn = (Button)findViewById(R.id.btBack);
		
		contact_phone_sms.setOnClickListener(this);
		contact_phone_tel.setOnClickListener(this);
		address_img.setOnClickListener(this);
		mBackBtn.setOnClickListener(this);
	}
	
	private class InitAsyncTask extends AsyncTask<String, Intent, Boolean> {
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mLoadingView.setVisibility(View.VISIBLE);
		}

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				mBean = DealerDBTask.getBeanById(mId);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			mLoadingView.setVisibility(View.GONE);
			if (result) {
				if (mBean != null) {
					fillData();
				}
			} else {
				Toast.makeText(DealerDetailActivity.this, "数据加载失败.", Toast.LENGTH_SHORT).show();
			}
			if (need_update) {
				RefreshDataTask();
			}
		}
		
	}
	
	private void RefreshDataTask() {
		sp = getSharedPreferences("userdata", 0);
		SetInfo set = IUtil.initSetInfo(sp);
		String mUrl = URLHelper.TEST_ACTION;
		RequestParams params = new RequestParams();
		params.put("reqCode", "DealerUpdateDataActionJk");
		params.put("GpsId", set.getDevice_id());
		params.put("id", mId);
		HttpRestClient.get(mUrl, params, new TextHttpResponseHandler() {
			
			@Override
			public void onStart() {
				super.onStart();
				mLoadingView.setVisibility(View.VISIBLE);
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
				mBean = gson.fromJson(responseString, DealerBean.class);
				if (mBean != null) {
					DealerDBTask.updateBean(mBean);
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
	
	private void fillData() {
		dealer_name.setText(mBean.dealer_name);
		nick_name.setText(mBean.dealer_name);
		parent_dealer_name.setText(mBean.parent_dealer_name);
		dealer_code.setText(mBean.dealer_code);
		region_name.setText(mBean.region_name);
		type_name.setText(mBean.type_name);
		contact_phone.setText(mBean.contact_phone);
		contact_person.setText(mBean.contact_person);
		fax.setText(mBean.fax);
		address.setText(mBean.address);
		remark.setText(mBean.remark);
		if (!TextUtils.isEmpty(mBean.type)) {
			if ("1".equals(mBean.type)) {
				type.setText("企业共享");
			} else if("3".equals(mBean.type)) {
				type.setText("员工共享");
			}
		}
		if (!TextUtils.isEmpty(mBean.contact_phone)) {
			contact_phone_sms.setVisibility(View.VISIBLE);
			contact_phone_tel.setVisibility(View.VISIBLE);
		}
		if (!TextUtils.isEmpty(mBean.lon) && !TextUtils.isEmpty(mBean.lat)) {
			address_img.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btBack:
			finish();
			break;
		case R.id.contact_phone_sms:
			try {
				Intent intent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:" + mBean.contact_phone));  
				startActivity(intent);
			} catch (Exception e) {
			}
			break;
		case R.id.contact_phone_tel:
			try {
				Intent it = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + mBean.contact_phone));  
				startActivity(it);   
			} catch (Exception e) {
			}
			break;
		case R.id.address_img:
			try {
				if(!TextUtils.isEmpty(mBean.lon) && !TextUtils.isEmpty(mBean.lat)){
					Intent intent = new Intent(this,ItemizedOverlayBaiduActivity.class);
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
