/**
 * Copyright (c) 2012-8-14 www.eastelsoft.com
 * $ID DBUtil.java 下午5:08:00 $
 */
package com.eastelsoft.lbs.db;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.entity.BulletinBean;
import com.eastelsoft.lbs.entity.CustBean;
import com.eastelsoft.lbs.entity.CustProp;
import com.eastelsoft.lbs.entity.GoodsBean;
import com.eastelsoft.lbs.entity.GoodsMonthCustBean;
import com.eastelsoft.lbs.entity.GoodsMonthTargetBean;
import com.eastelsoft.lbs.entity.GoodsReportBean;
import com.eastelsoft.lbs.entity.InfoBean;
import com.eastelsoft.lbs.entity.KnowledgeBean;
import com.eastelsoft.lbs.entity.LocBean;
import com.eastelsoft.lbs.entity.SalesReportBean;
import com.eastelsoft.lbs.entity.SalestaskAllocationBean;
import com.eastelsoft.lbs.entity.TimingLocationBean;
import com.eastelsoft.lbs.entity.VisitBean;
import com.eastelsoft.util.FileLog;

/**
 * 数据库操作工具类
 * 
 * @author lengcj
 */
public class DBUtil {

	private static final String TAG = "DBUtil";

	/*
	 * +"tl_id NTEXT PRIMARY KEY," +"tl_uploadDate NTEXT," +"tl_lon NTEXT,"
	 * +"tl_lat NTEXT," +"tl_accuracy NTEXT)"
	 */
	public static void insertLTiminglocation(
			SQLiteDatabase paramSQLiteDatabase, String... parms) {
		ContentValues localContentValues = new ContentValues();
		localContentValues.put("tl_id", parms[0]);
		localContentValues.put("tl_uploadDate", parms[1]);
		localContentValues.put("tl_lon", parms[2]);
		localContentValues.put("tl_lat", parms[3]);
		localContentValues.put("tl_accuracy", parms[4]);
		localContentValues.put("tl_seq", parms[5]);
		localContentValues.put("tl_power", parms[6]);
		localContentValues.put("tl_states", parms[7]);
		localContentValues.put("tl_signalStrengthValue", parms[8]);
		localContentValues.put("tl_cell", parms[9]);
		localContentValues.put("tl_wifi", parms[10]);

		paramSQLiteDatabase
				.insert("l_timinglocation", null, localContentValues);
		FileLog.i(TAG, "insertLTiminglocation");
		paramSQLiteDatabase.close();
	}

	public static ArrayList<TimingLocationBean> getDataFromLLTiminglocation(
			SQLiteDatabase paramSQLiteDatabase) {
		String[] arrayOfString = new String[1];
		arrayOfString[0] = "tl_id,tl_uploadDate,tl_lon,tl_lat,tl_accuracy,tl_seq,tl_power,tl_states,tl_signalStrengthValue,tl_cell,tl_wifi";
		Cursor cursor = paramSQLiteDatabase.query("l_timinglocation",
				arrayOfString, null, null, null, null, "tl_uploadDate DESC",
				null);
		ArrayList<TimingLocationBean> arrayList = new ArrayList<TimingLocationBean>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			TimingLocationBean ib = new TimingLocationBean();
			ib.setTl_id(cursor.getString(0));
			ib.setTl_uploadDate(cursor.getString(1));
			ib.setTl_lon(cursor.getString(2));
			ib.setTl_lat(cursor.getString(3));
			ib.setTl_accuracy(cursor.getString(4));
			ib.setTl_seq(cursor.getString(5));
			ib.setTl_power(cursor.getString(6));
			ib.setTl_states(cursor.getString(7));
			ib.setTl_signalStrengthValue(cursor.getString(8));
			ib.setTl_cell(cursor.getString(9));
			ib.setTl_wifi(cursor.getString(10));
			arrayList.add(ib);
			cursor.moveToNext();
		}
		cursor.close();
		paramSQLiteDatabase.close();
		return arrayList;
	}

	public static void deleteLTiminglocation(
			SQLiteDatabase paramSQLiteDatabase, String paramString) {
		paramSQLiteDatabase.delete("l_timinglocation", "tl_id='" + paramString
				+ "'", null);
		paramSQLiteDatabase.close();
	}

	public static void deleteLInfo(SQLiteDatabase paramSQLiteDatabase,
			String paramString) {
		paramSQLiteDatabase.delete("l_info", "info_auto_id='" + paramString
				+ "'", null);
		paramSQLiteDatabase.close();
	}

	public static void insertLInfo(SQLiteDatabase paramSQLiteDatabase,
			String... parms) {
		ContentValues localContentValues = new ContentValues();
	
		localContentValues.put("uploadDate", parms[0]);
		localContentValues.put("title", parms[1]);
		localContentValues.put("imgFile", parms[2]);
		localContentValues.put("remark", parms[3]);
		localContentValues.put("lon", parms[4]);
		localContentValues.put("lat", parms[5]);
		localContentValues.put("info_auto_id", parms[6]);
		localContentValues.put("location", parms[7]);
		localContentValues.put("istijiao", parms[8]);
		localContentValues.put("setLongtime", parms[9]);
		paramSQLiteDatabase.insert("l_info", null, localContentValues);
		System.out.println("读入本地数据库成功。。。。。");
		paramSQLiteDatabase.close();
	}
	
	public static void insertAudioInfo(SQLiteDatabase paramSQLiteDatabase,
			String... parms) {
		ContentValues localContentValues = new ContentValues();
		localContentValues.put("id", parms[0]);
		localContentValues.put("gpsid", parms[1]);
		localContentValues.put("type", parms[2]);
		paramSQLiteDatabase.insert("audio_info", null, localContentValues);
		paramSQLiteDatabase.close();
	}

	
	
	public static void updateLInfo(SQLiteDatabase paramSQLiteDatabase,
			String parms) {
		ContentValues localContentValues = new ContentValues();
		localContentValues.put("istijiao", "11");
		paramSQLiteDatabase.update("l_info", localContentValues,
				"info_auto_id=?", new String[] {parms});
		paramSQLiteDatabase.close();
	}

	public static void updateAudioInfo(SQLiteDatabase paramSQLiteDatabase,
			String parms) {
		ContentValues localContentValues = new ContentValues();
		localContentValues.put("istijiao", "22");
		paramSQLiteDatabase.update("audio_info", localContentValues,
				"id=?", new String[] {parms});
		paramSQLiteDatabase.close();
	}

	public static void updateLInfoToFail(SQLiteDatabase paramSQLiteDatabase,
			String parms) {
		ContentValues localContentValues = new ContentValues();
		localContentValues.put("istijiao", "00");
		paramSQLiteDatabase.update("l_info", localContentValues,
				"info_auto_id=?", new String[] {parms});
		paramSQLiteDatabase.close();
	}

	public static List<HashMap<String, Object>> getDataFromLInfo(
			SQLiteDatabase paramSQLiteDatabase) {
		String[] arrayOfString = new String[1];
		arrayOfString[0] = "info_auto_id,uploadDate,title,remark,lon,lat";
		Cursor cursor = paramSQLiteDatabase.query("l_info", arrayOfString,
				null, null, null, null, "uploadDate DESC", null);
		List<HashMap<String, Object>> mData = new ArrayList<HashMap<String, Object>>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			String info_auto_id = cursor.getString(0);
			String uploadDate = cursor.getString(1);
			String title = cursor.getString(2);
			// String remark = result.getString(3);
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("info_auto_id", info_auto_id);
			map.put("info_title", title);
			map.put("info_uploadDate", uploadDate);
			map.put("info_imgFile", R.drawable.line_bg);
			mData.add(map);
			cursor.moveToNext();
		}
		cursor.close();
		paramSQLiteDatabase.close();
		return mData;
	}

	/*
	 * 获取所以上报信息
	 */
	public static ArrayList<InfoBean> getDataFromLInfoA(
			SQLiteDatabase paramSQLiteDatabase) {
		String[] arrayOfString = new String[1];
		arrayOfString[0] = "info_auto_id,uploadDate,title,remark,lon,lat,istijiao";
		Cursor cursor = paramSQLiteDatabase.query("l_info", arrayOfString,
				null, null, null, null, "uploadDate DESC", null);
		ArrayList<InfoBean> arrayList = new ArrayList<InfoBean>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			InfoBean ib = new InfoBean();
			ib.setInfo_auto_id(cursor.getString(0));
			ib.setUploadDate(cursor.getString(1));
			ib.setTitle(cursor.getString(2));
			ib.setIstijiao(cursor.getString(6));
			arrayList.add(ib);
			cursor.moveToNext();
		}
		cursor.close();
		paramSQLiteDatabase.close();
		return arrayList;
	}

	/**
	 * 获取未上报信息
	 * 
	 * @param paramSQLiteDatabase
	 * @return
	 */
	public static ArrayList<InfoBean> getDataFromLInfoB(
			SQLiteDatabase paramSQLiteDatabase) {
		String[] arrayOfString = new String[1];
		arrayOfString[0] = "info_auto_id,uploadDate,title,remark,lon,lat,istijiao";
		Cursor cursor = paramSQLiteDatabase.query("l_info", arrayOfString,
				"istijiao='00'", null, null, null, "uploadDate DESC", null);
		ArrayList<InfoBean> arrayList = new ArrayList<InfoBean>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()){
			InfoBean ib = new InfoBean();
			ib.setInfo_auto_id(cursor.getString(0));
			ib.setUploadDate(cursor.getString(1));
			ib.setTitle(cursor.getString(2));
			ib.setIstijiao(cursor.getString(6));
			arrayList.add(ib);
			cursor.moveToNext();
		}
		cursor.close();
		paramSQLiteDatabase.close();
		return arrayList;
	}

	/**
	 * 获取客户拜访未上报信息
	 * 
	 * @param paramSQLiteDatabase
	 * @return
	 */
//	public static ArrayList<VisitBean> getDataFromBVisitB(
//			SQLiteDatabase paramSQLiteDatabase) {
//		Log.i("BaiFangService","查询前1");
//		String[] arrayOfString = new String[1];
//		arrayOfString[0] = "id, clientid,clientname, data,title, remark, lon, lat, location,imgFile, istijiao";
//		Log.i("BaiFangService","查询前");
//		Cursor cursor = paramSQLiteDatabase.query("l_visit", arrayOfString,
//				"istijiao='11'", null, null, null, "data DESC", null);
//		Log.i("BaiFangService", "查询后");
//		ArrayList<VisitBean> arrayList = new ArrayList<VisitBean>();
//		cursor.moveToFirst();
//		while (!cursor.isAfterLast()){
//			VisitBean ib = new VisitBean();
//			ib.setId(cursor.getString(0));
//			ib.setClientid(cursor.getString(1));
//			ib.setClientName(cursor.getString(2));
//			ib.setData(cursor.getString(3));
//			ib.setTitle(cursor.getString(4));
//			ib.setRemark(cursor.getString(5));
//			ib.setLon(cursor.getString(6));
//			ib.setLat(cursor.getString(7));
//			ib.setLocation(cursor.getString(8));
////			ib.setImgFile(cursor.getString(9));
//			ib.setIstijiao(cursor.getString(10));
//			Log.i("BaiFangService",ib.toString());
//			arrayList.add(ib);
//			cursor.moveToNext();
//		}
//		cursor.close();
//		paramSQLiteDatabase.close();
//		return arrayList;
//	}
	
	
	// 查询上报信息的总数

	public static int getCount(SQLiteDatabase paramSQLiteDatabase) {

		SQLiteDatabase db = paramSQLiteDatabase;

		String sql = "select count(*) from '" + "l_info" + "'";

		Cursor c = db.rawQuery(sql, null);

		c.moveToFirst();

		int length = c.getInt(0);

		c.close();

		db.close();

		return length;

	}

	// 获得一页信息数据
	public static ArrayList<InfoBean> getAllItems(int firstResult,
			int maxResult, SQLiteDatabase paramSQLiteDatabase) {

		ArrayList<InfoBean> arrayList = new ArrayList<InfoBean>();

		SQLiteDatabase db = paramSQLiteDatabase;

		String sql = "select * from '" + "l_info" + "' limit ?,?";

		Cursor cursor = db.rawQuery(

		sql,

		new String[] { String.valueOf(firstResult),

		String.valueOf(maxResult) });

		if (cursor != null && cursor.getCount() > 0) {

			cursor.moveToFirst();

		}

		for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {

			InfoBean ib = new InfoBean();

			ib.setInfo_auto_id(cursor.getString(0));

			ib.setUploadDate(cursor.getString(1));

			ib.setTitle(cursor.getString(2));
			FileLog.i("sql", ib.getInfo_auto_id() + "+" + ib.getUploadDate()
					+ "+" + ib.getTitle());

			arrayList.add(ib);

		}

		cursor.close();

		db.close();

		return arrayList;

	}

	public static HashMap<String, Object> getDataFromLInfoByID(
			SQLiteDatabase paramSQLiteDatabase, String paramString) {
		String[] arrayOfString = new String[1];
		arrayOfString[0] = "info_auto_id,uploadDate,title,remark,lon,lat,location,imgFile,istijiao,setLongtime";
		Cursor localCursor = paramSQLiteDatabase.query("l_info", arrayOfString,
				"info_auto_id='" + paramString + "'", null, null, null, null,
				null);
		HashMap<String, Object> localMap = new HashMap<String, Object>();
		localCursor.moveToFirst();
		while (!localCursor.isAfterLast()) {
			String info_auto_id = localCursor.getString(0);
			String uploadDate = localCursor.getString(1);
			String title = localCursor.getString(2);
			String remark = localCursor.getString(3);
			String lon = localCursor.getString(4);
			String lat = localCursor.getString(5);
			String location = localCursor.getString(6);
			String imgFile = localCursor.getString(7);
			String istijiao = localCursor.getString(8);
			String setLongtime = localCursor.getString(9);
			localMap.put("info_auto_id", info_auto_id);
			localMap.put("info_title", title);
			localMap.put("info_uploadDate", uploadDate);
			localMap.put("info_remark", remark);
			localMap.put("info_lon", lon);
			localMap.put("info_lat", lat);
			localMap.put("info_location", location);
			localMap.put("imgFile", imgFile);
			localMap.put("istijiao", istijiao);
			localMap.put("setLongtime", setLongtime);
			break;
			// localCursor.moveToNext();
		}
		localCursor.close();
		paramSQLiteDatabase.close();
		return localMap;
	}

	// 客户拜访
	/*
	 * db.execSQL("create table if not exists l_visit(" +
	 * "id NTEXT PRIMARY KEY," + "clientid NTEXT," + "clientName NTEXT," +
	 * "data NTEXT," + "title NTEXT," + "remark NTEXT," + "lon NTEXT," +
	 * "lat NTEXT," + "location NTEXT," + "imgFile NTEXT," + "istijiao NTEXT)");
	 */

	public static void deleteLVisit(SQLiteDatabase paramSQLiteDatabase,
			String paramString) {
		paramSQLiteDatabase.delete("l_visit", "id='" + paramString + "'", null);
		paramSQLiteDatabase.close();
	}

	public static void insertLVisit(SQLiteDatabase paramSQLiteDatabase,
			String... parms) {
		ContentValues localContentValues = new ContentValues();
		localContentValues.put("id", parms[0]);
		localContentValues.put("clientid", parms[1]);
		localContentValues.put("clientName", parms[2]);
		localContentValues.put("data", parms[3]);
		localContentValues.put("title", parms[4]);
		localContentValues.put("remark", parms[5]);
		localContentValues.put("lon", parms[6]);
		localContentValues.put("lat", parms[7]);
		localContentValues.put("location", parms[8]);
		localContentValues.put("imgFile", parms[9]);
		localContentValues.put("istijiao", parms[10]);

		paramSQLiteDatabase.insert("l_visit", null, localContentValues);
		paramSQLiteDatabase.close();
	}

	public static void updateLVisit(SQLiteDatabase paramSQLiteDatabase,
			String parm) {
		ContentValues localContentValues = new ContentValues();
		localContentValues.put("istijiao", "11");
		paramSQLiteDatabase.update("l_visit", localContentValues, "id=?",
				new String[] { parm });
		paramSQLiteDatabase.close();

	}

	public static ArrayList<VisitBean> getDataFromLVisitA(
			SQLiteDatabase paramSQLiteDatabase, String paramString) {
		String[] arrayOfString = new String[1];
		arrayOfString[0] = "id,clientid,clientName,data,title,remark,lon,lat,location,imgFile,istijiao";
		Cursor cursor = paramSQLiteDatabase.query("l_visit", arrayOfString,
				"clientid='" + paramString + "'", null, null, null,
				"data DESC", null);
		ArrayList<VisitBean> arrayList = new ArrayList<VisitBean>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			VisitBean ib = new VisitBean();
			ib.setId(cursor.getString(0));
			ib.setClientid(cursor.getString(1));
			ib.setClientName(cursor.getString(2));
			ib.setDate(cursor.getString(3));
			ib.setTitle(cursor.getString(4));
			ib.setRemark(cursor.getString(5));
			ib.setLon(cursor.getString(6));
			ib.setLat(cursor.getString(7));
			ib.setLocation(cursor.getString(8));
			ib.setImgFile(cursor.getString(9));
			ib.setIstijiao(cursor.getString(10));
			arrayList.add(ib);
			cursor.moveToNext();
		}
		cursor.close();
		paramSQLiteDatabase.close();
		return arrayList;
	}

//	public static List<VisitBean> getDateFromVisitB{
//		List<VisitBean> beans = new ArrayList<VisitBean>();
//		beans=getDataFromVisitA(new LocationSQLiteHelper(context, name, factory, version));
//		return beans;
//	}
	// 所有客户拜访
	public static ArrayList<VisitBean> getDataFromLVisitA(
			SQLiteDatabase paramSQLiteDatabase) {
		Log.i("BaiFangService","查询前1");
		String[] arrayOfString = new String[1];
		arrayOfString[0] = "id,clientid,clientName,data,title,remark,lon,lat,location,imgFile,istijiao";
		Cursor cursor = paramSQLiteDatabase.query("l_visit", arrayOfString,
				null, null, null, null, "data DESC", null);
		ArrayList<VisitBean> arrayList = new ArrayList<VisitBean>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			VisitBean ib = new VisitBean();
			ib.setId(cursor.getString(0));
			ib.setClientid(cursor.getString(1));
			ib.setClientName(cursor.getString(2));
			ib.setDate(cursor.getString(3));
			ib.setTitle(cursor.getString(4));
			ib.setRemark(cursor.getString(5));
			ib.setLon(cursor.getString(6));
			ib.setLat(cursor.getString(7));
			ib.setLocation(cursor.getString(8));
			ib.setImgFile(cursor.getString(9));
			ib.setIstijiao(cursor.getString(10));
			arrayList.add(ib);
			cursor.moveToNext();
		}
		cursor.close();
		paramSQLiteDatabase.close();

		Log.i("BaiFangService",arrayList.toString());
		return arrayList;
	}

	public static HashMap<String, Object> getDataFromLVisitByID(
			SQLiteDatabase paramSQLiteDatabase, String paramString) {
		String[] arrayOfString = new String[1];
		arrayOfString[0] = "id,clientid,clientName,data,title,remark,lon,lat,location,imgFile,istijiao";
		Cursor localCursor = paramSQLiteDatabase.query("l_visit",
				arrayOfString, "id='" + paramString + "'", null, null, null,
				null, null);
		HashMap<String, Object> localMap = new HashMap<String, Object>();
		localCursor.moveToFirst();
		while (!localCursor.isAfterLast()) {
			String id = localCursor.getString(0);
			String clientid = localCursor.getString(1);
			String clientName = localCursor.getString(2);
			String data = localCursor.getString(3);
			String title = localCursor.getString(4);
			String remark = localCursor.getString(5);
			String lon = localCursor.getString(6);
			String lat = localCursor.getString(7);
			String location = localCursor.getString(8);
			String imgFile = localCursor.getString(9);
			String istijiao = localCursor.getString(10);

			localMap.put("id", id);
			localMap.put("clientid", clientid);
			localMap.put("clientName", clientName);
			localMap.put("data", data);
			localMap.put("title", title);
			localMap.put("remark", remark);
			localMap.put("lon", lon);
			localMap.put("lat", lat);
			localMap.put("location", location);
			localMap.put("imgFile", imgFile);
			localMap.put("istijiao", istijiao);
			break;
			// localCursor.moveToNext();
		}
		localCursor.close();
		paramSQLiteDatabase.close();
		return localMap;
	}

	public static void deleteLPlan(SQLiteDatabase paramSQLiteDatabase,
			String paramString) {
		paramSQLiteDatabase.delete("l_plan", "planId='" + paramString + "'",
				null);
		paramSQLiteDatabase.close();
	}

	public static void insertLPlan(SQLiteDatabase paramSQLiteDatabase,
			String... parms) {
		ContentValues localContentValues = new ContentValues();

		localContentValues.put("updateCode", parms[0]);
		localContentValues.put("planId", parms[1]);
		localContentValues.put("planCode", parms[2]);
		localContentValues.put("planDate", parms[3]);
		localContentValues.put("lon", parms[4]);
		localContentValues.put("lat", parms[5]);
		localContentValues.put("location", parms[6]);
		localContentValues.put("remark", parms[7]);
		localContentValues.put("type", parms[8]);
		localContentValues.put("planUploadDate", parms[9]);
		localContentValues.put("planText", parms[10]);
		localContentValues.put("imgFile", parms[11]);
		localContentValues.put("resultCode", parms[12]);
		localContentValues.put("title", parms[13]);
		localContentValues.put("istijiao", parms[14]);
		localContentValues.put("releasedate", parms[15]);
		paramSQLiteDatabase.insert("l_plan", null, localContentValues);
		paramSQLiteDatabase.close();

	}

	public static void updateLPlan(SQLiteDatabase paramSQLiteDatabase,
			String... prams) {
		paramSQLiteDatabase
				.execSQL(
						"update l_plan set planCode = ?, planDate=?, lon = ?, lat = ?, location = ?,"
								+ " remark = ?, type = ?, title=?, releasedate=?  where planId = ? ",
						new Object[] { prams[0], prams[1], prams[2], prams[3],
								prams[4], prams[5], prams[6], prams[7],
								prams[8], prams[9] });

		paramSQLiteDatabase.close();
	}

	public static void updateLPlanResultCode(
			SQLiteDatabase paramSQLiteDatabase, String... prams) {
		paramSQLiteDatabase
				.execSQL(
						"update l_plan set planText = ?, planUploadDate=?, resultCode = ?, istijiao = ?"
								+ ",imgFile=?,uplon=?,uplat=?,uplocation=?  where planId = ? ",
						new Object[] { prams[0], prams[1], prams[2], prams[3],
								prams[4], prams[5], prams[6], prams[7],
								prams[8] });

		paramSQLiteDatabase.close();
	}

	public static void updateLPlanIstijiao(SQLiteDatabase paramSQLiteDatabase,
			String... prams) {
		paramSQLiteDatabase
				.execSQL(
						"update l_plan set planText = ?, planUploadDate=?, istijiao = ?,imgFile=?,uplon=?,uplat=?,uplocation=?  where planId = ? ",
						new Object[] { prams[0], prams[1], prams[2], prams[3],
								prams[4], prams[5], prams[6], prams[7] });
		paramSQLiteDatabase.close();
	}

	public static List<HashMap<String, Object>> getDataFromLPlan(
			SQLiteDatabase paramSQLiteDatabase, String paramString) {
		String[] arrayOfString = new String[1];
		arrayOfString[0] = "planId,remark,planDate,plancode,title,istijiao,resultCode";
		Cursor cursor = paramSQLiteDatabase.query("l_plan", arrayOfString,
				"resultCode='" + paramString + "'", null, null, null,
				"planDate DESC", null);
		List<HashMap<String, Object>> mData = new ArrayList<HashMap<String, Object>>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			String planId = cursor.getString(0);
			String remark = cursor.getString(1);
			String planDate = cursor.getString(2);
			String plancode = cursor.getString(3);
			String title = cursor.getString(4);
			String istijiao = cursor.getString(5);
			String resultCode = cursor.getString(6);
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("planId", planId);
			map.put("remark", remark);
			map.put("planDate", planDate);
			if ("00".equals(istijiao)) {
				map.put("plan_imgFile", R.drawable.contactsnotupload);
			}
			map.put("plancode", plancode);
			map.put("title", title);
			map.put("resultCode", resultCode);
			mData.add(map);
			cursor.moveToNext();
		}
		cursor.close();
		paramSQLiteDatabase.close();
		return mData;
	}

	public static HashMap<String, Object> getDataFromLPlanByID(
			SQLiteDatabase paramSQLiteDatabase, String paramString) {
		String[] arrayOfString = new String[1];

		arrayOfString[0] = "planId,planDate,lon,lat,location,remark,planText"
				+ ",resultCode,title,imgFile,uplon,uplat,uplocation,istijiao,releasedate,planUploadDate";
		Cursor localCursor = paramSQLiteDatabase.query("l_plan", arrayOfString,
				"planId='" + paramString + "'", null, null, null, null, null);

		HashMap<String, Object> localMap = new HashMap<String, Object>();
		localCursor.moveToFirst();
		while (!localCursor.isAfterLast()) {
			String planId = localCursor.getString(0);
			String planDate = localCursor.getString(1);
			String lon = localCursor.getString(2);
			String lat = localCursor.getString(3);
			String location = localCursor.getString(4);
			String remark = localCursor.getString(5);
			String planText = localCursor.getString(6);
			String resultCode = localCursor.getString(7);
			String title = localCursor.getString(8);
			String imgFile = localCursor.getString(9);
			String uplon = localCursor.getString(10);
			String uplat = localCursor.getString(11);
			String uplocation = localCursor.getString(12);
			String istijiao = localCursor.getString(13);

			String releasedate = localCursor.getString(14);
			String planUploadDate = localCursor.getString(15);
			localMap.put("planId", planId);
			localMap.put("planDate", planDate);
			localMap.put("lon", lon);
			localMap.put("lat", lat);
			localMap.put("location", location);
			localMap.put("remark", remark);
			localMap.put("planText", planText);
			localMap.put("resultCode", resultCode);
			localMap.put("title", title);
			localMap.put("imgFile", imgFile);
			localMap.put("uplon", uplon);
			localMap.put("uplat", uplat);
			localMap.put("uplocation", uplocation);
			localMap.put("istijiao", istijiao);
			localMap.put("releasedate", releasedate);
			localMap.put("planUploadDate", planUploadDate);
			break;
		}
		localCursor.close();
		paramSQLiteDatabase.close();
		return localMap;
	}

	public static void deleteLCust(SQLiteDatabase paramSQLiteDatabase) {
		// paramSQLiteDatabase.delete("l_plan", null, null);
		// paramSQLiteDatabase.close();
		paramSQLiteDatabase.execSQL("delete from l_cust", new Object[] {});
		paramSQLiteDatabase.close();
	}

	public static void deleteLCustHasUp(SQLiteDatabase paramSQLiteDatabase) {
		// paramSQLiteDatabase.delete("l_plan", null, null);
		// paramSQLiteDatabase.close();
		paramSQLiteDatabase.delete("l_cust", "istijiao!='" + "00" + "'", null);
		paramSQLiteDatabase.close();
	}

	public static void deleteLCustbyId(SQLiteDatabase paramSQLiteDatabase,
			String paramString) {
		paramSQLiteDatabase.delete("l_cust", "id='" + paramString + "'", null);
		paramSQLiteDatabase.close();
	}

	public static void insertLCust(SQLiteDatabase paramSQLiteDatabase,
			String... parms) {
		while (paramSQLiteDatabase.isDbLockedByOtherThreads()
				|| paramSQLiteDatabase.isDbLockedByCurrentThread()) {
			FileLog.w(TAG,
					"insert === db is locked by other or current threads!");
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		try {
			ContentValues localContentValues = new ContentValues();
			localContentValues.put("id", parms[0]);
			localContentValues.put("clientName", parms[1]);
			localContentValues.put("contacts", parms[2]);
			localContentValues.put("lon", parms[3]);
			localContentValues.put("lat", parms[4]);
			localContentValues.put("location", parms[5]);
			localContentValues.put("email", parms[6]);
			localContentValues.put("phone", parms[7]);
			localContentValues.put("address", parms[8]);
			localContentValues.put("type", parms[9]);
			localContentValues.put("py", parms[10]);
			localContentValues.put("istijiao", parms[11]);

			localContentValues.put("c_t_id", parms[12]);
			localContentValues.put("region_id", parms[13]);
			localContentValues.put("c_t_name", parms[14]);
			localContentValues.put("region_name", parms[15]);

			localContentValues.put("job", parms[16]);
			localContentValues.put("Phone2", parms[17]);
			localContentValues.put("Phone3", parms[18]);
			localContentValues.put("Phone4", parms[19]);

			paramSQLiteDatabase.insert("l_cust", null, localContentValues);
			paramSQLiteDatabase.close();
		} catch (Exception e) {
			FileLog.e(TAG, "insert cust db:" + e);
		}
	}

	public static void updateCustAll(SQLiteDatabase paramSQLiteDatabase,
			String... prams) {

		paramSQLiteDatabase
				.execSQL(
						"update l_cust set clientName = ?, contacts=?, lon = ?, lat = ?"
								+ ", location = ?, email = ?, phone = ?, address=?, type=?,py=?, istijiao=?"
								+ ", c_t_id=?, region_id=?, c_t_name=?, region_name=?, job=?, Phone2=?, Phone3=?, Phone4=? where id = ? ",
						new Object[] { prams[0], prams[1], prams[2], prams[3],
								prams[4], prams[5], prams[6], prams[7],
								prams[8], prams[9], prams[10], prams[11],
								prams[12], prams[13], prams[14], prams[15],
								prams[16], prams[17], prams[18], prams[19] });

		paramSQLiteDatabase.close();
	}

	public static void updateCustPy(SQLiteDatabase paramSQLiteDatabase,
			String... prams) {
		paramSQLiteDatabase.execSQL("update l_cust set py=? where id = ? ",
				new Object[] { prams[0], prams[1] });
		paramSQLiteDatabase.close();
	}

	public static void updateLCust(SQLiteDatabase paramSQLiteDatabase,
			String myid, String cid) {
		ContentValues localContentValues = new ContentValues();
		localContentValues.put("istijiao", "11");
		localContentValues.put("id", cid);
		paramSQLiteDatabase.update("l_cust", localContentValues, "id=?",
				new String[] { myid });
		paramSQLiteDatabase.close();

	}

	public static void insertLCustS(SQLiteDatabase paramSQLiteDatabase,
			String... parms) {
		ContentValues localContentValues = new ContentValues();
		localContentValues.put("id", parms[0]);
		localContentValues.put("clientName", parms[1]);
		localContentValues.put("contacts", parms[2]);
		localContentValues.put("lon", parms[3]);
		localContentValues.put("lat", parms[4]);
		localContentValues.put("email", parms[5]);
		localContentValues.put("phone", parms[6]);
		localContentValues.put("address", parms[7]);
		localContentValues.put("type", parms[8]);
		localContentValues.put("istijiao", parms[9]);

		paramSQLiteDatabase.insert("l_cust", null, localContentValues);
		paramSQLiteDatabase.close();
	}

	public static List<HashMap<String, Object>> getDataFromLCust(
			SQLiteDatabase paramSQLiteDatabase) {
		String[] arrayOfString = new String[1];
		arrayOfString[0] = "id,clientName,contacts,istijiao";
		Cursor cursor = paramSQLiteDatabase.query("l_cust", arrayOfString,
				null, null, null, null, null, null);
		List<HashMap<String, Object>> mData = new ArrayList<HashMap<String, Object>>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			String id = cursor.getString(0);
			String clientName = cursor.getString(1);
			String contacts = cursor.getString(2);
			String istijiao = cursor.getString(3);
			FileLog.i("sa", id + "|" + clientName + "|" + contacts + "|"
					+ istijiao);
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("id", id);
			map.put("clientName", clientName);
			map.put("contacts", contacts);
			map.put("istijiao", istijiao);
			mData.add(map);
			cursor.moveToNext();
		}
		cursor.close();
		paramSQLiteDatabase.close();
		return mData;
	}

	public static ArrayList<CustBean> getDataFromLCustMg(
			SQLiteDatabase paramSQLiteDatabase) {
		ArrayList<CustBean> arrayList = new ArrayList<CustBean>();
		try {
			String[] arrayOfString = new String[1];
			arrayOfString[0] = "id,clientName,contacts,py,istijiao";
			Cursor cursor = paramSQLiteDatabase.query("l_cust", arrayOfString,
					null, null, null, null, null, null);
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				CustBean ib = new CustBean();
				ib.setId(cursor.getString(0));
				ib.setClientName(cursor.getString(1));
				ib.setContacts(cursor.getString(2));
				ib.setClientNamePinYin(cursor.getString(3));
				ib.setIstijiao(cursor.getString(4));
				arrayList.add(ib);
				cursor.moveToNext();
			}
			cursor.close();
			paramSQLiteDatabase.close();
		} catch (Exception e) {
			FileLog.e(TAG, "db error:" + e);
		}
		return arrayList;
	}

	public static ArrayList<CustBean> getDataFromLCustMgHasUp(
			SQLiteDatabase paramSQLiteDatabase) {
		ArrayList<CustBean> arrayList = new ArrayList<CustBean>();
		try {
			String[] arrayOfString = new String[1];
			arrayOfString[0] = "id,clientName,contacts,py,istijiao";
			Cursor cursor = paramSQLiteDatabase.query("l_cust", arrayOfString,
					"istijiao='" + "11" + "'", null, null, null, null, null);
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				CustBean ib = new CustBean();
				ib.setId(cursor.getString(0));
				ib.setClientName(cursor.getString(1));
				ib.setContacts(cursor.getString(2));
				ib.setClientNamePinYin(cursor.getString(3));
				ib.setIstijiao(cursor.getString(4));

				arrayList.add(ib);

				cursor.moveToNext();
			}
			cursor.close();
			paramSQLiteDatabase.close();
		} catch (Exception e) {
			FileLog.e(TAG, "db error:" + e);
		}
		return arrayList;
	}

	// 模糊查询
	public static ArrayList<CustBean> getDataFromLCustMgHasUpAndLike(
			SQLiteDatabase paramSQLiteDatabase, String pp) {
		ArrayList<CustBean> arrayList = new ArrayList<CustBean>();
		try {
			String[] arrayOfString = new String[1];
			arrayOfString[0] = "id,clientName,contacts,py,istijiao";
			Cursor cursor = paramSQLiteDatabase.query("l_cust", arrayOfString,
					"clientName like '%" + pp + "%'" + "and istijiao='" + "11"
							+ "'", null, null, null, null, null);
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				CustBean ib = new CustBean();
				ib.setId(cursor.getString(0));
				ib.setClientName(cursor.getString(1));
				ib.setContacts(cursor.getString(2));
				ib.setClientNamePinYin(cursor.getString(3));
				ib.setIstijiao(cursor.getString(4));

				arrayList.add(ib);

				cursor.moveToNext();
			}
			cursor.close();
			paramSQLiteDatabase.close();
		} catch (Exception e) {
			FileLog.e(TAG, "db error:" + e);
		}
		return arrayList;
	}

	public static ArrayList<CustBean> getDataFromLCustMgEmployeeshare(
			SQLiteDatabase paramSQLiteDatabase) {
		ArrayList<CustBean> arrayList = new ArrayList<CustBean>();
		try {
			String[] arrayOfString = new String[1];
			arrayOfString[0] = "id,clientName,contacts,py,istijiao";
			Cursor cursor = paramSQLiteDatabase.query("l_cust", arrayOfString,
					"type='" + "1" + "'", null, null, null, null, null);
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				CustBean ib = new CustBean();
				ib.setId(cursor.getString(0));
				ib.setClientName(cursor.getString(1));
				ib.setContacts(cursor.getString(2));
				ib.setClientNamePinYin(cursor.getString(3));
				ib.setIstijiao(cursor.getString(4));

				arrayList.add(ib);

				cursor.moveToNext();
			}
			cursor.close();
			paramSQLiteDatabase.close();
		} catch (Exception e) {
			FileLog.e(TAG, "db error:" + e);
		}
		return arrayList;
	}

	public static ArrayList<CustBean> getDataFromLCustMgEmployeeprivate(
			SQLiteDatabase paramSQLiteDatabase) {
		ArrayList<CustBean> arrayList = new ArrayList<CustBean>();
		try {
			String[] arrayOfString = new String[1];
			arrayOfString[0] = "id,clientName,contacts,py,istijiao";
			Cursor cursor = paramSQLiteDatabase.query("l_cust", arrayOfString,
					"type='" + "3" + "'", null, null, null, null, null);
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				CustBean ib = new CustBean();
				ib.setId(cursor.getString(0));
				ib.setClientName(cursor.getString(1));
				ib.setContacts(cursor.getString(2));
				ib.setClientNamePinYin(cursor.getString(3));
				ib.setIstijiao(cursor.getString(4));

				arrayList.add(ib);

				cursor.moveToNext();
			}
			cursor.close();
			paramSQLiteDatabase.close();
		} catch (Exception e) {
			FileLog.e(TAG, "db error:" + e);
		}
		return arrayList;
	}

	public static HashMap<String, Object> getDataFromLCustByClientId(
			SQLiteDatabase paramSQLiteDatabase, String paramString) {
		String[] arrayOfString = new String[1];

		arrayOfString[0] = "clientName,contacts,lon,lat,location,email,phone,address"
				+ ",type,istijiao,c_t_id,region_id,c_t_name,region_name,job,Phone2,Phone3,Phone4";
		Cursor localCursor = paramSQLiteDatabase.query("l_cust", arrayOfString,
				"id='" + paramString + "'", null, null, null, null, null);

		HashMap<String, Object> localMap = new HashMap<String, Object>();
		localCursor.moveToFirst();
		while (!localCursor.isAfterLast()) {
			String clientName = localCursor.getString(0);
			String contacts = localCursor.getString(1);
			String lon = localCursor.getString(2);
			String lat = localCursor.getString(3);
			String location = localCursor.getString(4);
			String email = localCursor.getString(5);
			String phone = localCursor.getString(6);
			String address = localCursor.getString(7);
			String type = localCursor.getString(8);
			String istijiao = localCursor.getString(9);
			String c_t_id = localCursor.getString(10);
			String region_id = localCursor.getString(11);
			String c_t_name = localCursor.getString(12);
			String region_name = localCursor.getString(13);
			String job = localCursor.getString(14);
			String Phone2 = localCursor.getString(15);
			String Phone3 = localCursor.getString(16);
			String Phone4 = localCursor.getString(17);

			localMap.put("clientName", clientName);
			localMap.put("contacts", contacts);
			localMap.put("location", location);
			localMap.put("email", email);
			localMap.put("phone", phone);
			localMap.put("lon", lon);
			localMap.put("lat", lat);
			localMap.put("address", address);
			localMap.put("type", type);
			localMap.put("istijiao", istijiao);
			localMap.put("c_t_id", c_t_id);
			localMap.put("region_id", region_id);
			localMap.put("c_t_name", c_t_name);
			localMap.put("region_name", region_name);

			localMap.put("job", job);
			localMap.put("Phone2", Phone2);
			localMap.put("Phone3", Phone3);
			localMap.put("Phone4", Phone4);

			break;
			// localCursor.moveToNext();
		}
		localCursor.close();
		paramSQLiteDatabase.close();
		return localMap;
	}

	public static HashMap<String, Object> getDataFromLCustByClientIdA(
			SQLiteDatabase paramSQLiteDatabase, String paramString) {
		String[] arrayOfString = new String[1];
		arrayOfString[0] = "istijiao";
		Cursor localCursor = paramSQLiteDatabase.query("l_cust", arrayOfString,
				"id='" + paramString + "'", null, null, null, null, null);
		HashMap<String, Object> localMap = new HashMap<String, Object>();
		localCursor.moveToFirst();
		while (!localCursor.isAfterLast()) {
			String istijiao = localCursor.getString(0);
			localMap.put("istijiao", istijiao);
			break;
			// localCursor.moveToNext();
		}
		localCursor.close();
		paramSQLiteDatabase.close();
		return localMap;
	}

	public static HashMap<String, Object> getDataFromLCustByClientName(
			SQLiteDatabase paramSQLiteDatabase, String paramString) {
		String[] arrayOfString = new String[1];
		arrayOfString[0] = "clientName,contacts,lon,lat,location,email,phone";
		Cursor localCursor = paramSQLiteDatabase.query("l_cust", arrayOfString,
				"clientName='" + paramString + "'", null, null, null, null,
				null);
		HashMap<String, Object> localMap = new HashMap<String, Object>();
		localCursor.moveToFirst();
		while (!localCursor.isAfterLast()) {
			String clientName = localCursor.getString(0);
			String contacts = localCursor.getString(1);
			String lon = localCursor.getString(2);
			String lat = localCursor.getString(3);
			String location = localCursor.getString(4);
			String email = localCursor.getString(5);
			String phone = localCursor.getString(6);
			localMap.put("clientName", clientName);
			localMap.put("contacts", contacts);
			localMap.put("location", location);
			localMap.put("email", email);
			localMap.put("phone", phone);
			localMap.put("lon", lon);
			localMap.put("lat", lat);
			break;
			// localCursor.moveToNext();
		}
		localCursor.close();
		paramSQLiteDatabase.close();
		return localMap;
	}

	public static void insertLLoc(SQLiteDatabase paramSQLiteDatabase,
			String... parms) {
		ContentValues localContentValues = new ContentValues();
		localContentValues.put("locTime", parms[0]);
		localContentValues.put("chkTag", parms[1]);
		localContentValues.put("lon", parms[2]);
		localContentValues.put("lat", parms[3]);
		localContentValues.put("addr", parms[4]);
		localContentValues.put("accuracy", parms[5]);
		paramSQLiteDatabase.insert("l_loc", null, localContentValues);
		paramSQLiteDatabase.close();
	}

	public static List<HashMap<String, Object>> getDataFromLLoc(
			SQLiteDatabase paramSQLiteDatabase, String paramString) {
		String[] arrayOfString = new String[1];
		arrayOfString[0] = "locTime, accuracy";
		Cursor cursor = paramSQLiteDatabase.query("l_loc", arrayOfString,
				"chkTag='" + paramString + "'", null, null, null,
				"locTime DESC", null);
		List<HashMap<String, Object>> mData = new ArrayList<HashMap<String, Object>>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			String locTime = cursor.getString(0);
			String accuracy = cursor.getString(1);
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("locTime", locTime);
			map.put("accuracy", accuracy);
			mData.add(map);
			cursor.moveToNext();
		}
		cursor.close();
		paramSQLiteDatabase.close();
		return mData;
	}

	public static ArrayList<LocBean> getDataFromLLocA(
			SQLiteDatabase paramSQLiteDatabase, String paramString) {
		String[] arrayOfString = new String[1];
		arrayOfString[0] = "locTime, addr, lon, lat";
		Cursor cursor = paramSQLiteDatabase.query("l_loc", arrayOfString,
				"chkTag='" + paramString + "'", null, null, null,
				"locTime DESC", null);
		ArrayList<LocBean> arrayList = new ArrayList<LocBean>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {

			LocBean lb = new LocBean();
			lb.setLocTime(cursor.getString(0));
			lb.setAddr(cursor.getString(1));
			lb.setLon(cursor.getString(2));
			lb.setLat(cursor.getString(3));
			arrayList.add(lb);
			cursor.moveToNext();
		}
		cursor.close();
		paramSQLiteDatabase.close();
		return arrayList;
	}

	public static ArrayList<LocBean> getDataFromLLocA(
			SQLiteDatabase paramSQLiteDatabase, String paramString, String time) {

		String[] arrayOfString = new String[1];
		arrayOfString[0] = "locTime, addr, lon, lat";

		Cursor cursor = paramSQLiteDatabase.query("l_loc", arrayOfString,
				"locTime like '%" + time + "%'" + "and chkTag='" + paramString
						+ "'", null, null, null, "locTime DESC", null);
		ArrayList<LocBean> arrayList = new ArrayList<LocBean>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {

			LocBean lb = new LocBean();
			lb.setLocTime(cursor.getString(0));
			lb.setAddr(cursor.getString(1));
			lb.setLon(cursor.getString(2));
			lb.setLat(cursor.getString(3));
			arrayList.add(lb);
			cursor.moveToNext();
		}
		cursor.close();
		paramSQLiteDatabase.close();
		return arrayList;
	}

	public static void deleteLCustType(SQLiteDatabase paramSQLiteDatabase) {
		paramSQLiteDatabase.delete("l_cust_type", null, null);
		paramSQLiteDatabase.close();
	}

	public static void deleteLCustArea(SQLiteDatabase paramSQLiteDatabase) {
		paramSQLiteDatabase.delete("l_cust_area", null, null);
		paramSQLiteDatabase.close();
	}

	public static void insertLCustType(SQLiteDatabase paramSQLiteDatabase,
			String... parms) {
		ContentValues localContentValues = new ContentValues();
		localContentValues.put("id", parms[0]);
		localContentValues.put("name", parms[1]);
		paramSQLiteDatabase.insert("l_cust_type", null, localContentValues);
		paramSQLiteDatabase.close();
	}

	public static void insertLCustArea(SQLiteDatabase paramSQLiteDatabase,
			String... parms) {
		ContentValues localContentValues = new ContentValues();
		localContentValues.put("id", parms[0]);
		localContentValues.put("name", parms[1]);
		localContentValues.put("pid", parms[2]);
		localContentValues.put("level", parms[3]);
		paramSQLiteDatabase.insert("l_cust_area", null, localContentValues);
		paramSQLiteDatabase.close();
	}

	public static List<CustProp> getDataFromLCustType(
			SQLiteDatabase paramSQLiteDatabase) {
		String[] arrayOfString = new String[1];
		arrayOfString[0] = "id,name";
		Cursor cursor = paramSQLiteDatabase.query("l_cust_type", arrayOfString,
				null, null, null, null, null, null);
		List<CustProp> mData = new ArrayList<CustProp>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			String id = cursor.getString(0);
			String name = cursor.getString(1);
			CustProp cp = new CustProp();
			cp.setId(id);
			cp.setName(name);
			mData.add(cp);
			cursor.moveToNext();
		}
		cursor.close();
		paramSQLiteDatabase.close();
		return mData;
	}

	public static List<CustProp> getDataFromLCustArea(
			SQLiteDatabase paramSQLiteDatabase, String level) {
		String[] arrayOfString = new String[1];
		arrayOfString[0] = "id,name";
		Cursor cursor = paramSQLiteDatabase.query("l_cust_area", arrayOfString,
				"level='" + level + "'", null, null, null, null, null);
		List<CustProp> mData = new ArrayList<CustProp>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			String id = cursor.getString(0);
			String name = cursor.getString(1);
			CustProp cp = new CustProp();
			cp.setId(id);
			cp.setName(name);
			mData.add(cp);
			cursor.moveToNext();
		}
		cursor.close();
		paramSQLiteDatabase.close();
		return mData;
	}

	public static List<CustProp> getDataFromLCustAreaByPid(
			SQLiteDatabase paramSQLiteDatabase, String pid) {
		String[] arrayOfString = new String[1];
		arrayOfString[0] = "id,name";
		Cursor cursor = paramSQLiteDatabase.query("l_cust_area", arrayOfString,
				"pid='" + pid + "'", null, null, null, null, null);
		List<CustProp> mData = new ArrayList<CustProp>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			String id = cursor.getString(0);
			String name = cursor.getString(1);
			CustProp cp = new CustProp();
			cp.setId(id);
			cp.setName(name);
			mData.add(cp);
			cursor.moveToNext();
		}
		cursor.close();
		paramSQLiteDatabase.close();
		return mData;
	}

	// 商品清单获得商品
	public static ArrayList<GoodsBean> getDataFromLGoods(
			SQLiteDatabase paramSQLiteDatabase) {
		ArrayList<GoodsBean> arrayList = new ArrayList<GoodsBean>();
		// "id NTEXT,"
		// + "name NTEXT,"
		// + "packing NTEXT)");
		try {
			String[] arrayOfString = new String[1];
			arrayOfString[0] = "id,name,packing";
			Cursor cursor = paramSQLiteDatabase.query("l_goods", arrayOfString,
					null, null, null, null, null, null);
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				GoodsBean ib = new GoodsBean();
				ib.setId(cursor.getString(0));
				ib.setName(cursor.getString(1));
				ib.setPacking(cursor.getString(2));

				arrayList.add(ib);

				cursor.moveToNext();
			}
			cursor.close();
			paramSQLiteDatabase.close();
		} catch (Exception e) {
			FileLog.e(TAG, "db error:" + e);
		}
		return arrayList;
	}

	public static void deleteLGoods(SQLiteDatabase paramSQLiteDatabase) {
		paramSQLiteDatabase.delete("l_goods", null, null);
		paramSQLiteDatabase.close();
	}

	public static void insertGoods(SQLiteDatabase paramSQLiteDatabase,
			String... parms) {
		// id,name,packing
		ContentValues localContentValues = new ContentValues();
		localContentValues.put("id", parms[0]);
		localContentValues.put("name", parms[1]);
		localContentValues.put("packing", parms[2]);

		paramSQLiteDatabase.insert("l_goods", null, localContentValues);
		paramSQLiteDatabase.close();
	}

	// 销售上报模块
	public static void insertSalesReport(SQLiteDatabase paramSQLiteDatabase,
			String... parms) {

		ContentValues localContentValues = new ContentValues();
		localContentValues.put("id", parms[0]);
		localContentValues.put("clientid", parms[1]);
		localContentValues.put("clientName", parms[2]);
		localContentValues.put("date", parms[3]);
		localContentValues.put("goods_id", parms[4]);
		localContentValues.put("imgFile", parms[5]);
		localContentValues.put("remark", parms[6]);
		localContentValues.put("lon", parms[7]);
		localContentValues.put("lat", parms[8]);
		localContentValues.put("location", parms[9]);
		localContentValues.put("istijiao", parms[10]);
		localContentValues.put("submitdate", parms[11]);

		paramSQLiteDatabase.insert("l_salesreport", null, localContentValues);
		paramSQLiteDatabase.close();
	}

	public static void insertSalesReportGoods(
			SQLiteDatabase paramSQLiteDatabase, String... parms) {

		ContentValues localContentValues = new ContentValues();
		localContentValues.put("id", parms[0]);
		localContentValues.put("goods_id", parms[1]);
		localContentValues.put("name", parms[2]);
		localContentValues.put("amount", parms[3]);
		localContentValues.put("packing", parms[4]);
		localContentValues.put("each_id", parms[5]);
		paramSQLiteDatabase.insert("l_salesreport_goods", null,
				localContentValues);
		paramSQLiteDatabase.close();
	}

	public static ArrayList<SalesReportBean> getDataFromSalesReport(
			SQLiteDatabase paramSQLiteDatabase, String key) {

		String[] arrayOfString = new String[1];
		arrayOfString[0] = "id,clientid,clientName,date,goods_id,imgFile,remark,lon,lat,location,istijiao,submitdate";
		Cursor cursor = paramSQLiteDatabase.query("l_salesreport",
				arrayOfString, "clientName like '%" + key + "%'", null, null,
				null, "submitdate DESC", null);
		ArrayList<SalesReportBean> arrayList = new ArrayList<SalesReportBean>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			SalesReportBean ib = new SalesReportBean();
			ib.setId(cursor.getString(0));
			ib.setClientid(cursor.getString(1));
			ib.setClientName(cursor.getString(2));
			ib.setDate(cursor.getString(3));
			ib.setGoods_id(cursor.getString(4));
			ib.setImgFile(cursor.getString(5));
			ib.setRemark(cursor.getString(6));
			ib.setLon(cursor.getString(7));
			ib.setLat(cursor.getString(8));
			ib.setLocation(cursor.getString(9));
			ib.setIstijiao(cursor.getString(10));
			ib.setSubmitdate(cursor.getString(11));
			arrayList.add(ib);
			cursor.moveToNext();
		}
		cursor.close();
		paramSQLiteDatabase.close();
		return arrayList;
	}

	public static HashMap<String, Object> getDataFromSalesReportByID(
			SQLiteDatabase paramSQLiteDatabase, String paramString) {

		String[] arrayOfString = new String[1];
		arrayOfString[0] = "id,clientid,clientName,date,goods_id,imgFile,remark,lon,lat,location,istijiao,submitdate";
		Cursor localCursor = paramSQLiteDatabase.query("l_salesreport",
				arrayOfString, "id='" + paramString + "'", null, null, null,
				null, null);
		HashMap<String, Object> localMap = new HashMap<String, Object>();
		localCursor.moveToFirst();
		while (!localCursor.isAfterLast()) {
			String id = localCursor.getString(0);
			String clientid = localCursor.getString(1);
			String clientName = localCursor.getString(2);
			String date = localCursor.getString(3);
			String goods_id = localCursor.getString(4);
			String imgFile = localCursor.getString(5);
			String remark = localCursor.getString(6);
			String lon = localCursor.getString(7);
			String lat = localCursor.getString(8);
			String location = localCursor.getString(9);
			String istijiao = localCursor.getString(10);
			String submitdate = localCursor.getString(11);

			localMap.put("id", id);
			localMap.put("clientid", clientid);
			localMap.put("clientName", clientName);
			localMap.put("date", date);
			localMap.put("goods_id", goods_id);
			localMap.put("imgFile", imgFile);
			localMap.put("remark", remark);
			localMap.put("lon", lon);
			localMap.put("lat", lat);
			localMap.put("location", location);
			localMap.put("istijiao", istijiao);
			localMap.put("submitdate", submitdate);
			break;
			// localCursor.moveToNext();
		}
		localCursor.close();
		paramSQLiteDatabase.close();
		return localMap;
	}

	public static ArrayList<GoodsReportBean> getDataFromSalesReportGoodsByGoodsId(
			SQLiteDatabase paramSQLiteDatabase, String paramString) {

		String[] arrayOfString = new String[1];
		arrayOfString[0] = "id,goods_id,name,amount,packing,each_id";
		Cursor cursor = paramSQLiteDatabase.query("l_salesreport_goods",
				arrayOfString, "goods_id='" + paramString + "'", null, null,
				null, null, null);
		ArrayList<GoodsReportBean> arrayList = new ArrayList<GoodsReportBean>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			GoodsReportBean ib = new GoodsReportBean();

			ib.setName(cursor.getString(2));
			ib.setAmount(cursor.getString(3));
			ib.setPacking(cursor.getString(4));
			ib.setId(cursor.getString(5));
			arrayList.add(ib);
			cursor.moveToNext();
		}
		cursor.close();
		paramSQLiteDatabase.close();
		return arrayList;
	}

	// 删除
	public static void deleteSalesReportByID(
			SQLiteDatabase paramSQLiteDatabase, String paramString) {
		paramSQLiteDatabase.delete("l_salesreport", "id='" + paramString + "'",
				null);
		paramSQLiteDatabase.close();
	}

	public static void deleteSalesReportGoodsByGoodsId(
			SQLiteDatabase paramSQLiteDatabase, String paramString) {
		paramSQLiteDatabase.delete("l_salesreport_goods", "goods_id='"
				+ paramString + "'", null);
		paramSQLiteDatabase.close();
	}

	// 销售任务分配模块
	public static void insertSalesallocation(
			SQLiteDatabase paramSQLiteDatabase, String... parms) {

		ContentValues localContentValues = new ContentValues();
		localContentValues.put("id", parms[0]);
		localContentValues.put("clientid", parms[1]);
		localContentValues.put("clientName", parms[2]);
		localContentValues.put("date", parms[3]);
		localContentValues.put("goods_id", parms[4]);
		localContentValues.put("istijiao", parms[5]);

		paramSQLiteDatabase.insert("l_salesallocation", null,
				localContentValues);
		paramSQLiteDatabase.close();
	}

	public static void insertSalesallocationGoods(
			SQLiteDatabase paramSQLiteDatabase, String... parms) {
		// db.execSQL("create table if not exists l_salesallocation_goods("
		// + "id NTEXT,"
		// + "goods_id NTEXT,"
		// + "name NTEXT,"
		// + "amount NTEXT,"
		// + "unallocatedamount NTEXT,"
		// + "packing NTEXT)");

		ContentValues localContentValues = new ContentValues();
		localContentValues.put("id", parms[0]);
		localContentValues.put("goods_id", parms[1]);
		localContentValues.put("name", parms[2]);
		localContentValues.put("amount", parms[3]);
		localContentValues.put("unallocatedamount", parms[4]);
		localContentValues.put("packing", parms[5]);
		localContentValues.put("each_id", parms[6]);

		paramSQLiteDatabase.insert("l_salesallocation_goods", null,
				localContentValues);
		paramSQLiteDatabase.close();
	}

	public static ArrayList<SalestaskAllocationBean> getDataFromSalesallocation(
			SQLiteDatabase paramSQLiteDatabase, String key) {
		// db.execSQL("create table if not exists l_salesallocation("
		// + "id NTEXT,"
		// + "clientid NTEXT,"
		// + "clientName NTEXT,"
		// + "date NTEXT,"
		// + "goods_id NTEXT,"
		// + "istijiao NTEXT)");
		String[] arrayOfString = new String[1];
		arrayOfString[0] = "id,clientid,clientName,date,goods_id,istijiao";
		Cursor cursor = paramSQLiteDatabase.query("l_salesallocation",
				arrayOfString, "clientName like '%" + key + "%'", null, null,
				null, "date DESC,clientName COLLATE LOCALIZED", null);
		ArrayList<SalestaskAllocationBean> arrayList = new ArrayList<SalestaskAllocationBean>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			SalestaskAllocationBean ib = new SalestaskAllocationBean();
			ib.setId(cursor.getString(0));
			ib.setClientid(cursor.getString(1));
			ib.setClientName(cursor.getString(2));
			ib.setDate(cursor.getString(3));
			ib.setGoods_id(cursor.getString(4));
			ib.setIstijiao(cursor.getString(5));

			arrayList.add(ib);
			cursor.moveToNext();
		}
		cursor.close();
		paramSQLiteDatabase.close();
		return arrayList;
	}

	public static HashMap<String, Object> getDataFromSalesallocationByID(
			SQLiteDatabase paramSQLiteDatabase, String paramString) {
		// arrayOfString[0] = "id,clientid,clientName,date,goods_id,istijiao";
		String[] arrayOfString = new String[1];
		arrayOfString[0] = "id,clientid,clientName,date,goods_id,istijiao";
		Cursor localCursor = paramSQLiteDatabase.query("l_salesallocation",
				arrayOfString, "id='" + paramString + "'", null, null, null,
				null, null);
		HashMap<String, Object> localMap = new HashMap<String, Object>();
		localCursor.moveToFirst();
		while (!localCursor.isAfterLast()) {
			String id = localCursor.getString(0);
			String clientid = localCursor.getString(1);
			String clientName = localCursor.getString(2);
			String date = localCursor.getString(3);
			String goods_id = localCursor.getString(4);

			String istijiao = localCursor.getString(5);

			localMap.put("id", id);
			localMap.put("clientid", clientid);
			localMap.put("clientName", clientName);
			localMap.put("date", date);
			localMap.put("goods_id", goods_id);
			localMap.put("istijiao", istijiao);
			break;
			// localCursor.moveToNext();
		}
		localCursor.close();
		paramSQLiteDatabase.close();
		return localMap;
	}

	public static ArrayList<GoodsMonthCustBean> getDataFromSalesallocationGoodsByGoodsId(
			SQLiteDatabase paramSQLiteDatabase, String paramString) {
		// db.execSQL("create table if not exists l_salesallocation_goods("
		// + "id NTEXT,"
		// + "goods_id NTEXT,"
		// + "name NTEXT,"
		// + "amount NTEXT,"
		// + "unallocatedamount NTEXT,"
		// + "packing NTEXT)");

		String[] arrayOfString = new String[1];
		arrayOfString[0] = "id,goods_id,name,amount,unallocatedamount,packing,each_id";
		Cursor cursor = paramSQLiteDatabase.query("l_salesallocation_goods",
				arrayOfString, "goods_id='" + paramString + "'", null, null,
				null, null, null);
		ArrayList<GoodsMonthCustBean> arrayList = new ArrayList<GoodsMonthCustBean>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			GoodsMonthCustBean ib = new GoodsMonthCustBean();

			ib.setGoodsname(cursor.getString(2));
			ib.setAmount(cursor.getString(3));
			ib.setTarget(cursor.getString(4));
			ib.setGoodsunit(cursor.getString(5));
			ib.setId(cursor.getString(6));
			arrayList.add(ib);
			cursor.moveToNext();
		}
		cursor.close();
		paramSQLiteDatabase.close();
		return arrayList;
	}

	// 删除
	public static void deleteAllocationByID(SQLiteDatabase paramSQLiteDatabase,
			String paramString) {
		paramSQLiteDatabase.delete("l_salesallocation", "id='" + paramString
				+ "'", null);
		paramSQLiteDatabase.close();
	}

	public static void deleteAllocationGoodsByGoodsId(
			SQLiteDatabase paramSQLiteDatabase, String paramString) {
		paramSQLiteDatabase.delete("l_salesallocation_goods", "goods_id='"
				+ paramString + "'", null);
		paramSQLiteDatabase.close();
	}

	// yue模块
	public static void insertMonthstarget(SQLiteDatabase paramSQLiteDatabase,
			String... parms) {
		// + "id NTEXT,"
		// + "date NTEXT,"
		// + "goods_id NTEXT)");

		ContentValues localContentValues = new ContentValues();
		localContentValues.put("id", parms[0]);
		localContentValues.put("date", parms[1]);
		localContentValues.put("goods_id", parms[2]);

		paramSQLiteDatabase.insert("l_monthstarget", null, localContentValues);
		paramSQLiteDatabase.close();
	}

	public static void insertMonthstargetGoods(
			SQLiteDatabase paramSQLiteDatabase, String... parms) {

		ContentValues localContentValues = new ContentValues();
		localContentValues.put("id", parms[0]);
		localContentValues.put("goods_id", parms[1]);
		localContentValues.put("name", parms[2]);
		localContentValues.put("target", parms[3]);
		localContentValues.put("distribution", parms[4]);
		localContentValues.put("each_id", parms[5]);
		localContentValues.put("packing", parms[6]);
		paramSQLiteDatabase.insert("l_monthstarget_goods", null,
				localContentValues);
		paramSQLiteDatabase.close();
	}

	public static HashMap<String, Object> getDataFromMonthstargetByMonths(
			SQLiteDatabase paramSQLiteDatabase, String paramString) {
		// + "id NTEXT,"
		// // + "date NTEXT,"
		// // + "goods_id NTEXT)");
		String[] arrayOfString = new String[1];
		arrayOfString[0] = "id,date,goods_id";
		Cursor localCursor = paramSQLiteDatabase.query("l_monthstarget",
				arrayOfString, "date='" + paramString + "'", null, null, null,
				null, null);
		HashMap<String, Object> localMap = new HashMap<String, Object>();
		localCursor.moveToFirst();
		while (!localCursor.isAfterLast()) {
			String id = localCursor.getString(0);
			String date = localCursor.getString(1);
			String goods_id = localCursor.getString(2);
			localMap.put("id", id);
			localMap.put("date", date);
			localMap.put("goods_id", goods_id);

			break;
			// localCursor.moveToNext();
		}
		localCursor.close();
		paramSQLiteDatabase.close();
		return localMap;
	}

	public static ArrayList<GoodsMonthTargetBean> getDataFromMonthstargetGoodsByGoodsId(
			SQLiteDatabase paramSQLiteDatabase, String paramString) {
		// localContentValues.put("id", parms[0]);
		// localContentValues.put("goods_id", parms[1]);
		// localContentValues.put("name", parms[2]);
		// localContentValues.put("target", parms[3]);
		// localContentValues.put("distribution", parms[4]);
		// localContentValues.put("each_id", parms[5]);
		// localContentValues.put("packing", parms[6]);
		String[] arrayOfString = new String[1];
		arrayOfString[0] = "id,goods_id,name,target,distribution,each_id,packing";
		Cursor cursor = paramSQLiteDatabase.query("l_monthstarget_goods",
				arrayOfString, "goods_id='" + paramString + "'", null, null,
				null, null, null);
		ArrayList<GoodsMonthTargetBean> arrayList = new ArrayList<GoodsMonthTargetBean>();
		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			GoodsMonthTargetBean ib = new GoodsMonthTargetBean();

			ib.setName(cursor.getString(2));
			ib.setTarget(cursor.getString(3));
			ib.setDistribution(cursor.getString(4));
			ib.setEach_id(cursor.getString(5));
			ib.setPacking(cursor.getString(6));
			arrayList.add(ib);
			cursor.moveToNext();
		}
		cursor.close();
		paramSQLiteDatabase.close();
		return arrayList;
	}

	// 删除
	public static void deleteLl_monthstarget(SQLiteDatabase paramSQLiteDatabase) {
		paramSQLiteDatabase.delete("l_monthstarget", null, null);
		paramSQLiteDatabase.close();
	}

	public static void deletel_monthstarget_goods(
			SQLiteDatabase paramSQLiteDatabase) {
		paramSQLiteDatabase.delete("l_monthstarget_goods", null, null);
		paramSQLiteDatabase.close();
	}

	// 公告通知
	// 插入列表
	public static void insertLbulletin(SQLiteDatabase paramSQLiteDatabase,
			String... parms) {

		try {
			ContentValues localContentValues = new ContentValues();
			localContentValues.put("b_id", parms[0]);
			localContentValues.put("b_name", parms[1]);
			localContentValues.put("b_release_date", parms[2]);
			localContentValues.put("b_code", parms[3]);
			localContentValues.put("is_top", parms[4]);
			localContentValues.put("is_read", parms[5]);

			paramSQLiteDatabase.insert("l_bulletin", null, localContentValues);
			paramSQLiteDatabase.close();
		} catch (Exception e) {
			FileLog.e(TAG, "insert bulletin db:" + e);
		}
	}

	// 删除未读的
	public static void deleteLbulletinNoRead(SQLiteDatabase paramSQLiteDatabase) {

		paramSQLiteDatabase
				.delete("l_bulletin", "is_read!='" + "1" + "'", null);
		paramSQLiteDatabase.close();
	}

	// 删除所有的
	public static void deleteLbulletinAll(SQLiteDatabase paramSQLiteDatabase) {

		paramSQLiteDatabase.delete("l_bulletin", null, null);
		paramSQLiteDatabase.close();
	}

	// 删除掉有的id
	public static void deleteLbulletinHasRepeat(
			SQLiteDatabase paramSQLiteDatabase, String id) {

		paramSQLiteDatabase.delete("l_bulletin", "b_id='" + id + "'", null);
		paramSQLiteDatabase.close();
	}

	//
	public static boolean checkHasTheId(SQLiteDatabase paramSQLiteDatabase,
			String id) {
		ArrayList<BulletinBean> arrayList = new ArrayList<BulletinBean>();
		int size = 0;
		try {
			String[] arrayOfString = new String[1];
			arrayOfString[0] = "b_id";
			Cursor cursor = paramSQLiteDatabase.query("l_bulletin",
					arrayOfString, "b_id='" + id + "'", null, null, null, null,
					null);
			cursor.moveToFirst();
			size = cursor.getCount();

			cursor.close();
			paramSQLiteDatabase.close();
		} catch (Exception e) {

		}
		if (size > 0) {
			return true;

		} else {
			return false;

		}

	}

	public static BulletinBean getDataFromLbulletinById(
			SQLiteDatabase paramSQLiteDatabase, String id) {
		// db.execSQL("create table if not exists l_bulletin("
		// + "b_id NTEXT PRIMARY KEY," + "b_name NTEXT,"
		// + "b_release_date NTEXT," + "b_code NTEXT," + "is_top NTEXT,"
		// + "b_fail_date NTEXT," + "b_remark NTEXT," + "b_type NTEXT,"
		// + "u_name NTEXT," + "b_appendix NTEXT,"
		// + "b_appendix_title NTEXT," + "b_appendix_size NTEXT,"
		// + "is_read NTEXT)");
		BulletinBean bb = new BulletinBean();
		try {
			String[] arrayOfString = new String[1];
			arrayOfString[0] = "b_id,b_name,b_release_date,b_code,is_top,b_fail_date,b_remark,b_type,u_name"
					+ ",b_appendix,b_appendix_title,b_appendix_size,is_read";
			Cursor cursor = paramSQLiteDatabase.query("l_bulletin",
					arrayOfString, "b_id='" + id + "'", null, null, null,
					"is_top, b_release_date DESC", null);
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				String b_id = cursor.getString(0);
				String b_name = cursor.getString(1);
				String b_release_date = cursor.getString(2);
				String b_code = cursor.getString(3);
				String is_top = cursor.getString(4);
				String b_fail_date = cursor.getString(5);
				String b_remark = cursor.getString(6);
				String b_type = cursor.getString(7);
				String u_name = cursor.getString(8);
				String b_appendix = cursor.getString(9);
				String b_appendix_title = cursor.getString(10);
				String b_appendix_size = cursor.getString(11);
				String is_read = cursor.getString(12);

				bb.setB_id(b_id);
				bb.setB_name(b_name);
				bb.setB_release_date(b_release_date);
				bb.setB_code(b_code);
				bb.setIs_top(is_top);
				bb.setB_fail_date(b_fail_date);
				bb.setB_remark(b_remark);
				bb.setB_type(b_type);
				bb.setU_name(u_name);
				bb.setB_appendix(b_appendix);
				bb.setB_appendix_size(b_appendix_size);
				bb.setB_appendix_title(b_appendix_title);
				bb.setIs_read(is_read);

				break;
				// localCursor.moveToNext();
			}
			cursor.close();
			paramSQLiteDatabase.close();
		} catch (Exception e) {
			FileLog.e(TAG, "db error:" + e);
		}
		return bb;
	}

	// 获得列表数据
	public static ArrayList<BulletinBean> getDataFromLbulletin(
			SQLiteDatabase paramSQLiteDatabase,String key) {
		ArrayList<BulletinBean> arrayList = new ArrayList<BulletinBean>();
		try {
			String[] arrayOfString = new String[1];
			arrayOfString[0] = "b_id,b_name,b_release_date,b_code,is_top,is_read";
			Cursor cursor = paramSQLiteDatabase.query("l_bulletin",
					arrayOfString, "b_name like '%" + key + "%'", null, null, null,
					"is_top DESC, b_release_date DESC", null);
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				BulletinBean ib = new BulletinBean();
				ib.setB_id(cursor.getString(0));
				ib.setB_name(cursor.getString(1));
				ib.setB_release_date(cursor.getString(2));
				ib.setB_code(cursor.getString(3));
				ib.setIs_top(cursor.getString(4));
				ib.setIs_read(cursor.getString(5));

				arrayList.add(ib);

				cursor.moveToNext();
			}
			cursor.close();
			paramSQLiteDatabase.close();
		} catch (Exception e) {
			FileLog.e(TAG, "db error:" + e);
		}
		return arrayList;
	}

	public static void updateLbulletin(SQLiteDatabase paramSQLiteDatabase,
			String... prams) {

		paramSQLiteDatabase
				.execSQL(
						"update l_bulletin set b_code = ?, b_name=?, b_release_date = ?, b_fail_date = ?, b_remark = ?,"
								+ " b_type = ?, u_name = ?, b_appendix=?, b_appendix_title=?, b_appendix_size=?  where b_id = ? ",
						new Object[] { prams[0], prams[1], prams[2], prams[3],
								prams[4], prams[5], prams[6], prams[7],
								prams[8], prams[9], prams[10] });

		paramSQLiteDatabase.close();
	}

	public static void updateLbulletinToRead(
			SQLiteDatabase paramSQLiteDatabase, String... prams) {

		paramSQLiteDatabase.execSQL(
				"update l_bulletin set  is_read=?  where b_id = ? ",
				new Object[] { prams[0], prams[1] });

		paramSQLiteDatabase.close();
	}

	// 知识库
	// 获得列表数据
	public static ArrayList<KnowledgeBean> getDataFromLknowledgebase(
			SQLiteDatabase paramSQLiteDatabase) {
		ArrayList<KnowledgeBean> arrayList = new ArrayList<KnowledgeBean>();
		try {
			String[] arrayOfString = new String[1];
			arrayOfString[0] = "k_id,k_name,k_type,k_fatherid,k_appendix,k_appendix_title,k_appendix_size,is_read,k_code";
			Cursor cursor = paramSQLiteDatabase.query("l_knowledgebase",
					arrayOfString, null, null, null, null, "k_type DESC", null);
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				KnowledgeBean ib = new KnowledgeBean();
				ib.setK_id(cursor.getString(0));
				ib.setK_name(cursor.getString(1));
				ib.setK_type(cursor.getString(2));
				ib.setK_fatherid(cursor.getString(3));
				ib.setK_appendix(cursor.getString(4));
				ib.setK_appendix_title(cursor.getString(5));
				ib.setK_appendix_size(cursor.getString(6));
				ib.setIs_read(cursor.getString(7));
				ib.setK_code(cursor.getString(8));
				arrayList.add(ib);

				cursor.moveToNext();
			}
			cursor.close();
			paramSQLiteDatabase.close();
		} catch (Exception e) {
			FileLog.e(TAG, "db l_knowledgebase error:" + e);
		}
		return arrayList;
	}

	// 获得列表数据
	public static ArrayList<KnowledgeBean> getDataFromLknowledgebase(
			SQLiteDatabase paramSQLiteDatabase, String fatherid) {
		ArrayList<KnowledgeBean> arrayList = new ArrayList<KnowledgeBean>();
		try {
			String[] arrayOfString = new String[1];
			arrayOfString[0] = "k_id,k_name,k_type,k_fatherid,k_appendix,k_appendix_title,k_appendix_size,is_read,k_code";
			Cursor cursor = paramSQLiteDatabase.query("l_knowledgebase",
					arrayOfString, "k_fatherid='" + fatherid + "'", null, null,
					null, "k_type DESC", null);
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				KnowledgeBean ib = new KnowledgeBean();
				ib.setK_id(cursor.getString(0));
				ib.setK_name(cursor.getString(1));
				ib.setK_type(cursor.getString(2));
				ib.setK_fatherid(cursor.getString(3));
				ib.setK_appendix(cursor.getString(4));
				ib.setK_appendix_title(cursor.getString(5));
				ib.setK_appendix_size(cursor.getString(6));
				ib.setIs_read(cursor.getString(7));
				ib.setK_code(cursor.getString(8));
				arrayList.add(ib);

				cursor.moveToNext();
			}
			cursor.close();
			paramSQLiteDatabase.close();
		} catch (Exception e) {
			FileLog.e(TAG, "db l_knowledgebase error:" + e);
		}
		return arrayList;
	}

	// 插入列表
	public static void insertLknowledgebase(SQLiteDatabase paramSQLiteDatabase,
			String... parms) {
		// k_id,k_name,k_type,k_fatherid,k_appendix,k_appendix_title,k_appendix_size,is_read,k_code
		try {
			ContentValues localContentValues = new ContentValues();
			localContentValues.put("k_id", parms[0]);
			localContentValues.put("k_name", parms[1]);
			localContentValues.put("k_type", parms[2]);
			localContentValues.put("k_fatherid", parms[3]);
			localContentValues.put("k_appendix", parms[4]);
			localContentValues.put("k_appendix_title", parms[5]);
			localContentValues.put("k_appendix_size", parms[6]);
			localContentValues.put("is_read", parms[7]);
			localContentValues.put("k_code", parms[8]);

			paramSQLiteDatabase.insert("l_knowledgebase", null,
					localContentValues);
			paramSQLiteDatabase.close();
		} catch (Exception e) {
			FileLog.e(TAG, "insert l_knowledgebase db:" + e);
		}
	}
	
	public static void updateLknowledgebasecode(
			SQLiteDatabase paramSQLiteDatabase, String... prams) {

		paramSQLiteDatabase.execSQL(
				"update l_knowledgebase set  k_code=?  where k_id = ? ",
				new Object[] { prams[0], prams[1] });

		paramSQLiteDatabase.close();
	}
	public static void updateLknowledgebaseToRead(
			SQLiteDatabase paramSQLiteDatabase, String... prams) {

		paramSQLiteDatabase.execSQL(
				"update l_knowledgebase set  is_read=?  where k_id = ? ",
				new Object[] { prams[0], prams[1] });

		paramSQLiteDatabase.close();
	}

	// 删除所有的
	public static void deleteLknowledgebaseAll(
			SQLiteDatabase paramSQLiteDatabase) {

		paramSQLiteDatabase.delete("l_knowledgebase", null, null);
		paramSQLiteDatabase.close();
	}

	// 知识库详情
	// 删除掉有的id
	public static void deleteLknowledgeDetail(
			SQLiteDatabase paramSQLiteDatabase, String id) {

		paramSQLiteDatabase.delete("l_knowledgebasedetail",
				"b_id='" + id + "'", null);
		paramSQLiteDatabase.close();
	}

	public static void insertLknowledgeDetail(SQLiteDatabase paramSQLiteDatabase,
			String... parms) {
		try {
			ContentValues localContentValues = new ContentValues();
			localContentValues.put("b_id", parms[0]);
			localContentValues.put("b_name", parms[1]);
			localContentValues.put("b_fail_date", parms[2]);
			localContentValues.put("b_remark", parms[3]);
			localContentValues.put("b_appendix", parms[4]);
			localContentValues.put("b_appendix_title", parms[5]);
			localContentValues.put("b_appendix_size", parms[6]);

			paramSQLiteDatabase.insert("l_knowledgebasedetail", null, localContentValues);
			paramSQLiteDatabase.close();
		} catch (Exception e) {
			FileLog.e(TAG, "insert l_knowledgebasedetail db:" + e);
		}
	}
	public static BulletinBean getDataFromLknowledgebasedetailById(
			SQLiteDatabase paramSQLiteDatabase, String id) {
		BulletinBean bb = new BulletinBean();
		try {
			String[] arrayOfString = new String[1];
			arrayOfString[0] = "b_id,b_name,b_release_date,b_code,is_top,b_fail_date,b_remark,b_type,u_name"
					+ ",b_appendix,b_appendix_title,b_appendix_size,is_read";
			Cursor cursor = paramSQLiteDatabase.query("l_knowledgebasedetail",
					arrayOfString, "b_id='" + id + "'", null, null, null,
					null, null);
			cursor.moveToFirst();
			while (!cursor.isAfterLast()) {
				String b_id = cursor.getString(0);
				String b_name = cursor.getString(1);
				String b_release_date = cursor.getString(2);
				String b_code = cursor.getString(3);
				String is_top = cursor.getString(4);
				String b_fail_date = cursor.getString(5);
				String b_remark = cursor.getString(6);
				String b_type = cursor.getString(7);
				String u_name = cursor.getString(8);
				String b_appendix = cursor.getString(9);
				String b_appendix_title = cursor.getString(10);
				String b_appendix_size = cursor.getString(11);
				String is_read = cursor.getString(12);

				bb.setB_id(b_id);
				bb.setB_name(b_name);
				bb.setB_release_date(b_release_date);
				bb.setB_code(b_code);
				bb.setIs_top(is_top);
				bb.setB_fail_date(b_fail_date);
				bb.setB_remark(b_remark);
				bb.setB_type(b_type);
				bb.setU_name(u_name);
				bb.setB_appendix(b_appendix);
				bb.setB_appendix_size(b_appendix_size);
				bb.setB_appendix_title(b_appendix_title);
				bb.setIs_read(is_read);

				break;
				// localCursor.moveToNext();
			}
			cursor.close();
			paramSQLiteDatabase.close();
		} catch (Exception e) {
			FileLog.e(TAG, "db error:" + e);
		}
		return bb;
	}

}
