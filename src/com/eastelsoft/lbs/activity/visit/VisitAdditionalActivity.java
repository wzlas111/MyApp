package com.eastelsoft.lbs.activity.visit;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;

import org.apache.http.Header;

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
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.eastelsoft.lbs.MyGridView;
import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.activity.select.ClientDealerActivity;
import com.eastelsoft.lbs.activity.visit.adapter.GridPhotoAdapter;
import com.eastelsoft.lbs.bean.ResultBean;
import com.eastelsoft.lbs.bean.VisitBean;
import com.eastelsoft.lbs.db.VisitDBTask;
import com.eastelsoft.lbs.entity.SetInfo;
import com.eastelsoft.lbs.service.ImgUploadService;
import com.eastelsoft.lbs.service.VisitFinishService;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.FileUtil;
import com.eastelsoft.util.IUtil;
import com.eastelsoft.util.ImageThumbnail;
import com.eastelsoft.util.ImageUtil;
import com.eastelsoft.util.Util;
import com.eastelsoft.util.file.FileManager;
import com.eastelsoft.util.http.HttpRestClient;
import com.eastelsoft.util.http.URLHelper;
import com.google.gson.Gson;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.TextHttpResponseHandler;

public class VisitAdditionalActivity extends BaseActivity implements OnClickListener{
	
	private String mDealer_id = "";
	private VisitBean mBean;
	private GridPhotoAdapter mGridAdapter;
	private int mScreenWidth;
	private int mScreenHeight;
	
	private Button mBackBtn;
	private TextView mSaveDBBtn;
	private TextView mSaveUploadBtn;
	private Button mMechanicBtn;
	private Button mEvaluateBtn;
	private View row_dealer;
	private TextView dealer_name;
	private View row_start_time;
	private View row_arrive_time;
	private TextView start_time;
	private TextView arrive_time;
	private View row_service_start_time;
	private View row_service_end_time;
	private TextView service_start_time;
	private TextView service_end_time;
	private MyGridView grid_photo;
	
	private TextView mechanic_count;
	private TextView is_evaluate;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		parseIntent();
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		mScreenWidth = dm.widthPixels;
		mScreenHeight = dm.heightPixels;
		
		setContentView(R.layout.activity_visit_additional);
		initView();
		
		mBean = new VisitBean();
		mBean.id = UUID.randomUUID().toString();
	}
	
	@Override
	protected void onStop() {
		super.onStop();
	}
	
	private void parseIntent() {
	}
	
	private void initView() {
		mBackBtn = (Button)findViewById(R.id.btBack);
		mSaveDBBtn = (TextView)findViewById(R.id.save_db);
		mSaveUploadBtn = (TextView)findViewById(R.id.save_upload);
		mMechanicBtn = (Button)findViewById(R.id.mechanic_btn);
		mEvaluateBtn = (Button)findViewById(R.id.evaluate_btn);
		row_dealer = findViewById(R.id.row_dealer);
		dealer_name = (TextView)findViewById(R.id.dealer_name);
		row_start_time = findViewById(R.id.row_start_time);
		row_arrive_time = findViewById(R.id.row_arrive_time);
		start_time = (TextView)findViewById(R.id.start_time);
		arrive_time = (TextView)findViewById(R.id.arrive_time);
		row_service_start_time = findViewById(R.id.row_service_start_time);
		row_service_end_time = findViewById(R.id.row_service_end_time);
		service_start_time = (TextView)findViewById(R.id.service_start_time);
		service_end_time = (TextView)findViewById(R.id.service_end_time);
		grid_photo = (MyGridView)findViewById(R.id.gridPhoto);
		
		mechanic_count = (TextView)findViewById(R.id.mechanic_count);
		is_evaluate = (TextView)findViewById(R.id.is_evaluate);
		
		initAdd();
	}
	
	private void initAdd() {
		initGrid();
		
		String now = Util.getLocaleTime("yyyy-MM-dd HH:mm:ss");
		start_time.setText(now);
		arrive_time.setText(now);
		
		mBackBtn.setOnClickListener(this);
		mSaveDBBtn.setText("保存");
		mSaveDBBtn.setOnClickListener(this);
		mSaveUploadBtn.setOnClickListener(this);
		mMechanicBtn.setOnClickListener(this);
		mEvaluateBtn.setOnClickListener(this);
		row_dealer.setOnClickListener(this);
		row_start_time.setOnClickListener(this);
		row_arrive_time.setOnClickListener(this);
		row_service_start_time.setOnClickListener(this);
		row_service_end_time.setOnClickListener(this);
		mechanic_count.setOnClickListener(this);
		is_evaluate.setOnClickListener(this);
	}
	
	private boolean canSend() {
		if (TextUtils.isEmpty(mDealer_id)) {
			Toast.makeText(this, "请选择经销商.", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (TextUtils.isEmpty(start_time.getText().toString())) {
			Toast.makeText(this, "请填写出发时间.", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (TextUtils.isEmpty(arrive_time.getText().toString())) {
			Toast.makeText(this, "请填写抵达时间.", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (TextUtils.isEmpty(service_start_time.getText().toString())) {
			Toast.makeText(this, "请填写服务开始时间.", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (TextUtils.isEmpty(service_end_time.getText().toString())) {
			Toast.makeText(this, "请填写服务结束时间.", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	private void save() {
		if (canSend()) {
			StringBuffer sb = new StringBuffer("");
			for (int i = 0; i < photos_path.length; i++) {
				sb.append(photos_path[i]+"|");
			}
			
			mBean.dealer_id = mDealer_id;
			mBean.dealer_name = dealer_name.getText().toString();
			mBean.start_time = start_time.getText().toString();
			mBean.arrive_time = arrive_time.getText().toString();
			mBean.service_begin_time = service_start_time.getText().toString();
			mBean.service_end_time = service_end_time.getText().toString();
			mBean.is_upload = "0";
			mBean.status = "3";
			mBean.mechanic_count = "0";
			mBean.is_evaluate = "0";
			mBean.upload_date = Util.getLocaleTime("yyyy-MM-dd HH:mm:ss");
			mBean.visit_img = sb.toString();
			mBean.visit_img_num = String.valueOf(photos_path.length);
			
			openPopupWindowPG("数据上传中...");
			
			sp = getSharedPreferences("userdata", 0);
			SetInfo set = IUtil.initSetInfo(sp);
			Gson gson = new Gson();
			String json = gson.toJson(mBean);
			String mUrl = URLHelper.BASE_ACTION;
			RequestParams params = new RequestParams();
			params.put("reqCode", URLHelper.UPDATE_ADDITIONAL);
			params.put("json", json);
			params.put("data_id", mBean.id);
			params.put("gps_id", set.getDevice_id());
			HttpRestClient.post(mUrl, params, new TextHttpResponseHandler() {
				@Override
				public void onSuccess(int statusCode, Header[] headers, String responseString) {
					try {
						popupWindowPg.dismiss();
					} catch (Exception e) {
					}
					try {
						Gson gson = new Gson();
						ResultBean resultBean = gson.fromJson(responseString, ResultBean.class);
						if ("1".equals(resultBean.resultcode)) {
							
							if (photos_path.length > 0) {//upload img
								mBean.status = "4";
								saveDB();
								
								Intent intent = new Intent(VisitAdditionalActivity.this, ImgUploadService.class);
								intent.putExtra("id", mBean.id);
								intent.putExtra("photos_path", photos_path);
								startService(intent);
								Toast.makeText(VisitAdditionalActivity.this, getResources().getString(R.string.upload_form_success), Toast.LENGTH_SHORT).show();
							} else {
								mBean.status = "2";
								mBean.is_upload = "1";
								saveDB();
								Toast.makeText(VisitAdditionalActivity.this, getResources().getString(R.string.upload_success), Toast.LENGTH_SHORT).show();
							}
							finish();
						} else {
							Toast.makeText(VisitAdditionalActivity.this, getResources().getString(R.string.upload_fail), Toast.LENGTH_SHORT).show();
						}
						
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				@Override
				public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
					try {
						try {
							popupWindowPg.dismiss();
						} catch (Exception e) {
						}
						Toast.makeText(VisitAdditionalActivity.this, getResources().getString(R.string.upload_fail), Toast.LENGTH_SHORT).show();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
	
	private void saveDB() {
		VisitDBTask.addAdditionalBean(mBean);
	}
	
	@Override
	public void onClick(View v) {
		Intent intent;
		switch (v.getId()) {
		case R.id.btBack:
			finish();
			break;
		case R.id.save_db:
			save();
			break;
		case R.id.save_upload:
			
			break;
		case R.id.row_dealer:
			intent = new Intent(this, ClientDealerActivity.class);
			intent.putExtra("id", mDealer_id);
			intent.putExtra("type", "3");
			startActivityForResult(intent, 0);
			break;
		case R.id.row_start_time:
			showDatetimeDialog(1);
			break;
		case R.id.row_arrive_time:
			showDatetimeDialog(2);
			break;
		case R.id.row_service_start_time:
			showDatetimeDialog(3);
			break;
		case R.id.row_service_end_time:
			showDatetimeDialog(4);
			break;
		case R.id.mechanic_btn:
			intent = new Intent(this, VisitMcAddActivity.class);
			intent.putExtra("id", mBean.id);
			intent.putExtra("type", "2");
			startActivityForResult(intent, 1);
			break;
		case R.id.evaluate_btn:
			if (!"1".equals(mBean.is_evaluate)) {
				intent = new Intent(this, VisitEvaluateActivity.class);
				intent.putExtra("id", mBean.id);
				startActivityForResult(intent, 2);
			} else {
				Toast.makeText(this, "服务已评.", Toast.LENGTH_SHORT).show();
			}
//			intent = new Intent(this, VisitEvaluateActivity.class);
//			intent.putExtra("id", mBean.id);
//			intent.putExtra("type", "2");
//			startActivityForResult(intent, 2);
			break;
		case R.id.mechanic_count:
			intent = new Intent(this, VisitMcListActivity.class);
			intent.putExtra("id", mBean.id);
			startActivity(intent);
			break;
		case R.id.is_evaluate:
//			if ("1".equals(mBean.is_evaluate)) {
//				intent = new Intent(this, VisitEvaluateDetailActivity.class);
//				intent.putExtra("id", mBean.id);
//				startActivity(intent);
//			}
			break;
		}		
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 0:
			if (data != null) {
				mDealer_id = data.getStringExtra("checked_id");
				String name = data.getStringExtra("checked_name");
				dealer_name.setText(name);
			}
			break;
		case 1: // mc add 
			if (data != null) {
				int success = data.getIntExtra("success", 0);
				if (success == 1) { // add success
					int count = 0;
					try {
						count = Integer.parseInt(mBean.mechanic_count);
					} catch (Exception e) {
						e.printStackTrace();
					}
					mechanic_count.setText("机修记录( "+(count+1)+" )");
					mBean.mechanic_count = String.valueOf(count+1);
					VisitDBTask.updateMechanicCount(mBean);
				}
			}
			break;
		case 2: // evaluate add
			if (data != null) {
				int success = data.getIntExtra("success", 0);
				if (success == 1) { // add success
					is_evaluate.setText("服务评价(已评)");
					mBean.is_evaluate = "1";
					VisitDBTask.updateEvaluate(mBean);
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
	
	private void initGrid(List<String> paths){
		photos = new Bitmap[paths.size()];
		for (int i = 0; i < paths.size(); i++) {
			photos[i] = BitmapFactory.decodeFile(paths.get(i));
		}
		mGridAdapter = new GridPhotoAdapter(this, photos, mScreenWidth, mScreenHeight);
		grid_photo.setAdapter(mGridAdapter);
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
//						takePhoto();
//						choosePhoto();
						openPhotoWindow();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					Toast.makeText(VisitAdditionalActivity.this, getString(R.string.noSDCard), Toast.LENGTH_SHORT).show();
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
					arrive_time.setText(sb.toString());
				} else if(type == 3) {
					service_start_time.setText(sb.toString());
				} else if(type == 4) {
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
}
