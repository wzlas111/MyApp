package com.eastelsoft.lbs.activity.visit;

import java.util.Calendar;
import java.util.UUID;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;

import com.eastelsoft.lbs.MyGridView;
import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.activity.select.ClientSelectActivity;
import com.eastelsoft.lbs.activity.select.SignImgActivity;
import com.eastelsoft.lbs.activity.select.SignImgDetailActivity;
import com.eastelsoft.lbs.activity.visit.adapter.GridPhotoAdapter;
import com.eastelsoft.lbs.bean.VisitMcBean;
import com.eastelsoft.lbs.db.VisitMcDBTask;
import com.eastelsoft.util.ImageUtil;
import com.google.gson.Gson;

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
	private TextView client_name;
	private TextView mc_register_write;
	private TextView mc_type_write;
	private TextView mc_person_write;
	private TextView mc_info_write;
	private MyGridView grid_photo;
	private View row_start_time;
	private View row_end_time;
	private View row_repair_start_time;
	private View row_repair_end_time;
	private TextView start_time;
	private TextView end_time;
	private TextView repair_start_time;
	private TextView repair_end_time;
	private ImageView sign_img;
	private TextView sign_show;
	private TextView sign_delete;

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
		client_name = (TextView)findViewById(R.id.client_name);
		mc_register_write = (TextView)findViewById(R.id.mc_register_write);
		mc_type_write = (TextView)findViewById(R.id.mc_type_write);
		mc_person_write = (TextView)findViewById(R.id.mc_person_write);
		mc_info_write = (TextView)findViewById(R.id.mc_info_write);
		mc_register_write.setText("");
		mc_type_write.setText("");
		mc_person_write.setText("");
		mc_info_write.setText("");
		
		row_start_time = findViewById(R.id.row_start_time);
		row_end_time = findViewById(R.id.row_end_time);
		row_repair_start_time = findViewById(R.id.row_repair_start_time);
		row_repair_end_time = findViewById(R.id.row_repair_end_time);
		start_time = (TextView)findViewById(R.id.start_time);
		end_time = (TextView)findViewById(R.id.end_time);
		repair_start_time = (TextView)findViewById(R.id.repair_start_time);
		repair_end_time = (TextView)findViewById(R.id.repair_end_time);
		
		sign_img = (ImageView)findViewById(R.id.sign_img);
		sign_show = (TextView)findViewById(R.id.sign_show);
		sign_delete = (TextView)findViewById(R.id.sign_delete);
		
		grid_photo = (MyGridView)findViewById(R.id.gridPhoto);
		
		initGrid();
		
		mBackBtn.setOnClickListener(this);
		mSaveUploadBtn.setOnClickListener(this);
		mRow_client_name.setOnClickListener(this);
		mRow_mc_register.setOnClickListener(this);
		mRow_mc_type.setOnClickListener(this);
		mRow_mc_person.setOnClickListener(this);
		mRow_mc_info.setOnClickListener(this);
		row_start_time.setOnClickListener(this);
		row_end_time.setOnClickListener(this);
		row_repair_start_time.setOnClickListener(this);
		row_repair_end_time.setOnClickListener(this);
		sign_img.setOnClickListener(this);
		sign_show.setOnClickListener(this);
		sign_delete.setOnClickListener(this);
	}
	
	private void initGrid() {
		Resources res = getResources();
		Bitmap bitmap = ImageUtil.drawableToBitmap(res.getDrawable(R.drawable.addphoto_button_normal));
		Bitmap[] mBitmaps = {bitmap};
		mGridAdapter = new GridPhotoAdapter(this, mBitmaps, mScreenWidth, mScreenHeight);
		grid_photo.setAdapter(mGridAdapter);
	}
	
	private void save() {
		mBean.start_time = start_time.getText().toString();
		mBean.end_time = end_time.getText().toString();
		mBean.service_start_time = repair_start_time.getText().toString();
		mBean.service_end_time = repair_end_time.getText().toString();
		mBean.is_upload = "0";
		
		int success = 0;
		try {
			VisitMcDBTask.addBean(mBean);
			System.out.println(mBean.toString());
			success = 1;
		} catch (Exception e) {
			e.printStackTrace();
		}
		Intent intent = new Intent(this, VisitFinishActivity.class);
		intent.putExtra("success", success);
		setResult(1, intent);
		finish();
	}

	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.btBack:
			setResult(1);
			finish();
			break;
		case R.id.save_upload:
			save();
			break;
		case R.id.row_start_time:
			showDatetimeDialog(1);
			break;
		case R.id.row_end_time:
			showDatetimeDialog(2);
			break;
		case R.id.row_repair_start_time:
			showDatetimeDialog(3);
			break;
		case R.id.row_repair_end_time:
			showDatetimeDialog(4);
			break;
		case R.id.row_client_name:
			intent = new Intent(this, ClientSelectActivity.class);
			intent.putExtra("id", mBean.client_id);
			startActivityForResult(intent, 0);
			break;
		case R.id.row_mc_register:
			intent = new Intent(this, VisitMcRegisterActivity.class);
			intent.putExtra("id", mBean.id);
			intent.putExtra("json", mBean.mc_register_json);
			startActivityForResult(intent, 1);
			break;
		case R.id.row_mc_type:
			intent = new Intent(this, VisitMcTypeActivity.class);
			intent.putExtra("id", mBean.id);
			intent.putExtra("json", mBean.mc_type_json);
			startActivityForResult(intent, 2);
			break;
		case R.id.row_mc_person:
			intent = new Intent(this, VisitMcPersonActivity.class);
			intent.putExtra("id", mBean.id);
			intent.putExtra("json", mBean.mc_person_json);
			intent.putExtra("is_repair", mBean.is_repair);
			startActivityForResult(intent, 3);
			break;
		case R.id.row_mc_info:
			intent = new Intent(this, VisitMcInfoActivity.class);
			intent.putExtra("id", mBean.id);
			intent.putExtra("json", mBean.mc_info_json);
			startActivityForResult(intent, 4);
			break;
		case R.id.sign_img:
			intent = new Intent(this, SignImgActivity.class);
			intent.putExtra("type", "1");
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
			sign_img.setImageDrawable(getResources().getDrawable(R.drawable.addphoto_button_normal));
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 0: // handler ClientSelectActivity
			if (data != null) {
				String checked_id = data.getStringExtra("checked_id");
				String checked_name = data.getStringExtra("checked_name");
				client_name.setText(checked_name);
				mBean.client_id = checked_id;
				mBean.client_name = checked_name;
			}
			break;
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
		case 5: // handler SignImgActivity
			if (data != null) {
				try {
					String sign_path = data.getStringExtra("sign_path");
					String sign_name = data.getStringExtra("sign_name");
					mBean.client_sign = sign_name;
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
		default:
			break;
		}
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
					start_time.setText(sb.toString());
				} else if(type == 2) {
					end_time.setText(sb.toString());
				} else if(type == 3) {
					repair_start_time.setText(sb.toString());
				} else if(type == 4) {
					repair_end_time.setText(sb.toString());
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
}
