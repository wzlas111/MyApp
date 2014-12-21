package com.eastelsoft.lbs.db;

import java.util.ArrayList;
import java.util.List;

import com.eastelsoft.lbs.bean.VisitBean;
import com.eastelsoft.lbs.bean.VisitMcBean;
import com.eastelsoft.lbs.db.table.VisitMcTable;
import com.eastelsoft.lbs.db.table.VisitTable;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class VisitMcDBTask {

	private VisitMcDBTask() {}
	
	public static SQLiteDatabase getWsd() {
		return LocationSQLiteHelper.getInstance().getWritableDatabase();
	}
	
	public static SQLiteDatabase getRsd() {
		return LocationSQLiteHelper.getInstance().getReadableDatabase();
	}
	
	public static List<VisitMcBean> getBeanList(String visit_id) {
		List<VisitMcBean> plist = new ArrayList<VisitMcBean>();
		String sql = "select * from "+VisitMcTable.TABLE_NAME + " where visit_id = ? order by start_time";
		Cursor c = getRsd().rawQuery(sql, new String[]{visit_id});
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
			bean.is_upload = c.getString(c.getColumnIndex(VisitMcTable.IS_UPLOAD));
			plist.add(bean);
		}
		return plist;
	}
	
	public static DBResult addBean(VisitMcBean bean) {
		ContentValues values = new ContentValues();
		values.put(VisitMcTable.ID, bean.id);
		values.put(VisitMcTable.VISIT_ID, bean.visit_id);
		values.put(VisitMcTable.CLIENT_ID, bean.client_id);
		values.put(VisitMcTable.CLIENT_NAME, bean.client_name);
		values.put(VisitMcTable.START_TIME, bean.start_time);
		values.put(VisitMcTable.END_TIME, bean.end_time);
		values.put(VisitMcTable.SERVICE_START_TIME, bean.service_start_time);
		values.put(VisitMcTable.SERVICE_END_TIME, bean.service_end_time);
		values.put(VisitMcTable.IS_REPAIR, bean.is_repair);
		values.put(VisitMcTable.CLIENT_SIGN, bean.client_sign);
		values.put(VisitMcTable.UPLOAD_IMG, bean.upload_img);
		values.put(VisitMcTable.UPLOAD_IMG_NUM, bean.upload_img_num);
		values.put(VisitMcTable.IS_UPLOAD, bean.is_upload);
		
		values.put(VisitMcTable.MC_REGISTER_JSON, bean.mc_register_json);
		values.put(VisitMcTable.MC_TYPE_JSON, bean.mc_type_json);
		values.put(VisitMcTable.MC_PERSON_JSON, bean.mc_person_json);
		values.put(VisitMcTable.MC_INFO_JSON, bean.mc_info_json);
		
		getWsd().insert(VisitMcTable.TABLE_NAME, VisitMcTable.ID, values);
		
		return DBResult.add_successfully;
	}
	
	public static DBResult updateIsUploadBean(VisitMcBean bean) {
		ContentValues values = new ContentValues();
		values.put(VisitMcTable.IS_UPLOAD, bean.is_upload);
		
		getWsd().update(VisitMcTable.TABLE_NAME, values, "id=?", new String[]{bean.id});
		
		return DBResult.update_successfully;
	}
}
