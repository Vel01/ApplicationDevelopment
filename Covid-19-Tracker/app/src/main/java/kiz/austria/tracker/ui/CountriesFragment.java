package kiz.austria.tracker.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.Collections;

import kiz.austria.tracker.R;
import kiz.austria.tracker.adapter.CountriesRecyclerAdapter;
import kiz.austria.tracker.data.Addresses;
import kiz.austria.tracker.data.CountriesDataParser;
import kiz.austria.tracker.data.JSONRawData;
import kiz.austria.tracker.model.Nation;
import kiz.austria.tracker.util.TrackerDialog;
import kiz.austria.tracker.util.TrackerKeys;
import kiz.austria.tracker.util.TrackerPlate;
import kiz.austria.tracker.util.TrackerTextWatcher;

public class CountriesFragment extends Fragment implements
        View.OnClickListener,
        CountriesDataParser.OnDataAvailable {

    private static final String TAG = "CountriesFragment";

    @Override
    public void onDataAvailable(ArrayList<Nation> nations, JSONRawData.DownloadStatus status) {
        if (status == JSONRawData.DownloadStatus.OK) {
            Log.d(TAG, "onDataAvailable() data received from itself: " + nations.toString());
            mNations.addAll(nations);
            if (mNations != null && mNations.size() > 0) {
                displayData();
            }
        }
    }

    /**
     * Navigate back to GlobalFragment using navigation
     * back button.
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.imb_countries_back:
                mListener.onInflateGlobalFragment();
                break;
            case R.id.imb_countries_sort:
                mDialog = new TrackerDialog();
                Bundle args = new Bundle();
                args.putString(TrackerKeys.KEY_STYLE, TrackerKeys.STYLE_DIALOG_CUSTOM);
                args.putString(TrackerKeys.KEY_DIALOG_MESSAGE, null);
                args.putInt(TrackerKeys.KEY_DIALOG_ID, TrackerKeys.ACTION_DIALOG_SORT_MENU);
                mDialog.setView(initSortView());
                mDialog.setArguments(args);
                assert getFragmentManager() != null;
                mDialog.show(getFragmentManager(), null);
                break;
        }
    }

    //vars
    private ArrayList<Nation> mNations = new ArrayList<>();
    private CountriesRecyclerAdapter mCountriesRecyclerAdapter;
    private TrackerDialog mDialog = null;
    private Inflatable mListener;

    //widgets
    private RecyclerView mRecyclerView;
    private EditText mSearch;

    //layouts
    private View mChildShimmer;
    private View mChildMain;
    private ShimmerFrameLayout mShimmerFrameLayout;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity activity = getActivity();
        if (!(activity instanceof Inflatable) && activity != null) {
            throw new ClassCastException(activity.getClass().getSimpleName()
                    + " must implement Inflatable");
        }
        mListener = (Inflatable) activity;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: was called!");
        mShimmerFrameLayout.startShimmer();
        mShimmerFrameLayout.showShimmer(true);
        mChildShimmer.setVisibility(View.VISIBLE);
        mChildMain.setVisibility(View.GONE);
        CountriesDataParser countryNationDataParser = CountriesDataParser.getInstance(this);
        countryNationDataParser.execute(Addresses.Link.DATA_COUNTRIES);
    }

    private void displayData() {
        Log.d(TAG, "displayData() preparing to display");
        Log.d(TAG, "setting up data for display ");
        if (mChildShimmer != null && mChildShimmer.getVisibility() == View.VISIBLE) {
            Log.d(TAG, "data is displayed!");
            mShimmerFrameLayout.stopShimmer();
            mShimmerFrameLayout.hideShimmer();
            mChildShimmer.setVisibility(View.GONE);
            mChildMain.setVisibility(View.VISIBLE);
            mCountriesRecyclerAdapter.addList(mNations);
        }
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_countries, container, false);

        mChildShimmer = view.findViewById(R.id.child_layout_countries_shimmer);
        mChildMain = view.findViewById(R.id.child_layout_countries_main);
        mShimmerFrameLayout = view.findViewById(R.id.layout_countries_shimmer);

        mRecyclerView = view.findViewById(R.id.rv_countries_list);
        mSearch = view.findViewById(R.id.edt_countries_search);


        ImageView btnBack = view.findViewById(R.id.imb_countries_back);
        btnBack.setOnClickListener(this);

        ImageButton btnSort = view.findViewById(R.id.imb_countries_sort);
        btnSort.setOnClickListener(this);

        initRecyclerView();
        return view;
    }

    private void notifyChangedAdapter() {
        LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        assert manager != null;
        //scroll back to top
        manager.scrollToPositionWithOffset(0, 0);
        mCountriesRecyclerAdapter.addList(mNations);
        TrackerPlate.hideSoftKeyboard(getActivity());
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if (mCountriesRecyclerAdapter == null) {
            Log.d(TAG, "initRecyclerView() re-allocate adapter instance");
            mCountriesRecyclerAdapter = new CountriesRecyclerAdapter();
        }
        mRecyclerView.setAdapter(mCountriesRecyclerAdapter);
        initSearchText();
    }

    private void initSearchText() {
        mSearch.addTextChangedListener(new TrackerTextWatcher(mNations, mCountriesRecyclerAdapter, getActivity()));
    }

    private int sort(final int value_one, final int value_two) {

        if (value_one < value_two) {
            return -1;
        } else if (value_one > value_two) {
            return 1;
        }
        return 0;

    }

    private View initSortView() {
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.layout_countries_sort_dialog_container,
                null, false);
        ListView categories = view.findViewById(R.id.rv_countries_sort_list);
        if (getActivity() != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.layout_countries_sort_dialog_item, new String[]{
                    "Confirmed", "Deaths", "Recovered"});
            categories.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            categories.setOnItemClickListener((parent, view1, position, id) -> {
                switch (position) {
                    case TrackerKeys.MENU_SORT_CATEGORY_CONFIRMED:
                        Collections.sort(mNations, (o1, o2) -> sort(Integer.parseInt(o2.getConfirmed()), Integer.parseInt(o1.getConfirmed())));
                        notifyChangedAdapter();
                        break;
                    case TrackerKeys.MENU_SORT_CATEGORY_DEATHS:
                        Collections.sort(mNations, (o1, o2) -> sort(Integer.parseInt(o2.getDeaths()), Integer.parseInt(o1.getDeaths())));
                        notifyChangedAdapter();
                        break;
                    case TrackerKeys.MENU_SORT_CATEGORY_RECOVERED:
                        Collections.sort(mNations, (o1, o2) -> sort(Integer.parseInt(o2.getRecovered()), Integer.parseInt(o1.getRecovered())));
                        notifyChangedAdapter();
                        break;
                }
            });
        }
        return view;
    }

}
