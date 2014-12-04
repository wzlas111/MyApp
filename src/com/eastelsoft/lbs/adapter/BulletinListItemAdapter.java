package com.eastelsoft.lbs.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.adapter.InfoListViewAdapter.ViewHolder;
import com.eastelsoft.lbs.entity.BulletinBean;
import com.eastelsoft.lbs.entity.InfoBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BulletinListItemAdapter extends BaseAdapter {
	
	private LayoutInflater mInflater;    
    private  ArrayList<BulletinBean> arraylists ;    
    private Context context;
    


	public BulletinListItemAdapter(Context context,ArrayList<BulletinBean> arraylists) {
		super();
		this.arraylists = arraylists;
		this.context = context;
		mInflater = LayoutInflater.from(context);
		
	}
	public void changeArrayList(ArrayList<BulletinBean> al){
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
            convertView = mInflater.inflate(R.layout.bulletin_list_item, null);    
            
            holder.task_title = (TextView) convertView.findViewById(R.id.info_title);    
            holder.task_time = (TextView) convertView.findViewById(R.id.info_uploadDate); 
            holder.task_istijiao = (ImageView) convertView.findViewById(R.id.info_istijiao);
            convertView.setTag(holder);    
        } else {    
            holder = (ViewHolder) convertView.getTag();    
        }  
        holder.task_title.setText(arraylists.get(position).getB_name());    
        holder.task_time.setText(arraylists.get(position).getB_release_date());
        if("0".equals(arraylists.get(position).getIs_read())){
        	holder.task_istijiao.setBackgroundResource(R.drawable.zno_read);	
        }else{
        	holder.task_istijiao.setBackgroundResource(R.drawable.zis_read);
        	
        }
        return convertView;
	}
	
	
	
    public final class ViewHolder {    
        public TextView task_title;    
        public TextView task_time;
        public ImageView task_istijiao;
    } 

}
