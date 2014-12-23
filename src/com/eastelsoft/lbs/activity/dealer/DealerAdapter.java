package com.eastelsoft.lbs.activity.dealer;

import java.util.Arrays;
import java.util.List;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.bean.DealerDto.DealerBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class DealerAdapter extends BaseAdapter implements SectionIndexer{
	
	private static String[] mSections = { "#", "A", "B", "C", "D", "E", "F",
		"G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S",
		"T", "U", "V", "W", "X", "Y", "Z" };
	private int[] mPositions;
	
	private Context mContext;
	private List<DealerBean> mList;
	
	public DealerAdapter(Context context, List<DealerBean> list) {
		mContext = context;
		mList = list;
		
		initPosition();
	}
	
	private void initPosition() {
		mPositions = new int[mSections.length];
		Arrays.fill(mPositions, -1);
		
		for (int i = 0; i < mList.size(); i++) {
			DealerBean bean = mList.get(i);
			String py = bean.first_py;
			String py_index = "#";
			if (py != null && !"".equals(py)) {
				py_index = py.substring(0, 1).toUpperCase();
			}
			int sectionIndex = Arrays.binarySearch(mSections, py_index);
			if (sectionIndex < 0 || sectionIndex > mSections.length) {
				sectionIndex = 0;
			}
			if (mPositions[sectionIndex] == -1) {
				mPositions[sectionIndex] = i;
			}
		}
		
		int lastPos = -1;
		// now loop through, for all the ones not found, set position to the one
		// before them
		// this is to make sure the array is sorted for binary search to work
		for (int i = 0; i < mSections.length; i++)
		{
			if (mPositions[i] == -1)
				mPositions[i] = lastPos;
			lastPos = mPositions[i];
		}
	}
	
	private boolean isFirstInSection(int position) {
		int section = Arrays.binarySearch(mPositions, position);
		return section > -1;
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

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder = null;
		DealerBean bean = mList.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.dealer_listview_item, null);
			viewHolder.sectionTv = (TextView)convertView.findViewById(R.id.sectionTextView);
			viewHolder.fullNameTv = (TextView)convertView.findViewById(R.id.full_name);
//			viewHolder.telephoneTv = (TextView)convertView.findViewById(R.id.telephpne);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		if (isFirstInSection(position)) {
			viewHolder.sectionTv.setVisibility(View.VISIBLE);
			String py = bean.first_py;
			String py_index = "#";
			if (py != null && !"".equals(py)) {
				py_index = py.substring(0, 1).toUpperCase();
			}
			viewHolder.sectionTv.setText(py_index);
		} else {
			viewHolder.sectionTv.setVisibility(View.GONE);
		}
		
		viewHolder.fullNameTv.setText(bean.dealer_name);
//		viewHolder.telephoneTv.setText(bean.telephone);
		return convertView;
	}

	@Override
	public Object[] getSections() {
		return mSections;
	}

	@Override
	public int getPositionForSection(int sectionIndex) {
		return mPositions[sectionIndex];
	}

	@Override
	public int getSectionForPosition(int position) {
		int index = Arrays.binarySearch(mPositions, position);
		return index;
	}
	
	public class ViewHolder {
		public TextView sectionTv;
		public TextView fullNameTv;
		public TextView telephoneTv;
	}

}
