/**
 * Copyright (c) 2012-7-21 www.eastelsoft.com
 * $ID Mobile.java 上午12:42:13 $
 */
package com.eastelsoft.util;

/**
 * 手机号码检测
 */
public class Mobile {
	private static String regMobileStr = "^1(([3][456789])|([5][01789])|([8][78]))[0-9]{8}$";
	private static String regMobile3GStr = "^((157)|(18[78]))[0-9]{8}$";
	private static String regUnicomStr = "^1(([3][012])|([5][6])|([4][5])|([8][56]))[0-9]{8}$";
	private static String regUnicom3GStr = "^((156)|(18[56]))[0-9]{8}$";
	private static String regTelecomStr = "^1(([3][3])|([5][3])|([8][09]))[0-9]{8}$";
	private static String regTelocom3GStr = "^(18[09])[0-9]{8}$";

	private String mobile = "";
	private int facilitatorType = -1;
	private boolean isLawful = false;
	private boolean is3G = false;

	public Mobile(String mobile) {
		this.setMobile(mobile);
	}

	public void setMobile(String mobile) {
		if (mobile == null) {
			return;
		}
		/** */
		/** 第一步判断中国移动 */
		if (mobile.matches(Mobile.regMobileStr)) {
			this.mobile = mobile;
			this.setFacilitatorType(0);
			this.setLawful(true);
			if (mobile.matches(Mobile.regMobile3GStr)) {
				this.setIs3G(true);
			}
		}
		/** */
		/** 第二步判断中国联通 */
		else if (mobile.matches(Mobile.regUnicomStr)) {
			this.mobile = mobile;
			this.setFacilitatorType(1);
			this.setLawful(true);
			if (mobile.matches(Mobile.regUnicom3GStr)) {
				this.setIs3G(true);
			}
		}
		/** */
		/** 第三步判断中国电脑 */
		else if (mobile.matches(Mobile.regTelecomStr)) {
			this.mobile = mobile;
			this.setFacilitatorType(2);
			this.setLawful(true);
			if (mobile.matches(Mobile.regTelocom3GStr)) {
				this.setIs3G(true);
			}
		}
	}

	public String getMobile() {
		return mobile;
	}

	public int getFacilitatorType() {
		return facilitatorType;
	}

	public boolean isLawful() {
		return isLawful;
	}

	public boolean isIs3G() {
		return is3G;
	}

	private void setFacilitatorType(int facilitatorType) {
		this.facilitatorType = facilitatorType;
	}

	private void setLawful(boolean isLawful) {
		this.isLawful = isLawful;
	}

	private void setIs3G(boolean is3G) {
		this.is3G = is3G;
	}

	public String toString() {
		StringBuffer str = new StringBuffer();
		str.append("mobile:").append(this.getMobile()).append(",")
				.append("facilitatorType:").append(this.getFacilitatorType())
				.append(",").append("isLawful:").append(this.isLawful())
				.append(",").append("is3G:").append(this.isIs3G()).append(";");
		return str.toString();
	}

}
