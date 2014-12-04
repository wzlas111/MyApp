/**
 * Copyright (c) 2012-7-21 www.eastelsoft.com
 * $ID UDPUtil.java 上午12:39:18 $
 */
package com.eastelsoft.util;

import java.util.Calendar;
import java.util.Locale;

import android.location.Location;

import com.eastelsoft.lbs.entity.ContrResp;
import com.eastelsoft.lbs.entity.HeartbeatResp;
import com.eastelsoft.lbs.entity.Login1Req;
import com.eastelsoft.lbs.entity.Login1Resp;
import com.eastelsoft.lbs.entity.Login2Req;
import com.eastelsoft.lbs.entity.Login2Resp;
import com.eastelsoft.lbs.entity.RegReq;
import com.eastelsoft.lbs.entity.RegResp;
import com.eastelsoft.lbs.entity.ReportReq;
import com.eastelsoft.lbs.entity.ReportResp;
import com.eastelsoft.lbs.entity.SetInfo;
import com.eastelsoft.lbs.location.BaseStationAction.SItude;

/**
 * UPD工具类
 * @deprecated
 * @author lengcj
 */
public class UDPUtil {
	private static final String TAG = "UDPUtil";
	
	public static UDPPackage send(short cmd_id, String ip, 
			int port, Object obj) throws Exception {
		UDPPackage pack = new UDPPackage(cmd_id, ip, port, obj);
		pack.send();
		return pack;
	}
	
	public static byte[] getReqData(short cmd_id, String ip, 
			int port, Object obj) throws Exception {
		UDPPackage pack = new UDPPackage(cmd_id, ip, port, obj);
		return pack.getReqData();
	}
	
	/*public static RegResp sendReg(short msg_seq, String serialNumber, String imei, 
			String imsi,String phoneBrand,String phoneModel,String phoneOs,String softVersion,String phoneResolution) {
		RegResp regResp = null;
		try {
			RegReq regReq = new RegReq(Contant.REG_COMMAND_ID, 
					Contant.REG_CMD_ID, 
					msg_seq,
					Contant.DEF_DEVICE_ID, 
					Contant.DEVICE_TYPE, 
					serialNumber, 
					imei,
					imsi,
					phoneBrand,
					phoneModel,
					phoneOs,
					softVersion,
					phoneResolution);
			regResp = UDPUtil.send(Contant.REG_CMD_ID,  Contant.GATE_IP, Contant.GATE_PORT, regReq)
					.getRegResp();
		} catch (Exception e) {
			FileLog.i(TAG, "Send Reg Fail");
		}
		return regResp;
	}*/
	
	public static Login2Resp sendLogin2(short msg_seq, SetInfo set, Login1Resp login1Resp) {
		Login2Resp login2Resp = null;
		try {
			Login2Req login2Req = new Login2Req(
					Contant.LOGIN2_COMMAND_ID,
					Contant.LOGIN2_CMD_ID,
					msg_seq,
					set.getDevice_id(),
					login1Resp.getAdapter_ip(),
					login1Resp
							.getAdapter_port(),
					login1Resp.getAuth_code(),
					set.getImei());
			
			login2Resp = UDPUtil
					.send(Contant.LOGIN2_CMD_ID,
							login1Resp
									.getAdapter_ip(),
							login1Resp
									.getAdapter_port(),
							login2Req)
					.getLogin2Resp();
		} catch (Exception e) {
			FileLog.i(TAG, "Send Login2 Fail");
		}
		return login2Resp;
	}
	public static Login2Resp sendLogin2Udp(short msg_seq, SetInfo set, Login1Resp login1Resp) {
		Login2Resp login2Resp = null;
		try {
			Login2Req login2Req = new Login2Req(
					Contant.LOGIN2_COMMAND_ID,
					Contant.LOGIN2_CMD_ID,
					msg_seq,
					set.getDevice_id(),
					login1Resp.getAdapter_ip(),
					login1Resp
							.getAdapter_port(),
					login1Resp.getAuth_code(),
					set.getImei());
			login2Req.setUdp_adapter_port(login1Resp.getUdp_adapter_port());
			
			login2Resp = UDPUtil
					.send(Contant.LOGIN2_CMD_ID,
							login1Resp
									.getAdapter_ip(),
							login1Resp
									.getUdp_adapter_port(),
							login2Req)
					.getLogin2Resp();
		} catch (Exception e) {
			FileLog.i(TAG, "Send Login2 Fail");
		}
		return login2Resp;
	}
	
	public static Login1Resp sendLogin1(short msg_seq, SetInfo set,String softVersion) {
		Login1Resp login1Resp = null;
		try {
			Login1Req login1Req = new Login1Req(
					Contant.LOGIN1_COMMAND_ID,
					Contant.LOGIN1_CMD_ID,
					msg_seq,
					set.getDevice_id(), 
					Contant.DEVICE_TYPE,
					set.getSerialNumber(), 
					set.getImei(), 
					set.getImsi(),
					softVersion);
			login1Resp = UDPUtil.send(
					Contant.LOGIN1_CMD_ID,
					set.getGateip(),
					set.getGateudpport(), 
					login1Req)
					.getLogin1Resp();
		} catch (Exception e) {
			FileLog.i(TAG, "Send Login1 Fail");
		}
		return login1Resp;
	}
	
	public static ReportResp sendReport(short msg_seq, Location location,String uploadtime,
			SetInfo set, String power) {
		ReportResp reportResp = null;
		try {
			ReportReq reportReq = new ReportReq(
					Contant.REPORT_COMMAND_ID,
					Contant.REPORT_CMD_ID,
					msg_seq,
					set.getDevice_id(),
					Contant.PACKAGETYPE,
					Util.format(
							location.getLongitude(),
							"#.######"),
					Util.format(location.getLatitude(),
							"#.######"),
					power,
					//String.valueOf(satellitesCount),
					"3",
					set.getAuth_code(),
					Util.format(String.valueOf(location
							.getAccuracy())),
							uploadtime);
			
			reportResp = UDPUtil.send(
					Contant.REPORT_CMD_ID, set.getAdapter_ip(),
					set.getUdp_adapter_port(), reportReq)
					.getReportResp();
			
		} catch (Exception e) {
			FileLog.i(TAG, "Send Report Fail");
		}
		return reportResp;
	}
	
	public static ReportResp sendReportCache(short msg_seq, String loc,String lat,String acc,String uploadtime,
			SetInfo set, String power) {
		ReportResp reportResp = null;
		try {
			ReportReq reportReq = new ReportReq(
					Contant.REPORT_COMMAND_ID,
					Contant.REPORT_CMD_ID,
					msg_seq,
					set.getDevice_id(),
					Contant.PACKAGETYPECA,
					loc,
					lat,
					power,
					//String.valueOf(satellitesCount),
					"3",
					set.getAuth_code(),
					acc,
					uploadtime);
			
			reportResp = UDPUtil.send(
					Contant.REPORT_CMD_ID, set.getAdapter_ip(),
					set.getUdp_adapter_port(), reportReq)
					.getReportResp();
			
		} catch (Exception e) {
			FileLog.i(TAG, "Send Report Fail");
		}
		return reportResp;
	}
	
	public static ReportResp sendReport(short msg_seq, Location location, 
			SetInfo set, String power,String reportTag) {
		ReportResp reportResp = null;
		try {
			ReportReq reportReq = new ReportReq(
					Contant.CHECK_COMMAND_ID,
					Contant.CHECK_CMD_ID,
					msg_seq,
					set.getDevice_id(),
					Contant.PACKAGETYPE,
					Util.format(
							location.getLongitude(),
							"#.######"),
					Util.format(location.getLatitude(),
							"#.######"),
					power,
					//String.valueOf(satellitesCount),
					"3",
					reportTag,
					set.getAuth_code(),
					Util.format(String.valueOf(location
							.getAccuracy())),
					String.valueOf(Calendar
							.getInstance(Locale.CHINESE)
							.getTime().getTime() / 1000));
			
			
			reportResp = UDPUtil.send(
					Contant.CHECK_CMD_ID, set.getAdapter_ip(),
					set.getUdp_adapter_port(), reportReq)
					.getReportResp();
			
		} catch (Exception e) {
			FileLog.i(TAG, "Send Report Fail");
		}
		return reportResp;
		
	}
	
	public static ReportResp sendReport(short msg_seq, SItude location, 
			SetInfo set, String power) {
		ReportResp reportResp = null;
		try {
			ReportReq reportReq = new ReportReq(
					Contant.REPORT_COMMAND_ID,
					Contant.REPORT_CMD_ID,
					msg_seq,
					set.getDevice_id(),
					Contant.PACKAGETYPE,
					Util.format(
							Double.parseDouble(location.longitude),
							"#.######"),
					Util.format(Double.parseDouble(location.latitude),
							"#.######"),
					power,
					//String.valueOf(satellitesCount),
					"3",
					set.getAuth_code(),
					Util.format(String.valueOf(location
							.accuracy)),
					String.valueOf(Calendar
							.getInstance(Locale.CHINESE)
							.getTime().getTime() / 1000));
			
			reportResp = UDPUtil.send(
					Contant.REPORT_CMD_ID, set.getAdapter_ip(),
					set.getUdp_adapter_port(), reportReq)
					.getReportResp();
			
		} catch (Exception e) {
			FileLog.i(TAG, "Send Report Fail");
		}
		return reportResp;
		
		
	}
	public static ReportResp sendReport(short msg_seq, SItude location, 
			SetInfo set, String power,String reportTag) {
		ReportResp reportResp = null;
		try {
			ReportReq reportReq = new ReportReq(
					Contant.CHECK_COMMAND_ID,
					Contant.CHECK_CMD_ID,
					msg_seq,
					set.getDevice_id(),
					Contant.PACKAGETYPE,
					Util.format(
							Double.parseDouble(location.longitude),
							"#.######"),
					Util.format(Double.parseDouble(location.latitude),
							"#.######"),
					power,
					//String.valueOf(satellitesCount),
					"3",
					reportTag,
					set.getAuth_code(),
					Util.format(String.valueOf(location
							.accuracy)),
					String.valueOf(Calendar
							.getInstance(Locale.CHINESE)
							.getTime().getTime() / 1000));
			
			reportResp = UDPUtil.send(
					Contant.CHECK_CMD_ID, set.getAdapter_ip(),
					set.getUdp_adapter_port(), reportReq)
					.getReportResp();
			
		} catch (Exception e) {
			FileLog.i(TAG, "Send Report Fail");
		}
		return reportResp;
		
		
	}
	

	
	public static byte[] getLogin2ReqData(short msg_seq, SetInfo set, Login1Resp login1Resp) {
		byte[] data = null;
		try {
			Login2Req login2Req = new Login2Req(
					Contant.LOGIN2_COMMAND_ID,
					Contant.LOGIN2_CMD_ID,
					msg_seq,
					set.getDevice_id(),
					login1Resp.getAdapter_ip(),
					login1Resp
							.getAdapter_port(),
					login1Resp.getAuth_code(),
					set.getImei());
			
			data = UDPUtil
					.getReqData(Contant.LOGIN2_CMD_ID,
							login1Resp
									.getAdapter_ip(),
							login1Resp
									.getAdapter_port(),
							login2Req);
		} catch (Exception e) {
			FileLog.i(TAG, "Send Login2 Fail");
		}
		return data;
	}
	
	public static byte[] getReportReqData(short msg_seq, Location location, 
			SetInfo set, String power) {
		byte[] data = null;
		try {
			ReportReq reportReq = new ReportReq(
					Contant.REPORT_COMMAND_ID,
					Contant.REPORT_CMD_ID,
					msg_seq,
					set.getDevice_id(),
					Contant.PACKAGETYPE,
					Util.format(
							location.getLongitude(),
							"#.######"),
					Util.format(location.getLatitude(),
							"#.######"),
					power,
					//String.valueOf(satellitesCount),
					"3",
					set.getAuth_code(),
					Util.format(String.valueOf(location
							.getAccuracy())),
					String.valueOf(Calendar
							.getInstance(Locale.CHINESE)
							.getTime().getTime() / 1000));
			
			data = UDPUtil.getReqData(
					Contant.REPORT_CMD_ID, set.getAdapter_ip(),
					set.getAdapter_port(), reportReq);
			
		} catch (Exception e) {
			FileLog.i(TAG, "Send Report Fail");
		}
		return data;
	}
	
	public static byte[] getReportReqData(short msg_seq, Location location, 
			SetInfo set, String power, String reportTag) {
		byte[] data = null;
		try {
			ReportReq reportReq = new ReportReq(
					Contant.CHECK_COMMAND_ID,
					Contant.CHECK_CMD_ID,
					msg_seq,
					set.getDevice_id(),
					Contant.PACKAGETYPE,
					Util.format(
							location.getLongitude(),
							"#.######"),
					Util.format(location.getLatitude(),
							"#.######"),
					power,
					//String.valueOf(satellitesCount),
					"3",
					reportTag,
					set.getAuth_code(),
					Util.format(String.valueOf(location
							.getAccuracy())),
					String.valueOf(Calendar
							.getInstance(Locale.CHINESE)
							.getTime().getTime() / 1000));
			
			data = UDPUtil.getReqData(
					Contant.CHECK_CMD_ID, set.getAdapter_ip(),
					set.getAdapter_port(), reportReq);
			
		} catch (Exception e) {
			FileLog.i(TAG, "Send Report Fail");
		}
		return data;
	}
	public static byte[] getReportReqData(short msg_seq, SItude location, 
			SetInfo set, String power, String reportTag) {
		byte[] data = null;
		try {
			ReportReq reportReq = new ReportReq(
					Contant.CHECK_COMMAND_ID,
					Contant.CHECK_CMD_ID,
					msg_seq,
					set.getDevice_id(),
					Contant.PACKAGETYPE,
					Util.format(
							Double.parseDouble(location.longitude),
							"#.######"),
					Util.format(Double.parseDouble(location.latitude),
							"#.######"),
					power,
					//String.valueOf(satellitesCount),
					"3",
					reportTag,
					set.getAuth_code(),
					Util.format(String.valueOf(location
							.accuracy)),
					String.valueOf(Calendar
							.getInstance(Locale.CHINESE)
							.getTime().getTime() / 1000));
			
			data = UDPUtil.getReqData(
					Contant.CHECK_CMD_ID, set.getAdapter_ip(),
					set.getAdapter_port(), reportReq);
			
		} catch (Exception e) {
			FileLog.i(TAG, "Send Report Fail");
		}
		return data;
	}
	
	public static byte[] getReportReqData(short msg_seq, SItude location, 
			SetInfo set, String power) {
		byte[] data = null;
		try {
			ReportReq reportReq = new ReportReq(
					Contant.REPORT_COMMAND_ID,
					Contant.REPORT_CMD_ID,
					msg_seq,
					set.getDevice_id(),
					Contant.PACKAGETYPE,
					Util.format(
							Double.parseDouble(location.longitude),
							"#.######"),
					Util.format(Double.parseDouble(location.latitude),
							"#.######"),
					power,
					//String.valueOf(satellitesCount),
					"3",
					set.getAuth_code(),
					Util.format(String.valueOf(location
							.accuracy)),
					String.valueOf(Calendar
							.getInstance(Locale.CHINESE)
							.getTime().getTime() / 1000));
			
			data = UDPUtil.getReqData(
					Contant.REPORT_CMD_ID, set.getAdapter_ip(),
					set.getAdapter_port(), reportReq);
			
		} catch (Exception e) {
			FileLog.i(TAG, "Send Report Fail");
		}
		return data;
	}
	
	public static byte[] getContrRespData(short msg_seq, short ack_msg, SetInfo set,String type) {
		byte[] data = null;
		try {
			ContrResp contrResp = new ContrResp(
					Contant.CONTR_COMMAND_ID,
					Contant.CONTR_CMD_ID,
					msg_seq,
					"0", 
					type,
					set.getDevice_id(), 
					set.getAuth_code(), 
					ack_msg);
			
			data = UDPUtil.getReqData(
					Contant.CONTR_CMD_ID, set.getAdapter_ip(),
					set.getAdapter_port(), contrResp);
			
		} catch (Exception e) {
			FileLog.i(TAG, "Send getContrRespData Fail");
		}
		return data;
	}
	
	public static byte[] getHeartbeatRespData(short msg_seq, short ack_msg, SetInfo set) {
		byte[] data = null;
		try {
			HeartbeatResp heartbeatResp = new HeartbeatResp(
					Contant.HEARTBEAT_COMMAND_ID,
					Contant.HEARTBEAT_CMD_ID,
					msg_seq,
					"0", 
					ack_msg, 
					set.getDevice_id(), 
					set.getAuth_code());
			
			data = UDPUtil.getReqData(
					Contant.HEARTBEAT_CMD_ID, set.getAdapter_ip(),
					set.getAdapter_port(), heartbeatResp);
			
		} catch (Exception e) {
			FileLog.i(TAG, "Send heartbeat Fail");
		}
		return data;
	}

}