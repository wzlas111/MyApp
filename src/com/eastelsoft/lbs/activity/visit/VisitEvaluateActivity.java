package com.eastelsoft.lbs.activity.visit;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.activity.select.SignImgActivity;
import com.eastelsoft.lbs.activity.select.SignImgDetailActivity;
import com.eastelsoft.lbs.bean.VisitEvaluateBean;
import com.eastelsoft.lbs.db.VisitEvaluateDBTask;
import com.eastelsoft.lbs.service.VisitEvaluateService;
import com.eastelsoft.lbs.service.VisitFinishService;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

public class VisitEvaluateActivity extends Activity implements OnClickListener {

	private String mId;
	private String mType;
	private VisitEvaluateBean mBean;
	private String mServiceName = "";
	private String mServiceValue = "";
	private List<String> mList;

	private Button mBackBtn;
	private TextView mSaveUploadBtn;
	private LinearLayout mServicesLayout;
	private EditText visit_num;
	private EditText other_job;
	private EditText advise;
	private ImageView sign_img;
	private TextView sign_show;
	private TextView sign_delete;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		mId = intent.getStringExtra("id");
		mType = intent.getStringExtra("type");

		intiData();
		setContentView(R.layout.activity_visit_evaluate);
		initView();

		mBean = new VisitEvaluateBean();
		mBean.id = UUID.randomUUID().toString();
		mBean.visit_id = mId;
	}

	private void initView() {
		mBackBtn = (Button) findViewById(R.id.btBack);
		mSaveUploadBtn = (TextView) findViewById(R.id.save_upload);
		visit_num = (EditText) findViewById(R.id.visit_num);
		other_job = (EditText) findViewById(R.id.other_job);
		advise = (EditText) findViewById(R.id.advise);
		mServicesLayout = (LinearLayout) findViewById(R.id.services_layout);

		sign_img = (ImageView) findViewById(R.id.sign_img);
		sign_show = (TextView) findViewById(R.id.sign_show);
		sign_delete = (TextView) findViewById(R.id.sign_delete);

		mBackBtn.setOnClickListener(this);
		mSaveUploadBtn.setOnClickListener(this);
		sign_img.setOnClickListener(this);
		sign_show.setOnClickListener(this);
		sign_delete.setOnClickListener(this);

		initServices();
	}

	private void initServices() {
		for (int i = 0; i < mList.size(); i++) {
			String name = mList.get(i);
			View view = LayoutInflater.from(this).inflate(
					R.layout.widget_evaluate_tablerow, null);
			((TextView) view.findViewById(R.id.service_name)).setText(name);
			mServicesLayout.addView(view);
		}
	}

	private void intiData() {
		mList = new ArrayList<String>();
		mList.add("退货/零配件清退");
		mList.add("处理疑难问题");
		mList.add("店面培训+工厂培训");
		mList.add("聚焦产品跟踪");
		mList.add("新品推广宣导");
		mList.add("售前服务");
		mList.add("协助经销商展会");
		mList.add("和经销商沟通");
	}

	private void save() {
		getServices();
		mBean.visit_num = visit_num.getText().toString();
		mBean.other_job = other_job.getText().toString();
		mBean.advise = advise.getText().toString();
		mBean.is_upload = "0";
		mBean.service_name = mServiceName;
		mBean.service_value = mServiceValue;
		
		if (canSend()) {
			Toast.makeText(this, getResources().getString(R.string.upload_visit_evaluate_background), Toast.LENGTH_SHORT).show();
			
			Intent serviceIntent = new Intent(this, VisitEvaluateService.class);
			Bundle bundle = new Bundle();
			bundle.putParcelable("bean", mBean);
			bundle.putString("id", mBean.visit_id);
			serviceIntent.putExtras(bundle);
			startService(serviceIntent);
			
			Intent intent = new Intent(this, VisitFinishActivity.class);
			if ("2".equals(mType)) {
				intent = new Intent(this, VisitAdditionalActivity.class);
			}
			intent.putExtra("success", 1);
			setResult(RESULT_OK, intent);
			
			finish();
		}
	}
	
	private boolean canSend() {
		if (TextUtils.isEmpty(mBean.visit_num)) {
			Toast.makeText(this, "走访工厂数量不能为空.", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (TextUtils.isEmpty(mBean.client_sign)) {
			Toast.makeText(this, "签名不能为空.", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}

	private void getServices() {
		int count = mServicesLayout.getChildCount();
		for (int i = 0; i < count; i++) {
			View view = mServicesLayout.getChildAt(i);
			mServiceName += ((TextView) view.findViewById(R.id.service_name))
					.getText().toString()+"|";
			RadioGroup radioGroup = (RadioGroup) view
					.findViewById(R.id.service_value);
			int checked_id = radioGroup.getCheckedRadioButtonId();
			switch (checked_id) {
			case R.id.level3:
				mServiceValue += "优秀|";
				break;
			case R.id.level2:
				mServiceValue += "良好|";
				break;
			case R.id.level1:
				mServiceValue += "一般|";
				break;
			case R.id.level0:
				mServiceValue += "差|";
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.btBack:
			setResult(2);
			finish();
			break;
		case R.id.save_upload:
			save();
			break;
		case R.id.sign_img:
			intent = new Intent(this, SignImgActivity.class);
			intent.putExtra("type", "2");
			startActivityForResult(intent, 5);
			break;
		case R.id.sign_show:
			if (TextUtils.isEmpty(mBean.client_sign)) {
				return;
			}
			intent = new Intent(this, SignImgDetailActivity.class);
			intent.putExtra("path", mBean.client_sign);
			startActivity(intent);
			break;
		case R.id.sign_delete:
			sign_img.setClickable(true);
			sign_show.setVisibility(View.GONE);
			sign_delete.setVisibility(View.GONE);
			sign_img.setImageDrawable(getResources().getDrawable(
					R.drawable.addphoto_button_normal));
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 5: // handler SignImgActivity
			if (data != null) {
				try {
					String sign_path = data.getStringExtra("sign_path");
					String sign_name = data.getStringExtra("sign_name");
					mBean.client_sign = sign_path;
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 10;// 图片的长宽都是原来的1/10
					options.inTempStorage = new byte[5 * 1024];
					Bitmap mBitmap = BitmapFactory.decodeFile(sign_path, options);
					sign_img.setImageBitmap(mBitmap);
					sign_img.setClickable(false);
					sign_show.setVisibility(View.VISIBLE);
					sign_delete.setVisibility(View.VISIBLE);
					System.out.println("client_sign: "+ sign_path);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			break;
		}
	}

	class ViewHolder {
		TextView service_name;
	}
}
