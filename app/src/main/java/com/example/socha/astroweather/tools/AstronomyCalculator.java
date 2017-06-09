package com.example.socha.astroweather.tools;

import android.util.Log;

import com.astrocalculator.AstroCalculator;
import com.astrocalculator.AstroDateTime;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * Created by socha on 04.05.2017.
 */

public class AstronomyCalculator {
    private AstroCalculator astroCalculator;
    private AstroCalculator.Location loc;
    private  AstroDateTime datetime;
    //public static Double currentLongitude, currentLatitude, currenRefreshRate;

    public AstronomyCalculator(Double latitude, Double longitude) {
        Calendar mCalendar = new GregorianCalendar();
        TimeZone mTimeZone = mCalendar.getTimeZone();
        datetime = new AstroDateTime(Calendar.getInstance().get(Calendar.YEAR), Calendar.getInstance().get(Calendar.MONTH) + 1,
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH), Calendar.getInstance().get(Calendar.HOUR), Calendar.getInstance().get(Calendar.MINUTE),
                Calendar.getInstance().get(Calendar.SECOND), getOffset(), mTimeZone.inDaylightTime(new Date()));
        loc = new AstroCalculator.Location(latitude, longitude);
        astroCalculator = new AstroCalculator(datetime, loc);
    }

    public ArrayList<String> calculateSunInfo() {
        ArrayList<String> results = new ArrayList<String>();
        AstroCalculator.SunInfo sun = astroCalculator.getSunInfo();
        results.add("Wschód: " + sun.getSunrise().toString());
        results.add("Azymut: " + Double.toString(sun.getAzimuthRise()));
        results.add("Zachód: " + sun.getSunset().toString());
        results.add("Azymut: " + Double.toString(sun.getAzimuthSet()));
        AstroDateTime datetime2 = sun.getTwilightEvening();
        String temp = Integer.toString(datetime2.getHour()) + ":" + Integer.toString(datetime2.getMinute());
        AstroDateTime datetime3 = sun.getTwilightMorning();
        String temp2 = Integer.toString(datetime3.getHour()) + ":" + Integer.toString(datetime3.getMinute());
        results.add("Zachód cyw.: " + temp);
        results.add("Wschód cyw.: " + temp2);
        return results;
    }

    public ArrayList<String> calculateMoonInfo() {
        ArrayList<String> results = new ArrayList<String>();
        AstroCalculator.MoonInfo moon = astroCalculator.getMoonInfo();
        try {
            results.add("Wschód: " + moon.getMoonrise().toString());
            results.add("Zachód: " + moon.getMoonset().toString());
            results.add("Najbliższy nów: " + moon.getNextNewMoon().toString());
            results.add("Pełnia: " + moon.getNextFullMoon().toString());
            results.add("Faza: " + moon.getIllumination() + "%");

        } catch (NullPointerException esception) {
            Log.d("AstroCalcualtor Error!","AstroCalcualtor cant calculate for these arguments");
        }
        String temp = Integer.toString(datetime.getYear()) + "-" + Integer.toString(datetime.getMonth()) + "-" + Integer.toString(datetime.getDay());
        AstroDateTime now = moon.getNextNewMoon();
        String temp2 = Integer.toString(now.getYear()) + "-" + Integer.toString(now.getMonth()) + "-" + Integer.toString(now.getDay());

        try {
            Date dlast = new SimpleDateFormat("yyyy-mm-dd").parse((temp));
            Date dnow = new SimpleDateFormat("yyyy-mm-dd").parse((temp2));
            long diff = Math.abs(dnow.getTime() - dlast.getTime());
            long diffDays = diff / (24 * 60 * 60 * 1000);
            results.add("Dz. mies. syn.: " + Long.toString(diffDays));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return results;
    }

    public static int getOffset(){
        TimeZone timezone = TimeZone.getDefault();
        int seconds = timezone.getOffset(Calendar.ZONE_OFFSET)/1000;
        double minutes = seconds/60;
        double hours = minutes/60;
        int finalHours = (int) hours;
        return finalHours;
    }

}