package com.eastelsoft.util;

import android.view.View;
import android.view.animation.TranslateAnimation;

public class MoveBg {
	/**
	 * �ƶ�����
	 * 
	 * @param v
	 *            ��Ҫ�ƶ���View
	 * @param startX
	 *            ��ʼx���
	 * @param toX
	 *            ��ֹx���
	 * @param startY
	 *            ��ʼy���
	 * @param toY
	 *            ��ֹy���
	 */
	public static void moveFrontBg(View v, int startX, int toX, int startY, int toY) {
		TranslateAnimation anim = new TranslateAnimation(startX, toX, startY, toY);
		anim.setDuration(200);
		anim.setFillAfter(true);
		v.startAnimation(anim);
	}
}
