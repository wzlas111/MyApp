package com.eastelsoft.lbs.db;

import java.util.ArrayList;
import java.util.List;

import com.eastelsoft.lbs.bean.CommodityDto.CommodityBean;
import com.eastelsoft.lbs.bean.CommodityReasonDto.CommodityReasonBean;
import com.eastelsoft.lbs.bean.EvaluateDto;
import com.eastelsoft.lbs.bean.ProductTypeDto;
import com.eastelsoft.lbs.bean.SelectBean;
import com.eastelsoft.lbs.bean.VisitBean;
import com.eastelsoft.lbs.bean.EnterpriseTypeDto.EnterpriseTypeBean;
import com.eastelsoft.lbs.bean.EvaluateDto.EvaluateBean;
import com.eastelsoft.lbs.bean.OrderTypeDto.OrderTypeBean;
import com.eastelsoft.lbs.bean.ProductTypeDto.ProductTypeBean;
import com.eastelsoft.lbs.db.table.CommodityReasonTable;
import com.eastelsoft.lbs.db.table.CommodityTable;
import com.eastelsoft.lbs.db.table.DealerTable;
import com.eastelsoft.lbs.db.table.EnterpriseTypeTable;
import com.eastelsoft.lbs.db.table.EvaluateTable;
import com.eastelsoft.lbs.db.table.OrderTypeTable;
import com.eastelsoft.lbs.db.table.ProductTypeTable;
import com.eastelsoft.lbs.db.table.VisitTable;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

public class ParamsDBTask {
	
	private ParamsDBTask() {}
	
	public static SQLiteDatabase getWsd() {
		return LocationSQLiteHelper.getInstance().getWritableDatabase();
	}
	
	public static SQLiteDatabase getRsd() {
		return LocationSQLiteHelper.getInstance().getReadableDatabase();
	}
	
	/**
	 * 服务评价
	 * @param pList
	 * @return
	 */
	public static DBResult addEvaluateList(List<EvaluateBean> pList) {
		getWsd().beginTransaction();
		try {
			for (EvaluateBean bean : pList) {
				ContentValues values = new ContentValues();
				values.put(EvaluateTable.EVALUATE_ID, bean.evaluate_id);
				values.put(EvaluateTable.EVALUATE_NAME, bean.evaluate_name);
				values.put(EvaluateTable.SEQUENCE, bean.sequence);
				
				getWsd().insert(EvaluateTable.TABLE_NAME, EvaluateTable.EVALUATE_ID, values);
			}
			getWsd().setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		}
		getWsd().endTransaction();
		return DBResult.add_successfully;
	}
	
	public static DBResult deleteEvaluate() {
		String sql = "delete from "+EvaluateTable.TABLE_NAME;
		getWsd().execSQL(sql);
		
		return DBResult.delete_successfully;
	}
	
	public static List<String> getEvaluateList() {
		List<String> plist = new ArrayList<String>();
		String sql = "select * from "+EvaluateTable.TABLE_NAME + " order by "+EvaluateTable.SEQUENCE;
		Cursor c = getRsd().rawQuery(sql, null);
		while (c.moveToNext()) {
			String data = c.getString(c.getColumnIndex(EvaluateTable.EVALUATE_NAME));
			plist.add(data);
		}
		return plist;
	}
	
	/**
	 * 产品类型
	 * @param pList
	 * @return
	 */
	public static DBResult addProductTypeList(List<ProductTypeBean> pList) {
		getWsd().beginTransaction();
		try {
			for (ProductTypeBean bean : pList) {
				ContentValues values = new ContentValues();
				values.put(ProductTypeTable.PRODUCT_TYPE_ID, bean.product_type_id);
				values.put(ProductTypeTable.PRODUCT_TYPE_NAME, bean.product_type_name);
				
				getWsd().insert(ProductTypeTable.TABLE_NAME, ProductTypeTable.PRODUCT_TYPE_ID, values);
			}
			getWsd().setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		}
		getWsd().endTransaction();
		return DBResult.add_successfully;
	}
	
	public static DBResult deleteProductType() {
		String sql = "delete from "+ProductTypeTable.TABLE_NAME;
		getWsd().execSQL(sql);
		
		return DBResult.delete_successfully;
	}
	
	public static List<SelectBean> getProductTypeList() {
		List<SelectBean> plist = new ArrayList<SelectBean>();
		String sql = "select * from "+ProductTypeTable.TABLE_NAME;
		Cursor c = getRsd().rawQuery(sql, null);
		while (c.moveToNext()) {
			SelectBean bean = new SelectBean();
			bean.id = c.getString(c.getColumnIndex(ProductTypeTable.PRODUCT_TYPE_ID));
			bean.name = c.getString(c.getColumnIndex(ProductTypeTable.PRODUCT_TYPE_NAME));
			plist.add(bean);
		}
		return plist;
	}
	
	/**
	 * 订单类型
	 * @param pList
	 * @return
	 */
	public static DBResult addOrderTypeList(List<OrderTypeBean> pList) {
		getWsd().beginTransaction();
		try {
			for (OrderTypeBean bean : pList) {
				ContentValues values = new ContentValues();
				values.put(OrderTypeTable.ORDER_FORM_TYPE_ID, bean.order_form_type_id);
				values.put(OrderTypeTable.ORDER_FORM_TYPE_NAME, bean.order_form_type_name);
				
				getWsd().insert(OrderTypeTable.TABLE_NAME, OrderTypeTable.ORDER_FORM_TYPE_ID, values);
			}
			getWsd().setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		}
		getWsd().endTransaction();
		return DBResult.add_successfully;
	}
	
	public static DBResult deleteOrderType() {
		String sql = "delete from "+OrderTypeTable.TABLE_NAME;
		getWsd().execSQL(sql);
		
		return DBResult.delete_successfully;
	}
	
	public static List<SelectBean> getOrderTypeList() {
		List<SelectBean> plist = new ArrayList<SelectBean>();
		String sql = "select * from "+OrderTypeTable.TABLE_NAME;
		Cursor c = getRsd().rawQuery(sql, null);
		while (c.moveToNext()) {
			SelectBean bean = new SelectBean();
			bean.id = c.getString(c.getColumnIndex(OrderTypeTable.ORDER_FORM_TYPE_ID));
			bean.name = c.getString(c.getColumnIndex(OrderTypeTable.ORDER_FORM_TYPE_NAME));
			plist.add(bean);
		}
		return plist;
	}
	
	/**
	 * 单位类型
	 * @param pList
	 * @return
	 */
	public static DBResult addEnterpriseTypeList(List<EnterpriseTypeBean> pList) {
		getWsd().beginTransaction();
		try {
			for (EnterpriseTypeBean bean : pList) {
				ContentValues values = new ContentValues();
				values.put(EnterpriseTypeTable.ENTERPRISE_TYPE_ID, bean.enterpriseunits_type_id);
				values.put(EnterpriseTypeTable.ENTERPRISE_TYPE_NAME, bean.enterpriseunits_type_name);
				
				getWsd().insert(EnterpriseTypeTable.TABLE_NAME, EnterpriseTypeTable.ENTERPRISE_TYPE_ID, values);
			}
			getWsd().setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		}
		getWsd().endTransaction();
		return DBResult.add_successfully;
	}
	
	public static DBResult deleteEnterpriseType() {
		String sql = "delete from "+EnterpriseTypeTable.TABLE_NAME;
		getWsd().execSQL(sql);
		
		return DBResult.delete_successfully;
	}
	
	public static List<SelectBean> getEnterpriseTypeList() {
		List<SelectBean> plist = new ArrayList<SelectBean>();
		String sql = "select * from "+EnterpriseTypeTable.TABLE_NAME;
		Cursor c = getRsd().rawQuery(sql, null);
		while (c.moveToNext()) {
			SelectBean bean = new SelectBean();
			bean.id = c.getString(c.getColumnIndex(EnterpriseTypeTable.ENTERPRISE_TYPE_ID));
			bean.name = c.getString(c.getColumnIndex(EnterpriseTypeTable.ENTERPRISE_TYPE_NAME));
			plist.add(bean);
		}
		return plist;
	}
	
	/**
	 * 机器型号
	 * @param pList
	 * @return
	 */
	public static DBResult addCommodityList(List<CommodityBean> pList) {
		getWsd().beginTransaction();
		try {
			for (CommodityBean bean : pList) {
				ContentValues values = new ContentValues();
				values.put(CommodityTable.ID, bean.id);
				values.put(CommodityTable.NAME, bean.name);
				values.put(CommodityTable.PACKING, bean.packing);
				
				getWsd().insert(CommodityTable.TABLE_NAME, CommodityTable.ID, values);
			}
			getWsd().setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		}
		getWsd().endTransaction();
		return DBResult.add_successfully;
	}
	
	public static DBResult deleteCommodity() {
		String sql = "delete from "+CommodityTable.TABLE_NAME;
		getWsd().execSQL(sql);
		
		return DBResult.delete_successfully;
	}
	
	public static List<SelectBean> getCommodityList() {
		List<SelectBean> plist = new ArrayList<SelectBean>();
		String sql = "select * from "+CommodityTable.TABLE_NAME;
		Cursor c = getRsd().rawQuery(sql, null);
		while (c.moveToNext()) {
			SelectBean bean = new SelectBean();
			bean.id = c.getString(c.getColumnIndex(CommodityTable.ID));
			bean.name = c.getString(c.getColumnIndex(CommodityTable.NAME));
			plist.add(bean);
		}
		return plist;
	}
	
	/**
	 * 机器故障
	 * @param pList
	 * @return
	 */
	public static DBResult addCommodityReasonList(List<CommodityReasonBean> pList) {
		getWsd().beginTransaction();
		try {
			for (CommodityReasonBean bean : pList) {
				ContentValues values = new ContentValues();
				values.put(CommodityReasonTable.COMMODITY_MAINTENANCE_CONTENT, bean.commodity_maintenance_id);
				values.put(CommodityReasonTable.COMMODITY_ID, bean.commodity_id);
				values.put(CommodityReasonTable.COMMODITY_NAME, bean.commodity_name);
				values.put(CommodityReasonTable.COMMODITY_MAINTENANCE_ID, bean.commodity_maintenance_id);
				values.put(CommodityReasonTable.COMMODITY_SOLVE_CONTENT, bean.commodity_solve_content);
				values.put(CommodityReasonTable.COMMODITY_MAINTENANCE_CONTENT, bean.commodity_maintenance_content);
				
				getWsd().insert(CommodityReasonTable.TABLE_NAME, CommodityReasonTable.COMMODITY_MAINTENANCE_ID, values);
			}
			getWsd().setTransactionSuccessful();
		} catch (Exception e) {
			e.printStackTrace();
		}
		getWsd().endTransaction();
		return DBResult.add_successfully;
	}
	
	public static DBResult deleteCommodityReason() {
		String sql = "delete from "+CommodityReasonTable.TABLE_NAME;
		getWsd().execSQL(sql);
		
		return DBResult.delete_successfully;
	}
	
	public static List<SelectBean> getCommodityReasonList(String id) {
		List<SelectBean> plist = new ArrayList<SelectBean>();
		String sql = "select * from "+CommodityReasonTable.TABLE_NAME;
		if (!TextUtils.isEmpty(id)) {
			sql += " where commodity_id=?";
		}
		Cursor c = getRsd().rawQuery(sql, new String[]{id});
		while (c.moveToNext()) {
			SelectBean bean = new SelectBean();
			bean.id = c.getString(c.getColumnIndex(CommodityReasonTable.COMMODITY_MAINTENANCE_ID));
			bean.name = c.getString(c.getColumnIndex(CommodityReasonTable.COMMODITY_MAINTENANCE_CONTENT));
			plist.add(bean);
		}
		return plist;
	}
	
	public static List<SelectBean> getCommoditySolverList(String id) {
		List<SelectBean> plist = new ArrayList<SelectBean>();
		String sql = "select * from "+CommodityReasonTable.TABLE_NAME;
		if (!TextUtils.isEmpty(id)) {
			sql += " where commodity_id=?";
		}
		Cursor c = getRsd().rawQuery(sql, new String[]{id});
		while (c.moveToNext()) {
			SelectBean bean = new SelectBean();
			bean.id = c.getString(c.getColumnIndex(CommodityReasonTable.COMMODITY_MAINTENANCE_ID));
			bean.name = c.getString(c.getColumnIndex(CommodityReasonTable.COMMODITY_SOLVE_CONTENT));
			plist.add(bean);
		}
		return plist;
	}

}
