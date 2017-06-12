package com.example.socha.astroweather.fragments;

import com.example.socha.astroweather.GettingDataFromURL.JSONWeatherParser;
import com.example.socha.astroweather.GettingDataFromURL.WeatherHttpClient;
import com.example.socha.astroweather.MainActivity;
import com.example.socha.astroweather.R;
import com.example.socha.astroweather.adapter.DailyForecastPageAdapter;
//import com.example.socha.astroweather.fragments.WeatherFragment;
import com.example.socha.astroweather.model.Weather;
import com.example.socha.astroweather.model.WeatherForecast;
import com.example.socha.astroweather.tools.FileSquire;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.util.Calendar;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.support.v4.app.Fragment;
import android.widget.Toast;

import org.json.JSONException;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Date;

public class ForecastFragment extends Fragment {

    private TextView cityText;
    private TextView condDescr;
    private TextView temp;
    private TextView press;
    private TextView windSpeed;
    private TextView windDeg;
    private TextView unitTemp;

    private TextView hum;
    private ImageView imgView;

    public static final String forecastDaysNum = "5";
    private ViewPager pager;

    private MainActivity mainActivity;
    private Context context;

    private boolean fahrenheit = false;

    Date currentDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_forecast, container, false);
//        String city = "Warszawa";
//        String lang = "pl";

        cityText = (TextView) rootView.findViewById(R.id.cityText);
        temp = (TextView) rootView.findViewById(R.id.temp);
//        unitTemp = (TextView) findViewById(R.id.unittemp);
//        unitTemp.setText(" C");
        condDescr = (TextView) rootView.findViewById(R.id.skydesc);

        pager = (ViewPager) rootView.findViewById(R.id.forecastPager);
        imgView = (ImageView) rootView.findViewById(R.id.condIcon);

		condDescr = (TextView) rootView.findViewById(R.id.condDescr);

		hum = (TextView) rootView.findViewById(R.id.hum);
		press = (TextView) rootView.findViewById(R.id.press);
		windSpeed = (TextView) rootView.findViewById(R.id.windSpeed);
		windDeg = (TextView) rootView.findViewById(R.id.windDeg);

        //updateWeatherForecast();
        mainActivity = (MainActivity) getActivity();
        context = getContext();
        currentDate = new Date();

        return rootView;
    }

    public void updateWeatherForecast(boolean fahrenheit) {
        String city = MainActivity.city;
        String lang = "pl";

        cityText.setText(city);

        if(!mainActivity.isOnline()) {
            Toast.makeText(context, "Disconnected, info may be outdated!", Toast.LENGTH_SHORT).show();
        }

        this.fahrenheit = fahrenheit;

        JSONWeatherTask task = new JSONWeatherTask();
        task.execute(new String[]{city,lang});

        JSONForecastWeatherTask task1 = new JSONForecastWeatherTask();
        task1.execute(new String[]{city,lang, forecastDaysNum});
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    private class JSONWeatherTask extends AsyncTask<String, Void, Weather> {

        @Override
        protected Weather doInBackground(String... params) {
            Log.d("JSONWeatherTask","");

            Weather weather = new Weather();

            if(DateUtils.isToday(new FileSquire(context).checkFileDate("weather" + params[0])) || !mainActivity.isOnline()) {
                Log.d("Disconn or uptodate","Reading from file");
                Log.d("Time", new Float(new FileSquire(context).checkFileDate("weather" + params[0])).toString() + ", " + new Date());
                weather = new FileSquire(context).loadWeatherFromFile(params[0]);
            }
            else {
                try {
                    String data = ( (new WeatherHttpClient()).getWeatherData(params[0],params[1]));//, params[1]));
                    weather = JSONWeatherParser.getWeather(data);

                    System.out.println("Weather ["+weather+"]");
                    // Let's retrieve the icon
                    weather.iconBitmap = ( (new WeatherHttpClient()).getBitmapFromURL(weather.currentCondition.getIcon()));
                    new FileSquire(context).saveWeatherToFile(weather);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return weather;
        }

        @Override
        protected void onPostExecute(Weather weather) {
            super.onPostExecute(weather);

            if (weather.iconBitmap != null) {
                //Bitmap img = BitmapFactory.decodeByteArray(weather.iconData, 0, weather.iconData.length);
                imgView.setImageBitmap(weather.iconBitmap);
            }

            if(weather.location != null && weather.location.getCity() != null && weather.location.getCountry() != null) cityText.setText(weather.location.getCity() + "," + weather.location.getCountry());
            temp.setText("" + Math.round((weather.temperature.getTemp() - 275.15)));
            condDescr.setText(weather.currentCondition.getCondition() + "(" + weather.currentCondition.getDescr() + ")");
            if(fahrenheit) temp.setText("" + Math.round((weather.temperature.getTemp())) + " F");
            else temp.setText("" + Math.round((weather.temperature.getTemp() - 275.15)) + " C");
			hum.setText("" + weather.currentCondition.getHumidity() + "%");
			press.setText("" + weather.currentCondition.getPressure() + " hPa");
			windSpeed.setText("" + weather.wind.getSpeed() + " mps");
			windDeg.setText("" + weather.wind.getDeg() + " stopni");
        }
    }

    private class JSONForecastWeatherTask extends AsyncTask<String, Void, WeatherForecast> {

        @Override
        protected WeatherForecast doInBackground(String... params) {
            Log.d("JSONForecastWeatherTask","");
            WeatherForecast weatherForecast = new WeatherForecast();
            if(DateUtils.isToday(new FileSquire(context).checkFileDate("weatherForecast" + params[0])) || !mainActivity.isOnline()) {
                weatherForecast = new FileSquire(context).loadWeatherForecastFromFile(params[0]);
            } else {
                try {
                    String data = ( (new WeatherHttpClient()).getForecastWeatherData(params[0], params[1], params[2]));
                    weatherForecast = JSONWeatherParser.getForecastWeather(data);
                    new FileSquire(context).saveWeatherForecastToFile(weatherForecast,params[0]);
//                    weatherForecast = null;
//                    weatherForecast = loadWeatherForecastFromFile(params[0]);
                    System.out.println("Weather ["+weatherForecast+"]");
                    // Let's retrieve the icon
                    //weather.iconData = ( (new WeatherHttpClient()).getImage(weather.currentCondition.getIcon()));

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            //Log.d("jsontask", new Float(weatherForecast.getForecast(0).forecastTemp.min).toString());
            return weatherForecast;
        }

        @Override
        protected void onPostExecute(WeatherForecast forecastWeather) {
            super.onPostExecute(forecastWeather);
            DailyForecastPageAdapter adapter = new DailyForecastPageAdapter(Integer.parseInt(forecastDaysNum), getFragmentManager(), forecastWeather);
            pager.setAdapter(adapter);
        }
    }


}
