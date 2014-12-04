/**
 * Copyright (c) 2012-7-22 www.eastelsoft.com
 * $ID AndroidHttpClient.java 下午3:28:05 $
 */
package com.eastelsoft.util.http;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.eastelsoft.util.Contant;
import com.eastelsoft.util.FileLog;

/**
 * HttpClient
 * 
 * @author lengcj
 */
public class AndroidHttpClient {
	private final static String TAG = "AndroidHttpClient";

	/**
	 * HTTP GET
	 * 
	 * @param url
	 * @return
	 * @throws Exception
	 */
	public static String getContent(String url) throws Exception {
		StringBuilder sb = new StringBuilder();
		try {

			HttpClient client = new DefaultHttpClient();
			HttpParams httpParams = client.getParams();
			// 设置网络超时参数
			HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
			HttpConnectionParams.setSoTimeout(httpParams, 100000);
			HttpResponse response = client.execute(new HttpGet(url));
			HttpEntity entity = response.getEntity();
			if (entity != null) {
				BufferedReader reader = new BufferedReader(new InputStreamReader(
						entity.getContent(), "UTF-8"), 8192);
				int statusCode = response.getStatusLine().getStatusCode();
				switch(statusCode) {
				case 200:
					String line = null;
					while ((line = reader.readLine()) != null) {
						sb.append(line + "\n");
					}
					break;
				case 400:
					// 错误请求，地址错误
					break;
				case 403:
					// 服务器拒绝
					break;
				case 408:
					// 请求超时
					break;
				case 500:
					// 服务器端错误
					break;
				case 503:
					// 服务器不可用
					break;
				default:
					break;
				}
				
				reader.close();
			}
		} catch (Exception e) {
			FileLog.i(TAG, e.toString());
		}
		return sb.toString();
	}
	
	public static String getContent(String url, Map<String, String> param) {
		StringBuffer sb = new StringBuffer();
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpParams httpParams = httpClient.getParams();
			// 设置网络超时参数
			HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
			HttpConnectionParams.setSoTimeout(httpParams, 100000);
			HttpPost post = new HttpPost(url);
			
			MultipartEntity entity = new MultipartEntity();
			if (param != null && !param.isEmpty()){
				for (Map.Entry<String, String> entry : param.entrySet()) {
//					entity.addPart(entry.getKey(), new StringBody(new String(entry.getValue().getBytes(),"utf-8")));
					entity.addPart(entry.getKey(), new StringBody(entry.getValue()));
				}
			}
			post.setEntity(entity);
			HttpResponse response = httpClient.execute(post);
			int stateCode = response.getStatusLine().getStatusCode();
			if (stateCode == HttpStatus.SC_OK){
				HttpEntity result = response.getEntity();
				if (result != null) {
					InputStream is = result.getContent();
					BufferedReader br = new BufferedReader(
					new InputStreamReader(is));
					String tempLine;
					while ((tempLine = br.readLine()) != null) {
						sb.append(tempLine);
					}
				}
			}
			post.abort();
		} catch (Exception e) {
			FileLog.e(TAG, "getContent(...)" + e.toString());
		}
		return sb.toString();

	}

	public static String getContent(String url, Map<String, String> param,
			File file, String fileParaName) {

		StringBuffer sb = new StringBuffer();
		
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpParams httpParams = httpClient.getParams();
			// 设置网络超时参数
			HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
			HttpConnectionParams.setSoTimeout(httpParams, 100000);
			HttpPost post = new HttpPost(url);
			MultipartEntity entity = new MultipartEntity();
			if (param != null && !param.isEmpty()) {
				for (Map.Entry<String, String> entry : param.entrySet()) {
					entity.addPart(entry.getKey(), new StringBody(entry.getValue()));
				}
			}
			// 添加文件参数
			if (file != null && file.exists()){
				entity.addPart(fileParaName, new FileBody(file));
			}
			post.setEntity(entity);
			
			HttpResponse response = httpClient.execute(post);
			int stateCode = response.getStatusLine().getStatusCode();
			
			if (stateCode == HttpStatus.SC_OK) {
				HttpEntity result = response.getEntity();
				if (result != null) {
					InputStream is = result.getContent();
					BufferedReader br = new BufferedReader(
					new InputStreamReader(is));
					String tempLine;
					while ((tempLine = br.readLine()) != null) {
						sb.append(tempLine);
					}
				}
			}
			post.abort();
		} catch (Exception e) {
			FileLog.e(TAG, "getContent(...)" + e.toString());
		}
		return sb.toString();
	}
	
	public static String getContent(String url, Map<String, String> param,
			Map<String, File> files, String fileParaName) {
		StringBuffer sb = new StringBuffer();
		Log.e("InfoAddActivity","進入上傳方法中");
		try {
			HttpClient httpClient = new DefaultHttpClient();
			HttpParams httpParams = httpClient.getParams();
			// 设置网络超时参数
			HttpConnectionParams.setConnectionTimeout(httpParams, 10000);
			HttpConnectionParams.setSoTimeout(httpParams, 100000);
			
			HttpPost post = new HttpPost(url);
			
			MultipartEntity entity = new MultipartEntity();
			if (param != null && !param.isEmpty()) {
				for (Map.Entry<String, String> entry : param.entrySet()) {
					entity.addPart(entry.getKey(), new StringBody(entry.getValue()));
				}
			}

			if (files != null && !files.isEmpty()) {
				int i = 0;
				for (Map.Entry<String, File> entry : files.entrySet()) {
					entity.addPart(fileParaName + i, new FileBody(entry.getValue()));
					i++;
				}
			}

			post.setEntity(entity);
			Log.e("InfoAddActivity", param.toString()+"............"+url);
			HttpResponse response = httpClient.execute(post);
			int stateCode = response.getStatusLine().getStatusCode();
			Log.e("InfoAddActivity", stateCode+"");
			if (stateCode == HttpStatus.SC_OK) {
				HttpEntity result = response.getEntity();
				if (result != null) {
					InputStream is = result.getContent();
					BufferedReader br = new BufferedReader(
					new InputStreamReader(is));
					String tempLine;
					while ((tempLine = br.readLine()) != null) {
						sb.append(tempLine);
					}
				}
			}
			post.abort();
		} catch (Exception e) {
			FileLog.e(TAG, "getContent(...)" + e.toString());
		}
		return sb.toString();
	}

	/**
	 * 下载APK文件
	 * postMethod.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
        postMethod.addRequestHeader("Content-Type","text/html;charset=UTF-8");
        postMethod.setRequestHeader("Content-Type", "text/html;charset=UTF-8");
	 * @param path
	 * @param pd
	 * @return
	 * @throws Exception
	 */
	public static File getFileFromServer(String path, ProgressDialog pd,
			Context context) throws Exception {
		// 如果相等的话表示当前的sdcard挂载在手机上并且是可用的
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(10000);
			// 获取到文件的大小
			pd.setMax(conn.getContentLength());
			InputStream is = conn.getInputStream();
			File sdCardDir = Environment.getExternalStorageDirectory();
			File wallpaperDirectory = new File(sdCardDir.getCanonicalPath()
					+ "/eastelsoft/");
			wallpaperDirectory.mkdirs();
			File file = new File(sdCardDir.getCanonicalPath() + "/eastelsoft/"
					+ Contant.UPDATE_SAVENAME);
			FileOutputStream fos = new FileOutputStream(file);
			BufferedInputStream bis = new BufferedInputStream(is);
			byte[] buffer = new byte[1024];
			int len;
			int total = 0;
			while ((len = bis.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
				total += len;
				// 获取当前下载量
				pd.setProgress(total);
			}
			fos.close();
			bis.close();
			is.close();
			return file;
		} else {
			// 下载到设备内存
			InputStream input = null;
			try {
				URL connectURL = new URL(path);
				HttpURLConnection conn = (HttpURLConnection) connectURL
						.openConnection();
				pd.setMax(conn.getContentLength());
				conn.setRequestMethod("POST");
				conn.setReadTimeout(10000);

				input = conn.getInputStream();
			} catch (MalformedURLException e) {
				FileLog.e(TAG, e.toString());
				e.printStackTrace();
			} catch (IOException e) {
				FileLog.e(TAG, e.toString());
			}
			FileOutputStream outStream = null;
			try {
				outStream = context.openFileOutput(Contant.UPDATE_SAVENAME,
						Context.MODE_WORLD_READABLE
								| Context.MODE_WORLD_WRITEABLE);
				int temp = 0;
				byte[] data = new byte[1024];

				int total = 0;
				while ((temp = input.read(data)) != -1) {
					outStream.write(data, 0, temp);
					total += temp;
					// 获取当前下载量
					pd.setProgress(total);
				}
				File file = new File("/data/data/com.eastelsoft.lbs/files/"
						+ Contant.UPDATE_SAVENAME);
				return file;
			} catch (FileNotFoundException e) {
				FileLog.e(TAG, e.toString());
			} catch (IOException e) {
				FileLog.e(TAG, e.toString());
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			} finally {
				try {
					outStream.flush();
					outStream.close();
				} catch (IOException e) {
					FileLog.e(TAG, e.toString());
				}

			}

			return null;
		}
	}
	
	public static String download(Context context, String path, String filename)
			throws Exception {
		// 如果相等的话表示当前的sdcard挂载在手机上并且是可用的
		if (Environment.getExternalStorageState().equals(
				Environment.MEDIA_MOUNTED)) {
			URL url = new URL(path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setConnectTimeout(10000);
			// 获取到文件的大小
			InputStream is = conn.getInputStream();
			File sdCardDir = Environment.getExternalStorageDirectory();
			File wallpaperDirectory = new File(sdCardDir.getCanonicalPath()
					+ Contant.SDCARD_ANPENDIX_PATH_NOLINE);
			wallpaperDirectory.mkdirs();
			File file = new File(sdCardDir.getCanonicalPath()
					+ Contant.SDCARD_ANPENDIX_PATH + filename);
			if (file.exists()) {
				file.delete();
			}
			FileOutputStream fos = new FileOutputStream(file);
			BufferedInputStream bis = new BufferedInputStream(is);
			byte[] buffer = new byte[1024];
			int len;
			while ((len = bis.read(buffer)) != -1) {
				fos.write(buffer, 0, len);
			}
			fos.close();
			bis.close();
			is.close();
			return sdCardDir.getCanonicalPath() + Contant.SDCARD_ANPENDIX_PATH
					+ filename;
		} else {
			
			return null;
		}
	}

}
