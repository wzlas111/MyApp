/**
 * Copyright (c) 2013-5-31 www.eastelsoft.com
 * $ID WaitActivity.java 下午1:14:00 $
 */
package com.eastelsoft.lbs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * 进入软件的等待页面
 * 
 * @author lengcj
 */
public class WaitActivity extends Activity{
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_wait);
		start();

	}

	public void start() {
		new Thread() {
			public void run() {
				try {
					Thread.sleep(1 * 1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				Intent intent = new Intent();
				intent.setClass(WaitActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
			}
		}.start();
	}
	

}
