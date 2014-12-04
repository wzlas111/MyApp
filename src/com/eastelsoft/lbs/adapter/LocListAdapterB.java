package com.eastelsoft.lbs.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.adapter.InfoListViewAdapter.ViewHolder;
import com.eastelsoft.lbs.entity.InfoBean;
import com.eastelsoft.lbs.entity.LocBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LocListAdapterB extends BaseAdapter {

	private LayoutInflater mInflater;
	private ArrayList<LocBean> arraylists;
	// public static Map<Integer, Boolean> isSelected;
	private Context context;

	public LocListAdapterB(Context context, ArrayList<LocBean> arraylists) {
		super();
		this.arraylists = arraylists;
		this.context = context;
		mInflater = LayoutInflater.from(context);

	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return arraylists.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return arraylists.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		ViewHolder holder = null;
		// convertView为null的时候初始化convertView。
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.icheck_list_item, null);
			holder.task_img = (ImageView) convertView
					.findViewById(R.id.info_imgFile);
			holder.task_locTime = (TextView) convertView
					.findViewById(R.id.locTime);
			holder.task_accuracy = (TextView) convertView
					.findViewById(R.id.accuracy);

			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.task_img.setBackgroundResource(R.drawable.line_bg);
		holder.task_locTime.setText(arraylists.get(position).getLocTime());
		
		
		
		if (arraylists.get(position).getAddr() != null&&!"".equals(arraylists.get(position).getAddr())) {
			holder.task_accuracy.setText(arraylists.get(position).getAddr());
		}else{
			holder.task_accuracy.setText("经度："+arraylists.get(position).getLon()+"  纬度："+arraylists.get(position).getLat());
		}

		return convertView;
	}

	public final class ViewHolder {
		public ImageView task_img;
		public TextView task_locTime;
		public TextView task_accuracy;
	}

}
