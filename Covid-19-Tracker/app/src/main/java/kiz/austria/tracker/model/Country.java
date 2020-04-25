package kiz.austria.tracker.model;

public class Country extends Nation {
    public Country(String country, String cases, String deaths, String todayCases, String todayDeaths, String recovered, String active, String critical) {
        super(country, cases, deaths, todayCases, todayDeaths, recovered, active, critical);
    }

}
