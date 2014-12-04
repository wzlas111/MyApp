/**
 * Copyright (c) 2012-8-18 www.eastelsoft.com
 * $ID HelpActivity.java 下午9:46:19 $
 */
package com.eastelsoft.lbs;

import java.util.ArrayList;

import android.app.Activity;
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
import android.widget.Button;
import android.widget.ImageView;

import com.eastelsoft.util.FileLog;

/**
 * 帮助界面UI
 * 
 * @author lengcj
 */
public class HelpActivity extends Activity {
	private static final String TAG = "HelpActivity";
	private Button btBack;
	
	private ViewPager mViewPager;
	private ArrayList<View> mPageViews;
	private ImageView mImageView;
	private ImageView[] mImageViews;
	private ViewGroup mainViewGroup;
	private ViewGroup indicatorViewGroup;
	private LayoutInflater mInflater;
	//private Button btStart;
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mInflater = getLayoutInflater();

		mPageViews = new ArrayList<View>();
		mPageViews.add(mInflater.inflate(R.layout.w_item_1, null));
		mPageViews.add(mInflater.inflate(R.layout.w_item_2, null));
		mPageViews.add(mInflater.inflate(R.layout.w_item_3, null));
		mPageViews.add(mInflater.inflate(R.layout.w_item_4, null));
		//View view1=View.inflate(this,R.layout.w_item_5,null);
		//mPageViews.add(view1);
		
		//View viewTop=View.inflate(this,R.layout.activity_help,null);
		
		mImageViews = new ImageView[mPageViews.size()];
		mainViewGroup = (ViewGroup) mInflater.inflate(
				R.layout.activity_help, null);
		
		mViewPager = (ViewPager) mainViewGroup.findViewById(R.id.myviewpager);
		indicatorViewGroup = (ViewGroup) mainViewGroup
				.findViewById(R.id.mybottomviewgroup);

		for (int i = 0; i < mImageViews.length; i++) {
			mImageView = new ImageView(HelpActivity.this);
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
      
		btBack = (Button) findViewById(R.id.btBack);
		//btBack.setBackgroundResource(R.drawable.selector_go_back_true);
        btBack.setOnClickListener(new OnBtBackClickListenerImpl());  
        
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
		
	}
	
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
	
	private class OnBtBackClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				HelpActivity.this.finish();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}
}
