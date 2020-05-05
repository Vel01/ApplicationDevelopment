package kiz.austria.tracker.util;

import android.graphics.Typeface;

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


    public void setLegend(Legend.LegendVerticalAlignment verticalAlignment,
                          Legend.LegendHorizontalAlignment horizontalAlignment,
                          Legend.LegendOrientation legendOrientation) {
        mLegend.setVerticalAlignment(verticalAlignment);
        mLegend.setHorizontalAlignment(horizontalAlignment);
        mLegend.setOrientation(legendOrientation);
    }

    public void setLegend(float xEntrySpace, float yOffset) {
        mLegend.setXEntrySpace(xEntrySpace);
        mLegend.setYOffset(yOffset);
    }

    public void dataSetValuePosition(PieDataSet.ValuePosition yValuePosition,
                                     PieDataSet.ValuePosition xValuePosition) {

        mDataSet.setYValuePosition(yValuePosition);
        mDataSet.setXValuePosition(xValuePosition);
    }

    public void dataSetLinePart1OffsetPercentage(float value) {
        mDataSet.setValueLinePart1OffsetPercentage(value);
    }

    public void dataSetValuePartLength(float part1, float part2) {
        mDataSet.setValueLinePart1Length(part1);
        mDataSet.setValueLinePart2Length(part2);
    }

    public void dataSetSliceSpace(float spaceDp) {
        mDataSet.setSliceSpace(spaceDp);
    }

    public void dataSetSelectionShift(float shift) {
        mDataSet.setSelectionShift(shift);
    }

    public void dataSetColorTemplate(ArrayList<Integer> colors) {
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


}
