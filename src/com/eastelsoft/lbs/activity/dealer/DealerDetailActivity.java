package com.eastelsoft.lbs.activity.dealer;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.bean.DealerDto;
import com.eastelsoft.lbs.bean.DealerDto.DealerBean;
import com.eastelsoft.lbs.db.DealerDBTask;

public class DealerDetailActivity extends BaseActivity {
	
	private String mId;
	private DealerBean mBean;
	
	private View mBackBtn;
	private TextView dealer_name;
	private TextView nick_name;
	private TextView parent_dealer_name;
	private TextView dealer_code;
	private TextView location;
	private TextView typename;
	private TextView contact_person;
	private TextView contact_phone;
	private TextView fax;
	private TextView address;
	private TextView remark;
	private Button chat_btn;
	private View mLoadingView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		mId = intent.getStringExtra("id");
		
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
		location = (TextView)findViewById(R.id.location);
		typename = (TextView)findViewById(R.id.typename);
		contact_person = (TextView)findViewById(R.id.contact_person);
		contact_phone = (TextView)findViewById(R.id.contact_phone);
		fax = (TextView)findViewById(R.id.fax);
		address = (TextView)findViewById(R.id.address);
		remark = (TextView)findViewById(R.id.remark);
		chat_btn = (Button)findViewById(R.id.chat_btn);
		mLoadingView = findViewById(R.id.progress_bar);
		mBackBtn = (Button)findViewById(R.id.btBack);
		
		mBackBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
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
		}
		
		private void fillData() {
			dealer_name.setText(mBean.name);
			nick_name.setText(mBean.name);
			parent_dealer_name.setText(mBean.group_name);
			dealer_code.setText(mBean.group_id);
			contact_phone.setText(mBean.telephone);
			contact_person.setText(mBean.py_name);
			remark.setText(mBean.remark);
		}
	}
}
