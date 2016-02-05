package com.joshbgold.WeatherBuddy;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.widget.RelativeLayout;

/**
 * Created by JoshG on 2/4/2016.
 */
public class CityChooserActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_chooser);
        setBackgroundImage();
    }

    private void setBackgroundImage() {
        String weatherBackground = LoadPreferences("backgroundKey", "cloudy");
        RelativeLayout mLayout = (RelativeLayout) findViewById(R.id.city_chooser_layout);

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
