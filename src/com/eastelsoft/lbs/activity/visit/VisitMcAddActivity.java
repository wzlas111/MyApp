package com.eastelsoft.lbs.activity.visit;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.activity.BaseActivity;

public class VisitMcAddActivity extends BaseActivity implements OnClickListener {

	private String mId;

	private Button mBackBtn;
	private TextView mSaveUploadBtn;
	private View mRow_client_name;
	private View mRow_mc_register;
	private View mRow_mc_type;
	private View mRow_mc_person;
	private View mRow_mc_info;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parseIntent();

		setContentView(R.layout.activity_visit_mc_add);
		initView();
	}

	private void parseIntent() {
		Intent intent = getIntent();
		mId = intent.getStringExtra("id");
	}

	private void initView() {
		mBackBtn = (Button)findViewById(R.id.btBack);
		mSaveUploadBtn = (TextView)findViewById(R.id.save_upload);
		mRow_client_name = findViewById(R.id.row_client_name);
		mRow_mc_register = findViewById(R.id.row_mc_register);
		mRow_mc_type = findViewById(R.id.row_mc_type);
		mRow_mc_person = findViewById(R.id.row_mc_person);
		mRow_mc_info = findViewById(R.id.row_mc_info);
		
		mBackBtn.setOnClickListener(this);
		mSaveUploadBtn.setOnClickListener(this);
		mRow_client_name.setOnClickListener(this);
		mRow_mc_register.setOnClickListener(this);
		mRow_mc_type.setOnClickListener(this);
		mRow_mc_person.setOnClickListener(this);
		mRow_mc_info.setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.btBack:
			finish();
			break;
		case R.id.save_upload:
			
			break;
		case R.id.row_client_name:

			break;
		case R.id.row_mc_register:
			intent = new Intent(this, VisitMcRegisterAddActivity.class);
			intent.putExtra("id", mId);
			startActivity(intent);
			break;
		case R.id.row_mc_type:

			break;
		case R.id.row_mc_person:

			break;
		case R.id.row_mc_info:

			break;
		}
	}
}
