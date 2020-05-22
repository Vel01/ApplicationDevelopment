package kiz.austria.tracker.data;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kiz.austria.tracker.model.Nation;

public class DataParser extends AsyncTask<String, Void, List<Nation>> implements JSONRawData.OnDownloadComplete {

    private static final String TAG = "RawDataParser";

    private OnDataAvailable mOnDataAvailable;
    private JSONRawData.DownloadStatus mDownloadStatus;

    private List<Nation> mNations;
    private String destinationUri;

    public DataParser(OnDataAvailable onDataAvailable) {
        mOnDataAvailable = onDataAvailable;
    }

    @Override
    protected List<Nation> doInBackground(String... path) {
        destinationUri = path[0];
        JSONRawData JSONRawData = new JSONRawData(this);
        JSONRawData.runInTheSameThread(destinationUri);
        return mNations;
    }

    @Override
    protected void onPostExecute(List<Nation> nations) {
        if (mOnDataAvailable != null) {
            mOnDataAvailable.onDataAvailable(nations, mDownloadStatus);
        }
    }

    @Override
    public void onDownloadComplete(String data, JSONRawData.DownloadStatus status) {
        if (status == JSONRawData.DownloadStatus.OK) {

            if (destinationUri.equals(Addresses.Link.DATA_COUNTRIES)) {
                mNations = new ArrayList<>();
                try {
                    JSONArray jsonList = new JSONArray(data);
                    for (int i = 0; i < jsonList.length(); i++) {
                        JSONObject currentCovered = jsonList.getJSONObject(i);
                        String country = currentCovered.getString("country");
                        String cases = currentCovered.getString("cases");
                        String todayCases = currentCovered.getString("todayCases");
                        String deaths = currentCovered.getString("deaths");
                        String todayDeaths = currentCovered.getString("todayDeaths");
                        String recovered = currentCovered.getString("recovered");
                        String active = currentCovered.getString("active");
                        String critical = currentCovered.getString("critical");
                        mNations.add(new Nation(country, cases, deaths, todayCases, todayDeaths, recovered, active, critical));
                    }
                    mDownloadStatus = JSONRawData.DownloadStatus.OK;
                } catch (JSONException e) {
                    e.getMessage();
                    e.printStackTrace();
                }
            }

            if (destinationUri.equals(Addresses.Link.DATA_WORLD)) {
                mNations = new ArrayList<>();

                try {
                    JSONObject jsonObject = new JSONObject(data);
                    String country = jsonObject.getString("country");
                    String cases = jsonObject.getString("cases");
                    String deaths = jsonObject.getString("deaths");
                    String todayCases = jsonObject.getString("todayCases");
                    String todayDeaths = jsonObject.getString("todayDeaths");
                    String recovered = jsonObject.getString("recovered");
                    String active = jsonObject.getString("active");
                    String critical = jsonObject.getString("critical");
                    mNations.add(new Nation(country, cases, deaths, todayCases, todayDeaths, recovered, active, critical));
                    mDownloadStatus = JSONRawData.DownloadStatus.OK;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public interface OnDataAvailable {

        void onDataAvailable(List<Nation> nations, final JSONRawData.DownloadStatus status);
    }
}