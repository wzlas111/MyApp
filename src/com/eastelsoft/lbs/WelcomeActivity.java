/**
 * Copyright (c) 2012-8-12 www.eastelsoft.com
 * $ID WelcomeActivity.java 下午10:34:55 $
 */
package com.eastelsoft.lbs;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.content.Intent.ShortcutIconResource;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

/**
 * 欢迎界面
 * 
 * @author lengcj
 */
public class WelcomeActivity extends Activity {
	private ViewPager mViewPager;
	private ArrayList<View> mPageViews;
	private ImageView mImageView;
	private ImageView[] mImageViews;
	private ViewGroup mainViewGroup;
	private ViewGroup indicatorViewGroup;
	private LayoutInflater mInflater;
	private Button btStart;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		
		// 生成快捷方式
		SharedPreferences sp = getSharedPreferences("userdata", 0);
		String short_cut = sp.getString("short_cut", "");
		if ("".equals(short_cut)) {
			addShortcut();
			Editor editor = sp.edit();
			editor.putString("short_cut", "1");
			editor.commit();
		}
		
//		String welcome_view = sp.getString("welcome_view", "");
//		if("".equals(welcome_view)) {
//			Editor editor = sp.edit();
//			editor.putString("welcome_view", "1");
//			editor.commit();
//		} else {
//			Intent intent = new Intent();
//			intent.setClass(WelcomeActivity.this, WaitActivity.class);
//			startActivity(intent);
//			finish();
//		}
		
		Intent intent = new Intent();
		intent.setClass(WelcomeActivity.this, WaitActivity.class);
		startActivity(intent);
		finish();
		
		mInflater = getLayoutInflater();
		mPageViews = new ArrayList<View>();
		mPageViews.add(mInflater.inflate(R.layout.w_item_1, null));
		mPageViews.add(mInflater.inflate(R.layout.w_item_2, null));
		mPageViews.add(mInflater.inflate(R.layout.w_item_3, null));
		mPageViews.add(mInflater.inflate(R.layout.w_item_4, null));
		View view1=View.inflate(this,R.layout.w_item_5,null);
		mPageViews.add(view1);
		mImageViews = new ImageView[mPageViews.size()];
		mainViewGroup = (ViewGroup) mInflater.inflate(
				R.layout.activity_welcome, null);
		mViewPager = (ViewPager) mainViewGroup.findViewById(R.id.myviewpager);
		indicatorViewGroup = (ViewGroup) mainViewGroup
				.findViewById(R.id.mybottomviewgroup);

		for (int i = 0; i < mImageViews.length; i++) {
			mImageView = new ImageView(WelcomeActivity.this);
			mImageView.setLayoutParams(new LayoutParams(20, 20));
			mImageView.setPadding(20, 0, 20, 0);

			if (i == 0) {
				mImageView
						.setBackgroundResource(R.drawable.page_indicator_focused);
			} else {
				mImageView.setBackgroundResource(R.drawable.page_indicator);
			}

			mImageViews[i] = mImageView;

			indicatorViewGroup.addView(mImageViews[i]);
		}

		setContentView(mainViewGroup);

		mViewPager.setAdapter(new MyPagerAdapter());
		mViewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int arg0) {
				// TODO Auto-generated method stub
				for (int i = 0; i < mImageViews.length; i++) {
					if (i == arg0) {
						mImageViews[i]
								.setBackgroundResource(R.drawable.page_indicator_focused);
					} else {
						mImageViews[i]
								.setBackgroundResource(R.drawable.page_indicator);
					}
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
				// TODO Auto-generated method stub

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
				// TODO Auto-generated method stub

			}
		});
		
		// 
		btStart = (Button)view1.findViewById(R.id.btStart);
		btStart.setOnClickListener(new OnBtStartClickListenerImpl());
	}
	private class OnBtStartClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			Intent intent = new Intent();
			intent.setClass(WelcomeActivity.this, WaitActivity.class);
			startActivity(intent);
			finish();
		}
	};
	class MyPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return mPageViews.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public int getItemPosition(Object object) {
			// TODO Auto-generated method stub
			return super.getItemPosition(object);
		}

		@Override
		public void destroyItem(View arg0, int arg1, Object arg2) {
			// TODO Auto-generated method stub
			((ViewPager) arg0).removeView(mPageViews.get(arg1));
		}

		@Override
		public Object instantiateItem(View arg0, int arg1) {
			// TODO Auto-generated method stub
			((ViewPager) arg0).addView(mPageViews.get(arg1));
			return mPageViews.get(arg1);
		}

		@Override
		public void restoreState(Parcelable arg0, ClassLoader arg1) {
			// TODO Auto-generated method stub

		}

		@Override
		public Parcelable saveState() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void startUpdate(View arg0) {
			// TODO Auto-generated method stub

		}

		@Override
		public void finishUpdate(View arg0) {
			// TODO Auto-generated method stub

		}

	}

	private void addShortcut() {
		Intent shortcut = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");

		// 快捷方式的名称
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				getString(R.string.app_name));
		shortcut.putExtra("duplicate", false); // 不允许重复创建

		Intent shortcutIntent = new Intent(Intent.ACTION_MAIN);
		shortcutIntent.setClassName(this, this.getClass().getName());
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent);

		// 快捷方式的图标
		ShortcutIconResource iconRes = Intent.ShortcutIconResource.fromContext(
				this, R.drawable.icon);
		shortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, iconRes);

		sendBroadcast(shortcut);
	}
}
