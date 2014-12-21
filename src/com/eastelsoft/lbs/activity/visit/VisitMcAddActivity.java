package com.eastelsoft.lbs.activity.visit;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
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
import com.eastelsoft.lbs.service.VisitEvaluateService;
import com.eastelsoft.lbs.service.VisitMcService;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.FileUtil;
import com.eastelsoft.util.ImageThumbnail;
import com.eastelsoft.util.ImageUtil;
import com.eastelsoft.util.Util;
import com.eastelsoft.util.file.FileManager;

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
	
	private void save() {
		mBean.start_time = start_time.getText().toString();
		mBean.end_time = end_time.getText().toString();
		mBean.service_start_time = repair_start_time.getText().toString();
		mBean.service_end_time = repair_end_time.getText().toString();
		mBean.is_upload = "0";
		
//		int success = 0;
//		try {
//			VisitMcDBTask.addBean(mBean);
//			System.out.println(mBean.toString());
//			success = 1;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		Intent intent = new Intent(this, VisitFinishActivity.class);
//		intent.putExtra("success", success);
//		setResult(RESULT_OK, intent);
//		finish();
		if (canSend()) {
			Toast.makeText(this, getResources().getString(R.string.upload_visit_mc_background), Toast.LENGTH_SHORT).show();
			
			Intent serviceIntent = new Intent(this, VisitMcService.class);
			Bundle bundle = new Bundle();
			bundle.putParcelable("bean", mBean);
			bundle.putString("id", mBean.visit_id);
			bundle.putStringArray("photos_path", photos_path);
			serviceIntent.putExtras(bundle);
			startService(serviceIntent);
			
			Intent intent = new Intent(this, VisitFinishActivity.class);
			intent.putExtra("success", 1);
			setResult(RESULT_OK, intent);
			
			finish();
		}
	}
	
	private boolean canSend() {
		if (TextUtils.isEmpty(mBean.client_id)) {
			Toast.makeText(this, "请填写客户信息.", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (TextUtils.isEmpty(mBean.mc_register_json)) {
			Toast.makeText(this, "机修记录登录不能为空.", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (TextUtils.isEmpty(mBean.client_sign)) {
			Toast.makeText(this, "签名不能为空.", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
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
		case CAMERA_WITH_DATA:
			if (resultCode != RESULT_OK)
				return;
			try {
				popupWindow.dismiss();
			} catch (Exception e) {
				FileLog.e("VisitFinish", e.toString());
			}
			
			handlePhoto(FileManager.PHOTO_TEST);
			break;
		case PHOTO_PICKED_WITH_DATA:
			if (resultCode != RESULT_OK)
				return;
			try {
				popupWindow.dismiss();
			} catch (Exception e) {
				FileLog.e("VisitFinish", e.toString());
			}
			System.out.println(data);
			if (data == null) {
				Toast.makeText(this, "读取图片失败,请重试.", Toast.LENGTH_SHORT).show();
				return;
			}
			Uri photo_uri = data.getData();
			if (photo_uri == null) {
				Toast.makeText(this, "读取图片失败,请重试.", Toast.LENGTH_SHORT).show();
				return;
			}
			String photo_path = getChoosePath(photo_uri);
			
			handlePhoto(photo_path);
			break;
		}
	}
	
	/**
	 * 以下为图片处理代码
	 */
	// 拍照
	public static final int CAMERA_WITH_DATA = 1001;
	// 选择本地图片
	public static final int PHOTO_PICKED_WITH_DATA = 1002;
	
	//display
	private Bitmap[] photos;
	//path
	private String[] photos_path = new String[0];
	private void initGrid() {
		Resources res = getResources();
		Bitmap bitmap = ImageUtil.drawableToBitmap(res.getDrawable(R.drawable.addphoto_button_normal));
		photos = new Bitmap[1];
		photos[0] = bitmap;
		mGridAdapter = new GridPhotoAdapter(this, photos, mScreenWidth, mScreenHeight);
		grid_photo.setAdapter(mGridAdapter);
		grid_photo.setOnItemClickListener(new GridOnItemClick());
	}
	
	private void displayPhoto(Bitmap bitmap) {
		Resources res = getResources();
		Bitmap add_photo = ImageUtil.drawableToBitmap(res.getDrawable(R.drawable.addphoto_button_normal));
		Bitmap[] temp = photos;
		photos = new Bitmap[temp.length+1];
		//copy
		for (int i = 0; i < temp.length; i++) {
			photos[i] = temp[i];
		}
		photos[temp.length-1] = bitmap;
		photos[temp.length] = add_photo;
		mGridAdapter = new GridPhotoAdapter(this, photos, mScreenWidth, mScreenHeight);
		grid_photo.setAdapter(mGridAdapter);
		grid_photo.setOnItemClickListener(new GridOnItemClick());
	}
	
	private void takePhoto() {
		Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		File dir = new File(FileManager.BASE_PATH);
		if (!dir.exists()) {
			dir.mkdir();
		}
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(FileManager.PHOTO_TEST)));
		startActivityForResult(intent, CAMERA_WITH_DATA);
	}
	
	private void choosePhoto() {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction("android.intent.action.GET_CONTENT");
		Intent chooseIntent = Intent.createChooser(intent, "选择图片");
		startActivityForResult(chooseIntent, PHOTO_PICKED_WITH_DATA);
	}
	
	Cursor cursor = null;
	private String getChoosePath(Uri photoUri) {
		String picPath = "";
		String[] pojo = {MediaStore.Images.Media.DATA};  
        cursor = managedQuery(photoUri, pojo, null, null,null);     
        if(cursor != null )  
        {  
            int columnIndex = cursor.getColumnIndexOrThrow(pojo[0]);  
            cursor.moveToFirst();  
            picPath = cursor.getString(columnIndex);  
        }  
        return picPath;
	}
	
	private void handlePhoto(String photo_path) {
		Bitmap zoomBitmap = null;
		Bitmap saveBitmap = null;
		try {
			String path = photo_path;
			//zoom
			zoomBitmap = ImageThumbnail.PicZoom(path);
			//degree
			int degree = FileUtil.readPictureDegree(path);
			Matrix matrix = new Matrix();
			matrix.postRotate(degree);
			//save
			saveBitmap = Bitmap.createBitmap(zoomBitmap, 0, 0, zoomBitmap.getWidth(), zoomBitmap.getHeight(), matrix, true);
			String filename = Util.getLocaleTime("yyyyMMddHHmmss") + ".jpg";
			String filepath = FileUtil.saveBitmapToFileForPath(saveBitmap, filename);
			
			String[] temp = photos_path;
			photos_path = new String[temp.length+1];
			for (int i = 0; i < temp.length; i++) {
				photos_path[i] = temp[i];
			}
			photos_path[temp.length] = filepath;
			
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 10;// 图片的长宽都是原来的1/10
			FileInputStream f = new FileInputStream(filepath);
			BufferedInputStream bis = new BufferedInputStream(f);
			Bitmap bm = BitmapFactory.decodeStream(bis, null, options);
			displayPhoto(bm);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (zoomBitmap != null) {
				zoomBitmap.recycle();
			}
			if (saveBitmap != null) {
				saveBitmap.recycle();
			}
		}
	}
	
	private class GridOnItemClick implements OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,long id) {
			if (position == photos.length-1) {
				if (FileManager.isExternalStorageMounted()) {
					try {
						openPhotoWindow();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					Toast.makeText(VisitMcAddActivity.this, getString(R.string.noSDCard), Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
	
	private void openPhotoWindow() {
		try {
			LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			ViewGroup menuView = (ViewGroup) mLayoutInflater.inflate(
					R.layout.pop_photo_select, null, true);
			Button btClosex = (Button) menuView.findViewById(R.id.btClose);
			btClosex.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					try {
						popupWindow.dismiss();
					} catch (Exception e) {
						FileLog.e("VisitFinish", e.toString());
					}
				}
			});
			View row_take = menuView.findViewById(R.id.row_take);
			row_take.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					takePhoto();
				}
			});
			View row_choose = menuView.findViewById(R.id.row_choose);
			row_choose.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					choosePhoto();
				}
			});
			popupWindow = new PopupWindow(menuView, LayoutParams.MATCH_PARENT,
					LayoutParams.MATCH_PARENT, true); // 背景
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
			popupWindow.setAnimationStyle(R.style.PopupAnimation);
			popupWindow.showAtLocation(findViewById(R.id.parent),
					Gravity.CENTER | Gravity.CENTER, 0, 0);
			popupWindow.update();
		} catch (Exception e) {
			e.printStackTrace();
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
