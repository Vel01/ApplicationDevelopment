package kiz.austria.tracker.ui;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;
import java.util.Arrays;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import kiz.austria.tracker.R;
import kiz.austria.tracker.model.Nation;
import kiz.austria.tracker.util.TrackerBarChart;
import kiz.austria.tracker.util.TrackerKeys;
import kiz.austria.tracker.util.TrackerNumber;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectedNationFragment extends BaseFragment {
    private static final String TAG = "SelectedNationFragment";
    @BindView(R.id.chart_selected_country)
    BarChart mChart;

    //references
    private Nation mNation;
    //widgets
    @BindView(R.id.tv_cases)
    TextView mConfirmed;
    @BindView(R.id.tv_deaths)
    TextView mDeaths;
    @BindView(R.id.tv_recovered)
    TextView mRecovered;
    //ButterKnife
    private Unbinder mUnbinder;

    //values
    private int mIntCases;
    private int mIntDeaths;
    private int mIntCritical;
    private int mIntActive;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Bundle args = getArguments();
        if (args != null) {

            mNation = args.getParcelable(TrackerKeys.KEY_SELECTED_COUNTRY);
            assert mNation != null;
            mIntCases = Integer.parseInt(mNation.getTodayCases());
            mIntDeaths = Integer.parseInt(mNation.getTodayDeaths());
            mIntCritical = Integer.parseInt(mNation.getCritical());
            mIntActive = Integer.parseInt(mNation.getActive());

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_selected_nation, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        TrackerNumber.display(mConfirmed, Integer.parseInt(mNation.getConfirmed()));
        TrackerNumber.display(mDeaths, Integer.parseInt(mNation.getDeaths()));
        TrackerNumber.display(mRecovered, Integer.parseInt(mNation.getRecovered()));

        initBarChart();
        return view;
    }

    private void initBarChart() {
        String[] values = new String[]{"Today's Cases", "Today's Deaths", "Critical", "Active"};
        ArrayList<BarEntry> yValues = new ArrayList<>(
                Arrays.asList(
                        new BarEntry(0, mIntCases),
                        new BarEntry(1, mIntDeaths),
                        new BarEntry(2, mIntCritical),
                        new BarEntry(3, mIntActive)
                ));

        ArrayList<LegendEntry> entries = new ArrayList<>(Arrays.asList(
                new LegendEntry(values[0], Legend.LegendForm.CIRCLE, 10, 5, null, Color.rgb(217, 80, 138)),
                new LegendEntry(values[1], Legend.LegendForm.CIRCLE, 10, 5, null, Color.rgb(254, 149, 7)),
                new LegendEntry(values[2], Legend.LegendForm.CIRCLE, 10, 5, null, Color.rgb(254, 247, 120)),
                new LegendEntry(values[3], Legend.LegendForm.CIRCLE, 10, 5, null, Color.rgb(106, 167, 134))));

        TrackerBarChart barChart = new TrackerBarChart(mChart, values, tfLight);
        barChart.initChart();
        barChart.injectCustomMarkerView(new TrackerBarChart.TrackerMarkerView(getActivity(), R.layout.layout_marker_view));
        barChart.barDataSet(yValues, mNation.getCountry());
        barChart.initLegend(entries);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
    }
}

























