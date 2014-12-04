/**
 * Copyright (c) 2013-3-29 www.eastelsoft.com
 * $ID VideoActivity.java 上午8:40:40 $
 */
package com.eastelsoft.lbs;

import java.io.File;
import java.io.IOException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.media.MediaRecorder;
import android.media.MediaRecorder.OnInfoListener;
import android.os.Bundle;
import android.os.Environment;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.util.FileLog;
import com.eastelsoft.util.GlobalVar;

/**
 * 视频录制
 * 
 * @author lengcj
 */
public class VideoActivity extends BaseActivity {    
	public static final String TAG = "VideoActivity";
    private File myRecAudioFile;    
    private SurfaceView mSurfaceView;       
    private SurfaceHolder mSurfaceHolder;     
    private Button buttonStart;    
    private Button buttonStop;    
    private File dir;    
    private MediaRecorder recorder;
    private Camera camera;
    private boolean isStopRecord = true;
        
    @Override    
    public void onCreate(Bundle savedInstanceState) {    
        super.onCreate(savedInstanceState);    
        // 去掉标题栏 
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        // 设置全屏
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,  
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // 设置竖屏显示  
        //setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  
        // 选择支持半透明模式,在有surfaceview的activity中使用。  
        getWindow().setFormat(PixelFormat.TRANSLUCENT);  
        
        setContentView(R.layout.activity_video);    
        mSurfaceView = (SurfaceView) findViewById(R.id.videoView);       
        mSurfaceHolder = mSurfaceView.getHolder();       
        mSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);     
        buttonStart=(Button)findViewById(R.id.start);    
        buttonStop=(Button)findViewById(R.id.stop);    
        File defaultDir = Environment.getExternalStorageDirectory();    
        String path = defaultDir.getAbsolutePath() 
        		+ File.separator + "DCIM" + File.separator 
        		+ "eastelsoft"+File.separator;//创建文件夹存放视频    
        dir = new File(path);    
        if(!dir.exists()){    
            dir.mkdir();    
        }    
        recorder = new MediaRecorder();
         
        buttonStart.setOnClickListener(new OnClickListener() {    
            @Override    
            public void onClick(View v) {   
                buttonStart.setVisibility(View.GONE);
                buttonStop.setVisibility(View.VISIBLE);
                recorder();    
            }    
        });    
            
        buttonStop.setOnClickListener(new OnClickListener() {    
            @Override    
            public void onClick(View v) {    
            	release();
    			isStopRecord = true;
    			Intent it = new Intent(VideoActivity.this, InfoAddActivity.class);
    			setResult(RESULT_OK,it);
    			finish();
            }    
        });    
        
        // 初始化全局变量
        globalVar = (GlobalVar) getApplicationContext();
    }    
        
    @Override
	protected void onStop() {
    	FileLog.i(TAG, "onStop.......................");
    	try {
			if(camera != null) {
			    camera.release();
			    camera = null;
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		super.onStop();
	}   
        
    @Override
	protected void onDestroy() {
    	FileLog.i(TAG, "onDestroy.......................");
    	try {
			if(camera != null) {
			    camera.release();
			    camera = null;
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		super.onDestroy();
	}

	public void recorder() {    
        try {    
        	// 解决预览旋转90问题
            camera = Camera.open();
            Camera.Parameters parameters = camera.getParameters();
            parameters.setRotation(90);
            parameters.set("orientation", "portrait"); 
            camera.setParameters(parameters); 
            camera.setDisplayOrientation(90); // 真正有效的
            
            camera.stopPreview();
            camera.unlock();
            recorder.setCamera(camera);
        	
            myRecAudioFile = File.createTempFile("video", ".3gp",dir);//创建临时文件    
            globalVar.setVideo1(myRecAudioFile.getAbsolutePath());
            recorder.setPreviewDisplay(mSurfaceHolder.getSurface());//预览    
            recorder.setVideoSource(MediaRecorder.VideoSource.CAMERA);//视频源    
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC); //录音源为麦克风    
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);//输出格式为3gp    
            recorder.setVideoEncoder(MediaRecorder.VideoEncoder.H264);//视频编码    H263会导致上传到服务器文件破坏
            recorder.setVideoSize(320, 240);//设置视频录制的分辨率。必须放在设置编码和格式的后面，否则报错  
            // 华为c8812报错
            //recorder.setVideoFrameRate(5);// 设置录制的视频帧率。必须放在设置编码和格式的后面，否则报错  
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);//音频编码    
            //recorder.setMaxDuration(3* 60 * 1000);// 录制时长
            recorder.setMaxFileSize(1024 * 1024 *5); // 设置文件大小 
            recorder.setOutputFile(myRecAudioFile.getAbsolutePath());//保存路径    
            recorder.setPreviewDisplay(mSurfaceHolder.getSurface());
            recorder.setOnInfoListener(new OnInfoListener() {  
                
                @Override  
                public void onInfo(MediaRecorder mr, int what, int extra) {  
                    int a=mr.getMaxAmplitude();  
                    FileLog.i(TAG, "mrrrrrrrrrrrrrrr" + a + ":"  +what + ":" + extra);
                    if(a > 0) {
                    	release();
                    	Intent it = new Intent(VideoActivity.this, InfoAddActivity.class);
                    	setResult(RESULT_OK,it);
                    	finish();
                    }
                }  
            });  
            recorder.prepare();    
            recorder.start();    
            isStopRecord = false;
        } catch (IOException e) {    
            e.printStackTrace();    
        }    
    }    
	
	/**
	 * 释放资源
	 */
	public void release() {
		try {
			if(recorder != null && !isStopRecord) {
				recorder.stop();    
			    recorder.reset();    
			    recorder.release();    
			    recorder=null;    
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
		try {
			if(camera != null) {
			    camera.release();
			    camera = null;
			}
		} catch (IllegalStateException e) {
			e.printStackTrace();
		}
	}
	
	public boolean onKeyDown(int keyCode, KeyEvent event) {  
	    if (keyCode == KeyEvent.KEYCODE_BACK) {  
	    	release();
        	Intent it = new Intent(VideoActivity.this, InfoAddActivity.class);
        	setResult(RESULT_OK,it);
        	finish();
	    }  
	    if (keyCode == KeyEvent.KEYCODE_HOME) {  
	    	release();
	    }
	    return super.onKeyDown(keyCode, event);  
	}  
}    