package com.eastelsoft.lbs.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.adapter.InfoListViewAdapter.ViewHolder;
import com.eastelsoft.lbs.entity.BulletinBean;
import com.eastelsoft.lbs.entity.InfoBean;
import com.eastelsoft.lbs.entity.KnowledgeBean;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class KnowledgeBaseListItemAdapter extends BaseAdapter {

	private LayoutInflater mInflater;
	private ArrayList<KnowledgeBean> arraylists;
	private Context context;

	public KnowledgeBaseListItemAdapter(Context context,
			ArrayList<KnowledgeBean> arraylists) {
		super();
		this.arraylists = arraylists;
		this.context = context;
		mInflater = LayoutInflater.from(context);

	}

	public void changeArrayList(ArrayList<KnowledgeBean> al) {
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
			convertView = mInflater.inflate(R.layout.knowledgebase_list_item,
					null);

			holder.task_title = (TextView) convertView
					.findViewById(R.id.info_title);
			holder.task_type = (ImageView) convertView
					.findViewById(R.id.iv_type);
			holder.task_istijiao = (ImageView) convertView
					.findViewById(R.id.info_istijiao);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		holder.task_title.setText(arraylists.get(position).getK_name());

		holder.task_type.setBackgroundResource(nametoint(arraylists.get(
				position).getK_type()));
//		if ("0".equals(arraylists.get(position).getIs_read())) {
//			holder.task_istijiao.setVisibility(View.VISIBLE);
//			holder.task_istijiao.setBackgroundResource(R.drawable.zno_read);
//		} else if("1".equals(arraylists.get(position).getIs_read())){
//			holder.task_istijiao.setVisibility(View.VISIBLE);
//			holder.task_istijiao.setBackgroundResource(R.drawable.zis_read);
//			
//		}else{
//			holder.task_istijiao.setVisibility(View.INVISIBLE);
//			
//		}
		holder.task_istijiao.setVisibility(View.INVISIBLE);
		return convertView;
	}

	// （0：目录// 1：批量附件带内容的// 2：ppt直接附件// 3：doc直接附件// 4：jpg直接附件// 5：pdf直接附件//
	// 6：xls直接附件// 7：gif直接附件// 8：bmp直接附件// 9：png直接附件）
	public int nametoint(String dotstring) {
		int tp = R.drawable.zbig_txt;
		if (dotstring != null) {
			if (dotstring.equals("0")) {
				tp = R.drawable.zbig_c;
			} else if (dotstring.equals("1")) {
				tp = R.drawable.zbig_noc;
			} else if (dotstring.equals("2")) {
				tp = R.drawable.zbig_ppt;
			} else if (dotstring.equals("3")) {
				tp = R.drawable.zbig_word;
			} else if (dotstring.equals("5")) {
				tp = R.drawable.zbig_pdf;
			} else if (dotstring.equals("6")) {
				tp = R.drawable.zbig_excel;
			} else if (dotstring.equals("4") || dotstring.equals("7")
					|| dotstring.equals("8") || dotstring.equals("9")) {
				tp = R.drawable.zbig_jpg;
			} else {
				tp = R.drawable.zbig_txt;
			}
		}

		return tp;

	}

	public final class ViewHolder {
		public TextView task_title;
		public ImageView task_type;
		public ImageView task_istijiao;
	}

}
