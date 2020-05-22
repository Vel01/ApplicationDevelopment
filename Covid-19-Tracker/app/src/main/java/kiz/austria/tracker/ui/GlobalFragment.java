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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.muddzdev.styleabletoast.StyleableToast;

import java.util.ArrayList;
import java.util.Collections;
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
import kiz.austria.tracker.model.Nation;
import kiz.austria.tracker.util.TrackerHorizontalChart;
import kiz.austria.tracker.util.TrackerNumber;
import kiz.austria.tracker.util.TrackerPieChart;
import kiz.austria.tracker.util.TrackerUtility;

import static android.graphics.Color.rgb;

public class GlobalFragment extends BaseFragment implements
        View.OnClickListener, DataParser.OnDataAvailable, ConnectivityReceiver.ConnectivityReceiverListener, OnChartValueSelectedListener {

    private static final String TAG = "GlobalFragment";

    //widgets
    @BindView(R.id.chart_global_top_10)
    HorizontalBarChart horizontalChart;
    //ButterKnife
    private Unbinder mUnbinder;
    private List<Nation> topNations = new ArrayList<>();

    @Override
    public void onNetworkConnectionChanged(boolean isConnected) {
        Log.d(TAG, "onNetworkConnectionChanged() connected? " + isConnected);
        if (isConnected) {
            DataParser nationDataParser = new DataParser(this);
            nationDataParser.execute(Addresses.Link.DATA_COUNTRIES);
            return;
        }

        new StyleableToast.Builder(Objects.requireNonNull(getActivity())).iconStart(R.drawable.ic_signal_wifi_off)
                .text("No Internet Connection").textColor(getResources().getColor(R.color.md_white_1000))
                .backgroundColor(getResources().getColor(R.color.toast_connection_lost))
                .cornerRadius(10).length(Toast.LENGTH_LONG).show();
    }

    @Override
    public void onDataAvailable(List<Nation> nations, JSONRawData.DownloadStatus status) {
        if (status == JSONRawData.DownloadStatus.OK) {

            Collections.sort(nations, (o1, o2) ->
                    TrackerUtility.sort(Integer.parseInt(o1.getTodayCases()),
                            Integer.parseInt(o2.getTodayCases())));
            for (int i = 0; i < nations.size(); i++) {
                Nation nation = nations.get(i);
                if (nation.getCountry().toLowerCase().equals("world")) {
                    Log.d(TAG, "onDataAvailable() data received from itself: " + nation.toString());
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
            Log.d(TAG, "onDataAvailable: " + topNations.toString());
        }
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        BarEntry entry = (BarEntry) e;

        if (entry.getYVals() != null) {
            Log.i("VAL SELECTED", "Value: " + entry.getYVals()[h.getStackIndex()]);


            switch (h.getStackIndex()) {
                case 0:
                    new StyleableToast.Builder(Objects.requireNonNull(getActivity()))
                            .text(TrackerUtility.format(entry.getYVals()[h.getStackIndex()]))
                            .backgroundColor(getResources().getColor(R.color.red_two))
                            .textColor(getResources().getColor(R.color.red_one))
                            .iconStart(R.drawable.ic_confirmed_toast)
                            .cornerRadius(10).length(Toast.LENGTH_LONG).show();
                    break;
                case 1:
                    new StyleableToast.Builder(Objects.requireNonNull(getActivity()))
                            .text(TrackerUtility.format(entry.getYVals()[h.getStackIndex()])).textColor(getResources().getColor(R.color.grey_one))
                            .backgroundColor(getResources().getColor(R.color.grey_two))
                            .iconStart(R.drawable.ic_death_toast)
                            .cornerRadius(10).length(Toast.LENGTH_LONG).show();
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

    //layouts
    @BindView(R.id.layout_global_shimmer)
    ShimmerFrameLayout mShimmerFrameLayout;
    @BindView(R.id.include_layout_global_results)
    ConstraintLayout mChildMain;
    @BindView(R.id.child_layout_global_shimmer)
    View mChildShimmer;

    //vars
    private int disCases, disDeaths, disRecovered, disActive, disNewCases, disNewDeaths;

    @Override
    public void onNothingSelected() {

    }
    private Inflatable mListener;
    private boolean isPaused = false;

    @Override
    public void onAttach(@NonNull Context context) {
        Log.d(TAG, "onAttach: started");
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
        tvUpdate.setText(getCurrentDate());
        //go to countries widget
        btnViewAllCountries.setOnClickListener(this);
        return view;
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
        pieChart.initPieChart(tfRegular);
        pieChart.setLegend();
        pieChart.setLegend();
        pieChart.dataSet(colors);
        pieChart.dataSetAttributes(chart, 10f, Color.BLACK, tfLight);

    }

    private void initHorizontalChart() {

        TrackerHorizontalChart chart = new TrackerHorizontalChart(horizontalChart);
        horizontalChart.setOnChartValueSelectedListener(this);
        chart.setFonts(tfLight);
        chart.attributes();
        chart.setXAxis(setHorizontalChartLabel());
        chart.setAxisLeft();
        chart.setAxisRight();
        chart.setLegend();
        chart.setData(setHorizontalChartData(), "Top Daily Cases");

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
        if (!isPausedToStopReDownload()) {
            DataParser nationDataParser = new DataParser(this);
            nationDataParser.execute(Addresses.Link.DATA_COUNTRIES);
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
        stopShimmer();
        pausedToStopReDownload();
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
