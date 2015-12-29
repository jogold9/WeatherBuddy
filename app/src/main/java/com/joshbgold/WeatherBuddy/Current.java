package com.joshbgold.WeatherBuddy;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Current {

    private String mIcon;
    private String mSummary;
    private String mTimeZone;
    private long mTime;
    private double mTemperature;
    private double mPrecipChance;
    private double mHumidity;
    private double mWindSpeed;
    private int mWindBearing;
    private int mVisibility;
    private double mHighTemperature;
    private double mLowTemperature;
    private long mSunrise;
    private long mSunset;
    private double mMoonPhase;

    public String getMoonPhaseDescription() {

        String phaseDescription;
        if (mMoonPhase == 0) {
            phaseDescription = "new moon";
        } else if (mMoonPhase > 0 && mMoonPhase < 0.25) {
            phaseDescription = "waxing crescent";
        } else if (mMoonPhase == 0.25) {
            phaseDescription = "first quarter";
        } else if (mMoonPhase > 0.25 && mMoonPhase < 0.5) {
            phaseDescription = "waxing gibbous";
        } else if (mMoonPhase == 0.5) {
            phaseDescription = "full moon";
        } else if (mMoonPhase > 0.5 && mMoonPhase < 0.75) {
            phaseDescription = "waning gibbous";
        } else if (mMoonPhase == 0.75) {
            phaseDescription = "last quarter";
        } else if (mMoonPhase > 0.75 && mMoonPhase < 1) {
            phaseDescription = "waning crescent";
        } else phaseDescription = "--";

        return phaseDescription;
    }

    public void setMoonPhase(double moonPhase) {
        mMoonPhase = moonPhase;

    }

    public long getSunrise() {
        return mSunrise;
    }

    public String getFormattedSunriseTime(){  //converts Unix time we receive from API, to human readable time
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm");
        formatter.setTimeZone(TimeZone.getTimeZone(getTimeZone()));
        Date dateTime = new Date(getSunrise() * 1000);

        return formatter.format(dateTime);
    }

    public void setSunrise(long sunrise) {
        mSunrise = sunrise;
    }

    public long getSunset() {
        return mSunset;
    }

    public String getFormattedSunsetTime(){  //converts Unix time we receive from API, to human readable time
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm");
        formatter.setTimeZone(TimeZone.getTimeZone(getTimeZone()));
        Date dateTime = new Date(getSunset() * 1000);

        return formatter.format(dateTime);
    }

    public void setSunset(long sunset) {
        mSunset = sunset;
    }

    public double getHighTemperature() {
        return (int)Math.round(mHighTemperature);
    }

    public void setHighTemperature(double highTemperature) {
        mHighTemperature = highTemperature;
    }

    public double getLowTemperature() {
        return (int)Math.round(mLowTemperature);
    }

    public void setLowTemperature(double lowTemperature) {
        mLowTemperature = lowTemperature;
    }

    public int getVisibility() {
        return mVisibility;
    }

    public void setVisibility(int visibility) {
        mVisibility = visibility;
    }

    public int getWindSpeed() {
        return (int)Math.round(mWindSpeed);
    }

    public void setWindSpeed(double windSpeed) {
        mWindSpeed = windSpeed;
    }

    public int getWindBearing() {
        return mWindBearing;
    }

    public void setWindBearing(int windBearing) {
        mWindBearing = windBearing;
    }

    public String getTimeZone() {
        return mTimeZone;
    }

    public void setTimeZone(String timeZone) {
        mTimeZone = timeZone;
    }

    public long getTime() {
        return mTime;
    }

    public String getFormattedTime(){  //converts Unix time we receive from API, to human readable time
        SimpleDateFormat formatter = new SimpleDateFormat("h:mm a");
        formatter.setTimeZone(TimeZone.getTimeZone(getTimeZone()));
        Date dateTime = new Date(getTime() * 1000);

        return formatter.format(dateTime);
    }

    public void setTime(long time) {
        mTime = time;
    }

    public int getTemperature()
    {
        return (int)Math.round(mTemperature);
    }

    public void setTemperature(double temperature) {
        mTemperature = temperature;
    }

    public int getPrecipChance() {
        double precipPercentage = mPrecipChance * 100;
        return (int)Math.round(precipPercentage);
    }

    public void setPrecipChance(double precipChance) {
        mPrecipChance = precipChance;
    }

    public void setHumidity(double humidity) {
        mHumidity = humidity;
    }

    public int getHumidity() {
        //converting decimal humidity value to a percentage
        mHumidity = mHumidity * 100;
        return (int)Math.round(mHumidity);
    }

    public void setSummary(String summary) {
        mSummary = summary;
    }

    public String getSummary() {
        return mSummary;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }


    public int getIconId() {
        //clear-day, clear-night, rain, snow, sleet, wind, fog, cloudy, partly-cloudy-day, or partly-cloudy-night
        int iconId = R.drawable.clear_day;

        switch (mIcon) {
            case "clear-day":
                iconId = R.drawable.clear_day;
                break;
            case "clear-night":
                iconId = R.drawable.clear_night;
                break;
            case "rain":
                iconId = R.drawable.rain;
                break;
            case "snow":
                iconId = R.drawable.snow;
                break;
            case "sleet":
                iconId = R.drawable.sleet;
                break;
            case "wind":
                iconId = R.drawable.wind;
                break;
            case "fog":
                iconId = R.drawable.fog;
                break;
            case "cloudy":
                iconId = R.drawable.cloudy;
                break;
            case "partly-cloudy-day":
                iconId = R.drawable.partly_cloudy;
                break;
            case "partly-cloudy-night":
                iconId = R.drawable.cloudy_night;
                break;
        }
        return iconId;
    }

}


