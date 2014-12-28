package com.eastelsoft.lbs.activity.client;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.activity.select.ClientDealerActivity;
import com.eastelsoft.lbs.activity.select.ClientRegionActivity;
import com.eastelsoft.lbs.activity.select.ClientTypeActivity;
import com.eastelsoft.lbs.activity.select.ClientTypenameActivity;
import com.eastelsoft.lbs.bean.ClientDetailBean;
import com.eastelsoft.lbs.bean.ClientDto;
import com.eastelsoft.lbs.bean.ClientDto.ClientBean;
import com.eastelsoft.lbs.location.BaiduMapAction;
import com.eastelsoft.util.CallBack;
import com.eastelsoft.util.Util;
import com.google.gson.Gson;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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
	private String mLon = "";
	private String mLat = "";
	
	private View mRow_dealer;
	private View mRow_type;
	private View mRow_region;
	private View mRow_typename;
	private EditText client_name;
	private EditText client_code;
	private TextView dealer_name;
	private TextView type;
	private TextView region_name;
	private TextView typename;
	private EditText fax;
	private EditText address;
	private EditText remark;
	private Button location_btn;
	
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
		fax = (EditText)view.findViewById(R.id.fax);
		address = (EditText)view.findViewById(R.id.address);
		remark = (EditText)view.findViewById(R.id.remark);
		location_btn = (Button)view.findViewById(R.id.location_btn);
		
		location_btn.setOnClickListener(this);
		mRow_dealer = view.findViewById(R.id.row_dealer_name);
		mRow_dealer.setOnClickListener(this);
		mRow_type = view.findViewById(R.id.row_type);
		mRow_type.setOnClickListener(this);
		mRow_region = view.findViewById(R.id.row_region_name);
		mRow_region.setOnClickListener(this);
		mRow_typename = view.findViewById(R.id.row_typename);
		mRow_typename.setOnClickListener(this);
		
		InputMethodManager input = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		boolean isActive = input.isActive();
		System.out.println("input : "+isActive);
		if (isActive) {
			input.hideSoftInputFromWindow(view.getWindowToken(), 0);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
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
			intent.setClass(getActivity(), ClientDealerActivity.class);
			intent.putExtra("id", mChecked_dealer);
			intent.putExtra("type", "1");
			startActivityForResult(intent, 1);
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
		case R.id.location_btn:
			address.setText("正在获取中...");
			new BaiduMapAction(getActivity(), mapCallback, "2").startListener();
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
	
	public boolean canSend() {
		if (TextUtils.isEmpty(client_name.getText().toString())) {
			Toast.makeText(getActivity(), "请选择客户名称.", Toast.LENGTH_SHORT).show();
			return false;
		}
		if (TextUtils.isEmpty(type.getText().toString())) {
			Toast.makeText(getActivity(), "请选择客户共享.", Toast.LENGTH_SHORT).show();
			return false;
		}
		return true;
	}
	
	public String getJSON() {
		ClientBean mBean = new ClientDto().new ClientBean();
		mBean.id = mId;
		mBean.client_name = client_name.getText().toString();
		mBean.client_code = client_code.getText().toString();
		mBean.dealer_id = mChecked_dealer;
		mBean.dealer_name = dealer_name.getText().toString();
		mBean.type = mChecked_type;
		mBean.region_id = mChecked_region;
		mBean.region_name = region_name.getText().toString();
		mBean.type_id = mChecked_typename;
		mBean.type_name = typename.getText().toString();
		mBean.fax = fax.getText().toString();
		mBean.address = address.getText().toString();
		mBean.accuary = "-100";
		mBean.remark = remark.getText().toString();
		mBean.lon = mLon;
		mBean.lat = mLat;
		mBean.is_upload = "0";
		
		Gson gson = new Gson();
		String jsonString = gson.toJson(mBean, ClientBean.class);
		
		return jsonString;
	}
	
	private CallBack mapCallback = new CallBack() {
		public void execute(Object[] params) {
			Message msg = mHandler.obtainMessage();
			msg.what = 99;
			msg.obj = params;
			mHandler.sendMessage(msg);
		}
	};
	
	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 99:
				Location location = null;
				Object[] obj1 = (Object[]) msg.obj;
				if (obj1[0] != null) {
					location = (Location) obj1[0];
				}
				if (location != null) {
					try {
						mLon = Util.format(location.getLongitude(), "#.######");
						mLat = Util.format(location.getLatitude(), "#.######");
						String locationDesc = location.getExtras().getString("desc");
						address.setText(locationDesc);
					} catch (Exception e) {
						address.setText("获取定位信息失败");
					}
				} else {
					address.setText("获取定位信息失败");
				}
				break;
			}
		}
	};
	
}
