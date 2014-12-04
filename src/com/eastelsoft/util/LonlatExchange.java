package com.eastelsoft.util;

public class LonlatExchange {

	/**
	 * @param args
	 */
	public static String exchangTo(String str){
		String[] s = str.split("\\.");
		double minstr = Double.parseDouble(s[1])/(Math.pow(10, s[1].length()));
		double min=minstr*60;
		String[] s1 = (min+"").split("\\.");
		double secstr = Double.parseDouble(s1[1])/(Math.pow(10, s1[1].length()));
		double sec=secstr*60;
		String[] s2 = (sec+"").split("\\.");
		return s[0]+"°"+s1[0]+"'"+s2[0]+"."+s2[1].substring(0, 4)+"\"";
	}

}
