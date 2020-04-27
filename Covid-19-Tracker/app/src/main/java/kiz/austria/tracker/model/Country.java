package kiz.austria.tracker.model;

import android.os.Parcelable;

public class Country extends Nation implements Parcelable {
    public Country(String country, String cases, String deaths, String todayCases, String todayDeaths, String recovered, String active, String critical) {
        super(country, cases, deaths, todayCases, todayDeaths, recovered, active, critical);
    }

}
