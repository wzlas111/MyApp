/**
 * Copyright (c) 2013-5-31 www.eastelsoft.com
 * $ID SalesvMainGridViewAdapter.java 上午9:31:33 $
 */
package com.eastelsoft.lbs.adapter;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eastelsoft.lbs.R;

/**
 * 销量上报二级菜单数据适配
 * 
 * @author lengcj
 */
public class SalesvMainGridViewAdapter extends BaseAdapter {  
    private static final String TAG = "SalesvMainGridViewAdapter";  
    private String[] names = {"销量上报","销量任务查询","销量任务分配"};  
    private int[] icons = {R.drawable.app_07,R.drawable.app_salesv_task_query,R.drawable.app_salesv_task_allot};  
    private Context context;  
    LayoutInflater infalter;  
      
    public SalesvMainGridViewAdapter(Context context) {  
        this.context = context;  
        infalter = LayoutInflater.from(context);  
    }  
    // 返回gridview里面有多少个条目   
    public int getCount() {  
        return names.length;  
    }  
    //返回某个position对应的条目   
    public Object getItem(int position) {  
        return position;  
    }  
    //返回某个position对应的id  
    public long getItemId(int position) {  
        return position;  
    }  
    //返回某个位置对应的视图   
    public View getView(int position, View convertView, ViewGroup parent) {  
        //Log.i(TAG,"GETVIEW "+ position);  
        //把一个布局文件转换成视图  
        View view = infalter.inflate(R.layout.activity_salesv_main_item, null);  
        ImageView iv =  (ImageView) view.findViewById(R.id.main_gv_iv);  
        TextView  tv = (TextView) view.findViewById(R.id.main_gv_tv);  
//        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(90, 88);
//        params.gravity = Gravity.CENTER;
//        iv.setLayoutParams(params);
        //设置每一个item的名字和图标   
        iv.setImageResource(icons[position]);  
        tv.setText(names[position]);  
        return view;  
    }  
}
