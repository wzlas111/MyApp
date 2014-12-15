package com.eastelsoft.lbs.activity.visit;

import java.util.UUID;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.activity.select.McCompanyNatureActivity;
import com.eastelsoft.lbs.activity.select.McOrderTypeActivity;
import com.eastelsoft.lbs.activity.select.McProductTypeActivity;
import com.eastelsoft.lbs.bean.VisitMcTypeBean;
import com.google.gson.Gson;

public class VisitMcTypeActivity extends BaseActivity implements OnClickListener {
	
	private String mId;
	private String mJson;
	private VisitMcTypeBean mBean;
	private String product_type_id = "";
	private String order_type_id = "";
	private String company_nature_id = "";

	private Button mBackBtn;
	private TextView mSaveBtn;
	private View row_product_type;
	private View row_order_type;
	private View row_company_nature;
	private TextView product_type;
	private TextView order_type;
	private TextView company_nature;
	private EditText factory_persons;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parseIntent();
		
		setContentView(R.layout.visit_mc_type_add);
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
		row_product_type = findViewById(R.id.row_product_type);
		row_order_type = findViewById(R.id.row_order_type);
		row_company_nature = findViewById(R.id.row_company_nature);
		product_type = (TextView)findViewById(R.id.product_type);
		order_type = (TextView)findViewById(R.id.order_type);
		company_nature = (TextView)findViewById(R.id.company_nature);
		factory_persons = (EditText)findViewById(R.id.factory_persons);
		
		mBackBtn.setOnClickListener(this);
		mSaveBtn.setOnClickListener(this);
		row_product_type.setOnClickListener(this);
		row_order_type.setOnClickListener(this);
		row_company_nature.setOnClickListener(this);
		
		if (TextUtils.isEmpty(mJson)) {//add new row
			initData();
		} else {//fill data
			fillData();
		}
	}
	
	private void initData() {
		factory_persons.setText("");
		mBean = new VisitMcTypeBean();
	}
	
	private void fillData() {
		try {
			Gson gson = new Gson();
			mBean = gson.fromJson(mJson, VisitMcTypeBean.class);
			product_type.setText(mBean.product_name);
			order_type.setText(mBean.order_name);
			company_nature.setText(mBean.company_name);
			factory_persons.setText(mBean.factory_persons);
			product_type_id = mBean.product_id;
			order_type_id = mBean.order_id;
			company_nature_id = mBean.company_id;
		} catch (Exception e) {
			e.printStackTrace();
			mBean = new VisitMcTypeBean();
		}
	}

	private void save() {
		VisitMcTypeBean bean = new VisitMcTypeBean();
		bean.id = UUID.randomUUID().toString();
		bean.product_id = product_type_id;
		bean.product_name = product_type.getText().toString();
		bean.order_id = order_type_id;
		bean.order_name = order_type.getText().toString();
		bean.company_id = company_nature_id;
		bean.company_name = company_nature.getText().toString();
		bean.factory_persons = factory_persons.getText().toString();
		
		Gson gson = new Gson();
		String jsonString = gson.toJson(bean);
		Intent intent = new Intent(this, VisitMcAddActivity.class);
		intent.putExtra("json", jsonString);
		setResult(1, intent);
		finish();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 1: //product type
			if (data != null) {
				product_type_id = data.getStringExtra("checked_id");
				String name = data.getStringExtra("checked_name");
				product_type.setText(name);
			}
			break;
		case 2: //order type
			if (data != null) {
				order_type_id = data.getStringExtra("checked_id");
				String name = data.getStringExtra("checked_name");
				order_type.setText(name);
			}
			break;
		case 3: // company nature
			if (data != null) {
				company_nature_id = data.getStringExtra("checked_id");
				String name = data.getStringExtra("checked_name");
				company_nature.setText(name);
			}
			break;
		}
	}
	
	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.btBack:
			setResult(1);
			finish();
			break;
		case R.id.save:
			save();
			break;
		case R.id.row_product_type:
			intent = new Intent(this, McProductTypeActivity.class);
			intent.putExtra("id", product_type_id);
			startActivityForResult(intent, 1);
			break;
		case R.id.row_order_type:
			intent = new Intent(this, McOrderTypeActivity.class);
			intent.putExtra("id", order_type_id);
			startActivityForResult(intent, 2);	
			break;
		case R.id.row_company_nature:
			intent = new Intent(this, McCompanyNatureActivity.class);
			intent.putExtra("id", company_nature_id);
			startActivityForResult(intent, 3);
			break;
		}
	}
}
