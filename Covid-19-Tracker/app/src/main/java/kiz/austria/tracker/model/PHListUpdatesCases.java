package kiz.austria.tracker.model;

import android.os.Parcel;
import android.os.Parcelable;

import kiz.austria.tracker.util.TrackerUtility;

public class PHListUpdatesCases implements Parcelable {

    private String mCountry;
    private String mInfected;
    private String mTested;
    private String mRecovered;
    private String mDeceased;
    private String mPUI;
    private String mPUM;
    private String mLatestUpdate;

    public static final Creator<PHListUpdatesCases> CREATOR = new Creator<PHListUpdatesCases>() {
        @Override
        public PHListUpdatesCases createFromParcel(Parcel in) {
            return new PHListUpdatesCases(in);
        }

        @Override
        public PHListUpdatesCases[] newArray(int size) {
            return new PHListUpdatesCases[size];
        }
    };

    public PHListUpdatesCases() {
        this("N/A", "0", "0", "0", "0", "0", "0", TrackerUtility.getCurrentDate());
    }

    public PHListUpdatesCases(String latestUpdate) {
        this("N/A", "0", "0", "0", "0", "0", "0", latestUpdate);
    }

    public PHListUpdatesCases(String tested, String PUI, String PUM) {
        this("N/A", "0", tested, "0", "0", PUI, PUM, TrackerUtility.getCurrentDate());
    }

    public PHListUpdatesCases(String infected, String recovered, String deceased, String latestUpdate) {
        this("N/A", infected, "0", recovered, deceased, "0", "0", latestUpdate);
    }

    public PHListUpdatesCases(String country, String infected, String tested, String recovered, String deceased, String PUI, String PUM, String latestUpdate) {
        mCountry = country;
        mInfected = infected;
        mTested = tested;
        mRecovered = recovered;
        mDeceased = deceased;
        mPUI = PUI;
        mPUM = PUM;
        setLatestUpdate(latestUpdate);
    }

    protected PHListUpdatesCases(Parcel in) {
        mCountry = in.readString();
        mInfected = in.readString();
        mTested = in.readString();
        mRecovered = in.readString();
        mDeceased = in.readString();
        mPUI = in.readString();
        mPUM = in.readString();
        mLatestUpdate = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mCountry);
        dest.writeString(mInfected);
        dest.writeString(mTested);
        dest.writeString(mRecovered);
        dest.writeString(mDeceased);
        dest.writeString(mPUI);
        dest.writeString(mPUM);
        dest.writeString(mLatestUpdate);
    }

    @Override
    public String toString() {
        return "PHListUpdatesCases{" +
                "mCountry='" + mCountry + '\'' +
                ", mInfected='" + mInfected + '\'' +
                ", mTested='" + mTested + '\'' +
                ", mRecovered='" + mRecovered + '\'' +
                ", mDeceased='" + mDeceased + '\'' +
                ", mPUI='" + mPUI + '\'' +
                ", mPUM='" + mPUM + '\'' +
                ", mLatestUpdate='" + mLatestUpdate + '\'' +
                '}';
    }
}
