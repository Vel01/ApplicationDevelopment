package kiz.austria.tracker.util;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
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

}
