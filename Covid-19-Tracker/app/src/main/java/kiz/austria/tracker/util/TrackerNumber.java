package kiz.austria.tracker.util;

import android.widget.TextView;

import java.text.NumberFormat;
import java.util.Locale;

public final class TrackerNumber {

    private TrackerNumber() {
        //empty constructor
    }

    public static void display(final TextView mTextField, int value) {
        mTextField.setText(NumberFormat.getNumberInstance(Locale.US).format(value));

    }


}
