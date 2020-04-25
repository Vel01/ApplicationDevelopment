package kiz.austria.tracker.data;

import android.os.AsyncTask;

import org.json.JSONException;
import org.json.JSONObject;

import kiz.austria.tracker.model.Global;

public class GlobalDataParser extends AsyncTask<String, Void, Global> implements JSONRawData.OnDownloadComplete {

    private static final String TAG = "RawDataParser";

    private OnDataAvailable mOnDataAvailable;
    private JSONRawData.DownloadStatus mDownloadStatus;

    private Global mGlobal;
    private String destinationUri;

    public interface OnDataAvailable {
        void onDataAvailable(Global coverage, final JSONRawData.DownloadStatus status);
    }

    private GlobalDataParser(OnDataAvailable onDataAvailable) {
        mOnDataAvailable = onDataAvailable;
    }

    public static GlobalDataParser getInstance(OnDataAvailable onDataAvailable) {
        return new GlobalDataParser(onDataAvailable);
    }

    public void setOnDataAvailable(OnDataAvailable onDataAvailable) {
        mOnDataAvailable = onDataAvailable;
    }


    @Override
    protected Global doInBackground(String... path) {
        destinationUri = path[0];
        JSONRawData JSONRawData = new JSONRawData(this);
        JSONRawData.runInTheSameThread(destinationUri);
        return mGlobal;
    }

    @Override
    protected void onPostExecute(Global countriesListModel) {
        if (mOnDataAvailable != null) {
            mOnDataAvailable.onDataAvailable(countriesListModel, mDownloadStatus);
        }
    }

    @Override
    public void onDownloadComplete(String data, JSONRawData.DownloadStatus status) {
        if (status == JSONRawData.DownloadStatus.OK) {

//            if (destinationUri.equals(PathContract.Link.DATA_COUNTRIES)) {
//                try {
//                    JSONArray jsonList = new JSONArray(data);
//                    for (int i = 0; i < jsonList.length(); i++) {
//                        JSONObject currentCovered = jsonList.getJSONObject(i);
//                        String country = currentCovered.getString("country");
//                        String cases = currentCovered.getString("cases");
//                        String todayCases = currentCovered.getString("todayCases");
//                        String deaths = currentCovered.getString("deaths");
//                        String todayDeaths = currentCovered.getString("todayDeaths");
//                        String recovered = currentCovered.getString("recovered");
//                        String active = currentCovered.getString("active");
//                        String critical = currentCovered.getString("critical");
//                        Countries coverage = new Countries(country, cases, deaths, todayCases, todayDeaths, recovered, active, critical);
//                        mGlobal.add(coverage);
//                    }
//                    mDownloadStatus = RawData.DownloadStatus.OK;
//
//                } catch (JSONException e) {
//                    e.getMessage();
//                    e.printStackTrace();
//                }
//            }

//            if (destinationUri.equals(PathContract.Link.DATA_COUNTRY)) {
//                try {
//                    JSONObject currentCovered = new JSONObject(data);
//                    String country = currentCovered.getString("country");
//                    String cases = currentCovered.getString("cases");
//                    String todayCases = currentCovered.getString("todayCases");
//                    String deaths = currentCovered.getString("deaths");
//                    String todayDeaths = currentCovered.getString("todayDeaths");
//                    String recovered = currentCovered.getString("recovered");
//                    String active = currentCovered.getString("active");
//                    String critical = currentCovered.getString("critical");
//                    Country coverage = new Country(country, cases, deaths, todayCases, todayDeaths, recovered, active, critical);
//                    mGlobal.addCoverage(coverage);
//                    mDownloadStatus = RawData.DownloadStatus.OK;
//
//                } catch (JSONException e) {
//                    e.getMessage();
//                    e.printStackTrace();
//                }
//            }


            if (destinationUri.equals(Addresses.Link.DATA_GLOBAL)) {
                try {
                    JSONObject jsonObject = new JSONObject(data);
                    String cases = jsonObject.getString("cases");
                    String deaths = jsonObject.getString("deaths");
                    String recovered = jsonObject.getString("recovered");
                    mGlobal = new Global(cases, deaths, recovered);
                    mDownloadStatus = JSONRawData.DownloadStatus.OK;
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }

    }
}
