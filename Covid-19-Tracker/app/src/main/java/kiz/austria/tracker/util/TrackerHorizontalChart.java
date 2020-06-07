package kiz.austria.tracker.util;

import android.graphics.Typeface;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.LargeValueFormatter;

import java.util.List;

public class TrackerHorizontalChart {

    private final HorizontalBarChart chart;
    private Typeface tfLight;

    public TrackerHorizontalChart(HorizontalBarChart chart) {
        this.chart = chart;
    }

    public void setFonts(Typeface tfLight) {
        this.tfLight = tfLight;
    }

    public void attributes() {
        chart.setFitBars(false);
        chart.setScaleEnabled(false);
        chart.animateY(2500);
        chart.setMaxVisibleValueCount(10);
        chart.getDescription().setEnabled(false);
    }

    public XAxis setXAxis(XAxis.XAxisPosition position) {
        XAxis xl = chart.getXAxis();
        xl.setTextSize(10f);
        xl.setLabelCount(10);
        xl.setGranularity(1f);
        xl.setTypeface(tfLight);
        xl.setPosition(position);
        xl.setDrawAxisLine(false);
        xl.setDrawGridLines(false);

        return xl;
    }

    public void setAxisLeft() {
        YAxis yl = chart.getAxisLeft();
        yl.setSpaceTop(10f);
        yl.setAxisMinimum(0f);
        yl.setSpaceTop(35f);
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

    public void setLegend(List<LegendEntry> legend) {
        Legend l = chart.getLegend();
        l.setCustom(legend);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setTextSize(10f);
        l.setXEntrySpace(20f);
        l.setDrawInside(false);
    }

    public BarDataSet setData(List<BarEntry> values, String label, int[] colors) {
        BarDataSet set1 = new BarDataSet(values, label);
        set1.setColors(colors);
        BarData data = new BarData(set1);
        data.setValueTextSize(10f);
        data.setValueTypeface(tfLight);
        chart.setData(data);
        chart.invalidate();
        return set1;
    }


}
