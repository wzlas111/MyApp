package com.eastelsoft.lbs;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.eastelsoft.lbs.entity.SetInfo;
import com.eastelsoft.util.Contant;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.FileUtil;
import com.eastelsoft.util.IUtil;
import com.eastelsoft.util.http.AndroidHttpClient;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
/*
 * @author Mrz
 * 日志上传
 */

public class DailyActivity extends Activity implements OnClickListener{
	public final static String TAG="DailyActivity";
	private ListView listView;
	private LayoutInflater mInflater; 
	public  Map<Integer, CheckBoxHodler> isSelected=new HashMap<Integer, CheckBoxHodler>();;
	private boolean Flag_choseAll=true;
	private SharedPreferences sp;
	private ProgressDialog dialog; 
//	private Button button = null;
	/*
	 * (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 * Activty 初始化
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_daily);
		initView();
		
	}
	public void initView(){
		 findViewById(R.id.daily_btBack).setOnClickListener(this);
		 findViewById(R.id.daily_upload).setOnClickListener(this);
		 listView=(ListView) findViewById(R.id.daily_listview);
		 mInflater = LayoutInflater.from(this);
		 int i=0;
		 for(String s:FileUtil.findLogFile()){
			 isSelected.put(i, new CheckBoxHodler(s,false)); 
			 i++;
		 }
		 listView.setAdapter(new listViewAdapter());
		 listView.setOnItemClickListener(new onItemClickListener());
	}

	/*
	 * (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 * 点击触发事件
	 */
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.daily_btBack:
			this.finish();
			break;
			
		case R.id.daily_upload:
			final List<String> uploadFileName=new ArrayList<String>();
            for(int i=0;i<listView.getCount();i++){    
                if(isSelected.get(i).isFlag()){
                	uploadFileName.add(isSelected.get(i).getText());
                }
            }
           if(uploadFileName.size()==0){
        	   Toast.makeText(DailyActivity.this, "请选择", Toast.LENGTH_LONG).show();
        	   return;
           }
            new AlertDialog.Builder(this)
            	.setTitle("上传列表")
            	.setItems(uploadFileName.toArray(new String[]{}), null)
            	.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
//						FileUtil.deleteDailyFile();
					}
				})
            	.setPositiveButton("确定", new DialogInterface.OnClickListener() {
					
					@Override
					public void onClick(DialogInterface dialog, int which) {
						// TODO Auto-generated method stub
						Message message = new Message();
						message.what=3;
						handler.sendMessage(message);
						for(int i=0;i<uploadFileName.size();i++){
								new Thread(new uploadThread(uploadFileName.get(i))).start();
						}
						
					}
				})
            	.show();
			break;
		}
	}
	
	
//	Thread thread = new Thread(new Runnable() {
//		
//		@Override
//		public void run() {
//			// TODO Auto-generated method stub
//			sendDaily(uploadFileName.get(i));
//		}
//	});
	Handler handler = new Handler(){
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case 1:
				dialog.dismiss();
				Toast.makeText(DailyActivity.this, msg.obj+"日志上传成功", Toast.LENGTH_LONG).show();
				break;
			case 2:
				dialog.dismiss();
				Toast.makeText(DailyActivity.this, msg.obj+"日志上传失败", Toast.LENGTH_LONG).show();
				break;
			case 3:
				progress();
			default:
				
				break;
			}
		};
	};
	
	
	class uploadThread implements Runnable{
		public String str;
		public uploadThread(String str){
			this.str=str;
		}
		@Override
		public synchronized void run() {
			// TODO Auto-generated method stub
			try {
				Message message = new Message();
				if(sendDaily(str)){
					message.what=1;	
				}else{
					message.what=2;
				}
				message.obj=str;
				handler.sendMessage(message);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	/*
	 * 日志上传
	 */
	public boolean sendDaily(String filename) throws IOException, JSONException{
		String resultcode = "";
		sp = getSharedPreferences("userdata", 0);
		SetInfo set = IUtil.initSetInfo(sp);
		String url = set.getHttpip() + Contant.ACTION;
		Map<String, String> map = new HashMap<String, String>();
		map.put("reqCode", Contant.DAILY_UPLOAD_SEND);
		map.put("gps_id", set.getDevice_id());
		Map<String, File> fileMap = new HashMap<String, File>();
		if(filename!=null&&!"".equals(filename)){
			File file = new File(Environment.getExternalStorageDirectory().getCanonicalPath()+"/eastelsoft/"+filename);
			if(file!=null&&file.exists()){
				fileMap.put("dailyFile", file);
			}
		}
		String jsonStr = AndroidHttpClient.getContent(url, map,
				fileMap, "file1");
		jsonStr = IUtil.chkJsonStr(jsonStr);
		JSONArray array = new JSONArray(jsonStr);
		if (array.length() > 0) {
			JSONObject obj = array.getJSONObject(0);
			resultcode = obj.getString("resultcode");
		}
		if("1".equals(resultcode)){
			FileLog.i(TAG, filename+"日志上传成功--》resultcode"+resultcode);
			return true;
		}else{
			Log.i(TAG, filename+"daily upload failed-->resultcode"+resultcode);
			return false;
		}
	}
	
	
	
	/*
	 *日志列表选择
	 */
	private class onItemClickListener implements OnItemClickListener{

		@Override
		public void onItemClick(AdapterView<?> arg0, View view, int position,
				long arg3) {
			// TODO Auto-generated method stub
			CheckBox vHollder = (CheckBox) view.getTag(); 
			vHollder.toggle();
			isSelected.put(position, new CheckBoxHodler((String) vHollder.getText(), vHollder.isChecked()));
		}
	}
	/*
	 * ListView适配器
	 */
	
	private class listViewAdapter extends BaseAdapter{
		
		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return isSelected.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent){
			// TODO Auto-generated method stub
			 CheckBox holder = null;
				if(convertView==null){
					convertView = mInflater.inflate(R.layout.daily_adapter, null);
					holder=(CheckBox) convertView.findViewById(R.id.daily_checkBox1);
					convertView.setTag(holder);
				}else{
					holder = (CheckBox) convertView.getTag();
				}
				holder.setText(isSelected.get(position).getText());
				holder.setChecked(isSelected.get(position).isFlag());
				return convertView;
		}
	}
	/*
	 *实体类
	 */
	class CheckBoxHodler{
		public String text;
		public boolean flag;
		public CheckBoxHodler(String text, boolean flag) {
			super();
			this.text = text;
			this.flag = flag;
		}
		public String getText() {
			return text;
		}
		public void setText(String text) {
			this.text = text;
		}
		public boolean isFlag() {
			return flag;
		}
		public void setFlag(boolean flag) {
			this.flag = flag;
		}
		
	}
	
	public void progress(){
	dialog=new ProgressDialog(this);
	//设置进度条风格，风格为圆形，旋转的 
	dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER); 
	//设置ProgressDialog 标题 
	dialog.setTitle("上传日志"); 
	//设置ProgressDialog 提示信息 
	dialog.setMessage("正在上传中，请稍后"); 
	//设置ProgressDialog 标题图标 
	dialog.setIcon(android.R.drawable.ic_dialog_alert); 
	//设置ProgressDialog的最大进度 
//	dialog.setMax(100); 
	//设置ProgressDialog 的一个Button 
//	dialog.setButton("取消", new ProgressDialog.OnClickListener(){ 
//	    @Override 
//	    public void onClick(DialogInterface dialog, int which) { 
//	         
//	    } 
//	}); 
	//设置ProgressDialog 是否可以按退回按键取消 
	dialog.setCancelable(true); 
	//显示 
	dialog.show();
	}
}
