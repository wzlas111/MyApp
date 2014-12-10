package com.eastelsoft.lbs.activity.visit;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.eastelsoft.lbs.MyGridView;
import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.activity.visit.adapter.GridPhotoAdapter;
import com.eastelsoft.lbs.bean.VisitBean;
import com.eastelsoft.lbs.db.VisitDBTask;
import com.eastelsoft.util.ImageUtil;

public class VisitFinishActivity extends BaseActivity implements OnClickListener{
	
	private String mId;
	private String[] imgs;
	private VisitBean mBean;
	private GridPhotoAdapter mGridAdapter;
	private int mScreenWidth;
	private int mScreenHeight;
	
	private Button mBackBtn;
	private TextView mSaveDBBtn;
	private Button mMechanicBtn;
	private Button mEvaluateBtn;
	private TextView dealer_name;
	private TextView start_time;
	private TextView start_location;
	private TextView arrive_time;
	private TextView arrive_location;
	private MyGridView grid_photo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parseIntent();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		mScreenWidth = dm.widthPixels;
		mScreenHeight = dm.heightPixels;
		
		setContentView(R.layout.activity_visit_finish);
		initView();
		new DBCacheTask().execute("");
	}
	
	private void parseIntent() {
		Intent intent = getIntent();
		mId = intent.getStringExtra("id");
	}
	
	private void initView() {
		mBackBtn = (Button)findViewById(R.id.btBack);
		mSaveDBBtn = (TextView)findViewById(R.id.save_db);
		mMechanicBtn = (Button)findViewById(R.id.mechanic_btn);
		mEvaluateBtn = (Button)findViewById(R.id.evaluate_btn);
		dealer_name = (TextView)findViewById(R.id.dealer_name);
		start_time = (TextView)findViewById(R.id.start_time);
		start_location = (TextView)findViewById(R.id.start_location);
		arrive_time = (TextView)findViewById(R.id.arrive_time);
		arrive_location = (TextView)findViewById(R.id.arrive_location);
		grid_photo = (MyGridView)findViewById(R.id.gridPhoto);
		
		initGrid();
		
		mBackBtn.setOnClickListener(this);
		mSaveDBBtn.setOnClickListener(this);
		mMechanicBtn.setOnClickListener(this);
		mEvaluateBtn.setOnClickListener(this);
	}
	
	private void initGrid() {
		Resources res = getResources();
		Bitmap bitmap = ImageUtil.drawableToBitmap(res.getDrawable(R.drawable.addphoto_button_normal));
		Bitmap[] mBitmaps = {bitmap};
		mGridAdapter = new GridPhotoAdapter(this, mBitmaps, mScreenWidth, mScreenHeight);
		grid_photo.setAdapter(mGridAdapter);
	}
	
	private class DBCacheTask extends AsyncTask<String, Integer, Boolean> {
		@Override
		protected Boolean doInBackground(String... params) {
			try {
				mBean = VisitDBTask.getBeanById(mId);
			} catch (Exception e) {
				e.printStackTrace();
			}
			return true;
		}
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (mBean != null) {
				fillData();
			}
		}
	}
	
	private void fillData() {
		dealer_name.setText(mBean.dealer_name);
		start_time.setText(mBean.start_time);
		start_location.setText(mBean.start_location);
		arrive_time.setText(mBean.arrive_time);
		arrive_location.setText(mBean.arrive_location);
	}
	
	private void save() {
		
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.btBack:
			finish();
			break;
		case R.id.btSave:
			save();
			break;
		case R.id.mechanic_btn:
			intent = new Intent(this, VisitMcAddActivity.class);
			intent.putExtra("id", mBean.id);
			startActivity(intent);
			break;
		case R.id.evaluate_btn:
			
			break;
		}		
	}
}
