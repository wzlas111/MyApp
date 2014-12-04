package com.eastelsoft.lbs.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.adapter.InfoListViewAdapter.ViewHolder;
import com.eastelsoft.lbs.entity.InfoBean;
import com.eastelsoft.lbs.entity.VisitBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class VisitListItemAdapterA extends BaseAdapter {
	
	private LayoutInflater mInflater;    
    private  ArrayList<VisitBean> arraylists ;    
    //public static Map<Integer, Boolean> isSelected;
    private Context context;
    


	public VisitListItemAdapterA(Context context,ArrayList<VisitBean> arraylists) {
		super();
		this.arraylists = arraylists;
		this.context = context;
		mInflater = LayoutInflater.from(context);
		
	}
	public void changeArrayList(ArrayList<VisitBean> al){
		this.arraylists =al;
		
		
		
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
        //convertView为null的时候初始化convertView。    
        if (convertView == null) {    
            holder = new ViewHolder();    
            convertView = mInflater.inflate(R.layout.visit_list_item, null);    
            holder.task_img = (ImageView) convertView.findViewById(R.id.info_imgFile);    
            holder.task_title = (TextView) convertView.findViewById(R.id.info_title);    
            holder.task_time = (TextView) convertView.findViewById(R.id.info_uploadDate);
            holder.tast_istijiao = (ImageView) convertView.findViewById(R.id.info_istijiao);
            convertView.setTag(holder);    
        } else {    
            holder = (ViewHolder) convertView.getTag();    
        }    
        holder.task_img.setBackgroundResource(R.drawable.line_bg);    
        holder.task_title.setText(arraylists.get(position).getTitle());    
        holder.task_time.setText(arraylists.get(position).getDate());
        if("00".equals(arraylists.get(position).getIstijiao())){
        	holder.tast_istijiao.setBackgroundResource(R.drawable.visitnottijiao);	
        }else{
        	holder.tast_istijiao.setBackgroundResource(R.drawable.visittijiao);
        	
        }
        
        return convertView;
	}
	
	
	
    public final class ViewHolder {    
        public ImageView task_img;    
        public TextView task_title;    
        public TextView task_time;
        public ImageView tast_istijiao;
        
    } 

}
