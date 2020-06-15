package kiz.austria.tracker.util;

import android.app.Activity;
import android.os.Bundle;
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

import kiz.austria.tracker.R;

public class TrackerUtility {

    public static int sort(final int value_one, final int value_two) {
        return value_two - value_one;
    }

    public static Float convert(String value) {
        return Float.parseFloat(value);
    }

    public static Float convert(int value) {
        return (float) value;
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

    public static String formatDateReported(String input) throws ParseException {
        final String DATE_OLD_FORMAT = "yyyy-MM-dd";
        final String DATE_NEW_FORMAT = "MMMM dd, yyyy";
        SimpleDateFormat inputFormat = new SimpleDateFormat(DATE_OLD_FORMAT, Locale.ENGLISH);
        SimpleDateFormat outputFormat = new SimpleDateFormat(DATE_NEW_FORMAT, Locale.ENGLISH);
        Date date = inputFormat.parse(input);
        assert date != null;
        return outputFormat.format(date);
    }

    private static final int FADEOUT_DELAY_MS = 500;

    public static void runFadeAnimationOn(View target, boolean in) {
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
        TrackerUtility.runFadeAnimationOn(root, false);
        new Thread(() -> {
            try {
                Thread.sleep(FADEOUT_DELAY_MS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            activity.finish();
        }).start();
    }

    public static void warnUserToExit(FragmentActivity host) {
        TrackerDialog dialog = new TrackerDialog();

        Bundle args = new Bundle();
        args.putString(TrackerKeys.KEY_STYLE, TrackerKeys.STYLE_DIALOG_NORMAL);
        args.putInt(TrackerKeys.KEY_DIALOG_ID, TrackerKeys.ACTION_DIALOG_ON_BACK_PRESSED);
        args.putString(TrackerKeys.KEY_DIALOG_TITLE, "Do you want to exit?");
        args.putString(TrackerKeys.KEY_DIALOG_MESSAGE, "You can check the update later.");
        args.putInt(TrackerKeys.KEY_DIALOG_POSITIVE_RID, R.string.label_dialog_continue);
        args.putInt(TrackerKeys.KEY_DIALOG_NEGATIVE_RID, R.string.label_dialog_exit);

        dialog.setArguments(args);
        dialog.show(host.getSupportFragmentManager(), null);
    }
}
