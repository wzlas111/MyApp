package com.eastelsoft.lbs.db;

import java.util.ArrayList;
import java.util.List;

import com.eastelsoft.lbs.bean.VisitEvaluateBean;
import com.eastelsoft.lbs.db.table.VisitEvaluateTable;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class VisitEvaluateDBTask {

	private VisitEvaluateDBTask() {}
	
	public static SQLiteDatabase getWsd() {
		return LocationSQLiteHelper.getInstance().getWritableDatabase();
	}
	
	public static SQLiteDatabase getRsd() {
		return LocationSQLiteHelper.getInstance().getReadableDatabase();
	}
	
	public static VisitEvaluateBean getBeanByVisitId(String visit_id) {
		String sql = "select * from "+VisitEvaluateTable.TABLE_NAME + " where visit_id = ? ";
		Cursor c = getRsd().rawQuery(sql, new String[]{visit_id});
		if (c.moveToNext()) {
			VisitEvaluateBean bean = new VisitEvaluateBean();
			bean.id = c.getString(c.getColumnIndex(VisitEvaluateTable.ID));
			bean.visit_id = c.getString(c.getColumnIndex(VisitEvaluateTable.VISIT_ID));
			bean.visit_num = c.getString(c.getColumnIndex(VisitEvaluateTable.VISIT_NUM));
			bean.service_name = c.getString(c.getColumnIndex(VisitEvaluateTable.SERVICE_NAME));
			bean.service_value = c.getString(c.getColumnIndex(VisitEvaluateTable.SERVICE_VALUE));
			bean.other_job = c.getString(c.getColumnIndex(VisitEvaluateTable.OTHER_JOB));
			bean.advise = c.getString(c.getColumnIndex(VisitEvaluateTable.ADVISE));
			bean.client_sign = c.getString(c.getColumnIndex(VisitEvaluateTable.CLIENT_SIGN));
			bean.is_upload = c.getString(c.getColumnIndex(VisitEvaluateTable.IS_UPLOAD));
			return bean;
		}
		return null;
	}
	
	public static DBResult addBean(VisitEvaluateBean bean) {
		ContentValues values = new ContentValues();
		values.put(VisitEvaluateTable.ID, bean.id);
		values.put(VisitEvaluateTable.VISIT_ID, bean.visit_id);
		values.put(VisitEvaluateTable.VISIT_NUM, bean.visit_num);
		values.put(VisitEvaluateTable.SERVICE_NAME, bean.service_name);
		values.put(VisitEvaluateTable.SERVICE_VALUE, bean.service_value);
		values.put(VisitEvaluateTable.OTHER_JOB, bean.other_job);
		values.put(VisitEvaluateTable.ADVISE, bean.advise);
		values.put(VisitEvaluateTable.CLIENT_SIGN, bean.client_sign);
		values.put(VisitEvaluateTable.IS_UPLOAD, bean.is_upload);
		
		getWsd().insert(VisitEvaluateTable.TABLE_NAME, VisitEvaluateTable.ID, values);
		
		return DBResult.add_successfully;
	}
}
