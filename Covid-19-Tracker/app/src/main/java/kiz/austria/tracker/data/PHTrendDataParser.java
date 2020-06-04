package kiz.austria.tracker.data;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kiz.austria.tracker.model.PHCases;

public class PHTrendDataParser extends AsyncTask<String, Void, List<PHCases>> implements JSONRawData.OnDownloadComplete {

    private static final String TAG = "RawDataParser";

    private PHCases mDateUpdated;

    private InterestedData interest;
    private OnDataAvailable mOnDataAvailable;
    private JSONRawData.DownloadStatus mDownloadStatus;
    private List<PHCases> mPHCases;

    private String destinationUri;

    @Override
    public void onDownloadComplete(String data, JSONRawData.DownloadStatus status) {
        if (status == JSONRawData.DownloadStatus.OK) {

            if (destinationUri.equals(Addresses.Link.DATA_TREND_PHILIPPINES) && (interest == InterestedData.COMPLETE_DATA)) {
                mPHCases = new ArrayList<>();
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

                        mPHCases.add(new PHCases(country, infected, tested, recovered, deceased, pui, pum, latestUpdate));
                    }
                    mDownloadStatus = JSONRawData.DownloadStatus.OK;
                } catch (JSONException e) {
                    e.getMessage();
                    e.printStackTrace();
                    mDownloadStatus = JSONRawData.DownloadStatus.FAILED_OR_EMPTY;
                }
            }
            if (destinationUri.equals(Addresses.Link.DATA_TREND_PHILIPPINES) && (interest == InterestedData.UNDERINVESTIGATION_ONLY)) {
                mPHCases = new ArrayList<>();
                try {
                    JSONArray jsonList = new JSONArray(data);
                    for (int i = 0; i < jsonList.length(); i++) {
                        JSONObject jsonObject = jsonList.getJSONObject(i);
                        //Inconsistent object data
                        String tested = getObjectTested(jsonObject);
                        String pui = getObjectPUI(jsonObject);
                        String pum = getObjectPUM(jsonObject);

                        mPHCases.add(new PHCases(tested, pui, pum));
                    }
                    mDownloadStatus = JSONRawData.DownloadStatus.OK;
                } catch (JSONException e) {
                    e.getMessage();
                    e.printStackTrace();
                    mDownloadStatus = JSONRawData.DownloadStatus.FAILED_OR_EMPTY;
                }
            }

            if (destinationUri.equals(Addresses.Link.DATA_TREND_PHILIPPINES) && (interest == InterestedData.CASUALTIES_ONLY)) {
                mPHCases = new ArrayList<>();
                try {
                    JSONArray jsonList = new JSONArray(data);
                    for (int i = 0; i < jsonList.length(); i++) {
                        JSONObject jsonObject = jsonList.getJSONObject(i);
                        String infected = jsonObject.getString("infected");
                        String recovered = jsonObject.getString("recovered");
                        String deceased = jsonObject.getString("deceased");
                        String latestUpdate = jsonObject.getString("lastUpdatedAtApify");
                        mPHCases.add(new PHCases(infected, recovered, deceased, latestUpdate));
                    }
                    mDownloadStatus = JSONRawData.DownloadStatus.OK;
                } catch (JSONException e) {
                    e.getMessage();
                    e.printStackTrace();
                    mDownloadStatus = JSONRawData.DownloadStatus.FAILED_OR_EMPTY;
                }
            }

            if (destinationUri.equals(Addresses.Link.DATA_TREND_PHILIPPINES) && (interest == InterestedData.DATE_ONLY)) {
                try {
                    JSONArray jsonList = new JSONArray(data);
                    JSONObject jsonObject = jsonList.getJSONObject(jsonList.length() - 1);
                    String latestUpdate = jsonObject.getString("lastUpdatedAtApify");
                    mDateUpdated = new PHCases(latestUpdate);
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
    protected void onPostExecute(List<PHCases> trends) {
        if (mOnDataAvailable != null) {
            switch (interest) {
                case COMPLETE_DATA:
                    mOnDataAvailable.onDataTrendAvailable(trends, mDownloadStatus);
                    break;
                case UNDERINVESTIGATION_ONLY:
                    mOnDataAvailable.onDataUnderinvestigationTrendAvailable(trends, mDownloadStatus);
                    break;
                case CASUALTIES_ONLY:
                    mOnDataAvailable.onDataCasualtiesTrendAvailable(trends, mDownloadStatus);
                    break;
                case DATE_ONLY:
                    mOnDataAvailable.onDataLastUpdateAvailable(mDateUpdated, mDownloadStatus);
                    break;
            }
        }
    }

    public void setInterestData(InterestedData interest) {
        this.interest = interest;
    }

    public PHTrendDataParser(OnDataAvailable onDataAvailable) {
        mOnDataAvailable = onDataAvailable;
    }

    public enum InterestedData {COMPLETE_DATA, DATE_ONLY, CASUALTIES_ONLY, UNDERINVESTIGATION_ONLY}

    @Override
    protected List<PHCases> doInBackground(String... path) {
        destinationUri = path[0];
        JSONRawData JSONRawData = new JSONRawData(this);
        JSONRawData.runInTheSameThread(destinationUri);
        return mPHCases;
    }

    private String getObjectPUM(JSONObject currentCovered) {
        try {
            return currentCovered.getString("PUMs");
        } catch (JSONException e) {
            e.getMessage();
        }
        return "0";
    }

    private String getObjectPUI(JSONObject currentCovered) {
        try {
            return currentCovered.getString("PUIs");
        } catch (JSONException e) {
            e.getMessage();
        }
        return "0";
    }

    private String getObjectTested(JSONObject currentCovered) {
        try {
            return currentCovered.getString("tested");
        } catch (JSONException e) {
            e.getMessage();
        }
        return "0";
    }

    public interface OnDataAvailable {
        void onDataTrendAvailable(List<PHCases> trends, final JSONRawData.DownloadStatus status);

        void onDataCasualtiesTrendAvailable(List<PHCases> casualties, final JSONRawData.DownloadStatus status);

        void onDataUnderinvestigationTrendAvailable(List<PHCases> casualties, final JSONRawData.DownloadStatus status);

        void onDataLastUpdateAvailable(PHCases date, final JSONRawData.DownloadStatus status);

    }
}
