package com.eastelsoft.util.pinyin;

import java.util.ArrayList;

import com.eastelsoft.util.pinyin.HanziToPinyinA.Token;

public class PinyinUtil {
	public static String getPinYin(String input) {
		ArrayList<Token> tokens = HanziToPinyinA.getInstance().get(input);
		StringBuilder sb = new StringBuilder();
		if (tokens != null && tokens.size() > 0) {
			for (Token token : tokens) {
				if (Token.PINYIN == token.type) {
					sb.append(token.target);
				} else {
					sb.append(token.source);
				}
			}
		}
		return sb.toString().toLowerCase();
	}
}
