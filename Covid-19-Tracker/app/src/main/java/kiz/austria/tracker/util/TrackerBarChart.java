package kiz.austria.tracker.util;

import android.graphics.Typeface;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;


public class TrackerBarChart {

    private BarChart chart;
    private BarDataSet set;
    private String[] xAxisLabels;

    private Typeface tfLight;

    public TrackerBarChart(BarChart chart, String[] xAxisLabels, Typeface tfLight) {
        this.chart = chart;
        this.xAxisLabels = xAxisLabels;
        this.tfLight = tfLight;
    }

    public void initChart() {
        chart.getDescription().setEnabled(false);
        chart.getAxisRight().setEnabled(true);
        chart.setPinchZoom(true);
        chart.setFitBars(true);
        leftAxisLabel();
        rightAxisLabel();
        xAxisLabel();
    }

    public void injectCustomMarkerView(CustomMarkerView mv) {
        mv.setChartView(chart); // For bounds control
        chart.setMarker(mv); // Set the marker to the chart
    }

    public void barDataSet(ArrayList<BarEntry> yValues, String label) {
        ArrayList<BarEntry> barEntries = new ArrayList<>(yValues);
        set = new BarDataSet(barEntries, label);
        set.setColors(ColorTemplate.JOYFUL_COLORS);
        set.setDrawValues(true);
    }

    public void initLegend(ArrayList<LegendEntry> entries) {
        Legend l = chart.getLegend();
        l.setCustom(entries);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(10f);
        l.setXEntrySpace(10f);
        initBarData();
    }

    private void leftAxisLabel() {
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTypeface(tfLight);
        leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setDrawGridLines(true);
        leftAxis.setSpaceTop(35f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
    }

    private void rightAxisLabel() {
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setTypeface(tfLight);
        rightAxis.setValueFormatter(new LargeValueFormatter());
        rightAxis.setDrawGridLines(true);
        rightAxis.setSpaceTop(35f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
    }

    private void xAxisLabel() {
        XAxis xAxis = chart.getXAxis();
        xAxis.setTypeface(tfLight);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(xAxisLabels));
        xAxis.setTextSize(8f);
    }

    private void initBarData() {
        BarData data = new BarData(set);
        data.setValueFormatter(new LargeValueFormatter());
        data.setValueTypeface(tfLight);
        chart.setData(data);
        chart.getBarData().setBarWidth(.6f);
        chart.invalidate();
        chart.animateY(500);
    }

}
