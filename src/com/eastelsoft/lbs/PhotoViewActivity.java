/**
 * Copyright (c) 2012-8-18 www.eastelsoft.com
 * $ID PhotoViewActivity.java 下午2:50:03 $
 */
package com.eastelsoft.lbs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.eastelsoft.util.FileLog;

/**
 * 照片显示
 * 
 * @author lengcj
 */
public class PhotoViewActivity extends Activity {
	public static final String TAG = "PhotoViewActivity";
	private Intent intent;
	private Button btBack;
	private Button btDel;
	private ImageView imageView;
	private Uri imgUri;
	private Bitmap bm;
	private static final int IO_BUFFER_SIZE = 4 * 1024 * 1024 * 10;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_photoview);
		btBack = (Button) findViewById(R.id.btPhotoBack);
		btBack.setOnClickListener(new OnBtBackClickListenerImpl());
		btDel = (Button) findViewById(R.id.btPhoneDel);
		btDel.setOnClickListener(new OnBtDelClickListenerImpl());
	
		imageView = (ImageView)findViewById(R.id.photoImg);

		imageView.setVisibility(View.VISIBLE);
		
		
		intent = this.getIntent();
		String imgFileName = intent.getStringExtra("imgFileName");
		String opTag = intent.getStringExtra("opTag");
		if(opTag != null && "1".equals(opTag)) {
			btDel.setVisibility(View.GONE);
			btBack.setVisibility(View.GONE);
		}
		try {
			if (bm != null && !bm.isRecycled())
		    	bm.recycle();
			FileInputStream f = new FileInputStream("/mnt/sdcard/DCIM/eastelsoft/" + imgFileName); 
			bm = null; 
			BitmapFactory.Options options = new BitmapFactory.Options(); 
			options.inSampleSize = 1;//图片的长宽都是原来的1/8 
			BufferedInputStream bis = new BufferedInputStream(f); 
			bm = BitmapFactory.decodeStream(bis, null, options); 
			imageView.setImageBitmap(bm);
		} catch (FileNotFoundException e) {
		} 
	}
	
	private class OnBtBackClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				PhotoViewActivity.this.finish();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}
	
	private class OnBtDelClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				PhotoViewActivity.this.setResult(RESULT_OK, intent);
				PhotoViewActivity.this.finish();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}
	
	@Override  
	protected void onDestroy() {  
		super.onDestroy();
	    imgUri = null;
	    imageView = null;
	    if (bm != null && !bm.isRecycled())
	    	bm.recycle();
	    bm = null;
	}  

//	public static Bitmap GetLocalOrNetBitmap(String url) {
//		Bitmap bitmap = null;
//		InputStream in = null;
//		BufferedOutputStream out = null;
//		try {
//			in = new BufferedInputStream(new URL(url).openStream(),
//					IO_BUFFER_SIZE);
//			final ByteArrayOutputStream dataStream = new ByteArrayOutputStream();
//			out = new BufferedOutputStream(dataStream, IO_BUFFER_SIZE);
//			copy(in, out);
//			out.flush();
//			byte[] data = dataStream.toByteArray();
//			bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
//			data = null;
//			return bitmap;
//		} catch (IOException e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//	
//	private static void copy(InputStream in, OutputStream out)
//            throws IOException {
//        byte[] b = new byte[IO_BUFFER_SIZE];
//        int read;
//        while ((read = in.read(b)) != -1) {
//            out.write(b, 0, read);
//        }
//    }
}
