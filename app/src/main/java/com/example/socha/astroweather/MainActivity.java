package com.example.socha.astroweather;


import android.content.Context;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.example.socha.astroweather.fragments.ForecastFragment;
import com.example.socha.astroweather.fragments.InfoFragment;
import com.example.socha.astroweather.fragments.MoonFragment;
import com.example.socha.astroweather.fragments.SunFragment;
//import com.example.socha.astroweather.fragments.WeatherFragment;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends FragmentActivity {

    private static final int NUM_PAGES = 4;

    public static Double longitude = 0.0, latitude = 0.0, refreshRate = 1.0;
    public static String city = "Pabianice";

    public boolean isTablet;
    public static ViewPager mPager;
    public static ScreenSlidePagerAdapter mPagerAdapter;
    public static SunFragment sunFragment = new SunFragment();
    public static MoonFragment moonFragment = new MoonFragment();
    public static InfoFragment infoFragment = new InfoFragment();
    //public static WeatherFragment weatherFragment = new WeatherFragment();
    public static ForecastFragment forecastFragment = new ForecastFragment();
    public static ArrayList<String> cities;
    public static List<String> favouriteCities = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        readFavCities();

        isTablet = isTablet();
        setContentView(R.layout.activity_main_slim);

        // Instantiate a ViewPager and a PagerAdapter.
        initializePager();

        cities = getCitiesFromFile();
        Log.d("cities length:", new Integer(cities.size()).toString());

        if(isOnline()) {
            Toast.makeText(this, "is online!", Toast.LENGTH_LONG).show();
        } else Toast.makeText(this, "is not online!", Toast.LENGTH_LONG).show();
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

    public ArrayList<String> getCitiesFromFile() {
        Log.d("file loading start","");
        ArrayList<String> cities = new ArrayList<String>();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(getAssets().open("cities.txt")));

            // do reading, usually loop until end of file reading
            String mLine;
            while ((mLine = reader.readLine()) != null) {
                //Log.d("city:", mLine);
                cities.add(mLine);
            }
        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
        //Log.d("cities length:", new Integer(cities.size()).toString());
        return cities;
    }

    public void onStop() {
        super.onStop();
        try {
            FileOutputStream fOut = openFileOutput("favouriteCities.txt", MODE_WORLD_READABLE);
            OutputStreamWriter osw = new OutputStreamWriter(fOut);

            // Write the string to the file
            Log.d("fav",favouriteCities.toString());
            for(String string : favouriteCities) {
                osw.write(string + " ");
            }
            osw.write("\n");

       /* ensure that everything is
        * really written out and close */
            osw.flush();
            osw.close();
        } catch (FileNotFoundException e) {
            Log.d("FileNotFoundException","!!!");
        }
        catch (IOException e) {
            Log.d("IOException","!!!");
        }
        //savedInstanceState.putSerializable("", );
    }

    public void readFavCities() {
        try {
            FileInputStream fIn = openFileInput("favouriteCities.txt");
            InputStreamReader isr = new InputStreamReader(fIn);

        /* Prepare a char-Array that will
         * hold the chars we read back in. */
            char[] inputBuffer = new char[9000];

            // Fill the Buffer with data from the file
            isr.read(inputBuffer);

            // Transform the chars to a String
            String readString = new String(inputBuffer);
            readString = readString.substring(0,readString.indexOf("\n"));
            favouriteCities = new ArrayList<String>(Arrays.asList(readString.split(" ")));
//            Log.d("readString", readString);
//            Log.d("favouriteCities", favouriteCities.toString());

        } catch (FileNotFoundException e) {
            Log.d("FileNotFoundException","!!!");
        }
        catch (IOException e) {
            Log.d("IOException","!!!");
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        Toast.makeText(this, "Orientation changed!", Toast.LENGTH_LONG).show();
        initializePager();
        forecastFragment.updateWeatherForecast(infoFragment.toggleButton.isChecked());
    }

    public void initializePager() {
        mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setOffscreenPageLimit(10);
        mPagerAdapter.notifyDataSetChanged();
    }

    public class ScreenSlidePagerAdapter extends FragmentStatePagerAdapter {

        public ScreenSlidePagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) return infoFragment;
            if (position == 1) return forecastFragment;
            if (position == 2) return sunFragment;
            if (position == 3) return moonFragment;
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