package com.eastelsoft.lbs;

import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.util.Contant;
import com.eastelsoft.util.DeleteFileUtil;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.PopupWindow;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Toast;

public class ClearHistoryActivity extends BaseActivity implements OnClickListener{
private RadioGroup group;
private String clearFlag="";
//protected PopupWindow popupWindowPg;
//private ProgressDialog pd;
private String path=Environment.getExternalStorageDirectory()+"/DCIM/eastelsoft/";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_deletefile);
		initView();
	}
	public void initView(){
		 findViewById(R.id.clear_btBack).setOnClickListener(this);
		 findViewById(R.id.clearFile).setOnClickListener(this);
		 group =(RadioGroup) findViewById(R.id.clear_radioGroup);
		 group.setOnCheckedChangeListener(new radioGroupchange());
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.clear_btBack:
			this.finish();
			break;
		case R.id.clearFile:
			clearhistory();
		}
	}
	
	private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(msg.what==0){
				DeleteFileUtil.delAllFile(path, Contant.ALLTIME);
			}else if(msg.what==1){
				DeleteFileUtil.delAllFile(path, Contant.WEEKTIME);
			}else if(msg.what==2){
				DeleteFileUtil.delAllFile(path, Contant.MOUTHTIME);
			}
			if(DeleteFileUtil.ExistFlag==true){
				new AlertDialog.Builder(ClearHistoryActivity.this).setTitle("提示").setMessage(clearFlag+"删除成功").setPositiveButton("确定", null).show();
			}else if(DeleteFileUtil.ExistFlag==false){
				new AlertDialog.Builder(ClearHistoryActivity.this).setTitle("提示").setMessage("没有"+clearFlag).setPositiveButton("确定", null).show();
			}
			DeleteFileUtil.ExistFlag=false;
		}
	};

	public void clearhistory(){
		if("".equals(clearFlag)){
			Toast.makeText(ClearHistoryActivity.this, "请选择", Toast.LENGTH_LONG).show();
			return;
		}
	  new AlertDialog.Builder(this)
    	.setTitle("删除")
    	.setItems(new String[]{clearFlag}, null)
    	.setNegativeButton("取消", null)
    	.setPositiveButton("确定", new DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which){
					// TODO Auto-generated method stub
					ClearHistoryActivity.this.openPopupWindowPG("");
					btPopGps.setText(getResources().getString(R.string.loading_gengxibanben));					
					Message message =new Message();
					if("一个月前记录".equals(clearFlag)){
						message.what=2;
					}else if("一周前记录".equals(clearFlag)){
						message.what=1;
					}else if("清除所有的记录".equals(clearFlag)){
						message.what=0;
					}
//					pd = ProgressDialog.show(ClearHistoryActivity.this, "删除记录", "正在删除中......");
					handler.sendMessage(message);
				}
			})
    	.show();
	}
	
	private class radioGroupchange implements OnCheckedChangeListener{

		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			int radioButtonId = group.getCheckedRadioButtonId();
		    RadioButton rb = (RadioButton)ClearHistoryActivity.this.findViewById(radioButtonId);
		    clearFlag=(String) rb.getText();
		}
		
	}
}
