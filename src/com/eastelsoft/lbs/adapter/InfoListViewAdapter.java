/**
 * Copyright (c) 2012-8-13 www.eastelsoft.com
 * $ID InfoListViewAdapter.java 下午8:45:47 $
 */
package com.eastelsoft.lbs.adapter;

import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.db.DBUtil;
import com.eastelsoft.lbs.db.LocationSQLiteHelper;

/**
 * 上报记录
 * 
 * @author lengcj
 */
public class InfoListViewAdapter extends BaseAdapter {    
    private LayoutInflater mInflater;    
    public List<HashMap<String, Object>> mData;    
    //public static Map<Integer, Boolean> isSelected;
    private Context context;
    
    public InfoListViewAdapter(Context context) {
    	this.context = context;
        mInflater = LayoutInflater.from(context);    
        init();    
    }    
    
    //初始化    
    private void init() {
        LocationSQLiteHelper locationHelper = new LocationSQLiteHelper(context, null, null, 5);
        mData = DBUtil.getDataFromLInfo(locationHelper.getWritableDatabase());
        if(locationHelper != null)
        	locationHelper.getWritableDatabase().close();
    }    
    
    @Override    
    public int getCount() {    
        return mData.size();    
    }    
    
    @Override    
    public Object getItem(int position) {    
        return mData.get(position);    
    }    
    
    @Override    
    public long getItemId(int position) {    
        return 0;    
    }    
    
    @Override    
    public View getView(int position, View convertView, ViewGroup parent) {
    	@SuppressWarnings("unchecked")
		final HashMap<String, Object> map = (HashMap<String, Object>)getItem(position);
        ViewHolder holder = null;    
        //convertView为null的时候初始化convertView。    
        if (convertView == null) {    
            holder = new ViewHolder();    
            convertView = mInflater.inflate(R.layout.info_list_item, null);    
            holder.task_img = (ImageView) convertView.findViewById(R.id.info_imgFile);    
            holder.task_title = (TextView) convertView.findViewById(R.id.info_title);    
            holder.task_time = (TextView) convertView.findViewById(R.id.info_uploadDate);    
            convertView.setTag(holder);    
        } else {    
            holder = (ViewHolder) convertView.getTag();    
        }    
        holder.task_img.setBackgroundResource((Integer) mData.get(position).get(    
                "task_img"));    
        holder.task_title.setText(mData.get(position).get("task_title").toString());    
        holder.task_time.setText(mData.get(position).get("task_time").toString());  

		// 对ListView中的每一行信息配置OnClick事件
		convertView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Toast.makeText(context,
						"[convertView.setOnClickListener]点击了" 
						+ map.get("task_title").toString(),
						Toast.LENGTH_SHORT).show();
			}

		});

		// 对ListView中的每一行信息配置OnLongClick事件
		convertView.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				Toast.makeText(
						context,
						"[convertView.setOnLongClickListener]点击了" 
								+ map.get("task_time").toString(),
						Toast.LENGTH_SHORT).show();
				return true;
			}
		});
        return convertView;    
    }    
    
    public final class ViewHolder {    
        public ImageView task_img;    
        public TextView task_title;    
        public TextView task_time;    
    } 
    

}
