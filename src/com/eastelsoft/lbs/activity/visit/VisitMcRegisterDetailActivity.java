package com.eastelsoft.lbs.activity.visit;

import java.util.ArrayList;
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
import com.eastelsoft.lbs.bean.VisitMcRegisterBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class VisitMcRegisterDetailActivity extends BaseActivity implements OnClickListener{
	
	private String mId;
	private String mJson;
	private List<ViewHolder> mViewList;
	
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
		mAddBtn.setVisibility(View.GONE);
		mSaveBtn.setVisibility(View.GONE);
		
		mViewList = new ArrayList<ViewHolder>();
		if (TextUtils.isEmpty(mJson)) {//add new row
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
				View view = LayoutInflater.from(this).inflate(R.layout.widget_mc_register_detail_table, null);
				
				TextView mc_model_id = ((TextView)view.findViewById(R.id.mc_model_id));
				mc_model_id.setText(bean.model_id);
				TextView mc_model = ((TextView)view.findViewById(R.id.mc_model));
				mc_model.setText(bean.model_name);
				TextView mc_code = ((TextView)view.findViewById(R.id.mc_code));
				mc_code.setText(bean.code);
				TextView mc_reason = ((TextView)view.findViewById(R.id.mc_reason));
				mc_reason.setText(bean.reason);
				TextView mc_solver = ((TextView)view.findViewById(R.id.mc_solver));
				mc_solver.setText(bean.solver);
				
				ViewHolder holder = new ViewHolder();
				holder.mc_model_id = mc_model_id;
				holder.mc_model = mc_model;
				holder.mc_code = mc_code;
				holder.mc_reason = mc_reason;
				holder.mc_solver = mc_solver;
				mViewList.add(holder);
				
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
	
	class ViewHolder {
		TextView mc_model_id;
		TextView mc_model;
		TextView mc_code;
		TextView mc_reason;
		TextView mc_solver;
	}
}
