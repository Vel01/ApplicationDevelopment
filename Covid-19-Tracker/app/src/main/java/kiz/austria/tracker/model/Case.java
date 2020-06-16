package kiz.austria.tracker.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Case implements Parcelable {

    private final String caseNo;
    private final String gender;
    private final String age;
    private final String nationality;
    private final String residencePh;
    private final String travelHistory;
    private final String dateOfAnnouncement;
    private final String hospitalAdmittedTo;
    private final String healthStatus;
    private final String location;
    private final String latitude;
    private final String longitude;

    public static final Creator<Case> CREATOR = new Creator<Case>() {
        @Override
        public Case createFromParcel(Parcel in) {
            return new Case(in);
        }

        @Override
        public Case[] newArray(int size) {
            return new Case[size];
        }
    };

    public Case(String caseNo, String gender, String age, String nationality,
                String residencePh, String travelHistory, String dateOfAnnouncement,
                String hospitalAdmittedTo, String healthStatus, String location, String latitude, String longitude) {
        this.caseNo = caseNo;
        this.gender = gender;
        this.age = age;
        this.nationality = nationality;
        this.residencePh = residencePh;
        this.travelHistory = travelHistory;
        this.dateOfAnnouncement = dateOfAnnouncement;
        this.hospitalAdmittedTo = hospitalAdmittedTo;
        this.healthStatus = healthStatus;
        this.location = location;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    protected Case(Parcel in) {
        caseNo = in.readString();
        gender = in.readString();
        age = in.readString();
        nationality = in.readString();
        residencePh = in.readString();
        travelHistory = in.readString();
        dateOfAnnouncement = in.readString();
        hospitalAdmittedTo = in.readString();
        healthStatus = in.readString();
        location = in.readString();
        latitude = in.readString();
        longitude = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(caseNo);
        dest.writeString(gender);
        dest.writeString(age);
        dest.writeString(nationality);
        dest.writeString(residencePh);
        dest.writeString(travelHistory);
        dest.writeString(dateOfAnnouncement);
        dest.writeString(hospitalAdmittedTo);
        dest.writeString(healthStatus);
        dest.writeString(location);
        dest.writeString(latitude);
        dest.writeString(longitude);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public String getLocation() {
        return location;
    }

    public String getCaseNo() {
        return caseNo;
    }

    public String getGender() {
        return gender;
    }

    public String getAge() {
        return age;
    }

    public String getNationality() {
        return nationality;
    }

    public String getResidencePh() {
        return residencePh;
    }

    public String getTravelHistory() {
        return travelHistory;
    }

    public String getDateOfAnnouncement() {
        return dateOfAnnouncement;
    }

    public String getHospitalAdmittedTo() {
        return hospitalAdmittedTo;
    }

    public String getHealthStatus() {
        return healthStatus;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    @Override
    public String toString() {
        return "Case{" +
                "caseNo='" + caseNo + '\'' +
                ", gender='" + gender + '\'' +
                ", age='" + age + '\'' +
                ", nationality='" + nationality + '\'' +
                ", residencePh='" + residencePh + '\'' +
                ", travelHistory='" + travelHistory + '\'' +
                ", dateOfAnnouncement='" + dateOfAnnouncement + '\'' +
                ", hospitalAdmittedTo='" + hospitalAdmittedTo + '\'' +
                ", healthStatus='" + healthStatus + '\'' +
                ", location='" + location + '\'' +
                ", latitude='" + latitude + '\'' +
                ", longitude='" + longitude + '\'' +
                '}';
    }
}
