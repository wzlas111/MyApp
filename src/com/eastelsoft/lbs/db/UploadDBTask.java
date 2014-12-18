package com.eastelsoft.lbs.db;

import java.util.List;

import com.eastelsoft.lbs.bean.UploadImgBean;
import com.eastelsoft.lbs.bean.VisitBean;
import com.eastelsoft.lbs.db.table.UploadImgTable;
import com.eastelsoft.lbs.db.table.VisitTable;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;

public class UploadDBTask {
	
	private UploadDBTask() {}
	
	public static SQLiteDatabase getWsd() {
		return LocationSQLiteHelper.getInstance().getWritableDatabase();
	}
	
	public static SQLiteDatabase getRsd() {
		return LocationSQLiteHelper.getInstance().getReadableDatabase();
	}
	
	public static DBResult addImgBeanList(List<UploadImgBean> pList) {
		getWsd().beginTransaction();
		try {
			for (UploadImgBean bean : pList) {
				ContentValues values = new ContentValues();
				values.put(UploadImgTable.ID, bean.id);
				values.put(UploadImgTable.DATA_ID, bean.data_id);
				values.put(UploadImgTable.NAME, bean.name);
				values.put(UploadImgTable.PATH, bean.path);
				values.put(UploadImgTable.TYPE, bean.type);
				
				getWsd().insert(UploadImgTable.TABLE_NAME, UploadImgTable.ID, values);
			}
			getWsd().setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		}
		getWsd().endTransaction();
		return DBResult.add_successfully;
	}
	
	public static DBResult deleteImgBean(String id) {
		getWsd().delete(UploadImgTable.TABLE_NAME, "id=?", new String[]{id});
		return DBResult.delete_successfully;
	}

}
