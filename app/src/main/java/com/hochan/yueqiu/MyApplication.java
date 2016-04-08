package com.hochan.yueqiu;

import android.app.Application;

import com.hochan.tools.ScreenTools;
import com.wilddog.client.Wilddog;

/**
 * Created by Administrator on 2016/3/26.
 */
public class MyApplication extends Application {

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
    }
}
