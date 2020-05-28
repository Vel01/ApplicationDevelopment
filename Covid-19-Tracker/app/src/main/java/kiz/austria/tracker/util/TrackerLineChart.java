package kiz.austria.tracker.util;

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

    //    private List<ILineDataSet> dataSets = new ArrayList<>();
    private LineData data = new LineData();

    private ILineDataSet dataForInfected(List<PHTrend> trends) throws ParseException {
        Collections.sort(trends, (o1, o2) ->
                TrackerUtility.sort(Integer.parseInt(o2.getInfected()),
                        Integer.parseInt(o1.getInfected())));
        List<Entry> values = new ArrayList<>();

        float currentValue = -1;
        String currentDate = "";
        for (int j = 0; j < trends.size(); j++) {
            PHTrend trend = trends.get(j);
            float value = Float.parseFloat(trend.getInfected());
            String date = TrackerUtility.formatSimpleDate(trend.getLatestUpdate());
            if (currentValue != value && !currentDate.equals(date)) {
                values.add(new Entry(j, value));
                currentValue = value;
                currentDate = date;
            }
        }

        LineDataSet lineData = new LineDataSet(values, "Confirmed");
        lineData.setLineWidth(1f);
        lineData.setCircleRadius(2f);
        lineData.setDrawCircleHole(false);

        int color = colors[0];
        lineData.setColor(color);
        lineData.setCircleColor(color);
        return lineData;
    }

    private ILineDataSet dataForRecovered(List<PHTrend> trends) throws ParseException {
        Collections.sort(trends, (o1, o2) ->
                TrackerUtility.sort(Integer.parseInt(o2.getRecovered()),
                        Integer.parseInt(o1.getRecovered())));
        List<Entry> values = new ArrayList<>();

        float currentValue = -1;
        String currentDate = "";
        for (int j = 0; j < trends.size(); j++) {
            PHTrend trend = trends.get(j);
            float value = Float.parseFloat(trend.getRecovered());
            String date = TrackerUtility.formatSimpleDate(trend.getLatestUpdate());
            if (currentValue != value && !currentDate.equals(date)) {
                values.add(new Entry(j, value));
                currentValue = value;
                currentDate = date;
            }
        }

        LineDataSet lineData = new LineDataSet(values, "Recovered");
        lineData.setLineWidth(1f);
        lineData.setCircleRadius(2f);
        lineData.setDrawCircleHole(false);

        int color = colors[1];
        lineData.setColor(color);
        lineData.setCircleColor(color);
        return lineData;
    }

    private LineDataSet dataForDeceased(List<PHTrend> trends) throws ParseException {
        Collections.sort(trends, (o1, o2) ->
                TrackerUtility.sort(Integer.parseInt(o2.getDeceased()),
                        Integer.parseInt(o1.getDeceased())));
        List<Entry> values = new ArrayList<>();

        float currentValue = -1;
        String currentDate = "";
        for (int j = 0; j < trends.size(); j++) {
            PHTrend trend = trends.get(j);
            float value = Float.parseFloat(trend.getDeceased());
            String date = TrackerUtility.formatSimpleDate(trend.getLatestUpdate());
            if (currentValue != value && !currentDate.equals(date)) {
                values.add(new Entry(j, value));
                currentValue = value;
                currentDate = date;
            }
        }

        LineDataSet lineData = new LineDataSet(values, "Deceased");
        lineData.setLineWidth(1f);
        lineData.setCircleRadius(2f);
        lineData.setDrawCircleHole(false);

        int color = colors[2];
        lineData.setColor(color);
        lineData.setCircleColor(color);
        return lineData;
    }

    public void setLineChartData(List<PHTrend> trends) throws ParseException {

        TrackerSort.quickSort(trends, "INFECTED", 0, trends.size());
        data.addDataSet(dataForInfected(trends));

        TrackerSort.quickSort(trends, "RECOVERED", 0, trends.size());
        data.addDataSet(dataForRecovered(trends));

        TrackerSort.quickSort(trends, "DECEASED", 0, trends.size());
        data.addDataSet(dataForDeceased(trends));
        chart.setData(data);
        chart.invalidate();
    }

    public LineChart getChart() {
        return chart;
    }
}
