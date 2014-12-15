package com.eastelsoft.lbs.activity.select;

import java.io.File;

import com.eastelsoft.lbs.R;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class SignImgDetailActivity extends Activity implements OnClickListener {
	
	private String mBasePath = Environment.getExternalStorageDirectory() 
			+ File.separator + "DCIM" + File.separator + "eastelsoft" 
			+ File.separator + "sign" + File.separator;
	private String mPath;
	
	private ImageView mImgShow;
	private Button mBackBtn;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parseIntent();
		
		setContentView(R.layout.activity_visit_mc_sign_detail);
		mImgShow = (ImageView)findViewById(R.id.img_show);
		mBackBtn = (Button)findViewById(R.id.btBack);
		mBackBtn.setOnClickListener(this);
		
		Bitmap bitmap = BitmapFactory.decodeFile(mBasePath+mPath);
		mImgShow.setImageBitmap(bitmap);
	}
	
	private void parseIntent() {
		Intent intent = getIntent();
		mPath = intent.getStringExtra("path");
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btBack:
			finish();
			break;
		}
	}
}
