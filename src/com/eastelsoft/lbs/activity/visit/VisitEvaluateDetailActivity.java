package com.eastelsoft.lbs.activity.visit;

import java.io.File;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.activity.select.SignImgDetailActivity;
import com.eastelsoft.lbs.bean.VisitEvaluateBean;
import com.eastelsoft.lbs.db.VisitEvaluateDBTask;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class VisitEvaluateDetailActivity extends Activity implements OnClickListener {

	private String mBasePath = Environment.getExternalStorageDirectory() 
			+ File.separator + "DCIM" + File.separator + "eastelsoft" + File.separator + "sign" ;
	
	private String mId;
	private VisitEvaluateBean mBean;
	private String[] mNameList;
	private String[] mValueList;

	private Button mBackBtn;
	private LinearLayout mServicesLayout;
	private TextView visit_num;
	private TextView other_job;
	private TextView advise;
	private ImageView sign_img;
	private TextView sign_show;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		mId = intent.getStringExtra("id");

		setContentView(R.layout.activity_visit_evaluate_detail);
		initView();

		new DBCacheTask().execute("");
	}

	private void initView() {
		mBackBtn = (Button) findViewById(R.id.btBack);
		visit_num = (TextView) findViewById(R.id.visit_num);
		other_job = (TextView) findViewById(R.id.other_job);
		advise = (TextView) findViewById(R.id.advise);
		mServicesLayout = (LinearLayout) findViewById(R.id.services_layout);

		sign_img = (ImageView) findViewById(R.id.sign_img);
		sign_show = (TextView) findViewById(R.id.sign_show);

		mBackBtn.setOnClickListener(this);
		sign_show.setOnClickListener(this);
	}
	
	private class DBCacheTask extends AsyncTask<String, Integer, Boolean> {

		@Override
		protected Boolean doInBackground(String... params) {
			try {
				mBean = VisitEvaluateDBTask.getBeanByVisitId(mId);
			} catch (Exception e) {
				mBean = new VisitEvaluateBean();
				e.printStackTrace();
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			initData();
			initServices();
		}
		
	}

	private void initServices() {
		for (int i = 0; i < mNameList.length; i++) {
			String name = mNameList[i];
			String value = mValueList[i];
			View view = LayoutInflater.from(this).inflate(R.layout.widget_evaluate_tablerow_detail, null);
			((TextView) view.findViewById(R.id.service_name)).setText(name);
			((TextView) view.findViewById(R.id.service_value)).setText(value);
			mServicesLayout.addView(view);
		}
	}
	
	private void initData() {
		mNameList = mBean.service_name.split("\\|");
		mValueList = mBean.service_value.split("\\|");
		visit_num.setText(mBean.visit_num);
		other_job.setText(mBean.other_job);
		advise.setText(mBean.advise);
		if (!TextUtils.isEmpty(mBean.client_sign)) {
			String sign_path = mBasePath + File.separator + mBean.client_sign;
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 10;// 图片的长宽都是原来的1/10
			options.inTempStorage = new byte[5 * 1024];
			Bitmap mBitmap = BitmapFactory.decodeFile(sign_path, options);
			sign_img.setImageBitmap(mBitmap);
			sign_show.setVisibility(View.VISIBLE);
		} else {
			sign_show.setVisibility(View.GONE);
			sign_show.setText("无签名");
			sign_img.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.btBack:
			finish();
			break;
		case R.id.sign_show:
			if (TextUtils.isEmpty(mBean.client_sign)) {
				return;
			}
			intent = new Intent(this, SignImgDetailActivity.class);
			intent.putExtra("path", mBean.client_sign);
			startActivity(intent);
			break;
		}
	}
	
	class ViewHolder {
		TextView service_name;
	}
}
