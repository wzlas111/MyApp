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
import com.eastelsoft.lbs.activity.select.McModelActivity;
import com.eastelsoft.lbs.activity.select.McReasonActivity;
import com.eastelsoft.lbs.activity.select.McSolverActivity;
import com.eastelsoft.lbs.bean.VisitMcRegisterBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class VisitMcRegisterActivity extends BaseActivity implements OnClickListener{
	
	private String mId;
	private String mJson;
	private List<VisitMcRegisterBean> mList;
	private String mBrand_id = "";
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
		mAddBtn.setOnClickListener(this);
		mSaveBtn.setOnClickListener(this);
		
		mViewList = new ArrayList<ViewHolder>();
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
				
				TextView mc_model_id = ((TextView)view.findViewById(R.id.mc_model_id));
				mc_model_id.setText(bean.model_id);
				TextView mc_model = ((TextView)view.findViewById(R.id.mc_model));
				mc_model.setText(bean.model_name);
				EditText mc_code = ((EditText)view.findViewById(R.id.mc_code));
				mc_code.setText(bean.code);
				EditText mc_reason = ((EditText)view.findViewById(R.id.mc_reason));
				mc_reason.setText(bean.reason);
				EditText mc_solver = ((EditText)view.findViewById(R.id.mc_solver));
				mc_solver.setText(bean.solver);
				
				ViewHolder holder = new ViewHolder();
				holder.mc_model_id = mc_model_id;
				holder.mc_model = mc_model;
				holder.mc_code = mc_code;
				holder.mc_reason = mc_reason;
				holder.mc_solver = mc_solver;
				mViewList.add(holder);
				
				view.findViewById(R.id.row_model).setOnClickListener(new McModelOnClickListener(i));
				view.findViewById(R.id.row_reason).setOnClickListener(new McReasonOnClickListener(i));
				view.findViewById(R.id.row_solver).setOnClickListener(new McSolverOnClickListener(i));
				
				LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
				layoutParams.topMargin = 15;
				mFrameTable.addView(view, layoutParams);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int i = 0;
	private void addTableRow() {
		View view = LayoutInflater.from(this).inflate(R.layout.widget_mc_register_add_table, null);
		
		TextView mc_model_id = ((TextView)view.findViewById(R.id.mc_model_id));
		mc_model_id.setText("");
		TextView mc_model = ((TextView)view.findViewById(R.id.mc_model));
		mc_model.setText("");
		EditText mc_code = ((EditText)view.findViewById(R.id.mc_code));
		mc_code.setText("");
		EditText mc_reason = ((EditText)view.findViewById(R.id.mc_reason));
		mc_reason.setText("");
		EditText mc_solver = ((EditText)view.findViewById(R.id.mc_solver));
		mc_solver.setText("");
		
		ViewHolder holder = new ViewHolder();
		holder.mc_model_id = mc_model_id;
		holder.mc_model = mc_model;
		holder.mc_code = mc_code;
		holder.mc_reason = mc_reason;
		holder.mc_solver = mc_solver;
		mViewList.add(holder);
		
		//选择机型
		view.findViewById(R.id.row_model).setOnClickListener(new McModelOnClickListener(i));
		view.findViewById(R.id.row_reason).setOnClickListener(new McReasonOnClickListener(i));
		view.findViewById(R.id.row_solver).setOnClickListener(new McSolverOnClickListener(i));
		
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
		layoutParams.topMargin = 15;
		mFrameTable.addView(view, layoutParams);
		i++;
	}
	
	private class McModelOnClickListener implements OnClickListener {
		private int mIndex;
		public McModelOnClickListener(int index){
			mIndex = index;
		}
		@Override
		public void onClick(View v) {
			ViewHolder holder = mViewList.get(mIndex);
			Intent intent = new Intent(VisitMcRegisterActivity.this, McModelActivity.class);
			intent.putExtra("id", holder.mc_model_id.getText().toString());
			intent.putExtra("index", mIndex);
			startActivityForResult(intent, 1);
		}
	}
	
	private class McReasonOnClickListener implements OnClickListener {
		private int mIndex;
		public McReasonOnClickListener(int index){
			mIndex = index;
		}
		@Override
		public void onClick(View v) {
			ViewHolder holder = mViewList.get(mIndex);
			Intent intent = new Intent(VisitMcRegisterActivity.this, McReasonActivity.class);
			intent.putExtra("id", holder.mc_reason.getText().toString());
			intent.putExtra("index", mIndex);
			startActivityForResult(intent, 3);
		}
	}
	
	private class McSolverOnClickListener implements OnClickListener {
		private int mIndex;
		public McSolverOnClickListener(int index){
			mIndex = index;
		}
		@Override
		public void onClick(View v) {
			ViewHolder holder = mViewList.get(mIndex);
			Intent intent = new Intent(VisitMcRegisterActivity.this, McSolverActivity.class);
			intent.putExtra("id", holder.mc_solver.getText().toString());
			intent.putExtra("index", mIndex);
			startActivityForResult(intent, 4);
		}
	}
	
	private void save() {
		mList = new ArrayList<VisitMcRegisterBean>();
		int count = mFrameTable.getChildCount();
		for (int i = 0; i < count; i++) {
			View view = mFrameTable.getChildAt(i);
			VisitMcRegisterBean bean = new VisitMcRegisterBean();
			bean.id = UUID.randomUUID().toString();
			bean.model_id = ((TextView)view.findViewById(R.id.mc_model_id)).getText().toString();
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 1: //mc model
			if (data != null) {
				int index = data.getIntExtra("index", 0);
				String id = data.getStringExtra("checked_id");
				String name = data.getStringExtra("checked_name");
				ViewHolder holder = mViewList.get(index);
				holder.mc_model_id.setText(id);
				holder.mc_model.setText(name);
			}
			break;
		case 3: //mc reason
			if (data != null) {
				int index = data.getIntExtra("index", 0);
				String id = data.getStringExtra("checked_id");
				String name = data.getStringExtra("checked_name");
				ViewHolder holder = mViewList.get(index);
				holder.mc_reason.setText(name);
			}
			break;
		case 4: //mc solver
			if (data != null) {
				int index = data.getIntExtra("index", 0);
				String id = data.getStringExtra("checked_id");
				String name = data.getStringExtra("checked_name");
				ViewHolder holder = mViewList.get(index);
				holder.mc_solver.setText(name);
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
	
	class ViewHolder {
		TextView mc_model_id;
		TextView mc_model;
		EditText mc_code;
		EditText mc_reason;
		EditText mc_solver;
	}
}
