package com.example.socha.astroweather.tools;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.example.socha.astroweather.fragments.ForecastFragment;
import com.example.socha.astroweather.model.Weather;
import com.example.socha.astroweather.model.WeatherForecast;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class FileSquire {
    Context context = null;

    public FileSquire(Context context) {
        this.context = context;
    }

    public void saveWeatherToFile(Weather weather) {
        String fileName = "weather" + weather.location.getCity();
        Log.d("Saving weather to file", fileName);
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(weather);
            os.close();
            fos.close();
            Log.d("Saving bitmap", "bitmap" + fileName);
            fos = new FileOutputStream(context.getFilesDir() + "bitmap" + fileName);
            weather.iconBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
        } catch(IOException e) {
            e.printStackTrace();
            Log.d("IOException saving", fileName);
        }
    }

    public Weather loadWeatherFromFile(String fileName) {
        fileName = "weather" + fileName;
        try {
            FileInputStream fis = context.openFileInput(fileName);
            ObjectInputStream is = new ObjectInputStream(fis);
            Weather weather = (Weather) is.readObject();
            is.close();
            fis.close();
            //Log.d("files list", getListFiles(new File()));
            weather.iconBitmap = BitmapFactory.decodeFile(context.getFilesDir() + "bitmap"+fileName);
            return weather;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("IOException loading", fileName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.d("ClassNotFoundException", fileName);
        }
        return null;
    }

    public void saveWeatherForecastToFile(WeatherForecast weatherForecast, String fileName) {
        fileName = "weatherForecast" + fileName;
        Log.d("Saving weather to file", fileName);
        try {
            FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
            ObjectOutputStream os = new ObjectOutputStream(fos);
            os.writeObject(weatherForecast);
            os.close();
            fos.close();
//            for(int i = 0; i < Integer.parseInt(ForecastFragment.forecastDaysNum); i++) {
//                fos = new FileOutputStream(context.getFilesDir() + "bitmap" + i + fileName);
//                weatherForecast.getForecast(i).weather.iconBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
//                fos.close();
//            }
        } catch(IOException e) {
            e.printStackTrace();
            Log.d("IOException saving", fileName);
        }
    }

    public WeatherForecast loadWeatherForecastFromFile(String fileName) {
        fileName = "weatherForecast" + fileName;
        try {
            FileInputStream fis = context.openFileInput(fileName);
            ObjectInputStream is = new ObjectInputStream(fis);
            WeatherForecast weatherForecast = (WeatherForecast) is.readObject();
            is.close();
            fis.close();
//            for(int i = 0; i < Integer.parseInt(ForecastFragment.forecastDaysNum); i++) {
//                weatherForecast.getForecast(i).weather.iconBitmap = BitmapFactory.decodeFile(context.getFilesDir() + "bitmap" + i + fileName);
//            }
            return weatherForecast;
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("IOException loading", fileName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            Log.d("ClassNotFoundException", fileName);
        }
        return null;
    }


}
