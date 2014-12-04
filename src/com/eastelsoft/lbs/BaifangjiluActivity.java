/**
 * Copyright (c) 2012-8-13 www.eastelsoft.com
 * $ID InfoActivity.java 下午3:22:36 $
 */
package com.eastelsoft.lbs;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;



import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.adapter.InfoListItemAdapterA;
import com.eastelsoft.lbs.adapter.VisitListItemAdapterA;
import com.eastelsoft.lbs.db.DBUtil;
import com.eastelsoft.lbs.db.LocationSQLiteHelper;
import com.eastelsoft.lbs.entity.InfoBean;
import com.eastelsoft.lbs.entity.SetInfo;
import com.eastelsoft.lbs.entity.VisitBean;
import com.eastelsoft.lbs.service.BaiFangService;
import com.eastelsoft.lbs.service.BaiFangService.MBinder;
import com.eastelsoft.util.Contant;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.IUtil;
import com.eastelsoft.util.http.AndroidHttpClient;

/**
 * 拜访记录记录页面
 * 
 * @author lengcj
 */
public class BaifangjiluActivity extends BaseActivity {
	public static final String TAG = "BaifangjiluActivity";
	private ListView lv;
	private Button btBack;
	private Button btAddInfo;
	private LocationSQLiteHelper locationHelper;
	private String info_auto_id; 
//	HashMap<String, Object> localMap;
	// 定义适配器  
	private VisitListItemAdapterA listadpter;  
	private ArrayList<VisitBean> all_info = new ArrayList<VisitBean>();
	private ArrayList<VisitBean> arrayList = new ArrayList<VisitBean>();  
	
	private int number = 50;//每次获取多少条数据
	//总数
	private int totalCount=0;
	//总页数
	private int maxpage =0 ; 
	private boolean loadfinish = true;
	private int index = 0; 
	 private Thread BaifangAutoUploadthread=null;
	View footer;
	private BaiFangService baiFangService;
	private boolean mBound = false;
	
	@Override  
    public void onCreate(Bundle savedInstanceState) {
		FileLog.i(TAG, "onCreate");
		
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_baifangjilu);

        btBack = (Button)findViewById(R.id.btBack);
        btBack.setOnClickListener(new OnBtBackClickListenerImpl());
        btAddInfo = (Button)findViewById(R.id.btAddInfo);
        btAddInfo.setOnClickListener(new OnBtAddInfoClickListenerImpl());
        lv=(ListView)findViewById(android.R.id.list);
        footer = getLayoutInflater().inflate(R.layout.footer, null);
        lv.setOnItemClickListener(new OnItemClickListenerImpl());
        lv.setOnItemLongClickListener(new OnItemLongClickListenerImpl());
        lv.setOnScrollListener(new OnScrollListener(){  
        	public void onScrollStateChanged(AbsListView view, int scrollState) { 
        		
        	}  
            public void onScroll(AbsListView view, int firstVisibleItem,  
        	int visibleItemCount, int totalItemCount) {  
    			if((firstVisibleItem+visibleItemCount)==totalItemCount){//达到数据的最后一条记录
    				if(totalItemCount > 0){
    					if((totalItemCount<totalCount) && loadfinish){
    					loadfinish = false;
						lv.addFooterView(footer);
    			        new Thread(new AsyncUpdateDatasThread()).start();
    					}
    			        
    				}
        	    }
            }
        	});
        locationHelper = new LocationSQLiteHelper(this,
				null, null, 5);
		this.startService(new Intent(this, BaiFangService.class));
		Log.i("BaiFangService","开始service");
		Intent intent = new Intent("com.eastelsoft.lbs.service.BaiFangService");
		this.getApplicationContext().bindService(intent, sc,
				Context.BIND_AUTO_CREATE);
    }
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
		String s = intent.getStringExtra("SEND_SUCE");
			if("seccess".equals(s)){
				init();
			}
		}
	};
	/** 定交ServiceConnection，用于绑定Service的 */
	private ServiceConnection sc = new ServiceConnection() {

		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
			// 已经绑定了LocalService，强转IBinder对象，调用方法得到LocalService对象
			MBinder binder = (MBinder) service;
			baiFangService = binder.getService();
			mBound = true;
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {
			mBound = false;
		}
	};
	
	@Override  
	protected void onDestroy() {  
	    super.onDestroy();  
	    if (locationHelper  != null && locationHelper.getWritableDatabase() != null) {  
	    	locationHelper.getWritableDatabase().close();  
	    }
	    if (mBound) {
			this.getApplicationContext().unbindService(sc);
			mBound = false;
		}
	}  
	
	@Override
	protected void onStart(){
		if(isOnStart){
			IntentFilter filter = new IntentFilter();
			filter.addAction("BAIFANG_ZHENGYUHUI");
			registerReceiver(receiver, filter);		
			init();	
		}else{
			isOnStart =true;
		}
		super.onStart();  
	}
	private boolean isOnStart = true;
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
	}
	
	private class OnBtAddInfoClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {

				Intent intent = new Intent(BaifangjiluActivity.this,BaifangAddActivity.class);  
                startActivity(intent);  
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}

	class AsyncUpdateDatasThread implements Runnable {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			  
            
			index += number;  

			List<VisitBean> list = new ArrayList<VisitBean>();
			list = getNextpageItem(index,number,all_info);
			FileLog.i(TAG, "list "+list.size());

			Message msg = handler.obtainMessage();
			msg.obj = list;
			msg.what = 0;
			handler.sendMessage(msg);
		}
		
	}
	
	
	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			switch (msg.what) {
			case 0:
				arrayList.addAll((List<VisitBean>)(msg.obj));
				
				listadpter.notifyDataSetChanged(); 
				
				if(lv.getFooterViewsCount() > 0) lv.removeFooterView(footer);
				loadfinish = true;  

				break;
            case 1:
				break;	
			}
		}
	};
	
	private void init() {
		 
		
		index=0;
		all_info.clear();
		all_info = DBUtil.getDataFromLVisitA(locationHelper.getWritableDatabase());
		totalCount = all_info.size();
		arrayList = getNextpageItem(index,number,all_info);
		FileLog.i(TAG, arrayList);
		
		// 实例化适配器  
		listadpter = new VisitListItemAdapterA(BaifangjiluActivity.this, arrayList);
		// 填充适配器
		lv.addFooterView(footer);//添加页脚(放在ListView最后)
		lv.setAdapter(listadpter); 
		lv.removeFooterView(footer);
      	
	}
	

	
	private ArrayList<VisitBean> getNextpageItem(int index ,int number, ArrayList<VisitBean> al) {
		ArrayList<VisitBean> a =  new ArrayList<VisitBean>();
		for(int i =0;i<number;i++){
			int temp =i+index;
			if(temp < 0 || temp >= al.size()){
				break;	
			}else{
				VisitBean ib =new VisitBean();
				ib = al.get(temp);
				a.add(ib);	
			}
		}
		
		return a;
	}


	private class OnBtBackClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				BaifangjiluActivity.this.finish();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}
	
	
	
	private class OnItemClickListenerImpl implements OnItemClickListener {
		@Override
        public void onItemClick(AdapterView<?> arg0, View arg1,
                        int position, long id) {
			
			 /** 点击列表项时触发onItemClick方法，四个参数含义分别为 arg0：发生单击事件的AdapterView
			 * arg1：AdapterView中被点击的View position：当前点击的行在adapter的下标
			 * id：当前点击的行的id*/
			isOnStart = false;
			info_auto_id =arrayList.get(position).getId();
			// 跳转到查看页面
			Intent intent = new Intent(BaifangjiluActivity.this,BaifangViewActivity.class);  
			intent.putExtra("info_auto_id", info_auto_id); 
			startActivity(intent);  
        }
	}
	
	private class OnItemLongClickListenerImpl implements OnItemLongClickListener {
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				final int position, long id) {
			new AlertDialog.Builder(BaifangjiluActivity.this)
					.setTitle(Contant.OP)
					.setItems(R.array.planarrcontent,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									String[] PK = getResources()
											.getStringArray(
													R.array.planarrcontent);
									if (PK[which].equals(Contant.OP_DEL)) {
										
										String info_auto_id =arrayList.get(position).getId();
										DBUtil.deleteLVisit(locationHelper.getWritableDatabase(), 
												info_auto_id);
//										arrayList.remove(position);
//										
//										listadpter.notifyDataSetChanged(); // 实现数据的实时刷新
										init();
										Toast.makeText(BaifangjiluActivity.this,
												PK[which]+Contant.OP_SUCC, Toast.LENGTH_SHORT)
												.show();
									}
									if (PK[which].equals(Contant.OP_VIEW)) {
										isOnStart = false;
										String info_auto_id =arrayList.get(position).getId();
										// 跳转到查看页面
										Intent intent = new Intent(BaifangjiluActivity.this,BaifangViewActivity.class);  
										intent.putExtra("info_auto_id", info_auto_id); 
										startActivity(intent);  
									}
								}
							})
					.setNegativeButton(Contant.OP_CLOSE,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									// 关闭
								}
							}).show();
			return true;
		}
		
	}
}
