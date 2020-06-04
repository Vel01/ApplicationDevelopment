package kiz.austria.tracker.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CandleEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.LargeValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.github.mikephil.charting.utils.Utils;

import java.util.ArrayList;

import kiz.austria.tracker.R;


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
        chart.setExtraOffsets(10, 10, 10, 10);
        chart.getDescription().setEnabled(false);
        chart.getAxisRight().setEnabled(true);
        chart.setPinchZoom(true);
        chart.setFitBars(true);
        leftAxisLabel();
        rightAxisLabel();
        xAxisLabel();
    }

    public void injectCustomMarkerView(TrackerMarkerView mv) {
        mv.setChartView(chart); // For bounds control
        chart.setMarker(mv); // Set the marker to the chart
    }

    private static final int[] JOYFUL_COLORS = {
            Color.rgb(255, 68, 51), Color.rgb(233, 236, 239), Color.rgb(151, 242, 149)
    };

    public void barDataSet(ArrayList<BarEntry> yValues, String label) {
        ArrayList<BarEntry> barEntries = new ArrayList<>(yValues);
        set = new BarDataSet(barEntries, label);
        set.setColors(JOYFUL_COLORS);
        set.setDrawValues(true);
    }

    private void leftAxisLabel() {
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTypeface(tfLight);
        leftAxis.setValueFormatter(new LargeValueFormatter());
        leftAxis.setDrawGridLines(false);
        leftAxis.setSpaceTop(25f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
    }

    private void rightAxisLabel() {
        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setTypeface(tfLight);
        rightAxis.setValueFormatter(new LargeValueFormatter());
        rightAxis.setDrawGridLines(false);
        rightAxis.setSpaceTop(35f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
    }

    private void xAxisLabel() {
        XAxis xAxis = chart.getXAxis();
        xAxis.setTypeface(tfLight);
        xAxis.setDrawGridLines(false);
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

    public void initLegend(ArrayList<LegendEntry> entries) {
        Legend l = chart.getLegend();
        l.setCustom(entries);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setForm(Legend.LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(10f);
        l.setXEntrySpace(20f);
//        l.setDrawInside(false);
        initBarData();
    }

    @SuppressLint("ViewConstructor")
    public static class TrackerMarkerView extends MarkerView {

        private final TextView tvContent;

        public TrackerMarkerView(Context context, int layoutResource) {
            super(context, layoutResource);
            tvContent = findViewById(R.id.tv_content);
        }

        // runs every time the MarkerView is redrawn, can be used to update the
        // content (user-interface)
        @Override
        public void refreshContent(Entry e, Highlight highlight) {

            if (e instanceof CandleEntry) {

                CandleEntry ce = (CandleEntry) e;

                tvContent.setText(Utils.formatNumber(ce.getHigh(), 0, true));
            } else {

                tvContent.setText(Utils.formatNumber(e.getY(), 0, true));
            }
            super.refreshContent(e, highlight);
        }

        @Override
        public MPPointF getOffset() {
            return new MPPointF(-(getWidth() / 2), -getHeight());
        }
    }
}
