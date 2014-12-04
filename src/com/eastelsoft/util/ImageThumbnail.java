package com.eastelsoft.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.util.Log;

public class ImageThumbnail {

	/**
	 * 计算缩放比
	 * @param oldWidth
	 * @param oldHeight
	 * @param newWidth
	 * @param newHeight
	 * @return
	 */
    public static int reckonThumbnail(int oldWidth, int oldHeight, int newWidth, int newHeight) {
        if ((oldHeight > newHeight && oldWidth > newWidth)
                || (oldHeight <= newHeight && oldWidth > newWidth)) {
            int be = (int) (oldWidth / (float) newWidth);
            if (be <= 1)
                be = 1;
            return be;
        } else if (oldHeight > newHeight && oldWidth <= newWidth) {
            int be = (int) (oldHeight / (float) newHeight);
            if (be <= 1)
                be = 1;
            return be;
        }

        return 1;
    }

    /**
     * @param photoPath --原图路经
     * @param aFile     --保存缩图
     * @param newWidth  --缩图宽度
     * @param newHeight --缩图高度
     */
    public static boolean bitmapToFile(String photoPath, File aFile, int newWidth, int newHeight) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        // 获取这个图片的宽和高
        Bitmap bitmap = BitmapFactory.decodeFile(photoPath, options);
        options.inJustDecodeBounds = false;
 
        //计算缩放比
        options.inSampleSize = reckonThumbnail(options.outWidth, options.outHeight, newWidth, newHeight);

        bitmap = BitmapFactory.decodeFile(photoPath, options);

        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] photoBytes = baos.toByteArray();

            if (aFile.exists()) {
                aFile.delete();
            }
            aFile.createNewFile();

            FileOutputStream fos = new FileOutputStream(aFile);
            fos.write(photoBytes);
            fos.flush();
            fos.close();

            return true;
        } catch (Exception e1) {
            e1.printStackTrace();
            if (aFile.exists()) {
                aFile.delete();
            }
            Log.e("Bitmap To File Fail", e1.toString());
            return false;
        }
    }

    /**
     * 缩放图片
     * @param bmp
     * @param width
     * @param height
     * @return
     */
    public static Bitmap PicZoom(Bitmap bmp, int width, int height) {
        int bmpWidth = bmp.getWidth();
        int bmpHeght = bmp.getHeight();
        Matrix matrix = new Matrix();
        if(bmpHeght > bmpWidth) 
        	matrix.postScale((float) height / bmpHeght, (float) height / bmpHeght);
        else
        	matrix.postScale((float) width / bmpWidth, (float) width / bmpWidth);

        return Bitmap.createBitmap(bmp, 0, 0, bmpWidth, bmpHeght, matrix, true);
    }
    
    public static Bitmap PicZoom(String path) {
    	BitmapFactory.Options options1 = new BitmapFactory.Options();
		 options1.inJustDecodeBounds = true;
		 Bitmap bmp = BitmapFactory.decodeFile(path, options1);
		 int mWidth = options1.outWidth; // 原图宽度
		 int mHeight = options1.outHeight; // 原图高度
		 FileLog.i("TAG", "mWidth======================================" + mWidth);
		 FileLog.i("TAG", "mHeight======================================" + mHeight);
		 //FileLog.i(TAG, mWidth + " + " + mHeight);
		 //FileInputStream f = new FileInputStream(
		 //		 Environment.getExternalStorageDirectory()
		 //			 + "/DCIM/eastelsoft/" + imgFileName);
		 
		 // 计算缩图的宽高，生成压缩图片
		 BitmapFactory.Options options2 = new BitmapFactory.Options();
		 int nWidth = 0, nHeight = 0;
		 if(mWidth > mHeight) {
			 if(mHeight > 480)
				 nHeight = 480;
			 else
				 nHeight = mHeight;
			 nWidth = (nHeight * mWidth) / mHeight;
		 } else {
			 if(mWidth > 480) 
				 nWidth = 480;
			 else
				 nWidth = mWidth;
			 nHeight = (nWidth * mHeight) / mWidth;
		 }
		 options2.outWidth = nWidth;
		 options2.outHeight = nHeight;
		 //FileLog.i(TAG, nWidth + " + " + nHeight);
		 options2.inJustDecodeBounds = false;
		 int inSampleSize = mHeight / nHeight;
		 options2.inSampleSize = inSampleSize;
		 //options2.inPreferredConfig = Bitmap.Config.ARGB_4444; 
		 options2.inPurgeable = true;
		 options2.inInputShareable = true;
		 return BitmapFactory.decodeFile(path, options2);
    }
    
    /**
    * 把图片变成圆角   
    * @param bitmap 需要修改的图片   
    * @param pixels 圆角的弧度   
    * @return 圆角图片   
    */    
   public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {    
     
       Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);    
       Canvas canvas = new Canvas(output);    
     
       final int color = 0xff424242;    
       final Paint paint = new Paint();    
       final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());    
       final RectF rectF = new RectF(rect);    
       final float roundPx = pixels;    
     
       paint.setAntiAlias(true);    
       canvas.drawARGB(0, 0, 0, 0);    
       paint.setColor(color);    
       canvas.drawRoundRect(rectF, roundPx, roundPx, paint);    
     
       paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));    
       canvas.drawBitmap(bitmap, rect, rect, paint);    
     
       return output;    
   }  
   
   /**
    * 获取图片的倒影 
    * @param bitmap
    * @return
    */
	public static Bitmap createReflectionImageWithOrigin(Bitmap bitmap) {
		final int reflectionGap = 4;
		int width = bitmap.getWidth();
		int height = bitmap.getHeight();

		Matrix matrix = new Matrix();
		matrix.preScale(1, -1);

		Bitmap reflectionImage = Bitmap.createBitmap(bitmap, 0, height / 2,
				width, height / 2, matrix, false);

		Bitmap bitmapWithReflection = Bitmap.createBitmap(width,
				(height + height / 2), Config.ARGB_8888);

		Canvas canvas = new Canvas(bitmapWithReflection);
		canvas.drawBitmap(bitmap, 0, 0, null);
		Paint deafalutPaint = new Paint();
		canvas.drawRect(0, height, width, height + reflectionGap, deafalutPaint);

		canvas.drawBitmap(reflectionImage, 0, height + reflectionGap, null);

		Paint paint = new Paint();
		LinearGradient shader = new LinearGradient(0, bitmap.getHeight(), 0,
				bitmapWithReflection.getHeight() + reflectionGap, 0x70ffffff,
				0x00ffffff, TileMode.CLAMP);
		paint.setShader(shader);
		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
		canvas.drawRect(0, height, width, bitmapWithReflection.getHeight()
				+ reflectionGap, paint);
		return bitmapWithReflection;
	}
	
	/**
	 * 将Drawable转化为Bitmap 
	 * @param drawable
	 * @return
	 */
	public static Bitmap drawableToBitmap(Drawable drawable) {
		int width = drawable.getIntrinsicWidth();
		int height = drawable.getIntrinsicHeight();
		Bitmap bitmap = Bitmap.createBitmap(width, height, drawable
				.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888
				: Bitmap.Config.RGB_565);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, width, height);
		drawable.draw(canvas);
		return bitmap;
	}
	
	
	public static int readPictureDegree(String path) {
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
	
	
}
