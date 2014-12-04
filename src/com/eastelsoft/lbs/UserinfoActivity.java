/**
 * Copyright (c) 2013-6-9 www.eastelsoft.com
 * $ID UserinfoActivity.java 上午9:20:47 $
 */
package com.eastelsoft.lbs;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.GlobalVar;

/**
 * 个人信息页面
 * 
 * @author lengcj
 */
public class UserinfoActivity extends BaseActivity {

	private static final String TAG = "UserinfoActivity";
	private Button btBack;
	TextView tvMobile;
	TextView tvImsi;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_userinfo);
		btBack = (Button) findViewById(R.id.btBack);
		btBack.setOnClickListener(new OnBtBackClickListenerImpl());
		tvMobile = (TextView) findViewById(R.id.set_mobile_value);
		tvImsi = (TextView) findViewById(R.id.set_imsi_value);
		globalVar = (GlobalVar) getApplicationContext();
		// 获取系统参数
		sp = getSharedPreferences("userdata", 0);
		serialNumber = sp.getString("serialNumber", "");
		imsi = sp.getString("imsi", "");
		tvMobile.setText(serialNumber);
		tvImsi.setText(imsi);
	}

	@Override
	protected void onDestroy() {
		FileLog.i(TAG, "onDestroy....");
		super.onDestroy();
	}

	private class OnBtBackClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				UserinfoActivity.this.finish();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

}
