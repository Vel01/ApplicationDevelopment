package kiz.austria.tracker.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.mikephil.charting.charts.LineChart;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import kiz.austria.tracker.R;
import kiz.austria.tracker.broadcast.ConnectivityReceiver;
import kiz.austria.tracker.broadcast.TrackerApplication;
import kiz.austria.tracker.data.Addresses;
import kiz.austria.tracker.data.DataParser;
import kiz.austria.tracker.data.JSONRawData;
import kiz.austria.tracker.data.PHRecordParser;
import kiz.austria.tracker.data.PHTrendDataParser;
import kiz.austria.tracker.model.Nation;
import kiz.austria.tracker.model.PHRecord;
import kiz.austria.tracker.model.PHTrend;
import kiz.austria.tracker.util.TrackerLineChart;
import kiz.austria.tracker.util.TrackerNumber;
import kiz.austria.tracker.util.TrackerUtility;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhilippinesFragment extends Fragment implements DataParser.OnDataAvailable, PHTrendDataParser.OnDataAvailable, ConnectivityReceiver.ConnectivityReceiverListener, PHRecordParser.OnDataAvailable {

    private static final String TAG = "PhilippinesFragment";
    //widget
    @BindView(R.id.chart_ph_cases_trend)
    LineChart lineChart;
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
    private PHRecordParser mPHRecordParser = null;
    private List<PHTrend> mPHTrends;
    private List<PHRecord> mPHRecords;
    //variables
    private int mCountCases;
    private int mCountRecovered;
    private int mCountDeaths;
    private int mCountNewCases;
    private int mCountActive;
    private int mCountNewDeaths;
    private int mCount1to17 = 0;
    private int mCount18to30 = 0;
    private int mCount31to45 = 0;
    private int mCount46to60 = 0;
    private int mCount61up = 0;
    private boolean isPaused = false;

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        Log.d(TAG, "onNetworkConnectionChanged() connected? " + isConnected);
        if (isConnected) {
            mDataParser = new DataParser(this);
            mDataParser.execute(Addresses.Link.DATA_COUNTRIES);

            mPHTrendDataParser = new PHTrendDataParser(this);
            mPHTrendDataParser.setInterestData(PHTrendDataParser.InterestedData.CASUALTIES_ONLY);
            mPHTrendDataParser.execute(Addresses.Link.DATA_TREND_PHILIPPINES);

            mPHRecordParser = new PHRecordParser(this);
            mPHRecordParser.execute(Addresses.Link.DATA_PH_DROP_CASES);
            return;
        }

        TrackerUtility.message(getActivity(), "No Internet Connection",
                R.drawable.ic_signal_wifi_off, R.color.md_white_1000,
                R.color.toast_connection_lost);
    }

    @Override
    public void onDataTrendAvailable(List<PHTrend> trends, JSONRawData.DownloadStatus status) {
        //this method is not supported
    }

    @Override
    public void onDataCasualtiesTrendAvailable(List<PHTrend> casualties, JSONRawData.DownloadStatus status) {
        if (status == JSONRawData.DownloadStatus.OK && !mPHTrendDataParser.isCancelled()) {
            mPHTrends = casualties;
            initLineChart();

            getLatestUpdate(mPHTrends.get(casualties.size() - 1));
        }
    }

    @Override
    public void onDataUnderinvestigationTrendAvailable(List<PHTrend> casualties, JSONRawData.DownloadStatus status) {
        //this method is not supported
    }

    @Override
    public void onDataLastUpdateAvailable(PHTrend date, JSONRawData.DownloadStatus status) {
        //this method is not supported
    }

    private void getLatestUpdate(PHTrend trend) {
        try {
            tvLatestUpdate.setText(TrackerUtility.formatDate(trend.getLatestUpdate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDataAvailable(List<Nation> nations, JSONRawData.DownloadStatus status) {
        if (status == JSONRawData.DownloadStatus.OK && !mDataParser.isCancelled()) {
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
    public void onDataPHRecordAvailable(List<PHRecord> records, JSONRawData.DownloadStatus status) {
        Log.d(TAG, "onDataPHRecordAvailable() status " + status);
        if (status == JSONRawData.DownloadStatus.OK && !mPHRecordParser.isCancelled()) {
            Log.d(TAG, "onDataPHRecordAvailable() size = " + records.size());
            Log.d(TAG, "onDataPHRecordAvailable() data = " + records);
            mPHRecords = records;
            for (PHRecord record : records) {
                if (Integer.parseInt(record.getAge()) >= 61) mCount61up++;
                else if (Integer.parseInt(record.getAge()) <= 60 && Integer.parseInt(record.getAge()) >= 46)
                    mCount46to60++;
                else if (Integer.parseInt(record.getAge()) <= 45 && Integer.parseInt(record.getAge()) >= 31)
                    mCount31to45++;
                else if (Integer.parseInt(record.getAge()) <= 30 && Integer.parseInt(record.getAge()) >= 18)
                    mCount18to30++;
                else if (Integer.parseInt(record.getAge()) <= 17 && Integer.parseInt(record.getAge()) >= 1)
                    mCount1to17++;
            }
            Log.d(TAG, "onDataPHRecordAvailable() " + mCount61up + " " + mCount46to60 + " " + mCount31to45 + " " + mCount18to30 + " " + mCount1to17);
            displayData();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        Log.d(TAG, "onAttach: started");
        super.onAttach(context);
        initTrackerListener();
        if (ConnectivityReceiver.isConnected()) {
            TrackerUtility.message(getActivity(), "No Internet Connection",
                    R.drawable.ic_signal_wifi_off, R.color.md_white_1000,
                    R.color.toast_connection_lost);
        }
    }

    private void initTrackerListener() {
        TrackerApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_philippines, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        return view;
    }

    private void initLineChart() {

        TrackerLineChart chart = new TrackerLineChart(lineChart);
        chart.setAttributes();
        chart.setLeft();
        chart.setRight();
        chart.setXAxis(getLineChartLabel());
        chart.setLegend();
        chart.setLineChartData(mPHTrends);

    }

    private List<String> getLineChartLabel() {
        List<String> labels = new ArrayList<>();

        for (PHTrend trend : mPHTrends) {
            try {
                labels.add(TrackerUtility.formatSimpleDate(trend.getLatestUpdate()));
            } catch (ParseException e) {
                Log.d(TAG, "getLineChartLabel() Parse failed to read");
            }
        }

        return labels;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");
        if (!isPausedToStopReDownload()) {
            mDataParser = new DataParser(this);
            mDataParser.execute(Addresses.Link.DATA_PHILIPPINES);

            mPHTrendDataParser = new PHTrendDataParser(this);
            mPHTrendDataParser.setInterestData(PHTrendDataParser.InterestedData.CASUALTIES_ONLY);
            mPHTrendDataParser.execute(Addresses.Link.DATA_TREND_PHILIPPINES);

            mPHRecordParser = new PHRecordParser(this);
            mPHRecordParser.execute(Addresses.Link.DATA_PH_DROP_CASES);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
        cancelDownload();
        pausedToStopReDownload();
    }

    private void cancelDownload() {
        if (mDataParser != null) mDataParser.cancel(true);
        if (mPHTrendDataParser != null) mPHTrendDataParser.cancel(true);
        if (mPHRecordParser != null) mPHRecordParser.cancel(true);
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
        cancelDownload();
    }


}
