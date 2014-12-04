/**
 * Copyright (c) 2013-5-31 www.eastelsoft.com
 * $ID SalesvMainActivity.java 上午9:27:41 $
 */
package com.eastelsoft.lbs;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;

import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.adapter.SalesvMainGridViewAdapter;
import com.eastelsoft.util.FileLog;

/**
 * 销量上报二级菜单页面
 * 
 * @author lengcj
 */
public class SalesvMainActivity extends BaseActivity {
	public static final String TAG = "SalesvMainActivity";
	private GridView maingv;
	private Button btBack;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_salesv_main);

		// 获取到GridView
		maingv = (GridView) this.findViewById(R.id.gv_all);
		maingv.setAdapter(new SalesvMainGridViewAdapter(this));
		maingv.setOnItemClickListener(new MainItemClickListener());
		btBack = (Button) findViewById(R.id.btBack);
		btBack.setOnClickListener(new OnBtBackClickListenerImpl());
	}

	private class MainItemClickListener implements OnItemClickListener {

		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			Intent intent;
			switch (position) {
			case 0:
				intent = new Intent(SalesvMainActivity.this,
						SalesReportActivity.class);
				startActivity(intent);
				break;
			case 1:
				intent = new Intent(SalesvMainActivity.this,
						SalestaskQueryActivity.class);
				startActivity(intent);
				break;
			case 2:
				intent = new Intent(SalesvMainActivity.this,
						SalestaskAllocationActivity.class);
				startActivity(intent);
				break;
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	private class OnBtBackClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				SalesvMainActivity.this.finish();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}
}
