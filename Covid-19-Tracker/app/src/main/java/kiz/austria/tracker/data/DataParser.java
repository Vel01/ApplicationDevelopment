package kiz.austria.tracker.data;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import kiz.austria.tracker.list.ListModel;
import kiz.austria.tracker.model.Countries;
import kiz.austria.tracker.model.Country;
import kiz.austria.tracker.model.Global;

public class DataParser extends AsyncTask<String, Void, ListModel<Countries>> implements RawData.OnDownloadComplete {

    private static final String TAG = "RawDataParser";

    private final OnDataAvailable mOnDataAvailable;
    private RawData.DownloadStatus mDownloadStatus;

    private ListModel<Countries> mCoverages;
    private String destinationUri;

    public interface OnDataAvailable {
        void onDataAvailable(ListModel<Countries> coverage, final RawData.DownloadStatus status);
    }

    private DataParser(OnDataAvailable onDataAvailable) {
        mOnDataAvailable = onDataAvailable;
        mCoverages = new ListModel<>();
    }

    public static DataParser getInstance(OnDataAvailable onDataAvailable) {
        return new DataParser(onDataAvailable);
    }


    @Override
    protected ListModel<Countries> doInBackground(String... path) {
        destinationUri = path[0];
        RawData rawData = new RawData(this);
        rawData.runInTheSameThread(destinationUri);
        return mCoverages;
    }

    @Override
    protected void onPostExecute(ListModel<Countries> countriesListModel) {
        if (mOnDataAvailable != null) {
            mOnDataAvailable.onDataAvailable(countriesListModel, mDownloadStatus);
        }
    }

    @Override
    public void onDownloadComplete(String data, RawData.DownloadStatus status) {
        if (status == RawData.DownloadStatus.OK) {

            if (destinationUri.equals(PathContract.Link.DATA_COUNTRIES)) {
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
                        Countries coverage = new Countries(country, cases, deaths, todayCases, todayDeaths, recovered, active, critical);
                        mCoverages.addCoverage(coverage);
                    }
                    mDownloadStatus = RawData.DownloadStatus.OK;

                } catch (JSONException e) {
                    e.getMessage();
                    e.printStackTrace();
                }
            }

            if (destinationUri.equals(PathContract.Link.DATA_COUNTRY)) {
                try {
                    JSONObject currentCovered = new JSONObject(data);
                    String country = currentCovered.getString("country");
                    String cases = currentCovered.getString("cases");
                    String todayCases = currentCovered.getString("todayCases");
                    String deaths = currentCovered.getString("deaths");
                    String todayDeaths = currentCovered.getString("todayDeaths");
                    String recovered = currentCovered.getString("recovered");
                    String active = currentCovered.getString("active");
                    String critical = currentCovered.getString("critical");
                    Country coverage = new Country(country, cases, deaths, todayCases, todayDeaths, recovered, active, critical);
                    mCoverages.addCoverage(coverage);
                    mDownloadStatus = RawData.DownloadStatus.OK;

                } catch (JSONException e) {
                    e.getMessage();
                    e.printStackTrace();
                }
            }


            if (destinationUri.equals(PathContract.Link.DATA_GLOBAL)) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    String cases = jsonObject.getString("cases");
                    String deaths = jsonObject.getString("deaths");
                    String recovered = jsonObject.getString("recovered");
                    Global coverage = new Global(cases, deaths, recovered);
                    mCoverages.addCoverage(coverage);
                    mDownloadStatus = RawData.DownloadStatus.OK;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

    }
}
