package kiz.austria.tracker.data;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class JSONRawData extends AsyncTask<String, Void, String> {

    public enum DownloadStatus {IDLE, PROCESSING, NOT_INITIALISED, FAILED_OR_EMPTY, OK}

    private static final String TAG = "GetRawData";

    private DownloadStatus mDownloadStatus;

    private final OnDownloadComplete mOnDownloadComplete;

    public interface OnDownloadComplete {
        void onDownloadComplete(String data, DownloadStatus status);
    }

    JSONRawData(OnDownloadComplete onDownloadComplete) {
        mOnDownloadComplete = onDownloadComplete;
        mDownloadStatus = DownloadStatus.IDLE;
    }

    void runInTheSameThread(String path) {
        if (mOnDownloadComplete != null) {
            String data = doInBackground(path);
            mOnDownloadComplete.onDownloadComplete(data, mDownloadStatus);
        }
    }

    private String downloadJSON(String path) {

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
    protected String doInBackground(String... path) {
        if (path[0] == null) {
            mDownloadStatus = DownloadStatus.NOT_INITIALISED;
            return null;
        }
        return downloadJSON(path[0]);
    }

    @Override
    protected void onPostExecute(String data) {
        if (mOnDownloadComplete != null) {
            mOnDownloadComplete.onDownloadComplete(data, mDownloadStatus);
        }
    }
}
