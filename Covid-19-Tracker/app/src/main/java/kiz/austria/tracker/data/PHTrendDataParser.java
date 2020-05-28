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

    private InterestedData interest;

    private OnDataAvailable mOnDataAvailable;
    private JSONRawData.DownloadStatus mDownloadStatus;

    private List<PHTrend> mPHTrends;
    private String destinationUri;

    public void setInterestData(InterestedData interest) {
        this.interest = interest;
    }

    public PHTrendDataParser(OnDataAvailable onDataAvailable) {
        mOnDataAvailable = onDataAvailable;
    }

    @Override
    public void onDownloadComplete(String data, JSONRawData.DownloadStatus status) {
        if (status == JSONRawData.DownloadStatus.OK) {

            if (destinationUri.equals(Addresses.Link.DATA_TREND_PHILIPPINES) && (interest == InterestedData.COMPLETE_DATA)) {
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

            if (destinationUri.equals(Addresses.Link.DATA_TREND_PHILIPPINES) && (interest == InterestedData.CASUALTIES_ONLY)) {
                mPHTrends = new ArrayList<>();
                try {
                    JSONArray jsonList = new JSONArray(data);
                    for (int i = 0; i < jsonList.length(); i++) {
                        JSONObject jsonObject = jsonList.getJSONObject(i);
                        String infected = jsonObject.getString("infected");
                        String recovered = jsonObject.getString("recovered");
                        String deceased = jsonObject.getString("deceased");
                        String latestUpdate = jsonObject.getString("lastUpdatedAtApify");
                        mPHTrends.add(new PHTrend("N/A", infected, "0", recovered, deceased, "0", "0", latestUpdate));
                    }
                    mDownloadStatus = JSONRawData.DownloadStatus.OK;
                } catch (JSONException e) {
                    e.getMessage();
                    e.printStackTrace();
                    mDownloadStatus = JSONRawData.DownloadStatus.FAILED_OR_EMPTY;
                }
            }

            if (destinationUri.equals(Addresses.Link.DATA_TREND_PHILIPPINES) && (interest == InterestedData.DATE_ONLY)) {
                mPHTrends = new ArrayList<>();
                try {
                    JSONArray jsonList = new JSONArray(data);
                    for (int i = 0; i < jsonList.length(); i++) {
                        JSONObject jsonObject = jsonList.getJSONObject(i);
                        String latestUpdate = jsonObject.getString("lastUpdatedAtApify");
                        if (i == (jsonList.length() - 1))
                            mPHTrends.add(new PHTrend("0", "0", "0", "0", "0", "0", "0", latestUpdate));
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

    public enum InterestedData {COMPLETE_DATA, DATE_ONLY, CASUALTIES_ONLY, UNDERESTIMATION_ONLY}

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
