package com.eastelsoft.lbs.activity.client;

import java.util.List;

import org.apache.http.Header;

import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.bean.ClientContactsBean;
import com.eastelsoft.lbs.bean.ClientContactsDto;
import com.eastelsoft.lbs.db.ClientDBTask;
import com.eastelsoft.util.http.HttpRestClient;
import com.google.gson.Gson;
import com.loopj.android.http.TextHttpResponseHandler;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

@SuppressLint("ValidFragment")
public class ClientContactsFragment extends Fragment implements OnClickListener{

	private String mId;
	private List<ClientContactsBean> mList;
	
	private LinearLayout mFrameTable;
	
	public ClientContactsFragment(String id) {
		mId = id;
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_client_contacts, null);
	}
	
	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		mFrameTable = (LinearLayout)view.findViewById(R.id.frame_table);
		
		new DBCacheTask().execute("");
	}
	
	private class DBCacheTask extends AsyncTask<String, Integer, Boolean> {

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
		}
		
		@Override
		protected Boolean doInBackground(String... arg0) {
			try {
				mList = ClientDBTask.getContactsByClientId(mId);
			} catch (Exception e) {
				System.out.println("客户联系人详情加载失败.");
				e.printStackTrace();
			}
			return true;
		}
		
		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (mList.size() > 0) {
				fillData();
			}
			RefreshDataTask();
		}
	}
	
	private void RefreshDataTask() {
		String url = "http://58.240.63.104/managermobile.do?reqCode=custContacts";
		HttpRestClient.get(url, null, new TextHttpResponseHandler() {
			
			@Override
			public void onStart() {
				super.onStart();
			}
			
			@Override
			public void onSuccess(int statusCode, Header[] headers, String responseString) {
				if (!TextUtils.isEmpty(responseString)) {
					Message msg = new Message();
					msg.what = 0;
					msg.obj = responseString;
					mHandler.sendMessage(msg);
				} else {
				}
			}
			
			@Override
			public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
			}
		});
	}
	
	private ClientContactsDto mDto;
	private DataThread mDataThread;
	private class DataThread extends Thread {
		private String responseString;
		public DataThread(String param) {
			responseString = param;
		}
		@Override
		public void run() {
			try {
				Gson gson = new Gson();
				mDto = gson.fromJson(responseString, ClientContactsDto.class);
				mList = mDto.data;
				if (mList != null && mList.size() > 0) {
					updateDB();
					mHandler.sendEmptyMessage(1);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("dealer data handle success.");
		}
	}
	
	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case 0:
				mDataThread = new DataThread((String)msg.obj);
				mDataThread.start();
				break;
			case 1:
				updateData();
				break;
			}
		};
	};
	
	private void updateDB() {
		ClientDBTask.deleteContactsAll(mId);
		ClientDBTask.addContacts(mList);
	}
	
	private void fillData() {
		for (int i = 0; i < mList.size(); i++) {
			ClientContactsBean bean = mList.get(i);
			View view = LayoutInflater.from(getActivity()).inflate(R.layout.widget_contact_table, null);
			((TextView)view.findViewById(R.id.name)).setText(bean.name);
			((TextView)view.findViewById(R.id.position)).setText(bean.position);
			((TextView)view.findViewById(R.id.tel_1)).setText(bean.tel_1);
			((TextView)view.findViewById(R.id.tel_2)).setText(bean.tel_2);
			((TextView)view.findViewById(R.id.tel_3)).setText(bean.tel_3);
			((TextView)view.findViewById(R.id.remark)).setText(bean.remark);
			
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(-1, -2);
			layoutParams.topMargin = 15;
			mFrameTable.addView(view, layoutParams);
		}
	}
	
	private void updateData() {
		mFrameTable.removeAllViews();
		fillData();
	}

	@Override
	public void onClick(View v) {
	}
}
