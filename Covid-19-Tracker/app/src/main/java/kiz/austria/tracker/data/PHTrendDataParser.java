package kiz.austria.tracker.data;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kiz.austria.tracker.model.PHTrend;

public class PHTrendDataParser extends AsyncTask<String, Void, List<PHTrend>> implements JSONRawData.OnDownloadComplete {

    private static final String TAG = "RawDataParser";

    private OnDataAvailable mOnDataAvailable;
    private JSONRawData.DownloadStatus mDownloadStatus;

    private List<PHTrend> mPHTrends;
    private String destinationUri;

    public PHTrendDataParser(OnDataAvailable onDataAvailable) {
        mOnDataAvailable = onDataAvailable;
    }

    @Override
    protected List<PHTrend> doInBackground(String... path) {
        destinationUri = path[0];
        JSONRawData JSONRawData = new JSONRawData(this);
        JSONRawData.runInTheSameThread(destinationUri);
        return mPHTrends;
    }

    @Override
    protected void onPostExecute(List<PHTrend> trends) {
        if (mOnDataAvailable != null) {
            mOnDataAvailable.onDataTrendAvailable(trends, mDownloadStatus);
        }
    }

    @Override
    public void onDownloadComplete(String data, JSONRawData.DownloadStatus status) {
        if (status == JSONRawData.DownloadStatus.OK) {

            if (destinationUri.equals(Addresses.Link.DATA_TREND_PHILIPPINES)) {
                mPHTrends = new ArrayList<>();
                try {
                    JSONArray jsonList = new JSONArray(data);
                    for (int i = 0; i < jsonList.length(); i++) {
                        JSONObject jsonObject = jsonList.getJSONObject(i);
                        String country = jsonObject.getString("country");
                        String infected = jsonObject.getString("infected");
                        String recovered = jsonObject.getString("recovered");
                        String deceased = jsonObject.getString("deceased");
                        String latestUpdate = jsonObject.getString("lastUpdatedAtApify");

                        //Inconsistent object data
                        String tested = getObjectTested(jsonObject);
                        String pui = getObjectPUI(jsonObject);
                        String pum = getObjectPUM(jsonObject);

                        mPHTrends.add(new PHTrend(country, infected, tested, recovered, deceased, pui, pum, latestUpdate));
                    }
                    mDownloadStatus = JSONRawData.DownloadStatus.OK;
                } catch (JSONException e) {
                    e.getMessage();
                    e.printStackTrace();
                    mDownloadStatus = JSONRawData.DownloadStatus.FAILED_OR_EMPTY;
                }
            }
        }
    }

    private String getObjectPUM(JSONObject currentCovered) {
        try {
            return currentCovered.getString("PUMs");
        } catch (JSONException e) {
            Log.d(TAG, "onDownloadComplete() No value daw eh.");
        }
        return "0";
    }

    private String getObjectPUI(JSONObject currentCovered) {
        try {
            return currentCovered.getString("PUIs");
        } catch (JSONException e) {
            Log.d(TAG, "onDownloadComplete() No value daw eh.");
        }
        return "0";
    }

    private String getObjectTested(JSONObject currentCovered) {
        try {
            return currentCovered.getString("tested");
        } catch (JSONException e) {
            Log.d(TAG, "onDownloadComplete() No value daw eh.");
        }
        return "0";
    }

    public interface OnDataAvailable {
        void onDataTrendAvailable(List<PHTrend> trends, final JSONRawData.DownloadStatus status);
    }
}