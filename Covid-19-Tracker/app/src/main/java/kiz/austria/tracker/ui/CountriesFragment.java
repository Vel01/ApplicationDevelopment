package kiz.austria.tracker.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import kiz.austria.tracker.R;
import kiz.austria.tracker.adapter.CountriesRecyclerAdapter;
import kiz.austria.tracker.model.Nation;
import kiz.austria.tracker.util.TrackerDialog;
import kiz.austria.tracker.util.TrackerKeys;
import kiz.austria.tracker.util.TrackerTextWatcher;

public class CountriesFragment extends Fragment implements View.OnClickListener {

    private static final String TAG = "CountriesFragment";

    /**
     * Navigate back to GlobalFragment using navigation
     * back button.
     */
    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.imb_countries_back:
                mListener.onInflateGlobalFragment();
                break;
            case R.id.imb_countries_sort:
                TrackerDialog dialog = new TrackerDialog();

                Bundle args = new Bundle();
                args.putString(TrackerKeys.KEY_STYLE, TrackerKeys.STYLE_DIALOG_CUSTOM);
                args.putString(TrackerKeys.KEY_DIALOG_MESSAGE, null);
                args.putInt(TrackerKeys.KEY_DIALOG_ID, TrackerKeys.ACTION_DIALOG_SORT_MENU);
                dialog.setView(initSortView(dialog));
                dialog.setArguments(args);
                assert getFragmentManager() != null;
                dialog.show(getFragmentManager(), null);
                break;
        }
    }

    //vars
    private OnInflateFragmentListener mListener;
    private ArrayList<Nation> mNations = new ArrayList<>();
    private CountriesRecyclerAdapter mCountriesRecyclerAdapter;

    //widgets
    private RecyclerView mRecyclerView;
    private EditText mSearch;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        initInterface();
        getBundleArguments();
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
        mSearch = view.findViewById(R.id.edt_country_search);

        ImageView btnBack = view.findViewById(R.id.imb_countries_back);
        btnBack.setOnClickListener(this);

        ImageButton btnSort = view.findViewById(R.id.imb_countries_sort);
        btnSort.setOnClickListener(this);

        initRecyclerView();
        return view;
    }

    private void initInterface() {
        Activity activity = getActivity();
        if (!(activity instanceof OnInflateFragmentListener) && activity != null) {
            throw new ClassCastException(activity.getClass().getSimpleName()
                    + " must implement OnInflateFragmentListener interface");
        }
        mListener = (OnInflateFragmentListener) activity;

    }

    private void initRecyclerView() {
        LinearLayoutManager manager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(manager);
        mCountriesRecyclerAdapter = new CountriesRecyclerAdapter(mNations, getActivity(), manager);
        mRecyclerView.setAdapter(mCountriesRecyclerAdapter);
        initSearchText();
    }

    private void initSearchText() {
        mSearch.addTextChangedListener(new TrackerTextWatcher(mNations, mCountriesRecyclerAdapter, getActivity()));
    }

    private int sort(final int value_one, final int value_two) {

        if (value_one < value_two) {
            return -1;
        } else if (value_one > value_two) {
            return 1;
        }
        return 0;

    }

    private void notifyChangedAdapter() {
        LinearLayoutManager manager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        assert manager != null;
        //scroll back to top
        manager.scrollToPositionWithOffset(0, 0);
        mCountriesRecyclerAdapter.notifyDataSetChanged();
    }

    private View initSortView(final TrackerDialog dialog) {
        View view = getLayoutInflater().inflate(R.layout.layout_countries_sort_dialog_container, null);
        ListView categories = view.findViewById(R.id.rv_countries_sort_list);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), R.layout.layout_countries_sort_dialog_item, new String[]{
                "Confirmed", "Deaths", "Recovered"});
        categories.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        categories.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case TrackerKeys.MENU_SORT_CATEGORY_CONFIRMED:
                        Collections.sort(mNations, new Comparator<Nation>() {
                            @Override
                            public int compare(Nation o1, Nation o2) {
                                return sort(Integer.parseInt(o2.getCases()), Integer.parseInt(o1.getCases()));
                            }
                        });
                        notifyChangedAdapter();
                        dialog.dismiss();
                        break;
                    case TrackerKeys.MENU_SORT_CATEGORY_DEATHS:
                        Collections.sort(mNations, new Comparator<Nation>() {
                            @Override
                            public int compare(Nation o1, Nation o2) {
                                return sort(Integer.parseInt(o2.getDeaths()), Integer.parseInt(o1.getDeaths()));
                            }
                        });
                        notifyChangedAdapter();
                        dialog.dismiss();
                        break;
                    case TrackerKeys.MENU_SORT_CATEGORY_RECOVERED:
                        Collections.sort(mNations, new Comparator<Nation>() {
                            @Override
                            public int compare(Nation o1, Nation o2) {
                                return sort(Integer.parseInt(o2.getRecovered()), Integer.parseInt(o1.getRecovered()));
                            }
                        });
                        notifyChangedAdapter();
                        dialog.dismiss();
                        break;
                }
            }
        });
        return view;
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
}
