package com.eastelsoft.lbs.db;

import java.util.ArrayList;
import java.util.List;

import com.eastelsoft.lbs.ClearHistoryActivity;
import com.eastelsoft.lbs.bean.ClientContactsBean;
import com.eastelsoft.lbs.bean.ClientDetailBean;
import com.eastelsoft.lbs.bean.ClientDto;
import com.eastelsoft.lbs.bean.ClientDto.ClientBean;
import com.eastelsoft.lbs.bean.ClientMechanicsBean;
import com.eastelsoft.lbs.bean.ClientRegionDto;
import com.eastelsoft.lbs.bean.ClientRegionDto.RegionBean;
import com.eastelsoft.lbs.bean.ClientTypeDto;
import com.eastelsoft.lbs.bean.ClientTypeDto.TypeBean;
import com.eastelsoft.lbs.bean.SelectBean;
import com.eastelsoft.lbs.db.table.ClientContactsTable;
import com.eastelsoft.lbs.db.table.ClientMechanicsTable;
import com.eastelsoft.lbs.db.table.ClientRegionTable;
import com.eastelsoft.lbs.db.table.ClientTable;
import com.eastelsoft.lbs.db.table.ClientTypeTable;
import com.eastelsoft.util.FileLog;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class ClientDBTask {
	
	public static String TAG = "ClientDBTask";
	
	private ClientDBTask() {}
	
	public static SQLiteDatabase getWsd() {
		return LocationSQLiteHelper.getInstance().getWritableDatabase();
	}
	
	public static SQLiteDatabase getRsd() {
		return LocationSQLiteHelper.getInstance().getReadableDatabase();
	}
	
	public static List<ClientBean> getBeanList() {
		List<ClientBean> plist = new ArrayList<ClientBean>();
		String sql = "select * from " + ClientTable.TABLE_NAME + " order by first_py ";
		Cursor c = getRsd().rawQuery(sql, null);
		while (c.moveToNext()) {
			ClientBean bean = new ClientDto().new ClientBean();
			bean.id = c.getString(c.getColumnIndex(ClientTable.ID));
			bean.client_name = c.getString(c.getColumnIndex(ClientTable.CLIENT_NAME));
			bean.type = c.getString(c.getColumnIndex(ClientTable.TYPE));
			bean.first_py = c.getString(c.getColumnIndex(ClientTable.FIRST_PY));
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
				values.put(ClientTable.CLIENT_NAME, bean.client_name);
				values.put(ClientTable.FIRST_PY, bean.first_py);
				values.put(ClientTable.TYPE, bean.type);
				values.put(ClientTable.IS_UPLOAD, "1");
				values.put(ClientTable.UPDATECODE, "0");
				
				getWsd().insert(ClientTable.TABLE_NAME, null, values);
			}
			getWsd().setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		}
		getWsd().endTransaction();
		return DBResult.add_successfully;
	}
	
	/**
	 * delete is_upload = 1
	 * @return
	 */
	public static DBResult deleteAll() {
		String sql = "delete from "+ClientTable.TABLE_NAME + " where is_upload=?";
		getWsd().execSQL(sql,new String[]{"1"});
		
		return DBResult.delete_successfully;
	}
	
	public static DBResult deleteBean(String id) {
		String sql = "delete from "+ClientTable.TABLE_NAME+" where id = ?";
		getWsd().execSQL(sql, new String[]{id});
		
		return DBResult.delete_successfully;
	}
	
	public static DBResult addBean(ClientBean bean) {
		try {
			ContentValues values = new ContentValues();
			values.put(ClientTable.ID, bean.id);
			values.put(ClientTable.CLIENT_NAME, bean.client_name);
			values.put(ClientTable.IS_UPLOAD, bean.is_upload);
			values.put(ClientTable.CLIENT_CODE, bean.client_code);
			values.put(ClientTable.DEALER_ID, bean.dealer_id);
			values.put(ClientTable.DEALER_NAME, bean.dealer_name);
			values.put(ClientTable.TYPE, bean.type);
			values.put(ClientTable.REGION_ID, bean.region_id);
			values.put(ClientTable.REGION_NAME, bean.region_name);
			values.put(ClientTable.TYPE_ID, bean.type_id);
			values.put(ClientTable.TYPE_NAME, bean.type_name);
			values.put(ClientTable.FAX, bean.fax);
			values.put(ClientTable.ADDRESS, bean.address);
			values.put(ClientTable.REMARK, bean.remark);
			values.put(ClientTable.LON, bean.lon);
			values.put(ClientTable.LAT, bean.lat);
			values.put(ClientTable.ACCURARY, bean.accuary);
			values.put(ClientTable.FIRST_PY, bean.first_py);
			values.put(ClientTable.IS_UPLOAD, bean.is_upload);
			values.put(ClientTable.UPDATECODE, bean.updatecode);
			
			getWsd().insert(ClientTable.TABLE_NAME, null, values);
		} catch (Exception e) {
			FileLog.e(TAG, TAG+" e==>" + e.toString());
		}
		return DBResult.add_successfully;
	}
	
	public static DBResult updateIsUpload(String id) {
		ContentValues values = new ContentValues();
		values.put(ClientTable.IS_UPLOAD, "1");
		
		getWsd().update(ClientTable.TABLE_NAME, values, ClientTable.ID+"=?", new String[]{id});
		
		return DBResult.update_successfully;
	}
	
	public static DBResult updateBean(ClientBean bean) {
		ContentValues values = new ContentValues();
		values.put(ClientTable.CLIENT_CODE, bean.client_code);
		values.put(ClientTable.DEALER_ID, bean.dealer_id);
		values.put(ClientTable.DEALER_NAME, bean.dealer_name);
//		values.put(ClientTable.TYPE, bean.type);
		values.put(ClientTable.REGION_ID, bean.region_id);
		values.put(ClientTable.REGION_NAME, bean.region_name);
		values.put(ClientTable.TYPE_NAME, bean.type_name);
		values.put(ClientTable.FAX, bean.fax);
		values.put(ClientTable.ADDRESS, bean.address);
		values.put(ClientTable.REMARK, bean.remark);
		values.put(ClientTable.LON, bean.lon);
		values.put(ClientTable.LAT, bean.lat);
		values.put(ClientTable.UPDATECODE, bean.updatecode);
		
		getWsd().update(ClientTable.TABLE_NAME, values, ClientTable.ID+"=?", new String[]{bean.id});
		
		return DBResult.update_successfully;
	}
	
	public static ClientBean getBeanById(String id) {
		String sql = "select * from "+ClientTable.TABLE_NAME + " where id = ?";
		Cursor c = getRsd().rawQuery(sql, new String[]{id});
		if (c.moveToNext()) {
			ClientBean bean = new ClientDto().new ClientBean();
			bean.id = c.getString(c.getColumnIndex(ClientTable.ID));
			bean.client_name = c.getString(c.getColumnIndex(ClientTable.CLIENT_NAME));
			bean.client_code = c.getString(c.getColumnIndex(ClientTable.CLIENT_CODE));
			bean.dealer_id = c.getString(c.getColumnIndex(ClientTable.DEALER_ID));
			bean.dealer_name = c.getString(c.getColumnIndex(ClientTable.DEALER_NAME));
			bean.region_id = c.getString(c.getColumnIndex(ClientTable.REGION_ID));
			bean.region_name = c.getString(c.getColumnIndex(ClientTable.REGION_NAME));
			bean.type = c.getString(c.getColumnIndex(ClientTable.TYPE));
			bean.type_id = c.getString(c.getColumnIndex(ClientTable.TYPE_ID));
			bean.type_name = c.getString(c.getColumnIndex(ClientTable.TYPE_NAME));
			bean.fax = c.getString(c.getColumnIndex(ClientTable.FAX));
			bean.address = c.getString(c.getColumnIndex(ClientTable.ADDRESS));
			bean.remark = c.getString(c.getColumnIndex(ClientTable.REMARK));
			bean.lon = c.getString(c.getColumnIndex(ClientTable.LON));
			bean.lat = c.getString(c.getColumnIndex(ClientTable.LAT));
			bean.accuary = c.getString(c.getColumnIndex(ClientTable.ACCURARY));
			bean.is_upload = c.getString(c.getColumnIndex(ClientTable.IS_UPLOAD));
			bean.updatecode = c.getString(c.getColumnIndex(ClientTable.UPDATECODE));
			
			return bean;
		}
		return null;
	}
	
	/**
	 * 客户联系人
	 * @param client_id
	 * @return
	 */
	public static DBResult deleteContactsAll(String client_id) {
		String sql = "delete from "+ClientContactsTable.TABLE_NAME+" where "+ClientContactsTable.MAP_CLIENT_ID+"=?";
		getWsd().execSQL(sql,new String[]{client_id});
		
		return DBResult.delete_successfully;
	}
	
	public static List<ClientContactsBean> getContactsByClientId(String client_id) {
		List<ClientContactsBean> list = new ArrayList<ClientContactsBean>();
		String sql = "select * from "+ClientContactsTable.TABLE_NAME + " where "+ClientContactsTable.MAP_CLIENT_ID+" = ? order by "+ClientContactsTable.IS_MAIN+" desc";
		Cursor c = getRsd().rawQuery(sql, new String[]{client_id});
		while (c.moveToNext()) {
			ClientContactsBean bean = new ClientContactsBean();
			bean.contact_person_id = c.getString(c.getColumnIndex(ClientContactsTable.CONTACT_PERSON_ID));
			bean.contact_person_name = c.getString(c.getColumnIndex(ClientContactsTable.CONTACT_PERSON_NAME));
			bean.map_client_id = c.getString(c.getColumnIndex(ClientContactsTable.MAP_CLIENT_ID));
			bean.contact_phone_1 = c.getString(c.getColumnIndex(ClientContactsTable.CONTACT_PHONE_1));
			bean.contact_phone_2 = c.getString(c.getColumnIndex(ClientContactsTable.CONTACT_PHONE_2));
			bean.tel = c.getString(c.getColumnIndex(ClientContactsTable.TEL));
			bean.is_main = c.getString(c.getColumnIndex(ClientContactsTable.IS_MAIN));
			list.add(bean);
		}
		return list;
	}
	
	public static DBResult addContacts(List<ClientContactsBean> pList) {
		getWsd().beginTransaction();
		try {
			for (ClientContactsBean bean : pList) {
				ContentValues values = new ContentValues();
				values.put(ClientContactsTable.CONTACT_PERSON_ID, bean.contact_person_id);
				values.put(ClientContactsTable.CONTACT_PERSON_NAME, bean.contact_person_name);
				values.put(ClientContactsTable.MAP_CLIENT_ID, bean.map_client_id);
				values.put(ClientContactsTable.CONTACT_PHONE_1, bean.contact_phone_1);
				values.put(ClientContactsTable.CONTACT_PHONE_2, bean.contact_phone_2);
				values.put(ClientContactsTable.TEL, bean.tel);
				values.put(ClientContactsTable.IS_MAIN, bean.is_main);
				
				getWsd().insert(ClientContactsTable.TABLE_NAME, null, values);
			}
			getWsd().setTransactionSuccessful();
		} catch (Exception e) {
			FileLog.e(TAG, TAG+" e==>" + e.toString());
		}
		getWsd().endTransaction();
		return DBResult.add_successfully;
	}
	
	/**
	 * 客户维修人员
	 * @param client_id
	 * @return
	 */
	public static DBResult deleteMechanicsAll(String client_id) {
		String sql = "delete from "+ClientMechanicsTable.TABLE_NAME+" where "+ClientMechanicsTable.MAP_CLIENT_ID+"=?";
		getWsd().execSQL(sql,new String[]{client_id});
		
		return DBResult.delete_successfully;
	}
	
	public static List<ClientMechanicsBean> getMechanicsByClientId(String client_id) {
		List<ClientMechanicsBean> list = new ArrayList<ClientMechanicsBean>();
		String sql = "select * from "+ClientMechanicsTable.TABLE_NAME + " where "+ClientMechanicsTable.MAP_CLIENT_ID+" = ?";
		Cursor c = getRsd().rawQuery(sql, new String[]{client_id});
		while (c.moveToNext()) {
			ClientMechanicsBean bean = new ClientMechanicsBean();
			bean.maintain_person_id = c.getString(c.getColumnIndex(ClientMechanicsTable.MAINTAIN_PERSON_ID));
			bean.map_client_id = c.getString(c.getColumnIndex(ClientMechanicsTable.MAP_CLIENT_ID));
			bean.contact_person_name = c.getString(c.getColumnIndex(ClientMechanicsTable.CONTACT_PERSON_NAME));
			bean.contact_phone_1 = c.getString(c.getColumnIndex(ClientMechanicsTable.CONTACT_PHONE_1));
			bean.contact_phone_2 = c.getString(c.getColumnIndex(ClientMechanicsTable.CONTACT_PHONE_2));
			list.add(bean);
		}
		return list;
	}
	
	public static DBResult addMechanics(List<ClientMechanicsBean> pList) {
		getWsd().beginTransaction();
		try {
			for (ClientMechanicsBean bean : pList) {
				ContentValues values = new ContentValues();
				values.put(ClientMechanicsTable.MAINTAIN_PERSON_ID, bean.maintain_person_id);
				values.put(ClientMechanicsTable.CONTACT_PERSON_NAME, bean.contact_person_name);
				values.put(ClientMechanicsTable.MAP_CLIENT_ID, bean.map_client_id);
				values.put(ClientMechanicsTable.CONTACT_PHONE_1, bean.contact_phone_1);
				values.put(ClientMechanicsTable.CONTACT_PHONE_2, bean.contact_phone_2);
				
				getWsd().insert(ClientMechanicsTable.TABLE_NAME, null, values);
			}
			getWsd().setTransactionSuccessful();
		} catch (Exception e) {
			FileLog.e(TAG, TAG+" e==>" + e.toString());
		}
		getWsd().endTransaction();
		return DBResult.add_successfully;
	}
	
	/**
	 * 客户类型
	 * @param pList
	 * @return
	 */
	public static DBResult addType(List<TypeBean> pList) {
		getWsd().beginTransaction();
		try {
			for (TypeBean bean : pList) {
				ContentValues values = new ContentValues();
				values.put(ClientTypeTable.ID, bean.id);
				values.put(ClientTypeTable.NAME, bean.name);
				
				getWsd().insert(ClientTypeTable.TABLE_NAME, null, values);
			}
			getWsd().setTransactionSuccessful();
		} catch (Exception e) {
			FileLog.e(TAG, TAG+" e==>" + e.toString());
		}
		getWsd().endTransaction();
		return DBResult.add_successfully;
	}
	
	public static DBResult deleteType() {
		String sql = "delete from "+ClientTypeTable.TABLE_NAME;
		getWsd().execSQL(sql);
		
		return DBResult.delete_successfully;
	}
	
	public static List<SelectBean> getTypeList() {
		List<SelectBean> plist = new ArrayList<SelectBean>();
		String sql = "select * from " + ClientTypeTable.TABLE_NAME;
		Cursor c = getRsd().rawQuery(sql, null);
		while (c.moveToNext()) {
			SelectBean bean = new SelectBean();
			bean.id = c.getString(c.getColumnIndex(ClientTypeTable.ID));
			bean.name = c.getString(c.getColumnIndex(ClientTypeTable.NAME));
			plist.add(bean);
		}
		return plist;
	}
	
	/**
	 * 客户区域
	 * @param pList
	 * @return
	 */
	public static DBResult addRegion(List<RegionBean> pList) {
		getWsd().beginTransaction();
		try {
			for (RegionBean bean : pList) {
				ContentValues values = new ContentValues();
				values.put(ClientRegionTable.ID, bean.id);
				values.put(ClientRegionTable.NAME, bean.name);
				values.put(ClientRegionTable.PID, bean.pid);
				values.put(ClientRegionTable.LEVEL, bean.level);
				
				getWsd().insert(ClientRegionTable.TABLE_NAME, null, values);
			}
			getWsd().setTransactionSuccessful();
		} catch (Exception e) {
			FileLog.e(TAG, TAG+" e==>" + e.toString());
		}
		getWsd().endTransaction();
		return DBResult.add_successfully;
	}
	
	public static DBResult deleteRegion() {
		String sql = "delete from "+ClientRegionTable.TABLE_NAME;
		getWsd().execSQL(sql);
		
		return DBResult.delete_successfully;
	}
	
	public static List<SelectBean> getRegionList(String pid) {
		List<SelectBean> plist = new ArrayList<SelectBean>();
		String sql = "select * from " + ClientRegionTable.TABLE_NAME;
		if ("1".equals(pid)) {
			sql += " where level=?";
		} else {
			sql += " where pid=?";
			SelectBean bean = new SelectBean();
			bean.id = pid;
			bean.name = "全部";
			plist.add(bean);
		}
		Cursor c = getRsd().rawQuery(sql, new String[]{pid});
		while (c.moveToNext()) {
			SelectBean bean = new SelectBean();
			bean.id = c.getString(c.getColumnIndex(ClientRegionTable.ID));
			bean.name = c.getString(c.getColumnIndex(ClientRegionTable.NAME));
			bean.pid = c.getString(c.getColumnIndex(ClientRegionTable.PID));
			bean.level = c.getString(c.getColumnIndex(ClientRegionTable.LEVEL));
			plist.add(bean);
		}
		if (plist.size() == 1) {
			return new ArrayList<SelectBean>();
		}
		return plist;
	}
	
}
