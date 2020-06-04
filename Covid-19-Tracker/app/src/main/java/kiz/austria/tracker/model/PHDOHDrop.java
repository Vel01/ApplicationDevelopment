package kiz.austria.tracker.model;

public class PHDOHDrop {

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

    public PHDOHDrop(String casesCode, String age, String sex, String isAdmitted, String dateReported, String dateDied, String recoveredOn, String regionRes, String provCityRes, String location, String latitude, String longitude) {
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
        return "PHDOHDrop{" +
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
}
