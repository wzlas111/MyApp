package com.eastelsoft.lbs.db;

import java.util.ArrayList;
import java.util.List;

import com.eastelsoft.lbs.bean.ClientDetailBean;
import com.eastelsoft.lbs.bean.DealerDto;
import com.eastelsoft.lbs.bean.DealerDto.DealerBean;
import com.eastelsoft.lbs.db.table.ClientTable;
import com.eastelsoft.lbs.db.table.DealerTable;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class DealerDBTask {
	
	private DealerDBTask() {}
	
	public static SQLiteDatabase getWsd() {
		return LocationSQLiteHelper.getInstance().getWritableDatabase();
	}
	
	public static SQLiteDatabase getRsd() {
		return LocationSQLiteHelper.getInstance().getReadableDatabase();
	}
	
	public static DBResult addBeanList(List<DealerBean> pList) {
		getWsd().beginTransaction();
		try {
			for (DealerBean bean : pList) {
				ContentValues values = new ContentValues();
				values.put(DealerTable.ID, bean.id);
				values.put(DealerTable.DEALER_NAME, bean.dealer_name);
				values.put(DealerTable.PY_NAME, bean.first_py);
//				if (!TextUtils.isEmpty(bean.first_py)) {
//					values.put(DealerTable.PY_INDEX, bean.first_py.substring(0, 1));
//				} else {
//					values.put(DealerTable.PY_INDEX, "#");
//				}
				values.put(DealerTable.TYPE, bean.type);
				
				getWsd().insert(DealerTable.TABLE_NAME, DealerTable.ID, values);
			}
			getWsd().setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		}
		getWsd().endTransaction();
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
			bean.dealer_name = c.getString(c.getColumnIndex(DealerTable.DEALER_NAME));
			bean.dealer_code = c.getString(c.getColumnIndex(DealerTable.DEALER_CODE));
			bean.parent_dealer_name = c.getString(c.getColumnIndex(DealerTable.PARENT_DEALER_NAME));
			bean.region_name = c.getString(c.getColumnIndex(DealerTable.REGION_NAME));
			bean.type_name = c.getString(c.getColumnIndex(DealerTable.TYPE_NAME));
			bean.contact_person = c.getString(c.getColumnIndex(DealerTable.CONTACT_PERSON));
			bean.contact_phone = c.getString(c.getColumnIndex(DealerTable.CONTACT_PHONE));
			bean.fax = c.getString(c.getColumnIndex(DealerTable.FAX));
			bean.address = c.getString(c.getColumnIndex(DealerTable.ADDRESS));
			bean.lon = c.getString(c.getColumnIndex(DealerTable.LON));
			bean.lat = c.getString(c.getColumnIndex(DealerTable.LAT));
			bean.accuracy = c.getString(c.getColumnIndex(DealerTable.ACCURACY));
			bean.remark = c.getString(c.getColumnIndex(DealerTable.REMARK));
			bean.py_index = c.getString(c.getColumnIndex(DealerTable.PY_INDEX));
			bean.first_py = c.getString(c.getColumnIndex(DealerTable.PY_NAME));
			bean.type = c.getString(c.getColumnIndex(DealerTable.TYPE));
			return bean;
		}
		return null;
	}
	
	public static List<DealerBean> getBeanList() {
		List<DealerBean> plist = new ArrayList<DealerBean>();
		String sql = "select * from "+DealerTable.TABLE_NAME + " order by py_name";
		Cursor c = getRsd().rawQuery(sql, null);
		while (c.moveToNext()) {
			DealerBean bean = new DealerDto().new DealerBean();
			bean.id = c.getString(c.getColumnIndex(DealerTable.ID));
			bean.dealer_name = c.getString(c.getColumnIndex(DealerTable.DEALER_NAME));
			bean.dealer_code = c.getString(c.getColumnIndex(DealerTable.DEALER_CODE));
			bean.parent_dealer_name = c.getString(c.getColumnIndex(DealerTable.PARENT_DEALER_NAME));
			bean.region_name = c.getString(c.getColumnIndex(DealerTable.REGION_NAME));
			bean.type_name = c.getString(c.getColumnIndex(DealerTable.TYPE_NAME));
			bean.contact_person = c.getString(c.getColumnIndex(DealerTable.CONTACT_PERSON));
			bean.contact_phone = c.getString(c.getColumnIndex(DealerTable.CONTACT_PHONE));
			bean.fax = c.getString(c.getColumnIndex(DealerTable.FAX));
			bean.address = c.getString(c.getColumnIndex(DealerTable.ADDRESS));
			bean.lon = c.getString(c.getColumnIndex(DealerTable.LON));
			bean.lat = c.getString(c.getColumnIndex(DealerTable.LAT));
			bean.accuracy = c.getString(c.getColumnIndex(DealerTable.ACCURACY));
			bean.remark = c.getString(c.getColumnIndex(DealerTable.REMARK));
			bean.py_index = c.getString(c.getColumnIndex(DealerTable.PY_INDEX));
			bean.first_py = c.getString(c.getColumnIndex(DealerTable.PY_NAME));
			bean.type = c.getString(c.getColumnIndex(DealerTable.TYPE));
			plist.add(bean);
		}
		return plist;
	}
	
	public static DBResult updateBean(DealerBean bean) {
		ContentValues values = new ContentValues();
		values.put(DealerTable.PARENT_DEALER_NAME, bean.parent_dealer_name);
		values.put(DealerTable.DEALER_CODE, bean.dealer_code);
		values.put(DealerTable.REGION_ID, bean.region_id);
		values.put(DealerTable.REGION_NAME, bean.region_name);
		values.put(DealerTable.TYPE_ID, bean.type_id);
		values.put(DealerTable.TYPE_NAME, bean.type_name);
		values.put(DealerTable.CONTACT_PERSON, bean.contact_person);
		values.put(DealerTable.CONTACT_PHONE, bean.contact_phone);
		values.put(DealerTable.FAX, bean.fax);
		values.put(DealerTable.ADDRESS, bean.address);
		values.put(DealerTable.REMARK, bean.remark);
		values.put(DealerTable.LON, bean.lon);
		values.put(DealerTable.LAT, bean.lat);
		values.put(DealerTable.ACCURACY, bean.accuracy);
		
		getWsd().update(DealerTable.TABLE_NAME, values, DealerTable.ID+"=?", new String[]{bean.id});
		
		return DBResult.update_successfully;
	}

}
