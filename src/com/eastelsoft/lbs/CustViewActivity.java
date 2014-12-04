/**
 * Copyright (c) 2012-8-17 www.eastelsoft.com
 * $ID CustViewActivity.java 下午1:48:54 $
 */
package com.eastelsoft.lbs;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;


import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.db.DBUtil;
import com.eastelsoft.lbs.db.LocationSQLiteHelper;
import com.eastelsoft.lbs.entity.SetInfo;
import com.eastelsoft.util.Contant;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.IUtil;
import com.eastelsoft.util.http.AndroidHttpClient;

/**
 * 客户信息详情
 * 
 * @author lengcj
 */
public class CustViewActivity extends BaseActivity {
	public static final String TAG = "CustViewActivity";
	private Button btBack;
	private Button btupCust;
	private TextView tvCustClientName;
	private TextView tvCustContacts;
	
	private TextView tvCustJob;
	private TextView tvCustPhone2;
	private TextView tvCustPhone3;
	private TextView tvCustPhone4;
	private Button btCall2;
	private Button btSms2;
	private Button btCall3;
	private Button btSms3;
	private Button btCall4;
	private Button btSms4;
	private TextView tvCustLocation;
	private TextView tvCustEmail;
	private TextView tvCustType;
	private TextView tvCustArea;
	private TextView tvCustPhone;
	private TextView tvCustAddress;
	
	
	private Button btlocation;
	private Button btCall;
	private Button btSms;
	
//	private Button btkehubaifang;
//	private Button btbaifangjilu;
	
	
	private LocationSQLiteHelper locationHelper;
	
	HashMap<String, Object> localMap;
	private String clientName=""; 
	private String myid="";
	private String contact="";
	
	private String job="";
	private String phone2="";
	private String phone3="";
	private String phone4="";
    private String phone="";
    private String email="";
	private String location="";
	private String lon="";
	private String lat="";
	private String address="";
	private String istijiao="11";
	private String type="3";
	private String c_t_id;
	private String region_id;
	private String c_t_name;
	private String region_name;
	
	//客户上传成功得到的id
	private String cid;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_custview);
		
//		btkehubaifang = (Button) findViewById(R.id.btAddkehubaifang);
//		btkehubaifang.setOnClickListener(new OnBtBaifanClickListenerImpl());
//		btbaifangjilu = (Button) findViewById(R.id.btbaifangjilu);
//		btbaifangjilu.setOnClickListener(new OnBtBaifangJiluClickListenerImpl());
		btBack = (Button) findViewById(R.id.btBack);
		btBack.setOnClickListener(new OnBtBackClickListenerImpl());
		btupCust = (Button) findViewById(R.id.btupCust);
		btupCust.setOnClickListener(new OnBtCustUpClickListenerImpl());
		tvCustClientName = (TextView)findViewById(R.id.cust_clientName);
		tvCustContacts = (TextView)findViewById(R.id.cust_contacts);
		
		
		tvCustJob = (TextView)findViewById(R.id.cust_job);
		tvCustPhone2 = (TextView)findViewById(R.id.cust_phone2);
		tvCustPhone3 = (TextView)findViewById(R.id.cust_phone3);
		tvCustPhone4 = (TextView)findViewById(R.id.cust_phone4);
		
		btCall2= (Button) findViewById(R.id.cust_phone_img2);
		btCall2.setOnClickListener(new OnBtCall2ClickListenerImpl());
		btSms2= (Button) findViewById(R.id.cust_sms_img2);
		btSms2.setOnClickListener(new OnBtSms2ClickListenerImpl());
		btCall3= (Button) findViewById(R.id.cust_phone_img3);
		btCall3.setOnClickListener(new OnBtCall3ClickListenerImpl());
		btSms3= (Button) findViewById(R.id.cust_sms_img3);
		btSms3.setOnClickListener(new OnBtSms3ClickListenerImpl());
		btCall4= (Button) findViewById(R.id.cust_phone_img4);
		btCall4.setOnClickListener(new OnBtCall4ClickListenerImpl());
		btSms4= (Button) findViewById(R.id.cust_sms_img4);
		btSms4.setOnClickListener(new OnBtSms4ClickListenerImpl());
		
		
		tvCustLocation = (TextView)findViewById(R.id.cust_location);
		/*tvCustAddress = (TextView)findViewById(R.id.cust_address);*/
		tvCustEmail = (TextView)findViewById(R.id.cust_email);
		tvCustType = (TextView)findViewById(R.id.cust_type);
		tvCustArea = (TextView)findViewById(R.id.cust_area);
		tvCustPhone = (TextView)findViewById(R.id.cust_phone);
		//llCustPhoneId = (LinearLayout)findViewById(R.id.cust_phone_id);
		//llCustLocationId = (LinearLayout)findViewById(R.id.cust_location_id);
		//llCustSmsId = (LinearLayout)findViewById(R.id.cust_sms_img_id);
		btlocation= (Button) findViewById(R.id.cust_location_img);
		btlocation.setOnClickListener(new OnBtLocationClickListenerImpl());
		btCall= (Button) findViewById(R.id.cust_phone_img);
		btCall.setOnClickListener(new OnBtCallClickListenerImpl());
		btSms= (Button) findViewById(R.id.cust_sms_img);
		btSms.setOnClickListener(new OnBtSmsClickListenerImpl());
		
		locationHelper = new LocationSQLiteHelper(this, null, null, 5);
	
		
		Intent intent = getIntent();  		
        clientName = intent.getStringExtra("clientName"); 
        myid = intent.getStringExtra("myid"); 
//      localMap = DBUtil.getDataFromLCustByClientName(locationHelper.getWritableDatabase(), clientName);
        localMap = DBUtil.getDataFromLCustByClientId(locationHelper.getWritableDatabase(), myid);
        
        if(localMap != null) {
        	if(localMap.containsKey("clientName")) {
        		if(localMap.get("clientName") != null) {
        			clientName =localMap.get("clientName").toString();
        			tvCustClientName.setText(clientName);
        		}
        		if(localMap.get("contacts") != null) {
        			contact = localMap.get("contacts").toString();
        			tvCustContacts.setText(contact);
        		}
        		/*if(localMap.get("location") != null) {
        			this.location = localMap.get("location").toString();
        			tvCustLocation.setText(location);
        			
        		}*/
        		
    			if(localMap.get("job") != null) {
    				job = localMap.get("job").toString();
    				tvCustJob.setText(job);
        		}
    			
    			if(localMap.get("Phone2") != null) {
    				phone2 = localMap.get("Phone2").toString();
    				tvCustPhone2.setText(phone2);
        		}
    			if(localMap.get("Phone3") != null) {
    				phone3 = localMap.get("Phone3").toString();
    				tvCustPhone3.setText(phone3);
        		}
    			if(localMap.get("Phone4") != null) {
    				phone4 = localMap.get("Phone4").toString();
    				tvCustPhone4.setText(phone4);
        		}
        		if(localMap.get("email") != null) {
        			email = localMap.get("email").toString();
        			tvCustEmail.setText(email);
        		}
        		if(localMap.get("phone") != null) {
        			this.phone = localMap.get("phone").toString();
        			tvCustPhone.setText(phone);
        			
        		}
        		if(localMap.get("lon") != null) {
        			this.lon = localMap.get("lon").toString();
        		}
        		if(localMap.get("lat") != null) {
        			this.lat = localMap.get("lat").toString();
        		}
        		if(localMap.get("address") != null) {
        			this.address = localMap.get("address").toString();
        			tvCustLocation.setText(address);
        			
        		}
        		
        		if(localMap.get("istijiao") != null) {
        			this.istijiao = localMap.get("istijiao").toString();	
        		}else{
        			this.istijiao ="11";			
        		}
        		if(localMap.get("type") != null) {
        			this.type = localMap.get("type").toString();	
        		}
        		if(localMap.get("c_t_id") != null) {
        			this.c_t_id = localMap.get("c_t_id").toString();	
        		}
        		if(localMap.get("region_id") != null) {
        			this.region_id = localMap.get("region_id").toString();	
        		}
        		if(localMap.get("c_t_name") != null) {
        			this.c_t_name = localMap.get("c_t_name").toString();	
        			tvCustType.setText(c_t_name);
        		}
        		if(localMap.get("region_name") != null) {
        			this.region_name = localMap.get("region_name").toString();	
        			tvCustArea.setText(region_name);
        		}
        	}
        }
        if("00".equals(istijiao)){
        	btupCust.setVisibility(View.VISIBLE);	
        }
	}
	
	@Override  
	protected void onDestroy() {  
	    super.onDestroy();  
	    if (locationHelper  != null && locationHelper.getWritableDatabase() != null) {  
	    	locationHelper.getWritableDatabase().close();  
	    }  
	}  
	
	private class OnBtBackClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				CustViewActivity.this.finish();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}
	private class OnBtCustUpClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				CustViewActivity.this.finish();
				Intent intent = new Intent(CustViewActivity.this,CustEditActivity.class);
				intent.putExtra("myid", myid);
                startActivity(intent);	
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
			
		}
	}
	private class OnBtBaifanClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			
			if("00".equals(istijiao)){
				
				openPopupWindowAx("客户信息未上传到管理平台，无法新增拜访记录，是否同步客户信息到平台");
				
				
			}else{
				try {
					Intent intent = new Intent(CustViewActivity.this,BaifangAddActivity.class);
					intent.putExtra("clientName", clientName);
					intent.putExtra("myid", myid);
	                startActivity(intent);  
				} catch (Exception e) {
					FileLog.e(TAG, e.toString());
				}
				
			}
			
		}
	}
	private Button btClosex;
	private Button btCloseok;
	private Button btCloseno;
	
	protected void openPopupWindowAx(String msg) {
		try {
			LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			ViewGroup menuView = (ViewGroup) mLayoutInflater.inflate(
					R.layout.pop_commax, null, true);
			btClosex = (Button) menuView.findViewById(R.id.btClose);
			btClosex.setOnClickListener(new OnBtCloseLClickListenerImpl());
			btCloseok = (Button) menuView.findViewById(R.id.btClose1);
			btCloseok.setOnClickListener(new OnBtCloseLokClickListenerImpl());
			btCloseno = (Button) menuView.findViewById(R.id.btClose2);
			btCloseno.setOnClickListener(new OnBtCloseLnoClickListenerImpl());
			btPopText = (TextView) menuView.findViewById(R.id.btPopText);
			btPopText.setText(msg);
			popupWindow = new PopupWindow(menuView, LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT, true); // 背景
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
			popupWindow.setAnimationStyle(R.style.PopupAnimation);
			popupWindow.showAtLocation(findViewById(R.id.parent), Gravity.CENTER
					| Gravity.CENTER, 0, 0);
			popupWindow.update();
		} catch (Exception e) {
		}

	}
	protected class OnBtCloseLClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				popupWindow.dismiss();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}
	protected class OnBtCloseLokClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				popupWindow.dismiss();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
			try {
			// 上传数据
			
			CustViewActivity.this.openPopupWindowPG("");
			btPopGps.setText(getResources().getString(R.string.loading_custadd));
			Thread addInfoThread = new Thread(new AddCustThread());
			addInfoThread.start();
		} catch (Exception e) {
			FileLog.e(TAG, e.toString());
		}
		}
	}
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try {
				if (msg.what < 9) {
					//pDialog.cancel();
					try {
						popupWindowPg.dismiss();
					} catch (Exception e) {
						FileLog.e(TAG, e.toString());
					}
				}
				switch (msg.what) {
				case 0:
					if ("1".equals(msg.obj.toString())) {
						// 任务上报成功
						// dialog(InfoAddActivity.this,
						// getResources().getString(
						// R.string.info_upload_succ));
						// 返回成功数据写库
						
						// 增加客户通讯录
						/*DBUtil.insertLCust(
								locationHelper.getWritableDatabase(),
								cid, cname, ccontacter, lon, lat,locationDesc,
								cyouxiang, ctelephone,caddress,type,"11");*/
						
						//修改客户
						DBUtil.updateLCust(locationHelper.getWritableDatabase(), myid, cid);
						sp = getSharedPreferences("userdata", 0);
						SetInfo set = IUtil.initSetInfo(sp);
						
						String tp = set.getCustupdatecode();
						FileLog.i(TAG, tp);
						int a = Integer.parseInt(tp);
						a++;
						String re_tp = String.valueOf(a);
						IUtil.writeSharedPreference(sp, "custupdatecode",
								re_tp);
						FileLog.i(TAG, re_tp);
						  
						Toast.makeText(
								CustViewActivity.this,
								getResources().getString(
										R.string.cust_upload_succ),
								Toast.LENGTH_SHORT).show();
						CustViewActivity.this.finish();
					}else if("2".equals(msg.obj.toString())){
						dialog(CustViewActivity.this,
								"客户名称与平台上重复，请更换客户名称!");
						
					} else {
						dialog(CustViewActivity.this,
								getResources().getString(
										R.string.cust_upload_err));
					}
					break;
				case 1:
					dialog(CustViewActivity.this, msg.obj.toString());
					break;
				
				}
			} catch (Exception e) {
				// 异常中断
			}
		}
	};
	
	class AddCustThread implements Runnable {
		@Override
		public void run() {
			Looper.prepare();
			Message msg = handler.obtainMessage();
			try {
				sp = getSharedPreferences("userdata", 0);
				SetInfo set = IUtil.initSetInfo(sp);
				
				String url = set.getHttpip() + Contant.ACTION;
				Map<String, String> map = new HashMap<String, String>();
				map.put("reqCode", Contant.CLINET_UPLOAD_ACTION);
				map.put("gpsid", set.getDevice_id());
				map.put("pin", set.getAuth_code());
				map.put("clientname", clientName);
				map.put("contacts", contact);
				//新增字段
				
				map.put("job", job);
				map.put("Phone2", phone2);
				map.put("Phone3", phone3);
				map.put("Phone4", phone4);
				map.put("c_t_id", c_t_id);
				map.put("region_id", region_id);
				
				map.put("phone", phone);
				map.put("email", email);
				map.put("address", address);
				map.put("type", type);
				map.put("lon", lon);
				map.put("lat", lat);
				map.put("accuracy", "-100");
				FileLog.i(TAG, "kaishi");
				String jsonStr = AndroidHttpClient.getContent(url, map);
				FileLog.i(TAG, jsonStr);
				jsonStr = IUtil.chkJsonStr(jsonStr);
				JSONArray array = new JSONArray(jsonStr);
				String resultcode = "";
				if (array.length() > 0) {
					JSONObject obj = array.getJSONObject(0);
					resultcode = obj.getString("ResultCode");
					if("1".equals(resultcode)){
						cid =obj.getString("Id");	
					}
					FileLog.i(TAG, "resultcode==>" + resultcode);
					FileLog.i(TAG, "cid==>" + cid);
				}
				msg.what = 0;
				msg.obj = resultcode;
				// msg.obj = "信息上报成功";
				handler.sendMessage(msg);
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
				respMsg = getResources().getString(R.string.cust_upload_err);
				msg.what = 1;
				msg.obj = respMsg;
				handler.sendMessage(msg);
			}
			Looper.loop();
		}
	}
	protected class OnBtCloseLnoClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				popupWindow.dismiss();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}
	private class OnBtBaifangJiluClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			if("00".equals(istijiao)){
				/*Toast.makeText(
						CustViewActivity.this,
						"weitijiao",
						Toast.LENGTH_SHORT).show();*/
				
				openPopupWindowAx("客户信息未上传到管理平台，无法新增拜访记录，是否同步客户信息到平台");
				
				
			}else{
				try {
					Intent intent = new Intent(CustViewActivity.this,BaifangjiluActivity.class);
					intent.putExtra("clientName", clientName);
					intent.putExtra("myid", myid);
	                startActivity(intent);  
				} catch (Exception e) {
					FileLog.e(TAG, e.toString());
				}
			}
			
			
			
		}
	}
	
	private class OnBtLocationClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				if(lon!=null&&!lon.equals("")&&lat!=null&&!lat.equals("")){
					Intent intent = new Intent(CustViewActivity.this,ItemizedOverlayBaiduActivity.class);
					intent.putExtra("lon", lon);
					intent.putExtra("lat", lat);
					intent.putExtra("location", address);
					intent.putExtra("title", "客户位置");
	                startActivity(intent);	
				}
				  
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}
	
	private class OnBtCallClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				Uri uri = Uri.parse("tel:" + phone);  
				Intent it = new Intent(Intent.ACTION_CALL, uri);  
				startActivity(it);   
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}
	
	private class OnBtSmsClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				Intent intent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:" + phone));  
				startActivity(intent);
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}
	
	private class OnBtCall2ClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				Uri uri = Uri.parse("tel:" + phone2);  
				Intent it = new Intent(Intent.ACTION_CALL, uri);  
				startActivity(it);   
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}
	
	private class OnBtSms2ClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				Intent intent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:" + phone2));  
				startActivity(intent);
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}
	private class OnBtCall3ClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				Uri uri = Uri.parse("tel:" + phone3);  
				Intent it = new Intent(Intent.ACTION_CALL, uri);  
				startActivity(it);   
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}
	
	private class OnBtSms3ClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				Intent intent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:" + phone3));  
				startActivity(intent);
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}
	private class OnBtCall4ClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				Uri uri = Uri.parse("tel:" + phone4);  
				Intent it = new Intent(Intent.ACTION_CALL, uri);  
				startActivity(it);   
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}
	
	private class OnBtSms4ClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				Intent intent = new Intent(Intent.ACTION_SENDTO,Uri.parse("smsto:" + phone4));  
				startActivity(intent);
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}
}
