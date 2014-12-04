/**
 * Copyright (c) 2012-8-14 www.eastelsoft.com
 * $ID FileUtil.java 下午3:14:16 $
 */
package com.eastelsoft.util;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

/**
 * 文件操作类
 * 
 * @author lengcj
 */
public class FileUtil {
	public static final String TAG="FileUtil";
	/**
	 * 将Bitmap转换为File
	 *@param bitMap
	 *@return
	 */
	public static File saveBitmapToFile1(Bitmap bitMap) {
		String pictureDir = "";
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			bitMap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
			byte[] byteArray = baos.toByteArray();
			String saveDir = Environment.getExternalStorageDirectory()
					+ "/DCIM/eastelsoft";
			File dir = new File(saveDir);
			if (!dir.exists()) {
				dir.mkdir();
			}
			File file = new File(saveDir, "temp.jpg");
			file.delete();
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(byteArray);
			pictureDir = file.getPath();
			return file;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (baos != null) {
				try {
					baos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
//			try {
//				bitMap.recycle();
//			} catch (Exception e) {
//			}
		}
		return null;
	}
	
	public static File saveBitmapToFile(Bitmap bitMap, String filename) {
		String pictureDir = "";
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			bitMap.compress(Bitmap.CompressFormat.JPEG, 95, baos);
			
			byte[] byteArray = baos.toByteArray();
			String saveDir = Environment.getExternalStorageDirectory()
					+ "/DCIM/eastelsoft";
			File dir = new File(saveDir);
			if (!dir.exists()) {
				dir.mkdir();
			}
			File file = new File(saveDir, filename);
			file.delete();
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(byteArray);
			pictureDir = file.getPath();
			return file;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (baos != null) {
				try {
					baos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
//			try {
//				bitMap.recycle();
//			} catch (Exception e) {
//			}
		}
		return null;
	}
	
	public static String saveBitmapToFileForPath(Bitmap bitMap, String filename) {
		String pictureDir = "";
		FileOutputStream fos = null;
		BufferedOutputStream bos = null;
		ByteArrayOutputStream baos = null;
		try {
			baos = new ByteArrayOutputStream();
			bitMap.compress(Bitmap.CompressFormat.JPEG, 95, baos);
			
			byte[] byteArray = baos.toByteArray();
			String saveDir = Environment.getExternalStorageDirectory()
					+ "/DCIM/eastelsoft";
			File dir = new File(saveDir);
			if (!dir.exists()) {
				dir.mkdir();
			}
			File file = new File(saveDir, filename);
			file.delete();
			if (!file.exists()) {
				file.createNewFile();
			}
			fos = new FileOutputStream(file);
			bos = new BufferedOutputStream(fos);
			bos.write(byteArray);
			pictureDir = file.getPath();
			return saveDir + "/" + filename;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (baos != null) {
				try {
					baos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (fos != null) {
				try {
					fos.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
//			try {
//				bitMap.recycle();
//			} catch (Exception e) {
//			}
		}
		return null;
	}
	
	
	/*
	 * 遍历日志文件
	 */
	public  static List<String> findLogFile(){
		List<String> return_str=null;
		 try {
			File file = new File(Environment.getExternalStorageDirectory().getCanonicalPath()+"/eastelsoft/");
			return_str = new ArrayList<String>(Arrays.asList(file.list()));
			Iterator<String> iterator = return_str.iterator();
			while(iterator.hasNext()){
				String s = iterator.next();
				if(!s.endsWith(".txt")){
					iterator.remove();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return return_str;
	}
	
	public static void deleteDailyFile(){
		List<String> dailyFile=findLogFile();
		for(int i=0;i<dailyFile.size();i++){
				String[] s=dailyFile.get(i).split("-");
				DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");  
				Date dDate=null;
				try {
					dDate = format.parse(s[0]+"-"+s[1]+"-"+s[2]+" 00:00:00");
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}    
				Log.i(TAG,"--->"+s[0]+"-"+s[1]+"-"+s[2]+"????"+s[3]);
				if(dDate.getTime()+20*24*60*60*1000<System.currentTimeMillis()){
					deleteFile(dailyFile.get(i));
					Log.i(TAG, "删除日志");
				}else{
					Log.i(TAG, dailyFile.get(i)+"文件保留不删除");
				}

		}
	}
	
	
	public static void deleteFile(String str){
		try {
			File file = new File(Environment.getExternalStorageDirectory().getCanonicalPath()+"/eastelsoft/"+str);
			if(file.exists()){
				file.delete();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
