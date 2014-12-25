package com.eastelsoft.lbs.activity.reg;

import org.apache.http.Header;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.bean.ResultBean;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.Util;
import com.eastelsoft.util.http.HttpRestClient;
import com.eastelsoft.util.http.URLHelper;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

public class UnboundlingStep1Activity extends BaseActivity implements OnClickListener {
	
	public static String TAG = "UnboundlingStep1Activity";
	
	private Button mBtnNext;
	private EditText mEtTelephone;
	private Button mBtnBack;
	
	private String mNumber;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_unboundling_step1);
		
		mBtnNext = (Button)findViewById(R.id.btNext);
		mBtnNext.setOnClickListener(this);
		mBtnBack = (Button)findViewById(R.id.btBack);
		mBtnBack.setOnClickListener(this);
		mEtTelephone = (EditText)findViewById(R.id.telephone);
		mEtTelephone.setInputType(EditorInfo.TYPE_CLASS_PHONE);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btNext:
			networkAvailable = isNetworkAvailable();
			mNumber = mEtTelephone.getText().toString();
			// mobile = new Mobile(serialNumber);

			if (networkAvailable) {
				// if(mobile.getFacilitatorType() > -1) {
				if (Util.chkNumber(mNumber)) {
					// 关闭输入法
					InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					boolean isOpen = inputMethodManager.isActive();
					if (isOpen)
						inputMethodManager.hideSoftInputFromWindow(
								UnboundlingStep1Activity.this.getCurrentFocus().getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);
					
					openPopupWindowPG("");
					btPopGps.setText("请等待...");
					
					String mUrl = URLHelper.TEST_ACTION;
					RequestParams params = new RequestParams();
					params.put("reqCode", "queryGpsCode");
					params.put("sim_phone", mNumber);
					HttpRestClient.post(mUrl, params, new TextHttpResponseHandler() {
						@Override
						public void onSuccess(int statusCode, Header[] headers, String responseString) {
							FileLog.i(TAG, TAG+" data : "+responseString);
							try {
								popupWindowPg.dismiss();
							} catch (Exception e) {
							}
							Gson gson = new Gson();
							ResultBean resultBean = gson.fromJson(responseString, ResultBean.class);
							if ("1".equals(resultBean.resultcode)) {
								Intent intent = new Intent();
								intent.setClass(UnboundlingStep1Activity.this, UnboundlingStep2Activity.class);
								intent.putExtra("telephone", mNumber);
								startActivity(intent);
								finish();
							} else {
								Toast.makeText(getApplicationContext(), "请求失败，请稍后再试.", Toast.LENGTH_SHORT).show();
							}
						}
						@Override
						public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
							try {
								popupWindowPg.dismiss();
							} catch (Exception e) {
							}
							Toast.makeText(getApplicationContext(), "请求失败，请稍后再试.", Toast.LENGTH_SHORT).show();
						}
					});
				} else {
					respMsg = getResources().getString(R.string.reg_edit_error);
					Toast.makeText(getApplicationContext(), respMsg,
							Toast.LENGTH_SHORT).show();
				}
			} else {
				respMsg = getResources().getString(R.string.net_error);
				Toast.makeText(getApplicationContext(), respMsg,
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.btBack:
			finish();
			break;
		}
	}
	
}
