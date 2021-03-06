/*
 * Copyright (C) 2013 Surviving with Android (http://www.survivingwithandroid.com)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.socha.astroweather.fragments;

import com.example.socha.astroweather.GettingDataFromURL.JSONWeatherParser;
import com.example.socha.astroweather.GettingDataFromURL.WeatherHttpClient;
import com.example.socha.astroweather.MainActivity;
import com.example.socha.astroweather.R;
import com.example.socha.astroweather.adapter.DailyForecastPageAdapter;
import com.example.socha.astroweather.model.DayForecast;
import com.example.socha.astroweather.model.ForecastTemp;
import com.example.socha.astroweather.model.Weather;
import com.example.socha.astroweather.model.WeatherForecast;
import com.example.socha.astroweather.tools.FileSquire;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;

/**
 * @author Francesco
 *
 */
public class DayForecastFragment extends Fragment {
	
	public DayForecast dayForecast;
	private ImageView iconWeather;
	private MainActivity mainActivity;
	
	public DayForecastFragment() {}
	
	public void setForecast(DayForecast dayForecast) {
		
		this.dayForecast = dayForecast;
		Log.d("setForecast min", new Float(dayForecast.forecastTemp.min).toString());
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
//        if (savedInstanceState != null){
//            dayForecast = (DayForecast) savedInstanceState.getSerializable("dayForecast");
//        }
		View v = inflater.inflate(R.layout.dayforecast_fragment, container, false);
		
		TextView tempView = (TextView) v.findViewById(R.id.tempForecast);
		TextView descView = (TextView) v.findViewById(R.id.skydescForecast);
		//dayForecast.forecastTemp = new ForecastTemp();
		Log.d("min temp:", new Float(dayForecast.forecastTemp.min).toString());
        if(((MainActivity) getActivity()).infoFragment.toggleButton.isChecked())  tempView.setText( (int) ((dayForecast.forecastTemp.min + 273.15) * 9/5 - 459.67) + "-" + (int) ((dayForecast.forecastTemp.max + 273.15) * 9/5 - 459.67) + " F" );
        else tempView.setText( (int) (dayForecast.forecastTemp.min) + "-" + (int) (dayForecast.forecastTemp.max) + " C" );
		descView.setText(dayForecast.weather.currentCondition.getDescr());
		iconWeather = (ImageView) v.findViewById(R.id.forCondIcon);
		// Now we retrieve the weather icon
		Log.d("weatherIcon",dayForecast.weather.currentCondition.getIcon());
		JSONIconWeatherTask task = new JSONIconWeatherTask();
		task.execute(new String[]{dayForecast.weather.currentCondition.getIcon()});

		mainActivity = (MainActivity) getActivity();
		
		return v;
	}

	public void onSaveInstanceState(Bundle savedInstanceState) {
		super.onSaveInstanceState(savedInstanceState);

		//savedInstanceState.putSerializable("dayForecast", dayForecast);
	}

	private class JSONIconWeatherTask extends AsyncTask<String, Void, byte[]> {

		@Override
		protected byte[] doInBackground(String... params) {

			byte[] data = null;

			if(mainActivity.isOnline()) {
				try {

					// Let's retrieve the icon
					ByteArrayOutputStream stream = new ByteArrayOutputStream();
					Bitmap bmp = new WeatherHttpClient().getBitmapFromURL(params[0]);
					bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
					data = stream.toByteArray();

					new FileSquire(getContext()).saveBitmap(bmp,params[0]);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} else {
				Bitmap bmp = new FileSquire(getContext()).loadBitmap(params[0]);
				ByteArrayOutputStream stream = new ByteArrayOutputStream();
				bmp.compress(Bitmap.CompressFormat.PNG, 100, stream);
				data = stream.toByteArray();
			}


			return data;
	}

	@Override
		protected void onPostExecute(byte[] data) {
			super.onPostExecute(data);

			if (data != null) {
				Bitmap img = BitmapFactory.decodeByteArray(data, 0, data.length);
				iconWeather.setImageBitmap(img);
			}
		}



  }
}
