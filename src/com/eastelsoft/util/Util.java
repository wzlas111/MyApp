/**
 * Copyright (c) 2012-7-21 www.eastelsoft.com
 * $ID Util.java 上午12:42:13 $
 */
package com.eastelsoft.util;

import java.io.File;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.eastelsoft.lbs.BulletinviewActivity;
import com.eastelsoft.lbs.R;
import com.eastelsoft.util.contact.OpenFile;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * 通用工具类
 * 
 * @author lengcj
 * 
 */
public class Util {

	/**
	 * 输出16进制字符串
	 * 
	 * @param b
	 */
	public static void printHexString(byte[] b) {
		for (int i = 0; i < b.length; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			Log.i("REQDATA", hex.toUpperCase());
		}
	}

	/**
	 * 16位MD5
	 * 
	 * @param data
	 * @return
	 */
	public static String Md5_(byte[] data) {
		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(data);
			byte b[] = md.digest();

			int i;

			StringBuffer buf = new StringBuffer("");
			for (int offset = 0; offset < b.length; offset++) {
				i = b[offset];
				if (i < 0)
					i += 256;
				if (i < 16)
					buf.append("0");
				buf.append(Integer.toHexString(i));
			}
			return buf.toString().substring(8, 24);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "";
	}

	/**
	 * 转16进制
	 * 
	 * @param s
	 * @return
	 */
	public static String toStringHex(String s) {
		byte[] baKeyword = new byte[s.length() / 2];
		for (int i = 0; i < baKeyword.length; i++) {
			try {
				baKeyword[i] = (byte) (0xff & Integer.parseInt(
						s.substring(i * 2, i * 2 + 2), 16));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		try {
			s = new String(baKeyword, "utf-8 ");// UTF-16le:Not
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		return s;
	}

	/**
	 * 根据格式获取时间字符串
	 * 
	 * @param format
	 * @return
	 */
	public static String getLocaleTime(String format) {
		SimpleDateFormat simpleDateTimeFormat = new SimpleDateFormat(format);
		String time = simpleDateTimeFormat.format(Calendar.getInstance(
				Locale.CHINESE).getTime());
		return time;
	}

	/**
	 * 获取星期几
	 * 
	 * @return
	 */
	public static int getLocaleWeek() {
		Calendar cal = Calendar.getInstance(Locale.CHINESE);
		cal.setTime(new Date());
		int time = cal.get(Calendar.DAY_OF_WEEK);
		return time;
	}

	/**
	 * 格式化
	 * 
	 * @param str
	 * @return
	 */
	public static String format(Double d, String f) {
		DecimalFormat df = new DecimalFormat(f);
		String ret = df.format(d);
		return ret;
	}

	public static String format(String s) {
		String ret = s;
		if (s.indexOf(".") > -1)
			ret = s.substring(0, s.indexOf("."));
		return ret;
	}

	public static boolean chkNumber(String str) {
		if (!str.startsWith("1")) {
			return false;
		}
		if (str.length() < 11) {
			return false;
		}
		return isNumeric(str);
	}

	public static boolean isNumeric(String str) {
		Pattern pattern = Pattern.compile("[0-9]*");
		Matcher isNum = pattern.matcher(str);
		if (!isNum.matches()) {
			return false;
		}
		return true;
	}

	public static String dateToStr(Date date, String format) {
		SimpleDateFormat sdf = new SimpleDateFormat(format);// 时间格式化
		return sdf.format(date);
	}

	public static String getLocaleTime() {
		String ret = String.valueOf(Calendar.getInstance(Locale.CHINESE)
				.getTime().getTime() / 1000);
		return ret;
	}

	public static void main(String[] args) throws Exception {
		System.out.println(Util.getLocaleTime("yyyy-MM-dd hh:mm:ss"));
	}

	public static void playfile(String path,Context context) {
		File file;
		String fileName = "";
		Intent intent;
		try {
			file = new File(path);

			if (file != null && file.isFile()) {
				fileName = file.getName().toString().toLowerCase();
				if (CheckEndsWithInStringArray(fileName, context.getResources()
						.getStringArray(R.array.fileEndingPPt))) {
					System.out.println("打开PPt");
					intent = OpenFile.getPPTlFileIntent(file);
					context.startActivity(intent);
				} else if (CheckEndsWithInStringArray(fileName, context.getResources()
						.getStringArray(R.array.fileEndingImage))) {//
					System.out.println("打开Image");
					intent = OpenFile.getImageFileIntent(file);
					context.startActivity(intent);
				} else if (CheckEndsWithInStringArray(fileName, context.getResources()
						.getStringArray(R.array.fileEndingPdf))) {
					System.out.println("打开Pdf");
					intent = OpenFile.getPdfFileIntent(file);
					context.startActivity(intent);
				} else if (CheckEndsWithInStringArray(fileName, context.getResources()
						.getStringArray(R.array.fileEndingExcel))) {
					System.out.println("打开Excel");
					intent = OpenFile.getExcelFileIntent(file);
					context.startActivity(intent);
				} else if (CheckEndsWithInStringArray(fileName, context.getResources()
						.getStringArray(R.array.fileEndingWord))) {
					System.out.println("打开Word");
					intent = OpenFile.getWordFileIntent(file);
					context.startActivity(intent);
				} else if (CheckEndsWithInStringArray(fileName, context.getResources()
						.getStringArray(R.array.fileEndingText))) {
					System.out.println("打开TXT");
					intent = OpenFile.getTextFileIntent(file);
					context.startActivity(intent);
				} else if (CheckEndsWithInStringArray(fileName, context.getResources()
						.getStringArray(R.array.fileEndingPackage))) {
					System.out.println("打开Package");
					intent = OpenFile.getApkFileIntent(file);
					context.startActivity(intent);
				} else {
					Toast.makeText(context, "无法打开，请安装相应的软件！",
							Toast.LENGTH_SHORT).show();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			Toast.makeText(context, "无法打开，请安装相应的软件！",
					Toast.LENGTH_SHORT).show();

		}
	}
	
	
	public static boolean CheckEndsWithInStringArray(String checkItsEnd,
			String[] fileEndings) {
		for (String aEnd : fileEndings) {
			System.out.println("checkItsEnd:" + checkItsEnd + ",aEnd:" + aEnd);
			if (checkItsEnd.endsWith(aEnd))
				return true;
		}
		return false;

	}

}
