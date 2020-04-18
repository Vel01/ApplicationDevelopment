package kiz.austria.tracker.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

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
import kiz.austria.tracker.data.DataParser;
import kiz.austria.tracker.data.PathContract;
import kiz.austria.tracker.data.RawData;
import kiz.austria.tracker.list.ListModel;
import kiz.austria.tracker.model.Countries;
import kiz.austria.tracker.util.AnimationContract;
import kiz.austria.tracker.util.BaseFragment;

public class GlobalFragment extends BaseFragment implements DataParser.OnDataAvailable, OnChartValueSelectedListener {

    private static final String TAG = "GlobalFragment";

    private TextView tvCases, tvDeaths, tvRecovered;
    private int disCases, disDeaths, disRecovered;
    private PieChart chart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_global, container, false);
        tvCases = view.findViewById(R.id.tv_cases);
        tvDeaths = view.findViewById(R.id.tv_deaths);
        tvRecovered = view.findViewById(R.id.tv_recovered);
        chart = view.findViewById(R.id.chart_global_cases);
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        DataParser dataParser = DataParser.getInstance(this);
        dataParser.execute(PathContract.Link.DATA_GLOBAL);
    }

    @Override
    public void onDataAvailable(ListModel<Countries> data, RawData.DownloadStatus status) {
        if (status == RawData.DownloadStatus.OK) {
            for (Countries countries : data.getCoverages()) {
                Log.d(TAG, "onDownloadComplete: data is " + countries.toString());
                disCases = Integer.parseInt(countries.getCases());
                AnimationContract.Display.countNumber(tvCases, disCases);
                disDeaths = Integer.parseInt(countries.getDeaths());
                AnimationContract.Display.countNumber(tvDeaths, disDeaths);
                disRecovered = Integer.parseInt(countries.getRecovered());
                AnimationContract.Display.countNumber(tvRecovered, disRecovered);
                setPieChart();
            }

        } else Log.d(TAG, "onDownloadComplete: status " + status);
    }

    private void setPieChart() {

        chart.setUsePercentValues(true);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(5, 5, 5, 5);
        chart.setDragDecelerationFrictionCoef(0.95f);
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
}
