package kiz.austria.tracker.data;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kiz.austria.tracker.model.PHListUpdatesCases;

public class APIFYDataParser extends AsyncTask<String, Void, ArrayList<PHListUpdatesCases>> {

    private static final String TAG = "APIFYDataParser";
    private final OnDataAvailable mOnDataAvailable;
    private ArrayList<PHListUpdatesCases> mPHListUpdatesCasesList;
    private JSONRawData.DownloadStatus mDownloadStatus;
    private PHListUpdatesCases mLatestDateUpdated;
    private DownloadData mDownloadData;

    public APIFYDataParser(final OnDataAvailable onDataAvailable) {
        mOnDataAvailable = onDataAvailable;
    }

    public void download(DownloadData download) {
        mDownloadData = download;
    }

    @Override
    protected ArrayList<PHListUpdatesCases> doInBackground(String... raw) {

        if (mDownloadData == DownloadData.FULL_DATA) {
            mPHListUpdatesCasesList = new ArrayList<>();
            try {
                JSONArray jsonList = new JSONArray(raw[0]);
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

                    mPHListUpdatesCasesList.add(new PHListUpdatesCases(country, infected, tested, recovered, deceased, pui, pum, latestUpdate));
                }
                mDownloadStatus = JSONRawData.DownloadStatus.OK;
            } catch (JSONException e) {
                e.getMessage();
                e.printStackTrace();
                mDownloadStatus = JSONRawData.DownloadStatus.FAILED_OR_EMPTY;
            }
        }

        if (mDownloadData == DownloadData.DATE_ONLY) {
            try {
                JSONArray jsonList = new JSONArray(raw[0]);
                JSONObject jsonObject = jsonList.getJSONObject(jsonList.length() - 1);
                String latestUpdate = jsonObject.getString("lastUpdatedAtApify");
                mLatestDateUpdated = new PHListUpdatesCases(latestUpdate);
                mDownloadStatus = JSONRawData.DownloadStatus.OK;
            } catch (JSONException e) {
                e.getMessage();
                e.printStackTrace();
                mDownloadStatus = JSONRawData.DownloadStatus.FAILED_OR_EMPTY;
            }
        }

        if (mDownloadData == DownloadData.ESSENTIAL_DATA) {
            mPHListUpdatesCasesList = new ArrayList<>();
            try {
                JSONArray jsonList = new JSONArray(raw[0]);
                for (int i = 0; i < jsonList.length(); i++) {
                    JSONObject jsonObject = jsonList.getJSONObject(i);
                    String infected = jsonObject.getString("infected");
                    String recovered = jsonObject.getString("recovered");
                    String deceased = jsonObject.getString("deceased");
                    String latestUpdate = jsonObject.getString("lastUpdatedAtApify");
                    mPHListUpdatesCasesList.add(new PHListUpdatesCases(infected, recovered, deceased, latestUpdate));
                }
                mDownloadStatus = JSONRawData.DownloadStatus.OK;
            } catch (JSONException e) {
                e.getMessage();
                e.printStackTrace();
                mDownloadStatus = JSONRawData.DownloadStatus.FAILED_OR_EMPTY;
            }
        }

        if (mDownloadData == DownloadData.BASIC_DATA) {
            mPHListUpdatesCasesList = new ArrayList<>();
            try {
                JSONArray jsonList = new JSONArray(raw[0]);
                for (int i = 0; i < jsonList.length(); i++) {
                    JSONObject jsonObject = jsonList.getJSONObject(i);
                    //Inconsistent object data
                    String tested = getObjectTested(jsonObject);
                    String pui = getObjectPUI(jsonObject);
                    String pum = getObjectPUM(jsonObject);

                    mPHListUpdatesCasesList.add(new PHListUpdatesCases(tested, pui, pum));
                }
                mDownloadStatus = JSONRawData.DownloadStatus.OK;
            } catch (JSONException e) {
                e.getMessage();
                e.printStackTrace();
                mDownloadStatus = JSONRawData.DownloadStatus.FAILED_OR_EMPTY;
            }
        }

        return mPHListUpdatesCasesList;
    }

    @Override
    protected void onPostExecute(ArrayList<PHListUpdatesCases> phListUpdatesCases) {
        if (mOnDataAvailable != null) {
            switch (mDownloadData) {
                case FULL_DATA:
                    mOnDataAvailable.onFullDataAvailable(phListUpdatesCases, mDownloadStatus);
                    break;
                case ESSENTIAL_DATA:
                    mOnDataAvailable.onEssentialDataAvailable(phListUpdatesCases, mDownloadStatus);
                    break;
                case BASIC_DATA:
                    mOnDataAvailable.onBasicDataAvailable(phListUpdatesCases, mDownloadStatus);
                    break;
                case DATE_ONLY:
                    mOnDataAvailable.onDateAvailable(mLatestDateUpdated, mDownloadStatus);
                    break;
            }
        }
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

    public enum DownloadData {FULL_DATA, DATE_ONLY, ESSENTIAL_DATA, BASIC_DATA}

    public interface OnDataAvailable {
        void onFullDataAvailable(ArrayList<PHListUpdatesCases> dataList, final JSONRawData.DownloadStatus status);

        void onDateAvailable(PHListUpdatesCases data, final JSONRawData.DownloadStatus status);

        void onEssentialDataAvailable(List<PHListUpdatesCases> dataList, final JSONRawData.DownloadStatus status);

        void onBasicDataAvailable(List<PHListUpdatesCases> dataList, final JSONRawData.DownloadStatus status);

    }

}
