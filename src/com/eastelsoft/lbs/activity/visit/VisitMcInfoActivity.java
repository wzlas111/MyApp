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
import com.eastelsoft.lbs.activity.select.McInfoModelActivity;
import com.eastelsoft.lbs.activity.select.McModelActivity;
import com.eastelsoft.lbs.activity.select.McReasonActivity;
import com.eastelsoft.lbs.activity.select.McSolverActivity;
import com.eastelsoft.lbs.bean.VisitMcInfoBean;
import com.eastelsoft.lbs.bean.VisitMcRegisterBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class VisitMcInfoActivity extends BaseActivity implements OnClickListener{
	
	private String mId;
	private String mJson;
	private List<VisitMcInfoBean> mList;
	private List<ViewHolder> mViewList;
	
	private LinearLayout mFrameTable;
	private Button mBackBtn;
	private Button mAddBtn;
	private TextView mSaveBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parseIntent();
		
		setContentView(R.layout.visit_mc_info_add);
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
			List<VisitMcInfoBean> mList = gson.fromJson(mJson, new TypeToken<List<VisitMcInfoBean>>(){}.getType());
			for (int i = 0; i < mList.size(); i++) {
				VisitMcInfoBean bean = mList.get(i);
				View view = LayoutInflater.from(this).inflate(R.layout.widget_mc_info_add_table, null);
				
				TextView mc_info_model_id = ((TextView)view.findViewById(R.id.mc_info_model_id));
				mc_info_model_id.setText(bean.model_id);
				TextView mc_info_model_name = ((TextView)view.findViewById(R.id.mc_info_model_name));
				mc_info_model_name.setText(bean.model_name);
				EditText mc_info_brand = ((EditText)view.findViewById(R.id.mc_info_brand));
				mc_info_brand.setText(bean.brand_name);
				EditText mc_info_spec = ((EditText)view.findViewById(R.id.mc_info_spec));
				mc_info_spec.setText(bean.spec);
				EditText mc_info_num = ((EditText)view.findViewById(R.id.mc_info_num));
				mc_info_num.setText(bean.nums);
				EditText mc_info_years = ((EditText)view.findViewById(R.id.mc_info_years));
				mc_info_years.setText(bean.years);
				
				ViewHolder holder = new ViewHolder();
				holder.mc_model_id = mc_info_model_id;
				holder.mc_model_name = mc_info_model_name;
				mViewList.add(holder);
				
				view.findViewById(R.id.row_model).setOnClickListener(new McModelOnClickListener(i));
				
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
		View view = LayoutInflater.from(this).inflate(R.layout.widget_mc_info_add_table, null);
		
		TextView mc_info_model_id = ((TextView)view.findViewById(R.id.mc_info_model_id));
		mc_info_model_id.setText("");
		TextView mc_info_model_name = ((TextView)view.findViewById(R.id.mc_info_model_name));
		mc_info_model_name.setText("");
		EditText mc_info_brand = ((EditText)view.findViewById(R.id.mc_info_brand));
		mc_info_brand.setText("");
		EditText mc_info_spec = ((EditText)view.findViewById(R.id.mc_info_spec));
		mc_info_spec.setText("");
		EditText mc_info_num = ((EditText)view.findViewById(R.id.mc_info_num));
		mc_info_num.setText("");
		EditText mc_info_years = ((EditText)view.findViewById(R.id.mc_info_years));
		mc_info_years.setText("");
		
		ViewHolder holder = new ViewHolder();
		holder.mc_model_id = mc_info_model_id;
		holder.mc_model_name = mc_info_model_name;
		mViewList.add(holder);
		
		//选择机型
		view.findViewById(R.id.row_info_model).setOnClickListener(new McModelOnClickListener(i));
		
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
			Intent intent = new Intent(VisitMcInfoActivity.this, McInfoModelActivity.class);
			intent.putExtra("id", holder.mc_model_id.getText().toString());
			intent.putExtra("index", mIndex);
			startActivityForResult(intent, 1);
		}
	}
	
	private void save() {
		mList = new ArrayList<VisitMcInfoBean>();
		int count = mFrameTable.getChildCount();
		for (int i = 0; i < count; i++) {
			View view = mFrameTable.getChildAt(i);
			VisitMcInfoBean bean = new VisitMcInfoBean();
			bean.id = UUID.randomUUID().toString();
			bean.model_id = ((TextView)view.findViewById(R.id.mc_info_model_id)).getText().toString();
			bean.model_name = ((TextView)view.findViewById(R.id.mc_info_model_name)).getText().toString();
			bean.brand_name = ((EditText)view.findViewById(R.id.mc_info_brand)).getText().toString();
			bean.spec = ((EditText)view.findViewById(R.id.mc_info_spec)).getText().toString();
			bean.nums = ((EditText)view.findViewById(R.id.mc_info_num)).getText().toString();
			bean.years = ((EditText)view.findViewById(R.id.mc_info_years)).getText().toString();
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
				holder.mc_model_name.setText(name);
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
		TextView mc_model_name;
	}
}
