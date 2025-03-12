package com.online.course.manager.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPager2Adapter extends FragmentStateAdapter {

    private final List<Fragment> mFragments;

    public ViewPager2Adapter(@NonNull Fragment fragment) {
        super(fragment);
        mFragments = new ArrayList<>();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getItemCount() {
        return mFragments.size();
    }

    /**
     * @param fragment The fragment you want to add to the list.
     */

    public void add(Fragment fragment) {
        mFragments.add(fragment);
    }

}
