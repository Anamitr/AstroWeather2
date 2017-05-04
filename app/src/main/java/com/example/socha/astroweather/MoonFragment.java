package com.example.socha.astroweather;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.astrocalculator.AstroCalculator;
import com.astrocalculator.AstroDateTime;
import com.example.socha.astroweather.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

public class MoonFragment extends Fragment {
    private TextView moonRiseTextView, moonSetTextView, newMoonSetTextView, fullMoonSetAzimuthTextView, moonPhaseRiseAzimuthTextView, synodMoonSetAzimuthTextView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_moon, container, false);

        moonRiseTextView = (TextView)rootView.findViewById(R.id.moonRiseTextView);
        moonSetTextView = (TextView)rootView.findViewById(R.id.moonSetTextView);
        newMoonSetTextView = (TextView)rootView.findViewById(R.id.newMoonSetTextView);
        fullMoonSetAzimuthTextView = (TextView)rootView.findViewById(R.id.fullMoonSetAzimuthTextView);
        moonPhaseRiseAzimuthTextView = (TextView)rootView.findViewById(R.id.moonPhaseRiseAzimuthTextView);
        synodMoonSetAzimuthTextView = (TextView)rootView.findViewById(R.id.synodMoonSetAzimuthTextView);

        updateMoonInfo();
        final Handler handler=new Handler();
        final Runnable updateTask=new Runnable() {
            @Override
            public void run() {
                updateMoonInfo();
                handler.postDelayed(this, MainActivity.refreshRate.longValue() * 1000);
            }
        };
        handler.postDelayed(updateTask, MainActivity.refreshRate.longValue() * 1000);

        return rootView;
    }

    public void updateMoonInfo() {
        ArrayList<String> sunInfo = new AstronomyCalculator(MainActivity.latitude, MainActivity.longitude).calculateMoonInfo();
        this.moonRiseTextView.setText(sunInfo.remove(0));
        this.moonSetTextView.setText(sunInfo.remove(0));
        this.newMoonSetTextView.setText(sunInfo.remove(0));
        this.fullMoonSetAzimuthTextView.setText(sunInfo.remove(0));
        this.moonPhaseRiseAzimuthTextView.setText(sunInfo.remove(0));
        this.synodMoonSetAzimuthTextView.setText(sunInfo.remove(0));
    }

//    public void calculateMoonInfo() {
//        AstroCalculator.Location loc = new AstroCalculator.Location(MainActivity.latitude, MainActivity.longitude);
//        Calendar mCalendar = new GregorianCalendar();
//        TimeZone mTimeZone = mCalendar.getTimeZone();
//        AstroDateTime datetime = new AstroDateTime(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH) + 1,
//                Calendar.getInstance().get(Calendar.DAY_OF_MONTH), Calendar.getInstance().get(Calendar.HOUR), Calendar.getInstance().get(Calendar.MINUTE),
//                Calendar.getInstance().get(Calendar.SECOND), getOffset(), mTimeZone.inDaylightTime(new Date()));
//        AstroCalculator calc = new AstroCalculator(datetime, loc);
//        AstroCalculator.MoonInfo moon = calc.getMoonInfo();
//        try {
//            this.moonRiseTextView.setText("Wschód: " + moon.getMoonrise().toString());
//            this.moonSetTextView.setText("Zachód: " + moon.getMoonset().toString());
//            this.newMoonSetTextView.setText("Najbliższy nów: " + moon.getNextNewMoon().toString());
//            this.fullMoonSetAzimuthTextView.setText("Pełnia: " + moon.getNextFullMoon().toString());
//            this.moonPhaseRiseAzimuthTextView.setText("Faza: " + moon.getIllumination() + "%");
//
//        } catch (NullPointerException esception) {
//            Toast.makeText(getActivity(), "Błąd AstroCalculatora!", Toast.LENGTH_SHORT).show();
//        }
//        String temp = Integer.toString(datetime.getYear()) + "-" + Integer.toString(datetime.getMonth()) + "-" + Integer.toString(datetime.getDay());
//        AstroDateTime now = moon.getNextNewMoon();
//        String temp2 = Integer.toString(now.getYear()) + "-" + Integer.toString(now.getMonth()) + "-" + Integer.toString(now.getDay());
//
//        try {
//            Date dlast = new SimpleDateFormat("yyyy-mm-dd").parse((temp));
//            Date dnow = new SimpleDateFormat("yyyy-mm-dd").parse((temp2));
//            long diff = Math.abs(dnow.getTime() - dlast.getTime());
//            long diffDays = diff / (24 * 60 * 60 * 1000);
//            this.synodMoonSetAzimuthTextView.setText("Dz. mies. syn.: " + Long.toString(diffDays));
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
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