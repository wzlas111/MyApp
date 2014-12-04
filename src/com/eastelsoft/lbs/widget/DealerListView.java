package com.eastelsoft.lbs.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.ListAdapter;
import android.widget.ListView;

public class DealerListView extends ListView {
	protected IndexScroller mScroller = null;
	protected GestureDetector mGestureDetector = null;
	
	public DealerListView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}
	
	public void createScroller(){
		mScroller = new IndexScroller(getContext(), this);
		mScroller.setAutoHide(true);
		mScroller.setShowIndexContainer(true);
		
		mScroller.show();
	}
	
	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		if (mScroller != null) {
			mScroller.draw(canvas);
		}
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev)
	{
		// Intercept ListView's touch event
		if (mScroller != null && mScroller.onTouchEvent(ev))
			return true;

		if (mGestureDetector == null)
		{
			mGestureDetector = new GestureDetector(getContext(),
					new GestureDetector.SimpleOnGestureListener()
					{
						@Override
						public boolean onFling(MotionEvent e1, MotionEvent e2,
								float velocityX, float velocityY)
						{
							mScroller.show();
							return super.onFling(e1, e2, velocityX, velocityY);
						}

					});
		}
		mGestureDetector.onTouchEvent(ev);

		return super.onTouchEvent(ev);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev)
	{
		return true;
	}
	
	@Override
	public void setAdapter(ListAdapter adapter){
		super.setAdapter(adapter);
		if (mScroller != null)
			mScroller.setAdapter(adapter);
	}

	@Override
	protected void onSizeChanged(int w, int h, int oldw, int oldh){
		super.onSizeChanged(w, h, oldw, oldh);
		if (mScroller != null)
			mScroller.onSizeChanged(w, h, oldw, oldh);
	}
}
