package kiz.austria.tracker.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import kiz.austria.tracker.R;
import kiz.austria.tracker.adapter.DropRecyclerAdapter;
import kiz.austria.tracker.data.DownloadRawData;
import kiz.austria.tracker.data.DownloadedData;
import kiz.austria.tracker.data.parser.PHDOHDataParser;
import kiz.austria.tracker.model.DOHDrop;

public class DropFragment extends Fragment implements PHDOHDataParser.OnDataAvailable {

    private static final String TAG = "DropFragment";
    private final ArrayList<DOHDrop> mDropList = new ArrayList<>();
    @BindView(R.id.rv_drop_list)
    RecyclerView mRecyclerView;
    private Unbinder mUnBinder;
    private PHDOHDataParser mPHDOHDataParser = null;
    private DropRecyclerAdapter mDropRecyclerAdapter;

    private String mRawDataDropFromHerokuapp;

    @Override
    public void onDataPHDOHAvailable(List<DOHDrop> dohDrops, DownloadRawData.DownloadStatus status) {
        if (status == DownloadRawData.DownloadStatus.OK && !mPHDOHDataParser.isCancelled()) {
            Collections.reverse(dohDrops);
            mDropList.addAll(dohDrops);
            mDropRecyclerAdapter.addList(mDropList);

        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        mRawDataDropFromHerokuapp = DownloadedData.getInstance().getHerokuappDOHData();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_drop, container, false);
        mUnBinder = ButterKnife.bind(this, view);
        initRecyclerViewAdapter();
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mRawDataDropFromHerokuapp.isEmpty()) {
            mPHDOHDataParser = new PHDOHDataParser(this);
            mPHDOHDataParser.parse(PHDOHDataParser.ParseData.DOH_DROP);
            mPHDOHDataParser.execute(mRawDataDropFromHerokuapp);
        }
    }

    private void initRecyclerViewAdapter() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mDropRecyclerAdapter = new DropRecyclerAdapter();
        mRecyclerView.setAdapter(mDropRecyclerAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnBinder.unbind();
    }


}