package com.eastelsoft.lbs.photo;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.activity.BaseActivity;
import com.eastelsoft.util.Contant;
import com.eastelsoft.util.GlobalVar;

/**
 * 
 * @author 空山不空
 * Gallery图片页面，通过Intent得到GridView传过来的图片位置，加载图片，再设置适配器
 */
public class GalleryActivity extends BaseActivity {
	public int i_position = 0;
	private DisplayMetrics dm;
	private GlobalVar globalVar;
	private GalleryExt g;
	private String[] imgs;
	private ImageAdapter ia;
	private Button btBack;
	private Button btDel;
	private Intent intent;
	private RelativeLayout layout_top;
	private String op_type = Contant.VIEW;
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); 
		setContentView(R.layout.mygallery);	 
		
		globalVar = (GlobalVar) getApplication();
		layout_top = (RelativeLayout) this.findViewById(R.id.layout_top);
		
		btBack = (Button) this.findViewById(R.id.btBack);
		btDel = (Button) this.findViewById(R.id.btDel);
		btBack.setOnClickListener(new OnBtBackClickListenerImpl());
		btDel.setOnClickListener(new OnBtDelClickListenerImpl());
		
		imgs = globalVar.getImgs();
		
		dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		// 获得Gallery对象	
		g = (GalleryExt) findViewById(R.id.ga);
		//通过Intent得到GridView传过来的图片位置
		intent = getIntent();
		i_position = intent.getIntExtra("position", 0);	 
		op_type = intent.getStringExtra("type");
		if(op_type.equalsIgnoreCase(Contant.VIEW))
			layout_top.setVisibility(View.GONE); // 浏览的时候隐藏删除操作
		g.setVisibility(View.VISIBLE);
		// 添加ImageAdapter给Gallery对象
		ia=new ImageAdapter(this, imgs);		
		g.setAdapter(ia);
	 	g.setSelection(i_position); 	
	 	//加载动画
	 	Animation an= AnimationUtils.loadAnimation(this,R.anim.scale );
        g.setAnimation(an); 
	} 


	private class OnBtBackClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				GalleryActivity.this.finish();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private class OnBtDelClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			int p = ia.getOwnposition();
			//Toast.makeText(GalleryActivity.this, p + "", Toast.LENGTH_LONG).show();
			try {
				intent.putExtra("p", p);
				GalleryActivity.this.setResult(RESULT_OK, intent);
				GalleryActivity.this.finish();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}