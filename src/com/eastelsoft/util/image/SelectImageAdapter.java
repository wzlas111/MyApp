package com.eastelsoft.util.image;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.eastelsoft.lbs.R;
import com.eastelsoft.util.image.MyImageView.OnMeasureListener;
import com.eastelsoft.util.image.NativeImageLoader.NativeImageCallBack;
import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SelectImageAdapter extends BaseAdapter implements OnScrollListener{

	private Point mPoint = new Point(0, 0);
	private HashMap<Integer, Boolean> mSelectMap = new HashMap<Integer, Boolean>();
	private LayoutInflater mInflater;
	private List<String> mList;
	private GridView mGridView;
	
	public SelectImageAdapter(Context context, List<String> list, GridView gridView) {
		mInflater = LayoutInflater.from(context);
		mList = list;
		mGridView = gridView;
		mGridView.setOnScrollListener(this);
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		final ViewHolder viewHolder;
		String path = mList.get(position);
		
		if(convertView == null){
			convertView = mInflater.inflate(R.layout.select_image_item, null);
			viewHolder = new ViewHolder();
			viewHolder.mImageView = (MyImageView) convertView.findViewById(R.id.child_image);
			viewHolder.mCheckBox = (CheckBox) convertView.findViewById(R.id.child_checkbox);
			viewHolder.mImageView.setOnMeasureListener(new OnMeasureListener() {
				
				@Override
				public void onMeasureSize(int width, int height) {
					mPoint.set(width, height);
				}
			});
			
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
			viewHolder.mImageView.setImageResource(R.drawable.friends_sends_pictures_no);
		}
		viewHolder.mImageView.setTag(path);
		viewHolder.mCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(!mSelectMap.containsKey(position) || !mSelectMap.get(position)){
					addAnimation(viewHolder.mCheckBox);
				}
				mSelectMap.put(position, isChecked);
			}
		});
		
		viewHolder.mCheckBox.setChecked(mSelectMap.containsKey(position) ? mSelectMap.get(position) : false);
		
		Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(path);
		if(bitmap != null){
			viewHolder.mImageView.setImageBitmap(bitmap);
		}else{
			viewHolder.mImageView.setImageResource(R.drawable.friends_sends_pictures_no);
		}
		return convertView;
	}
	
	private void addAnimation(View view){
		float [] vaules = new float[]{0.5f, 0.6f, 0.7f, 0.8f, 0.9f, 1.0f, 1.1f, 1.2f, 1.3f, 1.25f, 1.2f, 1.15f, 1.1f, 1.0f};
		AnimatorSet set = new AnimatorSet();
		set.playTogether(ObjectAnimator.ofFloat(view, "scaleX", vaules), 
				ObjectAnimator.ofFloat(view, "scaleY", vaules));
		set.setDuration(150);
		set.start();
	}
	
	public ArrayList<String> getSelectItems(){
		ArrayList<String> list = new ArrayList<String>();
		for(Iterator<Map.Entry<Integer, Boolean>> it = mSelectMap.entrySet().iterator(); it.hasNext();){
			Map.Entry<Integer, Boolean> entry = it.next();
			if(entry.getValue()){
				int position = entry.getKey();
				list.add(mList.get(position));
			}
		}
		return list;
	}
	
	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Object getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	public static class ViewHolder{
		public MyImageView mImageView;
		public CheckBox mCheckBox;
	}

    private int mFirstVisibleItem;  
    private int mVisibleItemCount; 
    private boolean first_start = true;
	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == SCROLL_STATE_IDLE || first_start) {
			loadBitmaps();
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		mFirstVisibleItem = firstVisibleItem;
		mVisibleItemCount = visibleItemCount;
		if (first_start && visibleItemCount > 0) {
			loadBitmaps();
			first_start = false;
		}
	}
	
	private void loadBitmaps() {
		for (int i = mFirstVisibleItem; i < mFirstVisibleItem+mVisibleItemCount; i++) {
			String path = mList.get(i);
			NativeImageLoader.getInstance().loadNativeImage(path, mPoint, new NativeImageCallBack() {
				@Override
				public void onImageLoader(Bitmap bitmap, String path) {
					ImageView mImageView = (ImageView) mGridView.findViewWithTag(path);
					if(bitmap != null && mImageView != null){
						mImageView.setImageBitmap(bitmap);
					}
				}
			});
		}
	}

}
