package kiz.austria.tracker.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.muddzdev.styleabletoast.StyleableToast;

import java.text.ParseException;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import kiz.austria.tracker.R;
import kiz.austria.tracker.broadcast.ConnectivityReceiver;
import kiz.austria.tracker.broadcast.TrackerApplication;
import kiz.austria.tracker.data.Addresses;
import kiz.austria.tracker.data.DataParser;
import kiz.austria.tracker.data.JSONRawData;
import kiz.austria.tracker.data.PHTrendDataParser;
import kiz.austria.tracker.model.Nation;
import kiz.austria.tracker.model.PHTrend;
import kiz.austria.tracker.util.TrackerNumber;
import kiz.austria.tracker.util.TrackerUtility;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhilippinesFragment extends Fragment implements DataParser.OnDataAvailable, PHTrendDataParser.OnDataAvailable, ConnectivityReceiver.ConnectivityReceiverListener {

    private static final String TAG = "PhilippinesFragment";
    //widget
    @BindView(R.id.tv_cases)
    TextView tvCases;
    @BindView(R.id.tv_recovered)
    TextView tvRecovered;
    @BindView(R.id.tv_deaths)
    TextView tvDeaths;
    @BindView(R.id.tv_new_cases)
    TextView tvNewCases;
    @BindView(R.id.tv_active)
    TextView tvActive;
    @BindView(R.id.tv_new_deaths)
    TextView tvNewDeaths;
    @BindView(R.id.tv_update_date)
    TextView tvLatestUpdate;
    //layouts
    @BindView(R.id.layout_shimmer)
    ShimmerFrameLayout mShimmerFrameLayout;
    @BindView(R.id.include_layout_ph_results)
    ConstraintLayout mChildMain;
    @BindView(R.id.child_layout_ph_shimmer)
    View mChildShimmer;
    //ButterKnife
    private Unbinder mUnbinder;
    //references
    private DataParser mDataParser = null;
    private PHTrendDataParser mPHTrendDataParser = null;
    //variables
    private int mCountCases;
    private int mCountRecovered;
    private int mCountDeaths;
    private int mCountNewCases;
    private int mCountActive;
    private int mCountNewDeaths;
    private boolean isPaused = false;

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        Log.d(TAG, "onNetworkConnectionChanged() connected? " + isConnected);
        if (isConnected) {
            mDataParser = new DataParser(this);
            mDataParser.execute(Addresses.Link.DATA_COUNTRIES);

            mPHTrendDataParser = new PHTrendDataParser(this);
            mPHTrendDataParser.execute(Addresses.Link.DATA_TREND_PHILIPPINES);
            return;
        }

        new StyleableToast.Builder(Objects.requireNonNull(getActivity())).iconStart(R.drawable.ic_signal_wifi_off)
                .text("No Internet Connection").textColor(getResources().getColor(R.color.md_white_1000))
                .backgroundColor(getResources().getColor(R.color.toast_connection_lost))
                .cornerRadius(10).length(Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDataTrendAvailable(List<PHTrend> trends, JSONRawData.DownloadStatus status) {

        if (status == JSONRawData.DownloadStatus.OK) {
            PHTrend trend = trends.get(trends.size() - 1);
            setLatestUpdate(trend);
            displayData();
        }

    }

    private void setLatestUpdate(PHTrend trend) {
        Log.d(TAG, "onDataTrendAvailable() " + trend.getLatestUpdate());
        try {
            tvLatestUpdate.setText(TrackerUtility.formatDate(trend.getLatestUpdate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDataAvailable(List<Nation> nations, JSONRawData.DownloadStatus status) {
        if (status == JSONRawData.DownloadStatus.OK) {
            Log.d(TAG, "onDataAvailable() data received successfully!");
            Nation nation = nations.get(0);
            mCountCases = Integer.parseInt(nation.getConfirmed());
            mCountRecovered = Integer.parseInt(nation.getRecovered());
            mCountDeaths = Integer.parseInt(nation.getDeaths());
            mCountNewCases = Integer.parseInt(nation.getTodayCases());
            mCountActive = Integer.parseInt(nation.getActive());
            mCountNewDeaths = Integer.parseInt(nation.getTodayDeaths());
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        Log.d(TAG, "onAttach: started");
        super.onAttach(context);
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_philippines, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        if (!isPausedToStopReDownload()) {
            mDataParser = new DataParser(this);
            mDataParser.execute(Addresses.Link.DATA_PHILIPPINES);

            mPHTrendDataParser = new PHTrendDataParser(this);
            mPHTrendDataParser.execute(Addresses.Link.DATA_TREND_PHILIPPINES);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
        pausedToStopReDownload();
    }

    private void pausedToStopReDownload() {
        isPaused = true;
    }

    private boolean isPausedToStopReDownload() {
        return isPaused;
    }

    private void displayData() {
        Log.d(TAG, "displayData() preparing to display");
        if (mChildShimmer != null && mChildShimmer.getVisibility() == View.VISIBLE) {
            Log.d(TAG, "data is displayed!");
            stopShimmer();
            TrackerNumber.display(tvCases, mCountCases);
            TrackerNumber.display(tvDeaths, mCountDeaths);
            TrackerNumber.display(tvRecovered, mCountRecovered);
            TrackerNumber.display(tvActive, mCountActive);
            TrackerNumber.display(tvNewCases, mCountNewCases);
            TrackerNumber.display(tvNewDeaths, mCountNewDeaths);
        }
    }

    private void stopShimmer() {
        mShimmerFrameLayout.stopShimmer();
        mShimmerFrameLayout.hideShimmer();
        mChildShimmer.setVisibility(View.GONE);
        mChildMain.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView()");
        mUnbinder.unbind();
        mDataParser.cancel(true);
        mPHTrendDataParser.cancel(true);
    }

}
