package kiz.austria.tracker.util;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;
import java.util.List;

import kiz.austria.tracker.model.PHListUpdatesCases;

import static android.graphics.Color.rgb;

public class TrackerLineChart {
    private static final String TAG = "TrackerLineChart";
    private final int[] colors = new int[]{
            rgb(255, 68, 51),
            rgb(151, 242, 149),
            rgb(117, 117, 117)
    };
    private final LineChart chart;

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

    private LineData data = new LineData();

    public void setLineChartData(List<PHListUpdatesCases> trends) {
        data.addDataSet(dataForInfected(trends));
        data.addDataSet(dataForRecovered(trends));
        data.addDataSet(dataForDeceased(trends));
        chart.setData(data);
        chart.animateX(2500);
        chart.invalidate();
    }

    private LineDataSet lineDataConfig(List<Entry> values, String label, int color) {
        LineDataSet lineData = new LineDataSet(values, label);
        lineData.setLineWidth(1f);
        lineData.setCircleRadius(2f);
        lineData.setDrawCircleHole(false);
        lineData.setColor(color);
        lineData.setCircleColor(color);
        return lineData;
    }

    private float add(List<Entry> values, float currentValue, String data, int j) {
        float value = Float.parseFloat(data);
        if (currentValue != value) {
            values.add(new Entry(j, value));
            return value;
        }
        return value;
    }


    private ILineDataSet dataForInfected(List<PHListUpdatesCases> trends) {
        TrackerSort.quickSort(trends, "INFECTED", 0, trends.size());
        List<Entry> values = new ArrayList<>();
        float currentValue = -1;
        for (int j = 0; j < trends.size(); j++) {
            currentValue = add(values, currentValue,
                    trends.get(j).getInfected(), j);
        }

        return lineDataConfig(values, "Confirmed", colors[0]);
    }

    private ILineDataSet dataForRecovered(List<PHListUpdatesCases> trends) {
        TrackerSort.quickSort(trends, "RECOVERED", 0, trends.size());

        List<Entry> values = new ArrayList<>();

        float currentValue = -1;
        for (int j = 0; j < trends.size(); j++) {
            currentValue = add(values, currentValue,
                    trends.get(j).getRecovered(), j);
        }

        return lineDataConfig(values, "Recovered", colors[1]);
    }

    private LineDataSet dataForDeceased(List<PHListUpdatesCases> trends) {
        TrackerSort.quickSort(trends, "DECEASED", 0, trends.size());

        List<Entry> values = new ArrayList<>();

        float currentValue = -1;
        for (int j = 0; j < trends.size(); j++) {
            currentValue = add(values, currentValue,
                    trends.get(j).getDeceased(), j);
        }

        return lineDataConfig(values, "Deceased", colors[2]);
    }
}
