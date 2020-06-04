package kiz.austria.tracker.data;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kiz.austria.tracker.model.PHDOHDrop;

public class PHDOHParser extends AsyncTask<String, Void, List<PHDOHDrop>> implements JSONRawData.OnDownloadComplete {

    private static final String TAG = "RawDataParser";

    private OnDataAvailable mOnDataAvailable;
    private JSONRawData.DownloadStatus mDownloadStatus;

    private List<PHDOHDrop> mPHDOHDrop;
    private String destinationUri;

    public PHDOHParser(OnDataAvailable onDataAvailable) {
        mOnDataAvailable = onDataAvailable;
    }

    @Override
    protected List<PHDOHDrop> doInBackground(String... path) {
        destinationUri = path[0];
        JSONRawData JSONRawData = new JSONRawData(this);
        JSONRawData.runInTheSameThread(destinationUri);
        return mPHDOHDrop;
    }

    @Override
    protected void onPostExecute(List<PHDOHDrop> dohDrops) {
        if (mOnDataAvailable != null) {
            mOnDataAvailable.onDataPHDOHAvailable(dohDrops, mDownloadStatus);
        }
    }

    @Override
    public void onDownloadComplete(String data, JSONRawData.DownloadStatus status) {
        if (status == JSONRawData.DownloadStatus.OK) {

            if (destinationUri.equals(Addresses.Link.DATA_PH_DROP_DOH)) {
                mPHDOHDrop = new ArrayList<>();
                try {
                    JSONObject object = new JSONObject(data);
                    JSONArray jsonList = object.getJSONArray("data");
                    for (int i = 0; i < jsonList.length(); i++) {
                        JSONObject doh_drop = jsonList.getJSONObject(i);
                        String caseCode = doh_drop.getString("case_code");
                        String age = doh_drop.getString("age");
                        String sex = doh_drop.getString("sex");
                        String isAdmitted = doh_drop.getString("is_admitted");
                        String date_reported = doh_drop.getString("date_reported");
                        String date_died = doh_drop.getString("date_died");
                        String recovered_on = doh_drop.getString("recovered_on");
                        String region_res = doh_drop.getString("region_res");
                        String prov_city_res = doh_drop.getString("prov_city_res");
                        String location = getObjectLocation(doh_drop);
                        String latitude = doh_drop.getString("latitude");
                        String longitude = doh_drop.getString("longitude");

                        mPHDOHDrop.add(new PHDOHDrop(caseCode, age, sex, isAdmitted, date_reported, date_died,
                                recovered_on, region_res, prov_city_res, location, latitude, longitude));
                    }
                    mDownloadStatus = JSONRawData.DownloadStatus.OK;
                } catch (JSONException e) {
                    e.printStackTrace();
                    mDownloadStatus = JSONRawData.DownloadStatus.FAILED_OR_EMPTY;
                }
            }
        }
    }

    private String getObjectLocation(JSONObject doh_drop) {
        try {
            return doh_drop.getString("location");
        } catch (JSONException e) {
            e.getMessage();
        }
        return "";
    }

    public interface OnDataAvailable {
        void onDataPHDOHAvailable(List<PHDOHDrop> dohDrops, final JSONRawData.DownloadStatus status);
    }
}