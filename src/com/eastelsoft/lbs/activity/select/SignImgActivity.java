package com.eastelsoft.lbs.activity.select;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.activity.visit.VisitMcAddActivity;
import com.eastelsoft.lbs.widget.PaintView;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.TextView;

public class SignImgActivity extends Activity implements OnClickListener {

	private String mBasePath = Environment.getExternalStorageDirectory() 
			+ File.separator + "DCIM" + File.separator + "eastelsoft" + File.separator + "sign" ;
	private Bitmap mSignBitmap;
	private String mSignName;
	
	private Button mBackBtn;
	private TextView mSaveBtn;
	private TextView mResignBtn;
	private PaintView mPaintView;
	private DisplayMetrics mDisplayMetrics;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_visit_mc_sign);
		
		System.out.println("onCreate");
		initView();
	}
	
	private void initView() {
		mBackBtn = (Button)findViewById(R.id.btBack);
		mSaveBtn = (TextView)findViewById(R.id.save);
		mResignBtn = (TextView)findViewById(R.id.resign);
		mPaintView = (PaintView)findViewById(R.id.paint_view);
		
		mPaintView.requestFocus();
		
		mBackBtn.setOnClickListener(this);
		mSaveBtn.setOnClickListener(this);
		mResignBtn.setOnClickListener(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		System.out.println("onResume");
		mDisplayMetrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(mDisplayMetrics);
		mPaintView.init(mDisplayMetrics.widthPixels,mDisplayMetrics.heightPixels-125);
	}
	
	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btBack:
			setResult(1);
			finish();
			break;
		case R.id.save:
			mSignBitmap = mPaintView.getCachebBitmap();
			mSignName = createFile();
			/*BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 15;
			options.inTempStorage = new byte[5 * 1024];
			Bitmap zoombm = BitmapFactory.decodeFile(signPath, options);*/	
			Intent intent = new Intent(this, VisitMcAddActivity.class);
			intent.putExtra("sign_path", mBasePath + File.separator + mSignName);
			intent.putExtra("sign_name", mSignName);
			setResult(1, intent);
			finish();
			break;
		case R.id.resign:
			mPaintView.clear();
			break;
		}
	}
	
	/**
	 * 创建手写签名文件
	 * 
	 * @return
	 */
	private String createFile() {
		ByteArrayOutputStream baos = null;
		String _path = null;
		String _name = null;
		try {
			if (!new File(mBasePath).exists()) {
				new File(mBasePath).mkdir();
			}
			_name = System.currentTimeMillis() + ".jpg";
			_path = mBasePath + File.separator + _name;
			baos = new ByteArrayOutputStream();
			mSignBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			byte[] photoBytes = baos.toByteArray();
			if (photoBytes != null) {
				new FileOutputStream(new File(_path)).write(photoBytes);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (baos != null)
					baos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return _name;
	}
}
