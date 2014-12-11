package com.eastelsoft.lbs.activity.visit;

import java.util.Calendar;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

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
	private View row_service_start_time;
	private View row_service_end_time;
	private TextView service_start_time;
	private TextView service_end_time;
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
		row_service_start_time = findViewById(R.id.row_service_start_time);
		row_service_end_time = findViewById(R.id.row_service_end_time);
		service_start_time = (TextView)findViewById(R.id.service_start_time);
		service_end_time = (TextView)findViewById(R.id.service_end_time);
		grid_photo = (MyGridView)findViewById(R.id.gridPhoto);
		
		initGrid();
		
		mBackBtn.setOnClickListener(this);
		mSaveDBBtn.setOnClickListener(this);
		mMechanicBtn.setOnClickListener(this);
		mEvaluateBtn.setOnClickListener(this);
		row_service_start_time.setOnClickListener(this);
		row_service_end_time.setOnClickListener(this);
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
		VisitBean bean = new VisitBean();
		bean.id = mId;
		bean.service_begin_time = service_start_time.getText().toString();
		bean.service_end_time = service_end_time.getText().toString();
		bean.is_upload = "0";
		bean.status = "2";
		
		VisitDBTask.updateFinishBean(bean);
		finish();
	}
	
	private void showDatetimeDialog(final int type) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		View view = View.inflate(this, R.layout.widget_select_datetime, null);
		final DatePicker datePicker = (DatePicker)view.findViewById(R.id.date_picker);
		final TimePicker timePicker = (TimePicker)view.findViewById(R.id.time_picker);
		builder.setView(view);
		
		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(System.currentTimeMillis());
		datePicker.init(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), null);
		timePicker.setIs24HourView(true);
		timePicker.setCurrentHour(calendar.get(Calendar.HOUR_OF_DAY));
		timePicker.setCurrentMinute(calendar.get(Calendar.MINUTE));
		
		builder.setTitle("选取时间");
		builder.setPositiveButton("确 定", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				StringBuffer sb = new StringBuffer();
				sb.append(String.format("%d-%02d-%02d", datePicker.getYear(), datePicker.getMonth()+1, datePicker.getDayOfMonth()));
				sb.append(" ");
				sb.append(String.format("%02d", timePicker.getCurrentHour()));
				sb.append(":");
				sb.append(String.format("%02d", timePicker.getCurrentMinute()));
				sb.append(":00");
				if (type == 1) {
					service_start_time.setText(sb.toString());
				} else if(type == 2) {
					service_end_time.setText(sb.toString());
				}
				dialog.cancel();
			}
		});
		
		datePicker.clearFocus();
		timePicker.clearFocus();
		
		InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(datePicker.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(timePicker.getWindowToken(), 0);
		Dialog dialog = builder.create();
		dialog.show();
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
		case R.id.row_service_start_time:
			showDatetimeDialog(1);
			break;
		case R.id.row_service_end_time:
			showDatetimeDialog(2);
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
