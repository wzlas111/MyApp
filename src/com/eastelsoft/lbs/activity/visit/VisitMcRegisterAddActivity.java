package com.eastelsoft.lbs.activity.visit;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.activity.BaseActivity;

public class VisitMcRegisterAddActivity extends BaseActivity implements OnClickListener{
	
	private String mId;
	
	private LinearLayout mFrameTable;
	private Button mBackBtn;
	private Button mAddBtn;

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
	}
	
	private void initView() {
		mFrameTable = (LinearLayout)findViewById(R.id.frame_table);
		mAddBtn = (Button)findViewById(R.id.add_btn);
		mBackBtn = (Button)findViewById(R.id.btBack);
		
		mBackBtn.setOnClickListener(this);
		mAddBtn.setOnClickListener(this);
		addTableRow(i++);
	}

	int i = 0;
	private void addTableRow(int row) {
		View view = LayoutInflater.from(this).inflate(R.layout.widget_mc_register_add_table, null);
		((TextView)view.findViewById(R.id.mc_model)).setText("");
		((EditText)view.findViewById(R.id.mc_code)).setText("");
		((EditText)view.findViewById(R.id.mc_reason)).setText("");
		((EditText)view.findViewById(R.id.mc_solver)).setText("");
		
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
		layoutParams.topMargin = 15;
		mFrameTable.addView(view, layoutParams);
		
		i++;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btBack:
			finish();
			break;
		case R.id.add_btn:
			addTableRow(i);
			break;
		}
	}
}
