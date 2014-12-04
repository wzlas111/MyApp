package com.eastelsoft.lbs.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.entity.CustBean;
import com.eastelsoft.lbs.entity.CustProp;
import com.eastelsoft.lbs.entity.LocBean;

public class CstatisticsAdapter extends BaseAdapter {

    private Context context;
    public ArrayList<LocBean> list;
    private LayoutInflater inflater;
    public CstatisticsAdapter(Context context,ArrayList<LocBean> list){
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }
    
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public LocBean getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        Holder holder;
        if(convertView==null){
            holder = new Holder();
            convertView = inflater.inflate(R.layout.list_item_statis, null);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.longti = (TextView) convertView.findViewById(R.id.longti);
            holder.lati = (TextView) convertView.findViewById(R.id.lati);
            holder.address = (TextView) convertView.findViewById(R.id.address);
            convertView.setTag(holder);
        }else{
            holder = (Holder) convertView.getTag();  
        }
        
        holder.time.setText("时间："+list.get(position).getLocTime());
        holder.longti.setText("经度："+list.get(position).getLon());
        holder.lati.setText("纬度："+list.get(position).getLat());
        
        
        if (list.get(position).getAddr() != null&&!"".equals(list.get(position).getAddr())) {
			holder.address.setText("地址："+list.get(position).getAddr());
		}else{
			holder.address.setVisibility(View.GONE);
		}
        
       

        return convertView;
    }

    protected class Holder{
        TextView time;
        TextView longti;
        TextView lati;
        TextView address;
    }

}
