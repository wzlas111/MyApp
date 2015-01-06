package com.eastelsoft.lbs.activity.visit.adapter;

import java.util.List;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.bean.VisitBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class VisitAdapter extends BaseAdapter {
	
	private Context mContext;
	private List<VisitBean> mList;
	
	public VisitAdapter(Context context, List<VisitBean> list) {
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
		VisitBean bean = mList.get(position);
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.visit_listview_item, null);
			viewHolder = new ViewHolder();
			viewHolder.visit_img_red = (ImageView)convertView.findViewById(R.id.visit_img_red);
			viewHolder.visit_img_blue = (ImageView)convertView.findViewById(R.id.visit_img_blue);
			viewHolder.visit_title = (TextView)convertView.findViewById(R.id.visit_title);
			viewHolder.start_time = (TextView)convertView.findViewById(R.id.start_time);
			viewHolder.status = (TextView)convertView.findViewById(R.id.status);
			convertView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder)convertView.getTag();
		}
		
		String status = bean.status;
		String msg = "";
		int msg_color = mContext.getResources().getColor(R.color.is_upload_red);
		viewHolder.visit_img_red.setVisibility(View.VISIBLE);
		viewHolder.visit_img_blue.setVisibility(View.GONE);
		if ("0".equals(status)) {//出发
			msg = "已出发";
		} else if("1".equals(status)) {//到达
			msg = "已到达";
		} else if("2".equals(status)) {//提交成功
			msg = "已上传";
			msg_color = mContext.getResources().getColor(R.color.is_upload_blue);
			viewHolder.visit_img_blue.setVisibility(View.VISIBLE);
			viewHolder.visit_img_red.setVisibility(View.GONE);
		} else if("3".equals(status)) {//表单上传中
			msg = "上传中";
		} else if("4".equals(status)) {//图片上传中
			msg = "图片上传中";
		} else if("5".equals(status)) {//确认完成
			msg = "拜访完成";
			msg_color = mContext.getResources().getColor(R.color.is_upload_blue);
			viewHolder.visit_img_blue.setVisibility(View.VISIBLE);
			viewHolder.visit_img_red.setVisibility(View.GONE);
		} else if("9".equals(status)) {//上传失败
			msg = "上传失败";
		}
		viewHolder.visit_title.setText(bean.dealer_name);
		viewHolder.start_time.setText(bean.start_time);
		viewHolder.status.setTextColor(msg_color);
		viewHolder.status.setText(msg);
		
		return convertView;
	}
	
	class ViewHolder {
		ImageView visit_img_red;
		ImageView visit_img_blue;
		TextView visit_title;
		TextView start_time;
		TextView status;
	}

}
