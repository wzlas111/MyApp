/**
 * Copyright (c) 2012-8-15 www.eastelsoft.com
 * $ID InfoViewActivity.java 上午10:21:18 $
 */
package com.eastelsoft.lbs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONObject;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.media.MediaPlayer.OnCompletionListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore.Images;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.db.DBUtil;
import com.eastelsoft.lbs.db.LocationSQLiteHelper;
import com.eastelsoft.lbs.entity.SetInfo;
import com.eastelsoft.lbs.photo.GalleryActivity;
import com.eastelsoft.lbs.photo.ImageViewExt;
import com.eastelsoft.lbs.service.LocationService;
import com.eastelsoft.lbs.service.LocationService.MBinder;
import com.eastelsoft.util.Contant;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.GlobalVar;
import com.eastelsoft.util.IUtil;
import com.eastelsoft.util.http.AndroidHttpClient;

/**
 * 查看信息详情
 * @author lengcj
 */
public class InfoViewActivity extends BaseActivity {
	public static final String TAG = "InfoViewActivity";
	private Button btBack;
	private Button btAddInfo;
	private TextView tvInfoTitlehead;
	private TextView tvInfoTitle;
	private View tvInfoTitleline;
	public static  MediaPlayer mMediaPlayer=new MediaPlayer();
	private TextView tvInfoContenthead;
	private TextView tvInfoContent;
	private View tvInfoContentline;

	private TextView tvInfoUploadDatehead;
	private TextView tvInfoUploadDate;
	private View tvInfoUploadDateline;
	
	private TextView Info_location_head;
	private TextView tvInfoLocation;
	private View tvInfoLocationline;
	private RelativeLayout tvInfoLocationRelativeLayout;
	private Button tvinfolocation_img;
	
	private View tvInfoVideoline;
	private TextView tvInfoVideoHead;
//new add
	private int countdown;
	public static  TextView textRecord_time;
	private View tvInfoRecordline;
	private TextView tvInfoRecordHead;
	private ImageView recordView;
	
	private TextView imageViewhead;

	private ImageView videoView;

	private LocationSQLiteHelper locationHelper;
	private Bitmap bm;

	HashMap<String, Object> localMap;
	//final String filepath=Environment.getExternalStorageDirectory()+"/DCIM/eastelsoft/androidRecording.amr";
	private String imgFileName = "";
	private String reSoundName = "";
	private String videoName = "";
	private String location;
	private String lon;
	private String lat;
	private String istijiao = "";
	private String title;
	private String remark;
	private String info_auto_id;
	private String uploadDate;
	public static  String setLongtime1;
	private boolean check_playing=true;
	// private File imgFile;
	
	private DisplayMetrics dm;
	private GridImageAdapter ia;
	private GridView g;
	private int imageCol = 4;
	// 图片存款
	private Bitmap[] bms = new Bitmap[0];
	// 图片路径
	private String[] imgs = new String[0];

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_infoview);
		globalVar = (GlobalVar) getApplication();
		btBack = (Button) findViewById(R.id.btBack);
		btBack.setOnClickListener(new OnBtBackClickListenerImpl());
		btAddInfo = (Button) findViewById(R.id.btAddInfo);

		btAddInfo.setOnClickListener(new OnBtAddClickListenerImpl());
		tvInfoTitlehead = (TextView) findViewById(R.id.info_title_head);
		tvInfoTitle = (TextView) findViewById(R.id.info_title);
		tvInfoTitleline = (View) findViewById(R.id.info_title_line);

		tvInfoContenthead = (TextView) findViewById(R.id.info_content_head);
		tvInfoContent = (TextView) findViewById(R.id.info_content);
		tvInfoContentline = (View) findViewById(R.id.info_content_line);

		tvInfoUploadDatehead = (TextView) findViewById(R.id.info_uploadDate_head);
		tvInfoUploadDate = (TextView) findViewById(R.id.info_uploadDate);
		tvInfoUploadDateline = (View) findViewById(R.id.info_uploadDate_line);

		tvInfoLocation = (TextView) findViewById(R.id.info_location);
		tvInfoLocationline = (View) findViewById(R.id.info_location_line);
		Info_location_head = (TextView) findViewById(R.id.info_location_head);
		tvInfoLocationRelativeLayout = (RelativeLayout) findViewById(R.id.info_location_relativeLayout);

		tvinfolocation_img = (Button) findViewById(R.id.info_location_img);
		tvinfolocation_img
				.setOnClickListener(new OnInfoLocationClickListenerImpl());
		tvInfoVideoline = (View) findViewById(R.id.info_video_line);
		tvInfoVideoHead = (TextView) findViewById(R.id.info_video_head);

		tvInfoRecordline = (View)findViewById(R.id.info_record_line);
		tvInfoRecordHead = (TextView) findViewById(R.id.info_record_head);
		textRecord_time = (TextView) findViewById(R.id.text_time);
		
		recordView = (ImageView) findViewById(R.id.record_img);
		recordView.setOnClickListener(new OnInfoRecordClickListenerImpl());
		
		imageViewhead = (TextView) findViewById(R.id.info_img_head);

		g = (MyGridView) findViewById(R.id.gridPhoto);

		videoView = (ImageView) findViewById(R.id.video_img);
		videoView.setOnClickListener(new OnBtVideoViewClickListenerImpl());
		locationHelper = new LocationSQLiteHelper(this, null, null, 5);
		sp = getSharedPreferences("userdata", 0);
		// SetInfo set = IUtil.initSetInfo(sp);
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
		
		Intent intents = getIntent();
		this.info_auto_id = intents.getStringExtra("info_auto_id");

		localMap = DBUtil.getDataFromLInfoByID(
				locationHelper.getWritableDatabase(), info_auto_id);

		if (localMap != null) {
			if (localMap.containsKey("info_title")) {
				if (localMap.get("info_title") != null) {
					tvInfoTitlehead.setVisibility(View.VISIBLE);
					tvInfoTitle.setVisibility(View.VISIBLE);
					title = localMap.get("info_title").toString();
					tvInfoTitle.setText(title);
				}
			}

			if (localMap.containsKey("info_remark")) {
				if (localMap.get("info_remark") != null) {
					tvInfoContenthead.setVisibility(View.VISIBLE);
					tvInfoContent.setVisibility(View.VISIBLE);
					tvInfoTitleline.setVisibility(View.VISIBLE);
					remark = localMap.get("info_remark").toString();
					tvInfoContent.setText(remark);
				}
			}
			if (localMap.containsKey("info_uploadDate")) {
				if (localMap.get("info_uploadDate") != null) {
					tvInfoUploadDatehead.setVisibility(View.VISIBLE);
					tvInfoUploadDate.setVisibility(View.VISIBLE);
					tvInfoContentline.setVisibility(View.VISIBLE);
					uploadDate = localMap.get("info_uploadDate").toString();
					tvInfoUploadDate.setText(localMap.get("info_uploadDate")
							.toString());
				}
			}
			if (localMap.containsKey("info_location")) {
				if (localMap.get("info_location") != null) {
					String info_location = localMap.get("info_location")
							.toString();
					if (info_location != null && !"".equals(info_location)) {
						/*
						 * tvInfoLocationhead.setVisibility(View.VISIBLE);
						 * tvInfoLocation.setVisibility(View.VISIBLE);
						 */
						tvInfoUploadDateline.setVisibility(View.VISIBLE);
						tvInfoLocationRelativeLayout.setVisibility(View.VISIBLE);
						Info_location_head.setVisibility(View.VISIBLE);
						tvInfoLocation.setText(info_location);
					}
				}
			}
			if (localMap.get("info_location") != null) {
				this.location = localMap.get("info_location").toString();
			}
			if (localMap.get("info_lon") != null) {
				this.lon = localMap.get("info_lon").toString();
			}
			if (localMap.get("info_lat") != null) {
				this.lat = localMap.get("info_lat").toString();
			}
			if (localMap.get("istijiao") != null) {
				this.istijiao = localMap.get("istijiao").toString();
				if("00".equals(istijiao)){
					btAddInfo.setVisibility(View.VISIBLE);
				}
			}
			if(localMap.get("setLongtime")!=null){
				this.setLongtime1=localMap.get("setLongtime").toString();
				System.out.println("传入的setLongtime的值是"+this.setLongtime1);
			}

			// try {
			// FileInputStream f = new
			// FileInputStream(Environment.getExternalStorageDirectory()
			// + "/DCIM/eastelsoft/" + imgFileName1);
			// bm = null;
			// BitmapFactory.Options options = new BitmapFactory.Options();
			// options.inSampleSize = 10;//图片的长宽都是原来的1/10
			// BufferedInputStream bis = new BufferedInputStream(f);
			// bm = BitmapFactory.decodeStream(bis, null, options);
			// imageView1.setImageBitmap(bm);
			//
			// this.imgFile1 = new
			// File(Environment.getExternalStorageDirectory()
			// + "/DCIM/eastelsoft/" + imgFileName1);
			// imageView1.setVisibility(View.VISIBLE);
			// imageViewhead.setVisibility(View.VISIBLE);
			// tvInfoLocationline.setVisibility(View.VISIBLE);
			//
			// } catch (FileNotFoundException e) {
			// }

			if (localMap.containsKey("imgFile")) {
				if (localMap.get("imgFile") != null) {
					imgFileName = localMap.get("imgFile").toString();
					String[] files = imgFileName.split("\\|");

					List<String> listImgs = new ArrayList<String>();
					for (String s : files) {
						if (s != null && !"".equals(s)) {
							if (s.toLowerCase().endsWith("jpg")) {
								if (!s.toLowerCase().startsWith(
										Environment
												.getExternalStorageDirectory()
												.toString().toLowerCase())) {
									s = Environment
											.getExternalStorageDirectory()
											.toString()
											+ "/DCIM/eastelsoft/" + s;
								}
								listImgs.add(s);
							}else if(s.toLowerCase().endsWith("amr")){
								reSoundName=Environment
										.getExternalStorageDirectory()
										.toString()
										+ "/DCIM/eastelsoft/"+s;
								System.out.println("InfoViewActivity-------》"+reSoundName);
							}else{
								// 视频
								videoName = s;
							}
						}
					}
					if (listImgs.size() > 0) {
						imgs = new String[listImgs.size()];
						for (int i = 0; i < listImgs.size(); i++) {
							imgs[i] = listImgs.get(i);
						}

						tvInfoLocationline.setVisibility(View.VISIBLE);
						imageViewhead.setVisibility(View.VISIBLE);
					}
					displayPhoto();
					if (videoName != null && !"".equals(videoName)) {
						try {
							FileInputStream f = new FileInputStream(
									Environment.getExternalStorageDirectory()
											+ "/DCIM/eastelsoft/" + videoName);
							bm = null;
							BitmapFactory.Options options = new BitmapFactory.Options();
							options.inSampleSize = 10;// 图片的长宽都是原来的1/10
							// BufferedInputStream bis = new
							// BufferedInputStream(f);
							// bm = BitmapFactory.decodeStream(bis, null,
							// options);
							
							bm = ThumbnailUtils.createVideoThumbnail(
									Environment.getExternalStorageDirectory()
											+ "/DCIM/eastelsoft/" + videoName,
									Images.Thumbnails.MINI_KIND);

							videoView.setBackgroundDrawable(new BitmapDrawable(
									null, bm));
							videoView
									.setImageDrawable(this
											.getResources()
											.getDrawable(
													R.drawable.voiceassistant_playbtn_pressed));

							// this.imgFile = new
							// File(Environment.getExternalStorageDirectory()
							// + "/DCIM/eastelsoft/" + videoName);
							videoView.setVisibility(View.VISIBLE);
							tvInfoVideoline.setVisibility(View.VISIBLE);
							tvInfoVideoHead.setVisibility(View.VISIBLE);
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}
					}
				////////////////////////////////////
					if (reSoundName != null && !"".equals(reSoundName)){
							recordView.setVisibility(View.VISIBLE);
							tvInfoRecordline.setVisibility(View.VISIBLE);
							tvInfoRecordHead.setVisibility(View.VISIBLE);
							textRecord_time.setText(setLongtime1.toString()+"'");
							textRecord_time.setVisibility(View.VISIBLE);
					}
				}
			}
		}
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

	private void displayPhoto() {
		try {
			bms = new Bitmap[imgs.length];
			for (int i = 0; i < imgs.length; i++) {
				BitmapFactory.Options options = new BitmapFactory.Options();
				options.inSampleSize = 10;// 图片的长宽都是原来的1/10
				FileInputStream f = new FileInputStream(imgs[i]);
				BufferedInputStream bis = new BufferedInputStream(f);
				Bitmap bm = BitmapFactory.decodeStream(bis, null, options);
				bms[i] = bm;
			}

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
			globalVar.setImgs(imgs);
			// 打开照片预览页面
			Intent intent = new Intent();
			intent.setClass(InfoViewActivity.this, GalleryActivity.class);
			intent.putExtra("position", position);
			intent.putExtra("type", Contant.VIEW);
			InfoViewActivity.this.startActivity(intent);
			// InfoAddActivity.this.startActivityForResult(intent, PHOTO_DEL);

		}

		private Context mContext;
	}

	/**
	 * 设置GridView的图片适配器
	 */
	public class GridImageAdapter extends BaseAdapter {
		private Bitmap[] bms;

		public GridImageAdapter(Context c, Bitmap[] bms) {
			mContext = c;
			this.bms = bms;
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
						(dm.widthPixels - 100) / imageCol - 15,
						(dm.widthPixels - 100) / imageCol - 15));
				// imageView.setLayoutParams(new GridView.LayoutParams(80,80));
				imageView.setAdjustViewBounds(true);
				imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

			} else {
				imageView = (ImageViewExt) convertView;
			}

			imageView.setImageBitmap(bms[position]);

			return imageView;
		}

		private Context mContext;

	}

	@Override
	protected void onDestroy() {

		if (locationHelper != null
				&& locationHelper.getWritableDatabase() != null) {
			locationHelper.getWritableDatabase().close();
		}
		if (bm != null && !bm.isRecycled())
			bm.recycle();
		bm = null;
		if (mBound) {
			this.getApplicationContext().unbindService(sc);
			mBound = false;
		}
		super.onDestroy();

	}

//	int num;
	Handler handler2 = new Handler();
	Runnable thread = new Runnable() {
		@Override
		public void run() {
			countdown--;
		//	num=countdown;
			if(countdown>=Integer.parseInt(setLongtime1)-1){
				handler2.removeCallbacks(thread);
			}
			handler2.postDelayed(thread, 1000);
			textRecord_time.setText(countdown+"");
			if(countdown<1){
				textRecord_time.setText("播放完毕");
				handler2.removeCallbacks(thread);
			}
		}
	};
	
	private class OnInfoRecordClickListenerImpl implements OnClickListener{
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			File file = new File(reSoundName);
			if(file.exists()){
				textRecord_time.setText(setLongtime1);
				countdown=Integer.parseInt(setLongtime1);
				playMusic(reSoundName);		
				handler2.postDelayed(thread, 1000);
			}
		}
	}
	
	

	private class OnInfoLocationClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				Intent intent = new Intent(InfoViewActivity.this,
						ItemizedOverlayBaiduActivity.class);
				intent.putExtra("lon", lon);
				intent.putExtra("lat", lat);
				intent.putExtra("location", location);
				startActivity(intent);
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	private class OnBtBackClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				InfoViewActivity.this.finish();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	private class OnBtAddClickListenerImpl implements OnClickListener{
		public void onClick(View v) {
			try {
				Thread addInfoThread = new Thread(new AddInfoThread());
				addInfoThread.start();
				Toast.makeText(getApplicationContext(), "信息后台上传中...",
						Toast.LENGTH_LONG).show();
				InfoViewActivity.this.finish();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	class AddInfoThread implements Runnable {
		@Override
		public void run() {
			Looper.prepare();
			Message msg = handler.obtainMessage();
			locationService.updateChangeInformation(title, remark, lon, lat,
					uploadDate, info_auto_id, location, videoName, imgs,
					reSoundName);
			Looper.loop();
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
						// 任务上报成功
						// dialog(InfoAddActivity.this,
						// getResources().getString(
						// R.string.info_upload_succ));
						// 返回成功数据写库
						/*
						 * uploadDate =
						 * Util.getLocaleTime("yyyy-MM-dd HH:mm:ss"); if
						 * (imgFileName == null) imgFileName = "";
						 * DBUtil.insertLInfo(
						 * locationHelper.getWritableDatabase(), uploadDate,
						 * title, imgFileName, remark, lon, lat,
						 * UUID.randomUUID().toString(),
						 * tvInfoLocationDesc.getText().toString(),"11");
						 */

						try {
							DBUtil.updateLInfo(
									locationHelper.getWritableDatabase(),
									info_auto_id);

							Toast.makeText(
									InfoViewActivity.this,
									getResources().getString(
											R.string.info_upload_succ),
									Toast.LENGTH_SHORT).show();
							InfoViewActivity.this.finish();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();

						}
					} else {
						Toast.makeText(
								InfoViewActivity.this,
								getResources().getString(
										R.string.info_upload_err),
								Toast.LENGTH_SHORT).show();
					}
					break;
				case 1:
					Toast.makeText(InfoViewActivity.this,
							getResources().getString(R.string.info_upload_err),
							Toast.LENGTH_SHORT).show();
					break;

				}
			} catch (Exception e) {
				// 异常中断
			}
		}
	};

	private class OnBtVideoViewClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				// Intent it = new Intent(Intent.ACTION_VIEW);
				// Uri uri = Uri.parse(Environment.getExternalStorageDirectory()
				// + "/DCIM/eastelsoft/" + InfoViewActivity.this.videoName);
				// it.setDataAndType(uri , "video/*");
				// startActivity(it);

				Intent intent = new Intent(Intent.ACTION_VIEW);
				intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				intent.putExtra("oneshot", 0);
				intent.putExtra("configchange", 0);

				Uri uri = Uri.fromFile(new File(Environment
						.getExternalStorageDirectory()
						+ "/DCIM/eastelsoft/"
						+ InfoViewActivity.this.videoName));

				intent.setDataAndType(uri, "video/*");
				startActivity(intent);
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}
	
	
	// 播放录音
			public static void playMusic(String name) {
				synchronized(name){
				Log.i("播放声音", "playMusic");
				try {
					if (mMediaPlayer.isPlaying()){
						System.out.println("进入停止阶段InfoView");
						textRecord_time.setText(setLongtime1.toString());
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
			
}
