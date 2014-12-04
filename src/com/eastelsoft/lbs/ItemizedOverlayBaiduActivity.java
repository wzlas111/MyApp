package com.eastelsoft.lbs;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

/**Mr_Z
 * 演示覆盖物的用法
 */
public class ItemizedOverlayBaiduActivity extends Activity {

	/**
	 * MapView 是地图主控件
	 */
	private MapView mMapView = null;
	private BaiduMap mBaiduMap=null;
	private String lon;
	private String lat;
	private String location;
	private String title;
	private Button btBack;
	private TextView vv_title;
	public static Button button_overlay =null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Intent intent = getIntent();
		lon = intent.getStringExtra("lon");
		lat = intent.getStringExtra("lat");
		location = intent.getStringExtra("location");
		title = intent.getStringExtra("title");
		button_overlay = new Button(ItemizedOverlayBaiduActivity.this);
		button_overlay.setBackgroundResource(R.drawable.popup);
		 button_overlay.setText(location);
		setContentView(R.layout.activity_overlay);
		btBack = (Button) findViewById(R.id.btBack);
		btBack.setOnClickListener(new OnBtBackClickListenerImpl());
		vv_title = (TextView) findViewById(R.id.vv_title);
		if("客户位置".equals(title)){
			vv_title.setText(title);
		}else if("目的地".equals(title)){
			vv_title.setText(title);	
		}
		mMapView = (MapView) findViewById(R.id.bmapView);
		mBaiduMap=mMapView.getMap();
		LatLng p = new LatLng(Double.parseDouble(lat),Double.parseDouble(lon));
		MapStatusUpdate u1 = MapStatusUpdateFactory.zoomTo(18);
		mBaiduMap.setMapStatus(u1);
		MapStatusUpdate u2=MapStatusUpdateFactory.newLatLng(p);
		mBaiduMap.setMapStatus(u2);
		initOverlay();
	}

	private class OnBtBackClickListenerImpl implements OnClickListener {
		public void onClick(View v) {
			try {
				ItemizedOverlayBaiduActivity.this.finish();
			} catch (Exception e) {

			}
		}
	}

	public void initOverlay() {
		LatLng p = new LatLng(Double.parseDouble(lat),Double.parseDouble(lon));
		
		BitmapDescriptor bdA = BitmapDescriptorFactory
				.fromResource(R.drawable.icon_gcoding);
		OverlayOptions item2 =new MarkerOptions().position(p).icon(bdA).title(location);
		mBaiduMap.addOverlay(item2);
		mBaiduMap.setOnMarkerClickListener(new OnMarkerClickListener() {
			
			@Override
			public boolean onMarkerClick(Marker arg0) {
				// TODO Auto-generated method stub
				 
				LatLng p = new LatLng(Double.parseDouble(lat),Double.parseDouble(lon));
				mBaiduMap.showInfoWindow(new InfoWindow(button_overlay,p,null));
				return false;
			}
		});
		
	mBaiduMap.setOnMapClickListener(new OnMapClickListener() {
		
		@Override
		public boolean onMapPoiClick(MapPoi arg0) {
			// TODO Auto-generated method stub
			return false;
		}
		
		@Override
		public void onMapClick(LatLng arg0) {
			// TODO Auto-generated method stub
			mBaiduMap.hideInfoWindow();
		}
	});
	}

	@Override
	protected void onPause() {
		/**
		 * MapView的生命周期与Activity同步，当activity挂起时需调用MapView.onPause()
		 */
		mMapView.onPause();
		super.onPause();
	}

	@Override
	protected void onResume() {
		/**
		 * MapView的生命周期与Activity同步，当activity恢复时需调用MapView.onResume()
		 */
		mMapView.onResume();
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		/**
		 * MapView的生命周期与Activity同步，当activity销毁时需调用MapView.destroy()
		 */
		mMapView.onDestroy();
		super.onDestroy();
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);

	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		super.onRestoreInstanceState(savedInstanceState);
	}


}
