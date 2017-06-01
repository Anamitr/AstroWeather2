package com.example.socha.astroweather;


import android.content.res.Configuration;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.os.Bundle;
import android.util.DisplayMetrics;

import com.example.socha.astroweather.fragments.InfoFragment;
import com.example.socha.astroweather.fragments.MoonFragment;
import com.example.socha.astroweather.fragments.SunFragment;
import com.example.socha.astroweather.fragments.WeatherFragment;

public class MainActivity extends FragmentActivity {

    private static final int NUM_PAGES = 4;

    public static Double longitude = 0.0, latitude = 0.0, refreshRate = 1.0;
    public static String city = "Lodz", locLongitude = "0", locLatitude = "0";


    public boolean isTablet;
    private ViewPager mPager;
    private PagerAdapter mPagerAdapter;
    public static SunFragment sunFragment = new SunFragment();
    public static MoonFragment moonFragment = new MoonFragment();
    public static InfoFragment infoFragment = new InfoFragment();
    public static WeatherFragment weatherFragment = new WeatherFragment();
    public static ForecastActivity forecastActivity = new ForecastActivity();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        isTablet = isTablet();
        setContentView(R.layout.activity_main_slim);

        // Instantiate a ViewPager and a PagerAdapter.
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOffscreenPageLimit(10);
        mPagerAdapter.notifyDataSetChanged();
    }


    @Override
    public void onBackPressed() {
        if (mPager.getCurrentItem() == 0) {
            super.onBackPressed();
        } else {
            // Otherwise, select the previous step.
            mPager.setCurrentItem(mPager.getCurrentItem() - 1);
        }
    }

    public boolean isTablet() {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;
        if (width > 1024 && height > 1024) return true;
        else return false;
    }

    public static void updateInfo() {
        sunFragment.updateSunInfo();
        moonFragment.updateMoonInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mPagerAdapter.notifyDataSetChanged();
    }

    private class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {
        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) return infoFragment;
            if (position == 1) return forecastActivity;
            if (position == 2) return sunFragment;
            if (position == 3) return moonFragment;
            //if (position == 3) return locationFragment;
            else return new InfoFragment();
        }

        @Override
        public float getPageWidth(int position) {
            if (isTablet && getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
                return (0.333333333333333333333333333333333333f);
            else if (isTablet && getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT)
                return (0.5f);
            else return (1.0f);
        }

        @Override
        public int getCount() {
            return NUM_PAGES;
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }
    }

}