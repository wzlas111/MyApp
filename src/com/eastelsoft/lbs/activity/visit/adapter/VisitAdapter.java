package com.eastelsoft.lbs.activity.visit.adapter;

import java.util.List;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.bean.VisitBean;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class VisitAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<VisitBean> mList;
	private int mBlueColor;
	private int mRedClolor;
	private Drawable mBlueImg;
	private Drawable mRedImg;
	
	public VisitAdapter(Context context, List<VisitBean> list) {
		mContext = context;
		mList = list;
		
		mBlueColor = context.getResources().getColor(R.color.is_upload_blue);
		mRedClolor = context.getResources().getColor(R.color.is_upload_red);
		mBlueImg = context.getResources().getDrawable(R.drawable.check);
		mRedImg = context.getResources().getDrawable(R.drawable.tt_album_img_selected);
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
		VisitBean bean = mList.get(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.visit_listview_item, null);
			viewHolder = new ViewHolder();
			viewHolder.visit_img = (ImageView)convertView.findViewById(R.id.visit_img);
			viewHolder.visit_title = (TextView)convertView.findViewById(R.id.visit_title);
			viewHolder.start_time = (TextView)convertView.findViewById(R.id.start_time);
			viewHolder.status = (TextView)convertView.findViewById(R.id.status);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		String status = bean.status;
		String msg = "";
		int msg_color = mRedClolor;
		viewHolder.visit_img.setImageDrawable(mRedImg);
		if ("0".equals(status)) {//出发
			msg = "已出发";
		} else if("1".equals(status)) {//到达
			msg = "已到达";
		} else if("2".equals(status)) {//提交成功
			msg = "已上传";
			msg_color = mBlueColor;
			viewHolder.visit_img.setImageDrawable(mBlueImg);
		} else if("3".equals(status)) {//提交失败
			msg = "上传失败";
		}
		viewHolder.visit_title.setText(bean.dealer_name);
		viewHolder.start_time.setText(bean.start_time);
		viewHolder.status.setTextColor(msg_color);
		viewHolder.status.setText(msg);
		
		return convertView;
	}
	
	class ViewHolder {
		ImageView visit_img;
		TextView visit_title;
		TextView start_time;
		TextView status;
	}

}
