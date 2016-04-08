package com.hochan.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hochan.adapter.StatusAdapter;
import com.hochan.yueqiu.R;

import java.net.ContentHandler;

/**
 * Created by Administrator on 2016/4/3.
 */
public class StatusFragment extends Fragment{

    public final static String TAG = "tag";
    private View mView;
    private Context mContext;
    private RecyclerView rvStatus;
    private StatusAdapter mStatusAdapter;

    public static StatusFragment newInstance(String tag){
        StatusFragment statusFragment = new StatusFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TAG, tag);
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
        mView = getView();

        mContext = getContext();
        rvStatus = (RecyclerView) mView.findViewById(R.id.rv_statusList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
        rvStatus.setLayoutManager(layoutManager);
        rvStatus.setHasFixedSize(true);
        mStatusAdapter = new StatusAdapter(mContext);
        rvStatus.setAdapter(mStatusAdapter);
    }
}
