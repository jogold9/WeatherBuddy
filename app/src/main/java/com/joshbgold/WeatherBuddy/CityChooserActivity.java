package com.joshbgold.WeatherBuddy;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.ArrayList;

import butterknife.ButterKnife;

import static com.joshbgold.WeatherBuddy.MainActivity.citiesList;

/**
 * Created by JoshG on 2/4/2016.
 */
public class CityChooserActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.city_chooser);
        ImageView clear_button = (ImageView) findViewById(R.id.clear_button);
        clear_button.setOnClickListener(clearButtonClickListener);

        setBackgroundImage();

        LinearLayout layout = (LinearLayout) findViewById(R.id.city_chooser_layout);
        RadioGroup radioGroup = new RadioGroup(this);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        //layoutParams.addRule(LinearLayout.CENTER_IN_PARENT);
        layout.addView(radioGroup, layoutParams);

        RadioButton radioButtonView[] = new RadioButton[10];

        // for testing to see what cities are in the cities list
        /*for (int i = 0; i < citiesList.size(); i++) {
            Toast.makeText(CityChooserActivity.this, "City: " + citiesList.get(i), Toast.LENGTH_SHORT).show();
        }*/

        //citiesList = loadPrefsArray();

        for (int i = 0; i < citiesList.size() && i < 10; i++) {
            radioButtonView[i] = new RadioButton(this);
            radioButtonView[i].setText(citiesList.get(i).toString());
            radioButtonView[i].setTextSize(24);
            radioButtonView[i].setTextColor(ContextCompat.getColor(CityChooserActivity.this, R.color.white));
            radioButtonView[i].setButtonDrawable(R.drawable.ic_radio_button);
            radioButtonView[i].setOnClickListener(radioButtonClickListener);
            radioGroup.addView(radioButtonView[i], layoutParams);
        }

        layout.removeAllViews();
        layout.addView(clear_button);
        layout.addView(radioGroup, layoutParams);

        setBackgroundImage();

    }

   private View.OnClickListener clearButtonClickListener = (new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            //remove last city in citiesList from the array
            citiesList.remove(citiesList.size() - 1);

            //save new citiesList to prefs
            savePrefsArray(citiesList);

            //redraw the screen by restarting the CityChooserActivity
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
    });

    private View.OnClickListener radioButtonClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            String radioButtonName = ((RadioButton) view).getText().toString();
            //Toast.makeText(CityChooserActivity.this, "This radio button is named: " + radioButtonName, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(CityChooserActivity.this, MainActivity.class);
           // intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.putExtra("radioButtonCity", radioButtonName);
            startActivity(intent);
        }
    };

    private void setBackgroundImage() {
        String weatherBackground = LoadPreferences("backgroundKey", "cloudy");
        LinearLayout mLayout = (LinearLayout) findViewById(R.id.city_chooser_layout);

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
    private String LoadPreferences(String key, String value) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        return sharedPreferences.getString(key, value);
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

    //save prefs for string arrays / Arraylists
    public void savePrefsArray(ArrayList arrayList) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("array_size", arrayList.size());
        for (int i = 0; i < arrayList.size(); i++)
            editor.putString("array_" + i, arrayList.get(i).toString());
        editor.apply();
    }

}
