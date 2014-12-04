package com.eastelsoft.lbs.activity;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.baidu.lbsapi.auth.LBSAuthManagerListener;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.SupportMapFragment;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.overlayutil.DrivingRouteOverlay;
import com.baidu.mapapi.overlayutil.OverlayManager;
import com.baidu.mapapi.overlayutil.TransitRouteOverlay;
import com.baidu.mapapi.overlayutil.WalkingRouteOverlay;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.baidu.mapapi.search.sug.OnGetSuggestionResultListener;
import com.baidu.mapapi.search.sug.SuggestionResult;
import com.baidu.mapapi.search.sug.SuggestionSearch;
import com.baidu.mapapi.search.sug.SuggestionSearchOption;
import com.baidu.navisdk.BNaviPoint;
import com.baidu.navisdk.BaiduNaviManager;
import com.baidu.navisdk.BNaviEngineManager.NaviEngineInitListener;
import com.baidu.navisdk.BaiduNaviManager.OnStartNavigationListener;
import com.baidu.navisdk.comapi.routeplan.RoutePlanParams.NE_RoutePlan_Mode;
//import com.baidu.nplatform.comapi.basestruct.GeoPoint;
import com.eastelsoft.lbs.R;
import com.eastelsoft.lbs.navi.BNavigatorActivity;
import com.google.android.maps.GeoPoint;
//import com.eastelsoft.util.ZoomControlView;
/**
 * @author zhengyuhui
 * 此demo用来展示如何进行驾车、步行、公交路线搜索并在地图使用RouteOverlay、TransitOverlay绘制
 * 同时展示如何进行节点浏览并弹出泡泡
 */
public class RoutePlanDemo extends FragmentActivity implements BaiduMap.OnMapClickListener,
 OnGetRoutePlanResultListener {
//	,OnGetGeoCoderResultListener
	public static final String TAG="RoutePlanDemo";
	boolean mIsEngineInitSuccess = false;
    //浏览路线节点相关
    Button mBtnPre = null;//上一个节点
    Button mBtnNext = null;//下一个节点
    int nodeIndex = -2;//节点索引,供浏览节点时使用
    RouteLine route = null;
    OverlayManager routeOverlay = null;
    boolean useDefaultIcon = false;
    private TextView popupText = null;//泡泡view
    private View viewCache =   null;
    private  LatLng GEO_LatLng =null;
    //地图相关，使用继承MapView的MyRouteMapView目的是重写touch事件实现泡泡处理
    //如果不处理touch事件，则无需继承，直接使用MapView即可
    BaiduMap mBaidumap = null;
    //搜索相关
    RoutePlanSearch mSearch = null;    // 搜索模块，也可去掉地图模块独立使用
    private String city=null;
    private AutoCompleteTextView editSt;
    private AutoCompleteTextView editEn;
    Intent intent = getIntent();    
    private LinearLayout layout;
    private ImageButton search_button;
    private ImageButton road_button;
    private boolean search_flag=true;
    private boolean road_flag=true;
    private ViewPager mViewPager;
    private double start_lon=0;
    private double start_lat=0;
    SupportMapFragment map1=null;
   //起始点信息
    private BNaviPoint mStartPoint =null;
    private BNaviPoint mEndPoint =null;
    //驾车信息VIEW
    private View view1 =null;
    private Button disjunction_skim=null;
    private ArrayAdapter<String> sugAdapter = null;
    private SuggestionSearch mSuggestionSearch = null;
    private SuggestionSearch mSuggestionSearch2 = null;
    TextView taxi_time =null;
	TextView taxi_cost=null;
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.routeplan);
        ////////////////////////////////////////////////////////////
        sugAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_dropdown_item_1line);
        mViewPager = (ViewPager)findViewById(R.id.viewpage);
        LayoutInflater mLi = LayoutInflater.from(this);
        view1 = mLi.inflate(R.layout.routeplan_kid, null);
        //按下导航键开始导航
        Button button = (Button) view1.findViewById(R.id.navigation);
        button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
			        launchNavigatorViaPoints();
			}
		});
        disjunction_skim=(Button) view1.findViewById(R.id.disjunction_skim);
        disjunction_skim.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				 findViewById(R.id.viewpage).setVisibility(View.GONE);
				findViewById(R.id.pre_next).setVisibility(View.VISIBLE);
			}
		});
        final ArrayList<View> views = new ArrayList<View>();
        views.add(view1);
        PagerAdapter mPagerAdapter = new PagerAdapter() {
        
            @Override
            public boolean isViewFromObject(View arg0, Object arg1) {
                return arg0 == arg1;
            }
             
            @Override
            public int getCount() {
                return views.size();
            }
 
            @Override
            public void destroyItem(View container, int position, Object object) {
                ((ViewPager)container).removeView(views.get(position));
            }

            @Override
            public Object instantiateItem(View container, int position) {
                ((ViewPager)container).addView(views.get(position));
                return views.get(position);
            }
        };
         
        mViewPager.setAdapter(mPagerAdapter);
        CharSequence titleLable = "路线规划功能";
        setTitle(titleLable);
        Intent intent =getIntent();
        String lon=(String) intent.getExtras().get("lon");
        String lat=(String) intent.getExtras().get("lat");
        GEO_LatLng = new LatLng(Double.parseDouble(lat),Double.parseDouble(lon));
        //初始化地图
        MapStatusUpdate u1 = MapStatusUpdateFactory.newLatLng(GEO_LatLng);
		map1 = (SupportMapFragment)(getSupportFragmentManager()
				.findFragmentById(R.id.map_routePlan));
		mBaidumap=map1.getBaiduMap();
		mBaidumap.setMapStatus(u1);
        mBtnPre = (Button) findViewById(R.id.pre);
        mBtnNext = (Button) findViewById(R.id.next);
        layout=(LinearLayout) findViewById(R.id.search_dailog);
        search_button=(ImageButton) findViewById(R.id.searchbtn);
        road_button=(ImageButton) findViewById(R.id.traffic_state);

        ///search_buttion搜索点击事件
        search_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(search_flag){
					search_button.setImageResource(R.drawable.searchbtn_down);
					layout.setVisibility(View.GONE);
					search_flag=false;
				}else{
					search_button.setImageResource(R.drawable.searchbtn);
					layout.setVisibility(View.VISIBLE);
					search_flag=true;					
				}
			}
		});
        
        ///road_button路矿显示点击事件
        road_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if(road_flag){
					road_flag=false;
					road_button.setImageResource(R.drawable.main_icon_roadcondition_on);
					mBaidumap.setTrafficEnabled(true);
				}else{
					road_flag=true;
					road_button.setImageResource(R.drawable.main_icon_roadcondition_off);
					mBaidumap.setTrafficEnabled(false);
				}
			}
		});
        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);
        //地图点击事件处理
        mBaidumap.setOnMapClickListener(this);
        mBaidumap.setBuildingsEnabled(false);
        // 初始化搜索模块，注册事件监听
        mSearch = RoutePlanSearch.newInstance();
        mSearch.setOnGetRoutePlanResultListener(this);
        city=intent.getExtras().getString("city");
        mSuggestionSearch = SuggestionSearch.newInstance();
		mSuggestionSearch.setOnGetSuggestionResultListener(new OnGetSuggestionResultListener() {
			
			@Override
			public void onGetSuggestionResult(SuggestionResult res) {
				// TODO Auto-generated method stub
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
		});
        editSt = (AutoCompleteTextView)findViewById(R.id.start);
        editSt.setAdapter(sugAdapter);
        editSt.addTextChangedListener(new TextWatcher(){
			
			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3){
				// TODO Auto-generated method stub
				if (cs.length() <= 0) {
					return;
				}
//				String city = getIntent().getStringExtra("city");
				/**
				 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
				 */
				mSuggestionSearch
						.requestSuggestion((new SuggestionSearchOption())
								.keyword(cs.toString()).city(city));
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
			}
		});
        mSuggestionSearch2 = SuggestionSearch.newInstance();
		mSuggestionSearch2.setOnGetSuggestionResultListener(new OnGetSuggestionResultListener() {
			
			@Override
			public void onGetSuggestionResult(SuggestionResult res) {
				// TODO Auto-generated method stub
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
		});
        editEn = (AutoCompleteTextView) findViewById(R.id.end);
        editEn.setAdapter(sugAdapter);
        editEn.addTextChangedListener(new TextWatcher() {
			
			@Override
			public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				if (cs.length() <= 0) {
					return;
				}
				/**
				 * 使用建议搜索服务获取建议列表，结果在onSuggestionResult()中更新
				 */
				mSuggestionSearch2
						.requestSuggestion((new SuggestionSearchOption())
								.keyword(cs.toString()).city(city));
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
					int arg3) {
				// TODO Auto-generated method stub
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub
			}
		});
        BaiduNaviManager.getInstance().initEngine(this, getSdcardDir(),
                mNaviEngineInitListener, new LBSAuthManagerListener(){
                    @Override
                    public void onAuthResult(int status, String msg){
                        String str = null;
                        if (0 == status) {
                            str = "key校验成功!";
                        } else {
                            str = "key校验失败, " + msg;
                        }
                        Toast.makeText(RoutePlanDemo.this, str,
                                Toast.LENGTH_LONG).show();
                    } 
                });
    	
    }
    
    
    
    private Handler handler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			super.handleMessage(msg);
			if(msg.what==0){
				TextView taxi_time = (TextView) view1.findViewById(R.id.taxi_time);
				TextView taxi_cost = (TextView) view1.findViewById(R.id.taxi_cost);
//				TaxiInfo info = new TaxiInfo();
				RouteLine routeLine=new RouteLine();
				routeLine=(RouteLine) msg.obj;
				int hour = routeLine.getDuration()/3600;
				int minute=routeLine.getDuration()%3600/60;
				int second=routeLine.getDuration()%3600%60;
				String str=null;
				if(hour>0){
					str="总共需花费时间约为："+hour+"小时"+minute+"分钟"+second+"秒";
				}else{ 
					if(minute>0){
						str="总共需花费时间约为："+minute+"分钟"+second+"秒";
					} else {
						if(second>0){
							str="总共需花费时间约为："+second+"秒";
						}else{
							str="正在计算路线中......";
						}
					}
				}
				taxi_time.setText(str);
				taxi_cost.setText("路程约为"+routeLine.getDistance()/1000+"公里");
			}
		}
    	
    };
    /**
     * 发起路线规划搜索示例
     *
     * @param v
     */

    
    public void SearchButtonProcess(View v) throws IOException {
        //重置浏览节点的路线数据
        route = null;
        mBtnPre.setVisibility(View.INVISIBLE);
        mBtnNext.setVisibility(View.INVISIBLE);
        mBaidumap.clear();
        // 处理搜索按钮响应
        PlanNode stNode=null;
        PlanNode enNode=null;
        if(editSt.getText().toString()==null||"".equals(editSt.getText().toString())){
        	Toast.makeText(this, "输入起点不能为空", Toast.LENGTH_LONG).show();
        	return;
        }
        if(editEn.getText().toString()==null||"".equals(editEn.getText().toString())){
        	Toast.makeText(this, "输入终点不能为空", Toast.LENGTH_LONG).show();
        	return;
        }
        if("我的位置".equals(editSt.getText().toString())){
        	start_lat=Double.parseDouble(getIntent().getExtras().getString("lat"));
        	start_lon=Double.parseDouble(getIntent().getExtras().getString("lon"));
        	stNode = PlanNode.withLocation( new LatLng(start_lat,start_lon));        	
            mStartPoint = new BNaviPoint(Double.parseDouble(getIntent().getExtras().getString("lon"))-0.0065f 
            		,Double.parseDouble(getIntent().getExtras().getString("lat"))-0.006f,
            		getIntent().getExtras().getString("desc"), BNaviPoint.CoordinateType.GCJ02); 
            Log.i(TAG, "传入路径规划后的地址是："+getIntent().getExtras().getString("desc")
            		+"，传入后的经纬度是："+getIntent().getExtras().getString("lon")+","+getIntent().getExtras().getString("lat")+
            				"转换后的经纬度是："+start_lat+","+start_lon);
        }else{
        	GeoPoint pt1=null;
        	try {			
				List<Address> addresses = new Geocoder(this).getFromLocationName(editSt.getText().toString(), 1);
			  if (addresses != null && addresses.size() > 0) {  
		             int lat = (int) (addresses.get(0).getLatitude() * 1E6);  
		             int lng = (int) (addresses.get(0).getLongitude() * 1E6);  
		             pt1 = new GeoPoint(lat, lng);  
		         }  
        	Log.i(TAG, "--->lat:"+pt1.getLatitudeE6()+"lon:"+pt1.getLongitudeE6());
//google经纬地理转暂停使用。
//			GeoPoint pt1=LocationUtil.getFromLocationName(editSt.getText().toString());
        	mStartPoint = new BNaviPoint(pt1.getLongitudeE6()/1000000.0,pt1.getLatitudeE6()/1000000.0,
        	editSt.getText().toString(), BNaviPoint.CoordinateType.GCJ02); 
        	} catch (Exception e) {
        		// TODO: handle exception
        		Log.i(TAG, "报异常了");
        		GeoCoder geoCoder = GeoCoder.newInstance();
            	geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
    				
    				@Override
    				public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
    					// TODO Auto-generated method stub
    					
    				}
    				
    				@Override
    				public void onGetGeoCodeResult(GeoCodeResult result) {
    					// TODO Auto-generated method stub
    					LatLng latLng=result.getLocation();
    					mStartPoint = new BNaviPoint(latLng.longitude,latLng.latitude,
    			    	editSt.getText().toString(), BNaviPoint.CoordinateType.GCJ02); 
    				}
    			});
            	
            	geoCoder.geocode(new GeoCodeOption().city(city).address(editSt.getText().toString()));
        		e.printStackTrace();
        	}        
        	stNode = PlanNode.withCityNameAndPlaceName(city, editSt.getText().toString());
        }
        GeoPoint pt2 =null;
        try {
			List<Address> addresses = new Geocoder(this).getFromLocationName(editEn.getText().toString(), 1);
			  if (addresses != null && addresses.size() > 0) {  
		             int lat = (int) (addresses.get(0).getLatitude() * 1E6);  
		             int lng = (int) (addresses.get(0).getLongitude() * 1E6);  
		             Log.i(TAG, "lat:"+lat+"lon:"+lng);
		             pt2 = new GeoPoint(lat, lng);  
		      }  
//    	GeoPoint pt2=LocationUtil.getFromLocationName(editEn.getText().toString());
    	Log.i(TAG, "--->lat:"+pt2.getLatitudeE6()+"lon:"+pt2.getLongitudeE6());
    	mEndPoint = new BNaviPoint(pt2.getLongitudeE6()/1000000.0,pt2.getLatitudeE6()/1000000.0,
    	editEn.getText().toString(), BNaviPoint.CoordinateType.GCJ02); 
        } catch (Exception e) {
        	// TODO: handle exception
        	Log.i(TAG, "报异常了");
        	/*
        	 * 异常发生后采取另一种取地理标示转换方法
        	 */
        	GeoCoder geoCoder = GeoCoder.newInstance();
        	geoCoder.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
				
				@Override
				public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public void onGetGeoCodeResult(GeoCodeResult result) {
					// TODO Auto-generated method stub
					LatLng latLng=result.getLocation();
					Log.i(TAG, "异常后所报出的经纬度是："+latLng.latitude+","+latLng.longitude);
					mEndPoint = new BNaviPoint(latLng.longitude,latLng.latitude,editEn.getText().toString(), BNaviPoint.CoordinateType.GCJ02); 
				}
			});
        	
        	geoCoder.geocode(new GeoCodeOption().city(city).address(editEn.getText().toString()));
        }
        enNode = PlanNode.withCityNameAndPlaceName(city, editEn.getText().toString());
        
        //设置起终点信息，对于tranist search 来说，城市名无意义
        // 实际使用中请对起点终点城市进行正确的设定
        if (v.getId() == R.id.drive){
            if(taxi_time!=null&&taxi_cost!=null){
    	        taxi_time.setText("正在计算路线中......");
    			taxi_cost.setText("");
            }
            findViewById(R.id.viewpage).setVisibility(View.VISIBLE);
            findViewById(R.id.pre_next).setVisibility(View.GONE);
            mSearch.drivingSearch((new DrivingRoutePlanOption())
                    .from(stNode)
                    .to(enNode));
        } else if (v.getId() == R.id.transit) {
            mSearch.transitSearch((new TransitRoutePlanOption())
                    .from(stNode)
                    .city(city)
                    .to(enNode));
            findViewById(R.id.viewpage).setVisibility(View.GONE);
            findViewById(R.id.pre_next).setVisibility(View.VISIBLE);
        } else if (v.getId() == R.id.walk) {
            mSearch.walkingSearch((new WalkingRoutePlanOption())
                    .from(stNode)
                    .to(enNode));
            findViewById(R.id.viewpage).setVisibility(View.GONE);
            findViewById(R.id.pre_next).setVisibility(View.VISIBLE);
        }
    }
    
    
    /**
     * 节点浏览示例
     *
     * @param v
     */
    public void nodeClick(View v) {
        if (nodeIndex < -1 || route == null ||
                route.getAllStep() == null
                || nodeIndex > route.getAllStep().size()) {
            return;
        }
        //设置节点索引
        if (v.getId() == R.id.next && nodeIndex < route.getAllStep().size() - 1) {
            nodeIndex++;
        } else if (v.getId() == R.id.pre && nodeIndex > 1) {
            nodeIndex--;
        }
        if (nodeIndex < 0 || nodeIndex >= route.getAllStep().size()) {
            return;
        }

        //获取节结果信息
        LatLng nodeLocation = null;
        String nodeTitle = null;
        Object step = route.getAllStep().get(nodeIndex);
        if (step instanceof DrivingRouteLine.DrivingStep) {
            nodeLocation = ((DrivingRouteLine.DrivingStep) step).getEntrace().getLocation();
            nodeTitle = ((DrivingRouteLine.DrivingStep) step).getInstructions();
        } else if (step instanceof WalkingRouteLine.WalkingStep) {
            nodeLocation = ((WalkingRouteLine.WalkingStep) step).getEntrace().getLocation();
            nodeTitle = ((WalkingRouteLine.WalkingStep) step).getInstructions();
        } else if (step instanceof TransitRouteLine.TransitStep) {
            nodeLocation = ((TransitRouteLine.TransitStep) step).getEntrace().getLocation();
            nodeTitle = ((TransitRouteLine.TransitStep) step).getInstructions();
        }
        if (nodeLocation == null || nodeTitle == null) {
            return;
        }
        //移动节点至中心
        mBaidumap.setMapStatus(MapStatusUpdateFactory.newLatLng(nodeLocation));
        // show popup
        viewCache = getLayoutInflater().inflate(R.layout.custom_text_view, null);
        popupText = (TextView) viewCache.findViewById(R.id.textcache);
        popupText.setBackgroundResource(R.drawable.popup);
        popupText.setText(nodeTitle);
        mBaidumap.showInfoWindow(new InfoWindow(popupText, nodeLocation, null));
    }

    /**
     * 切换路线图标，刷新地图使其生效
     * 注意： 起终点图标使用中心对齐.
     */
    public void changeRouteIcon(View v){
        if (routeOverlay == null) {
            return;
        }
        if (useDefaultIcon) {
            ((Button) v).setText("自定义起终点图标");
            Toast.makeText(this,
                    "将使用系统起终点图标",
                    Toast.LENGTH_SHORT).show();
        }else {
            ((Button) v).setText("系统起终点图标");
            Toast.makeText(this,
                    "将使用自定义起终点图标",
                    Toast.LENGTH_SHORT).show();
        }
        useDefaultIcon = !useDefaultIcon;
        routeOverlay.removeFromMap();
        routeOverlay.addToMap();
    }


    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onGetWalkingRouteResult(WalkingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(RoutePlanDemo.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            //result.getSuggestAddrInfo()
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            mBtnPre.setVisibility(View.VISIBLE);
            mBtnNext.setVisibility(View.VISIBLE);
            route = result.getRouteLines().get(0);
            WalkingRouteOverlay overlay = new MyWalkingRouteOverlay(mBaidumap);
            mBaidumap.setOnMarkerClickListener(overlay);
            routeOverlay = overlay;
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
    }

    @Override
    public void onGetTransitRouteResult(TransitRouteResult result) {

        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(RoutePlanDemo.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            mBtnPre.setVisibility(View.VISIBLE);
            mBtnNext.setVisibility(View.VISIBLE);
            route = result.getRouteLines().get(0);
            TransitRouteOverlay overlay = new MyTransitRouteOverlay(mBaidumap);
            mBaidumap.setOnMarkerClickListener(overlay);
            routeOverlay = overlay;
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
        }
    }

    @Override
    public void onGetDrivingRouteResult(DrivingRouteResult result) {
        if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
            Toast.makeText(RoutePlanDemo.this, "抱歉，未找到结果", Toast.LENGTH_SHORT).show();
        }
        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_ROURE_ADDR) {
            //起终点或途经点地址有岐义，通过以下接口获取建议查询信息
            return;
        }
        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            nodeIndex = -1;
            mBtnPre.setVisibility(View.VISIBLE);
            mBtnNext.setVisibility(View.VISIBLE);
            route = result.getRouteLines().get(0);
            DrivingRouteOverlay overlay = new DrivingRouteOverlay(mBaidumap);
            routeOverlay = overlay;
            mBaidumap.setOnMarkerClickListener(overlay);
            overlay.setData(result.getRouteLines().get(0));
            overlay.addToMap();
            overlay.zoomToSpan();
            
            
            Message message = new Message();
            message.what=0;
            Log.i(TAG, "距离:"+route.getDistance()+",时间:"+route.getDuration()+",标题:"+route.getTitle());
            message.obj=route;
            handler.sendMessage(message);
        }
    }

//    //定制RouteOverly
//    private class MyDrivingRouteOverlay extends DrivingRouteOverlay {
//
//        public MyDrivingRouteOverlay(BaiduMap baiduMap) {
//            super(baiduMap);
//        }
//
//        @Override
//        public BitmapDescriptor getStartMarker() {
//            if (useDefaultIcon) {
//                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
//            }
//            return null;
//        }
//
//        @Override
//        public BitmapDescriptor getTerminalMarker() {
//            if (useDefaultIcon) {
//                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
//            }
//            return null;
//        }
//    }

    private class MyWalkingRouteOverlay extends WalkingRouteOverlay {

        public MyWalkingRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    private class MyTransitRouteOverlay extends TransitRouteOverlay {

        public MyTransitRouteOverlay(BaiduMap baiduMap) {
            super(baiduMap);
        }

        @Override
        public BitmapDescriptor getStartMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_st);
            }
            return null;
        }

        @Override
        public BitmapDescriptor getTerminalMarker() {
            if (useDefaultIcon) {
                return BitmapDescriptorFactory.fromResource(R.drawable.icon_en);
            }
            return null;
        }
    }

    @Override
    public void onMapClick(LatLng point) {
        mBaidumap.hideInfoWindow();
    }

    @Override
    public boolean onMapPoiClick(MapPoi poi) {
    	return false;
    }

    @Override
    protected void onPause() {
    	Log.i(TAG, "--->onPause");
    	map1.onPause();
        super.onPause();
        
    }

    @Override
    protected void onResume() {
    	Log.i(TAG, "--->onResume");
    	map1.onResume();
        super.onResume();
    }

    @Override
    protected void onDestroy() {
    	Log.i(TAG, "--->onDestroy");
    	map1.onDestroy();
        mSearch.destroy();
        super.onDestroy();
    }
	/********start*************/
	private NaviEngineInitListener mNaviEngineInitListener = new NaviEngineInitListener() {
		public void engineInitSuccess() {
			mIsEngineInitSuccess = true;
		}

		public void engineInitStart() {
		}

		public void engineInitFail() {
		}
	};
	private String getSdcardDir() {
		if (Environment.getExternalStorageState().equalsIgnoreCase(
				Environment.MEDIA_MOUNTED)) {
			return Environment.getExternalStorageDirectory().toString();
		}
		return null;
	}
	/*
	 * **一键导航
	 */
    private void launchNavigatorViaPoints(){
        //这里给出一个起终点示例，实际应用中可以通过POI检索、外部POI来源等方式获取起终点坐标行");
        List<BNaviPoint> points = new ArrayList<BNaviPoint>();
        points.add(mStartPoint);
        points.add(mEndPoint);
   	 	Log.i("RoutePlanDemo","结束---"+mEndPoint.getLatitude()+","+mEndPoint.getLongitude()+"--)))"+mEndPoint.getName()+"\n"+
   	 		"开始---"+mStartPoint.getLatitude()+","+mStartPoint.getLongitude()+"--)))"+mStartPoint.getName());
        BaiduNaviManager.getInstance().launchNavigator(this,
                points,                                          //路线点列表
                NE_RoutePlan_Mode.ROUTE_PLAN_MOD_MIN_TIME,       //算路方式
                true,                                            //真实导航
                BaiduNaviManager.STRATEGY_FORCE_ONLINE_PRIORITY, //在离线策略
                new OnStartNavigationListener() {                //跳转监听
                    @Override
                    public void onJumpToNavigator(Bundle configParams) {
                        Intent intent = new Intent(RoutePlanDemo.this, BNavigatorActivity.class);
                        intent.putExtras(configParams);
                        startActivity(intent);
                        Log.i(TAG, "---->finish");
                    }
                    
                    @Override
                    public void onJumpToDownloader() {
                    }
                });
    }
}
