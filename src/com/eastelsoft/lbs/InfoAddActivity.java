/*
 * Copyright (c) 2012-8-13 www.eastelsoft.com
 * $ID InfoAddActivity.java 下午9:47:37 $
*/
package com.eastelsoft.lbs;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.media.ExifInterface;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.ThumbnailUtils;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaRecorder.AudioSource;
import android.media.MediaRecorder.OutputFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;
import com.eastelsoft.lbs.InfoViewActivity.GridImageAdapter;
import com.eastelsoft.lbs.InfoViewActivity.OnItemClick;
import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.db.DBUtil;
import com.eastelsoft.lbs.db.LocationSQLiteHelper;
import com.eastelsoft.lbs.entity.InfoAddBean;
import com.eastelsoft.lbs.entity.SetInfo;
import com.eastelsoft.lbs.location.AMapAction;
import com.eastelsoft.lbs.location.BaiduMapAction;
import com.eastelsoft.lbs.photo.GalleryActivity;
import com.eastelsoft.lbs.photo.ImageViewExt;
import com.eastelsoft.lbs.service.LocationService;
import com.eastelsoft.lbs.service.LocationService.MBinder;
import com.eastelsoft.util.CallBack;
import com.eastelsoft.util.Contant;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.FileUtil;
import com.eastelsoft.util.GlobalVar;
import com.eastelsoft.util.IUtil;
import com.eastelsoft.util.ImageThumbnail;
import com.eastelsoft.util.ImageUtil;
import com.eastelsoft.util.LonlatExchange;
import com.eastelsoft.util.Util;
import com.eastelsoft.util.http.AndroidHttpClient;

/**
 * 新增信息上报
 * 
 * @author lengcj
 */
public class InfoAddActivity extends BaseActivity {
	public static final String TAG = "InfoAddActivity";
	private Button btBack;
	private Button btLocation;
	private Button btRecorderVideo;
	private int countdown;
	private MediaRecorder mr;
	private Button soundrecord;
	private ImageView imageView;
	private TextView recording_textView;
	private TextView textRecord_time;
	final String filepath = Environment.getExternalStorageDirectory()+"/DCIM/eastelsoft/";
	private boolean is_recording = false, recorded = false;
	public static MediaPlayer mMediaPlayer = new MediaPlayer();
	public boolean is_Play=true;
	public String setLongtime;
	private Button btSaveInfo;
	private ImageView imageVideo;
	private ImageView clearVideo;
	private EditText etTitle;
	private EditText etContent;
	private TextView tvInfoLocationDesc;
	private TextView tvInfoLocationTip;
	private LinearLayout llInfoLocationId;
	private LinearLayout llInfoVideoId;
	// 拍照
	private static final int CAMERA_WITH_DATA = 1001;
	// 选择本地图片
	private static final int PHOTO_PICKED_WITH_DATA = 1002;
	// 删除图
	private static final int PHOTO_DEL = 99990;
//	// 视频录制 - 自定义
	private static final int RECORDER_VIDEO = 2001;
	// 视频录制 - 调用系统
	private static final int RECORDER_VIDEO_SYS = 2002;
	// 上传的文件
//	private Bitmap bitMap_pic=null;
	private String uploadDate;
	private String id_info;
	private String title;
	private String imgFileName1;
	private String videoName;
	private String reSoundName="";
	private File videoFile;
	private String remark;
	private String lon = "";
	private String lat = "";
	private String location = "";
	private DisplayMetrics dm;
	private GridImageAdapter ia;
	private GridView g;
	private int imageCol = 4;
	// 图片存儲
	private Bitmap[] bms =null;
	// 图片路径
	private String[] imgs = new String[0];
	private LocationSQLiteHelper locationHelper;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		globalVar = (GlobalVar) getApplicationContext();
		if(savedInstanceState!=null){
			title = savedInstanceState.getString("title");
			globalVar.setTitle(title);
			remark = savedInstanceState.getString("remark");
			globalVar.setRemark(remark);
			lon = savedInstanceState.getString("lon");
			lat = savedInstanceState.getString("lat");
			location= savedInstanceState.getString("location");
			reSoundName = savedInstanceState.getString("reSoundName");
			globalVar.setReSoundName(reSoundName);
			videoName = savedInstanceState.getString("videoName");
			globalVar.setVideo1(videoName);
			imgs = savedInstanceState.getStringArray("imgs");
			globalVar.setImgs(imgs);
			id_info = savedInstanceState.getString("id_info");
			setLongtime = savedInstanceState.getString("setLongtime");
		}
		setContentView(R.layout.activity_infoadd);
		// 初始化视图控件
		initView();
		// 初始化数据库
		locationHelper = new LocationSQLiteHelper(this, null, null, 5);
		// 初始化全局变量
		// 初始化图片、视频、定位参数，解决三星等手机屏幕切换导致数据丢失的问题
		Log.i("InfoAddActivity","oncreate");
		networkAvailable = isNetworkAvailable();

		if (!networkAvailable) {
			respMsg = getResources().getString(R.string.net_error);
			Toast.makeText(getApplicationContext(), respMsg, Toast.LENGTH_SHORT)
					.show();
		}
		this.startService(new Intent(this, LocationService.class));
		Intent intent = new Intent("com.eastelsoft.lbs.service.LocationService");
		this.getApplicationContext().bindService(intent, sc,
				Context.BIND_AUTO_CREATE);
	}

	private LocationService locationService;
	private boolean mBound = false;
	/** 定交ServiceConnection，用于绑定Service的 */
	private ServiceConnection sc = new ServiceConnection() { 

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			// 已经绑定了LocalService，强转IBinder对象，调用方法得到LocalService对象
			MBinder binder = (MBinder) service;
			locationService = binder.getService();
			mBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
		}
	};

	private void initView() {
		btBack = (Button) findViewById(R.id.btBack);
		btBack.setOnClickListener(new OnBtBackClickListenerImpl());
		soundrecord = (Button) findViewById(R.id.soundrecord);
		soundrecord.setOnClickListener(new OnbtRecoredSoundClickListenerImpl());
		recording_textView = (TextView) findViewById(R.id.record_text);
		imageView = (ImageView) findViewById(R.id.record_imageView1);
		imageView.setOnLongClickListener(new RecoredSoundimageViewClickListenerIMpl());
		imageView.setOnClickListener(new RecordSoundPlayClickListenerImpl());
		textRecord_time = (TextView) findViewById(R.id.textRecord_time);
		btRecorderVideo = (Button) findViewById(R.id.btVideo);
		btRecorderVideo.setOnClickListener(new OnBtRecorderVideoClickListenerImpl());
		btLocation = (Button) findViewById(R.id.btLocation);
		btLocation.setOnClickListener(new OnBtLocationClickListenerImpl());
		btSaveInfo = (Button) findViewById(R.id.btSaveInfo);
		btSaveInfo.setOnClickListener(new OnBtAddInfoClickListenerImpl());
		etTitle = (EditText) findViewById(R.id.info_title);
		etContent = (EditText) findViewById(R.id.info_content);
		g = (MyGridView) findViewById(R.id.gridPhoto);
		imageVideo = (ImageView) findViewById(R.id.infoVideo);
		imageVideo.setOnClickListener(new OnBtVideoViewClickListenerImpl());
		clearVideo = (ImageView) findViewById(R.id.clearVideo);
		clearVideo.setOnClickListener(new OnBtVideoClearClickListenerImpl());
		tvInfoLocationDesc = (TextView) findViewById(R.id.infoLocationDesc);
		tvInfoLocationTip = (TextView) findViewById(R.id.infoLocationTip);
		llInfoLocationId = (LinearLayout) findViewById(R.id.infoLocationId);
		llInfoVideoId = (LinearLayout) findViewById(R.id.infoVideoId);
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		Log.i("InfoAddActivity", "onStart");
		initSasung();
		super.onStart();
	}

	/**
	 * 初始将内容显示到主界面上 若拍攝圖片，則放入bms內
	 */
	private void displayPhoto(Bitmap bm) {
		Resources res = getResources();
		Bitmap bitmap = ImageUtil.drawableToBitmap(res
				.getDrawable(R.drawable.addphoto_button_normal));
		Bitmap[] tmpbms = bms;
		if (bm == null){
			bms = new Bitmap[1];
			bms[0] = bitmap;
		} else {
			bms = new Bitmap[tmpbms.length + 1];
			for (int i = 0; i < tmpbms.length; i++){
				// 复制历史
				bms[i] = tmpbms[i];
			}
			// 更新新拍摄的照片
			bms[tmpbms.length - 1] = bm;
			bms[tmpbms.length] = bitmap;
		}
		// 初始化图片适配器
		ia = new GridImageAdapter(this, bms);
		g = (GridView) findViewById(R.id.gridPhoto);
		g.setAdapter(ia);
		g.setOnItemClickListener(new OnItemClick(this));
		// 得到屏幕的大小
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
	}

	private void displayPhotoBack(int p) {
		Bitmap[] tmpBms = new Bitmap[bms.length - 1];
		String[] tmpImgs = new String[bms.length - 2];
		List<Bitmap> listBms = new ArrayList<Bitmap>();
		List<String> listImgs = new ArrayList<String>();
		for (int i = 0; i < bms.length; i++) {
			if (i != p) {
				listBms.add(bms[i]);
			}
		}
		for (int i = 0; i < tmpBms.length; i++) {
			tmpBms[i] = listBms.get(i);
		}
		bms = tmpBms;
		for (int i = 0; i < imgs.length; i++) {
			if (i != p) {
				listImgs.add(imgs[i]);
			}
		}
		for (int i = 0; i < tmpImgs.length; i++) {
			tmpImgs[i] = listImgs.get(i);
		}
		imgs = tmpImgs;

		// 初始化图片适配器
		ia = new GridImageAdapter(this, bms);
		g = (GridView) findViewById(R.id.gridPhoto);
		g.setAdapter(ia);
		g.setOnItemClickListener(new OnItemClick(this));
		// 得到屏幕的大小
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		globalVar.setImgs(imgs);
	}

	/**
	 * 点击具体的小图片时，会链接到GridViewActivity页面，进行加载和展示
	 */
	public class OnItemClick implements OnItemClickListener {
		public OnItemClick(Context c) {
			mContext = c;
		}

		@Override
		public void onItemClick(AdapterView aview, View view, int position,
				long arg3) {
			globalVar.setTitle(etTitle.getText().toString());
			globalVar.setRemark(etContent.getText().toString());
			if (position == bms.length - 1){
				// 进入拍照页面
				try{
					doPickPhotoAction();
				}catch (Exception e){
					FileLog.e(TAG, e.toString());
				}
			} else {
				globalVar.setImgs(imgs);
				// 打开照片预览页面
				Intent intent = new Intent();
				intent.setClass(InfoAddActivity.this, GalleryActivity.class);
				intent.putExtra("position", position);
				intent.putExtra("type", Contant.ADD);
				// InfoAddActivity.this.startActivity(intent);
				InfoAddActivity.this.startActivityForResult(intent, PHOTO_DEL);
			}
		}
		private Context mContext;
	}

	/**
	 * 初始化定位信息
	 */
	private void initLocation() {
		// 定位
		Location location1 = globalVar.getInfoLocation();
		if (location1 != null) {
			displayLocation(location1);
			globalVar.setInfoLocation(location1);
		} else {
			tvInfoLocationDesc.setText("");
			tvInfoLocationDesc.setVisibility(View.GONE);
			tvInfoLocationTip.setVisibility(View.GONE);
			llInfoLocationId.setVisibility(View.GONE);
		}
	}

	/**
	 * 初始化视频信息
	 */
	private void initVideo() {
		this.videoName = globalVar.getVideo1();
		if (videoName != null && !"".equals(videoName)) {
			this.videoFile = new File(Environment.getExternalStorageDirectory()
					+ "/DCIM/eastelsoft/" + videoName);
			Bitmap bitap = ThumbnailUtils.createVideoThumbnail(Environment.getExternalStorageDirectory()
					+ "/DCIM/eastelsoft/" + videoName,
					Images.Thumbnails.MINI_KIND);
			imageVideo.setBackgroundDrawable(new BitmapDrawable(null, bitap));
			imageVideo.setImageDrawable(this.getResources().getDrawable(
					R.drawable.voiceassistant_playbtn_pressed));
			InfoAddActivity.this.clearVideo.setVisibility(View.VISIBLE);
			imageVideo.setVisibility(View.VISIBLE);
			llInfoVideoId.setVisibility(View.VISIBLE);
		}
//		if(bitMap_pic!=null&&!bitMap_pic.isRecycled()){
//			Log.i(TAG, "回收摄像展示");
//			bitMap_pic.recycle();
//		}
	}

	/**
	 * 初始化录音信息
	 */
	private void initReSound() {
		this.reSoundName = globalVar.getReSoundName();
		Log.i(TAG,"初始化录音---》"+reSoundName);
		if(reSoundName!=null&&!"".equals(reSoundName)){
			Log.i(TAG, "reSoundName");
			textRecord_time.setText(setLongtime.toString()+"'");
			recording_textView.setVisibility(View.GONE);
			textRecord_time.setVisibility(View.VISIBLE);
			imageView.setVisibility(View.VISIBLE);
		}
	}	
	
	
	/**
	 * 初始化图片信息
	 */
	private void initPic(){
		imgs = globalVar.getImgs();
		Bitmap bm=null;
		Bitmap bitmap=null;
		Log.i(TAG,"初始化initPic");
		if (imgs == null || imgs.length <= 0) {
			displayPhoto(null);
		} else {
			Resources res = getResources();
			bitmap = ImageUtil.drawableToBitmap(res.getDrawable(R.drawable.addphoto_button_normal));
			try{
				bms = new Bitmap[imgs.length + 1];
				
				for (int i = 0; i < imgs.length; i++){
					BitmapFactory.Options options = new BitmapFactory.Options();
					options.inSampleSize = 10;// 图片的长宽都是原来的1/10
					FileInputStream f = new FileInputStream(imgs[i]);
					BufferedInputStream bis = new BufferedInputStream(f);
					bm = BitmapFactory.decodeStream(bis, null, options);
					bms[i] = bm;
					}
				bms[imgs.length] = bitmap;
				// 初始化图片适配器
				ia = new GridImageAdapter(this, bms);
				g = (GridView) findViewById(R.id.gridPhoto);
				g.setAdapter(ia);
				g.setOnItemClickListener(new OnItemClick(this));
				// 得到屏幕的大小
				dm = new DisplayMetrics();
				getWindowManager().getDefaultDisplay().getMetrics(dm);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(bitmap!=null&&!bitmap.isRecycled()){
			bitmap.isRecycled();
		}
		if(bm!=null&&!bm.isRecycled()){
			bm.isRecycled();
		}
	}

	/**
	 * 解决屏幕切换问题
	 */
	private void initSasung() {
		Log.i(TAG, "initSasung");
		initLocation();
		initVideo();
		initPic();
		initReSound();
		title = globalVar.getTitle();
		if (title != null && !"".equals(title)) {
			etTitle.setText(title);
		}
		remark = globalVar.getRemark();
		if (remark != null && !"".equals(remark)) {
			etContent.setText(remark);
		}

	}

	@Override
	protected void onDestroy() {
		Log.i("InfoAddActivity", "OnDestroy");
		if (locationHelper != null) {
			locationHelper.getWritableDatabase().close();
		}
		if (mBound) {
			this.getApplicationContext().unbindService(sc);
			mBound = false;
		}
		super.onDestroy();
	}

	private class OnBtAddInfoClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				sp = getSharedPreferences("userdata", 0);
				SetInfo set = IUtil.initSetInfo(sp);
				uploadDate = Util.getLocaleTime("yyyy-MM-dd HH:mm:ss");
				id_info = set.getSerialNumber()
						+ Util.getLocaleTime("yyyyMMddHHmmssSSS");

				title = etTitle.getText().toString();
				remark = etContent.getText().toString();
				if ("".equals(title.trim())) {
					respMsg = getResources().getString(
							R.string.info_title_edit_error);
					Toast.makeText(getApplicationContext(), respMsg,
							Toast.LENGTH_SHORT).show();
					return;
				}
				if(reSoundName!=null&&!"".equals(reSoundName)){
					reSoundName=reSoundName.substring(reSoundName.length()-17);
				}
				Thread addInfoThread = new Thread(new AddInfoThread());
				addInfoThread.start();
				Toast.makeText(getApplicationContext(), "信息后台上传中...",
						Toast.LENGTH_LONG).show();
				InfoAddActivity.this.finish();
			} catch (Exception e) {
				FileLog.i(TAG, "info ADD Thread===========>" + e);
			}

		}
	}

	class AddInfoThread implements Runnable {
		@Override
		public void run() {
			locationService
					.updateInformation(title, remark, lon, lat, uploadDate,
							id_info, location, videoName, imgs,reSoundName,setLongtime);
		}
	}

	class LocationThread implements Runnable {
		private Location location;

		public LocationThread(Location location) {
			this.location = location;
		}

		@Override
		public void run() {
			Looper.prepare();
			Message msg = handler.obtainMessage();
			try {
				if (location != null) {
					displayLocation(location);
				} else {
					msg.what = 11;
					handler.sendMessage(msg);
				}
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
				msg.what = 11;
				handler.sendMessage(msg);
			}
		}
	}

	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try {
				if (msg.what < 9) {
					// pDialog.cancel();
					try {
						popupWindowPg.dismiss();
					} catch (Exception e) {
						FileLog.e(TAG, e.toString());
					}
				}
				switch (msg.what) {
				case 0:
					if ("1".equals(msg.obj.toString())) {
						// 返回成功数据写库
						String fileNames = "";
						if (imgs.length > 0) {
							for (int i = 0; i < imgs.length; i++) {
								fileNames += imgs[i] + "|";
							}
						}

						if (videoName != null && !"".equals(videoName)) {
							fileNames += videoName + "|";
						}
						
						
						if (fileNames.endsWith("|"))
							fileNames = fileNames.substring(0,
									(fileNames.length() - 1));
						DBUtil.insertLInfo(
								locationHelper.getWritableDatabase(),
								uploadDate, title, fileNames, remark, lon, lat,
								id_info, location, "11");
						Toast.makeText(
								InfoAddActivity.this,
								getResources().getString(
										R.string.info_upload_succ),
								Toast.LENGTH_SHORT).show();
						globalVar.setInfoLocation(null);
						globalVar.setImgs(new String[0]);
						globalVar.setVideo1("");
						InfoAddActivity.this.finish();
					} else {
						openPopupWindowAx("上传失败，是否存入本地");
					}
					break;
				case 1:
					openPopupWindowAx("上传失败，是否存入本地");
					break;
				case 11:
					tvInfoLocationDesc.setText("获取定位信息失败");
					tvInfoLocationDesc.setVisibility(View.VISIBLE);
					// imageViewLocationIcon.setVisibility(View.VISIBLE);
					tvInfoLocationTip.setVisibility(View.GONE);
					llInfoLocationId.setVisibility(View.GONE);
					break;
				case 10:
					location = msg.obj.toString();
					tvInfoLocationDesc.setText(location);
					tvInfoLocationDesc.setVisibility(View.VISIBLE);
					tvInfoLocationTip.setVisibility(View.GONE);
					llInfoLocationId.setVisibility(View.VISIBLE);
					// imageViewLocationIcon.setVisibility(View.VISIBLE);
				case 9:
					Location location = null;
					Object[] obj = (Object[]) msg.obj;
					if (obj[0] != null) {
						location = (Location) obj[0];
					}
					displayLocation(location);
					if (location != null) {
						displayLocation(location);
					} else {
						new AMapAction(InfoAddActivity.this, amapCallback, "")
								.startListener();
					}
					break;
				case 99:
					Location location1 = null;
					Object[] obj1 = (Object[]) msg.obj;
					if (obj1[0] != null) {
						location1 = (Location) obj1[0];
					}
					if (location1 != null) {
						displayLocation(location1);
						globalVar.setInfoLocation(location1);
					} else {
						tvInfoLocationDesc.setText("获取定位信息失败");
						tvInfoLocationDesc.setVisibility(View.VISIBLE);
						tvInfoLocationTip.setVisibility(View.GONE);
						llInfoLocationId.setVisibility(View.VISIBLE);
					}
					break;
				}
			} catch (Exception e) {
				// 异常中断
			}
		}
	};

	private Button btClosex;
	private Button btCloseok;
	private Button btCloseno;
	protected void openPopupWindowAx(String msg) {
		try {
			LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			ViewGroup menuView = (ViewGroup) mLayoutInflater.inflate(
					R.layout.pop_commax, null, true);
			btClosex = (Button) menuView.findViewById(R.id.btClose);
			btClosex.setOnClickListener(new OnBtCloseLClickListenerImpl());
			btCloseok = (Button) menuView.findViewById(R.id.btClose1);
			btCloseok.setOnClickListener(new OnBtCloseLokClickListenerImpl());
			btCloseno = (Button) menuView.findViewById(R.id.btClose2);
			btCloseno.setOnClickListener(new OnBtCloseLClickListenerImpl());
			btPopText = (TextView) menuView.findViewById(R.id.btPopText);
			btPopText.setText(msg);
			popupWindow = new PopupWindow(menuView, LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT, true); // 背景
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
			popupWindow.setAnimationStyle(R.style.PopupAnimation);
			popupWindow.showAtLocation(findViewById(R.id.parent),
					Gravity.CENTER | Gravity.CENTER, 0, 0);
			popupWindow.update();
		} catch (Exception e) {
		}
	}

	protected class OnBtCloseLClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				popupWindow.dismiss();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	protected class OnBtCloseLokClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				popupWindow.dismiss();
				// uploadDate = Util.getLocaleTime("yyyy-MM-dd HH:mm:ss");
				String fileNames = "";
				if (imgs.length > 0) {
					for (int i = 0; i < imgs.length; i++) {
						fileNames += imgs[i] + "|";
					}
				}
				if (videoName != null && !"".equals(videoName)) {
					fileNames += videoName + "|";
				}
				if(reSoundName !=null &&!"".equals(reSoundName)){
					fileNames += reSoundName+"|";
				}
				if (fileNames.endsWith("|"))
					fileNames = fileNames
							.substring(0, (fileNames.length() - 1));
				FileLog.i(TAG, "wei" + fileNames);
				DBUtil.insertLInfo(locationHelper.getWritableDatabase(),
						uploadDate, title, fileNames, remark, lon, lat,
						id_info, location, "00");
				Toast.makeText(InfoAddActivity.this, "存入本地成功",
						Toast.LENGTH_SHORT).show();
				globalVar.setInfoLocation(null);
				globalVar.setImgs(new String[0]);
				globalVar.setVideo1("");
				globalVar.setReSoundName("");
				InfoAddActivity.this.finish();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
				Toast.makeText(InfoAddActivity.this, "存入本地失败",
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	private CallBack amapCallback = new CallBack() {
		public void execute(Object[] paramArrayOfObject) {
			Message msg = handler.obtainMessage();
			msg.what = 99;
			msg.obj = paramArrayOfObject;
			handler.sendMessage(msg);
		}
	};

	private void displayLocation(Location location) {
		Message msg = handler.obtainMessage();
		try {
			InfoAddActivity.this.lon = Util.format(location.getLongitude(),
					"#.######");
			InfoAddActivity.this.lat = Util.format(location.getLatitude(),
					"#.######");
			String locationDesc = location.getExtras().getString("desc");
			if (!"".equals(locationDesc)) {
				msg.obj = locationDesc;//定位位置信息
				msg.what = 10;
				handler.sendMessage(msg);
			} else {
				msg.what = 11;
				handler.sendMessage(msg);
			}
		} catch (NumberFormatException e) {
			msg.what = 11;
			handler.sendMessage(msg);
		} catch (Exception e) {
			msg.what = 11;
			handler.sendMessage(msg);
		}
	}
	private class OnBtBackClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				globalVar.setInfoLocation(null);
				globalVar.setImgs(new String[0]);
				globalVar.setVideo1("");
				globalVar.setReSoundName("");
				InfoAddActivity.this.finish();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	private class OnBtRecorderVideoClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				globalVar.setTitle(etTitle.getText().toString());
				globalVar.setRemark(etContent.getText().toString());
				doPickVideoAction(); // 系统
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	/* new add RecoredSound */
	private class OnbtRecoredSoundClickListenerImpl implements OnClickListener {
		long time=0;
		@Override
		public void onClick(View arg0) {
			if(!Environment.getExternalStorageState().equals(  
				    Environment.MEDIA_MOUNTED)){
				return;
			}
			// TODO Auto-generated method stub
			if (is_recording) {
				Toast.makeText(InfoAddActivity.this, "录音完成", 200).show();
				soundrecord.setBackgroundColor(TRIM_MEMORY_BACKGROUND);
				mrstop();
				setLongtime=(System.currentTimeMillis()-time)/1000+"";
				textRecord_time.setText(setLongtime.toString()+"'");
				recording_textView.setVisibility(View.GONE);
				textRecord_time.setVisibility(View.VISIBLE);
				imageView.setVisibility(View.VISIBLE);
				globalVar.setReSoundName(reSoundName);
				is_recording = false;
			} else if (!is_recording) {
				reSoundName=filepath+System.currentTimeMillis()+".amr";
				Toast.makeText(InfoAddActivity.this, "开始录音", 200).show();
				soundrecord.setBackgroundColor(TRIM_MEMORY_BACKGROUND);
				imageView.setVisibility(View.GONE);
				textRecord_time.setVisibility(View.GONE);	
				recording_textView.setVisibility(View.VISIBLE);
				time  = System.currentTimeMillis();
				mrstart();
				is_recording = true;
				recorded = true;
			}
		}
	}

	private class RecoredSoundimageViewClickListenerIMpl implements
			OnLongClickListener {

		@Override
		public boolean onLongClick(View arg0) {
			// TODO Auto-generated method stub
			File file = new File(reSoundName);
			if (!file.exists()) {
				return false;
			}
			Builder builder = new Builder(InfoAddActivity.this);
			builder.setTitle("确认");
			builder.setMessage("确认要删除吗？");
			builder.setNeutralButton("是",
					new DialogInterface.OnClickListener(){
						@Override
						public void onClick(DialogInterface arg0, int arg1) {
							// TODO Auto-generated method stub
							File file = new File(filepath);
							file.delete();
							recorded = false;
							is_recording = false;
							imageView.setVisibility(View.GONE);
							textRecord_time.setVisibility(View.GONE);
							recording_textView.setVisibility(View.GONE);
							Toast.makeText(InfoAddActivity.this, "删除成功", 2000)
									.show();
						}
					});
			builder.setNegativeButton("否", null);
			AlertDialog alertDialog = builder.create();
			alertDialog.show();
			return false;
		}
	}
	///int num ;
	Handler handler2 = new Handler();
	Runnable thread = new Runnable(){
		@Override
		public void run() {
			countdown--;
			if(countdown>=Integer.parseInt(setLongtime)-1){
				handler2.removeCallbacks(thread);
			}
			handler2.postDelayed(thread, 1000);
			textRecord_time.setText(countdown+"");
			if(countdown<1){
				handler2.removeCallbacks(thread);
				textRecord_time.setText("播放完毕");
			}
		}
	};
	private class RecordSoundPlayClickListenerImpl implements OnClickListener {
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
				textRecord_time.setText(setLongtime);
				countdown=Integer.parseInt(setLongtime);
				playMusic(reSoundName);	
				handler2.postDelayed(thread, 1000);	
		}
	}

	// ///////////////////////////////////////////////////////////
	private class OnBtLocationClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				// 获取定位位置
				new BaiduMapAction(InfoAddActivity.this, amapCallback, "2")
						.startListener();
				tvInfoLocationTip.setVisibility(View.VISIBLE);
				tvInfoLocationDesc.setVisibility(View.GONE);
				InfoAddActivity.this.llInfoLocationId
						.setVisibility(View.VISIBLE);
				// imageViewLocationIcon.setVisibility(View.GONE);
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	public CallBack gpsCallback = new CallBack() {
		public void execute(Object[] paramArrayOfObject) {
			Message msg = handler.obtainMessage();
			msg.what = 9;
			msg.obj = paramArrayOfObject;
			handler.sendMessage(msg);
		}
	};

	private class OnBtVideoViewClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("oneshot", 0);
				intent.putExtra("configchange", 0);
				Uri uri = Uri.fromFile(new File(Environment
						.getExternalStorageDirectory()
						+ "/DCIM/eastelsoft/"
						+ InfoAddActivity.this.videoName));

				intent.setDataAndType(uri, "video/*");
				startActivity(intent);
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	public static Intent getVideoFileIntent(String param)

	{
		Intent intent = new Intent("android.intent.action.VIEW");

		intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

		intent.putExtra("oneshot", 0);

		intent.putExtra("configchange", 0);

		Uri uri = Uri.fromFile(new File(param));

		intent.setDataAndType(uri, "video/*");

		return intent;
	}

	private class OnBtVideoClearClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				InfoAddActivity.this.videoName = null;
				InfoAddActivity.this.videoFile = null;
				InfoAddActivity.this.globalVar.setVideo1("");
				InfoAddActivity.this.imageVideo.setVisibility(View.GONE);
				InfoAddActivity.this.clearVideo.setVisibility(View.GONE);
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	private void doPickPhotoAction() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
			doTakePhoto();// 用户点击了从照相机获取
		} else {
			Toast.makeText(InfoAddActivity.this, getString(R.string.noSDCard),
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 拍照获取图片
	 * 
	 */
	protected void doTakePhoto(){
		try {
			// int idle = this.getIdle(globalVar);
			Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
			File wallpaperDirectory = new File(
					Environment.getExternalStorageDirectory()
							+ "/DCIM/eastelsoft/");
			wallpaperDirectory.mkdirs();
			cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri
					.fromFile(new File(Environment
							.getExternalStorageDirectory()
							+ "/DCIM/eastelsoft"
							+ "/a123.jpg")));
			startActivityForResult(cameraIntent, CAMERA_WITH_DATA);
		}catch (ActivityNotFoundException e){
			e.printStackTrace();
		}
	}

	protected void doPickVideoAction() {
		String status = Environment.getExternalStorageState();
		if (status.equals(Environment.MEDIA_MOUNTED)) {// 判断是否有SD卡
			doTakeVideo();// 用户点击了从照相机获取
		} else {
			Toast.makeText(InfoAddActivity.this, getString(R.string.noSDCard),
					Toast.LENGTH_SHORT).show();
		}
	}

	/**
	 * 视频录制
	 */
	protected void doTakeVideo() {
		try {
			Intent cameraIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
			videoName = Util.getLocaleTime("yyyyMMddHHmmss") + ".3gp";
			globalVar.setVideo1(videoName);
			File wallpaperDirectory = new File(
					Environment.getExternalStorageDirectory()
							+ "/DCIM/eastelsoft/");
			wallpaperDirectory.mkdirs();

			cameraIntent.putExtra(MediaStore.EXTRA_SIZE_LIMIT, 1024 * 1024 * 5);
			cameraIntent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 0.5);
			startActivityForResult(cameraIntent, RECORDER_VIDEO_SYS);
		} catch (ActivityNotFoundException e) {
			e.printStackTrace();
		}
	}

	// 请求Gallery程序
	protected void doPickPhotoFromGallery() {
		Intent localIntent = new Intent();
		localIntent.setType("image/*");
		localIntent.setAction("android.intent.action.GET_CONTENT");
		Intent localIntent2 = Intent.createChooser(localIntent, "选择图片");
		startActivityForResult(localIntent2, PHOTO_PICKED_WITH_DATA);
	}

	// 因为调用了Camera和Gally所以要判断他们各自的返回情况,他们启动时是这样的startActivityForResult
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode != RESULT_OK)
			return;
		switch (requestCode){

		case CAMERA_WITH_DATA: // 拍照
			Bitmap bmap=null;
			Bitmap photoViewBitmap =null;
			try {
				String[] tmpimgs = imgs;
				imgs = new String[bms.length];
				// 获取图片的高度和宽度
				String path = Environment.getExternalStorageDirectory()
						+ "/DCIM/eastelsoft/a123.jpg";
				for (int i = 0; i < bms.length - 1; i++){
					imgs[i] = tmpimgs[i];
				}
				int degree = readPictureDegree(path);
				// 图片压缩
				bmap = ImageThumbnail.PicZoom(path);
				Matrix matrix = new Matrix();
				matrix.postRotate(degree);
				photoViewBitmap = Bitmap.createBitmap(bmap, 0, 0,
						bmap.getWidth(), bmap.getHeight(), matrix, true);
				String filename = Util.getLocaleTime("yyyyMMddHHmmss") + ".jpg";
				String newPath = FileUtil.saveBitmapToFileForPath(
						photoViewBitmap, filename);  
				imgs[bms.length - 1] = newPath;
				// 显示图片
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 10;// 图片的长宽都是原来的1/10
				FileInputStream f = new FileInputStream(newPath);
				BufferedInputStream bis = new BufferedInputStream(f);
				Bitmap bm = BitmapFactory.decodeStream(bis, null, options);
				displayPhoto(bm);
				// 增加全局变量
				globalVar.setImgs(imgs);
				if(photoViewBitmap!=null&&!photoViewBitmap.isRecycled()){
					photoViewBitmap.recycle();
				}
			} catch (Exception e) {
				FileLog.e(TAG,"============================================222" + e);
			}
			if(bmap!=null&&!bmap.isRecycled()){
				bmap.recycle();
			}
			if(photoViewBitmap!=null&&photoViewBitmap.isRecycled()){
				photoViewBitmap.recycle();
			}
			break;
		case PHOTO_DEL://圖片刪除
			Bundle bundle = data.getExtras();
			int p = bundle.getInt("p");
			displayPhotoBack(p);
			break;

		case RECORDER_VIDEO_SYS://錄制視頻
			Bitmap bitMap_pic=null;
			try {
				
				title = globalVar.getTitle();
				if (title != null && !"".equals(title)) {
					etTitle.setText(title);
				}
				
				remark = globalVar.getRemark();
				if (remark != null && !"".equals(remark)) {
					etContent.setText(remark);
				}

				String tmp = data.getDataString().toLowerCase();
				Uri uri = Uri.parse(tmp);

				String[] proj = { MediaStore.Images.Media.DATA };
				Cursor actualimagecursor = this.managedQuery(uri, proj, null,
						null, null);
				int actual_image_column_index = actualimagecursor
						.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
				actualimagecursor.moveToFirst();

				String img_path = actualimagecursor
						.getString(actual_image_column_index);
				Log.i(TAG, ">>>>>>>>>>>>>>>>>>>>>>>>>" + img_path);
				File file = new File(img_path);
				this.videoName = globalVar.getVideo1();
				this.videoFile = new File(
						Environment.getExternalStorageDirectory() + "/DCIM/eastelsoft/" + videoName);
				globalVar.setVideo1(videoName);
				try {
					if (file != null && videoFile != null) {
						copyFile(file, videoFile);
					}
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Log.i(TAG, "videoName--->"+videoName);
				bitMap_pic = ThumbnailUtils.createVideoThumbnail(
						Environment.getExternalStorageDirectory()
						+ "/DCIM/eastelsoft/" + videoName,
						Images.Thumbnails.MINI_KIND);
				Log.i(TAG,"bitMap_pic-->"+bitMap_pic);
				imageVideo.setBackgroundDrawable(new BitmapDrawable(null,
						bitMap_pic));
				imageVideo.setImageDrawable(this.getResources().getDrawable(
						R.drawable.voiceassistant_playbtn_pressed));
				imageVideo.setVisibility(View.VISIBLE);
				llInfoVideoId.setVisibility(View.VISIBLE);
				InfoAddActivity.this.clearVideo.setVisibility(View.VISIBLE);

			} catch (Exception e) {
				e.printStackTrace();
			}
			if(bitMap_pic!=null&&!bitMap_pic.isRecycled()){
				bitMap_pic.recycle();
			}
			break;
		}
	}

	// ScrollView用dispatchTouchEvent
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		Rect localRect1 = new Rect();
		Rect localRect2 = new Rect();
		((EditText) findViewById(R.id.info_title))
				.getGlobalVisibleRect(localRect1);
		((EditText) findViewById(R.id.info_content))
				.getGlobalVisibleRect(localRect2);
		Rect localRect3 = new Rect((int) event.getX(), (int) event.getY(),
				(int) event.getX(), (int) event.getY());
		if ((!localRect1.intersect(localRect3))
				&& (!localRect2.intersect(localRect3)))
			((InputMethodManager) getSystemService("input_method"))
					.hideSoftInputFromWindow(getWindow().peekDecorView()
							.getWindowToken(), 0);
		return super.dispatchTouchEvent(event);
	}

	/**
	 * 获取当前使用的位置
	 * 
	 * @return
	 * @throws Exception
	 */
	public int getIdle(GlobalVar globalVar) {
		if (globalVar.getImgFileName1() == null
				|| "".equals(globalVar.getImgFileName1())) {
			return 1;
		} else {
			File file = new File(Environment.getExternalStorageDirectory()
					+ "/DCIM/eastelsoft" + "/" + globalVar.getImgFileName1());
			if (!file.exists()) {
				return 1;
			}
		}
		if (globalVar.getImgFileName2() == null
				|| "".equals(globalVar.getImgFileName2())) {
			return 2;
		} else {
			File file = new File(Environment.getExternalStorageDirectory()
					+ "/DCIM/eastelsoft" + "/" + globalVar.getImgFileName2());
			if (!file.exists()) {
				return 2;
			}
		}
		return 3;
	}

	public void copyFile(File sourceFile, File targetFile) {
		FileInputStream input = null;
		BufferedInputStream inBuff = null;
		FileOutputStream output = null;
		BufferedOutputStream outBuff = null;
		try {
			// 新建文件输入流并对它进行缓冲
			input = new FileInputStream(sourceFile);
			inBuff = new BufferedInputStream(input);
			// 新建文件输出流并对它进行缓冲
			output = new FileOutputStream(targetFile);
			outBuff = new BufferedOutputStream(output);
			// 缓冲数组
			byte[] b = new byte[1024 * 5];
			int len;
			while ((len = inBuff.read(b)) != -1) {
				outBuff.write(b, 0, len);
			}
			// 刷新此缓冲的输出流
			outBuff.flush();
		} catch (IOException e) {

		} finally {
			// 关闭流
			if (null != inBuff) {

				try {
					inBuff.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				inBuff = null;
			}

			if (null != outBuff) {
				try {
					outBuff.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				outBuff = null;

			}

			if (null != output) {
				try {
					output.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				output = null;
			}

			if (null != input) {
				try {
					input.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				input = null;
			}
		}
	}

	/**
	 * 设置GridView的图片适配器
	 */
	public class GridImageAdapter extends BaseAdapter {
		private Bitmap[] bms;
		String img_num;

		public GridImageAdapter(Context c, Bitmap[] bms) {
			mContext = c;
			this.bms = bms;
			sp = getSharedPreferences("userdata", 0);
			img_num = sp.getString("img_num", Contant.IMG_NUM);
		}

		public int getCount() {
			return bms.length;
		}

		public Object getItem(int position) {
			return position;
		}

		public long getItemId(int position) {
			return position;
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ImageViewExt imageView;

			if (convertView == null) {
				imageView = new ImageViewExt(mContext);
				// 如果是横屏，GridView会展示4列图片，需要设置图片的大小
				imageView.setLayoutParams(new GridView.LayoutParams(
						(dm.widthPixels - 100) / imageCol - 30,
						(dm.widthPixels - 100) / imageCol - 30));
				// imageView.setLayoutParams(new GridView.LayoutParams(80,80));
				imageView.setAdjustViewBounds(true);
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
			} else {
				imageView = (ImageViewExt) convertView;
			}

			imageView.setImageBitmap(bms[position]);
			if ((position + 1) == bms.length) {
				if (String.valueOf(bms.length - 1).equals(img_num)) {
					imageView.setVisibility(View.INVISIBLE);
				}

			}

			return imageView;
		}

		private Context mContext;

	}

	private int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);

			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			default:
				degree = 0;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	private boolean mrstart() {
		// TODO Auto-generated method stub
		mr = new MediaRecorder();
		mr.setAudioSource(AudioSource.MIC);
		// 设置音源,这里是来自麦克风
		mr.setOutputFormat(OutputFormat.RAW_AMR);
		// 输出格式
		mr.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		// 编码
		mr.setOutputFile(reSoundName);
		// 输出文件路径
		try {
			mr.prepare();
			// 做些准备工作
			mr.start();
			// 开始
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	// 停止录音
	private void mrstop() {
		// TODO Auto-generated method stub
		if (mr!= null) {
			mr.stop();// 停止
			mr.release();// 释放
		}
	}
	
	// 播放录音
	public static void playMusic(String name) {
		synchronized(name){
		Log.i("播放声音", "playMusic");
		try {
			if (mMediaPlayer.isPlaying()) {
				System.out.println("进入停止阶段");
				mMediaPlayer.stop();
			}
			mMediaPlayer.reset();
			mMediaPlayer.setDataSource(name);
			mMediaPlayer.prepare();
			mMediaPlayer.start();
			mMediaPlayer.setOnCompletionListener(new OnCompletionListener() {
				public void onCompletion(MediaPlayer mp) {
				
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
		}
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		Log.i("InfoAddActivity", "onStop");
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// TODO Auto-generated method stub
		Log.i("InfoAddActivity", "onConfigurationChanged");
		super.onConfigurationChanged(newConfig);
	}

	@Override
	public void onTrimMemory(int level) {
		// TODO Auto-generated method stub
		super.onTrimMemory(level);
	}
	
	 @Override
	public void onLowMemory() {
		super.onLowMemory();
	}

		@Override
		protected void onSaveInstanceState(Bundle outState) {
			// TODO Auto-generated method stub
			Log.i(TAG, "开始保存信息");
			outState.putString("title", title);
			outState.putString("remark", remark);
			outState.putString("reSoundName",reSoundName);
			outState.putString("lon",lon);
			outState.putString("lat",lat);
			outState.putString("location", location);
			outState.putString("id_info", id_info);
			outState.putStringArray("imgs", imgs);
			outState.putString("videoName", videoName);
			outState.putString("setLongtime", setLongtime);
			super.onSaveInstanceState(outState);
		}
}
