package com.eastelsoft.lbs.db;

import java.util.ArrayList;
import java.util.List;

import com.eastelsoft.lbs.bean.DealerDto;
import com.eastelsoft.lbs.bean.DealerDto.DealerBean;
import com.eastelsoft.lbs.db.table.DealerTable;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class DealerDBTask {
	
	private DealerDBTask() {}
	
	public static SQLiteDatabase getWsd() {
		return LocationSQLiteHelper.getInstance().getWritableDatabase();
	}
	
	public static SQLiteDatabase getRsd() {
		return LocationSQLiteHelper.getInstance().getReadableDatabase();
	}
	
	public static DBResult addBean(DealerBean bean) {
		ContentValues values = new ContentValues();
		values.put(DealerTable.ID, bean.id);
		values.put(DealerTable.NAME, bean.name);
		values.put(DealerTable.TELEPHONE, bean.telephone);
		values.put(DealerTable.GROUP_ID, bean.group_id);
		values.put(DealerTable.GROUP_NAME, bean.group_name);
		values.put(DealerTable.REMARK, bean.remark);
		values.put(DealerTable.PY_INDEX, bean.py_index);
		values.put(DealerTable.PY_NAME, bean.py_name);
		
		getWsd().insert(DealerTable.TABLE_NAME, DealerTable.ID, values);
		return DBResult.add_successfully;
	}
	
	public static DBResult deleteBean(String id) {
		String sql = "delete from "+DealerTable.TABLE_NAME+" where id = ?";
		getWsd().execSQL(sql, new String[]{id});
		
		return DBResult.delete_successfully;
	}
	
	public static DBResult deleteAll() {
		String sql = "delete from "+DealerTable.TABLE_NAME;
		getWsd().execSQL(sql);
		
		return DBResult.delete_successfully;
	}
	
	public static DealerBean getBeanById(String id) {
		String sql = "select * from "+DealerTable.TABLE_NAME + " where id = ?";
		Cursor c = getRsd().rawQuery(sql, new String[]{id});
		if (c.moveToNext()) {
			DealerBean bean = new DealerDto().new DealerBean();
			bean.id = c.getString(c.getColumnIndex(DealerTable.ID));
			bean.name = c.getString(c.getColumnIndex(DealerTable.NAME));
			bean.telephone = c.getString(c.getColumnIndex(DealerTable.TELEPHONE));
			bean.group_id = c.getString(c.getColumnIndex(DealerTable.GROUP_ID));
			bean.group_name = c.getString(c.getColumnIndex(DealerTable.GROUP_NAME));
			bean.remark = c.getString(c.getColumnIndex(DealerTable.REMARK));
			bean.py_index = c.getString(c.getColumnIndex(DealerTable.PY_INDEX));
			bean.py_name = c.getString(c.getColumnIndex(DealerTable.PY_NAME));
			return bean;
		}
		return null;
	}
	
	public static List<DealerBean> getBeanList() {
		List<DealerBean> plist = new ArrayList<DealerBean>();
		String sql = "select * from "+DealerTable.TABLE_NAME;
		Cursor c = getRsd().rawQuery(sql, null);
		while (c.moveToNext()) {
			DealerBean bean = new DealerDto().new DealerBean();
			bean.id = c.getString(c.getColumnIndex(DealerTable.ID));
			bean.name = c.getString(c.getColumnIndex(DealerTable.NAME));
			bean.telephone = c.getString(c.getColumnIndex(DealerTable.TELEPHONE));
			bean.group_id = c.getString(c.getColumnIndex(DealerTable.GROUP_ID));
			bean.group_name = c.getString(c.getColumnIndex(DealerTable.GROUP_NAME));
			bean.remark = c.getString(c.getColumnIndex(DealerTable.REMARK));
			bean.py_index = c.getString(c.getColumnIndex(DealerTable.PY_INDEX));
			bean.py_name = c.getString(c.getColumnIndex(DealerTable.PY_NAME));
			plist.add(bean);
		}
		return plist;
	}

}
