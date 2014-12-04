package com.eastelsoft.lbs.activity.reg;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.activity.BaseActivity;

public class UnboundlingStep2Activity extends BaseActivity implements OnClickListener{

	private String mNumber = "";
	private String mVladNum = "";
	private int mCount = 60;
	
	private TextView mTvTip;
	private EditText mEtValidCode;
	private Button mBtnConfirm;
	private Button mBtnResend;
	private Button mBtnBack;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Intent intent = getIntent();
		mNumber = intent.getStringExtra("telephone");
		
		setContentView(R.layout.activity_unboundling_step2);
		
		new SendAsyncTask().execute("");
		
		mTvTip = (TextView)findViewById(R.id.tv_tip);
		mTvTip.setText("您的手机号是："+mNumber);
		mEtValidCode = (EditText)findViewById(R.id.valid_code);
		mBtnConfirm = (Button)findViewById(R.id.btConfirm);
		mBtnConfirm.setOnClickListener(this);
		mBtnResend = (Button)findViewById(R.id.btResend);
		mBtnResend.setOnClickListener(this);
		mBtnBack = (Button)findViewById(R.id.btBack);
		mBtnBack.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btConfirm:
			mVladNum = mEtValidCode.getText().toString();
			if (!TextUtils.isEmpty(mVladNum)) {
				
			} else {
				respMsg = "请输入正确的验证码.";
				Toast.makeText(getApplicationContext(), respMsg,
						Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.btResend:
			if (mBtnResend.isEnabled()) {
				new SendAsyncTask().execute("");
			}
			break;
		case R.id.btBack:
			finish();
			break;
		}
	}
	
	Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				if (mCount > 1) {
					mCount--;
					mBtnResend.setText("重新获取("+mCount+")");
					mBtnResend.setEnabled(false);
					mHandler.sendMessageDelayed(mHandler.obtainMessage(0), 1000);
				} else {
					mBtnResend.setText("重新获取");
					mBtnResend.setEnabled(true);
					mCount = 60;
				}
				break;
			}
		};
	};
	
	private class SendAsyncTask extends AsyncTask<String, Integer, Boolean> {
		
		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			mHandler.sendMessageDelayed(mHandler.obtainMessage(0), 1000);
		}

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				Thread.sleep(5 * 1000);
			} catch (InterruptedException e) {
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
		}
		
	}
}
