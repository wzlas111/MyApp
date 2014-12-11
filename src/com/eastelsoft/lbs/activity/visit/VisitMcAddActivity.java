package com.eastelsoft.lbs.activity.visit;

import java.util.UUID;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
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
import com.eastelsoft.lbs.bean.VisitMcBean;
import com.eastelsoft.util.ImageUtil;

public class VisitMcAddActivity extends BaseActivity implements OnClickListener {

	private String mId;
	private VisitMcBean mBean;
	private GridPhotoAdapter mGridAdapter;
	private int mScreenWidth;
	private int mScreenHeight;

	private Button mBackBtn;
	private TextView mSaveUploadBtn;
	private View mRow_client_name;
	private View mRow_mc_register;
	private View mRow_mc_type;
	private View mRow_mc_person;
	private View mRow_mc_info;
	private TextView mc_register_write;
	private TextView mc_type_write;
	private TextView mc_person_write;
	private TextView mc_info_write;
	private MyGridView grid_photo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parseIntent();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		mScreenWidth = dm.widthPixels;
		mScreenHeight = dm.heightPixels;

		setContentView(R.layout.activity_visit_mc_add);
		initView();
		mBean = new VisitMcBean();
		mBean.visit_id = mId;
		mBean.id = UUID.randomUUID().toString();
	}

	private void parseIntent() {
		Intent intent = getIntent();
		mId = intent.getStringExtra("id");
	}

	private void initView() {
		mBackBtn = (Button)findViewById(R.id.btBack);
		mSaveUploadBtn = (TextView)findViewById(R.id.save_upload);
		mRow_client_name = findViewById(R.id.row_client_name);
		mRow_mc_register = findViewById(R.id.row_mc_register);
		mRow_mc_type = findViewById(R.id.row_mc_type);
		mRow_mc_person = findViewById(R.id.row_mc_person);
		mRow_mc_info = findViewById(R.id.row_mc_info);
		mc_register_write = (TextView)findViewById(R.id.mc_register_write);
		mc_type_write = (TextView)findViewById(R.id.mc_type_write);
		mc_person_write = (TextView)findViewById(R.id.mc_person_write);
		mc_info_write = (TextView)findViewById(R.id.mc_info_write);
		mc_register_write.setText("");
		mc_type_write.setText("");
		mc_person_write.setText("");
		mc_info_write.setText("");
		
		grid_photo = (MyGridView)findViewById(R.id.gridPhoto);
		
		initGrid();
		
		mBackBtn.setOnClickListener(this);
		mSaveUploadBtn.setOnClickListener(this);
		mRow_client_name.setOnClickListener(this);
		mRow_mc_register.setOnClickListener(this);
		mRow_mc_type.setOnClickListener(this);
		mRow_mc_person.setOnClickListener(this);
		mRow_mc_info.setOnClickListener(this);
	}
	
	private void initGrid() {
		Resources res = getResources();
		Bitmap bitmap = ImageUtil.drawableToBitmap(res.getDrawable(R.drawable.addphoto_button_normal));
		Bitmap[] mBitmaps = {bitmap};
		mGridAdapter = new GridPhotoAdapter(this, mBitmaps, mScreenWidth, mScreenHeight);
		grid_photo.setAdapter(mGridAdapter);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.btBack:
			finish();
			break;
		case R.id.save_upload:
			
			break;
		case R.id.row_client_name:

			break;
		case R.id.row_mc_register:
			intent = new Intent(this, VisitMcRegisterActivity.class);
			intent.putExtra("id", mBean.id);
			intent.putExtra("json", mBean.mc_register_json);
			startActivityForResult(intent, 1);
			break;
		case R.id.row_mc_type:

			break;
		case R.id.row_mc_person:

			break;
		case R.id.row_mc_info:

			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 1: // handler VisitMcRegisterAddActivity
			if (data != null) {
				mc_register_write.setText("已填");
				String jsonString = data.getStringExtra("json");
				mBean.mc_register_json = jsonString;
				System.out.println("mc_register_json"+jsonString);
			}
			break;
		default:
			break;
		}
	}
}
