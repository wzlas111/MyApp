/**
 * Copyright (c) 2012-7-21 www.eastelsoft.com
 * $ID FileService.java 上午12:42:13 $
 */
package com.eastelsoft.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.content.Context;
import android.content.ContextWrapper;
import android.util.Log;

/**
 * Android私有文件读写
 * @author lengcj
 */
public class FileService extends ContextWrapper {
	
	public FileService(Context base) {
		super(base);
	}

	public boolean writeSettings(Context context, String data) {
		FileOutputStream fOut = null;
		OutputStreamWriter osw = null;

		try {
			fOut = openFileOutput("lbs_settings.dat", MODE_PRIVATE);
			osw = new OutputStreamWriter(fOut);
			osw.write(data);
			osw.flush();
			//Toast.makeText(context, "Settings saved", Toast.LENGTH_SHORT)
			//		.show();
			Log.i("FileService writeSettings", data);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			//Toast.makeText(context, "Settings not saved", Toast.LENGTH_SHORT)
			//		.show();
		} finally {
			try {
				osw.close();
				fOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return false;
	}
	
	/**
	 * 读参数设置文件
	 * @param context
	 * @return
	 */
	public String ReadSettings(Context context) {
		FileInputStream fIn = null;
		InputStreamReader isr = null;

		char[] inputBuffer = new char[255];
		String data = null;

		try {
			fIn = openFileInput("lbs_settings.dat");
			isr = new InputStreamReader(fIn);
			isr.read(inputBuffer);
			data = new String(inputBuffer);
			Log.i("FileService ReadSettings", data);
			//Toast.makeText(context, "Settings read:" + data, Toast.LENGTH_SHORT).show();
		} catch (Exception e) {
			//e.printStackTrace();
			//Toast.makeText(context, "Settings not read:" + e.getMessage(), Toast.LENGTH_SHORT)
			//		.show();
		} finally {
			try {
				if(isr != null)isr.close();
				if(fIn != null)fIn.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return data;
	}
}
