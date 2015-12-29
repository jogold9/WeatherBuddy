package com.joshbgold.WeatherBuddy;

import android.os.Parcel;
import android.os.Parcelable;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Day implements Parcelable{
    private long mTime;
    private String mSummary;
    private String mIcon;
    private String mTimeZone;

    private double mTemperatureMax;
    private double mTemperatureMin;
    private long mSunrise;
    private long mSunset;
    private double mMoonPhase;

    public long getTime() {
        return mTime;
    }

    public void setTime(long time) {
        mTime = time;
    }

    public String getSummary() {
        return mSummary;
    }
    public void setSummary(String summary) {
        mSummary = summary;
    }

    public int getTemperatureMax() {
        return (int)Math.round(mTemperatureMax);
    }

    public void setTemperatureMax(double temperatureMax) {
        mTemperatureMax = temperatureMax;
    }

    public double getTemperatureMin() {
        return (int)Math.round(mTemperatureMin);
    }

    public void setTemperatureMin(double temperatureMin) {
        mTemperatureMin = temperatureMin;
    }

    public String getIcon() {
        return mIcon;
    }

    public void setIcon(String icon) {
        mIcon = icon;
    }

    public String getTimeZone() {
        return mTimeZone;
    }

    public void setTimeZone(String timeZone) {
        mTimeZone = timeZone;
    }

    public int getIconId() {
        return Forecast.getIconId(mIcon);
    }

    public String getDayOfTheWeek() {
        SimpleDateFormat formatter = new SimpleDateFormat("EEEE");
        formatter.setTimeZone(TimeZone.getTimeZone(mTimeZone));
        Date dateTime = new Date(mTime * 1000);
        String dayOfTheWeek = formatter.format(dateTime);
        return dayOfTheWeek;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(mTime);
        dest.writeString(mSummary);
        dest.writeDouble(mTemperatureMax);
        dest.writeDouble(mTemperatureMin);
        dest.writeString(mIcon);
        dest.writeString(mTimeZone);
    }

    private Day(Parcel in) {
        mTime = in.readLong();
        mSummary = in.readString();
        mTemperatureMax = in.readDouble();
        mTemperatureMin = in.readDouble();
        mIcon = in.readString();
        mTimeZone = in.readString();
    }

    public Day() { }

    public static final Creator<Day> CREATOR = new Creator<Day>() {
        @Override
        public Day createFromParcel(Parcel source) {
            return new Day(source);
        }

        @Override
        public Day[] newArray(int size) {
            return new Day[size];
        }
    };

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

}
