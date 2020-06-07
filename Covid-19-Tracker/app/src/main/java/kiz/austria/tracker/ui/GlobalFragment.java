package kiz.austria.tracker.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import kiz.austria.tracker.R;
import kiz.austria.tracker.broadcast.ConnectivityReceiver;
import kiz.austria.tracker.broadcast.TrackerApplication;
import kiz.austria.tracker.data.APIFYDataParser;
import kiz.austria.tracker.data.DataParser;
import kiz.austria.tracker.data.DownloadedData;
import kiz.austria.tracker.data.JSONRawData;
import kiz.austria.tracker.data.NationDataParser;
import kiz.austria.tracker.data.PHTrendDataParser;
import kiz.austria.tracker.model.Nation;
import kiz.austria.tracker.model.PHListUpdatesCases;
import kiz.austria.tracker.model.Philippines;
import kiz.austria.tracker.util.TrackerHorizontalChart;
import kiz.austria.tracker.util.TrackerNumber;
import kiz.austria.tracker.util.TrackerPieChart;
import kiz.austria.tracker.util.TrackerUtility;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.rgb;

public class GlobalFragment extends BaseFragment implements
        View.OnClickListener, PHTrendDataParser.OnDataAvailable, DataParser.OnDataAvailable, ConnectivityReceiver.ConnectivityReceiverListener, OnChartValueSelectedListener, NationDataParser.OnDataAvailable, APIFYDataParser.OnDataAvailable {

    private static final String TAG = "GlobalFragment";

    //widgets
    @BindView(R.id.chart_global_top_10)
    HorizontalBarChart horizontalChart;
    //layouts
    @BindView(R.id.layout_shimmer)
    ShimmerFrameLayout mShimmerFrameLayout;
    //ButterKnife
    private Unbinder mUnbinder;

    @Override
    public void onDataAvailable(List<Nation> nations, JSONRawData.DownloadStatus status) {
//        if (status == JSONRawData.DownloadStatus.OK && !mDataParser.isCancelled()) {
//
//            Collections.sort(nations, (o1, o2) ->
//                    TrackerUtility.sort(Integer.parseInt(o1.getTodayCases()),
//                            Integer.parseInt(o2.getTodayCases())));
//            for (int i = 0; i < nations.size(); i++) {
//                Nation nation = nations.get(i);
//                if (nation.getCountry().toLowerCase().equals("world")) {
//                    Log.d(TAG, "onDataAvailable() data received from itself: " + nation.toString());
//                    disCases = Integer.parseInt(nation.getConfirmed());
//                    disDeaths = Integer.parseInt(nation.getDeaths());
//                    disRecovered = Integer.parseInt(nation.getRecovered());
//                    disNewCases = Integer.parseInt(nation.getTodayCases());
//                    disNewDeaths = Integer.parseInt(nation.getTodayDeaths());
//                    disActive = Integer.parseInt(nation.getActive());
//                    continue;
//                }
//                if (topNations.size() != 10) {
//                    topNations.add(nation);
//                    continue;
//                }
//                break;
//            }
//            Log.d(TAG, "onDataAvailable: " + topNations.toString());
//        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        BarEntry entry = (BarEntry) e;

        if (entry.getYVals() != null) {
            Log.i("VAL SELECTED", "Value: " + entry.getYVals()[h.getStackIndex()]);


            switch (h.getStackIndex()) {
                case 0:
                    TrackerUtility.message(getActivity(), TrackerUtility.format(entry.getYVals()[h.getStackIndex()]),
                            R.drawable.ic_confirmed_toast, R.color.red_one, R.color.red_two);
                    break;
                case 1:
                    TrackerUtility.message(getActivity(), TrackerUtility.format(entry.getYVals()[h.getStackIndex()]),
                            R.drawable.ic_death_toast, R.color.grey_one, R.color.grey_two);
                    break;
                default:
            }


        }
    }

    @BindView(R.id.chart_global_cases)
    PieChart chart;
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
    TextView tvUpdate;
    @BindView(R.id.btn_view_all_countries)
    CardView btnViewAllCountries;
    @BindView(R.id.include_layout_global_results)
    ConstraintLayout mChildMain;
    @BindView(R.id.child_layout_global_shimmer)
    View mChildShimmer;
    @BindView(R.id.container_refresher)
    SwipeRefreshLayout mRefreshLayout;

    //vars
    private boolean isPaused = false;
    private int disCases, disDeaths, disRecovered, disActive, disNewCases, disNewDeaths;
    private static String mRawDataCountriesFromHerokuapp;
    private static String mRawDataDateFromApify;

    //references
    private List<Nation> topNations = new ArrayList<>();
    private Inflatable mListener;
    private DataParser mDataParser = null;
    private NationDataParser mNationDataParser = null;
    private APIFYDataParser mAPIFYDataParser = null;
    private PHTrendDataParser mPHTrendDataParser = null;

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        Log.d(TAG, "onNetworkConnectionChanged() connected? " + isConnected);
        if (isConnected) {
//            mDataParser = new DataParser(this);
//            mDataParser.execute(Addresses.Link.DATA_COUNTRIES_FROM_HEROKUAPP);
//
//            mPHTrendDataParser = new PHTrendDataParser(this);
//            mPHTrendDataParser.setInterestData(PHTrendDataParser.InterestedData.DATE_ONLY);
//            mPHTrendDataParser.execute(Addresses.Link.DATA_PHILIPPINES_FROM_APIFY);

            mRawDataCountriesFromHerokuapp = DownloadedData.getInstance().getHerokuappCountriesData();
            mRawDataDateFromApify = DownloadedData.getInstance().getApifyData();

            if (!mRawDataCountriesFromHerokuapp.isEmpty() && mNationDataParser.getStatus() != NationDataParser.Status.RUNNING) {
                mNationDataParser.cancel(true);
                mNationDataParser = new NationDataParser(GlobalFragment.this);
                mNationDataParser.parse(NationDataParser.ParseData.COUNTRIES);
                mNationDataParser.execute(mRawDataCountriesFromHerokuapp);
            }

            if (!mRawDataDateFromApify.isEmpty() && mAPIFYDataParser.getStatus() != APIFYDataParser.Status.RUNNING) {
                mAPIFYDataParser.cancel(true);
                mAPIFYDataParser = new APIFYDataParser(GlobalFragment.this);
                mAPIFYDataParser.parse(APIFYDataParser.ParseData.DATE_ONLY);
                mAPIFYDataParser.execute(mRawDataDateFromApify);
            }
            return;
        }
        TrackerUtility.message(getActivity(), "No Internet Connection",
                R.drawable.ic_signal_wifi_off, R.color.md_white_1000,
                R.color.toast_connection_lost);
    }

    private static final int[] JOYFUL_COLORS = {
            Color.rgb(255, 68, 51), Color.rgb(233, 236, 239)
    };

    @Override
    public void onDataTrendAvailable(List<PHListUpdatesCases> trends, JSONRawData.DownloadStatus status) {
        //this method is not supported
    }

    @Override
    public void onDataCasualtiesTrendAvailable(List<PHListUpdatesCases> casualties, JSONRawData.DownloadStatus status) {
        //this method is not supported
    }

    @Override
    public void onDataUnderinvestigationTrendAvailable(List<PHListUpdatesCases> casualties, JSONRawData.DownloadStatus status) {
        //this method is not supported
    }

    @Override
    public void onDataLastUpdateAvailable(PHListUpdatesCases date, JSONRawData.DownloadStatus status) {
//        if (status == JSONRawData.DownloadStatus.OK && !mPHTrendDataParser.isCancelled()) {
//            setLatestUpdate(date);
//            displayData();
//        }
    }

    @Override
    public void onNothingSelected() {
        //this method is not supported
    }

    @Override
    public void onCountriesDataAvailable(ArrayList<Nation> nations, JSONRawData.DownloadStatus status) {
        if (status == JSONRawData.DownloadStatus.OK && !mNationDataParser.isCancelled()) {

            Collections.sort(nations, (o1, o2) ->
                    TrackerUtility.sort(Integer.parseInt(o1.getTodayCases()),
                            Integer.parseInt(o2.getTodayCases())));
            for (int i = 0; i < nations.size(); i++) {
                Nation nation = nations.get(i);
                if (nation.getCountry().toLowerCase().equals("world")) {
                    Log.d(TAG, "onCountriesDataAvailable() data received from itself.");
                    disCases = Integer.parseInt(nation.getConfirmed());
                    disDeaths = Integer.parseInt(nation.getDeaths());
                    disRecovered = Integer.parseInt(nation.getRecovered());
                    disNewCases = Integer.parseInt(nation.getTodayCases());
                    disNewDeaths = Integer.parseInt(nation.getTodayDeaths());
                    disActive = Integer.parseInt(nation.getActive());
                    continue;
                }
                if (topNations.size() != 10) {
                    topNations.add(nation);
                    continue;
                }
                break;
            }
            displayData();
        }
    }

    @Override
    public void onPhilippinesDataAvailable(Philippines philippines, JSONRawData.DownloadStatus status) {

    }

    @Override
    public void onFullDataAvailable(ArrayList<PHListUpdatesCases> dataList, JSONRawData.DownloadStatus status) {

    }

    @Override
    public void onDateAvailable(PHListUpdatesCases data, JSONRawData.DownloadStatus status) {
        if (status == JSONRawData.DownloadStatus.OK && !mAPIFYDataParser.isCancelled()) {
            setLatestUpdate(data);
        }
    }

    @Override
    public void onEssentialDataAvailable(List<PHListUpdatesCases> dataList, JSONRawData.DownloadStatus status) {

    }

    @Override
    public void onBasicDataAvailable(List<PHListUpdatesCases> dataList, JSONRawData.DownloadStatus status) {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        Log.d(TAG, "onAttach: started");
        super.onAttach(context);

        mRawDataCountriesFromHerokuapp = DownloadedData.getInstance().getHerokuappCountriesData();
        mRawDataDateFromApify = DownloadedData.getInstance().getApifyData();

        initInflatable();
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

    private void initInflatable() {
        Activity activity = getActivity();
        if (!(activity instanceof Inflatable) && activity != null) {
            throw new ClassCastException(activity.getClass().getSimpleName()
                    + " must implement Inflatable interface");
        }
        mListener = (Inflatable) activity;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: started");
        final View view = inflater.inflate(R.layout.fragment_global, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        //update widget
        tvUpdate.setText(TrackerUtility.getCurrentDate());
        //go to countries widget
        btnViewAllCountries.setOnClickListener(this);

        mNationDataParser = new NationDataParser(this);
        mAPIFYDataParser = new APIFYDataParser(this);
        return view;
    }

    private void setLatestUpdate(PHListUpdatesCases trend) {
        Log.d(TAG, "onDataTrendAvailable() " + trend.getLatestUpdate());
        try {
            tvUpdate.setText(TrackerUtility.formatDate(trend.getLatestUpdate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void initPieChart() {

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(disCases, "Confirmed"));
        entries.add(new PieEntry(disDeaths, "Deaths"));
        entries.add(new PieEntry(disRecovered, "Recovered"));

        // add a lot of colors
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(rgb(255, 68, 51));
        colors.add(rgb(233, 236, 239));
        colors.add(rgb(151, 242, 149));
        TrackerPieChart pieChart = new TrackerPieChart(chart, entries, null);
        pieChart.initPieChart(tfRegular, 10f, BLACK);
        pieChart.setLegend();
        pieChart.dataSet(colors);
        pieChart.dataSetAttributes(chart, 10f, Color.BLACK, tfLight);

    }

    private void initHorizontalChart() {

        TrackerHorizontalChart chart = new TrackerHorizontalChart(horizontalChart);
        horizontalChart.setOnChartValueSelectedListener(this);
        chart.setFonts(tfLight);
        chart.attributes();
        chart.setXAxis(XAxis.XAxisPosition.TOP).
                setValueFormatter(new IndexAxisValueFormatter(setHorizontalChartLabel()));
        chart.setAxisLeft();
        chart.setAxisRight();
        chart.setLegend(setEntries());
        chart.setData(setHorizontalChartData(), "Top Daily Cases", JOYFUL_COLORS);

    }

    private List<LegendEntry> setEntries() {
        return new ArrayList<>(Arrays.asList(
                new LegendEntry("Daily New Cases", Legend.LegendForm.SQUARE, 9f, 5, null, Color.rgb(255, 68, 51)),
                new LegendEntry("Daily New Deaths", Legend.LegendForm.SQUARE, 9f, 5, null, Color.rgb(233, 236, 239))));
    }

    private void sortTop10DailyCases() {
        Collections.sort(topNations, (o1, o2) ->
                TrackerUtility.sort(Integer.parseInt(o2.getTodayCases()),
                        Integer.parseInt(o1.getTodayCases())));
    }

    private List<BarEntry> setHorizontalChartData() {
        List<BarEntry> values = new ArrayList<>();

        sortTop10DailyCases();

        for (int i = 0; i < topNations.size(); i++) {
            Nation nation = topNations.get(i);
            values.add(new BarEntry(i, new float[]{
                    TrackerUtility.convert(nation.getTodayCases()),
                    TrackerUtility.convert(nation.getTodayDeaths())}));
        }

        return values;
    }

    private List<String> setHorizontalChartLabel() {
        List<String> labels = new ArrayList<>();

        sortTop10DailyCases();

        for (Nation nation : topNations) {
            labels.add(nation.getCountry());
        }
        return labels;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: was called!");

        mRefreshLayout.setOnRefreshListener(() -> {

            mRawDataCountriesFromHerokuapp = DownloadedData.getInstance().getHerokuappCountriesData();
            mRawDataDateFromApify = DownloadedData.getInstance().getApifyData();

            if (!mRawDataCountriesFromHerokuapp.isEmpty() && mNationDataParser.getStatus() != NationDataParser.Status.RUNNING) {
                mNationDataParser.cancel(true);
                mNationDataParser = new NationDataParser(GlobalFragment.this);
                mNationDataParser.parse(NationDataParser.ParseData.COUNTRIES);
                mNationDataParser.execute(mRawDataCountriesFromHerokuapp);
            }

            if (!mRawDataDateFromApify.isEmpty() && mAPIFYDataParser.getStatus() != APIFYDataParser.Status.RUNNING) {
                mAPIFYDataParser.cancel(true);
                mAPIFYDataParser = new APIFYDataParser(GlobalFragment.this);
                mAPIFYDataParser.parse(APIFYDataParser.ParseData.DATE_ONLY);
                mAPIFYDataParser.execute(mRawDataDateFromApify);
            }

            mRefreshLayout.setRefreshing(false);
        });

        if (!isPausedToStopReDownload()) {
//            mDataParser = new DataParser(this);
//            mDataParser.execute(Addresses.Link.DATA_COUNTRIES_FROM_HEROKUAPP);

            if (!mRawDataCountriesFromHerokuapp.isEmpty()) {
                mNationDataParser.parse(NationDataParser.ParseData.COUNTRIES);
                mNationDataParser.execute(mRawDataCountriesFromHerokuapp);
            }

            if (!mRawDataDateFromApify.isEmpty()) {
                mAPIFYDataParser.parse(APIFYDataParser.ParseData.DATE_ONLY);
                mAPIFYDataParser.execute(mRawDataDateFromApify);
            }

//            mPHTrendDataParser = new PHTrendDataParser(this);
//            mPHTrendDataParser.setInterestData(PHTrendDataParser.InterestedData.DATE_ONLY);
//            mPHTrendDataParser.execute(Addresses.Link.DATA_PHILIPPINES_FROM_APIFY);
        }
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
        mChildMain.setVisibility(View.VISIBLE);
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
        cancelDownload();
        stopShimmer();
        pausedToStopReDownload();
    }

    private void cancelDownload() {
        if (mDataParser != null) mDataParser.cancel(true);
        if (mNationDataParser != null) mNationDataParser.cancel(true);
        if (mAPIFYDataParser != null) mAPIFYDataParser.cancel(true);
        if (mPHTrendDataParser != null) mPHTrendDataParser.cancel(true);
    }

    private void displayData() {
        Log.d(TAG, "displayData() preparing to display");
        if (mChildShimmer != null && mChildShimmer.getVisibility() == View.VISIBLE) {
            Log.d(TAG, "setting up data for display " + mChildShimmer.getVisibility());
            Log.d(TAG, "data is displayed!");
            stopShimmer();
            TrackerNumber.display(tvCases, disCases);
            TrackerNumber.display(tvDeaths, disDeaths);
            TrackerNumber.display(tvRecovered, disRecovered);
            TrackerNumber.display(tvActive, disActive);
            TrackerNumber.display(tvNewCases, disNewCases);
            TrackerNumber.display(tvNewDeaths, disNewDeaths);
            initPieChart();
            initHorizontalChart();
        }
    }

    //events
    @Override
    public void onClick(View v) {
        //onClick() - View All Countries
        mListener.onInflateCountriesFragment();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView() data retained!");
        mUnbinder.unbind();
        cancelDownload();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() data was completely erased!");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "onDetach() data is still retained! (may not if onDestroy() is called)");
        mListener = null;
    }
}
