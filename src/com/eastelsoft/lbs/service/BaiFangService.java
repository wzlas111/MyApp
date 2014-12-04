
package com.eastelsoft.lbs.service;
import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.eastelsoft.lbs.db.DBUtil;
import com.eastelsoft.lbs.db.LocationSQLiteHelper;
import com.eastelsoft.lbs.entity.SetInfo;
import com.eastelsoft.lbs.entity.VisitBean;
import com.eastelsoft.util.Contant;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.IUtil;
import com.eastelsoft.util.http.AndroidHttpClient;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 *@创建时间：2014-8-22 上午9:27:27 
 *  
 *@author ZhengYh 
 *
 *@类说明：拜访记录后台---用于监听网络，并自动上传未上传的客户记录信息
 */
public class BaiFangService extends Service{
public static final String TAG="BaiFangService";
private ConnectivityManager connectivityManager;
private NetworkInfo info;
private boolean netchange = false;
private boolean isInfoUploading = false; // 当前是否正在进行文件上传
private int nettimes = 0;
private LocationSQLiteHelper locationHelper;
private SharedPreferences sp;
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
		locationHelper = new LocationSQLiteHelper(BaiFangService.this, null,
				null, 5);
		IntentFilter mFilter = new IntentFilter();
		mFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
		registerReceiver(mReceiver, mFilter);
	}

/**
 * 
 * 网络监听
 */
private BroadcastReceiver mReceiver = new BroadcastReceiver() {
	@Override
	public void onReceive(Context context, Intent intent){
		String action = intent.getAction();
		if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
			Log.d(TAG, "网络状态已经改变");
			connectivityManager = (ConnectivityManager)
			getSystemService(Context.CONNECTIVITY_SERVICE);
			info = connectivityManager.getActiveNetworkInfo();
			netchange = true; // 网络状态发生了变化
			nettimes += 1; // 网络变化的次数
			if (info != null && info.isAvailable()) {
				String name = info.getTypeName();
				FileLog.i(TAG + "network", "当前网络名称：" + name);
				// 有网络，检测是否有未上传的缓存信息
				if (!isInfoUploading) {
					Thread mThread = new Thread(new BaifangAutoUpload());
					mThread.start();
				}
			} else {
				Log.d(TAG + "network", "没有可用网络");
			}
		}
	}
};


/*
 * 增加自动上传的线程
 */
 class BaifangAutoUpload implements Runnable{
	private String title;
	private String remark;
	private String date;
	private String lon;
	private String lat;
	private String myid;
	private String clientName;
	private String imgFileName;
	private File imgFile=null;
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Intent intent= new Intent("BAIFANG_ZHENGYUHUI");
		isInfoUploading = true;
		try {
		Thread.sleep(10000);

		List<VisitBean> localMap = DBUtil.getDataFromLVisitA(locationHelper.getWritableDatabase());
		Log.i("BaiFangService", localMap.toString());
		for (VisitBean visitBean : localMap){
			if("00".equals(visitBean.getIstijiao())){
				sp = getSharedPreferences("userdata", 0);
				SetInfo set = IUtil.initSetInfo(sp);
				String url = set.getHttpip() + Contant.ACTION;
				Map<String, String> map = new HashMap<String, String>();
				map.put("reqCode", Contant.VISIT_UPLOAD_ACTION);
				map.put("gpsid", set.getDevice_id());
				map.put("pin", set.getAuth_code());
				if (visitBean.getClientid()!= null){
					this.myid=visitBean.getClientid();
					Log.e(TAG, myid);
					map.put("clientid", myid);
				}
				if (visitBean.getClientName()!= null){
					this.clientName=visitBean.getClientName();
					Log.e(TAG, clientName);
					map.put("clientname", clientName);
				}
				if(visitBean.getTitle()!=null){
					this.title = visitBean.getTitle();
					Log.e(TAG,title);
					map.put("title",title);
				}
				if(visitBean.getRemark()!=null){
					this.remark = visitBean.getRemark();
					Log.e(TAG, remark);
					map.put("remark",remark);
				}
				if(visitBean.getDate()!=null){
					this.date = visitBean.getDate();
					Log.e(TAG,date);
					map.put("date", date);
				}
				if(visitBean.getLon()!=null){
					this.lon = visitBean.getLon();
					Log.e(TAG,lon);
					map.put("lon",lon);
				}
				if(visitBean.getLat()!=null){
					this.lat=visitBean.getLat();
					Log.e(TAG,lat);
					map.put("lat", lat);
				}
				map.put("accuracy", "-100");
				if(visitBean.getImgFile()!=null&&!"".equals(visitBean.getImgFile())){
					this.imgFileName =visitBean.getImgFile();
					this.imgFile = new File("/mnt/sdcard/DCIM/eastelsoft/"+imgFileName);
				}
				FileLog.i(TAG, "拜访记录上传的信息为"+map.toString());
				String jsonStr = AndroidHttpClient.getContent(url, map,imgFile, "file1");
				jsonStr = IUtil.chkJsonStr(jsonStr);
				FileLog.i(TAG, "上传信息后，后台RESPONSE返回的是"+jsonStr);
				JSONArray array;
				array = new JSONArray(jsonStr);
				String resultcode = "";
				if (array.length() > 0) {
					JSONObject obj = array.getJSONObject(0);
					resultcode = obj.getString("ResultCode");
				}
				if("1".equals(resultcode)){
					intent.putExtra("SEND_SUCE", "seccess");
					sendBroadcast(intent);
					DBUtil.updateLVisit(locationHelper
							.getWritableDatabase(),visitBean.getId());
					}			
				}
		}
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		isInfoUploading = false;
	}
	
}

	@Override
	public IBinder onBind(Intent arg0) {
		// TODO Auto-generated method stub
		FileLog.i(TAG, "============> onBind");
		return null;
	}
/*
 * 绑定类
 */
	public class MBinder extends Binder {
		public BaiFangService getService() {
			// 返回Activity所关联的Service对象，这样在Activity里，就可调用Service里的一些公用方法和公用属性
			return BaiFangService.this;
		}
	}

}
