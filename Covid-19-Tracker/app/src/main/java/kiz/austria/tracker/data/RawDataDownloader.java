package kiz.austria.tracker.data;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class RawDataDownloader extends AsyncTask<String, Void, String> {

    private static final String TAG = "RawDataDownloader";
    private final OnDownloadComplete mOnDownloadComplete;
    private DownloadStatus mDownloadStatus;
    private String mCurrentAddress;

    public RawDataDownloader(OnDownloadComplete onDownloadComplete) {
        mOnDownloadComplete = onDownloadComplete;
        mDownloadStatus = DownloadStatus.IDLE;
    }

    private String downloadJSON(String path) {
        mCurrentAddress = path;

        HttpURLConnection connection = null;
        BufferedReader reader = null;
        try {
            mDownloadStatus = DownloadStatus.PROCESSING;
            URL url = new URL(path);
            connection = (HttpURLConnection) url.openConnection();
            int response = connection.getResponseCode();
            Log.d(TAG, "downloadJSON: response code: " + response);
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));

            StringBuilder data = new StringBuilder();

            int charsRead;
            char[] inputBuffer = new char[100000];

            while (true) {
                charsRead = reader.read(inputBuffer);

                if (charsRead < 0) break;
                if (charsRead > 0) {
                    data.append(String.copyValueOf(inputBuffer, 0, charsRead));
                }
            }

            mDownloadStatus = DownloadStatus.OK;
            return data.toString();
        } catch (MalformedURLException e) {
            Log.e(TAG, "downloadJSON: Invalid URL " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "downloadJSON: IO Exception reading data " + e.getMessage());
        } catch (SecurityException e) {
            Log.e(TAG, "downloadJSON: Security Exception, need permission? " + e.getMessage());
        } finally {
            if (connection != null) connection.disconnect();
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(TAG, "downloadJSON: Error closing stream " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        mDownloadStatus = DownloadStatus.FAILED_OR_EMPTY;
        return null;
    }

    @Override
    protected String doInBackground(String... paths) {

        if (paths[0] == null) {
            mDownloadStatus = DownloadStatus.NOT_INITIALISED;
            return null;
        }

        for (String path : paths) {

            String data = downloadJSON(path);

            if (mOnDownloadComplete != null) {
                switch (mCurrentAddress) {
                    case Addresses.Link.DATA_PHILIPPINES_FROM_APIFY:
                        mOnDownloadComplete.onDownloadCompleteFromApify(data, mDownloadStatus);
                        break;
                    case Addresses.Link.DATA_PHILIPPINES_DOH_DROP_FROM_HEROKUAPP:
                        mOnDownloadComplete.onDownloadCompleteDOHDataFromHerokuapp(data, mDownloadStatus);
                        break;
                    case Addresses.Link.DATA_PHILIPPINES_FROM_HEROKUAPP:
                        mOnDownloadComplete.onDownloadCompletePhilippinesDataFromHerokuapp(data, mDownloadStatus);
                        break;
                    case Addresses.Link.DATA_COUNTRIES_FROM_HEROKUAPP:
                        mOnDownloadComplete.onDownloadCompleteCountriesDataFromHerokuapp(data, mDownloadStatus);
                        break;
                }
            }
        }

        return null;
    }

    public enum DownloadStatus {IDLE, PROCESSING, NOT_INITIALISED, FAILED_OR_EMPTY, OK}

    public interface OnDownloadComplete {

        void onDownloadCompleteFromApify(String data, DownloadStatus status);

        void onDownloadCompleteDOHDataFromHerokuapp(String data, DownloadStatus status);

        void onDownloadCompletePhilippinesDataFromHerokuapp(String data, DownloadStatus status);

        void onDownloadCompleteCountriesDataFromHerokuapp(String data, DownloadStatus status);
    }

}
