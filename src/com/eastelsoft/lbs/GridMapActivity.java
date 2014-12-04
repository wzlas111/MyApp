/**
 * Copyright (c) 2012-8-16 www.eastelsoft.com
 * $ID GridMapActivity.java 下午7:53:15 $
 */
package com.eastelsoft.lbs;

import android.os.Bundle;

import com.mapabc.mapapi.core.GeoPoint;
import com.mapabc.mapapi.map.MapActivity;
import com.mapabc.mapapi.map.MapController;
import com.mapabc.mapapi.map.MapView;

/**
 * 高德地图显示
 * 
 * @author lengcj
 */
public class GridMapActivity extends MapActivity {
	private MapView mMapView;
	private MapController mMapController;
	private GeoPoint point;

	/**
	 * 显示栅格地图，启用内置缩放控件，并用MapController 控制地图的中心点及Zoom 级别
	 */
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_gridmap);
		mMapView = (MapView) findViewById(R.id.mapView);
		mMapView.setBuiltInZoomControls(true); // 设置启用内置的缩放控件
		mMapController = mMapView.getController(); // 得到mMapView
													// 的控制权,可以用它控制和驱动平移和缩放
		point = new GeoPoint((int) (39.982378 * 1E6), (int) (116.304923 * 1E6)); // 用给定的经纬度构造一个GeoPoint，单位是微度(度*
																					// 1E6)
		mMapController.setCenter(point); // 设置地图中心点
		mMapController.setZoom(12); // 设置地图zoom 级别
	}
}
