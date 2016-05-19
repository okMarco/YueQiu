package com.hochan.yueqiu;

import android.app.Application;

import com.avos.avoscloud.AVOSCloud;
import com.avos.avoscloud.AVUser;
import com.hochan.tools.ScreenTools;
import com.wilddog.client.Wilddog;

/**
 * Created by Administrator on 2016/3/26.
 */
public class MyApplication extends Application {

    public static final String OBJECT_FIELD = "Field";
    public static final String OBJECT_USER = "_User";

    public static final String FIELD_NAME = "name";
    public static final String FIELD_ADDRESS = "address";
    public static final String FIELD_GEO = "field_geo";
    public static final String FIELD_STATUS_COUNT = "status_count";
    public static final String FIELD_ID = "field_id";
    public static final String FIELD_STATUS = "status";

    public static final String STATUS_TARGET_FIELD = "target_field";
    public static final String STATUS_FIELD_NAME = "fid_name";
    public static final String STATUS_START_TIME = "start_time";
    public static final String STATUS_END_TIME = "end_time";
    public static final String STATUS_PARTICIPANTS_COUNT = "participants_count";
    public static final String STATUS_PARTICIPANTS = "participants";
    public static final String STATUS_SOURCE = "source";
    public static final String STATUS_CREATE_AT = "createAt";

    public static final String USER_ORIGIN_STATUS = "origin_status";
    public static final String USER_JOIN_STATUS = "join_status";
    public static final String USER_ORIGIN_STATUS_COUNT = "origin_status_count";
    public static final String USER_JOIN_STATUS_COUNT = "join_status_count";
    public static final String USER_AVATAR = "avatar";

    public static int mWidthOfScreen;
    public static int mHeightOfScreen;
    public static int mHeightOfDialog;
    public static int mWidthOfDialog;
    @Override
    public void onCreate() {
        super.onCreate();
        Wilddog.setAndroidContext(this);
        ScreenTools screenTools = ScreenTools.instance(this);
        mWidthOfScreen = screenTools.getScreenWidth();
        mHeightOfScreen = screenTools.getScreenHeight()-screenTools.dip2px(25);
        mHeightOfDialog = mHeightOfScreen - screenTools.dip2px(20);
        mWidthOfDialog = mWidthOfScreen - screenTools.dip2px(20);

        AVOSCloud.initialize(this, "89qPyjHaRatC5CKLbT0II4oj-gzGzoHsz",
                "E1nlIfu5y9pVVphXmEDgMYdS");
        AVOSCloud.setDebugLogEnabled(true);

        AVUser avUser = AVUser.getCurrentUser();
        if(avUser != null){
            System.out.println("当前用户不为空");
            System.out.println("用户名："+avUser.getUsername());
        }else{
            System.out.println("当前用户为空");
        }
    }
}
