package com.hochan.fragment;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.UiSettings;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.LatLngBounds;
import com.amap.api.maps2d.model.MarkerOptions;
import com.amap.api.maps2d.overlay.DrivingRouteOverlay;
import com.amap.api.maps2d.overlay.WalkRouteOverlay;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.BusRouteResult;
import com.amap.api.services.route.DrivePath;
import com.amap.api.services.route.DriveRouteResult;
import com.amap.api.services.route.RouteSearch;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkRouteResult;
import com.hochan.adapter.BusRouteAdapter;
import com.hochan.customview.CustomScrollLayout;
import com.hochan.route.BusResultListAdapter;
import com.hochan.route.DriveSegmentListAdapter;
import com.hochan.route.WalkSegmentListAdapter;
import com.hochan.util.AMapUtil;
import com.hochan.util.ToastUtil;
import com.hochan.yueqiu.MyApplication;
import com.hochan.yueqiu.R;
import com.hochan.yueqiu.SoccerFieldActivity;

import java.util.List;

/**
 * Created by Administrator on 2016/4/5.
 */
public class RouteFragment extends DialogFragment implements RouteSearch.OnRouteSearchListener{

    private LatLonPoint mStartPoint;
    private LatLonPoint mEndPoint;
    private MapView mapView;
    private View mView;

    private AMap aMap;
    private Context mContext;
    private RouteSearch mRouteSearch;
    private DriveRouteResult mDriveRouteResult;
    private BusRouteResult mBusRouteResult;
    private WalkRouteResult mWalkRouteResult;
    private int mType;
    private String mCurrentCityName = "北京";
    public static final int ROUTE_TYPE_BUS = 1;
    public static final int ROUTE_TYPE_DRIVE = 2;
    public static final int ROUTE_TYPE_WALK = 3;
    public static final String TYPE = "type";

    private ListView listViewRoute;
    private CustomScrollLayout scrollLayout;

    private UiSettings mUiSettings;
    private LinearLayout mBusResultLayout;
    private RelativeLayout mBottomLayout;
    private TextView mRotueTimeDes, mRouteDetailDes;
    private ImageView mBus;
    private ImageView mDrive;
    private ImageView mWalk;
    //private ListView mBusResultList;
    private ExpandableListView mBusResultList;
    private ProgressDialog progDialog = null;// 搜索时进度条
    public static RouteFragment newInstance(LatLonPoint startPoint, LatLonPoint endPoint, int type){
        RouteFragment routeFragment = new RouteFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(SoccerFieldActivity.STARTPOINT, startPoint);
        bundle.putParcelable(SoccerFieldActivity.ENDPOINT, endPoint);
        bundle.putInt(TYPE, type);
        routeFragment.setArguments(bundle);
        return routeFragment;
    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().requestWindowFeature(Window.FEATURE_NO_TITLE);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.route_fragment, container, false);
        if(getArguments().getInt(TYPE) == ROUTE_TYPE_BUS)
            view.findViewById(R.id.scrollLayout).setVisibility(View.GONE);
        else
            view.findViewById(R.id.scrollLayout).setVisibility(View.VISIBLE);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));//注意此处
        getDialog().getWindow().setLayout((int) (MyApplication.mWidthOfScreen), MyApplication.mHeightOfScreen);//这2行,和上面的一样,注意顺序就行;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mView = getView();
        mContext = getContext();
        mView.findViewById(R.id.scrollLayout).bringToFront();
        scrollLayout = (CustomScrollLayout) mView.findViewById(R.id.scrollLayout);
        listViewRoute = (ListView) mView.findViewById(R.id.route_segment_list);
        mapView = (MapView) getView().findViewById(R.id.route_map);
        mapView.onCreate(savedInstanceState);

        mType = getArguments().getInt(TYPE);
        mStartPoint = getArguments().getParcelable(SoccerFieldActivity.STARTPOINT);
        mEndPoint = getArguments().getParcelable(SoccerFieldActivity.ENDPOINT);

        init();
        setfromandtoMarker();
    }

    /**
     * 初始化AMap对象
     */
    private void init() {
        if (aMap == null) {
            aMap = mapView.getMap();
        }
        registerListener();
        mUiSettings = aMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(false);
        mRouteSearch = new RouteSearch(mContext);
        mRouteSearch.setRouteSearchListener(this);
        mBottomLayout = (RelativeLayout) mView.findViewById(R.id.bottom_layout);
        mBusResultLayout = (LinearLayout) mView.findViewById(R.id.bus_result);
        mRotueTimeDes = (TextView) mView.findViewById(R.id.firstline);
        mRouteDetailDes = (TextView) mView.findViewById(R.id.secondline);
        //mBusResultList = (ListView) mView.findViewById(R.id.bus_result_list);
        mBusResultList = (ExpandableListView) mView.findViewById(R.id.expListView);
    }

    /**
     * 注册监听
     */
    private void registerListener() {
//        aMap.setOnMapClickListener(RouteActivity.this);
//        aMap.setOnMarkerClickListener(RouteActivity.this);
//        aMap.setOnInfoWindowClickListener(RouteActivity.this);
//        aMap.setInfoWindowAdapter(RouteActivity.this);
    }


    private void setfromandtoMarker() {
        LatLngBounds.Builder builder = LatLngBounds.builder();
        builder.include(new LatLng(mStartPoint.getLatitude(), mStartPoint.getLongitude()));
        builder.include(new LatLng(mEndPoint.getLatitude(), mStartPoint.getLongitude()));
        aMap.moveCamera(CameraUpdateFactory.newLatLngBounds(builder.build(), 15));
        aMap.addMarker(new MarkerOptions()
                .position(AMapUtil.convertToLatLng(mStartPoint))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_start)));
        aMap.addMarker(new MarkerOptions()
                .position(AMapUtil.convertToLatLng(mEndPoint))
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_end)));
        if(mType == ROUTE_TYPE_DRIVE) {
            searchRouteResult(ROUTE_TYPE_DRIVE, RouteSearch.DrivingDefault);
            mapView.setVisibility(View.VISIBLE);
            mBusResultLayout.setVisibility(View.GONE);
        }
        if(mType == ROUTE_TYPE_BUS) {
            searchRouteResult(ROUTE_TYPE_BUS, RouteSearch.BusDefault);
            mapView.setVisibility(View.GONE);
            mBusResultLayout.setVisibility(View.VISIBLE);
        }
        if(mType == ROUTE_TYPE_WALK) {
            searchRouteResult(ROUTE_TYPE_WALK, RouteSearch.WalkDefault);
            mapView.setVisibility(View.VISIBLE);
            mBusResultLayout.setVisibility(View.GONE);
        }
    }

    /**
     * 开始搜索路径规划方案
     */
    public void searchRouteResult(int routeType, int mode) {
        if (mStartPoint == null) {
            //ToastUtil.show(mContext, "定位中，稍后再试...");
            return;
        }
        if (mEndPoint == null) {
            //ToastUtil.show(mContext, "终点未设置");
        }
        showProgressDialog();
        final RouteSearch.FromAndTo fromAndTo = new RouteSearch.FromAndTo(
                mStartPoint, mEndPoint);
        if (routeType == ROUTE_TYPE_BUS) {// 公交路径规划
            RouteSearch.BusRouteQuery query = new RouteSearch.BusRouteQuery(fromAndTo, mode,
                    mCurrentCityName, 0);// 第一个参数表示路径规划的起点和终点，第二个参数表示公交查询模式，第三个参数表示公交查询城市区号，第四个参数表示是否计算夜班车，0表示不计算
            mRouteSearch.calculateBusRouteAsyn(query);// 异步路径规划公交模式查询
        } else if (routeType == ROUTE_TYPE_DRIVE) {// 驾车路径规划
            RouteSearch.DriveRouteQuery query = new RouteSearch.DriveRouteQuery(fromAndTo, mode, null,
                    null, "");// 第一个参数表示路径规划的起点和终点，第二个参数表示驾车模式，第三个参数表示途经点，第四个参数表示避让区域，第五个参数表示避让道路
            mRouteSearch.calculateDriveRouteAsyn(query);// 异步路径规划驾车模式查询
        } else if (routeType == ROUTE_TYPE_WALK) {// 步行路径规划
            RouteSearch.WalkRouteQuery query = new RouteSearch.WalkRouteQuery(fromAndTo, mode);
            mRouteSearch.calculateWalkRouteAsyn(query);// 异步路径规划步行模式查询
        }
    }

    @Override
    public void onBusRouteSearched(BusRouteResult result, int errorCode) {
        dissmissProgressDialog();
        mBottomLayout.setVisibility(View.GONE);
        aMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == 1000) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mBusRouteResult = result;
                    //BusResultListAdapter mBusResultListAdapter = new BusResultListAdapter(mContext, mBusRouteResult);
                    BusRouteAdapter busRouteAdapter = new BusRouteAdapter(mContext, mBusRouteResult);
                    //mBusResultList.setAdapter(mBusResultListAdapter);
                    mBusResultList.setAdapter(busRouteAdapter);
                } else if (result != null && result.getPaths() == null) {
                    ToastUtil.show(mContext, R.string.no_result);
                }
            } else {
                ToastUtil.show(mContext, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(mContext, errorCode);
        }
    }

    @Override
    public void onDriveRouteSearched(DriveRouteResult result, int errorCode) {
        dissmissProgressDialog();
        aMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == 1000) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mDriveRouteResult = result;
                    final DrivePath drivePath = mDriveRouteResult.getPaths()
                            .get(0);
                    DrivingRouteOverlay drivingRouteOverlay = new DrivingRouteOverlay(
                            mContext, aMap, drivePath,
                            mDriveRouteResult.getStartPos(),
                            mDriveRouteResult.getTargetPos());

                    drivingRouteOverlay.removeFromMap();
                    drivingRouteOverlay.addToMap();
                    drivingRouteOverlay.zoomToSpan();
                    mBottomLayout.setVisibility(View.VISIBLE);
                    int dis = (int) drivePath.getDistance();
                    int dur = (int) drivePath.getDuration();
                    String des = AMapUtil.getFriendlyTime(dur)+"("+AMapUtil.getFriendlyLength(dis)+")";
                    mRotueTimeDes.setText(des);
                    mRouteDetailDes.setVisibility(View.VISIBLE);
                    int taxiCost = (int) mDriveRouteResult.getTaxiCost();
                    mRouteDetailDes.setText("打车约"+taxiCost+"元");

                    mBottomLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Toast.makeText(mContext, "点击", Toast.LENGTH_LONG).show();
                            DriveSegmentListAdapter listAdapter = new DriveSegmentListAdapter(getContext(), drivePath.getSteps());
                            listViewRoute.setAdapter(listAdapter);
                            Toast.makeText(mContext, ""+scrollLayout.getScrollY(), Toast.LENGTH_LONG).show();
                            if(scrollLayout.getScrollY() == 0)
                                scrollLayout.showListView();
                            else
                                scrollLayout.hideListView();
                        }
                    });
                } else if (result != null && result.getPaths() == null) {
                    ToastUtil.show(mContext, R.string.no_result);
                }

            } else {
                ToastUtil.show(mContext, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(mContext, errorCode);
        }


    }

    @Override
    public void onWalkRouteSearched(WalkRouteResult result, int errorCode) {
        dissmissProgressDialog();
        aMap.clear();// 清理地图上的所有覆盖物
        if (errorCode == 1000) {
            if (result != null && result.getPaths() != null) {
                if (result.getPaths().size() > 0) {
                    mWalkRouteResult = result;
                    final WalkPath walkPath = mWalkRouteResult.getPaths()
                            .get(0);
                    WalkRouteOverlay walkRouteOverlay = new WalkRouteOverlay(
                            mContext, aMap, walkPath,
                            mWalkRouteResult.getStartPos(),
                            mWalkRouteResult.getTargetPos());
                    walkRouteOverlay.removeFromMap();
                    walkRouteOverlay.addToMap();
                    walkRouteOverlay.zoomToSpan();
                    mBottomLayout.setVisibility(View.VISIBLE);
                    int dis = (int) walkPath.getDistance();
                    int dur = (int) walkPath.getDuration();
                    String des = AMapUtil.getFriendlyTime(dur)+"("+AMapUtil.getFriendlyLength(dis)+")";
                    mRotueTimeDes.setText(des);
                    mRouteDetailDes.setVisibility(View.GONE);
                    mBottomLayout.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            Toast.makeText(mContext, "点击", Toast.LENGTH_LONG).show();
                            WalkSegmentListAdapter listAdapter = new WalkSegmentListAdapter(getContext(), walkPath.getSteps());
                            listViewRoute.setAdapter(listAdapter);
                            Toast.makeText(mContext, "" + scrollLayout.getScrollY(), Toast.LENGTH_LONG).show();
                            if (scrollLayout.getScrollY() == 0)
                                scrollLayout.showListView();
                            else
                                scrollLayout.hideListView();
                        }
                    });
                } else if (result != null && result.getPaths() == null) {
                    ToastUtil.show(mContext, R.string.no_result);
                }

            } else {
                ToastUtil.show(mContext, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(mContext, errorCode);
        }
    }

    /**
     * 显示进度框
     */
    private void showProgressDialog() {
        if (progDialog == null)
            progDialog = new ProgressDialog(mContext);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setIndeterminate(false);
        progDialog.setCancelable(true);
        progDialog.setMessage("正在搜索");
        progDialog.show();
    }

    /**
     * 隐藏进度框
     */
    private void dissmissProgressDialog() {
        if (progDialog != null) {
            progDialog.dismiss();
        }
    }


    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }
}
