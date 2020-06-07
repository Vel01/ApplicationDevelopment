package kiz.austria.tracker.data;

public class DownloadedData {

    private static DownloadedData instance;

    private String mApifyData;
    private String mHerokuappDOHData;
    private String mHerokuappPhilippinesData;
    private String mHerokuappCountriesData;

    private DownloadedData() {
        mApifyData = "";
        mHerokuappDOHData = "";
        mHerokuappPhilippinesData = "";
        mHerokuappCountriesData = "";
    }

    public static DownloadedData getInstance() {
        if (instance == null) {
            instance = new DownloadedData();
        }
        return instance;
    }


    public void saveApifyData(String data) {
        mApifyData = data;
    }

    public void saveDOHData(String data) {
        mHerokuappDOHData = data;
    }

    public void savePhilippinesData(String data) {
        mHerokuappPhilippinesData = data;
    }

    public void saveCountriesData(String data) {
        mHerokuappCountriesData = data;
    }


    public String getApifyData() {
        return mApifyData;
    }

    public String getHerokuappDOHData() {
        return mHerokuappDOHData;
    }

    public String getHerokuappPhilippinesData() {
        return mHerokuappPhilippinesData;
    }

    public String getHerokuappCountriesData() {
        return mHerokuappCountriesData;
    }
}
