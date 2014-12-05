/**
 * Copyright (c) 2012-8-14 www.eastelsoft.com
 * $ID LocationSQLiteHelper.java 下午4:48:47 $
 */
package com.eastelsoft.lbs.db;

import com.eastelsoft.lbs.db.table.ClientTable;
import com.eastelsoft.lbs.db.table.DealerTable;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.GlobalVar;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * SQLite
 * 
 * @author lengcj
 */
public class LocationSQLiteHelper extends SQLiteOpenHelper {
	
	private static LocationSQLiteHelper instance = null;

	private static final String DATABASE_NAME = "location.db";
	
	static final String CREATE_DEALER_TABLE_SQL = "create table if not exists " + DealerTable.TABLE_NAME
            + "("
            + DealerTable.UID + " integer primary key autoincrement,"
            + DealerTable.ID + " text,"
            + DealerTable.NAME + " text,"
            + DealerTable.TELEPHONE + " text,"
            + DealerTable.GROUP_ID + " text,"
            + DealerTable.GROUP_NAME + " text,"
            + DealerTable.PY_INDEX + " text,"
            + DealerTable.PY_NAME + " text,"
            + DealerTable.REMARK + " text"
            + ");";
	
	static final String CREATE_CLIENT_TABLE_SQL = "create table if not exists " + ClientTable.TABLE_NAME
            + "("
            + ClientTable.UID + " integer primary key autoincrement,"
            + ClientTable.ID + " text,"
            + ClientTable.CLIENT_NAME + " text,"
            + ClientTable.CLIENT_CODE + " text,"
            + ClientTable.PY + " text,"
            + ClientTable.DEALER_ID + " text,"
            + ClientTable.DEALER_NAME + " text,"
            + ClientTable.TYPE + " text,"
            + ClientTable.TYPE_ID + " text"
            + ClientTable.TYPENAME + " text" 
            + ClientTable.REGION_ID + " text" 
            + ClientTable.REGION_NAME + " text" 
            + ClientTable.CONTACT_PHONE + " text" 
            + ClientTable.LON + " text" 
            + ClientTable.LAT + " text" 
            + ClientTable.EMAIL + " text" 
            + ClientTable.ADDRESS + " text" 
            + ClientTable.REMARK + " text" 
            + ClientTable.IS_UPLOAD + " text" 
            + ");";

	public LocationSQLiteHelper(Context context, String name,
			CursorFactory factory, int version) {
		// 2013-5-17,version 升为6
		super(context, DATABASE_NAME, null, 18);
	}
	
	public static synchronized LocationSQLiteHelper getInstance() {
		if (instance == null) {
			instance = new LocationSQLiteHelper(GlobalVar.getInstance(), null, null, 0);
		}
		return instance;
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		FileLog.i("LocationSQLiteHelper", "onCreate");
		
		db.execSQL("create table if not exists audio_info(" +
				"id NTEXT PRIMARY KEY," +"gpsid NTEXT,"+
				"type NTEXT,"+"istijiao NTEXT"+
				")");
		
		db.execSQL("create table if not exists l_info("
				+ "info_auto_id NTEXT PRIMARY KEY," + "uploadDate NTEXT,"
				+ "title NTEXT," + "imgFile NTEXT," + "remark NTEXT,"
				+ "location NTEXT," + "lon NTEXT," + "lat NTEXT,"
				+ "istijiao NTEXT,"+"setLongtime NTEXT)");
		
		// db.execSQL("create table if not exists l_timinglocation("
		// +"tl_id NTEXT PRIMARY KEY,"
		// +"tl_uploadDate NTEXT,"
		// +"tl_lon NTEXT,"
		// +"tl_lat NTEXT,"
		// +"tl_accuracy NTEXT)"
		// );
		
		db.execSQL("create table if not exists l_timinglocation("
				+ "tl_id NTEXT PRIMARY KEY," + "tl_uploadDate NTEXT,"
				+ "tl_lon NTEXT," + "tl_lat NTEXT," + "tl_accuracy NTEXT,"
				+ "tl_seq NTEXT," + "tl_power NTEXT," + "tl_states NTEXT,"
				+ "tl_signalStrengthValue NTEXT," + "tl_cell NTEXT,"
				+ "tl_wifi NTEXT)");

		// 建新表客户拜访
		db.execSQL("create table if not exists l_visit("
				+ "id NTEXT PRIMARY KEY," + "clientid NTEXT,"
				+ "clientname NTEXT," + "data NTEXT," + "title NTEXT,"
				+ "remark NTEXT," + "lon NTEXT," + "lat NTEXT,"
				+ "location NTEXT," + "imgFile NTEXT," + "istijiao NTEXT)");
		db.execSQL("create table if not exists l_plan(" + "updateCode NTEXT,"
				+ "planId NTEXT PRIMARY KEY," + "planCode NTEXT,"
				+ "planDate NTEXT," + "releasedate NTEXT," + "lon NTEXT,"
				+ "lat NTEXT," + "location NTEXT," + "remark NTEXT,"
				+ "type NTEXT," + "planUploadDate NTEXT," + "planText NTEXT,"
				+ "imgFile NTEXT," + "resultCode NTEXT," + "title NTEXT,"
				+ "uplon NTEXT," + "uplat NTEXT," + "uplocation NTEXT,"
				+ "istijiao NTEXT)");

		// 此表为新表 代替原来l_cust
		db.execSQL("create table if not exists l_cust("
				+ "id NTEXT PRIMARY KEY," + "clientName NTEXT,"
				+ "contacts NTEXT," + "lon NTEXT," + "lat NTEXT,"
				+ "location NTEXT," + "email NTEXT," + "phone NTEXT,"
				+ "job NTEXT," + "Phone2 NTEXT," + "Phone3 NTEXT,"
				+ "Phone4 NTEXT," + "address NTEXT," + "type NTEXT,"
				+ "py NTEXT," + "c_t_id NTEXT," + "region_id NTEXT,"
				+ "c_t_name NTEXT," + "region_name NTEXT," + "istijiao NTEXT)");

		db.execSQL("create table if not exists l_loc(" + "locTime NTEXT,"
				+ "chkTag NTEXT," + "lon NTEXT," + "lat NTEXT," + "addr NTEXT,"
				+ "accuracy NTEXT)");

		db.execSQL("create table if not exists l_cust_type(" + "id NTEXT,"
				+ "name NTEXT)");

		db.execSQL("create table if not exists l_cust_area(" + "id NTEXT,"
				+ "name NTEXT," + "pid NTEXT," + "level NTEXT)");

		// 销售服务
		// 商品
		db.execSQL("create table if not exists l_goods(" + "id NTEXT,"
				+ "name NTEXT," + "packing NTEXT)");
		// 销量上报
		db.execSQL("create table if not exists l_salesreport(" + "id NTEXT,"
				+ "clientid NTEXT," + "clientName NTEXT," + "date NTEXT,"
				+ "goods_id NTEXT," + "imgFile NTEXT," + "remark NTEXT,"
				+ "lon NTEXT," + "lat NTEXT," + "location NTEXT,"
				+ "submitdate NTEXT," + "istijiao NTEXT)");

		// goods_id对应销量上报中的，一对多关系
		db.execSQL("create table if not exists l_salesreport_goods("
				+ "id NTEXT," + "goods_id NTEXT," + "each_id NTEXT,"
				+ "name NTEXT," + "amount NTEXT," + "packing NTEXT)");

		// 销量任务分配
		db.execSQL("create table if not exists l_salesallocation("
				+ "id NTEXT," + "clientid NTEXT," + "clientName NTEXT,"
				+ "date NTEXT," + "goods_id NTEXT," + "istijiao NTEXT)");
		db.execSQL("create table if not exists l_salesallocation_goods("
				+ "id NTEXT," + "goods_id NTEXT," + "name NTEXT,"
				+ "amount NTEXT," + "unallocatedamount NTEXT,"
				+ "each_id NTEXT," + "packing NTEXT)");

		// 销量任务分配
		db.execSQL("create table if not exists l_monthstarget(" + "id NTEXT,"
				+ "date NTEXT," + "goods_id NTEXT)");
		db.execSQL("create table if not exists l_monthstarget_goods("
				+ "id NTEXT," + "goods_id NTEXT," + "name NTEXT,"
				+ "target NTEXT," + "distribution NTEXT," + "each_id NTEXT,"
				+ "packing NTEXT)");

		// 公告通知
		db.execSQL("create table if not exists l_bulletin("
				+ "b_id NTEXT PRIMARY KEY," + "b_name NTEXT,"
				+ "b_release_date NTEXT," + "b_code NTEXT," + "is_top NTEXT,"
				+ "b_fail_date NTEXT," + "b_remark NTEXT," + "b_type NTEXT,"
				+ "u_name NTEXT," + "b_appendix NTEXT,"
				+ "b_appendix_title NTEXT," + "b_appendix_size NTEXT,"
				+ "is_read NTEXT)");
		//知识库
		
		db.execSQL("create table if not exists l_knowledgebase("
				+ "k_id NTEXT PRIMARY KEY," + "k_name NTEXT,"
				+ "k_type NTEXT," + "k_fatherid NTEXT," + "k_appendix NTEXT,"
				+ "k_appendix_title NTEXT," + "k_appendix_size NTEXT," + "is_read NTEXT,"
				+ "k_code NTEXT)");
		db.execSQL("create table if not exists l_knowledgebasedetail("
				+ "b_id NTEXT PRIMARY KEY," + "b_name NTEXT,"
				+ "b_release_date NTEXT," + "b_code NTEXT," + "is_top NTEXT,"
				+ "b_fail_date NTEXT," + "b_remark NTEXT," + "b_type NTEXT,"
				+ "u_name NTEXT," + "b_appendix NTEXT,"
				+ "b_appendix_title NTEXT," + "b_appendix_size NTEXT,"
				+ "is_read NTEXT)");
		
		try {
			db.execSQL(CREATE_DEALER_TABLE_SQL);
			db.execSQL(CREATE_CLIENT_TABLE_SQL);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	// 当打开数据库时传入的版本号与当前的版本号不同时会调用该方法
	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		FileLog.i("LocationSQLiteHelper", "onUpgrade");
		try {
			db.execSQL("ALTER TABLE l_info ADD COLUMN setLongtime NTEXT");
		} catch (Exception e) {
			// TODO: handle exception
		}
		try {
			db.execSQL("ALTER TABLE l_cust ADD COLUMN py NTEXT");
		} catch (SQLException e) {
		}
		try {
			db.execSQL("ALTER TABLE l_cust ADD COLUMN c_t_id NTEXT");
		} catch (SQLException e) {
		}
		try {
			db.execSQL("ALTER TABLE l_cust ADD COLUMN region_id NTEXT");
		} catch (SQLException e) {
		}
		try {
			db.execSQL("ALTER TABLE l_cust ADD COLUMN c_t_name NTEXT");
		} catch (SQLException e) {
		}
		try {
			db.execSQL("ALTER TABLE l_cust ADD COLUMN region_name NTEXT");
		} catch (SQLException e) {
		}

		try {
			db.execSQL("ALTER TABLE l_cust ADD COLUMN job NTEXT");
		} catch (SQLException e) {
		}
		try {
			db.execSQL("ALTER TABLE l_cust ADD COLUMN Phone2 NTEXT");
		} catch (SQLException e) {
		}
		try {
			db.execSQL("ALTER TABLE l_cust ADD COLUMN Phone3 NTEXT");
		} catch (SQLException e) {
		}
		try {
			db.execSQL("ALTER TABLE l_cust ADD COLUMN Phone4 NTEXT");
		} catch (SQLException e) {
		}

		try {
			db.execSQL("ALTER TABLE l_plan ADD COLUMN istijiao NTEXT");
		} catch (SQLException e) {
		}

		try {
			db.execSQL("ALTER TABLE l_plan ADD COLUMN uplon NTEXT");
		} catch (SQLException e) {
		}
		try {
			db.execSQL("ALTER TABLE l_plan ADD COLUMN uplat NTEXT");
		} catch (SQLException e) {
		}
		try {
			db.execSQL("ALTER TABLE l_plan ADD COLUMN uplocation NTEXT");
		} catch (SQLException e) {
		}
		try {
			db.execSQL("ALTER TABLE l_plan ADD COLUMN releasedate NTEXT");
		} catch (SQLException e) {
		}

		// 2013/5/17
		try {
			db.execSQL("ALTER TABLE l_timinglocation ADD COLUMN tl_seq NTEXT");
		} catch (SQLException e) {
		}
		try {
			db.execSQL("ALTER TABLE l_timinglocation ADD COLUMN tl_power NTEXT");
		} catch (SQLException e) {
		}
		try {
			db.execSQL("ALTER TABLE l_timinglocation ADD COLUMN tl_states NTEXT");
		} catch (SQLException e) {
		}
		try {
			db.execSQL("ALTER TABLE l_timinglocation ADD COLUMN tl_signalStrengthValue NTEXT");
		} catch (SQLException e) {
		}
		try {
			db.execSQL("ALTER TABLE l_timinglocation ADD COLUMN tl_cell NTEXT");
		} catch (SQLException e) {
		}
		try {
			db.execSQL("ALTER TABLE l_timinglocation ADD COLUMN tl_wifi NTEXT");
		} catch (SQLException e) {
		}
		// 2013/5/17结束
		
		try {
			db.execSQL("ALTER TABLE client_table ADD COLUMN is_upload text");
		} catch (SQLException e) {
		}
		onCreate(db);

	}
}
