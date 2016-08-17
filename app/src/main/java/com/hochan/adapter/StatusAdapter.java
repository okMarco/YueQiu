package com.hochan.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.avos.avoscloud.AVFile;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVUser;
import com.hochan.fragment.StatusFragment;
import com.hochan.yueqiu.MyApplication;
import com.hochan.yueqiu.R;
import com.squareup.picasso.Picasso;
import com.tencent.tauth.bean.Pic;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2016/4/3.
 */
public class StatusAdapter extends RecyclerView.Adapter{

    private Context mContext;
    private List<AVObject> mStatusList = new ArrayList<>();

    public StatusAdapter(Context context){
        this.mContext = context;
    }

    public void setData(List<AVObject> list){
        this.mStatusList = list;
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.status_layout, parent, false);
        return new StatusViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        StatusViewHolder viewHolder = (StatusViewHolder) holder;
        AVObject avObject = mStatusList.get(position);
        AVUser avUser = avObject.getAVUser(MyApplication.STATUS_SOURCE);
        AVObject avObjectField = avObject.getAVObject(MyApplication.STATUS_TARGET_FIELD);
        viewHolder.tvUserName.setText(avUser.getUsername());
        viewHolder.tvCreateTime.setText(avObject.getCreatedAt().toString());
        viewHolder.tvLocation.setText(avObjectField.getString(MyApplication.FIELD_NAME));

        viewHolder.tvTime.setText(avObject.getDate(MyApplication.STATUS_START_TIME).toString());
        viewHolder.tvEndTime.setText(avObject.getDate(MyApplication.STATUS_END_TIME).toString());

        viewHolder.tvCount.setText(avObject.getString(MyApplication.STATUS_PARTICIPANTS_COUNT));

        AVFile avFile = avUser.getAVFile(MyApplication.USER_AVATAR);
        if(avFile != null) {
            System.out.println(avFile.getUrl());
            Picasso.with(mContext)
                    .load(avFile.getUrl())
                    .into(viewHolder.civUserIcon);
        }else{
            viewHolder.civUserIcon.setImageResource(R.drawable.avatar);
        }
    }

    @Override
    public int getItemCount() {
        return mStatusList.size();
    }

    class StatusViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public CircleImageView civUserIcon;
        public TextView tvUserName, tvCreateTime,
                tvLocation, tvTime, tvEndTime, tvCount;
        public Button btnDo;
        public LinearLayout llParticipants;

        public StatusViewHolder(View itemView) {
            super(itemView);
            civUserIcon = (CircleImageView) itemView.findViewById(R.id.cimg_userIcon);
            tvUserName = (TextView) itemView.findViewById(R.id.tv_userIcon);
            tvCreateTime = (TextView) itemView.findViewById(R.id.tv_createTime);
            tvLocation = (TextView) itemView.findViewById(R.id.tv_location);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            tvEndTime = (TextView) itemView.findViewById(R.id.tv_end_time);
            tvCount = (TextView) itemView.findViewById(R.id.tv_count);
            btnDo = (Button) itemView.findViewById(R.id.btn_do);
            llParticipants = (LinearLayout) itemView.findViewById(R.id.ll_participants);

            btnDo.setText("加入");
            btnDo.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
           switch (v.getId()){
               case R.id.btn_do:
                   if(btnDo.getText().equals("加入")) {

                       new AlertDialog.Builder(mContext)
                               .setTitle("加入约球")
                               .setMessage("确定加入该约球吗？")
                               .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {

                                   }
                               })
                               .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                   @Override
                                   public void onClick(DialogInterface dialog, int which) {
                                       btnDo.setText("退出");
                                   }
                               })
                               .show();
                   }
                   else if(btnDo.getText() == "退出")
                       btnDo.setText("加入");
           }
        }
    }

}
