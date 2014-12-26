package com.eastelsoft.lbs.activity.visit;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.bean.VisitMcPersonBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class VisitMcPersonDetailActivity extends BaseActivity implements OnClickListener{
	
	private String mId;
	private String mIsRepair = "0";
	private String mJson;
	
	private LinearLayout mFrameTable;
	private Button mBackBtn;
	private Button mAddBtn;
	private TextView mSaveBtn;
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
		
		mBackBtn.setOnClickListener(this);
		mAddBtn.setVisibility(View.GONE);
		mSaveBtn.setVisibility(View.GONE);
		
		if (TextUtils.isEmpty(mJson)) {//add new row
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
				View view = LayoutInflater.from(this).inflate(R.layout.widget_mc_person_detail_table, null);
				
				TextView mc_person_name = ((TextView)view.findViewById(R.id.mc_person_name));
				mc_person_name.setText(bean.name);
				TextView mc_person_tel = ((TextView)view.findViewById(R.id.mc_person_tel));
				mc_person_tel.setText(bean.tel);
				
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
				layoutParams.topMargin = 15;
				mFrameTable.addView(view, layoutParams);
			}
		} catch (Exception e) {
			e.printStackTrace();
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
