package com.eastelsoft.lbs.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.adapter.InfoListViewAdapter.ViewHolder;
import com.eastelsoft.lbs.entity.InfoBean;
import com.eastelsoft.lbs.entity.SalesReportBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SalesReportListAdapterA extends BaseAdapter {
	
	private LayoutInflater mInflater;    
    private  ArrayList<SalesReportBean> arraylists ;    
    private Context context;
    


	public SalesReportListAdapterA(Context context,ArrayList<SalesReportBean> arraylists) {
		super();
		this.arraylists = arraylists;
		this.context = context;
		mInflater = LayoutInflater.from(context);
		
	}
	public void changeArrayList(ArrayList<SalesReportBean> al){
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
            convertView = mInflater.inflate(R.layout.salesreport_list_item, null);  
            
            holder.task_img = (ImageView) convertView.findViewById(R.id.info_imgFile);  
            holder.task_update = (TextView) convertView.findViewById(R.id.info_uploadDate);  
            holder.task_cust = (TextView) convertView.findViewById(R.id.info_cust);    
            holder.task_date = (TextView) convertView.findViewById(R.id.info_date); 
            holder.task_istijiao = (ImageView) convertView.findViewById(R.id.info_istijiao);
            convertView.setTag(holder);    
        } else {    
            holder = (ViewHolder) convertView.getTag();    
        }    
        holder.task_img.setBackgroundResource(R.drawable.line_bg);    
        holder.task_update.setText(arraylists.get(position).getSubmitdate());    
//        holder.task_cust.setText(arraylists.get(position).getClientName());
//        holder.task_date.setText(arraylists.get(position).getDate());
        holder.task_cust.setText(arraylists.get(position).getClientName());
        holder.task_date.setText(arraylists.get(position).getDate());
        
        
        
        if("00".equals(arraylists.get(position).getIstijiao())){
        	holder.task_istijiao.setBackgroundResource(R.drawable.visitnottijiao);	
        }else{
        	holder.task_istijiao.setBackgroundResource(R.drawable.visittijiao);
        	
        }
        return convertView;
	}
	
	
	
    public final class ViewHolder {    
        public ImageView task_img; 
        public TextView task_update; 
        public TextView task_cust;    
        public TextView task_date;
        public ImageView task_istijiao;
    } 

}
