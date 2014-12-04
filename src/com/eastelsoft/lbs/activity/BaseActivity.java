/**
 * Copyright (c) 2012-7-21 www.eastelsoft.com
 * $ID BaseActivity.java 上午12:42:13 $
 */
package com.eastelsoft.lbs.activity;

import java.io.File;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.eastelsoft.lbs.MyGridView;
import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.entity.SetInfo;
import com.eastelsoft.util.Contant;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.GlobalVar;
import com.eastelsoft.util.IUtil;
import com.eastelsoft.util.Mobile;
import com.eastelsoft.util.UDPPackage;
import com.eastelsoft.util.http.AndroidHttpClient;

/**
 * 基础Activity
 * @author lengcj
 */
public class BaseActivity extends Activity {
	private static final String TAG = "BaseActivity";
	// 电池电量 %
	protected String power;
	
	protected String imei;
	
	protected String imsi;
	
	protected String serialNumber;
	
	protected String auth_code;
	
	protected String device_id = "0000000000000000";
	
	protected String timePeriod;
	
	protected String interval;
	
	protected String week;
	
	protected String filterDate;
	
	protected String minDistance;

	protected String adapter_ip;
	
	protected String adapter_port;
	
	protected String deviceType = "00010001";
	
	protected GlobalVar globalVar;
	
	protected Mobile mobile;
	
	// protected FileService fileService;
	
	protected String respMsg;
	
	protected TelephonyManager telephonyManager;
	
	protected ProgressDialog pDialog;
		
	protected boolean networkAvailable;
		
	protected UDPPackage pack;
	
	protected SharedPreferences sp;
	protected SetInfo setb;
	
	protected int verCode;
	
	protected String verName;
	
	protected String apkName;
	
	protected int newVerCode;
	
	protected String newVerName;
	
	protected Button btClose;
	protected CheckBox cb_nosee;
	
	protected Button btClose1;
	
	protected TextView btPopText;
	
	protected TextView btPopGps;
	
	protected PopupWindow popupWindow;
	
	protected PopupWindow popupWindowPg;
	
	// 等待对话框
	// protected ProgressDialog progressDialog;
	
	protected TextView tvHeadTitle;

	@Override
	protected void onDestroy() {
		//globalVar = null;
		mobile = null;
		//fileService = null;
		//telephonyManager = null;
		pDialog = null;
		//pack = null;
		sp = null;
		super.onDestroy();
	}
			
	/**
	 * 电池电量
	 */
	protected BroadcastReceiver batteryChangedReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
				int level = intent.getIntExtra("level", 0);
				int scale = intent.getIntExtra("scale", 100);
				//power = (level * 100 / scale) + "%";
				power = String.valueOf(level * 100 / scale);
			}
		}
	};
	
	/**
	 * 检测网络连接状态
	 * 
	 * @return
	 */
	public boolean isNetworkAvailable() {
		Context context = getApplicationContext();
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			Log.e("NetWork available", "getSystemService rend null");
		} else {// 获取所有网络连接信息
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {// 逐一查找状态为已连接的网络
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * 简单对话框
	 * @param context
	 * @param text
	 */
	protected void dialog(Context context, String text) {
		/**
		AlertDialog.Builder builder = new Builder(context);

		builder.setMessage(text);

		builder.setTitle("温馨提示");

		builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.dismiss();
			}
		});

		builder.create().show();
		*/
		openPopupWindow(text);
	}
	
	protected boolean getServerVer () {  
        try {
        	sp = getSharedPreferences("userdata", 0);
        	setb = IUtil.initSetInfo(sp);
        	String verjson = AndroidHttpClient.getContent(setb.getHttpip()
					+ Contant.UPDATE_VERJSON);
        	// 先用临时的测试
        	//                  [{\"appname\":\"weizh\",\"apkname\":\"gpsLocation.apk\",\"verName\":1.1.5,\"verCode\":2}]
            //String verjson = "[{\"appname\":\"位置在沃\",\"apkname\":\"gpsLocation.apk\",\"verName\":1.1.5,\"verCode\":2}]"; 
            JSONArray array = new JSONArray(verjson);  
            if (array.length() > 0) {  
                JSONObject obj = array.getJSONObject(0);  
                try {  
                    //newVerCode = Integer.parseInt(obj.getString("verCode"));
                    newVerName = obj.getString("vername");
                    apkName = obj.getString("apkname");
                } catch (Exception e) {  
                    return false;  
                }  
            }  
        } catch (Exception e) {  
            return false;  
        }  
        return true;  
    }
	
	Handler dHandler = new Handler(){  
	    @Override  
	    public void handleMessage(Message msg) {  
	        // TODO Auto-generated method stub  
	        super.handleMessage(msg);  
	        switch (msg.what) {  
	        case 0:
	        	try {
					popupWindowPg.dismiss();
				} catch (Exception e) {
					FileLog.e(TAG, e.toString());
				}
	            //对话框通知用户升级程序   
	        	try {
					Context context = (Context)msg.obj;
					showUpdataDialog(context);
				} catch (Exception e) {
				}  
	            break;  
	        case -1: 
	        	try {
					popupWindowPg.dismiss();
				} catch (Exception e) {
					FileLog.e(TAG, e.toString());
				}
	            //服务器超时   
	            Toast.makeText(getApplicationContext(), 
	            		"获取服务器更新信息失败", Toast.LENGTH_SHORT).show();  
	            break;    
	        case 2:  
	            //下载apk失败  
	            Toast.makeText(getApplicationContext(), 
	            		"下载新版本失败", Toast.LENGTH_SHORT).show();  
	            break;
	        case 1:
	        	break;
	        case 3:
	        	try {
					popupWindowPg.dismiss();
				} catch (Exception e) {
					FileLog.e(TAG, e.toString());
				}
	        	 Toast.makeText(getApplicationContext(), 
		            		"当前已是最新版本", Toast.LENGTH_SHORT).show();  
	        	 break;
	        default:break;
	        }
	    }  
	};
	
	public class CheckVersionTask implements Runnable{ 
		private Context context;
		public CheckVersionTask(Context context){
			this.context = context;
		}
	    public void run() {  
	        try {    
	   		 verName = IUtil.getVerName(context);
	   		 if(getServerVer()) {
//	   			 String tmpNewVarName = newVerName.replaceAll("\\.", "");
//	   			 String tmpVerName = verName.replaceAll("\\.", "");
//	   			 int newid = 1;
//	   			 int oldid = 1;
//	   			 try {
//	   				 newid = Integer.parseInt(tmpNewVarName);
//	   				 oldid = Integer.parseInt(tmpVerName);
//	   			 } catch(Exception e) {
//	   				 FileLog.e(TAG, e);
//	   			 }
	   			if(!newVerName.equals(verName)) {
	   			//if(newid > oldid) {
	   				 Log.i(TAG,"版本号不同 ,提示用户升级 ");  
	   				 Message msg = new Message();  
	   				 msg.what = 0;  // 提示升级
	   				 msg.obj = context;
	   				 dHandler.sendMessage(msg); 
	   			 } else {
	   				 Message msg = new Message();  
	   				 msg.what = 1;  // 不用升级
	   				 dHandler.sendMessage(msg); 
	   			 }
	   		 }
	        } catch (Exception e) {  
	            Message msg = new Message();  
	            msg.what = -1; // 失败
	            dHandler.sendMessage(msg);  
	        }   
	    }  
	}
	
	public class CheckVersionTask1 implements Runnable{ 
		private Context context;
		public CheckVersionTask1(Context context){
			this.context = context;
		}
	    public void run() {  
	        try {    
	   		 verName = IUtil.getVerName(context);
	   		 if(getServerVer()) {
//	   			 String tmpNewVarName = newVerName.replaceAll("\\.", "");
//	   			 String tmpVerName = verName.replaceAll("\\.", "");
//	   			 int newid = 1;
//	   			 int oldid = 1;
//	   			 try {
//	   				 newid = Integer.parseInt(tmpNewVarName);
//	   				 oldid = Integer.parseInt(tmpVerName);
//	   			 } catch(Exception e) {
//	   				 FileLog.e(TAG, e);
//	   			 }
	   			if(!newVerName.equals(verName)) {
	   			//if(newid > oldid) {
	   				 Log.i(TAG,"版本号不同 ,提示用户升级 ");  
	   				 Message msg = new Message();  
	   				 msg.what = 0;  // 提示升级
	   				 msg.obj = context;
	   				 dHandler.sendMessage(msg); 
	   			 } else {
	   				 Message msg = new Message();  
	   				 msg.what = 3;  // 不用升级
	   				 dHandler.sendMessage(msg); 
	   			 }
	   		 }
	        } catch (Exception e) {  
	            Message msg = new Message();  
	            msg.what = -1; // 失败
	            dHandler.sendMessage(msg);  
	        }   
	    }  
	}
	
	/* 
	 *  
	 * 弹出对话框通知用户更新程序  
	 *  
	 * 弹出对话框的步骤： 
	 *  1.创建alertDialog的builder.   
	 *  2.要给builder设置属性, 对话框的内容,样式,按钮 
	 *  3.通过builder 创建一个对话框 
	 *  4.对话框show()出来   
	 */  
	protected void showUpdataDialog(Context context) {  
	    try {
			AlertDialog.Builder builer = new Builder(context) ;   
			builer.setTitle("版本升级");
			StringBuffer sb = new StringBuffer();
			sb.append("当前版本:");
			sb.append(verName);
			sb.append(", 发现新版本:");
			sb.append(newVerName);
			builer.setMessage(sb.toString());  
			//当点确定按钮时从服务器上下载 新的apk 然后安装   
			builer.setPositiveButton("立即升级",// 设置确定按钮
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							downLoadApk();
						}

					});
			builer.setNegativeButton("以后再说",// 设置确定按钮
					new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.dismiss();
						}

					});
			AlertDialog dialog = builer.create();  
			dialog.show();
		} catch (Exception e) {
		}  
	}  
	
	protected void downLoadApk() {
		final ProgressDialog pBar;
		pBar = new  ProgressDialog(this);  
		pBar.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);  
		pBar.setMessage("正在下载更新");  
		pBar.show();  
	    new Thread(){  
	        @Override  
	        public void run() {  
	            try {
	            	sp = getSharedPreferences("userdata", 0);
	            	setb = IUtil.initSetInfo(sp);
	                File file = AndroidHttpClient.getFileFromServer(setb.getHttpip()
							+ apkName, pBar, BaseActivity.this);  
	                sleep(3000);  
	                installAPK(file);  
	                pBar.dismiss(); //结束掉进度条对话框  
	            } catch (Exception e) {  
	                Message msg = new Message();  
	                msg.what = 2;  
	                dHandler.sendMessage(msg);  
	                pBar.dismiss();
	            }  
	        }}.start();  
	}  

	private void installAPK(File file) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file),
				"application/vnd.android.package-archive");
		startActivity(intent);
	}
	
	protected void openPopupWindow() {
		try {
			LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			ViewGroup menuView = (ViewGroup) mLayoutInflater.inflate(
					R.layout.pop_check, null, true);
			btClose = (Button) menuView.findViewById(R.id.btClose);
			btClose.setOnClickListener(new OnBtCloseClickListenerImpl());
			popupWindow = new PopupWindow(menuView, LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT, true); // 全部背景置灰
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
			popupWindow.setAnimationStyle(R.style.PopupAnimation);
			popupWindow.showAtLocation(findViewById(R.id.parent), Gravity.CENTER
					| Gravity.CENTER, 0, 0);
			popupWindow.update();
		} catch (Exception e) {

		}

	}
	

	protected class OnBtCloseClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				popupWindow.dismiss();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}
	
	protected void openPopupWindow(String msg) {
		try {
			LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			ViewGroup menuView = (ViewGroup) mLayoutInflater.inflate(
					R.layout.pop_comm, null, true);
			btClose = (Button) menuView.findViewById(R.id.btClose);
			btClose.setOnClickListener(new OnBtCloseClickListenerImpl());
			btClose1 = (Button) menuView.findViewById(R.id.btClose1);
			btClose1.setOnClickListener(new OnBtCloseClickListenerImpl());
			btPopText = (TextView) menuView.findViewById(R.id.btPopText);
			btPopText.setText(msg);
			popupWindow = new PopupWindow(menuView, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT, true); // 背景
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
			popupWindow.setAnimationStyle(R.style.PopupAnimation);
			popupWindow.showAtLocation(findViewById(R.id.parent), Gravity.CENTER
					| Gravity.CENTER, 0, 0);
			popupWindow.update();
		} catch (Exception e) {
		}

	}
	/*protected void openPopupWindowAx(String msg) {
		try {
			LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			ViewGroup menuView = (ViewGroup) mLayoutInflater.inflate(
					R.layout.pop_commax, null, true);
			btClose = (Button) menuView.findViewById(R.id.btClose);
			btClose.setOnClickListener(new OnBtCloseClickListenerImpl());
			btClose1 = (Button) menuView.findViewById(R.id.btClose1);
			btClose1.setOnClickListener(new OnBtCloseClickListenerImpl());
			btPopText = (TextView) menuView.findViewById(R.id.btPopText);
			btPopText.setText(msg);
			popupWindow = new PopupWindow(menuView, LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT, true); // 背景
			popupWindow.setBackgroundDrawable(new BitmapDrawable());
			popupWindow.setAnimationStyle(R.style.PopupAnimation);
			popupWindow.showAtLocation(findViewById(R.id.parent), Gravity.CENTER
					| Gravity.CENTER, 0, 0);
			popupWindow.update();
		} catch (Exception e) {
		}

	}*/
	
	protected void openPopupWindowPG(String msg) {
		try {
			LayoutInflater mLayoutInflater = (LayoutInflater) getSystemService(LAYOUT_INFLATER_SERVICE);
			ViewGroup menuView = (ViewGroup) mLayoutInflater.inflate(
					R.layout.pop_gps, null, true);
			popupWindowPg = new PopupWindow(menuView, LayoutParams.FILL_PARENT,
					LayoutParams.FILL_PARENT, true); // 全部背景置灰
			btPopGps = (TextView) menuView.findViewById(R.id.pop_gps);
			popupWindowPg.setBackgroundDrawable(new BitmapDrawable());
			popupWindowPg.setAnimationStyle(R.style.PopupAnimation);
			popupWindowPg.showAtLocation(findViewById(R.id.parent), 
					Gravity.CENTER | Gravity.CENTER, 0, 0);
			popupWindowPg.update();
		} catch (Exception e) {

		}

	}
	
	protected void showToast(CharSequence msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
	
	
	// 加载对话框
		public static final int PROCESS_DIALOG = 0;
		public static final int DELETE_DIALOG = 1;


		@Override
		protected Dialog onCreateDialog(int id) {
			// TODO Auto-generated method stub
			switch (id) {
			case PROCESS_DIALOG:
				ProgressDialog dialog = new ProgressDialog(this);
				/*
				 * dialog.setTitle(getString(R.string.file_copy_title));
				 * dialog.setIcon(R.drawable.alert_dialog_icon);
				 */
				dialog.setMessage("加载中...");
				dialog.setIndeterminate(true);
				dialog.setCancelable(true);
				return dialog;
			case DELETE_DIALOG:
				ProgressDialog dialogDelete = new ProgressDialog(this);
				/*
				 * dialog.setTitle(getString(R.string.file_copy_title));
				 * dialog.setIcon(R.drawable.alert_dialog_icon);
				 */
				dialogDelete.setMessage("删除中...");
				dialogDelete.setIndeterminate(true);
				dialogDelete.setCancelable(false);
				return dialogDelete;

			}
			return null;
		}

		@Override
		protected void onPrepareDialog(int id, Dialog dialog) {
			// TODO Auto-generated method stub

		}

}
