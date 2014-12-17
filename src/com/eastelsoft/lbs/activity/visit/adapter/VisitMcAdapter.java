package com.eastelsoft.lbs.activity.visit.adapter;

import java.util.List;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.bean.VisitMcBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class VisitMcAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<VisitMcBean> mList;
	
	public VisitMcAdapter(Context context, List<VisitMcBean> list) {
		mContext = context;
		mList = list;
		
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
		VisitMcBean bean = mList.get(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.visit_mc_listview_item, null);
			viewHolder = new ViewHolder();
			viewHolder.client_name = (TextView)convertView.findViewById(R.id.client_name);
			viewHolder.start_time = (TextView)convertView.findViewById(R.id.start_time);
			viewHolder.is_upload = (TextView)convertView.findViewById(R.id.is_upload);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		String is_upload = bean.is_upload;
		String msg = "";
		if ("0".equals(is_upload)) {//出发
			msg = "提交中";
		} else if("1".equals(is_upload)) {//到达
			msg = "已提交";
		} 
		viewHolder.client_name.setText(bean.client_name);
		viewHolder.start_time.setText(bean.start_time);
		viewHolder.is_upload.setText(msg);
		
		return convertView;
	}
	
	class ViewHolder {
		TextView client_name;
		TextView start_time;
		TextView is_upload;
	}

}
