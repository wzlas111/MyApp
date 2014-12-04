/**
 * Copyright (c) 2012-8-13 www.eastelsoft.com
 * $ID MainGridViewAdapter.java 上午11:24:13 $
 */
package com.eastelsoft.lbs.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eastelsoft.lbs.R;
import com.eastelsoft.util.Contant;

/**
 * 九宫格适配数据
 * 
 * @author lengcj
 */
public class MainGridViewAdapter extends BaseAdapter {

	// private String[] names = { "我的任务", "考勤签到", "考勤签退", "信息上报", "客户信息",
	// "客户拜访",
	// "销量上报", "公告通知", "事务提醒", "知识库", "参数设置" };
	// private int[] icons = { R.drawable.app_mytask, R.drawable.app_check,
	// R.drawable.app_checkout, R.drawable.app_inforeport,
	// R.drawable.app_custinfo, R.drawable.app_visit,
	// R.drawable.app_salesv,
	// R.drawable.app_setting,R.drawable.app_setting,R.drawable.app_setting,
	// R.drawable.app_setting };
	// private Context context;
	// LayoutInflater infalter;

	private String[] names;
	private int[] icons;
	private Context context;
	private LayoutInflater infalter;
	private String menus;

	public MainGridViewAdapter(Context context, String menus) {
		this.context = context;
		this.menus = menus;
		infalter = LayoutInflater.from(context);
		String[] menuValue = menus.split(",");
		List<String> tmp = new ArrayList<String>();
		for (int i = 0; i < menuValue.length; i++) {
			if (menuValue[i].length() == 2) {
				// 长度为2的是一级菜单
				if (!"08".equals(menuValue[i]) && !"09".equals(menuValue[i])) {
					tmp.add(menuValue[i]);

				}

			}
		}
		String[] menu = new String[tmp.size()];
		for (int j = 0; j < tmp.size(); j++) {
			menu[j] = tmp.get(j);
		}
		names = new String[menu.length];
		icons = new int[menu.length];
		for (int i = 0; i < menu.length; i++) {
			// 菜单名称
			names[i] = Contant.MENUS_MAP.get(menu[i]);
			// 菜单图标
			icons[i] = getDrawableId(context, "app_" + menu[i]);
		}

	}

	public static int getDrawableId(Context paramContext, String paramString) {
		return paramContext.getResources().getIdentifier(paramString,
				"drawable", paramContext.getPackageName());
	}

	// 返回gridview里面有多少个条目
	public int getCount() {
		return names.length;
	}

	// 返回某个position对应的条目
	public Object getItem(int position) {
		return position;
	}

	// 返回某个position对应的id
	public long getItemId(int position) {
		return position;
	}

	// 返回某个位置对应的视图
	public View getView(int position, View convertView, ViewGroup parent) {
		// Log.i(TAG,"GETVIEW "+ position);
		// 把一个布局文件转换成视图
		View view = infalter.inflate(R.layout.activity_main_item, null);
		ImageView iv = (ImageView) view.findViewById(R.id.main_gv_iv);
		TextView tv = (TextView) view.findViewById(R.id.main_gv_tv);
		// LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(90,
		// 88);
		// params.gravity = Gravity.CENTER;
		// iv.setLayoutParams(params);
		// 设置每一个item的名字和图标
		iv.setImageResource(icons[position]);
		tv.setText(names[position]);
		return view;
	}

}
