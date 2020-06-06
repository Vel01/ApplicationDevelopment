package kiz.austria.tracker.data;

public class DownloadedData {

    private static DownloadedData instance;

    private String mApifyData;
    private String mHerokuappDOHData;

    private DownloadedData() {
        mApifyData = "";
        mHerokuappDOHData = "";
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


    public String getApifyData() {
        return mApifyData;
    }

    public String getHerokuappDOHData() {
        return mHerokuappDOHData;
    }
}
