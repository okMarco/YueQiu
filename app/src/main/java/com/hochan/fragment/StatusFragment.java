package com.hochan.fragment;

import android.content.Context;
import android.nfc.Tag;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.AVUser;
import com.avos.avoscloud.FindCallback;
import com.hochan.adapter.StatusAdapter;
import com.hochan.yueqiu.MyApplication;
import com.hochan.yueqiu.R;

import java.net.ContentHandler;
import java.util.List;

/**
 * Created by Administrator on 2016/4/3.
 */
public class StatusFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener{

    public final static String TAG = "tag";
    public final static int SOCCERFIELD = 0;
    public final static int MYINITIATE = 1;
    public final static int MYPARTICIPATE = 2;
    private View mView;
    private Context mContext;
    private RecyclerView rvStatus;
    private StatusAdapter mStatusAdapter;
    private String mFieldID;
    private int mTag = 0;

    private AVUser mAVUser;
    private SwipeRefreshLayout mSwipeRefresh;

    public static StatusFragment newInstance(int tag, String fieldID){
        StatusFragment statusFragment = new StatusFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(TAG, tag);
        bundle.putString(MyApplication.FIELD_ID, fieldID);
        statusFragment.setArguments(bundle);
        return statusFragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.status_fragment, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        System.out.println("onActivityCreated");

        mView = getView();

        mContext = getContext();
        mSwipeRefresh = (SwipeRefreshLayout) mView.findViewById(R.id.swipe_refresh_layout);
        mSwipeRefresh.setProgressBackgroundColorSchemeResource(R.color.colorPrimary);
        mSwipeRefresh.setColorSchemeResources(android.R.color.white);

        rvStatus = (RecyclerView) mView.findViewById(R.id.rv_statusList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        rvStatus.setLayoutManager(layoutManager);
        rvStatus.setHasFixedSize(true);
        mStatusAdapter = new StatusAdapter(mContext);
        rvStatus.setAdapter(mStatusAdapter);

        mAVUser = AVUser.getCurrentUser();

        mTag = getArguments().getInt(TAG);
        AVQuery<AVObject> avQuery = null;
        switch (mTag){
            case SOCCERFIELD:
                mFieldID = getArguments().getString(MyApplication.FIELD_ID);
                AVObject avObject = AVObject.createWithoutData(MyApplication.OBJECT_FIELD, mFieldID);
                avQuery = avObject.getRelation(MyApplication.FIELD_STATUS).getQuery();
                break;
            case MYINITIATE:
                avQuery = mAVUser.getRelation(MyApplication.USER_ORIGIN_STATUS).getQuery();
                break;
            case MYPARTICIPATE:
                avQuery = mAVUser.getRelation(MyApplication.USER_JOIN_STATUS).getQuery();
                break;
        }
        getStauses(avQuery);
    }

    private void getStauses(AVQuery<AVObject> avQuery){
        avQuery.include(MyApplication.STATUS_SOURCE);
        avQuery.include(MyApplication.STATUS_TARGET_FIELD);
        avQuery.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if(e == null) {
                    mStatusAdapter.setData(list);
                }
            }
        });
    }

    @Override
    public void onRefresh() {

    }
}
