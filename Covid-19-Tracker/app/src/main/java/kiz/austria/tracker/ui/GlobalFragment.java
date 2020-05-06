package kiz.austria.tracker.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
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
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

import kiz.austria.tracker.R;
import kiz.austria.tracker.model.Nation;
import kiz.austria.tracker.util.TrackerCountAnimation;
import kiz.austria.tracker.util.TrackerPieChart;

public class GlobalFragment extends BaseFragment implements OnGlobalDownloadCompletedListener,
        OnChartValueSelectedListener, View.OnClickListener {

    private static final String TAG = "GlobalFragment";

    private boolean isDisplayed = false;

    @Override
    public void onDataAvailable(Nation nation) {
        if (nation != null) {
            Log.d(TAG, "onDataAvailable() data received from host: " + nation.toString());
            disCases = Integer.parseInt(nation.getConfirmed());
            disDeaths = Integer.parseInt(nation.getDeaths());
            disRecovered = Integer.parseInt(nation.getRecovered());
            displayData();
            isDisplayed = true;
        }
    }

    //events
    @Override
    public void onClick(View v) {
        //onClick() - View All Countries
        mListener.onInflateCountriesFragment();
    }

    @Override
    public void onValueSelected(Entry e, Highlight h) {
        if (e == null)
            return;
        Log.i("VAL SELECTED",
                "Value: " + e.getY() + ", index: " + h.getX()
                        + ", DataSet index: " + h.getDataSetIndex());
    }

    @Override
    public void onNothingSelected() {
        Log.i("PieChart", "nothing selected");
    }

    //widgets
    private PieChart chart;
    private TextView tvCases;
    private TextView tvDeaths;
    private TextView tvRecovered;
    private ShimmerFrameLayout mShimmerFrameLayout;
    private View mChildMain;
    private View mChildShimmer;

    //vars
    private int disCases, disDeaths, disRecovered;
    private OnInflateFragmentListener mListener;

    @Override
    public void onAttach(@NonNull Context context) {
        Log.d(TAG, "onAttach: started");
        super.onAttach(context);
        iniInterface();
    }

    private void iniInterface() {
        Activity activity = getActivity();
        if (!(activity instanceof OnInflateFragmentListener) && activity != null) {
            throw new ClassCastException(activity.getClass().getSimpleName()
                    + " must implement OnInflateFragmentListener interface");
        }
        mListener = (OnInflateFragmentListener) activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //retain fragment instance to retain its states and avoid re-downloading data.
        setRetainInstance(true);
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

        //design
        initDesign(view);

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
        setTextViewTypeface(tvCases, tfRoboto);
        setTextViewTypeface(tvDeaths, tfRoboto);
        setTextViewTypeface(tvRecovered, tfRoboto);

        chart = view.findViewById(R.id.chart_global_cases);
        return view;
    }

    private void initDesign(View view) {
        //design
        TextView globalTitle = view.findViewById(R.id.title_global);
        TextView globalConfirmed = view.findViewById(R.id.title_confirmed);
        TextView globalDeaths = view.findViewById(R.id.title_deaths);
        TextView globalRecovered = view.findViewById(R.id.title_recovered);
        TextView globalViewAll = view.findViewById(R.id.btn_view_all_countries_title);
        setTextViewTypeface(globalTitle, tfRoboto);
        setTextViewTypeface(globalConfirmed, tfRoboto);
        setTextViewTypeface(globalDeaths, tfRoboto);
        setTextViewTypeface(globalRecovered, tfRoboto);
        setTextViewTypeface(globalViewAll, tfRoboto);

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
        // add a selection listener
        chart.setOnChartValueSelectedListener(this);
        chart.animateY(4500, Easing.EaseInOutQuad);
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
        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        pieChart.dataSetColorTemplate(colors);
        pieChart.dataSetAttributes(chart, 10f, Color.BLACK, tfLight);

    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume: was called!");
        if (isDisplayed) {
            Log.d(TAG, "onResume() re-display");
            displayData();
        }
    }


    private void displayData() {

        Log.d(TAG, "displayData() preparing to display");
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "run() setting up data for display ");

                if (mChildShimmer != null && mChildShimmer.getVisibility() == View.VISIBLE
                        && disCases != 0
                        && disDeaths != 0
                        && disRecovered != 0) {
                    Log.d(TAG, "run() data is displayed!");
                    mShimmerFrameLayout.stopShimmer();
                    mShimmerFrameLayout.hideShimmer();
                    mChildShimmer.setVisibility(View.GONE);
                    mChildMain.setVisibility(View.VISIBLE);
                    TrackerCountAnimation.Display.countNumber(tvCases, disCases);
                    TrackerCountAnimation.Display.countNumber(tvDeaths, disDeaths);
                    TrackerCountAnimation.Display.countNumber(tvRecovered, disRecovered);
                    initPieChart();
                }
            }
        }, 700);
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
