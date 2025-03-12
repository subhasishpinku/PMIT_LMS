package com.dgc.photoediting.fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TabHost;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import com.dgc.photoediting.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
public class Feed  extends Fragment implements TabHost.OnTabChangeListener, BottomNavigationView.OnNavigationItemSelectedListener{
    TabLayout tabLayout;
    TabHost tabHost;
    ViewPager viewPager;
//    private int[] tabicons ={
//            R.mipmap.pending,
//            R.mipmap.accept
//    };
  private ActionBar toolbar;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }
    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            getFragmentManager().beginTransaction().detach(this).attach(this).commit();
            Tab_Setting();
        }
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.dasboard_fragment, container, false);
        tabLayout = (TabLayout)rootView.findViewById(R.id.tab_layout);
        viewPager =(ViewPager)rootView.findViewById(R.id.view_pager);
        BottomNavigationView navigation = rootView.findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
        Tab_Setting();
        return rootView;
    }
     public void Tab_Setting(){
         tabLayout.addTab(tabLayout.newTab().setText("CATEGORY"));
         tabLayout.addTab(tabLayout.newTab().setText("RECOMMENDED"));
         tabLayout.addTab(tabLayout.newTab().setText("FEED"));
         tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
         if (getActivity() != null) {
             TabsAdapter tabsAdapter = new TabsAdapter(getActivity().getSupportFragmentManager(), tabLayout.getTabCount());
             viewPager.setAdapter(tabsAdapter);
         }
         // tabLayout = getHost();
         // tabLayout.getTabWidget().getChildAt(1).setBackgroundResource(R.drawable.ab);
         // tabLayout.getTabWidget().getChildAt(2).setBackgroundResource(R.drawable.ab);
         // tabLayout.getTabWidget().setCurrentTab(0);
         // tabLayout.getTabWidget().getChildAt(0).setBackgroundResource(R.drawable.abch);
         tabLayout.getTabAt(0);
         //tabLayout.getTabAt(1).setIcon(tabicons[1]);
         viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
         tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
             @Override
             public void onTabSelected(TabLayout.Tab tab) {
                 viewPager.setCurrentItem(tab.getPosition());
                 if (tab.getPosition()==0){
                     Log.e("TABB","0");
                 }
                 else if (tab.getPosition()==1){
                     Log.e("TABB","1");
                 }
                 else if (tab.getPosition()==2){
                     Log.e("TABB","2");
                 }
                 else {
                 }
             }
             @Override
             public void onTabUnselected(TabLayout.Tab tab) {
             }
             @Override
             public void onTabReselected(TabLayout.Tab tab) {
             }
         });
     }
    @Override
    public void onTabChanged(String tabId) {

    }
    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment fragment = null;
        switch (item.getItemId()) {
            case R.id.navigation_categories:
                fragment = new Features();
                break;
            case R.id.recommand:
                fragment = new Features();
                break;
            case R.id.feed:
                fragment = new Features();
                break;
//            case R.id.user:
//                fragment = new Features();
//                break;
        }
        return loadFragment(fragment);
    }

    private boolean loadFragment(Fragment fragment) {
        //switching fragment
        if (fragment != null) {
            getActivity().getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
