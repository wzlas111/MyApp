package com.eastelsoft.lbs.activity.select;

import java.util.List;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.bean.SelectBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class SelectAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<SelectBean> mList;
	private String mChecked_id;
	
	public SelectAdapter(Context context, List<SelectBean> list, String checked_id) {
		mContext = context;
		mList = list;
		mChecked_id = checked_id;
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
		SelectBean bean = mList.get(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.widget_select_item, null);
			viewHolder = new ViewHolder();
			viewHolder.nameTv = (TextView)convertView.findViewById(R.id.name);
			viewHolder.checkedView = convertView.findViewById(R.id.checked);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		viewHolder.nameTv.setText(bean.name);
		if (mChecked_id != null && !"".equals(mChecked_id)) {
			if (mChecked_id.equals(bean.id)) {
				viewHolder.checkedView.setVisibility(View.VISIBLE);
			} else {
				viewHolder.checkedView.setVisibility(View.INVISIBLE);
			}
		} else {
			viewHolder.checkedView.setVisibility(View.INVISIBLE);
		}
		
		return convertView;
	}

	public class ViewHolder {
		public TextView nameTv;
		public View checkedView;
	}
	
}
