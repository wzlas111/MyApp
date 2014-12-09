package com.eastelsoft.lbs.db;

import java.util.ArrayList;
import java.util.List;

import com.eastelsoft.lbs.bean.VisitBean;
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

}
