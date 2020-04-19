package kiz.austria.tracker.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import kiz.austria.tracker.R;

public class CountriesFragment extends Fragment implements View.OnClickListener {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_countries, container, false);

        ImageView btnBack = view.findViewById(R.id.btn_countries_back);
        btnBack.setOnClickListener(this);
        return view;
    }

    @Override
    public void onClick(View v) {
        assert getFragmentManager() != null;
        getFragmentManager().beginTransaction().replace(R.id.fragment_container, new GlobalFragment()).commit();

    }
}
