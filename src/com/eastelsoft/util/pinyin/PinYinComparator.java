package com.eastelsoft.util.pinyin;

import java.util.Comparator;

import com.eastelsoft.lbs.bean.DealerDto;

public class PinYinComparator implements Comparator<DealerDto.DealerBean> {

	@Override
	public int compare(DealerDto.DealerBean lhs, DealerDto.DealerBean rhs) {
		if (lhs.py_index == null|| rhs.py_index == null) {
			return -1;
		}
		return (lhs.py_name).compareTo(rhs.py_name);
	}

}
