package com.eastelsoft.lbs.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.adapter.InfoListViewAdapter.ViewHolder;
import com.eastelsoft.lbs.entity.BulletinAnpendixBean;
import com.eastelsoft.lbs.entity.BulletinBean;
import com.eastelsoft.lbs.entity.InfoBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class BulletinListAnpendixAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private ArrayList<BulletinAnpendixBean> arraylists;
	private Context context;

	public BulletinListAnpendixAdapter(Context context,
			ArrayList<BulletinAnpendixBean> arraylists) {
		super();
		this.arraylists = arraylists;
		this.context = context;
		mInflater = LayoutInflater.from(context);

	}

	public void changeArrayList(ArrayList<BulletinAnpendixBean> al) {
		this.arraylists = al;

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
		// convertView为null的时候初始化convertView。
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mInflater.inflate(R.layout.bulletinanpendix_item,
					null);
			holder.task_name = (TextView) convertView.findViewById(R.id.b_name);
			holder.task_pattern = (ImageView) convertView
					.findViewById(R.id.b_pattern);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		String allstring = arraylists.get(position).getAppendix_title();
		holder.task_name.setText(allstring);
		
		int toint = nametoint(allstring);
		holder.task_pattern.setImageResource(toint);

		return convertView;
	}

	public int nametoint(String dotstring) {
		int tp = R.drawable.zz_defeat;
		if (dotstring != null) {
			dotstring = dotstring.toLowerCase();
			if (dotstring.endsWith(".doc") || dotstring.endsWith(".docx")) {
				tp = R.drawable.zz_doc;
			} else if (dotstring.endsWith(".xls")
					|| dotstring.endsWith(".xlsx")) {
				tp = R.drawable.zz_exl;
			} else if (dotstring.endsWith(".pdf")) {
				tp = R.drawable.zz_pdf;
			} else if (dotstring.endsWith(".ppt")
					|| dotstring.endsWith(".pptx")) {
				tp = R.drawable.zz_ppt;
			} else if (dotstring.endsWith(".jpg")
					|| dotstring.endsWith(".gif")
					|| dotstring.endsWith(".bmp")
					|| dotstring.endsWith(".png")
					|| dotstring.endsWith(".jpeg")) {
				tp = R.drawable.zz_jpg;
			} else {
				tp = R.drawable.zz_defeat;
			}
		}

		return tp;

	}

	public final class ViewHolder {
		public TextView task_name;
		public ImageView task_pattern;
	}

}
