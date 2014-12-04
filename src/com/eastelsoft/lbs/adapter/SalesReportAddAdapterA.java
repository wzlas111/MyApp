package com.eastelsoft.lbs.adapter;

import java.util.ArrayList;
import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.SalesReportDetailActivity;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;

public class SalesReportAddAdapterA extends BaseAdapter {

	private LayoutInflater mInflater;
	private ArrayList<GoodsReportBean> arraylists;
	private Context context;
	private Integer index = -1;

	public SalesReportAddAdapterA(Context context,
			ArrayList<GoodsReportBean> arraylists) {
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
	public ArrayList<GoodsReportBean> getArrayList(){
		
		return arraylists;
		
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		ViewHolder holder = null;
		// convertView为null的时候初始化convertView。
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.goods_item_add, null);
			holder.goodsname = (TextView) convertView
					.findViewById(R.id.goodsname);
			holder.value = (EditText) convertView.findViewById(R.id.amount);
			holder.value.setTag(position);
			holder.value.setOnTouchListener(new OnTouchListener() {
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					if (event.getAction() == MotionEvent.ACTION_UP) {
						index = (Integer) v.getTag();
						
					}
					return false;
				}

			});
			class MyTextWatcher implements TextWatcher {
				private ViewHolder mHolder;

				public MyTextWatcher(ViewHolder holder) {
					mHolder = holder;
				}

				@Override
				public void onTextChanged(CharSequence s, int start, int before,
						int count) {
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start, int count,
						int after) {
				}

				@Override
				public void afterTextChanged(Editable s) {
//					!"".equals(s.toString()
					if (s != null) {
						int position = (Integer) mHolder.value.getTag();
						
						// mData.get(position).put("list_item_inputvalue",
						// s.toString());// 当EditText数据发生改变的时候存到data变量中
						arraylists.get(position).setAmount(s.toString());
						
					}
					System.out.println("c");
					
				}

			}

			holder.value.addTextChangedListener(new MyTextWatcher(holder));

			holder.goodsunit = (TextView) convertView
					.findViewById(R.id.goodsunit);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
			//***********************
			holder.value.setTag(position);
		}
		
		if (context.getClass() == SalesReportDetailActivity.class) {
			holder.value.setEnabled(false);
		}

		holder.goodsname.setText(arraylists.get(position).getName());
		holder.value.setText(arraylists.get(position).getAmount());
		holder.value.clearFocus();
		if (index != -1 && index == position) {
			holder.value.requestFocus();
		}
		holder.goodsunit.setText(arraylists.get(position).getPacking());

		return convertView;
	}

	public final class ViewHolder {
		public TextView goodsname;
		public EditText value;
		public TextView goodsunit;
	}

	

}
