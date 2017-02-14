package com.joshbgold.WeatherBuddy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.okhttp.Call;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import org.apache.commons.lang3.text.WordUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String HOURLY_FORECAST = "HOURLY_FORECAST";
    public static final String DAILY_FORECAST = "DAILY_FORECAST";

    protected static ArrayList citiesList = new ArrayList();
    private Forecast mForecast;
    private Current mCurrentWeather;
    private Day[] mDailyWeather;
    private double cityLatitude;
    private double cityLongitude;
    private String userInputCity;  //city that the user has typed in

    @InjectView(R.id.extendedForecast)
    ImageView extendedForecastButton;
    @InjectView(R.id.hourlyButton)
    ImageView hourlyButton;
    @InjectView(R.id.locationLabel)
    TextView mLocationLabel;
    @InjectView(R.id.timeLabel)
    TextView mTimeLabel;
    @InjectView(R.id.temperatureLabel)
    TextView mTemperatureLabel;
    @InjectView(R.id.humidityValue)
    TextView mHumidityValue;
    @InjectView(R.id.precipValue)
    TextView mPrecipValue;
    @InjectView(R.id.summaryLabel)
    TextView mSummaryLabel;
    @InjectView(R.id.iconImageView)
    ImageView mIconImageView;
    @InjectView(R.id.refreshImageView)
    ImageView mRefreshImageView;
    @InjectView(R.id.cities_icon)
    ImageView citiesButton;
    @InjectView(R.id.windValue)
    TextView mWindValue;
    @InjectView(R.id.highTempValue)
    TextView mHighTemperature;
    @InjectView(R.id.lowTempValue)
    TextView mLowTemperature;
    @InjectView(R.id.visibilityValue)
    TextView mVisibility;
    @InjectView(R.id.moonValue)
    TextView mPhaseDescription;
    @InjectView(R.id.sunriseValue)
    TextView mSunrise;
    @InjectView(R.id.sunsetValue)
    TextView mSunset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);

        final InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        citiesList = loadPrefsArray();

     /*   mProgressBar.setVisibility(View.INVISIBLE);*/

        userInputCity = loadPrefs("userInputCity", "Portland");
        mLocationLabel.setText(userInputCity);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            userInputCity = extras.getString("radioButtonCity");
            mLocationLabel.setText(userInputCity);
        }

        //hide the keyboard
        mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        //look up city lat a& long, get forecast, add to arrayList if not a duplicate entry
        try {
            getCoordinatesForCity(userInputCity);
            getForecast(cityLatitude, cityLongitude);
            AddInputCity(userInputCity);
        } catch (IOException e) {
            Toast.makeText(MainActivity.this, "Could not find this city name.  Make sure you have a network connection, " +
                    "and verify spelling of the city name.", Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }

        //open hourly forecast layout
        hourlyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HourlyForecast.class);
                intent.putExtra(HOURLY_FORECAST, mForecast.getHourlyForecast());
                startActivity(intent);
            }
        });

        //open extended forecast layout
        View.OnClickListener extendedForecast = (new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DailyForecastActivity.class);
                intent.putExtra(DAILY_FORECAST, mForecast.getDailyForecast());
                startActivity(intent);
            }
        });

        //open CityChooserActivity page to select a city from your saved list of cities
        citiesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, CityChooserActivity.class);
                startActivity(intent);
            }
        });

        //refreshes the current forecast
        mRefreshImageView.setOnClickListener(new View.OnClickListener() {
            boolean isValidCity = true;  //used to tell if this is a location worth saving

            @Override
            public void onClick(View v) {
                userInputCity = mLocationLabel.getText().toString().toLowerCase();
                //AddInputCity(userInputCity); //is this a duplicate add to the add on line 185?

                //hide the keyboard
                mInputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

                try {
                    getCoordinatesForCity(userInputCity);
                    getForecast(cityLatitude, cityLongitude);
                } catch (IOException e) {
                    Toast.makeText(MainActivity.this, "Could not find this city name.  Make sure you have a network connection, " +
                            "and verify spelling of the city name.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                    isValidCity = false;
                }

                if (isValidCity) {
                    WordUtils.capitalize(userInputCity);
                    //store the city for later use in arraylist and in preferences
                    AddInputCity(userInputCity);
                }

            }
        });

        extendedForecastButton.setOnClickListener(extendedForecast);
    }

    private void AddInputCity(String userInputCity) {
        if (!citiesList.contains(userInputCity)) {  //this city is not a duplicate
            citiesList.add(userInputCity);
            savePrefs("userInputCity", userInputCity);
            savePrefsArray(citiesList);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        savePrefs("userInputCity", userInputCity);
        savePrefsArray(citiesList);
    }

    private void getForecast(double latitude, double longitude) {

        String forecastURL = "https://api.forecast.io/forecast/" + Key.ApiKey + "/" + latitude + "," + longitude;

        if (isNetworkAvailable()) {

            toggleRefresh();

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(forecastURL)
                    .build();

            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });
                    alertUserAboutError();
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            toggleRefresh();
                        }
                    });

                    try {
                        String jsonData = response.body().string();
                        Log.v(TAG, jsonData);
                        if (response.isSuccessful()) {
                            mForecast = parseForecastDetails(jsonData);
                            mCurrentWeather = getCurrentDetails(jsonData);
                            mDailyWeather = getDailyForecast(jsonData);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    updateDisplay();
                                }

                            });

                            Log.v(TAG, response.body().string());
                        } else {
                            alertUserAboutError();
                        }
                    } catch (IOException e) {
                        Log.e(TAG, "Exception caught: ", e);
                    } catch (JSONException e) {
                        Log.e(TAG, "JSON Exception caught: ", e);
                    }

                }
            });
        }
    }

    private void toggleRefresh() {
       /* if (mProgressBar.getVisibility() == View.INVISIBLE) {
            mProgressBar.setVisibility(View.VISIBLE);
            mRefreshImageView.setVisibility(View.INVISIBLE);
        } else {
            mProgressBar.setVisibility(View.INVISIBLE);
            mRefreshImageView.setVisibility(View.VISIBLE);
        }*/
    }

    private void updateDisplay() {
     /*   Current current = mForecast.getCurrent();*/

        mTemperatureLabel.setText(mCurrentWeather.getTemperature() + "");
        mTimeLabel.setText("At " + mCurrentWeather.getFormattedTime() + " it is");
        mHumidityValue.setText(mCurrentWeather.getHumidity() + "%");
        mPrecipValue.setText(mCurrentWeather.getPrecipChance() + "%");
        mWindValue.setText(mCurrentWeather.getWindSpeed() + " MPH");
        mHighTemperature.setText(mDailyWeather[0].getTemperatureMax() + "");
        mLowTemperature.setText(mDailyWeather[0].getTemperatureMin() + "");
        mSummaryLabel.setText(mCurrentWeather.getSummary());
        mPhaseDescription.setText(mDailyWeather[0].getMoonPhaseDescription());
        mSunrise.setText(mDailyWeather[0].getFormattedSunriseTime());
        mSunset.setText(mDailyWeather[0].getFormattedSunsetTime());

        try {
            mVisibility.setText(mCurrentWeather.getVisibility() + " mi"); //this may have been throwing JSON exception as visibility could not be retrieved for some reason
        } catch (Exception e) {
            e.printStackTrace();
        }

        //set the appropriate weather icon based on the forecast
        Drawable drawable = ContextCompat.getDrawable(this, mCurrentWeather.getIconId());
        mIconImageView.setImageDrawable(drawable);
        setBackgroundImage();

    }

    //sets the appropriate weather background image based on the forecast
    private void setBackgroundImage() {
        RelativeLayout mLayout = (RelativeLayout) findViewById(R.id.weatherBuddyLayout);

        switch (mCurrentWeather.getIcon()) {
            case "clear-day":
                mLayout.setBackgroundResource(R.drawable.sunny_background);
                savePrefs("backgroundKey", "clear-day");
                break;
            case "clear-night":
                mLayout.setBackgroundResource(R.drawable.clear_night_background);
                savePrefs("backgroundKey", "clear-night");
                break;
            case "rain":
                mLayout.setBackgroundResource(R.drawable.rain_background);
                savePrefs("backgroundKey", "rain");
                break;
            case "snow":
                mLayout.setBackgroundResource(R.drawable.snow_background);
                savePrefs("backgroundKey", "snow");
                break;
            case "sleet":
                mLayout.setBackgroundResource(R.drawable.snow_background);
                savePrefs("backgroundKey", "sleet");
                break;
            case "wind":
                mLayout.setBackgroundResource(R.drawable.wind_background);
                savePrefs("backgroundKey", "wind");
                break;
            case "fog":
                mLayout.setBackgroundResource(R.drawable.fog_background);
                savePrefs("backgroundKey", "fog");
                break;
            case "cloudy":
                mLayout.setBackgroundResource(R.drawable.cloudy_background);
                savePrefs("backgroundKey", "cloudy");
                break;
            case "partly-cloudy-day":
                mLayout.setBackgroundResource(R.drawable.partly_cloudy_background);
                savePrefs("backgroundKey", "partly-cloudy-day");
                break;
            case "partly-cloudy-night":
                mLayout.setBackgroundResource(R.drawable.cloudy_night_background);
                savePrefs("backgroundKey", "partly-cloudy-night");
                break;
            default:
                mLayout.setBackgroundResource(R.drawable.cloudy);
                savePrefs("backgroundKey", "cloudy");
                break;
        }
    }

    private Current getCurrentDetails(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        Log.i(TAG, "From JSON: " + timezone);

        JSONObject currently = forecast.getJSONObject("currently");

        Current currentWeather = new Current();

        currentWeather.setHumidity(currently.getDouble("humidity"));
        currentWeather.setTime(currently.getLong("time"));
        currentWeather.setIcon(currently.getString("icon"));
        currentWeather.setPrecipChance(currently.getDouble("precipProbability"));
        currentWeather.setSummary(currently.getString("summary"));
        currentWeather.setTemperature(currently.getDouble("temperature"));
        currentWeather.setTimeZone(timezone);
        currentWeather.setWindSpeed(currently.getDouble("windSpeed"));
        currentWeather.setWindBearing(currently.getInt("windBearing"));
        try {   //this may have been throwing JSON exception as visibility could not be retrieved for some reason
            currentWeather.setVisibility(currently.getInt("visibility"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.d(TAG, currentWeather.getFormattedTime());
        Log.d(TAG, String.valueOf(currentWeather.getWindSpeed()) + " MPH");
        Log.d(TAG, String.valueOf(currentWeather.getWindBearing()) + " degrees");

        return currentWeather;
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager manager = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        boolean isAvailable = false;
        if (networkInfo != null && networkInfo.isConnected()) {
            isAvailable = true;
        } else {
            Toast.makeText(this, getString(R.string.network_unavailable_message),
                    Toast.LENGTH_LONG).show();
        }
        return isAvailable;
    }

    private void alertUserAboutError() {
        AlertDialogFragment dialog = new AlertDialogFragment();
        dialog.show(getFragmentManager(), "error_dialog");
    }

    private Forecast parseForecastDetails(String jsonData) throws JSONException {
        Forecast forecast = new Forecast();

        forecast.setCurrent(getCurrentDetails(jsonData));
        forecast.setHourlyForecast(getHourlyForecast(jsonData));
        forecast.setDailyForecast(getDailyForecast(jsonData));

        return forecast;
    }

    private Hour[] getHourlyForecast(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");
        JSONObject hourly = forecast.getJSONObject("hourly");
        JSONArray data = hourly.getJSONArray("data");

        Hour[] hours = new Hour[data.length()];

        for (int i = 0; i < data.length(); i++) {
            JSONObject jsonHour = data.getJSONObject(i);
            Hour hour = new Hour();

            hour.setSummary(jsonHour.getString("summary"));
            hour.setIcon(jsonHour.getString("icon"));
            hour.setTemperature(jsonHour.getDouble("temperature"));
            hour.setTime(jsonHour.getLong("time"));
            hour.setTimezone(timezone);

            hours[i] = hour;
        }

        return hours;
    }

    private Day[] getDailyForecast(String jsonData) throws JSONException {
        JSONObject forecast = new JSONObject(jsonData);
        String timezone = forecast.getString("timezone");

        JSONObject daily = forecast.getJSONObject("daily");
        JSONArray data = daily.getJSONArray("data");

        Day[] mDailyWeather = new Day[data.length()];

        for (int i = 0; i < data.length(); i++) {
            JSONObject jsonDay = data.getJSONObject(i);
            Day day = new Day();
            day.setTimeZone(timezone);
            day.setTemperatureMax(jsonDay.getDouble("temperatureMax"));
            day.setTemperatureMin(jsonDay.getDouble("temperatureMin"));
            day.setMoonPhase(jsonDay.getDouble("moonPhase"));
            day.setSunrise(jsonDay.getLong("sunriseTime"));
            day.setSunset(jsonDay.getLong("sunsetTime"));
            day.setTime(jsonDay.getLong("time"));
            day.setSummary(jsonDay.getString("summary"));
            day.setIcon(jsonDay.getString("icon"));
            mDailyWeather[i] = day;

        }
        return mDailyWeather;
    }

    //save prefs for strings
    public void savePrefs(String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    //get prefs for strings
    public String loadPrefs(String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return sharedPreferences.getString(key, value);
    }

    //save prefs for string arrays / Arraylists
    public void savePrefsArray(ArrayList arrayList) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("array_size", arrayList.size());
        for (int i = 0; i < arrayList.size(); i++)
            editor.putString("array_" + i, arrayList.get(i).toString());
        editor.apply();
    }

    //get prefs for string array / Arraylists
    public ArrayList<String> loadPrefsArray() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        ArrayList<String> arrayList = new ArrayList<String>(10);
        String temporaryCitiesString = "";
        int size = sharedPreferences.getInt("array_size", 0);
        for (int i = 0; i < size; i++) {
            temporaryCitiesString = (sharedPreferences.getString("array_" + i, null));
            arrayList.add(temporaryCitiesString);
        }
        return arrayList;
    }

    public void getCoordinatesForCity(String cityString) throws IOException {
        Geocoder geocoder = new Geocoder(this);

        List<Address> addressList = geocoder.getFromLocationName(cityString, 1);

        if (addressList != null && addressList.size() > 0) {
            Address address = addressList.get(0);
            cityLatitude = address.getLatitude();
            cityLongitude = address.getLongitude();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.action_bar, menu);

 /*       for(int i = 0; i < menu.size(); i++) {
            MenuItem item = menu.getItem(i);
            SpannableString spanString = new SpannableString(menu.getItem(i).getTitle().toString());
            spanString.setSpan(new ForegroundColorSpan(Color.WHITE), 0, spanString.length(), 0); //fix the color to white
            item.setTitle(spanString);
        }*/

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_saved_cities:
                //launch settings activity
                Intent intent = new Intent(this, CityChooserActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
