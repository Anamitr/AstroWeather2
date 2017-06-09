package com.example.socha.astroweather.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.socha.astroweather.MainActivity;
import com.example.socha.astroweather.R;
import com.example.socha.astroweather.tools.AstronomyCalculator;

import java.util.ArrayList;

public class SunFragment extends Fragment {

    private TextView sunriseTextView, sunriseAzimuthTextView, sunsetTextView, sunsetAzimuthTextView, civSunriseTextView, civSunsetAzimuthTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_sun, container, false);

        sunriseTextView = (TextView)rootView.findViewById(R.id.sunriseTextView);
        sunriseAzimuthTextView = (TextView)rootView.findViewById(R.id.sunriseAzimuthTextView);
        sunsetTextView = (TextView)rootView.findViewById(R.id.sunsetTextView);
        sunsetAzimuthTextView = (TextView)rootView.findViewById(R.id.sunsetAzimuthTextView);
        civSunriseTextView = (TextView)rootView.findViewById(R.id.civSunriseTextView);
        civSunsetAzimuthTextView = (TextView)rootView.findViewById(R.id.civSunsetAzimuthTextView);

        updateSunInfo();
        final Handler handler=new Handler();
        Runnable updateTask=new Runnable() {
            @Override
            public void run() {
                updateSunInfo();
                handler.postDelayed(this, MainActivity.refreshRate.longValue() * 1000);
            }
        };
        handler.postDelayed(updateTask, MainActivity.refreshRate.longValue() * 1000);

        return rootView;
    }

    public void updateSunInfo() {
        ArrayList<String> sunInfo = new AstronomyCalculator(MainActivity.latitude, MainActivity.longitude).calculateSunInfo();
        this.sunriseTextView.setText(sunInfo.remove(0));
        this.sunriseAzimuthTextView.setText(sunInfo.remove(0));
        this.sunsetTextView.setText(sunInfo.remove(0));
        this.sunsetAzimuthTextView.setText(sunInfo.remove(0));
        this.civSunsetAzimuthTextView.setText(sunInfo.remove(0));
        this.civSunriseTextView.setText(sunInfo.remove(0));
    }


//    public void calculateSunInfo() {
//        AstroCalculator.Location loc = new AstroCalculator.Location(MainActivity.latitude, MainActivity.longitude);
//        Calendar mCalendar = new GregorianCalendar();
//        TimeZone mTimeZone = mCalendar.getTimeZone();
//        AstroDateTime datetime = new AstroDateTime(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH) + 1,
//                Calendar.getInstance().get(Calendar.DAY_OF_MONTH), Calendar.getInstance().get(Calendar.HOUR), Calendar.getInstance().get(Calendar.MINUTE),
//                Calendar.getInstance().get(Calendar.SECOND), getOffset(), mTimeZone.inDaylightTime(new Date()));
//        AstroCalculator calc = new AstroCalculator(datetime, loc);
//        AstroCalculator.SunInfo sun = calc.getSunInfo();
//        this.sunriseTextView.setText("Wschód: " + sun.getSunrise().toString());
//        this.sunriseAzimuthTextView.setText("Azymut: " + Double.toString(sun.getAzimuthRise()));
//        this.sunsetTextView.setText("Zachód: " + sun.getSunset().toString());
//        this.sunsetAzimuthTextView.setText("Azymut: " + Double.toString(sun.getAzimuthSet()));
//        AstroDateTime datetime2 = sun.getTwilightEvening();
//        String temp = Integer.toString(datetime2.getHour()) + ":" + Integer.toString(datetime2.getMinute());
//        AstroDateTime datetime3 = sun.getTwilightMorning();
//        String temp2 = Integer.toString(datetime3.getHour()) + ":" + Integer.toString(datetime3.getMinute());
//        this.civSunsetAzimuthTextView.setText("Zachód cyw.: " + temp);
//        this.civSunriseTextView.setText("Wschód cyw.: " + temp2);
//    }
//
//    public int getOffset(){
//        TimeZone timezone = TimeZone.getDefault();
//        int seconds = timezone.getOffset(Calendar.ZONE_OFFSET)/1000;
//        double minutes = seconds/60;
//        double hours = minutes/60;
//        int finalHours = (int) hours;
//        return finalHours;
//    }
}
