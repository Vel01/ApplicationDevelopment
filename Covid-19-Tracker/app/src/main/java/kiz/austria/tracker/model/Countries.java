package kiz.austria.tracker.model;

public class Countries {

    private String mCountry;
    private String mCases;
    private String mDeaths;
    private String mTodayCases;
    private String mTodayDeaths;
    private String mRecovered;
    private String mActive;
    private String mCritical;

    public Countries(String country, String cases, String deaths, String todayCases, String todayDeaths, String recovered, String active, String critical) {
        mCountry = country;
        mCases = cases;
        mDeaths = deaths;
        mTodayCases = todayCases;
        mTodayDeaths = todayDeaths;
        mRecovered = recovered;
        mActive = active;
        mCritical = critical;
    }

    public String getCountry() {
        return mCountry;
    }

    public String getCases() {
        return mCases;
    }

    public String getDeaths() {
        return mDeaths;
    }

    public String getTodayCases() {
        return mTodayCases;
    }

    public String getTodayDeaths() {
        return mTodayDeaths;
    }

    public String getRecovered() {
        return mRecovered;
    }

    public String getActive() {
        return mActive;
    }

    public String getCritical() {
        return mCritical;
    }

    @androidx.annotation.NonNull
    @Override
    public String toString() {
        return "Countries{" +
                "mCountry='" + mCountry + '\'' +
                ", mCases='" + mCases + '\'' +
                ", mDeaths='" + mDeaths + '\'' +
                ", mTodayCases='" + mTodayCases + '\'' +
                ", mTodayDeaths='" + mTodayDeaths + '\'' +
                ", mRecovered='" + mRecovered + '\'' +
                ", mActive='" + mActive + '\'' +
                ", mCritical='" + mCritical + '\'' +
                '}';
    }
}
