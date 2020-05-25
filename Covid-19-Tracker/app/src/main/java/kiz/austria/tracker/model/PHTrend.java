package kiz.austria.tracker.model;

import kiz.austria.tracker.util.TrackerUtility;

public class PHTrend {

    private String mCountry;
    private String mInfected;
    private String mTested;
    private String mRecovered;
    private String mDeceased;
    private String mPUI;
    private String mPUM;
    private String mLatestUpdate;

    public PHTrend(String country, String infected, String tested, String recovered, String deceased, String PUI, String PUM, String latestUpdate) {
        mCountry = country;
        mInfected = infected;
        mTested = tested;
        mRecovered = recovered;
        mDeceased = deceased;
        mPUI = PUI;
        mPUM = PUM;
        setLatestUpdate(latestUpdate);
    }

    public String getCountry() {
        return mCountry;
    }

    public String getInfected() {
        return mInfected;
    }

    public String getTested() {
        return mTested;
    }

    public String getRecovered() {
        return mRecovered;
    }

    public String getDeceased() {
        return mDeceased;
    }

    public String getPUI() {
        return mPUI;
    }

    public String getPUM() {
        return mPUM;
    }

    public String getLatestUpdate() {
        return mLatestUpdate;
    }

    private void setLatestUpdate(String latestUpdate) {
        if (latestUpdate != null && !latestUpdate.equals("N/A")) mLatestUpdate = latestUpdate;
        else mLatestUpdate = TrackerUtility.getCurrentDate();
    }
}
