package kiz.austria.tracker.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
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
import kiz.austria.tracker.data.DownloadedData;
import kiz.austria.tracker.data.RawDataDownloader;
import kiz.austria.tracker.data.RawDataDownloader.DownloadStatus;
import kiz.austria.tracker.data.parser.APIFYDataParser;
import kiz.austria.tracker.data.parser.NationDataParser;
import kiz.austria.tracker.model.Nation;
import kiz.austria.tracker.model.PHCases;
import kiz.austria.tracker.model.Philippines;
import kiz.austria.tracker.util.TrackerHorizontalChart;
import kiz.austria.tracker.util.TrackerNumber;
import kiz.austria.tracker.util.TrackerPieChart;
import kiz.austria.tracker.util.TrackerUtility;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.rgb;

public class GlobalFragment extends BaseFragment implements
        View.OnClickListener, OnChartValueSelectedListener, NationDataParser.OnDataAvailable, APIFYDataParser.OnDataAvailable {

    private static final String TAG = "GlobalFragment";

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

    @Override
    public void onNothingSelected() {
        //this method is not supported
    }

    @Override
    public void onCountriesDataAvailable(ArrayList<Nation> nations, RawDataDownloader.DownloadStatus status) {
        if (status == DownloadStatus.OK && !mNationDataParser.isCancelled()) {

            Collections.sort(nations, (o1, o2) ->
                    TrackerUtility.sort(Integer.parseInt(o1.getTodayCases()),
                            Integer.parseInt(o2.getTodayCases())));
            for (int i = 0; i < nations.size(); i++) {
                Nation nation = nations.get(i);
                if (nation.getCountry().toLowerCase().equals("world")) {
                    Log.d(TAG, "onCountriesDataAvailable() data received from itself.");
                    mCases = Integer.parseInt(nation.getConfirmed());
                    mDeaths = Integer.parseInt(nation.getDeaths());
                    mRecovered = Integer.parseInt(nation.getRecovered());
                    mNewCases = Integer.parseInt(nation.getTodayCases());
                    mNewDeaths = Integer.parseInt(nation.getTodayDeaths());
                    mActive = Integer.parseInt(nation.getActive());
                    continue;
                }
                if (topNations.size() != 10) {
                    topNations.add(nation);
                    continue;
                }
                break;
            }
            displayData();
            initPieChart();
            initHorizontalChart();
        }
    }

    @Override
    public void onPhilippinesDataAvailable(Philippines philippines, DownloadStatus status) {

    }

    @Override
    public void onFullDataAvailable(ArrayList<PHCases> dataList, DownloadStatus status) {

    }

    @Override
    public void onDateAvailable(PHCases data, DownloadStatus status) {
        if (status == DownloadStatus.OK && !mAPIFYDataParser.isCancelled()) {
            setLatestUpdate(data);
        }
    }

    @Override
    public void onEssentialDataAvailable(List<PHCases> dataList, DownloadStatus status) {

    }

    @Override
    public void onBasicDataAvailable(List<PHCases> dataList, DownloadStatus status) {

    }

    @Override
    public void onAttach(@NonNull Context context) {
        Log.d(TAG, "onAttach: started");
        super.onAttach(context);
        initRawDataForParsing();
        initInflatable();
    }

    private void initInflatable() {
        Activity activity = getActivity();
        if (!(activity instanceof Inflatable) && activity != null) {
            throw new ClassCastException(activity.getClass().getSimpleName()
                    + " must implement Inflatable interface");
        }
        mListener = (Inflatable) activity;
    }


    private static final int[] JOYFUL_COLORS = {
            Color.rgb(255, 68, 51), Color.rgb(233, 236, 239)
    };

    //ButterKnife
    private Unbinder mUnbinder;
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

    @BindView(R.id.layout_scroll)
    ScrollView mScrollView;
    @BindView(R.id.layout_shimmer)
    ShimmerFrameLayout mShimmerFrameLayout;
    @BindView(R.id.chart_global_top_10)
    HorizontalBarChart horizontalChart;
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
    private int mCases, mDeaths, mRecovered, mActive, mNewCases, mNewDeaths;
    private static String mRawDataCountriesFromHerokuapp;
    private static String mRawDataDateFromApify;

    //references
    private List<Nation> topNations = new ArrayList<>();
    private Inflatable mListener;
    private NationDataParser mNationDataParser = null;
    private APIFYDataParser mAPIFYDataParser = null;

    private void resetStats() {
        mCases = 0;
        mDeaths = 0;
        mRecovered = 0;
        mActive = 0;
        mNewCases = 0;
        mNewDeaths = 0;
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

    private void setLatestUpdate(PHCases trend) {
        Log.d(TAG, "onDataTrendAvailable() " + trend.getLatestUpdate());
        try {
            tvUpdate.setText(TrackerUtility.formatDate(trend.getLatestUpdate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private void initPieChart() {

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(mCases, "Confirmed"));
        entries.add(new PieEntry(mDeaths, "Deaths"));
        entries.add(new PieEntry(mRecovered, "Recovered"));

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

        horizontalChart.setExtraOffsets(10, 10, 10, 10);
        horizontalChart.setOnChartValueSelectedListener(this);

        TrackerHorizontalChart chart = new TrackerHorizontalChart(horizontalChart);
        chart.setFonts(tfLight);
        chart.attributes();
        chart.setXAxis(XAxis.XAxisPosition.TOP).
                setValueFormatter(new IndexAxisValueFormatter(setHorizontalChartLabel()));
        chart.setAxisLeft();
        chart.setAxisRight();
        chart.setLegend(setEntries());
        chart.setData(setHorizontalChartData(), "Top Daily PHCases", JOYFUL_COLORS);

    }

    private List<LegendEntry> setEntries() {
        return new ArrayList<>(Arrays.asList(
                new LegendEntry("Daily New PHCases", Legend.LegendForm.SQUARE, 9f, 5, null, Color.rgb(255, 68, 51)),
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
        mRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mRefreshLayout.setOnRefreshListener(() -> {
            reParseRawData();
            mRefreshLayout.setRefreshing(false);
        });

        if (!isPausedToStopReDownload()) {

            if (!mRawDataCountriesFromHerokuapp.isEmpty()) {
                mNationDataParser.parse(NationDataParser.ParseData.COUNTRIES);
                mNationDataParser.execute(mRawDataCountriesFromHerokuapp);
            }

            if (!mRawDataDateFromApify.isEmpty()) {
                mAPIFYDataParser.parse(APIFYDataParser.ParseData.DATE_ONLY);
                mAPIFYDataParser.execute(mRawDataDateFromApify);
            }

        }
    }

    private void initRawDataForParsing() {
        mRawDataCountriesFromHerokuapp = DownloadedData.getInstance().getHerokuappCountriesData();
        mRawDataDateFromApify = DownloadedData.getInstance().getApifyData();
    }

    private void reParseRawData() {
        resetStats();
        initRawDataForParsing();
        restartShimmer();
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
    }

    private void pausedToStopReDownload() {
        isPaused = true;
    }

    private boolean isPausedToStopReDownload() {
        return isPaused;
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
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause()");
        stopShimmer();
        pausedToStopReDownload();
    }

    private void cancelDownload() {
        if (mNationDataParser != null) mNationDataParser.cancel(true);
        if (mAPIFYDataParser != null) mAPIFYDataParser.cancel(true);
    }

    private void displayData() {
        Log.d(TAG, "displayData() preparing to display");
        if (mChildShimmer != null && mChildShimmer.getVisibility() == View.VISIBLE) {
            Log.d(TAG, "data is displayed!");
            stopShimmer();
            TrackerNumber.display(tvCases, mCases);
            TrackerNumber.display(tvDeaths, mDeaths);
            TrackerNumber.display(tvRecovered, mRecovered);
            TrackerNumber.display(tvActive, mActive);
            TrackerNumber.display(tvNewCases, mNewCases);
            TrackerNumber.display(tvNewDeaths, mNewDeaths);

        }
    }

    //events
    @Override
    public void onClick(View v) {
        //onClick() - View All Countries
        mListener.onInflateCountriesFragment();
    }

    public int getScrollPosition() {
        return mScrollView.getScrollY();
    }

    public void resetScrollPosition() {
        mScrollView.scrollTo(0, 0);
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
