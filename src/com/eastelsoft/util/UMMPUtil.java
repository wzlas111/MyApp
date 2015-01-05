/**
 * Copyright (c) 2013-3-21 www.eastelsoft.com
 * $ID UmmpUtil.java 下午4:42:45 $
 */
package com.eastelsoft.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import android.location.Location;

import com.eastelsoft.lbs.entity.AuthCentreResp;
import com.eastelsoft.lbs.entity.Login1Resp;
import com.eastelsoft.lbs.entity.Login2Resp;
import com.eastelsoft.lbs.entity.RegResp;
import com.eastelsoft.lbs.entity.ReportResp;
import com.eastelsoft.lbs.entity.SetInfo;
import com.eastelsoft.lbs.location.BaseStationAction.SCell;

/**
 * UMMP协议UDP TCP 接口实现
 * 
 * @author lengcj
 */
public class UMMPUtil {
	private static final String TAG = "UMMPUtil";
	
	private static int h0 = 0|0x40;
	private static int h1 = 1|0x40;
	private static int h2 = 2|0x40;
	private static int h3 = 3|0x40;
	private static int h4 = 4|0x40;
	private static int h5 = 5|0x40;
	private static int h6 = 6|0x40;
	private static int h7 = 7|0x40;
	private static int h8 = 8|0x40;
	private static int h9 = 9|0x40;
	private static int h10 = 10|0x40;
	private static int h11 = 11|0x40;
	private static int h12 = 12|0x40;
	private static int h13 = 13|0x40;
	private static int h19 = 19|0x40;
	
	public static byte[] sendTcp(byte[] data, String ip, int port) {
		Socket socket = null;
		InputStream in = null;
		OutputStream out = null;
		try {
			FileLog.i("Send TCP", "ip=" + ip + ";port=" + port);
			socket = new Socket(ip, port);
			socket.setSoTimeout(Contant.TCP_TIMEOUT);
			//socket.setKeepAlive(true);
			in = socket.getInputStream();
			out = socket.getOutputStream();
			out.write(data);
			out.flush();
			int length = 0;
			byte[] buffer = new byte[1024]; 
			length = in.read(buffer);
			if (length > 0) {
				// 解析获取到的信息
				return buffer;
			}
		} catch (Exception e) {
			FileLog.e("Send TCP", e.toString() + "");
			
		} finally {
			try {
				if (in != null)
					in.close();
				if (out != null)
					out.close();
				if (socket != null)
					socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	/**
	 * UDP方式发送数据包
	 *@param reqData
	 *@param ip
	 *@param port
	 *@return
	 */
	public static byte[] send(byte[] reqData, String ip, int port) {
		DatagramPacket pack = null;
		DatagramSocket udpClient = null;
		try {
			pack = new DatagramPacket(reqData, reqData.length,
					InetAddress.getByName(ip), port);
			udpClient = new DatagramSocket();
			udpClient.send(pack);
			DatagramPacket pack2 = new DatagramPacket(new byte[1024], 1024);
			udpClient.setSoTimeout(Contant.TCP_TIMEOUT); // 超时时间，毫秒
			udpClient.receive(pack2);
			return pack2.getData();
		} catch (Exception e) {
			FileLog.e("Send UDP", e.toString() + "");
			return null;
		} finally {
			try {
				if(udpClient != null) {
					udpClient.disconnect();
					udpClient.close();
				}
				if(pack != null) {
					pack = null;
				}
				System.gc();
			} catch (Exception e) {
				FileLog.e("Close UDP", e.toString() + "");
			}
		}
	}
	
	
	public static AuthCentreResp sendAuthCentreReg(short msg_seq, String serialNumber) {
		AuthCentreResp authCentreResp = null;
		// 组长包体
		int tags[] = { h0 };
		String values[] = new String[tags.length];
		values[0] = serialNumber; // 手机号码

		byte[] bodyData = encodeBodyData(tags, values);

		// 组装包头
		byte[] headData = encodeHeadData((short) bodyData.length,
				Contant.AUTH_COMMAND_ID, msg_seq, (long) 0,
				Contant.DEF_DEVICE_ID, // 注册消息，使用默认设备ID
				(short) 0);

		// 组装包尾 ，注册包使用默认校验码
		byte[] footData = encodeFootData(bodyData, Contant.DEF_AUTH_CODE);

		// 数据包
		byte[] data = encodeData(headData, bodyData, footData);

		try {
			// 使用UDP方式发送
			byte[] receData = send(data, Contant.AUTHCENTRE_IP,
					Contant.AUTHCENTRE_PORT);

			authCentreResp = (AuthCentreResp) decodeResp(Contant.AUTH_CMD_ID,
					receData);
		} catch (Exception e) {
			FileLog.e(TAG, e);
			/*
			 * try { // 使用TCP方式发送 byte[] receData = sendTcp(data,
			 * Contant.GATE_IP, Contant.GATE_TCP_PORT); regResp =
			 * (RegResp)decodeResp(Contant.REG_CMD_ID, receData); } catch
			 * (Exception e1) { FileLog.e(TAG, e); }
			 */
		}
//		try { // 使用TCP方式发送 
//			byte[] receData = sendTcp(data,Contant.AUTHCENTRE_IP,Contant.AUTHCENTRE_PORT); 
//			authCentreResp = (AuthCentreResp) decodeResp(Contant.AUTH_CMD_ID,receData); 
//		} catch(Exception e) { 
//			FileLog.e(TAG, e); 
//		}
		return authCentreResp;
	}
	
	
	
	/**
	 * 用户注册
	 *@param msg_seq 序列号，注册接口填0
	 *@param serialNumber 手机号码
	 *@param imei
	 *@param imsi
	 *@param phoneBrand 手机品牌
	 *@param phoneModel 手机型号
	 *@param phoneOs 手机操作系统版本
	 *@param softVersion 软件版本
	 *@param phoneResolution 屏幕分辨率
	 *@return
	 */
	public static RegResp sendReg(short msg_seq, 
			String serialNumber,
			String imei,
			String imsi,
			String phoneBrand,
			String phoneModel,
			String phoneOs,
			String softVersion,
			String phoneResolution,
			SetInfo set
			) {
		RegResp regResp = null;
		// 组长包体
					int tags[] = {h0,h1,h2,h3,h6,h7,h8,h9,h10};
					String values[] = new String[tags.length];
					values[0] = Contant.DEVICE_TYPE; // 终端类型
					values[1] = serialNumber; // 手机号码
					values[2] = imei; // imei
					values[3] = imsi; // imsi
					values[4] = phoneBrand; // 手机品牌
					values[5] = phoneModel; // 手机型号
					values[6] = phoneOs; // 手机操作系统
					values[7] = softVersion; // 随E行版本号
					values[8] = phoneResolution; // 手机分辨率

					byte[] bodyData = encodeBodyData(tags, values);
					
					// 组装包头
					byte[] headData = encodeHeadData((short)bodyData.length ,
							Contant.REG_COMMAND_ID,
							msg_seq, 
							(long)0,
							Contant.DEF_DEVICE_ID, // 注册消息，使用默认设备ID
							(short)0);
					
					// 组装包尾 ，注册包使用默认校验码
					byte[] footData = encodeFootData(bodyData, Contant.DEF_AUTH_CODE); 
					
					// 数据包
					byte[] data = encodeData(headData, bodyData, footData);

		try {
			// 使用UDP方式发送
			byte[] receData = send(data, set.getGateip(), set.getGateudpport());
			regResp = (RegResp)decodeResp(Contant.REG_CMD_ID, receData);
		} catch (Exception e) {
			FileLog.e(TAG, e);
			try {
				// 使用TCP方式发送
				byte[] receData = sendTcp(data, set.getGateip(), set.getGatetcpport());
				regResp = (RegResp)decodeResp(Contant.REG_CMD_ID, receData);
			} catch (Exception e1) {
				FileLog.e(TAG, e);
			}
		}
		return regResp;
	}
	
	/**
	 * 认证中心登录
	 *@param msg_seq msg_seq 序列号，注册接口填0
	 *@param set 数据集合
	 *@param softVersion 软件版本
	 *@return
	 */
	public static Login1Resp sendLogin1(short msg_seq, 
			SetInfo set, 
			String phoneModel, 
			String softVersion) {
		Login1Resp login1Resp = null;
		// 组长包体
					int tags[] = {h0,h1,h2,h3,h6,h7};
					String values[] = new String[tags.length];
					values[0] = Contant.DEVICE_TYPE; // 终端类型
					values[1] = set.getSerialNumber(); // 手机号码
					values[2] = set.getImei(); // imei
					values[3] = set.getImsi(); // imsi
					values[4] = softVersion; // 随E行版本号
					values[5] = phoneModel; // 手机型号

					byte[] bodyData = encodeBodyData(tags, values);
					
					// 组装包头
					byte[] headData = encodeHeadData((short)bodyData.length ,
							Contant.LOGIN1_COMMAND_ID,
							msg_seq, 
							(long)0,
							set.getDevice_id(), 
							(short)0);
					
					// 组装包尾 ，登录包使用默认校验码
					byte[] footData = encodeFootData(bodyData, Contant.DEF_AUTH_CODE); 
					// 数据包
					byte[] data = encodeData(headData, bodyData, footData);
		try {
			byte[] receData = send(data, set.getGateip(), set.getGateudpport());
			login1Resp = (Login1Resp)decodeResp(Contant.LOGIN1_CMD_ID, receData);
//			byte[] receData = sendTcp(data, Contant.GATE_IP, Contant.GATE_TCP_PORT);
//			login1Resp = (Login1Resp)decodeResp(Contant.LOGIN1_CMD_ID, receData);
		} catch (Exception e) {
			FileLog.e(TAG, e);
			try {
				// 使用TCP方式发送
				byte[] receData = sendTcp(data, set.getGateip(), set.getGatetcpport());
				login1Resp = (Login1Resp)decodeResp(Contant.LOGIN1_CMD_ID, receData);
			} catch (Exception e1) {
				FileLog.e(TAG, e);
			}
		}
		return login1Resp;
	}
	
	public static Login2Resp sendLogin2(String adapter_ip, int adapter_port, byte[] data) {
		try {
			// 使用TCP方式发送
			byte[] receData = sendTcp(data, adapter_ip, adapter_port);
			Login2Resp login2Resp = (Login2Resp)decodeResp(Contant.LOGIN2_CMD_ID, receData);
			return login2Resp;
		} catch (Exception e) {
			FileLog.e(TAG, e);
		}
		return null;
	}
	
	public static Login2Resp sendLogin2UDP(String adapter_ip, int udp_adapter_port, byte[] data) {
		try {
			// 使用UDP方式发送
			byte[] receData = send(data, adapter_ip, udp_adapter_port);
			Login2Resp login2Resp = (Login2Resp)decodeResp(Contant.LOGIN2_CMD_ID, receData);
			return login2Resp;
		} catch (Exception e) {
			FileLog.e(TAG, e);
		}
		return null;
	}
	
	/**
	 * 组装二次登录请求包
	 *@param msg_seq
	 *@param set
	 *@param login1Resp
	 *@return
	 */
	public static byte[] getLogin2ReqData(short msg_seq, 
			SetInfo set, 
			Login1Resp login1Resp) {
		// 组长包体
		int tags[] = {h0,h1};
		String values[] = new String[tags.length];
		values[0] = set.getImei(); // imei
		values[1] = login1Resp.getAuth_code(); // 验证码

		byte[] bodyData = encodeBodyData(tags, values);
		
		// 组装包头
		byte[] headData = encodeHeadData((short)bodyData.length ,
				Contant.LOGIN2_COMMAND_ID,
				msg_seq, 
				(long)0,
				set.getDevice_id(), 
				(short)0);
		
		// 组装包尾 ，登录包使用默认校验码
		byte[] footData = encodeFootData(bodyData, login1Resp.getAuth_code()); 
		
		// 数据包
		byte[] data = encodeData(headData, bodyData, footData);

		return data;
	}
	
	public static ReportResp sendReportUDP(String package_type,
			short msg_seq, 
			Location location,
			String uploadDate,
			SetInfo set, 
			String power, 
			SCell cell,
			String reportTag,
			int signalStrengthValue) {
		if("0".equals(reportTag) || "1".equals(reportTag)) {
			return sendReport(package_type, msg_seq, location,uploadDate,set,power,cell, reportTag, signalStrengthValue);
		} else {
			return sendReport(package_type, msg_seq, location,uploadDate,set,power,cell, signalStrengthValue);
		}
	}
	
	public static ReportResp sendReport(String package_type,
			short msg_seq, 
			String lon,
			String lat,
			String acc,
			String uploadDate,
			String power,
			String state,
			String signalStrengthValue,
			String cell,
			SetInfo set) {
		ReportResp reportResp = null;
		// 组长包体
		int tags[] = {h0,h1,h2,h6,h7,h8,h11,h12,h13};
		String values[] = new String[tags.length];
		values[0] = package_type; // 包类型
		values[1] = lon; // 经度
		values[2] = lat; // 纬度
		values[3] = signalStrengthValue; // 纬度
		values[4] = power; // 纬度
		values[5] = state; // 卫星个数，先给一个默认值 
		values[6] = acc; // 精度
		values[7] = uploadDate;
		values[8] = cell; // 基站信息
		byte[] bodyData = encodeBodyData(tags, values);
		
		// 组装包头
		byte[] headData = encodeHeadData((short)bodyData.length ,
				Contant.REPORT_COMMAND_ID,
				msg_seq, 
				(long)0,
				set.getDevice_id(),
				(short)0);
		
		// 组装包尾 
		byte[] footData = encodeFootData(bodyData, set.getAuth_code());
		// 数据包
		byte[] data = encodeData(headData, bodyData, footData);	
		try {		
			byte[] receData = send(data, set.getAdapter_ip(), set.getUdp_adapter_port());
			reportResp = (ReportResp)decodeResp(Contant.REPORT_CMD_ID, receData);
		} catch (Exception e) {
			FileLog.e(TAG, e);
			try {
				// 使用TCP方式发送
				byte[] receData = sendTcp(data, set.getAdapter_ip(), set.getAdapter_port());
				reportResp = (ReportResp)decodeResp(Contant.REPORT_CMD_ID, receData);
			} catch (Exception e1) {
				FileLog.e(TAG, e);
			}
		}
		return reportResp;
	}
	
	/**
	 * 定时上报，立即上报
	 *@param package_type 上报参数10表示定位数据包，11表示延迟上报数据包
	 *@param msg_seq 包序号
	 *@param location 位置数据
	 *@param uploadtime 上报时间
	 *@param set 基础参数 设备ID，验证码，IP，UDP端口
	 *@param power 电量
	 *@param cell 基站信息
	 *@return
	 */
	public static ReportResp sendReport(String package_type,
			short msg_seq, 
			Location location,
			String uploadtime,
			SetInfo set, 
			String power, 
			SCell cell,
			int signalStrengthValue) {
		ReportResp reportResp = null;
		// 组长包体
		int tags[] = {h0,h1,h2,h6,h7,h8,h11,h12,h13};
		String values[] = new String[tags.length];
		values[0] = package_type; // 包类型
		values[1] = Util.format(location.getLongitude(),"#.######"); // 经度
		values[2] = Util.format(location.getLatitude(),"#.######"); // 纬度
		values[3] = String.valueOf(Math.abs(signalStrengthValue)); // 纬度
		values[4] = power; // 纬度
		values[5] = "3"; // 卫星个数，先给一个默认值 
		values[6] = Util.format(String.valueOf(location.getAccuracy())); // 精度
		values[7] = uploadtime;
		values[8] = "0,0,0,0"; // 基站信息
		if(cell != null)
			values[8] = cell.MCC+","+cell.MNC+","+cell.LAC+","+cell.CID;
		byte[] bodyData = encodeBodyData(tags, values);
		
		// 组装包头
		byte[] headData = encodeHeadData((short)bodyData.length ,
				Contant.REPORT_COMMAND_ID,
				msg_seq, 
				(long)0,
				set.getDevice_id(),
				(short)0);
		
		// 组装包尾 
		byte[] footData = encodeFootData(bodyData, set.getAuth_code());
		// 数据包
		byte[] data = encodeData(headData, bodyData, footData);	
		try {		
			byte[] receData = send(data, set.getAdapter_ip(), set.getUdp_adapter_port());
			reportResp = (ReportResp)decodeResp(Contant.REPORT_CMD_ID, receData);
		} catch (Exception e) {
			FileLog.e(TAG, e);
			try {
				// 使用TCP方式发送
				byte[] receData = sendTcp(data, set.getAdapter_ip(), set.getAdapter_port());
				reportResp = (ReportResp)decodeResp(Contant.REPORT_CMD_ID, receData);
			} catch (Exception e1) {
				FileLog.e(TAG, e);
			}
		}
		return reportResp;
	}
	
	/**
	 * 签到，签退
	 *@param package_type 上报参数10表示定位数据包，11表示延迟上报数据包
	 *@param msg_seq 包序号
	 *@param location 位置数据
	 *@param uploadtime 上报时间
	 *@param set 基础参数
	 *@param power 电量
	 *@param cell 基站信息
	 *@param reportTag 签到，签退标识
	 *@return
	 */
	public static ReportResp sendReport(String package_type,
			short msg_seq, 
			Location location,
			String uploadtime,
			SetInfo set, 
			String power, 
			SCell cell,
			String reportTag,
			int signalStrengthValue) {
		ReportResp reportResp = null;
		// 组长包体
		int tags[] = {h0,h1,h2,h6,h7,h8,h9,h11,h12,h13};
		String values[] = new String[tags.length];
		values[0] = package_type; // 包类型
		values[1] = Util.format(location.getLongitude(),"#.######"); // 经度
		values[2] = Util.format(location.getLatitude(),"#.######"); // 纬度
		values[3] = String.valueOf(Math.abs(signalStrengthValue)); // 纬度
		values[4] = power; // 纬度
		values[5] = "3"; // 卫星个数，先给一个默认值 
		values[6] = reportTag; // 精度
		values[7] = Util.format(String.valueOf(location.getAccuracy())); // 精度
		values[8] = uploadtime;
		values[9] = "0,0,0,0"; // 基站信息
		if(cell != null)
			values[9] = cell.MCC+","+cell.MNC+","+cell.LAC+","+cell.CID;
		byte[] bodyData = encodeBodyData(tags, values);
		
		// 组装包头
		byte[] headData = encodeHeadData((short)bodyData.length ,
				Contant.CHECK_COMMAND_ID,
				msg_seq, 
				(long)0,
				set.getDevice_id(),
				(short)0);
		
		// 组装包尾 
		byte[] footData = encodeFootData(bodyData, set.getAuth_code());
		
		// 数据包
		byte[] data = encodeData(headData, bodyData, footData);
		try {
			byte[] receData = send(data, set.getAdapter_ip(), set.getUdp_adapter_port());
			reportResp = (ReportResp)decodeResp(Contant.CHECK_CMD_ID, receData);
		} catch (Exception e) {
			FileLog.e(TAG, e);
			try {
				// 使用TCP方式发送
				byte[] receData = sendTcp(data, set.getAdapter_ip(), set.getAdapter_port());
				reportResp = (ReportResp)decodeResp(Contant.CHECK_CMD_ID, receData);
			} catch (Exception e1) {
				FileLog.e(TAG, e);
			}
		}
		return reportResp;
	}
	
	/**
	 * 组装所有数据
	 *@param headData
	 *@param bodyData
	 *@param footData
	 *@return
	 */
	public static byte[] encodeData(byte[] headData, byte[] bodyData, byte[] footData) {
		byte[] data = new byte[headData.length + bodyData.length + footData.length];
		System.arraycopy(headData, 0, data, 0, headData.length);
		System.arraycopy(bodyData, 0, data, headData.length, bodyData.length);
		System.arraycopy(footData, 0, data, headData.length + bodyData.length, footData.length);
		return data;
	}
	
	/**
	 * 组装包头数据
	 *@param length
	 *@param command_id
	 *@param msg_seq
	 *@param timestamp
	 *@param device_id
	 *@param ack
	 *@return
	 */
	public static byte[] encodeHeadData(short length, 
			short command_id, 
			short msg_seq, 
			long timestamp, 
			String device_id, 
			short ack) {
		byte[] headerData = new byte[32];
		length = (short)(length + 48);
		write(length, headerData, 0);
		headerData[2] = 1;
		headerData[3] = 0;
		write(command_id, headerData, 4);
		write(msg_seq, headerData, 6);
		write(timestamp, headerData, 8);
		System.arraycopy(device_id.getBytes(), 0, headerData, 12, device_id.getBytes().length);
		write(ack, headerData, 28);
		headerData[30] = 0;
		headerData[31] = 0; 
		return headerData;
	}
	
	/**
	 * 组装包体，
	 * 长度为：数据长度  +  包头长度(32) + 包尾长度(16) + TLV中的T和L的长度，T和L长度均未1　　　　　　　
	 *@param length
	 *@param tags
	 *@param values
	 *@return
	 */
	public static byte[] encodeBodyData(int[] tags, 
			String[] values) {
		// 
		int length = tags.length * 2 + encodeBodyDataLength(values);
		int tmpLength = 0;
		byte[] bodyData = new byte[length];
		for(int i = 0; i < tags.length; i++) {
			write(tags[i], bodyData, tmpLength);
			byte[] bytes = values[i].getBytes();
			write(bytes.length, bodyData, tmpLength + 1);
			System.arraycopy(bytes, 0, bodyData, tmpLength + 2, bytes.length);
			tmpLength = tmpLength + 2 + bytes.length;
		}
		return bodyData;
	}
	
	/**
	 * 获取数组中所有数据的长度
	 *@param values
	 *@return
	 */
	public static int encodeBodyDataLength(String[] values) {
		int length = 0;
		for(String value:values) {
			length += value.getBytes().length;
		}
		return length;
	}
	
	/**
	 * UMMP协议的报文摘要
	 *@param bodyData
	 *@return
	 */
	public static byte[] encodeFootData(byte[] bodyData, 
			String auth_code) {
		byte[] authCodeData = auth_code.getBytes();
		byte[] md5Data = new byte[bodyData.length + authCodeData.length];
		System.arraycopy(bodyData, 0, md5Data, 0, bodyData.length);
		System.arraycopy(authCodeData, 0, md5Data, bodyData.length, authCodeData.length);
		MD5 md5 = new MD5();
		byte[] footData = md5.getKeyBeanofByte(md5Data);
		return footData;
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
	 * 解析接口返回数据包
	 *@param cmd_id
	 *@param b
	 *@return
	 */
	public static Object decodeResp(short cmd_id, byte[] b) {
		Object obj = null;
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
		// 获取包体
		byte[] bodyByte = new byte[respLength - 48];
		for(int i = 0; i < respLength - 48; i++) {
			bodyByte[i] = b[32 + i];
		}
				
		// 解析包体
		Map<String, String> map = new HashMap<String, String>();
		map = TlvUtil.tlvToObj(bodyByte);
		switch(cmd_id) {
		case Contant.AUTH_CMD_ID:obj = decodeAuthCentreResp(map);break;
		case Contant.REG_CMD_ID:obj = decodeRegResp(map);break;
		case Contant.LOGIN1_CMD_ID:obj = decodeLogin1Resp(map);break;
		case Contant.LOGIN2_CMD_ID:obj = decodeLogin2Resp(map);break;
		case Contant.CHECK_CMD_ID:obj = decodeReportResp(map, seq);break;
		case Contant.REPORT_CMD_ID:obj = decodeReportResp(map, seq);break;
		
//		case 4:parseHeartbeatResp(map);break;
		default:break;
		}
		return obj;
	}
	
	public static ReportResp decodeReportResp(Map<String, String> map, int seq) {
		String ret = String.valueOf(h0);
		String interval = String.valueOf(h1);
		String week = String.valueOf(h2);
		String timePeriod = String.valueOf(h3);
		String filterDate = String.valueOf(h4);
		String rept = String.valueOf(h19);
		ReportResp reportResp = new ReportResp();
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
			reportResp = null;
		}
		return reportResp;
	}
	
	public static AuthCentreResp decodeAuthCentreResp(Map<String, String> map) {
		String ret = String.valueOf(h0);
		String gateip = String.valueOf(h1);
		String gatetcpport = String.valueOf(h2);
		String gateudpport = String.valueOf(h3);
		String httpip = String.valueOf(h4);
		String httpport = String.valueOf(h5);
		String pfsign = String.valueOf(h6);
		
		AuthCentreResp authCentreResp = new AuthCentreResp();
		if(map != null && map.containsKey(ret)) {
			String retValue = map.get(ret).toString();
			
			authCentreResp.setRet(retValue);
			if("0".equals(retValue)) {
				if(map.containsKey(gateip)) {
					authCentreResp.setGateip(map.get(gateip).toString());	
				}
				if(map.containsKey(gatetcpport)) {
					authCentreResp.setGatetcpport(map.get(gatetcpport).toString());	
				}
				if(map.containsKey(gateudpport)) {
					authCentreResp.setGateudpport(map.get(gateudpport).toString());
				}
				if(map.containsKey(httpip)) {
					authCentreResp.setHttpip(map.get(httpip).toString());	
				}
				if(map.containsKey(httpport)) {
					authCentreResp.setHttpport(map.get(httpport).toString());
				}
				if(map.containsKey(pfsign)) {
					authCentreResp.setPfsign(map.get(pfsign).toString());
				}
			}	
		} else {
			authCentreResp = null;
		}
		return authCentreResp;
	}
	
	
	public static RegResp decodeRegResp(Map<String, String> map) {
		String ret = String.valueOf(h0);
		String device_id = String.valueOf(h1);
		RegResp regResp = new RegResp();
		if(map != null && map.containsKey(ret)) {
			String retValue = map.get(ret).toString();
			regResp.setRet(retValue);
			if("0".equals(retValue) && map.containsKey(device_id)) {
				regResp.setDevice_id(map.get(device_id).toString());
			}
		} else {
			regResp = null;
		}
		return regResp;
	}
	
	public static Login1Resp decodeLogin1Resp(Map<String, String> map) {
		String ret = String.valueOf(h0);
		String adapter_ip = String.valueOf(h1);
		String adapter_port = String.valueOf(h2);
		String auth_code = String.valueOf(h3);
		String udp_adapter_port = String.valueOf(h5);
		String gps_time = String.valueOf(h6);
		Login1Resp login1Resp = new Login1Resp();
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
		return login1Resp;
	}
	
	public static Login2Resp decodeLogin2Resp(Map<String, String> map) {
		String ret = String.valueOf(h0);
		String interval = String.valueOf(h1);
		String week = String.valueOf(h2);
		String timePeriod = String.valueOf(h3);
		String filterDate = String.valueOf(h4);
		String rept = String.valueOf(h19);
		Login2Resp login2Resp = new Login2Resp();
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
		return login2Resp;
	}
}	
