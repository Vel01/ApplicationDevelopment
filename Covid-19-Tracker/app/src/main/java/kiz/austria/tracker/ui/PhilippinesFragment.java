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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import kiz.austria.tracker.R;
import kiz.austria.tracker.broadcast.ConnectivityReceiver;
import kiz.austria.tracker.broadcast.TrackerApplication;
import kiz.austria.tracker.data.APIFYDataParser;
import kiz.austria.tracker.data.DownloadedData;
import kiz.austria.tracker.data.JSONRawData;
import kiz.austria.tracker.data.NationDataParser;
import kiz.austria.tracker.data.PHDOHDataParser;
import kiz.austria.tracker.data.PHRecordParser;
import kiz.austria.tracker.model.Nation;
import kiz.austria.tracker.model.PHListDOHDrop;
import kiz.austria.tracker.model.PHListUpdatesCases;
import kiz.austria.tracker.model.PHRecord;
import kiz.austria.tracker.model.Philippines;
import kiz.austria.tracker.util.TrackerHorizontalChart;
import kiz.austria.tracker.util.TrackerLineChart;
import kiz.austria.tracker.util.TrackerNumber;
import kiz.austria.tracker.util.TrackerPieChart;
import kiz.austria.tracker.util.TrackerUtility;

import static android.graphics.Color.WHITE;
import static android.graphics.Color.rgb;

/**
 * A simple {@link Fragment} subclass.
 */
public class PhilippinesFragment extends BaseFragment implements ConnectivityReceiver.ConnectivityReceiverListener, PHRecordParser.OnDataAvailable, PHDOHDataParser.OnDataAvailable, OnChartValueSelectedListener, APIFYDataParser.OnDataAvailable, NationDataParser.OnDataAvailable {

    private static final String TAG = "PhilippinesFragment";
    private static final int[] COLORS = {
            rgb(255, 68, 51),
            rgb(0, 141, 203)
    };

    //widget
    @BindView(R.id.chart_ph_cases_trend)
    LineChart lineChart;
    @BindView(R.id.chart_ph_cases_by_age)
    HorizontalBarChart barChart;
    @BindView(R.id.chart_ph_cases_by_gender)
    PieChart pieChart;
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
    @BindView(R.id.container_refresher)
    SwipeRefreshLayout mRefreshLayout;
    //ButterKnife
    private Unbinder mUnbinder;
    //references
    private APIFYDataParser mAPIFYDataParser = null;
    private NationDataParser mNationDataParser = null;
    private PHDOHDataParser mPHDOHDataParser = null;
    //variables
    private int mCountCases;
    private int mCountRecovered;
    private int mCountDeaths;
    private int mCountNewCases;
    private int mCountActive;
    private int mCountNewDeaths;
    private int mCount1to17;
    private int mCount18to30;
    private int mCount31to45;
    private int mCount46to60;
    private int mCount61up;
    private int mGenderMale;
    private int mGenderFemale;
    private boolean isPaused = false;
    private static String mRawDataFromApify;
    private static String mRawDataDOHDropFromHerokuapp;
    private static String mRawDataPhilippinesFromHerokuapp;

    private void resetStats() {
        mCount1to17 = 0;
        mCount18to30 = 0;
        mCount31to45 = 0;
        mCount46to60 = 0;
        mCount61up = 0;
        mGenderMale = 0;
        mGenderFemale = 0;
    }

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        Log.d(TAG, "onNetworkConnectionChanged() connected? " + isConnected);
        if (isConnected) {
            reParseRawData();
        } else {
            TrackerUtility.message(getActivity(), "No Internet Connection",
                    R.drawable.ic_signal_wifi_off, R.color.md_white_1000,
                    R.color.toast_connection_lost);
        }
    }

    @Override
    public void onDataPHRecordAvailable(List<PHRecord> records, JSONRawData.DownloadStatus status) {
        Log.d(TAG, "onDataPHRecordAvailable() status " + status);

    }

    @Override
    public void onDataPHDOHAvailable(List<PHListDOHDrop> dohDrops, JSONRawData.DownloadStatus status) {
        if (status == JSONRawData.DownloadStatus.OK && !mPHDOHDataParser.isCancelled()) {
            Log.d(TAG, "onDataPHDOHAvailable() size = " + dohDrops.size());
            retrievedStats(dohDrops);
            initPieChart();
        }
    }

    private void retrievedStats(List<PHListDOHDrop> dohDrops) {
        for (PHListDOHDrop drop : dohDrops) {

            if (Integer.parseInt(drop.getAge()) >= 61) {
                mCount61up++;
            } else if (Integer.parseInt(drop.getAge()) >= 46 && Integer.parseInt(drop.getAge()) <= 60) {
                mCount46to60++;
            } else if (Integer.parseInt(drop.getAge()) >= 31 && Integer.parseInt(drop.getAge()) <= 45) {
                mCount31to45++;
            } else if (Integer.parseInt(drop.getAge()) >= 18 && Integer.parseInt(drop.getAge()) <= 30) {
                mCount18to30++;
            } else if (Integer.parseInt(drop.getAge()) >= 1 && Integer.parseInt(drop.getAge()) <= 17) {
                mCount1to17++;
            }
            if (drop.getSex().toLowerCase().equals("f")) mGenderFemale++;
            else mGenderMale++;
        }

        initBarChart(mCount1to17 + mCount18to30 + mCount31to45 + mCount46to60 + mCount61up);
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        Log.d("Entry selected", e.toString());
        int value = 0;
        switch ((int) e.getX()) {
            case 4:
                value = mCount61up;
                break;
            case 3:
                value = mCount46to60;
                break;
            case 2:
                value = mCount31to45;
                break;
            case 1:
                value = mCount18to30;
                break;
            case 0:
                value = mCount1to17;
                break;
        }

        TrackerUtility.message(getActivity(), String.valueOf(value),
                R.drawable.ic_confirmed_toast, R.color.red_one, R.color.red_two);
    }

    @Override
    public void onNothingSelected() {

    }

    private void getLatestUpdate(PHListUpdatesCases trend) {
        try {
            tvLatestUpdate.setText(TrackerUtility.formatDate(trend.getLatestUpdate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFullDataAvailable(ArrayList<PHListUpdatesCases> dataList, JSONRawData.DownloadStatus status) {

    }

    @Override
    public void onDateAvailable(PHListUpdatesCases data, JSONRawData.DownloadStatus status) {

    }

    @Override
    public void onEssentialDataAvailable(List<PHListUpdatesCases> dataList, JSONRawData.DownloadStatus status) {
        if (status == JSONRawData.DownloadStatus.OK && !mAPIFYDataParser.isCancelled()) {
            Log.d(TAG, "onEssentialDataAvailable() data received by PhilippinesFragment " + dataList.toString());
            initLineChart(dataList);
            getLatestUpdate(dataList.get(dataList.size() - 1));
        }
    }

    @Override
    public void onBasicDataAvailable(List<PHListUpdatesCases> dataList, JSONRawData.DownloadStatus status) {

    }

    @Override
    public void onCountriesDataAvailable(ArrayList<Nation> nations, JSONRawData.DownloadStatus status) {

    }

    @Override
    public void onPhilippinesDataAvailable(Philippines philippines, JSONRawData.DownloadStatus status) {
        if (status == JSONRawData.DownloadStatus.OK && !mNationDataParser.isCancelled()) {
            Log.d(TAG, "onEssentialDataAvailable() data received by PhilippinesFragment " + philippines);
            mCountCases = Integer.parseInt(philippines.getConfirmed());
            mCountRecovered = Integer.parseInt(philippines.getRecovered());
            mCountDeaths = Integer.parseInt(philippines.getDeaths());
            mCountNewCases = Integer.parseInt(philippines.getTodayCases());
            mCountActive = Integer.parseInt(philippines.getActive());
            mCountNewDeaths = Integer.parseInt(philippines.getTodayDeaths());
            displayData();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        Log.d(TAG, "onAttach: started");
        super.onAttach(context);
        initRawDataForParsing();
        initTrackerListener();
        if (ConnectivityReceiver.isNotConnected()) {
            TrackerUtility.message(getActivity(), "No Internet Connection",
                    R.drawable.ic_signal_wifi_off, R.color.md_white_1000,
                    R.color.toast_connection_lost);
        }
    }

    private void initRawDataForParsing() {
        mRawDataFromApify = DownloadedData.getInstance().getApifyData();
        mRawDataDOHDropFromHerokuapp = DownloadedData.getInstance().getHerokuappDOHData();
        mRawDataPhilippinesFromHerokuapp = DownloadedData.getInstance().getHerokuappPhilippinesData();
    }

    private void initTrackerListener() {
        TrackerApplication.getInstance().setConnectivityListener(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_philippines, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        mNationDataParser = new NationDataParser(this);
        mAPIFYDataParser = new APIFYDataParser(this);
        mPHDOHDataParser = new PHDOHDataParser(this);
        return view;
    }

    private void initLineChart(List<PHListUpdatesCases> casualties) {

        TrackerLineChart chart = new TrackerLineChart(lineChart);
        chart.setAttributes();
        chart.setLeft();
        chart.setRight();
        chart.setXAxis(getLineChartLabel(casualties));
        chart.setLegend();
        chart.setLineChartData(casualties);

    }

    private List<String> getLineChartLabel(List<PHListUpdatesCases> casualties) {
        List<String> labels = new ArrayList<>();

        for (PHListUpdatesCases trend : casualties) {
            try {
                labels.add(TrackerUtility.formatSimpleDate(trend.getLatestUpdate()));
            } catch (ParseException e) {
                Log.d(TAG, "getLineChartLabel() Parse failed to read");
            }
        }

        return labels;
    }

    private void initBarChart(int total) {
        barChart.getLegend().setEnabled(false);
        barChart.setDrawBarShadow(true);
        TrackerHorizontalChart chart = new TrackerHorizontalChart(barChart);
        barChart.setOnChartValueSelectedListener(this);
        chart.attributes();
        chart.setXAxis(XAxis.XAxisPosition.BOTTOM).setValueFormatter(new IndexAxisValueFormatter(Arrays.asList("1-17", "18-30", "31-45", "46-60", "60+")));
        chart.setAxisLeft();
        chart.setAxisRight();
        BarDataSet dataSet = chart.setData(setHorizontalChartData(total), "Cases by Age Group", new int[]{COLORS[0]});
        dataSet.setValueFormatter(new PercentFormatter());
        dataSet.setBarShadowColor(getResources().getColor(R.color.bar_bg));
    }

    private void initPieChart() {

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(mGenderMale, "Male"));
        entries.add(new PieEntry(mGenderFemale, "Female"));
        TrackerPieChart chart = new TrackerPieChart(pieChart, entries, "Cases by Gender");
        chart.initPieChart(tfRegular, 12f, WHITE);
        chart.setLegend();
        PieDataSet dataSet = chart.getDataSet();
        dataSet.setColors(COLORS);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.INSIDE_SLICE);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.INSIDE_SLICE);
        chart.dataSetAttributes(pieChart, 12f, WHITE, tfLight);

    }

    private List<BarEntry> setHorizontalChartData(int total) {
        List<BarEntry> values = new ArrayList<>();

        values.add(new BarEntry(0, new float[]{
                (TrackerUtility.convert(mCount1to17) / total) * 100F}));
        values.add(new BarEntry(1, new float[]{
                (TrackerUtility.convert(mCount18to30) / total) * 100F}));
        values.add(new BarEntry(2, new float[]{
                (TrackerUtility.convert(mCount31to45) / total) * 100F}));
        values.add(new BarEntry(3, new float[]{
                (TrackerUtility.convert(mCount46to60) / total) * 100F}));
        values.add(new BarEntry(4, new float[]{
                (TrackerUtility.convert(mCount61up) / total) * 100F}));

        return values;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume()");

        mRefreshLayout.setOnRefreshListener(() -> {
            reParseRawData();
            mRefreshLayout.setRefreshing(false);
        });

        if (!isPausedToStopReDownload()) {

            if (!mRawDataPhilippinesFromHerokuapp.isEmpty()) {
                mNationDataParser.parse(NationDataParser.ParseData.PHILIPPINES);
                mNationDataParser.execute(mRawDataPhilippinesFromHerokuapp);
            }


            if (!mRawDataFromApify.isEmpty()) {
                mAPIFYDataParser.parse(APIFYDataParser.ParseData.ESSENTIAL_DATA);
                mAPIFYDataParser.execute(mRawDataFromApify);
            }

            if (!mRawDataDOHDropFromHerokuapp.isEmpty()) {
                mPHDOHDataParser.parse(PHDOHDataParser.ParseData.DOH_DROP);
                mPHDOHDataParser.execute(mRawDataDOHDropFromHerokuapp);
            }
        }
    }

    private void reParseRawData() {
        resetStats();
        initRawDataForParsing();
        restartShimmer();

        if (!mRawDataPhilippinesFromHerokuapp.isEmpty() && mNationDataParser.getStatus() != NationDataParser.Status.RUNNING) {
            mNationDataParser.cancel(true);
            mNationDataParser = new NationDataParser(PhilippinesFragment.this);
            mNationDataParser.parse(NationDataParser.ParseData.PHILIPPINES);
            mNationDataParser.execute(mRawDataPhilippinesFromHerokuapp);
        }

        if (!mRawDataFromApify.isEmpty() && mAPIFYDataParser.getStatus() != APIFYDataParser.Status.RUNNING) {
            mAPIFYDataParser.cancel(true);
            mAPIFYDataParser = new APIFYDataParser(PhilippinesFragment.this);
            mAPIFYDataParser.parse(APIFYDataParser.ParseData.ESSENTIAL_DATA);
            mAPIFYDataParser.execute(mRawDataFromApify);
        }

        if (!mRawDataDOHDropFromHerokuapp.isEmpty() && mPHDOHDataParser.getStatus() != PHDOHDataParser.Status.RUNNING) {
            mPHDOHDataParser.cancel(true);
            mPHDOHDataParser = new PHDOHDataParser(PhilippinesFragment.this);
            mPHDOHDataParser.parse(PHDOHDataParser.ParseData.DOH_DROP);
            mPHDOHDataParser.execute(mRawDataDOHDropFromHerokuapp);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
        pausedToStopReDownload();
    }

    private void cancelDownload() {
        if (mNationDataParser != null) mNationDataParser.cancel(true);
        if (mPHDOHDataParser != null) mPHDOHDataParser.cancel(true);
        if (mAPIFYDataParser != null) mAPIFYDataParser.cancel(true);
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

    private void restartShimmer() {
        mChildShimmer.setVisibility(View.VISIBLE);
        mChildMain.setVisibility(View.INVISIBLE);
        mShimmerFrameLayout.startShimmer();
        mShimmerFrameLayout.showShimmer(true);
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