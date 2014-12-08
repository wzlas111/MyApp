package com.eastelsoft.lbs.activity.client;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.activity.select.ClientRegionActivity;
import com.eastelsoft.lbs.activity.select.ClientTypeActivity;
import com.eastelsoft.lbs.activity.select.ClientTypenameActivity;
import com.eastelsoft.lbs.bean.ClientDetailBean;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("ValidFragment")
public class ClientInfoAddFragment extends Fragment implements OnClickListener{
	
	private String mId;
	private String mChecked_dealer;
	private String mChecked_type;
	private String mChecked_region;
	private String mChecked_typename;
	private ClientDetailBean mBean;
	
	private View mRow_type;
	private View mRow_region;
	private View mRow_typename;
	private EditText client_name;
	private EditText client_code;
	private TextView dealer_name;
	private TextView type;
	private TextView region_name;
	private TextView typename;
	private EditText contact_phone;
	private EditText fax;
	private EditText address;
	private EditText remark;
	
	public ClientInfoAddFragment(String id) {
		mId = id;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_client_info_add, null);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		client_name = (EditText)view.findViewById(R.id.client_name);
		client_code = (EditText)view.findViewById(R.id.client_code);
		dealer_name = (TextView)view.findViewById(R.id.dealer_name);
		type = (TextView)view.findViewById(R.id.type);
		region_name = (TextView)view.findViewById(R.id.region_name);
		typename = (TextView)view.findViewById(R.id.typename);
		contact_phone = (EditText)view.findViewById(R.id.contact_phone);
		fax = (EditText)view.findViewById(R.id.fax);
		address = (EditText)view.findViewById(R.id.address);
		remark = (EditText)view.findViewById(R.id.remark);
		
		mRow_type = view.findViewById(R.id.row_type);
		mRow_type.setOnClickListener(this);
		mRow_region = view.findViewById(R.id.row_region_name);
		mRow_region.setOnClickListener(this);
		mRow_typename = view.findViewById(R.id.row_typename);
		mRow_typename.setOnClickListener(this);
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
	
	@Override
	public void onClick(View v) {
		Intent intent = new Intent();
		switch (v.getId()) {
		case R.id.row_dealer_name:
			
			break;
		case R.id.row_type:
			intent.setClass(getActivity(), ClientTypeActivity.class);
			intent.putExtra("id", mChecked_type);
			startActivityForResult(intent, 2);
			break;
		case R.id.row_region_name:
			intent.setClass(getActivity(), ClientRegionActivity.class);
			intent.putExtra("id", mChecked_region);
			startActivityForResult(intent, 3);
			break;
		case R.id.row_typename:
			intent.setClass(getActivity(), ClientTypenameActivity.class);
			intent.putExtra("id", mChecked_typename);
			startActivityForResult(intent, 4);
			break;
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case 1:
			if (data != null) {
				mChecked_dealer = data.getStringExtra("checked_id");
				String name = data.getStringExtra("checked_name");
				dealer_name.setText(name);
			}
			break;
		case 2:
			if (data != null) {
				mChecked_type = data.getStringExtra("checked_id");
				String name = data.getStringExtra("checked_name");
				type.setText(name);
			}
			break;
		case 3:
			if (data != null) {
				mChecked_region = data.getStringExtra("checked_id");
				String name = data.getStringExtra("checked_name");
				region_name.setText(name);
			}
			break;
		case 4:
			if (data != null) {
				mChecked_typename = data.getStringExtra("checked_id");
				String name = data.getStringExtra("checked_name");
				typename.setText(name);
			}
			break;
		}
	}
	
}
