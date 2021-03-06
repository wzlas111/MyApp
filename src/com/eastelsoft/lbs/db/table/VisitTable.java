package com.eastelsoft.lbs.db.table;

public class VisitTable {

	public static final String TABLE_NAME = "visit_table";
	
	public static final String UID = "uid";
	
	public static final String ID = "id";
	
    public static final String DEALER_ID = "dealer_id";
    
    public static final String DEALER_NAME = "dealer_name";
    
    public static final String PLAN_ID = "plan_id";
    
    public static final String PLAN_NAME = "plan_name";
    
    public static final String START_TIME = "start_time";
    
    public static final String START_LOCATION = "start_location";
    
    public static final String START_ACCURACY = "start_accuracy";
    
    public static final String START_LON = "start_lon";
    
    public static final String START_LAT = "start_lat";
    
    public static final String ARRIVE_TIME = "arrive_time";
    
    public static final String ARRIVE_LOCATION = "arrive_location";
    
    public static final String ARRIVE_ACCURACY = "arrvie_accuracy";
    
    public static final String ARRIVE_LON = "arrive_lon";
    
    public static final String ARRIVE_LAT = "arrive_lat";
    
    public static final String SERVICE_BEGIN_TIME = "service_begin_time";
    
    public static final String SERVICE_END_TIME = "service_end_time";
    
    public static final String VISIT_IMG = "visit_img";
    
    public static final String VISIT_IMG_NUM = "visit_img_num";
    
    //0:已出发; 1:到达; 2:已上传 ; 3:表单上传中; 4:图片上传中; 9:上传失败;
    public static final String STATUS = "status";
    //0:未上传; 1:已上传
    public static final String IS_UPLOAD = "is_upload";
    
    public static final String MECHANIC_COUNT = "mechanic_count";
    
    public static final String IS_EVALUATE = "is_evaluate";
	
}
