/**
 * Copyright (c) 2013-6-9 www.eastelsoft.com
 * $ID MsgAmindActivity.java 上午9:38:15 $
 */
package com.eastelsoft.lbs;

import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.util.FileLog;

/**
 * 消息提醒
 * 
 * @author lengcj
 */
public class MsgAmindActivity extends BaseActivity {

	private static final String TAG = "MsgAmindActivity";
	private Button btBack;

	ImageView set_shock_select_cb;

	ImageView set_msg_select_cb;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_msg_amind);
		btBack = (Button) findViewById(R.id.btBack);
		btBack.setOnClickListener(new OnBtBackClickListenerImpl());
		set_shock_select_cb = (ImageView) findViewById(R.id.set_shock_select_cb);
		set_shock_select_cb.setOnClickListener(new OnShockClickListenerImpl());
		set_msg_select_cb = (ImageView) findViewById(R.id.set_msg_select_cb);
		set_msg_select_cb.setOnClickListener(new OnMsgClickListenerImpl());

		// 设置开关项，前面的取参数过程需要修改
		sp = getSharedPreferences("userdata", 0);
		String shock_select = sp.getString("shock_select", "1");
		String msg_select = sp.getString("msg_select", "1");
		if ("1".equals(shock_select)) {
			set_shock_select_cb.setImageResource(R.drawable.images_on);
		} else {
			set_shock_select_cb.setImageResource(R.drawable.images_off);
		}
		if ("1".equals(msg_select)) {
			set_msg_select_cb.setImageResource(R.drawable.images_on);
		} else {
			set_msg_select_cb.setImageResource(R.drawable.images_off);
		}

	}

	@Override
	protected void onDestroy() {
		FileLog.i(TAG, "onDestroy....");
		super.onDestroy();
	}

	private class OnShockClickListenerImpl implements OnClickListener {

		public void onClick(View v) {
			sp = getSharedPreferences("userdata", 0);
			String shock_select = sp.getString("shock_select", "1");
			if ("1".equals(shock_select)) {
				set_shock_select_cb.setImageResource(R.drawable.images_off);
				Editor editor = sp.edit();
				editor.putString("shock_select", "0");
				editor.commit();
			} else {
				set_shock_select_cb.setImageResource(R.drawable.images_on);
				Editor editor = sp.edit();
				editor.putString("shock_select", "1");
				editor.commit();
			}
		}
	}

	private class OnMsgClickListenerImpl implements OnClickListener {

		public void onClick(View v) {
			sp = getSharedPreferences("userdata", 0);
			String msg_select = sp.getString("msg_select", "1");
			if ("1".equals(msg_select)) {
				set_msg_select_cb.setImageResource(R.drawable.images_off);
				Editor editor = sp.edit();
				editor.putString("msg_select", "0");
				editor.commit();
			} else {
				set_msg_select_cb.setImageResource(R.drawable.images_on);
				Editor editor = sp.edit();
				editor.putString("msg_select", "1");
				editor.commit();
			}
		}
	}

	private class OnBtBackClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				MsgAmindActivity.this.finish();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

}
