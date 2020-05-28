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
    private final String latitude;
    private final String longitude;

    public PHRecord(String caseNo, String gender, String age, String nationality, String residencePh, String travelHistory, String dateOfAnnouncement, String hospitalAdmittedTo, String healthStatus, String latitude, String longitude) {
        this.caseNo = caseNo;
        this.gender = gender;
        this.age = age;
        this.nationality = nationality;
        this.residencePh = residencePh;
        this.travelHistory = travelHistory;
        this.dateOfAnnouncement = dateOfAnnouncement;
        this.hospitalAdmittedTo = hospitalAdmittedTo;
        this.healthStatus = healthStatus;
        this.latitude = latitude;
        this.longitude = longitude;
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
}
