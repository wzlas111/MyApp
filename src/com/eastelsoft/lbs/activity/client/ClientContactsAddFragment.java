package com.eastelsoft.lbs.activity.client;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.bean.ClientContactsBean;
import com.google.gson.Gson;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class ClientContactsAddFragment extends Fragment implements OnClickListener{

	private String mId;
	
	private LinearLayout mFrameTable;
	private Button mAddBtn;
	
	public ClientContactsAddFragment(String id) {
		mId = id;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_client_contacts_add, null);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mFrameTable = (LinearLayout)view.findViewById(R.id.frame_table);
		mAddBtn = (Button)view.findViewById(R.id.add_btn);
		
		mAddBtn.setOnClickListener(this);
		addTableRow(i++);
	}
	
	int i = 0;
	private void addTableRow(int row) {
		View view = LayoutInflater.from(getActivity()).inflate(R.layout.widget_contact_add_table, null);
		((EditText)view.findViewById(R.id.name)).setText("");
		((EditText)view.findViewById(R.id.contact_phone_1)).setText("");
		((EditText)view.findViewById(R.id.contact_phone_2)).setText("");
		((EditText)view.findViewById(R.id.tel)).setText("");
		((EditText)view.findViewById(R.id.remark)).setText("");
		((TextView)view.findViewById(R.id.is_main)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = ((TextView)v).getText().toString();
				if ("是".equals(text)) {
					((TextView)v).setText("否");
				} else if("否".equals(text)) {
					((TextView)v).setText("是");
				}
			}
		});
		
		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
		layoutParams.topMargin = 15;
		mFrameTable.addView(view, layoutParams);
		
		i++;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.add_btn:
			addTableRow(i);
			break;
		}
	}
	
	public String getJSON() {
		List<ClientContactsBean> mList = new ArrayList<ClientContactsBean>();
		int count = mFrameTable.getChildCount();
		for (int i = 0; i < count; i++) {
			View view = mFrameTable.getChildAt(i);
			ClientContactsBean mBean = new ClientContactsBean();
			mBean.contact_person_id = UUID.randomUUID().toString();
			mBean.map_client_id = mId;
		
			String name = ((EditText)view.findViewById(R.id.name)).getText().toString();
			if (TextUtils.isEmpty(name)) {//if name has no value,so the data is invalid.
				continue;
			}
			mBean.contact_person_name = name;
			mBean.contact_phone_1 = ((EditText)view.findViewById(R.id.contact_phone_1)).getText().toString();
			mBean.contact_phone_2 = ((EditText)view.findViewById(R.id.contact_phone_2)).getText().toString();
			mBean.tel = ((EditText)view.findViewById(R.id.tel)).getText().toString();
			String is_main = ((TextView)view.findViewById(R.id.is_main)).getText().toString();
			if ("是".equals(is_main)) {
				mBean.is_main = "1";
			} else if("否".equals(is_main)) {
				mBean.is_main = "0";
			}
			mList.add(mBean);
		}
		Gson gson = new Gson();
		String jsonString = gson.toJson(mList);
		return jsonString;
	}
}
