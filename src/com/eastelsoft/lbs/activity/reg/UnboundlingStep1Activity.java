package com.eastelsoft.lbs.activity.reg;

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
import com.eastelsoft.util.Util;

public class UnboundlingStep1Activity extends BaseActivity implements OnClickListener {
	
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

					Intent intent = new Intent();
					intent.setClass(this, UnboundlingStep2Activity.class);
					intent.putExtra("telephone", mNumber);
					
					startActivity(intent);
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
