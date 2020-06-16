package kiz.austria.tracker.data.parser;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kiz.austria.tracker.data.RawDataDownloader.DownloadStatus;
import kiz.austria.tracker.model.Cases;

public class APIFYDataParser extends AsyncTask<String, Void, ArrayList<Cases>> {

    private static final String TAG = "APIFYDataParser";
    private final OnDataAvailable mOnDataAvailable;
    private ArrayList<Cases> mPHListUpdatesCasesList;
    private DownloadStatus mDownloadStatus;
    private Cases mLatestDateUpdated;
    private ParseData mParseData;

    public APIFYDataParser(final OnDataAvailable onDataAvailable) {
        mOnDataAvailable = onDataAvailable;
    }

    public void parse(ParseData parseData) {
        mParseData = parseData;
    }

    @Override
    protected ArrayList<Cases> doInBackground(String... raw) {

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

                    mPHListUpdatesCasesList.add(new Cases(country, infected, tested, recovered, deceased, pui, pum, latestUpdate));
                }
                mDownloadStatus = DownloadStatus.OK;
            } catch (JSONException e) {
                e.getMessage();
                e.printStackTrace();
                mDownloadStatus = DownloadStatus.FAILED_OR_EMPTY;
            }
        }

        if (mParseData == ParseData.DATE_ONLY) {
            try {
                JSONArray jsonList = new JSONArray(raw[0]);
                JSONObject jsonObject = jsonList.getJSONObject(jsonList.length() - 1);
                String latestUpdate = jsonObject.getString("lastUpdatedAtApify");
                mLatestDateUpdated = new Cases(latestUpdate);
                mDownloadStatus = DownloadStatus.OK;
            } catch (JSONException e) {
                e.getMessage();
                e.printStackTrace();
                mDownloadStatus = DownloadStatus.FAILED_OR_EMPTY;
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
                    mPHListUpdatesCasesList.add(new Cases(infected, recovered, deceased, latestUpdate));
                }
                mDownloadStatus = DownloadStatus.OK;
            } catch (JSONException e) {
                e.getMessage();
                e.printStackTrace();
                mDownloadStatus = DownloadStatus.FAILED_OR_EMPTY;
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

                    mPHListUpdatesCasesList.add(new Cases(tested, pui, pum));
                }
                mDownloadStatus = DownloadStatus.OK;
            } catch (JSONException e) {
                e.getMessage();
                e.printStackTrace();
                mDownloadStatus = DownloadStatus.FAILED_OR_EMPTY;
            }
        }

        return mPHListUpdatesCasesList;
    }

    @Override
    protected void onPostExecute(ArrayList<Cases> aCases) {
        if (mOnDataAvailable != null) {
            switch (mParseData) {
                case FULL_DATA:
                    mOnDataAvailable.onFullDataAvailable(aCases, mDownloadStatus);
                    break;
                case ESSENTIAL_DATA:
                    mOnDataAvailable.onEssentialDataAvailable(aCases, mDownloadStatus);
                    break;
                case BASIC_DATA:
                    mOnDataAvailable.onBasicDataAvailable(aCases, mDownloadStatus);
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
        void onFullDataAvailable(ArrayList<Cases> dataList, final DownloadStatus status);

        void onDateAvailable(Cases data, final DownloadStatus status);

        void onEssentialDataAvailable(List<Cases> dataList, final DownloadStatus status);

        void onBasicDataAvailable(List<Cases> dataList, final DownloadStatus status);

    }

}
