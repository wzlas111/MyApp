package com.eastelsoft.lbs.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.PoiOverlay;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiDetailSearchOption;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.eastelsoft.lbs.R;

/**
 * 演示poi搜索功能
 */
public class PoiSearchDemo extends FragmentActivity implements
		OnGetPoiSearchResultListener, OnGetSuggestionResultListener {

	private PoiSearch mPoiSearch = null;
	private SuggestionSearch mSuggestionSearch = null;
	private BaiduMap mBaiduMap = null;
	private EditText editText = null;
	/**
	 * 搜索关键字输入窗口
	 */
	private AutoCompleteTextView keyWorldsView = null;
	private ArrayAdapter<String> sugAdapter = null;
	private int load_Index = 0;

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_poisearch);
		// 初始化搜索模块，注册搜索事件监听
		Intent intent = getIntent();
		String city=intent.getExtras().getString("city");
		String lon = intent.getExtras().getString("lon");
		String lat = intent.getExtras().getString("lat");
		MapStatusUpdate u1 = MapStatusUpdateFactory.newLatLng(
				new LatLng(Double.parseDouble(lat),Double.parseDouble(lon)));
		SupportMapFragment map1= (SupportMapFragment) (getSupportFragmentManager()
				.findFragmentById(R.id.map_poiSearch));
		mBaiduMap=map1.getBaiduMap();
		mBaiduMap.setMapStatus(u1);
		editText=(EditText)findViewById(R.id.city);
		editText.setText(city);
		mPoiSearch = PoiSearch.newInstance();
		mPoiSearch.setOnGetPoiSearchResultListener(this);
		mSuggestionSearch = SuggestionSearch.newInstance();
		mSuggestionSearch.setOnGetSuggestionResultListener(this);
		keyWorldsView = (AutoCompleteTextView) findViewById(R.id.searchkey);
		sugAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line);
		keyWorldsView.setAdapter(sugAdapter);

		/**
		 * 当输入关键字变化时，动态更新建议列表
		 */
		keyWorldsView.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable arg0) {

			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1,
					int arg2, int arg3) {

			}

			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2,
					int arg3) {
				if (cs.length() <= 0) {
					return;
				}
				String city = ((EditText) findViewById(R.id.city)).getText()
						.toString();
				/**
				 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
				 */
				mSuggestionSearch
						.requestSuggestion((new SuggestionSearchOption())
								.keyword(cs.toString()).city(city));
			}
		});

	}

	@Override
	protected void onPause() {
		super.onPause();
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onDestroy() {
		mPoiSearch.destroy();
		mSuggestionSearch.destroy();
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

	/**
	 * 影响搜索按钮点击事件
	 * 
	 * @param v
	 */
	public void searchButtonProcess(View v) {
		EditText editCity = (EditText) findViewById(R.id.city);
		EditText editSearchKey = (EditText) findViewById(R.id.searchkey);
		mPoiSearch.searchInCity((new PoiCitySearchOption())
				.city(editCity.getText().toString())
				.keyword(editSearchKey.getText().toString())
				.pageNum(load_Index));
	}

	public void goToNextPage(View v) {
		load_Index++;
		searchButtonProcess(null);
	}

	@Override
	public void onGetPoiResult(PoiResult result) {
		if (result == null
				|| result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
			return;
		}
		if (result.error == SearchResult.ERRORNO.NO_ERROR) {
			mBaiduMap.clear();
			PoiOverlay overlay = new MyPoiOverlay(mBaiduMap);
			mBaiduMap.setOnMarkerClickListener(overlay);
			overlay.setData(result);
			overlay.addToMap();
			overlay.zoomToSpan();
			return;
		}
		if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {

			// 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
			String strInfo = "在";
			for (CityInfo cityInfo : result.getSuggestCityList()) {
				strInfo += cityInfo.city;
				strInfo += ",";
			}
			strInfo += "找到结果";
			Toast.makeText(PoiSearchDemo.this, strInfo, Toast.LENGTH_LONG)
					.show();
		}
	}

	@Override
	public void onGetPoiDetailResult(PoiDetailResult result) {
		if (result.error != SearchResult.ERRORNO.NO_ERROR) {
			Toast.makeText(PoiSearchDemo.this, "抱歉，未找到结果", Toast.LENGTH_SHORT)
					.show();
		} else {
			Toast.makeText(PoiSearchDemo.this, "成功，查看详情页面", Toast.LENGTH_SHORT)
					.show();
		}
	}

	@Override
	public void onGetSuggestionResult(SuggestionResult res) {
		if (res == null || res.getAllSuggestions() == null) {
			return;
		}
		sugAdapter.clear();
		for (SuggestionResult.SuggestionInfo info : res.getAllSuggestions()) {
			if (info.key != null)
				sugAdapter.add(info.key);
		}
		sugAdapter.notifyDataSetChanged();
	}

	private class MyPoiOverlay extends PoiOverlay {

		public MyPoiOverlay(BaiduMap baiduMap) {
			super(baiduMap);
		}

		@Override
		public boolean onPoiClick(int index) {
			super.onPoiClick(index);
			PoiInfo poi = getPoiResult().getAllPoi().get(index);
			if (poi.hasCaterDetails) {
				mPoiSearch.searchPoiDetail((new PoiDetailSearchOption())
						.poiUid(poi.uid));
			}
			return true;
		}
	}
}



//////////////////////////////////////////////////////////////////////
//package com.eastelsoft.lbs.activity;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.Editable;
//import android.text.TextWatcher;
//import android.view.View;
//import android.widget.ArrayAdapter;
//import android.widget.AutoCompleteTextView;
//import android.widget.EditText;
//import android.widget.Toast;
//
//import com.baidu.mapapi.BMapManager;
//import com.baidu.mapapi.map.MapView;
//import com.baidu.mapapi.search.MKAddrInfo;
//import com.baidu.mapapi.search.MKBusLineResult;
//import com.baidu.mapapi.search.MKDrivingRouteResult;
//import com.baidu.mapapi.search.MKPoiInfo;
//import com.baidu.mapapi.search.MKPoiResult;
//import com.baidu.mapapi.search.MKSearch;
//import com.baidu.mapapi.search.MKSearchListener;
//import com.baidu.mapapi.search.MKShareUrlResult;
//import com.baidu.mapapi.search.MKSuggestionInfo;
//import com.baidu.mapapi.search.MKSuggestionResult;
//import com.baidu.mapapi.search.MKTransitRouteResult;
//import com.baidu.mapapi.search.MKWalkingRouteResult;
//import com.baidu.platform.comapi.basestruct.GeoPoint;
//import com.eastelsoft.lbs.R;
//import com.eastelsoft.util.Contant;
//import com.eastelsoft.util.GlobalVar;
//
///**
// * 演示poi搜索功能
// */
//public class PoiSearchDemo extends Activity {
//
//	private MapView mMapView = null;
//	private MKSearch mSearch = null; // 搜索模块，也可去掉地图模块独立使用
//	/**
//	 * 搜索关键字输入窗口
//	 */
//	private AutoCompleteTextView keyWorldsView = null;
//	private ArrayAdapter<String> sugAdapter = null;
//	private int load_Index;
//	String city="";
//
//	@Override
//	public void onCreate(Bundle savedInstanceState) {
//		super.onCreate(savedInstanceState);
//		/**
//		 * 使用地图sdk前需先初始化BMapManager. BMapManager是全局的，可为多个MapView共用，它需要地图模块创建前创建，
//		 * 并在地图地图模块销毁后销毁，只要还有地图模块在使用，BMapManager就不应该销毁
//		 */
//		GlobalVar app = (GlobalVar) this.getApplication();
//		if (app.mBMapManager == null) {
//			app.mBMapManager = new BMapManager(this);
//			/**
//			 * 如果BMapManager没有初始化则初始化BMapManager
//			 */
//			app.mBMapManager.init(Contant.STRKEY,
//					new GlobalVar.MyGeneralListener());
//		}
//		setContentView(R.layout.activity_poisearch);
//		mMapView = (MapView) findViewById(R.id.bmapView);
//		mMapView.getController().enableClick(true);
//		mMapView.getController().setZoom(12);
//		mMapView.getOverlays().clear();
//		GeoPoint p ;
//		Intent intent = getIntent();  
//        String lon = intent.getStringExtra("lon"); 
//        String lat = intent.getStringExtra("lat");
//        city= intent.getStringExtra("city");
//       
//        
//        p  = new GeoPoint((int) (Double.parseDouble(lat) * 1E6), (int) (Double.parseDouble(lon) * 1E6));
//        mMapView.getController().setCenter(p);
//
//		// 初始化搜索模块，注册搜索事件监听
//		mSearch = new MKSearch();
//		mSearch.init(app.mBMapManager, new MKSearchListener() {
//			// 在此处理详情页结果
//			@Override
//			public void onGetPoiDetailSearchResult(int type, int error) {
//				if (error != 0) {
//					Toast.makeText(PoiSearchDemo.this, "抱歉，未找到结果",
//							Toast.LENGTH_SHORT).show();
//				} else {
//					Toast.makeText(PoiSearchDemo.this, "成功，查看详情页面",
//							Toast.LENGTH_SHORT).show();
//				}
//			}
//
//			/**
//			 * 在此处理poi搜索结果
//			 */
//			public void onGetPoiResult(MKPoiResult res, int type, int error) {
//				// 错误号可参考MKEvent中的定义
//				if (error != 0 || res == null) {
//					Toast.makeText(PoiSearchDemo.this, "抱歉，未找到结果",
//							Toast.LENGTH_LONG).show();
//					return;
//				}
//				// 将地图移动到第一个POI中心点
//				if (res.getCurrentNumPois() > 0) {
//					// 将poi结果显示到地图上
//					MyPoiOverlay poiOverlay = new MyPoiOverlay(
//							PoiSearchDemo.this, mMapView, mSearch);
//					poiOverlay.setData(res.getAllPoi());
//					mMapView.getOverlays().clear();
//					mMapView.getOverlays().add(poiOverlay);
//					mMapView.refresh();
//					// 当ePoiType为2（公交线路）或4（地铁线路）时， poi坐标为空
//					for (MKPoiInfo info : res.getAllPoi()) {
//						if (info.pt != null) {
//							mMapView.getController().animateTo(info.pt);
//							break;
//						}
//					}
//				} else if (res.getCityListNum() > 0) {
//					// 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
//					String strInfo = "在";
//					for (int i = 0; i < res.getCityListNum(); i++) {
//						strInfo += res.getCityListInfo(i).city;
//						strInfo += ",";
//					}
//					strInfo += "找到结果";
//					Toast.makeText(PoiSearchDemo.this, strInfo,
//							Toast.LENGTH_LONG).show();
//				}
//			}
//
//			public void onGetDrivingRouteResult(MKDrivingRouteResult res,
//					int error) {
//			}
//
//			public void onGetTransitRouteResult(MKTransitRouteResult res,
//					int error) {
//			}
//
//			public void onGetWalkingRouteResult(MKWalkingRouteResult res,
//					int error) {
//			}
//
//			public void onGetAddrResult(MKAddrInfo res, int error) {
//			}
//
//			public void onGetBusDetailResult(MKBusLineResult result, int iError) {
//			}
//
//			/**
//			 * 更新建议列表
//			 */
//			@Override
//			public void onGetSuggestionResult(MKSuggestionResult res, int arg1) {
//				if (res == null || res.getAllSuggestions() == null) {
//					return;
//				}
//				sugAdapter.clear();
//				for (MKSuggestionInfo info : res.getAllSuggestions()) {
//					if (info.key != null)
//						sugAdapter.add(info.key);
//				}
//				sugAdapter.notifyDataSetChanged();
//
//			}
//
//			@Override
//			public void onGetShareUrlResult(MKShareUrlResult result, int type,
//					int error) {
//				// TODO Auto-generated method stub
//
//			}
//		});
//
//		keyWorldsView = (AutoCompleteTextView) findViewById(R.id.searchkey);
//		sugAdapter = new ArrayAdapter<String>(this,
//				R.layout.auto_complete_new_style);
//		keyWorldsView.setAdapter(sugAdapter);
//
//		/**
//		 * 当输入关键字变化时，动态更新建议列表
//		 */
//		keyWorldsView.addTextChangedListener(new TextWatcher() {
//
//			@Override
//			public void afterTextChanged(Editable arg0) {
//
//			}
//
//			@Override
//			public void beforeTextChanged(CharSequence arg0, int arg1,
//					int arg2, int arg3) {
//
//			}
//
//			@Override
//			public void onTextChanged(CharSequence cs, int arg1, int arg2,
//					int arg3) {
//				if (cs.length() <= 0) {
//					return;
//				}
//				String city = ((EditText) findViewById(R.id.city)).getText()
//						.toString();
//				/**
//				 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
//				 */
//				mSearch.suggestionSearch(cs.toString(), city);
//			}
//		});
//		
//		EditText editCity = (EditText) findViewById(R.id.city);
//		editCity.setText(city);
//
//	}
//
//	@Override
//	protected void onPause() {
//		mMapView.onPause();
//		super.onPause();
//	}
//
//	@Override
//	protected void onResume() {
//		mMapView.onResume();
//		super.onResume();
//	}
//
//	@Override
//	protected void onDestroy() {
//		mMapView.destroy();
//		mSearch.destory();
//		super.onDestroy();
//	}
//
//	@Override
//	protected void onSaveInstanceState(Bundle outState) {
//		super.onSaveInstanceState(outState);
//		mMapView.onSaveInstanceState(outState);
//
//	}
//
//	@Override
//	protected void onRestoreInstanceState(Bundle savedInstanceState) {
//		super.onRestoreInstanceState(savedInstanceState);
//		mMapView.onRestoreInstanceState(savedInstanceState);
//	}
//
//	private void initMapView() {
//		mMapView.setLongClickable(true);
//		mMapView.getController().setZoom(14);
//		mMapView.getController().enableClick(true);
//		mMapView.setBuiltInZoomControls(true);
//	}
//
//	/**
//	 * 影响搜索按钮点击事件
//	 * 
//	 * @param v
//	 */
//	public void searchButtonProcess(View v) {
//		EditText editCity = (EditText) findViewById(R.id.city);
//		EditText editSearchKey = (EditText) findViewById(R.id.searchkey);
//		mSearch.poiSearchInCity(editCity.getText().toString(), editSearchKey
//				.getText().toString());
//	}
//
//	public void goToNextPage(View v) {
//		// 搜索下一组poi
//		int flag = mSearch.goToPoiPage(++load_Index);
//		if (flag != 0) {
//			Toast.makeText(PoiSearchDemo.this, "先搜索开始，然后再搜索下一组数据",
//					Toast.LENGTH_SHORT).show();
//		}
//	}
//}
