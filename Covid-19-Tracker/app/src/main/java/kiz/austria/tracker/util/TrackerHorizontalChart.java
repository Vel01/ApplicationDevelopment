package kiz.austria.tracker.util;

import android.graphics.Color;
import android.graphics.Typeface;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TrackerHorizontalChart {

    private static final int[] JOYFUL_COLORS = {
            Color.rgb(255, 68, 51), Color.rgb(233, 236, 239)
    };
    private final HorizontalBarChart chart;
    private Typeface tfLight;

    public TrackerHorizontalChart(HorizontalBarChart chart) {
        this.chart = chart;
    }

    public void setFonts(Typeface tfLight) {
        this.tfLight = tfLight;
    }

    public void attributes() {
        chart.setFitBars(true);
        chart.setPinchZoom(false);
        chart.animateY(2500);
        chart.setMaxVisibleValueCount(10);
        chart.getDescription().setEnabled(false);
        chart.setExtraOffsets(10, 10, 10, 10);
    }

    public void setXAxis(List<String> labels) {
        XAxis xl = chart.getXAxis();
        xl.setTextSize(10f);
        xl.setLabelCount(10);
        xl.setGranularity(1f);
        xl.setTypeface(tfLight);
        xl.setPosition(XAxis.XAxisPosition.TOP);
        xl.setDrawAxisLine(false);
        xl.setDrawGridLines(false);
        xl.setValueFormatter(new IndexAxisValueFormatter(labels));
    }

    public void setAxisLeft() {
        YAxis yl = chart.getAxisLeft();
        yl.setSpaceTop(10f);
        yl.setAxisMinimum(0f);
        yl.setTypeface(tfLight);
        yl.setDrawLabels(false);
        yl.setDrawAxisLine(false);
        yl.setDrawGridLines(false);
    }

    public void setAxisRight() {
        YAxis yr = chart.getAxisRight();
        yr.setAxisMinimum(0f);
        yr.setTypeface(tfLight);
        yr.setDrawLabels(false);
        yr.setDrawAxisLine(false);
        yr.setDrawGridLines(false);
        yr.setValueFormatter(new LargeValueFormatter());
    }

    public void setLegend() {
        Legend l = chart.getLegend();
        l.setCustom(setEntries());
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setTextSize(10f);
        l.setXEntrySpace(20f);
        l.setDrawInside(false);
    }

    public void setData(List<BarEntry> values, String label) {
        BarDataSet set1 = new BarDataSet(values, label);
        set1.setColors(getColors());
        BarData data = new BarData(set1);
        data.setValueTextSize(10f);
        data.setValueTypeface(tfLight);
        chart.setData(data);
        chart.invalidate();
    }

    private List<LegendEntry> setEntries() {
        return new ArrayList<>(Arrays.asList(
                new LegendEntry("Daily New Cases", Legend.LegendForm.SQUARE, 9f, 5, null, Color.rgb(255, 68, 51)),
                new LegendEntry("Daily New Deaths", Legend.LegendForm.SQUARE, 9f, 5, null, Color.rgb(233, 236, 239))));

    }

    private int[] getColors() {

        // have as many colors as stack-values per entry
        int[] colors = new int[2];


        System.arraycopy(JOYFUL_COLORS, 0, colors, 0, 2);

        return colors;
    }


}
