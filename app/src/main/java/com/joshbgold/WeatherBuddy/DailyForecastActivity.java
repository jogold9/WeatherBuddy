package com.joshbgold.WeatherBuddy;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class DailyForecastActivity extends Activity {

    private Day[] mDays;

    @InjectView(android.R.id.list) ListView mListView;
    @InjectView(android.R.id.empty)TextView mEmptyTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_forecast);
        ButterKnife.inject(this);

        Intent intent = getIntent();
        Parcelable[] parcelables = intent.getParcelableArrayExtra(MainActivity.DAILY_FORECAST);
        mDays = Arrays.copyOf(parcelables, parcelables.length, Day[].class);

        DayAdapter adapter = new DayAdapter(this, mDays);
        mListView.setAdapter(adapter);
        mListView.setEmptyView(mEmptyTextView);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                String dayOfTheWeek = mDays[position].getDayOfTheWeek();
                String conditions = mDays[position].getSummary();
                String highTemp = mDays[position].getTemperatureMax() + "";
                String message = String.format("On %s the high will be %s. %s", dayOfTheWeek,
                        highTemp, conditions);
                Toast.makeText(DailyForecastActivity.this, message, Toast.LENGTH_LONG).show();
            }
        });
        setBackgroundImage();
    }

    private void setBackgroundImage(){
        String weatherBackground = LoadPreferences("backgroundKey", "cloudy");
        RelativeLayout mLayout = (RelativeLayout) findViewById(R.id.daily_relative_layout);

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
                mLayout.setBackgroundResource(R.drawable.cloudy);
                break;
        }
    }

    //get prefs
    private String LoadPreferences(String key, String value){
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        String data = sharedPreferences.getString(key, value);
        return data;
    }
}
