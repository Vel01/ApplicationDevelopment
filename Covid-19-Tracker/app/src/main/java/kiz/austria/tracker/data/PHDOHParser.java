package kiz.austria.tracker.data;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kiz.austria.tracker.model.PHListDOHDrop;

public class PHDOHParser extends AsyncTask<String, Void, List<PHListDOHDrop>> {

    private static final String TAG = "RawDataParser";

    private ParseData mParseData;
    private List<PHListDOHDrop> mPHListDOHDrop;


    private OnDataAvailable mOnDataAvailable;
    private JSONRawData.DownloadStatus mDownloadStatus;

    public void parse(ParseData parseData) {
        mParseData = parseData;
    }

    public PHDOHParser(OnDataAvailable onDataAvailable) {
        mOnDataAvailable = onDataAvailable;
    }

    @Override
    protected List<PHListDOHDrop> doInBackground(String... path) {
        if (mParseData == ParseData.DOH_DROP) {
            mPHListDOHDrop = new ArrayList<>();
            try {
                JSONObject object = new JSONObject(path[0]);
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

                    mPHListDOHDrop.add(new PHListDOHDrop(caseCode, age, sex, isAdmitted, date_reported, date_died,
                            recovered_on, region_res, prov_city_res, location, latitude, longitude));
                }
                mDownloadStatus = JSONRawData.DownloadStatus.OK;
            } catch (JSONException e) {
                e.printStackTrace();
                mDownloadStatus = JSONRawData.DownloadStatus.FAILED_OR_EMPTY;
            }
        }
        return mPHListDOHDrop;
    }

    @Override
    protected void onPostExecute(List<PHListDOHDrop> dohDrops) {
        if (mOnDataAvailable != null) {
            mOnDataAvailable.onDataPHDOHAvailable(dohDrops, mDownloadStatus);
        }
    }

    public enum ParseData {DOH_DROP}

    private String getObjectLocation(JSONObject doh_drop) {
        try {
            return doh_drop.getString("location");
        } catch (JSONException e) {
            e.getMessage();
        }
        return "";
    }

    public interface OnDataAvailable {
        void onDataPHDOHAvailable(List<PHListDOHDrop> dohDrops, final JSONRawData.DownloadStatus status);
    }
}
