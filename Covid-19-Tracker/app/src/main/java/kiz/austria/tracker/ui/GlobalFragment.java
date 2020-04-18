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

import java.util.ArrayList;
import java.util.List;

import kiz.austria.tracker.R;
import kiz.austria.tracker.data.DataParser;
import kiz.austria.tracker.data.PathContract;
import kiz.austria.tracker.data.RawData;
import kiz.austria.tracker.list.ListModel;
import kiz.austria.tracker.model.Countries;
import kiz.austria.tracker.util.AnimationContract;

public class GlobalFragment extends Fragment implements DataParser.OnDataAvailable {

    private static final String TAG = "GlobalFragment";

    private TextView tvCases, tvDeaths, tvRecovered;
    private int disCases, disDeaths, disRecovered;
    private AnyChartView pieGlobalCases;

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

    @Override
    public void onResume() {
        super.onResume();
        DataParser dataParser = DataParser.getInstance(this);
        dataParser.execute(PathContract.Link.DATA_GLOBAL);
    }


    @Override
    public void onDataAvailable(ListModel<Countries> data, RawData.DownloadStatus status) {
        if (status == RawData.DownloadStatus.OK) {
            for (Countries countries : data.getCoverages()) {
                Log.d(TAG, "onDownloadComplete: data is " + countries.toString());
                disCases = Integer.parseInt(countries.getCases());
                AnimationContract.Display.countNumber(tvCases, disCases);
                disDeaths = Integer.parseInt(countries.getDeaths());
                AnimationContract.Display.countNumber(tvDeaths, disDeaths);
                disRecovered = Integer.parseInt(countries.getRecovered());
                AnimationContract.Display.countNumber(tvRecovered, disRecovered);
            }
            pieInit();

        } else Log.d(TAG, "onDownloadComplete: status " + status);
    }

}
