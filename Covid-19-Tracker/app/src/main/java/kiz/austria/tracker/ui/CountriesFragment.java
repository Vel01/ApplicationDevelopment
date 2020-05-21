package kiz.austria.tracker.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.muddzdev.styleabletoast.StyleableToast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import kiz.austria.tracker.R;
import kiz.austria.tracker.adapter.AdapterClickListener;
import kiz.austria.tracker.adapter.CountriesRecyclerAdapter;
import kiz.austria.tracker.broadcast.ConnectivityReceiver;
import kiz.austria.tracker.broadcast.TrackerApplication;
import kiz.austria.tracker.data.Addresses;
import kiz.austria.tracker.data.JSONRawData;
import kiz.austria.tracker.data.ListDataParser;
import kiz.austria.tracker.model.Nation;
import kiz.austria.tracker.util.TrackerDialog;
import kiz.austria.tracker.util.TrackerKeys;
import kiz.austria.tracker.util.TrackerPlate;
import kiz.austria.tracker.util.TrackerTextWatcher;

public class CountriesFragment extends BaseFragment implements
        Returnable,
        AdapterClickListener.OnAdapterClickListener,
        View.OnClickListener,
        ListDataParser.OnDataAvailable, ConnectivityReceiver.ConnectivityReceiverListener {

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

    @Override
    public void onItemClick(View view, int position) {
        Log.d(TAG, "onItemClick()" + position);
        Nation nation = mCountriesRecyclerAdapter.getNationsAdapterList().get(position);
        nation.setExpanded(!nation.isExpanded());
        mCountriesRecyclerAdapter.notifyItemChanged(position, position);
    }

    //widgets
    @BindView(R.id.rv_countries_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.edt_countries_search)
    EditText mSearch;
    @BindView(R.id.tv_countries_label)
    TextView mCountryLabel;
    @BindView(R.id.tv_countries_list_label)
    TextView mSimpleLabel;
    //layouts
    @BindView(R.id.child_layout_countries_shimmer)
    View mChildShimmer;

    //vars
    private boolean isPaused = false;
    @BindView(R.id.child_layout_countries_main)
    View mChildMain;
    @BindView(R.id.constraint_legend)
    ConstraintLayout mLegendLayout;
    @BindView(R.id.fragment_container_countries)
    FrameLayout mSelectedFrameLayout;
    @BindView(R.id.layout_countries_shimmer)
    ShimmerFrameLayout mShimmerFrameLayout;
    //ButterKnife
    private Unbinder mUnbinder;
    //references
    private CountriesRecyclerAdapter mCountriesRecyclerAdapter;
    private ArrayList<Nation> mNations = new ArrayList<>();
    private Inflatable mListener;
    private TrackerDialog mDialog = null;

    @Override
    public void onItemLongClick(View view, int position) {
        Log.d(TAG, "onItemLongClick()");

        Nation nation = mCountriesRecyclerAdapter.getNationsAdapterList().get(position);
        if (mSelectedFrameLayout.getVisibility() == View.GONE) {
            onBackPressed(true);
            mChildMain.setVisibility(View.GONE);
            mSelectedFrameLayout.setVisibility(View.VISIBLE);
            mSimpleLabel.setVisibility(View.INVISIBLE);
            mLegendLayout.setVisibility(View.INVISIBLE);

            mCountryLabel.setText(nation.getCountry());

            SelectedNationFragment fragment = new SelectedNationFragment();

            Bundle args = new Bundle();
            args.putParcelable(TrackerKeys.KEY_SELECTED_COUNTRY, nation);
            fragment.setArguments(args);

            FragmentManager manager = getFragmentManager();
            assert manager != null;
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.replace(R.id.fragment_container_countries, fragment, getString(R.string.tag_fragment_selected_country));
            transaction.commit();
        }

    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        Log.d(TAG, "onNetworkConnectionChanged() connected? " + isConnected);
        if (isConnected) {
            ListDataParser listDataParser = new ListDataParser(this);
            listDataParser.execute(Addresses.Link.DATA_COUNTRIES);
            return;
        }
        new StyleableToast.Builder(Objects.requireNonNull(getActivity())).iconStart(R.drawable.ic_signal_wifi_off)
                .text("No Internet Connection").textColor(getResources().getColor(R.color.md_white_1000))
                .backgroundColor(getResources().getColor(R.color.toast_connection_lost))
                .cornerRadius(10).length(Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        initInflatable();
        initTrackerListener();
        if (ConnectivityReceiver.isConnected()) {
            new StyleableToast.Builder(context).iconStart(R.drawable.ic_signal_wifi_off)
                    .text("No Internet Connection").textColor(getResources().getColor(R.color.md_white_1000))
                    .backgroundColor(getResources().getColor(R.color.toast_connection_lost))
                    .cornerRadius(10).length(Toast.LENGTH_LONG).show();
        }
    }

    private void initTrackerListener() {
        TrackerApplication.getInstance().setConnectivityListener(this);
    }

    private void initInflatable() {
        Activity activity = getActivity();
        if (!(activity instanceof Inflatable) && activity != null) {
            throw new ClassCastException(activity.getClass().getSimpleName()
                    + " must implement Inflatable");
        }
        mListener = (Inflatable) activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_countries, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        ImageView btnBack = view.findViewById(R.id.imb_countries_back);
        btnBack.setOnClickListener(this);

        ImageButton btnSort = view.findViewById(R.id.imb_countries_sort);
        btnSort.setOnClickListener(this);

        initRecyclerView();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: was called!");
        if (!isPausedToStopReDownload()) {
            ListDataParser listDataParser = new ListDataParser(this);
            listDataParser.execute(Addresses.Link.DATA_COUNTRIES);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
        stopShimmer();
        pausedToStopReDownload();
    }

    private void pausedToStopReDownload() {
        isPaused = true;
    }

    private boolean isPausedToStopReDownload() {
        return isPaused;
    }

    private void stopShimmer() {
        mShimmerFrameLayout.stopShimmer();
        mShimmerFrameLayout.hideShimmer();
        mChildShimmer.setVisibility(View.GONE);
        if (mSelectedFrameLayout.getVisibility() == View.GONE)
            mChildMain.setVisibility(View.VISIBLE);
    }

    private void displayData() {
        Log.d(TAG, "displayData() preparing to display");
        Log.d(TAG, "setting up data for display ");
        if (mChildShimmer != null && mChildShimmer.getVisibility() == View.VISIBLE) {
            Log.d(TAG, "data is displayed!");
            stopShimmer();
            mCountriesRecyclerAdapter.addList(mNations);
        }
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
            AdapterClickListener listener = new AdapterClickListener(getActivity(), mRecyclerView);
            listener.setOnAdapterClickListener(this);
            mCountriesRecyclerAdapter = new CountriesRecyclerAdapter();
            mRecyclerView.addOnItemTouchListener(listener);
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
        @SuppressLint("InflateParams") View view = getLayoutInflater().inflate(R.layout.dialog_countries_sort_container,
                null, false);
        ListView categories = view.findViewById(R.id.rv_countries_sort_list);
        if (getActivity() != null) {
            ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.dialog_countries_sort_item, new String[]{
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

    /**
     * Navigate back to GlobalFragment using navigation
     * back button.
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.imb_countries_back:
                if (mSelectedFrameLayout.getVisibility() == View.VISIBLE) {
                    initHideSelectedCountry();
                    break;
                }
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

    private void initHideSelectedCountry() {
        assert getFragmentManager() != null;
        FragmentManager manager = getFragmentManager();
        Fragment fragment = manager.findFragmentByTag(getString(R.string.tag_fragment_selected_country));
        FragmentTransaction transaction = manager.beginTransaction();
        assert fragment != null;
        transaction.remove(fragment);
        transaction.commit();

        onBackPressed(false);
        mCountryLabel.setText(getString(R.string.label_countries));
        mChildMain.setVisibility(View.VISIBLE);
        mSelectedFrameLayout.setVisibility(View.GONE);
        mSimpleLabel.setVisibility(View.VISIBLE);
        mLegendLayout.setVisibility(View.VISIBLE);

    }

    @Override
    public void onBackPressed(boolean returnable) {
        Objects.requireNonNull(getView()).setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener((v, keyCode, event) -> {
            if (returnable) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    // handle back button's click listener
                    initHideSelectedCountry();
                    return true;
                }
            }
            return false;
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView()");
        mUnbinder.unbind();
    }


}
