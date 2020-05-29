package kiz.austria.tracker.model;

public class PHRecord {

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

    public PHRecord(String caseNo, String gender, String age, String nationality, String residencePh, String travelHistory, String dateOfAnnouncement, String hospitalAdmittedTo, String healthStatus, String location, String latitude, String longitude) {
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
        return "PHRecord{" +
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
