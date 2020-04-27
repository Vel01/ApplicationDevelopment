package kiz.austria.tracker.model;

import android.os.Parcelable;

public class Global extends Nation implements Parcelable {

    public Global(String cases, String deaths, String recovered) {
        super("N/A", cases, deaths, "N/A", "N/A", recovered,
                "N/A", "N/A");
    }

}
