package com.eastelsoft.lbs.activity.visit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.bean.VisitMcRegisterBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class VisitMcRegisterActivity extends BaseActivity implements OnClickListener{
	
	private String mId;
	private String mJson;
	private List<VisitMcRegisterBean> mList;
	private String mBrand_id = "";
	
	private LinearLayout mFrameTable;
	private Button mBackBtn;
	private Button mAddBtn;
	private TextView mSaveBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parseIntent();
		
		setContentView(R.layout.visit_mc_register_add);
		initView();
	}
	
	private void parseIntent() {
		Intent intent = getIntent();
		mId = intent.getStringExtra("id");
		mJson = intent.getStringExtra("json");
	}
	
	private void initView() {
		mFrameTable = (LinearLayout)findViewById(R.id.frame_table);
		mAddBtn = (Button)findViewById(R.id.add_btn);
		mBackBtn = (Button)findViewById(R.id.btBack);
		mSaveBtn = (TextView)findViewById(R.id.save);
		
		mBackBtn.setOnClickListener(this);
		mAddBtn.setOnClickListener(this);
		mSaveBtn.setOnClickListener(this);
		
		if (TextUtils.isEmpty(mJson)) {//add new row
			addTableRow();
		} else {//fill data
			fillData();
		}
	}
	
	private void fillData() {
		try {
			Gson gson = new Gson();
			List<VisitMcRegisterBean> mList = gson.fromJson(mJson, new TypeToken<List<VisitMcRegisterBean>>(){}.getType());
			for (int i = 0; i < mList.size(); i++) {
				VisitMcRegisterBean bean = mList.get(i);
				View view = LayoutInflater.from(this).inflate(R.layout.widget_mc_register_add_table, null);
				((TextView)view.findViewById(R.id.mc_model_id)).setText(bean.model_id);
				((TextView)view.findViewById(R.id.mc_model)).setText(bean.model_name);
				((EditText)view.findViewById(R.id.mc_code)).setText(bean.code);
				((EditText)view.findViewById(R.id.mc_reason)).setText(bean.reason);
				((EditText)view.findViewById(R.id.mc_solver)).setText(bean.solver);
				
				//选择机型
				view.findViewById(R.id.row_model).setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						
					}
				});
				
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
				layoutParams.topMargin = 15;
				mFrameTable.addView(view, layoutParams);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addTableRow() {
		View view = LayoutInflater.from(this).inflate(R.layout.widget_mc_register_add_table, null);
		((TextView)view.findViewById(R.id.mc_model_id)).setText("");
		((TextView)view.findViewById(R.id.mc_model)).setText("");
		((EditText)view.findViewById(R.id.mc_code)).setText("");
		((EditText)view.findViewById(R.id.mc_reason)).setText("");
		((EditText)view.findViewById(R.id.mc_solver)).setText("");
		
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
		layoutParams.topMargin = 15;
		mFrameTable.addView(view, layoutParams);
		
	}
	
	private void save() {
		mList = new ArrayList<VisitMcRegisterBean>();
		int count = mFrameTable.getChildCount();
		for (int i = 0; i < count; i++) {
			View view = mFrameTable.getChildAt(i);
			VisitMcRegisterBean bean = new VisitMcRegisterBean();
			bean.id = UUID.randomUUID().toString();
			bean.model_id = mBrand_id;
			bean.model_name = ((TextView)view.findViewById(R.id.mc_model)).getText().toString();
			bean.code = ((EditText)view.findViewById(R.id.mc_code)).getText().toString();
			bean.reason = ((EditText)view.findViewById(R.id.mc_reason)).getText().toString();
			bean.solver = ((EditText)view.findViewById(R.id.mc_solver)).getText().toString();
			mList.add(bean);
		}
		if (count == 0) {
			setResult(1);
		} else {
			Gson gson = new Gson();
			String jsonString = gson.toJson(mList);
			Intent intent = new Intent(this, VisitMcAddActivity.class);
			intent.putExtra("json", jsonString);
			setResult(1, intent);
		}
		
		finish();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btBack:
			setResult(1);
			finish();
			break;
		case R.id.add_btn:
			addTableRow();
			break;
		case R.id.save:
			save();
			break;
		}
	}
}
