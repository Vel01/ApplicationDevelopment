package kiz.austria.tracker.ui;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kiz.austria.tracker.R;
import kiz.austria.tracker.data.DataParser;
import kiz.austria.tracker.data.PathContract;
import kiz.austria.tracker.data.RawData;
import kiz.austria.tracker.list.ListModel;
import kiz.austria.tracker.model.Countries;

public class GlobalFragment extends Fragment implements DataParser.OnDataAvailable {

    private static final String TAG = "GlobalFragment";

    private TextView tvCases, tvDeaths, tvRecovered;
    private int disCases, disDeaths, disRecovered;
    private AnyChartView pieGlobalCases;

    @Override
    public void onDataAvailable(ListModel<Countries> data, RawData.DownloadStatus status) {
        if (status == RawData.DownloadStatus.OK) {
            for (Countries countries : data.getCoverages()) {
                Log.d(TAG, "onDownloadComplete: data is " + countries.toString());
                disCases = Integer.parseInt(countries.getCases());
                tvCases.setText(NumberFormat.getNumberInstance(Locale.US).format(disCases));
                disDeaths = Integer.parseInt(countries.getDeaths());
                tvDeaths.setText(NumberFormat.getNumberInstance(Locale.US).format(disDeaths));
                disRecovered = Integer.parseInt(countries.getRecovered());
                tvRecovered.setText(NumberFormat.getNumberInstance(Locale.US).format(disRecovered));
            }
            pieInit();

        } else Log.d(TAG, "onDownloadComplete: status " + status);
    }

    private void pieInit() {


        Pie pie = AnyChart.pie();

        List<DataEntry> dataEntries = new ArrayList<>();
        Log.d(TAG, "pieInit: " + disCases);
        dataEntries.add(new ValueDataEntry("Cases", disCases));
        dataEntries.add(new ValueDataEntry("Deaths", disDeaths));
        dataEntries.add(new ValueDataEntry("Recovered", disRecovered));

        pie.data(dataEntries);
        pieGlobalCases.setChart(pie);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_global, container, false);
        tvCases = view.findViewById(R.id.tv_cases);
        tvDeaths = view.findViewById(R.id.tv_deaths);
        tvRecovered = view.findViewById(R.id.tv_recovered);
        pieGlobalCases = view.findViewById(R.id.any_pie_global_cases);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        DataParser dataParser = DataParser.getInstance(this);
        dataParser.execute(PathContract.Link.DATA_GLOBAL);
    }
}
