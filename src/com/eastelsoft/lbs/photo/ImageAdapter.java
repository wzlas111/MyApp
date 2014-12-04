package com.eastelsoft.lbs.photo;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Gallery.LayoutParams;
import android.widget.ImageView;

/**
 * 
 * 图片适配器，用来加载图片
 */
public class ImageAdapter extends BaseAdapter {
	// 图片适配器
	// 定义Context
	private int ownposition;

	public int getOwnposition() {
		return ownposition;
	}

	public void setOwnposition(int ownposition) {
		this.ownposition = ownposition;
	}

	//private Activity mActivity;
	
	private Context mContext;

	private String[] imgpath;
	
	WeakReference<GalleryActivity> weak;

	// 定义整型数组 即图片源

	// 声明 ImageAdapter
	public ImageAdapter(Context c, String[] imgpath) {
		this.mContext = c;
		this.imgpath = imgpath;
		this.weak = new WeakReference<GalleryActivity>((GalleryActivity)c);
	}

	// 获取图片的个数
	public int getCount() {
		return imgpath.length;
	}

	// 获取图片在库中的位置
	public Object getItem(int position) {
		ownposition = position;
		return position;
	}

	// 获取图片ID
	public long getItemId(int position) {
		ownposition = position;
		return position;
	}

	@SuppressWarnings("deprecation")
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ownposition = position;
		ImageView imageview = new ImageView(mContext);
		imageview.setBackgroundColor(0xFF000000);
		imageview.setScaleType(ImageView.ScaleType.FIT_CENTER);
		imageview.setLayoutParams(new GalleryExt.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		try {
			FileInputStream f = new FileInputStream(imgpath[position]);
			Bitmap bm = null;
			BitmapFactory.Options options = new BitmapFactory.Options();
			options.inSampleSize = 1;
			BufferedInputStream bis = new BufferedInputStream(f);
			bm = BitmapFactory.decodeStream(bis, null, options);
			imageview.setImageBitmap(bm);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		// imageview.setImageResource(ImageSource.mThumbIds[position]);
		// imageview.setAdjustViewBounds(true);
		// imageview.setLayoutParams(new GridView.LayoutParams(320, 480));
		// imageview.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
		return imageview;
	}
}
