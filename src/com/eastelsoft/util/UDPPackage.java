/**
 * Copyright (c) 2012-7-21 www.eastelsoft.com
 * $ID UDPPackage.java 上午12:42:13 $
 */
package com.eastelsoft.util;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.eastelsoft.lbs.entity.ContrResp;
import com.eastelsoft.lbs.entity.HeartbeatReq;
import com.eastelsoft.lbs.entity.HeartbeatResp;
import com.eastelsoft.lbs.entity.Login1Req;
import com.eastelsoft.lbs.entity.Login1Resp;
import com.eastelsoft.lbs.entity.Login2Req;
import com.eastelsoft.lbs.entity.Login2Resp;
import com.eastelsoft.lbs.entity.RegReq;
import com.eastelsoft.lbs.entity.RegResp;
import com.eastelsoft.lbs.entity.ReportReq;
import com.eastelsoft.lbs.entity.ReportResp;

/**
 * UPD组包解包程序
 * @deprecated
 * @author lengcj
 */
public class UDPPackage {

	private short cmd_id = 1; // 临时使用，区分网关登录和适配器登录
	
	private short length = 0;
	
	private String ip;
	
	private int port;
			
	private byte[] reqData;
	
	private int seq = 0;
	
	private int h0 = 0|0x40;
	private int h1 = 1|0x40;
	private int h2 = 2|0x40;
	private int h3 = 3|0x40;
	private int h4 = 4|0x40;
	private int h5 = 5|0x40;
	private int h6 = 6|0x40;
	private int h7 = 7|0x40;
	private int h8 = 8|0x40;
	private int h9 = 9|0x40;
	private int h10 = 10|0x40;
	private int h11 = 11|0x40;
	private int h12 = 12|0x40;
	
	private int h19 = 19|0x40;
	
	private RegReq regReq;
	
	private RegResp regResp;
	
	private Login1Req login1Req;
	
	private Login1Resp login1Resp;
	
	private Login2Req login2Req;
	
	private Login2Resp login2Resp;
	
	private ReportReq reportReq;
	
	private ReportResp reportResp;
	
	private HeartbeatReq heartbeatReq;
	
	private HeartbeatResp heartbeatResp;
	
	private ContrResp contrResp;
	
	public UDPPackage(short cmd_id, String ip, int port, Object obj) {
		this.cmd_id = cmd_id;
		this.ip = ip;
		this.port = port;
		assemblyReqData(obj);
	}
	
	/**
	 * 组装包体 
	 */
	public void assemblyReqData(Object obj) {
		short command_id = 1;
		short msg_seq = 1;
		long timestamp = 0;
		short ack = 0;
		String device_id = "0000000000000000";
		switch(cmd_id) {
		case 1:
			regReq=(RegReq)obj;
			command_id = regReq.getCommand_id();
			cmd_id = regReq.getCmd_id();
			msg_seq = regReq.getMsg_seq();
			device_id = regReq.getDevice_id();
			length = (short)regReq.getLength();
			break;
		case 21:
			login1Req=(Login1Req)obj;
			command_id = login1Req.getCommand_id();
			cmd_id = login1Req.getCmd_id();
			msg_seq = login1Req.getMsg_seq();
			device_id = login1Req.getDevice_id();
			length = (short)login1Req.getLength();
			break;
		case 22:
			login2Req=(Login2Req)obj;
			command_id = login2Req.getCommand_id();
			cmd_id = login2Req.getCmd_id();
			msg_seq = login2Req.getMsg_seq();
			device_id = login2Req.getDevice_id();
			length = (short)login2Req.getLength();
			break;
		case 11:
			reportReq=(ReportReq)obj;
			command_id = reportReq.getCommand_id();
			cmd_id = reportReq.getCmd_id();
			msg_seq = reportReq.getMsg_seq();
			device_id = reportReq.getDevice_id();
			length = (short)reportReq.getLength();
			break;
		case 211:
			reportReq=(ReportReq)obj;
			command_id = reportReq.getCommand_id();
			cmd_id = reportReq.getCmd_id();
			msg_seq = reportReq.getMsg_seq();
			device_id = reportReq.getDevice_id();
			length = (short)reportReq.getLength();
			break;
		case 4:
			heartbeatResp = (HeartbeatResp)obj;
			command_id = heartbeatResp.getCommand_id();
			cmd_id = heartbeatResp.getCmd_id();
			msg_seq = heartbeatResp.getMsg_seq();
			device_id = heartbeatResp.getDevice_id();
			length = (short)heartbeatResp.getLength();
			ack = heartbeatResp.getAck_seq();
			break;
		case 8:
			contrResp = (ContrResp)obj;
			command_id = contrResp.getCommand_id();
			cmd_id = contrResp.getCmd_id();
			msg_seq = contrResp.getMsg_seq();
			device_id = contrResp.getDevice_id();
			
			length = (short)contrResp.getLength();
			ack = contrResp.getAck_seq();
			break;
		default:break;
		}
		reqData = new byte[length];

		// 包头
		write(length, reqData, 0);
		reqData[2] = 1;
		reqData[3] = 0;
		write(command_id, reqData, 4);
		write(msg_seq, reqData, 6);
		write(timestamp, reqData, 8);
		System.arraycopy(device_id.getBytes(), 0, reqData, 12, device_id.getBytes().length);
		write(ack, reqData, 28);
		reqData[30] = 0;
		reqData[31] = 0; 
		
		// 组装包体和摘要
		switch(cmd_id) {
		case 1:assemblyRegData();break;
		case 21:assemblyLogin1Data();break;
		case 22:assemblyLogin2Data();break;
		case 4:heartbeatData();break;
		case 11:assemblyReportData();break;
		case 211:assemblyReportDataAuto();break;
		case 8:contrData();break;
		default:break;
		}
	}
	
	/** 
	 * 组装注册包体
	 */
	public void assemblyRegData() {
		int bodyLength = length - 48;
		byte[] bodyData = new byte[bodyLength];
		
		write(h0, bodyData, 0);
		write(regReq.getDeviceType().length(), bodyData, 1);
		byte[] bodyData1 = regReq.getDeviceType().getBytes();
		System.arraycopy(bodyData1, 0, bodyData, 2, bodyData1.length);
		
		write(h1, bodyData, 2 + bodyData1.length);
		write(regReq.getSerialNumber().length(), bodyData, 2 + bodyData1.length + 1);
		byte[] bodyData2 = regReq.getSerialNumber().getBytes();
		System.arraycopy(bodyData2, 0, bodyData, 2 + bodyData1.length + 2, bodyData2.length);
		
		write(h2, bodyData, 2 + bodyData1.length + 2 + bodyData2.length);
		write(regReq.getImei().length(), bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 1);
		byte[] bodyData3 = regReq.getImei().getBytes();
		System.arraycopy(bodyData3, 0, bodyData, 2 + bodyData1.length + 2+ bodyData2.length + 2, bodyData3.length);
		
		write(h3, bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length);
		write(regReq.getImsi().length(), bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length + 1);
		byte[] bodyData4 = regReq.getImsi().getBytes();
		System.arraycopy(bodyData4, 0, bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 
				+ bodyData3.length + 2, bodyData4.length);
		
		write(h6, bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length
				+ 2 + bodyData4.length);
		write(regReq.getPhoneBrand().length(), bodyData, 2 + bodyData1.length + 2 + bodyData2.length 
				+ 2 + bodyData3.length+ 2 + bodyData4.length + 1);
		byte[] bodyData5 = regReq.getPhoneBrand().getBytes();
		System.arraycopy(bodyData5, 0, bodyData, 2 + bodyData1.length + 2 + bodyData2.length 
				+ 2 + bodyData3.length+ 2 + bodyData4.length  + 2, bodyData5.length);
		
		write(h7, bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length
				+ 2 + bodyData4.length+ 2 + bodyData5.length);
		write(regReq.getPhoneModel().length(), bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length
				+ 2 + bodyData4.length+ 2 + bodyData5.length + 1);
		byte[] bodyData6 = regReq.getPhoneModel().getBytes();
		System.arraycopy(bodyData6, 0, bodyData,  2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length
				+ 2 + bodyData4.length+ 2 + bodyData5.length+ 2, bodyData6.length);
		
		write(h8, bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length
				+ 2 + bodyData4.length+ 2 + bodyData5.length+ 2 + bodyData6.length);
		write(regReq.getPhoneOs().length(), bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length
				+ 2 + bodyData4.length+ 2 + bodyData5.length +2 + bodyData6.length+ 1);
		byte[] bodyData7 = regReq.getPhoneOs().getBytes();
		System.arraycopy(bodyData7, 0, bodyData,  2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length
				+ 2 + bodyData4.length+ 2 + bodyData5.length +2 + bodyData6.length+ 2, bodyData7.length);
		
		write(h9, bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length
				+ 2 + bodyData4.length+ 2 + bodyData5.length+ 2 + bodyData6.length+ 2 + bodyData7.length);
		write(regReq.getSoftVersion().length(), bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length
				+ 2 + bodyData4.length+ 2 + bodyData5.length +2 + bodyData6.length+ 2 + bodyData7.length+ 1);
		byte[] bodyData8 = regReq.getSoftVersion().getBytes();
		System.arraycopy(bodyData8, 0, bodyData,  2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length
				+ 2 + bodyData4.length+ 2 + bodyData5.length +2 + bodyData6.length+ 2 + bodyData7.length+ 2, bodyData8.length);
		
		write(h10, bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length
				+ 2 + bodyData4.length+ 2 + bodyData5.length+ 2 + bodyData6.length+ 2 + bodyData7.length+ 2 + bodyData8.length);
		write(regReq.getPhoneResolution().length(), bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length
				+ 2 + bodyData4.length+ 2 + bodyData5.length +2 + bodyData6.length+ 2 + bodyData7.length+ 2 + bodyData8.length+1);
		byte[] bodyData9 = regReq.getPhoneResolution().getBytes();
		System.arraycopy(bodyData9, 0, bodyData,  2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length
				+ 2 + bodyData4.length+ 2 + bodyData5.length +2 + bodyData6.length+ 2 + bodyData7.length+ 2+bodyData8.length+ 2, bodyData9.length);
		
		
		System.arraycopy(bodyData, 0, reqData, 32, bodyLength);
		
		//String md5 = Util.Md5_(bodyData);
		//byte[] footData = md5.getBytes();
		
		byte[] footData = getFootData("0123456789abcdef");
		
		System.arraycopy(footData, 0, reqData, 32 + bodyLength, footData.length);
	}
	
	/**
	 * 组装登录包体
	 */
	public void assemblyLogin1Data() {
		int bodyLength = length - 48;
		byte[] bodyData = new byte[bodyLength];
		
		write(h0, bodyData, 0);
		write(login1Req.getDeviceType().length(), bodyData, 1);
		byte[] bodyData1 = login1Req.getDeviceType().getBytes();
		System.arraycopy(bodyData1, 0, bodyData, 2, bodyData1.length);
		
		write(h1, bodyData, 2 + bodyData1.length);
		write(login1Req.getSerialNumber().length(), bodyData, 2 + bodyData1.length + 1);
		byte[] bodyData2 = login1Req.getSerialNumber().getBytes();
		System.arraycopy(bodyData2, 0, bodyData, 2 + bodyData1.length + 2, bodyData2.length);
		
		write(h2, bodyData, 2 + bodyData1.length + 2 + bodyData2.length);
		write(login1Req.getImei().length(), bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 1);
		byte[] bodyData3 = login1Req.getImei().getBytes();
		System.arraycopy(bodyData3, 0, bodyData, 2 + bodyData1.length + 2+ bodyData2.length + 2, bodyData3.length);
		
		write(h3, bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length);
		write(login1Req.getImsi().length(), bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length + 1);
		byte[] bodyData4 = login1Req.getImsi().getBytes();
		System.arraycopy(bodyData4, 0, bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length + 2, bodyData4.length);
		
		write(h6, bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length+ 2 + bodyData4.length);
		write(login1Req.getSoftVersion().length(), bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length+ 2 + bodyData4.length + 1);
		byte[] bodyData5 = login1Req.getSoftVersion().getBytes();
		System.arraycopy(bodyData5, 0, bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length+ 2 + bodyData4.length + 2, bodyData5.length);
		
		System.arraycopy(bodyData, 0, reqData, 32, bodyLength);
		
//		String md5 = Util.Md5_(bodyData);
//		byte[] footData = md5.getBytes();
		byte[] footData = getFootData("0123456789abcdef");
		System.arraycopy(footData, 0, reqData, 32 + bodyLength, footData.length);
	}

	/**
	 * 组装登录包体
	 */
	public void assemblyLogin2Data() {
		int bodyLength = length - 48;
		byte[] bodyData = new byte[bodyLength];
		
		write(h0, bodyData, 0);
		write(login2Req.getImei().length(), bodyData, 1);
		byte[] bodyData1 = login2Req.getImei().getBytes();
		System.arraycopy(bodyData1, 0, bodyData, 2, bodyData1.length);
		
		write(h1, bodyData, 2 + bodyData1.length);
		write(login2Req.getAuth_code().length(), bodyData, 2 + bodyData1.length + 1);
		byte[] bodyData2 = login2Req.getAuth_code().getBytes();
		System.arraycopy(bodyData2, 0, bodyData, 2 + bodyData1.length + 2, bodyData2.length);
		
		System.arraycopy(bodyData, 0, reqData, 32, bodyLength);
		
		byte[] footData = getFootData(bodyData, login2Req.getAuth_code());
		
		System.arraycopy(footData, 0, reqData, 32 + bodyLength, footData.length);
	}
	
	/**
	 * 上报包体
	 */
	public void heartbeatData() {
		int bodyLength = length - 48;
		byte[] bodyData = new byte[bodyLength];
		
		write(h0, bodyData, 0);
		write(heartbeatResp.getRet().length(), bodyData, 1);
		byte[] bodyData1 = heartbeatResp.getRet().getBytes();
		System.arraycopy(bodyData1, 0, bodyData, 2, bodyData1.length);
		System.arraycopy(bodyData, 0, reqData, 32, bodyLength);
		
		byte[] footData = getFootData(bodyData, heartbeatResp.getAuth_code());
		
		System.arraycopy(footData, 0, reqData, 32 + bodyLength, footData.length);
	}
	
	/**
	 * 立即上报返回包 和工作计划
	 */
	public void contrData() {
		int bodyLength = length - 48;
		byte[] bodyData = new byte[bodyLength];
		System.out.println("contrData");
		//h0
		write(h0, bodyData, 0);
		write(contrResp.getRet().length(), bodyData, 1);
		byte[] bodyData1 = contrResp.getRet().getBytes();
		System.arraycopy(bodyData1, 0, bodyData, 2, bodyData1.length);
		//h1
		write(h1, bodyData, 2 + bodyData1.length);
		write(contrResp.getType().length() ,bodyData, 2 + bodyData1.length + 1);
		byte[] bodyData2 = contrResp.getType().getBytes();
		System.arraycopy(bodyData2, 0, bodyData, 2 + bodyData1.length + 2, bodyData2.length);
		System.out.println("ho h1");
		System.arraycopy(bodyData, 0, reqData, 32, bodyLength);
		
		byte[] footData = getFootData(bodyData, contrResp.getAuth_code());
		
		System.arraycopy(footData, 0, reqData, 32 + bodyLength, footData.length);
	}
	
	/**
	 * 上报包体
	 */
	public void assemblyReportData() {
		int bodyLength = length - 48;
		byte[] bodyData = new byte[bodyLength];
		
		write(h0, bodyData, 0);
		write(reportReq.getPackageType().length(), bodyData, 1);
		byte[] bodyData1 = reportReq.getPackageType().getBytes();
		System.arraycopy(bodyData1, 0, bodyData, 2, bodyData1.length);
		
		write(h1, bodyData, 2 + bodyData1.length);
		write(reportReq.getLongitude().length(), bodyData, 2 + bodyData1.length + 1);
		byte[] bodyData2 = reportReq.getLongitude().getBytes();
		System.arraycopy(bodyData2, 0, bodyData, 2 + bodyData1.length + 2, bodyData2.length);
		
		write(h2, bodyData, 2 + bodyData1.length + 2 + bodyData2.length);
		write(reportReq.getLatitude().length(), bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 1);
		byte[] bodyData3 = reportReq.getLatitude().getBytes();
		System.arraycopy(bodyData3, 0, bodyData, 2 + bodyData1.length + 2+ bodyData2.length + 2, bodyData3.length);
		
		// h7
		write(h7, bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length);
		write(reportReq.getPower().length(), bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length + 1);
		byte[] bodyData4 = reportReq.getPower().getBytes();
		System.arraycopy(bodyData4, 0, bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length + 2, bodyData4.length);
		
		// h8
		write(h8, bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length + 2 + bodyData4.length);
		write(reportReq.getSatellite().length(), bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length + 2 + bodyData4.length + 1);
		byte[] bodyData5 = reportReq.getSatellite().getBytes();
		System.arraycopy(bodyData5, 0, bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length + 2 + bodyData4.length + 2, bodyData5.length);
		
		// h9
		write(h9, bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length + 2 + bodyData4.length + 2 + bodyData5.length);
		write(reportReq.getReportTag().length(), bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length + 2 + bodyData4.length + 2 + bodyData5.length + 1);
		byte[] bodyData6 = reportReq.getReportTag().getBytes();
		System.arraycopy(bodyData6, 0, bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length + 2 + bodyData4.length + 2 + bodyData5.length + 2, bodyData6.length);
		
		// h11
		write(h11, bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length + 2 + bodyData4.length + 2 + bodyData5.length + 2 + bodyData6.length);
		write(reportReq.getAccuracy().length(), bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length + 2 + bodyData4.length + 2 + bodyData5.length + 2 + bodyData6.length + 1);
		byte[] bodyData7 = reportReq.getAccuracy().getBytes();
		System.arraycopy(bodyData7, 0, bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length + 2 + bodyData4.length + 2 + bodyData5.length + 2 + bodyData6.length + 2, bodyData7.length);

		// h12
		write(h12, bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length + 2 + bodyData4.length + 2 + bodyData5.length + 2 + bodyData6.length + 2 + bodyData7.length);
		write(reportReq.getReportTime().length(), bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length + 2 + bodyData4.length + 2 + bodyData5.length + 2 + bodyData6.length + 2 + bodyData7.length + 1);
		byte[] bodyData8 = reportReq.getReportTime().getBytes();
		System.arraycopy(bodyData8, 0, bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length + 2 + bodyData4.length + 2 + bodyData5.length + 2 + bodyData6.length + 2 + bodyData7.length + 2, bodyData8.length);
						
		System.arraycopy(bodyData, 0, reqData, 32, bodyLength);
		
		byte[] footData = getFootData(bodyData, reportReq.getAuth_code());
		
		System.arraycopy(footData, 0, reqData, 32 + bodyLength, footData.length);
	}

	/**
	 * 上报包体
	 */
	public void assemblyReportDataAuto() {
		int bodyLength = length - 48;
		byte[] bodyData = new byte[bodyLength];
		
		write(h0, bodyData, 0);
		write(reportReq.getPackageType().length(), bodyData, 1);
		byte[] bodyData1 = reportReq.getPackageType().getBytes();
		System.arraycopy(bodyData1, 0, bodyData, 2, bodyData1.length);
		
		write(h1, bodyData, 2 + bodyData1.length);
		write(reportReq.getLongitude().length(), bodyData, 2 + bodyData1.length + 1);
		byte[] bodyData2 = reportReq.getLongitude().getBytes();
		System.arraycopy(bodyData2, 0, bodyData, 2 + bodyData1.length + 2, bodyData2.length);
		
		write(h2, bodyData, 2 + bodyData1.length + 2 + bodyData2.length);
		write(reportReq.getLatitude().length(), bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 1);
		byte[] bodyData3 = reportReq.getLatitude().getBytes();
		System.arraycopy(bodyData3, 0, bodyData, 2 + bodyData1.length + 2+ bodyData2.length + 2, bodyData3.length);
		
		// h7
		write(h7, bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length);
		write(reportReq.getPower().length(), bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length + 1);
		byte[] bodyData4 = reportReq.getPower().getBytes();
		System.arraycopy(bodyData4, 0, bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length + 2, bodyData4.length);
		
		// h8
		write(h8, bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length + 2 + bodyData4.length);
		write(reportReq.getSatellite().length(), bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length + 2 + bodyData4.length + 1);
		byte[] bodyData5 = reportReq.getSatellite().getBytes();
		System.arraycopy(bodyData5, 0, bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length + 2 + bodyData4.length + 2, bodyData5.length);

		// h11
		write(h11, bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length + 2 + bodyData4.length + 2 + bodyData5.length);
		write(reportReq.getAccuracy().length(), bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length + 2 + bodyData4.length + 2 + bodyData5.length + 1);
		byte[] bodyData7 = reportReq.getAccuracy().getBytes();
		System.arraycopy(bodyData7, 0, bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length + 2 + bodyData4.length + 2 + bodyData5.length + 2, bodyData7.length);

		// h12
		write(h12, bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length + 2 + bodyData4.length + 2 + bodyData5.length+ 2 + bodyData7.length);
		write(reportReq.getReportTime().length(), bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length + 2 + bodyData4.length + 2 + bodyData5.length + 2 + bodyData7.length + 1);
		byte[] bodyData8 = reportReq.getReportTime().getBytes();
		System.arraycopy(bodyData8, 0, bodyData, 2 + bodyData1.length + 2 + bodyData2.length + 2 + bodyData3.length + 2 + bodyData4.length + 2 + bodyData5.length + 2 + bodyData7.length + 2, bodyData8.length);
						
		System.arraycopy(bodyData, 0, reqData, 32, bodyLength);
		
		byte[] footData = getFootData(bodyData, reportReq.getAuth_code());
		
		System.arraycopy(footData, 0, reqData, 32 + bodyLength, footData.length);
	}

	private static boolean write(int i, byte[] dest, int start) {
		dest[start] = (byte) i;
		return true;
	}

	private static boolean write(short b, byte[] dest, int start) {
		dest[start] = (byte) ((b >>> 8) & 0xFF);
		dest[start + 1] = (byte) ((b >>> 0) & 0xFF);
		return true;
	}
	
	private static boolean write(long b, byte[] dest, int start) {
		dest[start] = (byte) ((b >>> 24) & 0xFF);
		dest[start + 1] = (byte) ((b >>> 16) & 0xFF);
		dest[start + 2] = (byte) ((b >>> 8) & 0xFF);
		dest[start + 3] = (byte) ((b >>> 0) & 0xFF);
		return true;
	}
	
	/**
	 * 发送及接收消息
	 */
	public void send() {
		try {
			DatagramPacket pack = null;
			pack = new DatagramPacket(reqData, reqData.length,
					InetAddress.getByName(ip), port);
			DatagramSocket udpClient = new DatagramSocket();

			udpClient.send(pack);

			DatagramPacket pack2 = new DatagramPacket(new byte[1024], 1024);
			udpClient.setSoTimeout(Contant.TCP_TIMEOUT); // 超时时间，毫秒
			udpClient.receive(pack2);
			parseResp(cmd_id, pack2.getData());

		} catch (Exception e) {
			FileLog.e("Send UDP", e.toString() + "");
			regResp = null;
			login1Resp = null;
			login2Resp = null;
			reportResp = null;
		}
	}
	
	public void parseResp(short cmd_id, byte[] b) {
		// 包长
		byte bFirstLength = b[0];
		byte bSecondLength = b[1];
		int respLength = (int)((bSecondLength & 0xFF) | bFirstLength << 8);
		respLength &= 0x0000FFFF;
		// 获取包seq
		byte bFristSeq = b[28];
		byte bSecondSeq = b[29];
		int seq = (int) ((bSecondSeq & 0xFF) | bFristSeq << 8);
		seq &= 0x0000FFFF;
		this.seq = seq;
		// 获取包体
		byte[] bodyByte = new byte[respLength - 48];
		for(int i = 0; i < respLength - 48; i++) {
			bodyByte[i] = b[32 + i];
		}
				
		// 解析包体
		Map<String, String> map = new HashMap<String, String>();
		map = TlvUtil.tlvToObj(bodyByte);
		switch(cmd_id) {
		case 1:parseRegResp(map);break;
		case 21:parseLogin1Resp(map);break;
		case 22:parseLogin2Resp(map);break;
		case 11:parseReportResp(map);break;
		case 211:parseReportResp(map);break;
		case 4:parseHeartbeatResp(map);break;
		default:break;
		}
	}
	
	/**
	 * 解析注册返回包
	 *@param b
	 */
	public void parseRegResp(Map<String, String> map) {
		String ret = String.valueOf(h0);
		String device_id = String.valueOf(h1);
		regResp = new RegResp();
		if(map != null && map.containsKey(ret)) {
			String retValue = map.get(ret).toString();
			regResp.setRet(retValue);
			if("0".equals(retValue) && map.containsKey(device_id)) {
				regResp.setDevice_id(map.get(device_id).toString());
			}
		} else {
			regResp = null;
		}
			
	}
	
	/**
	 * 解析登录返回包
	 *@param b
	 */
	public void parseLogin1Resp(Map<String, String> map) {
		String ret = String.valueOf(h0);
		String adapter_ip = String.valueOf(h1);
		String adapter_port = String.valueOf(h2);
		String auth_code = String.valueOf(h3);
		String udp_adapter_port = String.valueOf(h5);
		String gps_time = String.valueOf(h6);
		login1Resp = new Login1Resp();
		if(map != null && map.containsKey(ret)) {
			String retValue = map.get(ret).toString();
			login1Resp.setRet(retValue);
			if("0".equals(retValue)) {
				if(map.containsKey(adapter_ip)) {
					login1Resp.setAdapter_ip(map.get(adapter_ip).toString());
				}
				if(map.containsKey(adapter_port)) {
					try {
						login1Resp.setAdapter_port(Integer.parseInt(map.get(adapter_port).toString()));
					} catch (NumberFormatException e) {
						login1Resp.setAdapter_port(0); // 容错
					}
				}
				//增加UDP端口
				if(map.containsKey(udp_adapter_port)) {
					try {
						login1Resp.setUdp_adapter_port(Integer.parseInt(map.get(udp_adapter_port).toString()));
					} catch (NumberFormatException e) {
						login1Resp.setUdp_adapter_port(0); // 容错
					}
				}
				if(map.containsKey(auth_code)) {
					login1Resp.setAuth_code(map.get(auth_code).toString());
				}
				// 增加搜星时间
				if(map.containsKey(gps_time)) {
					try {
						login1Resp.setGps_time(Long.parseLong(map.get(gps_time)));
					} catch (NumberFormatException e) {
						login1Resp.setGps_time(50); // 容错
					}
				}
			}
			
		} else {
			login1Resp = null;
		}
	}
	
	/**
	 * 解析二次登录返回包
	 *@param b
	 */
	public void parseLogin2Resp(Map<String, String> map) {
		String ret = String.valueOf(h0);
		String interval = String.valueOf(h1);
		String week = String.valueOf(h2);
		String timePeriod = String.valueOf(h3);
		String filterDate = String.valueOf(h4);
		String rept = String.valueOf(h19);
		login2Resp = new Login2Resp();
		if(map != null && map.containsKey(ret)) {
			String retValue = map.get(ret).toString();
			login2Resp.setRet(retValue);
			login2Resp.setSeq(seq);
			if("2".equals(retValue)) {
				if(map.containsKey(interval)) {
					try {
						login2Resp.setInterval(Integer.parseInt(map.get(interval).toString()));
					} catch (NumberFormatException e) {
						login2Resp.setInterval(60); // 默认值
					}
				}
				if(map.containsKey(week)) {
					login2Resp.setWeek(map.get(week).toString());
				}
				if(map.containsKey(timePeriod)) {
					login2Resp.setTimePeriod(map.get(timePeriod).toString());
				}
				if(map.containsKey(filterDate)) {
					login2Resp.setFilterDate(map.get(filterDate).toString());
				}
				if(map.containsKey(rept)) {
					login2Resp.setRept(map.get(rept).toString());
				}
			}
			
		} else {
			login2Resp = null;
		}
	}
	
	/**
	 * 解析上报返回包
	 *@param b
	 */
	public void parseReportResp(Map<String, String> map) {
		String ret = String.valueOf(h0);
		String interval = String.valueOf(h1);
		String week = String.valueOf(h2);
		String timePeriod = String.valueOf(h3);
		String filterDate = String.valueOf(h4);
		String rept = String.valueOf(h19);
		reportResp = new ReportResp();
		if(map != null && map.containsKey(ret)) {
			String retValue = map.get(ret).toString();
			reportResp.setSeq(seq);
			reportResp.setRet(retValue);
			if("2".equals(retValue)) {
				if(map.containsKey(interval)) {
					try {
						reportResp.setInterval(Integer.parseInt(map.get(interval).toString()));
					} catch (NumberFormatException e) {
						reportResp.setInterval(60); // 默认值
					}
				}
				if(map.containsKey(week)) {
					reportResp.setWeek(map.get(week).toString());
				}
				if(map.containsKey(timePeriod)) {
					reportResp.setTimePeriod(map.get(timePeriod).toString());
				}
				if(map.containsKey(filterDate)) {
					reportResp.setFilterDate(map.get(filterDate).toString());
				}
				if(map.containsKey(rept)) {
					reportResp.setRept(map.get(rept).toString());
				}
			}
			
		} else {
			login2Resp = null;
		}
	}
	
	/**
	 * 解析心跳返回包
	 *@param map
	 */
	public void parseHeartbeatResp(Map<String, String> map) {
		String ret = String.valueOf(h0);
		String rept = String.valueOf(h19);
		heartbeatResp = new HeartbeatResp();
		if(map != null && map.containsKey(ret)) {
			String retValue = map.get(ret).toString();
			heartbeatResp.setRet(retValue);
			if("0".equals(retValue)) {
				if(map.containsKey(rept)) {
					heartbeatResp.setRept(map.get(rept).toString());
				}
			}
			
		} else {
			heartbeatResp = null;
		}
	}
	
	/**
	 * UMMP协议的报文摘要
	 *@param bodyData
	 *@return
	 */
	private byte[] getFootData(byte[] bodyData, String auth_code) {
		byte[] authCodeData = auth_code.getBytes();
		byte[] md5Data = new byte[bodyData.length + authCodeData.length];
		System.arraycopy(bodyData, 0, md5Data, 0, bodyData.length);
		System.arraycopy(authCodeData, 0, md5Data, bodyData.length, authCodeData.length);
		MD5 md5 = new MD5();
		byte[] footData = md5.getKeyBeanofByte(md5Data);
		return footData;
	}
	
	private byte[] getFootData(String auth_code) {
		byte[] authCodeData = auth_code.getBytes();
		byte[] md5Data = new byte[authCodeData.length];
		System.arraycopy(authCodeData, 0, md5Data, 0, authCodeData.length);
		MD5 md5 = new MD5();
		byte[] footData = md5.getKeyBeanofByte(md5Data);
		return footData;
	}

	public short getCmd_id() {
		return cmd_id;
	}

	public void setCmd_id(short cmd_id) {
		this.cmd_id = cmd_id;
	}

	public short getLength() {
		return length;
	}

	public void setLength(short length) {
		this.length = length;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public byte[] getReqData() {
		return reqData;
	}

	public void setReqData(byte[] reqData) {
		this.reqData = reqData;
	}

	public RegReq getRegReq() {
		return regReq;
	}

	public void setRegReq(RegReq regReq) {
		this.regReq = regReq;
	}

	public RegResp getRegResp() {
		return regResp;
	}

	public void setRegResp(RegResp regResp) {
		this.regResp = regResp;
	}

	public Login1Req getLogin1Req() {
		return login1Req;
	}

	public void setLogin1Req(Login1Req login1Req) {
		this.login1Req = login1Req;
	}

	public Login1Resp getLogin1Resp() {
		return login1Resp;
	}

	public void setLogin1Resp(Login1Resp login1Resp) {
		this.login1Resp = login1Resp;
	}

	public Login2Req getLogin2Req() {
		return login2Req;
	}

	public void setLogin2Req(Login2Req login2Req) {
		this.login2Req = login2Req;
	}

	public Login2Resp getLogin2Resp() {
		return login2Resp;
	}

	public void setLogin2Resp(Login2Resp login2Resp) {
		this.login2Resp = login2Resp;
	}

	public ReportReq getReportReq() {
		return reportReq;
	}

	public void setReportReq(ReportReq reportReq) {
		this.reportReq = reportReq;
	}

	public ReportResp getReportResp() {
		return reportResp;
	}

	public void setReportResp(ReportResp reportResp) {
		this.reportResp = reportResp;
	}

	public HeartbeatReq getHeartbeatReq() {
		return heartbeatReq;
	}

	public void setHeartbeatReq(HeartbeatReq heartbeatReq) {
		this.heartbeatReq = heartbeatReq;
	}

	public HeartbeatResp getHeartbeatResp() {
		return heartbeatResp;
	}

	public void setHeartbeatResp(HeartbeatResp heartbeatResp) {
		this.heartbeatResp = heartbeatResp;
	}

	@Override
	public String toString() {
		return "UDPPackage [cmd_id=" + cmd_id + ", length=" + length + ", ip="
				+ ip + ", port=" + port + ", reqData="
				+ Arrays.toString(reqData) + ", h0=" + h0 + ", h1=" + h1
				+ ", h2=" + h2 + ", h3=" + h3 + ", h4=" + h4 + ", h7=" + h7
				+ ", h8=" + h8 + ", h9=" + h9 + ", h11=" + h11 + ", h12=" + h12
				+ ", h19=" + h19 + ", regReq=" + regReq + ", regResp="
				+ regResp + ", login1Req=" + login1Req + ", login1Resp="
				+ login1Resp + ", login2Req=" + login2Req + ", login2Resp="
				+ login2Resp + ", reportReq=" + reportReq + ", reportResp="
				+ reportResp + ", heartbeatReq=" + heartbeatReq
				+ ", heartbeatResp=" + heartbeatResp + "]";
	}
	
	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}
	
	
}

