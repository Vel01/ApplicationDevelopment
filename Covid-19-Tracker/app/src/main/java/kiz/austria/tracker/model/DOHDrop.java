package kiz.austria.tracker.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DOHDrop implements Parcelable {

    private String casesCode;
    private String age;
    private String sex;
    private String isAdmitted;
    private String dateReported;
    private String dateDied;
    private String recoveredOn;
    private String regionRes;
    private String provCityRes;
    private String location;
    private String latitude;
    private String longitude;

    public static final Creator<DOHDrop> CREATOR = new Creator<DOHDrop>() {
        @Override
        public DOHDrop createFromParcel(Parcel in) {
            return new DOHDrop(in);
        }

        @Override
        public DOHDrop[] newArray(int size) {
            return new DOHDrop[size];
        }
    };

    public DOHDrop(String casesCode, String age, String sex, String isAdmitted, String dateReported, String dateDied, String recoveredOn, String regionRes, String provCityRes, String location, String latitude, String longitude) {
        this.casesCode = casesCode;
        this.age = setAge(age);
        this.sex = sex;
        this.isAdmitted = isAdmitted;
        this.dateReported = dateReported;
        this.dateDied = dateDied;
        this.recoveredOn = recoveredOn;
        this.regionRes = regionRes;
        this.provCityRes = provCityRes;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    protected DOHDrop(Parcel in) {
        casesCode = in.readString();
        age = in.readString();
        sex = in.readString();
        isAdmitted = in.readString();
        dateReported = in.readString();
        dateDied = in.readString();
        recoveredOn = in.readString();
        regionRes = in.readString();
        provCityRes = in.readString();
        location = in.readString();
        latitude = in.readString();
        longitude = in.readString();
    }

    private String setAge(String age) {

        return (age.isEmpty()) ? "0" : age;
    }

    public String getCasesCode() {
        return casesCode;
    }

    public String getAge() {
        return age;
    }

    public String getSex() {
        return sex;
    }

    public String getIsAdmitted() {
        return isAdmitted;
    }

    public String getDateReported() {
        return dateReported;
    }

    public String getDateDied() {
        return dateDied;
    }

    public String getRecoveredOn() {
        return recoveredOn;
    }

    public String getRegionRes() {
        return regionRes;
    }

    public String getProvCityRes() {
        return provCityRes;
    }

    public String getLocation() {
        return location;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "DOHDrop{" +
                "casesCode='" + casesCode + '\'' +
                ", age='" + age + '\'' +
                ", sex='" + sex + '\'' +
                ", isAdmitted='" + isAdmitted + '\'' +
                ", dateReported='" + dateReported + '\'' +
                ", dateDied='" + dateDied + '\'' +
                ", recoveredOn='" + recoveredOn + '\'' +
                ", regionRes='" + regionRes + '\'' +
                ", provCityRes='" + provCityRes + '\'' +
                ", location='" + location + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(casesCode);
        dest.writeString(age);
        dest.writeString(sex);
        dest.writeString(isAdmitted);
        dest.writeString(dateReported);
        dest.writeString(dateDied);
        dest.writeString(recoveredOn);
        dest.writeString(regionRes);
        dest.writeString(provCityRes);
        dest.writeString(location);
        dest.writeString(latitude);
        dest.writeString(longitude);
    }
}
