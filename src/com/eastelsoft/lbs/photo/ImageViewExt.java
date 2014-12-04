package com.eastelsoft.lbs.photo;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 
 * @author 空山不空 扩展ImageView类，将图片加上边框，并且设置边框为灰色
 */
public class ImageViewExt extends ImageView {
	// 将图片加灰色的边框
	private int color;

	public ImageViewExt(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		color = Color.GRAY;
	}

	public ImageViewExt(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		color = Color.GRAY;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub

		super.onDraw(canvas);
		// 画边框
//		Rect rec = canvas.getClipBounds();
//		rec.bottom--;
//		rec.right--;
//		Paint paint = new Paint();
//		paint.setColor(color);
//		paint.setStrokeWidth(1); // 边框
//		paint.setStyle(Paint.Style.STROKE);
//		canvas.drawRect(rec, paint);
	}
}