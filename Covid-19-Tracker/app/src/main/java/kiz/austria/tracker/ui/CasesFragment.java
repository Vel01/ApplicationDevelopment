package kiz.austria.tracker.ui;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
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
import kiz.austria.tracker.adapter.CaseRecyclerAdapter;
import kiz.austria.tracker.data.DownloadedData;
import kiz.austria.tracker.data.RawDataDownloader;
import kiz.austria.tracker.data.parser.CaseDataParser;
import kiz.austria.tracker.model.Case;

public class CasesFragment extends BaseFragment implements AdapterClickListener.OnAdapterClickListener, CaseDataParser.OnDataAvailable {

    private static final String TAG = "CasesFragment";

    @BindView(R.id.rv_cases_list)
    RecyclerView mRecyclerView;
    @BindView(R.id.container_refresher)
    SwipeRefreshLayout mRefreshLayout;

    private boolean isPaused = false;
    private String mRawDataCasesFromHerokuapp;

    private Unbinder mUnbinder;
    private CaseRecyclerAdapter mCaseRecyclerAdapter;
    private CaseDataParser mCaseDataParser = null;

    private ArrayList<Case> mCasesList = new ArrayList<>();
    private LinearLayoutManager mLinearLayoutManager;

    @Override
    public void onDataCasesAvailable(ArrayList<Case> cases, RawDataDownloader.DownloadStatus status) {
        Log.d(TAG, "onDataCasesAvailable() cases " + cases.toString());
        if (status == RawDataDownloader.DownloadStatus.OK && !mCaseDataParser.isCancelled()) {
            Log.d(TAG, "onDataCasesAvailable() pasok dito");
            Collections.reverse(cases);
            mCasesList.addAll(cases);
            adaptWithData();
        }

    }

    @Override
    public void onItemClick(View view, int position) {
        Log.d(TAG, "onItemClick() position " + position);
        Case current = mCasesList.get(position);
        Log.d(TAG, "onItemClick() data is " + current);
    }

    @Override
    public void onItemLongClick(View view, int position) {
        //not supported.
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        initRawDataForParsing();
    }

    private void initRawDataForParsing() {
        mRawDataCasesFromHerokuapp = DownloadedData.getInstance().getHerokuappCasesData();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_cases, container, false);
        mUnbinder = ButterKnife.bind(this, view);
        initRecyclerView();
        return view;
    }

    private void adaptWithData() {
        mCaseRecyclerAdapter.addList(mCasesList);
        mRecyclerView.setAdapter(mCaseRecyclerAdapter);
    }

    private void adaptEmptyData() {
        if (!mCasesList.isEmpty()) {
            mCasesList.clear();
            mCaseRecyclerAdapter.addList(mCasesList);
            mRecyclerView.setAdapter(mCaseRecyclerAdapter);
        }
    }

    private void initRecyclerView() {
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);
        AdapterClickListener listener = new AdapterClickListener(getActivity(), mRecyclerView);
        listener.setOnAdapterClickListener(this);
        mCaseRecyclerAdapter = new CaseRecyclerAdapter();
        mRecyclerView.addOnItemTouchListener(listener);
        mRecyclerView.setAdapter(mCaseRecyclerAdapter);

    }

    @Override
    public void onResume() {
        super.onResume();
        mRefreshLayout.setColorSchemeResources(R.color.colorAccent);
        mRefreshLayout.setOnRefreshListener(() -> {

            adaptEmptyData();
            initRawDataForParsing();
            if (!mRawDataCasesFromHerokuapp.isEmpty() && mCaseDataParser.getStatus() != CaseDataParser.Status.RUNNING) {
                mCaseDataParser.cancel(true);
                mCaseDataParser = new CaseDataParser(CasesFragment.this);
                mCaseDataParser.parse(CaseDataParser.ParseData.CASES);
                mCaseDataParser.execute(mRawDataCasesFromHerokuapp);
            }

            mRefreshLayout.setRefreshing(false);
        });

        if (!isPausedToStopReDownload()) {
            if (!mRawDataCasesFromHerokuapp.isEmpty()) {
                mCaseDataParser = new CaseDataParser(this);
                mCaseDataParser.parse(CaseDataParser.ParseData.CASES);
                mCaseDataParser.execute(mRawDataCasesFromHerokuapp);
            }
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        pausedToStopReDownload();
    }


    private void cancelDownload() {
        if (mCaseDataParser != null) mCaseDataParser.cancel(true);
    }

    private void pausedToStopReDownload() {
        isPaused = true;
    }

    private boolean isPausedToStopReDownload() {
        return isPaused;
    }

    public int getScrollPosition() {
        return mRecyclerView.computeVerticalScrollOffset();
    }

    public void resetScrollPosition() {
        mLinearLayoutManager.scrollToPositionWithOffset(0, 0);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mUnbinder.unbind();
        cancelDownload();
    }


}