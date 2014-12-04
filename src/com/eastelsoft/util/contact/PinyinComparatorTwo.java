package com.eastelsoft.util.contact;

import java.util.Comparator;

import com.eastelsoft.lbs.entity.CustBean;

public class PinyinComparatorTwo implements Comparator<CustBean> {

	@Override
	public int compare(CustBean arg0, CustBean arg1) {

		/*
		 * String str1 = PingYinUtil.getPingYin((String) arg0.getClientName());
		 * String str2 = PingYinUtil.getPingYin((String) arg1.getClientName());
		 */
		String str1 = arg0.getClientNamePinYin();
		String str2 = arg1.getClientNamePinYin();
		if (str1 == null
				|| "".equals(str1)) {
			str1 = arg0.getClientName();
		}
		if (str2 == null
				|| "".equals(str2)) {
			str2 = arg1.getClientName();
		}
		
		return str1.compareTo(str2);
	}

}
