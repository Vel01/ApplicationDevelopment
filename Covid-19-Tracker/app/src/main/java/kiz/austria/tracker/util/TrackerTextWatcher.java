package kiz.austria.tracker.util;

import android.app.Activity;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

import java.util.ArrayList;

import kiz.austria.tracker.adapter.CountriesRecyclerAdapter;
import kiz.austria.tracker.model.Nation;

public class TrackerTextWatcher implements TextWatcher {

    private static final String TAG = "TrackerTextWatcher";

    private ArrayList<Nation> mNations;
    private CountriesRecyclerAdapter mAdapter;
    private Handler mHandler = new Handler();
    private Activity mActivity;

    public TrackerTextWatcher(ArrayList<Nation> nations, CountriesRecyclerAdapter adapter, Activity activity) {
        mNations = nations;
        mAdapter = adapter;
        mActivity = activity;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

        if (mHandler != null)
            mHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void afterTextChanged(Editable s) {
        filter(s.toString());
        if (s.length() >= 2) {
            mHandler = new Handler();
            final int DELAY = 3000;
            mHandler.postDelayed(() -> {
                Log.d(TAG, "run: manager here...");
                TrackerPlate.hideSoftKeyboard(mActivity);
            }, DELAY);
        }
    }

    private void filter(String s) {
        ArrayList<Nation> nations = new ArrayList<>();
        for (Nation nation : mNations) {
            if (nation.getCountry().toLowerCase().contains(s.toLowerCase())) {
                nations.add(nation);
            }
        }

        mAdapter.addList(nations);
    }
}
