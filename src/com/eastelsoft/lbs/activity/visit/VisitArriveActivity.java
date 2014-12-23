package com.eastelsoft.lbs.activity.visit;

import org.apache.http.Header;

import android.content.Intent;
import android.location.Location;
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

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.bean.ResultBean;
import com.eastelsoft.lbs.bean.VisitBean;
import com.eastelsoft.lbs.db.VisitDBTask;
import com.eastelsoft.lbs.location.BaiduMapAction;
import com.eastelsoft.util.CallBack;
import com.eastelsoft.util.Util;
import com.eastelsoft.util.http.HttpRestClient;
import com.eastelsoft.util.http.URLHelper;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

public class VisitArriveActivity extends BaseActivity implements OnClickListener{
	
	private String mId;
	private VisitBean mBean;
	private String mlon;
	private String mlat;
	
	private Button mBackBtn;
	private Button mSaveBtn;
	private Button mArriveBtn;
	private TextView dealer_name;
	private TextView start_time;
	private TextView start_location;
	private TextView arrive_time;
	private TextView arrive_location;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parseIntent();
		
		setContentView(R.layout.activity_visit_arrive);
		initView();
		new DBCacheTask().execute("");
	}
	
	private void parseIntent() {
		Intent intent = getIntent();
		mId = intent.getStringExtra("id");
	}
	
	private void initView() {
		mBackBtn = (Button)findViewById(R.id.btBack);
		mSaveBtn = (Button)findViewById(R.id.btSave);
		mArriveBtn = (Button)findViewById(R.id.arrive_btn);
		dealer_name = (TextView)findViewById(R.id.dealer_name);
		start_time = (TextView)findViewById(R.id.start_time);
		start_location = (TextView)findViewById(R.id.start_location);
		arrive_time = (TextView)findViewById(R.id.arrive_time);
		arrive_location = (TextView)findViewById(R.id.arrive_location);
		
		mBackBtn.setOnClickListener(this);
		mSaveBtn.setOnClickListener(this);
		mArriveBtn.setOnClickListener(this);
	}
	
	private class DBCacheTask extends AsyncTask<String, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				mBean = VisitDBTask.getBeanById(mId);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (mBean != null) {
				fillData();
			}
		}
		
	}
	
	private void fillData() {
		dealer_name.setText(mBean.dealer_name);
		start_time.setText(mBean.start_time);
		start_location.setText(mBean.start_location);
	}
	
	VisitBean bean = new VisitBean();
	private void save() {
		bean.id = mId;
		bean.arrive_time = arrive_time.getText().toString();
		bean.arrive_location = arrive_location.getText().toString();
		bean.arrive_lon = mlon;
		bean.arrive_lat = mlat;
		bean.arrive_accuracy = "-100";
		bean.status = "1";
		
		if (!canSend()) {
			return;
		}
		
		openPopupWindowPG("数据上传中...");
		
		Gson gson = new Gson();
		String json = gson.toJson(bean);
		String mUrl = URLHelper.BASE_ACTION;
		RequestParams params = new RequestParams();
		params.put("reqCode", URLHelper.UPDATE_ARRIVE);
		params.put("json", json);
		params.put("data_id", bean.id);
		HttpRestClient.post(mUrl, params, new TextHttpResponseHandler() {
			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
				try {
					try {
						popupWindowPg.dismiss();
					} catch (Exception e) {
					}
					Gson gson = new Gson();
					ResultBean resultBean = gson.fromJson(responseString, ResultBean.class);
					if ("1".equals(resultBean.resultcode)) {
						saveDB();
						Toast.makeText(VisitArriveActivity.this, getResources().getString(R.string.upload_success), Toast.LENGTH_SHORT).show();
						finish();
					} else {
						Toast.makeText(VisitArriveActivity.this, getResources().getString(R.string.upload_fail), Toast.LENGTH_SHORT).show();
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				try {
					popupWindowPg.dismiss();
					Toast.makeText(VisitArriveActivity.this, getResources().getString(R.string.upload_fail), Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private boolean canSend() {
		if (TextUtils.isEmpty(bean.arrive_time)) {
			Toast.makeText(this, "请进行抵达签到.", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	private void saveDB() {
		VisitDBTask.updateArriveBean(bean);
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btBack:
			finish();
			break;
		case R.id.btSave:
			save();
			break;
		case R.id.arrive_btn:
			String now = Util.getLocaleTime("yyyy-MM-dd hh:mm:ss");
			arrive_time.setText(now);
			arrive_location.setText("正在获取中...");
			new BaiduMapAction(this, mapCallback, "2").startListener();
			break;
		}
	}
	
	private CallBack mapCallback = new CallBack() {
		public void execute(Object[] params) {
			Message msg = mHandler.obtainMessage();
			msg.what = 99;
			msg.obj = params;
			mHandler.sendMessage(msg);
		}
	};
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 99:
				Location location = null;
				Object[] obj1 = (Object[]) msg.obj;
				if (obj1[0] != null) {
					location = (Location) obj1[0];
				}
				if (location != null) {
					try {
						mlon = Util.format(location.getLongitude(), "#.######");
						mlat = Util.format(location.getLatitude(), "#.######");
						String locationDesc = location.getExtras().getString("desc");
						arrive_location.setText(locationDesc);
					} catch (Exception e) {
						arrive_location.setText("获取定位信息失败");
					}
				} else {
					arrive_location.setText("获取定位信息失败");
				}
				break;
			}
		}
	};
}
