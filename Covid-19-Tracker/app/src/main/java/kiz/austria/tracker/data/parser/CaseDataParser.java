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
                    JSONObject case_data = jsonList.getJSONObject(i);
                    String caseCode = case_data.getString("case_no");
                    String sex = getSex(case_data);
                    String age = getAge(case_data);
                    String nationality = case_data.getString("nationality");
                    String residence_in_the_ph = case_data.getString("residence_in_the_ph");
                    String travel_history = case_data.getString("travel_history");
                    String date_of_announcement_to_public = case_data.getString("date_of_announcement_to_public");
                    String hospital_admitted_to = case_data.getString("hospital_admitted_to");
                    String health_status = case_data.getString("health_status");
                    String location = case_data.getString("location");
                    String latitude = case_data.getString("latitude");
                    String longitude = case_data.getString("longitude");

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


    private String getSex(JSONObject case_data) {
        final String DEFAULT = "TBA";
        try {
            String current = case_data.getString("sex").toLowerCase();
            if (current.equals("m")) return "Male";
            else if (current.equals("f")) return "Female";
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return DEFAULT;
    }

    private String getAge(JSONObject case_data) {
        final String DEFAULT = "TBA";
        try {
            String current = case_data.getString("age");
            if (Character.isDigit(current.charAt(0))) return current;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return DEFAULT;
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
