package kiz.austria.tracker.data.parser;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kiz.austria.tracker.data.DownloadRawData;
import kiz.austria.tracker.model.PHListUpdatesCases;

public class APIFYDataParser extends AsyncTask<String, Void, ArrayList<PHListUpdatesCases>> {

    private static final String TAG = "APIFYDataParser";
    private final OnDataAvailable mOnDataAvailable;
    private ArrayList<PHListUpdatesCases> mPHListUpdatesCasesList;
    private DownloadRawData.DownloadStatus mDownloadStatus;
    private PHListUpdatesCases mLatestDateUpdated;
    private ParseData mParseData;

    public APIFYDataParser(final OnDataAvailable onDataAvailable) {
        mOnDataAvailable = onDataAvailable;
    }

    public void parse(ParseData parseData) {
        mParseData = parseData;
    }

    @Override
    protected ArrayList<PHListUpdatesCases> doInBackground(String... raw) {

        if (mParseData == ParseData.FULL_DATA) {
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
                mDownloadStatus = DownloadRawData.DownloadStatus.OK;
            } catch (JSONException e) {
                e.getMessage();
                e.printStackTrace();
                mDownloadStatus = DownloadRawData.DownloadStatus.FAILED_OR_EMPTY;
            }
        }

        if (mParseData == ParseData.DATE_ONLY) {
            try {
                JSONArray jsonList = new JSONArray(raw[0]);
                JSONObject jsonObject = jsonList.getJSONObject(jsonList.length() - 1);
                String latestUpdate = jsonObject.getString("lastUpdatedAtApify");
                mLatestDateUpdated = new PHListUpdatesCases(latestUpdate);
                mDownloadStatus = DownloadRawData.DownloadStatus.OK;
            } catch (JSONException e) {
                e.getMessage();
                e.printStackTrace();
                mDownloadStatus = DownloadRawData.DownloadStatus.FAILED_OR_EMPTY;
            }
        }

        if (mParseData == ParseData.ESSENTIAL_DATA) {
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
                mDownloadStatus = DownloadRawData.DownloadStatus.OK;
            } catch (JSONException e) {
                e.getMessage();
                e.printStackTrace();
                mDownloadStatus = DownloadRawData.DownloadStatus.FAILED_OR_EMPTY;
            }
        }

        if (mParseData == ParseData.BASIC_DATA) {
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
                mDownloadStatus = DownloadRawData.DownloadStatus.OK;
            } catch (JSONException e) {
                e.getMessage();
                e.printStackTrace();
                mDownloadStatus = DownloadRawData.DownloadStatus.FAILED_OR_EMPTY;
            }
        }

        return mPHListUpdatesCasesList;
    }

    @Override
    protected void onPostExecute(ArrayList<PHListUpdatesCases> phListUpdatesCases) {
        if (mOnDataAvailable != null) {
            switch (mParseData) {
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

    public enum ParseData {FULL_DATA, DATE_ONLY, ESSENTIAL_DATA, BASIC_DATA}

    public interface OnDataAvailable {
        void onFullDataAvailable(ArrayList<PHListUpdatesCases> dataList, final DownloadRawData.DownloadStatus status);

        void onDateAvailable(PHListUpdatesCases data, final DownloadRawData.DownloadStatus status);

        void onEssentialDataAvailable(List<PHListUpdatesCases> dataList, final DownloadRawData.DownloadStatus status);

        void onBasicDataAvailable(List<PHListUpdatesCases> dataList, final DownloadRawData.DownloadStatus status);

    }

}
