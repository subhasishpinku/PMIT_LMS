package com.dgc.photoediting;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.dgc.photoediting.fragment.Features;
import com.dgc.photoediting.fragment.TabsAdapter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

public class Catagories_Activity extends AppCompatActivity implements TabHost.OnTabChangeListener, BottomNavigationView.OnNavigationItemSelectedListener{
    TabLayout tabLayout;
    TabHost tabHost;
    ViewPager viewPager;
    //    private int[] tabicons ={
//            R.mipmap.pending,
//            R.mipmap.accept
//    };
    private ActionBar toolbar;
    ImageView img;
    TextView tv;
    Toolbar toolbar1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.catagories_activity);
        tabLayout = (TabLayout)findViewById(R.id.tab_layout);
        viewPager =(ViewPager)findViewById(R.id.view_pager);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(this);
        Tab_Setting();

        initToolBar();
    }
    public void initToolBar() {
        toolbar1 = (Toolbar) findViewById(R.id.toolbar);
        toolbar1.setTitle("catagories");
        setSupportActionBar(toolbar1);
        toolbar1.setNavigationIcon(R.drawable.ic_baseline_arrow_back_24);
        toolbar1.setNavigationOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        finish();
                    }
                }
        );
    }
    public void Tab_Setting(){
        tabLayout.addTab(tabLayout.newTab().setText("CATEGORY"));
        tabLayout.addTab(tabLayout.newTab().setText("RECOMMENDED"));
        tabLayout.addTab(tabLayout.newTab().setText("FEED"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        if (getApplication() != null) {
            TabsAdapter tabsAdapter = new TabsAdapter(this.getSupportFragmentManager(), tabLayout.getTabCount());
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
            this.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }
}
