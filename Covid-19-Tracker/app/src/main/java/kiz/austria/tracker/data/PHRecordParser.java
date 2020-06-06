package kiz.austria.tracker.data;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import kiz.austria.tracker.model.PHRecord;

public class PHRecordParser extends AsyncTask<String, Void, List<PHRecord>> implements JSONRawData.OnDownloadComplete {

    private static final String TAG = "RawDataParser";

    private OnDataAvailable mOnDataAvailable;
    private JSONRawData.DownloadStatus mDownloadStatus;

    private List<PHRecord> mPHRecord;
    private String destinationUri;

    public PHRecordParser(OnDataAvailable onDataAvailable) {
        mOnDataAvailable = onDataAvailable;
    }

    @Override
    protected List<PHRecord> doInBackground(String... path) {
        destinationUri = path[0];
        JSONRawData JSONRawData = new JSONRawData(this);
        JSONRawData.runInTheSameThread(destinationUri);
        return mPHRecord;
    }

    @Override
    protected void onPostExecute(List<PHRecord> records) {
        if (mOnDataAvailable != null) {
            mOnDataAvailable.onDataPHRecordAvailable(records, mDownloadStatus);
        }
    }

    @Override
    public void onDownloadCompleteFromApify(String data, JSONRawData.DownloadStatus status) {
        if (status == JSONRawData.DownloadStatus.OK) {

            if (destinationUri.equals(Addresses.Link.DATA_PHILIPPINES_CASES_FROM_HEROKUAPP)) {
                mPHRecord = new ArrayList<>();
                try {
                    JSONObject object = new JSONObject(data);
                    JSONArray jsonList = object.getJSONArray("data");
                    for (int i = 0; i < jsonList.length(); i++) {
                        JSONObject currentCovered = jsonList.getJSONObject(i);
                        String caseNo = currentCovered.getString("case_no");
                        String gender = currentCovered.getString("sex");
                        String age = currentCovered.getString("age");
                        if (age.equals("null")) age = "0";
                        String nationality = currentCovered.getString("nationality");
                        String residencePh = currentCovered.getString("residence_in_the_ph");
                        String travelHistory = currentCovered.getString("travel_history");
                        String dateOfAnnouncement = currentCovered.getString("date_of_announcement_to_public");
                        String hospitalAdmittedTo = currentCovered.getString("hospital_admitted_to");
                        String healthStatus = currentCovered.getString("health_status");
                        String location = currentCovered.getString("location");
                        String latitude = currentCovered.getString("residence_lat");
                        String longitude = currentCovered.getString("residence_long");
                        mPHRecord.add(new PHRecord(caseNo, gender, age, nationality, residencePh, travelHistory,
                                dateOfAnnouncement, hospitalAdmittedTo, healthStatus, location, latitude, longitude));
                    }
                    mDownloadStatus = JSONRawData.DownloadStatus.OK;
                } catch (JSONException e) {
                    e.printStackTrace();
                    mDownloadStatus = JSONRawData.DownloadStatus.FAILED_OR_EMPTY;
                }
            }
        }
    }

    @Override
    public void onDownloadCompleteDOHDataFromHerokuapp(String data, JSONRawData.DownloadStatus status) {

    }

    @Override
    public void onDownloadCompletePhilippinesDataFromHerokuapp(String data, JSONRawData.DownloadStatus status) {

    }

    public interface OnDataAvailable {
        void onDataPHRecordAvailable(List<PHRecord> records, final JSONRawData.DownloadStatus status);
    }
}
