/**
 * Copyright (c) 2012-7-30 www.eastelsoft.com
 * $ID TcpPackage.java 下午2:43:22 $
 */
package com.eastelsoft.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import com.eastelsoft.lbs.entity.ContrReq;
import com.eastelsoft.lbs.entity.HeartbeatReq;
import com.eastelsoft.lbs.entity.Login2Resp;
import com.eastelsoft.lbs.entity.ReportResp;

/**
 * TCP报文解析
 * 
 * @author lengcj
 */
public class TcpPackage {
	private int h0 = 0|0x40;
	private int h1 = 1|0x40;
	private int h2 = 2|0x40;
	private int h3 = 3|0x40;
	private int h4 = 4|0x40;
	//private int h5 = 5|0x40;
	//private int h6 = 6|0x40;
	//private int h7 = 7|0x40;
	//private int h8 = 8|0x40;
	//private int h9 = 9|0x40;
	//private int h10 = 10|0x40;
	//private int h11 = 11|0x40;
	//private int h12 = 12|0x40;
	//private int h13 = 13|0x40;
	
	private int h19 = 19|0x40;
		
	private Login2Resp login2Resp;
		
	private ReportResp reportResp;
	
	private HeartbeatReq heartbeatReq;
	
	private ContrReq contrReq;
	
	private int seq = 0;
	
	private int command_id;
	
	public void parseResp(byte[] b) {
		// 包长
		byte bFirstLength = b[0];
		byte bSecondLength = b[1];
		int respLength = (int) ((bSecondLength & 0xFF) | bFirstLength << 8);
		respLength &= 0x0000FFFF;
		// command_id
		byte bFirstCmd = b[4];
		byte bSecondCmd = b[5];
		int command_id = (int) ((bSecondCmd & 0xFF) | bFirstCmd << 8);
		command_id &= 0x0000FFFF;
		this.command_id = command_id;
		
		
		// 获取包seq
		byte bFristSeq = b[28];
		byte bSecondSeq = b[29];
		int seq = (int) ((bSecondSeq & 0xFF) | bFristSeq << 8);
		seq &= 0x0000FFFF;
		this.seq = seq;
		// 获取包体
		byte[] bodyByte = new byte[respLength - 48];
		for (int i = 0; i < respLength - 48; i++) {
			bodyByte[i] = b[32 + i];
		}
		// 解析包体
		Map<String, String> map = new HashMap<String, String>();
		map = TlvUtil.tlvToObj(bodyByte);
		
		
		switch(command_id) {
		case 0x0007:
			// 配置下发请求，结构同二次登录返回包
			parseLogin2Resp(map);
			break;
		case 0x8002:
			// 二次登录返回包
			parseLogin2Resp(map);
			break;
		case 0x8011:
			// 上报，签到返回包
			parseReportResp(map);
			break;
		case 0x0004:
			// 心跳
			parseHeartbeatReq(map);
			break;
		case 0x0008:
			// 立即上报请求
			parseContrReq(map);
			break;
		default:break;
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
			reportResp = null;
		}
	}
	
	/**
	 * 解析心跳包
	 *@param map
	 */
	public void parseHeartbeatReq(Map<String, String> map) {
		String ret = String.valueOf(h0);
		contrReq = new ContrReq();
		if(map != null && map.containsKey(ret)) {
			String retValue = map.get(ret).toString();
			heartbeatReq.setType(retValue);
			
		} else {
			heartbeatReq = null;
		}
	}
	
	public void parseContrReq(Map<String, String> map) {
		
		
		String ret = String.valueOf(h0);
		//是h19吗
		String type = String.valueOf(h19); 
		contrReq = new ContrReq();
		if(map != null && map.containsKey(ret)) {
			
			String retValue = map.get(ret).toString();
			contrReq.setCmd(retValue);
			
			if(map != null && map.containsKey(type)){
			
			String num = map.get(type).toString();
			contrReq.setNum(num);
			
			
			}
			
			
		} else {
			
			contrReq = null;
		}
	}
	
	public ContrReq getContrReq() {
		return contrReq;
	}

	public void setContrReq(ContrReq contrReq) {
		this.contrReq = contrReq;
	}

	public Login2Resp getLogin2Resp() {
		return login2Resp;
	}

	public void setLogin2Resp(Login2Resp login2Resp) {
		this.login2Resp = login2Resp;
	}

	public ReportResp getReportResp() {
		return reportResp;
	}

	public void setReportResp(ReportResp reportResp) {
		this.reportResp = reportResp;
	}

	public int getSeq() {
		return seq;
	}

	public void setSeq(int seq) {
		this.seq = seq;
	}

	public int getCommand_id() {
		return command_id;
	}

	public void setCommand_id(int command_id) {
		this.command_id = command_id;
	}
	
}
