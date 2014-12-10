package com.eastelsoft.lbs.db;

import java.util.ArrayList;
import java.util.List;

import com.eastelsoft.lbs.bean.VisitBean;
import com.eastelsoft.lbs.db.table.DealerTable;
import com.eastelsoft.lbs.db.table.VisitTable;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class VisitDBTask {
	
	private VisitDBTask() {}
	
	public static SQLiteDatabase getWsd() {
		return LocationSQLiteHelper.getInstance().getWritableDatabase();
	}
	
	public static SQLiteDatabase getRsd() {
		return LocationSQLiteHelper.getInstance().getReadableDatabase();
	}
	
	public static DBResult deleteBean(String id) {
		String sql = "delete from "+VisitTable.TABLE_NAME+" where id = ?";
		getWsd().execSQL(sql, new String[]{id});
		
		return DBResult.delete_successfully;
	}
	
	public static DBResult deleteAll() {
		String sql = "delete from "+VisitTable.TABLE_NAME;
		getWsd().execSQL(sql);
		
		return DBResult.delete_successfully;
	}
	
	public static VisitBean getBeanById(String id) {
		String sql = "select * from "+VisitTable.TABLE_NAME + " where id = ?";
		Cursor c = getRsd().rawQuery(sql, new String[]{id});
		if (c.moveToNext()) {
			VisitBean bean = new VisitBean();
			bean.id = c.getString(c.getColumnIndex(VisitTable.ID));
			bean.plan_id = c.getString(c.getColumnIndex(VisitTable.PLAN_ID));
			bean.plan_name = c.getString(c.getColumnIndex(VisitTable.PLAN_NAME));
			bean.dealer_id = c.getString(c.getColumnIndex(VisitTable.DEALER_ID));
			bean.dealer_name = c.getString(c.getColumnIndex(VisitTable.DEALER_NAME));
			bean.start_time = c.getString(c.getColumnIndex(VisitTable.START_TIME));
			bean.start_location = c.getString(c.getColumnIndex(VisitTable.START_LOCATION));
			bean.start_lon = c.getString(c.getColumnIndex(VisitTable.START_LON));
			bean.start_lat = c.getString(c.getColumnIndex(VisitTable.START_LAT));
			bean.arrive_time = c.getString(c.getColumnIndex(VisitTable.ARRIVE_TIME));
			bean.arrive_location = c.getString(c.getColumnIndex(VisitTable.ARRIVE_LOCATION));
			bean.arrive_lon = c.getString(c.getColumnIndex(VisitTable.ARRIVE_LON));
			bean.arrive_lat = c.getString(c.getColumnIndex(VisitTable.ARRIVE_LAT));
			bean.status = c.getString(c.getColumnIndex(VisitTable.STATUS));
			return bean;
		}
		return null;
	}
	
	public static List<VisitBean> getBeanList() {
		List<VisitBean> plist = new ArrayList<VisitBean>();
		String sql = "select * from "+VisitTable.TABLE_NAME + " order by status,start_time desc";
		Cursor c = getRsd().rawQuery(sql, null);
		while (c.moveToNext()) {
			VisitBean bean = new VisitBean();
			bean.id = c.getString(c.getColumnIndex(VisitTable.ID));
			bean.dealer_name = c.getString(c.getColumnIndex(VisitTable.DEALER_NAME));
			bean.start_time = c.getString(c.getColumnIndex(VisitTable.START_TIME));
			bean.status = c.getString(c.getColumnIndex(VisitTable.STATUS));
			plist.add(bean);
		}
		return plist;
	}
	
	public static DBResult addStartBean(VisitBean bean) {
		ContentValues values = new ContentValues();
		values.put(VisitTable.ID, bean.id);
		values.put(VisitTable.PLAN_ID, bean.plan_id);
		values.put(VisitTable.PLAN_NAME, bean.plan_name);
		values.put(VisitTable.DEALER_ID, bean.dealer_id);
		values.put(VisitTable.DEALER_NAME, bean.dealer_name);
		values.put(VisitTable.START_TIME, bean.start_time);
		values.put(VisitTable.START_LOCATION, bean.start_location);
		values.put(VisitTable.START_LON, bean.start_lon);
		values.put(VisitTable.START_LAT, bean.start_lat);
		values.put(VisitTable.STATUS, bean.status);
		
		getWsd().insert(VisitTable.TABLE_NAME, VisitTable.ID, values);
		
		return DBResult.add_successfully;
	}
	
	public static DBResult updateArriveBean(VisitBean bean) {
		ContentValues values = new ContentValues();
		values.put(VisitTable.ARRIVE_TIME, bean.arrive_time);
		values.put(VisitTable.ARRIVE_LOCATION, bean.arrive_location);
		values.put(VisitTable.ARRIVE_LON, bean.arrive_lon);
		values.put(VisitTable.ARRIVE_LAT, bean.arrive_lat);
		values.put(VisitTable.STATUS, bean.status);
		
		getWsd().update(VisitTable.TABLE_NAME, values, "id=?", new String[]{bean.id});
		
		return DBResult.update_successfully;
	}

}
