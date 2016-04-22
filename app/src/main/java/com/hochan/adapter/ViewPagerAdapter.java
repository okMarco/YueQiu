package com.hochan.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.hochan.fragment.StatusFragment;

/**
 * Created by Administrator on 2016/3/23.
 */
public class ViewPagerAdapter extends FragmentPagerAdapter {

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position){
            case 0:
                return StatusFragment.newInstance(StatusFragment.MYINITIATE);
            case 1:
                return StatusFragment.newInstance(StatusFragment.MYPARTICIPATE);
        }
        return null;
    }

    @Override
    public int getCount() {
        return 2;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "我发起的";
            case 1:
                return "我加入的";
        }
        return null;
    }
}