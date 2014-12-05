package com.eastelsoft.lbs.db;

import java.util.ArrayList;
import java.util.List;

import com.eastelsoft.lbs.bean.ClientDto;
import com.eastelsoft.lbs.bean.DealerDto;
import com.eastelsoft.lbs.bean.ClientDto.ClientBean;
import com.eastelsoft.lbs.bean.DealerDto.DealerBean;
import com.eastelsoft.lbs.db.table.ClientTable;
import com.eastelsoft.lbs.db.table.DealerTable;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ClientDBTask {
	
	private ClientDBTask() {}
	
	public static SQLiteDatabase getWsd() {
		return LocationSQLiteHelper.getInstance().getWritableDatabase();
	}
	
	public static SQLiteDatabase getRsd() {
		return LocationSQLiteHelper.getInstance().getReadableDatabase();
	}
	
	public static List<ClientBean> getBeanList() {
		List<ClientBean> plist = new ArrayList<ClientBean>();
		String sql = "select * from " + ClientTable.TABLE_NAME;
		Cursor c = getRsd().rawQuery(sql, null);
		while (c.moveToNext()) {
			ClientBean bean = new ClientDto().new ClientBean();
			bean.id = c.getString(c.getColumnIndex(ClientTable.ID));
			bean.name = c.getString(c.getColumnIndex(ClientTable.CLIENT_NAME));
			bean.py = c.getString(c.getColumnIndex(ClientTable.PY));
			bean.is_upload = c.getString(c.getColumnIndex(ClientTable.IS_UPLOAD));
			plist.add(bean);
		}
		return plist;
	}
	
	public static DBResult addBeanList(List<ClientBean> pList) {
		try {
			getWsd().beginTransaction();
			for (ClientBean bean : pList) {
				ContentValues values = new ContentValues();
				values.put(ClientTable.ID, bean.id);
				values.put(ClientTable.CLIENT_NAME, bean.name);
				values.put(ClientTable.PY, bean.py);
				values.put(ClientTable.IS_UPLOAD, "1");
				
				getWsd().insert(ClientTable.TABLE_NAME, null, values);
			}
			getWsd().setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		}
		getWsd().endTransaction();
		return DBResult.add_successfully;
	}
	
	public static DBResult deleteAll() {
		String sql = "delete from "+ClientTable.TABLE_NAME;
		getWsd().execSQL(sql);
		
		return DBResult.delete_successfully;
	}
	
	public static DBResult deleteBean(String id) {
		String sql = "delete from "+DealerTable.TABLE_NAME+" where id = ?";
		getWsd().execSQL(sql, new String[]{id});
		
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
	
}
