package com.eastelsoft.lbs.activity.client;

import java.util.Arrays;
import java.util.List;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.bean.ClientDto.ClientBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

public class ClientAdapter extends BaseAdapter implements SectionIndexer{
	
	private static String[] mSections = { "#", "A", "B", "C", "D", "E", "F",
		"G", "H", "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S",
		"T", "U", "V", "W", "X", "Y", "Z" };
	private int[] mPositions;
	
	private Context mContext;
	private List<ClientBean> mList;
	
	public ClientAdapter(Context context, List<ClientBean> list) {
		mContext = context;
		mList = list;
		
		initPosition();
	}
	
	private void initPosition() {
		mPositions = new int[mSections.length];
		Arrays.fill(mPositions, -1);
		
		for (int i = 0; i < mList.size(); i++) {
			ClientBean bean = mList.get(i);
			String py = bean.py;
			String py_index = "#";
			if (py != null && !"".equals(py)) {
				py_index = py.substring(0, 1);
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
		ClientBean bean = mList.get(position);
		if (convertView == null) {
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(mContext).inflate(R.layout.client_listview_item, null);
			viewHolder.sectionTv = (TextView)convertView.findViewById(R.id.sectionTextView);
			viewHolder.fullNameTv = (TextView)convertView.findViewById(R.id.full_name);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		if (isFirstInSection(position)) {
			viewHolder.sectionTv.setVisibility(View.VISIBLE);
			String py = bean.py;
			String py_index = "#";
			if (py != null && !"".equals(py)) {
				py_index = py.substring(0, 1);
			}
			viewHolder.sectionTv.setText(py_index);
			System.out.println("getView : "+position);
		} else {
			viewHolder.sectionTv.setVisibility(View.GONE);
		}
		
		viewHolder.fullNameTv.setText(bean.client_name);
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
	}

}
