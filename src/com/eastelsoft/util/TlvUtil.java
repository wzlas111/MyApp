/**
 * Copyright (c) 2012-7-21 www.eastelsoft.com
 * $ID TlvUtil.java 下午6:30:48 $
 */
package com.eastelsoft.util;

import java.util.HashMap;
import java.util.Map;

/**
 * TLV格式解析类
 * @author lengcj
 */
public class TlvUtil {

	/**
	 *TLV格式数据转换为对象
	 *@param data
	 *@return
	 */
	public static Map<String, String> tlvToObj(byte[] data)  {
		Map<String, String> map = new HashMap<String, String>();
		while(data.length > 0) {
			byte bTag = data[0];
			String tag = String.valueOf(bTag);
			byte bLen = data[1];
			String len = String.valueOf(bLen);
			byte[] bValue = new byte[Integer.parseInt(len)];
			for(int i = 0; i < Integer.parseInt(len); i++) {
				bValue[i] = data[i+2];
			}
			String value = new String(bValue);
			/**
			TlvInfo tlvInfo = new TlvInfo(Integer.parseInt(tag), 
					Integer.parseInt(len), 
					value);
			tlvInfos.add(tlvInfo);
			*/
			map.put((String)tag, value); // 用map是为了后面方便操作
			if(data.length > (2 + Integer.parseInt(len))) {
				int tmpLen = data.length - (2 + Integer.parseInt(len));
				byte[] tmp = new byte[tmpLen];
				for(int i = 0; i < tmpLen; i++) {
					tmp[i] = data[i + (2 + Integer.parseInt(len))];
				}
				data = tmp;
			} else {
				break;
			}
		}
		
		return map;
	}
}
