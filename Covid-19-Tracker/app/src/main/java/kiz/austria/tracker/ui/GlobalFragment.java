package kiz.austria.tracker.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;

import kiz.austria.tracker.R;
import kiz.austria.tracker.data.Addresses;
import kiz.austria.tracker.data.JSONRawData;
import kiz.austria.tracker.data.NationDataParser;
import kiz.austria.tracker.model.Nation;
import kiz.austria.tracker.util.TextCountAnimation;

public class GlobalFragment extends BaseFragment implements NationDataParser.OnDataAvailable,
        OnChartValueSelectedListener, View.OnClickListener {

    private static final String TAG = "GlobalFragment";

    @Override
    public void onDataAvailable(Nation global, JSONRawData.DownloadStatus status) {
        if (status == JSONRawData.DownloadStatus.OK) {
            Log.d(TAG, "onDataAvailable: started");


            Log.d(TAG, "onDownloadComplete: data is " + global.toString());
            disCases = Integer.parseInt(global.getCases());
            disDeaths = Integer.parseInt(global.getDeaths());
            disRecovered = Integer.parseInt(global.getRecovered());

            TextCountAnimation.Display.countNumber(tvCases, disCases);
            TextCountAnimation.Display.countNumber(tvDeaths, disDeaths);
            TextCountAnimation.Display.countNumber(tvRecovered, disRecovered);
            initPieChart();
        } else Log.d(TAG, "onDownloadComplete: status " + status);
    }


    //widgets
    private PieChart chart;
    private TextView tvCases;
    private TextView tvDeaths;
    private TextView tvRecovered;
    private ShimmerFrameLayout mShimmerFrameLayout;
    private LinearLayout mChildShimmer, mChildMain;

    //vars
    private int disCases, disDeaths, disRecovered;
    private InflateFragment mInflatingFragment;

    @Override
    public void onAttach(@NonNull Context context) {
        Log.d(TAG, "onAttach: started");
        super.onAttach(context);

        NationDataParser<Nation> nationNationDataParser = NationDataParser.getInstance(this);
        nationNationDataParser.execute(Addresses.Link.DATA_GLOBAL);

        mInflatingFragment = (InflateFragment) context;
        Log.d(TAG, "onAttach: ended");
    }


    @Override
    public void onResume() {
        super.onResume();
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
        LinearLayout btnViewAllCountries = view.findViewById(R.id.btn_view_all_countries);
        btnViewAllCountries.setOnClickListener(this);

        //global result widgets
        tvCases = view.findViewById(R.id.tv_cases);
        tvDeaths = view.findViewById(R.id.tv_deaths);
        tvRecovered = view.findViewById(R.id.tv_recovered);

        chart = view.findViewById(R.id.chart_global_cases);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (mChildShimmer.getVisibility() == View.VISIBLE) {
                    mShimmerFrameLayout.stopShimmer();
                    mShimmerFrameLayout.hideShimmer();
                    mChildShimmer.setVisibility(View.GONE);
                    mChildMain.setVisibility(View.VISIBLE);

                }
            }
        }, 700);

        Log.d(TAG, "onCreateView: ended");
        return view;
    }

    private void initPieChart() {

        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5, 5, 5, 5);
        chart.setDragDecelerationFrictionCoef(0.90f);
        chart.setDrawHoleEnabled(true);
        chart.setHoleColor(Color.WHITE);
        chart.setTransparentCircleColor(Color.WHITE);
        chart.setTransparentCircleAlpha(110);
        chart.setHoleRadius(30f);
        chart.setTransparentCircleRadius(35f);
        chart.setRotationAngle(50);
        chart.setRotationEnabled(true);
        chart.setHighlightPerTapEnabled(true);
        // add a selection listener
        chart.setOnChartValueSelectedListener(this);
        chart.animateY(1400, Easing.EaseInOutQuad);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setXEntrySpace(7f);
        l.setYEntrySpace(0f);
        l.setYOffset(10f);

        // entry label styling
        chart.setEntryLabelColor(Color.WHITE);
        chart.setEntryLabelTypeface(tfRegular);
        chart.setEntryLabelTextSize(12f);

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry(disCases, "Confirmed"));
        entries.add(new PieEntry(disDeaths, "Deaths"));
        entries.add(new PieEntry(disRecovered, "Recovered"));

        PieDataSet dataSet = new PieDataSet(entries, null);

        dataSet.setDrawIcons(false);
        dataSet.setSliceSpace(3f);
        dataSet.setIconsOffset(new MPPointF(0, 40));
        dataSet.setSelectionShift(10f);

        // add a lot of colors
        ArrayList<Integer> colors = new ArrayList<>();

        for (int c : ColorTemplate.JOYFUL_COLORS)
            colors.add(c);
        colors.add(ColorTemplate.getHoloBlue());
        dataSet.setColors(colors);

        PieData data = new PieData(dataSet);
        data.setValueFormatter(new PercentFormatter(chart));
        data.setValueTextSize(10f);
        data.setValueTextColor(Color.BLACK);
        data.setValueTypeface(tfLight);
        chart.setData(data);

        // undo all highlights
        chart.highlightValues(null);
        chart.invalidate();
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

    @Override
    public void onClick(View v) {
        mInflatingFragment.inflateCountriesFragment();
    }

}
