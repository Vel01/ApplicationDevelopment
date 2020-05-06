package kiz.austria.tracker.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
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
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.ArrayList;
import java.util.Collections;

import kiz.austria.tracker.R;
import kiz.austria.tracker.adapter.CountriesRecyclerAdapter;
import kiz.austria.tracker.model.Nation;
import kiz.austria.tracker.util.TrackerDialog;
import kiz.austria.tracker.util.TrackerKeys;
import kiz.austria.tracker.util.TrackerPlate;
import kiz.austria.tracker.util.TrackerTextWatcher;

public class CountriesFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "CountriesFragment";

    /**
     * Navigate back to GlobalFragment using navigation
     * back button.
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.imb_countries_back:

                FragmentManager manager = getFragmentManager();
                assert manager != null;
                manager.popBackStackImmediate(getString(R.string.tag_fragment_global), 0);

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
        getBundleArguments();
    }

    private void getBundleArguments() {
        Bundle args = this.getArguments();
        if (args != null) {
            ArrayList<Nation> nations = args.getParcelableArrayList(getString(R.string.intent_countries));
            if (nations != null) {
                mNations.addAll(nations);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        displayData();
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    private void displayData() {
        Log.d(TAG, "displayData() preparing to display");
        new Handler().postDelayed(() -> {
            Log.d(TAG, "run() setting up data for display ");
            if (mChildShimmer != null && mChildShimmer.getVisibility() == View.VISIBLE) {
                Log.d(TAG, "run() data is displayed!");
                //add all
                mShimmerFrameLayout.stopShimmer();
                mShimmerFrameLayout.hideShimmer();
                mChildShimmer.setVisibility(View.GONE);
                mChildMain.setVisibility(View.VISIBLE);
            }
        }, 700);
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

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        if (mCountriesRecyclerAdapter == null) {
            Log.d(TAG, "initRecyclerView() re-allocate adapter instance");
            mCountriesRecyclerAdapter = new CountriesRecyclerAdapter();
        }
        mRecyclerView.setAdapter(mCountriesRecyclerAdapter);
        mCountriesRecyclerAdapter.addList(mNations);
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

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach()");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView()");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy()");
    }
}
