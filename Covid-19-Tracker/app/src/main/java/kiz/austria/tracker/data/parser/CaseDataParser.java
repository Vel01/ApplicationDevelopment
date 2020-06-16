package kiz.austria.tracker.data.parser;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import kiz.austria.tracker.data.RawDataDownloader.DownloadStatus;
import kiz.austria.tracker.model.Case;

public class CaseDataParser extends AsyncTask<String, Void, ArrayList<Case>> {

    private static final String TAG = "CaseDataParser";

    private ParseData mParseData;
    private ArrayList<Case> mCasesList;


    private OnDataAvailable mOnDataAvailable;
    private DownloadStatus mDownloadStatus;

    public CaseDataParser(OnDataAvailable onDataAvailable) {
        mOnDataAvailable = onDataAvailable;
    }

    public void parse(ParseData parseData) {
        mParseData = parseData;
    }

    @Override
    protected ArrayList<Case> doInBackground(String... path) {
        if (mParseData == ParseData.CASES) {
            mCasesList = new ArrayList<>();
            try {
                JSONObject object = new JSONObject(path[0]);
                JSONArray jsonList = object.getJSONArray("data");
                for (int i = 0; i < jsonList.length(); i++) {
                    JSONObject case_drop = jsonList.getJSONObject(i);
                    String caseCode = getObjectTested(case_drop);
                    String sex = case_drop.getString("sex");
                    String age = case_drop.getString("age");
                    String nationality = case_drop.getString("nationality");
                    String residence_in_the_ph = case_drop.getString("residence_in_the_ph");
                    String travel_history = case_drop.getString("travel_history");
                    String date_of_announcement_to_public = case_drop.getString("date_of_announcement_to_public");
                    String hospital_admitted_to = case_drop.getString("hospital_admitted_to");
                    String health_status = case_drop.getString("health_status");
                    String location = case_drop.getString("location");
                    String latitude = case_drop.getString("latitude");
                    String longitude = case_drop.getString("longitude");

                    mCasesList.add(new Case(caseCode, sex, age, nationality, residence_in_the_ph, travel_history,
                            date_of_announcement_to_public, hospital_admitted_to, health_status, location, latitude, longitude));
                }
                mDownloadStatus = DownloadStatus.OK;
            } catch (JSONException e) {
                e.printStackTrace();
                mDownloadStatus = DownloadStatus.FAILED_OR_EMPTY;
            }
        }

        return mCasesList;
    }


    private String getObjectTested(JSONObject caseNo) {
        try {
            return caseNo.getString("case_code");
        } catch (JSONException e) {
            e.getMessage();
        }
        return "Case No.";
    }


    @Override
    protected void onPostExecute(ArrayList<Case> cases) {
        if (mOnDataAvailable != null) {
            mOnDataAvailable.onDataCasesAvailable(cases, mDownloadStatus);
        }
    }

    public enum ParseData {CASES, PHILIPPINES}

    public interface OnDataAvailable {
        void onDataCasesAvailable(ArrayList<Case> cases, final DownloadStatus status);
    }
}
