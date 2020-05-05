package kiz.austria.tracker.util;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public final class TrackerPlate {

    private TrackerPlate() {
    }

    public static void hideSoftKeyboard(Activity context) {
        if (context != null) {
            View view = context.getCurrentFocus();
            if (view != null) {
                InputMethodManager manager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
                if (manager != null) {
                    manager.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
            }
        }
    }

}
