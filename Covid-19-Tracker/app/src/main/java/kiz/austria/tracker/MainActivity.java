package kiz.austria.tracker;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import kiz.austria.tracker.list.ListModel;
import kiz.austria.tracker.data.RawData;
import kiz.austria.tracker.data.PathContract;
import kiz.austria.tracker.data.DataParser;
import kiz.austria.tracker.model.Countries;

public class MainActivity extends AppCompatActivity implements DataParser.OnDataAvailable {

    private static final String TAG = "MainActivity";

    private TextView tvCases, tvDeaths, tvRecovered;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvCases = findViewById(R.id.tv_cases);
        tvDeaths = findViewById(R.id.tv_deaths);
        tvRecovered = findViewById(R.id.tv_recovered);
    }

    @Override
    protected void onResume() {
        super.onResume();
        DataParser dataParser = DataParser.getInstance(this);
        dataParser.execute(PathContract.Link.DATA_GLOBAL);
    }

    @Override
    public void onDataAvailable(ListModel<Countries> data, RawData.DownloadStatus status) {
        if (status == RawData.DownloadStatus.OK) {
            for (Countries countries : data.getCoverages()) {
                Log.d(TAG, "onDownloadComplete: data is " + countries.toString());
                tvCases.setText(countries.getCases());
                tvDeaths.setText(countries.getDeaths());
                tvRecovered.setText(countries.getRecovered());
            }
        } else Log.d(TAG, "onDownloadComplete: status " + status);
    }
}
