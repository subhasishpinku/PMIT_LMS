package com.online.course.manager.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private List<Fragment> mFragments;
    private List<String> mFragmentsTitles;

    public ViewPagerAdapter(FragmentManager fm) {
        super(fm, FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        mFragments = new ArrayList<>();
        mFragmentsTitles = new ArrayList<>();
    }

    public ViewPagerAdapter(@NonNull FragmentManager fm, int behavior) {
        super(fm, behavior);
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

    /**
     * @param fragment The fragment you want to add to the list.
     * @param title    The fragment title.
     */

    public void add(Fragment fragment, String title) {
        mFragments.add(fragment);
        mFragmentsTitles.add(title);
    }

    public void removeAll() {
        mFragments.clear();
        mFragmentsTitles.clear();
    }


    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentsTitles.get(position);
    }

}
