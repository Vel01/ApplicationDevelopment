package kiz.austria.tracker.util;

import java.text.NumberFormat;

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

}
