package kiz.austria.tracker.data;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kiz.austria.tracker.model.Nation;

public class CountriesDataParser extends AsyncTask<String, Void, ArrayList<Nation>> implements JSONRawData.OnDownloadComplete {

    private static final String TAG = "RawDataParser";

    private OnDataAvailable mOnDataAvailable;
    private JSONRawData.DownloadStatus mDownloadStatus;

    private ArrayList<Nation> mNations = new ArrayList<>();
    private String destinationUri;

    public interface OnDataAvailable {

        void onDataAvailable(ArrayList<Nation> nations, final JSONRawData.DownloadStatus status);
    }

    private CountriesDataParser(OnDataAvailable onDataAvailable) {
        mOnDataAvailable = onDataAvailable;
    }

    public static CountriesDataParser getInstance(OnDataAvailable onDataAvailable) {
        return new CountriesDataParser(onDataAvailable);
    }

    public void setOnDataAvailable(OnDataAvailable onDataAvailable) {
        mOnDataAvailable = onDataAvailable;
    }

    @Override
    protected ArrayList<Nation> doInBackground(String... path) {
        destinationUri = path[0];
        JSONRawData JSONRawData = new JSONRawData(this);
        JSONRawData.runInTheSameThread(destinationUri);
        return mNations;
    }

    @Override
    protected void onPostExecute(ArrayList<Nation> nations) {
        if (mOnDataAvailable != null) {
            mOnDataAvailable.onDataAvailable(nations, mDownloadStatus);
        }
    }

    @Override
    public void onDownloadComplete(String data, JSONRawData.DownloadStatus status) {
        if (status == JSONRawData.DownloadStatus.OK) {

            if (destinationUri.equals(Addresses.Link.DATA_COUNTRIES)) {
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


        }

    }
}
