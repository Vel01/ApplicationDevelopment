package kiz.austria.tracker.data;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kiz.austria.tracker.model.Nation;
import kiz.austria.tracker.model.Philippines;

public class NationDataParser extends AsyncTask<String, Void, ArrayList<Nation>> {

    private static final String TAG = "NationDataParser";

    public enum ParseData {PHILIPPINES, COUNTRIES}


    private JSONRawData.DownloadStatus mDownloadStatus;
    private final OnDataAvailable mOnDataAvailable;
    private ArrayList<Nation> mNations;
    private Philippines mPhilippines;
    private ParseData mParseData;

    public interface OnDataAvailable {
        void onCountriesDataAvailable(ArrayList<Nation> nations, final JSONRawData.DownloadStatus status);

        void onPhilippinesDataAvailable(Philippines philippines, final JSONRawData.DownloadStatus status);
    }

    public NationDataParser(OnDataAvailable onDataAvailable) {
        mOnDataAvailable = onDataAvailable;
    }

    public void parse(ParseData parseData) {
        mParseData = parseData;
    }

    @Override
    protected ArrayList<Nation> doInBackground(String... raw) {

        if (mParseData == ParseData.COUNTRIES) {
            mNations = new ArrayList<>();
            try {
                JSONArray jsonList = new JSONArray(raw[0]);
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
                mDownloadStatus = JSONRawData.DownloadStatus.FAILED_OR_EMPTY;
            }
        }

        if (mParseData == ParseData.PHILIPPINES) {

            try {
                JSONObject jsonObject = new JSONObject(raw[0]);
                String cases = jsonObject.getString("cases");
                String deaths = jsonObject.getString("deaths");
                String todayCases = jsonObject.getString("todayCases");
                String todayDeaths = jsonObject.getString("todayDeaths");
                String recovered = jsonObject.getString("recovered");
                String active = jsonObject.getString("active");
                String critical = jsonObject.getString("critical");
                mPhilippines = new Philippines(cases, todayCases, deaths, todayDeaths, recovered, active, critical);
                mDownloadStatus = JSONRawData.DownloadStatus.OK;
            } catch (JSONException e) {
                e.printStackTrace();
                mDownloadStatus = JSONRawData.DownloadStatus.FAILED_OR_EMPTY;
            }
        }

        return mNations;
    }

    @Override
    protected void onPostExecute(ArrayList<Nation> nations) {
        if (mOnDataAvailable != null) {
            switch (mParseData) {
                case COUNTRIES:
                    mOnDataAvailable.onCountriesDataAvailable(nations, mDownloadStatus);
                    break;
                case PHILIPPINES:
                    mOnDataAvailable.onPhilippinesDataAvailable(mPhilippines, mDownloadStatus);
                    break;
            }
        }

    }
}
