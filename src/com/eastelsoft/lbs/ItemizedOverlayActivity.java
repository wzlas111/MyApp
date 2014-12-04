/**
 * Copyright (c) 2012-8-16 www.eastelsoft.com
 * $ID ItemizedOverlayActivity.java 下午9:19:41 $
 */
package com.eastelsoft.lbs;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

import com.eastelsoft.util.FileLog;
import com.mapabc.mapapi.core.GeoPoint;
import com.mapabc.mapapi.core.OverlayItem;
import com.mapabc.mapapi.map.ItemizedOverlay;
import com.mapabc.mapapi.map.MapActivity;
import com.mapabc.mapapi.map.MapController;
import com.mapabc.mapapi.map.MapView;
import com.mapabc.mapapi.map.Projection;

/**
 * 高德地图
 * 
 * @author lengcj
 */
public class ItemizedOverlayActivity extends MapActivity {
	private final static String TAG = "ItemizedOverlayActivity";
	private Button btBack;
	MapView mMapView;
	MapController mMapController;
	private GeoPoint point;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_itemizedoverlay);
		btBack = (Button) findViewById(R.id.btBack);
		btBack.setOnClickListener(new OnBtBackClickListenerImpl());
		
		Intent intent = getIntent();  
        String lon = intent.getStringExtra("lon"); 
        String lat = intent.getStringExtra("lat");
        String location = intent.getStringExtra("location");
		
		mMapView = (MapView) findViewById(R.id.main_mapView);
		mMapView.setBuiltInZoomControls(true); // 设置启用内置的缩放控件
		mMapController = mMapView.getController();
		point = new GeoPoint((int) (Double.parseDouble(lat) * 1E6), (int) (Double.parseDouble(lon) * 1E6));
		mMapController.setCenter(point); // 设置地图中心点
		mMapController.setZoom(12); // 设置地图zoom 级别
		Drawable marker = getResources().getDrawable(R.drawable.da_marker_red); // 得到需要标在地图上的资源
		marker.setBounds(0, 0, marker.getIntrinsicWidth(),
				marker.getIntrinsicHeight()); // 为maker定义位置和边界
		mMapView.getOverlays().add(new OverItemT(marker, this, lon, lat, location)); // 添加ItemizedOverlay实例到mMapView
	}
	
	private class OnBtBackClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				 ItemizedOverlayActivity.this.finish();
			} catch (Exception e) {
				 FileLog.e(TAG, e.toString());
			}
		}
	}
}

/**
 * 某个类型的覆盖物，包含多个类型相同、显示方式相同、处理方式相同的项时，使用此类.
 */
class OverItemT extends ItemizedOverlay<OverlayItem> {
	private List<OverlayItem> GeoList = new ArrayList<OverlayItem>();
	private Drawable marker;
	private Context mContext;

	private double mLat1 = 30.9022; // point1纬度
	private double mLon1 = 116.3922; // point1经度
	private String mLocation1 = ""; // 位置名称

	public OverItemT(Drawable marker, Context context, String... parms) {
		super(boundCenterBottom(marker));
		this.marker = marker;
		this.mContext = context;
		this.mLat1 = Double.parseDouble(parms[1]);
		this.mLon1 = Double.parseDouble(parms[0]);
		this.mLocation1 = parms[2];
		// 用给定的经纬度构造GeoPoint，单位是微度 (度 * 1E6)
		GeoPoint p1 = new GeoPoint((int) (mLat1 * 1E6), (int) (mLon1 * 1E6));
		// 构造OverlayItem的三个参数依次为：item的位置，标题文本，文字片段
		GeoList.add(new OverlayItem(p1, mLocation1, "经度：" + parms[0] + ",纬度：" + parms[1]));
		populate(); // createItem(int)方法构造item。一旦有了数据，在调用其它方法前，首先调用这个方法
	}

	@Override
	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		// Projection接口用于屏幕像素点坐标系统和地球表面经纬度点坐标系统之间的变换
		Projection projection = mapView.getProjection();
		for (int index = size() - 1; index >= 0; index--) { // 遍历GeoList
			OverlayItem overLayItem = getItem(index); // 得到给定索引的item
			String title = overLayItem.getTitle();
			// 把经纬度变换到相对于MapView左上角的屏幕像素坐标
			Point point = projection.toPixels(overLayItem.getPoint(), null);
			// 可在此处添加您的绘制代码
			Paint paintText = new Paint();
			paintText.setColor(Color.BLACK);
			paintText.setTextSize(15);
			canvas.drawText(title, point.x - 30, point.y - 25, paintText); // 绘制文本
		}
		super.draw(canvas, mapView, shadow);
		// 调整一个drawable边界，使得（0，0）是这个drawable底部最后一行中心的一个像素
		boundCenterBottom(marker);
	}

	@Override
	protected OverlayItem createItem(int i) {
		// TODO Auto-generated method stub
		return GeoList.get(i);
	}

	@Override
	public int size() {
		// TODO Auto-generated method stub
		return GeoList.size();
	}

	@Override
	// 处理当点击事件
	protected boolean onTap(int i) {
		setFocus(GeoList.get(i));
		Toast.makeText(this.mContext, GeoList.get(i).getSnippet(),
				Toast.LENGTH_SHORT).show();
		return true;
	}

	@Override
	public boolean onTap(GeoPoint point, MapView mapView) {
		// TODO Auto-generated method stub
		return super.onTap(point, mapView);
	}

}
