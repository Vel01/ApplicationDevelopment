package kiz.austria.tracker.util;

import android.graphics.Typeface;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;

import java.util.ArrayList;
import java.util.List;

public class TrackerPieChart {

    private PieChart mPieChart;
    private Legend mLegend;
    private PieDataSet mDataSet;


    public TrackerPieChart(PieChart pieChart, List<PieEntry> entries, String label) {
        mPieChart = pieChart;
        mLegend = mPieChart.getLegend();
        mDataSet = new PieDataSet(entries, label);
    }

    public void initPieChart(Typeface tfRegular, float textSize, int color) {

        mPieChart.setUsePercentValues(true);
        mPieChart.getDescription().setEnabled(false);
        mPieChart.setExtraOffsets(5, 10, 5, 5);
        mPieChart.setDragDecelerationFrictionCoef(0.90f);
        mPieChart.setDrawHoleEnabled(true);
        mPieChart.setRotationEnabled(true);
        mPieChart.setRotationAngle(-5);
        mPieChart.setHighlightPerTapEnabled(true);
        mPieChart.animateY(4000, Easing.EaseInOutQuad);
        // entry label styling
        mPieChart.setEntryLabelColor(color);
        mPieChart.setEntryLabelTypeface(tfRegular);
        mPieChart.setEntryLabelTextSize(textSize);
    }


    public void setLegend() {
        mLegend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        mLegend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        mLegend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        mLegend.setXEntrySpace(7f);
        mLegend.setYOffset(10f);
    }

    public void dataSet(ArrayList<Integer> colors) {
        mDataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        mDataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        mDataSet.setValueLinePart1OffsetPercentage(90.0f);
        mDataSet.setValueLinePart1Length(.5f);
        mDataSet.setValueLinePart2Length(.4f);
        mDataSet.setSliceSpace(2f);
        mDataSet.setSelectionShift(4f);
        mDataSet.setColors(colors);
    }

    public void dataSetAttributes(PieChart toFormat,
                                  float textSize, int textColor,
                                  Typeface typeface) {

        PieData data = new PieData(mDataSet);
        data.setValueFormatter(new PercentFormatter(toFormat));
        data.setValueTextSize(textSize);
        data.setValueTextColor(textColor);
        data.setValueTypeface(typeface);
        mPieChart.setData(data);
        mPieChart.highlightValue(null);
        mPieChart.invalidate();
    }

    public PieDataSet getDataSet() {
        return mDataSet;
    }
}
