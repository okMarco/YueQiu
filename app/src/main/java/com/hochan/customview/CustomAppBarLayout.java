package com.hochan.customview;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.util.AttributeSet;
import android.widget.Scroller;

/**
 * Created by Administrator on 2016/4/2.
 */
public class CustomAppBarLayout extends AppBarLayout{

    private Scroller mScroller;
    public CustomAppBarLayout(Context context) {
        super(context);
    }

    public CustomAppBarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context);
    }

    public void showSearchLayout(){
        mScroller.startScroll(0, 0, 0, getMeasuredHeight(), 1000);
        invalidate();
    }

    public void hideSearchLayout(){
        mScroller.startScroll(0, getScrollY(), 0, 0-getScrollY(), 1000);
        invalidate();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(mScroller.computeScrollOffset()){
            scrollTo(0, mScroller.getCurrY());
            invalidate();
        }
    }

}
