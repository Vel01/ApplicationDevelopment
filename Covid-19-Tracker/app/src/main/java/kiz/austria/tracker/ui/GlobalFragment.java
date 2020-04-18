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

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_global, container, false);
        tvCases = view.findViewById(R.id.tv_cases);
        tvDeaths = view.findViewById(R.id.tv_deaths);
        tvRecovered = view.findViewById(R.id.tv_recovered);
        return view;
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

        } else Log.d(TAG, "onDownloadComplete: status " + status);
    }

}
