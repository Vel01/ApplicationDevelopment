package kiz.austria.tracker.util;

import android.app.Activity;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.muddzdev.styleabletoast.StyleableToast;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class TrackerUtility {

    public static int sort(final int value_one, final int value_two) {
        return value_two - value_one;
    }

    public static Float convert(String value) {
        return Float.parseFloat(value);
    }

    public static String format(float value) {
        return NumberFormat.getNumberInstance().format(value);
    }

    public static void message(FragmentActivity activity, String message, int iconStart, int textColor, int bgColor) {
        new StyleableToast.Builder(Objects.requireNonNull(activity)).iconStart(iconStart)
                .text(message).textColor(activity.getResources().getColor(textColor))
                .backgroundColor(activity.getResources().getColor(bgColor))
                .cornerRadius(10).length(Toast.LENGTH_LONG).show();
    }

    public static String getCurrentDate() {
        final String DATE_FORMAT_2 = "EEEE, MMM d, yyyy";
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT_2, Locale.US);
        dateFormat.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date c = Calendar.getInstance().getTime();
        return "(Last Updated: " + dateFormat.format(c) + ")";
    }

    public static String formatDate(String input) throws ParseException {
        final String DATE_OLD_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        final String DATE_NEW_FORMAT = "EEEE, MMM d, yyyy HH:mm:ss a z";
        SimpleDateFormat inputFormat = new SimpleDateFormat(DATE_OLD_FORMAT, Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat(DATE_NEW_FORMAT, Locale.ENGLISH);
        Date date = inputFormat.parse(input);
        assert date != null;
        return "(Last Updated: " + outputFormat.format(date) + ")";
    }

    public static String formatSimpleDate(String input) throws ParseException {
        final String DATE_OLD_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
        final String DATE_NEW_FORMAT = "L/d/yy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(DATE_OLD_FORMAT, Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat(DATE_NEW_FORMAT, Locale.ENGLISH);
        Date date = inputFormat.parse(input);
        assert date != null;
        return outputFormat.format(date);
    }

    private static final int FADEOUT_DELAY_MS = 500;

    public static void runFadeAnimationOn(Activity ctx, View target, boolean in) {
        int start, finish;
        if (in) {
            start = 0;
            finish = 1;
        } else {
            start = 1;
            finish = 0;
        }
        AlphaAnimation fade = new AlphaAnimation(start, finish);
        fade.setDuration(1000);
        fade.setFillAfter(true);
        target.startAnimation(fade);
    }

    public static void finishFade(Activity activity, View root) {
        TrackerUtility.runFadeAnimationOn(activity, root, false);
        new Thread(() -> {
            try {
                Thread.sleep(FADEOUT_DELAY_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            activity.finish();
        }).start();
    }

}
