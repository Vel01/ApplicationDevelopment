package kiz.austria.tracker.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import kiz.austria.tracker.R;
import kiz.austria.tracker.model.Nation;
import kiz.austria.tracker.util.TrackerKeys;
import kiz.austria.tracker.util.TrackerNumber;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectedNationFragment extends Fragment {

    //references
    private Nation mNation;
    //widgets
    private TextView mConfirmed;
    private TextView mDeaths;
    private TextView mRecovered;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Bundle args = getArguments();
        if (args != null) {

            mNation = args.getParcelable(TrackerKeys.KEY_SELECTED_COUNTRY);
            System.out.println(mNation);

        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_selected_nation, container, false);
        mConfirmed = view.findViewById(R.id.tv_cases);
        mDeaths = view.findViewById(R.id.tv_deaths);
        mRecovered = view.findViewById(R.id.tv_recovered);
        TrackerNumber.display(mConfirmed, Integer.parseInt(mNation.getConfirmed()));
        TrackerNumber.display(mDeaths, Integer.parseInt(mNation.getDeaths()));
        TrackerNumber.display(mRecovered, Integer.parseInt(mNation.getRecovered()));

        return view;
    }
}
