package com.eastelsoft.lbs.activity.visit;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.eastelsoft.lbs.MyGridView;
import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.activity.select.ClientSelectActivity;
import com.eastelsoft.lbs.activity.select.SignImgActivity;
import com.eastelsoft.lbs.activity.select.SignImgDetailActivity;
import com.eastelsoft.lbs.activity.visit.adapter.GridPhotoAdapter;
import com.eastelsoft.lbs.bean.VisitMcBean;
import com.eastelsoft.lbs.db.VisitMcDBTask;
import com.eastelsoft.lbs.photo.GalleryActivity;
import com.eastelsoft.util.Contant;
import com.eastelsoft.util.GlobalVar;

public class VisitMcDetailActivity extends BaseActivity implements OnClickListener {

	private String mId;
	private VisitMcBean mBean;
	private GridPhotoAdapter mGridAdapter;
	private int mScreenWidth;
	private int mScreenHeight;

	private Button mBackBtn;
	private View mRow_mc_register;
	private View mRow_mc_type;
	private View mRow_mc_person;
	private View mRow_mc_info;
	private TextView client_name;
	private TextView mc_register_write;
	private TextView mc_type_write;
	private TextView mc_person_write;
	private TextView mc_info_write;
	private MyGridView grid_photo;
	private TextView start_time;
	private TextView end_time;
	private TextView repair_start_time;
	private TextView repair_end_time;
	private ImageView sign_img;
	private TextView sign_show;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		globalVar = (GlobalVar) getApplicationContext();
		sp = getSharedPreferences("userdata", 0);
		parseIntent();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		mScreenWidth = dm.widthPixels;
		mScreenHeight = dm.heightPixels;

		setContentView(R.layout.activity_visit_mc_detail);
		initView();
		
		new InitDataTask().execute("");
	}

	private void parseIntent() {
		Intent intent = getIntent();
		mId = intent.getStringExtra("id");
	}

	private void initView() {
		mBackBtn = (Button)findViewById(R.id.btBack);
		mRow_mc_register = findViewById(R.id.row_mc_register);
		mRow_mc_type = findViewById(R.id.row_mc_type);
		mRow_mc_person = findViewById(R.id.row_mc_person);
		mRow_mc_info = findViewById(R.id.row_mc_info);
		client_name = (TextView)findViewById(R.id.client_name);
		mc_register_write = (TextView)findViewById(R.id.mc_register_write);
		mc_type_write = (TextView)findViewById(R.id.mc_type_write);
		mc_person_write = (TextView)findViewById(R.id.mc_person_write);
		mc_info_write = (TextView)findViewById(R.id.mc_info_write);
		
		start_time = (TextView)findViewById(R.id.start_time);
		end_time = (TextView)findViewById(R.id.end_time);
		repair_start_time = (TextView)findViewById(R.id.repair_start_time);
		repair_end_time = (TextView)findViewById(R.id.repair_end_time);
		
		sign_img = (ImageView)findViewById(R.id.sign_img);
		sign_show = (TextView)findViewById(R.id.sign_show);
		
		grid_photo = (MyGridView)findViewById(R.id.gridPhoto);
		
		mBackBtn.setOnClickListener(this);
		mRow_mc_register.setOnClickListener(this);
		mRow_mc_type.setOnClickListener(this);
		mRow_mc_person.setOnClickListener(this);
		mRow_mc_info.setOnClickListener(this);
		sign_img.setOnClickListener(this);
		sign_show.setOnClickListener(this);
	}
	
	private class InitDataTask extends AsyncTask<String, Integer, Boolean> {
		@Override
		protected Boolean doInBackground(String... params) {
			try {
				mBean = VisitMcDBTask.getBean(mId);
			} catch (Exception e) {
				e.printStackTrace();
				mBean = new VisitMcBean();
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
		client_name.setText(mBean.client_name);
		start_time.setText(mBean.start_time);
		end_time.setText(mBean.end_time);
		repair_start_time.setText(mBean.service_start_time);
		repair_end_time.setText(mBean.service_end_time);
		if (TextUtils.isEmpty(mBean.mc_register_json)) {
			mc_register_write.setText("无");
		} else {
			mc_register_write.setText("已填");
		}
		if (TextUtils.isEmpty(mBean.mc_type_json)) {
			mc_type_write.setText("无");
		} else {
			mc_type_write.setText("已填");
		}
		if (TextUtils.isEmpty(mBean.mc_person_json)) {
			mc_person_write.setText("无");
		} else {
			mc_person_write.setText("已填");
		}
		if (TextUtils.isEmpty(mBean.mc_info_json)) {
			mc_info_write.setText("无");
		} else {
			mc_info_write.setText("已填");
		}
		
		initClientSign(mBean.client_sign);
		
		String photos = mBean.upload_img;
		System.out.println("photos : "+photos);
		if (photos != null && photos.length() > 0) {
			String[] photos_path = photos.split("\\|");
			List<String> p_list = new ArrayList<String>();
			for (int i = 0; i < photos_path.length; i++) {
				if (!TextUtils.isEmpty(photos_path[i])) {
					p_list.add(photos_path[i]);
				}
			}
			initGrid(p_list);
		}
	}
	
	private void initClientSign(String path) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inSampleSize = 10;// 图片的长宽都是原来的1/10
		options.inTempStorage = new byte[5 * 1024];
		Bitmap mBitmap = BitmapFactory.decodeFile(path, options);
		sign_img.setImageBitmap(mBitmap);
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.btBack:
			setResult(1);
			finish();
			break;
		case R.id.row_mc_register:
			intent = new Intent(this, VisitMcRegisterDetailActivity.class);
			intent.putExtra("id", mBean.id);
			intent.putExtra("json", mBean.mc_register_json);
			startActivityForResult(intent, 1);
			break;
		case R.id.row_mc_type:
			intent = new Intent(this, VisitMcTypeDetailActivity.class);
			intent.putExtra("id", mBean.id);
			intent.putExtra("json", mBean.mc_type_json);
			startActivityForResult(intent, 2);
			break;
		case R.id.row_mc_person:
			intent = new Intent(this, VisitMcPersonDetailActivity.class);
			intent.putExtra("id", mBean.id);
			intent.putExtra("json", mBean.mc_person_json);
			intent.putExtra("is_repair", mBean.is_repair);
			startActivityForResult(intent, 3);
			break;
		case R.id.row_mc_info:
			intent = new Intent(this, VisitMcInfoDetailActivity.class);
			intent.putExtra("id", mBean.id);
			intent.putExtra("json", mBean.mc_info_json);
			startActivityForResult(intent, 4);
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
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 1: // handler VisitMcRegisterAddActivity
			if (data != null) {
				mc_register_write.setText("已填");
				String jsonString = data.getStringExtra("json");
				mBean.mc_register_json = jsonString;
				System.out.println("mc_register_json: "+jsonString);
			}
			break;
		case 2: // handler VisitMcTypeAddActivity
			if (data != null) {
				mc_type_write.setText("已填");
				String jsonString = data.getStringExtra("json");
				mBean.mc_type_json = jsonString;
				System.out.println("mc_type_json: "+jsonString);
			}
			break;
		case 3: // handler VisitMcPersonAddActivity
			if (data != null) {
				mc_person_write.setText("已填");
				String jsonString = data.getStringExtra("json");
				String is_repair = data.getStringExtra("is_repair");
				mBean.mc_person_json = jsonString;
				mBean.is_repair = is_repair;
				System.out.println("is_repair : "+is_repair);
				System.out.println("mc_person_json: "+jsonString);
			}
			break;
		case 4: // handler VisitMcInfoAddActivity
			if (data != null) {
				mc_info_write.setText("已填");
				String jsonString = data.getStringExtra("json");
				mBean.mc_info_json = jsonString;
				System.out.println("mc_info_json: "+jsonString);
			}
			break;
		}
	}
	
	//display
	private Bitmap[] photos;
	//path
	private String[] photos_path = new String[0];
	private void initGrid(List<String> paths){
		photos = new Bitmap[paths.size()];
		photos_path = new String[paths.size()];
		for (int i = 0; i < paths.size(); i++) {
			photos[i] = BitmapFactory.decodeFile(paths.get(i));
			photos_path[i] = paths.get(i);
		}
		mGridAdapter = new GridPhotoAdapter(this, photos, mScreenWidth, mScreenHeight, "100");
		grid_photo.setAdapter(mGridAdapter);
		grid_photo.setOnItemClickListener(new GridOnItemClick());
	}
	
	private class GridOnItemClick implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
			globalVar.setImgs(photos_path);
			Intent intent = new Intent();
			intent.setClass(VisitMcDetailActivity.this, GalleryActivity.class);
			intent.putExtra("position", position);
			intent.putExtra("type", Contant.VIEW);
			startActivity(intent);
		}
	}
	
}
