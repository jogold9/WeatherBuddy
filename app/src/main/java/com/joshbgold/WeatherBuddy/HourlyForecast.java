package com.joshbgold.WeatherBuddy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.RelativeLayout;

import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class HourlyForecast extends AppCompatActivity {

    private Hour[] mHours;

    @InjectView(R.id.recyclerView)RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hourly_forecast);
        ButterKnife.inject(this);

        Intent intent = getIntent();
        Parcelable[] parcelables = intent.getParcelableArrayExtra(MainActivity.HOURLY_FORECAST);
        mHours = Arrays.copyOf(parcelables, parcelables.length, Hour[].class);

        HourAdapter adapter = new HourAdapter(this, mHours);
        mRecyclerView.setAdapter(adapter);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);

        mRecyclerView.setHasFixedSize(true);

        setBackgroundImage();
    }

    private void setBackgroundImage(){
        String weatherBackground = LoadPreferences("backgroundKey", "cloudy");
        RelativeLayout mLayout = (RelativeLayout) findViewById(R.id.hourly_forecast_layout);

        switch (weatherBackground) {
            case "clear-day":
                mLayout.setBackgroundResource(R.drawable.sunny_background);
                break;
            case "clear-night":
                mLayout.setBackgroundResource(R.drawable.clear_night_background);
                break;
            case "rain":
                mLayout.setBackgroundResource(R.drawable.rain_background);
                break;
            case "snow":
                mLayout.setBackgroundResource(R.drawable.snow_background);
                break;
            case "sleet":
                mLayout.setBackgroundResource(R.drawable.snow_background);
                break;
            case "wind":
                mLayout.setBackgroundResource(R.drawable.wind_background);
                break;
            case "fog":
                mLayout.setBackgroundResource(R.drawable.fog_background);
                break;
            case "cloudy":
                mLayout.setBackgroundResource(R.drawable.cloudy_background);
                break;
            case "partly-cloudy-day":
                mLayout.setBackgroundResource(R.drawable.partly_cloudy_background);
                break;
            case "partly-cloudy-night":
                mLayout.setBackgroundResource(R.drawable.cloudy_night_background);
                break;
            default:
                mLayout.setBackgroundResource(R.drawable.cloudy_background);
                break;
        }
    }

    //get prefs
    private String LoadPreferences(String key, String value){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return sharedPreferences.getString(key, value);
    }
}
