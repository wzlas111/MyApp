package com.eastelsoft.lbs.adapter;

import java.util.ArrayList;
import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.entity.GoodsMonthTargetBean;
import com.eastelsoft.lbs.entity.GoodsReportBean;
import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.TextView;

public class SalesMonthsTargetAdapterA extends BaseAdapter {

	private LayoutInflater mInflater;
	private ArrayList<GoodsMonthTargetBean> arraylists;
	private Context context;

	public SalesMonthsTargetAdapterA(Context context,
			ArrayList<GoodsMonthTargetBean> arraylists) {
		super();
		this.arraylists = arraylists;
		this.context = context;
		mInflater = LayoutInflater.from(context);

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
	public ArrayList<GoodsMonthTargetBean> getArrayList(){
		
		return arraylists;
		
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		ViewHolder holder = null;
		// convertView为null的时候初始化convertView。
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.goods_item_target, null);
			holder.goodsname = (TextView) convertView.findViewById(R.id.goodsname);
			holder.value = (TextView) convertView.findViewById(R.id.amount);
			
			holder.goodsunit = (TextView) convertView.findViewById(R.id.goodsunit);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
			
		}

		holder.goodsname.setText(arraylists.get(position).getName());
		holder.value.setText(arraylists.get(position).getTarget());
		holder.goodsunit.setText(arraylists.get(position).getPacking());

		return convertView;
	}

	public final class ViewHolder {
		public TextView goodsname;
		public TextView value;
		public TextView goodsunit;
	}

	

}
