package kiz.austria.tracker.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
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
import kiz.austria.tracker.model.Nation;

public class CountriesFragment extends Fragment implements View.OnClickListener, //CountriesDataParser.OnDataAvailable,
        OnBackPressed {

    private static final String TAG = "CountriesFragment";

    //events
    @Override
    public void onClick(View v) {
        //onClick() navigation back to global fragment
        mListener.onInflateGlobalFragment();
    }

    //vars
    private OnInflateFragmentListener mListener;
    private ArrayList<Nation> mNations = new ArrayList<>();
    private RecyclerView mRecyclerView;
    private CountriesRecyclerAdapter mCountriesRecyclerAdapter;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        initInterface();
        getBundleArguments();
    }

    private void initInterface() {
        Activity activity = getActivity();
        if (!(activity instanceof OnInflateFragmentListener) && activity != null) {
            throw new ClassCastException(activity.getClass().getSimpleName()
                    + " must implement OnInflateFragmentListener interface");
        }
        mListener = (OnInflateFragmentListener) activity;

    }

    private void getBundleArguments() {
        Bundle args = this.getArguments();
        if (args != null) {
            ArrayList<Nation> nations = args.getParcelableArrayList(getString(R.string.intent_countries));
            if (nations != null) {
                mNations.addAll(nations);
            }
        }
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
    public void onResume() {
        super.onResume();
        mCountriesRecyclerAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public boolean onBackPressed() {
        //TODO: AlertDialog, ask user if he/she want's to quit the app.
        getActivity().finish();
        return true;
    }

}
