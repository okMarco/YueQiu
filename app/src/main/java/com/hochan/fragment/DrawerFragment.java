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
import android.widget.TextView;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVUser;
import com.hochan.yueqiu.R;
import com.hochan.yueqiu.RecordActivity;
import com.squareup.picasso.Picasso;
import com.tencent.tauth.bean.Pic;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2016/4/5.
 */
public class DrawerFragment extends Fragment implements View.OnClickListener{

    private Button btnMyRecord;
    private View mView;
    private Context mContext;
    private TextView tvUserName;
    private CircleImageView civUserIcon;

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
    public void onResume() {
        super.onResume();
        AVUser avUser = AVUser.getCurrentUser();
        if(avUser != null){
            if(tvUserName != null) {
                tvUserName.setText(avUser.getUsername());
                AVFile avFile = avUser.getAVFile("avatar");
                if(avFile != null) {
                    Picasso.with(mContext)
                            .load(avFile.getUrl())
                            .into(civUserIcon);
                }
            }
        }else{
            tvUserName.setText("未登录");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mView = getView();
        mContext = getContext();
        btnMyRecord = (Button) mView.findViewById(R.id.btn_record);
        tvUserName = (TextView) mView.findViewById(R.id.tv_user_name);
        civUserIcon = (CircleImageView) mView.findViewById(R.id.civ_user_icon);
        btnMyRecord.setOnClickListener(this);

        AVUser avUser = AVUser.getCurrentUser();
        if(avUser != null){
            tvUserName.setText(avUser.getUsername());
            AVFile avFile = avUser.getAVFile("avatar");
            if(avFile != null) {
                Picasso.with(mContext)
                        .load(avFile.getUrl())
                        .into(civUserIcon);
            }
        }else{
            tvUserName.setText("未登录");
        }
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
