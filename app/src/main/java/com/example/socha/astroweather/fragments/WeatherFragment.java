package com.example.socha.astroweather.fragments;

import org.json.JSONException;

import com.example.socha.astroweather.GettingDataFromURL.JSONWeatherParser;
import com.example.socha.astroweather.GettingDataFromURL.WeatherHttpClient;
import com.example.socha.astroweather.MainActivity;
import com.example.socha.astroweather.R;
import com.example.socha.astroweather.model.Weather;

import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v4.app.Fragment;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WeatherFragment extends Fragment {

    private TextView cityText;
    private TextView condDescr;
    private TextView temp;
    private TextView pressLab, press;
    private TextView windLab, windSpeed;
    private TextView windDeg;
    private TextView humLab, hum;
    private ImageView imgView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_weather, container, false);


        cityText = (TextView)rootView.findViewById(R.id.cityText);
        condDescr = (TextView)rootView.findViewById(R.id.condDescr);
        temp = (TextView)rootView.findViewById(R.id.temp);
        hum = (TextView)rootView.findViewById(R.id.hum);
        pressLab = (TextView)rootView.findViewById(R.id.pressLab);
        press = (TextView)rootView.findViewById(R.id.press);
        windLab = (TextView)rootView.findViewById(R.id.windLab);
        windSpeed = (TextView)rootView.findViewById(R.id.windSpeed);
        windDeg = (TextView)rootView.findViewById(R.id.windDeg);
        humLab = (TextView)rootView.findViewById(R.id.humLab);
        List<TextView> textViews = Arrays.asList(cityText, condDescr, temp, pressLab, press, windLab, windSpeed, windDeg, humLab, hum);
        for(TextView textView : textViews) {
            textView.setTextColor(Color.WHITE);
        }

        imgView = (ImageView)rootView.findViewById(R.id.condIcon);

        updateWeather();

        return rootView;
    }

    public void updateWeather() {
        //String city = "Lodz";
        JSONWeatherTask task = new JSONWeatherTask();
        task.execute(new String[]{MainActivity.city});
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }


    private class JSONWeatherTask extends AsyncTask<String, Void, Weather> {

        @Override
        protected Weather doInBackground(String... params) {
            Weather weather = new Weather();
            Log.d("params: ",params[0]);
            String data = ( (new WeatherHttpClient()).getWeatherData(params[0]));

            try {
                weather = JSONWeatherParser.getWeather(data);

                // Let's retrieve the icon
                //weather.iconData = ( (new WeatherHttpClient()).getImage(weather.currentCondition.getIcon()));
                weather.iconBitmap = new WeatherHttpClient().getBitmapFromURL(weather.currentCondition.getIcon());

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return weather;

        }


        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);

            if (weather.iconBitmap != null) {
                //Bitmap img = BitmapFactory.decodeByteArray(weather.iconData, 0, weather.iconData.length);
                //imgView.setImageBitmap(img);
                imgView.setImageBitmap(weather.iconBitmap);
            }

            cityText.setText(weather.location.getCity() + "," + weather.location.getCountry());
            condDescr.setText(weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescr() + ")");
            temp.setText("" + Math.round((weather.temperature.getTemp() - 273.15)) + " C");
            hum.setText("" + weather.currentCondition.getHumidity() + "%");
            press.setText("" + weather.currentCondition.getPressure() + " hPa");
            windSpeed.setText("" + weather.wind.getSpeed() + " mps");
            windDeg.setText("" + weather.wind.getDeg() + " stopni");
        }
    }
}
