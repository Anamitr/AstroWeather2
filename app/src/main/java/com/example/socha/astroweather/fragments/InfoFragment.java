package com.example.socha.astroweather.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.socha.astroweather.MainActivity;
import com.example.socha.astroweather.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;


public class InfoFragment extends Fragment {
    public TextView timeTextView, longitudeTextView, latitudeTextView, refreshRateTextView, cityTextView;
    public Button btnUpdateWeather;
    public ListView citiesListView;
    public LinearLayout listViewLinearLayout;
    public ToggleButton toggleButton;

    private static Pattern pattern = Pattern.compile("\\s");
    private float scale;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_info, container, false);

        timeTextView = (TextView)rootView.findViewById(R.id.timeTextView);
        longitudeTextView = (TextView)rootView.findViewById(R.id.longitudeTextView);
        latitudeTextView = (TextView)rootView.findViewById(R.id.latitudeTextView);
        refreshRateTextView = (TextView)rootView.findViewById(R.id.refreshRateTextView);
        cityTextView = (TextView)rootView.findViewById(R.id.cityTextView);
        btnUpdateWeather = (Button)rootView.findViewById(R.id.btnUpdateWeather);
        citiesListView = (ListView)rootView.findViewById(R.id.citiesListView);
        listViewLinearLayout = (LinearLayout) rootView.findViewById(R.id.listViewLinearLayout);
        toggleButton = (ToggleButton) rootView.findViewById(R.id.toggleButton);

        //MainActivity.city = cityTextView.getText().toString();
        cityTextView.setText(MainActivity.city);

        scale = getContext().getResources().getDisplayMetrics().density;
        listViewLinearLayout.getLayoutParams().height = (int) (50 * scale + 0.5f)*MainActivity.favouriteCities.size();

        longitudeTextView.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                try {
                    String text = longitudeTextView.getText().toString();
                    if(pattern.matcher(text).find()) throw new NumberFormatException();
                    MainActivity.longitude = Double.parseDouble(text);
                    MainActivity.updateInfo();
                } catch (NumberFormatException e) {
                    Toast.makeText(getActivity(), "Nieprawidłowa liczba!", Toast.LENGTH_SHORT).show();
                    MainActivity.longitude = 0.0;
                    longitudeTextView.setText("0");
                }

            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        latitudeTextView.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                try {
                    String text = latitudeTextView.getText().toString();
                    if(pattern.matcher(text).find()) throw new NumberFormatException();
                    Double latitude = Double.parseDouble(latitudeTextView.getText().toString());
                    if(latitude > -90 && latitude < 90)
                        MainActivity.longitude = latitude;
                    else throw new NumberFormatException();
                    MainActivity.updateInfo();
                } catch (NumberFormatException e) {
                    Toast.makeText(getActivity(), "Nieprawidłowa liczba!", Toast.LENGTH_SHORT).show();
                    MainActivity.latitude = 0.0;
                    latitudeTextView.setText("0");
                }

            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        refreshRateTextView.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                try {
                    String text = refreshRateTextView.getText().toString();
                    if(pattern.matcher(text).find()) throw new NumberFormatException();
                    MainActivity.refreshRate = Double.parseDouble(text);
                    MainActivity.updateInfo();
                } catch (NumberFormatException e) {
                    Toast.makeText(getActivity(), "Nieprawidłowa liczba!", Toast.LENGTH_SHORT).show();
                    MainActivity.refreshRate = 1.0;
                    refreshRateTextView.setText("1");
                }

            }
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
        });

        Log.d("Info onCreateView","Ustawiamy btn listenera");
        btnUpdateWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newCity = cityTextView.getText().toString();
                newCity = newCity.substring(0, 1).toUpperCase() + newCity.substring(1);
                boolean fahrenheit = toggleButton.isChecked();

                if(MainActivity.cities.contains(newCity)) {
                    if(((MainActivity) getActivity()).isOnline()) {
                        if( !MainActivity.favouriteCities.contains(newCity)) {
                            MainActivity.favouriteCities.add(newCity);
                            updateListView();
                        }
                        MainActivity.city = newCity;
                        MainActivity.forecastFragment.updateWeatherForecast(fahrenheit);
                    } else {
                        if( MainActivity.favouriteCities.contains(newCity)) {
                            MainActivity.city = newCity;
                            MainActivity.forecastFragment.updateWeatherForecast(fahrenheit);
                        } else {
                            Toast.makeText(getContext(), "Disconnected, can't download new city!", Toast.LENGTH_LONG).show();
                        }
                    }

                } else {
                    Toast.makeText(getActivity(), "Nie ma takiego miasta!",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        updateListView();
        citiesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                String selectedFromList = (String) (citiesListView.getItemAtPosition(position));
                MainActivity.city = selectedFromList;
                MainActivity.forecastFragment.updateWeatherForecast(toggleButton.isChecked());
            }});

        return rootView;
    }

    public void updateListView() {
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                getActivity(),
                android.R.layout.simple_list_item_1,
                MainActivity.favouriteCities);
        citiesListView.setAdapter(arrayAdapter);
        listViewLinearLayout.getLayoutParams().height += (int) (50 * scale + 0.5f);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Handler handler=new Handler();

        final Runnable updateTask=new Runnable() {
            @Override
            public void run() {
                Calendar cal = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
                timeTextView.setText(sdf.format(cal.getTime()));
//                latitudeTextView.setText("Szer. geog. : " + MainActivity.latitude);
//                longitudeTextView.setText(MainActivity.longitude);
//                refreshRateTextView.setText("Odświeżaj co: " + MainActivity.refreshRate + "s");
                handler.postDelayed(this, 1000);
            }
        };
        handler.postDelayed(updateTask, 1000);
    }

    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
    }

    public void updateTextViews() {
        longitudeTextView.setText(MainActivity.longitude.toString());
        latitudeTextView.setText(MainActivity.latitude.toString());
        refreshRateTextView.setText(MainActivity.refreshRate.toString());
    }

}