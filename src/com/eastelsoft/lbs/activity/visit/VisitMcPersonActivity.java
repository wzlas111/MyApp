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
import android.widget.Toast;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.activity.select.McIsRepairActivity;
import com.eastelsoft.lbs.bean.VisitMcPersonBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class VisitMcPersonActivity extends BaseActivity implements OnClickListener{
	
	private String mId;
	private String mIsRepair;
	private String mJson;
	private List<VisitMcPersonBean> mList;
	
	private LinearLayout mFrameTable;
	private Button mBackBtn;
	private Button mAddBtn;
	private TextView mSaveBtn;
	private View mRow_repair;
	private TextView mMcRepairTv;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parseIntent();
		
		setContentView(R.layout.visit_mc_person_add);
		initView();
	}
	
	private void parseIntent() {
		Intent intent = getIntent();
		mId = intent.getStringExtra("id");
		mJson = intent.getStringExtra("json");
		mIsRepair = intent.getStringExtra("is_repair");
	}
	
	private void initView() {
		mFrameTable = (LinearLayout)findViewById(R.id.frame_table);
		mAddBtn = (Button)findViewById(R.id.add_btn);
		mBackBtn = (Button)findViewById(R.id.btBack);
		mSaveBtn = (TextView)findViewById(R.id.save);
		mMcRepairTv = (TextView)findViewById(R.id.mc_person_repair);
		if (!TextUtils.isEmpty(mIsRepair)) {
			if ("0".equals(mIsRepair)) {
				mMcRepairTv.setText("否");
			} else if("1".equals(mIsRepair)) {
				mMcRepairTv.setText("是");
			}
		}
		mRow_repair = findViewById(R.id.row_mc_person_repair);
		mRow_repair.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(VisitMcPersonActivity.this, McIsRepairActivity.class);
				intent.putExtra("id", mIsRepair);
				startActivityForResult(intent, 1);
			}
		});
		
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
			List<VisitMcPersonBean> mList = gson.fromJson(mJson, new TypeToken<List<VisitMcPersonBean>>(){}.getType());
			for (int i = 0; i < mList.size(); i++) {
				VisitMcPersonBean bean = mList.get(i);
				View view = LayoutInflater.from(this).inflate(R.layout.widget_mc_person_add_table, null);
				
				EditText mc_person_name = ((EditText)view.findViewById(R.id.mc_person_name));
				mc_person_name.setText(bean.name);
				EditText mc_person_tel = ((EditText)view.findViewById(R.id.mc_person_tel));
				mc_person_tel.setText(bean.tel);
				
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
				layoutParams.topMargin = 15;
				mFrameTable.addView(view, layoutParams);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addTableRow() {
		View view = LayoutInflater.from(this).inflate(R.layout.widget_mc_person_add_table, null);
		
		EditText mc_person_name = ((EditText)view.findViewById(R.id.mc_person_name));
		mc_person_name.setText("");
		EditText mc_person_tel = ((EditText)view.findViewById(R.id.mc_person_tel));
		mc_person_tel.setText("");
		
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
		layoutParams.topMargin = 15;
		mFrameTable.addView(view, layoutParams);
	}
	
	private boolean canSend() {
		String text = mMcRepairTv.getText().toString();
		if (TextUtils.isEmpty(text)) {
			Toast.makeText(this, "抢填写是否机修.", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	private void save() {
		if (!canSend()) {
			return;
		}
		String text = mMcRepairTv.getText().toString();
		if ("是".equals(text)) {
			mIsRepair = "1";
		}else if("否".equals(text)) {
			mIsRepair = "0";
		}
		mList = new ArrayList<VisitMcPersonBean>();
		int count = mFrameTable.getChildCount();
		for (int i = 0; i < count; i++) {
			View view = mFrameTable.getChildAt(i);
			VisitMcPersonBean bean = new VisitMcPersonBean();
			bean.id = UUID.randomUUID().toString();
			bean.name = ((EditText)view.findViewById(R.id.mc_person_name)).getText().toString();
			bean.tel = ((EditText)view.findViewById(R.id.mc_person_tel)).getText().toString();
			mList.add(bean);
		}
		
		if (TextUtils.isEmpty(mIsRepair)) {
			setResult(1);
		} else {
			Gson gson = new Gson();
			String jsonString = "";
			if (count == 0) {
				jsonString = "";
			} else {
				jsonString = gson.toJson(mList);
			}
			Intent intent = new Intent(this, VisitMcAddActivity.class);
			intent.putExtra("json", jsonString);
			intent.putExtra("is_repair", mIsRepair);
			setResult(1, intent);
		}
		
		finish();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 1: //mc is repair
			if (data != null) {
				String id = data.getStringExtra("checked_id");
				String name = data.getStringExtra("checked_name");
				mMcRepairTv.setText(name);
				mIsRepair = id;
				if ("0".equals(mIsRepair)) {//否
					mFrameTable.removeAllViews();
					mAddBtn.setVisibility(View.GONE);
				} else if("1".equals(mIsRepair)) {//是
					mAddBtn.setVisibility(View.VISIBLE);
					int count = mFrameTable.getChildCount();
					if (count == 0) {
						addTableRow();
					}
				}
			}
			break;
		}
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
