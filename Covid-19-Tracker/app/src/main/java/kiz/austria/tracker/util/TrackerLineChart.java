package kiz.austria.tracker.util;

import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kiz.austria.tracker.model.PHTrend;

import static android.graphics.Color.rgb;

public class TrackerLineChart {
    private static final String TAG = "TrackerLineChart";
    private final LineChart chart;
    private final int[] colors = new int[]{
            rgb(255, 68, 51),
            rgb(151, 242, 149),
            rgb(117, 117, 117)
    };

    public TrackerLineChart(LineChart chart) {
        this.chart = chart;
    }

    public void setAttributes() {
        chart.setDrawGridBackground(false);
        chart.getDescription().setEnabled(false);
        chart.setDrawBorders(false);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setPinchZoom(true);
        chart.setExtraOffsets(10, 10, 10, 10);
        chart.resetTracking();
    }

    public void setLeft() {
        chart.getAxisLeft().setEnabled(true);
        chart.getAxisLeft().setDrawGridLines(true);
    }

    public void setRight() {
        chart.getAxisRight().setEnabled(false);
        chart.getAxisRight().setDrawAxisLine(false);
    }

    public void setXAxis(List<String> labelValues) {
        chart.getXAxis().setEnabled(true);
        chart.getXAxis().setDrawAxisLine(true);
        chart.getXAxis().setDrawGridLines(true);
        chart.getXAxis().setGranularity(1);
        chart.getXAxis().setLabelRotationAngle(-50);
        chart.getXAxis().setAxisMinimum(0);
        chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(
                labelValues));
    }

    public void setLegend() {
        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setYEntrySpace(10);
        l.setDrawInside(false);
    }

    private void setData(List<Entry> values, int current, float value) {
        Entry entry = new Entry(current, value);
        Log.d(TAG, "setData() entry = " + entry);
        ;
        values.add(entry);
    }

    public void setLineChartData(List<PHTrend> trends) throws ParseException {


        List<ILineDataSet> dataSets = new ArrayList<>();

        for (int i = 0; i < 3; i++) {

            List<Entry> values = new ArrayList<>();

            if (i == 0) {
                Collections.sort(trends, (o1, o2) ->
                        TrackerUtility.sort(Integer.parseInt(o2.getInfected()),
                                Integer.parseInt(o1.getInfected())));
            } else if (i == 1) {
                Collections.sort(trends, (o1, o2) ->
                        TrackerUtility.sort(Integer.parseInt(o2.getRecovered()),
                                Integer.parseInt(o1.getRecovered())));
            } else {
                Collections.sort(trends, (o1, o2) ->
                        TrackerUtility.sort(Integer.parseInt(o2.getDeceased()),
                                Integer.parseInt(o1.getDeceased())));
            }

            float currentValue = -1;
            String currentDate = "";
            int count = 1;
            for (int j = 0; j < trends.size(); j++) {
                PHTrend trend = trends.get(j);

                if (i == 0) {
                    float value = Float.parseFloat(trend.getInfected());
                    String date = TrackerUtility.formatSimpleDate(trend.getLatestUpdate());
                    if (currentValue != value && !currentDate.equals(date)) {
                        setData(values, j, value);
                        currentValue = value;
                        currentDate = date;
                    }
                }
                if (i == 1) {
                    float value = Float.parseFloat(trend.getRecovered());
                    String date = TrackerUtility.formatSimpleDate(trend.getLatestUpdate());
                    if (currentValue != value && !currentDate.equals(date)) {
                        setData(values, j, value);
                        currentValue = value;
                        currentDate = date;
                    }
                }
                if (i == 2) {
                    float value = Float.parseFloat(trend.getDeceased());
                    String date = TrackerUtility.formatSimpleDate(trend.getLatestUpdate());
                    if (currentValue != value && !currentDate.equals(date)) {
                        setData(values, j, value);
                        currentValue = value;
                        currentDate = date;
                    }
                }
            }


            String label;
            if (i == 0) label = "Confirmed";
            else if (i == 1) label = "Recovered";
            else label = "Deceased";

            LineDataSet data = new LineDataSet(values, label);
            data.setLineWidth(1f);
            data.setCircleRadius(2f);
            data.setDrawCircleHole(false);

            int color = colors[i % colors.length];
            data.setColor(color);
            data.setCircleColor(color);
            dataSets.add(data);

        }

        LineData data = new LineData(dataSets);
        chart.setData(data);
        chart.invalidate();
    }

    public LineChart getChart() {
        return chart;
    }
}
