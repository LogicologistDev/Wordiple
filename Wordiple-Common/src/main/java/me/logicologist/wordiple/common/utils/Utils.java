package me.logicologist.wordiple.common.utils;

import java.text.DecimalFormat;
import java.util.function.Function;

public class Utils {

    private static final String VERSION = "1.0.1";
    private static final String ASSET_VERSION = "1.0.2";

    public static String formatNumber(double number) {
        return new DecimalFormat(number % 1 == 0 ? "#,###.##" : "#,##0.00").format(number);
    }

    public static String formatNumber(String number) {
        double parse = Double.parseDouble(number);
        return new DecimalFormat(parse % 1 == 0 ? "#,###.##" : "#,##0.00").format(parse);
    }

    public static String formatShortTime(long seconds) {
        StringBuilder sb = new StringBuilder();
        seconds = addUnit(sb, seconds, 604800, w -> w + "w, ");
        seconds = addUnit(sb, seconds, 86400, d -> d + "d, ");
        seconds = addUnit(sb, seconds, 3600, h -> h + "h, ");
        seconds = addUnit(sb, seconds, 60, m -> m + "m, ");
        addUnit(sb, seconds, 1, s -> s + "s, ");

        String timeString = sb.toString().replaceFirst("(?s)(.*), ", "$1");
        timeString = timeString.replaceFirst("(?s)(.*),", "$1,");
        return timeString.isEmpty() ? "0s" : timeString;
    }

    public static String formatTime(long seconds) {
        StringBuilder sb = new StringBuilder();
        seconds = addUnit(sb, seconds, 604800, w -> w + " week" + (w == 1 ? "" : "s") + ", ");
        seconds = addUnit(sb, seconds, 86400, d -> d + " day" + (d == 1 ? "" : "s") + ", ");
        seconds = addUnit(sb, seconds, 3600, h -> h + " hour" + (h == 1 ? "" : "s") + ", ");
        seconds = addUnit(sb, seconds, 60, m -> m + " minute" + (m == 1 ? "" : "s") + ", ");
        addUnit(sb, seconds, 1, s -> s + " second" + (s == 1 ? "" : "s") + ", ");

        String timeString = sb.toString().replaceFirst("(?s)(.*), ", "$1");
        timeString = timeString.replaceFirst("(?s)(.*),", "$1,");
        return timeString.isEmpty() ? "0 seconds" : timeString;
    }

    private static long addUnit(StringBuilder sb, long sec, long unit, Function<Long, String> s) {
        long n;
        if ((n = sec / unit) > 0) {
            sb.append(s.apply(n));
            sec %= (n * unit);
        }
        return sec;
    }

    public static String getVersion() {
        return VERSION;
    }

    public static String getAssetVersion() {
        return ASSET_VERSION;
    }
}
