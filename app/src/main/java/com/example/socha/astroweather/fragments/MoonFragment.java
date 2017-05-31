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
import com.example.socha.astroweather.astronomy.AstronomyCalculator;

import java.util.ArrayList;

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
}