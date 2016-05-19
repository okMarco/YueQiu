package com.hochan.yueqiu;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.MenuBuilder;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextPaint;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdate;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.LocationSource;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.CircleOptions;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.Marker;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.core.PoiItem;
import com.amap.api.services.core.SuggestionCity;
import com.amap.api.services.poisearch.PoiResult;
import com.amap.api.services.poisearch.PoiSearch;
import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVGeoPoint;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.GetCallback;
import com.avos.avoscloud.SaveCallback;
import com.hochan.customview.CustomAppBarLayout;
import com.hochan.fragment.DrawerFragment;
import com.wilddog.client.AuthData;
import com.wilddog.client.Wilddog;
import com.wilddog.client.WilddogError;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements LocationSource,
        AMapLocationListener , View.OnClickListener, PoiSearch.OnPoiSearchListener,
        AMap.OnMarkerClickListener, AMap.InfoWindowAdapter {

    private MapView mapView;
    private AMap aMap;
    private UiSettings mUiSettings;
    private OnLocationChangedListener mListener;
    private AMapLocationClient mlocationClient;
    private AMapLocationClientOption mLocationOption;

    //view
    private ActionBarDrawerToggle mDrawerToggle;
    private DrawerLayout mDrawerLayout;
    private CustomAppBarLayout mAppBarLayout;
    private ImageButton imgbtnBack;
    private FloatingActionButton mFab;

    private PoiResult poiResult; // poi返回的结果
    private int currentPage = 0;// 当前页面，从0开始计数
    private PoiSearch.Query query;// Poi查询条件类
    private PoiSearch poiSearch;
    private LatLonPoint lp = null;
    private String keyWord = "田径场|足球场";
    private String city = "";
    private List<PoiItem> poiItems;// poi数据
    private Marker detailMarker;
    private Marker mlastMarker;
    private MyPoiOverlay poiOverlay;// poi图层


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @SuppressWarnings("deprecation")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Intent intent = new Intent(this, LoginActivity.class);
        //startActivity(intent);
        //getWindow().setStatusBarColor(getResources().getColor(R.color.colorPrimaryDark));
        setContentView(R.layout.activity_main);
        mAppBarLayout = (CustomAppBarLayout) findViewById(R.id.appBarLayout);
        imgbtnBack = (ImageButton) findViewById(R.id.back);
        imgbtnBack.setOnClickListener(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitleTextColor(getResources().getColor(R.color.colorTitleText));
        toolbar.setTitle(R.string.title_text_main);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mAppBarLayout.showSearchLayout();
                Toast.makeText(getApplicationContext(), ""+item.getItemId(), Toast.LENGTH_LONG).show();
                return true;
            }
        });

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, PostActivity.class);
                startActivity(intent);
            }
        });

        getSupportFragmentManager().beginTransaction().add(R.id.rl_drawerFragment, DrawerFragment.newInstance()).commit();
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, toolbar, R.string.open, R.string.close);
        mDrawerToggle.setHomeAsUpIndicator(R.drawable.ic_indicator);
        mDrawerToggle.syncState();

        mapView = (MapView) findViewById(R.id.map);
        mapView.setVisibility(View.INVISIBLE);
        mapView.onCreate(savedInstanceState);

        init();

    }


    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
            mUiSettings = aMap.getUiSettings();
            aMap.setLocationSource(this);
            mUiSettings.setMyLocationButtonEnabled(true);
            aMap.setMyLocationEnabled(true);
            mUiSettings.setZoomControlsEnabled(false);
            aMap.moveCamera(new CameraUpdateFactory().zoomTo(15));
            aMap.setOnMarkerClickListener(this);
            aMap.setInfoWindowAdapter(this);
        }

    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }


    /**
     * 激活定位
     */
    @Override
    public void activate(OnLocationChangedListener listener) {
        mListener = listener;
        if (mlocationClient == null) {
            mlocationClient = new AMapLocationClient(this);
            mLocationOption = new AMapLocationClientOption();
            //设置定位监听
            mlocationClient.setLocationListener(this);
            //设置为高精度定位模式
            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
            //设置定位参数
            mlocationClient.setLocationOption(mLocationOption);
            // 此方法为每隔固定时间会发起一次定位请求，为了减少电量消耗或网络流量消耗，
            // 注意设置合适的定位时间的间隔（最小间隔支持为2000ms），并且在合适时间调用stopLocation()方法来取消定位请求
            // 在定位结束后，在合适的生命周期调用onDestroy()方法
            // 在单次定位情况下，定位无论成功与否，都无需调用stopLocation()方法移除请求，定位sdk内部会移除
            mlocationClient.startLocation();
        }
    }

    /**
     * 停止定位
     */
    @Override
    public void deactivate() {
        mListener = null;
        if (mlocationClient != null) {
            mlocationClient.stopLocation();
            mlocationClient.onDestroy();
        }
        mlocationClient = null;
    }

    /**
     * 定位成功后回调函数
     */
    @Override
    public void onLocationChanged(AMapLocation amapLocation) {
        if (mListener != null && amapLocation != null) {
            if (amapLocation != null
                    && amapLocation.getErrorCode() == 0) {
                mListener.onLocationChanged(amapLocation);// 显示系统小蓝点
                mapView.setVisibility(View.VISIBLE);
                city = amapLocation.getCity();
                lp = new LatLonPoint(amapLocation.getLatitude(), amapLocation.getLongitude());
                deactivate();
                doSearchQuery();
            } else {
                mapView.setVisibility(View.VISIBLE);
                String errText = "定位失败," + amapLocation.getErrorCode()+ ": " + amapLocation.getErrorInfo();
                Toast.makeText(getApplicationContext(), errText, Toast.LENGTH_LONG).show();
                Log.e("AmapErr", errText);
            }
        }
    }

    /**
     * 开始进行poi搜索
     */
    protected void doSearchQuery() {
        currentPage = 0;
        query = new PoiSearch.Query(keyWord, "", city);// 第一个参数表示搜索字符串，第二个参数表示poi搜索类型，第三个参数表示poi搜索区域（空字符串代表全国）
        query.setPageSize(20);// 设置每页最多返回多少条poiitem
        query.setPageNum(currentPage);// 设置查第一页

        if (lp != null) {
            poiSearch = new PoiSearch(this, query);
            poiSearch.setOnPoiSearchListener(this);
            poiSearch.setBound(new PoiSearch.SearchBound(lp, 5000, true));//
            // 设置搜索区域为以lp点为圆心，其周围5000米范围
            poiSearch.searchPOIAsyn();// 异步搜索
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.back:
                mAppBarLayout.hideSearchLayout();
        }
    }

    @Override
    public void onPoiSearched(PoiResult result, int rcode) {
        if (rcode == 1000) {
            if (result != null && result.getQuery() != null) {// 搜索poi的结果
                if (result.getQuery().equals(query)) {// 是否是同一条
                    poiResult = result;
                    poiItems = poiResult.getPois();// 取得第一页的poiitem数据，页数从数字0开始
                    List<SuggestionCity> suggestionCities = poiResult
                            .getSearchSuggestionCitys();// 当搜索不到poiitem数据时，会返回含有搜索关键字的城市信息
                    if (poiItems != null && poiItems.size() > 0) {
                        //清理之前搜索结果的marker
                        if (poiOverlay !=null) {
                            poiOverlay.removeFromMap();
                        }
                        aMap.clear();
                        poiOverlay = new MyPoiOverlay(aMap, poiItems);
                        poiOverlay.addToMap();
                        poiOverlay.zoomToSpan();

                        aMap.addMarker(new MarkerOptions()
                                .anchor(0.5f, 0.5f)
                                .icon(BitmapDescriptorFactory
                                        .fromBitmap(BitmapFactory.decodeResource(
                                                getResources(), R.drawable.point4)))
                                .position(new LatLng(lp.getLatitude(), lp.getLongitude())));

//                        aMap.addCircle(new CircleOptions()
//                                .center(new LatLng(lp.getLatitude(),
//                                        lp.getLongitude())).radius(5000)
//                                .strokeColor(Color.TRANSPARENT)
//                                .fillColor(Color.argb(0, 1, 1, 1))
//                                .strokeWidth(2));
                    } else if (suggestionCities != null
                            && suggestionCities.size() > 0) {
                        //showSuggestCity(suggestionCities);
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.no_result, Toast.LENGTH_LONG).show();
                    }
                }
            } else {
                Toast.makeText(getApplicationContext(), R.string.no_result, Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onPoiItemSearched(PoiItem poiItem, int i) {

    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        //Toast.makeText(getApplicationContext(), ""+marker.getPosition(), Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
        intent.setClass(MainActivity.this, SoccerFieldActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString(MyApplication.FIELD_ID, poiOverlay.mFieldID.get(poiOverlay.getPoiIndex(marker)));
        bundle.putString(SoccerFieldActivity.NAME, marker.getTitle());
        bundle.putString(SoccerFieldActivity.LOCATION, marker.getSnippet());
        bundle.putParcelable(SoccerFieldActivity.STARTPOINT, new LatLonPoint(lp.getLatitude(), lp.getLongitude()));
        bundle.putParcelable(SoccerFieldActivity.ENDPOINT, new LatLonPoint(marker.getPosition().latitude, marker.getPosition().longitude));
        intent.putExtras(bundle);
        startActivity(intent);
        return true;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        View view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.custom_info_layout, null);
        TextView content = (TextView) view.findViewById(R.id.tv_content);
        //content.setText("约球：0");
        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {

        return null;
    }

    private class MyPoiOverlay {
        private AMap mamap;
        private List<PoiItem> mPois;
        private ArrayList<Marker> mPoiMarks = new ArrayList<Marker>();
        private ArrayList<String> mFieldID = new ArrayList<>();

        public MyPoiOverlay(AMap amap ,List<PoiItem> pois) {
            mamap = amap;
            mPois = pois;
        }

        /**
         * 添加Marker到地图中。
         * @since V2.1.0
         */
        public void addToMap() {
            for (int i = 0; i < mPois.size(); i++) {

                final int index = i;

                final AVGeoPoint avGeoPoint = new AVGeoPoint();
                avGeoPoint.setLatitude(mPois.get(index).getLatLonPoint().getLatitude());
                avGeoPoint.setLongitude(mPois.get(index).getLatLonPoint().getLongitude());

                AVQuery<AVObject> avQuery = new AVQuery<>(MyApplication.OBJECT_FIELD);
                avQuery.whereEqualTo(MyApplication.FIELD_GEO, avGeoPoint);
                avQuery.getFirstInBackground(new GetCallback<AVObject>() {
                    @Override
                    public void done(AVObject ravObject, AVException e) {
                        if(e != null)
                            e.printStackTrace();
                        if(ravObject != null) {
                            System.out.println("获取成功：" + ravObject.getObjectId()
                                    + " " + ravObject.getString(MyApplication.FIELD_NAME)
                                    + " " + ravObject.getString(MyApplication.FIELD_ADDRESS));
                            int statusCount = ravObject.getInt(MyApplication.FIELD_STATUS_COUNT);
                            String fieldID = ravObject.getObjectId();
                            Marker marker = mamap.addMarker(getMarkerOptions(index, statusCount));
                            PoiItem item = mPois.get(index);
                            marker.setObject(item);
                            //marker.showInfoWindow();
                            mPoiMarks.add(marker);
                            mFieldID.add(fieldID);
                        }else{
                            System.out.println("球场不存在--->开始新建");
                            final AVObject avObject = new AVObject(MyApplication.OBJECT_FIELD);
                            avObject.put(MyApplication.FIELD_NAME, mPois.get(index).getTitle());
                            avObject.put(MyApplication.FIELD_ADDRESS, mPois.get(index).getProvinceName()+mPois.get(index).getCityName()
                                    +mPois.get(index).getAdName()+mPois.get(index).getSnippet());
                            AVGeoPoint avGeoPoint = new AVGeoPoint();
                            avGeoPoint.setLatitude(mPois.get(index).getLatLonPoint().getLatitude());
                            avGeoPoint.setLongitude(mPois.get(index).getLatLonPoint().getLongitude());
                            avObject.put(MyApplication.FIELD_GEO, avGeoPoint);
                            avObject.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(AVException e) {
                                    if(e == null){
                                        Marker marker = mamap.addMarker(getMarkerOptions(index, 0));
                                        PoiItem item = mPois.get(index);
                                        marker.setObject(item);
                                        //marker.showInfoWindow();
                                        mPoiMarks.add(marker);
                                        mFieldID.add(avObject.getObjectId());
                                    }
                                }
                            });
                        }
                    }
                });
            }
        }

        /**
         * 去掉PoiOverlay上所有的Marker。
         *
         * @since V2.1.0
         */
        public void removeFromMap() {
            for (Marker mark : mPoiMarks) {
                mark.remove();
            }
        }

        /**
         * 移动镜头到当前的视角。
         * @since V2.1.0
         */
        public void zoomToSpan() {
            if (mPois != null && mPois.size() > 0) {
                if (mamap == null)
                    return;
                LatLngBounds bounds = getLatLngBounds();
                mamap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 100));
            }
        }

        private LatLngBounds getLatLngBounds() {
            LatLngBounds.Builder b = LatLngBounds.builder();
            for (int i = 0; i < mPois.size(); i++) {
                b.include(new LatLng(mPois.get(i).getLatLonPoint().getLatitude(),
                        mPois.get(i).getLatLonPoint().getLongitude()));
            }
            return b.build();
        }

        private MarkerOptions getMarkerOptions(final int index, int statusCount) {

            return new MarkerOptions()
                    .position(
                            new LatLng(mPois.get(index).getLatLonPoint()
                                    .getLatitude(), mPois.get(index)
                                    .getLatLonPoint().getLongitude()))
                    .title(getTitle(index)).snippet(getSnippet(index))
                    .icon(getBitmapDescriptor(statusCount));
        }

        protected String getTitle(int index) {
            return mPois.get(index).getTitle();
        }

        protected String getSnippet(int index) {
            //return mPois.get(index).getBusinessArea();
            PoiItem poiItem = mPois.get(index);
            return  "地址："+poiItem.getProvinceName()+
                    poiItem.getCityName()+
                    poiItem.getAdName()+ poiItem.getSnippet();
        }

        /**
         * 从marker中得到poi在list的位置。
         *
         * @param marker 一个标记的对象。
         * @return 返回该marker对应的poi在list的位置。
         * @since V2.1.0
         */
        public int getPoiIndex(Marker marker) {
            for (int i = 0; i < mPoiMarks.size(); i++) {
                if (mPoiMarks.get(i).equals(marker)) {
                    return i;
                }
            }
            return -1;
        }

        /**
         * 返回第index的poi的信息。
         * @param index 第几个poi。
         * @return poi的信息。poi对象详见搜索服务模块的基础核心包（com.amap.api.services.core）中的类 <strong><a href="../../../../../../Search/com/amap/api/services/core/PoiItem.html" title="com.amap.api.services.core中的类">PoiItem</a></strong>。
         * @since V2.1.0
         */
        public PoiItem getPoiItem(int index) {
            if (index < 0 || index >= mPois.size()) {
                return null;
            }
            return mPois.get(index);
        }

        protected BitmapDescriptor getBitmapDescriptor(int arg0) {
            //TextView textView = (TextView) findViewById(R.id.tv_markerContent);
            TextView textView = new TextView(getApplicationContext());
            textView.setDrawingCacheEnabled(true);
            textView.setBackgroundResource(R.drawable.poi_marker_content);
            textView.setText(String.valueOf(arg0));
            textView.setGravity(Gravity.CENTER);
            textView.setTextColor(getResources().getColor(android.R.color.white));
            textView.setTextSize(15);
            textView.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            textView.layout(0, 0, textView.getMeasuredWidth(), textView.getMeasuredHeight());

            Bitmap bitmap = textView.getDrawingCache();
            BitmapDescriptor icon = BitmapDescriptorFactory.fromBitmap(bitmap);
            return icon;
        }
    }
}
