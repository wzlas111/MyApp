package com.eastelsoft.util.file;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Arrays;
import java.util.Comparator;

import com.eastelsoft.util.GlobalVar;

import android.os.Environment;
import android.text.TextUtils;

public class FileManager {
	
	public static String BASE_PATH = Environment.getExternalStorageDirectory() 
			+ File.separator + "DCIM" 
			+ File.separator + "eastelsoft" 
			+ File.separator;
	
	public static String PHOTO_SIGN = BASE_PATH + "sign";
	
	public static String PHOTO_TEST = BASE_PATH + "test.jpg";
	
	public static String LOG = "log";
	public static String CRASH = "crash";
	
	public static String getSdCardPath() {
		if (isExternalStorageMounted()) {
			return GlobalVar.getInstance().getExternalCacheDir().getAbsolutePath();
		}
		return "";
	}
	
	public static String getLogDir() {
		if (!isExternalStorageMounted()) {
			return "";
		} else {
			String path = getSdCardPath() + File.separator + LOG;
			if (!new File(path).exists()) {
				new File(path).mkdir();
			}
			return path;
		}
	}
	
	public static String getCrashDir() {
		if (!isExternalStorageMounted()) {
			return "";
		} else {
			String path = getSdCardPath() + File.separator + CRASH;
			if (!new File(path).exists()) {
				new File(path).mkdir();
			}
			return path;
		}
		
	}
	
	/**
	 * search for all logs
	 * @return
	 */
	public static String[] searchForLogs() {
		String path = getLogDir();
		if (TextUtils.isEmpty(path)) {
			return null;
		}
		File dir = new File(path);
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File dir, String filename) {
				return filename.endsWith(".txt");
			}
		};
		String[] files = dir.list(filter);
		for (int i = 0; i < files.length; i++) {
			files[i] = path + File.separator + files[i];
		}
		Arrays.sort(files, new Comparator<String>() {
			public int compare(String lhs, String rhs) {
				long l = new File(lhs).lastModified();
				long r = new File(rhs).lastModified();
				if (l > r) {
					return -1;
				} else {
					return 1;
				}
			}
		});
		return files;
	}
	
	public static boolean isExternalStorageMounted() {
		boolean canRead = Environment.getExternalStorageDirectory().canRead();
		boolean canWrite = Environment.getExternalStorageDirectory().canWrite();
		boolean mounted = Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
		return (canRead && canWrite && mounted);
	} 
}
