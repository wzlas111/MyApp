/**
 * Copyright (c) 2012-7-21 www.eastelsoft.com
 * $ID TabRegActivity.java 上午12:42:13 $
 */
package com.eastelsoft.lbs;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.activity.reg.UnboundlingStep1Activity;
import com.eastelsoft.lbs.entity.AuthCentreResp;
import com.eastelsoft.lbs.entity.Login1Resp;
import com.eastelsoft.lbs.entity.Login2Resp;
import com.eastelsoft.lbs.entity.RegResp;
import com.eastelsoft.lbs.entity.SetInfo;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.GlobalVar;
import com.eastelsoft.util.IUtil;
import com.eastelsoft.util.UMMPUtil;
import com.eastelsoft.util.Util;

/**
 * 注册页面
 * 
 * @author lengcj
 */
public class RegActivity extends BaseActivity {

	private static final String TAG = "RegActivity";
	private Button btReg;
	private Button btUnbundling;
	private EditText editReg;
	private AuthCentreResp authCentreResp;
	private RegResp regResp;
	private Login1Resp login1Resp;

	private String phoneBrand = "NULL";
	private String phoneModel = "NULL";
	private String phoneOs = "NULL";
	private String softVersion = "NULL";
	private String phoneResolution = "NULL";

	private SetInfo set;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reg);

		btReg = (Button) findViewById(R.id.btReg);
		btReg.setOnClickListener(new OnBtRegClickListenerImpl());
		btUnbundling = (Button) findViewById(R.id.btUnbundling);
		btUnbundling.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RegActivity.this, UnboundlingStep1Activity.class);
				startActivity(intent);
			}
		});
		editReg = (EditText) findViewById(R.id.editReg);
		// 设置数字键盘
		editReg.setInputType(EditorInfo.TYPE_CLASS_PHONE);
		// 获取imei、imsi
		telephonyManager = (TelephonyManager) this
				.getSystemService(Context.TELEPHONY_SERVICE);
		imei = telephonyManager.getDeviceId() != null ? telephonyManager
				.getDeviceId() : "1234567890";
		imsi = telephonyManager.getSubscriberId();
		if (imsi == null || "".equals(imsi)) {
			imsi = "11111111";
		}

		getphoneinformaition();
		// 初始化全局变量
		globalVar = (GlobalVar) getApplicationContext();
		// 初始化文件服务类
		// fileService = new FileService(TabRegActivity.this);

		// 检查版本更新
		// Thread checkVersionTask = new Thread(new CheckVersionTask(this));
		// checkVersionTask.start();

		// 初始化sp
		sp = getSharedPreferences("userdata", 0);
	}

	private void getphoneinformaition() {
		// TODO Auto-generated method stub
		try {
			softVersion = this.getPackageManager().getPackageInfo(
					this.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			softVersion = "NULL";
		}

		if (Build.BRAND != null) {
			phoneBrand = Build.BRAND;
		}
		if (Build.MODEL != null) {
			phoneModel = Build.MODEL;
		}
		if (Build.VERSION.RELEASE != null) {
			phoneOs = Build.VERSION.RELEASE;
		}

		try {
			DisplayMetrics metric = new DisplayMetrics();
			getWindowManager().getDefaultDisplay().getMetrics(metric);
			int width = metric.widthPixels; // 屏幕宽度（像素）
			int height = metric.heightPixels; // 屏幕高度（像素）
			String w = String.valueOf(width);
			String h = String.valueOf(height);
			StringBuffer s = new StringBuffer();
			s.append(w);
			s.append("*");
			s.append(h);
			phoneResolution = s.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			phoneResolution = "NULL";
		}

		FileLog.i(TAG, softVersion);
		FileLog.i(TAG, phoneBrand);
		FileLog.i(TAG, phoneModel);
		FileLog.i(TAG, phoneOs);
		FileLog.i(TAG, phoneResolution);

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	
	/**
	 * 监听确认按钮
	 * 
	 */
	private class OnBtRegClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			networkAvailable = isNetworkAvailable();
			serialNumber = editReg.getText().toString();
			// mobile = new Mobile(serialNumber);

			if (networkAvailable) {
				// if(mobile.getFacilitatorType() > -1) {
				if (Util.chkNumber(serialNumber)) {
					// 关闭输入法
					InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
					boolean isOpen = inputMethodManager.isActive();
					if (isOpen)
						inputMethodManager.hideSoftInputFromWindow(
								RegActivity.this.getCurrentFocus()
										.getWindowToken(),
								InputMethodManager.HIDE_NOT_ALWAYS);

					// pDialog = new ProgressDialog(RegActivity.this);
					// pDialog.setTitle(getResources().getString(R.string.checkin_tips));
					// pDialog.setMessage(getResources().getString(R.string.loading));
					// pDialog.show();
					RegActivity.this.openPopupWindowPG("");
					btPopGps.setText(getResources().getString(
							R.string.loading_reg));
					Thread regThread = new Thread(new RegisterThread());
					regThread.start();
				} else {
					respMsg = getResources().getString(R.string.reg_edit_error);
					Toast.makeText(getApplicationContext(), respMsg,
							Toast.LENGTH_SHORT).show();
				}
			} else {
				respMsg = getResources().getString(R.string.net_error);
				Toast.makeText(getApplicationContext(), respMsg,
						Toast.LENGTH_SHORT).show();
			}
		}
	}

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			try {
				// pDialog.cancel();
				try {
					popupWindowPg.dismiss();
				} catch (Exception e) {
					FileLog.e(TAG, e.toString());
				}
				if (msg.what == 1) {
					respMsg = msg.obj.toString();
					Toast.makeText(getApplicationContext(), respMsg,
							Toast.LENGTH_SHORT).show();
				}
				if (msg.what == 0) {
					Intent intent = new Intent();
					intent.setClass(RegActivity.this, MainActivity.class);
					intent.putExtra("is_reg", true);
					startActivity(intent);
					finish();
				}
			} catch (Exception e) {
				// 异常中断
			}
		}
	};

	/**
	 * 注册线程类
	 * 
	 */
	class RegisterThread implements Runnable {
		@Override
		public void run() {
			Looper.prepare();
			Message msg = new Message();
			try {
				// 保存测试数据
				// sp = getSharedPreferences("userdata",0);
				// Editor editor = sp.edit();
				// editor.putString("adapter_ip", "211.0.0.1");
				// editor.putString("adapter_port", "12312");
				// editor.putString("auth_code", "abcdeefff");
				// editor.putString("serialNumber", "13968692253");
				// editor.putString("device_id", "0000000000000000");
				// editor.putString("timePeriod", "07:00-22:59");
				// editor.putString("interval", "600"); // 这里放的是秒
				// editor.putString("week", "1111111");
				// editor.putString("filterDate", "20121001,20121002");
				// editor.putString("minDistance", "1000");
				// editor.putString("imei", imei);
				// imsi = "460030912121001";
				// editor.putString("imsi", imsi);
				// editor.commit();
				//
				// msg.what = 0;
				// handler.sendMessage(msg);
				// 进入统一认证中心
				authCentreResp = UMMPUtil.sendAuthCentreReg((short) 0,
						serialNumber);
				System.out.println("authCentreResp=");
				System.out.println(authCentreResp);
				// System.out.println(authCentreResp.getGateudpport());

				if (authCentreResp != null
						&& "0".equals(authCentreResp.getRet())) {
					// 保存统一认证中心返回数据

					IUtil.writeSharedPreferences(sp, authCentreResp);
					sp = getSharedPreferences("userdata", 0);
					set = IUtil.initSetInfo(sp);

					// 注册
					// regResp = UDPUtil.sendReg((short) 0, serialNumber, imei,
					// imsi
					// ,phoneBrand,phoneModel,phoneOs,softVersion,phoneResolution);
					// phoneBrand = "索爱";
					// phoneModel = "SA-i950至尊版";
					regResp = UMMPUtil.sendReg((short) 0, serialNumber, imei,
							imsi, phoneBrand, phoneModel, phoneOs, softVersion,
							phoneResolution, set);
					Log.i("InfoAddActivity", regResp+"");
					if (regResp != null && "0".equals(regResp.getRet())) {
						// 开始登录
						FileLog.i(TAG, regResp.toString());
						/* msg_seq = IUtil.getMsgReq(globalVar); */
						// SetInfo set = new SetInfo();
						set.setSerialNumber(serialNumber);
						set.setImei(imei);
						set.setImsi(imsi);
						set.setDevice_id(regResp.getDevice_id());
						// login1Resp = UDPUtil.sendLogin1((short) 0,
						// set,softVersion);
						login1Resp = UMMPUtil.sendLogin1((short) 0, set,
								phoneModel, softVersion);
						if (login1Resp != null
								&& "0".equals(login1Resp.getRet())) {
							FileLog.i(TAG, login1Resp.toString());
							// 二次登录
							// short msg_seq = IUtil.getMsgReq(globalVar);

							// tcp方式发送数据
							// byte[] data = UDPUtil.getLogin2ReqData(msg_seq,
							// set, login1Resp);
							byte[] data = UMMPUtil.getLogin2ReqData((short) 0,
									set, login1Resp);
							// Login2Resp login2Resp =
							// TcpPackage.sendLogin2(login1Resp.getAdapter_ip(),
							// login1Resp.getAdapter_port(), data);

							Login2Resp login2Resp = UMMPUtil.sendLogin2(
									login1Resp.getAdapter_ip(),
									login1Resp.getAdapter_port(), data);

							if (login2Resp != null
									&& ("0".equals(login2Resp.getRet()) || "2"
											.equals(login2Resp.getRet()))) {
								sp = getSharedPreferences("userdata", 0);
								FileLog.i(TAG, login2Resp.toString());
								IUtil.writeSharedPreferences(sp, regResp,
										login1Resp, login2Resp, serialNumber,
										imei, imsi);
								msg.what = 0;
								msg.obj = respMsg;
								handler.sendMessage(msg);

							} else {
								respMsg = getResources().getString(
										R.string.login_error2);
								msg.what = 1;
								msg.obj = respMsg;
								handler.sendMessage(msg);
							}
						} else {
							// 认证中心登录失败后的提示
							respMsg = getResources().getString(
									R.string.login_error1);
							if (login1Resp != null) {
								if (login1Resp.getRet() != null
										&& "1".equals(login1Resp.getRet())) {
									respMsg = getResources().getString(
											R.string.login_error11);
								}
								if (login1Resp.getRet() != null
										&& "2".equals(login1Resp.getRet())) {
									respMsg = getResources().getString(
											R.string.login_error12);
								}
								if (login1Resp.getRet() != null
										&& "3".equals(login1Resp.getRet())) {
									respMsg = getResources().getString(
											R.string.login_error13);
								}
								if (login1Resp.getRet() != null
										&& "4".equals(login1Resp.getRet())) {
									respMsg = getResources().getString(
											R.string.login_error14);
								}
							}
							msg.what = 1;
							msg.obj = respMsg;
							handler.sendMessage(msg);
						}
					} else {
						// 注册失败，给出各种提示
						respMsg = getResources().getString(R.string.reg_error);
						if (regResp != null) {
							if (regResp.getRet() != null
									&& "1".equals(regResp.getRet())) {
								respMsg = getResources().getString(
										R.string.reg_error1);
							}
							if (regResp.getRet() != null
									&& "2".equals(regResp.getRet())) {
								respMsg = getResources().getString(
										R.string.reg_error2);
							}
							if (regResp.getRet() != null
									&& "3".equals(regResp.getRet())) {
								respMsg = getResources().getString(
										R.string.reg_error3);
							}
							if (regResp.getRet() != null
									&& "4".equals(regResp.getRet())) {
								respMsg = getResources().getString(
										R.string.reg_error4);
							}
						}
						msg.what = 1;
						msg.obj = respMsg;
						handler.sendMessage(msg);
					}
				} else {
					respMsg = getResources().getString(R.string.authc_error);
					if (regResp != null) {
						if (regResp.getRet() != null
								&& "1".equals(regResp.getRet())) {
							respMsg = getResources().getString(
									R.string.authc_error1);
						}
						if (regResp.getRet() != null
								&& "2".equals(regResp.getRet())) {
							respMsg = getResources().getString(
									R.string.authc_error2);
						}

					}
					msg.what = 1;
					msg.obj = respMsg;
					handler.sendMessage(msg);
				}

			} catch (Exception e) {
				FileLog.e(TAG, e.toString() + "");
				respMsg = getResources().getString(R.string.reg_error);
				msg.what = 1;
				msg.obj = respMsg;
				handler.sendMessage(msg);
			}

			Looper.loop();
		}
	}

	// ScrollView用dispatchTouchEvent
	@Override
	public boolean dispatchTouchEvent(MotionEvent event) {
		Rect localRect1 = new Rect();
		((EditText) findViewById(R.id.editReg))
				.getGlobalVisibleRect(localRect1);
		Rect localRect3 = new Rect((int) event.getX(), (int) event.getY(),
				(int) event.getX(), (int) event.getY());
		if (!localRect1.intersect(localRect3))
			((InputMethodManager) getSystemService("input_method"))
					.hideSoftInputFromWindow(getWindow().peekDecorView()
							.getWindowToken(), 0);
		return super.dispatchTouchEvent(event);
	}

	// private CallBack tcpCallback = new CallBack() {
	// public void execute(Object[] paramArrayOfObject) {
	// Message msg = handler.obtainMessage();
	// msg.what = 2;
	//
	// TcpPackage pack = null;
	// if(paramArrayOfObject[0] != null)
	// pack = (TcpPackage)paramArrayOfObject[0];
	// if(pack == null) {
	// respMsg = getResources().getString(R.string.second_login_error);
	// msg.what = 1;
	// } else {
	// Login2Resp login2Resp = pack.getLogin2Resp();
	// if (login2Resp != null
	// && ( "0".equals(login2Resp.getRet())
	// || "2".equals(login2Resp.getRet()))) {
	// sp = getSharedPreferences("userdata",0);
	// IUtil.writeSharedPreferences(sp, regResp, login1Resp, login2Resp,
	// serialNumber, imei, imsi);
	// msg.what = 0;
	// } else {
	// respMsg = getResources().getString(R.string.second_login_error);
	// msg.what = 1;
	// }
	// }
	// msg.obj = respMsg;
	// handler.sendMessage(msg);
	// }
	// };
}
