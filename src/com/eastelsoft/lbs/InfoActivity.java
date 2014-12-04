/**
 * Copyright (c) 2012-8-13 www.eastelsoft.com
 * $ID InfoActivity.java 下午3:22:36 $
 */
package com.eastelsoft.lbs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
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



import com.eastelsoft.lbs.CustActivity.InitThread;
import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.lbs.adapter.InfoListItemAdapterA;
import com.eastelsoft.lbs.db.DBUtil;
import com.eastelsoft.lbs.db.LocationSQLiteHelper;
import com.eastelsoft.lbs.entity.InfoBean;
import com.eastelsoft.lbs.service.LocationService;
import com.eastelsoft.lbs.service.LocationService.MBinder;
import com.eastelsoft.util.Contant;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.GlobalVar;

/**
 * 信息上报记录页面
 * 
 * @author lengcj
 */
public class InfoActivity extends BaseActivity {
	public static final String TAG = "InfoActivity";
	private ListView lv;
	private Button btBack;
	private Button btAddInfo;
	private LocationSQLiteHelper locationHelper;
	// 定义适配器  
	private InfoListItemAdapterA listadpter;  
	private ArrayList<InfoBean> all_info = new ArrayList<InfoBean>();
	private ArrayList<InfoBean> arrayList = new ArrayList<InfoBean>();  
	private int number = 50;//每次获取多少条数据
	//总数
	private int totalCount=0;
	//总页数
	private int maxpage =0 ; 
	private boolean loadfinish = true;
	private int index = 0; 
	View footer;	
	
	@Override  
    public void onCreate(Bundle savedInstanceState){
		FileLog.i(TAG, "onCreate");
        super.onCreate(savedInstanceState);  
        setContentView(R.layout.activity_info);  
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

            	
            	/*final int loadtotal = totalItemCount;*/
    			/*int lastItemid = lv.getLastVisiblePosition();//获取当前屏幕最后Item的ID
                */    			
    			if((firstVisibleItem+visibleItemCount)==totalItemCount){//达到数据的最后一条记录
    				if(totalItemCount > 0){
    					//当前页
    					
    					/*int currentpage = totalItemCount%number == 0 ? totalItemCount/number : totalItemCount/number+1;
    					FileLog.i(TAG, currentpage);
    					FileLog.i(TAG, maxpage);*/
    					//int nextpage = currentpage + 1;//下一页
    					 
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
        // 初始化全局变量
        globalVar = (GlobalVar) getApplicationContext();

    }

	@Override  
	protected void onDestroy() {  
	    super.onDestroy();  
	    if (locationHelper  != null && locationHelper.getWritableDatabase() != null) {  
	    	locationHelper.getWritableDatabase().close();
	    }
	}  
	
	@Override
	protected void onStart() {
//		if(isOnStart){
			IntentFilter filter = new IntentFilter();			
			filter.addAction("ACTION_ZHENGYUHUI");
			registerReceiver(receiver, filter);			
			init();	
//		}else{
//			isOnStart =true;
//		}
		
		super.onStart();  
	}
	private boolean isOnStart = true;
	
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		
	}
	
	
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		FileLog.i(TAG, "onNewIntent");
		init();	 
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

			List<InfoBean> list = new ArrayList<InfoBean>();
			

			/*list = DBUtil.getAllItems(index, number,locationHelper.getWritableDatabase()); */ 
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
				arrayList.addAll((List<InfoBean>)(msg.obj));
				
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
		totalCount = DBUtil.getCount(locationHelper.getWritableDatabase());  
		FileLog.i(TAG, totalCount);
		maxpage = totalCount%number == 0 ? totalCount/number : totalCount/number+1;
		FileLog.i(TAG, maxpage);
		all_info.clear();
		all_info = DBUtil.getDataFromLInfoA(locationHelper.getWritableDatabase());
		
		/*arrayList = DBUtil.getAllItems(index, 20,locationHelper.getWritableDatabase()); */
		arrayList = getNextpageItem(index,number,all_info);
		FileLog.i(TAG, arrayList);
		
		// 实例化适配器  
		listadpter = new InfoListItemAdapterA(InfoActivity.this, arrayList);
		// 填充适配器
		lv.addFooterView(footer);//添加页脚(放在ListView最后)
		lv.setAdapter(listadpter); 
		
		lv.removeFooterView(footer);
      	
	}
	

	
	private ArrayList<InfoBean> getNextpageItem(int index ,int number, ArrayList<InfoBean> al) {
		ArrayList<InfoBean> a =  new ArrayList<InfoBean>();
		for(int i =0;i<number;i++){
			int temp =i+index;
			if(temp < 0 || temp >= al.size()){
				break;	
			}else{
				InfoBean ib =new InfoBean();
				ib = al.get(temp);
				a.add(ib);	
			}
		}
		
		return a;
	}


	private class OnBtBackClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				InfoActivity.this.finish();
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}
	
	private class OnBtAddInfoClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				// 先清理掉原始数据
				globalVar.setInfoLocation(null);
				globalVar.setImgs(new String[0]);
				globalVar.setVideo1("");
				globalVar.setTitle("");
				globalVar.setRemark("");
				Intent intent = new Intent(InfoActivity.this,InfoAddActivity.class);  
                startActivity(intent);  
			} catch (Exception e) {
				FileLog.e(TAG, e.toString());
			}
		}
	}
	
	private class OnItemClickListenerImpl implements OnItemClickListener {
		@Override
        public void onItemClick(AdapterView<?> arg0, View arg1,
                        int position, long id) {
//			isOnStart = false;
			String info_auto_id =arrayList.get(position).getInfo_auto_id();
			// 跳转到查看页面
			Intent intent = new Intent(InfoActivity.this,InfoViewActivity.class);  
			intent.putExtra("info_auto_id", info_auto_id); 
			startActivity(intent);  
        }
	}
	
	private class OnItemLongClickListenerImpl implements OnItemLongClickListener {
		@Override
		public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
				final int position, long id) {
			new AlertDialog.Builder(InfoActivity.this)
					.setTitle(Contant.OP)
					.setItems(R.array.infoarrcontent,
							new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog,
										int which) {
									String[] PK = getResources()
											.getStringArray(
													R.array.infoarrcontent);
									if (PK[which].equals(Contant.OP_DEL)) {
										/*String info_auto_id = mData.get(position)
												.get("info_auto_id").toString();*/
										String info_auto_id =arrayList.get(position).getInfo_auto_id();
										DBUtil.deleteLInfo(locationHelper.getWritableDatabase(), 
												info_auto_id);
//										arrayList.remove(position);
//										listadpter.notifyDataSetChanged(); // 实现数据的实时刷新
										init();
										Toast.makeText(InfoActivity.this,
												PK[which]+Contant.OP_SUCC, Toast.LENGTH_SHORT)
												.show();
									}
									if (PK[which].equals(Contant.OP_ADD)) {
										// 跳转到新增页面
										Intent intent = new Intent(InfoActivity.this,InfoAddActivity.class);  
						                startActivity(intent);  
									}
									if (PK[which].equals(Contant.OP_VIEW)) {
//										isOnStart = false;
										String info_auto_id =arrayList.get(position).getInfo_auto_id();
										// 跳转到查看页面
										Intent intent = new Intent(InfoActivity.this,InfoViewActivity.class);  
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

	private BroadcastReceiver receiver = new BroadcastReceiver(){
		@Override
		public void onReceive(Context context, Intent intent){
			Log.i("LocationService", "广播已接收");
		String s = intent.getStringExtra("SEND_SUCESS");
			if("send_success".equals(s)){
				init();			
			}
		}
	};
}
