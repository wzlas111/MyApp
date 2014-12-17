/**
 * Copyright (c) 2012-8-14 www.eastelsoft.com
 * $ID LocationSQLiteHelper.java 下午4:48:47 $
 */
package com.eastelsoft.lbs.db;

import com.eastelsoft.lbs.db.table.ClientContactsTable;
import com.eastelsoft.lbs.db.table.ClientMechanicsTable;
import com.eastelsoft.lbs.db.table.ClientRegionTable;
import com.eastelsoft.lbs.db.table.ClientTable;
import com.eastelsoft.lbs.db.table.ClientTypeTable;
import com.eastelsoft.lbs.db.table.DealerTable;
import com.eastelsoft.lbs.db.table.UploadImgTable;
import com.eastelsoft.lbs.db.table.VisitEvaluateTable;
import com.eastelsoft.lbs.db.table.VisitMcTable;
import com.eastelsoft.lbs.db.table.VisitTable;
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

	private static final String DATABASE_NAME = "exing.db";
	
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
            + ClientTable.TYPE_ID + " text,"
            + ClientTable.TYPENAME + " text," 
            + ClientTable.REGION_ID + " text," 
            + ClientTable.REGION_NAME + " text," 
            + ClientTable.CONTACT_PHONE + " text," 
            + ClientTable.FAX + " text," 
            + ClientTable.LON + " text," 
            + ClientTable.LAT + " text," 
            + ClientTable.EMAIL + " text," 
            + ClientTable.ADDRESS + " text," 
            + ClientTable.REMARK + " text," 
            + ClientTable.IS_UPLOAD + " text" 
            + ");";
	
	static final String CREATE_CLIENT_CONATCTS_TABLE_SQL = "create table if not exists " + ClientContactsTable.TABLE_NAME
            + "("
            + ClientContactsTable.UID + " integer primary key autoincrement,"
            + ClientContactsTable.ID + " text,"
            + ClientContactsTable.CLIENT_ID + " text,"
            + ClientContactsTable.NAME + " text,"
            + ClientContactsTable.tel_1 + " text,"
            + ClientContactsTable.tel_2 + " text,"
            + ClientContactsTable.tel_3 + " text,"
            + ClientContactsTable.POSITION + " text,"
            + DealerTable.REMARK + " text"
            + ");";
	
	static final String CREATE_CLIENT_MECHANICS_TABLE_SQL = "create table if not exists " + ClientMechanicsTable.TABLE_NAME
            + "("
            + ClientMechanicsTable.UID + " integer primary key autoincrement,"
            + ClientMechanicsTable.ID + " text,"
            + ClientMechanicsTable.CLIENT_ID + " text,"
            + ClientMechanicsTable.NAME + " text,"
            + ClientMechanicsTable.tel_1 + " text,"
            + ClientMechanicsTable.tel_2 + " text,"
            + ClientMechanicsTable.REMARK + " text"
            + ");";
	
	static final String CREATE_CLIENT_TYPE_TABLE_SQL = "create table if not exists " + ClientTypeTable.TABLE_NAME
            + "("
            + ClientTypeTable.UID + " integer primary key autoincrement,"
            + ClientTypeTable.ID + " text,"
            + ClientTypeTable.NAME + " text"
            + ");";
	
	static final String CREATE_CLIENT_REGION_TABLE_SQL = "create table if not exists " + ClientRegionTable.TABLE_NAME
            + "("
            + ClientRegionTable.UID + " integer primary key autoincrement,"
            + ClientRegionTable.ID + " text,"
            + ClientRegionTable.PID + " text,"
            + ClientRegionTable.LEVEL + " text,"
            + ClientRegionTable.NAME + " text"
            + ");";
	
	static final String CREATE_VISIT_TABLE_SQL = "create table if not exists " + VisitTable.TABLE_NAME
            + "("
            + VisitTable.UID + " integer primary key autoincrement,"
            + VisitTable.ID + " text,"
            + VisitTable.STATUS + " text,"
            + VisitTable.DEALER_ID + " text,"
            + VisitTable.DEALER_NAME + " text,"
            + VisitTable.PLAN_ID + " text,"
            + VisitTable.PLAN_NAME + " text,"
            + VisitTable.START_TIME + " text,"
            + VisitTable.START_LOCATION + " text,"
            + VisitTable.START_LON + " text,"
            + VisitTable.START_LAT + " text,"
            + VisitTable.ARRIVE_TIME + " text,"
            + VisitTable.ARRIVE_LOCATION + " text,"
            + VisitTable.ARRIVE_LON + " text,"
            + VisitTable.ARRIVE_LAT + " text,"
            + VisitTable.SERVICE_BEGIN_TIME + " text,"
            + VisitTable.SERVICE_END_TIME + " text,"
            + VisitTable.VISIT_IMG + " text,"
            + VisitTable.IS_UPLOAD + " text,"
            + VisitTable.MECHANIC_COUNT + " text,"
            + VisitTable.IS_EVALUATE + " text"
            + ");";
	
	static final String CREATE_VISIT_MC_TABLE_SQL = "create table if not exists " + VisitMcTable.TABLE_NAME
            + "("
            + VisitMcTable.UID + " integer primary key autoincrement,"
            + VisitMcTable.ID + " text,"
            + VisitMcTable.VISIT_ID + " text,"
            + VisitMcTable.CLIENT_ID + " text,"
            + VisitMcTable.CLIENT_NAME + " text,"
            + VisitMcTable.START_TIME + " text,"
            + VisitMcTable.END_TIME + " text,"
            + VisitMcTable.SERVICE_START_TIME + " text,"
            + VisitMcTable.SERVICE_END_TIME + " text,"
            + VisitMcTable.IS_REPAIR + " text,"
            + VisitMcTable.MC_REGISTER_JSON + " text,"
            + VisitMcTable.MC_TYPE_JSON + " text,"
            + VisitMcTable.MC_PERSON_JSON + " text,"
            + VisitMcTable.MC_INFO_JSON + " text,"
            + VisitMcTable.CLIENT_SIGN + " text,"
            + VisitMcTable.UPLOAD_IMG + " text,"
            + VisitMcTable.IS_UPLOAD + " text"
            + ");";
	
	static final String CREATE_VISIT_EVALUATE_TABLE_SQL = "create table if not exists " + VisitEvaluateTable.TABLE_NAME
            + "("
            + VisitEvaluateTable.UID + " integer primary key autoincrement,"
            + VisitEvaluateTable.ID + " text,"
            + VisitEvaluateTable.VISIT_ID + " text,"
            + VisitEvaluateTable.VISIT_NUM + " text,"
            + VisitEvaluateTable.SERVICE_VALUE + " text,"
            + VisitEvaluateTable.SERVICE_NAME + " text,"
            + VisitEvaluateTable.OTHER_JOB + " text,"
            + VisitEvaluateTable.ADVISE + " text,"
            + VisitEvaluateTable.CLIENT_SIGN + " text,"
            + VisitEvaluateTable.IS_UPLOAD + " text"
            + ");";
	
	static final String CREATE_UPLOAD_IMG_TABLE_SQL = "create table if not exists " + UploadImgTable.TABLE_NAME
            + "("
            + UploadImgTable.UID + " integer primary key autoincrement,"
            + UploadImgTable.DATA_ID + " text,"
            + UploadImgTable.NAME + " text,"
            + UploadImgTable.PATH + " text,"
            + UploadImgTable.TYPE + " text"
            + ");";

	public LocationSQLiteHelper(Context context, String name,
			CursorFactory factory, int version) {
		super(context, DATABASE_NAME, null, 32);
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
		
		try {
			db.execSQL(CREATE_DEALER_TABLE_SQL);
			db.execSQL(CREATE_CLIENT_TABLE_SQL);
			db.execSQL(CREATE_CLIENT_CONATCTS_TABLE_SQL);
			db.execSQL(CREATE_CLIENT_MECHANICS_TABLE_SQL);
			db.execSQL(CREATE_CLIENT_TYPE_TABLE_SQL);
			db.execSQL(CREATE_CLIENT_REGION_TABLE_SQL);
			db.execSQL(CREATE_VISIT_TABLE_SQL);
			db.execSQL(CREATE_VISIT_MC_TABLE_SQL);
			db.execSQL(CREATE_UPLOAD_IMG_TABLE_SQL);
			db.execSQL(CREATE_VISIT_EVALUATE_TABLE_SQL);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		db.execSQL("create table if not exists audio_info(" +
				"id NTEXT PRIMARY KEY," +"gpsid NTEXT,"+
				"type NTEXT,"+"istijiao NTEXT"+
				")");
		
		db.execSQL("create table if not exists l_info("
				+ "info_auto_id NTEXT PRIMARY KEY," + "uploadDate NTEXT,"
				+ "title NTEXT," + "imgFile NTEXT," + "remark NTEXT,"
				+ "location NTEXT," + "lon NTEXT," + "lat NTEXT,"
				+ "istijiao NTEXT,"+"setLongtime NTEXT)");
		
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
