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
import com.eastelsoft.lbs.bean.VisitMcInfoBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

public class VisitMcInfoDetailActivity extends BaseActivity implements OnClickListener{
	
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
			List<VisitMcInfoBean> mList = gson.fromJson(mJson, new TypeToken<List<VisitMcInfoBean>>(){}.getType());
			for (int i = 0; i < mList.size(); i++) {
				VisitMcInfoBean bean = mList.get(i);
				View view = LayoutInflater.from(this).inflate(R.layout.widget_mc_info_detail_table, null);
				
				TextView mc_info_model_id = ((TextView)view.findViewById(R.id.mc_info_model_id));
				mc_info_model_id.setText(bean.model_id);
				TextView mc_info_model_name = ((TextView)view.findViewById(R.id.mc_info_model_name));
				mc_info_model_name.setText(bean.model_name);
				TextView mc_info_brand = ((TextView)view.findViewById(R.id.mc_info_brand));
				mc_info_brand.setText(bean.brand_name);
				TextView mc_info_spec = ((TextView)view.findViewById(R.id.mc_info_spec));
				mc_info_spec.setText(bean.spec);
				TextView mc_info_num = ((TextView)view.findViewById(R.id.mc_info_num));
				mc_info_num.setText(bean.nums);
				TextView mc_info_years = ((TextView)view.findViewById(R.id.mc_info_years));
				mc_info_years.setText(bean.years);
				
				ViewHolder holder = new ViewHolder();
				holder.mc_model_id = mc_info_model_id;
				holder.mc_model_name = mc_info_model_name;
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
		TextView mc_model_name;
	}
}
