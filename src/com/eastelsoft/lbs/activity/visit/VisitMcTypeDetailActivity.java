package com.eastelsoft.lbs.activity.visit;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.bean.VisitMcTypeBean;
import com.google.gson.Gson;

public class VisitMcTypeDetailActivity extends BaseActivity implements OnClickListener {
	
	private String mId;
	private String mJson;
	private VisitMcTypeBean mBean;

	private Button mBackBtn;
	private TextView mSaveBtn;
	private TextView product_type;
	private TextView order_type;
	private TextView company_nature;
	private TextView factory_persons;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parseIntent();
		
		setContentView(R.layout.visit_mc_type_detail);
		initView();
	}
	
	private void parseIntent() {
		Intent intent = getIntent();
		mId = intent.getStringExtra("id");
		mJson = intent.getStringExtra("json");
	}
	
	private void initView() {
		mBackBtn = (Button)findViewById(R.id.btBack);
		mSaveBtn = (TextView)findViewById(R.id.save);
		product_type = (TextView)findViewById(R.id.product_type);
		order_type = (TextView)findViewById(R.id.order_type);
		company_nature = (TextView)findViewById(R.id.company_nature);
		factory_persons = (TextView)findViewById(R.id.factory_persons);
		
		mBackBtn.setOnClickListener(this);
		mSaveBtn.setVisibility(View.GONE);
		
		if (TextUtils.isEmpty(mJson)) {//add new row
		} else {//fill data
			fillData();
		}
	}
	
	private void fillData() {
		try {
			Gson gson = new Gson();
			mBean = gson.fromJson(mJson, VisitMcTypeBean.class);
			product_type.setText(mBean.product_name);
			order_type.setText(mBean.order_name);
			company_nature.setText(mBean.company_name);
			factory_persons.setText(mBean.factory_persons);
		} catch (Exception e) {
			e.printStackTrace();
			mBean = new VisitMcTypeBean();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btBack:
			setResult(1);
			finish();
			break;
		}
	}
}
