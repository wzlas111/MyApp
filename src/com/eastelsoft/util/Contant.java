/**
 * Copyright (c) 2012-7-21 www.eastelsoft.com
 * $ID Contant.java 上午12:42:13 $
 */
package com.eastelsoft.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 静态变量
 * 
 * @author lengcj
 */
public class Contant {

	public final static String SDCARD_PATH = "/mnt/sdcard/DCIM/eastelsoft/";
/*
 * 吉林认证中心地址：222.161.197.244
 * 测试认证中心地址：10.0.65.13
 * 浙江认证中心地址：124.160.11.13
 */
	public final static String AUTHCENTRE_IP = "124.160.11.13";
	public final static int AUTHCENTRE_PORT = 10710;

	// public final static String AUTHCENTRE_IP = "58.240.63.105";
	// public final static String GATE_IP = "113.200.200.34";
	// public final static String GATE_IP = "dw.wldushi.com";
	// public final static String GATE_IP = "etis.vicp.cc";
	// public final static String GATE_IP = "58.240.63.105";
	// public final static String GATE_IP = "loc.10010zj.com.cn";
	// public final static String GATE_IP = "dc.wldushi.com";
	// public final static int GATE_PORT = 10646; // 默认UDP连接端口
	// public final static int GATE_PORT = 8090;
	// public final static int GATE_TCP_PORT = 10704; // 默认TCP连接端口

	public final static int TCP_TIMEOUT = 10000;

	// 监听变化，时间间隔 5秒钟
	public final static int GPS_MIN = 5000;

	// 心跳时间
	public final static int HEARTBEAT_TIME = 30000;

	@SuppressWarnings("serial")
	public final static Map<String, String> MENUS_MAP = new HashMap<String, String>() {
		{
			put("01", "我的任务");
			put("02", "考勤签到");
			put("03", "考勤签退");
			put("04", "信息上报");
			put("05", "客户信息");
			put("06", "客户拜访");
			put("07", "销量上报");
			put("10", "公告通知");
			put("11", "知识库");
			put("24", "经销商拜访");
			put("25", "经销商信息");
			put("98", "事务提醒");
			put("99", "参数设置");
		}
	};
//	private String[] names = { "我的任务", "考勤签到", "考勤签退", "信息上报", "客户信息", "客户拜访",
//			"销量上报", "公告通知", "事务提醒", "知识库", "参数设置" };

	// http 10.0.65.18:6917
	// public static final String UPDATE_SERVER = "http://113.200.200.35/";
	// public static final String UPDATE_SERVER = "http://10.0.65.115:8899/";
	// public static final String UPDATE_SERVER = "http://etis.vicp.cc:6917/";
	// public static final String UPDATE_SERVER = "http://58.240.63.105/";
	// public static final String UPDATE_SERVER = "http://loc.10010zj.com.cn/";
	// public static final String UPDATE_SERVER = "http://dc.wldushi.com/";
	// public static final String UPDATE_APKNAME = "LBSBase.apk";

	public static final String UPDATE_VERJSON = "/mobile.do?reqCode=queryForAndroidBoss";

	public static final String UPDATE_SAVENAME = "gpsLocation.apk";

	public static final String PLAN_UPDATE_ACTION = "/mobile.do?reqCode=PlanUpdateAction";

	public static final String ACTION = "mobile.do";
	// 销量上报
	public static final String COMMODITYLISTACTION = "commoditylist.do";

	public static final String PLAN_UPDATE_REQCODE = "PlanUpdateAction";

	public static final String PLAN_UPLOAD_REQCODE = "PlanUploadAction";

	public static final String GPS_DATA_UPLOAD_ACTION = "GpsDataUploadAction";
	
	public static final String VIDEO_UPLOAD_NEW="ImgUploadNew";
	
	public static final String DAILY_UPLOAD_SEND="UploadMobileLog";
 
	public static final String AUDIO_UPLOAD_ACTION="AudioUpload";
	
	public static final String VISIT_UPLOAD_ACTION = "VisitUploadAction";

	public static final String CLINET_UPLOAD_ACTION = "ClientUploadAction";

	public static final String CLINET_REPORT_ACTION = "sendReport";

	public static final String CLIENT_UPDATE_ACTION = "/mobile.do?reqCode=ClientUpdateAction";

	public static final String CLIENT_UPDATE_REQCODE = "ClientUpdateAction";

	public static final String CLINET_REGION_UPDATE_ACTION = "ClientRegionUpdateAction";

	public static final String CLINET_TYPE_UPDATE_ACTION = "ClientTypeUpdateAction";
	
	//菜单获取
	public static final String 	EAUSER_MENU = "EauserMenu";

	// 销量模块接口
	public static final String C_U_ACTION = "/commoditylist.do?reqCode=CommodityUpdateAction";
	public static final String CO_UPDATE_REQCODE = "CommodityUpdateAction";

	public static final String PERSONNEL_MARKET_UPDATE_ACTION = "PersonnelMarketUploadAction";

	public static final String P_M_ACTION = "/commoditylist.do?reqCode=PersonnelMarketErrandUpdateAction";
	public static final String P_M_E_REQCODE = "PersonnelMarketErrandUpdateAction";

	public static final String CLIENT_MARKET_ERRAND_ACTION = "ClientMarketErrandUploadAction";
	public static final String CLIENT_M_UPDATE_ACTION = "ClientMarketErrandUpdateAction";

	public static final String CLIENT_MARKET_ACTION = "ClientMarketUploadAction";
	//
	public static final String CITYCODE_ACTION = "/mobile.do?reqCode=QueryCityCode";
	
	public static final String SALESREPORTCHANGE_ACTION = "android.salesreport.listchange.action";
	public static final String SALESTASKALLOCATIONCHANGE_ACTION = "android.salestaskallocation.listchange.action";
	
	//我的任务快捷回复
	public static final String PlANMSGGET="PlanMsgUpdate";
	// 公告通知 知识库
	// 下载文件附件路径
	public final static String SDCARD_ANPENDIX_PATH_NOLINE = "/eastelsoft/anpendix";
	public final static String SDCARD_ANPENDIX_PATH = "/eastelsoft/anpendix/";

	public static final String BULLETIN_UPDATE_ACTION = "/mobile.do?reqCode=BulletinAction";
	public static final String BULLETIN_ACTION = "BulletinAction";
	public static final String BULLETIN_DETAIL_ACTION = "BulletinDetailAction";
	
	public static final String PLANREAD_ACTION = "PlanReadAction";
	public static final String BULLETIN_READ_ACTION = "BulletinReadAction";
	
	public static final String KNOWLEDGE_UPDATE_ACTION = "/mobile.do?reqCode=KnowledgeBaseAction";
	public static final String KNOWLEDGE_ACTION = "KnowledgeBaseAction";
	
	public static final String KNOWLEDGEDETAIL_UPDATE_ACTION = "/mobile.do?reqCode=KnowledgeBaseDetailAction";
	public static final String KNOWLEDGEDETAILDETAIL_ACTION = "KnowledgeBaseDetailAction";
	
	public static final String KNOWLEDGEBASE_READ_ACTION = "KnowledgeBaseReadAction";
	
	// 命令字
	public final static String DEVICE_TYPE = "00010002";

	public final static String DEF_DEVICE_ID = "0000000000000000";

	public final static String DEF_AUTH_CODE = "0123456789abcdef";

	public final static short AUTH_COMMAND_ID = 0x0014;

	public final static short AUTH_CMD_ID = 14;

	public final static short REG_COMMAND_ID = 1;

	public final static short REG_CMD_ID = 1;

	public final static short LOGIN1_COMMAND_ID = 2;

	public final static short LOGIN1_CMD_ID = 21;

	public final static short LOGIN2_COMMAND_ID = 2;

	public final static short LOGIN2_CMD_ID = 22;

	public final static short CHECK_COMMAND_ID = 0x11;

	public final static short CHECK_CMD_ID = 11;

	public final static short REPORT_COMMAND_ID = 0x11;

	public final static short REPORT_CMD_ID = 211;

	public final static short CONTR_COMMAND_ID = (short) 0x8008;

	public final static short CONTR_CMD_ID = 8;

	public final static short HEARTBEAT_COMMAND_ID = (short) 0x8004;

	public final static short HEARTBEAT_CMD_ID = 4;

	// 默认初始配置
	public final static String TIMEPERIOD = "07:30-19:30";

	public final static String INTERVAL = "180";

	public final static String WEEK = "0111110";

	public final static String FILTERDATE = "20131001,20131002";

	public final static String MINDISTANCE = "1000";

	public final static String PACKAGETYPE = "10";

	public final static String PACKAGETYPECA = "11";
	
	public final static String IMG_NUM = "10";

	public final static String OP = "操作";

	public final static String OP_CLOSE = "关闭";

	public final static String OP_VIEW = "查看";

	public final static String OP_DEL = "删除";

	public final static String OP_ADD = "新增";

	public final static String OP_SUCC = "成功";

	public final static String OP_FAIL = "失败";

	public final static String OP_CANNOT_DEL = "待办事项，不允许删除";

	public final static String PLAN_NOT_DO = "0"; // 待办任务

	public final static String PLAN_HAS_DO = "1"; // 已办任务

	public final static String TEST_DATA_INFO_TAG = "[{\"updatecode\":\"1\"}]";

	public final static String TEST_DATA_INFO_QUERY = "{\"plandata\":[{\"planid\":\"781151cf9fba46ab831ee7fab59aef6b\",\"title\":\"采购4\",\"lon\":\"120.154295\",\"remark\":\"采购板砖\",\"location\":\"浙江省,杭州市,拱墅区,潮王路\",\"plandate\":\"20120822\",\"type\":\"1\",\"lat\":\"30.286568\",\"plancode\":\"0\"},{\"planid\":\"9475f567e0434f16a812bf5401b739ec\",\"title\":\"采购1\",\"lon\":\"120.207596\",\"remark\":\"采购电脑\",\"location\":\"浙江省,杭州市,江干区,新塘路\",\"plandate\":\"20120818\",\"type\":\"1\",\"lat\":\"30.277748\",\"plancode\":\"0\"},{\"planid\":\"a0bba5f602c74844bf8364f5fa64c24c\",\"title\":\"采购3\",\"lon\":\"120.168114\",\"remark\":\"采购窗户\",\"location\":\"浙江省,杭州市,下城区,上塘路\",\"plandate\":\"20120820\",\"type\":\"1\",\"lat\":\"30.282195\",\"plancode\":\"0\"},{\"planid\":\"fb758dbfb2754897a9504cf24df3baef\",\"title\":\"采购2\",\"lon\":\"120.183048\",\"remark\":\"采购服装\",\"location\":\"浙江省,杭州市,下城区,岳帅桥\",\"plandate\":\"20120819\",\"type\":\"1\",\"lat\":\"30.289903\",\"plancode\":\"0\"},{\"planid\":\"68f54c56a310441d926ccc591217b8ee\",\"type\":\"3\"}],\"updatecode\":\"3\"}";

	public final static String ON_LINE = "111";
	public final static String LINGING = "000";
	public final static String OFF_LINE = "-111";

	public final static String ADD = "1";
	public final static String VIEW = "2";
	
	public final static long WEEKTIME=604800000l;
	public final static long MOUTHTIME=2592000000l;
	public final static long ALLTIME=0;
	//Debug签名
	public final static String STRKEY="Tm4dUmuUXLGo6Q52lWEBqBNG";
	//MrZ_lbs签名
	//	public final static String STRKEY="bkXGesQGycgQH7LqHsqzpGxk";
	
}
