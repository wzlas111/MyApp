package com.eastelsoft.util.image;

import java.util.ArrayList;
import java.util.List;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.activity.visit.VisitAdditionalActivity;
import com.eastelsoft.lbs.activity.visit.VisitFinishActivity;
import com.eastelsoft.lbs.activity.visit.VisitMcAddActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.GridView;
import android.widget.Toast;

public class SelectImageActivity extends Activity {
	
	private String type;
	private int left_num;
	
	private View mBackBtn;
	private View mSaveBtn;
	private GridView mGridView;
	private List<String> list;
	private SelectImageAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		type = getIntent().getStringExtra("type");
		left_num = getIntent().getIntExtra("left_num", 0);
		
		setContentView(R.layout.activity_select_image);
		
		mBackBtn = findViewById(R.id.btBack);
		mBackBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				finish();
			}
		});
		mSaveBtn = findViewById(R.id.btSave);
		mSaveBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				ArrayList<String> list = adapter.getSelectItems();
				if (list.size() > left_num) {//超过剩余图片
					Toast.makeText(SelectImageActivity.this, "最多还可以上传 "+left_num+" 图片.", Toast.LENGTH_SHORT).show();
					return;
				}
				Intent intent = new Intent();
				if ("1".equals(type)) {//拜访图片
					intent.setClass(SelectImageActivity.this, VisitFinishActivity.class);
				} else if("2".equals(type)) {//拜访补录图片
					intent.setClass(SelectImageActivity.this, VisitAdditionalActivity.class);
				} else if("3".equals(type)) {//机修图片
					intent.setClass(SelectImageActivity.this, VisitMcAddActivity.class);
				}
				intent.putStringArrayListExtra("paths", list);
				setResult(RESULT_OK,intent);
				finish();
			}
		});
		mGridView = (GridView) findViewById(R.id.child_grid);
		
		list = new ArrayList<String>();
		adapter = new SelectImageAdapter(this, list, mGridView);
		mGridView.setAdapter(adapter);
		
		getImages();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
	}
	
	private final static int SCAN_OK = 1;
	private ProgressDialog mProgressDialog;
	private void getImages() {
		if(!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			Toast.makeText(this, "图片加载失败.", Toast.LENGTH_SHORT).show();
			return;
		}
		mProgressDialog = ProgressDialog.show(this, null, "图片加载中...");
		new Thread(new Runnable() {
			@Override
			public void run() {
				Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
				ContentResolver mContentResolver = SelectImageActivity.this.getContentResolver();
				Cursor mCursor = mContentResolver.query(mImageUri, null,
						MediaStore.Images.Media.MIME_TYPE + "=? or "
					  + MediaStore.Images.Media.MIME_TYPE + "=? or "
					  + MediaStore.Images.Media.MIME_TYPE + "=?",
						new String[] { "image/jpg", "image/jpeg", "image/png" }, MediaStore.Images.Media.DATE_MODIFIED+" DESC");
				
				while (mCursor.moveToNext()) {
					String path = mCursor.getString(mCursor.getColumnIndex(MediaStore.Images.Media.DATA));
					list.add(path);
				}
				mCursor.close();
				mHandler.sendEmptyMessage(SCAN_OK);
			}
		}).start();
	}
	
	private Handler mHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case SCAN_OK:
				mProgressDialog.dismiss();
				adapter = new SelectImageAdapter(SelectImageActivity.this, list, mGridView);
				mGridView.setAdapter(adapter);
				break;
			}
		}
	};
	
}
