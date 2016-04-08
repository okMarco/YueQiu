package com.hochan.customview;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/4/5.
 */
public class CustomScrollLayout extends LinearLayout{

    private Scroller mScroller;
    private int mHeightOfBottom;
    public CustomScrollLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        View child = getChildAt(0);
        child.layout(0, getMeasuredHeight()-child.getMeasuredHeight(), child.getMeasuredWidth(),
                getMeasuredHeight());
        mHeightOfBottom = child.getMeasuredHeight();
        child = getChildAt(1);
        child.layout(0, getMeasuredHeight(), child.getMeasuredWidth(), getMeasuredHeight() + child.getMeasuredHeight());
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if(mScroller.computeScrollOffset()){
            scrollTo(0, mScroller.getCurrY());
            invalidate();
        }
    }

    public void showListView(){
       // Toast.makeText(getContext(), "mHeightOfBottom "+mHeightOfBottom+" getMeasuredHeight() "+getMeasuredHeight(), Toast.LENGTH_LONG).show();
        mScroller.startScroll(0, 0, 0, getMeasuredHeight() - mHeightOfBottom, 1000);
        invalidate();
    }

    public void hideListView(){
        mScroller.startScroll(0, getScrollY(), 0, 0- getScrollY(), 1000);
        invalidate();
    }
}
