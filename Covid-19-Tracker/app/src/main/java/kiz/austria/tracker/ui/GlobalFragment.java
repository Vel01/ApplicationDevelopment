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

import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import java.util.ArrayList;

import kiz.austria.tracker.R;
import kiz.austria.tracker.data.Addresses;
import kiz.austria.tracker.data.JSONRawData;
import kiz.austria.tracker.data.NationDataParser;
import kiz.austria.tracker.model.Nation;
import kiz.austria.tracker.util.TrackerNumber;
import kiz.austria.tracker.util.TrackerPieChart;

import static android.graphics.Color.rgb;

public class GlobalFragment extends BaseFragment implements
        View.OnClickListener, NationDataParser.OnDataAvailable {

    private static final String TAG = "GlobalFragment";

    @Override
    public void onDataAvailable(Nation nation, JSONRawData.DownloadStatus status) {
        Log.d(TAG, "onDataAvailable() data received from itself: " + nation.toString());
        disCases = Integer.parseInt(nation.getConfirmed());
        disDeaths = Integer.parseInt(nation.getDeaths());
        disRecovered = Integer.parseInt(nation.getRecovered());
        disNewCases = Integer.parseInt(nation.getTodayCases());
        disNewDeaths = Integer.parseInt(nation.getTodayDeaths());
        disActive = Integer.parseInt(nation.getActive());
        displayData();
    }

    //events
    @Override
    public void onClick(View v) {
        //onClick() - View All Countries
        mListener.onInflateCountriesFragment();
    }

    //widgets
    private PieChart chart;
    private TextView tvCases;
    private TextView tvDeaths;
    private TextView tvRecovered;
    private TextView tvNewCases;
    private TextView tvNewDeaths;
    private TextView tvActive;

    private ShimmerFrameLayout mShimmerFrameLayout;
    private View mChildMain;
    private View mChildShimmer;

    //vars
    private int disCases, disDeaths, disRecovered, disActive, disNewCases, disNewDeaths;
    private Inflatable mListener;
    private boolean isPaused = false;

    @Override
    public void onAttach(@NonNull Context context) {
        Log.d(TAG, "onAttach: started");
        super.onAttach(context);
        iniInterface();
    }

    private void iniInterface() {
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

        //shimmer widgets
        mShimmerFrameLayout = view.findViewById(R.id.layout_global_shimmer);
        mChildShimmer = view.findViewById(R.id.child_layout_global_shimmer);
        mChildMain = view.findViewById(R.id.child_layout_global_main);

        //update widget
        TextView tvUpdate = view.findViewById(R.id.tv_update_date);
        tvUpdate.setText(getCurrentDate());

        //go to countries widget
        CardView btnViewAllCountries = view.findViewById(R.id.btn_view_all_countries);
        btnViewAllCountries.setOnClickListener(this);

        //global result widgets
        tvCases = view.findViewById(R.id.tv_cases);
        tvDeaths = view.findViewById(R.id.tv_deaths);
        tvRecovered = view.findViewById(R.id.tv_recovered);
        tvActive = view.findViewById(R.id.tv_active);
        tvNewCases = view.findViewById(R.id.tv_new_cases);
        tvNewDeaths = view.findViewById(R.id.tv_new_deaths);

        chart = view.findViewById(R.id.chart_global_cases);
        return view;
    }

    private void initPieChart() {

        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5, 5, 5, 5);
        chart.setDragDecelerationFrictionCoef(0.90f);
        chart.setDrawHoleEnabled(false);
        chart.setRotationEnabled(true);
        chart.setRotationAngle(50);
        chart.setHighlightPerTapEnabled(true);
        chart.animateY(4000, Easing.EaseInOutQuad);
        // entry label styling
        chart.setEntryLabelColor(Color.BLACK);
        chart.setEntryLabelTypeface(tfRegular);
        chart.setEntryLabelTextSize(10f);

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(disCases, "Confirmed"));
        entries.add(new PieEntry(disDeaths, "Deaths"));
        entries.add(new PieEntry(disRecovered, "Recovered"));
        TrackerPieChart pieChart = new TrackerPieChart(chart, entries, null);

        pieChart.setLegend(Legend.LegendVerticalAlignment.BOTTOM,
                Legend.LegendHorizontalAlignment.CENTER,
                Legend.LegendOrientation.HORIZONTAL);
        pieChart.setLegend(7f, 10f);
        pieChart.dataSetValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE,
                PieDataSet.ValuePosition.OUTSIDE_SLICE);
        pieChart.dataSetLinePart1OffsetPercentage(90.0f);
        pieChart.dataSetValuePartLength(.7f, .5f);
        pieChart.dataSetSliceSpace(2f);
        pieChart.dataSetSelectionShift(4f);
        // add a lot of colors
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(rgb(244, 67, 54));
        colors.add(rgb(211, 47, 47));
        colors.add(rgb(255, 235, 59));
        pieChart.dataSetColorTemplate(colors);
        pieChart.dataSetAttributes(chart, 10f, Color.BLACK, tfLight);

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: was called!");
        if (!isPausedToStopReDownload()) {
            NationDataParser<Nation> nationNationDataParser = NationDataParser.getInstance(this);
            nationNationDataParser.execute(Addresses.Link.DATA_WORLD);
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
        Log.d(TAG, "setting up data for display ");
        if (mChildShimmer != null && mChildShimmer.getVisibility() == View.VISIBLE) {
            Log.d(TAG, "data is displayed!");
            stopShimmer();
            TrackerNumber.display(tvCases, disCases);
            TrackerNumber.display(tvDeaths, disDeaths);
            TrackerNumber.display(tvRecovered, disRecovered);
            TrackerNumber.display(tvActive, disActive);
            TrackerNumber.display(tvNewCases, disNewCases);
            TrackerNumber.display(tvNewDeaths, disNewDeaths);
            initPieChart();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "onDestroyView() data retained!");
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
