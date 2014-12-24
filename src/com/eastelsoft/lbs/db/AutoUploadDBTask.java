package com.eastelsoft.lbs.db;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.eastelsoft.lbs.bean.UploadImgBean;
import com.eastelsoft.lbs.bean.VisitBean;
import com.eastelsoft.lbs.bean.VisitEvaluateBean;
import com.eastelsoft.lbs.bean.VisitMcBean;
import com.eastelsoft.lbs.db.table.UploadImgTable;
import com.eastelsoft.lbs.db.table.VisitEvaluateTable;
import com.eastelsoft.lbs.db.table.VisitMcTable;
import com.eastelsoft.lbs.db.table.VisitTable;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class AutoUploadDBTask {
	
private AutoUploadDBTask() {}
	
	public static SQLiteDatabase getWsd() {
		return LocationSQLiteHelper.getInstance().getWritableDatabase();
	}
	
	public static SQLiteDatabase getRsd() {
		return LocationSQLiteHelper.getInstance().getReadableDatabase();
	}
	
	public static List<VisitBean> getVisitForm() {
		List<VisitBean> plist = new ArrayList<VisitBean>();
		String sql = "select * from "+VisitTable.TABLE_NAME + " where is_upload='0' and status='3' order by start_time";
		Cursor c = getRsd().rawQuery(sql, null);
		while (c.moveToNext()) {
			VisitBean bean = new VisitBean();
			bean.id = c.getString(c.getColumnIndex(VisitTable.ID));
			bean.service_begin_time = c.getString(c.getColumnIndex(VisitTable.SERVICE_BEGIN_TIME));
			bean.service_end_time = c.getString(c.getColumnIndex(VisitTable.SERVICE_END_TIME));
			bean.visit_img = c.getString(c.getColumnIndex(VisitTable.VISIT_IMG));
			bean.visit_img_num = c.getString(c.getColumnIndex(VisitTable.VISIT_IMG_NUM));
			bean.is_upload = c.getString(c.getColumnIndex(VisitTable.IS_UPLOAD));
			bean.status = c.getString(c.getColumnIndex(VisitTable.STATUS));
			plist.add(bean);
		}
		return plist;
	}
	
	public static List<VisitEvaluateBean> getEvaluate() {
		List<VisitEvaluateBean> plist = new ArrayList<VisitEvaluateBean>();
		String sql = "select * from "+VisitEvaluateTable.TABLE_NAME + " where is_upload = '0'";
		Cursor c = getRsd().rawQuery(sql, null);
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
			plist.add(bean);
		}
		return plist;
	}
	
	public static List<VisitMcBean> getMc() {
		List<VisitMcBean> plist = new ArrayList<VisitMcBean>();
		String sql = "select * from "+VisitMcTable.TABLE_NAME + " where is_upload='0' order by start_time";
		Cursor c = getRsd().rawQuery(sql, null);
		while (c.moveToNext()) {
			VisitMcBean bean = new VisitMcBean();
			bean.id = c.getString(c.getColumnIndex(VisitMcTable.ID));
			bean.visit_id = c.getString(c.getColumnIndex(VisitMcTable.VISIT_ID));
			bean.client_id = c.getString(c.getColumnIndex(VisitMcTable.CLIENT_ID));
			bean.client_name = c.getString(c.getColumnIndex(VisitMcTable.CLIENT_NAME));
			bean.start_time = c.getString(c.getColumnIndex(VisitMcTable.START_TIME));
			bean.end_time = c.getString(c.getColumnIndex(VisitMcTable.END_TIME));
			bean.service_start_time = c.getString(c.getColumnIndex(VisitMcTable.SERVICE_START_TIME));
			bean.service_end_time = c.getString(c.getColumnIndex(VisitMcTable.SERVICE_END_TIME));
			bean.is_repair = c.getString(c.getColumnIndex(VisitMcTable.IS_REPAIR));
			bean.mc_register_json = c.getString(c.getColumnIndex(VisitMcTable.MC_REGISTER_JSON));
			bean.mc_type_json = c.getString(c.getColumnIndex(VisitMcTable.MC_TYPE_JSON));
			bean.mc_person_json = c.getString(c.getColumnIndex(VisitMcTable.MC_PERSON_JSON));
			bean.mc_info_json = c.getString(c.getColumnIndex(VisitMcTable.MC_INFO_JSON));
			bean.client_sign = c.getString(c.getColumnIndex(VisitMcTable.CLIENT_SIGN));
			bean.upload_img = c.getString(c.getColumnIndex(VisitMcTable.UPLOAD_IMG));
			bean.upload_img_num = c.getString(c.getColumnIndex(VisitMcTable.UPLOAD_IMG_NUM));
			bean.is_upload = c.getString(c.getColumnIndex(VisitMcTable.IS_UPLOAD));
			plist.add(bean);
		}
		return plist;
	}
	
	public static List<UploadImgBean> getUploadImg() {
		List<UploadImgBean> plist = new ArrayList<UploadImgBean>();
		String sql = "select * from "+UploadImgTable.TABLE_NAME;
		Cursor c = getRsd().rawQuery(sql, null);
		while (c.moveToNext()) {
			UploadImgBean bean = new UploadImgBean();
			bean.id = c.getString(c.getColumnIndex(UploadImgTable.ID));
			bean.data_id = c.getString(c.getColumnIndex(UploadImgTable.DATA_ID));
			bean.type = c.getString(c.getColumnIndex(UploadImgTable.TYPE));
			bean.name = c.getString(c.getColumnIndex(UploadImgTable.NAME));
			bean.path = c.getString(c.getColumnIndex(UploadImgTable.PATH));
			plist.add(bean);
		}
		return plist;
	}
	
	public static DBResult updateVisitForm(String id) {
		ContentValues values = new ContentValues();
		values.put(VisitTable.IS_UPLOAD, "1");
		values.put(VisitTable.STATUS, "2");
		
		getWsd().update(VisitTable.TABLE_NAME, values, "id=?", new String[]{id});
		
		return DBResult.update_successfully;
	}
	
	public static DBResult updateMc(String id,String is_upload) {
		ContentValues values = new ContentValues();
		values.put(VisitMcTable.IS_UPLOAD, is_upload);
		
		getWsd().update(VisitMcTable.TABLE_NAME, values, "id=?", new String[]{id});
		
		return DBResult.update_successfully;
	}
	
	public static DBResult updateEvaluate(String id) {
		ContentValues values = new ContentValues();
		values.put(VisitTable.IS_UPLOAD, "1");
		
		getWsd().update(VisitEvaluateTable.TABLE_NAME, values, "id=?", new String[]{id});
		
		return DBResult.update_successfully;
	}

}
