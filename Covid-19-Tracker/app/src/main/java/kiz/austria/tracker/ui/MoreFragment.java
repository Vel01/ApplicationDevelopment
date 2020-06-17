package kiz.austria.tracker.ui;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import kiz.austria.tracker.R;

import static android.graphics.Color.rgb;

public class MoreFragment extends Fragment {

    private ModifiableBar mModifiableBar;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        Activity activity = getActivity();
        if (!(activity instanceof ModifiableBar) && activity != null) {
            throw new ClassCastException(activity.getClass().getSimpleName()
                    + " must implement Modifiable interface.");
        }

        mModifiableBar = (ModifiableBar) activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_more, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        mModifiableBar.updateActionBarColor(rgb(239, 83, 80));
    }

    @Override
    public void onPause() {
        super.onPause();
        mModifiableBar.updateActionBarColor(rgb(255, 255, 255));

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mModifiableBar = null;
    }

}