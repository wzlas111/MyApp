/**
 * Copyright (c) 2012-7-21 www.eastelsoft.com
 * $ID TabAboutActivity.java 上午12:42:13 $
 */
package com.eastelsoft.lbs;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.entity.SetInfo;
import com.eastelsoft.lbs.location.BaseStationAction;
import com.eastelsoft.lbs.location.BaseStationAction.SItude;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.IUtil;

/**
 * 关于页面
 * @author lengcj
 */
public class AboutActivity extends BaseActivity {
	
	private static final String TAG = "AboutActivity";
	
	private TextView tvVersion;
	private Button btBack;
	private Button btChkVer;
	private TextView tv_lt;
	private TextView tv_remind_desc;
	private SetInfo set;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        // 检测上下文数据
        sp = getSharedPreferences("userdata", 0);
		set = IUtil.initSetInfo(sp);
        btBack = (Button)findViewById(R.id.btBack);
        btBack.setOnClickListener(new OnBtBackClickListenerImpl());
        btChkVer = (Button) findViewById(R.id.chkVer);
		//btnRefresh.setOnClickListener(OnClickEvent);
        btChkVer.setOnClickListener(new OnBtChkVerClickListenerImpl());
        tvVersion = (TextView)findViewById(R.id.version_desc);
        // 获取版本号
        String versionName = IUtil.getVerName(AboutActivity.this);
        String version_desc = getResources().getString(
				R.string.version_desc);
		tvVersion.setText(version_desc + versionName);
		
		tv_lt = (TextView) findViewById(R.id.tv_lt);
		tv_remind_desc = (TextView) findViewById(R.id.tv_remind_desc);
		if("0".equals(set.getPfsign())){
			tv_lt.setVisibility(View.GONE);
		}

    }
    
    private class OnBtChkVerClickListenerImpl implements OnClickListener {
    	
		public void onClick(View v) {
			networkAvailable = isNetworkAvailable();
			// 测试基站定位
//			Thread thread = new Thread(new LocationThread());
//			thread.start();
    		if(networkAvailable) {
    			AboutActivity.this.openPopupWindowPG("");
				btPopGps.setText(getResources().getString(R.string.loading_gengxibanben));
    			Thread checkVersionTask = new Thread(new CheckVersionTask1(AboutActivity.this));
				checkVersionTask.start();
    		} else {
    			respMsg = getResources().getString(R.string.net_error);
				Toast.makeText(getApplicationContext(), respMsg, Toast.LENGTH_SHORT).show();
    		}
		}
	}
    
	class LocationThread implements Runnable {
		@Override
		public void run() {
			SItude location = new BaseStationAction(AboutActivity.this).location1();
//			Location location = new WifiAction().startLisinter(TabAboutActivity.this);
			Message msg = handler.obtainMessage();
			msg.obj = location;
			handler.sendMessage(msg);
		}
	}
	
	private Handler handler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			try {
				if(msg.obj != null)
					dialog(AboutActivity.this,msg.obj.toString());
				else
					dialog(AboutActivity.this,"test");
			} catch (Exception e) {
				// 异常中断
			}
		}
	};
	
	private class OnBtBackClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				AboutActivity.this.finish();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}
}
