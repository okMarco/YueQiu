package com.hochan.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.hochan.yueqiu.R;
import com.hochan.yueqiu.RecordActivity;

/**
 * Created by Administrator on 2016/4/5.
 */
public class DrawerFragment extends Fragment implements View.OnClickListener{

    private Button btnMyRecord;
    private View mView;
    private Context mContext;

    public static DrawerFragment newInstance(){
        DrawerFragment drawerFragment = new DrawerFragment();
        return drawerFragment;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.drawer_layout, container, false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mView = getView();
        mContext = getContext();
        btnMyRecord = (Button) mView.findViewById(R.id.btn_record);
        btnMyRecord.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_record:
                Intent intent = new Intent();
                intent.setClass(mContext, RecordActivity.class);
                mContext.startActivity(intent);
                break;
        }
    }
}
