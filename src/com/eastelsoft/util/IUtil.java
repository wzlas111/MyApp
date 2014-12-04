/**
 * Copyright (c) 2012-7-21 www.eastelsoft.com
 * $ID IUtil.java 上午12:42:13 $
 */
package com.eastelsoft.util;

import java.util.LinkedList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager.NameNotFoundException;

import com.eastelsoft.lbs.entity.AuthCentreResp;
import com.eastelsoft.lbs.entity.Login1Resp;
import com.eastelsoft.lbs.entity.Login2Resp;
import com.eastelsoft.lbs.entity.RegResp;
import com.eastelsoft.lbs.entity.ReportResp;
import com.eastelsoft.lbs.entity.SetInfo;

/**
 * 业务相关工具类
 * @author lengcj
 */
public class IUtil {
	private static final String TAG = "IUtil";

	/**
	 * 获取版本号码
	 *@param context
	 *@return
	 */
	public static int getVerCode(Context context) {
		int verCode = -1;
		try {
			verCode = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
		}
		return verCode;
	}
	
	/**
	 * 获取版本名
	 *@param context
	 *@return
	 */
	public static String getVerName(Context context) {
		String verName = "";
		try {
			verName = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
		}
		return verName;
	}
	
	/**
	 * 保存SharedPreference
	 *@param sp
	 *@param key
	 *@param value
	 */
	public static void writeSharedPreference(SharedPreferences sp, 
			String key, String value) {
		try {
			Editor editor = sp.edit();
			editor.putString(key, value);
			editor.commit();
		} catch (Exception e) {
		}
	}
	
	/**
	 * 保存配置数据信息
	 *@param sp
	 *@param regResp
	 *@param login1Resp
	 *@param login2Resp
	 *@param serialNumber
	 *@param imei
	 *@param imsi
	 */
	public static void writeSharedPreferences(SharedPreferences sp, RegResp regResp, 
			Login1Resp login1Resp, Login2Resp login2Resp, String serialNumber, 
			String imei, String imsi) {
		// 二次登录成功，写注册文件
		try {
			String timePeriod = Contant.TIMEPERIOD;
			String interval = Contant.INTERVAL;
			String week = Contant.WEEK;
			String filterDate = Contant.FILTERDATE;
			String minDistance = Contant.MINDISTANCE;
			if("2".equals(login2Resp.getRet())) {
				if(login2Resp.getTimePeriod() != null && !"".equals(login2Resp.getTimePeriod()))
					timePeriod = login2Resp.getTimePeriod();
				if(login2Resp.getInterval() > -1)
					interval = String.valueOf(login2Resp.getInterval());
				if(login2Resp.getWeek() != null && !"".equals(login2Resp.getWeek()))
					week = login2Resp.getWeek();
				if(login2Resp.getFilterDate() != null && !"".equals(login2Resp.getFilterDate())){
					filterDate = login2Resp.getFilterDate();
				}else{
					filterDate="无";	
				}
				
				
			}

			
			Editor editor = sp.edit();
			editor.putString("adapter_ip", login1Resp.getAdapter_ip());
			editor.putString("adapter_port", String.valueOf(login1Resp.getAdapter_port()));
			//增加UDP端口
			editor.putString("udp_adapter_port", String.valueOf(login1Resp.getUdp_adapter_port()));
			editor.putString("auth_code", login1Resp.getAuth_code());
			editor.putString("gps_time", String.valueOf(login1Resp.getGps_time()));
			editor.putString("serialNumber", serialNumber);
			editor.putString("device_id", regResp.getDevice_id());
			editor.putString("timePeriod", timePeriod);
			editor.putString("interval", interval);
			editor.putString("week", week);
			editor.putString("filterDate", filterDate);
			editor.putString("minDistance", minDistance);
			editor.putString("imei", imei);
			editor.putString("imsi", imsi);
			editor.commit();
		} catch (Exception e) {
			FileLog.e(TAG, "writeSharedPreferences error 1===>" + e.toString());
		}
	}
	
	
	/**
	 * 保存配置数据信息
	 *@param sp
	 *@param login1Resp
	 */
	public static void writeSharedPreferences(SharedPreferences sp,  
			AuthCentreResp authCentreResp) {
		FileLog.i(TAG, "writeSharedPreferences--authCentreResp");
		// 一次登录成功，写注册文件
		try {
			Editor editor = sp.edit();
			editor.putString("gateip", authCentreResp.getGateip());
			editor.putString("gatetcpport", authCentreResp.getGatetcpport());
			
			editor.putString("gateudpport", authCentreResp.getGateudpport());
			editor.putString("httpip", "http://"+authCentreResp.getHttpip()+":"+authCentreResp.getHttpport()+"/");
			editor.putString("pfsign", authCentreResp.getPfsign());
			
			editor.commit();
		} catch (Exception e) {
			FileLog.e(TAG, "writeSharedPreferences error 2===>" + e.toString());
		}
	}
	
	/**
	 * 保存配置数据信息
	 *@param sp
	 *@param login1Resp
	 */
	public static void writeSharedPreferences(SharedPreferences sp,  
			Login1Resp login1Resp) {
		FileLog.i(TAG, "writeSharedPreferences--login1Resp");
		// 一次登录成功，写注册文件
		try {
			Editor editor = sp.edit();
			editor.putString("adapter_ip", login1Resp.getAdapter_ip());
			
			editor.putString("adapter_port", String.valueOf(login1Resp.getAdapter_port()));
			editor.putString("udp_adapter_port", String.valueOf(login1Resp.getUdp_adapter_port()));
			editor.putString("auth_code", login1Resp.getAuth_code());
			editor.commit();
		} catch (Exception e) {
			FileLog.e(TAG, "writeSharedPreferences error 2===>" + e.toString());
		}
	}
	/**
	 * 保存配置数据信息
	 *@param sp
	 *@param login1Resp
	 */
	public static void writeSharedPreferencesUdp(SharedPreferences sp,  
			Login1Resp login1Resp) {
		// 一次登录成功，写注册文件
		FileLog.i(TAG, "writeSharedPreferences--login1Resp");
		try {
			Editor editor = sp.edit();
			editor.putString("adapter_ip", login1Resp.getAdapter_ip());
			editor.putString("udp_adapter_port", String.valueOf(login1Resp.getUdp_adapter_port()));
			editor.putString("auth_code", login1Resp.getAuth_code());
			editor.commit();
		} catch (Exception e) {
			FileLog.e(TAG, "writeSharedPreferences error 2===>" + e.toString());
		}
	}
	
	/**
	 * 保存配置数据信息
	 *@param sp
	 *@param login2Resp
	 */
	public static void writeSharedPreferences(SharedPreferences sp,  
			Login2Resp login2Resp) {
		// 二次登录成功，写注册文件
		FileLog.i(TAG, "writeSharedPreferences--login2Resp");
		try {
			Editor editor = sp.edit();
			if(login2Resp.getTimePeriod() != null)
				editor.putString("timePeriod", login2Resp.getTimePeriod());
			editor.putString("interval",
					String.valueOf(login2Resp.getInterval()));
			if(login2Resp.getWeek() != null)
				editor.putString("week", login2Resp.getWeek());
			if(login2Resp.getFilterDate() != null){
				editor.putString("filterDate", login2Resp.getFilterDate());
			}else{
				editor.putString("filterDate", "无");
				
			}
			
			editor.commit();
		} catch (Exception e) {
			FileLog.e(TAG, "writeSharedPreferences error 3===>" + e.toString());
		}
	}
	
	public static void writeSharedPreferences(SharedPreferences sp,  
			ReportResp reportResp) {
		// 二次登录成功，写注册文件
		FileLog.i(TAG, "writeSharedPreferences--reportResp");
		try {
			Editor editor = sp.edit();
			if(reportResp.getTimePeriod() != null)
				editor.putString("timePeriod", reportResp.getTimePeriod());
			editor.putString("interval",
					String.valueOf(reportResp.getInterval()));
			if(reportResp.getWeek() != null)
				editor.putString("week", reportResp.getWeek());
			if(reportResp.getFilterDate() != null){
				editor.putString("filterDate", reportResp.getFilterDate());
			}else{
				editor.putString("filterDate", "无");
				
			}
			editor.commit();
		} catch (Exception e) {
			FileLog.e(TAG, "writeSharedPreferences error 4===>" + e.toString());
		}
	}
	
	
	/**
	 * 
	 *@param str
	 *@return
	 */
	public static String changeWeek(String str) {
		String ret = "";
		if(str!=null&&!"".equals(str)){
			str.length();
			String one = str.substring(0, str.length()-1);
			String two=str.substring(str.length()-1, str.length());
			ret = two+one;
			
			
		}
		
		return ret.trim();
	}
	
	/**
	 * 解析周数据 0表示无效1表示有效
	 *@param str
	 *@return
	 */
	public static String parseWeek(String str) {
		String ret = "";
		for(int i = 0; i < str.length(); i++) {
			String s = str.substring(i, i + 1);
			String r = "";
			if("1".equals(s)) r = tranWeek(i);
			ret += " " + r;
		}
		return ret.trim();
	}
	
	/**
	 * 周转换
	 *@param i
	 *@return
	 */
	public static String tranWeek(int i) {
		String str = "";
		switch(i){
		case 0:str="周日";break;
		case 1:str="周一";break;
		case 2:str="周二";break;
		case 3:str="周三";break;
		case 4:str="周四";break;
		case 5:str="周五";break;
		case 6:str="周六";break;
		default:break;
		}
		return str;
	}
	
	/**
	 * 判断当前是否需要执行数据上报
	 *@param timePeriod
	 *@param week
	 *@param filterDate
	 *@return
	 *@throws Exception
	 */
	public static boolean isNeedSendReport(String timePeriod, String week, 
			String filterDate) throws Exception {
		boolean need = false;
		String currentDate = Util.getLocaleTime("yyyyMMdd");
		// 不定位日期
		if(filterDate.indexOf(currentDate) > 0) 
			return false;
		
		// 周
		int currentWeek = Util.getLocaleWeek();
		if("0".equals(week.substring(currentWeek - 1, currentWeek))) {
			return false;
		}
		
		// 时间段
		String currentTime = Util.getLocaleTime("HH:mm");
		String[] arr = timePeriod.split(",");
		for(int i = 0; i < arr.length; i++) {
			String tmpArr[] = arr[i].split("-");
			if(tmpArr.length != 2) {
				// 错误的数据
				need = false;
				break;
			} else {
				if(tmpArr[0].length()==4) {
					tmpArr[0] = "0" + tmpArr[0];
				}
				if(tmpArr[1].length()==4) {
					tmpArr[1] = "0" + tmpArr[0];
				}
				if (currentTime.compareTo(tmpArr[0])  >= 0 && currentTime.compareTo(tmpArr[1]) <= 0) {
				   need = true;
				   break;
				}
			}
		}
		return need;
	}
	
	/**
	 * 获取序列号，自增量，最大60000，超过循环
	 *@param globalVar
	 *@return
	 */
	public synchronized static short getMsgReq(GlobalVar globalVar) {
		int tmpSeq = globalVar.getMsg_seq();
		if(tmpSeq > 60000) tmpSeq =1;
		short msg_seq = (short)(tmpSeq);
		globalVar.setMsg_seq(tmpSeq + 1);
		return msg_seq;
	}
	
	public synchronized static void setMsgReq(GlobalVar globalVar) {
		globalVar.setMsg_seq(1);
		
	}
	
	/**
	 * 增加发送请求
	 *@param globalVar
	 *@param data
	 */
	public synchronized static void addSendList(GlobalVar globalVar, byte[] data) {
		LinkedList<byte[]> list = globalVar.getSendList();
		list.add(data);
		globalVar.setSendList(list);
	}
	
	public static SetInfo initSetInfo(SharedPreferences sp) {
		SetInfo set = new SetInfo();
		String serialNumber = sp.getString("serialNumber", "");
		String imei = sp.getString("imei", "");
		String imsi = sp.getString("imsi", "");
		String adapter_ip = sp.getString("adapter_ip", "");
		int adapter_port = Integer.parseInt(sp.getString("adapter_port", 
				"10646"));
		//增加UDP端口
		int udp_adapter_port = Integer.parseInt(sp.getString("udp_adapter_port", 
				"10646"));
		long gps_time = Long.parseLong(sp.getString("gps_time", "50"));
		String auth_code = sp.getString("auth_code", "");
		String device_id = sp.getString("device_id", "");
		String timePeriod = sp.getString("timePeriod", "");
		int interval = Integer.parseInt(sp.getString("interval", "60"));
		String week = sp.getString("week", "0111110");
		String filterDate = sp.getString("filterDate", "");
		String planupdatecode = sp.getString("planupdatecode", "");
		String custupdatecode = sp.getString("custupdatecode", "");
		
		String goodsupdatecode = sp.getString("goodsupdatecode", "");
		String monthstargetupdatecode = sp.getString("monthstargetupdatecode", "");
		String shock_select = sp.getString("shock_select", "1");
		String msg_select = sp.getString("msg_select", "1");
		//统一认证中心
		String gateip = sp.getString("gateip", "");
		String gatetcpport = sp.getString("gatetcpport", "10646");
		String gateudpport = sp.getString("gateudpport", "10646");
		String httpip = sp.getString("httpip", "");
		String pfsign = sp.getString("pfsign", "");
		set.setGateip(gateip);
		set.setGatetcpport(Integer.parseInt(gatetcpport));
		set.setGateudpport(Integer.parseInt(gateudpport));
		set.setHttpip(httpip);
		set.setPfsign(pfsign);
		
		
		set.setSerialNumber(serialNumber);
		set.setImei(imei);
		set.setImsi(imsi);
		set.setAdapter_ip(adapter_ip);
		set.setAdapter_port(adapter_port);
		//增加UDP端口
		set.setUdp_adapter_port(udp_adapter_port);
		set.setAuth_code(auth_code);
		set.setDevice_id(device_id);
		set.setTimePeriod(timePeriod);
		set.setInterval(interval);
		set.setWeek(week);
		set.setFilterDate(filterDate);
		set.setPlanupdatecode(planupdatecode);
		set.setCustupdatecode(custupdatecode);
		set.setGoodsupdatecode(goodsupdatecode);
		set.setMonthstargetupdatecode(monthstargetupdatecode);
		set.setShock_select(shock_select);
		set.setMsg_select(msg_select);
		set.setGps_time(gps_time);
		return set;
	}
	
	public static String chkJsonStr(String jsonStr) {
		if(!jsonStr.startsWith("[")) {
			jsonStr = "[" + jsonStr;
		}
		if(!jsonStr.endsWith("]")) {
			jsonStr = jsonStr + "]";
		}
		return jsonStr;
	}
}
