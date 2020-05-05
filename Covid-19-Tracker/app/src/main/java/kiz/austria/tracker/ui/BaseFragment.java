package kiz.austria.tracker.ui;

import android.graphics.Typeface;
import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class BaseFragment extends Fragment {

    private static final String DATE_FORMAT_2 = "MMM dd, yyyy";
    protected Typeface tfRegular;
    protected Typeface tfLight;
    protected Typeface tfRoboto;
    protected Typeface tfRobotoBold;
    protected Typeface tfRobotoBlack;



    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getActivity() != null) {
            tfRegular = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Regular.ttf");
            tfLight = Typeface.createFromAsset(getActivity().getAssets(), "OpenSans-Light.ttf");
            tfRoboto = Typeface.createFromAsset(getActivity().getAssets(), "roboto.ttf");
            tfRobotoBold = Typeface.createFromAsset(getActivity().getAssets(), "roboto_bold.ttf");
            tfRobotoBlack = Typeface.createFromAsset(getActivity().getAssets(), "roboto_black.ttf");
        }


    }

    protected String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_2, Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date c = Calendar.getInstance().getTime();
        return "Last Update: " + dateFormat.format(c);
    }

    protected void setTextViewTypeface(TextView view, Typeface typeface) {
        view.setTypeface(typeface);
    }

}
