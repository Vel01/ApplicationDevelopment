package kiz.austria.tracker.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.ArrayList;
import java.util.Collections;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import kiz.austria.tracker.R;
import kiz.austria.tracker.adapter.AdapterClickListener;
import kiz.austria.tracker.adapter.DropRecyclerAdapter;
import kiz.austria.tracker.data.DownloadedData;
import kiz.austria.tracker.data.RawDataDownloader.DownloadStatus;
import kiz.austria.tracker.data.parser.PHDOHDataParser;
import kiz.austria.tracker.model.DOHDrop;
import kiz.austria.tracker.util.TrackerKeys;

public class DropFragment extends Fragment implements PHDOHDataParser.OnDataAvailable, AdapterClickListener.OnAdapterClickListener {

    private static final String TAG = "DropFragment";

    @BindView(R.id.rv_drop_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.container_refresher)
    SwipeRefreshLayout mRefreshLayout;

    private Unbinder mUnBinder;
    private PHDOHDataParser mPHDOHDataParser = null;
    private DropRecyclerAdapter mDropRecyclerAdapter;
    private final ArrayList<DOHDrop> mDropList = new ArrayList<>();

    private String mRawDataDropFromHerokuapp;
    private boolean isPaused = false;


    @Override
    public void onItemClick(View view, int position) {
        Log.d(TAG, "onItemClick() item clicked " + position);
        DOHDrop data = mDropList.get(position);
        Intent intent = new Intent(getActivity(), DropActivity.class);
        intent.putExtra(TrackerKeys.KEY_DROP_DOH_DATA, data);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, int position) {

    }


    @Override
    public void onDataPHDOHAvailable(ArrayList<DOHDrop> dohDrops, DownloadStatus status) {
        if (status == DownloadStatus.OK && !mPHDOHDataParser.isCancelled()) {
            Collections.reverse(dohDrops);
            mDropList.addAll(dohDrops);
            adaptWithData();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mRawDataDropFromHerokuapp = DownloadedData.getInstance().getHerokuappDOHData();
    }

    private void initRawDataForParsing() {
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

    private void initRecyclerViewAdapter() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        AdapterClickListener listener = new AdapterClickListener(getActivity(), mRecyclerView);
        listener.setOnAdapterClickListener(this);
        mDropRecyclerAdapter = new DropRecyclerAdapter();
        mRecyclerView.addOnItemTouchListener(listener);
        mRecyclerView.setAdapter(mDropRecyclerAdapter);
    }

    private void adaptWithData() {
        mDropRecyclerAdapter.addList(mDropList);
        mRecyclerView.setAdapter(mDropRecyclerAdapter);
    }

    private void adaptEmptyData() {
        if (!mDropList.isEmpty()) {
            mDropList.clear();
            mDropRecyclerAdapter.addList(mDropList);
            mRecyclerView.setAdapter(mDropRecyclerAdapter);
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        mRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mRefreshLayout.setOnRefreshListener(() -> {
            adaptEmptyData();
            initRawDataForParsing();
            if (!mRawDataDropFromHerokuapp.isEmpty() && mPHDOHDataParser.getStatus() != PHDOHDataParser.Status.RUNNING) {
                mPHDOHDataParser.cancel(true);
                mPHDOHDataParser = new PHDOHDataParser(DropFragment.this);
                mPHDOHDataParser.parse(PHDOHDataParser.ParseData.DOH_DROP);
                mPHDOHDataParser.execute(mRawDataDropFromHerokuapp);
            }
            mRefreshLayout.setRefreshing(false);
        });

        if (!isPausedToStopReDownload()) {
            if (!mRawDataDropFromHerokuapp.isEmpty()) {
                mPHDOHDataParser = new PHDOHDataParser(this);
                mPHDOHDataParser.parse(PHDOHDataParser.ParseData.DOH_DROP);
                mPHDOHDataParser.execute(mRawDataDropFromHerokuapp);
            }
        }
    }

    private void pausedToStopReDownload() {
        isPaused = true;
    }

    private boolean isPausedToStopReDownload() {
        return isPaused;
    }

    @Override
    public void onPause() {
        super.onPause();
        pausedToStopReDownload();
    }

    private void cancelDownload() {
        if (mPHDOHDataParser != null) mPHDOHDataParser.cancel(true);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnBinder.unbind();
        cancelDownload();
    }


}