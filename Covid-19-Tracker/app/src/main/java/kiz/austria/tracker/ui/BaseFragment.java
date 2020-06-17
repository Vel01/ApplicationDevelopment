package kiz.austria.tracker.ui;

import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public abstract class BaseFragment extends Fragment {

    Typeface tfRegular;
    Typeface tfLight;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity() != null) {
            tfRegular = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");
            tfLight = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf");
        }
    }

    public abstract int getScrollPosition();

    public abstract void resetScrollPosition();
}
