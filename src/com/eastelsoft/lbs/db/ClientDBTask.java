package com.eastelsoft.lbs.db;

import java.util.ArrayList;
import java.util.List;

import com.eastelsoft.lbs.bean.ClientContactsBean;
import com.eastelsoft.lbs.bean.ClientDetailBean;
import com.eastelsoft.lbs.bean.ClientDto;
import com.eastelsoft.lbs.bean.ClientDto.ClientBean;
import com.eastelsoft.lbs.bean.ClientMechanicsBean;
import com.eastelsoft.lbs.db.table.ClientContactsTable;
import com.eastelsoft.lbs.db.table.ClientMechanicsTable;
import com.eastelsoft.lbs.db.table.ClientTable;

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
		getWsd().beginTransaction();
		try {
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
		String sql = "delete from "+ClientTable.TABLE_NAME+" where id = ?";
		getWsd().execSQL(sql, new String[]{id});
		
		return DBResult.delete_successfully;
	}
	
	public static DBResult updateBean(ClientDetailBean bean) {
		ContentValues values = new ContentValues();
		values.put(ClientTable.CLIENT_NAME, bean.client_name);
		values.put(ClientTable.CLIENT_CODE, bean.client_code);
		values.put(ClientTable.DEALER_NAME, bean.dealer_name);
		values.put(ClientTable.TYPE, bean.type);
		values.put(ClientTable.REGION_NAME, bean.region_name);
		values.put(ClientTable.TYPENAME, bean.typename);
		values.put(ClientTable.CONTACT_PHONE, bean.contact_phone);
		values.put(ClientTable.FAX, bean.fax);
		values.put(ClientTable.ADDRESS, bean.address);
		values.put(ClientTable.REMARK, bean.remark);
		values.put(ClientTable.LON, bean.lon);
		values.put(ClientTable.LAT, bean.lat);
		
		getWsd().update(ClientTable.TABLE_NAME, values, ClientTable.ID+"=?", new String[]{bean.id});
		
		return DBResult.update_successfully;
	}
	
	public static ClientDetailBean getBeanById(String id) {
		String sql = "select * from "+ClientTable.TABLE_NAME + " where id = ?";
		Cursor c = getRsd().rawQuery(sql, new String[]{id});
		if (c.moveToNext()) {
			ClientDetailBean bean = new ClientDetailBean();
			bean.id = c.getString(c.getColumnIndex(ClientTable.ID));
			bean.client_name = c.getString(c.getColumnIndex(ClientTable.CLIENT_NAME));
			try {
				bean.client_code = c.getString(c.getColumnIndex(ClientTable.CLIENT_CODE));
				bean.dealer_name = c.getString(c.getColumnIndex(ClientTable.DEALER_NAME));
				bean.type = c.getString(c.getColumnIndex(ClientTable.TYPE));
				bean.region_name = c.getString(c.getColumnIndex(ClientTable.REGION_NAME));
				bean.typename = c.getString(c.getColumnIndex(ClientTable.TYPENAME));
				bean.contact_phone = c.getString(c.getColumnIndex(ClientTable.CONTACT_PHONE));
				bean.fax = c.getString(c.getColumnIndex(ClientTable.FAX));
				bean.address = c.getString(c.getColumnIndex(ClientTable.ADDRESS));
				bean.remark = c.getString(c.getColumnIndex(ClientTable.REMARK));
				bean.lon = c.getString(c.getColumnIndex(ClientTable.LON));
				bean.lat = c.getString(c.getColumnIndex(ClientTable.LAT));
			} catch (Exception e) {
			}
			
			return bean;
		}
		return null;
	}
	
	public static DBResult deleteContactsAll(String client_id) {
		String sql = "delete from "+ClientContactsTable.TABLE_NAME+" where client_id=?";
		getWsd().execSQL(sql,new String[]{client_id});
		
		return DBResult.delete_successfully;
	}
	
	public static List<ClientContactsBean> getContactsByClientId(String client_id) {
		List<ClientContactsBean> list = new ArrayList<ClientContactsBean>();
		String sql = "select * from "+ClientContactsTable.TABLE_NAME + " where id = ?";
		Cursor c = getRsd().rawQuery(sql, new String[]{client_id});
		while (c.moveToNext()) {
			ClientContactsBean bean = new ClientContactsBean();
			bean.id = c.getString(c.getColumnIndex(ClientContactsTable.ID));
			bean.name = c.getString(c.getColumnIndex(ClientContactsTable.NAME));
			bean.position = c.getString(c.getColumnIndex(ClientContactsTable.POSITION));
			bean.tel_1 = c.getString(c.getColumnIndex(ClientContactsTable.tel_1));
			bean.tel_2 = c.getString(c.getColumnIndex(ClientContactsTable.tel_2));
			bean.tel_3 = c.getString(c.getColumnIndex(ClientContactsTable.tel_3));
			bean.remark = c.getString(c.getColumnIndex(ClientContactsTable.REMARK));
			list.add(bean);
		}
		return list;
	}
	
	public static DBResult addContacts(List<ClientContactsBean> pList) {
		getWsd().beginTransaction();
		try {
			for (ClientContactsBean bean : pList) {
				ContentValues values = new ContentValues();
				values.put(ClientContactsTable.ID, bean.id);
				values.put(ClientContactsTable.NAME, bean.name);
				values.put(ClientContactsTable.POSITION, bean.position);
				values.put(ClientContactsTable.tel_1, bean.tel_1);
				values.put(ClientContactsTable.tel_2, bean.tel_2);
				values.put(ClientContactsTable.tel_3, bean.tel_3);
				values.put(ClientContactsTable.REMARK, bean.remark);
				values.put(ClientContactsTable.CLIENT_ID, bean.client_id);
				
				getWsd().insert(ClientContactsTable.TABLE_NAME, null, values);
			}
			getWsd().setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		}
		getWsd().endTransaction();
		return DBResult.add_successfully;
	}
	
	public static DBResult deleteMechanicsAll(String client_id) {
		String sql = "delete from "+ClientMechanicsTable.TABLE_NAME+" where client_id=?";
		getWsd().execSQL(sql,new String[]{client_id});
		
		return DBResult.delete_successfully;
	}
	
	public static List<ClientMechanicsBean> getMechanicsByClientId(String client_id) {
		List<ClientMechanicsBean> list = new ArrayList<ClientMechanicsBean>();
		String sql = "select * from "+ClientMechanicsTable.TABLE_NAME + " where id = ?";
		Cursor c = getRsd().rawQuery(sql, new String[]{client_id});
		while (c.moveToNext()) {
			ClientMechanicsBean bean = new ClientMechanicsBean();
			bean.id = c.getString(c.getColumnIndex(ClientMechanicsTable.ID));
			bean.name = c.getString(c.getColumnIndex(ClientMechanicsTable.NAME));
			bean.tel_1 = c.getString(c.getColumnIndex(ClientMechanicsTable.tel_1));
			bean.tel_2 = c.getString(c.getColumnIndex(ClientMechanicsTable.tel_2));
			bean.remark = c.getString(c.getColumnIndex(ClientMechanicsTable.REMARK));
			list.add(bean);
		}
		return list;
	}
	
	public static DBResult addMechanics(List<ClientMechanicsBean> pList) {
		getWsd().beginTransaction();
		try {
			for (ClientMechanicsBean bean : pList) {
				ContentValues values = new ContentValues();
				values.put(ClientMechanicsTable.ID, bean.id);
				values.put(ClientMechanicsTable.NAME, bean.name);
				values.put(ClientMechanicsTable.tel_1, bean.tel_1);
				values.put(ClientMechanicsTable.tel_2, bean.tel_2);
				values.put(ClientMechanicsTable.REMARK, bean.remark);
				values.put(ClientMechanicsTable.CLIENT_ID, bean.client_id);
				
				getWsd().insert(ClientMechanicsTable.TABLE_NAME, null, values);
			}
			getWsd().setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		}
		getWsd().endTransaction();
		return DBResult.add_successfully;
	}
	
}
