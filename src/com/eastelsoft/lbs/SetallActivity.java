/**
 * Copyright (c) 2013-6-6 www.eastelsoft.com
 * $ID SetallActivity.java 下午4:17:18 $
 */
package com.eastelsoft.lbs;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.util.Contant;
import com.eastelsoft.util.DeleteFileUtil;
import com.eastelsoft.util.FileLog;

/**
 * 2.0版本设置界面
 * 
 * @author lengcj
 */
public class SetallActivity extends BaseActivity {

	private static final String TAG = "SetallActivity";
	private Button btBack;
	private LinearLayout ll_set_userinfo;
	private LinearLayout ll_set_msg_amind;
	private LinearLayout ll_set_system_para;
	private LinearLayout ll_set_clear_history;
	private LinearLayout ll_set_help;
	private LinearLayout ll_set_version;
	private LinearLayout ll_set_about;
	private LinearLayout ll_set_daily;
//	private ProgressDialog pd;
	private String path=Environment.getExternalStorageDirectory()+"/DCIM/eastelsoft/";
	private String[] choiceItem=new String[]{"全部","一周前","一个月前"};
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		try {
			setContentView(R.layout.activity_setall);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		initView();
	}
	
//	private Handler handler = new Handler(){
//		@Override
//		public void handleMessage(Message msg) {
//			// TODO Auto-generated method stub
//			super.handleMessage(msg);
//			if(msg.what==0){
//				DeleteFileUtil.delAllFile(path, Contant.ALLTIME);
//			}else if(msg.what==1){
//				DeleteFileUtil.delAllFile(path, Contant.WEEKTIME);
//			}else if(msg.what==2){
//				DeleteFileUtil.delAllFile(path, Contant.MOUTHTIME);
//			}
//			pd.dismiss();
//			if(DeleteFileUtil.ExistFlag==true){
//				new AlertDialog.Builder(SetallActivity.this).setTitle("提示").setMessage(choiceItem[msg.what]+"的文件删除成功").setPositiveButton("确定", null).show();
//			}else if(DeleteFileUtil.ExistFlag==false){
//				new AlertDialog.Builder(SetallActivity.this).setTitle("提示").setMessage("没有"+choiceItem[msg.what]+"文件").setPositiveButton("确定", null).show();
//			}
//			DeleteFileUtil.ExistFlag=false;
//		}
//	};
	
	private void initView() {
		btBack = (Button)findViewById(R.id.btBack);
        btBack.setOnClickListener(new OnBtBackClickListenerImpl());
        ll_set_userinfo = (LinearLayout) findViewById(R.id.set_userinfo);
        ll_set_msg_amind = (LinearLayout) findViewById(R.id.set_msg_amind);
        ll_set_system_para = (LinearLayout) findViewById(R.id.set_system_para);
        ll_set_clear_history = (LinearLayout) findViewById(R.id.set_clear_history);
        ll_set_help = (LinearLayout) findViewById(R.id.set_help);
        ll_set_version = (LinearLayout) findViewById(R.id.set_version);
        ll_set_about = (LinearLayout) findViewById(R.id.set_about);
        ll_set_daily=(LinearLayout)findViewById(R.id.set_daily);
        ll_set_userinfo.setOnClickListener(new OnSetUserinfoClickListenerImpl());
        ll_set_msg_amind.setOnClickListener(new OnSetMsgAmindClickListenerImpl());
        ll_set_system_para.setOnClickListener(new OnSetSystemParaClickListenerImpl());
        ll_set_clear_history.setOnClickListener(new OnSetClearHistoryClickListenerImpl());
        ll_set_help.setOnClickListener(new OnSetHelpClickListenerImpl());
        ll_set_version.setOnClickListener(new OnBtChkVerClickListenerImpl());
        ll_set_about.setOnClickListener(new OnSetAboutClickListenerImpl());
        ll_set_daily.setOnClickListener(new OnSetDailyClickListenerImpl());
	}
	
	@Override
	protected void onDestroy() {
		FileLog.i(TAG, "onDestroy....");
		super.onDestroy();
	}
	
	
	private class OnBtBackClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				SetallActivity.this.finish();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}
	
	private class OnSetUserinfoClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				Intent intent = new Intent(SetallActivity.this, UserinfoActivity.class);
				startActivity(intent);
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}
	
	private class OnSetMsgAmindClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				Intent intent = new Intent(SetallActivity.this, MsgAmindActivity.class);
				startActivity(intent);
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}
	
	private class OnSetSystemParaClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				Intent intent = new Intent(SetallActivity.this, SystemParaActivity.class);
				startActivity(intent);
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}
	
	private class OnSetClearHistoryClickListenerImpl implements OnClickListener {
		
//		int i=0;
		public void onClick(View v) {
			try {
				Intent intent = new Intent(SetallActivity.this, ClearHistoryActivity.class);
				startActivity(intent);
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
//			new AlertDialog.Builder(SetallActivity.this)
//		 	.setTitle("选择删除日期")
//		 	.setIcon(android.R.drawable.ic_dialog_info)                
//		 	.setSingleChoiceItems(choiceItem, 0, 
//		 			new DialogInterface.OnClickListener() {                         
//		 	     public void onClick(DialogInterface dialog, int which) {
//		 	    	 i=which;
//			 	     }
//			 	  }
//		 	)
//		 	.setNegativeButton("取消", null)
//		 	.setPositiveButton("确认", 
//		 			new DialogInterface.OnClickListener() {
//				@Override
//				public void onClick(DialogInterface arg0, int arg1) {
//					// TODO Auto-generated method stub
//					new AlertDialog.Builder(SetallActivity.this) 
//				 	.setTitle("确认")
//				 	.setMessage("确定要删除"+choiceItem[i]+"的文件吗？")
//				 	.setPositiveButton("是", new DialogInterface.OnClickListener() {
//
//						@Override
//						public void onClick(DialogInterface arg0, int arg1) {
//							// TODO Auto-generated method stub
//							Message message =new Message();
//							if(i==0){
//								message.what=0;
//								pd = ProgressDialog.show(SetallActivity.this, "删除所有的文件", "正在删除中......");
//								handler.sendMessage(message);
//							}else if(i==1){
//								message.what=1;
//								pd = ProgressDialog.show(SetallActivity.this, "删除一周前的文件", "正在删除中......");
//								handler.sendMessage(message);
//							}else if(i==2){
//								message.what=2;
//								pd = ProgressDialog.show(SetallActivity.this, "删除一月前的文件", "正在删除中......");
//								handler.sendMessage(message);
//							}
//							i=0;
//						}
//				 	}
//					)
//				 	.setNegativeButton("否", null)
//				 	.show();
//				}
//			}).show();
		}
	}
	
	private class OnSetHelpClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				Intent intent = new Intent(SetallActivity.this, HelpActivity.class);
				startActivity(intent);
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}
	
	private class OnSetAboutClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				Intent intent = new Intent(SetallActivity.this, AboutActivity.class);
				startActivity(intent);
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}
	
	
	private class OnSetDailyClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				Intent intent = new Intent(SetallActivity.this, DailyActivity.class);
				startActivity(intent);
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}
	
	private class OnBtChkVerClickListenerImpl implements OnClickListener {
    	
		public void onClick(View v) {
			networkAvailable = isNetworkAvailable();
    		if(networkAvailable) {
    			SetallActivity.this.openPopupWindowPG("");
				btPopGps.setText(getResources().getString(R.string.loading_gengxibanben));
    			Thread checkVersionTask = new Thread(new CheckVersionTask1(SetallActivity.this));
				checkVersionTask.start();
    		} else {
    			respMsg = getResources().getString(R.string.net_error);
				Toast.makeText(getApplicationContext(), respMsg, Toast.LENGTH_SHORT).show();
    		}
		}
	}
}