package com.eastelsoft.lbs.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.adapter.InfoListViewAdapter.ViewHolder;
import com.eastelsoft.lbs.entity.InfoBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class InfoListItemAdapterA extends BaseAdapter {

	private LayoutInflater mInflater;
	private ArrayList<InfoBean> arraylists;
	// public static Map<Integer, Boolean> isSelected;
	private Context context;

	public InfoListItemAdapterA(Context context, ArrayList<InfoBean> arraylists) {
		super();
		this.arraylists = arraylists;
		this.context = context;
		mInflater = LayoutInflater.from(context);

	}

	public void changeArrayList(ArrayList<InfoBean> al) {
		this.arraylists = al;

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
			convertView = mInflater.inflate(R.layout.info_list_item, null);
			holder.task_img = (ImageView) convertView
					.findViewById(R.id.info_imgFile);
			holder.task_title = (TextView) convertView
					.findViewById(R.id.info_title);
			holder.task_time = (TextView) convertView
					.findViewById(R.id.info_uploadDate);
			holder.task_istijiao = (ImageView) convertView
					.findViewById(R.id.info_istijiao);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.task_img.setBackgroundResource(arraylists.get(position)
				.getInfo_imgFile());
		holder.task_title.setText(arraylists.get(position).getTitle());
		holder.task_time.setText(arraylists.get(position).getUploadDate());

		if ("11".equals(arraylists.get(position).getIstijiao())) {
			holder.task_istijiao.setBackgroundResource(R.drawable.visittijiao);
		} else {
			holder.task_istijiao
					.setBackgroundResource(R.drawable.visitnottijiao);
		}
		System.out.println(arraylists.get(position).getIstijiao()+"  "+arraylists.get(position).getTitle() );
		return convertView;
	}

	public final class ViewHolder {
		public ImageView task_img;
		public TextView task_title;
		public TextView task_time;
		public ImageView task_istijiao;
	}

}
