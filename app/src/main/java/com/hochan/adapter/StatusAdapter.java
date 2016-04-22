package com.hochan.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hochan.fragment.StatusFragment;
import com.hochan.yueqiu.R;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Administrator on 2016/4/3.
 */
public class StatusAdapter extends RecyclerView.Adapter{

    private Context mContext;
    public StatusAdapter(Context context){
        this.mContext = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(mContext).inflate(R.layout.status_layout, parent, false);
        return new StatusViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        StatusViewHolder viewHolder = (StatusViewHolder) holder;
        switch (position){
            case 0:
                viewHolder.civUserIcon.setImageResource(R.drawable.avatar_0);
                break;
            case 1:
                viewHolder.civUserIcon.setImageResource(R.drawable.avatar_1);
                break;
            case 2:
                viewHolder.civUserIcon.setImageResource(R.drawable.avatar_2);
                break;
            case 3:
                viewHolder.civUserIcon.setImageResource(R.drawable.avatar_3);
                break;
            case 4:
                viewHolder.civUserIcon.setImageResource(R.drawable.avatar_4);
                break;
        }
        //viewHolder.tvUserName.setText("HO");
//        for(int i = 0; i < 6; i++){
//            CircleImageView circleImageView = new CircleImageView(mContext);
//            circleImageView.setBackgroundResource(R.drawable.avatar);
//            circleImageView.setLayoutParams(new LinearLayout.LayoutParams(100, 100));
//            viewHolder.llParticipants.addView(circleImageView);
//        }
    }

    @Override
    public int getItemCount() {
        return 20;
    }

    class StatusViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public CircleImageView civUserIcon;
        public TextView tvUserName, tvCreateTime, tvLocation, tvTime, tvCount;
        public Button btnDo;
        public LinearLayout llParticipants;

        public StatusViewHolder(View itemView) {
            super(itemView);
            civUserIcon = (CircleImageView) itemView.findViewById(R.id.cimg_userIcon);
            tvUserName = (TextView) itemView.findViewById(R.id.tv_userIcon);
            tvCreateTime = (TextView) itemView.findViewById(R.id.tv_createTime);
            tvLocation = (TextView) itemView.findViewById(R.id.tv_createTime);
            tvTime = (TextView) itemView.findViewById(R.id.tv_time);
            tvCount = (TextView) itemView.findViewById(R.id.tv_count);
            btnDo = (Button) itemView.findViewById(R.id.btn_do);
            llParticipants = (LinearLayout) itemView.findViewById(R.id.ll_participants);

            btnDo.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
           switch (v.getId()){
               case R.id.btn_do:
                   if(btnDo.getText() == "加入")
                        btnDo.setText("退出");
                   else if(btnDo.getText() == "退出")
                       btnDo.setText("加入");
           }
        }
    }

}
