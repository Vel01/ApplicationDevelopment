package kiz.austria.tracker.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Nation implements Parcelable {

    private String mCountry;
    private String mCases;
    private String mDeaths;
    private String mTodayCases;
    private String mTodayDeaths;
    private String mRecovered;
    private String mActive;
    private String mCritical;

    public Nation(String country, String cases, String deaths, String todayCases,
                  String todayDeaths, String recovered, String active, String critical) {
        mCountry = validate(country);
        mCases = validate(cases);
        mDeaths = validate(deaths);
        mTodayCases = validate(todayCases);
        mTodayDeaths = validate(todayDeaths);
        mRecovered = validate(recovered);
        mActive = validate(active);
        mCritical = validate(critical);
    }

    private String validate(String value) {
        return (value.equals("null")) ? "0" : value;
    }

    protected Nation(Parcel in) {
        mCountry = in.readString();
        mCases = in.readString();
        mDeaths = in.readString();
        mTodayCases = in.readString();
        mTodayDeaths = in.readString();
        mRecovered = in.readString();
        mActive = in.readString();
        mCritical = in.readString();
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


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mCountry);
        dest.writeString(mCases);
        dest.writeString(mDeaths);
        dest.writeString(mTodayCases);
        dest.writeString(mTodayDeaths);
        dest.writeString(mRecovered);
        dest.writeString(mActive);
        dest.writeString(mCritical);
    }
}
