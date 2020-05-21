package kiz.austria.tracker.data;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import kiz.austria.tracker.model.Nation;

public class DataParser extends AsyncTask<String, Void, Nation> implements JSONRawData.OnDownloadComplete {

    private static final String TAG = "RawDataParser";

    private OnDataAvailable mOnDataAvailable;
    private JSONRawData.DownloadStatus mDownloadStatus;

    private Nation mNation;
    private String destinationUri;

    public interface OnDataAvailable {
        void onDataAvailable(Nation nation, final JSONRawData.DownloadStatus status);
    }

    private DataParser(OnDataAvailable onDataAvailable) {
        mOnDataAvailable = onDataAvailable;
    }

    public static DataParser getInstance(OnDataAvailable onDataAvailable) {
        return new DataParser(onDataAvailable);
    }

    public void setOnDataAvailable(OnDataAvailable onDataAvailable) {
        mOnDataAvailable = onDataAvailable;
    }


    @Override
    protected Nation doInBackground(String... path) {
        destinationUri = path[0];
        JSONRawData JSONRawData = new JSONRawData(this);
        JSONRawData.runInTheSameThread(destinationUri);
        return mNation;
    }

    @Override
    protected void onPostExecute(Nation nation) {
        if (mOnDataAvailable != null) {
            mOnDataAvailable.onDataAvailable(nation, mDownloadStatus);
        }
    }

    @Override
    public void onDownloadComplete(String data, JSONRawData.DownloadStatus status) {
        if (status == JSONRawData.DownloadStatus.OK) {

            if (destinationUri.equals(Addresses.Link.DATA_WORLD)) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    String country = jsonObject.getString("country");
                    String confirmed = jsonObject.getString("cases");
                    String deaths = jsonObject.getString("deaths");
                    String today_cases = jsonObject.getString("todayCases");
                    String today_deaths = jsonObject.getString("todayDeaths");
                    String recovered = jsonObject.getString("recovered");
                    String active = jsonObject.getString("active");
                    String critical = jsonObject.getString("critical");
                    mNation = new Nation(country, confirmed,
                            deaths, today_cases, today_deaths,
                            recovered, active, critical);
                    mDownloadStatus = JSONRawData.DownloadStatus.OK;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
