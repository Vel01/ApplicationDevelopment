package kiz.austria.tracker.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import kiz.austria.tracker.R;
import kiz.austria.tracker.adapter.CountriesRecyclerAdapter;
import kiz.austria.tracker.data.Addresses;
import kiz.austria.tracker.data.CountriesDataParser;
import kiz.austria.tracker.data.JSONRawData;
import kiz.austria.tracker.model.Nation;

public class CountriesFragment extends Fragment implements View.OnClickListener, CountriesDataParser.OnDataAvailable {

    private static final String TAG = "CountriesFragment";

    @Override
    public void onDataAvailable(ArrayList<Nation> nations, JSONRawData.DownloadStatus status) {
        if (status == JSONRawData.DownloadStatus.OK) {
            Log.d(TAG, "onDataAvailable: started with " + nations.toString());
            mNations.addAll(nations);
            mCountriesRecyclerAdapter.notifyDataSetChanged();
        }

    }

    //vars
    private InflateFragment mInflatingFragment;
    private ArrayList<Nation> mNations = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private CountriesRecyclerAdapter mCountriesRecyclerAdapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        CountriesDataParser countryNationDataParser = CountriesDataParser.getInstance(this);
        countryNationDataParser.execute(Addresses.Link.DATA_COUNTRIES);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_countries, container, false);
        mRecyclerView = view.findViewById(R.id.rv_countries_list);
        ImageView btnBack = view.findViewById(R.id.btn_countries_back);
        btnBack.setOnClickListener(this);
        initRecyclerView();
        return view;
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mCountriesRecyclerAdapter = new CountriesRecyclerAdapter(mNations);
        mRecyclerView.setAdapter(mCountriesRecyclerAdapter);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mInflatingFragment = (InflateFragment) context;
    }

    @Override
    public void onClick(View v) {
        mInflatingFragment.inflateGlobalFragment();
    }


}
