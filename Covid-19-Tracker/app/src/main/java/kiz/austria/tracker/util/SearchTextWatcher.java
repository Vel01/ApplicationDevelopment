package kiz.austria.tracker.util;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;

import kiz.austria.tracker.adapter.CountriesRecyclerAdapter;
import kiz.austria.tracker.model.Nation;

public class SearchTextWatcher implements TextWatcher {


    private final int DELAY = 3000;

    private ArrayList<Nation> mNations;
    private CountriesRecyclerAdapter mAdapter;
    private Handler mHandler = new Handler();
    private Activity mActivity;

    public SearchTextWatcher(ArrayList<Nation> nations, CountriesRecyclerAdapter adapter, Activity activity) {
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
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    InputMethodManager manager = (InputMethodManager) mActivity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    View view = mActivity.getCurrentFocus();
                    if (manager != null && view != null) {
                        manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
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

        mAdapter.addFilter(nations);
    }
}
