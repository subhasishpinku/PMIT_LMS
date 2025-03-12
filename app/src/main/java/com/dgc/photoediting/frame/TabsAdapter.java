package com.dgc.photoediting.frame;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import com.dgc.photoediting.fragment.Recent;
import com.dgc.photoediting.fragment.Top;
import com.dgc.photoediting.fragment.Trending;

public class TabsAdapter extends FragmentStatePagerAdapter {
    int mNumOfTabs;
    public TabsAdapter(FragmentManager fm, int NoofTabs){
        super(fm);
        this.mNumOfTabs = NoofTabs;
    }
    @Override
    public int getCount() {
        return mNumOfTabs;
    }
    @Override
    public Fragment getItem(int position){
        switch (position){
            case 0:
                Top top = new Top();
                return top;
            case 1:
                Trending trending = new Trending();
                return trending;
            case 2:
                Recent recent = new Recent();
                return recent;
            default:
                return null;
        }
    }
}