package com.eastelsoft.lbs.activity.visit.adapter;

import com.eastelsoft.lbs.photo.ImageViewExt;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

public class GridPhotoAdapter extends BaseAdapter {

	private Context mContext;
	private Bitmap[] mBitmaps;
	private int mWidth;
	private int mHeight;
	private String img_num = "5";

	public GridPhotoAdapter(Context context, Bitmap[] bitmaps, int width, int height) {
		mContext = context;
		mBitmaps = bitmaps;
		mWidth = width;
		mHeight = height;
	}

	@Override
	public int getCount() {
		return mBitmaps.length;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ImageViewExt imageView;

		if (convertView == null) {
			imageView = new ImageViewExt(mContext);
			// 如果是横屏，GridView会展示4列图片，需要设置图片的大小
			imageView.setLayoutParams(new GridView.LayoutParams(
					(mWidth - 100) / 4 - 30,
					(mWidth - 100) / 4 - 30));
			imageView.setAdjustViewBounds(true);
			imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
		} else {
			imageView = (ImageViewExt) convertView;
		}

		imageView.setImageBitmap(mBitmaps[position]);
		if ((position + 1) == mBitmaps.length) {
			if (String.valueOf(mBitmaps.length - 1).equals(img_num)) {
				imageView.setVisibility(View.INVISIBLE);
			}
		}

		return imageView;
	}
}
