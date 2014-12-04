/**
 * Copyright (c) 2012-7-21 www.eastelsoft.com
 * $ID TabSetActivity.java 上午12:42:13 $
 */
package com.eastelsoft.lbs;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.entity.Login2Resp;
import com.eastelsoft.lbs.service.LocationService;
import com.eastelsoft.lbs.service.LocationService.MBinder;
import com.eastelsoft.util.CallBack;
import com.eastelsoft.util.Contant;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.GlobalVar;
import com.eastelsoft.util.IUtil;

/**
 * 参数设置页面
 * @author lengcj
 */
public class SetActivity extends BaseActivity {

	private static final String TAG = "TabSetActivity";
	private Button btBack;
	TextView tvSetIP;
	
	TextView tvTimePeriod;
	
	TextView tvInterval;
	
	TextView tvWeek;
	
	TextView tvFilterDate;
	
	TextView tvChkVer;
	
	TextView tvMobile;
	
	TextView tvImsi;
	
	ImageView set_shock_select_cb;
	
	ImageView set_msg_select_cb;
	
	private Button btRefresh;
	
	private LocationService locationService;
	private boolean mBound = false;
	    
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_set);
		btBack = (Button)findViewById(R.id.btBack);
        btBack.setOnClickListener(new OnBtBackClickListenerImpl());
		tvSetIP = (TextView)findViewById(R.id.set_ip);
		tvTimePeriod = (TextView)findViewById(R.id.timePeriod);
		tvInterval = (TextView)findViewById(R.id.interval);
		tvWeek = (TextView)findViewById(R.id.week);
		tvFilterDate = (TextView)findViewById(R.id.filterDate);
		tvMobile = (TextView)findViewById(R.id.set_mobile_value);
		tvImsi = (TextView)findViewById(R.id.set_imsi_value);
		
		btRefresh = (Button) findViewById(R.id.img_refresh);
		btRefresh.setOnClickListener(new OnBtRefreshClickListenerImpl());
		
		set_shock_select_cb = (ImageView) findViewById(R.id.set_shock_select_cb);
		set_shock_select_cb.setOnClickListener(new OnShockClickListenerImpl());
		
		set_msg_select_cb = (ImageView) findViewById(R.id.set_msg_select_cb);
		set_msg_select_cb.setOnClickListener(new OnMsgClickListenerImpl());
		
		globalVar = (GlobalVar) getApplicationContext();

		// 获取系统参数
    	sp = getSharedPreferences("userdata",0);
		serialNumber = sp.getString("serialNumber", "");
		imei = sp.getString("imei", "");
		imsi = sp.getString("imsi", "");
		adapter_ip = sp.getString("adapter_ip", "");
		adapter_port = sp.getString("adapter_port", "10647");
		auth_code = sp.getString("auth_code", "");
		device_id = sp.getString("device_id", "");
		timePeriod = sp.getString("timePeriod", "");
		interval = sp.getString("interval", "10");
		week = sp.getString("week", "0111110");
		filterDate = sp.getString("filterDate", "");
		minDistance = sp.getString("minDistance", "1000");
		
		tvSetIP.setText(sp.getString("gateip", ""));
		tvTimePeriod.setText(timePeriod);
		tvInterval.setText((Integer.parseInt(interval)) + "秒");
		tvWeek.setText(IUtil.parseWeek(week));
		tvFilterDate.setText(filterDate);
		tvMobile.setText(serialNumber);
		tvImsi.setText(imsi);

		// 绑定Service，绑定后就会调用mConnetion里的onServiceConnected方法  
        Intent intent = new Intent("com.eastelsoft.lbs.service.LocationService");  
        this.getApplicationContext().bindService(intent, sc, Context.BIND_AUTO_CREATE); 
        
        // 设置开关项，前面的取参数过程需要修改
        sp = getSharedPreferences("userdata", 0);
		String shock_select = sp.getString("shock_select", "1");
		String msg_select = sp.getString("msg_select", "1");
		if("1".equals(shock_select)) {
			set_shock_select_cb.setImageResource(R.drawable.images_on);
		} else {
			set_shock_select_cb.setImageResource(R.drawable.images_off);
		}
		if("1".equals(msg_select)) {
			set_msg_select_cb.setImageResource(R.drawable.images_on);
		} else {
			set_msg_select_cb.setImageResource(R.drawable.images_off);
		}

	}
	
	@Override
	protected void onDestroy() {
		FileLog.i(TAG, "onDestroy....");
		if (mBound) {  
            this.getApplicationContext().unbindService(sc);  
            mBound = false;  
        }
		super.onDestroy();

	}
	
	private class OnBtRefreshClickListenerImpl implements OnClickListener {
	
		public void onClick(View v) {
			networkAvailable = isNetworkAvailable();
    		if(networkAvailable) {
    			if(adapter_ip.length() == 0) {
    				respMsg = getResources().getString(R.string.check_error_login);
    				Toast.makeText(getApplicationContext(), respMsg, Toast.LENGTH_SHORT).show();
    			} else {
	    			//pDialog = new ProgressDialog(SetActivity.this);
					//pDialog.setTitle(getResources().getString(R.string.checkin_tips));
					//pDialog.setMessage(getResources().getString(R.string.loading));
					//pDialog.show();
    				
    				SetActivity.this.openPopupWindowPG("");
    				btPopGps.setText(getResources().getString(R.string.loading_set));

//					Thread setThread = new Thread(new SetThread());
//					setThread.start();
					locationService.refreshSet(tcpCallback);
    			}
    		} else {
    			respMsg = getResources().getString(R.string.net_error);
				Toast.makeText(getApplicationContext(), respMsg, Toast.LENGTH_SHORT).show();
    		}
		}
	}
	
	private class OnShockClickListenerImpl implements OnClickListener {
		
		public void onClick(View v) {
			sp = getSharedPreferences("userdata", 0);
			String shock_select = sp.getString("shock_select", "1");
			if("1".equals(shock_select)) {
				set_shock_select_cb.setImageResource(R.drawable.images_off);
				Editor editor = sp.edit();
				editor.putString("shock_select", "0");
				editor.commit();
			} else {
				set_shock_select_cb.setImageResource(R.drawable.images_on);
				Editor editor = sp.edit();
				editor.putString("shock_select", "1");
				editor.commit();
			}
		}
	}
	
	private class OnMsgClickListenerImpl implements OnClickListener {
		
		public void onClick(View v) {
			sp = getSharedPreferences("userdata", 0);
			String msg_select = sp.getString("msg_select", "1");
			if("1".equals(msg_select)) {
				set_msg_select_cb.setImageResource(R.drawable.images_off);
				Editor editor = sp.edit();
				editor.putString("msg_select", "0");
				editor.commit();
			} else {
				set_msg_select_cb.setImageResource(R.drawable.images_on);
				Editor editor = sp.edit();
				editor.putString("msg_select", "1");
				editor.commit();
			}
		}
	}
	
    private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try {
				//pDialog.cancel();
				try {
					popupWindowPg.dismiss();
				} catch (Exception e) {
					FileLog.e(TAG, e.toString());
				}
				FileLog.i(TAG, "msg.what==>" + msg);
				FileLog.i(TAG, "msg.what==>" + msg.what);
				switch(msg.what) {
					case 0:
						dialog(SetActivity.this, msg.obj.toString());
						break;
					case 1:
						Login2Resp login2Resp = (Login2Resp)msg.obj;
						if(login2Resp.getTimePeriod() != null)
							tvTimePeriod.setText(login2Resp.getTimePeriod());
						tvInterval.setText(login2Resp.getInterval() + "秒");
						if(login2Resp.getWeek() != null)
							tvWeek.setText(IUtil.parseWeek(login2Resp.getWeek()));
						if(login2Resp.getFilterDate() != null){
							tvFilterDate.setText(login2Resp.getFilterDate());
						}else{
							tvFilterDate.setText("无");
							
						}
						String tmpMsg = getResources().getString(R.string.set_refrcesh_succ);
						dialog(SetActivity.this,tmpMsg);
						break;
					default:
						respMsg = getResources().getString(
								R.string.set_refresh_error);
						dialog(SetActivity.this,respMsg);
						break;
				}
				
			}catch(Exception e) {
				// 异常中断
				respMsg = getResources().getString(
						R.string.set_refresh_error);
				dialog(SetActivity.this,respMsg);
			}
		}
	};
	

//	public OnClickListener mocl = new TextView.OnClickListener() {
//		@Override
//		public void onClick(View v) {
//			switch (v.getId()) {
//			// 短信分组群发 单击 监听
//			case R.id.chkVer:
//				Thread checkVersionTask = new Thread(new CheckVersionTask1(TabSetActivity.this));
//				checkVersionTask.start();
//				break;
//			}
//		}
//	};
	
	   /** 定交ServiceConnection，用于绑定Service的*/  
    private ServiceConnection sc = new ServiceConnection() {  
  
        @Override  
        public void onServiceConnected(ComponentName className,  
                IBinder service) {  
            // 已经绑定了LocalService，强转IBinder对象，调用方法得到LocalService对象  
        	MBinder binder = (MBinder) service;  
            locationService = binder.getService();  
            mBound = true;  
        }  
  
        @Override  
        public void onServiceDisconnected(ComponentName arg0) {  
            mBound = false;  
        }  
    };
    
    private CallBack tcpCallback = new CallBack() {
		public void execute(Object[] paramArrayOfObject) {
			Message msg = handler.obtainMessage();
			msg.what = 0;
			msg.obj = respMsg;
			Login2Resp login2Resp = null;
			respMsg = getResources().getString(
					R.string.set_refresh_error);
			if(paramArrayOfObject[0] != null)
				login2Resp = (Login2Resp)paramArrayOfObject[0];
			if(login2Resp != null) {
				if("0".equals(login2Resp.getRet()) 
						|| "2".equals(login2Resp.getRet())) {
					msg.what = 1; // 成功
					msg.obj = login2Resp;
				}
			}
			handler.sendMessage(msg);
		}
	};
	
	private class OnBtBackClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				SetActivity.this.finish();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}
}
