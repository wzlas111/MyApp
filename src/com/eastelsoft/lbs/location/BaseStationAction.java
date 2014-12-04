/**
 * Copyright (c) 2012-8-4 www.eastelsoft.com
 * $ID NetworkAction.java 下午10:13:19 $
 */
package com.eastelsoft.lbs.location;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.telephony.NeighboringCellInfo;
import android.telephony.TelephonyManager;
import android.telephony.cdma.CdmaCellLocation;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;

import com.eastelsoft.util.CallBack;
import com.eastelsoft.util.FileLog;

/**
 * 基站定位
 * 
 * @author lengcj
 */
public class BaseStationAction {
	 
	private final static String TAG = "BaseStationAction";
	
	private Context context;
	
	public BaseStationAction(Context context) {
		this.context = context;
	}
	
	public BaseStationAction(Context context, CallBack callBack) {
		this.context = context;
	}
	
	private CdmaCellLocation location = null;
	
	public class CellIDInfo {
		public int cellId;
		public String mobileCountryCode;
		public String mobileNetworkCode;
		public int locationAreaCode;
		public String radioType;
		public CellIDInfo(){}
	}
	
    /** 基站信息结构体 */
    public class SCell{
    	public int MCC;
    	public int MNC;
    	public int LAC;
    	public int CID;
    }
    
    /** 经纬度信息结构体 */
    public class SItude{
    	public String latitude;
    	public String longitude;
    	public String accuracy;
    }
	
	public Location location() {
		TelephonyManager tm = (TelephonyManager)context.getSystemService(Context.TELEPHONY_SERVICE);
		int type = tm.getNetworkType();
		Location loc = null;
		ArrayList<CellIDInfo> CellID = new ArrayList<CellIDInfo>();
		FileLog.i(TAG, "TelephonyManager.getNetworkType==" + type);
		if (type == TelephonyManager.NETWORK_TYPE_EVDO_A 
				|| type == TelephonyManager.NETWORK_TYPE_CDMA 
				|| type ==TelephonyManager.NETWORK_TYPE_1xRTT) {
			location = (CdmaCellLocation) tm.getCellLocation();
			int cellIDs = location.getBaseStationId();
			int networkID = location.getNetworkId();
			StringBuilder nsb = new StringBuilder();
			nsb.append(location.getSystemId());
            CellIDInfo info = new CellIDInfo();
            info.cellId = cellIDs;
            info.locationAreaCode = networkID;
            info.mobileNetworkCode = nsb.toString();
            info.mobileCountryCode = tm.getNetworkOperator().substring(0, 3);
            info.radioType = "cdma";
            CellID.add(info);
		} else if(type == TelephonyManager.NETWORK_TYPE_EDGE) {
			GsmCellLocation location = (GsmCellLocation)tm.getCellLocation();  
			int cellIDs = location.getCid();  
			int lac = location.getLac(); 
			CellIDInfo info = new CellIDInfo();
            info.cellId = cellIDs;
            info.locationAreaCode = lac;
            info.mobileNetworkCode = tm.getNetworkOperator().substring(3, 5);   
            info.mobileCountryCode = tm.getNetworkOperator().substring(0, 3);
            info.radioType = "gsm";
            CellID.add(info);
		} else if(type == TelephonyManager.NETWORK_TYPE_GPRS 
				|| type == TelephonyManager.NETWORK_TYPE_HSDPA) {
			GsmCellLocation location = (GsmCellLocation)tm.getCellLocation();  
			int cellIDs = location.getCid();  
			int lac = location.getLac(); 
			CellIDInfo info = new CellIDInfo();
            info.cellId = cellIDs;
            info.locationAreaCode = lac;
            info.radioType = "gsm";
            CellID.add(info);
		} else {
			CellID = null;
		}
		if(CellID != null)
			loc = callGear(CellID);
		return loc;
		
		/** 根据经纬度获取位置信息
		if(loc != null)
		{
			try {
				
				StringBuilder sb = new StringBuilder();
				String pos = getLocation(loc);
				sb.append("CellID:");
				sb.append(CellID.get(0).cellId);
				sb.append("+\n");
				
				sb.append("home_mobile_country_code:");
				sb.append(CellID.get(0).mobileCountryCode);
				sb.append("++\n");
				
				sb.append("mobileNetworkCode:");
				sb.append(CellID.get(0).mobileNetworkCode);
				sb.append("++\n");
				
				sb.append("locationAreaCode:");
				sb.append(CellID.get(0).locationAreaCode);
				sb.append("++\n");
				sb.append(pos);
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}*/
	}
	
	private Location callGear(ArrayList<CellIDInfo> cellID) {
    	if (cellID == null) return null;
    	DefaultHttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(
				"http://www.google.com/loc/json");
		JSONObject holder = new JSONObject();
		try {
			holder.put("version", "1.1.0");
			holder.put("host", "maps.google.com");
			holder.put("home_mobile_country_code", cellID.get(0).mobileCountryCode);
			holder.put("home_mobile_network_code", cellID.get(0).mobileNetworkCode);
			holder.put("radio_type", cellID.get(0).radioType);
			holder.put("request_address", true);
			if ("460".equals(cellID.get(0).mobileCountryCode)) 
				holder.put("address_language", "zh_CN");
			else
				holder.put("address_language", "en_US");
			JSONObject data,current_data;
			JSONArray array = new JSONArray();
			current_data = new JSONObject();
			current_data.put("cell_id", cellID.get(0).cellId);
			current_data.put("location_area_code", cellID.get(0).locationAreaCode);
			current_data.put("mobile_country_code", cellID.get(0).mobileCountryCode);
			current_data.put("mobile_network_code", cellID.get(0).mobileNetworkCode);
			current_data.put("age", 0);
			array.put(current_data);
			if (cellID.size() > 2) {
				for (int i = 1; i < cellID.size(); i++) {
					data = new JSONObject();
					data.put("cell_id", cellID.get(i).cellId);
					data.put("location_area_code", cellID.get(i).locationAreaCode);
					data.put("mobile_country_code", cellID.get(i).mobileCountryCode);
					data.put("mobile_network_code", cellID.get(i).mobileNetworkCode);
					data.put("age", 0);
					array.put(data);
				}
			}
			holder.put("cell_towers", array);
			StringEntity se = new StringEntity(holder.toString());
			Log.e("Location send", holder.toString());
			post.setEntity(se);
			HttpResponse resp = client.execute(post);
			HttpEntity entity = resp.getEntity();

			BufferedReader br = new BufferedReader(
					new InputStreamReader(entity.getContent()));
			StringBuffer sb = new StringBuffer();
			String result = br.readLine();
			while (result != null) {
				Log.e("Locaiton receive", result);
				sb.append(result);
				result = br.readLine();
			}
			if(sb.length() <= 1)
				return null;
			data = new JSONObject(sb.toString());
			data = (JSONObject) data.get("location");

			Location loc = new Location(LocationManager.NETWORK_PROVIDER);
			loc.setLatitude((Double) data.get("latitude"));
			loc.setLongitude((Double) data.get("longitude"));
			loc.setAccuracy(Float.parseFloat(data.get("accuracy").toString()));
			loc.setTime(GetUTCTime());
			return loc;
		} catch (JSONException e) {
			return null;
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	private String getLocation(Location itude) throws Exception {
		String resultString = "";

		String urlString = String.format("http://maps.google.cn/maps/geo?key=abcdefg&q=%s,%s", itude.getLatitude(), itude.getLongitude());
		Log.i("URL", urlString);

		HttpClient client = new DefaultHttpClient();
		HttpGet get = new HttpGet(urlString);
		try {
			HttpResponse response = client.execute(get);
			HttpEntity entity = response.getEntity();
			BufferedReader buffReader = new BufferedReader(new InputStreamReader(entity.getContent()));
			StringBuffer strBuff = new StringBuffer();
			String result = null;
			while ((result = buffReader.readLine()) != null) {
				strBuff.append(result);
			}
			resultString = strBuff.toString();

			if (resultString != null && resultString.length() > 0) {
				JSONObject jsonobject = new JSONObject(resultString);
				JSONArray jsonArray = new JSONArray(jsonobject.get("Placemark").toString());
				resultString = "";
				for (int i = 0; i < jsonArray.length(); i++) {
					resultString = jsonArray.getJSONObject(i).getString("address");
				}
			}
		} catch (Exception e) {
			throw new Exception("" + e.getMessage());
		} finally {
			get.abort();
			client = null;
		}

		return resultString;
	}
	*/
	
	public long GetUTCTime() { 
        Calendar cal = Calendar.getInstance(Locale.CHINA); 
        int zoneOffset = cal.get(java.util.Calendar.ZONE_OFFSET); 
        int dstOffset = cal.get(java.util.Calendar.DST_OFFSET); 
        cal.add(java.util.Calendar.MILLISECOND, -(zoneOffset + dstOffset)); 
        return cal.getTimeInMillis();
    }
	
	public SCell location2() {
		try {
			/** 获取基站数据 */
			//SCell cell = getCellInfo();

			/** 根据基站数据获取经纬度 */
			//SItude itude = getItude(cell);
			//SItude itude = getItude(getCellInfos());
			//return itude;
			/** 获取地理位置 */
			//String location = getLocation(itude);
			//getCellInfos();
			return getCellInfo();

		} catch (Exception e) {
		}
		return null;
	}
	
	public SItude location1() {
		try {
			/** 获取基站数据 */
			//SCell cell = getCellInfo();

			/** 根据基站数据获取经纬度 */
			//SItude itude = getItude(cell);
			//SItude itude = getItude(getCellInfos());
			//return itude;
			/** 获取地理位置 */
			//String location = getLocation(itude);
			getCellInfos();

		} catch (Exception e) {
		}
		return null;
	}
	
	public SItude location1(String lon, String lat) {
		try {
			/** 根据基站数据获取经纬度 */
			SItude itude = new SItude();
			itude.longitude = lon;
			itude.latitude = lat;
			return itude;
		} catch (Exception e) {
		}
		return null;
	}
	
	/**
	 * 获取基站信息
	 * 
	 * @throws Exception
	 */
	private SCell getCellInfo() throws Exception {
		SCell cell = new SCell();

		/** 调用API获取基站信息 */
		TelephonyManager mTelNet = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		GsmCellLocation location = (GsmCellLocation) mTelNet.getCellLocation();
		if (location == null)
			throw new Exception("获取基站信息失败");

		String operator = mTelNet.getNetworkOperator();
		int mcc = Integer.parseInt(operator.substring(0, 3));
		int mnc = Integer.parseInt(operator.substring(3));
		int cid = location.getCid();
		int lac = location.getLac();

		/** 将获得的数据放到结构体中 */
		cell.MCC = mcc;
		cell.MNC = mnc;
		cell.LAC = lac;
		cell.CID = cid;
		FileLog.i(TAG, "cell.MCC================>" + cell.MCC);
		FileLog.i(TAG, "cell.MNC================>" + cell.MNC);
		FileLog.i(TAG, "cell.LAC================>" + cell.LAC);
		FileLog.i(TAG, "cell.CID================>" + cell.CID);
		return cell;
	}
	
	private ArrayList<SCell> getCellInfos() throws Exception {
		ArrayList<SCell> scells = new ArrayList<SCell>();
		SCell cell = new SCell();

		/** 调用API获取基站信息 */
		TelephonyManager mTelNet = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		GsmCellLocation location = (GsmCellLocation) mTelNet.getCellLocation();
		if (location == null)
			throw new Exception("获取基站信息失败");

		String operator = mTelNet.getNetworkOperator();
		int mcc = Integer.parseInt(operator.substring(0, 3));
		int mnc = Integer.parseInt(operator.substring(3));
		int cid = location.getCid();
		int lac = location.getLac();

		/** 将获得的数据放到结构体中 */
		cell.MCC = mcc;
		cell.MNC = mnc;
		cell.LAC = lac;
		cell.CID = cid;
		scells.add(cell);
		
		FileLog.i(TAG, "cell.MCC================>" + cell.MCC);
		FileLog.i(TAG, "cell.MNC================>" + cell.MNC);
		FileLog.i(TAG, "cell.LAC================>" + cell.LAC);
		FileLog.i(TAG, "cell.CID================>" + cell.CID);
		
		// 获得相邻的基站信息
		List<NeighboringCellInfo> list = mTelNet.getNeighboringCellInfo(); 
		int size = list.size();  
        for (int i = 0; i < size; i++) {  

        	SCell cell1 = new SCell();
        	cell1.CID = list.get(i).getCid();  
            cell1.MCC = mcc;  
            cell1.MNC = mnc;  
            cell1.LAC = lac;  
            FileLog.i(TAG, "NeighboringCellInfo================");
            FileLog.i(TAG, "cell1.MCC================>" + cell1.MCC);
    		FileLog.i(TAG, "cell1.MNC================>" + cell1.MNC);
    		FileLog.i(TAG, "cell1.LAC================>" + cell1.LAC);
    		FileLog.i(TAG, "cell1.CID================>" + cell1.CID);
            scells.add(cell1);
        }  

		return scells;
	}
    
	/**
	 * 获取经纬度
	 * 
	 * @throws Exception
	 */
	private SItude getItude(SCell cell) throws Exception {
		SItude itude = new SItude();

		/** 采用Android默认的HttpClient */
		HttpClient client = new DefaultHttpClient();
		/** 采用POST方法 */
		HttpPost post = new HttpPost("http://www.google.com/loc/json");
		try {
			/** 构造POST的JSON数据 */
			JSONObject holder = new JSONObject();
			holder.put("version", "1.1.0");
			holder.put("host", "maps.google.com");
			holder.put("address_language", "zh_CN");
			holder.put("request_address", true);
			holder.put("radio_type", "gsm");
			holder.put("carrier", "HTC");

			JSONObject tower = new JSONObject();
			tower.put("mobile_country_code", cell.MCC);
			tower.put("mobile_network_code", cell.MNC);
			tower.put("cell_id", cell.CID);
			tower.put("location_area_code", cell.LAC);

			JSONArray towerarray = new JSONArray();
			towerarray.put(tower);
			holder.put("cell_towers", towerarray);

			StringEntity query = new StringEntity(holder.toString());
			post.setEntity(query);

			/** 发出POST数据并获取返回数据 */
			HttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();
			BufferedReader buffReader = new BufferedReader(new InputStreamReader(entity.getContent()));
			StringBuffer strBuff = new StringBuffer();
			String result = null;
			while ((result = buffReader.readLine()) != null) {
				strBuff.append(result);
			}

			/** 解析返回的JSON数据获得经纬度 */
			JSONObject json = new JSONObject(strBuff.toString());
			JSONObject subjosn = new JSONObject(json.getString("location"));

			itude.latitude = subjosn.getString("latitude");
			itude.longitude = subjosn.getString("longitude");
			itude.accuracy = subjosn.getString("accuracy");
			Log.i("Itude", itude.latitude + itude.longitude);
			
		} catch (Exception e) {
			Log.e(e.getMessage(), e.toString());
			throw new Exception("获取经纬度出现错误:"+e.getMessage());
		} finally{
			post.abort();
			client = null;
		}
		
    	return itude;
    }
	
	class ConnectThread implements Runnable {
		private ArrayList<SCell> cell;
		public ConnectThread(ArrayList<SCell> cell){
			this.cell = cell;
		}
		@Override
		public void run() {
			SItude itude = new SItude();

			/** 采用Android默认的HttpClient */
			HttpClient client = new DefaultHttpClient();
			/** 采用POST方法 */
			HttpPost post = new HttpPost("http://www.google.com/loc/json");
			try {
				/** 构造POST的JSON数据 */
				JSONObject holder = new JSONObject();
				holder.put("version", "1.1.0");
				holder.put("host", "maps.google.com");
				holder.put("address_language", "zh_CN");
				holder.put("request_address", true);
				holder.put("radio_type", "gsm");
				holder.put("carrier", "HTC");

				JSONObject tower = new JSONObject();
				tower.put("mobile_country_code", cell.get(0).MCC);
				tower.put("mobile_network_code", cell.get(0).MNC);
				tower.put("cell_id", cell.get(0).CID);
				tower.put("location_area_code", cell.get(0).LAC);

				JSONArray towerarray = new JSONArray();
				towerarray.put(tower);
				FileLog.i(TAG, "cell==>" + cell.size());
				if(cell.size() > 2) {
					for(int i=1;i< cell.size();i++) {
						JSONObject tower1 = new JSONObject();
						tower1.put("mobile_country_code", cell.get(0).MCC);
						tower1.put("mobile_network_code", cell.get(0).MNC);
						tower1.put("cell_id", cell.get(0).CID);
						tower1.put("location_area_code", cell.get(0).LAC);
						towerarray.put(tower1);
					}
				}
				
				holder.put("cell_towers", towerarray);

				StringEntity query = new StringEntity(holder.toString());
				post.setEntity(query);

				/** 发出POST数据并获取返回数据 */
				HttpResponse response = client.execute(post);
				HttpEntity entity = response.getEntity();
				BufferedReader buffReader = new BufferedReader(new InputStreamReader(entity.getContent()));
				StringBuffer strBuff = new StringBuffer();
				String result = null;
				while ((result = buffReader.readLine()) != null) {
					strBuff.append(result);
				}

				/** 解析返回的JSON数据获得经纬度 */
				JSONObject json = new JSONObject(strBuff.toString());
				JSONObject subjosn = new JSONObject(json.getString("location"));

				itude.latitude = subjosn.getString("latitude");
				itude.longitude = subjosn.getString("longitude");
				itude.accuracy = subjosn.getString("accuracy");
				Log.i("Itude", itude.latitude + itude.longitude);
				
			} catch (Exception e) {
				Log.e(e.getMessage(), e.toString());
			} finally{
				post.abort();
				client = null;
			}
		}
	};
	
	/**
	 * 获取经纬度
	 * 
	 * @throws Exception
	 */
	private SItude getItude(ArrayList<SCell> cell) throws Exception {
		
		
		SItude itude = new SItude();

		/** 采用Android默认的HttpClient */
		HttpClient client = new DefaultHttpClient();
		/** 采用POST方法 */
		HttpPost post = new HttpPost("http://www.google.com/loc/json");
		try {
			/** 构造POST的JSON数据 */
			JSONObject holder = new JSONObject();
			holder.put("version", "1.1.0");
			holder.put("host", "maps.google.com");
			holder.put("address_language", "zh_CN");
			holder.put("request_address", true);
			holder.put("radio_type", "gsm");
			holder.put("carrier", "HTC");

			JSONObject tower = new JSONObject();
			tower.put("mobile_country_code", cell.get(0).MCC);
			tower.put("mobile_network_code", cell.get(0).MNC);
			tower.put("cell_id", cell.get(0).CID);
			tower.put("location_area_code", cell.get(0).LAC);

			JSONArray towerarray = new JSONArray();
			towerarray.put(tower);
			FileLog.i(TAG, "cell==>" + cell.size());
			if(cell.size() > 2) {
				for(int i=1;i< cell.size();i++) {
					JSONObject tower1 = new JSONObject();
					tower1.put("mobile_country_code", cell.get(0).MCC);
					tower1.put("mobile_network_code", cell.get(0).MNC);
					tower1.put("cell_id", cell.get(0).CID);
					tower1.put("location_area_code", cell.get(0).LAC);
					towerarray.put(tower1);
				}
			}
			
			holder.put("cell_towers", towerarray);

			StringEntity query = new StringEntity(holder.toString());
			post.setEntity(query);

			/** 发出POST数据并获取返回数据 */
			HttpResponse response = client.execute(post);
			HttpEntity entity = response.getEntity();
			BufferedReader buffReader = new BufferedReader(new InputStreamReader(entity.getContent()));
			StringBuffer strBuff = new StringBuffer();
			String result = null;
			while ((result = buffReader.readLine()) != null) {
				strBuff.append(result);
			}

			/** 解析返回的JSON数据获得经纬度 */
			JSONObject json = new JSONObject(strBuff.toString());
			JSONObject subjosn = new JSONObject(json.getString("location"));

			itude.latitude = subjosn.getString("latitude");
			itude.longitude = subjosn.getString("longitude");
			itude.accuracy = subjosn.getString("accuracy");
			Log.i("Itude", itude.latitude + itude.longitude);
			
		} catch (Exception e) {
			Log.e(TAG, e.toString());
			//throw new Exception("获取经纬度出现错误:"+e.getMessage());
		} finally{
			post.abort();
			client = null;
		}
		
    	return itude;
    }
    
	/**
	 * 获取地理位置
	 * 
	 * @throws Exception
	 */
	public String getLocation(SItude itude) throws Exception {
		String resultString = "";

		/** 这里采用get方法，直接将参数加到URL上 */
		String urlString = String.format("http://maps.google.cn/maps/geo?key=abcdefg&q=%s,%s", itude.latitude, itude.longitude);
		Log.i("URL", urlString);

		/** 新建HttpClient */
		HttpClient client = new DefaultHttpClient();
		/** 采用GET方法 */
		HttpGet get = new HttpGet(urlString);
		try {
			/** 发起GET请求并获得返回数据 */
			HttpResponse response = client.execute(get);
			HttpEntity entity = response.getEntity();
			BufferedReader buffReader = new BufferedReader(new InputStreamReader(entity.getContent()));
			StringBuffer strBuff = new StringBuffer();
			String result = null;
			while ((result = buffReader.readLine()) != null) {
				strBuff.append(result);
			}
			resultString = strBuff.toString();

			/** 解析JSON数据，获得物理地址 */
			if (resultString != null && resultString.length() > 0) {
				JSONObject jsonobject = new JSONObject(resultString);
				JSONArray jsonArray = new JSONArray(jsonobject.get("Placemark").toString());
				resultString = "";
				for (int i = 0; i < jsonArray.length(); i++) {
					resultString = jsonArray.getJSONObject(i).getString("address");
				}
			}
		} catch (Exception e) {
			throw new Exception("获取物理位置出现错误:" + e.getMessage());
		} finally {
			get.abort();
			client = null;
		}

		return resultString;
	}
    
//	/** 显示结果 */
//	private void showResult(SCell cell, String location) {
//		TextView cellText = (TextView) findViewById(R.id.cellText);
//		cellText.setText(String.format("基站信息：mcc:%d, mnc:%d, lac:%d, cid:%d",
//				cell.MCC, cell.MNC, cell.LAC, cell.CID));
//
//		TextView locationText = (TextView) findViewById(R.id.lacationText);
//		locationText.setText("物理位置：" + location);
//	}
}
