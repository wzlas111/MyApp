package com.eastelsoft.lbs.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.entity.CustProp;

public class CustTypeAdapter extends BaseAdapter {

    private Context context;
    public List<CustProp> list;
    private LayoutInflater inflater;
    public CustTypeAdapter(Context context,List list){
        this.context = context;
        this.list = list;
        inflater = LayoutInflater.from(context);
    }
    
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public CustProp getItem(int position) {
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
            convertView = inflater.inflate(R.layout.list_item_type, null);
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.id = (TextView) convertView.findViewById(R.id.id);
            convertView.setTag(holder);
        }else{
            holder = (Holder) convertView.getTag();
        }
        holder.name.setText(list.get(position).getName());
        holder.id.setText(list.get(position).getId());

        return convertView;
    }

    protected class Holder{
        TextView id;
        TextView name;
    }

}
