package kiz.austria.tracker.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Nation implements Parcelable {

    private String mCountry;
    private String mConfirmed;
    private String mDeaths;
    private String mTodayCases;
    private String mTodayDeaths;
    private String mRecovered;
    private String mActive;
    private String mCritical;
    private boolean mExpanded;

    public Nation(String country, String confirmed, String deaths, String todayCases,
                  String todayDeaths, String recovered, String active, String critical) {
        mCountry = validate(country);
        mConfirmed = validate(confirmed);
        mDeaths = validate(deaths);
        mTodayCases = validate(todayCases);
        mTodayDeaths = validate(todayDeaths);
        mRecovered = validate(recovered);
        mActive = validate(active);
        mCritical = validate(critical);
        mExpanded = false;
    }

    private String validate(String value) {
        return (value.equals("null")) ? "0" : value;
    }

    protected Nation(Parcel in) {
        mCountry = in.readString();
        mConfirmed = in.readString();
        mDeaths = in.readString();
        mTodayCases = in.readString();
        mTodayDeaths = in.readString();
        mRecovered = in.readString();
        mActive = in.readString();
        mCritical = in.readString();
        mExpanded = in.readByte() != 0;
    }

    public static final Creator<Nation> CREATOR = new Creator<Nation>() {
        @Override
        public Nation createFromParcel(Parcel in) {
            return new Nation(in);
        }

        @Override
        public Nation[] newArray(int size) {
            return new Nation[size];
        }
    };

    public String getCountry() {
        return mCountry;
    }

    public String getConfirmed() {
        return mConfirmed;
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

    public boolean isExpanded() {
        return mExpanded;
    }

    public void setExpanded(boolean expanded) {
        mExpanded = expanded;
    }

    @androidx.annotation.NonNull
    @Override
    public String toString() {
        return "Countries{" +
                "mCountry='" + mCountry + '\'' +
                ", mCases='" + mConfirmed + '\'' +
                ", mDeaths='" + mDeaths + '\'' +
                ", mTodayCases='" + mTodayCases + '\'' +
                ", mTodayDeaths='" + mTodayDeaths + '\'' +
                ", mRecovered='" + mRecovered + '\'' +
                ", mActive='" + mActive + '\'' +
                ", mCritical='" + mCritical + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mCountry);
        dest.writeString(mConfirmed);
        dest.writeString(mDeaths);
        dest.writeString(mTodayCases);
        dest.writeString(mTodayDeaths);
        dest.writeString(mRecovered);
        dest.writeString(mActive);
        dest.writeString(mCritical);
        dest.writeByte((byte) (mExpanded ? 1 : 0));
    }

}
