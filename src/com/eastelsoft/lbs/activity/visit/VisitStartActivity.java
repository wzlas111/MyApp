package com.eastelsoft.lbs.activity.visit;

import java.util.UUID;

import org.apache.http.Header;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.activity.select.ClientDealerActivity;
import com.eastelsoft.lbs.bean.ResultBean;
import com.eastelsoft.lbs.bean.VisitBean;
import com.eastelsoft.lbs.db.VisitDBTask;
import com.eastelsoft.lbs.entity.SetInfo;
import com.eastelsoft.lbs.location.BaiduMapAction;
import com.eastelsoft.util.CallBack;
import com.eastelsoft.util.IUtil;
import com.eastelsoft.util.Util;
import com.eastelsoft.util.http.HttpRestClient;
import com.eastelsoft.util.http.URLHelper;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

public class VisitStartActivity extends BaseActivity implements OnClickListener{
	
	private String mDealer_id = "";
	private String mPlan_id = "";
	private String mlon;
	private String mlat;
	
	private Button mBackBtn;
	private Button mSaveBtn;
	private Button mStartBtn;
	private EditText plan_name;
	private TextView dealer_name;
	private TextView start_time;
	private TextView start_location;
	private View row_dealer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_visit_start);
		
		initView();
	}
	
	private void initView() {
		mBackBtn = (Button)findViewById(R.id.btBack);
		mSaveBtn = (Button)findViewById(R.id.btSave);
		mStartBtn = (Button)findViewById(R.id.start_btn);
		plan_name = (EditText)findViewById(R.id.plan_name);
		dealer_name = (TextView)findViewById(R.id.dealer_name);
		start_time = (TextView)findViewById(R.id.start_time);
		start_location = (TextView)findViewById(R.id.start_location);
		row_dealer = findViewById(R.id.row_dealer);
		
		mBackBtn.setOnClickListener(this);
		mSaveBtn.setOnClickListener(this);
		mStartBtn.setOnClickListener(this);
		row_dealer.setOnClickListener(this);
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 1:
			if (data != null) {
				mDealer_id = data.getStringExtra("checked_id");
				String name = data.getStringExtra("checked_name");
				dealer_name.setText(name);
			}
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
						start_location.setText(locationDesc);
					} catch (Exception e) {
						start_location.setText("获取定位信息失败");
					}
				} else {
					start_location.setText("获取定位信息失败");
				}
				break;
			}
		}
	};
	
	/**
	 * upload to server,now just save to DB.
	 */
	VisitBean bean = new VisitBean();
	private void save() {
		bean.id = UUID.randomUUID().toString();
		bean.plan_id = mPlan_id;
		bean.plan_name = plan_name.getText().toString();
		bean.dealer_id = mDealer_id;
		bean.dealer_name = dealer_name.getText().toString();
		bean.start_time = start_time.getText().toString();
		bean.start_location = start_location.getText().toString();
		bean.start_accuracy = "-100";
		bean.start_lon = mlon;
		bean.start_lat = mlat;
		bean.status = "0";
		
		if (!canSend()) {
			return;
		}
		
		openPopupWindowPG("数据上传中...");
		
		sp = getSharedPreferences("userdata", 0);
		SetInfo set = IUtil.initSetInfo(sp);
		Gson gson = new Gson();
		String json = gson.toJson(bean);
		String mUrl = URLHelper.BASE_ACTION;
		RequestParams params = new RequestParams();
		params.put("reqCode", URLHelper.UPDATE_START);
		params.put("json", json);
		params.put("data_id", bean.id);
		params.put("gps_id", set.getDevice_id());
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
					if ("1".equals(resultBean.result_code)) {
						saveDB();
						Toast.makeText(VisitStartActivity.this, getResources().getString(R.string.upload_success), Toast.LENGTH_SHORT).show();
						finish();
					} else {
						Toast.makeText(VisitStartActivity.this, getResources().getString(R.string.upload_fail), Toast.LENGTH_SHORT).show();
					}
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
				try {
					popupWindowPg.dismiss();
					Toast.makeText(VisitStartActivity.this, getResources().getString(R.string.upload_fail), Toast.LENGTH_SHORT).show();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
	private boolean canSend() {
		if (TextUtils.isEmpty(bean.dealer_id)) {
			Toast.makeText(this, "请选择经销商.", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (TextUtils.isEmpty(bean.start_time)) {
			Toast.makeText(this, "请进行出发签到.", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	private void saveDB() {
		VisitDBTask.addStartBean(bean);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.btBack:
			finish();
			break;
		case R.id.btSave:
			save();
			break;
		case R.id.start_btn:
			String now = Util.getLocaleTime("yyyy-MM-dd hh:mm:ss");
			start_time.setText(now);
			start_location.setText("正在获取中...");
			new BaiduMapAction(this, mapCallback, "2").startListener();
			break;
		case R.id.row_dealer:
			intent = new Intent(this, ClientDealerActivity.class);
			intent.putExtra("id", mDealer_id);
			intent.putExtra("type", "2");
			startActivityForResult(intent, 1);
			break;
		}
	}
}
